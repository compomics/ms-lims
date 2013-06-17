/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.enumeration;

/**
 *
 * @author Davy
 */
public enum QuantitationMetaType {
    /**
     * The filename with the raw information of a RatioGroupCollection.
     */
    FILENAME("Filename"),

    /**
     * The runname of the RatioGroupCollection.
     */
    RUNNAME("Runname"),

    /**
     * The runname of the RatioGroupCollection.
     */
    MASCOTTASKID("MascotTaskId"),

    /**
     * The datfileid
     */
    DATFILEID("Datfileid");


    private String iName;

    private QuantitationMetaType(String aName) {
        iName = aName;
    }


    /**
     * Returns a name for the enumeration type.
     * @return String for the type.
     */
    public String toString() {
        return iName;
    }
}
