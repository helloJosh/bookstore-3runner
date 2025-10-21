package com.nhnacademy.coupon.config;

import com.nhnacademy.coupon.entity.*;
import com.nhnacademy.coupon.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitConfig {
    private final CouponUsageRepository couponUsageRepository;
    private final CouponTypeRepository couponTypeRepository;

    private final CategoryCouponRepository categoryCouponRepository;
    private final CategoryCouponUsageRepository categoryCouponUsageRepository;

    private final FixedCouponRepository fixedCouponRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (false){
            CouponUsage couponUsage = new CouponUsage("category 1 usage");
            couponUsage = couponUsageRepository.save(couponUsage);
            CategoryCoupon categoryCoupon = new CategoryCoupon(1L);
            categoryCoupon = categoryCouponRepository.save(categoryCoupon);
            CategoryCouponUsage categoryCouponUsage = new CategoryCouponUsage(couponUsage, categoryCoupon);
            categoryCouponUsage = categoryCouponUsageRepository.save(categoryCouponUsage);

            CouponType couponType = new CouponType("10000won discount");
            couponType = couponTypeRepository.save(couponType);
            FixedCoupon fixedCoupon = new FixedCoupon(couponType.getId(), 10000);
            fixedCoupon = fixedCouponRepository.save(fixedCoupon);
        }
    }
}
