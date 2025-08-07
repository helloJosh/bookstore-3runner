package com.nhnacademy.front.refund.feign;

import com.nhnacademy.front.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "refundRecordMemberControllerClient", url = "${feign.client.url}")
public interface RefundRecordMemberControllerClient {

	@PostMapping("/bookstore/refundRecord/members/{purchaseBookId}")
	ApiResponse<Long> createRefundRecordMemberRedis(
		@PathVariable(name = "purchaseBookId") Long purchaseBookId);

	@PutMapping("/bookstore/refundRecord/members/{purchaseBookId}")
	ApiResponse<Long> updateRefundRecordMember(
		@RequestParam(name = "orderNumber") Long orderNumber,
		@PathVariable(name = "purchaseBookId") Long purchaseBookId,
		@RequestParam(name = "quantity") int quantity);

	@DeleteMapping("/bookstore/refundRecord/members/{purchaseBookId}")
	ApiResponse<Long> deleteRefundRecordMember(
		@PathVariable(name = "purchaseBookId") Long purchaseBookId
	);

	@PostMapping("/bookstore/refundRecord/members/save/{refundId}")
	ApiResponse<Boolean> createRefundRecordMember(
		@RequestParam(name = "orderNumber") Long orderNumber,
		@PathVariable(name = "refundId") Long refundId
	);

	@PutMapping("/bookstore/refundRecord/members/all/{orderNumber}")
	ApiResponse<Long> updateRefundRecordAllMember(
		@PathVariable(name = "orderNumber") Long orderNumber
	);

	@PutMapping("/bookstore/refundRecord/members/all/zero/{orderNumber}")
	ApiResponse<Long> updateRefundRecordAllZeroMember(
		@PathVariable(name = "orderNumber") Long orderNumber
	);
}
