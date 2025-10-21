package com.nhnacademy.coupon.exceptionhandler;

public class CouponTypeDoesNotExistException extends RuntimeException{
    public CouponTypeDoesNotExistException(String message) {
        super(message);
    }
}
