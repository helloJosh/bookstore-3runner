package com.nhnacademy.coupon.util;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.List;

public class RedisTime {

    public static long nowMillis(StringRedisTemplate r) {
        List<Object> t = r.execute((RedisCallback<List<Object>>) con -> Collections.singletonList(con.time()));
        if (t == null || t.size() < 2) return System.currentTimeMillis();
        long sec = Long.parseLong(t.get(0).toString());
        long usec = Long.parseLong(t.get(1).toString());
        return sec * 1000L + usec / 1000L;
    }
}
