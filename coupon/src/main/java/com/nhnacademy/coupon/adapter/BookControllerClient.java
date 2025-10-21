package com.nhnacademy.coupon.adapter;

import com.nhnacademy.coupon.dto.adapter.BookForCouponResponse;
import com.nhnacademy.coupon.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "BookControllerClient", url = "http://${feign.bookstore.url}")
public interface BookControllerClient {
    @GetMapping("bookstore/books/list")
    ApiResponse<List<BookForCouponResponse>> readAllBooksForCoupon(
            @RequestParam("ids") List<Long> ids
    );
}
