// 技术栈：Spring Boot + MyBatis + 策略模式
package com.food.service;

import com.food.common.Result;
import com.food.entity.*;
import com.food.enums.OrderStatus;
import com.food.mapper.*;
import com.food.strategy.CouponStrategy;
import com.food.strategy.CouponStrategyContext;
import com.food.strategy.OrderStateContext;
import com.food.strategy.OrderStateStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    private CouponService couponService;

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private MenuItemMapper menuItemMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private OrderStatusLogMapper orderStatusLogMapper;

    @Autowired
    private CouponMapper couponMapper;

    @Autowired
    private CouponStrategyContext couponStrategyContext;
    @Autowired
    private OrderStateContext orderStateContext;

    /**
     * 从购物车下单（简单版，不考虑库存扣减和优惠券）
     */
    @Transactional
    public Result placeOrder(Long userId, Long addressId, Long userCouponId, String remark) {
        // 1. 获取用户购物车
        List<Cart> cartItems = cartMapper.selectByUserId(userId);
        if (cartItems.isEmpty()) {
            return Result.fail(400, "购物车为空");
        }

        // 2. 获取地址
        Address address = addressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            return Result.fail(400, "地址无效");
        }

        // 3. 校验购物车内商品是否属于同一商家（暂定只能下一个商家的单）
        Long merchantId = cartItems.get(0).getMerchantId();
        for (Cart item : cartItems) {
            if (!item.getMerchantId().equals(merchantId)) {
                return Result.fail(400, "购物车包含不同商家商品，请分开下单");
            }
        }

        // 4. 计算金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Cart cart : cartItems) {
            MenuItem menu = menuItemMapper.selectById(cart.getMenuItemId());
            if (menu == null || menu.getStatus() != 1) {
                return Result.fail(400, "菜品 " + menu.getName() + " 已下架");
            }
            BigDecimal itemTotal = menu.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 5. 获取商家配送费
        Merchant merchant = merchantMapper.selectById(merchantId);
        BigDecimal deliveryFee = merchant.getDeliveryFee() != null ? merchant.getDeliveryFee() : BigDecimal.ZERO;

        // 6. 获取折扣费用
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (userCouponId != null) {
            // 校验优惠券归属、状态、门槛
            UserCoupon userCoupon = couponService.getUserCouponById(userCouponId);
            if (userCoupon == null || !userCoupon.getUserId().equals(userId) || userCoupon.getStatus() != 0) {
                return Result.fail(400, "优惠券不可用");
            }
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());  // 注意注入 couponMapper
            if (coupon == null || LocalDateTime.now().isBefore(coupon.getStartTime())
                    || LocalDateTime.now().isAfter(coupon.getEndTime())) {
                return Result.fail(400, "优惠券无效或已过期");
            }
            // 适用性检查：平台券或此商家的券
            if (coupon.getMerchantId() != null && !coupon.getMerchantId().equals(merchantId)) {
                return Result.fail(400, "优惠券不适用于该商家");
            }
            // 计算折扣
            CouponStrategy strategy = couponStrategyContext.getStrategy(coupon);
            discountAmount = strategy.calculateDiscount(totalAmount, coupon);
            if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return Result.fail(400, "不满足优惠券使用条件");
            }
        }

        BigDecimal finalAmount = totalAmount.add(deliveryFee).subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) finalAmount = BigDecimal.ZERO;

        // 7. 创建订单
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setAddressId(addressId);
        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(deliveryFee);
        order.setDiscountAmount(discountAmount);
        order.setFinalAmount(finalAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT.getCode());
        order.setRemark(remark);
        orderMapper.insert(order);

        // 8. 生成订单明细
        for (Cart cart : cartItems) {
            MenuItem menu = menuItemMapper.selectById(cart.getMenuItemId());
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setMenuItemId(menu.getId());
            item.setItemName(menu.getName());
            item.setPrice(menu.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setTotalPrice(menu.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            orderItemMapper.insert(item);
        }

        // 8. 清空购物车
        cartMapper.deleteByUserId(userId);

        // 9. 记录订单状态日志
        addStatusLog(order.getId(), null, OrderStatus.PENDING_PAYMENT.getCode(), "user", "提交订单");

        return Result.success(order);
    }

    /**
     * 通用状态变更方法，使用策略模式
     */
    @Transactional
    public Result changeOrderStatus(Long orderId, OrderStatus targetStatus, String operator, String remark) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }

        // 获取对应状态的策略并执行
        OrderStateStrategy strategy = orderStateContext.getStrategy(targetStatus);
        strategy.execute(order, operator);

        // 更新订单状态和时间
        orderMapper.updateStatus(order.getId(), order.getStatus(), order.getPayTime(),
                order.getAcceptTime(), order.getDeliveryTime(), order.getCompleteTime(), order.getCancelTime());

        // 记录日志
        addStatusLog(orderId, order.getStatus(), targetStatus.getCode(), operator, remark);
        return Result.success("状态变更成功");
    }

    /**
     * 用户支付（模拟，实际需对接支付）
     */
    public Result payOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.fail(403, "无权操作");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT.getCode()) {
            return Result.fail(400, "当前状态不可支付");
        }
        // 使用策略变更到已支付
        return changeOrderStatus(orderId, OrderStatus.PAID, "user", "用户支付");
    }

    /**
     * 查询用户订单列表
     */
    public Result getUserOrders(Long userId) {
        List<Order> orders = orderMapper.selectByUserId(userId);
        return Result.success(orders);
    }

    /**
     * 查询商家订单列表
     */
    public Result getMerchantOrders(Long merchantId) {
        List<Order> orders = orderMapper.selectByMerchantId(merchantId);
        return Result.success(orders);
    }

    /**
     * 查询订单详情（含订单项）
     */
    public Result getOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.fail(404, "订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        // 可以封装一个 DTO 返回，这里简单把 items 放到一个 Map
        return Result.success(new Object[]{order, items});
    }

    private void addStatusLog(Long orderId, Integer fromStatus, Integer toStatus, String operator, String remark) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setOperator(operator);
        log.setRemark(remark);
        orderStatusLogMapper.insert(log);
    }

    public Order getOrderById(Long orderId) {
        return orderMapper.selectById(orderId);
    }
}
