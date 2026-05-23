package com.lifepulse.controller.admin;

import com.lifepulse.common.PageResult;
import com.lifepulse.common.Result;
import com.lifepulse.dto.PageDTO;
import com.lifepulse.entity.Order;
import com.lifepulse.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
public class OrderAdminController {

    private final OrderService orderService;

    public OrderAdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 后台分页查询全部订单
    @GetMapping("/list")
    public Result<PageResult<Order>> getOrderPage(PageDTO dto) {
        return Result.success(orderService.getAdminOrderPage(dto));
    }

    // 后台修改订单状态
    @PutMapping("/status/{orderId}/{status}")
    public Result<String> changeOrderStatus(@PathVariable Long orderId, @PathVariable Integer status) {
        orderService.updateOrderStatus(orderId, status);
        return Result.success("订单状态修改成功");
    }

    // 后台删除订单
    @DeleteMapping("/{id}")
    public Result<String> deleteOrder(@PathVariable Long id) {
        orderService.removeById(id);
        return Result.success("删除成功");
    }
}