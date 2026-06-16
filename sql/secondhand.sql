-- MySQL dump 10.13  Distrib 9.7.0, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: secondhand
-- ------------------------------------------------------
-- Server version	9.7.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '2ad9a8b4-58e6-11f1-8258-d880836c35f6:1-593';

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人姓名',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '具体地址',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `user_id` int DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `address_sys_user_id_fk` (`user_id`),
  CONSTRAINT `address_sys_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` VALUES (15,'人','上海市/市辖区/黄浦区','207街','13000001111',20),(16,'某某某','山西省/太原市/小店区','318号街道','12345678901',21),(17,'某人','北京市/市辖区/朝阳区','318号街道','17299998888',21),(18,'人','广东省/广州市/天河区','石牌街道','13000000111',22);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banner` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '说明',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (1,'我们为您的每一笔交易保驾护航！','http://127.0.0.1:8080/files/057c6c353aa34293a6e0eff6088ad717.jpg'),(2,'每一件二手物品，都是独一无二的故事！','http://127.0.0.1:8080/files/5048f6465dbb426bb9adac8aabdf448f.jpg'),(3,'让闲置物品重获新生，环保又省钱！','http://127.0.0.1:8080/files/136fad831c364030bd419ac56d99c2da.jpg'),(4,'轻松转手，闲置变现超简单！','http://127.0.0.1:8080/files/c9a6267fabd9468b88c2bfe11879b226.jpg');
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat`
--

DROP TABLE IF EXISTS `chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文本内容',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文本类型',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建时间',
  `from_user_id` int DEFAULT NULL COMMENT '来自用户id',
  `to_user_id` int DEFAULT NULL COMMENT '发往用户id',
  `is_read` tinyint DEFAULT NULL COMMENT '是否已读',
  PRIMARY KEY (`id`),
  KEY `chat_sys_user_id_fk` (`from_user_id`),
  KEY `chat_sys_user_id_fk_2` (`to_user_id`),
  CONSTRAINT `chat_sys_user_id_fk` FOREIGN KEY (`from_user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chat_sys_user_id_fk_2` FOREIGN KEY (`to_user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天管理';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat`
--

