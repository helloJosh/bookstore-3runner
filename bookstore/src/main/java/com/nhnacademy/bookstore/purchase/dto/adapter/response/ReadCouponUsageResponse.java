package com.nhnacademy.bookstore.purchase.dto.adapter.response;

import lombok.Builder;

@Builder
public record ReadCouponUsageResponse(Long couponUsageId, String usage) {
}
