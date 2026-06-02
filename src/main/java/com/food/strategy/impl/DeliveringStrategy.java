// 技术栈：策略模式
package com.food.strategy.impl;

import com.food.entity.Order;
import com.food.enums.OrderStatus;
import com.food.strategy.OrderStateStrategy;
import org.springframework.stereotype.Component;

@Component
public class DeliveringStrategy implements OrderStateStrategy {
    @Override
    public void execute(Order order, String operator) {
        order.setStatus(OrderStatus.DELIVERING.getCode());
        // delivery_time 在订单中表示配送开始时间，此处暂不设置，由 delivery 表记录
    }
    @Override
    public OrderStatus getTargetStatus() {
        return OrderStatus.DELIVERING;
    }
}
