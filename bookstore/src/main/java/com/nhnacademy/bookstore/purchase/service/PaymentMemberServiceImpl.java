package com.nhnacademy.bookstore.purchase.service;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.payment.Payment;
import com.nhnacademy.bookstore.entity.payment.enums.PaymentStatus;
import com.nhnacademy.bookstore.entity.pointpolicy.PointPolicy;
import com.nhnacademy.bookstore.member.pointrecord.service.PointRecordService;
import com.nhnacademy.bookstore.purchase.dto.request.ReadAllBookCartMemberRequest;
import com.nhnacademy.bookstore.purchase.dto.response.ReadAllBookCartMemberResponse;
import com.nhnacademy.bookstore.purchase.dto.request.CreatePaymentMemberRequest;
import com.nhnacademy.bookstore.purchase.repository.PaymentRepository;
import com.nhnacademy.bookstore.purchase.exception.PointPolicyDoesNotExistException;
import com.nhnacademy.bookstore.purchase.repository.PointPolicyRepository;
import com.nhnacademy.bookstore.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.exception.PurchaseDoesNotExistException;
import com.nhnacademy.bookstore.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.dto.request.CreatePurchaseBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class PaymentMemberServiceImpl implements PaymentMemberService {
    private final BookCartMemberService bookCartMemberService;
    private final PurchaseMemberService purchaseMemberService;
    private final PurchaseBookService purchaseBookService;
    private final PointRecordService pointRecordService;
    private final PurchaseCouponService purchaseCouponService;
    private final CouponMemberService couponMemberService;
    private final PaymentRepository paymentRepository;
    private final PurchaseRepository purchaseRepository;
    private final BookRepository bookRepository;
    private final PointPolicyRepository pointPolicyRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long payment(CreatePaymentMemberRequest createPaymentMemberRequest) {
        PointPolicy pointPolicy = pointPolicyRepository
                .findByPolicyName("적립률").orElseThrow(()->new PointPolicyDoesNotExistException("포인트 정책이 없습니다"));
        final double POINT_RATE = 0.01 * pointPolicy.getPolicyValue();


        Long purchaseId = purchaseMemberService.createPurchase(
                CreatePurchaseRequest.builder()
                        .orderId(createPaymentMemberRequest.orderId())
                        .road(createPaymentMemberRequest.road())
                        .totalPrice(createPaymentMemberRequest.amount())
                        .shippingDate(createPaymentMemberRequest.shippingDate())
                        .isPacking(createPaymentMemberRequest.isPacking())
                        .deliveryPrice(3000).build(),
                createPaymentMemberRequest.memberId()
        );


        paymentRepository.save(
                new Payment(
                        createPaymentMemberRequest.orderId(),
                        createPaymentMemberRequest.amount(),
                        0,
                        createPaymentMemberRequest.paymentKey(),
                        PaymentStatus.SUCCESS,
                        purchaseRepository.findById(purchaseId).orElseThrow(()->new PurchaseDoesNotExistException("주문이 없습니다."))
                )
        );

        List<ReadAllBookCartMemberResponse> bookCartMemberResponseList = bookCartMemberService.readAllCartMember(ReadAllBookCartMemberRequest.builder().userId(createPaymentMemberRequest.memberId()).build());

        for (ReadAllBookCartMemberResponse bookCartMemberResponse : bookCartMemberResponseList) {
            purchaseBookService.createPurchaseBook(
                    CreatePurchaseBookRequest.builder()
                            .bookId(bookCartMemberResponse.bookId())
                            .purchaseId(purchaseId)
                            .price(bookCartMemberResponse.price())
                            .quantity(bookCartMemberResponse.quantity())
                            .build()
            );

            Book book = bookRepository.findById(bookCartMemberResponse.bookId())
                    .orElseThrow(()-> new BookDoesNotExistException("북아이디 : " + bookCartMemberResponse.bookId() + "가 없습니다."));

            if (book.getQuantity() - bookCartMemberResponse.quantity() >= 0 ) {
                book.setQuantity(book.getQuantity()-bookCartMemberResponse.quantity());
            } else {
                throw new RuntimeException("북아이디 : " + bookCartMemberResponse.bookId() + "가 현재 재고가 없습니다.");
            }

            bookRepository.save(book);
        }


        pointRecordService.save(
                (long)(createPaymentMemberRequest.amount() * POINT_RATE),
                createPaymentMemberRequest.orderId() + " : 주문 적립",
                createPaymentMemberRequest.memberId(),
                purchaseId
        );

        if (createPaymentMemberRequest.discountedPoint() != 0) {
            pointRecordService.save(
                    -1L * createPaymentMemberRequest.discountedPoint(),
                    createPaymentMemberRequest.orderId() + " : 주문 사용",
                    createPaymentMemberRequest.memberId(),
                    purchaseId
            );
        }

        //쿠폰 사용
        if (createPaymentMemberRequest.couponFormId() != 0) {
//            Long couponId = couponMemberService
//                    .readCoupon(createPaymentMemberRequest.couponFormId());

            couponMemberService.useCoupons(
                    createPaymentMemberRequest.couponFormId(),
                    createPaymentMemberRequest.memberId()
            );

            purchaseCouponService.create(
                    purchaseId,
                    createPaymentMemberRequest.couponFormId(),
                    createPaymentMemberRequest.discountedPrice()
            );
        }

        bookCartMemberService.deleteAllBookCart(createPaymentMemberRequest.memberId());

        return purchaseId;
    }
}
