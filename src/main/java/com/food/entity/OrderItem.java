// 技术栈：Lombok
package com.food.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long menuItemId;
    private String itemName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalPrice;
}
