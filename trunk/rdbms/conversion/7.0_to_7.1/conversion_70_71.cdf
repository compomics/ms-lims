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
# Conversion from ms_lims 7.0 to ms_lims 7.1 #
#                                            #
##############################################


# SPECTRUMFILE table
# ====================
#
#  - Addition of two new columns: highest_peak_in_spectrum and total_spectrum_intensity
#  - Automatic population of these two columns
#  - Set both highest_peak_in_spectrum and total_spectrum_intensity to 'not null' status
#    with a default of '0.0'.

# Step 1
# ALTER TABLE spectrumfile ADD COLUMN highest_peak_in_spectrum DECIMAL(20,4) NULL DEFAULT NULL  AFTER filename, ADD COLUMN total_spectrum_intensity DECIMAL(20,4) NULL DEFAULT NULL  AFTER filename;

# Step 2
!com.compomics.mslims.db.conversiontool.implementations.Populate_TIC_and_highest_peak_StepImpl

# Step 3
#ALTER TABLE spectrumfile CHANGE COLUMN total_spectrum_intensity total_spectrum_intensity DECIMAL(20,2) NOT NULL DEFAULT '0.0'  , CHANGE COLUMN highest_peak_in_spectrum highest_peak_in_spectrum DECIMAL(20,2) NOT NULL DEFAULT '0.0';

# Step 4
!com.compomics.mslims.db.conversiontool.implementations.Fix_Precorsor_Water_Loss_Label_StepImpl

# Step 5
!com.compomics.mslims.db.conversiontool.implementations.Remove_fragmention_redundancy_StepImpl