/*
Navicat MariaDB Data Transfer

Source Server         : localhost
Source Server Version : 100019
Source Host           : localhost:3306
Source Database       : essh_v2.0

Target Server Type    : MariaDB
Target Server Version : 100019
File Encoding         : 65001

Date: 2015-05-31 10:06:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_disk_file
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_file`;
CREATE TABLE `t_disk_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(128) NOT NULL,
  `file_path` varchar(1024) DEFAULT NULL,
  `file_size` bigint(20) DEFAULT NULL,
  `file_suffix` varchar(36) DEFAULT NULL,
  `file_type` int(11) DEFAULT NULL,
  `keyword` varchar(128) DEFAULT NULL,
  `name` varchar(512) NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `share_user_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `folder_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_451ihoi6yukkju3h47fo3ydyk` (`folder_id`),
  CONSTRAINT `FK_451ihoi6yukkju3h47fo3ydyk` FOREIGN KEY (`folder_id`) REFERENCES `t_disk_folder` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_folder
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_folder`;
CREATE TABLE `t_disk_folder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `folder_authorize` int(11) DEFAULT NULL,
  `limit_size` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `organ_id` bigint(20) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_folder
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_organ_storage
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_organ_storage`;
CREATE TABLE `t_disk_organ_storage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `limit_size` int(11) DEFAULT NULL,
  `organ_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_organ_storage
-- ----------------------------

-- ----------------------------
-- Table structure for t_disk_user_storage
-- ----------------------------
DROP TABLE IF EXISTS `t_disk_user_storage`;
CREATE TABLE `t_disk_user_storage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `limit_size` int(11) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_disk_user_storage
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_notice
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_notice`;
CREATE TABLE `t_notice_notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `content` varchar(4096) DEFAULT NULL,
  `effect_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `end_top_day` int(11) DEFAULT NULL,
  `is_record_read` int(11) DEFAULT NULL,
  `is_to_all` int(11) DEFAULT NULL,
  `is_top` int(11) DEFAULT NULL,
  `notice_mode` int(11) DEFAULT NULL,
  `organ_id` bigint(20) DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `title` varchar(512) DEFAULT NULL,
  `type` varchar(36) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice_notice
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_notice_file
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_notice_file`;
CREATE TABLE `t_notice_notice_file` (
  `notice_id` bigint(20) NOT NULL,
  `file_id` bigint(20) DEFAULT NULL,
  KEY `FK_klwa06kkhdlcrcvnlruxdmpq9` (`notice_id`),
  CONSTRAINT `FK_klwa06kkhdlcrcvnlruxdmpq9` FOREIGN KEY (`notice_id`) REFERENCES `t_notice_notice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice_notice_file
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_notice_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_notice_organ`;
CREATE TABLE `t_notice_notice_organ` (
  `notice_id` bigint(20) NOT NULL,
  `organ_id` bigint(20) DEFAULT NULL,
  KEY `FK_cnciwgtc6470yvwtkhevxabhc` (`notice_id`),
  CONSTRAINT `FK_cnciwgtc6470yvwtkhevxabhc` FOREIGN KEY (`notice_id`) REFERENCES `t_notice_notice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice_notice_organ
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_notice_scope
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_notice_scope`;
CREATE TABLE `t_notice_notice_scope` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `notice_id` bigint(20) DEFAULT NULL,
  `is_read` int(11) DEFAULT NULL,
  `organ_id` bigint(20) DEFAULT NULL,
  `read_time` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice_notice_scope
-- ----------------------------

-- ----------------------------
-- Table structure for t_notice_notice_user
-- ----------------------------
DROP TABLE IF EXISTS `t_notice_notice_user`;
CREATE TABLE `t_notice_notice_user` (
  `notice_id` bigint(20) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  KEY `FK_aubwmu312txuoglpde3ecqxng` (`notice_id`),
  CONSTRAINT `FK_aubwmu312txuoglpde3ecqxng` FOREIGN KEY (`notice_id`) REFERENCES `t_notice_notice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice_notice_user
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_bug
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_bug`;
CREATE TABLE `t_sys_bug` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `color` varchar(12) DEFAULT NULL,
  `content` text,
  `title` varchar(255) DEFAULT NULL,
  `type` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_bug
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_config`;
CREATE TABLE `t_sys_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `value` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_config
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dictionary`;
CREATE TABLE `t_sys_dictionary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `remak` varchar(100) DEFAULT NULL,
  `value` varchar(100) DEFAULT NULL,
  `dictionarytype_code` varchar(36) DEFAULT NULL,
  `parent_code` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_oya5576xpt36m50jajdtf15k8` (`code`) USING BTREE,
  UNIQUE KEY `UK_ft2gulyh54nygkbyhu05rkd5l` (`name`) USING BTREE,
  KEY `FK_5sqpn4281nmn3kyxe9qh3clsq` (`dictionarytype_code`) USING BTREE,
  KEY `FK_h94gyv9yt8lsfmsl6o7nbxgq4` (`parent_code`) USING BTREE,
  CONSTRAINT `t_sys_dictionary_ibfk_1` FOREIGN KEY (`parent_code`) REFERENCES `t_sys_dictionary` (`code`),
  CONSTRAINT `t_sys_dictionary_ibfk_2` FOREIGN KEY (`dictionarytype_code`) REFERENCES `t_sys_dictionarytype` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dictionary
-- ----------------------------
INSERT INTO `t_sys_dictionary` VALUES ('1', '2014-07-12 22:49:43', 'admin', '0', null, null, '0', 'bug001', '通知公告', '1', '', 'bug001', 'bug', null);
INSERT INTO `t_sys_dictionary` VALUES ('2', '2014-07-12 23:06:33', 'admin', '0', null, null, '0', 'bug002', '栏目', '2', '', 'bug002', 'bug', null);
INSERT INTO `t_sys_dictionary` VALUES ('5', '2014-07-12 23:19:35', 'admin', '0', null, null, '0', 'bug002001', '子栏目', '3', '', 'bug002001', 'bug', 'bug002');

-- ----------------------------
-- Table structure for t_sys_dictionarytype
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_dictionarytype`;
CREATE TABLE `t_sys_dictionarytype` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `group_code` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_od88eya35c9mpgmpv80mhb2p7` (`code`) USING BTREE,
  UNIQUE KEY `UK_bhiy88uioc7rokt14kgpumcv3` (`name`) USING BTREE,
  KEY `FK_j3genbrg817v6683pnm5hacnr` (`group_code`) USING BTREE,
  CONSTRAINT `t_sys_dictionarytype_ibfk_1` FOREIGN KEY (`group_code`) REFERENCES `t_sys_dictionarytype` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_dictionarytype
-- ----------------------------
INSERT INTO `t_sys_dictionarytype` VALUES ('1', '2014-07-12 22:48:44', 'admin', '0', '2014-07-12 22:49:04', 'admin', '2', 'sys', '系统管理', '1', '', null);
INSERT INTO `t_sys_dictionarytype` VALUES ('2', '2014-07-12 22:49:13', 'admin', '0', null, null, '0', 'bug', '内容管理', '2', '', 'sys');

-- ----------------------------
-- Table structure for t_sys_log
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_log`;
CREATE TABLE `t_sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `action` varchar(255) DEFAULT NULL,
  `action_time` varchar(20) DEFAULT NULL,
  `browser_type` varchar(255) DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `login_name` varchar(36) DEFAULT NULL,
  `module` varchar(36) DEFAULT NULL,
  `oper_time` datetime DEFAULT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=171 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_log
-- ----------------------------
INSERT INTO `t_sys_log` VALUES ('131', '2015-05-30 11:31:37', 'admin', '0', '2015-05-30 11:31:37', 'admin', '0', '用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 11:31:37', null, '0');
INSERT INTO `t_sys_log` VALUES ('132', '2015-05-30 13:28:18', 'admin', '0', '2015-05-30 13:28:18', 'admin', '0', '用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 13:28:18', null, '0');
INSERT INTO `t_sys_log` VALUES ('133', '2015-05-30 13:35:52', 'admin', '0', '2015-05-30 13:35:52', 'admin', '0', '用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 13:35:52', null, '0');
INSERT INTO `t_sys_log` VALUES ('134', '2015-05-30 14:34:27', 'admin', '0', '2015-05-30 14:34:27', 'admin', '0', '用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 14:34:27', null, '0');
INSERT INTO `t_sys_log` VALUES ('135', '2015-05-30 14:42:36', 'admin', '0', '2015-05-30 14:42:36', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 14:42:36', null, '0');
INSERT INTO `t_sys_log` VALUES ('136', '2015-05-30 17:42:48', 'admin', '0', '2015-05-30 17:42:48', 'admin', '0', '[admin]用户非正常注销', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-logout', '2015-05-30 17:42:48', null, '0');
INSERT INTO `t_sys_log` VALUES ('137', '2015-05-30 17:52:22', 'admin', '0', '2015-05-30 17:52:22', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 17:52:22', null, '0');
INSERT INTO `t_sys_log` VALUES ('138', '2015-05-30 18:19:00', 'admin', '0', '2015-05-30 18:19:00', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 18:19:00', null, '0');
INSERT INTO `t_sys_log` VALUES ('139', '2015-05-30 18:19:48', 'admin', '0', '2015-05-30 18:19:48', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('140', '2015-05-30 18:32:28', 'admin', '0', '2015-05-30 18:32:28', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('141', '2015-05-30 18:34:09', 'admin', '0', '2015-05-30 18:34:09', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('142', '2015-05-30 19:59:11', 'admin', '0', '2015-05-30 19:59:11', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 19:59:11', null, '0');
INSERT INTO `t_sys_log` VALUES ('143', '2015-05-30 20:58:53', 'admin', '0', '2015-05-30 20:58:53', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 20:58:53', null, '0');
INSERT INTO `t_sys_log` VALUES ('144', '2015-05-30 21:07:59', 'admin', '0', '2015-05-30 21:07:59', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 21:07:59', null, '0');
INSERT INTO `t_sys_log` VALUES ('145', '2015-05-30 21:08:32', 'admin', '0', '2015-05-30 21:08:32', 'admin', '0', '[admin]用户注销', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-logout', '2015-05-30 21:08:32', null, '0');
INSERT INTO `t_sys_log` VALUES ('146', '2015-05-30 21:08:41', 'admin', '0', '2015-05-30 21:08:41', 'admin', '0', '[admin]用户注销', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-logout', '2015-05-30 21:08:41', null, '0');
INSERT INTO `t_sys_log` VALUES ('147', '2015-05-30 21:10:35', 'admin', '0', '2015-05-30 21:10:35', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 21:10:35', null, '0');
INSERT INTO `t_sys_log` VALUES ('148', '2015-05-30 21:11:36', 'admin', '0', '2015-05-30 21:11:36', 'admin', '0', '[admin]用户登录', '0', 'IE11', '127.0.0.1', 'admin', 'LoginController-login', '2015-05-30 21:11:36', null, '0');
INSERT INTO `t_sys_log` VALUES ('149', '2015-05-30 21:14:03', 'admin', '0', '2015-05-30 21:14:03', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 21:14:03', null, '0');
INSERT INTO `t_sys_log` VALUES ('150', '2015-05-30 21:21:21', 'admin', '0', '2015-05-30 21:21:21', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 21:21:21', null, '0');
INSERT INTO `t_sys_log` VALUES ('151', '2015-05-30 21:23:01', 'admin', '0', '2015-05-30 21:23:01', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/a/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('152', '2015-05-30 21:24:39', 'admin', '0', '2015-05-30 21:24:39', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/a/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('153', '2015-05-30 21:25:16', 'admin', '0', '2015-05-30 21:25:16', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/a/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('154', '2015-05-30 21:25:53', 'admin', '0', '2015-05-30 21:25:53', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/a/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('155', '2015-05-30 21:26:15', 'admin', '0', '2015-05-30 21:26:15', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/a/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('156', '2015-05-30 21:27:18', 'admin', '0', '2015-05-30 21:27:18', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/a/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('157', '2015-05-30 21:42:16', 'admin', '0', '2015-05-30 21:42:16', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 21:42:16', null, '0');
INSERT INTO `t_sys_log` VALUES ('158', '2015-05-30 21:44:46', 'admin', '0', '2015-05-30 21:44:46', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 21:44:46', null, '0');
INSERT INTO `t_sys_log` VALUES ('159', '2015-05-30 22:04:36', 'admin', '0', '2015-05-30 22:04:36', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 22:04:36', null, '0');
INSERT INTO `t_sys_log` VALUES ('160', '2015-05-30 22:06:47', 'admin', '0', '2015-05-30 22:06:47', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 22:06:47', null, '0');
INSERT INTO `t_sys_log` VALUES ('161', '2015-05-30 22:08:44', 'admin', '0', '2015-05-30 22:08:44', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 22:08:44', null, '0');
INSERT INTO `t_sys_log` VALUES ('162', '2015-05-30 22:10:36', 'admin', '0', '2015-05-30 22:10:36', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 22:10:36', null, '0');
INSERT INTO `t_sys_log` VALUES ('163', '2015-05-30 22:13:15', 'admin', '0', '2015-05-30 22:13:15', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 22:13:14', null, '0');
INSERT INTO `t_sys_log` VALUES ('164', '2015-05-30 22:14:38', 'admin', '0', '2015-05-30 22:14:38', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-30 22:14:38', null, '0');
INSERT INTO `t_sys_log` VALUES ('165', '2015-05-30 22:15:22', 'admin', '0', '2015-05-30 22:15:22', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/a/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('166', '2015-05-31 09:41:18', 'admin', '0', '2015-05-31 09:41:18', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-31 09:41:18', null, '0');
INSERT INTO `t_sys_log` VALUES ('167', '2015-05-31 09:42:01', 'admin', '0', '2015-05-31 09:42:01', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/a/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('168', '2015-05-31 09:42:07', 'admin', '0', '2015-05-31 09:42:07', 'admin', '0', 'POST', null, 'Chrome', '0:0:0:0:0:0:0:1', 'admin', '/a/disk/diskTree', null, '请求参数：异常信息：java.lang.NullPointerException: 参数[organId]不能为null.', '3');
INSERT INTO `t_sys_log` VALUES ('169', '2015-05-31 09:42:37', 'admin', '0', '2015-05-31 09:42:37', 'admin', '0', '[admin]用户注销', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-logout', '2015-05-31 09:42:37', null, '0');
INSERT INTO `t_sys_log` VALUES ('170', '2015-05-31 09:42:38', 'admin', '0', '2015-05-31 09:42:38', 'admin', '0', '[admin]用户登录', '0', 'Chrome', '0:0:0:0:0:0:0:1', 'admin', 'LoginController-login', '2015-05-31 09:42:38', null, '0');

-- ----------------------------
-- Table structure for t_sys_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_organ`;
CREATE TABLE `t_sys_organ` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `fax` varchar(64) DEFAULT NULL,
  `manager_user_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `order_no` int(11) DEFAULT NULL,
  `phone` varchar(64) DEFAULT NULL,
  `sys_code` varchar(36) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `super_manager_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_xhoy66phxt86eq9i1xdp9typ` (`name`) USING BTREE,
  KEY `FK_sixv5h7puaswyuptn1f4mnq5b` (`parent_id`) USING BTREE,
  KEY `FK_klg4pxi6cmbo6nu6tvj652x2` (`super_manager_user_id`) USING BTREE,
  CONSTRAINT `t_sys_organ_ibfk_1` FOREIGN KEY (`super_manager_user_id`) REFERENCES `t_sys_user` (`id`),
  CONSTRAINT `t_sys_organ_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `t_sys_organ` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_organ
-- ----------------------------
INSERT INTO `t_sys_organ` VALUES ('1', '2014-07-21 21:40:21', 'admin', '0', '2014-07-21 21:40:58', 'admin', '1', '', '', '', '1', '尔演科技', '1', '', '00', '0', null, '1');
INSERT INTO `t_sys_organ` VALUES ('2', '2014-07-21 21:40:42', 'admin', '0', '2014-08-01 20:19:15', 'admin', '5', '', '', '', null, '研发中心', '2', '', '0001', '1', '1', null);
INSERT INTO `t_sys_organ` VALUES ('3', '2014-07-30 19:13:06', 'admin', '0', '2014-08-01 20:19:06', 'admin', '5', '', '', '', null, '软件研发部', '3', '', '000101', '1', '2', null);
INSERT INTO `t_sys_organ` VALUES ('4', '2014-07-30 19:13:28', 'admin', '0', null, null, '0', '', '', '', null, '商务中心', '4', '', '0002', '0', '1', null);
INSERT INTO `t_sys_organ` VALUES ('5', '2014-07-30 19:13:42', 'admin', '0', '2014-07-30 19:13:50', 'admin', '1', '', '', '', null, '行政部', '5', '', '000201', '0', '4', null);

-- ----------------------------
-- Table structure for t_sys_post
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_post`;
CREATE TABLE `t_sys_post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `remark` varchar(36) DEFAULT NULL,
  `organ_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_do2moeabchk0jr90i3d7qjkhe` (`organ_id`) USING BTREE,
  CONSTRAINT `t_sys_post_ibfk_1` FOREIGN KEY (`organ_id`) REFERENCES `t_sys_organ` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_post
-- ----------------------------
INSERT INTO `t_sys_post` VALUES ('1', '2014-07-22 19:30:39', 'admin', '0', '2014-07-25 13:07:43', 'admin', '8', '', '软件工程师', '', '2');

-- ----------------------------
-- Table structure for t_sys_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_resource`;
CREATE TABLE `t_sys_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL,
  `icon_cls` varchar(255) DEFAULT NULL,
  `mark_url` varchar(2000) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  `order_no` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_h232wdc1pbxbfbgmckm1msf92` (`parent_id`) USING BTREE,
  CONSTRAINT `t_sys_resource_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `t_sys_resource` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_resource
-- ----------------------------
INSERT INTO `t_sys_resource` VALUES ('1', null, null, '0', '2014-08-26 12:28:26', 'admin', '5', '', null, 'eu-icon-application', '', '系统管理', '1', '0', '', null);
INSERT INTO `t_sys_resource` VALUES ('2', null, null, '0', '2014-08-26 12:28:35', 'admin', '11', '', null, 'eu-icon-folder', '', '资源管理', '2', '0', 'sys/resource', '1');
INSERT INTO `t_sys_resource` VALUES ('3', '2013-11-12 22:13:42', 'admin', '0', '2014-08-26 12:28:40', 'admin', '15', '', null, 'eu-icon-group', '', '角色管理', '3', '0', 'sys/role', '1');
INSERT INTO `t_sys_resource` VALUES ('4', '2013-11-12 22:14:10', 'admin', '0', '2014-08-26 12:28:49', 'admin', '7', '', null, 'eu-icon-group', '', '机构管理', '4', '0', 'sys/organ', '1');
INSERT INTO `t_sys_resource` VALUES ('5', '2013-11-12 22:14:28', 'admin', '0', '2014-08-26 12:29:08', 'admin', '5', '', null, 'eu-icon-user', '', '用户管理', '6', '0', 'sys/user', '1');
INSERT INTO `t_sys_resource` VALUES ('7', '2013-11-12 22:15:13', 'admin', '0', '2014-08-26 12:29:14', 'admin', '5', '', null, 'eu-icon-book', '', '字典类型', '7', '0', 'sys/dictionary-type', '1');
INSERT INTO `t_sys_resource` VALUES ('8', '2013-11-12 22:15:40', 'admin', '0', '2014-08-26 12:29:24', 'admin', '8', '', null, 'eu-icon-email', '', '数据字典', '8', '0', 'sys/dictionary', '1');
INSERT INTO `t_sys_resource` VALUES ('9', '2013-11-12 22:15:57', 'admin', '0', '2014-08-26 12:29:35', 'admin', '5', '', null, 'eu-icon-bug', '', '内容管理', '9', '0', 'sys/bug', '1');
INSERT INTO `t_sys_resource` VALUES ('11', '2013-11-16 23:45:54', 'admin', '0', '2014-08-26 12:32:51', 'admin', '6', 'bug:add', null, 'easyui-icon-add', '', '新增', '10', '1', '', '9');
INSERT INTO `t_sys_resource` VALUES ('16', '2013-11-17 00:10:23', 'admin', '0', '2014-08-26 12:32:59', 'admin', '3', 'bug:edit', null, 'easyui-icon-edit', '', '编辑', '11', '1', '', '9');
INSERT INTO `t_sys_resource` VALUES ('17', '2013-11-17 00:11:05', 'admin', '0', '2013-11-17 20:16:38', 'admin', '2', 'bug:importExcel', null, '', '', 'Excel导入', '12', '1', '', '9');
INSERT INTO `t_sys_resource` VALUES ('18', '2013-11-17 00:13:35', 'admin', '0', '2013-11-17 20:16:41', 'admin', '1', 'bug:exportExcel', null, '', '', 'Excel导出', '13', '1', '', '9');
INSERT INTO `t_sys_resource` VALUES ('19', '2013-11-17 00:14:32', 'admin', '0', '2014-08-26 12:33:09', 'admin', '2', 'bug:remove', null, 'easyui-icon-remove', '', '批量删除', '14', '1', '', '9');
INSERT INTO `t_sys_resource` VALUES ('20', '2013-11-17 00:46:09', 'eryan', '0', '2014-08-26 12:33:19', 'admin', '2', 'bug:view', null, 'easyui-icon-search', '', '查看', '15', '1', '', '9');
INSERT INTO `t_sys_resource` VALUES ('21', '2013-12-08 17:26:38', 'admin', '0', '2014-08-26 12:29:44', 'admin', '5', '', null, 'eu-icon-monitor', '', '日志管理', '10', '0', 'sys/log', '1');
INSERT INTO `t_sys_resource` VALUES ('26', '2014-06-11 19:43:48', 'admin', '0', '2014-08-26 12:29:03', 'admin', '5', '', null, 'eu-icon-vcard', '', '岗位管理', '5', '0', 'sys/post', '1');
INSERT INTO `t_sys_resource` VALUES ('29', '2015-05-30 13:28:51', 'admin', '0', '2015-05-30 13:28:51', 'admin', '0', '', null, '', '', '配置管理', '16', '0', 'sys/config', '1');
INSERT INTO `t_sys_resource` VALUES ('30', '2015-05-30 18:19:23', 'admin', '0', '2015-05-30 18:19:43', 'admin', '1', '', null, '', '', '云盘管理', '17', '0', 'disk', '1');
INSERT INTO `t_sys_resource` VALUES ('31', '2015-05-30 21:42:50', 'admin', '0', '2015-05-30 21:42:56', 'admin', '1', '', null, 'eu-icon-application', '', '我的工作', '18', '0', '', null);
INSERT INTO `t_sys_resource` VALUES ('32', '2015-05-30 21:43:14', 'admin', '0', '2015-05-30 21:43:14', 'admin', '0', '', null, '', '', '我的通知', '19', '0', 'notice/notice', '31');

-- ----------------------------
-- Table structure for t_sys_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ljbh7lmeyig6pbauwq7mv4atk` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_role_resource`;
CREATE TABLE `t_sys_role_resource` (
  `role_id` bigint(20) NOT NULL,
  `resource_id` bigint(20) NOT NULL,
  KEY `FK_o4bwr9474osu6umabgmxbs3qk` (`resource_id`) USING BTREE,
  KEY `FK_ehsis8jjgkl0tp0xq3rnawvvp` (`role_id`) USING BTREE,
  CONSTRAINT `t_sys_role_resource_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`id`),
  CONSTRAINT `t_sys_role_resource_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `t_sys_resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_role_resource
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_user
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user`;
CREATE TABLE `t_sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `create_user` varchar(36) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `login_name` varchar(36) NOT NULL,
  `mobilephone` varchar(36) DEFAULT NULL,
  `name` varchar(36) DEFAULT NULL,
  `password` varchar(64) NOT NULL,
  `sex` int(11) DEFAULT NULL,
  `tel` varchar(36) DEFAULT NULL,
  `default_organid` bigint(20) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `order_no` int(11) DEFAULT NULL,
  `photo` varchar(1000) DEFAULT NULL,
  `remark` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_f5qmscb98th0wvaayv07mrunh` (`default_organid`) USING BTREE,
  CONSTRAINT `t_sys_user_ibfk_1` FOREIGN KEY (`default_organid`) REFERENCES `t_sys_organ` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user
-- ----------------------------
INSERT INTO `t_sys_user` VALUES ('1', null, null, '0', '2015-05-31 09:42:32', 'admin', '8', '', '', 'admin', '', '管理员', '5f4dcc3b5aa765d61d8327deb882cf99', '2', '', '1', null, null, '', '');

-- ----------------------------
-- Table structure for t_sys_user_organ
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_organ`;
CREATE TABLE `t_sys_user_organ` (
  `user_id` bigint(20) NOT NULL,
  `organ_id` bigint(20) NOT NULL,
  KEY `FK_g9ryaj7pdmtol9p8kqosbc2j2` (`organ_id`) USING BTREE,
  KEY `FK_l3dn8e1drj3p7i2b73pe40vu5` (`user_id`) USING BTREE,
  CONSTRAINT `t_sys_user_organ_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`id`),
  CONSTRAINT `t_sys_user_organ_ibfk_2` FOREIGN KEY (`organ_id`) REFERENCES `t_sys_organ` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_organ
-- ----------------------------
INSERT INTO `t_sys_user_organ` VALUES ('1', '1');

-- ----------------------------
-- Table structure for t_sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_post`;
CREATE TABLE `t_sys_user_post` (
  `user_id` bigint(20) NOT NULL,
  `post_id` bigint(20) NOT NULL,
  KEY `FK_6o9ao50lhoc3w2pveiy5ull8e` (`post_id`) USING BTREE,
  KEY `FK_ifxlp9gug6x73orq3cliviugy` (`user_id`) USING BTREE,
  CONSTRAINT `t_sys_user_post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`id`),
  CONSTRAINT `t_sys_user_post_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `t_sys_post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_post
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_user_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_resource`;
CREATE TABLE `t_sys_user_resource` (
  `user_id` bigint(20) NOT NULL,
  `resource_id` bigint(20) NOT NULL,
  KEY `FK_3w943h1pd1874m8cwu8l8h5rm` (`resource_id`) USING BTREE,
  KEY `FK_ft3xc4i3casdwlrlhsxr6mstu` (`user_id`) USING BTREE,
  CONSTRAINT `t_sys_user_resource_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`id`),
  CONSTRAINT `t_sys_user_resource_ibfk_2` FOREIGN KEY (`resource_id`) REFERENCES `t_sys_resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_resource
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_user_role`;
CREATE TABLE `t_sys_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  KEY `FK_fhpxr8fohqwyr0ee4fjkf1xd2` (`role_id`) USING BTREE,
  KEY `FK_rxiutb8ymuxoc91qa0able8vb` (`user_id`) USING BTREE,
  CONSTRAINT `t_sys_user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `t_sys_user` (`id`),
  CONSTRAINT `t_sys_user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `t_sys_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for t_sys_version_log
-- ----------------------------
DROP TABLE IF EXISTS `t_sys_version_log`;
CREATE TABLE `t_sys_version_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_type` int(11) DEFAULT NULL,
  `remark` varchar(8192) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` varchar(36) DEFAULT NULL,
  `url` varchar(1024) DEFAULT NULL,
  `version_code` varchar(36) DEFAULT NULL,
  `version_name` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_sys_version_log
-- ----------------------------
