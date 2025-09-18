package com.nhnacademy.bookstore.purchase.dto.adapter.request;

import lombok.Builder;

@Builder
public record CreateFixedCouponRequest(int discountPrice) {
}
