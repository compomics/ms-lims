/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 4-okt-2005
 * Time: 14:56:37
 */
package com.compomics.mslims.util.fileio;

import com.compomics.mslims.util.interfaces.BrukerCompound;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/10/27 12:33:20 $
 */

/**
 * This class represents a small part of the data in a Bruker compound single.
 *
 * @author Lennart
 * @version $Id: BrukerCompoundSingle.java,v 1.3 2005/10/27 12:33:20 lennart Exp $
 */
public class BrukerCompoundSingle implements BrukerCompound {
    /**
     * The mass of the compound.
     */
    private double iMass = 0.0;

    /**
     * The position of the compound.
     */
    private String iPosition = null;

    /**
     * The regulation of the compound (should be 1 for a real single).
     */
    private double iRegulation = 0.0;

    /**
     * The area for this ion.
     */
    private double iArea = 0.0;

    /**
     * The intensity for this ion.
     */
    private double iIntensity = 0.0;

    /**
     * Total score attributed by Bruker software to this compound.
     */
    private double iTotalScore = 0.0;

    /**
     * Signal-to-noise ratio for this compound.
     */
    private double iS2n = 0.0;

    /**
     * This constructor takes all the data for a BrukerCompoundSingle.
     *
     * @param aMass double with the mass of the compound.
     * @param aPosition String with the position of the compound.
     * @param aRegulation   double with the regulation for the compound.
     * @param aArea double with the area for the compound.
     * @param aIntensity double with the intensity for the compound.
     * @param aTotalScore   double with the total score for the compound.
     * @param aS2n  double with the signal to noise ratio for the compound.
     */
    public BrukerCompoundSingle(double aMass, String aPosition, double aRegulation, double aArea, double aIntensity, double aTotalScore, double aS2n) {
        iMass = aMass;
        iPosition = aPosition;
        iRegulation = aRegulation;
        iArea = aArea;
        iIntensity = aIntensity;
        iTotalScore = aTotalScore;
        iS2n = aS2n;
    }

    public double getMZForCharge(int aCharge) {
        double temp = iMass+(1.007825*aCharge);
        temp /= aCharge;
        return temp;
    }

    /**
     * This method returns 'true' if this compound has a signal-to-noise ratio
     * equal to or above the specified threshold.
     *
     * @param aS2n  double with the signal-to-noise threshold.
     * @return  boolean that indicates whether this compound passes the filter.
     */
    public boolean passesS2nFilter(double aS2n) {
        boolean result = false;
        if(iS2n >= aS2n) {
            result = true;
        }
        return result;
    }

    public boolean isSingle() {
        return true;
    }

    public double getMass() {
        return iMass;
    }

    public String getPosition() {
        return iPosition;
    }

    public double getRegulation(boolean aUseArea) {
        return iRegulation;
    }

    public double getTotalScore() {
        return iTotalScore;
    }

    public double getS2n() {
        return iS2n;
    }

    public double getArea() {
        return iArea;
    }

    public double getIntensity() {
        return iIntensity;
    }
}
