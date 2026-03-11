-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: simccs_db
-- ------------------------------------------------------
-- Server version	8.0.41

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
-- Current Database: `simccs_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `simccs_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `simccs_db`;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) DEFAULT NULL,
  `details` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,'APPROVE_USER','Approved user admin@simccs.com','2026-02-08 11:57:39.852798','ADMIN'),(2,'APPROVE_USER','Approved user kambalengununudaniel@gmail.com','2026-02-08 12:01:55.480958','ADMIN'),(3,'BAN_USER','Banned user admin@simccs.com','2026-02-08 12:13:48.935680','ADMIN'),(4,'APPROVE_USER','Approved user iradsham@gmail.com','2026-02-16 10:21:08.184900','ADMIN'),(5,'APPROVE_USER','Approved user radukmisha34@gmail.com','2026-02-16 13:35:15.611589','ADMIN'),(6,'APPROVE_USER','Approved user dartluise@gmail.com','2026-02-17 10:33:05.947187','ADMIN'),(7,'BAN_USER','Banned user dartluise@gmail.com','2026-02-17 10:33:12.888863','ADMIN'),(8,'APPROVE_USER','Approved user dartluise@gmail.com','2026-02-17 10:33:18.055309','ADMIN'),(9,'APPROVE_USER','Approved user gretklein@gmail.com','2026-02-17 10:35:14.461583','ADMIN'),(10,'BAN_USER','Banned user dartluise@gmail.com','2026-02-24 13:21:10.077517','ADMIN'),(11,'APPROVE_USER','Approved user shiminanai38@gmail.com','2026-02-28 13:20:47.847800','ADMIN');
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (3,'chill'),(2,'death'),(1,'violance');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `channels`
--

