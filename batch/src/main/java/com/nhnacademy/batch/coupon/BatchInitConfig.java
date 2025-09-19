package com.nhnacademy.batch.coupon;

import com.nhnacademy.batch.coupon.adapter.CouponControllerClient;
import com.nhnacademy.batch.coupon.step.Steps;
import com.nhnacademy.batch.coupon.tasklet.Tasklets;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Component
@Slf4j
@RequiredArgsConstructor
public class BatchInitConfig {
    private final JobRepository jobRepository;
    private final JobRegistry jobRegistry;
    private final EntityManagerFactory entityManagerFactory;
    private final PlatformTransactionManager transactionManager;
    private final CouponControllerClient couponControllerClient;

    private final Steps step;
    private final Tasklets couponSaveTasklet;

    @PostConstruct
    public void run() throws DuplicateJobException {
        String jobId1 = "batch-issue-birthday-coupon";
        String jobId2 = "batch-coupon-save";

        Job job1 = new JobBuilder(jobId1, jobRepository)
                .start(step.birthdayIssueStep(jobRepository))
                .build();


        Job job2 = new JobBuilder(jobId2, jobRepository)
                .start(step.couponSaveStep(jobRepository))
                .build();

        try {
            jobRegistry.register(new ReferenceJobFactory(job1));
            log.info("Job Registered: {}", jobId1);
        } catch (Exception e) {
            if (e.getMessage().contains("already registered")) {
                log.warn("Job '{}' already registered", jobId1);
            } else {
                log.error("Job '{}' register fail", jobId1, e);
                throw e;
            }
        }
        try {
            jobRegistry.register(new ReferenceJobFactory(job2));
            log.info("Job Registered: {}", jobId2);
        } catch (Exception e) {
            if (e.getMessage().contains("already registered")) {
                log.warn("Job '{}' already registered", jobId2);
            } else {
                log.error("Job '{}' register fail", jobId2, e);
                throw e;
            }
        }
    }

}
