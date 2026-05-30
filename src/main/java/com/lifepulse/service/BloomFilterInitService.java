package com.lifepulse.service;

import com.lifepulse.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BloomFilterInitService {

    public static final String USER_PHONE_BLOOM_FILTER = "user:phone:bloom";

    private final RedissonClient redissonClient;
    private final UserMapper userMapper;

    public BloomFilterInitService(RedissonClient redissonClient, UserMapper userMapper) {
        this.redissonClient = redissonClient;
        this.userMapper = userMapper;
    }

    /**
     * 项目启动后执行，初始化布隆过滤器
     */
    @PostConstruct
    public void initBloomFilter() {
        log.info("开始初始化用户手机号布隆过滤器...");

        // 1. 获取布隆过滤器实例
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(USER_PHONE_BLOOM_FILTER);

        // 2. 设置布隆过滤器的参数并初始化
        // 预计存放100万个元素，期望的误判率是0.01 (1%)
        // 您可以根据实际用户规模调整这两个参数
        long expectedInsertions = 1_000_000L;
        double falseProbability = 0.01;
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(expectedInsertions, falseProbability);
            log.info("布隆过滤器不存在，已初始化。预计容量: {}, 误判率: {}", expectedInsertions, falseProbability);
        } else {
            log.info("布隆过滤器已存在，无需重复初始化。");
        }


        // 3. 从数据库查询所有手机号
        List<String> allPhones = userMapper.selectAllPhones();
        if (allPhones == null || allPhones.isEmpty()) {
            log.warn("数据库中没有用户手机号，布隆过滤器为空。");
            return;
        }

        // 4. 将手机号批量添加到布隆过滤器
        log.info("查询到 {} 个手机号，准备添加到布隆过滤器...", allPhones.size());
        for (String phone : allPhones) {
            bloomFilter.add(phone);
        }

        log.info("用户手机号布隆过滤器初始化完成！已添加 {} 个手机号。", bloomFilter.count());
    }
}

