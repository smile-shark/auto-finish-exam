package com.smileShark.utils;

import com.smileShark.constant.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisLockUtil {
    private final StringRedisTemplate stringRedisTemplate;
    private final Constant constant;

    public boolean tryLock(String key, long timeoutSec) {
        // 获取锁
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(constant.PROJECT_NAME + key, "value", timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String key) {
        // 释放锁
        stringRedisTemplate.delete(constant.PROJECT_NAME + key);
    }
}
