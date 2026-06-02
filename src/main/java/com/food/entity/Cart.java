// 技术栈：Lombok
package com.food.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Cart {
    private Long id;
    private Long userId;
    private Long merchantId;
    private Long menuItemId;
    private Integer quantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
