package com.nhnacademy.coupon.repository;

import com.nhnacademy.coupon.entity.CouponType;
import com.nhnacademy.coupon.entity.FixedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 고정쿠폰 JPA 저장소.
 *
 * @author 김병우
 */
public interface FixedCouponRepository extends JpaRepository<FixedCoupon, Long> {
    Optional<FixedCoupon> findByCouponTypeId(Long couponTypeId);
}
