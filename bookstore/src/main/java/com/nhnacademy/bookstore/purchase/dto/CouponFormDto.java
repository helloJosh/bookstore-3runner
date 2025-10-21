package com.nhnacademy.bookstore.purchase.dto;

import lombok.Builder;

@Builder
public record CouponFormDto(Long id, String name) {
    }
