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
# Conversion from ms_lims 7.1 to ms_lims 7.2 #
#                                            #
##############################################


# INSTRUMENT table
# ====================
#
#  - Change the classname for each instrument's StorageEngine.

# Step 1
!com.compomics.mslims.db.conversiontool.implementations.Package_Change_StepImpl

# Step 2 - create the new quantitation_group table
CREATE TABLE `quantitation_group` (  `quantitation_groupid` INTEGER UNSIGNED NOT NULL,  `l_quantitation_fileid` INTEGER UNSIGNED NOT NULL,  `file_ref` VARCHAR(15) NOT NULL,  `username` VARCHAR(45) NOT NULL,  `creationdate` DATETIME NOT NULL,  `modificationdate` DATETIME NOT NULL ) ENGINE = MyISAM;

# Step 3 - Fill the newly created quantitation_group table
!com.compomics.mslims.db.conversiontool.implementations.QuantitationGroupFiller

# Step 4 - alter the created table
ALTER TABLE `quantitation_group` MODIFY COLUMN `quantitation_groupid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY (`quantitation_groupid`);

# Step 5 - set the correct auto increment starter
!com.compomics.mslims.db.conversiontool.implementations.QuantitationGroupIncrementSetter

# Step 6 - alter the quantitation table
ALTER TABLE `quantitation` DROP COLUMN `l_quantitation_fileid`, DROP COLUMN `file_ref`, CHANGE COLUMN `quantitation_link` `l_quantitation_groupid` INTEGER UNSIGNED NOT NULL DEFAULT 0, DROP INDEX `quantitation_link`, ADD INDEX `l_quantitation_groupid` USING BTREE(`l_quantitation_groupid`);

# Step 7 - alter the identification_to_quantitation table
ALTER TABLE `identification_to_quantitation` CHANGE COLUMN `quantitation_link` `l_quantitation_groupid` INTEGER UNSIGNED NOT NULL DEFAULT 0, DROP INDEX `quantitation id`, ADD INDEX `l_quantitation_groupid` USING BTREE(`l_quantitation_groupid`);

# Step 8 - create the new spectrumfile blob table
CREATE  TABLE `spectrum_file` (  `l_spectrumid` INT(10) unsigned NOT NULL,  `file` LONGBLOB NOT NULL,  PRIMARY KEY (`l_spectrumid`)) ENGINE=MyISAM;

# Step 9 - fill the newly created spectrumfile_blob table
!com.compomics.mslims.db.conversiontool.implementations.SpectrumfileBlobFiller

# Step 10 - drop the file column on the spectrumfile table
ALTER TABLE `spectrumfile` DROP COLUMN `file`;

# Step 11 - optimize the spectrumfile table
OPTIMIZE TABLE `spectrumfile`;

# Step 12 - alter spectrumfile to spectrum
ALTER TABLE spectrumfile rename to spectrum

# Step 13 - alter spectrumfileid to spectrumid
ALTER TABLE `spectrum` CHANGE COLUMN `spectrumfileid` `spectrumid` int(10) UNSIGNED NOT NULL DEFAULT NULL AUTO_INCREMENT;

# Step 14 - alter identification.l_spectrumfileid to identification.l_spectrumid
ALTER TABLE `identification` CHANGE COLUMN `l_spectrumfileid` `l_spectrumid` int(10) unsigned NOT NULL DEFAULT '0';

# Step 15 - add index to quantitation_file table on the filename
ALTER TABLE `quantitation_file` ADD INDEX filename USING BTREE(`filename`);