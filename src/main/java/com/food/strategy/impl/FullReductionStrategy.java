// 技术栈：策略模式
package com.food.strategy.impl;

import com.food.entity.Coupon;
import com.food.strategy.CouponStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class FullReductionStrategy implements CouponStrategy {
    @Override
    public BigDecimal calculateDiscount(BigDecimal totalAmount, Coupon coupon) {
        if (coupon.getType() != 1) {
            throw new IllegalArgumentException("非满减券");
        }
        if (totalAmount.compareTo(coupon.getMinAmount()) < 0) {
            return BigDecimal.ZERO;
        }
        // 最多减到0，避免负数
        BigDecimal discount = coupon.getDiscount();
        return totalAmount.compareTo(discount) < 0 ? totalAmount : discount;
    }
}
