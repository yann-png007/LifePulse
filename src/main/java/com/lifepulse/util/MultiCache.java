package com.lifepulse.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class MultiCache {

    // 本地缓存配置：最大1000条，写入后5分钟过期
    private final Cache<String, Object> localCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final RedisUtil redisUtil;

    @Autowired
    public MultiCache(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    // Redis默认过期时间：30分钟（和你原来的常量保持一致）
    private static final long DEFAULT_REDIS_TTL = 30 * 60;

    /**
     * 多级缓存查询（带类型转换）
     * @param key 缓存key
     * @param clazz 目标数据类型
     * @return 转换后的对象
     */
    public <T> T getCache(String key, Class<T> clazz) {
        // 1. 先查本地缓存
        Object localData = localCache.getIfPresent(key);
        if (localData != null) {
            return clazz.cast(localData);
        }

        // 2. 再查Redis
        Object redisData = redisUtil.get(key);
        if (redisData != null) {
            // 回写到本地缓存
            localCache.put(key, redisData);
            return clazz.cast(redisData);
        }

        return null;
    }

    /**
     * 设置缓存（带自定义过期时间）
     * @param key 缓存key
     * @param data 缓存数据
     * @param expireTime 过期时间（秒）
     */
    public void setCache(String key, Object data, long expireTime) {
        // 写入本地缓存
        localCache.put(key, data);
        // 写入Redis，并增加随机过期时间，防止缓存雪崩
        long randomTTL = expireTime + ThreadLocalRandom.current().nextLong(expireTime / 10);
        redisUtil.set(key, data, randomTTL);
    }

    /**
     * 设置缓存（默认过期时间：30分钟）
     * @param key 缓存key
     * @param data 缓存数据
     */
    public void setCache(String key, Object data) {
        this.setCache(key, data, DEFAULT_REDIS_TTL);
    }

    /**
     * 删除缓存（双端清除）
     * @param key 缓存key
     */
    public void removeCache(String key) {
        localCache.invalidate(key);
        redisUtil.delete(key);
    }
}