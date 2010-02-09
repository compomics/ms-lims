/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-mei-2005
 * Time: 10:04:45
 */
package com.compomics.mslims.util.interfaces;

import com.compomics.util.interfaces.SpectrumFile;/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2007/10/22 10:31:17 $
 */

/**
 * This interface describes the behaviour for a spectrum quality assigner.
 *
 * @author Lennart Martens
 * @version $Id: SpectrumQualityAssigner.java,v 1.2 2007/10/22 10:31:17 lennart Exp $
 */
public interface SpectrumQualityAssigner {

    /**
     * This method returns a double with a score for the spectrum quality.
     * How this double has to be interpreted should be documented by the implementation.
     *
     * @param aSpectrum SpectrumFile with the spectrum to evaluate
     * @return  double with a value representing the quality judgement for the spectrum.
     *                 The implementation should document how this value needs to be evaluated.
     */
    public abstract double getSpectrumQualityScore(SpectrumFile aSpectrum);
}
