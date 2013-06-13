/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 27-mrt-03
 * Time: 18:15:40
 */
package com.compomics.mslims.util.fileio.mergefiles;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.MascotGenericFile;
import com.compomics.mslims.util.mascot.MascotIdentifiedSpectrum;
import com.compomics.util.interfaces.SpectrumFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2009/01/30 10:31:05 $
 */

/**
 * This class allows easy reading and retrieving of Mascot Generic Format mergefile contents.
 *
 * @author Lennart Martens.
 */
public class MascotGenericMergeFileReader extends MergeFileReaderAncestor {
    // Class specific log4j logger for MascotGenericMergeFileReader instances.
    private static Logger logger = Logger.getLogger(MascotGenericMergeFileReader.class);

    /**
     * This String holds the run identification for this mergefile.
     */
    protected String iRunName = null;

    /**
     * This String holds the comments located on top of the MascotGenericMergeFile.
     */
    protected String iComments = null;

    /**
     * Default constructor.
     */
    public MascotGenericMergeFileReader() {
    }

    /**
     * This constructor opens the specified mergefile and maps it to memory.
     *
     * @param aMergeFile String with the fully qualified name of the file.
     * @throws java.io.IOException when the file could not be read.
     */
    public MascotGenericMergeFileReader(String aMergeFile) throws IOException {
        this(new File(aMergeFile));
    }

    /**
     * This constructor opens the specified mergefile and maps it to memory.
     *
     * @param aMergeFile File with a pointer to the mergefile.
     * @throws java.io.IOException when the file could not be read.
     */
    public MascotGenericMergeFileReader(File aMergeFile) throws IOException {
        this.load(aMergeFile);
    }

    /**
     * This method reports on the comments in the merge file.
     *
     * @return String with the comments.
     */
    public String getComments() {
        return iComments;
    }

    /**
     * This method allows the setting of comments on the mergefile. It is up to the caller to make sure every line is
     * prefixed with '#'.
     *
     * @param aComments String with the comments.
     */
    public void setComments(String aComments) {
        iComments = aComments;
    }

    /**
     * This method reports the run name for the merge file.
     *
     * @return String with the run name.
     */
    public String getRunName() {
        return iRunName;
    }

    /**
     * This method returns the run name for the merge file.
     *
     * @param aRunName String with the run name.
     */
    public void setRunName(String aRunName) {
        iRunName = aRunName;
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
        MascotGenericFile result = null;
        int nbrIDs = 0;
        Vector idIndices = new Vector(5, 2);
        for (int i = 0; i < liSize; i++) {
            MascotGenericFile mgf = (MascotGenericFile) this.iSpectrumFiles.get(i);
            if (mgf.corresponds(aMis)) {
                result = mgf;
                nbrIDs++;
                idIndices.add(new Integer(i));
            }
        }
        // Check for a match, compensate possible aberrations.
        if (nbrIDs == 0) {
            logger.error("Found no match (" + nbrIDs + ") for '" + aMis.getSearchTitle() + "' in file '" + this.iFilename + "'!");
        } else if (nbrIDs > 1) {
            logger.error("Found more than one match (" + nbrIDs + ") for '" + aMis.getSearchTitle() + "' in file '" + this.iFilename + "'!");
        }

        return result;
    }

