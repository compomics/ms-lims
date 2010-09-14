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
# Conversion from ms_lims 7.2 to ms_lims 7.5 #
#                                            #
##############################################


# SPECTRUMFILE table
# ====================
#
#  - Addition of three new columns: precuros_mass, precursor_intensity and precursor_rentention
#  - Automatic population of these columns. When missing, they are set to the default value of 0.0.

# Step 1 - Create the scangroup, charge and masstocharge values in the spectrum table.
# ALTER TABLE spectrum ADD COLUMN mass_to_charge DECIMAL(20,4) NULL DEFAULT NULL AFTER filename, ADD COLUMN charge INT NULL DEFAULT NULL AFTER filename;

# Step 2 - Create the scan table.
# CREATE  TABLE IF NOT EXISTS `scan` (  `scanid` INT(10) NOT NULL AUTO_INCREMENT ,  `l_spectrumid` INT(10) UNSIGNED NOT NULL DEFAULT '0' ,  `number` SMALLINT NULL DEFAULT '0' ,  `rtsec` DECIMAL(20,4) NULL DEFAULT '0' ,  `creationdate` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00' ,  `modificationdate` DATETIME NOT NULL DEFAULT '0000-00-00 00:00:00' ,  PRIMARY KEY (`scanid`) ) ENGINE = MyISAM;

# Step 4 - Fill the tables.
!com.compomics.mslims.db.conversiontool.implementations.Populate_Precursor_Metrics_StepImpl

# Step 5 - Set default values.
ALTER TABLE spectrum CHANGE COLUMN mass_to_charge mass_to_charge DECIMAL(20,4) NOT NULL DEFAULT '0', CHANGE COLUMN charge charge INT NOT NULL DEFAULT '0.0';

# Step 7 - Add scan indices.
ALTER TABLE `scan` ADD INDEX spectrum(`l_spectrumid`);



