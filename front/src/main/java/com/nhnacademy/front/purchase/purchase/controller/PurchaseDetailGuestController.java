package com.nhnacademy.front.purchase.purchase.controller;

import com.nhnacademy.front.purchase.purchase.dto.purchase.response.ReadPurchaseResponse;
import com.nhnacademy.front.purchase.purchase.service.PurchaseDetailGuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 비회원 주문 내역확인 컨트롤러
 *
 * @author 정주혁
 *
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/orders/guests")
public class PurchaseDetailGuestController {

	private final PurchaseDetailGuestService purchaseGuestService;


	/**
	 * 비회원 주문 확인 창
	 * @return 비회원 주문 확인 창으로 이동
	 */
	@GetMapping("/login")
	public String login() {

		return "order-login";
	}

	/**
	 *
	 * 비회원 주문 확인 인증
	 *
	 * @param orderNumber 주문 번호
	 * @param password 비밀 번호
	 * @return
	 */
	@PostMapping
	public String login(
		@RequestParam String orderNumber,
		@RequestParam String password,
		Model model
		) {
		if(purchaseGuestService.validatePurchase(orderNumber, password)){

			ReadPurchaseResponse response = purchaseGuestService.readGuestPurchases(orderNumber,password);
			if(Objects.isNull(response)){
				return "redirect:/orders/guests/login";
			}
			model.addAttribute("guestorder",response);
			model.addAttribute("guestorderbooks",purchaseGuestService.readGuestPurchaseBooks(orderNumber));
			return "purchase/guest/order-detail-guest";
		}
		else{
			return "redirect:/orders/guests/login";
		}



	}

	/**
	 * 주문 확정
	 *
	 * @param purchaseId 주문 확정할 id
	 * @return 주문 확정후 다시 자기 페이지로 이동
	 */
	@PostMapping("/{purchaseId}")
	public String orderConfirmed(@PathVariable(name = "purchaseId") String purchaseId){
		purchaseGuestService.updatePurchaseStatus(purchaseId);

		return "redirect:/orders/guests/login";
	}


}
