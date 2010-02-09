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
import com.compomics.util.io.FilenameExtensionFilter;
import com.compomics.util.sun.SwingWorker;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2005/03/04 08:36:12 $
 */

/**
 * This class loads all the dta spectra from a specified folder recursively.
 *
 * @author Lennart Martens
 */
public class LoadDTAWorker extends SwingWorker {

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
    private Vector iNames=  null;

    /**
     * This variable contains a reference to the caller.
     */
    private Flamable iOuter = null;

    /**
     * Th progress bar.
     */
    private DefaultProgressBar iProgress = null;

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
    public LoadDTAWorker(File[] aList, Vector aStored, Vector aNames, Flamable aParent, DefaultProgressBar aProgress) {
        list = aList;
        iStoredLCs = aStored;
        iNames = aNames;
        iOuter = aParent;
        iProgress = aProgress;
    }

    /**
     * This method reads all the LC runs from the list of files and initializes the 'names' instance variable
     * with the corresponding LCRun objects.
     */
    public Object construct() {
        for (int i = 0; i < list.length; i++) {
            File file = list[i];
            // Cycle all the items in the directory.
            if(file.isDirectory()) {
                // Find all the LCRun folders (ie, folders that contain '.dta' files.
                try {
                    this.recurseForDTAs(file);
                } catch(IOException ioe) {
                    iOuter.passHotPotato(ioe, "Unable to scan for XML files in '" + file.getName() + "'!");
                }
            }
            iProgress.setValue(i+1);
        }
        return "";
    }


    /**
     * This method attempts to find dta spectra in the specified folders, if it does find them,
     * the folder above the dta files (the parent of those files) is considered to be the LCRun,
     * the count of files is added to this and the process continues. Note that this method recurses.
     *
     * @param aParent   File with the starting point for the recursive search.
     * @throws java.io.IOException  whenever the folder could not be read recursively.
     */
    private void recurseForDTAs(File aParent) throws IOException {
        // Check out the dir for '.dta' files.
        FilenameExtensionFilter ffe = new FilenameExtensionFilter(".dta");
        String[] contents = aParent.list(ffe);
        // If dtaContent > 0, this folder contains dta files.
        // We should therefore store it as an LCRun and that will be it.
        // Note that no further recursion for this folder is thus
        // carried out in this case!
        if(contents != null && contents.length > 0 && !iStoredLCs.contains(aParent.getName())) {
            String name = aParent.getName();
            String path = aParent.getAbsolutePath();
            LCRun run = new LCRun(name, 1, contents.length);
            run.setPathname(path);
            iNames.add(run);
        } else {
            // Okay, no dta files found.
            // We can continue this way, so subject each directory
            // in this directory in turn to the recursive process.
            File[] files = aParent.listFiles();
            if(files != null && files.length > 0) {
                // Cycle all found items.
                for (int i = 0; i < files.length; i++) {
                    File lFile = files[i];
                    // Only look at directories.
                    if(lFile.isDirectory()) {
                        this.recurseForDTAs(lFile);
                    }
                }
            }
        }
    }
}
