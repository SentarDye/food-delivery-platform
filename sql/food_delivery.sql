CREATE DATABASE IF NOT EXISTS food_delivery;

use food_delivery;

-- 1. 用户表（原 user 表，改名 customer，避免保留字）
CREATE TABLE `customer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `phone` VARCHAR(20) NOT NULL,
    `password` VARCHAR(200) NOT NULL,
    `nickname` VARCHAR(50) DEFAULT NULL,
    `avatar` VARCHAR(255) DEFAULT NULL,
    `balance` DECIMAL(10,2) DEFAULT 0.00,
    `is_deleted` TINYINT DEFAULT 0 COMMENT '0正常 1已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone_deleted` (`phone`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 商家表
CREATE TABLE `merchant` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `password` VARCHAR(200) NOT NULL,
    `address` VARCHAR(255) DEFAULT NULL,
    `latitude` DECIMAL(10,8) DEFAULT NULL,
    `longitude` DECIMAL(11,8) DEFAULT NULL,
    `business_hours` VARCHAR(255) DEFAULT NULL COMMENT '营业时间段，如 09:00-21:00',
    `delivery_fee` DECIMAL(10,2) DEFAULT 0.00,
    `min_order_amount` DECIMAL(10,2) DEFAULT 0.00,
    `delivery_time` INT DEFAULT NULL COMMENT '预计送达分钟数',
    `status` TINYINT DEFAULT 0 COMMENT '0待审核 1通过 2驳回',
    `reject_reason` VARCHAR(255) DEFAULT NULL,
    `commission_rate` DECIMAL(5,2) DEFAULT 15.00 COMMENT '平台抽成比例%',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '0正常 1已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone_deleted` (`phone`, `is_deleted`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- 3. 菜品分类表
CREATE TABLE `menu_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `merchant_id` BIGINT NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `sort_order` INT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

-- 4. 菜单项表
CREATE TABLE `menu_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `merchant_id` BIGINT NOT NULL,
    `category_id` BIGINT DEFAULT NULL,
    `name` VARCHAR(100) NOT NULL,
    `price` DECIMAL(10,2) NOT NULL,
    `image` VARCHAR(255) DEFAULT NULL,
    `description` VARCHAR(500) DEFAULT NULL,
    `stock` INT DEFAULT -1 COMMENT '-1表示无限',
    `sort_order` INT DEFAULT 0,
    `sell_count` INT DEFAULT 0,
    `max_purchase_per_user` INT DEFAULT 0 COMMENT '0表示不限购',
    `status` TINYINT DEFAULT 1 COMMENT '0下架 1上架',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '0正常 1已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`category_id`) REFERENCES `menu_category`(`id`) ON DELETE SET NULL,
    KEY `idx_merchant_status_sort` (`merchant_id`, `status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单项表';

-- 5. 用户地址表
CREATE TABLE `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `receiver_name` VARCHAR(50) NOT NULL,
    `receiver_phone` VARCHAR(20) NOT NULL,
    `province` VARCHAR(50) DEFAULT NULL,
    `city` VARCHAR(50) DEFAULT NULL,
    `district` VARCHAR(50) DEFAULT NULL,
    `detail` VARCHAR(255) NOT NULL,
    `latitude` DECIMAL(10,8) DEFAULT NULL,
    `longitude` DECIMAL(11,8) DEFAULT NULL,
    `is_default` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `customer`(`id`) ON DELETE CASCADE,
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 6. 骑手表
CREATE TABLE `rider` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `phone` VARCHAR(20) NOT NULL,
    `password` VARCHAR(200) NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `latitude` DECIMAL(10,8) DEFAULT NULL,
    `longitude` DECIMAL(11,8) DEFAULT NULL,
    `status` TINYINT DEFAULT 0 COMMENT '0空闲 1配送中 2休息',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '0正常 1已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone_deleted` (`phone`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='骑手表';

-- 7. 优惠券模板表
CREATE TABLE `coupon` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `merchant_id` BIGINT DEFAULT NULL COMMENT 'NULL表示平台券',
    `name` VARCHAR(100) NOT NULL,
    `type` TINYINT NOT NULL COMMENT '1满减 2折扣',
    `discount` DECIMAL(10,2) NOT NULL COMMENT '满减金额或折扣比例(0-100)',
    `min_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '使用门槛',
    `total_quantity` INT DEFAULT NULL COMMENT '总发行量，NULL表示无限',
    `user_limit` INT DEFAULT 1 COMMENT '每人限领张数',
    `start_time` DATETIME NOT NULL,
    `end_time` DATETIME NOT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`) ON DELETE CASCADE,
    KEY `idx_merchant_id` (`merchant_id`),
    KEY `idx_time` (`start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

-- 8. 用户领券表
CREATE TABLE `user_coupon` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `coupon_id` BIGINT NOT NULL,
    `status` TINYINT DEFAULT 0 COMMENT '0未使用 1已使用 2已过期',
    `used_order_id` BIGINT DEFAULT NULL,
    `receive_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `use_time` DATETIME DEFAULT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`user_id`) REFERENCES `customer`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`coupon_id`) REFERENCES `coupon`(`id`),
    KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户领券表';

