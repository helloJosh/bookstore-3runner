package com.nhnacademy.front.purchase.purchase.service;

import com.nhnacademy.front.entity.purchase.enums.PurchaseStatus;
import com.nhnacademy.front.purchase.purchase.dto.purchase.response.ReadPurchase;
import com.nhnacademy.front.purchase.purchase.dto.purchase.response.ReadPurchaseBookResponse;
import org.springframework.data.domain.Page;

import java.util.List;


/**
 * 회원 주문조회 service Interface
 *
 * @author 정주혁
 */
public interface PurchaseDetailMemberService {

	Page<ReadPurchase> readPurchases(int page);

	List<ReadPurchaseBookResponse> readPurchaseBookResponses(Long purchaseId);

	PurchaseStatus readPurchaseStatus(Long purchaseId);

	void updatePurchaseStatus(long purchaseId);
}
