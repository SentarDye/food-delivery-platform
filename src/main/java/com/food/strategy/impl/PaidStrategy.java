// 技术栈：策略模式
package com.food.strategy.impl;

import com.food.entity.Order;
import com.food.enums.OrderStatus;
import com.food.strategy.OrderStateStrategy;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class PaidStrategy implements OrderStateStrategy {
    @Override
    public void execute(Order order, String operator) {
        order.setStatus(OrderStatus.PAID.getCode());
        order.setPayTime(LocalDateTime.now());
        // 支付成功后可通知商家（后续消息推送）
    }
    @Override
    public OrderStatus getTargetStatus() {
        return OrderStatus.PAID;
    }
}
