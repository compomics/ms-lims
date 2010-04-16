/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 4-okt-2005
 * Time: 14:40:21
 */
package com.compomics.mslims.util.fileio;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.interfaces.BrukerCompound;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/10/27 12:33:20 $
 */

/**
 * This class represents a small part of the data in a Bruker compound couple.
 *
 * @author Lennart Martens
 * @version $Id: BrukerCompoundCouple.java,v 1.3 2005/10/27 12:33:20 lennart Exp $
 */
public class BrukerCompoundCouple implements BrukerCompound {
    // Class specific log4j logger for BrukerCompoundCouple instances.
    private static Logger logger = Logger.getLogger(BrukerCompoundCouple.class);

    /**
     * The mass of the light ion.
     */
    private double iLightMass = 0.0;
    /**
     * The mass of the heavy ion.
     */
    private double iHeavyMass = 0.0;
    /**
     * The total score for the light ion.
     */
    private double iLightTotalScore = 0.0;
    /**
     * The total score for the heavy ion.
     */
    private double iHeavyTotalScore = 0.0;
    /**
     * The signal-to-noise ratio for the light ion.
     */
    private double iLightS2n = 0.0;
    /**
     * The signal-to-noise ratio for the heavy ion.
     */
    private double iHeavyS2n = 0.0;
    /**
     * The light area.
     */
    private double iLightArea = 0.0;
    /**
     * The heavy area.
     */
    private double iHeavyArea = 0.0;
    /**
     * The light intensity (averaged).
     */
    private double iLightIntensity = 0.0;
    /**
     * The heavy intensity (averaged).
     */
    private double iHeavyIntensity = 0.0;
    /**
     * The ratio (light over heavy) of the ions.
     */
    private double iRegulation = 0.0;
    /**
     * The position of the light ion on the sample plate.
     */
    private String iLightPosition = null;
    /**
     * The position of the heavy ion on the sample plate.
     */
    private String iHeavyPosition = null;

    /**
     * We here cache the mass of the highest scoring ion.
     */
    private double iHighestScoringMass = 0.0;

    /**
     * We here cache the position of the highest scoring ion.
     */
    private String iHighestScoringPosition = null;

    /**
     * This constructor takes all details for a couple.
     *
     * @param aLightMass       double with the mass of the light ion.
     * @param aHeavyMass       double with the mass of the heavy ion.
     * @param aLightTotalScore double with the total score for the light ion.
     * @param aHeavyTotalScore double with the total score for the heavy ion.
     * @param aLightArea       double with the area for the light ion.
     * @param aHeavyArea       double with the area for the heavy ion.
     * @param aLightIntensity  double with the intensity for the light ion.
     * @param aHeavyIntensity  double with the intensity for the heavy ion.
     * @param aLightS2n        double with the signal-to-noise ratio for the light ion.
     * @param aHeavyS2n        double with the signal-to-noise ratio for the heavy ion.
     * @param aRegulation      double with the ratio (light over heavy) of the ions.
     * @param aLightPosition   String with the position of the light ion.
     * @param aHeavyPosition   String with the position of the heavy ion.
     */
    public BrukerCompoundCouple(double aLightMass, double aHeavyMass, double aLightTotalScore, double aHeavyTotalScore, double aLightArea, double aHeavyArea, double aLightIntensity, double aHeavyIntensity, double aLightS2n, double aHeavyS2n, double aRegulation, String aLightPosition, String aHeavyPosition) {
        this.iLightMass = aLightMass;
        this.iHeavyMass = aHeavyMass;
        this.iLightTotalScore = aLightTotalScore;
        this.iHeavyTotalScore = aHeavyTotalScore;
        this.iLightArea = aLightArea;
        this.iHeavyArea = aHeavyArea;
        this.iLightIntensity = aLightIntensity;
        this.iHeavyIntensity = aHeavyIntensity;
        this.iLightS2n = aLightS2n;
        this.iHeavyS2n = aHeavyS2n;
        this.iRegulation = aRegulation;
        this.iLightPosition = aLightPosition;
        this.iHeavyPosition = aHeavyPosition;
        // Cache.
        if (iLightTotalScore > iHeavyTotalScore) {
            iHighestScoringMass = iLightMass;
            iHighestScoringPosition = iLightPosition;
        } else if (iLightTotalScore < iHeavyTotalScore) {
            iHighestScoringMass = iHeavyMass;
            iHighestScoringPosition = iHeavyPosition;
        } else {
            // Both have equal total score.
            // Then use signal-to-noise ratio.
            if (iLightS2n > iHeavyS2n) {
                iHighestScoringMass = iLightMass;
                iHighestScoringPosition = iLightPosition;
            } else if (iLightS2n < iHeavyS2n) {
                iHighestScoringMass = iHeavyMass;
                iHighestScoringPosition = iHeavyPosition;
            }
        }
    }

    public BrukerCompoundCouple(BrukerCompoundSingle aLight, BrukerCompoundSingle aHeavy) {
        this(aLight.getMass(), aHeavy.getMass(), aLight.getTotalScore(), aHeavy.getTotalScore(), aLight.getArea(), aHeavy.getArea(), aLight.getIntensity(), aHeavy.getIntensity(), aLight.getS2n(), aHeavy.getS2n(), aLight.getRegulation(true), aLight.getPosition(), aHeavy.getPosition());
        if (!(aLight.isSingle() && aHeavy.isSingle())) {
            throw new IllegalArgumentException("Only two singles can be joined into a couple. You attempted the coupling with at least one pre-existing couple!");
        }
        if (aLight.getRegulation(true) != aHeavy.getRegulation(true)) {
            throw new IllegalArgumentException("This does not appear to be a couple as their ratios differ!");
        }
    }

    public double getMZForCharge(int aCharge) {
        double temp = iHighestScoringMass + (1.007825 * aCharge);
        temp /= aCharge;
        return temp;
    }

    /**
     * This method returns 'true' if at least one ion (be it light or heavy) has a signal-to-noise ratio equal to or
     * above the specified threshold.
     *
     * @param aS2n double with the signal-to-noise threshold.
     * @return boolean that indicates whether the couple passes the filter.
     */
    public boolean passesS2nFilter(double aS2n) {
        boolean result = false;
        if (iLightS2n >= aS2n || iHeavyS2n >= aS2n) {
            result = true;
        }
        return result;
    }

    public boolean isSingle() {
        return false;
    }

    public double getMass() {
        return this.iHighestScoringMass;
    }

    public String getPosition() {
        return this.iHighestScoringPosition;
    }

    public double getTotalScore() {
        return Math.max(this.iLightTotalScore, this.iHeavyTotalScore);
    }

    public double getHeavyMass() {
        return iHeavyMass;
    }

    public String getHeavyPosition() {
        return iHeavyPosition;
    }

    public double getHeavyTotalScore() {
        return iHeavyTotalScore;
    }

    public double getLightMass() {
        return iLightMass;
    }

    public String getLightPosition() {
        return iLightPosition;
    }

    public double getLightTotalScore() {
        return iLightTotalScore;
    }

    public double getRegulation(boolean aUseArea) {
        double result = 0.0;
        if (aUseArea) {
            result = iLightArea / iHeavyArea;
        } else {
            result = iLightIntensity / iHeavyIntensity;
        }
        return result;
    }

    public double getHeavyS2n() {
        return iHeavyS2n;
    }

    public double getLightS2n() {
        return iLightS2n;
    }
}
