package com.nhnacademy.batch.coupon.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.batch.coupon.itemprocessor.Processors;
import com.nhnacademy.batch.coupon.itemreader.Readers;
import com.nhnacademy.batch.coupon.itemwriter.Writers;
import com.nhnacademy.batch.coupon.tasklet.Tasklets;
import com.nhnacademy.batch.entity.member.Member;
import com.nhnacademy.batch.exceptionHandler.RetryException;
import com.nhnacademy.batch.exceptionHandler.SkipException;
import com.nhnacademy.batch.repository.CouponFormRepository;
import com.nhnacademy.batch.repository.CouponTypeRepository;
import com.nhnacademy.batch.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@RequiredArgsConstructor
@Configuration
public class Steps {
    private final PlatformTransactionManager transactionManager;
    private final Processors processors;
    private final Readers readers;
    private final Writers writers;

    private final StringRedisTemplate redis;
    private final CouponUsageRepository couponUsageRepository;
    private final CouponTypeRepository couponTypeRepository;
    private final CouponFormRepository couponFormRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public Step birthdayIssueStep(JobRepository jobRepository){
        return new StepBuilder("birthdayIssueStep", jobRepository)
                .<Member, Member>chunk(10, transactionManager)
                .reader(readers.couponReader())
                .processor(processors.couponProcessor())
                .writer(writers.couponWriter())
                .faultTolerant()
                .retryLimit(3)
                .retry(RetryException.class)
                .skipLimit(5)
                .skip(SkipException.class)
                .build();
    }

    @Bean
    public Step couponSaveStep(JobRepository jobRepository){
        return new StepBuilder("couponSaveStep", jobRepository)
                .tasklet(
                        new Tasklets(
                            redis, couponUsageRepository, couponTypeRepository,
                            couponFormRepository, objectMapper),
                        transactionManager)
                .build();
    }
}
