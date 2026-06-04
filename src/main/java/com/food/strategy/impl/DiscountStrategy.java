// 技术栈：策略模式
package com.food.strategy.impl;

import com.food.entity.Coupon;
import com.food.strategy.CouponStrategy;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DiscountStrategy implements CouponStrategy {
    @Override
    public BigDecimal calculateDiscount(BigDecimal totalAmount, Coupon coupon) {
        if (coupon.getType() != 2) {
            throw new IllegalArgumentException("非折扣券");
        }
        if (totalAmount.compareTo(coupon.getMinAmount()) < 0) {
            return BigDecimal.ZERO;
        }
        // discount 为折扣比例，如 80 表示打8折
        BigDecimal rate = coupon.getDiscount().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal discounted = totalAmount.multiply(rate);
        return totalAmount.subtract(discounted).setScale(2, RoundingMode.HALF_UP);
    }
}
