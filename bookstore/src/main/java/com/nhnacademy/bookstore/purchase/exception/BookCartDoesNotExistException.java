package com.nhnacademy.bookstore.purchase.exception;

public class BookCartDoesNotExistException extends RuntimeException{
    public BookCartDoesNotExistException(String message) {
        super(message);
    }
}
