-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: localhost    Database: school_question_data
-- ------------------------------------------------------
-- Server version	8.0.39

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

--
-- Table structure for table `chapter`
--

use `school_question_data`

DROP TABLE IF EXISTS `chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chapter` (
  `chapter_id` char(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `chapter_title` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin,
  `chapter_name` text CHARACTER SET utf8mb3 COLLATE utf8mb3_bin,
  `course_id` char(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  PRIMARY KEY (`chapter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `course_id` char(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `course_name` text CHARACTER SET utf8mb3,
  `lesson_id` char(32) COLLATE utf8mb3_bin DEFAULT NULL COMMENT '教师端的新课程id',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `early_morning_report`
--

DROP TABLE IF EXISTS `early_morning_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `early_morning_report` (
  `on_day` date NOT NULL COMMENT '日期',
  `data` json DEFAULT NULL,
  PRIMARY KEY (`on_day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COMMENT='早报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `question_and_answer`
--

DROP TABLE IF EXISTS `question_and_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_and_answer` (
  `question_id` char(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `question` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `answers` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `start_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `subsection`
--

DROP TABLE IF EXISTS `subsection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subsection` (
  `subsection_id` char(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `subsection_name` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `course_id` char(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  `chapter_id` char(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin DEFAULT NULL,
  PRIMARY KEY (`subsection_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `undo_log`
--

DROP TABLE IF EXISTS `undo_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `undo_log` (
  `branch_id` bigint NOT NULL COMMENT '分支事务id',
  `xid` varchar(128) NOT NULL COMMENT '全局事务id',
  `context` varchar(128) NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int NOT NULL COMMENT '0:normal status,1:defense status',
  `log_create` datetime(6) NOT NULL COMMENT 'create datetime',
  `log_modified` datetime(6) NOT NULL COMMENT 'modify datetime',
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='at transaction mode undo table';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `username` varchar(10) CHARACTER SET utf8mb3 DEFAULT NULL,
  `user_password` varchar(100) CHARACTER SET utf8mb3 DEFAULT NULL,
  `is_test` int DEFAULT '0' COMMENT '是否是测试账号',
  `class_name` text COLLATE utf8mb3_bin,
  `class_id` char(32) COLLATE utf8mb3_bin DEFAULT NULL,
  `teacher_name` text COLLATE utf8mb3_bin,
  `teacher_id` char(32) COLLATE utf8mb3_bin DEFAULT NULL,
  `level_id` char(32) COLLATE utf8mb3_bin DEFAULT NULL,
  `level_name` text COLLATE utf8mb3_bin,
  `school_id` char(32) COLLATE utf8mb3_bin DEFAULT NULL,
  `school_name` text COLLATE utf8mb3_bin,
  `identity` int DEFAULT '0' COMMENT '身份：0学生，1教师，2管理',
  `token` json DEFAULT NULL,
  `token_create_time` datetime DEFAULT NULL,
  `qq_account` varchar(20) COLLATE utf8mb3_bin DEFAULT NULL COMMENT 'qq账号',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_pk` (`qq_account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_bin COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

insert into user (user_id,username,user_password) values ('AdminIsSmileShark','管理者','simple_password');

-- 授予用户远程访问权限
GRANT ALL PRIVILEGES ON *.* TO 'sharktool'@'%' IDENTIFIED BY 'sharktool';
FLUSH PRIVILEGES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-05 16:25:23
