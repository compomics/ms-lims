/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation.ratios;

import com.compomics.mslimscore.util.QuantitationExtension;
import com.compomics.mslimscore.util.interfaces.Ratio;
import com.compomics.mslimscore.util.quantitation.QuantitativeValidationSingleton;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class DistillerRatio implements Ratio {
	// Class specific log4j logger for DistillerRatio instances.
	 private static Logger logger = Logger.getLogger(DistillerRatio.class);

    /**
     * The ratio itself.
     */
    private double iRatio;
    /**
     * The type of the Ratio.
     * ex: The type 'L/H' shows a ratio between a 'Light' and 'Heavy' component.
     */
    private String iType;
    /**
     * The quality measure for the DistillerRatio.
     * ex: 'standard errror'
     */
    private double iQuality;
    /**
     * The valid status of the Ratio by MascotDistiller.
     */
    private boolean iValid;
    /**
     * The different not valid states by MascotDistiller.
     */
    private ArrayList<String> iStates = new ArrayList<String>();
    /**
     * The different not valid extra infos by MascotDistiller.
     */
    private ArrayList<String> iInfos = new ArrayList<String>();
    /**
     * The quantitation stored in the database and linked to this DistillerRatio.
     */
    private QuantitationExtension iQuantitationStoredInDb = null;
    /**
     * A comment on the valid status of the ratio
     */
    private String iComment;
    /**
     * This distiller validation singelton holds information for the calculation of the ratio
     */
    private QuantitativeValidationSingleton iQuantitativeValidationSingelton = QuantitativeValidationSingleton.getInstance();
    /**
     * The Parent DistillerRatioGroup for this ratio
     */
    private DistillerRatioGroup iParentRatioGroup;
    /**
     * The original ratio
     */
    private double iOriginalRatio;
    /**
     * The number of the part in the list of the intensity sorted ratios
     */
    private int iPartNumber = 0;
    /**
     * The MAD before normalization
     */
    private double iPreNormMAD;
    /**
     * The MAD after normalization
     */
    private double iNormMAD;
    /**
     * The number of times the ratio is updated
     */
    private int iUpdates = 0;
    /**
     * The index of the data source
     */
    private double iIndex = -1.0;
    /**
     * The original source index
     */
    private double iOriginalIndex = -1.0;
    /**
     * boolean that indicates if the ratio was inverted
     */
    private boolean iInverted = false;


    /**
     * Constructs a new DistillerRatio instance.
     *
     * @param aRatio   The Ratio measurement
     * @param aType    The ratio type
     * @param aQuality The quality measure for the ratio
     * @param aValid   The valid status for this ratio
     * @param aParentRatioGroup The parent RatioGroup
     */
    public DistillerRatio(Double aRatio, String aType, double aQuality, String aValid, DistillerRatioGroup aParentRatioGroup) {
        this.iRatio = aRatio;
        this.iOriginalRatio = aRatio;
        this.iType = aType;
        this.iQuality = aQuality;
        this.iValid = Boolean.parseBoolean(aValid);
        this.iParentRatioGroup = aParentRatioGroup;
    }

    /**
     * {@inheritDoc}
     */
    public double getRatio(boolean aLog2Ratio) {
        double aRatio = iRatio;
        if(iQuantitativeValidationSingelton.isUseOriginalRatio()){
          aRatio = iOriginalRatio;
        }
        if (aLog2Ratio) {
          return Math.log(aRatio) / Math.log(2);
        } else {
          return aRatio;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return iType;
    }

    //getters

    /**
     * Getter for property 'quality'.
     *
     * @return Value for property 'quality'.
     */
    public double getQuality() {
        return iQuality;
    }


    /**
     * {@inheritDoc}
     */
    public boolean getValid() {
        return iValid;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "" + iRatio + "@" + iValid;
    }

    /**
     * This method stores the non valid reasons
     *
     * @param lState the non valid state
     * @param lInfo  the non valid extra info
     */
    public void addNonValidReason(String lState, String lInfo) {
        this.iStates.add(lState);
        this.iInfos.add(lInfo);
    }

    /**
     * This method gives an ArrayList with not valid reasons
     *
     * @return ArrayList<String>
     */
    public ArrayList<String> getNotValidState() {
        return iStates;
    }

    /**
     * This method gives an ArrayList with the extra info for the not valid reason
     *
     * @return ArrayList<String>
     */
    public ArrayList<String> getNotValidExtraInfo() {
        return iInfos;
    }

    /**
     * A setter for the QuantitationStoredInDb property
     *
     * @param lQuant The Quantitation found in the db that is linked to this ratio
     */
    public void setQuantitationStoredInDb(QuantitationExtension lQuant) {
        iQuantitationStoredInDb = lQuant;
    }

    /**
     * This method gives the Quantitation found in the db that is linked to this ratio
     *
     * @return QuantitationExtension The Quantitation found in the db linked to this ratio
     */
    public QuantitationExtension getQuantitationStoredInDb() {
        return iQuantitationStoredInDb;
    }

    /**
     * Setter for the Valid property
     *
     * @param aValid
     */
    public void setValid(boolean aValid) {
        iValid = aValid;
    }

    /**
     * Setter for the comment property
     *
     * @param aComment
     */
    public void setComment(String aComment) {
        iComment = aComment;
    }

    /**
     * Getter for the comment property. If we are in the database mode, it will get the comment from the
     * QuantitationExtension linked to this Ratio.
     *
     * @return String with the comment on the valid status of the ratio
     */
    public String getComment() {
        if (iQuantitativeValidationSingelton.isDatabaseMode()) {
            //in database mode
            //get the comment linked to the Quantitation stored in the db
            String lComment = this.getQuantitationStoredInDb().getComment();
            if(lComment == null){
                lComment = "";
            }
            return lComment;
        }
        //not in database mode
        //return the comment to linked to this DistillerRatio
        return iComment;
    }


    /**
     * Getter for the parent DistillerRatioGroup
     * @return DistillerRatioGroup
     */
    public DistillerRatioGroup getParentRatioGroup() {
        return iParentRatioGroup;
    }

    public void setType(String lType) {
        iType = lType;
    }

    public double getOriginalRatio(boolean aLog2Ratio) {
        if (aLog2Ratio) {
            return Math.log(iOriginalRatio) / Math.log(2);
        } else {
            return iOriginalRatio;
        }
    }


    public void setRecalculatedRatio(double lNewRatio) {
        iRatio = Math.pow(2,lNewRatio);
        iUpdates = iUpdates + 1;
    }

    public void setRecalculatedRatio(double lNewRatio, boolean lLog2) {
        if(lLog2){
            iRatio = Math.pow(2,lNewRatio);
        } else {
            iRatio = lNewRatio;
        }
        iUpdates = iUpdates + 1;
    }

    public void setOriginalRatio(double lOriginalRatio){
        iOriginalRatio = Math.pow(2,lOriginalRatio);
    }

    public void setNormalizationPart(int lNumber){
        this.iPartNumber = lNumber;
    }

    public int getNormatlizationPart(){
        return this.iPartNumber;
    }

    public double getPreNormalizedMAD(){
        return this.iPreNormMAD;
    }

    public void setPreNormalizedMAD(double lPreMAD){
        this.iPreNormMAD = lPreMAD;
    }

    public double getNormalizedMAD(){
        return this.iNormMAD;
    }

    public void setNormalizedMAD(double lMAD){
        this.iNormMAD = lMAD;
    }

    public int getNumberOfRatioUpdates(){
        return iUpdates;
    }

    public void setIndex(double v){
        iIndex = v;
    }

    public void setOriginalIndex(double v){
        iOriginalIndex = v;
    }

    public double getIndex(){
        return iIndex;
    }

    public double getOriginalIndex(){
        return iOriginalIndex;
    }

    public void setInverted(boolean lInverted){
        this.iInverted = lInverted;
    }

    public boolean getInverted(){
        return this.iInverted;
    }
}
