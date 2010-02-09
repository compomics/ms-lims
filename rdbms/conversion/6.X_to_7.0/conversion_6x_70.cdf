#####################################################################
#                                                                   #
# This is a database Conversion Definition File ('.cdf' extension). #
#                                                                   #
# CDF files are read and interpreted by the DBConversionTool class  #
# of the ms_lims project (http://genesis.UGent.be/ms_lims).         #
#                                                                   #
# Note that lines starting with '#' indicate comments and will be   #
# skipped.                                                          #
# Lines starting with '!' indicate programmatic steps and should    #
# be followed by the fully qualified classname of the relevant      #
# DBConverterStep implementation.                                   #
#                                                                   #
# All other lines are considered to be SQL statements. There should #
# be only one statement per line and no statements spanning         #
# multiple lines!                                                   #
#                                                                   #
#####################################################################


##############################################
#                                            #
# Conversion from ms_lims 6.x to ms_lims 7.0 #
#                                            #
##############################################


#create quantitation table
CREATE TABLE  `quantitation` (  `quantitationid` int(10) unsigned NOT NULL auto_increment,  `l_quantitation_fileid` int(10) unsigned default NULL,  `file_ref` varchar(15) default NULL,  `quantitation_link` int(10) unsigned NOT NULL default '0',  `ratio` double NOT NULL default '0',  `standard_error` double default NULL,  `type` varchar(15) NOT NULL default '',  `valid` tinyint(1) default NULL,  `comment` varchar(100) default NULL,  `username` varchar(45) NOT NULL default '',  `creationdate` datetime NOT NULL default '0000-00-00 00:00:00',  `modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',  PRIMARY KEY  (`quantitationid`),  KEY `ratio` (`ratio`),  KEY `type` (`type`),  KEY `quantitation_link` (`quantitation_link`),  KEY `l_quantitation_fileid` (`l_quantitation_fileid`),  KEY `valid` (`valid`)) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
#create quantitation_file table
CREATE TABLE  `quantitation_file` (  `quantitation_fileid` int(10) unsigned NOT NULL auto_increment,  `filename` varchar(100) NOT NULL default '',  `type` varchar(15) NOT NULL default '',  `file` longblob NOT NULL,  `username` varchar(45) NOT NULL default '',  `creationdate` datetime NOT NULL default '0000-00-00 00:00:00',  `modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',  PRIMARY KEY  USING BTREE (`quantitation_fileid`)) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
#create identification_to_quantitation table
CREATE TABLE  `identification_to_quantitation` (  `itqid` int(10) unsigned NOT NULL auto_increment,  `l_identificationid` int(10) unsigned NOT NULL default '0',  `quantitation_link` int(10) unsigned NOT NULL default '0',  `type` varchar(15) NOT NULL default '',  `username` varchar(45) NOT NULL default '',  `creationdate` datetime NOT NULL default '0000-00-00 00:00:00',  `modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',  PRIMARY KEY  (`itqid`),  KEY `identification id` (`l_identificationid`),  KEY `type` (`type`),  KEY `quantitation id` USING BTREE (`quantitation_link`)) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;
#add the datfile_query to the identification table
ALTER TABLE `identification` ADD COLUMN `datfile_query` INTEGER UNSIGNED DEFAULT NULL AFTER `l_datfileid`;
#add the homology to the identification table
ALTER TABLE `identification` ADD COLUMN `homology` DOUBLE DEFAULT NULL AFTER `score`;
#change the name of the cofradic table to "protocol", also change "cofradicid" to "protocolid"
ALTER TABLE `cofradic` RENAME TO `protocol`, CHANGE COLUMN `cofradicid` `protocolid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, DROP PRIMARY KEY, ADD PRIMARY KEY  USING BTREE(`protocolid`);
#change "l_cofradicid" to "l_protocolid" in the project table
ALTER TABLE `project` CHANGE COLUMN `l_cofradicid` `l_protocolid` INTEGER UNSIGNED NOT NULL DEFAULT 0;
#create validation table
CREATE TABLE `Validation` (  `validationid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,  `l_identificationid` INTEGER UNSIGNED NOT NULL,  `comment` TEXT NOT NULL,  `status` tinyint(1) unsigned NOT NULL,  `username` VARCHAR(45) NOT NULL,  `creationdate` DATETIME NOT NULL,  `modificationdate` DATETIME NOT NULL,  PRIMARY KEY (`validationid`))ENGINE = MyISAM;
