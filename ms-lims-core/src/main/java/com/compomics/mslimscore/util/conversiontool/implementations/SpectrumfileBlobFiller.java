package com.compomics.mslimscore.util.conversiontool.implementations;

import com.compomics.mslimscore.util.conversiontool.interfaces.DBConverterStep;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: kenny Date: Apr 9, 2010 Time: 11:26:01 AM
 * <p/>
 * This class
 */
public class SpectrumfileBlobFiller implements DBConverterStep {
// ------------------------------ FIELDS ------------------------------

    // Class specific log4j logger for SpectrumfileBlobFiller instances.
    private static Logger logger = Logger.getLogger(SpectrumfileBlobFiller.class);
    private Integer iMaximumSpectrumfileid;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DBConverterStep ---------------------

    public boolean performConversionStep(final Connection aConn) {
        boolean lError = false;
        int lNumberOfSpectrumfiles = getNumberOfSpectrumfiles(aConn);
        int lRollingOffset = 1;
        iMaximumSpectrumfileid = -1;
        try {
            // Get the number of spectra currently stored in the table.
            PreparedStatement stat = aConn.prepareStatement("select MAX(spectrumfileid) from spectrumfile;");
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                iMaximumSpectrumfileid = rs.getInt(1);
            }
            rs.close();
            stat.close();

            // Start iterating all spectrum rows in the database.
            while (lRollingOffset < iMaximumSpectrumfileid) {

                String lQuery = getSpectrumfileQuery(lRollingOffset, lNumberOfSpectrumfiles, aConn);

                if (lQuery != null) {
                    stat = aConn.prepareStatement(lQuery);
                    stat.executeUpdate();
                }

                lRollingOffset = lRollingOffset + lNumberOfSpectrumfiles;

                stat.close();
                logger.info("Inserted " + lRollingOffset + " so far in the spectrum_file table...");
            }

            // Ok, exiting the while loop means that the rolling offset has finished
            // fetching all spectrums with an id below the MAX value.
            lError = false;
        } catch (SQLException e) {
            logger.error("SQLException thrown while filling the spectrum_file blob table!!", e);
            lError = true;
        }

        return lError;
    }

    /**
     * This method returns the ideal number of spectrums that should be used for the cycic updates.
     *
     * @param aConn
     * @return The number of spectrums that is ideally used to perfrom subselects in relation to the MySQL
     *         open_file_limit.
     */
    private int getNumberOfSpectrumfiles(final Connection aConn) {
        int lResult = 10;
        try {
            PreparedStatement ps = aConn.prepareStatement("show variables where Variable_name='open_files_limit'");
            ResultSet rs = ps.executeQuery();
            rs.next();
            Long lOpenFileLimit = Long.parseLong(rs.getString("Value"));
            if (lOpenFileLimit > 1000) {
                lResult = 1000;
            } else {
                lResult = (int) (lOpenFileLimit / 2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lResult;
    }

// -------------------------- OTHER METHODS --------------------------

    private String getSpectrumfileQuery(int aOffset, int aLength, final Connection aConn) throws SQLException {
        String lResult = null;

        // First assert whether the next set of spectrumid enhold any spectra at all.
        int lCurrentMin = aOffset;
        int lCurrentMax = aOffset + aLength;
        String lTestQuery = "select spectrumfileid from spectrumfile where spectrumfileid >= " + lCurrentMin + " and spectrumfileid < " + lCurrentMax;
        PreparedStatement stat = aConn.prepareStatement(lTestQuery);
        ResultSet rs = stat.executeQuery();
        boolean passTest = false;
        Vector<Integer> lSpectrumfileids = new Vector<Integer>();
        while (rs.next()) {
            // At least one spectrum in this range,
            lSpectrumfileids.add(rs.getInt(1));
        }
        stat.close();
        rs.close();

        // If passed, get a new batch of spectrumid's based on a rolling index
        if (lSpectrumfileids.size() > 0) {
            StringBuilder sb = new StringBuilder();

            /*
            INSERT INTO t2 (b, c)
            VALUES ((SELECT a FROM t1 WHERE b='Chip'), 'shoulder'),
            ((SELECT a FROM t1 WHERE b='Chip'), 'old block'),
            ((SELECT a FROM t1 WHERE b='John'), 'toilet'),
            ((SELECT a FROM t1 WHERE b='John'), 'long,silver'),
            ((SELECT a FROM t1 WHERE b='John'), 'li''l');
            */

            sb.append("INSERT INTO spectrum_file (l_spectrumid, file) VALUES ");
            // Add subselects for each spectrum.
            for (int i = 0; i < lSpectrumfileids.size(); i++) {
                Integer lCurrentSpectrumfileid = lSpectrumfileids.get(i);
                sb.append("(" + lCurrentSpectrumfileid + ", (SELECT file from spectrumfile where spectrumfileid=" + lCurrentSpectrumfileid + "))");
                // Stop at the end.
                if (lCurrentSpectrumfileid == iMaximumSpectrumfileid) {
                    break;
                } else if (i < (lSpectrumfileids.size() - 1)) {
                    sb.append(",");
                }
            }
            sb.append(";");
            lResult = sb.toString();
        }

        return lResult;
    }
}