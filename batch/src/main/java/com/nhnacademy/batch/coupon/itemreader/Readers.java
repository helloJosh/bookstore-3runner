package com.nhnacademy.batch.coupon.itemreader;

import com.nhnacademy.batch.entity.member.Member;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class Readers {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public JpaPagingItemReader<Member> couponReader(){

        return new JpaPagingItemReaderBuilder<Member>()
                .name("jpapagingreader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT m FROM Member m WHERE m.birthday = CURRENT_DATE")
                .build();
    }
}
