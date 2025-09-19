package com.nhnacademy.batch.coupon.tasklet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.batch.dto.CreateCouponFormRequest;
import com.nhnacademy.batch.entity.coupon.CouponForm;
import com.nhnacademy.batch.repository.CouponFormRepository;
import com.nhnacademy.batch.repository.CouponTypeRepository;
import com.nhnacademy.batch.repository.CouponUsageRepository;
import com.nhnacademy.batch.utils.RedisTime;
import com.nhnacademy.batch.utils.SlotKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class Tasklets implements Tasklet {
    private final StringRedisTemplate redis;
    private final CouponUsageRepository couponUsageRepository;
    private final CouponTypeRepository couponTypeRepository;
    private final CouponFormRepository couponFormRepository;
    private final ObjectMapper objectMapper;

    private static final String COUNT_KEY = "req:{%s}:count";
    private static final String LIST_KEY  = "req:{%s}:list";
    private static final String PROC_KEY  = "req:{%s}:proc";
    private static final String LOCK_KEY  = "req:{%s}:lock";

    // list→proc 스왑 (원자)
    private static final String LUA_SWAP = """
        if redis.call('EXISTS', KEYS[1]) == 1 then
          if redis.call('EXISTS', KEYS[2]) == 1 then
            return -1
          else
            redis.call('RENAME', KEYS[1], KEYS[2])
            return 1
          end
        else
          return 0
        end
    """;

    @Override
    public RepeatStatus execute(StepContribution c, ChunkContext ctx) throws Exception {
        long now = RedisTime.nowMillis(redis);

        // 1) 직전 슬롯 우선 처리
        String currentPrevSlot = SlotKey.previousSlot(now);
        processOneSlot(currentPrevSlot);

        // 2) 백로그 슬롯(더 옛날 슬롯들)도 추가 처리
        //    - req:{slot}:proc 에 잔여가 있거나
        //    - req:{slot}:list 가 남아있는 경우 전부 처리
        for (String oldSlot : findBacklogSlotsOlderThan(currentPrevSlot)) {
            try {
                processOneSlot(oldSlot);
            } catch (Exception e) {
                log.error("Backlog slot processing failed. slot={}", oldSlot, e);
                // 실패했다고 배치 전체 중단하지 않고 다음 슬롯 계속 시도
            }
        }

        return RepeatStatus.FINISHED;
    }

    /** 슬롯 하나 처리: proc에 있으면 바로 처리, 없으면 list→proc 스왑 후 처리 */
    private void processOneSlot(String slot) throws Exception {
        String countKey = COUNT_KEY.formatted(slot);
        String listKey  = LIST_KEY.formatted(slot);
        String procKey  = PROC_KEY.formatted(slot);
        String lockKey  = LOCK_KEY.formatted(slot);

        // ── (선택) 분산 락: 동시 배치 실행 방지
        Boolean gotLock = redis.opsForValue().setIfAbsent(lockKey, "1", Duration.ofMinutes(15));
        if (gotLock == null || !gotLock) {
            // 다른 배치가 이미 처리 중
            log.warn("Skip slot={} due to lock exists", slot);
            return;
        }

        try {
            // 1) proc에 잔여가 있으면 스왑 생략하고 바로 처리
            Long procLen = redis.opsForList().size(procKey);
            if (procLen == null || procLen == 0L) {
                // 2) proc이 비었으면 list→proc 스왑 시도
                Long swapped = redis.execute((RedisCallback<Long>) con ->
                        con.eval(LUA_SWAP.getBytes(), ReturnType.INTEGER, 2, listKey.getBytes(), procKey.getBytes()));

                if (swapped == null || swapped == 0) {
                    // 처리할 것 없음: countKey 초기화만
                    redis.opsForValue().setIfAbsent(countKey, "0");
                    return;
                }
                if (swapped == -1) {
                    throw new IllegalStateException("Previous batch not finished for slot " + slot);
                }
            }

            // 3) 기대치(expected) 확보 (없으면 proc 리스트 길이로 보정)
            long expected = 0;
            String v = redis.opsForValue().get(countKey);
            if (v != null) {
                try { expected = Long.parseLong(v); } catch (NumberFormatException ignore) {}
            }
            if (expected == 0) {
                Long len = redis.opsForList().size(procKey);
                expected = (len == null) ? 0 : len;
            }

            // 4) 실제 저장 루프
            long processed = consumeAndSave(procKey);

            // 5) 검증
            if (processed < expected) {
                log.error("Count mismatch: slot={} expected={} processed={}", slot, expected, processed);
                // 정책에 따라 예외/알람/보류 중 택1
                // throw new IllegalStateException(...);
            }

            // 6) 초기화
            redis.opsForValue().set(countKey, "0");
            redis.delete(procKey);
        } finally {
            redis.delete(lockKey);
        }
    }

    /** proc 리스트를 CHUNK 단위로 읽어 CouponForm 벌크 저장 */
    private long consumeAndSave(String procKey) throws Exception {
        final int CHUNK = 5_000;
        long processed = 0;

        while (true) {
            Long len = redis.opsForList().size(procKey);
            if (len == null || len == 0) break;

            int take = (int) Math.min(len, CHUNK);
            List<String> batch = redis.opsForList().range(procKey, 0, take - 1);
            if (batch == null || batch.isEmpty()) break;

            List<CouponForm> couponFormList = new ArrayList<>(batch.size());
            for (String msg : batch) {
                CreateCouponFormRequest request = objectMapper.readValue(msg, CreateCouponFormRequest.class);
                var couponUsage = couponUsageRepository.findById(request.couponUsageId()).orElseThrow();
                var couponType  = couponTypeRepository.findById(request.couponTypeId()).orElseThrow();

                CouponForm couponForm = new CouponForm();
                couponForm.setBasicDetails(
                        request.startDate(),
                        request.endDate(),
                        request.name(),
                        request.quantity(),
                        UUID.randomUUID(),
                        request.maxPrice(),
                        request.minPrice()
                );
                couponForm.setCouponDetails(couponType, couponUsage);
                couponFormList.add(couponForm);
            }

            // JPA 벌크 save (배치 최적화는 yml의 hibernate.jdbc.batch_size 등으로)
            couponFormRepository.saveAll(couponFormList);

            // 처리한 만큼 LTRIM
            redis.opsForList().trim(procKey, take, -1);
            processed += batch.size();
        }

        return processed;
    }

    /** 현재 처리한 직전 슬롯보다 "더 이전"의 슬롯들 중, list 또는 proc에 잔여가 있는 슬롯들을 수집 */
    private List<String> findBacklogSlotsOlderThan(String currentPrevSlot) {
        // 성능을 위해 KEYS 대신 SCAN 사용
        Set<String> slots = new HashSet<>();

        // req:{*}:proc 와 req:{*}:list 를 각각 SCAN
        scanKeys("req:{*:proc}", key -> {
            String slot = extractSlotFromTaggedKey(key, "proc");
            if (slot != null && slot.compareTo(currentPrevSlot) < 0) slots.add(slot);
        });
        scanKeys("req:{*:list}", key -> {
            String slot = extractSlotFromTaggedKey(key, "list");
            if (slot != null && slot.compareTo(currentPrevSlot) < 0) slots.add(slot);
        });

        // 오래된 → 최신 순으로 정렬해서 처리(선택)
        List<String> ordered = new ArrayList<>(slots);
        Collections.sort(ordered);
        return ordered;
    }

    /** Spring Data Redis SCAN 유틸 */
    private void scanKeys(String pattern, Consumer<String> onKey) {
        // pattern 예: "req:{*:proc}"  (해시태그 포함 패턴)
        redis.execute((RedisCallback<Object>) connection -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match(pattern)
                    .count(1000)
                    .build())) {
                cursor.forEachRemaining(k -> onKey.accept(new String(k, StandardCharsets.UTF_8)));
            } catch (Exception e) {
                log.warn("SCAN failed pattern={}", pattern, e);
            }
            return null;
        });
    }

    /** "req:{<slot>}:proc" 형태의 키에서 slot 추출 */
    private String extractSlotFromTaggedKey(String key, String suffix) {
        // 예: key = "req:{20250919T1300}:proc"
        String marker = "}:" + suffix;
        int braceOpen = key.indexOf("{");
        int braceClose = key.indexOf("}");
        if (braceOpen < 0 || braceClose < 0 || braceClose <= braceOpen) return null;
        if (!key.endsWith("}:" + suffix)) return null;
        return key.substring(braceOpen + 1, braceClose);
    }


