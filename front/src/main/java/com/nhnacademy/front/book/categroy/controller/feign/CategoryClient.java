package com.nhnacademy.front.book.categroy.controller.feign;

import com.nhnacademy.front.book.book.dto.response.BookListResponse;
import com.nhnacademy.front.book.categroy.dto.request.CreateCategoryRequest;
import com.nhnacademy.front.book.categroy.dto.response.CategoryChildrenResponse;
import com.nhnacademy.front.util.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 책 카테고리 관련 feignClient
 *
 * @author 한민기
 */
@FeignClient(name = "BookCategoryClient", url = "${feign.client.url}/bookstore/categories")
public interface CategoryClient {
	/**
	 * 모든 카테고리를 상위 밑에 하위 카테고리로 넣어 가져오는 메소드.
	 *
	 * @return 카테고리 안에 자식들
	 */
	@GetMapping
	ApiResponse<List<CategoryChildrenResponse>> readAllCategoryList();

	/**
	 * 카테고리 삭제 메소드.
	 *
	 * @param categoryId 삭제할 카테고리 아이디.
	 * @return 성공 여부
	 */
	@DeleteMapping("/{categoryId}")
	ApiResponse<Void> deleteCategory(@PathVariable("categoryId") Long categoryId);

	/**
	 * 카테고리 추가.
	 *
	 * @param dto 추가할 카테고리의 내용
	 * @return 성공 여부
	 */
	@PostMapping
	ApiResponse<Void> createCategory(@RequestBody CreateCategoryRequest dto);

	/**
	 * 카테고리에 관련된 도서페이지 조회 메서드.
	 *
	 * @param page 페이지
	 * @param size 사이즈
	 * @param sort 정렬
	 * @param categoryId 카테고리 넘버
	 * @return 한민기
	 */
	@GetMapping("/books")
	ApiResponse<Page<BookListResponse>> readCategoryAllBooks(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "12") int size,
			@RequestParam(name = "sort", defaultValue = "publishedDate,desc") String sort,
			@RequestParam(name = "categoryId", defaultValue = "0") Long categoryId
	);
}
