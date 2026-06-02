// 技术栈：Lombok
package com.food.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private Long addressId;
    private BigDecimal totalAmount;      // 商品总金额
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;      // 实付
    private Integer status;
    private LocalDateTime payTime;
    private LocalDateTime acceptTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    private String remark;
    private LocalDateTime createTime;
}
