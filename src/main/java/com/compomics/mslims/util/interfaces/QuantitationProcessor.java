package com.compomics.mslims.util.interfaces;

import org.apache.log4j.Logger;

import com.compomics.rover.general.quantitation.RatioGroupCollection;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 24-nov-2008 Time: 14:28:13 The 'QuantitationProcessor ' class was created
 * for separate processing different quantitation formats into RatioGroupCollections.
 */
public interface QuantitationProcessor {
    /**
     * Returns whether more RatioGroupCollections are left.
     *
     * @return true if more, false if else.
     */
    boolean hasNext();

    /**
     * Returns the next RatioGroupCollection.
     *
     * @return The next RatioGroupCollection.
     */
    RatioGroupCollection next();
}
