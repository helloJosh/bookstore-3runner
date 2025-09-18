package com.nhnacademy.bookstore.purchase.exception;

public class PointPolicyDoesNotExistException extends RuntimeException{
    public PointPolicyDoesNotExistException(String message) {
        super(message);
    }
}
