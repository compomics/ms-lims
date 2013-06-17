/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import com.compomics.mslimscore.util.interfaces.PeptideIdentification;
import com.compomics.mslimscore.util.interfaces.Ratio;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class RatioGroup {
	// Class specific log4j logger for RatioGroup instances.
	 private static Logger logger = Logger.getLogger(RatioGroup.class);
    /**
     * The List with Identification instances to be linked to a ratio.
     */
    protected ArrayList<PeptideIdentification> iIdentifications = new ArrayList<PeptideIdentification>();
    /**
     * The List with Peptide types to be linked to a ratio.
     * Note that this list is created in sync with iIdentifications.
     * Therefore, iIdentifications[0] is of type iPeptideTypes[0]
     */
    protected ArrayList<String> iPeptideTypes =  new ArrayList<String>();
    /**
     * the List with Ratio instances.
     */
    protected ArrayList<Ratio> iRatios = new ArrayList<Ratio>();
    /**
     * The unmodified peptide sequence of this group.
     */
    protected String iPeptideSequence;
    /**
     * A reference to the parent RatioGroupCollection wherefrom this RatioGroup is part of.
     */
    protected RatioGroupCollection iParentCollection = null;
    /**
     * A String[] with the different protein accessions that are linked to this peptide identification
     */
    private String[] iProteinAccessions;
    /**
     * Boolean, true if a filter selected this RatioGroup
     */
    private boolean iSelected = false;
    /**
     * The razor protein accession
     */
    private String iRazorProteinAccession;

    /** Constructs a new RatioGroup. */ // Empty constructor.
    // Use the setters
    public RatioGroup() {
    }

    public RatioGroup(final RatioGroupCollection aRatioGroupCollection) {
        iParentCollection = aRatioGroupCollection;
    }

    /**
     * Getter for property 'parentCollection'.
     *
     * @return Value for property 'parentCollection'.
     */
    public RatioGroupCollection getParentCollection() {
        return iParentCollection;
    }


    public PeptideIdentification getIdentification(int aIndex) {
        return iIdentifications.get(aIndex);
    }

    /**
     * Returns the Peptide Type at the given index in this group.
     * @param aIndex
     * @return
     */
    public String getPeptideType(int aIndex) {
        return iPeptideTypes.get(aIndex);
    }

    /**
     * Returns all Peptide Type
     * @return ArrayList with the different types
     */
    public ArrayList<String> getAllPeptideTypes() {
        return iPeptideTypes;
    }

    /**
     * sets all Peptide Type
     * @param aNewTypes ArrayList with the converted types
     */
    public void setAllPeptideTypes(ArrayList<String> aNewTypes) {
        iPeptideTypes = aNewTypes;
    }

    /**
     * Returns the
     * @param aIndex
     * @return
     */
    public Ratio getRatio(int aIndex) {
        return iRatios.get(aIndex);
    }

    /**
     * Getter for property 'numberOfIdentifications'.
     *
     * @return Value for property 'numberOfIdentifications'.
     */
    public int getNumberOfIdentifications(){
        return iIdentifications.size();
    }


    /**
     * Getter for property 'numberOfRatios'.
     *
     * @return Value for property 'numberOfRatios'.
     */
    public int getNumberOfRatios(){
        return iRatios.size();
    }

    /**
     * Getter for property 'numberOfTypes'.
     *
     * @return Value for property 'numberOfTypes'.
     */
    public int getNumberOfTypes(){
        return iPeptideTypes.size();
    }

    /**
     * Add a PeptideIdentification to the RatioGroup.
     * @param aIdentification The PeptideIdentification
     * @param aType The type of the given PeptideIdentification
     */
    public void addIdentification(PeptideIdentification aIdentification, String aType){
        aIdentification.setType(aType);
        iIdentifications.add(aIdentification);
        iPeptideTypes.add(aType);
    }

    /**
     * Add a ratio to this ratiogroup
     * @param aRatio
     */
    public void addRatio(Ratio aRatio){
        iRatios.add(aRatio);
    }

    /**
     * Get a ratio by ratio type (L/H, ...)
     * @param aType a ratio type
     * @return Ratio
     */
    public Ratio getRatioByType(String aType){
        Ratio lRatio = null;
        for(int i = 0; i<this.getNumberOfRatios(); i++){
            if(aType.equalsIgnoreCase(this.getRatio(i).getType())){
                lRatio = this.getRatio(i);
            }
        }
        return lRatio;
    }

    /**
     * Getter for property 'peptideSequence'.
     *
     * @return Value for property 'peptideSequence'.
     */
    public String getPeptideSequence() {
        return iPeptideSequence;
    }

    /**
     * Setter for property 'peptideSequence'.
     *
     * @param aPeptideSequence Value to set for property 'peptideSequence'.
     */
    public void setPeptideSequence(final String aPeptideSequence) {
        iPeptideSequence = aPeptideSequence;
    }

    /**
     * Returns the PeptideIdentification for the given Type.
     * @param aType The type of the PeptideIdentification
     * @return The requested PeptideIdentification. <br><b>null if no match!</b>
     */
    public PeptideIdentification getIdentificationForType(String aType){
        // Iterate over all the types of the RatioGroup.
        for (int i = 0; i < iPeptideTypes.size(); i++) {
            String s = iPeptideTypes.get(i);
            if(s.equals(aType)){
                // Return the Identification that matches the type parameter.
                return iIdentifications.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the PeptideIdentification for the given Type.
     * @param aType The type of the PeptideIdentification
     * @return A vector with the requested PeptideIdentifications. <br><b>null if no match!</b>
     */
    public Vector<PeptideIdentification> getIdentificationsForType(String aType){
        Vector<PeptideIdentification> lResult = null;
        // Iterate over all the types of the RatioGroup.
        for (int i = 0; i < iPeptideTypes.size(); i++) {
            String s = iPeptideTypes.get(i);
            if(s.equals(aType)){
                // Return the Identification that matches the type parameter.
                if(lResult == null){
                    lResult = new Vector<PeptideIdentification>();
                }
                lResult.add(iIdentifications.get(i));
            }
        }
        return lResult;
    }

    /**
     * This method gets the protein accessions (also isoforms) linked to the identifications.
     * @return String[] with the different protein accessions
     */
    public String[] getProteinAccessions(){
        if(iProteinAccessions == null){
            //create an vector to store the accessions in
            Vector<String> lAccessionVector = new Vector<String>();
            for(int i = 0; i<iIdentifications.size(); i++){
                //find the proteins for every identification linked to this ratio group
                if(iIdentifications.get(i) != null){
                    String lProtein = iIdentifications.get(i).getAccession();
                    String lIsoforms = iIdentifications.get(i).getIsoforms();
                    //check if the protein is already found
                    boolean lNewProtein = true;
                    for(int j = 0; j<lAccessionVector.size(); j ++){
                        if(lAccessionVector.get(j).equalsIgnoreCase(lProtein)){
                            lNewProtein = false;
                        }
                    }
                    if(lNewProtein){
                        lAccessionVector.add(lProtein);
                    }
                    //check for every isoform if the isoform is already found
                    if(lIsoforms.length() > 0){

                        lIsoforms = lIsoforms.replace("^A",",");
                        String[] lIsoformsFound = lIsoforms.split(",");
                        for(int j = 0; j<lIsoformsFound.length; j++){
                            String lIsoform = lIsoformsFound[j];
                            if(lIsoform.length() > 0){
                                lIsoform = lIsoform.substring(0,lIsoform.indexOf(" "));
                                boolean lNewIsoform = true;
                                for(int k = 0; k<lAccessionVector.size(); k ++){
                                    if(lAccessionVector.get(k).equalsIgnoreCase(lIsoform)){
                                        lNewIsoform = false;
                                    }
                                }
                                if(lNewIsoform){
                                    lAccessionVector.add(lIsoform);
                                }
                            }
                        }
                    }
                }
            }
            String[] lProteins = new String[lAccessionVector.size()];
            lAccessionVector.toArray(lProteins);
            iProteinAccessions = lProteins;
        }
        return iProteinAccessions;
    }

    /**
     * This method will return all the protein accessions linked to this ratio group as one string.
     * The accessions are seperated by ", ".
     *
     * @return String with the protein accessions seperated by ", "
     */
    public String getProteinAccessionsAsString(){
        String[] lProteinAccessions = this.getProteinAccessions();
        String lAccessions = lProteinAccessions[0];
        for(int i = 1; i<lProteinAccessions.length; i ++){
            lAccessions = lAccessions + ", " + lProteinAccessions[i];
        }
        return lAccessions;
    }

    /**
     * Getter for property 'iSelected'.
     *
     * @return Value for property 'iSelected'.
     */
    public boolean isSelected() {
        return iSelected;
    }

    /**
     * Setter for property 'iSelected'.
     * @param aSelected
     */
    public void setSelected(boolean aSelected) {
        this.iSelected = aSelected;
    }

    /**
     * Getter for the razor protein accession
     * @return String with the razor protein accession
     */
    public String getRazorProteinAccession() {
        return iRazorProteinAccession;
    }

    /**
     * Setter for the razor protein accession
     * @param aRazorAccession String with the accession to set
     */
    public void setRazorProteinAccession(String aRazorAccession) {
        this.iRazorProteinAccession = aRazorAccession;
    }

     public void deleteRatio(Ratio lRatio) {
        iRatios.remove(lRatio);
    }


    public double getSummedIntensityForRatioType(String lType){
        return 0.0;
    }

    public double getIntensityForComponent(String lComponent){
        return 0.0;
    }

}

