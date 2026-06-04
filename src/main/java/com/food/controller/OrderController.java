// 技术栈：Spring Boot + JWT
package com.food.controller;

import com.food.common.Result;
import com.food.entity.Order;
import com.food.enums.OrderStatus;
import com.food.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    // 用户端：下单
    @PostMapping("/place")
    public Result placeOrder(@RequestParam Long addressId,
                             @RequestParam(required = false) Long userCouponId,
                             @RequestParam(required = false) String remark,
                             HttpServletRequest request) {
        Long userId = getUserId(request);
        return orderService.placeOrder(userId, addressId, userCouponId, remark);
    }

    // 用户端：支付
    @PostMapping("/pay")
    public Result pay(@RequestParam Long orderId,
                      HttpServletRequest request) {
        Long userId = getUserId(request);
        return orderService.payOrder(userId, orderId);
    }

    // 用户端：查看我的订单
    @GetMapping("/user/list")
    public Result userOrderList(HttpServletRequest request) {
        Long userId = getUserId(request);
        return orderService.getUserOrders(userId);
    }

    // 商家端：查看本店订单
    @GetMapping("/merchant/list")
    public Result merchantOrderList(HttpServletRequest request) {
        Long merchantId = getMerchantId(request);
        return orderService.getMerchantOrders(merchantId);
    }

    // 商家端：接单
    @PostMapping("/merchant/accept")
    public Result acceptOrder(@RequestParam Long orderId,
                              HttpServletRequest request) {
        Long merchantId = getMerchantId(request);
        // 校验订单是否属于该商家
        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getMerchantId().equals(merchantId)) {
            return Result.fail(403, "无权操作");
        }
        return orderService.changeOrderStatus(orderId, OrderStatus.ACCEPTED, "merchant", "商家接单");
    }

    // 通用：查询订单详情
    @GetMapping("/detail")
    public Result detail(@RequestParam Long orderId) {
        return orderService.getOrderDetail(orderId);
    }
}
