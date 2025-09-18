package com.nhnacademy.bookstore.purchase.dto.adapter.response;

import lombok.Builder;

@Builder
public record ReadRatioCouponResponse(
        Long ratioCouponId,
        Long couponTypeId,
        double discountRate,
        int discountMaxPrice) {
}
