/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 21-jan-2004
 * Time: 10:49:01
 */
package com.compomics.mslimscore.util.fileio.mergefiles;


import com.compomics.mslimscore.util.fileio.interfaces.MergeFileReader;
import com.compomics.util.interfaces.SpectrumFile;

import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2007/10/22 10:31:17 $
 */

/**
 * This class conveniently wraps the core functionality of a MergeFileReader implementation in an abstract superclass.
 *
 * @author Lennart Martens
 * @version $Id: MergeFileReaderAncestor.java,v 1.2 2007/10/22 10:31:17 lennart Exp $
 */
public abstract class MergeFileReaderAncestor implements MergeFileReader {

    /**
     * This Vector will hold all the spectrum files in the mergefile.
     */
    protected Vector iSpectrumFiles = null;

    /**
     * The filename for this mergefile.
     */
    protected String iFilename = null;

    /**
     * This method reports on the spectrum files currently held in this merge file.
     *
     * @return Vector  with the currently held SpectrumFiles.
     */
    public Vector getSpectrumFiles() {
        return this.iSpectrumFiles;
    }

    /**
     * Simple getter for the filename for this Mergefile.
     *
     * @return String  with the filename.
     */
    public String getFilename() {
        return this.iFilename;
    }

    /**
     * Shortcut method that reports all the spectrum filenames in a String array.
     *
     * @return String[]    with the filenames of all the spectrum files in this mergefile.
     */
    public String[] getAllSpectrumFilenames() {
        String[] result = null;
        // See how many PKL files there are.
        int liSize = this.iSpectrumFiles.size();
        // Dim the array accordingly.
        result = new String[liSize];
        // Cycle all and add the names to the array.
        for (int i = 0; i < liSize; i++) {
            SpectrumFile lFile = (SpectrumFile) iSpectrumFiles.elementAt(i);
            result[i] = lFile.getFilename();
        }
        // Voila.
        return result;
    }
}
