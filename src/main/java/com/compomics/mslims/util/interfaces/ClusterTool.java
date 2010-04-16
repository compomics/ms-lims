/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-mei-2005
 * Time: 9:59:42
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
 * This interface describes the behaviour for a clustertool.
 *
 * @author Lennart Martens
 * @version $Id: ClusterTool.java,v 1.2 2007/10/22 10:31:17 lennart Exp $
 */
public interface ClusterTool {

    /**
     * This method takes an array of SpectrumFile instances, performs its clustering magic and returns an array of
     * Cluster instances that group all the spectra in clusters. Note that clusters consisting of only a single spectrum
     * can be expected as non-clusterable ('unique') spectra should be retained as 'clusters of one'.
     *
     * @param aSpectra SpectrumFile[] with the spectra to cluster
     * @return Cluster[]   with the clusters composed of the specified spectra
     */
    public abstract Cluster[] clusterSpectra(SpectrumFile[] aSpectra);
}
