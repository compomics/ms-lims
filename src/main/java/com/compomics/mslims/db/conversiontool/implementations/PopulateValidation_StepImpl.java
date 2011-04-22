/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 2-jan-2006
 * Time: 14:47:49
 */
package com.compomics.mslims.db.conversiontool.implementations;

import com.compomics.mslims.db.accessors.Validation;
import com.compomics.mslims.db.accessors.Validationtype;
import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2007/10/12 19:33:03 $
 */

/**
 * This class handles the conversion of Validation status variables to validationtype fields.
 *
 * @author Lennart
 * @version $Id: PopulateValidation.java,v 1.1 2007/10/12 19:33:03 lennart Exp $
 */
public class PopulateValidation_StepImpl implements DBConverterStep {
    // Class specific log4j logger for Modified_sequence_correctionStepImpl instances.
    private static Logger logger = Logger.getLogger(PopulateValidation_StepImpl.class);
    public int iRange;

    /**
     * Default constructor.
     */
    public PopulateValidation_StepImpl() {
        // Wee need to ensure there is always a default public constructor!
    }

    /**
     * This method will be called whenever this step should be executed.
     *
     * @param aConn Connection on which to perform the step.
     * @return boolean that indicates success ('false') or failure ('true').
     */
    public boolean performConversionStep(Connection aConn) {
        boolean error = false;
        int lPersistCounter = 0;

        try {

            PreparedStatement lPreparedStatement;
            ResultSet rs;

            // 1. Get maximum identificationid
            String lMaxIdentificationQuery = "select max(identificationid) from identification";
            lPreparedStatement = aConn.prepareStatement(lMaxIdentificationQuery);
            rs = lPreparedStatement.executeQuery();
            rs.next();
            int lMaxIdentificationID = rs.getInt(1);

            // Ok! Now we now the max value to grow the intervals.
            iRange = 10000;
            int lLoopsNeeded = (lMaxIdentificationID / iRange) + 1;

            for (int i = 0; i < lLoopsNeeded; i++) {
                int lRunningLow = i * iRange;
                int lRunningHigh = (i + 1) * iRange;

                // First get all validations from this interval.
                String lQuery = "select l_identificationid from validation where l_identificationid >= ? and l_identificationid < ?";
                lPreparedStatement = aConn.prepareStatement(lQuery);
                lPreparedStatement.setLong(1, lRunningLow);
                lPreparedStatement.setLong(2, lRunningHigh);

                rs = lPreparedStatement.executeQuery();

                HashSet lValidatedIdentifications = new HashSet();


                // Store all validated identifications that are returned int his interval.
                while (rs.next()) {
                    lValidatedIdentifications.add(rs.getLong(1));
                }

                // close the rs and statement.
                rs.close();
                lPreparedStatement.close();


                // Ok, now get all the identifications in this interval.
                lQuery = "select identificationid from identification where identificationid >= ? and identificationid < ?";
                lPreparedStatement = aConn.prepareStatement(lQuery);
                lPreparedStatement.setLong(1, lRunningLow);
                lPreparedStatement.setLong(2, lRunningHigh);

                rs = lPreparedStatement.executeQuery();

                ArrayList<Validation> lValidationList = new ArrayList<Validation>();
                while (rs.next()) {
                    // Iterate over all identifications in this interval.
                    Long lIdentificationID = new Long(rs.getLong(1));

                    if (lValidatedIdentifications.add(lIdentificationID) == true) {
                        // This identification was not yet validated -- make a new row!!
                        Validation lValidation = new Validation();

                        lValidation.setL_identificationid(lIdentificationID);
                        lValidation.setL_validationtypeid(Validationtype.NOT_VALIDATED);

                        lValidationList.add(lValidation);
//                        lValidation.persist(aConn);
                        lPersistCounter++;
                    }
                }

                rs.close();
                lPreparedStatement.close();

                // Ok, all new Validation objects have been made - now make a batch query!
                StringBuffer lBuffer = new StringBuffer();

                lBuffer.append("INSERT INTO validation (validationid, l_identificationid, l_validationtypeid, auto_comment, manual_comment, username, creationdate, modificationdate) values ");
                for (Validation lValidation : lValidationList) {
                    lBuffer.append("( NULL, "
                            + lValidation.getL_identificationid() + ", "
                            + lValidation.getL_validationtypeid() + ", "
                            + lValidation.getAuto_comment() + ", "
                            + lValidation.getManual_comment() + ", "
                            + "CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),");
                }

                String lBatchInsertQuery = lBuffer.substring(0, lBuffer.length() - 1);

                lPreparedStatement = aConn.prepareStatement(lBatchInsertQuery);
                lPreparedStatement.executeUpdate();
                logger.debug("persist counter update:\t" + lPersistCounter);

            }

            logger.info("Finished populating the Validation table with non-validated default values is complete (n=" + lPersistCounter + ").");

        } catch (SQLException sqle) {
            logger.error("\n\nError converting validation status: ");
            logger.error(sqle.getMessage());
            logger.error(sqle.getMessage(), sqle);
            error = true;
        }
        return error;
    }
}