LOCK TABLES `chat` WRITE;
/*!40000 ALTER TABLE `chat` DISABLE KEYS */;
INSERT INTO `chat` VALUES (1,'hahahaha','text','2026-06-15 19:22:35',21,20,1),(2,'请问这个东西还在吗','text','2026-06-15 19:47:25',20,21,1),(3,'在的','text','2026-06-15 19:47:34',21,20,1),(4,'可以小刀吗','text','2026-06-15 19:47:42',20,21,1),(5,'刀多少','text','2026-06-15 19:47:48',21,20,1),(6,'10块可以吗','text','2026-06-15 19:48:01',20,21,1),(7,'行','text','2026-06-15 19:48:06',21,20,1),(8,'我改好价了','text','2026-06-15 19:48:17',21,20,1),(9,'你拍下','text','2026-06-15 19:48:20',21,20,1),(10,'好','text','2026-06-15 19:48:23',20,21,1),(11,'你好，商品还在吗','text','2026-06-15 20:48:38',22,21,1),(12,'在的','text','2026-06-15 21:36:18',21,22,1),(13,'偏远地区包邮吗？','text','2026-06-15 21:44:36',22,21,1),(16,'这是什么？','text','2026-06-15 22:23:34',23,21,1),(17,'字面意思','text','2026-06-15 22:25:29',21,23,1),(19,'有购买证明吗','text','2026-06-15 22:56:58',22,21,1),(20,'有','text','2026-06-15 22:57:10',21,22,1),(21,'发来看一下','text','2026-06-15 22:57:22',22,21,1),(22,'包邮','text','2026-06-15 23:03:16',21,22,1),(36,'你好','text','2026-06-16 15:29:34',23,21,1),(37,'东西还在吗','text','2026-06-16 15:29:44',23,21,1),(38,'东西还在吗','text','2026-06-16 15:32:05',22,23,1),(39,'在的','text','2026-06-16 15:32:28',23,22,1),(40,'接受小刀吗','text','2026-06-16 15:32:35',22,23,1),(41,'刀多少','text','2026-06-16 15:32:45',23,22,1),(42,'50包邮可以吗','text','2026-06-16 15:32:56',22,23,1),(43,'行，我改价格你拍下','text','2026-06-16 15:33:10',23,22,1),(44,'好','text','2026-06-16 15:33:15',22,23,1);
/*!40000 ALTER TABLE `chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `collect`
--

DROP TABLE IF EXISTS `collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `collect` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int DEFAULT NULL COMMENT '用户id',
  `item_id` int DEFAULT NULL COMMENT '收藏id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_id` (`user_id`,`item_id`) USING BTREE,
  KEY `collect_goods_id_fk` (`item_id`),
  CONSTRAINT `collect_goods_id_fk` FOREIGN KEY (`item_id`) REFERENCES `goods` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `collect_sys_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `collect`
--

LOCK TABLES `collect` WRITE;
/*!40000 ALTER TABLE `collect` DISABLE KEYS */;
INSERT INTO `collect` VALUES (106,20,112);
/*!40000 ALTER TABLE `collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `goods`
--

DROP TABLE IF EXISTS `goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `goods` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品名称',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品图',
  `img_list` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品多图',
  `type_id` int DEFAULT NULL COMMENT '分类id',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `re_price` decimal(10,2) DEFAULT NULL COMMENT '原来价格',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '商品详情信息',
  `place` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地点',
  `shipment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发货设置',
  `user_id` int DEFAULT NULL COMMENT '发布用户id',
  `num` int DEFAULT NULL COMMENT '浏览量',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品状态',
  `quality` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品成色',
  `date` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发布日期',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `goods_type_id_fk` (`type_id`),
  KEY `goods_sys_user_id_fk` (`user_id`),
  CONSTRAINT `goods_sys_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `goods_type_id_fk` FOREIGN KEY (`type_id`) REFERENCES `type` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `goods`
--

LOCK TABLES `goods` WRITE;
/*!40000 ALTER TABLE `goods` DISABLE KEYS */;
INSERT INTO `goods` VALUES (107,'速冻水饺','http://127.0.0.1:8080/files/36a29778271244a399da606c5c1866c3.jpg','',7,30.00,40.00,'<p><br></p>','广东省/广州市/荔湾区','包邮',20,10,'已售出','全新','2026-06-11'),(109,'吃一次亏','http://127.0.0.1:8080/files/733d5eca06eb4d8f84c64b64a6e11035.png','',3,20.00,0.00,'<p>20块钱吃一次亏</p>','天津市/市辖区/和平区','包邮',20,7,'已售出','九成新','2026-06-11'),(110,'4070ti 显卡 9成新','http://127.0.0.1:8080/files/7e067878273c4466888f4eeb3761f820.jpg','',1,4655.00,5300.00,'<p>4070ti 显卡 9成新 不包邮 拒小刀</p>','福建省/厦门市/海沧区','不包邮',20,16,'已售出','九成新','2026-06-11'),(111,'软件工程 二手书 正版','http://127.0.0.1:8080/files/8470b433d175459fa7829ee7a7ca77e5.jpg','',8,15.00,30.00,'<p>软件工程 二手书 正版 拒小刀</p>','广东省/广州市/天河区','包邮',20,35,'上架','八成新','2026-06-11'),(112,'赛博观鸟','http://127.0.0.1:8080/files/1b03f1e8ad504c29ab51bc2547675da4.png','',3,10.00,10.00,'<p>赛博观鸟，买了不退</p>','河北省/石家庄市/长安区','无需发货',21,30,'上架','全新','2026-06-15'),(113,'九号电动车','http://127.0.0.1:8080/files/0f889c247a894fdda1e2dd6718e25586.jpg','http://127.0.0.1:8080/files/545d7340e4c14e42a7c2c0b26f366892.jpg,http://127.0.0.1:8080/files/d714dba01bfc4ed0bd2b4d5bc3c5cb23.jpg',9,680.00,1200.00,'<p><span style=\"color: rgb(31, 31, 31); background-color: rgb(255, 255, 255);\">【直接拍】九号电动车 自用一个月二手电动车！ 有头盔充电器挡风被，功能完好.能跑70公里左右.全部电动车都是带电瓶的，原装正品，手续齐全，全国各地可以随时上牌 包装都在仅拆封，数量不多先到先得 直接拍就好，七天无理由退换，整车带电池发出，标价就是出售价！！！支持7天无理由退款退货，顺风快递送货上门！喜欢直接拍下付款48小时内发货，已开启自助下单</span></p>','北京市/市辖区/丰台区','不包邮',23,7,'上架','九成新','2026-06-15'),(114,'小偿领养！家养小体茶杯博美幼犬领养','http://127.0.0.1:8080/files/0ba4d4b0e01b47e898857a7840842253.jpg','',7,1.36,1.36,'<p><span style=\"color: rgb(31, 31, 31); background-color: rgb(255, 255, 255);\">小偿领养！标价就是卖价！因个人原因，没时间照顾，家养小体茶杯博美幼犬，两个多月<br>了，按时做了疫苗驱虫，健康活泼，身体特别好，胖乎乎的。狗狗找好主人</span></p>','黑龙江省/哈尔滨市/道里区','包邮',23,3,'上架','全新','2026-06-15'),(115,'i5-12400F散片 九成新','http://127.0.0.1:8080/files/07ead8fcd99d4f2b8172104f1f31a361.jpg','',1,560.00,1100.00,'<p>i5-12400F散片</p><p>九成新</p>','山西省/太原市/小店区','包邮',23,7,'已售出','九成新','2026-06-15'),(116,'金百达银爵DDR4内存条','http://127.0.0.1:8080/files/f89cef7461794fe5b1d813bdf5bb1e28.jpg','',10,111.00,199.00,'<p>个人自用出！</p><p>8GBx2 三星颗粒 3200mhz</p><p>KingBank</p><p>不接受到手刀</p><p>二手产品 不退不换</p><p>不包邮 不接受刀</p><p>有微小瑕疵但不影响使用</p>','广东省/广州市/天河区','包邮',23,5,'上架','九成新','2026-06-16');
/*!40000 ALTER TABLE `goods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
INSERT INTO `notice` VALUES (1,'二手交易平台安全提醒1','近期我们发现部分商品存在诈骗风险，请注意个人财产安全！','2026-06-06 16:53:42'),(2,'二手交易平台升级公告','为了用户体验我们将升级该平台！','2026-06-06 17:02:59'),(4,'二手交易平台欢迎你','淘好物、出闲置，省心又划算','2026-06-11 22:25:58');
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `no` varchar(255) DEFAULT NULL COMMENT '订单号',
  `item_id` int DEFAULT NULL COMMENT '商品id',
  `item_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `item_img` varchar(255) DEFAULT NULL COMMENT '商品图片',
  `from_id` int DEFAULT NULL COMMENT '卖家id',
  `to_id` int DEFAULT NULL COMMENT '买家id',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `time` varchar(255) DEFAULT NULL COMMENT '下单时间',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `to_rate` int DEFAULT NULL COMMENT '买家评分',
  `to_review` varchar(255) DEFAULT NULL COMMENT '买家评价',
  `address` varchar(255) DEFAULT NULL COMMENT '省市区',
  `info` varchar(255) DEFAULT NULL COMMENT '详细地址',
  `name` varchar(255) DEFAULT NULL COMMENT '收货人姓名',
  `phone` varchar(255) DEFAULT NULL COMMENT '收货人联系方式',
  PRIMARY KEY (`id`),
  UNIQUE KEY `orders_no_uindex` (`no`),
  KEY `orders_goods_id_fk` (`item_id`),
  CONSTRAINT `orders_goods_id_fk` FOREIGN KEY (`item_id`) REFERENCES `goods` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'20260611200936825995',107,'速冻水饺','http://127.0.0.1:8080/files/36a29778271244a399da606c5c1866c3.jpg',20,21,30.00,'2026-06-11 20:09:36','交易完成',5,'东西非常好','河北省/秦皇岛市/北戴河区','222','111','111'),(2,'20260611235724762328',110,'4070ti 显卡 9成新','http://127.0.0.1:8080/files/7e067878273c4466888f4eeb3761f820.jpg',20,21,4655.00,'2026-06-11 23:57:24','待支付',NULL,NULL,'山西省/太原市/小店区','318号街道','某某某','12345678901'),(3,'20260612211328834775',109,'吃一次亏','http://127.0.0.1:8080/files/733d5eca06eb4d8f84c64b64a6e11035.png',20,21,20.00,'2026-06-12 21:13:28','交易完成',5,'东西非常差','山西省/长治市/潞城区','1','1','1'),(4,'20260616000838806052',115,'i5-12400F散片 九成新','http://127.0.0.1:8080/files/07ead8fcd99d4f2b8172104f1f31a361.jpg',23,22,560.00,'2026-06-16 00:08:38','待支付',NULL,NULL,'广东省/广州市/天河区','石牌街道','人','13000000111'),(5,'20260616172154216630',112,'赛博观鸟','http://127.0.0.1:8080/files/1b03f1e8ad504c29ab51bc2547675da4.png',21,22,10.00,'2026-06-16 17:21:54','交易关闭',NULL,NULL,'广东省/广州市/天河区','石牌街道','人','13000000111');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_admin`
--

DROP TABLE IF EXISTS `sys_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_admin` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_admin`
--

LOCK TABLES `sys_admin` WRITE;
/*!40000 ALTER TABLE `sys_admin` DISABLE KEYS */;
INSERT INTO `sys_admin` VALUES (11,'zhangsan','张三','$2a$10$cOLdXxq3smsic3ZXIF3gVOlvkfVfPUJj5pa5iSetb7BqrC6ZgLGaq','http://127.0.0.1:8080/files/b6884a4d92aa4a648daac43c26574d5f.jpg'),(12,'gly','gly','$2a$10$J/kdqeZxdc/sBHn8JCEXWO0ymi224JZc4AVuNNrPGS92V07gDMMdW',NULL),(13,'adm','adm','$2a$10$wvLedrfTbFzMc8IZj01ooelSsiusfBEqIfCAPAzy3ouQIbpcyUZXm',NULL),(14,'stu2','student2','$2a$10$p6Ep1YCGVLAvg6QFY.HrfONvzdvG/zLPCaQKp6cIFh823R62jZ5la',NULL);
/*!40000 ALTER TABLE `sys_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (20,'wangwu','ww','$2a$10$1ds9N3Xo5AFos5mNNXsdButbOqroUQig.ORxwNS9BJV/4UEEtz1gO','http://127.0.0.1:8080/files/8367364326af45dd9da5b8cfd8448b0b.png'),(21,'yinw','xs','$2a$10$R8hP34nQbKU7D2/ZlBrqsONxESegZTehp8frAMNzW/gPVSFQl074e','http://127.0.0.1:8080/files/e2f769a5817c4d9b95887612fea788cb.jpg'),(22,'que','jd','$2a$10$bvbU7TXaxzL1toXd2eQGSu0PYK6vHs2zWWhbrWR20VXdoB1Pxxwda','http://127.0.0.1:8080/files/bbdfa2a60a02472ba3a2b36930951512.jpg'),(23,'spw','tyw','$2a$10$0Yk7FJ/lshQsL0pygU9Zf..2cFSFI55e0DaNUY0uw2lJREfbvkawu','http://127.0.0.1:8080/files/08be83e6f176493ba222c7651b80dcdf.png'),(24,'ren','ren','$2a$10$FLpmjb5ZA6dCWu4bPOs9keSJBBb7GIV13XB27bNABSU3Vsivs9Xl6',NULL),(25,'stu','stu','$2a$10$CjwoDnD6aDzQl6rowQ3oXegHXR0llV2zCHFVs1y0v7fsDaCI7Dfgy',NULL),(26,'stu1','student1','$2a$10$qa2Lh55dmRHpImf8kEK.u.h6n3o9mwRTHWbpTz3oQa8jrKDbEf0li',NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `type`
--

DROP TABLE IF EXISTS `type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
  `info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '简介',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片',
  `status` tinyint DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `type`
--

LOCK TABLES `type` WRITE;
/*!40000 ALTER TABLE `type` DISABLE KEYS */;
INSERT INTO `type` VALUES (1,'手机/数码','热门装备放心入','http://127.0.0.1:8080/files/aaaaa20260606214253.jpg','http://127.0.0.1:8080/files/ca303a14179c49caacb08b8bf06a9bcd.jpg',1),(2,'服饰/箱包','时尚产品低价淘','http://127.0.0.1:8080/files/aaaab20260606214253.jpg','http://127.0.0.1:8080/files/f06b4220c84d40279925e40ae423f1b2.jpg',0),(3,'技能/卡券','吃喝玩乐放心购','http://127.0.0.1:8080/files/aaaac20260606214253.jpg','http://127.0.0.1:8080/files/beba7f3ed8484082b448cbedfe4aa50a.jpg',0),(4,'母婴/美容','呵护健康美丽随','http://127.0.0.1:8080/files/aaaad20260606214253.jpg','http://127.0.0.1:8080/files/004f51024f584b1999143d0c3c39cc7a.jpg',0),(5,'家具/家电','品质生活尽在此','http://127.0.0.1:8080/files/aaaae20260606214253.jpg','http://127.0.0.1:8080/files/d54505fcffb742c9af983d2f062ce5ed.jpg',0),(6,'文玩/珠宝','珍贵收藏佳品遇','http://127.0.0.1:8080/files/aaaaf20260606214253.jpg','http://127.0.0.1:8080/files/235823875e964acb822eed017772015d.jpg',0),(7,'食品/宠物','美味宠爱花相伴','http://127.0.0.1:8080/files/aaaag20260606214253.jpg','http://127.0.0.1:8080/files/895d1f4a81984c65b2b50d7a1fb562ab.jpg',1),(8,'图书/游戏','阅读娱乐一站购','http://127.0.0.1:8080/files/aaaah20260606214253.jpg','http://127.0.0.1:8080/files/108bcc5ea84646b19424ccf6455fceb1.jpg',1),(9,'汽车/电动车','轻松出行全面购','http://127.0.0.1:8080/files/aaaai20260606214253.jpg','http://127.0.0.1:8080/files/4a83a16038714ab3baf29cf4533c0e8c.jpg',0),(10,'五金/设备','品质工具安心选','http://127.0.0.1:8080/files/aaaaj20260606214253.jpg','http://127.0.0.1:8080/files/46f51f1d0ecf4a1e9785cd82146a2661.jpg',1);
/*!40000 ALTER TABLE `type` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-16 20:28:38