DROP TABLE IF EXISTS `channels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `channels` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `report_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlw4k451ty7vjo4s9j5gh20imm` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `channels`
--

LOCK TABLES `channels` WRITE;
/*!40000 ALTER TABLE `channels` DISABLE KEYS */;
INSERT INTO `channels` VALUES (1,'2026-02-26 23:33:05.480824','report-5','REPORT','Discussion for: Participate in Discussion Thread They can globally connect to and send messages in the dedicated Chat/Discussion Thread attached to a specific report.',5),(2,'2026-02-27 08:07:12.944822','report-6','REPORT','Discussion for: message',6),(3,'2026-02-27 08:24:16.216698','report-1','REPORT','Discussion for: tshisekedi was killed',1),(4,'2026-02-27 08:24:20.930832','report-2','REPORT','Discussion for: Ecole du ciquantenaire burned',2),(5,'2026-02-27 08:24:25.382715','report-3','REPORT','Discussion for: second',3),(6,'2026-02-27 08:24:31.885263','report-4','REPORT','Discussion for: Today is the second day of us testing the application',4),(7,'2026-02-28 13:31:10.492840','report-7','REPORT','Discussion for: gemini',7),(8,'2026-03-03 11:09:45.461511','report-8','REPORT','Discussion for: Sanctions against rwanda',8);
/*!40000 ALTER TABLE `channels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_publication`
--

DROP TABLE IF EXISTS `event_publication`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_publication` (
  `id` binary(16) NOT NULL,
  `completion_date` datetime(6) DEFAULT NULL,
  `event_type` varchar(255) DEFAULT NULL,
  `listener_id` varchar(255) DEFAULT NULL,
  `publication_date` datetime(6) DEFAULT NULL,
  `serialized_event` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_publication`
--

LOCK TABLES `event_publication` WRITE;
/*!40000 ALTER TABLE `event_publication` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_publication` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `media_attachments`
--

DROP TABLE IF EXISTS `media_attachments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media_attachments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_path` varchar(255) NOT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `is_encrypted` bit(1) DEFAULT NULL,
  `report_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkmv7jyxge0ff9r5ompaq71jtv` (`report_id`),
  CONSTRAINT `FKkmv7jyxge0ff9r5ompaq71jtv` FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `media_attachments`
--

LOCK TABLES `media_attachments` WRITE;
/*!40000 ALTER TABLE `media_attachments` DISABLE KEYS */;
INSERT INTO `media_attachments` VALUES (1,'c2c40d37-3cad-452d-a57b-7dd8368fa743_mic(2).jpg','unknown',_binary '\0',8),(2,'17fca4e7-bb0e-419d-bc0d-d897a0614a3b_mic(2).jpg','unknown',_binary '\0',1),(3,'6264fc9f-5745-497d-9d8e-2e712c8f9167_mic(2).jpg','unknown',_binary '\0',8),(4,'0195a1e5-a6ea-40cb-94b8-859ac14f1124_GTRW1212.JPG','unknown',_binary '\0',8),(5,'8f106943-25bf-49e6-b79b-5f1ac197f32b_Space 1.JPG','unknown',_binary '\0',2);
/*!40000 ALTER TABLE `media_attachments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content_encrypted` text NOT NULL,
  `sent_at` datetime(6) DEFAULT NULL,
  `channel_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3u3ckbhwq9se1cmopk2pq05b2` (`channel_id`),
  KEY `FK4ui4nnwntodh6wjvck53dbk9m` (`sender_id`),
  CONSTRAINT `FK3u3ckbhwq9se1cmopk2pq05b2` FOREIGN KEY (`channel_id`) REFERENCES `channels` (`id`),
  CONSTRAINT `FK4ui4nnwntodh6wjvck53dbk9m` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (7,'JRmCVghtYyDsNbmBOJrx7uC4cchx4GjlEwv4qFRCxwg=','2026-02-27 10:12:24.247855',3,3),(8,'DnGt9lCzphsBob4zB5NV6i1aAqUdypsBXiXgxEv+aDDhkkcidctnrTA1cCbpH5D5CRTfIx/sa/7oZBoJYRWgxOHBuOs6WYIdZSBehj1Q/9M=','2026-02-27 13:25:24.077877',5,2),(13,'fGpqbq7icAI7+Ow5z1o/NA==','2026-02-27 13:28:59.392809',1,3),(21,'RNAkI+pVdawUqMs4wt6LwQ==','2026-02-27 14:08:50.266331',1,3),(22,'a/ZNG/rClhh67jDRc6fIUqmqBgaewKBjvzrdrxEMTUE=','2026-02-27 14:09:02.875506',1,3),(23,'EMzkqI39mpCQBKAuYRS0gQ==','2026-02-27 14:09:51.044756',3,7),(24,'TQu84mfAbnKwOdLJygUUiw==','2026-02-27 14:19:54.559607',3,7),(25,'u+JuDnWah7QQuRDKsoK1hQ==','2026-02-27 14:20:01.109192',3,7),(26,'cB5p/IVz8XOfNPbEHwCjWg==','2026-02-28 13:05:48.461698',4,3),(27,'l+EPDUumG/iCN9RGn0KgFw==','2026-02-28 13:21:55.691789',3,8),(28,'pMCsi/Ie/LlX0qW5iHoQ0zi5HoE6/Pme2ORLxzh+Z3wBAT8Rbkv5DjuncLFI0a7zGmPiEprqraKpWXTpWFEmk2vDeuBCzRqAdJvIdjlUx0lFkTQYRpHcCl3dQgvzWk46UXwM9KICnZtt2d9sSCeQ7g==','2026-03-03 11:11:03.923601',8,2),(29,'uIcWSMXzxXeqvfiX+5s79msd9yqvZJzK7MdUcvnFwvarZzkw8aPKtFbxau467q4kY+Jtz8x+ntoOea0jdPpYJ6jxVnH+AQSk2zIbmcpniCUEkR6nlnMzBIG6WspxBugSwmFlDkC4tPeZEIOGhV0a64Vdq1pjXojuuOtZoA3+gr/eE6IvJ03IvKqepAXqD2UAv/Klq2g8jMDWlBu1MIA1Xp8ofjhGg8nBodrJsSp84EuYnn9wFVe4JwU35PtTYeOoQZirQIFIfCE6HIK8QErXyA==','2026-03-03 11:16:57.950427',1,2),(30,'4MDIJ81KC0rQ/e+71AdjwbtFQ5WQ/DweaTdsQqA/eh+NWiA+AoSRcRzAwJd51htrNys3ZDAo4/rO1R8vVzX4jQ==','2026-03-03 17:03:06.629550',3,7);
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `misinformation_flags`
--

DROP TABLE IF EXISTS `misinformation_flags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `misinformation_flags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ai_confidence_score` double DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `flagged_by_user_id` bigint DEFAULT NULL,
  `report_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK72n3r47txbmptgq9rwxrxwlsh` (`flagged_by_user_id`),
  KEY `FKnwb4g8wqdfoulm8v4spkbblh2` (`report_id`),
  CONSTRAINT `FK72n3r47txbmptgq9rwxrxwlsh` FOREIGN KEY (`flagged_by_user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKnwb4g8wqdfoulm8v4spkbblh2` FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `misinformation_flags`
--

LOCK TABLES `misinformation_flags` WRITE;
/*!40000 ALTER TABLE `misinformation_flags` DISABLE KEYS */;
INSERT INTO `misinformation_flags` VALUES (1,1,'2026-02-17 10:52:31.559722','Manually flagged by Editor/Admin',NULL,3),(2,1,'2026-02-24 12:02:34.344292','Manually flagged by Editor/Admin',NULL,4),(3,1,'2026-02-26 15:57:30.432855','Manually flagged by Editor/Admin',NULL,1),(4,1,'2026-02-26 22:14:16.261476','Manually flagged by Editor/Admin',NULL,3);
/*!40000 ALTER TABLE `misinformation_flags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg0guo4k8krgpwuagos61oc06j` (`token`),
  UNIQUE KEY `UKf90ivichjaokvmovxpnlm5nin` (`user_id`),
  CONSTRAINT `FK83nsrttkwkb6ym0anu051mtxn` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refreshtoken`
--

DROP TABLE IF EXISTS `refreshtoken`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refreshtoken` (
  `id` bigint NOT NULL,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKor156wbneyk8noo4jstv55ii3` (`token`),
  UNIQUE KEY `UK81otwtvdhcw7y3ipoijtlb1g3` (`user_id`),
  CONSTRAINT `FKa652xrdji49m4isx38pp4p80p` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refreshtoken`
--

LOCK TABLES `refreshtoken` WRITE;
/*!40000 ALTER TABLE `refreshtoken` DISABLE KEYS */;
INSERT INTO `refreshtoken` VALUES (1,'2026-02-09 11:56:55.569442','248304c4-2611-4c40-920f-4d8dd4696215',1),(2,'2026-03-04 18:14:15.531916','f0da71ba-1e36-40a8-a600-cdb83b453bee',3),(3,'2026-03-04 14:29:14.379171','054dc5ec-5bf6-46d2-8d09-28d09919574c',2),(102,'2026-02-25 11:56:44.698087','ef88f0c2-69b6-44eb-a894-d93ea4892583',4),(152,'2026-02-17 13:36:31.387403','56eb16ea-9684-4d78-a3f8-2074dd0ef962',5),(202,'2026-03-04 15:13:31.640245','e9f5a629-0137-48f5-b790-272a70de2428',7),(252,'2026-03-01 13:21:36.901564','e29aebed-fbeb-495e-b26b-19f89f228201',8);
/*!40000 ALTER TABLE `refreshtoken` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refreshtoken_seq`
--

DROP TABLE IF EXISTS `refreshtoken_seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refreshtoken_seq` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refreshtoken_seq`
--

LOCK TABLES `refreshtoken_seq` WRITE;
/*!40000 ALTER TABLE `refreshtoken_seq` DISABLE KEYS */;
INSERT INTO `refreshtoken_seq` VALUES (351);
/*!40000 ALTER TABLE `refreshtoken_seq` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_categories`
--

DROP TABLE IF EXISTS `report_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_categories` (
  `report_id` bigint NOT NULL,
  `category_id` int NOT NULL,
  PRIMARY KEY (`report_id`,`category_id`),
  KEY `FKa6v6afv41bxdb3ldqe4glri2o` (`category_id`),
  CONSTRAINT `FKa6v6afv41bxdb3ldqe4glri2o` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`),
  CONSTRAINT `FKpk69di6porvnf02oeu6dx6tb5` FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_categories`
--

LOCK TABLES `report_categories` WRITE;
/*!40000 ALTER TABLE `report_categories` DISABLE KEYS */;
INSERT INTO `report_categories` VALUES (1,1),(2,1),(3,1),(2,2),(3,2),(4,3);
/*!40000 ALTER TABLE `report_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_versions`
--

DROP TABLE IF EXISTS `report_versions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_versions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `change_reason` varchar(255) DEFAULT NULL,
  `content_snapshot_encrypted` text,
  `created_at` datetime(6) DEFAULT NULL,
  `version_number` int DEFAULT NULL,
  `editor_id` bigint DEFAULT NULL,
  `report_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt8drmm5a57pwf6diq1vx1uxgq` (`editor_id`),
  KEY `FK5a51wkwigpxpoatdpgl57mjeq` (`report_id`),
  CONSTRAINT `FK5a51wkwigpxpoatdpgl57mjeq` FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`),
  CONSTRAINT `FKt8drmm5a57pwf6diq1vx1uxgq` FOREIGN KEY (`editor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_versions`
--

LOCK TABLES `report_versions` WRITE;
/*!40000 ALTER TABLE `report_versions` DISABLE KEYS */;
INSERT INTO `report_versions` VALUES (1,'Update request by gretklein@gmail.com','8KJJz2AAAA1gVvXiO1QRgOw6aOp9gGrTsv5Ah7HNlHm3j60jv+Wdazhp+HdFNZ50B1GwGF7E87yoVuxCB/ztCA==','2026-02-17 10:37:30.155137',1,7,1),(2,'Update request by gretklein@gmail.com','8KJJz2AAAA1gVvXiO1QRgOw6aOp9gGrTsv5Ah7HNlHm3j60jv+Wdazhp+HdFNZ50E+Na8BFe1VpxZyv2tcoDoOFPSRR74dD/aEl5j4dDXIk=','2026-02-17 11:41:59.398693',2,7,1),(3,'Update request by gretklein@gmail.com','8KJJz2AAAA1gVvXiO1QRgOw6aOp9gGrTsv5Ah7HNlHm3j60jv+Wdazhp+HdFNZ50E+Na8BFe1VpxZyv2tcoDoOFPSRR74dD/aEl5j4dDXIk=','2026-02-26 19:33:19.338264',3,7,1),(4,'Update request by gretklein@gmail.com','eXrgMiuuLeeH6sWMh1AWFchoAvf3G+dKY7aabwZr8MAxfchb8yZA+GxdMZK5ZxPiqTRr+NLH4StV2LrENEAgRGAOreK6uyDQ/e1NYBUhfPs99MGFm/Q3nEKgOzPVcH7V9tEbPLdwWdEJzzn1aPlpo8R7R1MrHeBfPiOoHJIQ6em2KpVCqtwpSje6qGpE1sl21xRv1ktQQ209/5QPjpPQbQ==','2026-02-26 19:33:48.766726',4,7,1),(5,'Update request by gretklein@gmail.com','jez4OoVLeEjDrNXWv3+8qw==','2026-02-26 21:52:04.980316',1,7,2),(6,'Update request by gretklein@gmail.com','eBQA7YT4KY8lhGTzvIIImw==','2026-02-27 08:10:27.773028',1,7,6),(7,'Update request by gretklein@gmail.com','1Ce+m5dETyxePVyNQ9Cj5eivghnCEViocyxGuKt1QlWD0fobU/DfHD3guca6u1Sb5P/7eq4fIbmN/pbsoZ3i5w==','2026-02-28 13:53:22.913359',1,7,7),(8,'Update request by gretklein@gmail.com','eBQA7YT4KY8lhGTzvIIImw==','2026-03-03 10:11:30.711908',2,7,6),(9,'Update request by gretklein@gmail.com','bXGTCT4VKrIoltizx5FEjMDkH4iFidBRRVDXvAl+7LIBTSACrQFFelXTMyNCLOEHmKMtEEQajifWF3MyB19De5DbSWUJEWVATPS8qSYN+HJIKfL8CmYnn1Du5hj2sbRn0u784MOmBg9BEjUtEMYZsxXmW6/7HysKIOPPmdsMn6q0OmkHgNoNmsBst6wu2XoADPUKOzd1KVM3yyVYDTn9E0gwNXrndli+7/S1Xo7J9fVZ9a42waHGMtsZAAcodoza','2026-03-03 10:12:46.537532',3,7,6),(10,'Update request by gretklein@gmail.com','z0wXSPNTTSImBB6yvBafynJd/iitmOMZgIgHlhYVwkyAaka8kR26Z/98NYClfPaU9sd6s234uzL9BW2J2Jue/8NxSVwmZRNkXGuimCSpnw7U6vDV9SPumLBZFikdzvHxZcSE9NG4a1LrPWFOpFDGivtYz+q49+Upb2f9CWA+hEqF79BhuBTWujOrcJ75vv4+bWlNquxIJzu0t2gp9GPxY+IU97y5p+Pk46mACysoDBRivdvWoph+h2tCG82YKW67ePO/GIOo9EudZIiDhlCIZvtun1y6uf0bRL+zDVYEWgpBmKtAgUh8ITocgrxAStfI','2026-03-03 10:36:46.805418',4,7,6),(11,'Update request by kambalengununudaniel@gmail.com','yH8QvExPDc+OTQQMn9S2riKlbSpk7Od4H4/86pnYwEaz9rGYteEG4hN1+YCQlfYsH+dogUfWls5rqRtVNIDc9UrgHKHl9UYP8PKldpShGwJbpHa3/MYNTWZyXNCZNjrr','2026-03-03 14:48:58.932777',1,2,8),(12,'Update request by kambalengununudaniel@gmail.com','eXrgMiuuLeeH6sWMh1AWFchoAvf3G+dKY7aabwZr8MAxfchb8yZA+GxdMZK5ZxPiqTRr+NLH4StV2LrENEAgRGAOreK6uyDQ/e1NYBUhfPs99MGFm/Q3nEKgOzPVcH7V9tEbPLdwWdEJzzn1aPlpo8R7R1MrHeBfPiOoHJIQ6em2KpVCqtwpSje6qGpE1sl21xRv1ktQQ209/5QPjpPQbQ==','2026-03-03 14:50:22.313398',5,2,1),(13,'Update request by kambalengununudaniel@gmail.com','yH8QvExPDc+OTQQMn9S2riKlbSpk7Od4H4/86pnYwEaz9rGYteEG4hN1+YCQlfYsH+dogUfWls5rqRtVNIDc9UrgHKHl9UYP8PKldpShGwJbpHa3/MYNTWZyXNCZNjrr','2026-03-03 15:11:49.074172',2,2,8),(14,'Update request by gretklein@gmail.com','yH8QvExPDc+OTQQMn9S2riKlbSpk7Od4H4/86pnYwEaz9rGYteEG4hN1+YCQlfYsH+dogUfWls5rqRtVNIDc9UrgHKHl9UYP8PKldpShGwJbpHa3/MYNTWZyXNCZNjrr','2026-03-03 15:16:51.847341',3,7,8),(17,'Update request by gretklein@gmail.com','6UKSAwUlWVtx8VikKKrJBNGvuiHPEpgQW3Nl7POEPbE0zSun6yp6m8y8ZutfgbY0OaazPJAq1U/06g+EPScsw50qcVDsEwNo5T/LyPDcjwTOSr1iKnONG7ev8LNT+MCGyCKzm0HA1ExWw9VW4XDT4kRvrIBnvxUwujUEZ3aRqqEKpyOkncirMMOQ44KCkM/B','2026-03-03 15:30:16.528278',2,7,2);
/*!40000 ALTER TABLE `report_versions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `casualty_count` int DEFAULT NULL,
  `content_encrypted` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `location_lat` double DEFAULT NULL,
  `location_lng` double DEFAULT NULL,
  `status` enum('DRAFT','PUBLISHED','REJECTED','SUBMITTED','VERIFIED') DEFAULT NULL,
  `summary` text,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `author_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb6m0c7yr0xjys3y3uwhgopmao` (`author_id`),
  CONSTRAINT `FKb6m0c7yr0xjys3y3uwhgopmao` FOREIGN KEY (`author_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
INSERT INTO `reports` VALUES (1,NULL,'eXrgMiuuLeeH6sWMh1AWFchoAvf3G+dKY7aabwZr8MAxfchb8yZA+GxdMZK5ZxPiqTRr+NLH4StV2LrENEAgRGAOreK6uyDQ/e1NYBUhfPs99MGFm/Q3nEKgOzPVcH7V9tEbPLdwWdEJzzn1aPlpo8R7R1MrHeBfPiOoHJIQ6em2KpVCqtwpSje6qGpE1sl21xRv1ktQQ209/5QPjpPQbQ==','2026-02-16 13:26:06.407499',12333,736363,'PUBLISHED','in the province of bandundu, because of the war by the m23, the president tshisekedi was killed','tshisekedi was killed','2026-02-26 19:33:48.781647',4),(2,NULL,'6UKSAwUlWVtx8VikKKrJBNGvuiHPEpgQW3Nl7POEPbE0zSun6yp6m8y8ZutfgbY0OaazPJAq1U/06g+EPScsw50qcVDsEwNo5T/LyPDcjwTOSr1iKnONG7ev8LNT+MCGyCKzm0HA1ExWw9VW4XDT4kRvrIBnvxUwujUEZ3aRqqEKpyOkncirMMOQ44KCkM/B','2026-02-16 13:37:37.166745',3,3,'VERIFIED','last week the ecole du cinquantenaire Goma, took fire, from the internat, and caused death to 100 student o the petrochemistry option','Ecole du ciquantenaire burned','2026-02-26 21:52:05.055386',5),(3,NULL,'cs03n8yGA3si5wNyXMUTyg==','2026-02-17 10:48:12.981628',45,672,'REJECTED','sencond','second','2026-02-17 10:53:57.465237',2),(4,23,'WiSwvkRRe4DO8IC1TKHZMG2yx4YqHDBlwiA4CARjr5luufpUDR9YjAZ3/IaDS0MdpHgQX+12972jKfWzu9jr1PBJvu1X+xp/M1evOr2IvbXiljYunnFFdtXsNLIzQ3FWRD7tb6cv6GRY0JJsqG2e7mkLuGoGTwYcc2ml6flIl5MFJyqjSoSegulLhbx8uXYV4pY2Lp5xRXbV7DSyM0NxVkQ+7W+nL+hkWNCSbKhtnu5pC7hqBk8GHHNppen5SJeTBScqo0qEnoLpS4W8fLl2FeKWNi6ecUV21ew0sjNDcVZEPu1vpy/oZFjQkmyobZ7uaQu4agZPBhxzaaXp+UiXk7BIRCNI8cAMlPaSM+dNiAI=','2026-02-24 11:55:23.569738',23,55,'VERIFIED','on february 24, we are doing the second test of our application','Today is the second day of us testing the application','2026-02-24 12:00:53.033802',2),(5,NULL,'HrBxeivv383QJcEmClrXrZDOQrFfz16/fCYkRGZ9UTaEACRsyh8rP9WvuYraH5OyOI14Q2JqzgGMXR9Wt7AY5wy9RNjjQ8pyVaj5twmnkp0qOFh4E72Q6tie/3ebwXuV1wEdeUykj7DjYvWIWNCixSAXgNksaVXewdUSh6BZ0qWklrm1i92plEKjiNIT5WhCA/mEDUUBkeRL/nAFioIO4w==','2026-02-26 23:33:05.396480',4,6,'DRAFT','Participate in Discussion Thread\nThey can globally connect to and send messages in the dedicated Chat/Discussion Thread attached to a specific report.','Participate in Discussion Thread They can globally connect to and send messages in the dedicated Chat/Discussion Thread attached to a specific report.','2026-02-26 23:38:11.679440',2),(6,NULL,'dkCoRF5I00PDbTEugO9Kc9FsQgk5jTpVlftNCip/3niCds0FqabgfJ00v6EUxsqvoNGwQfV0X//xPwquDfqLqhWrvJqcIAS28ZgIkaVgaTVmVbQt71Rbib2Z0jiNP9RLnmzjSrGLq6fRrHf/g33wnHsHXul+hFhXR3jHG8+mk8ykIDM5y6ovCWqZpVQjEmeQOf/uLNitb68wWCeP6SG0VVCoCPpgN1GL+Cjm+4BtHf9LgQd3ohoTncu1cMnIBzYhoOFiruJ23bpcFiAIaqC3o5OB/T5p+7qjkkxM7B3gRo91nivyqqBqrg40QNjIcmSrfRY3sKV6l/p1mGQSANEgWUM9SMGIeEdTqxuf50NoTPetETLIKnQenp2tDuOSlbanCZ2mUrwjNa/PS3o3msQEvxI1XgFfr4FYK8DFiCR7RCJr0eIJf+8dj2RT31qurfVdlAPSnjmPYfqb8i7OTa0R+A==','2026-02-27 08:07:12.762803',2,3,'DRAFT','the Us GOVERNMENT sanctioned the RDF army for collaborating with the m23','Sanction to the RDF army from the US','2026-03-03 18:12:23.273930',2),(7,NULL,'1Ce+m5dETyxePVyNQ9Cj5eivghnCEViocyxGuKt1QlWD0fobU/DfHD3guca6u1Sb5P/7eq4fIbmN/pbsoZ3i5w==','2026-02-28 13:31:10.470110',2,2,'DRAFT','death in nyagatare','gemini','2026-03-03 17:43:59.212099',8),(8,NULL,'yH8QvExPDc+OTQQMn9S2riKlbSpk7Od4H4/86pnYwEaz9rGYteEG4hN1+YCQlfYsH+dogUfWls5rqRtVNIDc9UrgHKHl9UYP8PKldpShGwJbpHa3/MYNTWZyXNCZNjrr','2026-03-03 11:09:45.306112',34,4,'DRAFT','the rwanda millitary rdf, got sanctions for participating and helping the m23 rebels in congo','Sanctions against rwanda','2026-03-03 11:14:56.752386',2);
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` enum('ROLE_ADMIN','ROLE_EDITOR','ROLE_JOURNALIST') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_JOURNALIST'),(2,'ROLE_EDITOR'),(3,'ROLE_ADMIN');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_backups`
--

DROP TABLE IF EXISTS `system_backups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_backups` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `file_size_mb` double DEFAULT NULL,
  `filename` varchar(255) NOT NULL,
  `log_message` text,
  `status` enum('FAILED','IN_PROGRESS','SUCCESS') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_backups`
--

LOCK TABLES `system_backups` WRITE;
/*!40000 ALTER TABLE `system_backups` DISABLE KEYS */;
INSERT INTO `system_backups` VALUES (1,'2026-01-24 08:13:17.980467',NULL,'backup_AUTO_20260124_081317.sql','Cannot run program \"pg_dump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(2,'2026-01-25 13:21:26.477844',NULL,'backup_AUTO_20260125_132126.sql','Cannot run program \"pg_dump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(3,'2026-02-03 09:57:53.504638',NULL,'backup_AUTO_20260203_115753.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(4,'2026-02-09 09:20:17.414802',NULL,'backup_AUTO_20260209_112017.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(5,'2026-02-10 11:17:31.089304',NULL,'backup_AUTO_20260210_131731.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(6,'2026-02-15 08:22:37.862689',NULL,'backup_AUTO_20260215_102237.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(7,'2026-02-16 08:53:15.404215',NULL,'backup_AUTO_20260216_105315.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(8,'2026-02-16 13:22:40.243434',0,'backup_MANUAL_20260216_152240.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(9,'2026-02-16 13:22:50.021800',0,'backup_MANUAL_20260216_152250.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(10,'2026-02-17 09:24:29.347057',0,'backup_AUTO_20260217_112429.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(11,'2026-02-24 15:20:45.414996',0,'backup_MANUAL_20260224_172045.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(12,'2026-02-24 15:21:26.389343',0,'backup_MANUAL_20260224_172126.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(13,'2026-02-27 00:00:00.051924',0,'backup_AUTO_20260227_020000.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(14,'2026-02-28 16:30:25.256220',0,'backup_MANUAL_20260228_183025.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(15,'2026-02-28 16:30:32.753583',0,'backup_MANUAL_20260228_183032.sql','Cannot run program \"mysqldump\": CreateProcess error=2, The system cannot find the file specified','FAILED'),(16,'2026-02-28 16:50:06.099293',0.02935791015625,'backup_MANUAL_20260228_185006.sql','Backup completed successfully.','SUCCESS'),(17,'2026-02-28 16:50:07.500681',0.029481887817382812,'backup_MANUAL_20260228_185007.sql','Backup completed successfully.','SUCCESS'),(18,'2026-02-28 17:01:32.294355',0,'backup_MANUAL_20260228_190132.sql',NULL,'IN_PROGRESS'),(19,'2026-02-28 17:04:53.182043',0.02969646453857422,'backup_MANUAL_20260228_190453.sql','Backup completed successfully.','SUCCESS'),(20,'2026-03-03 18:14:19.473867',0,'backup_MANUAL_20260303_201419.sql',NULL,'IN_PROGRESS');
/*!40000 ALTER TABLE `system_backups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (2,1),(4,1),(5,1),(6,1),(8,1),(7,2),(1,3),(3,3);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(50) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `is_enabled` bit(1) DEFAULT NULL,
  `mfa_enabled` bit(1) DEFAULT NULL,
  `mfa_secret` varchar(255) DEFAULT NULL,
  `password_hash` varchar(120) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-02-04 22:37:39.112341','admin@simccs.com','Super Admin',_binary '\0',_binary '\0',NULL,'$2a$10$Cp3TWPupiNw0ru13tsoI8.O1mtDzXT77yrbVw4c6hKp7FTJ1Q8oNG'),(2,'2026-02-08 11:53:29.162980','kambalengununudaniel@gmail.com','123456',_binary '',_binary '','LGRZ5WGZX4E7RVMTYH2OR3EIMOSYENS6','$2a$10$yBMyeuK3GX16Rs16LKOONOy4i0HKZoXqje/2yMXzFZAeiYoGuJfbi'),(3,'2026-02-08 12:09:30.106214','danielngun04@gmail.com','Super Admin',_binary '',_binary '','KSQMQJ6CUEMS7TDBULQY3GJ45IBZNTFU','$2a$10$iHjHCoUOX6KDsSrqg2bvlemu.DldZxT9nfQqh44VpMlGDK24.jKTa'),(4,'2026-02-16 09:56:53.369966','iradsham@gmail.com','irad',_binary '',_binary '\0',NULL,'$2a$10$wPF/SdI4xbNHX6mk19tcPuMjPEHg5jjS.AsrO9hyhVv8BuYuYMqW2'),(5,'2026-02-16 13:34:40.533349','radukmisha34@gmail.com','Rachel Ngununu',_binary '',_binary '\0',NULL,'$2a$10$Jn2TupvHfJJ4BKLqwcf9b.AK.Dbq4Ngsq52rVoXasu4OCNgf.RzN.'),(6,'2026-02-17 10:16:38.566499','dartluise@gmail.com','kercy',_binary '\0',_binary '\0',NULL,'$2a$10$zXfM4U9Qlw.4X8jkKen7ke34i9lgq/dcD3zmff9jgeIkCgwXvvSqS'),(7,'2026-02-17 10:34:33.168672','gretklein@gmail.com','greit',_binary '',_binary '\0',NULL,'$2a$10$dI8ug37yNstJMWsnENOsY.hAHKKuOykX7xaWGNtjXQKqBmc/WGa3m'),(8,'2026-02-28 13:18:57.588119','shiminanai38@gmail.com','shimi ',_binary '',_binary '\0',NULL,'$2a$10$FYjhUkRSlttBz3KfEcafYep6DIfPBCLkNyac1ypq7woHY04viTLFe');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workflow_actions`
--

DROP TABLE IF EXISTS `workflow_actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workflow_actions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action_type` enum('APPROVE','FLAG_MISINFORMATION','REJECT','REQUEST_REVISION') DEFAULT NULL,
  `comment` text,
  `timestamp` datetime(6) DEFAULT NULL,
  `actor_id` bigint NOT NULL,
  `report_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfvuahgbih907ellyscvq7i3jo` (`actor_id`),
  KEY `FK9u3nqr13467avap7q25mgl5o5` (`report_id`),
  CONSTRAINT `FK9u3nqr13467avap7q25mgl5o5` FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`),
  CONSTRAINT `FKfvuahgbih907ellyscvq7i3jo` FOREIGN KEY (`actor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workflow_actions`
--

LOCK TABLES `workflow_actions` WRITE;
/*!40000 ALTER TABLE `workflow_actions` DISABLE KEYS */;
INSERT INTO `workflow_actions` VALUES (1,'REQUEST_REVISION','please make sure that u check the number correctly. take also a picture confirming your data','2026-02-16 13:31:44.735983',3,1),(2,'FLAG_MISINFORMATION','this is not true','2026-02-16 13:33:13.796688',3,1),(3,'APPROVE',NULL,'2026-02-16 16:21:15.374515',3,1),(4,'APPROVE',NULL,'2026-02-17 10:38:16.166637',7,1),(5,'APPROVE',NULL,'2026-02-17 10:39:28.224382',7,2),(6,'FLAG_MISINFORMATION','review','2026-02-17 10:51:22.415642',7,3),(7,'REJECT',NULL,'2026-02-17 10:53:57.462246',7,3),(8,'APPROVE','this is great','2026-02-24 12:00:52.999551',7,4),(9,'REQUEST_REVISION','what do you men by that?\n','2026-02-26 23:38:11.665175',7,5),(10,'REQUEST_REVISION','clarify well about when was the communique from the us bureau','2026-03-03 11:14:56.739374',7,8),(11,'REQUEST_REVISION','actually you submitted a report with no content','2026-03-03 17:43:58.766353',7,7),(12,'REQUEST_REVISION','yoo clarify, give the source of your information','2026-03-03 18:12:23.169173',7,6);
/*!40000 ALTER TABLE `workflow_actions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-03 20:14:20
