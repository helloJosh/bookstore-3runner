package com.nhnacademy.bookstore.purchase.exception;

public class ReadBookCartMemberRequestFormException extends RuntimeException{
	public ReadBookCartMemberRequestFormException() {
		super("조회 request 폼 오류");
	}
}
