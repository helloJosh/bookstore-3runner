package com.nhnacademy.batch.repository;

import com.nhnacademy.batch.entity.coupon.CategoryCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {
}
