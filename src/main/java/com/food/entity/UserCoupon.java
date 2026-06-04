// 技术栈：Lombok
package com.food.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private Integer status;       // 0未使用 1已使用 2已过期
    private Long usedOrderId;
    private LocalDateTime receiveTime;
    private LocalDateTime useTime;
}
