package com.compomics.mslims.db.conversiontool.implementations;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA. User: kenny Date: Aug 18, 2009 Time: 2:45:11 PM
 * <p/>
 * This class
 */
public class Fix_Precorsor_Water_Loss_Label_StepImpl implements DBConverterStep {
    // Class specific log4j logger for Fix_Precorsor_Water_Loss_Label_StepImpl instances.
    private static Logger logger = Logger.getLogger(Fix_Precorsor_Water_Loss_Label_StepImpl.class);

    public Fix_Precorsor_Water_Loss_Label_StepImpl() {
    }

    public boolean performConversionStep(Connection aConn) {
        logger.info("\tStarting to update fragmention ionnames from H20(0=numeric) into H2O(O=character)..");

        boolean error = false;

        // We'll have to ensure that, for each spectrum that does not yet have
        // a total intensity or highest peak, that these two columns are populated.
        int lFragmentIonID = 0;
        int lTriedCount = 0;
        int lSuccess = 0;
        String lIonName = null;
        String lNewIonName = null;
        double lThreshold = 1000;
        int lLimit = 1000;
        int lCountToLimit = 0;
        try {
            // We split the MySQL queries by limits.
            // Do new Queries to fetch 'H20' fragmentions while there are as much
            // fragmentions left as the limit! The do-while loop will end
            // when there are less Counts then then preset limit.
            do {
                ResultSet rs;
                Statement stat = aConn.createStatement();
                rs = stat.executeQuery("SELECT * FROM fragmention f where ionname regexp '.*H20.*' limit " + lLimit);
                lCountToLimit = 0;
                while (rs.next()) {
                    lCountToLimit++;


                    // Get existing vars.
                    lFragmentIonID = rs.getInt(1);

                    lTriedCount++;
                    lIonName = rs.getString(4);
                    // Replace 'H20' (numeric) into 'H2O' (character)
                    lNewIonName = lIonName.replaceAll("H20", "H2O");

                    // Create the update statement.
                    PreparedStatement ps = aConn.prepareStatement("update fragmention set ionname=? where fragmentionid=?");
                    ps.setString(1, lNewIonName);
                    ps.setInt(2, lFragmentIonID);

                    // Perform the update statement.
                    lSuccess += ps.executeUpdate();

                    if ((((double) lTriedCount) % lThreshold) == 0) {
                        logger.info("\t  " + lTriedCount + " ionnames updated..");
                    }
                }

                rs.close();
                stat.close();

                rs = null;
                stat = null;

                System.gc();

            } while (lCountToLimit == lLimit);


            logger.info("\tSuccessfully updated " + lSuccess + " out of " + lTriedCount + " spectrumfile records.");

        } catch (Exception e) {
            logger.error("\n\nError updating Fragmention ionnames from H20(numeric) to H2O(character): ");
            logger.error(e.getMessage());
            logger.error(e.getMessage(), e);
            error = true;
        }
        return error;
    }
}
