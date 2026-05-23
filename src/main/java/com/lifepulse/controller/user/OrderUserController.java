package com.lifepulse.controller.user;

import com.google.common.util.concurrent.RateLimiter;
import com.lifepulse.common.PageResult;
import com.lifepulse.common.Result;
import com.lifepulse.common.ResultCode;
import com.lifepulse.dto.PageDTO;
import com.lifepulse.dto.OrderDTO;
import com.lifepulse.service.OrderService;
import com.lifepulse.vo.OrderVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
public class OrderUserController {

    private final OrderService orderService;

    // Guava RateLimiter，每秒放行200个请求
    private static final RateLimiter rateLimiter = RateLimiter.create(200);

    public OrderUserController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 用户下单
    @PostMapping("/create")
    public Result<String> createOrder(@Validated @RequestBody OrderDTO dto) {
        // 单机限流
        if (!rateLimiter.tryAcquire()) {
            return Result.error(ResultCode.REQUEST_BUSY);
        }
        String orderNo = orderService.createUserOrder(dto);
        return Result.success(orderNo);
    }

    // 用户分页查询自己订单
    @GetMapping("/list")
    public Result<PageResult<OrderVO>> getUserOrderList(PageDTO dto, @RequestParam Long userId) {
        return Result.success(orderService.getUserOrderPage(userId, dto));
    }

    // 取消订单
    @PostMapping("/cancel/{orderId}")
    public Result<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.userCancelOrder(orderId);
        return Result.success();
    }
}