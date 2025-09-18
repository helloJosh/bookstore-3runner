package com.nhnacademy.bookstore.purchase.service;

import com.nhnacademy.bookstore.entity.coupon.Coupon;
import com.nhnacademy.bookstore.entity.coupon.enums.CouponStatus;
import com.nhnacademy.bookstore.member.member.exception.MemberNotExistsException;
import com.nhnacademy.bookstore.member.member.repository.MemberRepository;
import com.nhnacademy.bookstore.purchase.adapter.CouponControllerClient;
import com.nhnacademy.bookstore.purchase.dto.adapter.request.CreateCouponFormRequest;
import com.nhnacademy.bookstore.purchase.dto.adapter.response.ReadCouponTypeResponse;
import com.nhnacademy.bookstore.purchase.dto.adapter.response.ReadCouponUsageResponse;
import com.nhnacademy.bookstore.purchase.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 맴버 어드민 서비스.
 *
 * @author 김병우
 */
@Transactional
@Service
@RequiredArgsConstructor
public class CouponAdminServiceImpl implements CouponAdminService {
    private final CouponControllerClient couponControllerClient;
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReadCouponTypeResponse> readTypes() {
        return couponControllerClient.readAllTypes().getBody().getData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReadCouponUsageResponse> readUsages() {
        return couponControllerClient.readAllUsages().getBody().getData();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long createCoupon(CreateCouponFormRequest createCouponFormRequest, Long memberId) {
        Long couponFormId = couponControllerClient.createCouponForm(createCouponFormRequest).getBody().getData();

        Coupon coupon = new Coupon(
                couponFormId,
                CouponStatus.READY,
                memberRepository.findById(memberId).orElseThrow(MemberNotExistsException::new)
        );

        couponRepository.save(coupon);

        return coupon.getId();
    }
}
