/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 19-Jun-2009
 * Time: 17:13:26
 */
package com.compomics.mslims.db.conversiontool.implementations;

import com.compomics.mslims.db.accessors.Spectrumfile;
import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;
import com.compomics.mslims.util.fileio.MascotGenericFile;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
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
 * @version $Id: Populate_TIC_and_highest_peak_StepImpl.java,v 1.3 2009/11/06 11:47:15 kenny Exp $
 */
public class Populate_TIC_and_highest_peak_StepImpl implements DBConverterStep {

    public Populate_TIC_and_highest_peak_StepImpl() {
    }

    public boolean performConversionStep(Connection aConn) {
        boolean error = false;

        // We'll have to ensure that, for each spectrum that does not yet have
        // a total intensity or highest peak, that these two columns are populated.
        try {
            // First locate all relevant spectrumfile rows.
            Statement stat = aConn.createStatement();
            ResultSet rs = stat.executeQuery("select spectrumfileid from spectrumfile where (highest_peak_in_spectrum is null or total_spectrum_intensity is null) or (highest_peak_in_spectrum = 0.0 or total_spectrum_intensity = 0.0)");
            ArrayList<Integer> spectra = new ArrayList<Integer>();
            while (rs.next()) {
                spectra.add(rs.getInt(1));
            }
            rs.close();
            stat.close();
            System.out.println("\tFound " + spectra.size() + " spectrumfile records to update.");

            // Now update each of these.
            PreparedStatement ps = aConn.prepareStatement("update spectrumfile set highest_peak_in_spectrum=?, total_spectrum_intensity=? where spectrumfileid=?");
            int count = 0;
            int triedCount = 0;
            double rollingThreshold = 5.0;
            for (Iterator lIntegerIterator = spectra.iterator(); lIntegerIterator.hasNext();) {
                triedCount++;
                long specID = (Integer) lIntegerIterator.next();

                MascotGenericFile mgf;
                double maxInt;
                double tic;

                try {
                    Spectrumfile sf = Spectrumfile.findFromID(specID, aConn);
                    mgf = new MascotGenericFile(sf.getFilename(), new String(sf.getUnzippedFile()));
                    maxInt = mgf.getHighestIntensity();
                    tic = mgf.getTotalIntensity();
                    ps.setString(1, "" + maxInt);
                    ps.setString(2, "" + tic);
                    ps.setLong(3, specID);
                    int updated = ps.executeUpdate();
                    if (updated != 1) {
                        System.err.println(" *** Error updating spectrumfile with id '" + specID + "': updated " + updated + " rows instead of the expected 1!");
                    } else {
                        count++;
                    }
                }
                catch (Exception e) {
                    System.err.println(" *** Error catched while updating spectrumfile with id '" + specID + "' Setting intensity fields to '-1'. " +
                            "\n*** Message:" + e.getMessage());
                    e.printStackTrace();
                    // If an error was thrown, set the intensity fields to -1 in the catch block.
                    setToFailure(specID, ps);
                }
                ps.clearParameters();
                if (((((double) triedCount) / spectra.size()) * 100) > rollingThreshold) {
                    System.out.println("\t  " + rollingThreshold + "% complete");
                    rollingThreshold += 5.0;
                }
            }

            System.out.println("\tSuccessfully updated " + count + " out of " + spectra.size() + " spectrumfile records.");
            if ((count - spectra.size()) < 0) {
                System.err.println("\n *** Note that there were " + (spectra.size() - count) + " spectrumfile rows that were NOT updated!\n *** Please see error messages above (indicated by 'leading ***') for details!");
            }
            // Flag successful completion.
            error = false;
        } catch (Exception e) {
            System.err.println("\n\nError updating SpectrumFile with total intensity and highest peak: ");
            System.err.println(e.getMessage());
            e.printStackTrace();
            error = true;
        }
        return error;
    }

    /**
     * Sets the intensity values for the given SpectrumfileID to -1 upon a failing update.
     *
     * @param aSpecID The SpectrumfileID to update.
     * @param aPs     The PreparedStatement to execute the update.
     * @throws SQLException
     */
    private void setToFailure(final long aSpecID, final PreparedStatement aPs) throws SQLException {
        aPs.clearParameters();
        aPs.setDouble(1, -1);
        aPs.setDouble(2, -1);
        aPs.setLong(3, aSpecID);

        int updated = aPs.executeUpdate();
        if (updated != 1) {
            System.err.println(" *** Error updating spectrumfile with id '" + aSpecID + "': updated " + updated + " rows instead of the expected 1!");
        }
    }
}