//    @Override
//    public RepeatStatus execute(StepContribution c, ChunkContext ctx) throws Exception {
//        long now = RedisTime.nowMillis(redis);
//        String slot = SlotKey.previousSlot(now);
//
//        String countKey = "req:count:" + slot;
//        String fromKey  = "req:list:"  + slot;
//        String procKey  = "req:proc:"  + slot;
//
//        Long swapped = redis.execute((RedisCallback<Long>) con ->
//                con.eval(LUA_SWAP.getBytes(), ReturnType.INTEGER, 2, fromKey.getBytes(), procKey.getBytes()));
//        if (swapped == null || swapped == 0) {
//            redis.opsForValue().setIfAbsent(countKey, "0");
//            return RepeatStatus.FINISHED;
//        }
//        if (swapped == -1) {
//            throw new IllegalStateException("Previous batch not finished for slot " + slot);
//        }
//
//        long expected = 0;
//        String v = redis.opsForValue().get(countKey);
//        if (v != null) expected = Long.parseLong(v);
//
//        long processed = 0;
//        final int CHUNK = 5_000;
//
//        while (true) {
//            Long len = redis.opsForList().size(procKey);
//            if (len == null || len == 0) break;
//
//            long take = Math.min(len, CHUNK);
//            // LRANGE 0..take-1
//            var batch = redis.opsForList().range(procKey, 0, take - 1);
//            if (batch == null || batch.isEmpty()) break;
//
//            List<CouponForm> couponFormList = new ArrayList<>();
//            for (String msg : batch) {
//                CreateCouponFormRequest request = objectMapper.readValue(msg, CreateCouponFormRequest.class);
//                CouponUsage couponUsage = couponUsageRepository
//                        .findById(request.couponUsageId())
//                        .orElseThrow();
//
//                CouponType couponType = couponTypeRepository
//                        .findById(request.couponTypeId())
//                        .orElseThrow();
//
//                CouponForm couponForm = new CouponForm();
//
//                couponForm.setBasicDetails(
//                        request.startDate(),
//                        request.endDate(),
//                        request.name(),
//                        request.quantity(),
//                        UUID.randomUUID(),
//                        request.maxPrice(),
//                        request.minPrice()
//                );
//
//                couponForm.setCouponDetails(
//                        couponType,
//                        couponUsage
//                );
//
//                couponFormList.add(couponForm);
//            }
//            couponFormRepository.saveAll(couponFormList);
//
//            // 처리한 만큼 LTRIM
//            redis.opsForList().trim(procKey, take, -1);
//            processed += batch.size();
//        }
//
//        if (processed != expected) {
//            // 필요 정책에 따라 경고/조정
//            // 기대치보다 적으면 실패로 간주
//            if (processed < expected) {
//                log.error("Mismatch slot={} expected={} processed={}", slot, expected, processed);
//                //throw new IllegalStateException("Mismatch slot=" + slot + " expected=" + expected + " processed=" + processed);
//            }
//        }
//
//        redis.opsForValue().set(countKey, "0");
//        redis.delete(procKey);
//        return RepeatStatus.FINISHED;
//    }
}
