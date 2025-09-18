package com.nhnacademy.coupon.dto.adapter;

import lombok.Builder;

@Builder
public record BookForCouponResponse(long id, String title) {
}