-- 9. 订单主表
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_no` VARCHAR(32) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `merchant_id` BIGINT NOT NULL,
    `address_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '商品总金额',
    `delivery_fee` DECIMAL(10,2) DEFAULT 0.00,
    `discount_amount` DECIMAL(10,2) DEFAULT 0.00,
    `final_amount` DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `status` TINYINT NOT NULL COMMENT '0待支付 1已支付 2商家接单 3配送中 4已完成 5已取消 6退款中',
    `pay_time` DATETIME DEFAULT NULL,
    `accept_time` DATETIME DEFAULT NULL,
    `delivery_time` DATETIME DEFAULT NULL,
    `complete_time` DATETIME DEFAULT NULL,
    `cancel_time` DATETIME DEFAULT NULL,
    `remark` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    FOREIGN KEY (`user_id`) REFERENCES `customer`(`id`),
    FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`),
    FOREIGN KEY (`address_id`) REFERENCES `address`(`id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_merchant_status` (`merchant_id`, `status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 10. 订单明细表
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `menu_item_id` BIGINT NOT NULL,
    `item_name` VARCHAR(100) NOT NULL COMMENT '快照名称',
    `price` DECIMAL(10,2) NOT NULL,
    `quantity` INT NOT NULL,
    `total_price` DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 11. 订单状态日志表
CREATE TABLE `order_status_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `from_status` TINYINT DEFAULT NULL,
    `to_status` TINYINT NOT NULL,
    `operator` VARCHAR(50) DEFAULT NULL COMMENT 'user/merchant/rider/system',
    `remark` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态日志表';

-- 12. 配送单表
CREATE TABLE `delivery` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `rider_id` BIGINT NOT NULL,
    `pickup_time` DATETIME DEFAULT NULL,
    `delivery_time` DATETIME DEFAULT NULL,
    `status` TINYINT DEFAULT 0 COMMENT '0待接单 1已取货 2已送达',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
    FOREIGN KEY (`rider_id`) REFERENCES `rider`(`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_rider_status` (`rider_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送单表';

-- 13. 评价表
CREATE TABLE `rating` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `merchant_id` BIGINT NOT NULL,
    `score` TINYINT NOT NULL CHECK (score BETWEEN 1 AND 5),
    `content` VARCHAR(500) DEFAULT NULL,
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '多图逗号分隔',
    `reply` VARCHAR(500) DEFAULT NULL COMMENT '商家回复',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `customer`(`id`),
    FOREIGN KEY (`merchant_id`) REFERENCES `merchant`(`id`),
    UNIQUE KEY `uk_order_id` (`order_id`) COMMENT '一单只能评价一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 14. 购物车表
CREATE TABLE `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `merchant_id` BIGINT NOT NULL,
    `menu_item_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_item` (`user_id`, `menu_item_id`),
    FOREIGN KEY (`user_id`) REFERENCES `customer`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`menu_item_id`) REFERENCES `menu_item`(`id`),
    KEY `idx_user_merchant` (`user_id`, `merchant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 15. 订单历史表（用于归档超过6个月的已完成订单）
-- 结构与 orders 表几乎相同，但增加 archive_time 字段
CREATE TABLE `orders_history` (
    `id` BIGINT NOT NULL,
    `order_no` VARCHAR(32) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `merchant_id` BIGINT NOT NULL,
    `address_id` BIGINT NOT NULL,
    `total_amount` DECIMAL(10,2) NOT NULL,
    `delivery_fee` DECIMAL(10,2) DEFAULT 0.00,
    `discount_amount` DECIMAL(10,2) DEFAULT 0.00,
    `final_amount` DECIMAL(10,2) NOT NULL,
    `status` TINYINT NOT NULL,
    `pay_time` DATETIME DEFAULT NULL,
    `accept_time` DATETIME DEFAULT NULL,
    `delivery_time` DATETIME DEFAULT NULL,
    `complete_time` DATETIME DEFAULT NULL,
    `cancel_time` DATETIME DEFAULT NULL,
    `remark` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME NOT NULL,
    `archive_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '归档时间',
    PRIMARY KEY (`id`),
    KEY `idx_archive_time` (`archive_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单历史表（归档表）';