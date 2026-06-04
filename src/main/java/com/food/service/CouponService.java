// 技术栈：Spring Boot + MyBatis
package com.food.service;

import com.food.common.Result;
import com.food.entity.Coupon;
import com.food.entity.UserCoupon;
import com.food.mapper.CouponMapper;
import com.food.mapper.UserCouponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private UserCouponMapper userCouponMapper;

    /**
     * 管理后台：创建优惠券
     */
    public Result createCoupon(Long merchantId, String name, Integer type, BigDecimal discount,
                               BigDecimal minAmount, Integer totalQuantity, Integer userLimit,
                               LocalDateTime startTime, LocalDateTime endTime) {
        Coupon coupon = new Coupon();
        coupon.setMerchantId(merchantId);
        coupon.setName(name);
        coupon.setType(type);
        coupon.setDiscount(discount);
        coupon.setMinAmount(minAmount);
        coupon.setTotalQuantity(totalQuantity);
        coupon.setUserLimit(userLimit);
        coupon.setStartTime(startTime);
        coupon.setEndTime(endTime);
        couponMapper.insert(coupon);
        return Result.success(coupon);
    }

    /**
     * 用户领取优惠券
     */
    @Transactional
    public Result receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            return Result.fail(404, "优惠券不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            return Result.fail(400, "不在领取时间范围内");
        }
        // 限量检查
        if (coupon.getTotalQuantity() != null) {
            if (coupon.getTotalQuantity() <= 0) {
                return Result.fail(400, "优惠券已领完");
            }
        }
        // 用户限领
        int count = userCouponMapper.countUserCoupon(userId, couponId);
        if (coupon.getUserLimit() != null && count >= coupon.getUserLimit()) {
            return Result.fail(400, "领取已达上限");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0);
        userCoupon.setReceiveTime(now);
        userCouponMapper.insert(userCoupon);

        if (coupon.getTotalQuantity() != null) {
            couponMapper.updateTotalQuantity(couponId, -1);
        }
        return Result.success("领取成功");
    }

    /**
     * 查看用户可用优惠券（未使用且未过期）
     */
    public Result getMyAvailableCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponMapper.selectByUserIdAndStatus(userId, 0);
        // 过滤已过期
        LocalDateTime now = LocalDateTime.now();
        List<UserCoupon> valid = userCoupons.stream().filter(uc -> {
            Coupon c = couponMapper.selectById(uc.getCouponId());
            return c != null && now.isBefore(c.getEndTime()) && now.isAfter(c.getStartTime());
        }).collect(Collectors.toList());
        return Result.success(valid);
    }

    /**
     * 获取单个用户优惠券信息（内部使用）
     */
    public UserCoupon getUserCouponById(Long userCouponId) {
        return userCouponMapper.selectById(userCouponId);
    }

    /**
     * 使用优惠券（标记已使用，由订单服务调用）
     */
    public void useCoupon(Long userCouponId, Long orderId) {
        UserCoupon uc = new UserCoupon();
        uc.setId(userCouponId);
        uc.setStatus(1);
        uc.setUsedOrderId(orderId);
        uc.setUseTime(LocalDateTime.now());
        userCouponMapper.updateById(uc);
    }
}