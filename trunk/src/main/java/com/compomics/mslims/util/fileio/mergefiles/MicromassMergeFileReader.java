/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-feb-03
 * Time: 14:30:52
 */
package com.compomics.mslims.util.fileio.mergefiles;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.PKLFile;
import com.compomics.mslims.util.mascot.MascotIdentifiedSpectrum;
import com.compomics.util.interfaces.SpectrumFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2007/10/22 10:31:17 $
 */

/**
 * This class allows easy reading and retrieving of mergefile contents.
 *
 * @author Lennart Martens.
 */
public class MicromassMergeFileReader extends MergeFileReaderAncestor {
    // Class specific log4j logger for MicromassMergeFileReader instances.
    private static Logger logger = Logger.getLogger(MicromassMergeFileReader.class);

    /**
     * Default constructor.
     */
    public MicromassMergeFileReader() {
    }

    /**
     * This constructor opens the specified mergefile and maps it to memory.
     *
     * @param aMergeFile String with the fully qualified name of the file.
     * @throws java.io.IOException when the file could not be read.
     */
    public MicromassMergeFileReader(String aMergeFile) throws IOException {
        this(new File(aMergeFile));
    }

    /**
     * This constructor opens the specified mergefile and maps it to memory.
     *
     * @param aMergeFile File with a pointer to the mergefile.
     * @throws java.io.IOException when the file could not be read.
     */
    public MicromassMergeFileReader(File aMergeFile) throws IOException {
        this.load(aMergeFile);
    }

    /**
     * This method will return a matching PKL file for the given MascotIdentifiedSpectrum (if any), or 'null' if none
     * found. It is based on the 'corresponds' method of the PKLFile class.
     *
     * @param aMis MascotIdentifiedSpectrum to compare to.
     * @return PKLFile with the corresponding PKLFile or 'null' if none found.
     */
    public SpectrumFile findMatchingSpectrumFile(MascotIdentifiedSpectrum aMis) {
        int liSize = this.iSpectrumFiles.size();
        PKLFile result = null;
        int nbrIDs = 0;
        Vector idIndices = new Vector(5, 2);
        for (int i = 0; i < liSize; i++) {
            PKLFile pkl = (PKLFile) this.iSpectrumFiles.get(i);
            if (pkl.corresponds(aMis)) {
                result = pkl;
                nbrIDs++;
                idIndices.add(new Integer(i));
            }
        }
        // Check for a match, compensate possible aberrations.
        if (nbrIDs == 0) {
            logger.error("Found no match (" + nbrIDs + ") for '" + aMis.getPrecursorMZ() + " " + aMis.getChargeState() + ";" + aMis.getLowestMass() + "-" + aMis.getHighestMass() + ";" + aMis.getLeastIntense() + "-" + aMis.getMostIntense() + "' in file '" + this.iFilename + "'!");
            logger.error("\tRecycling while attempting to compensate with coarseCheck method...");
            int secondLevelCount = 0;
            for (int i = 0; i < liSize; i++) {
                PKLFile pkl = (PKLFile) this.iSpectrumFiles.get(i);
                if (pkl.coarseCheck(aMis)) {
                    result = pkl;
                    secondLevelCount++;
                }
            }
            if (secondLevelCount > 1) {
                logger.error("\tFound more than one match (" + nbrIDs + "); last one will be kept.");
            } else if (secondLevelCount == 1) {
                logger.error("\tCompensated! Problem resolved!");
            } else {
                logger.error("\tStill no hit found!");
            }
        } else if (nbrIDs > 1) {
            logger.error("Found more than one match (" + nbrIDs + ") for '" + aMis.getPrecursorMZ() + " " + aMis.getChargeState() + ";" + aMis.getLowestMass() + "-" + aMis.getHighestMass() + ";" + aMis.getLeastIntense() + "-" + aMis.getMostIntense() + "' in file '" + this.iFilename + "'!");
            logger.error("\tRecycling while attempting to compensate with deepCheck method...");
            int secondLevelCount = 0;
            for (int i = 0; i < nbrIDs; i++) {
                PKLFile pkl = (PKLFile) this.iSpectrumFiles.get(((Integer) idIndices.get(i)).intValue());
                if (pkl.deepCheck(aMis)) {
                    result = pkl;
                    secondLevelCount++;
                }
            }
            if (secondLevelCount > 1) {
                logger.error("\tUnable to compensate! Still " + secondLevelCount + " matches remaining! Last one will be kept!");
            } else if (secondLevelCount == 1) {
                logger.error("\tCompensated! Problem resolved!");
            } else {
                logger.error("\tNo hit found with deeper check! Keeping last hit from earlier check!");
            }
        }

        return result;
    }

    /**
     * THis method returns the filename of the pklfile that was found matching the specified MascotIdentifiedSpectrum,
     * or 'null' if no match was found.
     *
     * @param aMis MascotIdentifiedSpectrum to compare to.
     * @return String  with the filename of the corresponding PKL file, or 'null' if none was found.
     */
    public String getCorrespondingSpectrumFilename(MascotIdentifiedSpectrum aMis) {
        String result = null;

        SpectrumFile temp = this.findMatchingSpectrumFile(aMis);
        if (temp != null) {
            result = temp.getFilename();
        }

        return result;
    }

