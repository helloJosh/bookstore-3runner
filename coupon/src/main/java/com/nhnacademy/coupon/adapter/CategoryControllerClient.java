package com.nhnacademy.coupon.adapter;

import com.nhnacademy.coupon.dto.adapter.CategoryForCouponResponse;
import com.nhnacademy.coupon.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "CategoryControllerClient", url = "http://${feign.bookstore.url}")
public interface CategoryControllerClient {

    @GetMapping("bookstore/categories/list")
    ApiResponse<List<CategoryForCouponResponse>> readAllCategoriesList(@RequestParam List<Long> ids);
}
