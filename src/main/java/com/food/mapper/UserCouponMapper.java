// 技术栈：MyBatis
package com.food.mapper;

import com.food.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserCouponMapper {
    void insert(UserCoupon userCoupon);
    List<UserCoupon> selectByUserIdAndStatus(@Param("userId") Long userId,
                                             @Param("status") Integer status);
    UserCoupon selectById(@Param("id") Long id);
    void updateById(UserCoupon userCoupon);
    int countUserCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
}
