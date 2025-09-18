package com.nhnacademy.batch.repository;

import com.nhnacademy.batch.entity.coupon.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 쿠폰타입 저장소.
 *
 * @author 김병우
 */
public interface CouponTypeRepository extends JpaRepository<CouponType, Long> {
}
