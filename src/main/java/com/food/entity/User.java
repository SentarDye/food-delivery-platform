package com.food.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String phone;
    private String password;
    private String nickname;
    private String avatar;
    private BigDecimal balance;
    private Integer isDeleted;  // 0正常 1已删除
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
