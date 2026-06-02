// 技术栈：Lombok
package com.food.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Rider {
    private Long id;
    private String phone;
    private String password;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer status;       // 0空闲 1配送中 2休息
    private Integer isDeleted;    // 0正常 1已删除
    private LocalDateTime createTime;
}
