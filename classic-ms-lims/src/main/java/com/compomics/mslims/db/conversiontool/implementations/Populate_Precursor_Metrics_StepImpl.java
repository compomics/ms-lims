/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 19-Jun-2009
 * Time: 17:13:26
 */
package com.compomics.mslims.db.conversiontool.implementations;

import com.compomics.mslims.db.accessors.ScanTableAccessor;
import com.compomics.mslims.db.accessors.Spectrum;
import com.compomics.mslims.db.accessors.Spectrum_file;
import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;
import com.compomics.mslims.util.fileio.MascotGenericFile;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2009/11/06 11:47:15 $
 */

/**
 * This class
 *
 * @author martlenn
 * @version $Id: Populate_Precursor_Metrics_StepImpl.java,v 1.3 2009/11/06 11:47:15 kenny Exp $
 */
public class Populate_Precursor_Metrics_StepImpl implements DBConverterStep {
    // INSTANCE VARIABLES.

    // Class specific log4j logger for Populate_Precursor_Metrics_StepImpl instances.
    private static Logger logger = Logger.getLogger(Populate_Precursor_Metrics_StepImpl.class);
    // CONSTRUCTOR.

    public Populate_Precursor_Metrics_StepImpl() {
    }


    // INTERFACE METHODS.

    public boolean performConversionStep(Connection aConn) {
        boolean error = false;
        int limit = 10000;
        int loopCounter = 0;
        MascotGenericFile mgf = null;
        // Do the process in pieces of size=limit.

        // We'll have to ensure that, for each spectrum that does not yet have
        // a total intensity or highest peak, that these two columns are populated.
        try {
            int lProgress = 0;
            // First, estimate the total number of spectra to be processed.
            PreparedStatement init = aConn.prepareStatement("select count(distinct spectrumid) as update_count from spectrum where (mass_to_charge is null or charge is null)");
            ResultSet initRs = init.executeQuery();
            initRs.next();
            long lTotalSpectra = initRs.getLong("update_count");


            do {
                loopCounter = 0;
                // First locate next set of relevant spectrum rows (n = limit).
                Statement stat = aConn.createStatement();
                ResultSet rs = stat.executeQuery("select * from spectrum where (mass_to_charge is null or charge is null) limit " + limit);
                Spectrum lSpectrum;
                Spectrum_file lSpectrum_file;

                while (rs.next()) {
                    lSpectrum = new Spectrum(rs);
                    lSpectrum_file = Spectrum_file.findFromID(lSpectrum.getSpectrumid(), aConn);

                    double lMZ;
                    int lCharge;

                    if (lSpectrum_file == null) {
                        logger.info("Spectrum_file not found for spectrumid '" + lSpectrum.getSpectrumid() + "'!!\n Setting charge and mass_to_charge to 0.");
                        lCharge = 0;
                        lMZ = 0;
                    } else {
                        mgf = new MascotGenericFile(lSpectrum.getFilename(), new String(lSpectrum_file.getUnzippedFile()));
                        lCharge = mgf.getCharge();
                        lMZ = mgf.getPrecursorMZ();
                    }

                    PreparedStatement ps = aConn.prepareStatement("update spectrum set mass_to_charge=?, charge=? where spectrumid=?");
                    ps.setDouble(1, lMZ);
                    ps.setInt(2, lCharge);
                    ps.setLong(3, lSpectrum.getSpectrumid());

                    ps.executeUpdate();
                    ps.close();

                    // Now persist the scan information, if any.
                    if (mgf != null) {
                        if (mgf.getRetentionInSeconds() != null) {
                            double[] lRTInSeconds = mgf.getRetentionInSeconds();
                            int[] lScanNumbers = mgf.getScanNumbers();
                            for (int j = 0; j < lRTInSeconds.length; j++) {
                                double lRTInSecond = lRTInSeconds[j];

                                ScanTableAccessor lScanTableAccessor = new ScanTableAccessor();
                                lScanTableAccessor.setL_spectrumid(lSpectrum.getSpectrumid());
                                lScanTableAccessor.setRtsec(lRTInSecond);
                                if (lScanNumbers != null) {
                                    lScanTableAccessor.setNumber(lScanNumbers[j]);
                                }
                                lScanTableAccessor.persist(aConn);
                            }
                        }
                    }

                    loopCounter++;
                    if (lProgress % 1000 == 0) {
                        System.out.print(".");
                    }
                }


                rs.close();
                stat.close();

                lProgress = lProgress + loopCounter;

                if (lProgress % 10000 == 0) {
                    double lProgressPercentage = (100 * lProgress) / lTotalSpectra;
                    BigDecimal bd = new BigDecimal(lProgressPercentage);
                    bd.setScale(1, BigDecimal.ROUND_UP);
                    lProgressPercentage = bd.doubleValue();
                    logger.info("\t" + lProgressPercentage + "%\n");
                }

                // If less then limit, then the sql query offered less rows then the limit and this was the last resultset!!
            } while (loopCounter == limit);

            logger.info("\tSuccessfully updated " + lProgress + " out of " + lTotalSpectra + " spectrum records.");
            long lDifference = lProgress - lTotalSpectra;
            if (lDifference < 0) {
                logger.error("\n *** Note that there were " + lDifference + " spectrum rows that were NOT updated!\n *** Please see error messages above (indicated by 'leading ***') for details!");
            }

            // Flag successful completion.
            error = false;
        } catch (Exception e) {
            logger.error("\n\nError updating Spectrum with retention time, precursorMZ or charge: \n" + mgf.toString());
            logger.error(e.getMessage());
            logger.error(e.getMessage(), e);
            error = true;
        }
        return error;
    }


    // CLASS METHODS.

    /**
     * Sets the intensity values for the given SpectrumfileID to -1 upon a failing update.
     *
     * @param aSpecID The SpectrumfileID to update.
     * @param aPs     The PreparedStatement to execute the update.
     * @throws java.sql.SQLException
     */
    private void setToFailure(final long aSpecID, final PreparedStatement aPs) throws SQLException {
        aPs.clearParameters();
        aPs.setDouble(1, -1);
        aPs.setDouble(2, -1);
        aPs.setLong(3, aSpecID);

        int updated = aPs.executeUpdate();
        if (updated != 1) {
            logger.error(" *** Error updating spectrum with id '" + aSpecID + "': updated " + updated + " rows instead of the expected 1!");
        }
    }


    // GETTERS AND SETTERS.


}
