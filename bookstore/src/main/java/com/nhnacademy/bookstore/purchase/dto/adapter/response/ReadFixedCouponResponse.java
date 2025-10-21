package com.nhnacademy.bookstore.purchase.dto.adapter.response;

import lombok.Builder;

@Builder
public record ReadFixedCouponResponse(
        Long fixedCouponId,
        Long couponTypeId,
        int discountPrice) {
}
