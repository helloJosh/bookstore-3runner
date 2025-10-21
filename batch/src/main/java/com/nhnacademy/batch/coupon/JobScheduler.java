package com.nhnacademy.batch.coupon;

import com.nhnacademy.batch.coupon.tasklet.Tasklets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobScheduler {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    @Scheduled(cron = "0 0 0 * * ?")
    public void issueCouponTime() {
        String jobId1 = "batch-issue-birthday-coupon";
        executor.submit(() -> {
            try {
                Job job = jobRegistry.getJob(jobId1);
                JobExecution execution = jobLauncher.run(
                        job,
                        new JobParametersBuilder()
                                .addLocalDateTime("startedAt", LocalDateTime.now())
                                .toJobParameters()
                );
                log.info("job finished : jobId={}", execution.getJobId());
            } catch (Exception e) {
                log.error("Job fail: {}", jobId1, e);
            }
        });
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void run() throws Exception {
        String jobId2 = "batch-coupon-save";
        executor.submit(() -> {
            try {
                Job job = jobRegistry.getJob(jobId2);
                JobExecution execution = jobLauncher.run(
                        job,
                        new JobParametersBuilder()
                                .addLocalDateTime("startedAt", LocalDateTime.now())
                                .toJobParameters()
                );
                log.info("job finished : jobId={}", execution.getJobId());
            } catch (Exception e) {
                log.error("Job fail: {}", jobId2, e);
            }
        });
    }
}
