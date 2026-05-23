package com.lifepulse.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 基础set
    public void set(String key,Object value,long time){
        redisTemplate.opsForValue().set(key,value,time, TimeUnit.SECONDS);
    }

    // 基础get
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    // 删除key
    public void delete(String key){
        redisTemplate.delete(key);
    }

    // 判断key是否存在
    public boolean hasKey(String key){
        return redisTemplate.hasKey(key);
    }

    // 秒杀专用：原子递减（解决你的报错）
    public Long decrement(String key, long delta) {
        // 使用Redis原生INCRBY，保证原子性，防止并发问题
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // 递减失败时回滚库存用
    public void increment(String key, long delta) {
        redisTemplate.opsForValue().increment(key, delta);
    }
}