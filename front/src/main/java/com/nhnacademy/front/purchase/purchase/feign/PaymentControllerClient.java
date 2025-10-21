package com.nhnacademy.front.purchase.purchase.feign;

import org.json.simple.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PaymentControllerClient", url = "${feign.client.url}")
public interface PaymentControllerClient {
	@RequestMapping(value = "/bookstore/payments/guests/confirm")
	ResponseEntity<JSONObject> confirmGuestPayment(
		@RequestParam(value = "cartId", required = false) Long cartId,
		@RequestParam(value = "address", required = false) String address,
		@RequestParam(value = "password", required = false) String password,
		@RequestParam(value = "isPacking", required = false) String isPacking,
		@RequestParam(value = "shipping", required = false) String shipping,
		@RequestBody String jsonBody) throws Exception;

	@RequestMapping(value = "/bookstore/payments/members/confirm")
	ResponseEntity<JSONObject> confirmMemberPayment(
		@RequestParam(value = "discountedPrice", required = false) String discountedPrice,
		@RequestParam(value = "discountedPoint", required = false) String discountedPoint,
		@RequestParam(value = "isPacking", required = false) String isPacking,
		@RequestParam(value = "shipping",required = false) String shipping,
		@RequestParam(value = "road",required = false) String road,
		@RequestParam(value = "couponFormId",required = false) Long couponFormId,
		@RequestBody String jsonBody) throws Exception;

}
