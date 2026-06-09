/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80409 (8.4.9)
 Source Host           : localhost:3306
 Source Schema         : secondhand

 Target Server Type    : MySQL
 Target Server Version : 80409 (8.4.9)
 File Encoding         : 65001

 Date: 08/06/2026 23:07:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` int NOT NULL COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '说明',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '我们为您的每一笔交易保驾护航！', 'http://127.0.0.1:8080/files/07bbe4b58a684fdf9fa14cb313d89bb9.jpg');
INSERT INTO `banner` VALUES (2, '每一件二手物品，都是独一无二的故事！', 'http://127.0.0.1:8080/files/77f0c22b43eb4f3790dbc76215759a88.jpg');
INSERT INTO `banner` VALUES (3, '让闲置物品重获新生，环保又省钱！', 'http://127.0.0.1:8080/files/18ecf61aae4147958a6bc264da45aaf1.jpg');

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品名称',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品图',
  `img_list` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品多图',
  `type_id` int NULL DEFAULT NULL COMMENT '分类id',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '价格',
  `re_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原来价格',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '商品详情信息',
  `place` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '地点',
  `shipment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发货设置',
  `user_id` int NULL DEFAULT NULL COMMENT '发布用户id',
  `num` int NULL DEFAULT NULL COMMENT '浏览量',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品状态',
  `quality` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '商品成色',
  `date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '发布日期',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 107 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES (101, 'NUTO酒红色短袖风衣外套', 'http://127.0.0.1:8080/files/goods-nuto-red-coat.jpg', 'http://127.0.0.1:8080/files/goods-nuto-red-coat.jpg', 2, 128.00, 269.00, '酒红色短袖风衣外套，版型挺括，适合春秋搭配，图片实拍，成色较新。', '广东广州', '包邮', 9, 18, '上架', '九成新', '2026-06-07');
INSERT INTO `goods` VALUES (102, '富士 instax mini 拍立得相纸', 'http://127.0.0.1:8080/files/goods-instax-film.jpg', 'http://127.0.0.1:8080/files/goods-instax-film.jpg', 1, 49.90, 69.00, '富士 instax mini 拍立得相纸，20张装，适合 mini 系列相机使用。', '浙江杭州', '不包邮', 9, 32, '上架', '全新', '2026-06-07');
INSERT INTO `goods` VALUES (103, '白色字母短袖 T 恤', 'http://127.0.0.1:8080/files/goods-white-tshirt.jpg', 'http://127.0.0.1:8080/files/goods-white-tshirt.jpg', 2, 39.90, 99.00, '白色宽松短袖 T 恤，胸前字母印花，日常百搭，轻微使用痕迹。', '河北省/秦皇岛市/北戴河区', '包邮', 9, 25, '上架', '八成新', '2026-06-07');
INSERT INTO `goods` VALUES (104, '真维斯牛仔短裤', 'http://127.0.0.1:8080/files/goods-denim-shorts.jpg', 'http://127.0.0.1:8080/files/goods-denim-shorts.jpg', 2, 59.90, 159.00, '真维斯牛仔短裤，破洞流苏设计，适合夏季穿搭，成色良好。', '上海', '包邮', 9, 41, '上架', '九成新', '2026-06-07');
INSERT INTO `goods` VALUES (106, '测试', 'http://127.0.0.1:8080/files/c15c50d5933e417ca3b016712924e4fc.jpg', 'http://127.0.0.1:8080/files/411026cbd4bd459dbee2e62771bb5b33.jpg', 1, 12.00, 15.00, '<p>123456</p>', '北京市/市辖区/东城区', '包邮', 17, 0, '上架', '全新', '2026-06-08');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '二手交易平台安全提醒1', '近期我们发现部分商品存在诈骗风险，请注意个人财产安全！', '2026-06-06 16:53:42');
INSERT INTO `notice` VALUES (2, '二手交易平台升级公告', '为了用户体验我们将升级该平台！', '2026-06-06 17:02:59');

