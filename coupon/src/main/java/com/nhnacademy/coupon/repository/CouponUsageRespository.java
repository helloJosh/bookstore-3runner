package com.nhnacademy.coupon.repository;

import com.nhnacademy.coupon.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUsageRespository extends JpaRepository<CouponUsage, Long> {
}
