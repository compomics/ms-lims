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
# Conversion from ms_lims 5.x to ms_lims 6.0 #
#                                            #
##############################################


# DEFAULTS
# ========
# 
#  - The following defaults will be used for the auditing columns if they are not yet present.
# 
#  	+ USERNAME: CONVERSION_FROM_MS_LIMS_5
# 	+ CREATIONDATE: CURRENT_TIMESTAMP
# 	+ MODIFICATIONDATE: CURRENT_TIMESTAMP


# IDENTIFICATION table
# ====================
# 
#  - Addition of ion_coverage column.
#  - Conversion of modified_sequence column

# Step 1
ALTER TABLE identification ADD COLUMN ion_coverage text after modified_sequence;

# Step 2
!com.compomics.mslims.db.conversiontool.implementations.Modified_sequence_correctionStepImpl


# FRAGMENTION table
# =================
#
#  - Addition of the FRAGMENTION table.

# Step 3
CREATE TABLE `fragmention` (`fragmentionid` int(10) unsigned NOT NULL auto_increment,`l_identificationid` int(10) unsigned NOT NULL default '0',`iontype` int(10) unsigned NOT NULL default '0',`ionname` varchar(45) NOT NULL default '',`l_ionscoringid` int(10) unsigned NOT NULL default '0',`mz` decimal(12,4) NOT NULL default '0.0000',`intensity` int(10) unsigned default NULL,`fragmentionnumber` int(10) unsigned default NULL,`massdelta` decimal(12,4) default NULL,`masserrormargin` decimal(12,4) NOT NULL default '0.0000',`username` varchar(45) NOT NULL default '',`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',PRIMARY KEY  (`fragmentionid`),KEY `l_identificationid index` (`l_identificationid`),KEY `l_ionscoringid index` (`l_ionscoringid`),KEY `iontype index` USING BTREE (`iontype`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;


# IONSCORING table
# ================
#
#  - Addition of the IONSCORING table.
#  - Insertion of the three Mascot ion scoring types.

# Step 4
CREATE TABLE `ionscoring` (`ionscoringid` int(10) unsigned NOT NULL,`description` varchar(255) NOT NULL default '',`username` varchar(45) NOT NULL default '',`creationdate` datetime NOT NULL default '0000-00-00 00:00:00',`modificationdate` datetime NOT NULL default '0000-00-00 00:00:00',PRIMARY KEY  (`ionscoringid`)) ENGINE=MyISAM DEFAULT CHARSET=latin1;

# Step 5
INSERT INTO `ionscoring` VALUES (0,'Not significant, not used for scoring','CONVERSION_FROM_MS_LIMS_5', CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(2,'Significant, and used for scoring','CONVERSION_FROM_MS_LIMS_5',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(1,'Significant, but not used for scoring','CONVERSION_FROM_MS_LIMS_5',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);