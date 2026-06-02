// 技术栈：Lombok
package com.food.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Address {
    private Long id;
    private Long userId;          // 对应 customer.id
    private String receiverName;
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer isDefault;   // 0非默认 1默认
    private LocalDateTime createTime;
}
