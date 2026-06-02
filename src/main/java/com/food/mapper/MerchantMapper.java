package com.food.mapper;

import com.food.entity.Merchant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MerchantMapper {
    void insert(Merchant merchant);
    Merchant selectByPhone(@Param("phone") String phone);
    Merchant selectById(@Param("id") Long id);
    void updateById(Merchant merchant);
    void softDeleteById(@Param("id") Long id);
}
