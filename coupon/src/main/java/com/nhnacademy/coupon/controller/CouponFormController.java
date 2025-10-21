package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.dto.request.CreateCouponFormRequest;
import com.nhnacademy.coupon.dto.response.ReadCouponFormResponse;
import com.nhnacademy.coupon.dto.request.ReadCouponFormRequest;
import com.nhnacademy.coupon.service.CouponFormService;
import com.nhnacademy.coupon.util.ApiResponse;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 쿠폰 폼 컨트롤러.
 *
 * @author 김병우
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponFormController {
    private final CouponFormService couponFormService;

    @PostMapping("/members/forms")
    public ApiResponse<List<ReadCouponFormResponse>> readCouponForm(
            @RequestBody ReadCouponFormRequest readCouponFormRequest
    ) {
        return ApiResponse.success(couponFormService.readAll(readCouponFormRequest.couponFormIds()));
    }

    @PostMapping("/forms")
    public ApiResponse<Long> createCouponForm (
            @RequestBody CreateCouponFormRequest createCouponFormRequest
    ) {
        return ApiResponse.createSuccess(couponFormService.create(createCouponFormRequest));
    }

    @PostMapping("/forms-batch")
    public ApiResponse<Long> createCouponFormBatch (
            @RequestBody CreateCouponFormRequest createCouponFormRequest
    ) {
        log.info("create coupon batch request");
        couponFormService.createBatch(createCouponFormRequest);
        return ApiResponse.success(0L
        );
    }



    @GetMapping("/forms")
    public ApiResponse<List<ReadCouponFormResponse>> readAllCouponForms() {
        return ApiResponse.success(couponFormService.readAllForms());
    }
}
