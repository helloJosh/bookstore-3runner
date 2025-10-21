package com.nhnacademy.bookstore.purchase.exception;

public class BookCartArgumentErrorException extends RuntimeException{
    public BookCartArgumentErrorException(String message) {
        super(message);
    }
}
