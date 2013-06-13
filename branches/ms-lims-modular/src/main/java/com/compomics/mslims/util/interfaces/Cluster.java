/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-mei-2005
 * Time: 9:56:53
 */
package com.compomics.mslims.util.interfaces;

import org.apache.log4j.Logger;

import com.compomics.util.interfaces.SpectrumFile;/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2007/10/22 10:31:17 $
 */

/**
 * This interface describes the behaviour for a cluster of spectra
 *
 * @author Lennart Martens
 * @version $Id: Cluster.java,v 1.2 2007/10/22 10:31:17 lennart Exp $
 */
public interface Cluster {

    /**
     * This method should return a display name for the cluster of spectra.
     *
     * @return String with a display name for the cluster of spectra.
     */
    public abstract String getName();

    /**
     * This method should return the number of spectra that were grouped to form this cluster. A valid return value is
     * any positive, non-zero integer.
     *
     * @return int with the number of spectra in this cluster. Result should be a positive, non-zero integer.
     */
    public abstract int getSpectrumCount();

    /**
     * This method returns the spectra that this cluster is composed of as an array of SpectrumFile instances.
     *
     * @return SpectrumFile[] with the spectra that this cluster is composed of.
     */
    public abstract SpectrumFile[] getSpectra();

    /**
     * This method allows the caller to add a spectrum to the cluster.
     *
     * @param aSpectrum SpectrumFile to add to the cluster.
     */
    public abstract void addSpectrum(SpectrumFile aSpectrum);
}
