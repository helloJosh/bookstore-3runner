package com.nhnacademy.batch.repository;

import com.nhnacademy.batch.entity.coupon.CategoryCouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryCouponUsageRepository extends JpaRepository<CategoryCouponUsage, Long> {
    @Query("SELECT c.categoryCoupon.categoryId FROM CategoryCouponUsage c WHERE c.couponUsage.id = :couponUsageId")
    List<Long> findCategoryIdsByCouponUsageId(@Param("couponUsageId") Long couponUsageId);
}
