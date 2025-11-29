package com.nhnacademy.batch.messagequeue;

import com.nhnacademy.batch.utils.RedisTime;
import com.nhnacademy.batch.utils.SlotKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponRequestConsumer {
    private final StringRedisTemplate redis;

    private static final String COUNT_KEY = "req:{%s}:count";
    private static final String LIST_KEY  = "req:{%s}:list";

    @RabbitListener(queues = "q.create.request")
    public void onMessage(String msg) {
        log.info("q.create.request : {} consumer recieved msg", msg);
        long now = RedisTime.nowMillis(redis);
        String slot = SlotKey.currentSlot(now);

        String countKey = COUNT_KEY.formatted(slot);
        String listKey  = LIST_KEY.formatted(slot);

        Long v = redis.opsForValue().increment(countKey);
        if (v != null && v == 1L) {
            redis.expire(countKey, Duration.ofDays(3));
        }

        redis.opsForList().rightPush(listKey, msg);
        if (v != null && v == 1L) {
            redis.expire(listKey, Duration.ofDays(3));
        }
    }
}
