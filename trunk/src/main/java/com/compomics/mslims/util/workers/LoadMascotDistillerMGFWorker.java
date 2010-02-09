/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 16-jul-2003
 * Time: 16:06:27
 */
package com.compomics.mslims.util.workers;

import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.fileio.mergefiles.MascotGenericMergeFileReader;
import com.compomics.mslims.util.fileio.mergefiles.MergeFileReaderFactory;
import com.compomics.mslims.util.fileio.mergefiles.MergeFileReaderAncestor;
import com.compomics.mslims.util.fileio.mergefiles.MascotDistillerMergeFileReader;
import com.compomics.mslims.util.fileio.FileExtensionFilter;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.SwingWorker;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.ArrayList;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2008/11/28 16:07:18 $
 */

/**
 * This class loads all the LC runs from a specified folder.
 *
 * @author Lennart Martens
 */
public class LoadMascotDistillerMGFWorker extends SwingWorker {

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
    private Vector names=  null;

    /**
     * This variable contains a reference to the caller.
     */
    private Flamable outer = null;

    /**
     * Th progress bar.
     */
    private DefaultProgressBar progress = null;

    /**
     * This constructor allows the creation and initialization of this Runner.
     * It takes the necessary arguments to create a workable runner.
     *
     * @param aList File[] with the list of files to cycle through while looking for LC runs.
     * @param aStored   Vector with the names of the LC runs that are already stored in the DB.
     * @param aNames    Vector that is treated like a <b>reference parameter</b> and that will contain the
     *                  LCRun instances found in the specified list of files.
     * @param aParent   EsquireSpectrumStorage instance that called this worker.
     * @param aProgress DefaultProgressBar to show the progress on.
     */
    public LoadMascotDistillerMGFWorker(File[] aList, Vector aStored, Vector aNames, Flamable aParent, DefaultProgressBar aProgress) {
        list = aList;
        iStoredLCs = aStored;
        names = aNames;
        outer = aParent;
        progress = aProgress;
    }


    /**
     * This method reads all the LC runs from the list of files and initializes the 'names' instance variable
     * with the corresponding LCRun objects.
     */
    public Object construct() {
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            // Try to load each file.
            try  {
                MascotDistillerMergeFileReader mfr = new MascotDistillerMergeFileReader(file);
                // Only store this stuff if the LC run is not already in the database.
                if(!iStoredLCs.contains(mfr.getRunName())) {
                    // Get the number of spectra contained in this run.
                    int count = mfr.getSpectrumFiles().size();
                    // Get the name for this LC run.
                    String name = mfr.getRunName();
                    // Create a new LCRun instance based on the data gathered.
                    LCRun tempC = new LCRun(name, 0, count);
                    // Now set description and the pathname.
                    String comments = mfr.getComments();
                    if(comments != null && comments.trim().equals("")) {
                        comments = null;
                    }
                    tempC.setDescription(comments);
                    try {
                        tempC.setPathname(file.getCanonicalPath());
                    } catch(IOException ioe) {
                        outer.passHotPotato(ioe, "Unable to locate file for LC run '" +  mfr.getRunName() +"'!");
                    }
                    // Add the new LCRun to the 'names' Vector.
                    names.add(tempC);
                }
            } catch(IOException ioe) {
                // Skip this file, it's not readable, so probably not an LC run file.
            }
            progress.setValue(i+1);
        }
        return "";
    }
}