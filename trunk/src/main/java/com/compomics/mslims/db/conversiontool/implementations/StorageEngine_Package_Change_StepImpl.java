package com.compomics.mslims.db.conversiontool.implementations;

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
public class StorageEngine_Package_Change_StepImpl implements DBConverterStep {

    public StorageEngine_Package_Change_StepImpl() {
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
                String lNewStorageClassName = lOldStorageClassName.replace("be.proteomics.lims", "com.compomics.mslims");
                lInstrument.setStorageclassname(lNewStorageClassName);
                int lUpdatedRows = lInstrument.update(aConn);
                lInstrumentCounter = lInstrumentCounter + lUpdatedRows;
            }

            System.out.println(
                    "StorageEngine_Package_Change dbconverter step has successfully updated the " +
                            "classname of the StorageEngine in " + lInstrumentCounter + " instruments");


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return false;
    }
}
