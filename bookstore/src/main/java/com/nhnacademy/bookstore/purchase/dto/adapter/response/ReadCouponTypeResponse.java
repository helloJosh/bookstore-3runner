package com.nhnacademy.bookstore.purchase.dto.adapter.response;

import lombok.Builder;

@Builder
public record ReadCouponTypeResponse(Long couponTypeId, String type) {
}
