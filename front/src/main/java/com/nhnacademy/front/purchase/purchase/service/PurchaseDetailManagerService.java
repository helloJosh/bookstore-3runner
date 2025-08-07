package com.nhnacademy.front.purchase.purchase.service;

import com.nhnacademy.front.purchase.purchase.dto.purchase.response.ReadPurchaseResponse;
import org.springframework.data.domain.Page;

/**
 * 관리자 주문조회 service Interface
 *
 * @author 정주혁
 */
public interface PurchaseDetailManagerService {
	Page<ReadPurchaseResponse> readPurchase(int size,int page, String sort);

	Long updatePurchaseStatus(String purchase,String status);
}
