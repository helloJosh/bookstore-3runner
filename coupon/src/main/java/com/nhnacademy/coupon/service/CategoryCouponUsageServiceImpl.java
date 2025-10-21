package com.nhnacademy.coupon.service;


import com.nhnacademy.coupon.adapter.CategoryControllerClient;
import com.nhnacademy.coupon.dto.adapter.CategoryForCouponResponse;
import com.nhnacademy.coupon.entity.CategoryCoupon;
import com.nhnacademy.coupon.entity.CategoryCouponUsage;
import com.nhnacademy.coupon.entity.CouponUsage;
import com.nhnacademy.coupon.repository.CategoryCouponRepository;
import com.nhnacademy.coupon.repository.CategoryCouponUsageRepository;
import com.nhnacademy.coupon.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 쿠폰 사용처 서비스 구현체.
 *
 * @author 김병우
 */
@Transactional
@Service
@RequiredArgsConstructor
public class CategoryCouponUsageServiceImpl implements CategoryCouponUsageService {
    private final CouponUsageRepository couponUsageRespository;
    private final CategoryCouponUsageRepository categoryCouponUsageRepository;
    private final CategoryCouponRepository categoryCouponRepository;
    private final CategoryControllerClient categoryControllerClient;

    @Override
    public Long create(List<Long> categoryIds) {
        StringBuilder usage = new StringBuilder("사용가능 카테고리 : ");

        List<CategoryForCouponResponse> categorys = categoryControllerClient.readAllCategoriesList(categoryIds).getBody().getData();

        for(CategoryForCouponResponse category : categorys){
            usage.append(category.name()).append(",");
        }

        CouponUsage couponUsage = new CouponUsage(usage.toString());
        couponUsageRespository.save(couponUsage);

        for (Long l : categoryIds) {
            Optional<CategoryCoupon> categoryCouponOptional = categoryCouponRepository.findById(l);
            CategoryCoupon categoryCoupon;

            if(categoryCouponOptional.isEmpty()){
                categoryCoupon = new CategoryCoupon(l);
                categoryCouponRepository.save(categoryCoupon);
            } else {
                categoryCoupon = categoryCouponOptional.get();
            }

            CategoryCouponUsage categoryCouponUsage = new CategoryCouponUsage(couponUsage, categoryCoupon);

            categoryCouponUsageRepository.save(categoryCouponUsage);
        }

        return couponUsage.getId();
    }

    @Override
    public List<Long> readCategorys(Long couponUsageId) {
        return categoryCouponUsageRepository.findCategoryIdsByCouponUsageId(couponUsageId);
    }
}
