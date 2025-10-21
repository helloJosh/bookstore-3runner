package com.nhnacademy.batch.book.tag.repository;

import java.util.List;

import com.nhnacademy.batch.book.tag.response.BookTagResponse;

public interface TagCustomRepository {
	List<BookTagResponse> bookCategories(long minBookId, long maxBookId);
}
