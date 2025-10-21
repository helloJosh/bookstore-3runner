package com.nhnacademy.bookstore.purchase.dto.adapter.request;

import lombok.Builder;

@Builder
public record CreateRatioCouponRequest(double discountRate, int discountMaxPrice) {
}
