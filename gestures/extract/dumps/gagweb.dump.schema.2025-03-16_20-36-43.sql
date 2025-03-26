/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.10-MariaDB, for Linux (x86_64)
--
-- Host: localhost    Database: gagweb
-- ------------------------------------------------------
-- Server version	10.11.10-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `DataLine`
--

DROP TABLE IF EXISTS `DataLine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DataLine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `hand` int(11) DEFAULT NULL,
  `position` int(11) DEFAULT NULL,
  `timestamp` datetime(6) NOT NULL,
  `gesture_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsnkxnlcmlnty9v9kubxijifvg` (`gesture_id`),
  CONSTRAINT `FKsnkxnlcmlnty9v9kubxijifvg` FOREIGN KEY (`gesture_id`) REFERENCES `Gesture` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6322 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FingerDataLine`
--

DROP TABLE IF EXISTS `FingerDataLine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FingerDataLine` (
  `accX` smallint(6) NOT NULL,
  `accY` smallint(6) NOT NULL,
  `accZ` smallint(6) NOT NULL,
  `quatA` float NOT NULL,
  `quatX` float NOT NULL,
  `quatY` float NOT NULL,
  `quatZ` float NOT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK5hmeuwbdujk12vlnbff0y7slp` FOREIGN KEY (`id`) REFERENCES `DataLine` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Gesture`
--

DROP TABLE IF EXISTS `Gesture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Gesture` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dateCreated` datetime(6) NOT NULL,
  `delay` float NOT NULL DEFAULT 1,
  `exec` text DEFAULT NULL,
  `isActive` tinyint(1) DEFAULT 0,
  `isFiltered` tinyint(1) DEFAULT 0,
  `shouldMatch` float NOT NULL DEFAULT 0.2,
  `userAlias` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlj9u2k0b5vn4tdb7g9u1aixmt` (`user_id`),
  CONSTRAINT `FKlj9u2k0b5vn4tdb7g9u1aixmt` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `HandDevice`
--

DROP TABLE IF EXISTS `HandDevice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HandDevice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deviceId` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_llplmvgj3spoksah32m8mgyed` (`deviceId`),
  KEY `FK8bp4ofcc2wpmdlwgw6sd8h1h3` (`user_id`),
  CONSTRAINT `FK8bp4ofcc2wpmdlwgw6sd8h1h3` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SensorOffset`
--

DROP TABLE IF EXISTS `SensorOffset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SensorOffset` (
  `DTYPE` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sensorType` int(11) DEFAULT NULL,
  `x` smallint(6) NOT NULL,
  `y` smallint(6) NOT NULL,
  `z` smallint(6) NOT NULL,
  `position` int(11) DEFAULT NULL,
  `offsets` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKede13pgc4jcpm16aqf5fqkutk` (`offsets`),
  CONSTRAINT `FKede13pgc4jcpm16aqf5fqkutk` FOREIGN KEY (`offsets`) REFERENCES `HandDevice` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `User` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role` int(11) NOT NULL,
  `thirdPartyId` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mavn2l30e90fhc6pqe3kucub0` (`thirdPartyId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WristDataLine`
--

DROP TABLE IF EXISTS `WristDataLine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WristDataLine` (
  `magX` smallint(6) NOT NULL,
  `magY` smallint(6) NOT NULL,
  `magZ` smallint(6) NOT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FKlk9fhu1pskjgdstlslrbgblv1` FOREIGN KEY (`id`) REFERENCES `FingerDataLine` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-16 20:36:43
