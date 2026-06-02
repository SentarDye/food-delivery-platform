package com.food.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Merchant {
    private Long id;
    private String name;
    private String phone;
    private String password;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String businessHours;
    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    // 预计送达分钟数
    private Integer deliveryTime;       // 预计送达分钟数
    private Integer status;             // 0待审核 1通过 2驳回
    private String rejectReason;        // 驳回原因
    private BigDecimal commissionRate;  // 平台抽成比例%
    private Integer isDeleted;          // 0正常 1已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
