/*
SQLyog Trial v13.2.1 (64 bit)
MySQL - 8.0.31 : Database - yhs_job
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE
DATABASE /*!32312 IF NOT EXISTS*/`yhs_job` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE
`yhs_job`;

/*Table structure for table `xxl_job_group` */

DROP TABLE IF EXISTS `xxl_job_group`;

CREATE TABLE `xxl_job_group`
(
    `id`           int                                                          NOT NULL AUTO_INCREMENT,
    `app_name`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器AppName',
    `title`        varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行器名称',
    `address_type` tinyint                                                      NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
    `address_list` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '执行器地址列表，多地址逗号分隔',
    `update_time`  datetime                                                              DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=199 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `xxl_job_info` */

DROP TABLE IF EXISTS `xxl_job_info`;

CREATE TABLE `xxl_job_info`
(
    `id`                        int                                                           NOT NULL AUTO_INCREMENT,
    `job_group`                 int                                                           NOT NULL COMMENT '执行器主键ID',
    `job_desc`                  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `add_time`                  datetime                                                               DEFAULT NULL,
    `update_time`               datetime                                                               DEFAULT NULL,
    `author`                    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT NULL COMMENT '作者',
    `alarm_email`               varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '报警邮件',
    `schedule_type`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
    `schedule_conf`             varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
    `misfire_strategy`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
    `executor_route_strategy`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT NULL COMMENT '执行器路由策略',
    `executor_handler`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param`            varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '执行器任务参数',
    `executor_block_strategy`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT NULL COMMENT '阻塞处理策略',
    `executor_timeout`          int                                                           NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
    `executor_fail_retry_count` int                                                           NOT NULL DEFAULT '0' COMMENT '失败重试次数',
    `glue_type`                 varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT 'GLUE类型',
    `glue_source`               mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'GLUE源代码',
    `glue_remark`               varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT 'GLUE备注',
    `glue_updatetime`           datetime                                                               DEFAULT NULL COMMENT 'GLUE更新时间',
    `child_jobid`               varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
    `trigger_status`            tinyint                                                       NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
    `trigger_last_time`         bigint                                                        NOT NULL DEFAULT '0' COMMENT '上次调度时间',
    `trigger_next_time`         bigint                                                        NOT NULL DEFAULT '0' COMMENT '下次调度时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1155 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `xxl_job_log` */

DROP TABLE IF EXISTS `xxl_job_log`;

CREATE TABLE `xxl_job_log`
(
    `id`                        bigint  NOT NULL AUTO_INCREMENT,
    `job_group`                 int     NOT NULL COMMENT '执行器主键ID',
    `job_id`                    int     NOT NULL COMMENT '任务，主键ID',
    `executor_address`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
    `executor_handler`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行器任务handler',
    `executor_param`            varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '执行器任务参数',
    `executor_sharding_param`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
    `executor_fail_retry_count` int     NOT NULL                                              DEFAULT '0' COMMENT '失败重试次数',
    `trigger_time`              datetime                                                      DEFAULT NULL COMMENT '调度-时间',
    `trigger_code`              int     NOT NULL COMMENT '调度-结果',
    `trigger_msg`               text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '调度-日志',
    `handle_time`               datetime                                                      DEFAULT NULL COMMENT '执行-时间',
    `handle_code`               int     NOT NULL COMMENT '执行-状态',
    `handle_msg`                text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '执行-日志',
    `alarm_status`              tinyint NOT NULL                                              DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                         `I_trigger_time` (`trigger_time`) USING BTREE,
    KEY                         `I_handle_code` (`handle_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=126383752 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `xxl_job_log_report` */

DROP TABLE IF EXISTS `xxl_job_log_report`;

CREATE TABLE `xxl_job_log_report`
(
    `id`            int NOT NULL AUTO_INCREMENT,
    `trigger_day`   datetime     DEFAULT NULL COMMENT '调度-时间',
    `running_count` int NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
    `suc_count`     int NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
    `fail_count`    int NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
    `update_time`   datetime     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=603 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `xxl_job_registry` */

DROP TABLE IF EXISTS `xxl_job_registry`;

CREATE TABLE `xxl_job_registry`
(
    `id`             int                                                           NOT NULL AUTO_INCREMENT,
    `registry_group` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL,
    `registry_key`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `registry_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `update_time`    datetime DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    KEY              `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=90183 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Table structure for table `xxl_job_lock` */

DROP TABLE IF EXISTS `xxl_job_lock`;

CREATE TABLE `xxl_job_lock`
(
    `lock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '锁名称',
    PRIMARY KEY (`lock_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `xxl_job_lock` */

insert into `xxl_job_lock`(`lock_name`)
values ('schedule_lock');

/*Table structure for table `xxl_job_logglue` */

DROP TABLE IF EXISTS `xxl_job_logglue`;

CREATE TABLE `xxl_job_logglue`
(
    `id`          int                                                           NOT NULL AUTO_INCREMENT,
    `job_id`      int                                                           NOT NULL COMMENT '任务，主键ID',
    `glue_type`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'GLUE类型',
    `glue_source` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT 'GLUE源代码',
    `glue_remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'GLUE备注',
    `add_time`    datetime                                                     DEFAULT NULL,
    `update_time` datetime                                                     DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `xxl_job_logglue` */

insert into `xxl_job_logglue`(`id`, `job_id`, `glue_type`, `glue_source`, `glue_remark`, `add_time`, `update_time`)
values (1, 5, 'GLUE_GROOVY',
        'package com.xxl.job.service.handler;\n\nimport com.xxl.job.core.context.XxlJobHelper;\nimport com.xxl.job.core.handler.IJobHandler;\n\npublic class DemoGlueJobHandler extends IJobHandler {\n  \n  	@Resource\n    private RedisClientService redisClientService;\n  	\n\n	@Override\n	public void execute() throws Exception {\n		XxlJobHelper.log(\"XXL-JOB, Hello World.\");\n        String lock = redisClientService.get(\"lock\");\n        XxlJobHelper.log(\"LOCK IS -> {}\",lock);\n        if(lock == null || lock == \'\'){\n        	XxlJobHelper.log(\"LOCK IS Null -> {}\",lock);\n            throw new RuntimeException(\"Lock is null\");\n        }\n        XxlJobHelper.log(\"LOCK IS NOT NULL -> {}\",lock);\n	}\n\n}\n',
        'test', '2022-04-28 14:40:41', '2022-04-28 14:40:41'),
       (2, 5, 'GLUE_GROOVY',
        'package com.xxl.job.service.handler;\n\nimport com.xxl.job.core.context.XxlJobHelper;\nimport com.xxl.job.core.handler.IJobHandler;\nimport com.yhs.springboot.redis.core.RedisClientService;\n\npublic class DemoGlueJobHandler extends IJobHandler {\n  \n  	@Resource\n    private RedisClientService redisClientService;\n  	\n\n	@Override\n	public void execute() throws Exception {\n        \n		XxlJobHelper.log(\"XXL-JOB, Hello World.\");\n        String lock = redisClientService.get(\"lock\");\n        XxlJobHelper.log(\"LOCK IS -> {}\",lock);\n        if(lock == null || lock == \'\'){\n        	XxlJobHelper.log(\"LOCK IS Null -> {}\",lock);\n            throw new RuntimeException(\"Lock is null\");\n        }\n        XxlJobHelper.log(\"LOCK IS NOT NULL -> {}\",lock);\n	}\n\n}\n',
        'import', '2022-04-28 14:47:59', '2022-04-28 14:47:59'),
       (3, 5, 'GLUE_GROOVY',
        'package com.xxl.job.service.handler;\n\nimport com.xxl.job.core.context.XxlJobHelper;\nimport com.xxl.job.core.handler.IJobHandler;\nimport com.yhs.springboot.redis.core.RedisClientService;\n\npublic class DemoGlueJobHandler extends IJobHandler {\n  \n  	@Resource\n    private RedisClientService redisClientService;\n  	\n\n	@Override\n	public void execute() throws Exception {\n        \n		XxlJobHelper.log(\"XXL-JOB, Hello World.\");\n        String lock = redisClientService.get(\"lock\");\n        XxlJobHelper.log(\"LOCK IS -> {}\",lock);\n        if(lock == null || lock == \'\'){\n        	XxlJobHelper.log(\"LOCK IS Null -> {}\",lock);\n            throw new RuntimeException(\"Lock is null\");\n        }\n        XxlJobHelper.log(\"LOCK IS NOT NULL -> {}\",lock);\n	}\n\n}\n',
        '2123', '2022-04-28 14:48:36', '2022-04-28 14:48:36'),
       (4, 5, 'GLUE_GROOVY',
        'package com.xxl.job.service.handler;\n\nimport com.xxl.job.core.context.XxlJobHelper;\nimport com.xxl.job.core.handler.IJobHandler;\nimport com.yhs.springboot.redis.core.RedisClientService;\nimport javax.annotation.Resource;\n\npublic class DemoGlueJobHandler extends IJobHandler {\n  \n  	@Resource\n    private RedisClientService redisClientService;\n  	\n\n	@Override\n	public void execute() throws Exception {\n        \n		XxlJobHelper.log(\"XXL-JOB, Hello World.\");\n        String lock = redisClientService.get(\"lock\");\n        XxlJobHelper.log(\"LOCK IS -> {}\",lock);\n        if(lock == null || lock == \'\'){\n        	XxlJobHelper.log(\"LOCK IS Null -> {}\",lock);\n            throw new RuntimeException(\"Lock is null\");\n        }\n        XxlJobHelper.log(\"LOCK IS NOT NULL -> {}\",lock);\n	}\n\n}\n',
        'import', '2022-04-28 14:49:17', '2022-04-28 14:49:17');

/*Table structure for table `xxl_job_user` */

DROP TABLE IF EXISTS `xxl_job_user`;

CREATE TABLE `xxl_job_user`
(
    `id`         int                                                          NOT NULL AUTO_INCREMENT,
    `username`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
    `password`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
    `role`       tinyint                                                      NOT NULL COMMENT '角色：0-普通用户、1-管理员',
    `permission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*Data for the table `xxl_job_user` */

insert into `xxl_job_user`(`id`, `username`, `password`, `role`, `permission`)
values (1, 'admin', '1c9788565b556da8e2bdba65eca856bf', 1, NULL),
       (2, 'yhgservice', '03bd3600d385a9966155f73ec759785f', 1, ''),
       (3, 'sale', '03bd3600d385a9966155f73ec759785f', 1, '');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
