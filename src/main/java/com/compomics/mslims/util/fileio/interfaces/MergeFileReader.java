/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 28-mrt-03
 * Time: 9:47:52
 */
package com.compomics.mslims.util.fileio.interfaces;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.mascot.MascotIdentifiedSpectrum;
import com.compomics.util.interfaces.SpectrumFile;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2007/10/22 10:31:16 $
 */

/**
 * This interface describes the behaviour for a MergeFileReader.
 *
 * @author Lennart
 */
public interface MergeFileReader {
    /**
     * This method will return a matching SpectrumFile for the given MascotIdentifiedSpectrum (if any), or 'null' if
     * none found. It is based on the 'corresponds' method of the specific SpectrumFile instance.
     *
     * @param aMis MascotIdentifiedSpectrum to compare to.
     * @return SpectrumFile with the corresponding SpectrumFile or 'null' if none found.
     */
    SpectrumFile findMatchingSpectrumFile(MascotIdentifiedSpectrum aMis);

    /**
     * This method reports on the spectrum files currently held in this merge file.
     *
     * @return Vector  with the currently held SpectrumFile implementations.
     */
    Vector getSpectrumFiles();

    /**
     * This method returns the filename of the spectrum file that was found matching the specified
     * MascotIdentifiedSpectrum, or 'null' if no match was found.
     *
     * @param aMis MascotIdentifiedSpectrum to compare to.
     * @return String  with the filename of the corresponding spectrum file, or 'null' if none was found.
     */
    String getCorrespondingSpectrumFilename(MascotIdentifiedSpectrum aMis);

    /**
     * This method returns a String representation of this instance.
     *
     * @return String  with the String representation of the object.
     */
    String toString();

    /**
     * Simple getter for the filename for this Mergefile.
     *
     * @return String  with the filename.
     */
    String getFilename();

    /**
     * Shortcut method that reports all the known spectrum filenames (present in this mergefile) in a String array.
     *
     * @return String[]    with the filenames of all the spectrum files in this mergefile.
     */
    String[] getAllSpectrumFilenames();

    /**
     * This method reports whether this MergeFileReader can read the specified file.
     *
     * @param aFile File with the file to check readability for.
     * @return boolean that indicates whether this MergeFileReader can read the specified file.
     */
    boolean canRead(File aFile);

    /**
     * This method loads the specified file in this MergeFileReader.
     *
     * @param aFile File with the file to load.
     * @throws IOException when the loading operation failed.
     */
    void load(File aFile) throws IOException;
}
