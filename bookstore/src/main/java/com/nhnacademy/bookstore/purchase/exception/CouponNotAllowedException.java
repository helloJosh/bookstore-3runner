package com.nhnacademy.bookstore.purchase.exception;

public class CouponNotAllowedException extends RuntimeException{
    public CouponNotAllowedException(String message) {
        super(message);
    }
}
