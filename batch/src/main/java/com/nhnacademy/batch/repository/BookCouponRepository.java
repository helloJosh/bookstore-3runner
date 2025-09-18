package com.nhnacademy.batch.repository;

import com.nhnacademy.batch.entity.coupon.BookCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
}
