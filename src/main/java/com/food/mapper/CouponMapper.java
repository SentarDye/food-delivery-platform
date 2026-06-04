// 技术栈：MyBatis
package com.food.mapper;

import com.food.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CouponMapper {
    void insert(Coupon coupon);
    Coupon selectById(@Param("id") Long id);
    List<Coupon> selectAvailable(@Param("merchantId") Long merchantId,
                                 @Param("now") java.time.LocalDateTime now);
    void updateTotalQuantity(@Param("id") Long id, @Param("delta") int delta);
}
