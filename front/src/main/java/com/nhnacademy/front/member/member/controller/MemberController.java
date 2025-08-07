package com.nhnacademy.front.member.member.controller;

import com.nhnacademy.front.auth.adapter.DormantAdapter;
import com.nhnacademy.front.auth.service.LoginService;
import com.nhnacademy.front.member.address.dto.response.AddressResponse;
import com.nhnacademy.front.member.address.feign.AddressControllerClient;
import com.nhnacademy.front.member.member.dto.request.CreateMemberRequest;
import com.nhnacademy.front.member.member.feign.MemberControllerClient;
import com.nhnacademy.front.purchase.purchase.dto.member.response.GetMemberResponse;
import com.nhnacademy.front.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
	private final MemberControllerClient memberControllerClient;
	private final AddressControllerClient addressControllerClient;
	private final LoginService loginService;
	private final DormantAdapter dormantAdapter;

	@GetMapping("/member/createForm")
	public String createSignInPage() {
		return "signin";
	}//등록페이지

	@PostMapping("/member")
	public String signIn(@RequestBody CreateMemberRequest createMemberRequest) {
		ApiResponse<Long> result = memberControllerClient.createMembers(createMemberRequest);
		if (!result.getHeader().isSuccessful()) {
			return "redirect:/member/createForm";
		}
		return "redirect:/";
	}

	@GetMapping("/member/mypageForm")
	public String myPage() {
		return "mypage";
	}

	@GetMapping("/member/mypage")
	public String memberMyPage() {
		return "mypage/mypage";
	}

	@GetMapping("/member")
	public String myPage(Model model) {
		ApiResponse<GetMemberResponse> memberDetails = memberControllerClient.readById();
		ApiResponse<List<AddressResponse>> addresses = addressControllerClient.readAllAddresses();
		model.addAttribute("member", memberDetails.getBody().getData());
		model.addAttribute("addresses", addresses.getBody().getData());
		return "memberdetail";
	}


	@GetMapping("/member/dormant")
	public String dormantForm() {
		return "dormant";
	}


}

