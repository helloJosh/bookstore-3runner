package com.nhnacademy.batch.batch.coupon.job;

import com.nhnacademy.batch.batch.coupon.tasklet.CouponSaveTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final CouponSaveTasklet tasklet;

    @Scheduled(cron = "0 */10 * * * *")
    public void run() throws Exception {
        tasklet.execute(null, null);
    }
}
