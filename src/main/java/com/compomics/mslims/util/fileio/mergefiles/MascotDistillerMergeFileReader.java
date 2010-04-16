package com.compomics.mslims.util.fileio.mergefiles;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.MascotGenericFile;
import com.compomics.mascotdatfile.util.mascot.Query;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 14-okt-2008 Time: 17:00:56 The 'MascotDistillerMergeFileReader ' class
 * was created to adapt the MascotGenericMergeFileReader to specific Mascot Distiller behaviour. More in specific, the
 * creation of the filenames differs. Also thereby we must maintain the 'TITLE' value of of the spectrum that is being
 * processed.
 */
public class MascotDistillerMergeFileReader extends MascotGenericMergeFileReader {
    // Class specific log4j logger for MascotDistillerMergeFileReader instances.
    private static Logger logger = Logger.getLogger(MascotDistillerMergeFileReader.class);

    private String iCurrentSpectrumTitle;
    private String iCurrentSpectrumScans;
    private String iCurrentCharge;


    /**
     * Default constructor.
     */
    public MascotDistillerMergeFileReader() {
    }

    /**
     * This constructor opens the specified mergefile and maps it to memory.
     *
     * @param aMergeFile String with the fully qualified name of the file.
     * @throws java.io.IOException when the file could not be read.
     */
    public MascotDistillerMergeFileReader(final String aMergeFile) throws IOException {
        super(aMergeFile);
    }

    public MascotDistillerMergeFileReader(final File aMergeFile) throws IOException {
        super(aMergeFile);
    }

