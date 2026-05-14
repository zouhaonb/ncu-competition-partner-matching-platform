-- =============================================
-- 高校竞赛队友匹配平台 - 数据库初始化脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS match_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE match_platform;

-- -----------------------------------------
-- 用户表
-- -----------------------------------------
DROP TABLE IF EXISTS `application`;
DROP TABLE IF EXISTS `recruitment_required_tag`;
DROP TABLE IF EXISTS `recruitment`;
DROP TABLE IF EXISTS `user_tag`;
DROP TABLE IF EXISTS `tag`;
DROP TABLE IF EXISTS `competition_category`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `student_id` VARCHAR(20) NOT NULL COMMENT '学号，用于登录',
    `name` VARCHAR(50) NOT NULL COMMENT '姓名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `qq` VARCHAR(20) DEFAULT NULL COMMENT 'QQ号',
    `intro` VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER=普通用户, ADMIN=管理员',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- -----------------------------------------
-- 标签表
-- -----------------------------------------
CREATE TABLE `tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能标签表';

-- -----------------------------------------
-- 用户-标签关联表
-- -----------------------------------------
CREATE TABLE `user_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `proficiency` TINYINT NOT NULL DEFAULT 1 COMMENT '熟练度：1=了解, 2=掌握, 3=精通',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tag` (`user_id`, `tag_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签关联表';

-- -----------------------------------------
-- 竞赛类别表
-- -----------------------------------------
CREATE TABLE `competition_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '类别ID',
    `name` VARCHAR(50) NOT NULL COMMENT '类别名称',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='竞赛类别表';

-- -----------------------------------------
-- 招募信息表
-- -----------------------------------------
CREATE TABLE `recruitment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '招募ID',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者用户ID',
    `title` VARCHAR(200) NOT NULL COMMENT '招募标题',
    `category_id` BIGINT NOT NULL COMMENT '竞赛类别ID',
    `required_number` INT NOT NULL DEFAULT 1 COMMENT '需要人数',
    `description` TEXT DEFAULT NULL COMMENT '招募描述',
    `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN' COMMENT '状态：OPEN=开放中, CLOSED=已关闭',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    PRIMARY KEY (`id`),
    KEY `idx_publisher_id` (`publisher_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招募信息表';

-- -----------------------------------------
-- 招募-所需标签关联表
-- -----------------------------------------
CREATE TABLE `recruitment_required_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `recruitment_id` BIGINT NOT NULL COMMENT '招募ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_recruitment_tag` (`recruitment_id`, `tag_id`),
    KEY `idx_recruitment_id` (`recruitment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招募所需标签表';

-- -----------------------------------------
-- 申请表
-- -----------------------------------------
CREATE TABLE `application` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `recruitment_id` BIGINT NOT NULL COMMENT '招募ID',
    `applicant_id` BIGINT NOT NULL COMMENT '申请者用户ID',
    `reason` VARCHAR(500) DEFAULT NULL COMMENT '申请理由',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING=待审核, ACCEPTED=已通过, REJECTED=已拒绝',
    `apply_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_recruitment_applicant` (`recruitment_id`, `applicant_id`),
    KEY `idx_recruitment_id` (`recruitment_id`),
    KEY `idx_applicant_id` (`applicant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='申请表';

-- =============================================
-- 初始数据
-- =============================================

-- 标签
INSERT INTO `tag` (`name`) VALUES
('Java'), ('Python'), ('C++'), ('数据分析'), ('机器学习'),
('前端开发'), ('后端开发'), ('UI设计'), ('项目管理'), ('论文写作'),
('算法设计'), ('MATLAB'), ('单片机'), ('嵌入式'), ('FPGA');

-- 竞赛类别
INSERT INTO `competition_category` (`name`) VALUES
('数学建模'), ('创新创业'), ('程序设计'), ('电子设计'), ('机械创新'), ('英语竞赛'), ('挑战杯'), ('其他');

-- 测试用户（密码均为 123456，BCrypt加密）
INSERT INTO `user` (`student_id`, `name`, `password`, `phone`, `qq`, `intro`, `role`) VALUES
('8008123001', '张三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138001', '10001', '计算机专业大三学生，热爱算法和数学建模', 'USER'),
('8008123002', '李四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138002', '10002', '数据科学专业，擅长Python和数据分析', 'USER'),
('root', '管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '', '', '系统管理员', 'ADMIN');

-- 张三的标签: Java(精通), Python(掌握), 算法设计(精通), 后端开发(精通), 机器学习(了解)
INSERT INTO `user_tag` (`user_id`, `tag_id`, `proficiency`) VALUES
(1, 1, 3), (1, 2, 2), (1, 11, 3), (1, 7, 3), (1, 5, 1);

-- 李四的标签: Python(精通), 数据分析(掌握), 机器学习(精通), 前端开发(了解), 论文写作(掌握)
INSERT INTO `user_tag` (`user_id`, `tag_id`, `proficiency`) VALUES
(2, 2, 3), (2, 4, 2), (2, 5, 3), (2, 6, 1), (2, 10, 2);

-- 张三发布一条招募
INSERT INTO `recruitment` (`publisher_id`, `title`, `category_id`, `required_number`, `description`, `status`) VALUES
(1, '数学建模美赛队友招募', 1, 3, '参加2024年美赛MCM，需要擅长建模和编程的队友，有经验者优先。', 'OPEN');

-- 该招募需要的标签（算法设计、Python、论文写作）
INSERT INTO `recruitment_required_tag` (`recruitment_id`, `tag_id`) VALUES
(1, 11), (1, 2), (1, 10);
