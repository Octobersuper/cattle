/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : cattle

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2020-04-10 17:13:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `img` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES ('1', '/banner/7a2ddb9e-652e-4400-9898-5a666fc3ca89.jpg');

-- ----------------------------
-- Table structure for carry
-- ----------------------------
DROP TABLE IF EXISTS `carry`;
CREATE TABLE `carry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '转账记录表',
  `userid` bigint(20) DEFAULT NULL,
  `touser` bigint(20) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `money` double(11,1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of carry
-- ----------------------------
INSERT INTO `carry` VALUES ('1', '1', '1', '2020-04-16 10:32:30', '10.0');
INSERT INTO `carry` VALUES ('2', '1', '2', '2020-04-29 10:35:34', '20.0');

-- ----------------------------
-- Table structure for diamond_shop
-- ----------------------------
DROP TABLE IF EXISTS `diamond_shop`;
CREATE TABLE `diamond_shop` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `state` int(11) DEFAULT '1' COMMENT '0 禁用 1启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of diamond_shop
-- ----------------------------
INSERT INTO `diamond_shop` VALUES ('1', '支付宝', '1');
INSERT INTO `diamond_shop` VALUES ('2', '微信', '1');

-- ----------------------------
-- Table structure for game_backtable
-- ----------------------------
DROP TABLE IF EXISTS `game_backtable`;
CREATE TABLE `game_backtable` (
  `backuserid` bigint(11) NOT NULL AUTO_INCREMENT,
  `backname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `account` varchar(255) DEFAULT NULL COMMENT '账号',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `role` int(11) DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`backuserid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of game_backtable
-- ----------------------------
INSERT INTO `game_backtable` VALUES ('1', 'admin', '123123', '123123', '962464');

-- ----------------------------
-- Table structure for game_diamond_shop
-- ----------------------------
DROP TABLE IF EXISTS `game_diamond_shop`;
CREATE TABLE `game_diamond_shop` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `money` double(11,0) DEFAULT NULL COMMENT '金额',
  `d_id` bigint(20) DEFAULT NULL COMMENT '商品id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of game_diamond_shop
-- ----------------------------
INSERT INTO `game_diamond_shop` VALUES ('1', '10金币', '10', '1');
INSERT INTO `game_diamond_shop` VALUES ('2', '100金币', '100', '1');
INSERT INTO `game_diamond_shop` VALUES ('3', '200金币', '200', '1');
INSERT INTO `game_diamond_shop` VALUES ('4', '300金币', '300', '1');
INSERT INTO `game_diamond_shop` VALUES ('5', '10金币', '10', '2');
INSERT INTO `game_diamond_shop` VALUES ('6', '100金币', '100', '2');
INSERT INTO `game_diamond_shop` VALUES ('7', '200金币', '200', '2');
INSERT INTO `game_diamond_shop` VALUES ('8', '300金币', '300', '2');

-- ----------------------------
-- Table structure for game_introduce
-- ----------------------------
DROP TABLE IF EXISTS `game_introduce`;
CREATE TABLE `game_introduce` (
  `introduceid` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '玩法介绍表',
  `value` text COMMENT '详细介绍',
  PRIMARY KEY (`introduceid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of game_introduce
-- ----------------------------
INSERT INTO `game_introduce` VALUES ('1', '基础分为1分\r\n特殊牌型，可以不在同一道中\r\n至尊一条龙：基础分✖26，同花色的牌从2-A 的13张牌\r\n一条龙：基础分✖13，不同花色的从2-A的13张牌\r\n普通牌型，必须在同一道中\r\n五同：尾道基础分*10，中道基础分*20，五张相同的牌，如55555、KKKKK等\r\n同花顺：尾道基础分*5，中道基础分*10（红波浪模式中，同花顺遇三道通赢则尾道基础分*4，中道基础分*10）同样花色的并且点数连续的五张牌，如梅花23456、红包56789、A2345为第二大的同花顺、10JQKA为最大顺子牌型。\r\n铁支：尾道基础分*4，中道基础分*8，四张相同的牌，如2222 JJJJ BBBB等，\r\n葫芦：中道基础分*2，三条带一对牌型，如33322、77788，66699等，\r\n同花：同样花色但不连续的五张牌，如梅花289JQ KJ963，多一色模式中，同花两对》同花对子》同花\r\n顺子：花色不同但是点数连续的 五张牌，如23456 10JQK ，A2345为第二大牌型，10JQKA为最大牌型\r\n三条：头道基础分*3，三张通点数牌型，如KKK53 999QA等\r\n两对：有两个对子牌型，如22AA5，33556等\r\n对子：两张点数相同的牌型，如22367，336QK等\r\n乌龙：不满足以上任何条件的散牌。');

