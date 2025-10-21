package com.nhnacademy.bookstore.purchase.exception;

public class CartDoesNotExistException extends RuntimeException{
    public CartDoesNotExistException(String message) {
        super(message);
    }
}
