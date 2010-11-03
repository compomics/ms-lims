package com.compomics.mslims.util.workers;

import be.proteomics.util.sun.SwingWorker;
import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.fileio.filters.MascotGenericFileFilter;
import com.compomics.mslims.util.fileio.mergefiles.MascotGenericMergeFileReader;
import com.compomics.util.interfaces.Flamable;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: kennyhelsens
 * Date: Oct 25, 2010
 * Time: 1:01:50 PM
 */
public class LoadMicroTOFQWorker extends SwingWorker {
    // Class specific log4j logger for LoadUltraflexXMLWorker instances.
    private static Logger logger = Logger.getLogger(LoadMicroTOFQWorker.class);

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

    public LoadMicroTOFQWorker(File[] aList, Vector aStoredLCRuns, Vector aFoundLCRuns, Flamable aParent, DefaultProgressBar aProgress) {
        list = aList;
        iStoredLCs = aStoredLCRuns;
        names = aFoundLCRuns;
        outer = aParent;
        progress = aProgress;
    }

    /**
     * This method reads all the LC runs from the list of files and initializes the 'names' instance variable with the
     * corresponding LCRun objects.
     */
    public Object construct() {
        try {
            for (int i = 0; i < list.length; i++) {
                File file = list[i];
                // Create the LCRun name, based on this name.
                String name = file.getName();

                if (name.endsWith(".d")) {
                    int lEndIndex = name.lastIndexOf(".d");
                    // Cut the trailing ".d".
                    name = name.substring(0, lEndIndex);
                    // Replace any spaces with '_'.
                    name = name.replace(' ', '_');
                    // If Directory, and non existing lrrunname.
                    if (file.isDirectory() && !iStoredLCs.contains(name)) {
                        // Filter for child .mgf files.
                        File[] lMGFFiles = file.listFiles(new MascotGenericFileFilter());
                        if (lMGFFiles.length > 0) {
                            int lCounter = 0;
                            for (int j = 0; j < lMGFFiles.length; j++) {
                                MascotGenericMergeFileReader lMascotGenericMergeFileReader = new MascotGenericMergeFileReader(lMGFFiles[j]);
                                lCounter = lCounter + lMascotGenericMergeFileReader.getSpectrumFiles().size();
                            }
                            LCRun tempC = new LCRun(name, 1, lCounter);
                            tempC.setPathname(file.getCanonicalPath());
                            names.add(tempC);
                        }
                    }
                }
                progress.setValue(i + 1);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return "";
    }
}
