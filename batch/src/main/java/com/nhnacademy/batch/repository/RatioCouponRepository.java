package com.nhnacademy.batch.repository;

import com.nhnacademy.batch.entity.coupon.CouponType;
import com.nhnacademy.batch.entity.coupon.RatioCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * 비율 쿠폰 JPA 저장소 인터페이스.
 *
 * @author 김병우
 */
public interface RatioCouponRepository extends JpaRepository<RatioCoupon, Long> {
    Optional<RatioCoupon> findByCouponType(CouponType couponType);
}
