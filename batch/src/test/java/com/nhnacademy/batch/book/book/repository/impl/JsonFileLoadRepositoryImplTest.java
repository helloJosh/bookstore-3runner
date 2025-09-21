package com.nhnacademy.batch.book.book.repository.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonFileLoadRepositoryImplTest {

	@Autowired
	private JsonFileLoadRepositoryImpl jsonFileLoadRepository;

	private static final String FILE_NAME = "test.json";

	@Test
	void testJsonFileLoad() throws IOException {
		// given
		String result = jsonFileLoadRepository.jsonFileLoad(FILE_NAME);

		// then
		assertThat(result).isNotNull();
	}

	@Test
	void testJsonFileLoad_FileNotFound() throws IOException {
		// given
		// when
		String result = jsonFileLoadRepository.jsonFileLoad(FILE_NAME + "/ss");

		// then
		assertThat(result).isEqualTo("파일 불러오기 실패");
	}
}
