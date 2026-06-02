// 技术栈：MyBatis
package com.food.mapper;

import com.food.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface OrderItemMapper {
    void insert(OrderItem item);
    List<OrderItem> selectByOrderId(Long orderId);
}
