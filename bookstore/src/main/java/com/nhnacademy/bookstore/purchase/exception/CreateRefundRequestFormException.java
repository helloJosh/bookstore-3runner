package com.nhnacademy.bookstore.purchase.exception;

public class CreateRefundRequestFormException extends RuntimeException {
	public CreateRefundRequestFormException() {
		super("환불 생성, 잘못된 값");
	}
}