    /**
     * This method returns the filename of the pklfile that was found matching the specified MascotIdentifiedSpectrum,
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
        // Add the comments.
        result.append(this.iComments);
        // Add the Strings for each pkl file.
        int liSize = this.iSpectrumFiles.size();
        for (int i = 0; i < liSize; i++) {
            result.append(this.iSpectrumFiles.get(i).toString() + "\n\n");
        }
        // Voila.
        return result.toString();
    }

    /**
     * This method reports whether this MergeFileReader can read the specified file.
     *
     * @param aFile File with the file to check readability for.
     * @return boolean that indicates whether this MergeFileReader can read the specified file.
     */
    public boolean canRead(File aFile) {
        boolean result = false;

        if (aFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(aFile));
                String line = null;
                line = br.readLine();
                // Skip empty lines, comment lines (starting wth '#') and general parameters (contain 'key=value' mappings).
                while (line != null && (line.trim().equals("") || line.trim().startsWith("#") || line.indexOf("=") > 0)) {
                    line = br.readLine();
                }
                // Now we should have the first, non-empty, non-comment line.
                if (line != null && line.startsWith("BEGIN IONS")) {
                    // We should be able to read this.
                    result = true;
                }

                //check the following line, the title must be readible by this MascotDistillerMergeFileReader
                line = br.readLine();
                if (line != null) {
                    try {
                        String lLCRun = null;
                        int lCompound = -1;
                        int lBeginScan = -1;
                        int lEndScan = -1;
                        int lSumOfScans = 1;

                        // a) Parse the Lcrun
                        lLCRun = line.substring(line.lastIndexOf('\\') + 1, line.lastIndexOf('.'));

                        if (line.indexOf("TITLE=") == 0) {
                            line = line.substring(6);
                        }

                        // b) Parse the compound number
                        lCompound = Integer.valueOf(line.substring(0, line.indexOf(':')));

                        // c) Find out the sum of scans
                        if (line.indexOf("Sum") >= 0) {
                            // c1 Multiple scans from this spectrum!
                            lBeginScan = Integer.valueOf(line.substring(line.indexOf("range ") + 6, line.indexOf(" (rt=")));
                            lEndScan = Integer.valueOf(line.substring(line.indexOf(") to ") + 5, line.lastIndexOf(" (rt=")));
                            lSumOfScans = Integer.valueOf(line.substring(line.indexOf("Sum of ") + 7, line.lastIndexOf(" scans ")));
                        } else {
                            // c2 Single scan form this spectrum!
                            lBeginScan = Integer.valueOf(line.substring(line.indexOf("Scan ") + 5, line.indexOf(" (rt=")));
                        }
                        //if we can get here, this means the title is a distiller title.
                        //This is not a job for the MascotGenericMergeFileReader
                        result = false;
                    } catch (IndexOutOfBoundsException e) {
                        //title could not be parsed
                        //this is ok for the MascotGenericMergeFileReader
                    }
                }
                br.close();
            } catch (IOException ioe) {
                // Do nothing here.
                // If we can't read it, we don't even bother.
            }
        }
        // Report our findings.
        return result;
    }

    /**
     * This method reports on all the titles for each of the MascotGenericFiles this mergefile contains.
     *
     * @return String[]    with the title for each spectrum.
     */
    public String[] getAllSpectrumTitles() {
        String[] result = new String[this.iSpectrumFiles.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ((MascotGenericFile) this.iSpectrumFiles.get(i)).getTitle();
        }
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
            // Read the filename.
            this.iFilename = aFile.getName();

            BufferedReader br = new BufferedReader(new FileReader(aFile));

            // First parse the header.
            // First (non-empty?) line can be CHARGE= --> omit it if present.
            // Next up are comment blocks.
            // First non-empty comment line is raw filename (fully qualified).
            // Second non-empty comment line holds the run title.
            // Rest holds additional info which will be stored in the 'iComments' variable.
            // First comment line without spaces after the '###' is part of the first spectrum.
            String line = null;
            int commentLineCounter = 0;
            int lineCounter = 0;
            int spectrumCounter = 0;
            boolean inSpectrum = false;
            StringBuffer tempComments = new StringBuffer();
            StringBuffer spectrum = new StringBuffer();
            // Cycle the file.
            boolean runnameNotYetFound = true;
            while ((line = br.readLine()) != null) {
                lineCounter++;
                line = line.trim();
                // Skip empty lines and file-level charge statement.
                if (line.equals("") || (lineCounter == 1 && line.startsWith("CHARGE"))) {
                    continue;
                }
                // Comment lines.
                else if (line.startsWith("#") && !inSpectrum) {
                    // First strip off the comment markings in a new String ('cleanLine').
                    String cleanLine = this.cleanCommentMarks(line);
                    // If cleanLine trimmed is empty String, it's an empty comment line
                    // and therefore skipped without counting.
                    String cleanLineTrimmed = cleanLine.trim();
                    if (cleanLineTrimmed.equals("")) {
                        continue;
                    }
                    // If it is not empty String, yet starts with a space (note that we verify
                    // using the untrimmed cleanLine!), it is a header
                    // comment, so we start by counting it!
                    else if (cleanLine.startsWith(" ") || cleanLine.startsWith("\t")) {
                        commentLineCounter++;
                        // See if it is the second non-empty comment line.
                        if (runnameNotYetFound && commentLineCounter >= 2 && cleanLineTrimmed.indexOf("Instrument:") < 0 && cleanLineTrimmed.indexOf("Manufacturer:") < 0) {
                            // This line contains the run name.
                            this.iRunName = cleanLineTrimmed;
                            runnameNotYetFound = false;
                        }
                        // Every non-empty comment line is added to the tempComments
                        // StringBuffer, the contents of which are afterwards copied into
                        // the 'iComments' variable.
                        tempComments.append(line + "\n");
                    }
                    // Spectrum comment. Start a new Spectrum!
                    else {
                        inSpectrum = true;
                        spectrum.append(line + "\n");
                    }
                }
                // Not an empty line, not an initial charge line, not a comment line and inside a spectrum.
                // It could be 'BEGIN IONS', 'END IONS', 'TITLE=...', 'PEPMASS=...',
                // in-spectrum 'CHARGE=...' or, finally, a genuine peak line.
                // Whatever it is, add it to the spectrum StringBuffer.
                else if (inSpectrum) {
                    // Adding this line to the spectrum StringBuffer.
                    spectrum.append(line + "\n");
                    // See if it was an 'END IONS', in which case we stop being in a spectrum.
                    if (line.indexOf("END IONS") >= 0) {
                        // End detected. Much to do!
                        // Reset boolean.
                        inSpectrum = false;
                        // Increment the spectrumCounter by one.
                        spectrumCounter++;
                        // Create a filename for the spectrum, based on the filename of the mergefile, with
                        // an '_[spectrumCounter]' before the extension (eg., myParent.mgf --> myParent_1.mgf).
                        String spectrumFilename = this.createSpectrumFilename(spectrumCounter);
                        // Parse the contents of the spectrum StringBuffer into a MascotGenericFile.
                        MascotGenericFile mgf = new MascotGenericFile(spectrumFilename, spectrum.toString());
                        // Add it to the collection of SpectrumFiles.
                        this.iSpectrumFiles.add(mgf);
                        // Reset the spectrum StringBuffer.
                        spectrum = new StringBuffer();
                    }
                }
                // If we're not in a spectrum, see if the line is 'BEGIN IONS', which marks the begin of a spectrum!
                else if (line.indexOf("BEGIN IONS") >= 0) {
                    inSpectrum = true;
                    spectrum.append(line + "\n");
                }
            }
            // Initialize the comments.
            this.iComments = tempComments.toString();

            br.close();
        }
        // Set the filename as well!
        this.iFilename = aFile.getName();

        // If we do not have a run name by now, we just take the filename, minus the extension.
        if (this.iRunName == null) {
            // See if there is an extension,
            // and if there isn't, just take the filename as-is.
            int location = this.iFilename.lastIndexOf(".");
            if (location > 0) {
                iRunName = this.iFilename.substring(0, location);
            } else {
                iRunName = this.iFilename;
            }
        }
    }

    /**
     * This method strips a line of its prefixed '#' markings. Note that no trimming is performed.
     *
     * @param aCommentLine String with the commentline to strip prefixed '#'-ings from.
     * @return String with the comment line minus the prefixed '#'-ings.
     */
    protected String cleanCommentMarks(String aCommentLine) {
        StringBuffer result = new StringBuffer(aCommentLine);
        while (result.length() > 0 && result.charAt(0) == '#') {
            result.deleteCharAt(0);
        }
        return result.toString();
    }

    /**
     * This method creates a filename for an individual spectrum, based on the filename of the mergefile (in variable
     * 'iFilename'), with an '_[aNumber]' spliced in before the extension (eg., myParent.mgf --> myParent_1.mgf).
     *
     * @param aNumber int with the number to splice into the filename.
     * @return String with a filename for this spectrumfile.
     */
    protected String createSpectrumFilename(int aNumber) {
        int extensionStart = this.iFilename.lastIndexOf(".");
        return this.iFilename.substring(0, extensionStart) + "_" + aNumber + this.iFilename.substring(extensionStart);
    }
}
