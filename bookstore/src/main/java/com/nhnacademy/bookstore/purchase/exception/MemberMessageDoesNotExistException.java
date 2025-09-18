package com.nhnacademy.bookstore.purchase.exception;

public class MemberMessageDoesNotExistException extends RuntimeException{
    public MemberMessageDoesNotExistException(String message) {
        super(message);
    }
}
