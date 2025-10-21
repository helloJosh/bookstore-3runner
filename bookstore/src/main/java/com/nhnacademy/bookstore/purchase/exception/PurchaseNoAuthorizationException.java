package com.nhnacademy.bookstore.purchase.exception;

public class PurchaseNoAuthorizationException extends RuntimeException{
    public PurchaseNoAuthorizationException(String message) {
        super(message);
    }
}
