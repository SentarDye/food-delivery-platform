// 技术栈：策略模式
package com.food.strategy.impl;

import com.food.entity.Order;
import com.food.enums.OrderStatus;
import com.food.strategy.OrderStateStrategy;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class CompletedStrategy implements OrderStateStrategy {
    @Override
    public void execute(Order order, String operator) {
        order.setStatus(OrderStatus.COMPLETED.getCode());
        order.setCompleteTime(LocalDateTime.now());
    }
    @Override
    public OrderStatus getTargetStatus() {
        return OrderStatus.COMPLETED;
    }
}
