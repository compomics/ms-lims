package com.compomics.mslims.util.workers;

import com.compomics.util.sun.SwingWorker;
import com.compomics.util.interfaces.Flamable;
import com.compomics.mslims.util.interfaces.QuantitationStorageEngine;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.rover.general.quantitation.RatioGroupCollection;
import com.compomics.rover.general.enumeration.QuantitationMetaType;

import java.util.Vector;
import java.util.HashMap;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas Colaert
 * Date: 21-nov-2008
 * Time: 10:33:50
 * To change this template use File | Settings | File Templates.
 */


/**
 * This class
 *
 * @author Lennart
 * @version $Id: QuantitationStorageWorker.java,v 1.2 2009/03/11 13:57:45 niklaas Exp $
 */
public class QuantitationStorageWorker extends SwingWorker {



    /**
     * This is the reference to the Vector that holds all results to store.
     */
    private Vector<RatioGroupCollection> iRatioGroupCollections = null;

    /**
     * This variable contains a reference to the caller.
     */
    private Flamable iFlamable = null;

    /**
     * The progress bar.
     */
    private DefaultProgressBar iProgress = null;

    private HashMap<String, Boolean> iWorkerReport;

    private QuantitationStorageEngine iEngine;

    /**
     * This constructor allows the creation and initialization of this Runner.
     * It takes the necessary arguments to create a workable runner.
     *
     * @param aEngine The QuantitationStorageEngine to be used.
     * @param aRatioGroupCollections Vector that holds all the results to store.
     * @param aParent   Flamable instance that called this worker.
     * @param aProgress DefaultProgressBar to show the progress on.
     * @param aWorkerReport The HashMap that stores reporting information.
     */
    public QuantitationStorageWorker(QuantitationStorageEngine aEngine, Vector<RatioGroupCollection> aRatioGroupCollections, Flamable aParent, DefaultProgressBar aProgress, HashMap<String, Boolean> aWorkerReport) {
        iEngine = aEngine;
        iRatioGroupCollections = aRatioGroupCollections;
        iFlamable = aParent;
        iProgress = aProgress;
        iWorkerReport = aWorkerReport;
    }

    /**
     * Runs the thread to store the given RatioGroupCollections one by one.
     */
    public Object construct() {
        for (int i = 0; i < iRatioGroupCollections.size(); i++) {
            RatioGroupCollection lRatioGroupCollection = iRatioGroupCollections.elementAt(i);
            iProgress.setValue(iProgress.getValue()+1);
            boolean status = false;
            try {
                
                status = iEngine.storeQuantitation(lRatioGroupCollection);
            } catch (IOException e) {
                System.err.println("Failing!");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Failing!");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Failing!");
                e.printStackTrace();
            } finally {
                iWorkerReport.put((String) lRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME), status);
            }
        }
        iProgress.setValue(iProgress.getMaximum());
        return "";
    }
}