-- ----------------------------
-- Table structure for game_notice
-- ----------------------------
DROP TABLE IF EXISTS `game_notice`;
CREATE TABLE `game_notice` (
  `noticeid` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '公告表',
  `value` varchar(255) DEFAULT NULL COMMENT '公告内容',
  PRIMARY KEY (`noticeid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of game_notice
-- ----------------------------
INSERT INTO `game_notice` VALUES ('1', '今日充值_充100送200!！1');

-- ----------------------------
-- Table structure for game_recharge
-- ----------------------------
DROP TABLE IF EXISTS `game_recharge`;
CREATE TABLE `game_recharge` (
  `rechargeid` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '充值提现记录表',
  `userid` bigint(11) DEFAULT NULL COMMENT '玩家id',
  `money` double(10,2) DEFAULT NULL COMMENT '充值金额',
  `createtime` datetime DEFAULT NULL COMMENT '操作时间',
  `type` int(11) DEFAULT NULL COMMENT '1充值记录  2提现记录  3支付宝提现  4银行卡提现',
  `state` int(11) DEFAULT '0' COMMENT '状态 0审核中 1已通过  2已拒绝',
  PRIMARY KEY (`rechargeid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of game_recharge
-- ----------------------------
INSERT INTO `game_recharge` VALUES ('81', '202089', '100.00', '2019-05-21 00:00:00', '3', '0');
INSERT INTO `game_recharge` VALUES ('82', '202089', '10.00', '2019-05-21 00:00:00', '3', '2');
INSERT INTO `game_recharge` VALUES ('83', '202089', '100.00', '2019-05-21 00:00:00', '3', '0');
INSERT INTO `game_recharge` VALUES ('84', '202089', '10.00', '2019-05-21 00:00:00', '3', '0');
INSERT INTO `game_recharge` VALUES ('85', '202089', '10.00', '2019-05-21 00:00:00', '3', '0');
INSERT INTO `game_recharge` VALUES ('86', '202089', '10.00', '2019-05-21 00:00:00', '3', '0');
INSERT INTO `game_recharge` VALUES ('87', '202089', '50.00', '2019-05-21 00:00:00', '4', '0');
INSERT INTO `game_recharge` VALUES ('88', '202089', '10.00', '2019-05-21 00:00:00', '4', '0');
INSERT INTO `game_recharge` VALUES ('89', '202089', '10.00', '2019-05-20 00:00:00', '4', '0');
INSERT INTO `game_recharge` VALUES ('90', '202089', '200.00', '2019-05-22 00:00:00', '4', '2');
INSERT INTO `game_recharge` VALUES ('91', '202089', '1.00', '2019-05-22 00:00:00', '4', '0');
INSERT INTO `game_recharge` VALUES ('92', '202089', '11.00', '2019-06-05 00:00:00', '4', '0');
INSERT INTO `game_recharge` VALUES ('93', '202089', '111.00', '2019-06-05 00:00:00', '3', '0');
INSERT INTO `game_recharge` VALUES ('94', '202089', '9000.00', '2019-06-06 00:00:00', '3', '0');
INSERT INTO `game_recharge` VALUES ('95', '202089', '643.00', '2019-06-06 00:00:00', '3', '0');
INSERT INTO `game_recharge` VALUES ('96', '202089', '43.00', '2019-06-19 00:00:00', '3', '2');
INSERT INTO `game_recharge` VALUES ('97', '202089', '0.00', '2019-07-10 00:00:00', '3', '0');

-- ----------------------------
-- Table structure for game_service
-- ----------------------------
DROP TABLE IF EXISTS `game_service`;
CREATE TABLE `game_service` (
  `serviceid` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '客服表',
  `qq` varchar(255) DEFAULT NULL COMMENT '二维码',
  `wx` varchar(255) DEFAULT NULL COMMENT '微信号',
  `img` varchar(255) DEFAULT NULL COMMENT '头像',
  `url` varchar(255) DEFAULT NULL COMMENT '客服链接',
  PRIMARY KEY (`serviceid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of game_service
-- ----------------------------
INSERT INTO `game_service` VALUES ('1', '1452261981', 'WX11552233', '/service/2b40a4c4-8e95-4a47-bc41-7dfa0f29ee0f.jpg', 'http://www.baidu.com');

-- ----------------------------
-- Table structure for insure_record
-- ----------------------------
DROP TABLE IF EXISTS `insure_record`;
CREATE TABLE `insure_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '保险箱转入转出记录',
  `createtime` datetime DEFAULT NULL,
  `money` double(11,1) DEFAULT NULL,
  `userid` bigint(20) DEFAULT NULL,
  `type` int(11) DEFAULT NULL COMMENT '0 转入 1转出',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of insure_record
-- ----------------------------

-- ----------------------------
-- Table structure for pk_record_table
-- ----------------------------
DROP TABLE IF EXISTS `pk_record_table`;
CREATE TABLE `pk_record_table` (
  `recordid` bigint(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) DEFAULT NULL COMMENT '用户id',
  `number` int(11) DEFAULT NULL COMMENT '分数',
  `money` varchar(255) DEFAULT NULL COMMENT '余额',
  `createdate` datetime DEFAULT NULL,
  PRIMARY KEY (`recordid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of pk_record_table
-- ----------------------------
INSERT INTO `pk_record_table` VALUES ('1', '202089', '1', '498682', null);
INSERT INTO `pk_record_table` VALUES ('2', '202095', '-1', '498682', null);
INSERT INTO `pk_record_table` VALUES ('3', '202089', '-1', '498682', null);
INSERT INTO `pk_record_table` VALUES ('4', '202095', '1', '498682', null);
INSERT INTO `pk_record_table` VALUES ('5', '202101', '-2', '435623', null);
INSERT INTO `pk_record_table` VALUES ('6', '202089', '2', '435623', null);
INSERT INTO `pk_record_table` VALUES ('7', '202097', '0', '435623', null);
INSERT INTO `pk_record_table` VALUES ('8', '202101', '-7', '435623', null);
INSERT INTO `pk_record_table` VALUES ('9', '202089', '7', '435623', null);
INSERT INTO `pk_record_table` VALUES ('10', '202097', '0', '435623', null);
INSERT INTO `pk_record_table` VALUES ('11', '202101', '6', '435623', null);
INSERT INTO `pk_record_table` VALUES ('12', '202089', '-9', '435623', null);
INSERT INTO `pk_record_table` VALUES ('13', '202097', '3', '435623', null);
INSERT INTO `pk_record_table` VALUES ('14', '202101', '-6', '435623', null);
INSERT INTO `pk_record_table` VALUES ('15', '202089', '0', '435623', null);
INSERT INTO `pk_record_table` VALUES ('16', '202097', '6', '435623', null);
INSERT INTO `pk_record_table` VALUES ('17', '202101', '5', '435623', null);
INSERT INTO `pk_record_table` VALUES ('18', '202089', '-12', '435623', null);
INSERT INTO `pk_record_table` VALUES ('19', '202097', '7', '435623', null);
INSERT INTO `pk_record_table` VALUES ('20', '202096', '-3', '466157', null);
INSERT INTO `pk_record_table` VALUES ('21', '202095', '6', '466157', null);
INSERT INTO `pk_record_table` VALUES ('22', '202099', '12', '466157', null);
INSERT INTO `pk_record_table` VALUES ('23', '202098', '-15', '466157', null);
INSERT INTO `pk_record_table` VALUES ('24', '202099', '144', '484773', null);
INSERT INTO `pk_record_table` VALUES ('25', '202098', '-48', '484773', null);
INSERT INTO `pk_record_table` VALUES ('26', '202095', '-48', '484773', null);
INSERT INTO `pk_record_table` VALUES ('27', '202096', '-48', '484773', null);
INSERT INTO `pk_record_table` VALUES ('28', '202099', '-5', '484773', null);
INSERT INTO `pk_record_table` VALUES ('29', '202098', '-26', '484773', null);
INSERT INTO `pk_record_table` VALUES ('30', '202095', '14', '484773', null);
INSERT INTO `pk_record_table` VALUES ('31', '202096', '17', '484773', null);
INSERT INTO `pk_record_table` VALUES ('32', '202099', '-1', '484773', null);
INSERT INTO `pk_record_table` VALUES ('33', '202098', '-5', '484773', null);
INSERT INTO `pk_record_table` VALUES ('34', '202095', '9', '484773', null);
INSERT INTO `pk_record_table` VALUES ('35', '202096', '-3', '484773', null);
INSERT INTO `pk_record_table` VALUES ('36', '202099', '-37', '484773', null);
INSERT INTO `pk_record_table` VALUES ('37', '202098', '64', '484773', null);
INSERT INTO `pk_record_table` VALUES ('38', '202095', '8', '484773', null);
INSERT INTO `pk_record_table` VALUES ('39', '202096', '-35', '484773', null);
INSERT INTO `pk_record_table` VALUES ('40', '202099', '-26', '484773', null);
INSERT INTO `pk_record_table` VALUES ('41', '202098', '-2', '484773', null);
INSERT INTO `pk_record_table` VALUES ('42', '202095', '18', '484773', null);
INSERT INTO `pk_record_table` VALUES ('43', '202096', '10', '484773', null);
INSERT INTO `pk_record_table` VALUES ('44', '202089', '-19', '455966', null);
INSERT INTO `pk_record_table` VALUES ('45', '202096', '-3', '455966', null);
INSERT INTO `pk_record_table` VALUES ('46', '202099', '18', '455966', null);
INSERT INTO `pk_record_table` VALUES ('47', '202097', '4', '455966', null);

-- ----------------------------
-- Table structure for rooms
-- ----------------------------
DROP TABLE IF EXISTS `rooms`;
CREATE TABLE `rooms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `createtime` datetime DEFAULT NULL,
  `roomnumber` int(11) DEFAULT NULL COMMENT '房间号',
  `maxnumber` int(11) DEFAULT NULL COMMENT '局数',
  `fen` double(11,2) DEFAULT NULL COMMENT '底分',
  `jionfen` int(11) DEFAULT NULL COMMENT '准入分',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rooms
-- ----------------------------

-- ----------------------------
-- Table structure for user_table
-- ----------------------------
DROP TABLE IF EXISTS `user_table`;
CREATE TABLE `user_table` (
  `userid` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `openid` varchar(255) DEFAULT NULL,
  `avatarurl` varchar(255) DEFAULT '0' COMMENT '头像',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(255) DEFAULT '无' COMMENT '手机号',
  `password` varchar(50) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL COMMENT '创建时间',
  `money` double(11,1) DEFAULT '1000.0' COMMENT '金币',
  `diamond` double(11,1) DEFAULT '0.0' COMMENT '房卡',
  `state` int(11) DEFAULT '0' COMMENT '账号状态 0正常 1冻结',
  `isLogin` int(11) DEFAULT '0' COMMENT '是否已登录',
  `sex` int(11) DEFAULT '0' COMMENT '0 女  1男',
  `fId` bigint(20) DEFAULT '0' COMMENT '上级ID  0无上级',
  `role` int(11) DEFAULT '0' COMMENT '角色  0普通玩家   1推广员',
  `code` varchar(6) DEFAULT '0' COMMENT '6位邀请码',
  `insure` double(11,1) DEFAULT '0.0' COMMENT '保险箱金额',
  `zfb` varchar(255) DEFAULT NULL COMMENT '支付宝账号',
  `bankcard` varchar(255) DEFAULT NULL COMMENT '银行卡账号',
  `backwater` int(11) DEFAULT '0' COMMENT '反水比例',
  `winodds` int(11) DEFAULT '-1' COMMENT '胜率',
  PRIMARY KEY (`userid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=202102 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user_table
-- ----------------------------
INSERT INTO `user_table` VALUES ('202089', '111111', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'MyAccount1', '无', null, '2020-04-10 15:45:50', '166.2', '58.0', '0', '0', '1', '202090', '1', '926555', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202090', '222222', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'MyAccount2', '无', null, '2020-04-10 15:45:50', '100.3', '64.0', '0', '0', '1', '202089', '0', '300050', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202091', '333333', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'MyAccount3', '无', null, '2020-04-10 15:45:50', '100.0', '50.0', '0', '0', '1', '0', '0', '684395', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202092', '666666', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'MyAccount6', '无', null, '2020-04-10 15:45:50', '100.0', '50.0', '0', '0', '1', '0', '0', '380376', '0.0', '15152014114', '123456', '0', '10');
INSERT INTO `user_table` VALUES ('202093', '555555', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'MyAccount5', '无', null, '2020-04-10 15:45:50', '100.0', '50.0', '0', '0', '1', '0', '0', '005133', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202094', '444444', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'MyAccount4', '无', null, '2020-04-10 15:45:50', '100.0', '52.0', '0', '0', '1', '202089', '0', '878817', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202095', 'opqlU56mM9O59NK3uLGFQq_IyBxU6', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'instance6', '无', null, '2020-04-10 15:45:50', '100.0', '50.0', '0', '0', '1', '0', '0', '056803', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202096', 'opqlU56mM9O59NK3uLGFQq_IyBxU2', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'instance1', '无', null, '2020-04-10 15:45:50', '100.0', '52.0', '0', '0', '1', '202097', '0', '462324', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202097', 'opqlU56mM9O59NK3uLGFQq_IyBxU1', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'instance2', '无', null, '2020-04-10 15:45:50', '100.0', '52.0', '0', '0', '1', '0', '0', '277670', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202098', 'opqlU56mM9O59NK3uLGFQq_IyBxU4', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'instance4', '无', null, '2020-04-10 15:45:50', '100.0', '50.0', '0', '0', '1', '0', '0', '754069', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202099', 'opqlU56mM9O59NK3uLGFQq_IyBxU3', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'instance3', '无', null, '2020-04-10 15:45:50', '100.0', '50.0', '0', '0', '1', '0', '0', '346562', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202100', 'opqlU56mM9O59NK3uLGFQq_IyBxU5', 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI6GR7rfOJ2H4oFGW8W1ACbmUBx2WlQjrqvXZjbB0w98E6icpr4iatdKHHGocHBNlnaGBSBZaaZ0grw/132', 'instance5', '无', null, '2020-04-10 15:45:50', '100.0', '50.0', '0', '0', '1', '0', '0', '682988', '0.0', '15152014114', '123456', '0', '-1');
INSERT INTO `user_table` VALUES ('202101', 'owalt1E88m1ITAgCYu3KGZMGnXlY', 'http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83eru7OIibuASgic3J8wtbYkn9mMcSAKSZvoLmrctqriacV0iaMrGygBia5R0cwfyhxNTYjk7CDqowfsmspA/132', '张文方', '无', null, '2020-04-10 15:45:50', '100.0', '62.0', '0', '0', '1', '202089', '0', '942343', '0.0', '15152014114', '123456', '0', '-1');
