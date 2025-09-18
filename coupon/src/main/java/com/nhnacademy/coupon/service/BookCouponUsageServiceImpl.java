package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.repository.BookCouponRepository;
import com.nhnacademy.coupon.adapter.BookControllerClient;
import com.nhnacademy.coupon.dto.adapter.BookForCouponResponse;
import com.nhnacademy.coupon.repository.BookCouponUsageRepository;
import com.nhnacademy.coupon.repository.CouponUsageRespository;
import com.nhnacademy.coupon.entity.BookCoupon;
import com.nhnacademy.coupon.entity.BookCouponUsage;
import com.nhnacademy.coupon.entity.CouponUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 북 쿠폰 사용처 서비스 구현체.
 *
 * @author 김병우
 */
@Transactional
@RequiredArgsConstructor
@Service
public class BookCouponUsageServiceImpl implements BookCouponUsageService {
    private final CouponUsageRespository couponUsageRespository;
    private final BookCouponUsageRepository bookCouponUsageRepository;
    private final BookCouponRepository bookCouponRepository;
    private final BookControllerClient bookControllerClient;

    @Override
    public Long create(List<Long> bookIds) {
        StringBuilder usage = new StringBuilder("사용가능 도서 : ");


        List<BookForCouponResponse> books = bookControllerClient.readAllBooksForCoupon(bookIds).getBody().getData();

        for(BookForCouponResponse book : books){
            usage.append(book.title()).append(",");
        }

        CouponUsage couponUsage = new CouponUsage(usage.toString());
        couponUsageRespository.save(couponUsage);

        for(Long l : bookIds){
            Optional<BookCoupon> bookCouponOptional = bookCouponRepository.findById(l);
            BookCoupon bookCoupon;

            if(bookCouponOptional.isEmpty()){
                bookCoupon = new BookCoupon(l);
                bookCouponRepository.save(bookCoupon);
            } else {
                bookCoupon = bookCouponOptional.get();
            }

            BookCouponUsage bookCouponUsage = new BookCouponUsage(couponUsage, bookCoupon);

            bookCouponUsageRepository.save(bookCouponUsage);
        }

        return couponUsage.getId();
    }

    @Override
    public List<Long> readBooks(Long couponUsageId) {

        return bookCouponUsageRepository.findBookIdsByCouponUsageId(couponUsageId);
    }
}
