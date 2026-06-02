// 技术栈：MyBatis
package com.food.mapper;

import com.food.entity.Rider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RiderMapper {
    void insert(Rider rider);
    Rider selectByPhone(@Param("phone") String phone);
    Rider selectById(@Param("id") Long id);
    void updateById(Rider rider);
    void softDeleteById(@Param("id") Long id);
}
