/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import com.compomics.mslimscore.util.enumeration.QuantitationSource;
import com.compomics.mslimscore.util.interfaces.Ratio;
import com.compomics.mslimscore.util.quantitation.QuantitativeValidationSingleton;
import java.util.Vector;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class QuantitativePeptideGroup {
	// Class specific log4j logger for QuantitativePeptideGroup instances.
	 private static Logger logger = Logger.getLogger(QuantitativePeptideGroup.class);

    /**
     * The peptide sequence of this peptide group
     */
    private String iSequence;
    /**
     * Boolean that indicates the status if this peptide group is wanted in the ratio calculation
     */
    private boolean iUsedInCalculations;
    /**
     * Boolean that indicates if it is collapsed or visible
     */
    private boolean iCollapsed;
    /**
     * Vector with ratio groups linked to this protein
     */
    private Vector<RatioGroup> iRatioGroups = new Vector<RatioGroup>();
    /**
     * This quantitative validation singelton holds information for the calculation of the ratio
     */
    private QuantitativeValidationSingleton iQuantitativeValidationSingelton = QuantitativeValidationSingleton.getInstance();
    /**
     * The amino acid before this sequence
     */
    private String iPreSequence = null;
    /**
     * The amino acid after this sequence
     */
    private String iPostSequence = null;
    /**
     * The start position of the peptide in the protein
     */
    private int iStartPosition;
    /**
     * The end position of the peptide in the protein
     */
    private int iEndPosition;
    /**
     * String that indicates the razor accession of the protein linked to this peptide group
     */
    private String iRazorAccession = null;
    /**
     * boolean that indicates if the peptides are linked to more than one protein
     */
    private Boolean iMultiLink = null;


    /**
     * Constructor
     * @param aSequence The peptide sequence of this Peptide Group
     * @param aUsed boolean that indicates if this peptide group is used in the calculations of the ratio
     * @param aCollapsed boolean that indicates if this peptide group is collapsed or visible
     */
    public QuantitativePeptideGroup(String aSequence, boolean aUsed, boolean aCollapsed){
        //set the sequence
        this.iSequence = aSequence;
        //set the booleans
        this.iUsedInCalculations = aUsed;
        this.iCollapsed = aCollapsed;
    }

    /**
     * This method adds a RatioGroup to the RatioGroups Vector
     * @param aRatioGroup RatioGroup to add
     */
    public void addRatioGroup(RatioGroup aRatioGroup){
        if(!iRatioGroups.contains(aRatioGroup)){
            //it's not added, so add it
            iRatioGroups.add(aRatioGroup);
        }
    }

    /**
     * Setter for the iUsedInCalculations parameter
     * @param aUsed boolean
     */
    public void setUsedInCalculation(boolean aUsed){
        iUsedInCalculations = aUsed;
    }

/**
     * Setter for the iCollapsed parameter
     * @param aColl boolean
     */
    public void setCollapsed(boolean aColl){
        iCollapsed = aColl;
    }




    /**
     * Getter for the peptide sequence
     * @return String with the sequence
     */
    public String getSequence() {
        return iSequence;
    }

    /**
     * Getter for the full peptide sequence
     * @return String with the full sequence
     */
    public String getFullSequence(){
        String lResult = "";
        if(iPreSequence != null){
            lResult = iPreSequence + ".";
        }
        lResult = lResult + iSequence;
        if(iPostSequence != null){
            lResult = lResult + "." + iPostSequence;
        }
        return lResult;
    }

    /**
     * Getter for the is used in ratio calculations boolean
     * @return boolean
     */
    public boolean isUsedInCalculations() {
        return iUsedInCalculations;
    }


    /**
     * Getter for the post sequence
     * @return String with the post sequence
     */
    public String getPostSequence() {
        if(iPostSequence == null){
            return "-";
        }
        return iPostSequence;
    }

    /**
     * Getter for the pre sequence
     * @return String with the pre sequence
     */
    public String getPreSequence() {
        if(iPreSequence ==  null){
            return "-";
        }
        return iPreSequence;
    }

    /**
     * Getter for the is collapsed boolean
     * @return boolean
     */
    public boolean isCollapsed() {
        return iCollapsed;
    }

    /**
     * Getter for the vector with the RatioGroups
     * @return Vector<RatioGroup>
     */
    public Vector<RatioGroup> getRatioGroups() {
        return iRatioGroups;
    }

    /**
     * Getter for the ratio mean of the different Ratiogroups for a specific ratio type.
     * The log2 status and the useOnlyValidRatio status from the iQuantitativeValidationSingelton will be used to do the calculations
     * @param aType String with the specific ratio type
     * @return double with the ratio mean
     */
    public Double getMeanRatioForGroup(String aType){
        int lCounter = 0;
        double lSum = 0.0;
        for(int i = 0; i<iRatioGroups.size(); i ++){
            Ratio lRatio = iRatioGroups.get(i).getRatioByType(aType);
            if(lRatio != null){
                if (iQuantitativeValidationSingelton.isUseOnlyValidRatioForProteinMean()) {
                    if (lRatio.getValid()) {
                        lSum = lSum + lRatio.getRatio(iQuantitativeValidationSingelton.isLog2());
                        lCounter = lCounter + 1;
                    }
                } else {
                    lSum = lSum + lRatio.getRatio(iQuantitativeValidationSingelton.isLog2());
                    lCounter = lCounter + 1;
                }
            }
        }
        Double lResult = lSum / (double)lCounter;
        if(lCounter == 0){
            return null;
        }
        if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && this.getRatioGroups().get(0).getProteinAccessions().length != 1){
            return null;
        }
        return Math.round(lResult*10000.0)/10000.0;
    }

    /**
     * This method will give all the ratios for a specific type.
     * The log2 status and the useOnlyValidRatio status from the iQuantitativeValidationSingelton will be used to do the calculations
     * @param aType String with the specific ratio type
     * @return Vector<Double> with all the ratios
     */
    public Vector<Double> getRatioValuesForType(String aType){
        return this.getRatioValuesForType(aType,iQuantitativeValidationSingelton.isLog2(), -1);
    }

    /**
     * This method will give all the ratios for a specific type.
     * The log2 status and the useOnlyValidRatio status from the iQuantitativeValidationSingelton will be used to do the calculations
     * @param aType String with the specific ratio type
     * @param aIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @return Vector<Double> with all the ratios
     */
    public Vector<Double> getRatioValuesForType(String aType, int aIndex){
        return this.getRatioValuesForType(aType,iQuantitativeValidationSingelton.isLog2(), aIndex);
    }


    /**
     * This method will give all the ratios for a specific type.
     * The log2 status and the useOnlyValidRatio status from the iQuantitativeValidationSingelton will be used to do the calculations
     * @param aType String with the specific ratio type
     * @param isLog2 Boolean that indicates if we have to use log 2 values
     * @param aIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @return Vector<Double> with all the ratios
     */
    public Vector<Double> getRatioValuesForType(String aType, boolean isLog2, int aIndex){
        Vector<Double> lResult = new Vector<Double>();
        for(int i = 0; i<iRatioGroups.size(); i ++){
            Ratio lRatio = iRatioGroups.get(i).getRatioByType(aType);
            if(lRatio != null){
                boolean lUse = true;
                if(aIndex >= 0){
                    //an index is specified
                    if(lRatio.getParentRatioGroup().getParentCollection().getIndex() != aIndex){
                        //the ratio is from an unwanted source
                        lUse = false;
                    }
                }
                if(lUse){
                    if (iQuantitativeValidationSingelton.isUseOnlyValidRatioForProteinMean()) {
                        if (lRatio.getValid()) {
                            if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && iRatioGroups.get(i).getProteinAccessions().length != 1){
                                //lResult.add(Double.NaN);
                            } else {
                                lResult.add(lRatio.getRatio(isLog2));
                            }
                        }
                    } else {
                        if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && iRatioGroups.get(i).getProteinAccessions().length != 1){
                            //lResult.add(Double.NaN);
                        } else {
                            lResult.add(lRatio.getRatio(isLog2));
                        }
                    }
                }
            }
        }
        return lResult;
    }

    /**
     * This method will give all the ratios for a specific type.
     * The log2 status and the useOnlyValidRatio status from the iQuantitativeValidationSingelton will be used to do the calculations
     * @param aType String with the specific ratio type
     * @param isLog2 Boolean that indicates if we have to use log 2 values
     * @param aIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @return Vector<Double> with all the ratios
     */
    public Vector<Ratio> getRatiosForType(String aType, boolean isLog2, int aIndex){
        Vector<Ratio> lResult = new Vector<Ratio>();
        for(int i = 0; i<iRatioGroups.size(); i ++){
            Ratio lRatio = iRatioGroups.get(i).getRatioByType(aType);
            if(lRatio != null){
                boolean lUse = true;
                if(aIndex >= 0){
                    //an index is specified
                    if(lRatio.getParentRatioGroup().getParentCollection().getIndex() != aIndex){
                        //the ratio is from an unwanted source
                        lUse = false;
                    }
                }
                if(lUse){
                    if (iQuantitativeValidationSingelton.isUseOnlyValidRatioForProteinMean()) {
                        if (lRatio.getValid()) {
                            if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && iRatioGroups.get(i).getProteinAccessions().length != 1){
                                //lResult.add(Double.NaN);
                            } else {
                                lResult.add(lRatio);
                            }
                        }
                    } else {
                        if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && iRatioGroups.get(i).getProteinAccessions().length != 1){
                            //lResult.add(Double.NaN);
                        } else {
                            lResult.add(lRatio);
                        }
                    }
                }
            }
        }
        return lResult;
    }

    /**
     * This method will give all the summed intensities for one ratio type
     *
     * @param aType String with the specific ratio type
     * @param isLog2 Boolean that indicates if we have to use log 2 values
     * @param aIndex Int that indicates the index of a specific source, if this int is -1 all the sources will be used
     * @return Vector<Double> with all the intensities
     */
    public Vector<Double> getIntensitiesForType(String aType, boolean isLog2, int aIndex){
        Vector<Double> lResult = new Vector<Double>();
        for(int i = 0; i<iRatioGroups.size(); i ++){
            Ratio lRatio = iRatioGroups.get(i).getRatioByType(aType);
            if(lRatio != null){
                boolean lUse = true;
                if(aIndex >= 0){
                    //an index is specified
                    if(lRatio.getParentRatioGroup().getParentCollection().getIndex() != aIndex){
                        //the ratio is from an unwanted source
                        lUse = false;
                    }
                }
                if(lUse){
                    if (iQuantitativeValidationSingelton.isUseOnlyValidRatioForProteinMean()) {
                        if (lRatio.getValid()) {
                            if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && iRatioGroups.get(i).getProteinAccessions().length != 1){
                                //lResult.add(Double.NaN);
                            } else {
                                if(lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.DISTILLER_QUANT_TOOLBOX_ROV || lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.DISTILLER_QUANT_TOOLBOX_MS_LIMS){
                                    DistillerRatioGroup lRatioGroup = (DistillerRatioGroup) lRatio.getParentRatioGroup();
                                    lResult.add(lRatioGroup.getSummedIntensityForRatioType(aType));
                                } else if(lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.MAX_QUANT_NO_SIGN || lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.MAX_QUANT || lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.MAX_QUANT_MS_LIMS){
                                    MaxQuantRatioGroup lRatioGroup = (MaxQuantRatioGroup) lRatio.getParentRatioGroup();
                                    lResult.add(lRatioGroup.getSummedIntensityForRatioType(aType));
                                } else {
                                    lResult.add(0.0);
                                }
                            }
                        }
                    } else {
                        if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && iRatioGroups.get(i).getProteinAccessions().length != 1){
                            //lResult.add(Double.NaN);
                        } else {
                            if(lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.DISTILLER_QUANT_TOOLBOX_ROV || lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.DISTILLER_QUANT_TOOLBOX_MS_LIMS){
                                DistillerRatioGroup lRatioGroup = (DistillerRatioGroup) lRatio.getParentRatioGroup();
                                lResult.add(lRatioGroup.getSummedIntensityForRatioType(aType));
                            } else if(lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.MAX_QUANT_NO_SIGN || lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.MAX_QUANT || lRatio.getParentRatioGroup().getParentCollection().getQuantitationSource() == QuantitationSource.MAX_QUANT_MS_LIMS){
                                MaxQuantRatioGroup lRatioGroup = (MaxQuantRatioGroup) lRatio.getParentRatioGroup();
                                lResult.add(lRatioGroup.getSummedIntensityForRatioType(aType));
                            } else {
                                lResult.add(0.0);
                            }
                        }
                    }
                }
            }
        }
        return lResult;
    }




    /**
     * Check if the ratiogroups of this PeptideGroup can be linked to more than one protein
     * @return boolean
     */
    public boolean isLinkedToMoreProteins(){
        if(iMultiLink == null){
            boolean lMultiLink = false;
            for(int i = 0; i<iRatioGroups.size(); i ++){
                if(iRatioGroups.get(i).getProteinAccessions().length > 1){
                    lMultiLink = true;
                }
            }
            iMultiLink = lMultiLink;
        }
        return iMultiLink;
    }

    /**
     * Get the razor protein accession linked to this peptide group
     * @return String with the razor protein accession
     */
    public String getRazorAccession(){
        if(iRazorAccession == null){
            Vector<String> lAccessions = new Vector<String>();
            Vector<Integer> lCounts = new Vector<Integer>();
            for(int i = 0; i<iRatioGroups.size(); i ++){
                String lRazor = iRatioGroups.get(i).getRazorProteinAccession().trim();
                boolean lFound = false;
                int lIndex = 0;
                for(int j = 0; j<lAccessions.size(); j ++){
                    if(lAccessions.get(j).equalsIgnoreCase(lRazor)){
                        lFound =  true;
                        lIndex = j;
                    }
                }

                if(lFound){
                    lCounts.set(lIndex, lCounts.get(lIndex) + 1);
                } else {
                    lAccessions.add(lRazor);
                    lCounts.add(1);
                }
            }

            int lIndex = 0;
            int lMax = 0;
            for(int i = 0; i<lCounts.size(); i ++){
                if(lCounts.get(i) > lMax){
                    lIndex = i;
                    lMax = lCounts.get(i);
                }
            }
            iRazorAccession = lAccessions.get(lIndex);
        }
        return iRazorAccession;
    }

    /**
     * Getter for the protein accessions linked to this RatioGroup of this peptideGroup
     * @return String with the protein accessions
     */
    public String getProteinsLinkedToGroupAsString(){
        String lAccession = "";
        Vector<String> lAccessions = new Vector<String>();
        for(int i = 0; i<iRatioGroups.size(); i ++){
            String[] lAccesssionsRatioGroup = iRatioGroups.get(i).getProteinAccessions();
            for(int j = 0; j<lAccesssionsRatioGroup.length; j ++){

                boolean lFound = false;
                for(int k = 0; k<lAccessions.size(); k ++){
                    if(lAccessions.get(k).equalsIgnoreCase(lAccesssionsRatioGroup[j])){
                        lFound =  true;
                    }
                }

                if(!lFound){
                    lAccessions.add(lAccesssionsRatioGroup[j]);
                    if(lAccession.length() > 0){
                        lAccession = lAccession + ", " +  lAccesssionsRatioGroup[j];
                    } else {
                        lAccession = lAccesssionsRatioGroup[j];
                    }
                }
            }
        }
        return lAccession;
    }

    /**
     * Calculates the number of valid ratios for a specific ratio type
     * @param lType String with the ratio type
     * @return int with the number of valid ratios for the type
     */
    public int getNumberOfValidRatiosForType(String lType) {
        int lCounter = 0;
        for(int i = 0; i<iRatioGroups.size(); i ++){
            if(iRatioGroups.get(i).getRatioByType(lType) != null){
                if(iRatioGroups.get(i).getRatioByType(lType).getValid()){
                    lCounter = lCounter + 1;
                }
            }
        }
        return lCounter; 
    }

    /**
     * Getter for the peptide start position
     * @return int with the start position
     */
    public int getStartPosition() {
        return iStartPosition;
    }

    /**
     * Setter for the peptide start position
     * @param aStartPosition int with the start position
     */
    public void setStartPosition(int aStartPosition) {
        this.iStartPosition = aStartPosition;
    }

    /**
     * Getter for the peptide end position
     * @return int with the end position
     */
    public int getEndPosition() {
        return iEndPosition;
    }

    /**
     * Setter for the peptide end position
     * @param aEndPosition int with the end position
     */
    public void setEndPosition(int aEndPosition) {
        this.iEndPosition = aEndPosition;
    }

    /**
     * Setter for the pre sequence amino acid string
     * @param aPreSequence
     */
    public void setPreSequence(String aPreSequence) {
        this.iPreSequence = aPreSequence;
    }

    /**
     * Setter for the post sequence amino acid string
     * @param aPostSequence
     */
    public void setPostSequence(String aPostSequence) {
        this.iPostSequence = aPostSequence;
    }

    /**
     * This method calculates the standard deviation for the ratios from this peptide group linked to a specific ratio
     * @param aType String with the ratio type
     * @return double with the calculated SD
     */
    public double getSDForGroup(String aType) {
        DescriptiveStatistics lStat = new DescriptiveStatistics();
        for(int i = 0; i<iRatioGroups.size(); i ++){
            Ratio lRatio = iRatioGroups.get(i).getRatioByType(aType);
            if(lRatio != null){
                if (iQuantitativeValidationSingelton.isUseOnlyValidRatioForProteinMean()) {
                    if (lRatio.getValid()) {
                        lStat.addValue(lRatio.getRatio(true));
                    }
                } else {
                    lStat.addValue(lRatio.getRatio(true));
                }
            }
        }
        Double lResult = lStat.getStandardDeviation();
        if(iQuantitativeValidationSingelton.isUseOnlyUniqueRatioForProteinMean() && this.getRatioGroups().get(0).getProteinAccessions().length != 1){
            return 0.0;
        }
        return Math.round(lResult*10000.0)/10000.0;
    }

}
