package com.nhnacademy.coupon.dto.adapter;

import lombok.Builder;

@Builder
public record CategoryForCouponResponse(long id, String name) {
}
