package com.food.mapper;

import com.food.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insert(User user);
    User selectByPhone(@Param("phone") String phone);
    User selectById(@Param("id") Long id);
    void updateById(User user);
    void softDeleteById(@Param("id") Long id);
}
