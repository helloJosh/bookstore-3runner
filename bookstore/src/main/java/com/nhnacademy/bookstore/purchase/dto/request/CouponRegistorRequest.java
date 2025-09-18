package com.nhnacademy.bookstore.purchase.dto.request;

import lombok.Builder;

@Builder
public record CouponRegistorRequest(String code) {
    }
