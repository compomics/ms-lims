-- MySQL dump 10.10
--
-- Host: localhost    Database: projects
-- ------------------------------------------------------
-- Server version	4.1.18-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `protocol`
--

DROP TABLE IF EXISTS `protocol`;
CREATE TABLE `protocol` (
  `protocolid` int(10) unsigned NOT NULL auto_increment,
  `type` varchar(50) NOT NULL default '',
  `description` text,
  `username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`protocolid`),
  UNIQUE KEY `type` (`type`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `protocol`
--


/*!40000 ALTER TABLE `protocol` DISABLE KEYS */;
LOCK TABLES `protocol` WRITE;
INSERT INTO `protocol` VALUES (1,'MetOx','Methionine oxidation COFRADIC','CONVERSION_FROM_MS_LIMS_4','2006-01-04 20:04:16','2006-01-04 20:04:16'),(2,'Cys','Cysteine COFRADIC','CONVERSION_FROM_MS_LIMS_4','2006-01-04 20:04:16','2006-01-04 20:04:16'),(3,'Nterm','N-terminal COFRADIC','CONVERSION_FROM_MS_LIMS_4','2006-01-04 20:04:16','2006-01-04 20:04:16'),(4,'Phospho','Phospho-COFRADIC','CONVERSION_FROM_MS_LIMS_4','2006-01-04 20:04:16','2006-01-04 20:04:16'),(5,'none','No COFRADIC(TM) chemistry used','CONVERSION_FROM_MS_LIMS_4','2006-01-04 20:04:16','2006-01-04 20:04:16'),(6,'Nitro-Tyr','Nitro-tyrosine COFRADIC','CONVERSION_FROM_MS_LIMS_4','2006-01-04 20:04:16','2006-01-04 20:04:16'),(7,'FSBA','FSBA COFRADIC','CONVERSION_FROM_MS_LIMS_4','2006-01-04 20:04:16','2006-01-04 20:04:16'),(8,'N-glyco','Asn glycosylation COFRADIC','CONVERSION_FROM_MS_LIMS_4','2006-01-04 20:04:16','2006-01-04 20:04:16');
UNLOCK TABLES;
/*!40000 ALTER TABLE `protocol` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

