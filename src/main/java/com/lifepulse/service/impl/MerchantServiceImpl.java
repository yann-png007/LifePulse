package com.lifepulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifepulse.common.PageResult;
import com.lifepulse.common.Result;
import com.lifepulse.common.ResultCode;
import com.lifepulse.dto.PageDTO;
import com.lifepulse.entity.Merchant;
import com.lifepulse.mapper.MerchantMapper;
import com.lifepulse.service.MerchantService;
import com.lifepulse.util.MultiCache;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    private final MultiCache multiCache;
    private final RedissonClient redissonClient;

    public MerchantServiceImpl(MultiCache multiCache, RedissonClient redissonClient) {
        this.multiCache = multiCache;
        this.redissonClient = redissonClient;
    }

    // 缓存KEY
    private static final String MERCHANT_INFO_CACHE_KEY = "merchant:info:";
    // 分布式锁KEY
    private static final String LOCK_MERCHANT_KEY = "lock:merchant:";
    // 空对象缓存过期时间（分钟）
    private static final long NULL_CACHE_TTL = 5;

    // ======================== 【管理员接口】 ========================
    /**
     * 新增商户
     */
    @Override
    @Transactional
    public void addMerchant(Merchant merchant) {
        merchant.setHotFlag(1);
        merchant.setIsDeleted(0);
        merchant.setCreateTime(LocalDateTime.now());
        merchant.setUpdateTime(LocalDateTime.now());
        save(merchant);

        // 写入缓存
        multiCache.setCache(MERCHANT_INFO_CACHE_KEY + merchant.getId(), merchant);
    }

    /**
     * 修改商户
     */
    @Override
    @Transactional
    public void updateMerchant(Merchant merchant) {
        merchant.setHotFlag(1);
        merchant.setUpdateTime(LocalDateTime.now());
        updateById(merchant);

        // 更新缓存
        multiCache.setCache(MERCHANT_INFO_CACHE_KEY + merchant.getId(), merchant);
    }

    /**
     * 删除商户（逻辑删除）
     */
    @Override
    @Transactional
    public void removeMerchant(Long id) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setIsDeleted(1);
        updateById(merchant);

        // 删除缓存
        multiCache.removeCache(MERCHANT_INFO_CACHE_KEY + id);
    }

    // ======================== 【用户接口】 ========================
    /**
     * 用户端：根据ID查询商户详情（带多级缓存）
     */
    @Override
    public Result<Merchant> getMerchantById(Long id) {
        String cacheKey = MERCHANT_INFO_CACHE_KEY + id;

        // 1. 查缓存
        Merchant cacheData = multiCache.getCache(cacheKey, Merchant.class);
        if (cacheData != null) {
            // 命中空对象缓存，直接返回不存在
            if (cacheData.getId() == null) {
                return Result.error(ResultCode.MERCHANT_NOT_EXIST);
            }
            return Result.success(cacheData);
        }

        // 2. 缓存未命中，获取分布式锁，防止缓存击穿
        RLock lock = redissonClient.getLock(LOCK_MERCHANT_KEY + id);
        try {
            // 尝试获取锁，等待10秒，锁自动释放时间30秒
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                // 双重检查，可能在等待锁的过程中已有其他线程回写了缓存
                cacheData = multiCache.getCache(cacheKey, Merchant.class);
                if (cacheData != null) {
                    if (cacheData.getId() == null) {
                        return Result.error(ResultCode.MERCHANT_NOT_EXIST);
                    }
                    return Result.success(cacheData);
                }

                // 3. 查库
                Merchant dbInfo = getById(id);
                if (dbInfo == null || dbInfo.getIsDeleted() == 1) {
                    // 数据库不存在，缓存空对象防止缓存穿透，设置较短过期时间
                    multiCache.setCache(cacheKey, new Merchant(), TimeUnit.MINUTES.toSeconds(NULL_CACHE_TTL));
                    return Result.error(ResultCode.MERCHANT_NOT_EXIST);
                }

                // 4. 数据库存在，回写缓存
                multiCache.setCache(cacheKey, dbInfo);
                return Result.success(dbInfo);

            } else {
                // 获取锁失败，说明有其他线程正在查询，稍等后重试（或直接失败）
                // 为简化，我们直接返回系统繁忙
                return Result.error(ResultCode.REQUEST_BUSY);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.error(ResultCode.FAIL);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 用户端：分页 + 关键词查询（Controller真正需要的）
     */
    @Override
    public Result<PageResult<Merchant>> listMerchant(PageDTO dto, String keyword) {
        // 1. 构建分页对象（自动处理 limit 和 offset）
        Page<Merchant> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getIsDeleted, 0)
                .orderByDesc(Merchant::getUpdateTime);

        // 关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Merchant::getName, keyword);
        }

        // 3. 执行真正的分页查询（只查当前页的数据）
        Page<Merchant> merchantPage = this.page(page, wrapper);

        // 4. 封装成你的 PageResult
        PageResult<Merchant> pageResult = new PageResult<>();
        pageResult.setList(merchantPage.getRecords()); // 当前页数据
        pageResult.setTotal(merchantPage.getTotal());  // 总条数

        return Result.success(pageResult);
    }

}