package com.nhnacademy.bookstore.purchase.dto.adapter.request;

import lombok.Builder;

import java.util.List;

@Builder
public record ReadCouponFormRequest(
        List<Long> couponFormIds) {
}
