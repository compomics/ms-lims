/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 20-jan-2004
 * Time: 10:10:00
 */
package com.compomics.mslims.util.fileio;


import com.compomics.util.interfaces.SpectrumFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2009/11/06 11:47:15 $
 */

/**
 * This class presents an abstract ancestor for all standard SpectrumFile implementations.
 *
 * @author Lennart Martens
 * @version $Id: SpectrumFileAncestor.java,v 1.4 2009/11/06 11:47:15 kenny Exp $
 */
public abstract class SpectrumFileAncestor implements SpectrumFile {

    /**
     * This variable holds the filename for the spectrum file.
     */
    protected String iFilename = null;
    /**
     * This HashMap holds all the peaks in the spectrum file.
     */
    protected HashMap iPeaks = new HashMap();
    /**
     * This variable holds the precursor M/Z
     */
    protected double iPrecursorMz = -1.0;
    /**
     * This variable holds the charge state.
     */
    protected int iCharge = 0;
    /**
     * The precursor intensity.
     */
    protected double iIntensity = -1.0;

    /**
     * This method reports on the charge of the precursor ion.
     * Note that when the charge could not be determined, this
     * method will return '0'.
     *
     * @return int with the charge of the precursor, or '0'
     *         if no charge state is known.
     */
    public int getCharge() {
        return iCharge;
    }

    /**
     * This method reports on the filename for the file.
     *
     * @return String with the filename for the file.
     */
    public String getFilename() {
        return iFilename;
    }

    /**
     * This method reports on the intensity of the precursor ion.
     *
     * @return double with the intensity of the precursor ion.
     */
    public double getIntensity() {
        return iIntensity;
    }

    /**
     * This method reports on the peaks in the spectrum, with the
     * Doubles for the masses as keys in the HashMap, and the intensities
     * for each peak as Double value for that mass key.
     *
     * @return HashMap with Doubles as keys (the masses) and Doubles as values (the intensities).
     */
    public HashMap getPeaks() {
        return iPeaks;
    }

    /**
     * This method reports on the precursor M/Z
     *
     * @return double with the precursor M/Z
     */
    public double getPrecursorMZ() {
        return iPrecursorMz;
    }

    /**
     * This method sets the charge of the precursor ion. When the charge is not known,
     * it should be set to '0'.
     *
     * @param aCharge int with the charge of the precursor ion.
     */
    public void setCharge(int aCharge) {
        this.iCharge = aCharge;
    }

    /**
     * This method sets the filename for the file.
     *
     * @param aFilename String with the filename for the file.
     */
    public void setFilename(String aFilename) {
        this.iFilename = aFilename;
    }

    /**
     * This method sets the intensity of the precursor ion.
     *
     * @param aIntensity double with the intensity of the precursor ion.
     */
    public void setIntensity(double aIntensity) {
        this.iIntensity = aIntensity;
    }

    /**
     * This method sets the peaks on the spectrum.
     * Doubles for the masses as keys in the HashMap, and the intensities
     * for each peak as Double value for that mass key.
     *
     * @param aPeaks HashMap with Doubles as keys (the masses) and Doubles as values (the intensities).
     */
    public void setPeaks(HashMap aPeaks) {
        this.iPeaks = aPeaks;
    }

    /**
     * This method sets the precursor M/Z on the file.
     *
     * @param aPrecursorMZ double with the precursor M/Z
     */
    public void setPrecursorMZ(double aPrecursorMZ) {
        this.iPrecursorMz = aPrecursorMZ;
    }

    public double getTotalIntensity() {
        Iterator iter = this.iPeaks.values().iterator();
        double totalIntensity = 0.0;
        while (iter.hasNext()) {
            totalIntensity += (Double) iter.next();
        }
        return round(totalIntensity);
    }


    public double getHighestIntensity() {
        Iterator iter = this.iPeaks.values().iterator();
        double highestIntensity = -1.0;
        while (iter.hasNext()) {
            double temp = (Double) iter.next();
            if (temp > highestIntensity) {
                highestIntensity = temp;
            }
        }
        return round(highestIntensity);
    }

    private double round(final double aTotalIntensity) {
        BigDecimal bd = new BigDecimal(aTotalIntensity).setScale(2, RoundingMode.UP);
        return bd.doubleValue();
    }
}
