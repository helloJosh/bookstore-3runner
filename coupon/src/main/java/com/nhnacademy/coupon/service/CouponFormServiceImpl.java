package com.nhnacademy.coupon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.coupon.dto.CouponFormDto;
import com.nhnacademy.coupon.dto.request.CreateCouponFormRequest;
import com.nhnacademy.coupon.dto.response.ReadCouponFormResponse;
import com.nhnacademy.coupon.exceptionhandler.CouponFormNotExistException;
import com.nhnacademy.coupon.messagequeue.producer.CouponRequestPublisher;
import com.nhnacademy.coupon.repository.CouponFormRepository;
import com.nhnacademy.coupon.exceptionhandler.CouponTypeDoesNotExistException;
import com.nhnacademy.coupon.repository.CouponTypeRepository;
import com.nhnacademy.coupon.exceptionhandler.CouponUsageDoesNotExistException;
import com.nhnacademy.coupon.repository.CouponUsageRepository;
import com.nhnacademy.coupon.entity.CouponForm;
import com.nhnacademy.coupon.entity.CouponType;
import com.nhnacademy.coupon.entity.CouponUsage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 쿠폰폼 서비스 구현체.
 *
 * @author 김병우
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CouponFormServiceImpl implements CouponFormService {
    private final CouponUsageRepository couponUsageRepository;
    private final CouponTypeRepository couponTypeRepository;
    private final CouponFormRepository couponFormRepository;
    private final BookCouponUsageService bookCouponUsageService;
    private final CategoryCouponUsageService categoryCouponUsageService;
    private final FixedCouponService fixedCouponService;
    private final RatioCouponService ratioCouponService;

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private static final String QUEUE_NAME_2 = "3RUNNER-COUPON-EXPIRED-IN-THREE-DAY";
    private static final String QUEUE_NAME_1 = "3RUNNER-COUPON-ISSUED";

    private final CouponRequestPublisher publisher;

    @Override
    public void use(String code) {
        CouponForm couponForm = couponFormRepository.findCouponFormByCode(UUID.fromString(code))
                        .orElseThrow(
                                () -> new CouponFormNotExistException("쿠폰이 존재 하지 않습니다.")
                        );
        couponForm.setQuantity(couponForm.getQuantity() - 1);
        log.info("쿠폰 수량 차감 : {}", couponForm.getQuantity() - 1);
        couponFormRepository.save(couponForm);
    }

    @Override
    public Long create(CreateCouponFormRequest createCouponFormRequest) {
        CouponUsage couponUsage = couponUsageRepository
                .findById(createCouponFormRequest.couponUsageId())
                .orElseThrow(
                        ()-> new CouponUsageDoesNotExistException(createCouponFormRequest.couponUsageId()+"가 존재하지 않습니다.")
                );

        CouponType couponType = couponTypeRepository
                .findById(createCouponFormRequest.couponTypeId())
                .orElseThrow(
                        ()-> new CouponTypeDoesNotExistException(createCouponFormRequest.couponTypeId()+"가 존재하지 않습니다.")
                );

        CouponForm couponForm = new CouponForm();

        couponForm.setBasicDetails(
                createCouponFormRequest.startDate(),
                createCouponFormRequest.endDate(),
                createCouponFormRequest.name(),
                createCouponFormRequest.quantity(),
                UUID.randomUUID(),
                createCouponFormRequest.maxPrice(),
                createCouponFormRequest.minPrice()
        );

        couponForm.setCouponDetails(
                couponType,
                couponUsage
        );

        couponFormRepository.save(couponForm);
        return couponForm.getId();
    }

    @Override
    public void createBatch(CreateCouponFormRequest createCouponFormRequest) {
        publisher.send(createCouponFormRequest);
    }

    @Override
    public CouponForm read(Long couponFormId) {
        return couponFormRepository.findById(couponFormId).orElseThrow(()->new CouponFormNotExistException(""));
    }

    @Override
    public List<ReadCouponFormResponse> readAll(List<Long> couponFormIds) {
        List<CouponForm> couponForms = couponFormRepository.findAllByIdIn(couponFormIds);

        return couponForms.stream()
                .map(couponForm -> ReadCouponFormResponse.builder()
                        .couponFormId(couponForm.getId())
                        .startDate(couponForm.getStartDate())
                        .endDate(couponForm.getEndDate())
                        .createdAt(couponForm.getCreatedAt())
                        .name(couponForm.getName())
                        .code(couponForm.getCode())
                        .maxPrice(couponForm.getMaxPrice())
                        .minPrice(couponForm.getMinPrice())
                        .couponTypeId(couponForm.getCouponType().getId())
                        .couponUsageId(couponForm.getCouponUsage().getId())
                        .usage(couponForm.getCouponUsage().getUsage())
                        .type(couponForm.getCouponType().getType())
                        .books(bookCouponUsageService.readBooks(couponForm.getCouponUsage().getId()))
                        .categorys(categoryCouponUsageService.readCategorys(couponForm.getCouponUsage().getId()))
                        .discountPrice(fixedCouponService.read(couponForm.getCouponType().getId()).discountPrice())
                        .discountRate(ratioCouponService.read(couponForm.getCouponType().getId()).discountRate())
                        .discountMax(ratioCouponService.read(couponForm.getCouponType().getId()).discountMaxPrice())
                        .build())
                .toList();
    }

    @Override
    public List<ReadCouponFormResponse> readAllForms() {
        return couponFormRepository.findAll().stream()
                .map(couponForm -> ReadCouponFormResponse.builder()
                        .couponFormId(couponForm.getId())
                        .startDate(couponForm.getStartDate())
                        .endDate(couponForm.getEndDate())
                        .createdAt(couponForm.getCreatedAt())
                        .name(couponForm.getName())
                        .code(couponForm.getCode())
                        .maxPrice(couponForm.getMaxPrice())
                        .minPrice(couponForm.getMinPrice())
                        .couponTypeId(couponForm.getCouponType().getId())
                        .couponUsageId(couponForm.getCouponUsage().getId())
                        .usage(couponForm.getCouponUsage().getUsage())
                        .type(couponForm.getCouponType().getType())
                        .books(bookCouponUsageService.readBooks(couponForm.getCouponUsage().getId()))
                        .categorys(categoryCouponUsageService.readCategorys(couponForm.getCouponUsage().getId()))
                        .discountPrice(fixedCouponService.read(couponForm.getCouponType().getId()).discountPrice())
                        .discountRate(ratioCouponService.read(couponForm.getCouponType().getId()).discountRate())
                        .discountMax(ratioCouponService.read(couponForm.getCouponType().getId()).discountMaxPrice())
                        .build())
                .toList();
    }

    @Override
    @Async
    @Scheduled(cron = "0 0 13 * * ?")
    public void sendNoticeCouponsExpiringThreeDaysLater() throws JsonProcessingException {
        List<CouponForm> couponsExpiringThreeDaysLater = couponFormRepository.findCouponsExpiringThreeDaysLater();

        List<CouponFormDto> data = couponsExpiringThreeDaysLater.stream().map(o-> CouponFormDto.builder()
                .id(o.getId())
                .name(o.getName())
                .build())
                .toList();

        for (CouponForm couponForm : couponsExpiringThreeDaysLater) {
            log.info(couponForm.getName());
            log.info(String.valueOf(couponForm.getId()));
        }

        rabbitTemplate.convertAndSend(QUEUE_NAME_2, objectMapper.writeValueAsString(data));
    }

}
