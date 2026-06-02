package com.food.mapper;

import com.food.entity.MenuItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface MenuItemMapper {
    void insert(MenuItem menuItem);
    void updateById(MenuItem menuItem);
    void softDeleteById(@Param("id") Long id);
    MenuItem selectById(@Param("id") Long id);
    List<MenuItem> selectByMerchantId(@Param("merchantId") Long merchantId);
}
