// 技术栈：Lombok
package com.food.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Delivery {
    private Long id;
    private Long orderId;
    private Long riderId;
    private LocalDateTime pickupTime;     // 取餐时间
    private LocalDateTime deliveryTime;   // 送达时间
    private Integer status;               // 0待接单 1已取货 2已送达
}
