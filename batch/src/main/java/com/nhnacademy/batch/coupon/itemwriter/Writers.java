package com.nhnacademy.batch.coupon.itemwriter;

import com.nhnacademy.batch.entity.member.Member;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class Writers {
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public JpaItemWriter<Member> couponWriter(){
        JpaItemWriter<Member> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
