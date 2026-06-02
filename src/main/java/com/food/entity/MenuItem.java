package com.food.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MenuItem {
    private Long id;
    private Long merchantId;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private String image;
    private String description;
    private Integer stock;              // -1表示无限
    private Integer sortOrder;
    private Integer sellCount;
    private Integer maxPurchasePerUser; // 0表示不限购
    private Integer status;             // 0下架 1上架
    private Integer isDeleted;          // 0正常 1已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
