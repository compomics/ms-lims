package com.compomics.mslims.db.conversiontool.implementations;

import com.compomics.mslims.db.accessors.Validationtype;
import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA. User: kenny Date: Feb 25, 2010 Time: 11:15:49 AM
 * <p/>
 * This class
 */
public class ValidationStatusToValidationType_StepImpl implements DBConverterStep {
    // Class specific log4j logger for Package_Change_StepImpl instances.
    private static Logger logger = Logger.getLogger(ValidationStatusToValidationType_StepImpl.class);

    public ValidationStatusToValidationType_StepImpl() {
        // Empty constructor.
    }

    /**
     * This method retrieves all instruments in the database, retrieves the class references for each Instrument, and
     * changes the "be.proteomics.*" package structure into "com.compomics.*"
     *
     * @param aConn Connection on which to perform the step.
     * @return boolean with success state of the update step.
     */
    public boolean performConversionStep(final Connection aConn) {
        boolean lError = false;
        try {
            // 1. INSTRUMENTS.
            // Retrieve the instruments on the passed connection.
            String lQuery = "select validationid, status from validation";
            PreparedStatement lPreparedStatement = aConn.prepareStatement(lQuery);
            ResultSet rs = lPreparedStatement.executeQuery();

            // Iterate over the given instruments.
            int lValidationCounter = 0;
            while (rs.next()) {
                // Get the id and status for this validation.
                int lValidationID = rs.getInt(1);
                int lStatus = rs.getInt(2);

                // Get
                int lValidationTypeId = transformStatus(lStatus);

                PreparedStatement lStat = aConn.prepareStatement("UPDATE validation SET l_validationtypeid = ?, modificationdate = CURRENT_TIMESTAMP WHERE validationid = ?");
                lStat.setLong(1, lValidationTypeId);
                lStat.setLong(2, lValidationID);

                int result = lStat.executeUpdate();
                lValidationCounter++;
                lStat.close();
            }

            lPreparedStatement.close();
            rs.close();


            logger.info("ValidationStatusToValidationType dbconverter step has successfully updated the " +
                    "status information to validationtypes for " + lValidationCounter + " entries");


        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    /**
     * Transform the boolean status into ValidationTypes.
     *
     * @param aStatus
     * @return
     */
    private int transformStatus(int aStatus) {

        int lValidationTypeId;

        if (aStatus == 0) {
            lValidationTypeId = Validationtype.REJECT_AUTO;
        } else if (aStatus == 1) {
            lValidationTypeId = Validationtype.ACCEPT_AUTO;
        } else {
            throw new RuntimeException("Status should have come from a boolean variable!!\nSpecified status:\t" + aStatus);
        }

        return lValidationTypeId;
    }
}
