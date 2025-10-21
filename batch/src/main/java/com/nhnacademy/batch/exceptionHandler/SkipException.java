package com.nhnacademy.batch.exceptionHandler;

public class SkipException extends RuntimeException{
    public SkipException() {
        super("배치 3번 실패 청크 넘어갑니다");
    }
}
