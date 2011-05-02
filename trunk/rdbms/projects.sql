-- MySQL dump 10.13  Distrib 5.1.37, for debian-linux-gnu (i486)
--
-- Host: localhost    Database: projects_2apr
-- ------------------------------------------------------
-- Server version	5.1.37-1ubuntu5.1-log

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
-- Table structure for table `binfile`
--

DROP TABLE IF EXISTS `binfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `binfile` (
  `binfileid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_projectid` int(10) unsigned NOT NULL DEFAULT '0',
  `l_filedescriptionid` int(10) unsigned NOT NULL DEFAULT '0',
  `file` longblob NOT NULL,
  `filename` varchar(250) NOT NULL DEFAULT '',
  `comments` text,
  `originalpath` varchar(250) NOT NULL DEFAULT '',
  `originalhost` varchar(50) NOT NULL DEFAULT '',
  `originaluser` varchar(50) NOT NULL DEFAULT '',
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`binfileid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `datfile`
--

DROP TABLE IF EXISTS `datfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datfile` (
  `datfileid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filename` varchar(250) NOT NULL DEFAULT '',
  `file` longblob NOT NULL,
  `server` varchar(250) NOT NULL DEFAULT '',
  `folder` varchar(250) NOT NULL DEFAULT '',
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`datfileid`),
  KEY `Filename` (`filename`) USING HASH
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 MAX_ROWS=1000000 AVG_ROW_LENGTH=2658940;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `filedescriptor`
--

DROP TABLE IF EXISTS `filedescriptor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `filedescriptor` (
  `filedescriptorid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `short_label` varchar(100) NOT NULL DEFAULT '',
  `description` text,
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`filedescriptorid`),
  UNIQUE KEY `short_label` (`short_label`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fragmentation`
--

DROP TABLE IF EXISTS `fragmentation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fragmentation` (
  `fragmentationid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(45) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `creationdate` DATETIME NOT NULL,
  `modificationdate` DATETIME NOT NULL,
  PRIMARY KEY (`fragmentationid`)
) ENGINE = MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `fragmention`
--

DROP TABLE IF EXISTS `fragmention`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fragmention` (
  `fragmentionid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_identificationid` int(10) unsigned NOT NULL DEFAULT '0',
  `iontype` int(10) unsigned NOT NULL DEFAULT '0',
  `ionname` varchar(45) NOT NULL DEFAULT '',
  `l_ionscoringid` int(10) unsigned NOT NULL DEFAULT '0',
  `mz` decimal(12,4) NOT NULL DEFAULT '0.0000',
  `intensity` int(10) unsigned DEFAULT NULL,
  `fragmentionnumber` int(10) unsigned DEFAULT NULL,
  `massdelta` decimal(12,4) DEFAULT NULL,
  `masserrormargin` decimal(12,4) NOT NULL DEFAULT '0.0000',
  `username` varchar(45) NOT NULL DEFAULT '',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`fragmentionid`),
  KEY `l_identificationid index` (`l_identificationid`),
  KEY `l_ionscoringid index` (`l_ionscoringid`),
  KEY `iontype index` (`iontype`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `id_to_phospho`
--

DROP TABLE IF EXISTS `id_to_phospho`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `id_to_phospho` (
  `l_id` int(10) unsigned NOT NULL DEFAULT '0',
  `l_phosphorylationid` int(10) unsigned NOT NULL DEFAULT '0',
  `conversionid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`conversionid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identification`
--

