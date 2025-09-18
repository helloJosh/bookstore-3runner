package com.nhnacademy.coupon.exceptionhandler;

public class CouponUsageDoesNotExistException extends RuntimeException{
    public CouponUsageDoesNotExistException(String message) {
        super(message);
    }
}