    /**
     * This method returns a String representation of this instance.
     *
     * @return String  with the String representation of the object.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        // Add the filename.
        result.append(this.iFilename + "\n");
        // Add the Strings for each pkl file.
        int liSize = this.iSpectrumFiles.size();
        for (int i = 0; i < liSize; i++) {
            result.append(this.iSpectrumFiles.get(i).toString() + "\n");
        }
        // Voila.
        return result.toString();
    }

    /**
     * Called by the garbage collector on an object when garbage collection determines that there are no more references
     * to the object. A subclass overrides the <code>finalize</code> method to dispose of system resources or to perform
     * other cleanup.
     * <p/>
     * The general contract of <tt>finalize</tt> is that it is invoked if and when the Java<font
     * size="-2"><sup>TM</sup></font> virtual machine has determined that there is no longer any means by which this
     * object can be accessed by any thread that has not yet died, except as a result of an action taken by the
     * finalization of some other object or class which is ready to be finalized. The <tt>finalize</tt> method may take
     * any action, including making this object available again to other threads; the usual purpose of
     * <tt>finalize</tt>, however, is to perform cleanup actions before the object is irrevocably discarded. For
     * example, the finalize method for an object that represents an input/output connection might perform explicit I/O
     * transactions to break the connection before the object is permanently discarded.
     * <p/>
     * The <tt>finalize</tt> method of class <tt>Object</tt> performs no special action; it simply returns normally.
     * Subclasses of <tt>Object</tt> may override this definition.
     * <p/>
     * The Java programming language does not guarantee which thread will invoke the <tt>finalize</tt> method for any
     * given object. It is guaranteed, however, that the thread that invokes finalize will not be holding any
     * user-visible synchronization locks when finalize is invoked. If an uncaught exception is thrown by the finalize
     * method, the exception is ignored and finalization of that object terminates.
     * <p/>
     * After the <tt>finalize</tt> method has been invoked for an object, no further action is taken until the Java
     * virtual machine has again determined that there is no longer any means by which this object can be accessed by
     * any thread that has not yet died, including possible actions by other objects or classes which are ready to be
     * finalized, at which point the object may be discarded.
     * <p/>
     * The <tt>finalize</tt> method is never invoked more than once by a Java virtual machine for any given object.
     * <p/>
     * Any exception thrown by the <code>finalize</code> method causes the finalization of this object to be halted, but
     * is otherwise ignored.
     *
     * @throws java.lang.Throwable the <code>Exception</code> raised by this method
     */
    public void finalize() throws Throwable {
        this.iFilename = null;
        this.iSpectrumFiles = null;
        super.finalize();
    }

    /**
     * This method reports whether this MergeFileReader can read the specified file.
     *
     * @param aFile File with the file to check readability for.
     * @return boolean that indicates whether this MergeFileReader can read the specified file.
     */
    public boolean canRead(File aFile) {
        // Default is 'not readable'.
        boolean result = false;
        // Only do the hard work if there is a file at all.
        if (aFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(aFile));
                String line = null;
                line = br.readLine();
                while (line != null && line.trim().equals("")) {
                    br.readLine();
                }
                // Now we have the first non-empty line in 'line'.
                try {
                    // A PKL file in a mergefile should have at least three
                    // and at most four tokens in the first line:
                    // <mass> <intensity> <charge> [<filename>]
                    StringTokenizer st = new StringTokenizer(line, " ");
                    int count = st.countTokens();
                    double mass = -1.0;
                    double intensity = -1.0;
                    int charge = 0;
                    mass = Double.parseDouble(st.nextToken());
                    intensity = Double.parseDouble(st.nextToken());
                    charge = Integer.parseInt(st.nextToken());
                    if ((count == 4 || count == 3) && mass >= 0 && intensity >= 0 && charge != 0) {
                        // We should be able to read this.
                        result = true;
                    }
                } catch (Exception e) {
                    // So it is not a micromass-derived mergefile.
                    // Just let the execution continue, as the result is default set to
                    // 'false' anyway.
                }
                br.close();
            } catch (IOException ioe) {
                // Do nothing here.
                // If we can't read it, we don't even bother.
            }
        }
        // Report on the findings.
        return result;
    }

    /**
     * This method loads the specified file in this MergeFileReader.
     *
     * @param aFile File with the file to load.
     * @throws java.io.IOException when the loading operation failed.
     */
    public void load(File aFile) throws IOException {
        iSpectrumFiles = new Vector(300, 10);
        if (!aFile.exists()) {
            throw new IOException("Mergefile '" + aFile.getCanonicalPath() + "' could not be found!");
        } else {
            BufferedReader br = new BufferedReader(new FileReader(aFile));
            StringBuffer lsb = null;
            String line = null;
            while ((line = br.readLine()) != null) {
                // Skip empty lines.
                if (line.trim().equals("")) {
                    continue;
                }
                StringTokenizer lst = new StringTokenizer(line.trim(), " ");
                if (lst.countTokens() >= 3) {
                    // New line!
                    if (lsb != null) {
                        this.iSpectrumFiles.add(new PKLFile(lsb.toString()));
                    }
                    lsb = new StringBuffer(line + "\n");
                } else {
                    lsb.append(line + "\n");
                }
            }
            br.close();
            this.iSpectrumFiles.add(new PKLFile(lsb.toString()));
        }
        // The last entry needs be parsed as well!
        this.iFilename = aFile.getName();
    }
}
