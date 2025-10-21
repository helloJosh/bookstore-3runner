package com.nhnacademy.batch.repository;

import com.nhnacademy.batch.entity.coupon.CouponType;
import com.nhnacademy.batch.entity.coupon.FixedCoupon;
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
