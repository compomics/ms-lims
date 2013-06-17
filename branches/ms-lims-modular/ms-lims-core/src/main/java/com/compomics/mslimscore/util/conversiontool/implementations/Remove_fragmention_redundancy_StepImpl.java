package com.compomics.mslimscore.util.conversiontool.implementations;

import org.apache.log4j.Logger;

import com.compomics.mslimsdb.accessors.Fragmention;
import com.compomics.mslimscore.util.conversiontool.interfaces.DBConverterStep;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA. User: kenny Date: Aug 18, 2009 Time: 6:26:45 PM
 * <p/>
 * This class
 */
public class Remove_fragmention_redundancy_StepImpl implements DBConverterStep {
    // Class specific log4j logger for Remove_fragmention_redundancy_StepImpl instances.
    private static Logger logger = Logger.getLogger(Remove_fragmention_redundancy_StepImpl.class);

    public Remove_fragmention_redundancy_StepImpl() {
    }

    public boolean performConversionStep(Connection aConn) {
        logger.info("\tStarting to remove redundant fragmentions by identificationids..");

        boolean error = false;

        // We'll have to ensure that, for each spectrum that does not yet have
        // a total intensity or highest peak, that these two columns are populated.
        int lIdentificationID;
        int lSuccess = 0;
        double lThreshold = 10000;
        try {
            // First locate all relevant spectrum rows.
            PreparedStatement lPrepearedStatementIdentifications = aConn.prepareStatement("SELECT identificationid FROM identification");
            ResultSet rsOuter = lPrepearedStatementIdentifications.executeQuery();

            while (rsOuter.next()) {
                // Iterate over all IdentificationIDs.
                lIdentificationID = rsOuter.getInt(1);

                // Create the select statement to fetch all fragmentions of this identificationID.
                PreparedStatement lPreparedStatementFragmentions = aConn.prepareStatement("select * from fragmention where l_identificationid=?");
                lPreparedStatementFragmentions.setInt(1, lIdentificationID);

                // Create an inner resultset to iterate over the fragmentions.
                ResultSet rsInner = lPreparedStatementFragmentions.executeQuery();
                // Use a HashSet to filter unique values of the fragmentions.
                HashSet lSet = new HashSet();
                while (rsInner.next()) {
                    Fragmention lFragmention = new Fragmention(rsInner);
                    boolean isNewElement = lSet.add(lFragmention.toString());
                    // If the toString (ionType,ionNumber and mZ) is not new in the HashSet,
                    if (!isNewElement) {
                        // then delete this element.
                        lSuccess += lFragmention.delete(aConn);
                        logger.info("Deleted redundant fragmention " + lFragmention.toString() + " for identificationid '" + lIdentificationID + "'.");
                        lSuccess++;
                    }
                }

                if ((((double) lSuccess) % lThreshold) == 0) {
                }
                logger.info("Successfully deleted " + "'" + lSuccess + "' redundant ions!");
                rsInner.close();
                lPreparedStatementFragmentions.close();
            }

            rsOuter.close();
            lPrepearedStatementIdentifications.close();

            logger.info("\tSuccessfully removed " + lSuccess + " redundant fragmentions.");

        } catch (Exception e) {
            logger.error("\n\nError while removing redundant fragmentions:");
            logger.error(e.getMessage());
            logger.error(e.getMessage(), e);
            error = true;
        }
        return error;
    }


}
