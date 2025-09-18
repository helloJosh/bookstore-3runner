package com.nhnacademy.front.book.comment.controller.fegin;

import com.nhnacademy.front.book.comment.dto.request.CreateCommentRequest;
import com.nhnacademy.front.book.comment.dto.response.CommentResponse;
import com.nhnacademy.front.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "CommentClient", url = "${feign.client.url}/bookstore")
public interface CommentClient {
	@PostMapping("/books/reviews/{reviewId}")
	ApiResponse<Void> createComment(
			@PathVariable("reviewId") Long reviewId,
			@RequestHeader("Member-id") Long memberId,
			@RequestBody CreateCommentRequest createCommentRequest
	);

	@GetMapping("/books/reviews/{reviewId}/comments")
	ApiResponse<Page<CommentResponse>> readAllCommentsByReviewId (
			@PathVariable("reviewId") Long reviewId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "page", defaultValue = "10") int size
	);

	@GetMapping("/books/reviews/member/comments")
	ApiResponse<Page<CommentResponse>> readAllCommentsByMemberId(
		@RequestHeader(value = "Member-Id", required = false) Long memberId,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "page", defaultValue = "10") int size
	);

	@DeleteMapping("/books/reviews/{commentId}/delete")
	ApiResponse<Void> deleteComment (
			@PathVariable("commentId") Long commentId,
			@RequestHeader(value = "Member-Id", required = false) Long memberId
	);
}

