package com.compomics.mslimscore.util.workers;

import org.apache.log4j.Logger;

import com.compomics.util.sun.SwingWorker;
import com.compomics.util.interfaces.Flamable;
import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mslimsdb.accessors.LCRun;
import com.compomics.mslimscore.util.fileio.mergefiles.MergeFileReaderFactory;
import com.compomics.mslimscore.util.fileio.mergefiles.MascotGenericMergeFileReader;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-sep-2007
 * Time: 14:55:42
 */

/**
 * This class loads all the MGF MS/MS spectra from a ABI 4800 - T2SMascot output specified folder. This class locates al
 * lcruns from a given topfolder.
 *
 * @author Helsens Kenny 2007-09-05
 */
public class Load4800MGFWorker extends SwingWorker {
    // Class specific log4j logger for Load4800MGFWorker instances.
    private static Logger logger = Logger.getLogger(Load4800MGFWorker.class);

    /**
     * The list of files to browse through.
     */
    private File[] list = null;

    /**
     * This Vector holds the LC runs that are already stored in the DB.
     */
    private Vector iStoredLCs = null;

    /**
     * This Vector will hold the names of the found LC runs.
     */
    private Vector names = null;

    /**
     * This variable contains a reference to the caller.
     */
    private Flamable outer = null;

    /**
     * Th progress bar.
     */
    private DefaultProgressBar progress = null;

    /**
     * This constructor allows the creation and initialization of this Runner. It takes the necessary arguments to
     * create a workable runner.
     *
     * @param aList     File[] with the list of files to cycle through while looking for LC runs.
     * @param aStored   Vector with the names of the LC runs that are already stored in the DB.
     * @param aNames    Vector that is treated like a <b>reference parameter</b> and that will contain the LCRun
     *                  instances found in the specified list of files.
     * @param aParent   EsquireSpectrumStorage instance that called this worker.
     * @param aProgress DefaultProgressBar to show the progress on.
     */
    public Load4800MGFWorker(File[] aList, Vector aStored, Vector aNames, Flamable aParent, DefaultProgressBar aProgress) {
        list = aList;
        iStoredLCs = aStored;
        names = aNames;
        outer = aParent;
        progress = aProgress;
    }

    /**
     * This method reads all the LC runs from the list of files and initializes the 'names' instance variable with the
     * corresponding LCRun objects.
     */
    public Object construct() {
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            // Create the LCRun name, based on this name and the name of the parent.
            String name = file.getName();
            if (file.isDirectory()) {
                // Get all the MGF files for this subdirectory.
                Vector msmsFiles = new Vector(10, 5);
                try {
                    this.recurseForMsmsMGFFiles(file, msmsFiles);
                } catch (IOException ioe) {
                    outer.passHotPotato(ioe, "Unable to scan for MS/MS MGF files in '" + file.getName() + "'!");
                }
                // Only do this when ms/ms scans have been found.
                if (msmsFiles.size() > 0) {
                    LCRun tempC = new LCRun(name, 1, msmsFiles.size());
                    try {
                        tempC.setPathname(file.getCanonicalPath());
                    } catch (IOException ioe) {
                        outer.passHotPotato(ioe, "Unable to locate MGF-files for '" + file.getName() + "'!");
                    }
                    // Adding the scan to the list.
                    names.add(tempC);
                }
            }
            progress.setValue(i + 1);
        }
        return "";
    }


    /**
     * This method attempts to find all the ms/ms spectra in the specified folder.
     *
     * @param aParent    File with the starting point for the recursive search.
     * @param aMSMSFiles Vector  that will contain the Files for all the MS/MS MGF files.
     * @throws java.io.IOException whenever the folder could not be read recursively.
     */
    private void recurseForMsmsMGFFiles(File aParent, Vector aMSMSFiles) throws IOException {
        // Check out the dir.
        File[] files = aParent.listFiles();
        // Cycle all found items.
        for (int i = 0; i < files.length; i++) {
            File lFile = files[i];
            // Directories are recursed, when MGF MS/MS files are found, these are added.
            if (lFile.isDirectory()) {
                // Keep trying.
                this.recurseForMsmsMGFFiles(lFile, aMSMSFiles);
            } else if (lFile.getName().toUpperCase().endsWith(".MGF")) {
                // Try to load each file.
                try {
                    Object temp = MergeFileReaderFactory.getReaderForMergeFile(lFile);
                    if (temp instanceof MascotGenericMergeFileReader) {
                        MascotGenericMergeFileReader mfr = (MascotGenericMergeFileReader) temp;
                        // Only store this stuff if the LC run is not already in the database.
                        if (!iStoredLCs.contains(mfr.getRunName())) {
                            // Get the number of spectra contained in this run.
                            int count = mfr.getSpectrumFiles().size();
                            // Get the name for this LC run.
                            String name = mfr.getRunName();
                            // Create a new LCRun instance based on the data gathered.
                            LCRun tempC = new LCRun(name, 0, count);
                            // Now set description and the pathname.
                            String comments = mfr.getComments();
                            if (comments != null && comments.trim().equals("")) {
                                comments = null;
                            }
                            tempC.setDescription(comments);
                            try {
                                tempC.setPathname(lFile.getCanonicalPath());
                            } catch (IOException ioe) {
                                outer.passHotPotato(ioe, "Unable to locate file for LC run '" + mfr.getRunName() + "'!");
                            }
                            // Add the new LCRun to the 'names' Vector.
                            names.add(tempC);
                        }
                    } else {
                        // Do nothing.
                    }
                } catch (IOException ioe) {
                    // Skip this file, it's not readable, so probably not an LC run file.
                }

            }
        }
    }
}
