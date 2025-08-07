package com.nhnacademy.batch.batch.coupon.exception;

public class RetryException extends RuntimeException{
    public RetryException() {
        super("배치 실패 : 재시도");
    }
}
