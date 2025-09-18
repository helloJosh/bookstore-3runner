package com.nhnacademy.coupon.repository;

import com.nhnacademy.coupon.entity.BookCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCouponRepository extends JpaRepository<BookCoupon, Long> {
}
