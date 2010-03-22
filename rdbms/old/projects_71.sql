-- MySQL dump 10.13  Distrib 5.1.30, for Win32 (ia32)
--
-- Host: muppet03    Database: projects
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
-- Table structure for table `binfile`
--

DROP TABLE IF EXISTS `binfile`;
CREATE TABLE `binfile` (
`binfileid` int(10) unsigned NOT NULL auto_increment,
`l_projectid` int(10) unsigned NOT NULL default '0',
`l_filedescriptionid` int(10) unsigned NOT NULL default '0',
`file` longblob NOT NULL,
`filename` varchar(250) NOT NULL default '',
`comments` text,
`originalpath` varchar(250) NOT NULL default '',
`originalhost` varchar(50) NOT NULL default '',
`originaluser` varchar(50) NOT NULL default '',
`username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`binfileid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `datfile`
--

DROP TABLE IF EXISTS `datfile`;
CREATE TABLE `datfile` (
`datfileid` int(10) unsigned NOT NULL auto_increment,
`filename` varchar(250) NOT NULL default '',
`file` longblob NOT NULL,
`server` varchar(250) NOT NULL default '',
`folder` varchar(250) NOT NULL default '',
`username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`datfileid`),
KEY `Filename` USING HASH (`filename`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 MAX_ROWS=1000000 AVG_ROW_LENGTH=2658940;

--
-- Table structure for table `filedescriptor`
--

DROP TABLE IF EXISTS `filedescriptor`;
CREATE TABLE `filedescriptor` (
`filedescriptorid` int(10) unsigned NOT NULL auto_increment,
`short_label` varchar(100) NOT NULL default '',
`description` text,
`username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`filedescriptorid`),
UNIQUE KEY `short_label` (`short_label`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `fragmention`
--

DROP TABLE IF EXISTS `fragmention`;
CREATE TABLE `fragmention` (
`fragmentionid` int(10) unsigned NOT NULL auto_increment,
`l_identificationid` int(10) unsigned NOT NULL default '0',
`iontype` int(10) unsigned NOT NULL default '0',
`ionname` varchar(45) NOT NULL default '',
`l_ionscoringid` int(10) unsigned NOT NULL default '0',
`mz` decimal(12,4) NOT NULL default '0.0000',
`intensity` int(10) unsigned default NULL,
`fragmentionnumber` int(10) unsigned default NULL,
`massdelta` decimal(12,4) default NULL,
`masserrormargin` decimal(12,4) NOT NULL default '0.0000',
`username` varchar(45) NOT NULL default '',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`fragmentionid`),
KEY `l_identificationid index` (`l_identificationid`),
KEY `l_ionscoringid index` (`l_ionscoringid`),
KEY `iontype index` USING BTREE (`iontype`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `id_to_phospho`
--

DROP TABLE IF EXISTS `id_to_phospho`;
CREATE TABLE `id_to_phospho` (
`l_id` int(10) unsigned NOT NULL default '0',
`l_phosphorylationid` int(10) unsigned NOT NULL default '0',
`conversionid` int(10) unsigned NOT NULL auto_increment,
PRIMARY KEY  (`conversionid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `identification`
--

DROP TABLE IF EXISTS `identification`;
CREATE TABLE `identification` (
`identificationid` int(10) unsigned NOT NULL auto_increment,
`l_spectrumfileid` int(10) unsigned NOT NULL default '0',
`l_datfileid` int(10) unsigned NOT NULL default '0',
`datfile_query` int(10) unsigned default NULL,
`accession` varchar(50) NOT NULL default '',
`start` int(10) unsigned NOT NULL default '0',
`end` int(10) unsigned NOT NULL default '0',
`enzymatic` char(2) NOT NULL default '',
`sequence` varchar(150) NOT NULL default '',
`modified_sequence` text NOT NULL,
`ion_coverage` text,
`score` int(10) unsigned NOT NULL default '0',
`homology` double default NULL,
`exp_mass` decimal(12,4) NOT NULL default '0.0000',
`cal_mass` decimal(12,4) NOT NULL default '0.0000',
`light_isotope` decimal(12,4) default NULL,
`heavy_isotope` decimal(12,4) default NULL,
`valid` tinyint(4) default NULL,
`Description` text NOT NULL,
`identitythreshold` int(50) unsigned NOT NULL default '0',
`confidence` decimal(12,4) NOT NULL default '0.0500',
`DB` varchar(50) NOT NULL default '',
`title` varchar(250) NOT NULL default '',
`precursor` decimal(12,4) NOT NULL default '0.0000',
`charge` smallint(50) unsigned NOT NULL default '0',
`isoforms` text,
`db_filename` varchar(100) default NULL,
`mascot_version` varchar(25) default NULL,
`username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`identificationid`),
KEY `title_index` (`title`),
KEY `sequence_index` (`sequence`),
KEY `modified_sequence_index` (`modified_sequence`(50)),
KEY `accession_index` (`accession`),
KEY `l_spectrumfileid` (`l_spectrumfileid`),
KEY `l_datfileid` (`l_datfileid`),
KEY `valid` (`valid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `identification_to_quantitation`
--

DROP TABLE IF EXISTS `identification_to_quantitation`;
CREATE TABLE `identification_to_quantitation` (
`itqid` int(10) unsigned NOT NULL auto_increment,
`l_identificationid` int(10) unsigned NOT NULL default '0',
`quantitation_link` int(10) unsigned NOT NULL default '0',
`type` varchar(15) NOT NULL default '',
`username` varchar(45) NOT NULL default '',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`itqid`),
KEY `identification id` (`l_identificationid`),
KEY `type` (`type`),
KEY `quantitation id` USING BTREE (`quantitation_link`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `instrument`
--

DROP TABLE IF EXISTS `instrument`;
CREATE TABLE `instrument` (
`instrumentid` int(10) unsigned NOT NULL auto_increment,
`name` varchar(150) default NULL,
`description` text,
`storageclassname` varchar(250) default NULL,
`propertiesfilename` varchar(250) default NULL,
`differential_calibration` decimal(12,8) default NULL,
`username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`instrumentid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `ionscoring`
--

DROP TABLE IF EXISTS `ionscoring`;
CREATE TABLE `ionscoring` (
`ionscoringid` int(10) unsigned NOT NULL default '0',
`description` varchar(255) NOT NULL default '',
`username` varchar(45) NOT NULL default '',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`ionscoringid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `lcrun`
--

DROP TABLE IF EXISTS `lcrun`;
CREATE TABLE `lcrun` (
`lcrunid` int(10) unsigned NOT NULL auto_increment,
`l_projectid` int(10) unsigned NOT NULL default '0',
`description` text,
`filecount` int(11) NOT NULL default '0',
`name` varchar(150) NOT NULL default '',
`dvd_master_number` int(11) default NULL,
`dvd_secondary_number` int(10) unsigned default NULL,
`primary_fraction` int(10) unsigned default NULL,
`username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`lcrunid`),
UNIQUE KEY `name` (`name`),
KEY `creationdateindex` (`creationdate`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `phosphorylation`
--

DROP TABLE IF EXISTS `phosphorylation`;
CREATE TABLE `phosphorylation` (
`phosphorylationid` int(10) unsigned NOT NULL auto_increment,
`l_status` int(10) unsigned NOT NULL default '0',
`residue` varchar(20) default NULL,
`location` int(10) unsigned NOT NULL default '0',
`accession` varchar(250) NOT NULL default '',
`context` varchar(100) default NULL,
`score` decimal(12,5) default NULL,
`threshold` decimal(12,5) default NULL,
`creationdate` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
`description` varchar(250) default NULL,
PRIMARY KEY  (`phosphorylationid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
`projectid` int(10) unsigned NOT NULL auto_increment,
`l_userid` int(10) unsigned NOT NULL default '0',
`l_protocolid` int(10) unsigned NOT NULL default '0',
`title` varchar(250) NOT NULL default '',
`description` text,
`username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`projectid`),
UNIQUE KEY `title` (`title`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

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
PRIMARY KEY  USING BTREE (`protocolid`),
UNIQUE KEY `type` USING BTREE (`type`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `quantitation`
--

DROP TABLE IF EXISTS `quantitation`;
CREATE TABLE `quantitation` (
`quantitationid` int(10) unsigned NOT NULL auto_increment,
`l_quantitation_fileid` int(10) unsigned default NULL,
`file_ref` varchar(15) default NULL,
`quantitation_link` int(10) unsigned NOT NULL default '0',
`ratio` double NOT NULL default '0',
`standard_error` double default NULL,
`type` varchar(15) NOT NULL default '',
`valid` tinyint(1) default NULL,
`comment` varchar(100) default NULL,
`username` varchar(45) NOT NULL default '',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`quantitationid`),
KEY `ratio` (`ratio`),
KEY `type` (`type`),
KEY `quantitation_link` (`quantitation_link`),
KEY `l_quantitation_fileid` (`l_quantitation_fileid`),
KEY `valid` (`valid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `quantitation_file`
--

DROP TABLE IF EXISTS `quantitation_file`;
CREATE TABLE `quantitation_file` (
`quantitation_fileid` int(10) unsigned NOT NULL auto_increment,
`filename` varchar(100) NOT NULL default '',
`type` varchar(15) NOT NULL default '',
`file` longblob NOT NULL,
`username` varchar(45) NOT NULL default '',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  USING BTREE (`quantitation_fileid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `spectrumfile`
--

DROP TABLE IF EXISTS `spectrumfile`;
CREATE TABLE `spectrumfile` (
`spectrumfileid` int(10) unsigned NOT NULL auto_increment,
`l_lcrunid` int(10) unsigned NOT NULL default '0',
`l_projectid` int(10) unsigned NOT NULL default '0',
`l_instrumentid` int(10) unsigned NOT NULL default '0',
`searched` int(10) unsigned default '0',
`identified` int(10) unsigned default '0',
`file` longblob NOT NULL,
`filename` varchar(250) NOT NULL default '',
`total_spectrum_intensity` DECIMAL(20,4) NOT NULL default '0.0',
`highest_peak_in_spectrum` DECIMAL(20,4) NOT NULL default '0.0',
`username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`spectrumfileid`),
UNIQUE KEY `filename` (`filename`),
UNIQUE KEY `filename_index` (`filename`),
KEY `l_projectid_index` (`l_projectid`),
KEY `l_instrumentid` (`l_instrumentid`),
KEY `l_lcrunidindex` (`l_lcrunid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 MAX_ROWS=10000000 AVG_ROW_LENGTH=10000;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
`userid` int(10) unsigned NOT NULL auto_increment,
`name` varchar(100) NOT NULL default '',
`username` varchar(45) NOT NULL default 'CONVERSION_FROM_MS_LIMS_4',
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`userid`),
UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `validation`
--

DROP TABLE IF EXISTS `validation`;
CREATE TABLE `validation` (
`validationid` int(10) unsigned NOT NULL auto_increment,
`l_identificationid` int(10) unsigned NOT NULL default '0',
`comment` text DEFAULT NULL,
`status` tinyint(1) unsigned NOT NULL default '0',
`l_userid` int(11) DEFAULT NULL,
`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',
`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (`validationid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-03-11 12:32:31


