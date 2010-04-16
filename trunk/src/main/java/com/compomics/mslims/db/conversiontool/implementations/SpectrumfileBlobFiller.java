package com.compomics.mslims.db.conversiontool.implementations;

import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA. User: kenny Date: Apr 9, 2010 Time: 11:26:01 AM
 * <p/>
 * This class
 */
public class SpectrumfileBlobFiller implements DBConverterStep {

    // Class specific log4j logger for SpectrumfileBlobFiller instances.
    private static Logger logger = Logger.getLogger(SpectrumfileBlobFiller.class);
    private Integer iMaximumSpectrumfileid;

    public boolean performConversionStep(final Connection aConn) {
        boolean lError = true;
        int lNumberOfSpectrumfiles = 1000;
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

            // Start iterating all spectrumfile rows in the database.
            while (lRollingOffset < iMaximumSpectrumfileid) {
                // Get a new batch of spectrumfileid's based on a rolling index
                String lQuery = getSpectrumfileQuery(lRollingOffset, lNumberOfSpectrumfiles);
                stat = aConn.prepareStatement(lQuery);
                int result = stat.executeUpdate();
                lRollingOffset = lRollingOffset + lNumberOfSpectrumfiles;
                stat.close();
                logger.info("Inserted " + lRollingOffset + " so far in the spectrum_file table...");
            }

            // Ok, exiting the while loop means that the rolling offset has finished
            // fetching all spectrumfiles with an id below the MAX value.
            lError = false;

        } catch (SQLException e) {
            logger.error("SQLException thrown while filling the spectrum_file blob table!!", e);
            lError = false;
        }

        return lError;
    }


    private String getSpectrumfileQuery(int aOffset, int aLength) {
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
        // Add subselects for each spectrumfile.
        for (int i = 0; i < aLength; i++) {
            int lCurrentSpectrumfileid = aOffset + i;
            sb.append("(" + lCurrentSpectrumfileid + ", (SELECT file from spectrumfile where spectrumfileid=" + lCurrentSpectrumfileid + "))");
            // Stop at the end.
            if (lCurrentSpectrumfileid == iMaximumSpectrumfileid) {
                break;
            } else if (i < aLength - 1) {
                sb.append(",");
            }

        }
        sb.append(";");
        return sb.toString();
    }
}