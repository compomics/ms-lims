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
!com.compomics.mslims.db.conversiontool.implementations.StorageEngine_Package_Change_StepImpl