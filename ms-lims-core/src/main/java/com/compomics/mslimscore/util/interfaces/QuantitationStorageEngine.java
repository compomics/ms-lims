package com.compomics.mslimscore.util.interfaces;


import com.compomics.mslimscore.util.quantitation.ratios.RatioGroupCollection;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 25-nov-2008 Time: 13:13:36 The 'QuantitationStorageEngine ' interface
 * requires implementations to be able to store a RatioGroupCollection into the ms_lims system.
 */
public interface QuantitationStorageEngine {
    /**
     * Store the including RatioGroups from the collection into the ms_lims system.
     *
     * @param aRatioGroupCollection The collection with RatioGroups to be stored.
     * @return boolean with success or failure of the storage.
     * @throws IOException  Throws an input-output error while packing and persisting the data.
     * @throws SQLException Throws an sql error if storage goes wrong.
     */
    boolean storeQuantitation(RatioGroupCollection aRatioGroupCollection) throws IOException, SQLException;
}
