/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 23-jun-2004
 * Time: 14:14:20
 */
package com.compomics.mslims.util.workers;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Identification;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.gui.tree.MascotSearch;
import com.compomics.mslims.util.mascot.MascotResultsProcessor;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.SwingWorker;

import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2007/02/05 15:05:35 $
 */

/**
 * This class
 *
 * @author Lennart
 * @version $Id: MascotResultsProcessorWorker.java,v 1.5 2007/02/05 15:05:35 kenny Exp $
 */
public class MascotResultsProcessorWorker extends SwingWorker {
    // Class specific log4j logger for MascotResultsProcessorWorker instances.
    private static Logger logger = Logger.getLogger(MascotResultsProcessorWorker.class);

    /**
     * MascotResultsProcessor that will handling all the effective processing.
     */
    private MascotResultsProcessor iProcessor = null;

    /**
     * This is the reference to the Vector that will hold all results.
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
     * Th progress bar.
     */
    private DefaultProgressBar iProgress = null;

    /**
     * This constructor allows the creation and initialization of this Runner. It takes the necessary arguments to
     * create a workable runner.
     *
     * @param aProcessor   MascotResultsProcessor that will handling all the effective processing.
     * @param aAllSearches MascotSearch[] that holds the searches to process.
     * @param aAllResults  Vector to hold the results of the processed searches. Note that this is a reference
     *                     parameter!
     * @param aParent      Flamable instance that called this worker.
     * @param aProgress    DefaultProgressBar to show the progress on.
     */
    public MascotResultsProcessorWorker(MascotResultsProcessor aProcessor, MascotSearch[] aAllSearches, Vector aAllResults, Flamable aParent, DefaultProgressBar aProgress) {
        iProcessor = aProcessor;
        iAllSearches = aAllSearches;
        iAllResults = aAllResults;
        iFlamable = aParent;
        iProgress = aProgress;
    }

    /**
     * This method presents all searches to process to the specified MascotResultsProcessor.
     */
    public Object construct() {
        Vector tempAll = new Vector(100, 50);
        for (int i = 0; i < iAllSearches.length; i++) {
            MascotSearch lSearch = iAllSearches[i];
            Vector temp = iProcessor.processIDs(lSearch.getMergefile(), lSearch.getDatfile(), iFlamable, iProgress);
            tempAll.addAll(temp);
        }
        // Now cycle to check for uniqueness.
        int resultSize = tempAll.size();
        if (iProgress != null) {
            iProgress.setValue(iProgress.getValue() + 1);
            iProgress.setMessage("Checking for unique spectra...");
        }
        // This HashMap will hold filename - IdentificationTableAccessor combinations.
        // Whenever a spectrum is identified twice, we can pick it out thanks to this HashMap.
        HashMap uniquenessChecker = new HashMap(resultSize);
        // At this point we should try to compensate for multiple occurrences of a single
        // spectrum. This is possible due to the buggy way that Mascot handles multiple
        // charge states. In particular, a non-charge assigned query is split in 'n' new queries,
        // with 'n' the number of possible charge states. If more than one charge state scores above
        // threshold, we would be inserting a duplicate identification for the spectrum.
        // Therefore, only retain the identification with the highest score here.
        for (int i = 0; i < resultSize; i++) {
            Object tempObj = tempAll.get(i);
            if (tempObj instanceof Identification) {
                Identification ita = (Identification) tempObj;
                String name = ita.getTemporarySpectrumfilename();
                if (uniquenessChecker.containsKey(name)) {
                    Identification oldID = (Identification) uniquenessChecker.get(name);
                    long oldScore = oldID.getScore();
                    if (oldScore < ita.getScore()) {
                        uniquenessChecker.put(name, ita);
                    } else if (oldScore == ita.getScore()) {
                        // If one spectrum scores likewise in two identifications, store the most confident identification.
                        // ex: When a spectrum is searched twice against a normal swissprot and a truncated swissprot database)
                        long oldDeltaThreshold = oldScore - oldID.getIdentitythreshold();
                        long itaDeltaThreshold = ita.getScore() - ita.getIdentitythreshold();
                        if (oldDeltaThreshold < itaDeltaThreshold) {
                            uniquenessChecker.put(name, ita);
                        }
                    }
                } else {
                    uniquenessChecker.put(name, ita);
                }
            } else {
                // No IdentificationTableAncestor, just let it pass.
                iAllResults.add(tempObj);
            }
        }
        if (iProgress != null) {
            iProgress.setValue(iProgress.getValue() + 1);
            iProgress.setMessage("Combining unique results...");
        }
        // Now to add all the uniques.
        iAllResults.addAll(uniquenessChecker.values());

        iProgress.setValue(iProgress.getValue() + 1);
        return "";
    }
}
