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
                .setIfAbsent(RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, key), "value", timeoutSec, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    public void unlock(String key) {
        // 释放锁
        stringRedisTemplate.delete(RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, key));
    }

    /**
     * 获取用户验证码锁，只有在有锁的时候才会去查询，如果没有锁，则直接返回false
     */
    public boolean getUserCodeLock(){
        return stringRedisTemplate.opsForValue().get(RedisKeyUtil
                .getSimpleKey(constant.PROJECT_NAME, "userCodeLock")) != null;
    }

    /**
     * 每次有用户尝试登录都会刷新锁的时间
     */
    public void setUserCodeLock(){
        stringRedisTemplate.opsForValue().set(RedisKeyUtil
                .getSimpleKey(constant.PROJECT_NAME, "userCodeLock"), "haveLock", 10, TimeUnit.MINUTES);
    }


    /**
     * 获取课程搜索锁，如果没有就不执行匹配编号的操作
     */
    public String getCourseSearchLock(String account){
        return stringRedisTemplate.opsForValue().get(RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "courseSearchLock",account));
    }

    /**
     * 每次有课程搜索都会刷新锁的时间
     */
    public void setCourseSearchLock(String account,String value){
        stringRedisTemplate.opsForValue().set(RedisKeyUtil.getSimpleKey(constant.PROJECT_NAME, "courseSearchLock", account), value, 10, TimeUnit.MINUTES);
    }



}