-- ----------------------------
-- Table structure for sys_admin
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_admin
-- ----------------------------
INSERT INTO `sys_admin` VALUES (8, '222', NULL, '$2a$10$yeGfHzdqYo5rT60RldZThuk/N1h7LwORy/19J51ub81jVevvH95AW', 'http://127.0.0.1:8080/files/44e1b6f9762243469580f16e480242ad.jpg');
INSERT INTO `sys_admin` VALUES (9, 'admin', NULL, '$2a$10$nlMLKTlHU1CANNcY6KpKn.QDW4rSSRyIv4OTRucvTlEvKISkxGqs6', NULL);
INSERT INTO `sys_admin` VALUES (10, 'xiaoxiao', NULL, '$2a$10$iO365nUfqrZzF/r.05QJ/eUML7Nhfi.mn9Z9pV3e58hr/rB4mBJ0K', NULL);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (9, 'lisi', '李四', '$2a$10$m/RYPeE7fTn4ZcgZ6RmwxO9g01HIIIqfc4GVs8EESyTlTXSiwe6gW', 'http://127.0.0.1:8080/files/d66ea0b5685d4c8387aba50e0791caab.jpg');
INSERT INTO `sys_user` VALUES (12, 'zhangsan', '张三', '$2a$10$m/RYPeE7fTn4ZcgZ6RmwxO9g01HIIIqfc4GVs8EESyTlTXSiwe6gW', 'http://127.0.0.1:8080/files/44e1b6f9762243469580f16e480242ad.jpg');
INSERT INTO `sys_user` VALUES (14, 'wangwu', '王五', '$2a$10$m/RYPeE7fTn4ZcgZ6RmwxO9g01HIIIqfc4GVs8EESyTlTXSiwe6gW', 'http://127.0.0.1:8080/files/07bbe4b58a684fdf9fa14cb313d89bb9.jpg');
INSERT INTO `sys_user` VALUES (15, 'zhaoliu', '赵六', '$2a$10$m/RYPeE7fTn4ZcgZ6RmwxO9g01HIIIqfc4GVs8EESyTlTXSiwe6gW', 'http://127.0.0.1:8080/files/77f0c22b43eb4f3790dbc76215759a88.jpg');
INSERT INTO `sys_user` VALUES (16, 'xiaomei', '小美', '$2a$10$m/RYPeE7fTn4ZcgZ6RmwxO9g01HIIIqfc4GVs8EESyTlTXSiwe6gW', 'http://127.0.0.1:8080/files/18ecf61aae4147958a6bc264da45aaf1.jpg');
INSERT INTO `sys_user` VALUES (17, 'xiaoming', NULL, '$2a$10$YY5bziypqvptO.aOWlyFyOQZSXZLX/GzH.FJzd24pRcEkhp6sjCUG', NULL);

-- ----------------------------
-- Table structure for type
-- ----------------------------
DROP TABLE IF EXISTS `type`;
CREATE TABLE `type`  (
  `id` int NOT NULL COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '名称',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '简介',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片',
  `status` tinyint NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of type
-- ----------------------------
INSERT INTO `type` VALUES (1, '手机/数码/', '热门装备放心入', 'http://127.0.0.1:8080/files/aaaaa20260606214253.jpg', 'http://127.0.0.1:8080/files/07bbe4b58a684fdf9fa14cb313d89bb9.jpg', 1);
INSERT INTO `type` VALUES (2, '服饰/箱包/', '时尚产品低价淘', 'http://127.0.0.1:8080/files/aaaab20260606214253.jpg', 'http://127.0.0.1:8080/files/8a283f9aa30740ae9ff990fd464a00b8.jpg', 1);
INSERT INTO `type` VALUES (3, '技能/卡券/', '吃喝玩乐放心购', 'http://127.0.0.1:8080/files/aaaac20260606214253.jpg', 'http://127.0.0.1:8080/files/77f0c22b43eb4f3790dbc76215759a88.jpg', 0);
INSERT INTO `type` VALUES (4, '母婴/美妆/', '呵护健康美丽随', 'http://127.0.0.1:8080/files/aaaad20260606214253.jpg', 'http://127.0.0.1:8080/files/aaaaa.png', 0);
INSERT INTO `type` VALUES (5, '家具/家电/', '品质生活尽在此', 'http://127.0.0.1:8080/files/aaaae20260606214253.jpg', 'http://127.0.0.1:8080/files/07bbe4b58a684fdf9fa14cb313d89bb9.jpg', 0);
INSERT INTO `type` VALUES (6, '文玩/珠宝/', '珍贵收藏佳品遇', 'http://127.0.0.1:8080/files/aaaaf20260606214253.jpg', 'http://127.0.0.1:8080/files/8a283f9aa30740ae9ff990fd464a00b8.jpg', 0);
INSERT INTO `type` VALUES (7, '食品/宠物/', '美味宠爱花相伴', 'http://127.0.0.1:8080/files/aaaag20260606214253.jpg', 'http://127.0.0.1:8080/files/aaaaa.png', 0);
INSERT INTO `type` VALUES (8, '图书/游戏/', '阅读娱乐一站购', 'http://127.0.0.1:8080/files/aaaah20260606214253.jpg', 'http://127.0.0.1:8080/files/b361e373bfca4e609cdaa4e4ebc869dd.png', 0);
INSERT INTO `type` VALUES (9, '汽车/电动车', '轻松出行全面购', 'http://127.0.0.1:8080/files/aaaai20260606214253.jpg', 'http://127.0.0.1:8080/files/eec360b4f9c47c0bc9eed72f81c6c0b.png', 1);
INSERT INTO `type` VALUES (10, '五金/设备/', '品质工具安心选', 'http://127.0.0.1:8080/files/aaaaj20260606214253.jpg', 'http://127.0.0.1:8080/files/65318436b3cd4bd2a469735ae7b3f122.png', 1);

SET FOREIGN_KEY_CHECKS = 1;




