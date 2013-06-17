/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class RatioType {
	// Class specific log4j logger for RatioType instances.
	 private static Logger logger = Logger.getLogger(RatioType.class);

    private String iType;
    private String[] iComponents;
    private String iUnregulatedComponent;
    private Vector<String> iUnregulatedComponentsBySet = new Vector<String>();
    private double iMedian;

    public RatioType(String iType, String[] iComponents, String aUnregulatedComponent, double aMedian) {
        this.iType = iType;
        this.iComponents = iComponents;
        this.iUnregulatedComponent = aUnregulatedComponent;
        this.iUnregulatedComponentsBySet.add(aUnregulatedComponent);
        this.iMedian =  aMedian;
    }


    public String getType() {
        return iType;
    }

    public String[] getComponents() {
        return iComponents;
    }

    public String getUnregulatedComponent() {
        return iUnregulatedComponent;
    }

    public void addUnregulatedComponentForSet(String lUnregulatedComponent) {
        iUnregulatedComponentsBySet.add(lUnregulatedComponent);
    }

    public Vector<String> getUnregulatedComponentsBySet() {
        return iUnregulatedComponentsBySet;
    }

    public double getMedian() {
        return iMedian;
    }
}

