package com.nhnacademy.bookstore.purchase.dto.adapter.request;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateCategoryCouponRequest(List<Long> categoryIds) {
}
