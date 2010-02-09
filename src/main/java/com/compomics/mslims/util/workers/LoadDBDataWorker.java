package com.compomics.mslims.util.workers;

/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 17-feb-2005
 * Time: 12:04:07
 */

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/17 15:33:52 $
 */

import com.compomics.mslims.db.accessors.Identification;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.SwingWorker;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class implements a SwingWorker that loads identifications from the DB for a given Collection of
 * spectra and that adapts a DefaultProgressBar in the process.
 *
 * @author Lennart Martens
 * @version $Id: LoadDBDataWorker.java,v 1.1 2005/02/17 15:33:52 lennart Exp $
 */
public class LoadDBDataWorker extends SwingWorker {
    /**
     * The progressbar to show progress on.
     */
    private DefaultProgressBar iProgress = null;

    /**
     * The DB connection to read the identifications from.
     */
    private Connection iConn = null;

    /**
     * The HashMap to store the mappings in. This is passed as a reference parameter!
     */
    private HashMap iSpectrumToID = null;

    /**
     * The Collection with spectra to search the identifications for.
     */
    private Collection iSpectraNames = null;

    /**
     * The flamable parent to inform of any errors.
     */
    private Flamable iParent = null;

    /**
     * This constructor takes all the initialization parameters necessary. Note that the 'aSpectrumToID' HashMap parameter
     * is a reference parameter and that the results of the SwingWorker's efforts will be stored in here.
     *
     * @param aParent   Flamable instance that will receive error notifications, if any.
     * @param aConn Connection to read the identifications from.
     * @param aSpectrumToID HashMap to store the mappings in. Spectrum filename (String) will be key, Identification instance
     *                      will be value, if any - else the value will be 'null'. Note that this is a reference parameter!
     * @param aSpectraNames Collection with the spectrum filenames to search identifications for.
     * @param aProgress DefaultProgressBar to display the progress on.
     */
    public LoadDBDataWorker(Flamable aParent, Connection aConn, HashMap aSpectrumToID, Collection aSpectraNames, DefaultProgressBar aProgress) {
        iParent = aParent;
        iProgress = aProgress;
        iConn = aConn;
        iSpectrumToID = aSpectrumToID;
        iSpectraNames = aSpectraNames;
    }

    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public Object construct() {
        // Read all identifications and adjust progressbar accordingly.
        String filename = null;
        try {
            for (Iterator lIterator = iSpectraNames.iterator(); lIterator.hasNext();) {
                filename = (String)lIterator.next();
                iProgress.setMessage("Reading identifications for " + filename + "...");
                Identification id = Identification.getIdentification(iConn, filename);
                iSpectrumToID.put(filename, id);
                iProgress.setValue(iProgress.getValue()+1);
            }
            iProgress.setValue(iProgress.getMaximum());
            iProgress.dispose();
        } catch(Exception e) {
            iParent.passHotPotato(e, "Unable to read possible identification for spectrum '" + filename + "': " + e.getMessage());
        }
        return "";
    }
}