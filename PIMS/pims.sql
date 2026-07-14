-- ========================================
-- 物业管理系统（PIMS）数据库初始化脚本
-- 数据库：MySQL 8.0
-- 字符集：utf8mb4
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `pims` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `pims`;

-- ========================================
-- 1. 用户表
-- ========================================
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `room_number` VARCHAR(20) NOT NULL COMMENT '房号',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0未入住，1已入住',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除，1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_room_number` (`room_number`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ========================================
-- 2. 管理员表
-- ========================================
DROP TABLE IF EXISTS `t_admin`;
CREATE TABLE `t_admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `is_super_admin` TINYINT NOT NULL DEFAULT 0 COMMENT '是否超级管理员：0否，1是',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- ========================================
-- 3. 住户信息表
-- ========================================
DROP TABLE IF EXISTS `t_resident`;
CREATE TABLE `t_resident` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '住户ID',
  `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '住户姓名',
  `id_card` VARCHAR(18) NOT NULL COMMENT '身份证号',
  `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
  `room_number` VARCHAR(20) NOT NULL COMMENT '房号',
  `area` DECIMAL(10,2) NOT NULL COMMENT '住房面积（平方米）',
  `price_per_sqm` DECIMAL(10,2) NOT NULL DEFAULT 2.00 COMMENT '物业费单价（元/平方米）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '入住状态：0未入住，1已入住',
  `register_time` DATETIME NOT NULL COMMENT '注册时间',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_room_number` (`room_number`),
  KEY `idx_name` (`name`),
  KEY `idx_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='住户信息表';

-- ========================================
-- 4. 入住审核表
-- ========================================
DROP TABLE IF EXISTS `t_audit_record`;
CREATE TABLE `t_audit_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
  `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '住户姓名',
  `id_card` VARCHAR(18) NOT NULL COMMENT '身份证号',
  `area` DECIMAL(10,2) NOT NULL COMMENT '住房面积',
  `room_number` VARCHAR(20) NOT NULL COMMENT '房号',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态：0待审核，1通过，2拒绝',
  `admin_id` BIGINT DEFAULT NULL COMMENT '审核管理员ID',
  `audit_time` DATETIME DEFAULT NULL COMMENT '审核时间',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_admin_id` (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入住审核表';

-- ========================================
-- 5. 缴费记录表
-- ========================================
DROP TABLE IF EXISTS `t_payment_record`;
CREATE TABLE `t_payment_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '缴费记录ID',
  `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
  `room_number` VARCHAR(20) NOT NULL COMMENT '房号',
  `area` DECIMAL(10,2) NOT NULL COMMENT '住房面积',
  `price_per_sqm` DECIMAL(10,2) NOT NULL COMMENT '物业费单价',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
  `payment_time` DATETIME NOT NULL COMMENT '缴费时间',
  `payment_month` VARCHAR(7) NOT NULL COMMENT '缴费月份（YYYY-MM）',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_payment_time` (`payment_time`),
  KEY `idx_payment_month` (`payment_month`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费记录表';

-- ========================================
-- 6. 系统消息表
-- ========================================
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0未读，1已读',
  `message_type` TINYINT NOT NULL DEFAULT 1 COMMENT '消息类型：1缴费提醒',
  `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统消息表';

-- ========================================
-- 初始化数据
-- ========================================

-- 插入超级管理员（用户名：admin，密码：admin123）
-- 密码使用BCrypt加密，这里使用的是 admin123 的BCrypt加密结果
INSERT INTO `t_admin` (`id`, `username`, `password`, `is_super_admin`, `created_time`, `updated_time`) 
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, NOW(), NOW());

-- ========================================
-- 测试数据（可选）
-- ========================================

-- 插入测试用户（用户名：A101，密码：abc123）
-- INSERT INTO `t_user` (`phone`, `password`, `room_number`, `status`) 
-- VALUES ('13800138001', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'A101', 0);

-- INSERT INTO `t_user` (`phone`, `password`, `room_number`, `status`) 
-- VALUES ('13800138002', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'A102', 1);

-- 插入测试住户信息
-- INSERT INTO `t_resident` (`user_id`, `name`, `id_card`, `phone`, `room_number`, `area`, `price_per_sqm`, `status`, `register_time`) 
-- VALUES (1001, '张三', '110101199001011234', '13800138002', 'A102', 80.50, 2.00, 1, '2024-01-15 10:00:00');

-- 插入测试缴费记录
-- INSERT INTO `t_payment_record` (`user_id`, `room_number`, `area`, `price_per_sqm`, `amount`, `payment_time`, `payment_month`) 
-- VALUES (1001, 'A102', 80.50, 2.00, 161.00, '2024-01-20 14:30:00', '2024-01');

-- ========================================
-- 说明
-- ========================================
-- 1. 默认超级管理员账号：admin / admin123
-- 2. 所有密码字段使用BCrypt加密
-- 3. 测试数据已注释，根据需要解除注释
-- 4. 数据库字符集：utf8mb4，支持emoji等特殊字符
-- 5. 所有表使用InnoDB引擎，支持事务
