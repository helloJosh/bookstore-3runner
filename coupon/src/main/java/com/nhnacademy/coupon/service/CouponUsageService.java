package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.dto.response.ReadCouponUsageResponse;

import java.util.List;

public interface CouponUsageService {
    List<ReadCouponUsageResponse> readAll();
}
