package com.nhnacademy.bookstore.purchase.payment.service.impl;

import com.nhnacademy.bookstore.book.book.exception.BookDoesNotExistException;
import com.nhnacademy.bookstore.book.book.repository.BookRepository;
import com.nhnacademy.bookstore.entity.book.Book;
import com.nhnacademy.bookstore.entity.payment.Payment;
import com.nhnacademy.bookstore.entity.purchase.Purchase;
import com.nhnacademy.bookstore.purchase.dto.response.ReadBookCartGuestResponse;
import com.nhnacademy.bookstore.purchase.service.BookCartGuestService;
import com.nhnacademy.bookstore.purchase.dto.request.CreatePaymentGuestRequest;
import com.nhnacademy.bookstore.purchase.repository.PaymentRepository;
import com.nhnacademy.bookstore.purchase.service.PaymentGuestServiceImpl;
import com.nhnacademy.bookstore.purchase.dto.request.CreatePurchaseRequest;
import com.nhnacademy.bookstore.purchase.repository.PurchaseRepository;
import com.nhnacademy.bookstore.purchase.service.PurchaseGuestService;
import com.nhnacademy.bookstore.purchase.dto.request.CreatePurchaseBookRequest;
import com.nhnacademy.bookstore.purchase.service.PurchaseBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PaymentGuestServiceImplTest {

    @InjectMocks
    private PaymentGuestServiceImpl paymentGuestService;

    @Mock
    private BookCartGuestService bookCartGuestService;

    @Mock
    private PurchaseGuestService purchaseGuestService;

    @Mock
    private PurchaseBookService purchaseBookService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPayment() {
        CreatePaymentGuestRequest request = CreatePaymentGuestRequest.builder()
                .orderId("orderId")
                .road("road")
                .password("password")
                .isPacking(true)
                .shippingDate(ZonedDateTime.now())
                .amount(10000)
                .paymentKey("paymentKey")
                .cartId(1L)
                .build();

        Long purchaseId = 1L;
        given(purchaseGuestService.createPurchase(any(CreatePurchaseRequest.class))).willReturn(purchaseId);
        given(purchaseRepository.findById(purchaseId)).willReturn(Optional.of(mock(Purchase.class)));
        List<ReadBookCartGuestResponse> bookCartResponses = List.of(
                new ReadBookCartGuestResponse(1L, 1L, 1000, "url", "title", 2, 10)
        );
        given(bookCartGuestService.readAllBookCart(request.cartId())).willReturn(bookCartResponses);

        Book book = new Book();
        book.setId(1L);
        book.setQuantity(10);
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));

        Long result = paymentGuestService.payment(request);

        assertEquals(purchaseId, result);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(purchaseBookService, times(1)).createPurchaseBook(any(CreatePurchaseBookRequest.class));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testPayment_BookDoesNotExist() {
        CreatePaymentGuestRequest request = CreatePaymentGuestRequest.builder()
                .orderId("orderId")
                .road("road")
                .password("password")
                .isPacking(true)
                .shippingDate(ZonedDateTime.now())
                .amount(10000)
                .paymentKey("paymentKey")
                .cartId(1L)
                .build();

        Long purchaseId = 1L;
        given(purchaseGuestService.createPurchase(any(CreatePurchaseRequest.class))).willReturn(purchaseId);
        given(purchaseRepository.findById(purchaseId)).willReturn(Optional.of(mock(Purchase.class)));
        List<ReadBookCartGuestResponse> bookCartResponses = List.of(
                new ReadBookCartGuestResponse(1L, 1L, 1000, "url", "title", 2, 10)
        );
        given(bookCartGuestService.readAllBookCart(request.cartId())).willReturn(bookCartResponses);

        given(bookRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(BookDoesNotExistException.class, () -> paymentGuestService.payment(request));
    }

    @Test
    void testPayment_InsufficientStock() {
        CreatePaymentGuestRequest request = CreatePaymentGuestRequest.builder()
                .orderId("orderId")
                .road("road")
                .password("password")
                .isPacking(true)
                .shippingDate(ZonedDateTime.now())
                .amount(10000)
                .paymentKey("paymentKey")
                .cartId(1L)
                .build();

        Long purchaseId = 1L;
        given(purchaseGuestService.createPurchase(any(CreatePurchaseRequest.class))).willReturn(purchaseId);
        given(purchaseRepository.findById(purchaseId)).willReturn(Optional.of(mock(Purchase.class)));
        List<ReadBookCartGuestResponse> bookCartResponses = List.of(
                new ReadBookCartGuestResponse(1L, 1L, 1000, "url", "title", 11, 10)
        );
        given(bookCartGuestService.readAllBookCart(request.cartId())).willReturn(bookCartResponses);

        Book book = new Book();
        book.setId(1L);
        book.setQuantity(10);
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));

        assertThrows(RuntimeException.class, () -> paymentGuestService.payment(request));
    }
}