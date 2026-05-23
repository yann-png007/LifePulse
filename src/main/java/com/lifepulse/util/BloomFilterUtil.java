package com.lifepulse.util;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * 布隆过滤器工具
 * 拦截不存在Key查询，彻底解决缓存穿透问题
 */
@Component
public class BloomFilterUtil {
    private BloomFilter<String> bloomFilter;

    // 项目启动初始化布隆过滤器
    @PostConstruct
    public void initBloomFilter(){
        bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                1000000,
                0.01
        );
    }

    // 存入数据
    public void addData(String data){
        bloomFilter.put(data);
    }

    // 判断是否可能存在
    public boolean isExist(String data){
        return bloomFilter.mightContain(data);
    }
}