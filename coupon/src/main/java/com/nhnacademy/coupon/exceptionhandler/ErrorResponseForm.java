package com.nhnacademy.coupon.exceptionhandler;

import lombok.Builder;

@Builder
public record ErrorResponseForm(String title, int status, String timestamp) {
}