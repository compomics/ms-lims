/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 16-jul-2003
 * Time: 16:06:27
 */
package com.compomics.mslims.util.workers;

import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.SwingWorker;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class loads all the XML spectra from a specified folder.
 *
 * @author Lennart Martens
 */
public class LoadUltraflexXMLWorker extends SwingWorker {
    // Class specific log4j logger for LoadUltraflexXMLWorker instances.
    private static Logger logger = Logger.getLogger(LoadUltraflexXMLWorker.class);

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
    public LoadUltraflexXMLWorker(File[] aList, Vector aStored, Vector aNames, Flamable aParent, DefaultProgressBar aProgress) {
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
            String name = file.getParentFile().getName() + "_" + file.getName();
            name = name.replace(' ', '_');
            if (file.isDirectory() && !iStoredLCs.contains(name)) {
                // Get all the xml files for this subdirectory.
                Vector msmsFiles = new Vector(10, 5);
                try {
                    this.recurseForXMLs(file, msmsFiles);
                } catch (IOException ioe) {
                    outer.passHotPotato(ioe, "Unable to scan for XML files in '" + file.getName() + "'!");
                }
                // Only do this when ms/ms scans have been found.
                if (msmsFiles.size() > 0) {
                    LCRun tempC = new LCRun(name, 1, msmsFiles.size());
                    try {
                        tempC.setPathname(file.getCanonicalPath());
                    } catch (IOException ioe) {
                        outer.passHotPotato(ioe, "Unable to locate XML-files for '" + file.getName() + "'!");
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
     * This method attempts to find all the lift spectra in the specified folder.
     *
     * @param aParent    File with the starting point for the recursive search.
     * @param aMSMSFiles Vector  that will contain the names of all the folders that start with 'lift' (regardless of
     *                   case)
     * @throws IOException whenever the folder could not be read recursively.
     */
    private void recurseForXMLs(File aParent, Vector aMSMSFiles) throws IOException {
        // Check out the dir.
        File[] files = aParent.listFiles();
        // Cycle all found items.
        for (int i = 0; i < files.length; i++) {
            File lFile = files[i];
            if (lFile.isDirectory()) {
                if (isLiftFolder(lFile)) {
                    aMSMSFiles.add(lFile.getName());
                } else {
                    // Keep trying.
                    this.recurseForXMLs(lFile, aMSMSFiles);
                }
            }
        }
    }

    /**
     * This method returns whether the given File is a required LIFT folder.
     *
     * @param aFile Diretory with appropriate filename.
     * @return True if the folder contains ".lift.lift", ".lift" or ".lift_2".
     */
    public static boolean isLiftFolder(File aFile) {
        // Only look at directories.
        // If the dirname contains '.lift.lift' (regardless of case),
        // it's a liftspectrum.

        if (aFile.getName().toLowerCase().indexOf(".lift.lift") >= 0) {
            return true;
        } else if (aFile.getName().toLowerCase().indexOf(".lift") >= 0) {
            return true;
        } else if (aFile.getName().toLowerCase().indexOf(".lift_2") >= 0) {
            return true;
        }
        // No diretory,
        return false;
    }
}
