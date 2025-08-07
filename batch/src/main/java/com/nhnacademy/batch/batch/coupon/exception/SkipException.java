package com.nhnacademy.batch.batch.coupon.exception;

public class SkipException extends RuntimeException{
    public SkipException() {
        super("배치 3번 실패 청크 넘어갑니다");
    }
}
