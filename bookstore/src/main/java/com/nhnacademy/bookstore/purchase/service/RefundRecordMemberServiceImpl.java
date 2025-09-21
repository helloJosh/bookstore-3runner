package com.nhnacademy.bookstore.purchase.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nhnacademy.bookstore.entity.purchasebook.PurchaseBook;
import com.nhnacademy.bookstore.entity.refund.Refund;
import com.nhnacademy.bookstore.entity.refundrecord.RefundRecord;
import com.nhnacademy.bookstore.purchase.dto.response.ReadBookByPurchase;
import com.nhnacademy.bookstore.purchase.dto.response.ReadPurchaseBookResponse;
import com.nhnacademy.bookstore.purchase.exception.NotExistsPurchaseBook;
import com.nhnacademy.bookstore.purchase.repository.PurchaseBookCustomRepository;
import com.nhnacademy.bookstore.purchase.repository.PurchaseBookRepository;
import com.nhnacademy.bookstore.purchase.repository.RefundRepository;
import com.nhnacademy.bookstore.purchase.dto.response.ReadRefundRecordResponse;
import com.nhnacademy.bookstore.purchase.exception.AlreadyExistsRefundRecordRedis;
import com.nhnacademy.bookstore.purchase.exception.NotExistsRefundRecordRedis;
import com.nhnacademy.bookstore.purchase.repository.RefundRecordRedisRepository;
import com.nhnacademy.bookstore.purchase.repository.RefundRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundRecordMemberServiceImpl implements RefundRecordMemberService {

	private final RefundRecordRepository refundRecordRepository;
	private final RefundRecordRedisRepository refundRecordRedisRepository;
	private final PurchaseBookRepository purchaseBookRepository;
	private final RefundRepository refundRepository;
	private final PurchaseBookCustomRepository purchaseBookCustomRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long createRefundRecordRedis(Long orderNumber, Long purchaseBookId, int price, int quantity,
		ReadBookByPurchase readBookByPurchase) {
		if (refundRecordRedisRepository.detailIsHit("Refund_member " + orderNumber, purchaseBookId)) {
			throw new AlreadyExistsRefundRecordRedis();
		}
		ReadRefundRecordResponse readRefundRecordResponse = ReadRefundRecordResponse.builder()
			.readBookByPurchase(readBookByPurchase)
			.id(purchaseBookId)
			.price(price)
			.quantity(quantity)
			.build();
		return refundRecordRedisRepository.create("Refund_member " + orderNumber, purchaseBookId, readRefundRecordResponse);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long createRefundRecordAllRedis(Long orderNumber) {
		if (refundRecordRedisRepository.isHit("Refund_member " + orderNumber)) {
			throw new AlreadyExistsRefundRecordRedis();
		}
		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readBookPurchaseResponses(orderNumber);
		for (ReadPurchaseBookResponse readPurchaseBookResponse : responses) {
			createRefundRecordRedis(orderNumber, readPurchaseBookResponse.id(),
				readPurchaseBookResponse.price(),
				readPurchaseBookResponse.quantity(), readPurchaseBookResponse.readBookByPurchase());
		}
		return orderNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long updateRefundRecordRedis(Long orderNumber, Long purchaseBookId, int quantity) {
		ReadPurchaseBookResponse purchaseBook = purchaseBookCustomRepository.readPurchaseBookResponse(
			purchaseBookId);
		if (!refundRecordRedisRepository.detailIsHit("Refund_member " + orderNumber, purchaseBookId)) {

			return createRefundRecordRedis(orderNumber, purchaseBookId,
				(purchaseBook.price() / purchaseBook.quantity()) * quantity, quantity,
				purchaseBook.readBookByPurchase());

		}
		return refundRecordRedisRepository.update("Refund_member " + orderNumber, purchaseBookId, quantity,
			purchaseBook.price() / purchaseBook.quantity());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long updateRefundRecordAllRedis(Long orderNumber) {
		if (!refundRecordRedisRepository.isHit("Refund_member " + orderNumber)) {
			return createRefundRecordAllRedis(orderNumber);
		}

		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readBookPurchaseResponses(orderNumber);
		for (ReadPurchaseBookResponse readPurchaseBookResponse : responses) {
			if (!refundRecordRedisRepository.detailIsHit("Refund_member " + orderNumber, readPurchaseBookResponse.id())) {
				createRefundRecordRedis(orderNumber, readPurchaseBookResponse.id(),
					readPurchaseBookResponse.price(),
					readPurchaseBookResponse.quantity(), readPurchaseBookResponse.readBookByPurchase());
			} else {
				PurchaseBook purchaseBook = purchaseBookRepository.findById(readPurchaseBookResponse.id()).orElse(null);
				refundRecordRedisRepository.update("Refund_member " + orderNumber, readPurchaseBookResponse.id(),
					readPurchaseBookResponse.quantity(), purchaseBook.getPrice() / purchaseBook.getQuantity());
			}
		}
		return orderNumber;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long updateRefundRecordZeroAllRedis(Long orderNumber) {
		if (!refundRecordRedisRepository.isHit("Refund_member " + orderNumber)) {
			return createRefundRecordAllRedis(orderNumber);
		}
		List<ReadPurchaseBookResponse> responses = purchaseBookCustomRepository.readBookPurchaseResponses(orderNumber);
		for (ReadPurchaseBookResponse readPurchaseBookResponse : responses) {
			if (!refundRecordRedisRepository.detailIsHit("Refund_member " + orderNumber, readPurchaseBookResponse.id())) {
				createRefundRecordRedis(orderNumber, readPurchaseBookResponse.id(),
					0,
					0, readPurchaseBookResponse.readBookByPurchase());
			} else {
				refundRecordRedisRepository.update("Refund_member " + orderNumber, readPurchaseBookResponse.id(),
					0, 0);
			}
		}
		return orderNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long deleteRefundRecordRedis(Long orderNumber, Long purchaseBookId) {
		if (!refundRecordRedisRepository.isHit("Refund_member " + orderNumber)) {
			throw new NotExistsRefundRecordRedis();
		}
		return refundRecordRedisRepository.delete("Refund_member " + orderNumber, purchaseBookId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ReadRefundRecordResponse> readRefundRecordRedis(Long orderNumber) {
		if (!refundRecordRedisRepository.isHit("Refund_member " + orderNumber)) {
			throw new NotExistsRefundRecordRedis();
		}

		return refundRecordRedisRepository.readAll("Refund_member " + orderNumber);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean createRefundRecord(Long orderNumber, Long refundId) {
		if (!refundRecordRedisRepository.isHit("Refund_member " + orderNumber)) {
			return false;
		}
		Refund refund = refundRepository.findById(refundId).orElseThrow(RuntimeException::new);
		if (!refund.getRefundRecordList().isEmpty()) {
			return false;
		}
		List<ReadRefundRecordResponse> readRefundRecordResponseList = readRefundRecordRedis(orderNumber);

		for (ReadRefundRecordResponse readRefundRecordResponse : readRefundRecordResponseList) {
			RefundRecord refundRecord = new RefundRecord();
			refundRecord.setRefund(refund);
			refundRecord.setPurchaseBook(purchaseBookRepository.findById(readRefundRecordResponse.id()).orElseThrow(
				NotExistsPurchaseBook::new));
			refundRecord.setPrice(readRefundRecordResponse.price());
			refundRecord.setQuantity(readRefundRecordResponse.quantity());
			refundRecordRepository.save(refundRecord);
		}
		refundRecordRedisRepository.deleteAll("Refund_member " + orderNumber);

		return true;

	}

}
