/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 23-jun-2004
 * Time: 14:14:20
 */
package com.compomics.mslimscore.util.workers;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mslimscore.gui.tree.MascotSearch;
import com.compomics.mslimscore.util.mascot.MascotResultsProcessor;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.SwingWorker;

import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2007/10/12 15:38:36 $
 */

/**
 * This class
 *
 * @author Lennart
 * @version $Id: MascotResultsStorageWorker.java,v 1.3 2007/10/12 15:38:36 lennart Exp $
 */
public class MascotResultsStorageWorker extends SwingWorker {
    // Class specific log4j logger for MascotResultsStorageWorker instances.
    private static Logger logger = Logger.getLogger(MascotResultsStorageWorker.class);

    /**
     * MascotResultsProcessor that will handling all the effective processing.
     */
    private MascotResultsProcessor iProcessor = null;

    /**
     * This is the reference to the Vector that holds all results to store.
     */
    private Vector iAllResults = null;

    /**
     * This is the array that holds all the searches to process.
     */
    private MascotSearch[] iAllSearches = null;

    /**
     * This variable contains a reference to the caller.
     */
    private Flamable iFlamable = null;

    /**
     * The progress bar.
     */
    private DefaultProgressBar iProgress = null;

    /**
     * This constructor allows the creation and initialization of this Runner. It takes the necessary arguments to
     * create a workable runner.
     *
     * @param aProcessor   MascotResultsProcessor that will handling all the effective processing.
     * @param aAllResults  Vector that holds all the results to store.
     * @param aAllSearches MascotSearch[] that holds all seraches.
     * @param aParent      Flamable instance that called this worker.
     * @param aProgress    DefaultProgressBar to show the progress on.
     */
    public MascotResultsStorageWorker(MascotResultsProcessor aProcessor, Vector aAllResults, MascotSearch[] aAllSearches, Flamable aParent, DefaultProgressBar aProgress) {
        iProcessor = aProcessor;
        iAllResults = aAllResults;
        iAllSearches = aAllSearches;
        iFlamable = aParent;
        iProgress = aProgress;
    }

    /**
     * This method presents all searches to process to the specified MascotResultsProcessor.
     */
    public Object construct() {
        iProcessor.storeData(iAllResults, iFlamable, iProgress);
        iProgress.setValue(iProgress.getValue() + 1);
        return "";
    }
}
