// 技术栈：Spring Boot + JWT
package com.food.controller;

import com.food.common.Result;
import com.food.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/rider/delivery")
public class DeliveryController extends BaseController {

    @Autowired
    private DeliveryService deliveryService;

    // 获取待抢配送单列表
    @GetMapping("/pending")
    public Result pendingList() {
        return deliveryService.getPendingDeliveries();
    }

    // 抢单
    @PostMapping("/grab")
    public Result grab(@RequestParam Long deliveryId,
                       HttpServletRequest request) {
        Long riderId = getRiderId(request);
        return deliveryService.grabOrder(riderId, deliveryId);
    }

    // 取餐确认
    @PostMapping("/pickup")
    public Result pickup(@RequestParam Long deliveryId,
                         HttpServletRequest request) {
        Long riderId = getRiderId(request);
        return deliveryService.pickupOrder(riderId, deliveryId);
    }

    // 送达确认
    @PostMapping("/complete")
    public Result complete(@RequestParam Long deliveryId,
                           HttpServletRequest request) {
        Long riderId = getRiderId(request);
        return deliveryService.completeOrder(riderId, deliveryId);
    }

    // 骑手收益
    @GetMapping("/stats")
    public Result stats(HttpServletRequest request) {
        Long riderId = getRiderId(request);
        return deliveryService.getRiderStats(riderId);
    }
}