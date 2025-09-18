package com.nhnacademy.batch.batch.coupon.birthday;

import com.nhnacademy.batch.batch.coupon.exception.RetryException;
import com.nhnacademy.batch.batch.coupon.exception.SkipException;
import com.nhnacademy.batch.batch.coupon.feign.CouponControllerClient;
import com.nhnacademy.batch.entity.member.Member;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 쿠폰 배치 설정.
 *
 * @author 김병우
 */
@RequiredArgsConstructor
@Configuration
public class CouponBatchConfiguration {
    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;
    private final CouponControllerClient couponControllerClient;
    @Bean
    public Job couponJob(JobRepository jobRepository, Step couponStep){
        return new JobBuilder("couponJob", jobRepository).start(couponStep).build();
    }
    @Bean
    public Step couponStep(JobRepository jobRepository){
        return new StepBuilder("couponStep", jobRepository)
                .<Member, Member>chunk(10, transactionManager)
                .reader(couponReader())
                .processor(couponProcessor())
                .writer(couponWriter())
                .faultTolerant()
                .retryLimit(3)
                .retry(RetryException.class)
                .skipLimit(5)
                .skip(SkipException.class)
                .build();
    }
    @Bean
    public JpaPagingItemReader<Member> couponReader(){
        return new JpaPagingItemReaderBuilder<Member>()
                .name("jpapagingreader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT m FROM Member m WHERE m.birthday = CURRENT_DATE")
                .build();
    }
    @Bean
    public ItemProcessor<Member, Member> couponProcessor(){
        return (member-> {
             couponControllerClient.registerCouponBook(member.getId());
            return member;
        });
    }
    @Bean
    public JpaItemWriter<Member> couponWriter(){
        JpaItemWriter<Member> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
