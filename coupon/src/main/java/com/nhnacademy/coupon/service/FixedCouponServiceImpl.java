package com.nhnacademy.coupon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.coupon.exceptionhandler.CouponTypeDoesNotExistException;
import com.nhnacademy.coupon.repository.CouponTypeRepository;
import com.nhnacademy.coupon.dto.response.ReadFixedCouponResponse;
import com.nhnacademy.coupon.repository.FixedCouponRepository;
import com.nhnacademy.coupon.entity.CouponType;
import com.nhnacademy.coupon.entity.FixedCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 고정 쿠폰 서비스 구현체.
 *
 * @author 김병우
 */
@Transactional
@Service
@RequiredArgsConstructor
public class FixedCouponServiceImpl implements FixedCouponService {
    private final ObjectMapper objectMapper;
    private final FixedCouponRepository fixedCouponRepository;
    private final CouponTypeRepository couponTypeRepository;

    @Override
    public Long create(int discountPrice) {
        String type = "고정할인 : "+discountPrice;
        CouponType couponType = new CouponType(type);
        couponTypeRepository.save(couponType);

        FixedCoupon fixedCoupon = new FixedCoupon(couponType.getId(), discountPrice);
        fixedCouponRepository.save(fixedCoupon);

        return couponType.getId();
    }

    @Override
    public ReadFixedCouponResponse read(Long couponTypeId) {
        CouponType couponType = couponTypeRepository
                .findById(couponTypeId)
                .orElseThrow(()-> new CouponTypeDoesNotExistException(couponTypeId+"가 없습니다"));

        Optional<FixedCoupon> fixedCouponOptional = fixedCouponRepository
                .findByCouponTypeId(couponType.getId());

        FixedCoupon fixedCoupon = fixedCouponOptional.orElseGet(() -> new FixedCoupon(couponType.getId(), 0));

        return ReadFixedCouponResponse.builder()
                .fixedCouponId(fixedCoupon.getId())
                .couponTypeId(couponTypeId)
                .discountPrice(fixedCoupon.getDiscountPrice())
                .build();
    }
}
