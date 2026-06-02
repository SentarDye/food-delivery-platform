// 技术栈：MyBatis
package com.food.mapper;

import com.food.entity.OrderStatusLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderStatusLogMapper {
    void insert(OrderStatusLog log);
}
