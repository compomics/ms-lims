package com.compomics.mslims.db.conversiontool.implementations;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Instrument;
import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA. User: kenny Date: Feb 25, 2010 Time: 11:15:49 AM
 * <p/>
 * This class
 */
public class Package_Change_StepImpl implements DBConverterStep {
    // Class specific log4j logger for Package_Change_StepImpl instances.
    private static Logger logger = Logger.getLogger(Package_Change_StepImpl.class);

    public Package_Change_StepImpl() {
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
        try {
            // Retrieve the instruments on the passed connection.
            String lQuery = "select * from instrument";
            PreparedStatement lPreparedStatement = aConn.prepareStatement(lQuery);
            ResultSet rs = lPreparedStatement.executeQuery();

            // Iterate over the given instruments.
            int lInstrumentCounter = 0;
            while (rs.next()) {
                lInstrumentCounter++;
                Instrument lInstrument = new Instrument(rs);
                String lOldStorageClassName = lInstrument.getStorageclassname();
                try {
                    if (lOldStorageClassName != null) {
                        String lNewStorageClassName = lOldStorageClassName.replace("be.proteomics.lims", "com.compomics.mslims");
                        lInstrument.setStorageclassname(lNewStorageClassName);
                        lInstrument.update(aConn);
                        lInstrumentCounter++;
                    }
                } catch (NullPointerException npe) {
                    // Do nothing. The if condition above throws a nullpointer when lOldStorageClassName is null..
                }
            }

            logger.info("StorageEngine_Package_Change dbconverter step has successfully updated the " +
                    "classname of the StorageEngine in " + lInstrumentCounter + " instruments");


        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }


        return false;
    }
}
