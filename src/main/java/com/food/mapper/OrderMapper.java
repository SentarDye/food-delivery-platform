// 技术栈：MyBatis
package com.food.mapper;

import com.food.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Order order);
    Order selectById(@Param("id") Long id);
    List<Order> selectByUserId(@Param("userId") Long userId);
    List<Order> selectByMerchantId(@Param("merchantId") Long merchantId);
    void updateStatus(@Param("id") Long id, @Param("status") Integer status,
                      @Param("payTime") java.time.LocalDateTime payTime,
                      @Param("acceptTime") java.time.LocalDateTime acceptTime,
                      @Param("deliveryTime") java.time.LocalDateTime deliveryTime,
                      @Param("completeTime") java.time.LocalDateTime completeTime,
                      @Param("cancelTime") java.time.LocalDateTime cancelTime);
}
