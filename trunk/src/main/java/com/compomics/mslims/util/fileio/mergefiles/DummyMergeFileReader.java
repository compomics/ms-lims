/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 9-jul-2003
 * Time: 11:05:18
 */
package com.compomics.mslims.util.fileio.mergefiles;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.interfaces.MergeFileReader;
import com.compomics.mslims.util.mascot.MascotIdentifiedSpectrum;
import com.compomics.util.interfaces.SpectrumFile;

import java.io.File;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2007/10/22 10:31:17 $
 */

/**
 * This class
 *
 * @author Lennart
 */
public class DummyMergeFileReader implements MergeFileReader {
    // Class specific log4j logger for DummyMergeFileReader instances.
    private static Logger logger = Logger.getLogger(DummyMergeFileReader.class);

    /**
     * Useless constructor.
     *
     * @param aFile File that is not in any way used.
     */
    public DummyMergeFileReader(File aFile) {
    }

    /**
     * Default constructor.
     */
    public DummyMergeFileReader() {
    }

    /**
     * This method will return a matching PKL file for the given MascotIdentifiedSpectrum (if any), or 'null' if none
     * found. It is based on the 'corresponds' method of the PKLFile class.
     *
     * @param aMis MascotIdentifiedSpectrum to compare to.
     * @return PKLFile with the corresponding PKLFile or 'null' if none found.
     */
    public SpectrumFile findMatchingSpectrumFile(MascotIdentifiedSpectrum aMis) {
        return null;
    }

    /**
     * Shortcut method that reports all teh PKL filenames in a String array.
     *
     * @return String[]    with the filenames of all the PKL files in this mergefile.
     */
    public String[] getAllSpectrumFilenames() {
        return new String[0];
    }

    /**
     * THis method returns the filename of the pklfile that was found matching the specified MascotIdentifiedSpectrum,
     * or 'null' if no match was found.
     *
     * @param aMis MascotIdentifiedSpectrum to compare to.
     * @return String  with the filename of the corresponding PKL file, or 'null' if none was found.
     */
    public String getCorrespondingSpectrumFilename(MascotIdentifiedSpectrum aMis) {
        return "*";
    }

    /**
     * Simple getter for the filename for this Mergefile.
     *
     * @return String  with the filename.
     */
    public String getFilename() {
        return "*";
    }

    /**
     * This method reports on the PKL files currently held in this merge file.
     *
     * @return Vector  with the currently held PKLFiles.
     */
    public Vector getSpectrumFiles() {
        return new Vector();
    }

    /**
     * This method reports whether this MergeFileReader can read the specified file.
     *
     * @param aFile File with the file to check readability for.
     * @return boolean that indicates whether this MergeFileReader can read the specified file.
     */
    public boolean canRead(File aFile) {
        // Always return false!
        // This dummyreader is the last resort when all else fails, and thus should not be
        // allowed to interfere with the autodetection!
        return false;
    }

    /**
     * This method does nothing with the provided file as this implementation is just a dummy.
     *
     * @param aFile with a file that is completely ignored by this implementation.
     */
    public void load(File aFile) {
    }
}
