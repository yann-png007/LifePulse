package com.lifepulse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lifepulse.common.PageResult;
import com.lifepulse.dto.PageDTO;
import com.lifepulse.dto.OrderDTO;
import com.lifepulse.entity.Order;
import com.lifepulse.vo.OrderVO;

public interface OrderService extends IService<Order> {
    String createUserOrder(OrderDTO dto);
    PageResult<OrderVO> getUserOrderPage(Long userId, PageDTO dto);
    void userCancelOrder(Long orderId);
    PageResult<Order> getAdminOrderPage(PageDTO dto);
    void updateOrderStatus(Long orderId, Integer status);
}