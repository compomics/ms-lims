/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.interfaces;

/**
 *
 * @author Davy
 */

import com.compomics.mslimscore.util.quantitation.ratios.RatioGroup;

public interface Ratio {

    /**
     * Returns the ratio between two measurements.
     * @param aLog2Ratio If this boolean is true, the log2(ratio) will be given.
     * @return double value of the ratio.
     */
    public double getRatio(boolean aLog2Ratio);

    /**
     * Returns the type of the Ratio.
     * ex: The type 'H/L' shows a ratio between a 'Heavy' and 'Light' component.
     * @return The String type of the Ratio.
     */
    public String getType();

    /**
     * Getter for property 'valid'.
     *
     * @return Value for property 'valid'.
     */
    public boolean getValid();

    /**
     * Getter for the Comment
     * @return String
     */
    public String getComment();

    /**
     * Setter for the Comment
     * @param aComment String with the comment on this ratio
     */
    public void setComment(String aComment);

    /**
     * Setter for property 'valid'.
     * @param aValid boolean
     */
    public void setValid(boolean aValid);

    /**
     * Getter for the parent DistillerRatioGroup
     * @return DistillerRatioGroup
     */
    //TODO hmm
    public RatioGroup getParentRatioGroup();

    /**
     * Setter for the ratiotype
     * @param lType String with the ratiotype
     */
    public void setType(String lType);

    public double getOriginalRatio(boolean aLog2Ratio);

    /**
     * Set a recalculated ratio
     * @param lNewRatio Double ratio in log 2 value
     */
    public void setRecalculatedRatio(double lNewRatio);

    public void setRecalculatedRatio(double lNewRatio, boolean lLog2);

    public void setOriginalRatio(double lOriginalRatio);

    public void setNormalizationPart(int j);

    public int getNormatlizationPart();

    public double getPreNormalizedMAD();

    public void setPreNormalizedMAD(double lPreMAD); 

    public double getNormalizedMAD();

    public void setNormalizedMAD(double lMAD);

    public int getNumberOfRatioUpdates();


    public void setIndex(double v);

    public void setOriginalIndex(double v);

    public double getIndex();

    public double getOriginalIndex();

    public void setInverted(boolean lInverted);

    public boolean getInverted();
}
