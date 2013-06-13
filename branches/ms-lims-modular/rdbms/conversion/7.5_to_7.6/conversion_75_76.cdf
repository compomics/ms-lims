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
# Conversion from ms_lims 7.5 to ms_lims 7.6 #
#                                            #
##############################################
####################
# Update - PART 1  #
####################
# Step 1. Create the fragmentation table
CREATE TABLE `fragmentation` (`fragmentationid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,  `description` VARCHAR(45) NOT NULL,  `username` VARCHAR(45) NOT NULL,  `creationdate` DATETIME NOT NULL,`modificationdate` DATETIME NOT NULL, PRIMARY KEY (`fragmentationid`) ) ENGINE = MyISAM;
# Step 2. Insert data in the fragmentation table
INSERT INTO fragmentation (fragmentationid, description, username, creationdate, modificationdate) values (1, 'CID', CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),(2, 'ETD', CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
# Step 3. Add l_fragmentationid column in spectrum table
ALTER TABLE `spectrum` ADD COLUMN `l_fragmentationid` INTEGER UNSIGNED NOT NULL DEFAULT 1 AFTER `l_projectid`
# Step 4. Create the ms_lims_properties table
CREATE TABLE `ms_lims_properties` ( `ms_lims_propertiesid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, `key` VARCHAR(45) NOT NULL, `value` VARCHAR(45) NOT NULL, `username` VARCHAR(45) NOT NULL, `creationdate` DATETIME NOT NULL, `modificationdate` DATETIME NOT NULL,  PRIMARY KEY (`ms_lims_propertiesid`), UNIQUE KEY(`key`) ) ENGINE = MyISAM;
# Step 5. Create the modification_conversion table
CREATE TABLE `modification_conversion` ( `modification_conversionid` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, `modification` VARCHAR(45) NOT NULL, `conversion` VARCHAR(45) NOT NULL, `username` VARCHAR(45) NOT NULL, `creationdate` DATETIME NOT NULL, `modificationdate` DATETIME NOT NULL,   PRIMARY KEY (`modification_conversionid`), UNIQUE KEY(`modification`) ) ENGINE = MyISAM;
# Step 5. Insert values in the newly created tables
INSERT INTO `ms_lims_properties` VALUES (1,'version','7.6',CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO `ms_lims_properties` VALUES (2,'modification_conversion_version','1',CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
INSERT INTO `modification_conversion` VALUES (1,'Pyro-carbamidomethyl (N-term C)','Pyr',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(2,'Gln->pyro-Glu (N-term Q)','Pyr',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(3,'Glu->pyro-glu (N-term E)','Pyr',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(4,'Acetyl (K)','Ace',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(5,'Acetyl (ST)','Ace',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(6,'Acetyl (N-term)','Ace',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(7,'Acetyl:2H(3) (K)','AcD3K',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(8,'Acetyl:2H(3) (N-term)','AcD3',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(9,'N-Formyl (Protein)','For',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(10,'N-Acetyl (Protein)','AcPr',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(11,'Oxidation (M)','Mox',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(12,'Deamidated (NQ)','Dam',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(13,'Label:13C(6) (K)','c13',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(14,'Label:13C(6) (R)','C13',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(15,'Label:18O(1) (C-term)','C180i',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(16,'Label:18O(2) (C-term)','C18O',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(17,'O18Ser (S)','S18O',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(18,'O18Thr (T)','T18O',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(19,'Lys-SBO18 (K)','KsbH',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(20,'Tyr-SBO18 (Y)','YsbH',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(21,'Label:13C(5) (P)','C13',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(22,'Pro 5xC(13)1xN(15) (P)','C13N15',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(23,'Label:13C(6)15N(4) (R)','C13N15',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(24,'Arg 13C6-15N4 (R)','C13N15',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(25,'NMA (R)','NMA',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(26,'NMAC13 (R)','NMA',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(27,'NMAC13N15 (R)','NMA',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(28,'Arg 6xC(13) (R)','C13',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(29,'Pro 5xC(13) (P)','C13',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(30,'Carbamidomethyl (C)','Cmm',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(31,'Myristoylation (term)','Myr',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(32,'Amide (C-term)','Ami',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(33,'Acetyl_heavy (N-term)','AcD3',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(34,'Acetyl_heavy (K)','AcD3K',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(35,'Methyl ester (DE)','Met',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(36,'Methyl ester (C-term)','Met',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(37,'Phospho (STY)','P',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(38,'Phospho (ST)','P',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(39,'Phospho (Y)','P',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(40,'Sulphone (M)','Sul',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(41,'Pyro-cmC (N-term camC)','Pyc',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(42,'Pyro-glu (N-term Q)','Pyr',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(43,'Pyro-glu (N-term E)','Pyr',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(44,'Deamidation (NQ)','Dam',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(45,'HSe','Hse',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(46,'Propionamide (C)','Prp',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(47,'Carbamyl (N-term)','Car',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(48,'thioprop (N-term)','Tpr',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(49,'thiopropcar (N-term)','Tpc',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(50,'ThiopropLys (K)','Tpl',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(51,'ThiopropCarbLys (K)','Tcl',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(52,'Homoarg (K)','Har',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(53,'Biotin-S-S-Lysin (K)','BSL',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(54,'Oxidation - 32 (W)','Wox',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(55,'Oxidation - 48 (W)','Wo2',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(56,'Tyrosinamine (Y)','Yam',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(57,'Tyrosineaminesulfon (Y) ','YAS',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(58,'Tyrosinsulfon (Y) ',' YS',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(59,'Nitro (Y) ',' NO',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(60,'Cysteic_acid (C)','CyA',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(61,'4H_reagent (K)','4H',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(62,'Carbamyl (K)','Cak',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(63,'Phospho-NL (S)','Pnl',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(64,'Phospho-NL (T)','Pnl',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(65,'hydroxykynurenin (W)','Hkn',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(66,'kynurenin (W)','Kyn',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(67,'Oxidation (HW)','Ox',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(68,'Gly-Gly epsilonNH2(K) ',' GGe',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(69,'Methyl (N-term)','Met',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(70,'Tyrosinamine1 (Y)','Yam1',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(71,'Lys-DSP (K)','Dsp',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(72,'Propionyl (N-term)','prop',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(73,'Carbamidomethylox (C)','Cmox1',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(74,'Carbamidomethyloxox (C)','Cmox2',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(75,'6Da/0Da loss 5xC13 1xN15 (M) ','Mloss',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(76,'6Da/0Da gain (M) ','Mgain',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(77,'TyrosinamineAcet (Y) ','Yamac',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(78,'6Da/0Da loss 5xC13 1xN15 (Mox) ','Moxloss',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(79,'6Da/0Da gain (Mox) ','Moxgain',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(80,'MoxC13 (Mox) ',' MoxC13',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(81,'Amino (Y) ',' Yam',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(82,'NBS (W)','NBS',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(83,'Lys-SBA (K)','Ksba',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(84,'His-SBA (H)','Hsba',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(85,'Cys-SBA (C)','Csba',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(86,'Ser-SBA (S)','Ssba',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(87,'His-SB (H)','Hsb',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(88,'Cys-SB (C)','Csb',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(89,'Ser-SB (S)','Ssb',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(90,'Lys-SB (K)','Ksb',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(91,'Tyr-SB (Y)','Ysb',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(92,'Lys-CO1 (K)','CO1',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(93,'RP-CO1 (RP)','CO1',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(94,'threo-CO1 (T)','CO1',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),(95,'Lys-BCO1 (K)','BCO1',CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)


####################
# Update - PART 2  #
####################

# Validation and Validationtype tables
# ====================
#
#  - Split comment column into automatic comments and user comments.
#  - Convert the status Boolean into a relation to a new Validationtype table.
#  - Remove the userid column and create a new column for the Connection username.
#  - Automatic population of the Validation table such that each identification has a validation row by default.

# Step 6 - Alter the validation table.
ALTER TABLE `validation` ADD COLUMN `l_validationtypeid` INT NOT NULL DEFAULT '0' AFTER `l_identificationid`, CHANGE COLUMN `comment` `auto_comment` TEXT NULL DEFAULT NULL, DROP COLUMN `l_userid` , ADD COLUMN `manual_comment` TEXT NULL DEFAULT NULL  AFTER `auto_comment` , ADD COLUMN `username` VARCHAR(45) NULL AFTER `l_validationtypeid`;

# Step 7. Create the new validationtype table.
CREATE  TABLE `validationtype` (`validationtypeid` INT NOT NULL ,`name` VARCHAR(45) NOT NULL ,PRIMARY KEY (`validationtypeid`) ) ENGINE = MyISAM;

# Step 8. Create the default validationtypes.
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( 0, "not validated");
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( 1, "automatic accepted");
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( -1, "automatic rejected");
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( 2, "manual accepted");
INSERT INTO `validationtype` (`validationtypeid`, `name`) VALUES ( -2, "manual rejected");

# Step 9 - Transform the values of the former "status" column into the new Validationtype column.
!com.compomics.mslims.db.conversiontool.implementations.ValidationStatusToValidationType_StepImpl

# Step 10 - Insert default Validation rows for all identification rows in ms-lims.
!com.compomics.mslims.db.conversiontool.implementations.PopulateValidation_StepImpl

# Step 11 - Drop the former "status" column
ALTER TABLE `validation` DROP COLUMN `status`;

# Step 12 - Add index to the Validation table
ALTER TABLE `validation` ADD INDEX index_identificationid(`l_identificationid`);