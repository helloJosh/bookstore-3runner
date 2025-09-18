package com.nhnacademy.bookstore.purchase.exception;

public class CouponDoesNotExistException extends RuntimeException{
    public CouponDoesNotExistException(String message) {
        super(message);
    }
}
