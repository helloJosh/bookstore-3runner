package com.nhnacademy.bookstore.purchase.dto.response;

import lombok.Builder;

/**
 * 환불 내역 response dto
 *
 * @author 정주혁
 *
 * @param id
 * @param quantity
 * @param price
 * @param readBookByPurchase
 */
@Builder
public record ReadRefundRecordResponse(long id, int quantity, int price, ReadBookByPurchase readBookByPurchase) {
}
