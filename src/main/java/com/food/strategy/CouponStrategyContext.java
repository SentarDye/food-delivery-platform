// 技术栈：策略模式
package com.food.strategy;

import com.food.entity.Coupon;
import com.food.strategy.impl.FullReductionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CouponStrategyContext {
    private final Map<Integer, CouponStrategy> strategyMap;

    @Autowired
    public CouponStrategyContext(List<CouponStrategy> strategies) {
        strategyMap = strategies.stream().collect(Collectors.toMap(
                s -> s instanceof FullReductionStrategy ? 1 : 2,
                Function.identity()
        ));
    }

    public CouponStrategy getStrategy(Coupon coupon) {
        CouponStrategy strategy = strategyMap.get(coupon.getType());
        if (strategy == null) {
            throw new IllegalArgumentException("未知优惠券类型: " + coupon.getType());
        }
        return strategy;
    }
}
