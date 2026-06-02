// 技术栈：策略模式
package com.food.strategy;

import com.food.entity.Order;
import com.food.enums.OrderStatus;

/**
 * 订单状态操作策略接口
 */
public interface OrderStateStrategy {
    /**
     * 执行状态变更操作
     * @param order 订单对象
     * @param operator 操作者标识（user/merchant/rider/system）
     */
    void execute(Order order, String operator);

    /**
     * 该策略支持的目标状态
     */
    OrderStatus getTargetStatus();
}
