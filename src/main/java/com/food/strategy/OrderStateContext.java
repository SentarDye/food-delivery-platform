// 技术栈：策略模式 + Spring 依赖注入
package com.food.strategy;

import com.food.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderStateContext {
    @Autowired(required = false)
    private List<OrderStateStrategy> strategies;

    private final Map<OrderStatus, OrderStateStrategy> strategyMap = new EnumMap<>(OrderStatus.class);

    @PostConstruct
    public void init() {
        if (strategies != null) {
            for (OrderStateStrategy strategy : strategies) {
                strategyMap.put(strategy.getTargetStatus(), strategy);
            }
        }
    }

    public OrderStateStrategy getStrategy(OrderStatus targetStatus) {
        OrderStateStrategy strategy = strategyMap.get(targetStatus);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的状态变更: " + targetStatus);
        }
        return strategy;
    }
}
