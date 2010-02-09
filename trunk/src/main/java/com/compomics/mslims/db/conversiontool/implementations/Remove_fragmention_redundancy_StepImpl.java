package com.compomics.mslims.db.conversiontool.implementations;

import com.compomics.mslims.db.accessors.Fragmention;
import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: Aug 18, 2009
 * Time: 6:26:45 PM
 * <p/>
 * This class
 */
public class Remove_fragmention_redundancy_StepImpl implements DBConverterStep {

    public Remove_fragmention_redundancy_StepImpl() {
    }

    public boolean performConversionStep(Connection aConn) {
        System.out.println("\tStarting to remove redundant fragmentions by identificationids..");

        boolean error = false;

        // We'll have to ensure that, for each spectrum that does not yet have
        // a total intensity or highest peak, that these two columns are populated.
        int lIdentificationID;
        int lSuccess = 0;
        double lThreshold = 10000;
        try {
            // First locate all relevant spectrumfile rows.
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
                        System.out.println("Deleted redundant fragmention " + lFragmention.toString() + " for identificationid '" + lIdentificationID + "'.");
                        lSuccess++;
                    }
                }

                if ((((double) lSuccess) % lThreshold) == 0) {
                }
                System.out.println("Successfully deleted " + "'" + lSuccess + "' redundant ions!");
                rsInner.close();
                lPreparedStatementFragmentions.close();
            }

            rsOuter.close();
            lPrepearedStatementIdentifications.close();

            System.out.println("\tSuccessfully removed " + lSuccess + " redundant fragmentions.");

        } catch (Exception e) {
            System.err.println("\n\nError while removing redundant fragmentions:");
            System.err.println(e.getMessage());
            e.printStackTrace();
            error = true;
        }
        return error;
    }


}
