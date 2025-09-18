package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.dto.response.ReadCouponTypeResponse;
import com.nhnacademy.coupon.repository.CouponTypeRepository;
import com.nhnacademy.coupon.entity.CouponType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 쿠폰타입 서비스 구현체.
 *
 * @author 김병우
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CouponTypeServiceImpl implements CouponTypeService {
    private final CouponTypeRepository couponTypeRepository;

    @Override
    public List<ReadCouponTypeResponse> readAll() {
        return couponTypeRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    private ReadCouponTypeResponse convertToResponse(CouponType couponType) {
        return ReadCouponTypeResponse.builder()
                .couponTypeId(couponType.getId())
                .type(couponType.getType())
                .build();
    }
}
