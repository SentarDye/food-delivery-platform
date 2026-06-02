// 技术栈：MyBatis
package com.food.mapper;

import com.food.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AddressMapper {
    void insert(Address address);
    void updateById(Address address);
    void deleteById(@Param("id") Long id);
    Address selectById(@Param("id") Long id);
    List<Address> selectByUserId(@Param("userId") Long userId);
    void clearDefaultByUserId(@Param("userId") Long userId);  // 清除用户所有默认地址
}
