/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 23-jun-2004
 * Time: 14:14:20
 */
package com.compomics.mslimscore.util.workers;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mslimscore.util.interfaces.QuantitationProcessor;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.SwingWorker;
import com.compomics.mslimscore.util.quantitation.ratios.RatioGroupCollection;
import com.compomics.mslimscore.util.enumeration.QuantitationMetaType;

import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2009/05/18 08:01:11 $
 */

/**
 * This class
 *
 * @author Lennart
 * @version $Id: QuantitationWorker.java,v 1.3 2009/05/18 08:01:11 niklaas Exp $
 */
public class QuantitationWorker extends SwingWorker {
    // Class specific log4j logger for QuantitationWorker instances.
    private static Logger logger = Logger.getLogger(QuantitationWorker.class);

    /**
     * RovFileProcessorWorker that will handling all the effective processing.
     */
    private QuantitationProcessor iQuantitationProcessor = null;

    /**
     * This is the reference to the Vector that will hold all results.
     */
    private Vector<RatioGroupCollection> iResultsByReference = null;

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
     * @param aQuantitationProcessor RovFileProcessorWorker that will handling all the effective processing.
     * @param aResultsByReference    Vector to hold the results of the processed searches. Note that this is a reference
     *                               parameter!
     * @param aParent                Flamable instance that called this worker.
     * @param aProgress              DefaultProgressBar to show the progress on.
     */
    public QuantitationWorker(QuantitationProcessor aQuantitationProcessor, Vector aResultsByReference, Flamable aParent, DefaultProgressBar aProgress) {
        iQuantitationProcessor = aQuantitationProcessor;
        iResultsByReference = aResultsByReference;
        iFlamable = aParent;
        iProgress = aProgress;
    }


    /**
     * This method performs all the processing to be done by the QuantitationProcessor. All resulting
     * RatioGroupCollections are stored in a Reference Vector.
     */
    public Object construct() {
        Vector<RatioGroupCollection> tempAll = new Vector<RatioGroupCollection>(100, 50);
        while (iQuantitationProcessor.hasNext()) {
            RatioGroupCollection lCollection = iQuantitationProcessor.next();

            try {
                if (iProgress != null && lCollection != null) {
                    iProgress.setValue(iProgress.getValue() + 1);
                    tempAll.add(lCollection);
                    if (Class.forName("com.compomics.mslimscore.util.quantitation.fileio.Ms_limsiTraqQuantitationProcessor").isInstance(iQuantitationProcessor)) {
                        String lDatfileName = lCollection.getMetaData(QuantitationMetaType.FILENAME).toString();
                        iProgress.setMessage("Processing datfile '" + lDatfileName + "'");
                    } else if (Class.forName("com.compomics.mslimscore.util.quantitation.fileio.MascotQuantitationProcessor").isInstance(iQuantitationProcessor)) {
                        String lRunName = lCollection.getMetaData(QuantitationMetaType.RUNNAME).toString();
                        iProgress.setMessage("Processing Distiller rov file for run '" + lRunName + "'");
                    }
                }
            } catch (ClassNotFoundException e) {
                iFlamable.passHotPotato(new Throwable(e.getMessage()));
            }
        }

        iResultsByReference.addAll(tempAll);
        iProgress.setValue(iProgress.getMaximum());
        return "";
    }
}
