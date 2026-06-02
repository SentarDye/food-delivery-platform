// 技术栈：Lombok
package com.food.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderStatusLog {
    private Long id;
    private Long orderId;
    private Integer fromStatus;
    private Integer toStatus;
    private String operator;
    private String remark;
    private LocalDateTime createTime;
}
