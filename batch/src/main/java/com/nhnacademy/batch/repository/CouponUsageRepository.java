package com.nhnacademy.batch.repository;

import com.nhnacademy.batch.entity.coupon.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
}
