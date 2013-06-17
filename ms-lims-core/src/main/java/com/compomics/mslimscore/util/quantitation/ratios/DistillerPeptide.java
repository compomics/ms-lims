/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class DistillerPeptide {
	// Class specific log4j logger for DistillerPeptide instances.
	 private static Logger logger = Logger.getLogger(DistillerPeptide.class);

    /**
     * The QueryNumber in the Mascot results file.
     */
    private int iQuery;
    /**
     * The rank in the Mascot results file.
     */
    private int iRank;
    /**
     * The Peptide sequence.
     */
    private String iSequence;
    /**
     * A numerical String indicating the modifications.
     */
    private String iVariableModifications;

    /**
     * The composition of the peptide in regard to the quantitation. (ex: Heavy, Light, ..)
     */
    private String iComposition;
    /**
     * The valid status of the peptide as analyzed by Distiller.
     * If false, Mascot Distiller judged the quantitation as 'dodgy'.
     */
    private boolean iValid;
    /**
     * The calculated mass of the (modified!) petpide.
     */
    private double iCallMass;

    /**
     * The observed mass of the peptide.
     */
    private double iObsMass;

    /**
     * The theoretical versus experimental mass error.
     */
    private double iDeltalMass;
    /**
     * The Mascot ionscore of the peptide.
     */
    private double iScore;
    /**
     * The peptide type
     */
    private String iType;
    /**
     * The peptide varModsString.
     */
    private String iVarMods;

    public DistillerPeptide(int aQuery, int aRank, String aSequence, String aVarMods, String aComposition, String aStatus) {
        this.iQuery = aQuery;
        this.iRank = aRank;
        this.iSequence = aSequence;
        this.iVariableModifications = aVarMods;
        this.iComposition = aComposition;
        this.iValid = Boolean.parseBoolean(aStatus);
    }

    /**
     * Setter for property 'callMass'.
     *
     * @param iCallMass Value to set for property 'callMass'.
     */
    public void setCallMass(double iCallMass) {
        this.iCallMass = iCallMass;
    }

    /**
     * Setter for property 'obsMass'.
     *
     * @param iObsMass Value to set for property 'obsMass'.
     */
    public void setObsMass(double iObsMass) {
        this.iObsMass = iObsMass;
    }

    /**
     * Setter for property 'deltalMass'.
     *
     * @param iDeltalMass Value to set for property 'deltalMass'.
     */
    public void setDeltalMass(double iDeltalMass) {
        this.iDeltalMass = iDeltalMass;
    }

    /**
     * Setter for property 'score'.
     *
     * @param iScore Value to set for property 'score'.
     */
    public void setScore(double iScore) {
        this.iScore = iScore;
    }

    /**
     * Setter for property 'iType'.
     *
     * @param aType Value to set for property 'iType'.
     */
    public void setType(String aType) {
        this.iType = aType;
    }

    /**
     * Setter for property 'iVarMods'.
     *
     * @param aVarMods Value to set for property 'iVarMods'.
     */
    public void setVarMods(String aVarMods) {
        this.iVarMods = aVarMods;
    }

    //getters

    /**
     * Getter for property 'query'.
     *
     * @return Value for property 'query'.
     */
    public int getQuery() {
        return iQuery;
    }

    /**
     * Getter for property 'rank'.
     *
     * @return Value for property 'rank'.
     */
    public int getRank() {
        return iRank;
    }

    /**
     * Getter for property 'sequence'.
     *
     * @return Value for property 'sequence'.
     */
    public String getSequence() {
        return iSequence;
    }

    /**
     * Getter for property 'iVarMods'.
     *
     * @return Value for property 'iVarMods'.
     */
    public String getVarMods() {
        return iVarMods;
    }

    /**
     * Getter for property 'type'.
     *
     * @return Value for property 'type'.
     */
    public String getType() {
        return iType;
    }

    /**
     * Getter for property 'variableModifications'.
     *
     * @return Value for property 'variableModifications'.
     */
    public String getVariableModifications() {
        return iVariableModifications;
    }

    /**
     * Getter for property 'composition'.
     *
     * @return Value for property 'composition'.
     */
    public String getComposition() {
        return iComposition;
    }

    /**
     * Getter for property 'valid'.
     *
     * @return Value for property 'valid'.
     */
    public boolean getValid() {
        return iValid;
    }

    /**
     * Getter for property 'callMass'.
     *
     * @return Value for property 'callMass'.
     */
    public double getCallMass() {
        return iCallMass;
    }

    /**
     * Getter for property 'obsMass'.
     *
     * @return Value for property 'obsMass'.
     */
    public double getObsMass() {
        return iObsMass;
    }

    /**
     * Getter for property 'deltalMass'.
     *
     * @return Value for property 'deltalMass'.
     */
    public double getDeltalMass() {
        return iDeltalMass;
    }

    /**
     * Getter for property 'score'.
     *
     * @return Value for property 'score'.
     */
    public double getScore() {
        return iScore;
    }

    /**
     * Getter for property 'charge'.
     *
     * @return Value for property 'charge'.
     */
    public int getCharge(){
        int charge = 0;
        charge = (int) Math.floor(iCallMass / iObsMass);
        return charge + 1;
    }
}
