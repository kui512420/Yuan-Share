/*
 Navicat Premium Data Transfer

 Source Server         : 魁魁
 Source Server Type    : MySQL
 Source Server Version : 80038
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80038
 File Encoding         : 65001

 Date: 31/05/2025 23:14:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `article_id` int(0) NOT NULL AUTO_INCREMENT,
  `article_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `article_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `article_tag` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `article_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `article_authorid` int(0) NOT NULL,
  `publish_time` timestamp(0) NULL,
  `last_time` timestamp(0) NULL DEFAULT NULL,
  `article_status` int(0) NOT NULL COMMENT '0 1',
  `article_type` int(0) NOT NULL,
  `article_author` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `top` int(0) NOT NULL DEFAULT 0 COMMENT '0不是顶置，1 是',
  `like_count` int(10) UNSIGNED ZEROFILL NULL DEFAULT 0000000000,
  PRIMARY KEY (`article_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 150 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for articlelike
-- ----------------------------
DROP TABLE IF EXISTS `articlelike`;
CREATE TABLE `articlelike`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `articleId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `userId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `likeTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 180 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(0) NOT NULL COMMENT '评论人的id',
  `article_id` int(0) NOT NULL COMMENT '被评论文章的id',
  `comment_date` datetime(0) NULL DEFAULT NULL COMMENT '评论的时间',
  `comment_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评论的内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `goods_id` int(0) NOT NULL AUTO_INCREMENT,
  `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `goods_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `goods_price` decimal(10, 2) NULL DEFAULT NULL,
  `goods_updatetime` datetime(0) NULL DEFAULT NULL,
  `goods_createtime` datetime(0) NULL DEFAULT NULL,
  `goods_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `goods_count` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `goods_sales` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `goods_status` int(0) NULL DEFAULT 1,
  PRIMARY KEY (`goods_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for info
-- ----------------------------
DROP TABLE IF EXISTS `info`;
CREATE TABLE `info`  (
  `infoid` int(0) NOT NULL AUTO_INCREMENT,
  `userid` int(0) NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `info` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `time` timestamp(0) NULL DEFAULT NULL,
  PRIMARY KEY (`infoid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 175 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `login_time` timestamp(0) NULL DEFAULT NULL,
  `login_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `notice_id` int(0) NOT NULL AUTO_INCREMENT,
  `notice_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `notice_issuer` int(0) NULL DEFAULT NULL,
  `notice_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `notice_createtime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sign
-- ----------------------------
DROP TABLE IF EXISTS `sign`;
CREATE TABLE `sign`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `userid` int(0) NOT NULL,
  `time` datetime(0) NULL,
  `bean` int(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `headsrc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_estonian_ci NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_estonian_ci NULL DEFAULT NULL,
  `status` int(0) NULL DEFAULT 1 COMMENT '0:停用，1正常',
  `type` int(0) NULL DEFAULT 1 COMMENT '0:管理员，1:用户',
  `register_time` timestamp(0) NULL DEFAULT NULL,
  `last_login` timestamp(0) NULL DEFAULT NULL,
  `bean` int(0) NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_estonian_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 89138 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_estonian_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_file
-- ----------------------------
DROP TABLE IF EXISTS `user_file`;
CREATE TABLE `user_file`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `user_id` int(0) NULL DEFAULT NULL,
  `file_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Procedure structure for InsertLoginLogData
-- ----------------------------
DROP PROCEDURE IF EXISTS `InsertLoginLogData`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertLoginLogData`()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 80 DO
        INSERT INTO `login_log` (`id`, `username`, `login_time`, `login_ip`, `login_agent`)
        VALUES (
            i,
            CONCAT('user', i),
            DATE_ADD('2024-01-01 00:00:00', INTERVAL FLOOR(RAND() * 30) DAY),
            CONCAT('192.168.1.', i),
            CONCAT('Mozilla/5.0 (Device', i, ') AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36')
        );
        SET i = i + 1;
    END WHILE;
END
;;
delimiter ;

-- ----------------------------
-- Procedure structure for InsertTestData
-- ----------------------------
DROP PROCEDURE IF EXISTS `InsertTestData`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertTestData`()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 300 DO
        INSERT INTO `a` (
            `username`,
            `password`,
            `headsrc`,
            `status`,
            `type`,
            `register_time`,
            `last_login`,
            `bean`
        ) VALUES (
            CONCAT('user', i),
            MD5(CONCAT('password', i)),
            CONCAT('https://example.com/headsrc/', i, '.jpg'),
            FLOOR(RAND() * 2), -- 随机生成 0 或 1
            FLOOR(RAND() * 2), -- 随机生成 0 或 1
            -- 随机生成过去一年的注册时间
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY), 
            -- 随机生成过去一个月的最后登录时间
            DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY), 
            FLOOR(RAND() * 1000) -- 随机生成 0 到 999 的豆子数
        );
        SET i = i + 1;
    END WHILE;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
