// 技术栈：Lombok
package com.food.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private Long id;
    private Long merchantId;      // NULL 表示平台券
    private String name;
    private Integer type;         // 1满减 2折扣
    private BigDecimal discount;  // 满减金额 或 折扣比例(0-100)
    private BigDecimal minAmount; // 使用门槛
    private Integer totalQuantity;// 总发行量，NULL无限
    private Integer userLimit;    // 每人限领张数
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
}