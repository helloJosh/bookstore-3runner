package com.nhnacademy.bookstore.purchase.exception;

public class PurchaseFormArgumentErrorException extends RuntimeException{
    public PurchaseFormArgumentErrorException(String message) {
        super(message);
    }
}
