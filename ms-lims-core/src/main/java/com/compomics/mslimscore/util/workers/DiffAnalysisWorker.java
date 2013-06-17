/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 23-jun-2004
 * Time: 14:14:20
 */
package com.compomics.mslimscore.util.workers;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mslimscore.util.diff.DiffAnalysisCore;
import com.compomics.mslimscore.util.diff.DifferentialProject;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.SwingWorker;

import javax.swing.*;
import java.sql.SQLException;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.6 $
 * $Date: 2005/09/27 12:38:54 $
 */

/**
 * This class represents a SwingWorker that invokes the component that handles the differential analysis in a separate
 * thread.
 *
 * @author Lennart Martens
 * @version $Id: DiffAnalysisWorker.java,v 1.6 2005/09/27 12:38:54 lennart Exp $
 */
public class DiffAnalysisWorker extends SwingWorker {
    // Class specific log4j logger for DiffAnalysisWorker instances.
    private static Logger logger = Logger.getLogger(DiffAnalysisWorker.class);

    /**
     * This variable contains a reference to the caller.
     */
    private Flamable iFlamable = null;

    /**
     * Th progress bar.
     */
    private DefaultProgressBar iProgress = null;

    /**
     * DiffAnalysisCore component to which the work will be delegated.
     */
    private DiffAnalysisCore iCore = null;

    /**
     * The differential projects to perform the analysis on.
     */
    private DifferentialProject[] iProjects = null;

    /**
     * The ID for the selected instrument.
     */
    private long iInstrumentID = 0l;

    /**
     * The HashMap to store the results in.
     */
    private HashMap iResults = null;

    /**
     * This constructor allows the creation and initialization of this Runner. It takes the necessary arguments to
     * create a workable runner.
     *
     * @param aCore         DiffAnalysisCore that will perform the actual work.
     * @param aParent       Flamable instance that called this worker.
     * @param aProgress     DefaultProgressBar to show the progress on.
     * @param aProjects     DifferentialProject[] with the projects to analyze.
     * @param aInstrumentID long with the ID for the instrument selected.
     * @param aResults      HashMap to contain the results after completion (keys are constants on DiffAnalysisCore).
     *                      This is a reference parameter!
     */
    public DiffAnalysisWorker(DiffAnalysisCore aCore, Flamable aParent, DefaultProgressBar aProgress, DifferentialProject[] aProjects, long aInstrumentID, HashMap aResults) {
        this.iCore = aCore;
        iFlamable = aParent;
        iProgress = aProgress;
        this.iProjects = aProjects;
        this.iInstrumentID = aInstrumentID;
        this.iResults = aResults;
    }

    /**
     * This method presents all gathered data to the DiffAnalysisCore component.
     */
    public Object construct() {
        try {
            iCore.processProjects(iProjects, iInstrumentID, iResults, iProgress);
            iProgress.setValue(iProgress.getMaximum());
        } catch (SQLException sqle) {
            if (iFlamable instanceof JFrame) {
                JFrame parent = (JFrame) iFlamable;
                if (iProgress != null) {
                    iProgress.setVisible(false);
                    iProgress.dispose();
                }
                JOptionPane.showMessageDialog(parent, "Database error occurred: " + sqle.getMessage(), "Database error!", JOptionPane.ERROR_MESSAGE);
            }
            logger.error(sqle.getMessage(), sqle);
        } catch (NumberFormatException nfe) {
            logger.error(nfe.getMessage(), nfe);
            if (iFlamable instanceof JFrame) {
                JFrame parent = (JFrame) iFlamable;
                if (iProgress != null) {
                    iProgress.setVisible(false);
                    iProgress.dispose();
                }
                JOptionPane.showMessageDialog(parent, new String[]{"Recentering error occurred: " + nfe.getMessage(), " ", "You should either reconsider the recentering point", "or", "do not recenter this data at all."}, "Recentering error!", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (iProgress != null) {
                iProgress.setVisible(false);
                iProgress.dispose();
            }
            iFlamable.passHotPotato(e, "Unable to process differential data: " + e.getMessage());
        }
        return "";
    }
}
