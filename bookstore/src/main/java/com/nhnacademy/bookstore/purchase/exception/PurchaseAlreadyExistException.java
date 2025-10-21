package com.nhnacademy.bookstore.purchase.exception;

public class PurchaseAlreadyExistException extends RuntimeException{
    public PurchaseAlreadyExistException(String message) {
        super(message);
    }
}
