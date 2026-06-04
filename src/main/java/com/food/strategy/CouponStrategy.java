// 技术栈：策略模式
package com.food.strategy;

import com.food.entity.Coupon;
import java.math.BigDecimal;

public interface CouponStrategy {
    /**
     * 计算折扣金额
     * @param totalAmount 订单原总价
     * @param coupon 优惠券
     * @return 折扣掉的金额
     */
    BigDecimal calculateDiscount(BigDecimal totalAmount, Coupon coupon);
}