DROP TABLE IF EXISTS `identification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identification` (
  `identificationid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_spectrumid` int(10) unsigned NOT NULL DEFAULT '0',
  `l_datfileid` int(10) unsigned NOT NULL DEFAULT '0',
  `datfile_query` int(10) unsigned DEFAULT NULL,
  `accession` varchar(50) NOT NULL DEFAULT '',
  `start` int(10) unsigned NOT NULL DEFAULT '0',
  `end` int(10) unsigned NOT NULL DEFAULT '0',
  `enzymatic` char(2) NOT NULL DEFAULT '',
  `sequence` varchar(150) NOT NULL DEFAULT '',
  `modified_sequence` text NOT NULL,
  `ion_coverage` text,
  `score` int(10) unsigned NOT NULL DEFAULT '0',
  `homology` double DEFAULT NULL,
  `exp_mass` decimal(12,4) NOT NULL DEFAULT '0.0000',
  `cal_mass` decimal(12,4) NOT NULL DEFAULT '0.0000',
  `light_isotope` decimal(12,4) DEFAULT NULL,
  `heavy_isotope` decimal(12,4) DEFAULT NULL,
  `Description` text NOT NULL,
  `identitythreshold` int(50) unsigned NOT NULL DEFAULT '0',
  `confidence` decimal(12,4) NOT NULL DEFAULT '0.0500',
  `DB` varchar(50) NOT NULL DEFAULT '',
  `title` varchar(250) NOT NULL DEFAULT '',
  `precursor` decimal(12,4) NOT NULL DEFAULT '0.0000',
  `charge` smallint(50) unsigned NOT NULL DEFAULT '0',
  `isoforms` text,
  `db_filename` varchar(100) DEFAULT NULL,
  `mascot_version` varchar(25) DEFAULT NULL,
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`identificationid`),
  KEY `title_index` (`title`),
  KEY `sequence_index` (`sequence`),
  KEY `modified_sequence_index` (`modified_sequence`(50)),
  KEY `accession_index` (`accession`),
  KEY `l_spectrumfileid` (`l_spectrumid`),
  KEY `l_datfileid` (`l_datfileid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `identification_to_quantitation`
--

DROP TABLE IF EXISTS `identification_to_quantitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `identification_to_quantitation` (
  `itqid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_identificationid` int(10) unsigned NOT NULL DEFAULT '0',
  `l_quantitation_groupid` int(10) unsigned NOT NULL DEFAULT '0',
  `type` varchar(15) NOT NULL DEFAULT '',
  `username` varchar(45) NOT NULL DEFAULT '',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`itqid`),
  KEY `identification id` (`l_identificationid`),
  KEY `type` (`type`),
  KEY `l_quantitation_groupid` (`l_quantitation_groupid`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `instrument`
--

DROP TABLE IF EXISTS `instrument`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `instrument` (
  `instrumentid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(150) DEFAULT NULL,
  `description` text,
  `storageclassname` varchar(250) DEFAULT NULL,
  `propertiesfilename` varchar(250) DEFAULT NULL,
  `differential_calibration` decimal(12,8) DEFAULT NULL,
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`instrumentid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ionscoring`
--

DROP TABLE IF EXISTS `ionscoring`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ionscoring` (
  `ionscoringid` int(10) unsigned NOT NULL DEFAULT '0',
  `description` varchar(255) NOT NULL DEFAULT '',
  `username` varchar(45) NOT NULL DEFAULT '',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`ionscoringid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `lcrun`
--

DROP TABLE IF EXISTS `lcrun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lcrun` (
  `lcrunid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_projectid` int(10) unsigned NOT NULL DEFAULT '0',
  `description` text,
  `filecount` int(11) NOT NULL DEFAULT '0',
  `name` varchar(150) NOT NULL DEFAULT '',
  `dvd_master_number` int(11) DEFAULT NULL,
  `dvd_secondary_number` int(10) unsigned DEFAULT NULL,
  `primary_fraction` int(10) unsigned DEFAULT NULL,
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`lcrunid`),
  UNIQUE KEY `name` (`name`),
  KEY `creationdateindex` (`creationdate`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `modification_conversion`
--

DROP TABLE IF EXISTS `modification_conversion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `modification_conversion` (
 `modification_conversionid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
 `modification` VARCHAR(45) NOT NULL,
 `conversion` VARCHAR(45) NOT NULL,
 `username` VARCHAR(45) NOT NULL,
 `creationdate` DATETIME NOT NULL,
 `modificationdate` DATETIME NOT NULL,
 PRIMARY KEY (`modification_conversionid`),
 UNIQUE KEY(`modification`)
) ENGINE = MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ms_lims_properties`
--
DROP TABLE IF EXISTS `ms_lims_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ms_lims_properties` (
  `ms_lims_propertiesid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `key` VARCHAR(45) NOT NULL,
  `value` VARCHAR(45) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `creationdate` DATETIME NOT NULL,
  `modificationdate` DATETIME NOT NULL,
  PRIMARY KEY (`ms_lims_propertiesid`),
  UNIQUE KEY(`key`)
) ENGINE = MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `phosphorylation`
--

DROP TABLE IF EXISTS `phosphorylation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phosphorylation` (
  `phosphorylationid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_status` int(10) unsigned NOT NULL DEFAULT '0',
  `residue` varchar(20) DEFAULT NULL,
  `location` int(10) unsigned NOT NULL DEFAULT '0',
  `accession` varchar(250) NOT NULL DEFAULT '',
  `context` varchar(100) DEFAULT NULL,
  `score` decimal(12,5) DEFAULT NULL,
  `threshold` decimal(12,5) DEFAULT NULL,
  `creationdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `description` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`phosphorylationid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `projectid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_userid` int(10) unsigned NOT NULL DEFAULT '0',
  `l_protocolid` int(10) unsigned NOT NULL DEFAULT '0',
  `title` varchar(250) NOT NULL DEFAULT '',
  `description` text,
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`projectid`),
  UNIQUE KEY `title` (`title`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `projectanalyzertool`
--

DROP TABLE IF EXISTS `projectanalyzertool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `projectanalyzertool` (
  `projectanalyzertoolid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `toolname` varchar(100) NOT NULL DEFAULT '',
  `description` text,
  `toolclassname` varchar(250) NOT NULL DEFAULT '',
  `toolparameters` text,
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`projectanalyzertoolid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `protocol`
--

DROP TABLE IF EXISTS `protocol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `protocol` (
  `protocolid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `type` varchar(50) NOT NULL DEFAULT '',
  `description` text,
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`protocolid`) USING BTREE,
  UNIQUE KEY `type` (`type`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantitation`
--

DROP TABLE IF EXISTS `quantitation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantitation` (
  `quantitationid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_quantitation_groupid` int(10) unsigned NOT NULL DEFAULT '0',
  `ratio` double NOT NULL DEFAULT '0',
  `standard_error` double DEFAULT NULL,
  `type` varchar(15) NOT NULL DEFAULT '',
  `valid` tinyint(1) DEFAULT NULL,
  `comment` varchar(100) DEFAULT NULL,
  `username` varchar(45) NOT NULL DEFAULT '',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`quantitationid`),
  KEY `ratio` (`ratio`),
  KEY `type` (`type`),
  KEY `valid` (`valid`),
  KEY `l_quantitation_groupid` (`l_quantitation_groupid`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantitation_file`
--

DROP TABLE IF EXISTS `quantitation_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantitation_file` (
  `quantitation_fileid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filename` varchar(100) NOT NULL DEFAULT '',
  `type` varchar(15) NOT NULL DEFAULT '',
  `file` longblob NOT NULL,
  `username` varchar(45) NOT NULL DEFAULT '',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`quantitation_fileid`) USING BTREE,
  KEY `filename` (`filename`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quantitation_group`
--

DROP TABLE IF EXISTS `quantitation_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quantitation_group` (
  `quantitation_groupid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_quantitation_fileid` int(10) unsigned NOT NULL,
  `file_ref` varchar(15) NOT NULL,
  `username` varchar(45) NOT NULL,
  `creationdate` datetime NOT NULL,
  `modificationdate` datetime NOT NULL,
  PRIMARY KEY (`quantitation_groupid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `scan`
--

DROP TABLE IF EXISTS `scan`;
CREATE TABLE  `scan` (
  `scanid` int(10) NOT NULL AUTO_INCREMENT,
  `l_spectrumid` int(10) unsigned NOT NULL DEFAULT '0',
  `number` smallint(6) DEFAULT '0',
  `rtsec` decimal(20,4) DEFAULT '0.0000',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`scanid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


--
-- Table structure for table `spectrum`
--

DROP TABLE IF EXISTS `spectrum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spectrum` (
  `spectrumid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_lcrunid` int(10) unsigned NOT NULL DEFAULT '0',
  `l_projectid` int(10) unsigned NOT NULL DEFAULT '0',
  `l_fragmentationid` int(10) unsigned NOT NULL DEFAULT '0',
  `l_instrumentid` int(10) unsigned NOT NULL DEFAULT '0',
  `searched` int(10) unsigned DEFAULT '0',
  `identified` int(10) unsigned DEFAULT '0',
  `filename` varchar(250) NOT NULL DEFAULT '',
  `charge` int(11) DEFAULT NULL,
  `mass_to_charge` decimal(20,4) DEFAULT NULL,
  `total_spectrum_intensity` decimal(20,4) NOT NULL DEFAULT '0.0000',
  `highest_peak_in_spectrum` decimal(20,4) NOT NULL DEFAULT '0.0000',
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`spectrumid`),
  UNIQUE KEY `filename` (`filename`),
  UNIQUE KEY `filename_index` (`filename`),
  KEY `l_projectid_index` (`l_projectid`),
  KEY `l_instrumentid` (`l_instrumentid`),
  KEY `l_lcrunidindex` (`l_lcrunid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1 MAX_ROWS=10000000 AVG_ROW_LENGTH=10000;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `spectrum_file`
--

DROP TABLE IF EXISTS `spectrum_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `spectrum_file` (
  `l_spectrumid` int(10) unsigned NOT NULL,
  `file` longblob NOT NULL,
  PRIMARY KEY (`l_spectrumid`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `status`
--

DROP TABLE IF EXISTS `status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `status` (
  `statusid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`statusid`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `userid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '',
  `username` varchar(45) NOT NULL DEFAULT 'CONVERSION_FROM_MS_LIMS_4',
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`userid`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `validation`
--

DROP TABLE IF EXISTS `validation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `validation` (
  `validationid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `l_identificationid` int(10) unsigned NOT NULL DEFAULT '0',
  `l_validationtypeid` int(11) NOT NULL DEFAULT '0',
  `auto_comment` text,
  `manual_comment` text,
  `username` varchar(45) DEFAULT NULL,
  `creationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modificationdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`validationid`),
  KEY `l_identificationid` (`l_identificationid`)
) ENGINE=MyISAM  AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

--
-- Table structure for table `validationtype`
--
DROP TABLE IF EXISTS `validationtype`;
CREATE TABLE `validationtype` (
  `validationtypeid` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`validationtypeid`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1;

-- Insert default validation values.
-- NOT-VALIDATED
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( 0, "not validated");

-- AUTOMATIC ACCEPT
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( 1, "automatic accepted");
-- AUTOMATIC REJECT
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( -1, "automatic rejected");

-- MANUAL ACCEPT
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( 2, "manual accepted");
-- MANUAL REJECT
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( -2, "manual rejected");

-- Insert default ionscoring types
INSERT INTO ionscoring (`ionscoringid`, `description`, `username`, `creationdate`, `modificationdate`) VALUES (0,"scoring-FALSE||significant-FALSE","default", CURDATE(), CURDATE());
INSERT INTO ionscoring (`ionscoringid`, `description`, `username`, `creationdate`, `modificationdate`) VALUES (1,"scoring-FALSE||significant-TRUE","default", CURDATE(), CURDATE());
INSERT INTO ionscoring (`ionscoringid`, `description`, `username`, `creationdate`, `modificationdate`) VALUES (2,"scoring-TRUE||significant-TRUE","default", CURDATE(), CURDATE());

INSERT INTO `ms_lims_properties` VALUES (1,'version','${version.number}',CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `ms_lims_properties` VALUES (2,'modification_conversion_version','1',CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO fragmentation (fragmentationid, description, username, creationdate, modificationdate) values (1, 'CID', CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),(2, 'ETD', CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
