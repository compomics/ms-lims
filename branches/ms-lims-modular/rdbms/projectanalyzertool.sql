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
-- Table structure for table `projectanalyzertool`
--

DROP TABLE IF EXISTS `projectanalyzertool`;
CREATE TABLE `projectanalyzertool` (
  `projectanalyzertoolid` int(10) unsigned NOT NULL auto_increment,
  `toolname` varchar(100) NOT NULL default '',
  `description` text,
  `toolclassname` varchar(250) NOT NULL default '',
  `toolparameters` text,
  `username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`projectanalyzertoolid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `projectanalyzertool`
--


/*!40000 ALTER TABLE `projectanalyzertool` DISABLE KEYS */;
LOCK TABLES `projectanalyzertool` WRITE;
INSERT INTO `projectanalyzertool` VALUES (4,'Binary file retriever tool','This tool allows the retrieval of binary files associated with the project.','com.compomics.mslims.gui.projectanalyzertools.BinaryFileRetrieverTool',NULL,'CONVERSION_FROM_MS_LIMS_4','2005-03-21 15:08:07','2006-01-04 20:04:28'),(3,'Query tool','This tool allows predefined queries to be excuted against the project.','com.compomics.mslims.gui.projectanalyzertools.ProjectSQLTool',NULL,'CONVERSION_FROM_MS_LIMS_4','2005-03-07 20:14:36','2006-01-04 20:04:28'),(5,'Descriptive numbers tool','This tool generates some descriptive and informative numbers about the project\n\nAdditional details are provided for:\n	- N-terminal COFRADIC projects,\n	- Met oxidation COFRADIC projects and\n	- Cys COFRADIC projects','com.compomics.mslims.gui.projectanalyzertools.DescriptiveNumbersTool',NULL,'CONVERSION_FROM_MS_LIMS_4','2005-03-23 11:20:08','2006-01-04 20:04:28'),(6,'Identification filter','This tool allows you to filter your identifications','com.compomics.mslims.gui.projectanalyzertools.IdentificationFilter',NULL,'niklaas@%','2009-02-25 16:22:03','2009-02-25 17:22:03');
UNLOCK TABLES;
/*!40000 ALTER TABLE `projectanalyzertool` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

