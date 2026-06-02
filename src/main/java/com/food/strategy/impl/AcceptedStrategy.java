// 技术栈：策略模式
package com.food.strategy.impl;

import com.food.entity.Order;
import com.food.enums.OrderStatus;
import com.food.mapper.DeliveryMapper;
import com.food.service.DeliveryService;
import com.food.strategy.OrderStateStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class AcceptedStrategy implements OrderStateStrategy {

    @Autowired
    private DeliveryService deliveryService;  // 注入生成配送单

    @Override
    public void execute(Order order, String operator) {
        order.setStatus(OrderStatus.ACCEPTED.getCode());
        order.setAcceptTime(LocalDateTime.now());
        // 生成配送单
        deliveryService.createDelivery(order.getId());
    }

    @Override
    public OrderStatus getTargetStatus() {
        return OrderStatus.ACCEPTED;
    }
}