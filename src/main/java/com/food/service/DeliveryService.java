// 技术栈：Spring Boot + MyBatis + 策略模式
package com.food.service;

import com.food.common.Result;
import com.food.entity.*;
import com.food.enums.OrderStatus;
import com.food.mapper.*;
import com.food.strategy.OrderStateContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryMapper deliveryMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RiderMapper riderMapper;
    @Autowired
    private OrderService orderService;

    /**
     * 生成待接单配送单（商家接单后调用）
     */
    public void createDelivery(Long orderId) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setStatus(0);
        deliveryMapper.insert(delivery);
    }

    /**
     * 骑手抢单
     */
    @Transactional
    public Result grabOrder(Long riderId, Long deliveryId) {
        // 检查骑手状态（必须空闲）
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null || rider.getStatus() != 0) {
            return Result.fail(400, "骑手不可抢单");
        }
        // 获取配送单
        Delivery delivery = deliveryMapper.selectById(deliveryId);
        if (delivery == null || delivery.getStatus() != 0) {
            return Result.fail(400, "配送单已被抢或不存在");
        }
        // 检查订单状态是否为已接单（配送中才能抢）
        Order order = orderMapper.selectById(delivery.getOrderId());
        if (order == null || order.getStatus() != OrderStatus.ACCEPTED.getCode()) {
            return Result.fail(400, "订单状态不可配送");
        }

        // 绑定骑手，更新配送单状态为已取货？这里我们暂将status保持0，待取餐时改为1
        // 根据流程：抢单后骑手去取餐，所以 delivery.status 仍为0，但 rider_id 已绑定
        delivery.setRiderId(riderId);
        deliveryMapper.updateById(delivery);

        // 骑手状态改为配送中
        rider.setStatus(1);
        riderMapper.updateById(rider);

        // 订单状态改为配送中
        orderService.changeOrderStatus(order.getId(), OrderStatus.DELIVERING, "rider", "骑手抢单");

        return Result.success("抢单成功");
    }

    /**
     * 骑手取餐确认
     */
    @Transactional
    public Result pickupOrder(Long riderId, Long deliveryId) {
        Delivery delivery = deliveryMapper.selectById(deliveryId);
        if (delivery == null || !delivery.getRiderId().equals(riderId)) {
            return Result.fail(403, "无权操作");
        }
        if (delivery.getStatus() != 0) {
            return Result.fail(400, "已取餐或已完成");
        }
        delivery.setStatus(1);
        delivery.setPickupTime(LocalDateTime.now());
        deliveryMapper.updateById(delivery);
        return Result.success("取餐成功");
    }

    /**
     * 骑手送达确认
     */
    @Transactional
    public Result completeOrder(Long riderId, Long deliveryId) {
        Delivery delivery = deliveryMapper.selectById(deliveryId);
        if (delivery == null || !delivery.getRiderId().equals(riderId)) {
            return Result.fail(403, "无权操作");
        }
        if (delivery.getStatus() != 1) {
            return Result.fail(400, "未取餐或已送达");
        }
        delivery.setStatus(2);
        delivery.setDeliveryTime(LocalDateTime.now());
        deliveryMapper.updateById(delivery);

        // 订单状态改为已完成
        orderService.changeOrderStatus(delivery.getOrderId(), OrderStatus.COMPLETED, "rider", "骑手送达");

        // 骑手状态恢复空闲
        Rider rider = riderMapper.selectById(riderId);
        rider.setStatus(0);
        riderMapper.updateById(rider);

        return Result.success("送达成功");
    }

    /**
     * 骑手收益统计（简单版：完成单数）
     */
    public Result getRiderStats(Long riderId) {
        List<Delivery> completed = deliveryMapper.selectByRiderId(riderId);
        long completedCount = completed.stream().filter(d -> d.getStatus() == 2).count();
        // 后续可结合配送费计算收益
        return Result.success("完成单数: " + completedCount);
    }

    public Result getPendingDeliveries() {
        List<Delivery> list = deliveryMapper.selectPending();
        return Result.success(list);
    }
}