    /**
     * {@inheritDoc} This extension keeps track of the 'TITLE' value for usage in the 'createSpectrumFilename' method.
     */
    public void load(final File aFile) throws IOException {
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
            boolean lMultiFile = false;
            Vector<String> lMultiRawFileNames = new Vector<String>();
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
                    String cleanLine = super.cleanCommentMarks(line);
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
                // It could that multiple raw files were used to create the mergefile. If there are different lines with something like
                //'_DISTILLER_RAWFILE[0]={1}C:\Users\mascot\...\QstarE04494.wiff' this is the case
                else if (line.startsWith("_DISTILLER_RAWFILE")) {
                    lMultiRawFileNames.add(line.substring(line.lastIndexOf("}")));
                }
                // Not an empty line, not an initial charge line, not a comment line and inside a spectrum.
                // It could be 'BEGIN IONS', 'END IONS', 'TITLE=...', 'PEPMASS=...',
                // in-spectrum 'CHARGE=...' or, finally, a genuine peak line.
                // Whatever it is, add it to the spectrum StringBuffer.
                else if (inSpectrum) {
                    // Adding this line to the spectrum StringBuffer.
                    spectrum.append(line + "\n");

                    // Keep track of the 'TITLE' value for further usage in the filename creation.
                    // Note that this is the only difference with the parent.
                    if (line.indexOf("TITLE") >= 0) {
                        iCurrentSpectrumTitle = line;
                        if (lMultiFile) {
                            //check if this is linked to two files
                            String lTemp = iCurrentSpectrumTitle.substring(iCurrentSpectrumTitle.lastIndexOf("[") + 1, iCurrentSpectrumTitle.lastIndexOf("]"));
                            if (lTemp.indexOf(",") > 0) {
                                //linked to multiple files
                                iCurrentSpectrumTitle = iCurrentSpectrumTitle.substring(0, iCurrentSpectrumTitle.lastIndexOf(lTemp) + (lTemp.substring(0, lTemp.indexOf(","))).length()) + "]";
                            }
                        }
                    }
                    // Keep track of the 'SCANS' value for further usage in the filename creation.
                    if (line.indexOf("SCANS") >= 0) {
                        iCurrentSpectrumScans = line;
                    }

                    // Keep track of the 'CHARGE' value for further usage in the filename creation.
                    if (line.indexOf("CHARGE") >= 0) {
                        iCurrentCharge = line.substring(line.indexOf("=") + 1);
                    }

                    // See if it was an 'END IONS', in which case we stop being in a spectrum.
                    if (line.indexOf("END IONS") >= 0) {
                        // End detected. Much to do!
                        // Reset boolean.
                        inSpectrum = false;
                        // Increment the spectrumCounter by one.
                        spectrumCounter++;
                        String[] lMultiFileNameArray = new String[lMultiRawFileNames.size()];
                        lMultiRawFileNames.toArray(lMultiFileNameArray);

                        if (lMultiFileNameArray.length > 1) {
                            lMultiFile = true;
                        }

                        // Create a filename for the spectrum, based on the filename of the mergefile, with
                        // an '_[spectrumCounter]' before the extension (eg., myParent.mgf --> myParent_1.mgf).
                        String spectrumFilename = this.createSpectrumFilename(spectrumCounter, lMultiFile, lMultiFileNameArray, iCurrentSpectrumScans, iCurrentCharge);
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
            int lStartIndex = this.iFilename.lastIndexOf("~") + 1;

            // MASCOT DISTILLER_QUANTITATION_TOOLBOX SPECIFIC
            int lStopIndex = this.iFilename.toUpperCase().lastIndexOf(".RAW");
            if (lStartIndex > 0) {
                iRunName = this.iFilename.substring(lStartIndex, lStopIndex);
            } else {
                iRunName = this.iFilename;
            }
        }
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
                    } catch (IndexOutOfBoundsException e) {
                        //title could not be parsed
                        result = false;
                    }
                } else {
                    //there should be a title
                    result = false;
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
     * {@inheritDoc}
     */
    protected String createSpectrumFilename(final int aNumber, boolean aMutliFile, String[] aMultiFileNames, String aScans, String aCharge) {
        return Query.processMGFTitleToFilename(iCurrentSpectrumTitle, aMutliFile, aMultiFileNames, aScans, aCharge);
    }

    /**
     * This method returns a filename for the .mgf file custom created by its 'TITLE' value.
     *
     * @param aTitle          The 'TITLE' value from the .mgf file.
     * @param aMutliFile      Boolean that indicates if we must find the filename in the multifilename vector.
     * @param aMultiFileNames Vector with different filenames for the raw files.
     * @param aScans          The 'SCANS' value from the .mgf file.
     * @return The filename as created for the given 'TITLE' by the MascotDistillerMergeFileReader.
     */
    public static String processMGFTitleToFilename(String aTitle, boolean aMutliFile, Vector<String> aMultiFileNames, String aScans) {

        // aNumber is not used as this information is also inside the 'TITLE' field.
        // We prefer to use the counter from the 'TITLE' field as this returns identically in
        // the Mascot Result files.

        // Example:
        // a single scan
        // TITLE=704: Scan 1440 (rt=17.7728) [C:\XCalibur\data\data_linda\L59_Bart_Metox_080530A_forward_p2A01.RAW]
        //
        // b summed scan
        // TITLE=705: Sum of 2 scans in range 1441 (rt=17.7785) to 1651 (rt=19.1359) [C:\XCalibur\data\data_linda\L59_Bart_Metox_080530A_forward_p2A01.RAW]
        //
        // c multifile
        // TITLE=1: Sum of 4 scans in range rt=639.562 to rt=658.607 from file [0]
        // The "0" is an index reference to the filename in the multifilename vector
        // SCANS=687 or SCANS=678-687

        String lLCRun = null;
        int lCompound = -1;
        int lBeginScan = -1;
        int lEndScan = -1;
        int lSumOfScans = 1;

        // a) Parse the Lcrun
        if (aMutliFile) {
            //it's a multifile
            //get the index from "from file [index]"
            int lIindex = Integer.valueOf(aTitle.substring(aTitle.lastIndexOf("[") + 1, aTitle.lastIndexOf("]")));
            lLCRun = aMultiFileNames.get(lIindex).substring(aMultiFileNames.get(lIindex).lastIndexOf("\\" + 1, aMultiFileNames.get(lIindex).lastIndexOf(".")));
        } else {
            //not a multifile, parse the lcrun from the title string
            lLCRun = aTitle.substring(aTitle.lastIndexOf('\\') + 1, aTitle.lastIndexOf('.'));
        }

        if (aTitle.indexOf("TITLE=") == 0) {
            aTitle = aTitle.substring(6);
        }

        // b) Parse the compound number
        lCompound = Integer.valueOf(aTitle.substring(0, aTitle.indexOf(':')));

        // c) Find out the sum of scans
        if (aTitle.indexOf("Sum") >= 0) {
            // c1 Multiple scans from this spectrum!
            if (aMutliFile) {
                //in the multifile title the scans are not there, it has to be parsed from the aScans string
                lBeginScan = Integer.valueOf(aScans.substring(aScans.indexOf("=") + 1, aScans.indexOf("-")));
                lEndScan = Integer.valueOf(aScans.substring(aScans.indexOf("-") + 1));
            } else {
                lBeginScan = Integer.valueOf(aTitle.substring(aTitle.indexOf("range ") + 6, aTitle.indexOf(" (rt=")));
                lEndScan = Integer.valueOf(aTitle.substring(aTitle.indexOf(") to ") + 5, aTitle.lastIndexOf(" (rt=")));
            }
            lSumOfScans = Integer.valueOf(aTitle.substring(aTitle.indexOf("Sum of ") + 7, aTitle.lastIndexOf(" scans ")));
        } else {
            // c2 Single scan form this spectrum!
            if (aMutliFile) {
                //in the multifile title the scan is not there, it has to be parsed from the aScans string
                lBeginScan = Integer.valueOf(aScans.substring(aScans.indexOf("=") + 1));
            } else {
                lBeginScan = Integer.valueOf(aTitle.substring(aTitle.indexOf("Scan ") + 5, aTitle.indexOf(" (rt=")));
            }
        }

        String lResult = "";
        if (lSumOfScans == 1) {
            // Single scan
            lResult = lLCRun + "_" + lCompound + "_" + lBeginScan + "_" + lSumOfScans + ".mgf";
        } else if (lSumOfScans > 1) {
            // Summed scan
            lResult = lLCRun + "_" + lCompound + "_" + lBeginScan + "." + lEndScan + "_" + lSumOfScans + ".mgf";
        }
        return lResult;
    }


}
