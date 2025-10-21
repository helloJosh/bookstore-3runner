package com.nhnacademy.bookstore.purchase.exception;

public class CouponDuplicatedRegisterException extends RuntimeException{
    public CouponDuplicatedRegisterException(String message) {
        super(message);
    }
}
