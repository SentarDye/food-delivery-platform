// 技术栈：MyBatis
package com.food.mapper;

import com.food.entity.Delivery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DeliveryMapper {
    void insert(Delivery delivery);
    Delivery selectByOrderId(@Param("orderId") Long orderId);
    Delivery selectById(@Param("id") Long id);
    void updateById(Delivery delivery);
    List<Delivery> selectPending();  // 待接单配送单
    List<Delivery> selectByRiderId(@Param("riderId") Long riderId);
}
