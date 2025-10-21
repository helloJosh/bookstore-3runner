package com.nhnacademy.batch.coupon.itemprocessor;

import com.nhnacademy.batch.coupon.adapter.CouponControllerClient;
import com.nhnacademy.batch.entity.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Processors {
    private final CouponControllerClient couponControllerClient;

    @Bean
    public ItemProcessor<Member, Member> couponProcessor(){
        return (member-> {
            couponControllerClient.registerCouponBook(member.getId());
            return member;
        });
    }
}
