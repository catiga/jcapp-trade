/*
 Navicat MySQL Data Transfer

 Source Server         : 10.3.66.160
 Source Server Type    : MySQL
 Source Server Version : 50643
 Source Host           : 10.3.66.160:3306
 Source Schema         : hp_trade

 Target Server Type    : MySQL
 Target Server Version : 50643
 File Encoding         : 65001

 Date: 28/04/2019 22:18:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cash_collect_setting
-- ----------------------------
DROP TABLE IF EXISTS `cash_collect_setting`;
CREATE TABLE `cash_collect_setting`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NULL DEFAULT NULL,
  `vv` decimal(10, 2) NULL DEFAULT NULL,
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `tip` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `cscode` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1000',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for cashier_counter_ocs
-- ----------------------------
DROP TABLE IF EXISTS `cashier_counter_ocs`;
CREATE TABLE `cashier_counter_ocs`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `cc_id` bigint(20) NULL DEFAULT NULL,
  `oc` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of cashier_counter_ocs
-- ----------------------------
INSERT INTO `cashier_counter_ocs` VALUES (6, 2, '2000');
INSERT INTO `cashier_counter_ocs` VALUES (8, 3, '2000');
INSERT INTO `cashier_counter_ocs` VALUES (10, 4, '2000');
INSERT INTO `cashier_counter_ocs` VALUES (11, 1, '2000');

-- ----------------------------
-- Table structure for cashier_doer_log
-- ----------------------------
DROP TABLE IF EXISTS `cashier_doer_log`;
CREATE TABLE `cashier_doer_log`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `ccid` bigint(20) NULL DEFAULT NULL,
  `ccsn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ccname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pid` bigint(20) NULL DEFAULT NULL,
  `do_ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `start_time` timestamp(0) NULL DEFAULT NULL,
  `end_time` timestamp(0) NULL DEFAULT NULL,
  `utoken` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `uname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ss` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '00',
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `uid` bigint(11) NULL DEFAULT NULL,
  `settle` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 114 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for counter_conf
-- ----------------------------
DROP TABLE IF EXISTS `counter_conf`;
CREATE TABLE `counter_conf`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `cc_id` bigint(20) NULL DEFAULT NULL,
  `cft` char(3) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `info` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `param` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NULL DEFAULT NULL,
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `html` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of counter_conf
-- ----------------------------
INSERT INTO `counter_conf` VALUES (1, 1, '100', 'EPTM86', 'EPTM86', '', '{\"oc\":[{\"oc\":\"2000\",\"name\":\"票务订单\"}]}', '2019-04-25 18:38:28', '2019-04-25 18:38:28', 0, '<div style=\"position:relative; z-index:100; width:350px; height:380px;\">\r\n  <font id=\"p_ycmc\" style=\"font-size: 13px;position:absolute; z-index:200; top:0px; left:80px;\">[[store_name]]</font>\r\n  <font id=\"p_yingtingmingcheng\" style=\"font-size: 13px;position:absolute; z-index:200; top:20px; left:25px;\">[[hall_name]]</font>\r\n  <font id=\"p_shijian\" style=\"font-size: 13px;position:absolute; z-index:200; top:20px;; left:130px;\">[[plan_date]]</font>\r\n  <font id=\"p_yingpianmingcheng\" style=\"font-size: 13px;position:absolute; z-index:200; top:40px; left:25px\">[[film_name]]</font>\r\n  <font id=\"p_ttype\" style=\"font-size: 13px;position:absolute; z-index:200; top:80px; left:25px;\">现场票</font>\r\n  <font id=\"p_stime\" style=\"font-size: 13px;position:absolute; z-index:200; top:100px; left:45px;\">[[plan_time]]</font>\r\n  <font id=\"p_price\" style=\"font-size: 13px;position:absolute; z-index:200; top:60px; left:130px;\">60元</font>\r\n  <font id=\"p_o_price\" style=\"font-size: 13px;position:absolute; z-index:200; top:73px; left:130px;\">服务费0元</font>\r\n  <font id=\"p_seller\" style=\"font-size: 13px;position:absolute; z-index:200; top:100px; left:130px;\">006</font>\r\n  \r\n  <font id=\"pf_ticn1\" style=\"font-size: 13px;position:absolute; z-index:200; top:185px; left:215px;\"></font>\r\n  <font id=\"pf_ticn2\" style=\"font-size: 13px;position:absolute; z-index:200; top:198px; left:215px;\"></font>\r\n  \r\n  <font id=\"pf_piaojia\" style=\"font-size: 13px;position:absolute; z-index:200; top:245px; left:215px;\"></font>\r\n  <font id=\"pf_tic_no\" style=\"font-size: 13px;position:absolute; z-index:200; top:228px; left:50px;\">[[order_num]]</font>\r\n  \r\n  <font id=\"tip\" style=\"font-size: 13px;position:absolute; z-index:200; top:255px; left:50px;\"></font>\r\n </div>');
INSERT INTO `counter_conf` VALUES (2, 2, '100', 'EPTM86', 'EPTM86', '', '{\"oc\":[{\"oc\":\"2000\",\"name\":\"票务订单\"}]}', '2019-04-25 18:38:49', '2019-04-25 18:38:49', 0, '<div style=\"position:relative; z-index:100; width:350px; height:380px;\">\r\n  <font id=\"p_ycmc\" style=\"font-size: 13px;position:absolute; z-index:200; top:0px; left:80px;\">[[store_name]]</font>\r\n  <font id=\"p_yingtingmingcheng\" style=\"font-size: 13px;position:absolute; z-index:200; top:20px; left:25px;\">[[hall_name]]</font>\r\n  <font id=\"p_shijian\" style=\"font-size: 13px;position:absolute; z-index:200; top:20px;; left:130px;\">[[plan_date]]</font>\r\n  <font id=\"p_yingpianmingcheng\" style=\"font-size: 13px;position:absolute; z-index:200; top:40px; left:25px\">[[film_name]]</font>\r\n  <font id=\"p_ttype\" style=\"font-size: 13px;position:absolute; z-index:200; top:80px; left:25px;\">现场票</font>\r\n  <font id=\"p_stime\" style=\"font-size: 13px;position:absolute; z-index:200; top:100px; left:45px;\">[[plan_time]]</font>\r\n  <font id=\"p_price\" style=\"font-size: 13px;position:absolute; z-index:200; top:60px; left:130px;\">60元</font>\r\n  <font id=\"p_o_price\" style=\"font-size: 13px;position:absolute; z-index:200; top:73px; left:130px;\">服务费0元</font>\r\n  <font id=\"p_seller\" style=\"font-size: 13px;position:absolute; z-index:200; top:100px; left:130px;\">006</font>\r\n  \r\n  <font id=\"pf_ticn1\" style=\"font-size: 13px;position:absolute; z-index:200; top:185px; left:215px;\"></font>\r\n  <font id=\"pf_ticn2\" style=\"font-size: 13px;position:absolute; z-index:200; top:198px; left:215px;\"></font>\r\n  \r\n  <font id=\"pf_piaojia\" style=\"font-size: 13px;position:absolute; z-index:200; top:245px; left:215px;\"></font>\r\n  <font id=\"pf_tic_no\" style=\"font-size: 13px;position:absolute; z-index:200; top:228px; left:50px;\">[[order_num]]</font>\r\n  \r\n  <font id=\"tip\" style=\"font-size: 13px;position:absolute; z-index:200; top:255px; left:50px;\"></font>\r\n </div>');
INSERT INTO `counter_conf` VALUES (3, 3, '100', 'EPTM86', 'EPTM86', '', '{\"oc\":[{\"oc\":\"2000\",\"name\":\"票务订单\"}]}', '2019-04-25 18:39:00', '2019-04-25 18:39:00', 0, '<div style=\"position:relative; z-index:100; width:350px; height:380px;\">\r\n  <font id=\"p_ycmc\" style=\"font-size: 13px;position:absolute; z-index:200; top:0px; left:80px;\">[[store_name]]</font>\r\n  <font id=\"p_yingtingmingcheng\" style=\"font-size: 13px;position:absolute; z-index:200; top:20px; left:25px;\">[[hall_name]]</font>\r\n  <font id=\"p_shijian\" style=\"font-size: 13px;position:absolute; z-index:200; top:20px;; left:130px;\">[[plan_date]]</font>\r\n  <font id=\"p_yingpianmingcheng\" style=\"font-size: 13px;position:absolute; z-index:200; top:40px; left:25px\">[[film_name]]</font>\r\n  <font id=\"p_ttype\" style=\"font-size: 13px;position:absolute; z-index:200; top:80px; left:25px;\">现场票</font>\r\n  <font id=\"p_stime\" style=\"font-size: 13px;position:absolute; z-index:200; top:100px; left:45px;\">[[plan_time]]</font>\r\n  <font id=\"p_price\" style=\"font-size: 13px;position:absolute; z-index:200; top:60px; left:130px;\">60元</font>\r\n  <font id=\"p_o_price\" style=\"font-size: 13px;position:absolute; z-index:200; top:73px; left:130px;\">服务费0元</font>\r\n  <font id=\"p_seller\" style=\"font-size: 13px;position:absolute; z-index:200; top:100px; left:130px;\">006</font>\r\n  \r\n  <font id=\"pf_ticn1\" style=\"font-size: 13px;position:absolute; z-index:200; top:185px; left:215px;\"></font>\r\n  <font id=\"pf_ticn2\" style=\"font-size: 13px;position:absolute; z-index:200; top:198px; left:215px;\"></font>\r\n  \r\n  <font id=\"pf_piaojia\" style=\"font-size: 13px;position:absolute; z-index:200; top:245px; left:215px;\"></font>\r\n  <font id=\"pf_tic_no\" style=\"font-size: 13px;position:absolute; z-index:200; top:228px; left:50px;\">[[order_num]]</font>\r\n  \r\n  <font id=\"tip\" style=\"font-size: 13px;position:absolute; z-index:200; top:255px; left:50px;\"></font>\r\n </div>');
INSERT INTO `counter_conf` VALUES (4, 4, '100', 'EPTM86', 'EPTM86', '', '{\"oc\":[{\"oc\":\"2000\",\"name\":\"票务订单\"}]}', '2019-04-25 18:39:08', '2019-04-25 18:39:08', 0, '<div style=\"position:relative; z-index:100; width:350px; height:380px;\">\r\n  <font id=\"p_ycmc\" style=\"font-size: 13px;position:absolute; z-index:200; top:0px; left:80px;\">[[store_name]]</font>\r\n  <font id=\"p_yingtingmingcheng\" style=\"font-size: 13px;position:absolute; z-index:200; top:20px; left:25px;\">[[hall_name]]</font>\r\n  <font id=\"p_shijian\" style=\"font-size: 13px;position:absolute; z-index:200; top:20px;; left:130px;\">[[plan_date]]</font>\r\n  <font id=\"p_yingpianmingcheng\" style=\"font-size: 13px;position:absolute; z-index:200; top:40px; left:25px\">[[film_name]]</font>\r\n  <font id=\"p_ttype\" style=\"font-size: 13px;position:absolute; z-index:200; top:80px; left:25px;\">现场票</font>\r\n  <font id=\"p_stime\" style=\"font-size: 13px;position:absolute; z-index:200; top:100px; left:45px;\">[[plan_time]]</font>\r\n  <font id=\"p_price\" style=\"font-size: 13px;position:absolute; z-index:200; top:60px; left:130px;\">60元</font>\r\n  <font id=\"p_o_price\" style=\"font-size: 13px;position:absolute; z-index:200; top:73px; left:130px;\">服务费0元</font>\r\n  <font id=\"p_seller\" style=\"font-size: 13px;position:absolute; z-index:200; top:100px; left:130px;\">006</font>\r\n  \r\n  <font id=\"pf_ticn1\" style=\"font-size: 13px;position:absolute; z-index:200; top:185px; left:215px;\"></font>\r\n  <font id=\"pf_ticn2\" style=\"font-size: 13px;position:absolute; z-index:200; top:198px; left:215px;\"></font>\r\n  \r\n  <font id=\"pf_piaojia\" style=\"font-size: 13px;position:absolute; z-index:200; top:245px; left:215px;\"></font>\r\n  <font id=\"pf_tic_no\" style=\"font-size: 13px;position:absolute; z-index:200; top:228px; left:50px;\">[[order_num]]</font>\r\n  \r\n  <font id=\"tip\" style=\"font-size: 13px;position:absolute; z-index:200; top:255px; left:50px;\"></font>\r\n </div>');

-- ----------------------------
-- Table structure for sp_invoice_info
-- ----------------------------
DROP TABLE IF EXISTS `sp_invoice_info`;
CREATE TABLE `sp_invoice_info`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `pid` bigint(20) NULL DEFAULT NULL,
  `unit_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `unit_tax` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收款人税号',
  `unit_account` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收款人账号',
  `unit_addr` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `unit_bank` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `unit_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `tax_fee` decimal(10, 2) NULL DEFAULT NULL,
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for supp_conf_ts
-- ----------------------------
DROP TABLE IF EXISTS `supp_conf_ts`;
CREATE TABLE `supp_conf_ts`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `tyc` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `code_cat` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `rb` tinyint(4) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of supp_conf_ts
-- ----------------------------
INSERT INTO `supp_conf_ts` VALUES (1, '1000', '001001', '001', '现金支付', '2019-04-24 11:13:11', '2019-04-24 11:13:11', 0, 1);
INSERT INTO `supp_conf_ts` VALUES (2, '1000', '201001', '201', '微信支付被扫', '2019-04-24 11:13:14', '2019-04-24 11:13:14', 0, 1);
INSERT INTO `supp_conf_ts` VALUES (3, '1000', '202001', '202', '支付宝被扫', '2019-04-24 11:13:17', '2019-04-24 11:13:17', 0, 1);
INSERT INTO `supp_conf_ts` VALUES (4, '1000', '301001', '301', '现在支付POS', '2019-04-24 11:13:18', '2019-04-24 11:13:18', 0, 1);
INSERT INTO `supp_conf_ts` VALUES (5, '1000', '202002', '202', '支付宝主扫', '2019-04-24 17:54:39', '2019-04-24 17:54:43', 0, 1);
INSERT INTO `supp_conf_ts` VALUES (6, '1000', '90009000weixin', '900', '微信支付记账', '2019-04-25 20:07:29', '2019-04-27 14:57:00', 0, 1);

-- ----------------------------
-- Table structure for trade_cashier_counter
-- ----------------------------
DROP TABLE IF EXISTS `trade_cashier_counter`;
CREATE TABLE `trade_cashier_counter`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `sn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `info` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pid` bigint(20) NULL DEFAULT NULL,
  `sid` bigint(20) NULL DEFAULT NULL,
  `sname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `inuse` tinyint(4) NOT NULL DEFAULT 0,
  `ctcode` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '1000',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of trade_cashier_counter
-- ----------------------------
INSERT INTO `trade_cashier_counter` VALUES (1, '1001', '封阳', '前台售票厅1', 1, 1, '花果山球幕', '2019-04-22 23:40:56', '2019-04-28 18:02:41', 0, 0, '1000');
INSERT INTO `trade_cashier_counter` VALUES (2, '1002', '周雪颖', '002', 1, 1, '猴王归来', '2019-04-24 18:11:30', '2019-04-28 16:40:26', 0, 0, '1000');
INSERT INTO `trade_cashier_counter` VALUES (3, '1003', '王艺桦', '', 1, 1, '猴王归来', '2019-04-25 14:10:46', '2019-04-28 18:02:22', 0, 1, '1000');
INSERT INTO `trade_cashier_counter` VALUES (4, '1004', '前台售票4', '', 1, 1, '猴王归来', '2019-04-25 14:18:28', '2019-04-28 13:48:59', 0, 1, '1000');

-- ----------------------------
-- Table structure for trade_info
-- ----------------------------
DROP TABLE IF EXISTS `trade_info`;
CREATE TABLE `trade_info`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `pid` bigint(20) NULL DEFAULT NULL,
  `tnum` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `tss` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `pay_time` datetime(0) NULL DEFAULT NULL,
  `pay_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `total_amount` decimal(16, 2) NULL DEFAULT NULL,
  `pay_amount` decimal(16, 2) NULL DEFAULT NULL,
  `tname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `tbody` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `storeid` bigint(20) NULL DEFAULT NULL,
  `storename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `buyerid` bigint(20) NULL DEFAULT NULL,
  `buyerphone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `buyername` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `handle_fee` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `service_fee` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `log_id` bigint(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `tnum`(`tnum`) USING BTREE,
  INDEX `tss`(`tss`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 146 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for trade_invoice
-- ----------------------------
DROP TABLE IF EXISTS `trade_invoice`;
CREATE TABLE `trade_invoice`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `t_id` bigint(20) NULL DEFAULT NULL,
  `invo_set_id` bigint(20) NULL DEFAULT NULL,
  `is_unit_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `is_unit_tax` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `is_tax_fee` decimal(10, 2) NULL DEFAULT NULL,
  `pay_unit_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_unit_tax` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_bank` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_account` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_addr` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `invoice_amount` decimal(12, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for trade_order
-- ----------------------------
DROP TABLE IF EXISTS `trade_order`;
CREATE TABLE `trade_order`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `t_id` bigint(20) NULL DEFAULT NULL,
  `order_id` bigint(20) NULL DEFAULT NULL,
  `order_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `order_data` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `oss` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `total_amount` decimal(12, 2) NULL DEFAULT NULL,
  `pay_amount` decimal(12, 2) NULL DEFAULT NULL,
  `tp_amount` decimal(12, 2) NULL DEFAULT NULL,
  `pid` bigint(20) NULL DEFAULT NULL,
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `oc` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `storeid` bigint(20) NULL DEFAULT NULL,
  `storename` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `buyerid` bigint(20) NULL DEFAULT NULL,
  `buyerphone` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `buyername` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ouid` bigint(20) NULL DEFAULT NULL,
  `ouname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `handle_fee` decimal(10, 2) NOT NULL DEFAULT 0.00,
  `service_fee` decimal(10, 2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_num`(`order_num`) USING BTREE,
  INDEX `t_id`(`t_id`) USING BTREE,
  INDEX `order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 146 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for trade_pay_info
-- ----------------------------
DROP TABLE IF EXISTS `trade_pay_info`;
CREATE TABLE `trade_pay_info`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `t_id` bigint(20) NULL DEFAULT NULL,
  `t_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `trans_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `trans_user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `trans_total_amount` decimal(10, 2) NULL DEFAULT NULL,
  `trans_pay_amount` decimal(10, 2) NULL DEFAULT NULL,
  `trans_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `bank_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `trans_get_account` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pay_type` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '10',
  `trans_cash_col` decimal(10, 2) NULL DEFAULT NULL,
  `log_id` bigint(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 124 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for trade_preferential
-- ----------------------------
DROP TABLE IF EXISTS `trade_preferential`;
CREATE TABLE `trade_preferential`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `t_id` bigint(20) NULL DEFAULT NULL,
  `t_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `order_id` bigint(20) NULL DEFAULT NULL,
  `order_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `oc` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pref_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pref_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pref_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `pref_type` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '10',
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `flag` tinyint(4) NOT NULL DEFAULT 0,
  `max_deduct` decimal(10, 2) NULL DEFAULT NULL,
  `real_deduct` decimal(10, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for trade_trigger_rog
-- ----------------------------
DROP TABLE IF EXISTS `trade_trigger_rog`;
CREATE TABLE `trade_trigger_rog`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `appcode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `callback` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `dataformat` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `flag` tinyint(4) NULL DEFAULT NULL,
  `a_time` datetime(0) NULL DEFAULT NULL,
  `c_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `rushed` bigint(20) NULL DEFAULT NULL,
  `order_type` char(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of trade_trigger_rog
-- ----------------------------
INSERT INTO `trade_trigger_rog` VALUES (1, 'ticketingsys', '/incall/order/notify', '79c2c627a27e2694560b9586b05d76088beecda5b42f5b454c7c84daac129f43', NULL, 0, '2019-04-22 23:42:32', '2019-04-22 23:42:32', 11750400, '2000');
INSERT INTO `trade_trigger_rog` VALUES (2, 'ticketingsys', '/incall/order/notify', 'c7517d0158108869ecc339be215e890afa3f62b92dae28b8aaa85ab76b6ece2e', NULL, 0, '2019-04-22 23:42:32', '2019-04-22 23:42:32', 9158400, '2010');
INSERT INTO `trade_trigger_rog` VALUES (3, 'crm', '/api/order/recharge', 'a05e8acee698d57ba91381b0b25fd45d826bcc9be04b047e20ca0446b9b4fd8d', NULL, 0, '2019-04-23 10:43:47', '2019-04-23 10:43:47', 15033600, '8001');
INSERT INTO `trade_trigger_rog` VALUES (4, 'crm', '/api/order/createOrder', '6fc214b24ea2ede774f3a552fd23bc10bcd0b04226dd726e648e39be7959dddc', NULL, 0, '2019-04-23 10:43:47', '2019-04-23 10:43:47', 17107200, '8000');

SET FOREIGN_KEY_CHECKS = 1;
