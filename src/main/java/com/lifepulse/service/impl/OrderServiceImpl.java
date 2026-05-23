package com.lifepulse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lifepulse.common.PageResult;
import com.lifepulse.dto.PageDTO;
import com.lifepulse.dto.OrderDTO;
import com.lifepulse.entity.Coupon;
import com.lifepulse.entity.Merchant;
import com.lifepulse.entity.Order;
import com.lifepulse.mapper.CouponMapper;
import com.lifepulse.mapper.MerchantMapper;
import com.lifepulse.mapper.OrderMapper;
import com.lifepulse.service.OrderService;
import com.lifepulse.util.UserContext;
import com.lifepulse.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;



@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final StringRedisTemplate stringRedisTemplate;
    private final MerchantMapper merchantMapper;
    private final CouponMapper couponMapper;

    private static final String ORDER_IDEMPOTENT_KEY_PREFIX = "order:idempotent:";

    public OrderServiceImpl(StringRedisTemplate stringRedisTemplate, MerchantMapper merchantMapper, CouponMapper couponMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.merchantMapper = merchantMapper;
        this.couponMapper = couponMapper;
    }

    @Override
    @Transactional
    public String createUserOrder(OrderDTO dto) {
        // 1. 幂等性校验
        String idempotentKey = ORDER_IDEMPOTENT_KEY_PREFIX + dto.getRequestId();
        // 使用 setIfAbsent 实现 SETNX，并设置过期时间，防止永久占用
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(idempotentKey, "1", 24, TimeUnit.HOURS);

        if (Boolean.FALSE.equals(success)) {
            // 如果设置失败，说明是重复请求，直接返回之前存储的订单ID（或一个特定标识）
            // 在一个完整的实现中，这里应该返回之前成功创建的订单ID
            // 为简化，我们先返回一个重复请求的提示
            return "repeated_request"; // 或者可以从Redis中获取已存在的订单ID返回
        }

        Order order = new Order();
        order.setUserId(UserContext.getUserId());
        order.setMerchantId(dto.getMerchantId());
        order.setCouponId(dto.getCouponId());
        order.setStatus(0); // 待支付
        save(order);

        // 将真正的订单ID存回Redis，以便重复请求时可以返回
        stringRedisTemplate.opsForValue().set(idempotentKey, String.valueOf(order.getId()));

        return String.valueOf(order.getId());
    }

    @Override
    public PageResult<OrderVO> getUserOrderPage(Long userId, PageDTO dto) {
        // 1. 分页查询基础订单信息
        Page<Order> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId).orderByDesc(Order::getCreateTime);
        page(page, wrapper);

        List<Order> orderList = page.getRecords();
        PageResult<OrderVO> pageResult = new PageResult<>();
        pageResult.setTotal(page.getTotal());

        if (orderList.isEmpty()) {
            pageResult.setList(List.of());
            return pageResult;
        }

        // 2. 收集关联ID
        List<Long> merchantIds = orderList.stream().map(Order::getMerchantId).distinct().collect(Collectors.toList());
        List<Long> couponIds = orderList.stream().map(Order::getCouponId).distinct().collect(Collectors.toList());

        // 3. 批量查询关联信息
        Map<Long, String> merchantNameMap = merchantMapper.selectBatchIds(merchantIds).stream()
                .collect(Collectors.toMap(Merchant::getId, Merchant::getName));
        Map<Long, String> couponTitleMap = couponMapper.selectBatchIds(couponIds).stream()
                .collect(Collectors.toMap(Coupon::getId, Coupon::getCouponName));

        // 4. 转换为VO对象
        List<OrderVO> voList = orderList.stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            vo.setMerchantName(merchantNameMap.get(order.getMerchantId()));
            vo.setCouponTitle(couponTitleMap.get(order.getCouponId()));
            vo.setStatusText(convertStatusToText(order.getStatus()));
            return vo;
        }).collect(Collectors.toList());

        pageResult.setList(voList);
        return pageResult;
    }

    private String convertStatusToText(Integer status) {
        if (status == null) return "未知状态";
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已完成";
            case 2 -> "已取消";
            case 3 -> "已退款";
            case 4 -> "支付失败";
            default -> "未知状态";
        };
    }

    @Override
    @Transactional
    public void userCancelOrder(Long orderId) {
        Order order = getById(orderId);
        if (order.getStatus() == 0) {
            order.setStatus(2); // 已取消
            updateById(order);
        }
    }

    @Override
    public PageResult<Order> getAdminOrderPage(PageDTO dto) {
        Page<Order> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        page(page);
        PageResult<Order> pageResult = new PageResult<>();
        pageResult.setList(page.getRecords());
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }

    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        Order order = new Order();
        order.setId(orderId);
        order.setStatus(status);
        updateById(order);
    }
}