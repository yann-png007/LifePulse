package com.lifepulse.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Redisson分布式锁配置
 * 用于秒杀一人一单、并发控制
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private String redisPort;

    @Value("${spring.data.redis.password:}") // 使用冒号设置默认值为空字符串
    private String redisPassword;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setDatabase(0)
                .setConnectionMinimumIdleSize(10)
                .setConnectionPoolSize(64);

        // 只有当配置文件中提供了密码时，才设置密码
        if (StringUtils.hasText(redisPassword)) {
            config.useSingleServer().setPassword(redisPassword);
        }

        return Redisson.create(config);
    }
}