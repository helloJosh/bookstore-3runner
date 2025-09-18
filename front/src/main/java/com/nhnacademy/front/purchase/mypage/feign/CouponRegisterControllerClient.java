package com.nhnacademy.front.purchase.mypage.feign;

import com.nhnacademy.front.purchase.mypage.dto.request.CouponRegistorRequest;
import com.nhnacademy.front.purchase.purchase.dto.coupon.response.ReadCouponFormResponse;
import com.nhnacademy.front.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "CouponMemberControllerClient2", url = "${feign.client.url}")
public interface CouponRegisterControllerClient {
	@PostMapping("/bookstore/members/coupons")
	ApiResponse<Long> registerCoupon(
			@RequestBody CouponRegistorRequest couponRegistorRequest
	);

	@GetMapping("/bookstore/members/coupons")
	ApiResponse<List<ReadCouponFormResponse>> readCoupons();

	@PostMapping("/bookstore/members/coupons/books/{bookId}")
	ApiResponse<Boolean> registerCouponBook(
			@PathVariable("bookId") Long bookId
	);

}