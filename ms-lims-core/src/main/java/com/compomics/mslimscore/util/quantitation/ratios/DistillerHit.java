/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import com.compomics.mslimscore.util.IdentificationExtension;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class DistillerHit {
	// Class specific log4j logger for DistillerHit instances.
	 private static Logger logger = Logger.getLogger(DistillerHit.class);
    /**
     * The hitnumber serves as an 'identifier' within Mascot Distiller.
     */
    private int iHitNumber;
    /**
     * The collection of DistillerPeptides of the DistillerHit instance.
     */
    private Vector<DistillerPeptide> iDistillerPeptides = new Vector<DistillerPeptide>();
    /**
     * The collection of DistillerRatioGroups of the DistillerHit instance.
     */
    private Vector<DistillerRatioGroup> iDistillerRatioGroups = new Vector<DistillerRatioGroup>();

    /**
     * Constructs a DistillerHit instance wherein DistillerPeptide and DistillerRatioGroup instances can be stored.
     * @param aHit The hit identifier of the Hit within Mascot distiller.
     */
    public DistillerHit(int aHit) {
        this.iHitNumber = aHit;
    }

    /**
     * Add a DistillerPeptide into the DistillerHit.
     * @param aDistillerPeptide The DistillerPeptide to be attached to the DistillerHit.
     */
    public void addDistillerPeptide(DistillerPeptide aDistillerPeptide){
        this.iDistillerPeptides.add(aDistillerPeptide);
    }

    /**
     * Add a DistillerRatioGroup into the DistillerHit.
     * @param aDistillerRatioGroup The DistillerRatioGroup to be attached to the DistillerHit.
     */
    public void addHitRatioGroup(DistillerRatioGroup aDistillerRatioGroup){
        this.iDistillerRatioGroups.add(aDistillerRatioGroup);
    }

    /**
     * This method ties up the instance DistillerRatioGroups with the given IdentificationExtension parameters.
     * @param aIdentifications The Identification instances to tied up to the instance DistillerRatioGroups.
     */
    public void matchIdentificationsToRatios(IdentificationExtension[] aIdentifications){
        for(int h = 0; h< iDistillerRatioGroups.size(); h ++){
            DistillerRatioGroup lRatioGroup = this.getDistillerRatioGroup(h);
            lRatioGroup.linkIdentificationsAndQueries(aIdentifications);
        }
    }

    /**
     * Returns the Hit number within the distiller quantitation file.
     * @return Integer The hitnumber
     */
    public int getDistillerHitNumber() {
        return iHitNumber;
    }

    /**
     * Returns the collection of DistillerPeptides of this DistillerHit.
     * @return Vector with DistillerPeptides.
     */
    public Vector<DistillerPeptide> getDistillerPeptides() {
        return iDistillerPeptides;
    }

    /**
     * Returns the collection of DistillerRatioGroups of this DistillerHit.
     * @return Vector with DistillerRatioGroups.
     */
    public Vector getDistillerRatioGroups() {
        return iDistillerRatioGroups;
    }

    /**
     * Returns the number of DistillerRatioGroups.
     * @return int
     */
    public int getNumberOfDisitillerRatioGroups(){
        return iDistillerRatioGroups.size();
    }

    /**
     * Returns the number of DistillerPeptides.
     * @return int
     */
    public int getNumberOfDistillerPeptides(){
        return iDistillerPeptides.size();
    }

    /**
     * Returns the DistillerRatioGroup at the given index (0-based!)
     * @param aIndex The index of the requested DistillerRatioGroup
     * @return The requested DistillerRatioGroup
     */
    public DistillerRatioGroup getDistillerRatioGroup(int aIndex) {
        return iDistillerRatioGroups.get(aIndex);
    }

}
