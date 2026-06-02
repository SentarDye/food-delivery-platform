// 技术栈：MyBatis
package com.food.mapper;

import com.food.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CartMapper {
    void insert(Cart cart);
    void updateQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);
    void deleteById(@Param("id") Long id);
    void deleteByUserId(@Param("userId") Long userId);
    Cart selectByUserIdAndItemId(@Param("userId") Long userId, @Param("menuItemId") Long menuItemId);
    List<Cart> selectByUserId(@Param("userId") Long userId);
}
