package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.dto.response.ReadRatioCouponResponse;

/**
 * 비율 쿠폰 서비스 인터페이스
 *
 * @author 김병우
 */
public interface RatioCouponService {

    /**
     * 비율 쿠폰 생성.
     *
     * @param discountRate 할인비율
     * @param discountMaxPrice 최대할인가격
     * @return 쿠폰타입 아이디
     */
    Long create(double discountRate, int discountMaxPrice);

    /**
     * 비율 쿠폰 읽기.
     *
     * @param couponTypeId 쿠폰타입아이디
     * @return 반환Dto
     */
    ReadRatioCouponResponse read(Long couponTypeId);
}
