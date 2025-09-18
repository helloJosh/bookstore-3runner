package com.nhnacademy.coupon.repository;

import com.nhnacademy.coupon.entity.CategoryCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryCouponRepository extends JpaRepository<CategoryCoupon, Long> {
}
