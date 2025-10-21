package com.nhnacademy.batch.exceptionHandler;

public class RetryException extends RuntimeException{
    public RetryException() {
        super("배치 실패 : 재시도");
    }
}
