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
# Conversion from ms_lims 4.1 to ms_lims 5.0 #
#                                            #
##############################################


# DEFAULTS
# ========
# 
#  - The following defaults will be used for the auditing columns if they are not yet present.
# 
#  	+ USERNAME: CONVERSION_FROM_MS_LIMS_4
# 	+ CREATIONDATE: CURRENT_TIMESTAMP
# 	+ MODIFICATIONDATE: CURRENT_TIMESTAMP


# COFRADIC table
# ==============
# 
#  - Addition of username, creationdate, modificationdate columns (init all with defaults)

# Step 1
alter table cofradic add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 2
alter table cofradic add creationdate DATETIME not null;
# Step 3
alter table cofradic add modificationdate DATETIME not null;
# Step 4
update cofradic set creationdate=CURRENT_TIMESTAMP, modificationdate=CURRENT_TIMESTAMP;


# USER table
# ==========
# 
#  - Addition of username, creationdate, modificationdate columns (init all with defaults)
# Step 5
alter table user add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 6
alter table user add creationdate DATETIME not null;
# Step 7
alter table user add modificationdate DATETIME not null;
# Step 8
update user set creationdate=CURRENT_TIMESTAMP, modificationdate=CURRENT_TIMESTAMP;


# FILEDESCRIPTOR table
# ====================
# 
#  - Addition of username, modificationdate columns (init all with defaults), change creationdate to datetime and move it to correct location.
# Step 9
alter table filedescriptor add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 10
alter table filedescriptor add modificationdate DATETIME not null;
# Step 11
alter table filedescriptor modify creationdate datetime not null after username;
# Step 12
update filedescriptor set modificationdate=CURRENT_TIMESTAMP;


# BINFILE table
# =============
# 
#  - Addition of username, modificationdate columns (init all with defaults), change creationdate to datetime and move it to correct location.
# Step 13
alter table binfile add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 14
alter table binfile add modificationdate DATETIME not null;
# Step 15
alter table binfile modify creationdate datetime not null after username;
# Step 16
update binfile set modificationdate=CURRENT_TIMESTAMP;


# LCRUN table
# ===========
# 
#  - Addition of username, modificationdate columns (init all with defaults), change creationdate to datetime and move it to correct location.
# Step 17
alter table lcrun add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 18
alter table lcrun add modificationdate DATETIME not null;
# Step 19
alter table lcrun modify creationdate datetime not null after username;
# Step 20
update lcrun set modificationdate=CURRENT_TIMESTAMP;


# INSTRUMENT table
# ================
#
#  - Addition of username, modificationdate columns (init all with defaults), change creationdate to datetime and move it to correct location.
# Step 21
alter table instrument add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 22
alter table instrument add modificationdate DATETIME not null;
# Step 23
alter table instrument modify creationdate datetime not null after username;
# Step 24
update instrument set modificationdate=CURRENT_TIMESTAMP;


# PROJECTANALYZERTOOL table
# =========================
# 
#  - Addition of username, modificationdate columns (init all with defaults), change creationdate to datetime and move it to correct location.
# Step 25
alter table projectanalyzertool add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 26
alter table projectanalyzertool add modificationdate DATETIME not null;
# Step 27
alter table projectanalyzertool modify creationdate datetime not null after username;
# Step 28
update projectanalyzertool set modificationdate=CURRENT_TIMESTAMP;


# SPECTRUMFILE table
# ==================
# 
#  - Move l_lcrunid, l_projectid and l_instrumentid to correct location
#  - Move filename to correct location
#  - Addition of username, modificationdate columns (init all with defaults), change creationdate to datetime and move it to correct location.
# Step 29
alter table spectrumfile modify l_lcrunid int unsigned not null after spectrumfileid;
# Step 30
alter table spectrumfile modify l_projectid int unsigned not null after l_lcrunid;
# Step 31
alter table spectrumfile modify l_instrumentid int unsigned not null after l_projectid;
# Step 32
alter table spectrumfile modify filename varchar(250) not null after file;
# Step 33
alter table spectrumfile add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 34
alter table spectrumfile add modificationdate DATETIME not null;
# Step 35
alter table spectrumfile modify creationdate datetime not null after username;
# Step 36
update spectrumfile set modificationdate=CURRENT_TIMESTAMP;


# PROJECT table
# =============
#
#  - Move l_userid, l_cofradicid to correct location
#  - Addition of username, modificationdate columns (init all with defaults), change creationdate to datetime and move it to correct location.
# Step 37
alter table project modify l_userid int unsigned not null after projectid;
# Step 38
alter table project modify l_cofradicid int unsigned not null after l_userid;
# Step 39
alter table project add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 40
alter table project add modificationdate DATETIME not null;
# Step 41
alter table project modify creationdate datetime not null after username;
# Step 42
update project set modificationdate=CURRENT_TIMESTAMP;


# DATFILE(S) table
# ================
# 
#  - Rename table DATFILES to DATFILE
#  - Add datfileid column (will become primary key, use programmatic step first for inserting the values) at the correct location
#  - Add columns server, folder.
#  - Addition of username, modificationdate columns (init all with defaults), change creationdate to datetime and move it to correct location.
#  - Make datfileid the primary key.
# Step 43
rename table datfiles to datfile;
# Step 44
alter table datfile drop primary key;
# Step 45
alter table datfile add datfileid int unsigned not null AUTO_INCREMENT primary key first;
# Step 46
alter table datfile add server varchar(250) not null;
# Step 47
alter table datfile add folder varchar(250) not null;
# Step 48
alter table datfile add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 49
alter table datfile add modificationdate DATETIME not null;
# Step 50
alter table datfile modify creationdate datetime not null after username;
# Step 51
update datfile set modificationdate=CURRENT_TIMESTAMP;


# IDENTIFICATION table
# ====================
# 
#  - Rename column id to identificationid
#  - Rename column 16O to light_isotope
#  - Rename column 18O to heavy_isotope
#  - Add columns l_spectrumfileid, l_datfileid (foreign keys will be programmatically initialised later)
#  - Addition of username, modificationdate columns (init all with defaults), change creationdate to datetime and move it to correct location.
#  - Run programmatic script to update DATFILE and IDENTIFICATION tables.
#  - Drop columns filename, datfile and server in IDENTIFICATION as these are now no longer used.
# Step 52
alter table identification change ID identificationid int unsigned not null auto_increment;
# Step 53
alter table identification change 16O light_isotope decimal(12,4);
# Step 54
alter table identification change 18O heavy_isotope decimal(12,4);
# Step 55
alter table identification add l_spectrumfileid int unsigned not null after identificationid;
# Step 56
alter table identification add l_datfileid int unsigned not null after l_spectrumfileid;
# Step 57
alter table identification add username varchar(45) not null default 'CONVERSION_FROM_MS_LIMS_4';
# Step 58
alter table identification add modificationdate DATETIME not null;
# Step 59
alter table identification modify creationdate datetime not null after username;
# Step 60
update identification set modificationdate=CURRENT_TIMESTAMP;

# Step 61
!com.compomics.mslims.db.conversiontool.implementations.Datfiles_and_Identification_to_50DBConverterStepImpl

# Step 62
alter table identification drop filename;
# Step 63
alter table identification drop server;
# Step 64
alter table identification drop datfile;
