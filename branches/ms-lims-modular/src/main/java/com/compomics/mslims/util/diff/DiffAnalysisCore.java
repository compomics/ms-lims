/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 11-okt-2004
 * Time: 7:40:41
 */
package com.compomics.mslims.util.diff;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Identification;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import be.proteomics.statlib.descriptive.BasicStats;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.12 $
 * $Date: 2006/02/04 10:07:05 $
 */

/**
 * This class carries out a full, non-redundant (at the peptide sequence level) differential analysis for any number of
 * projects. The analysis is based on the robust Huber scale estimator and the median for location.
 *
 * @author Lennart Martens
 * @version $Id: DiffAnalysisCore.java,v 1.12 2006/02/04 10:07:05 lennart Exp $
 */
public class DiffAnalysisCore {
    // Class specific log4j logger for DiffAnalysisCore instances.
    private static Logger logger = Logger.getLogger(DiffAnalysisCore.class);
    /**
     * This variable causes the average ratio for merged couples to be calculated as the weighted average ratios. The
     * calculation comprises several stages. First, all the individual ratios are calculated, then, all light
     * intensities are summed and the relative contribution of each light intensity is calculated for each couple. The
     * same is done for the heavy intensities. Next, all ratios are multiplied by this relative contribution for total
     * light intensity and the results are summed. the same applies for the heavy intensities. We now have two weighted
     * average ratios: one for heavy and one for light intensities. The final result is the average of these two.<br />
     * <i>Example:</i> <br /> (34,11), (23,12), (36, 20), (48, 15) averaged with WEIGHTED_RATIOS, yields a ratio of:
     * 2.503. <br /> A very intense yet aberrant signal still influences the average. <br /> <i>Example:</i> <br />
     * (1234,1311), (23,12), (36, 20), (48, 15) averaged with WEIGHTED_RATIOS, yields the a ratio of: 1.034. The
     * reported average is thus weighted.
     */
    public static final int WEIGHTED_RATIOS = 0;

    /**
     * This variable causes the average ratio for merged couples to be calculated as the average of all the individual
     * ratios.<br /> <i>Example:</i> <br /> (34,11), (23,12), (36, 20), (48, 15) averaged with AVERAGE_RATIOS, yields
     * the individual ratios (3.09), (1.92), (1.80), (3.20). The average ratio then is: 2.5025. <br /> A very intense
     * yet aberrant signal no longer greatly influences the average. <br /> <i>Example:</i> <br /> (1234,1311), (23,12),
     * (36, 20), (48, 15) averaged with AVERAGE_RATIOS, yields the individual ratios of (0.94), (1.92), (1.80), (3.20).
     * The average ratio then is: 1.965. You can see that the first measurement (ratio appr. 1) influences the trend in
     * the subsequent measurements only as one out of four measurements (ratios appr. 2.5). The reported average is not
     * weighted.
     */
    public static final int AVERAGE_RATIOS = 1;

    /**
     * The constant that signals that the caller wants to use robust statistics.
     */
    public static final int ROBUST_STATISTICS = 0;
    /**
     * The constant that signals that the caller wants to use standard statistics.
     */
    public static final int STANDARD_STATISTICS = 1;

    /**
     * The key in the results HashMap for the location estimator.
     */
    public static final String MU_HAT = "MU_HAT";

    /**
     * The key in the results HashMap for the scale estimator.
     */
    public static final String SIGMA_HAT = "SIGMA_HAT";

    /**
     * The instrument calibration error.
     */
    public static final String INSTRUMENT_STDEV = "INSTRUMENT_STDEV";

    /**
     * The key in the results HashMap for the number of data elements used.
     */
    public static final String COUNT = "COUNT";

    /**
     * The key in the results HashMap for the number of iterations used to achhieve convergence.
     */
    public static final String ITERATIONS = "ITERATIONS";

    /**
     * The key in the results HashMap for the DiffCouples Vector.
     */
    public static final String DIFFCOUPLES = "DIFFCOUPLES";

    /**
     * The key in the results HashMap for the averaging method int.
     */
    public static final String AVERAGING_METHOD = "AVERAGING_METHOD";


    /**
     * The database connection to read all data from.
     */
    private Connection iConn = null;

    /**
     * The calibrated standard deviation for a 1/1 ratio sample.
     */
    private double iCalibratedStDev = 0.0;

    /**
     * The type of statistics used. Can be evaluated against the X_STATISTICS constants defined on this class.
     */
    private int iStatType = -1;

    /**
     * The averaging method applied when calculating ratios for merged couples. Should be equal to one of the constants
     * defined on the DiffCouple class.
     */
    private int iAveragingMethod = 0;

    /**
     * When this Double is not 'null', the projects will all be recentered to this value.
     */
    private Double iRecenter = null;

    /**
     * The where clause addition to append to the select statement for the identifications.
     */
    private String iWhereAddition = null;

    /**
     * This constructor takes the connection to read data from and the calibrated standard deviation of the instrument
     * as arguments. The averaging method can be chosen.
     *
     * @param aConn            Connection with the DB connection to read data from.
     * @param aCalibratedStDev double with the calibrated standard deviation of the instrument for a 1/1 sample.
     * @param aStatType        int with the type of statistics that this instance will calculate. Use the constants
     *                         defined on this class (ROBUST_STATISTICS or STANDARD_STATISTICS).
     * @param aAveragingMethod int with the averaging method for merged couples. Use the constants defined on this class
     *                         (SUM_INTENSITIES or AVERAGE_RATIOS).
     * @param aRecenter        Double with the value to recenter each project to. Can be 'null' for no recentering.
     * @param aWhereAddition   String to append to the whereclause. Will be prefixed with ' AND ' before appending. Can
     *                         be 'null' for no addition to the default clause.
     */
    public DiffAnalysisCore(Connection aConn, double aCalibratedStDev, int aStatType, int aAveragingMethod, Double aRecenter, String aWhereAddition) {
        this.iConn = aConn;
        this.iCalibratedStDev = aCalibratedStDev;
        this.iStatType = aStatType;
        if (aAveragingMethod != WEIGHTED_RATIOS && aAveragingMethod != AVERAGE_RATIOS) {
            throw new IllegalArgumentException("The averaging method you specified ('" + aAveragingMethod + "') is not known to me. Please restrict yourself to the constants defined on this class!");
        }
        this.iAveragingMethod = aAveragingMethod;
        this.iRecenter = aRecenter;
        // The where clause.
        if (aWhereAddition == null) {
            iWhereAddition = "";
        } else {
            iWhereAddition = " AND " + aWhereAddition;
        }
    }

    /**
     * This constructor takes the connection to read data from and the calibrated standard deviation of the instrument
     * as arguments. The averaging method can be chosen and a recentering point can be specified.
     *
     * @param aConn            Connection with the DB connection to read data from.
     * @param aCalibratedStDev double with the calibrated standard deviation of the instrument for a 1/1 sample.
     * @param aStatType        int with the type of statistics that this instance will calculate. Use the constants
     *                         defined on this class (ROBUST_STATISTICS or STANDARD_STATISTICS).
     * @param aAveragingMethod int with the averaging method for merged couples. Use the constants defined on this class
     *                         (SUM_INTENSITIES or AVERAGE_RATIOS).
     * @param aRecenter        Double with the value to recenter each project to. Can be 'null' for no recentering.
     */
    public DiffAnalysisCore(Connection aConn, double aCalibratedStDev, int aStatType, int aAveragingMethod, Double aRecenter) {
        this(aConn, aCalibratedStDev, aStatType, aAveragingMethod, aRecenter, null);
    }

    /**
     * This constructor takes the connection to read data from and the calibrated standard deviation of the instrument
     * as arguments. The averaging method defaults to DiffCouple.SUM_INTENSITIES.
     *
     * @param aConn            Connection with the DB connection to read data from.
     * @param aCalibratedStDev double with the calibrated standard deviation of the instrument for a 1/1 sample.
     * @param aStatType        int with the type of statistics that this instance will calculate. Use the constants
     *                         defined on this class (ROBUST_STATISTICS or STANDARD_STATISTICS).
     */
    public DiffAnalysisCore(Connection aConn, double aCalibratedStDev, int aStatType) {
        this(aConn, aCalibratedStDev, aStatType, WEIGHTED_RATIOS, null);
    }

    /**
     * This constructor takes the connection to read data from and the calibrated standard deviation of the instrument
     * as arguments. The averaging method defaults to DiffCouple.SUM_INTENSITIES.
     *
     * @param aConn            Connection with the DB connection to read data from.
     * @param aCalibratedStDev double with the calibrated standard deviation of the instrument for a 1/1 sample.
     * @param aAveragingMethod int with the averaging method for merged couples. Use the constants defined on this class
     *                         (SUM_INTENSITIES or AVERAGE_RATIOS).
     * @param aStatType        int with the type of statistics that this instance will calculate. Use the constants
     *                         defined on this class (ROBUST_STATISTICS or STANDARD_STATISTICS).
     */
    public DiffAnalysisCore(Connection aConn, double aCalibratedStDev, int aStatType, int aAveragingMethod) {
        this(aConn, aCalibratedStDev, aStatType, aAveragingMethod, null);
    }


    /**
     * This method reads all the differential data from the specified instrument for the specified projects and stores
     * the results in the results HashMap, which is treated as a reference parameter. The results in the HashMap are
     * keyed by the constants defined on this class (MU_HAT, SIGMA_HAT, INSTRUMENT_STDEV, COUNT, ITERATIONS,
     * DIFFCOUPLES, AVERAGING_METHOD).
     *
     * @param aProjects     DifferentialProject[] with the projects to analyze.
     * @param aInstrumentID long with the ID for the instrument selected.
     * @param aResults      HashMap in which to store the results. <b>Note</b> that this is a reference parameter!
     * @param aProgress     DefaultProgressBar to show the progress on. Can be 'null'.
     * @throws SQLException when the retrieval of the project data failed.
     */
    public void processProjects(DifferentialProject[] aProjects, long aInstrumentID, HashMap aResults, DefaultProgressBar aProgress) throws SQLException {
        // First acquire all the necessary data for each of the projects.
        Vector allIDs = new Vector();
        HashMap recentering = combineAllIdentifcations(aProjects, aInstrumentID, allIDs, aProgress, iRecenter);
        if (allIDs.size() == 0) {
            return;
        }
        // Display progress message.
        if (aProgress != null) {
            aProgress.setMessage("Clearing sequence redundancy across all projects...");
        }
        Vector clearedSet = clearSequenceRedundancy(allIDs);
        int liSize = clearedSet.size();
        double[] log2Ratios = new double[liSize];
        for (int i = 0; i < liSize; i++) {
            DiffCouple dc = (DiffCouple) clearedSet.elementAt(i);
            switch (iAveragingMethod) {
                case WEIGHTED_RATIOS:
                    log2Ratios[i] = dc.getLog2RatioAsWeightedRatio();
                    break;
                case AVERAGE_RATIOS:
                    log2Ratios[i] = dc.getLog2RatioAsAverageRatio();
                    break;
                default:
                    log2Ratios[i] = dc.getLog2Ratio();
                    break;
            }
            // This test detects the Double.NaN return from
            // the getLog2XYZ functions, since whenever 1 or more elements in
            // a comparison are NaN, the '!=' operation returns 'true' in Java.
            if (log2Ratios[i] != log2Ratios[i]) {
                throw new NumberFormatException("Negative ratio detected for " + (dc.getCount() == 1 ? "spectrum with filename '" + dc.getFilename() + "'." : "cluster around sequence '" + dc.getSequence() + "'."));
            }
        }
        String type = null;
        if (iStatType == ROBUST_STATISTICS) {
            type = "robust";
        } else {
            type = "standard";
        }
        // Calculate statistics.
        if (aProgress != null) {
            aProgress.setValue(aProgress.getValue() + 1);
            aProgress.setMessage("Calculating " + type + " statistics...");
        }
        int n = log2Ratios.length;
        double mean = 0.0;
        double stdev = 0.0;
        int iterations = 0;
        if (iStatType == ROBUST_STATISTICS) {
            // Calculate µ[hat] and sigma[hat].
            double[] estimators = BasicStats.hubers(log2Ratios, 1e-06, false);
            mean = estimators[0];
            stdev = estimators[1];
            iterations = (int) estimators[2];
        } else {
            mean = BasicStats.mean(log2Ratios);
            stdev = BasicStats.stdev(log2Ratios, mean);
            iterations = 1;
        }
        // Now calculate a stdev for delta (which is the SQRT(stdev[median]^2 + stdev[peptide]^2))
        double stdevDelta = Math.sqrt(Math.pow(stdev, 2) + Math.pow(iCalibratedStDev, 2));
        // Apply statistical analysis to the DiffCouples.
        if (aProgress != null) {
            aProgress.setValue(aProgress.getValue() + 1);
            aProgress.setMessage("Applying statistical analysis to the couples...");
        }
        // Cycle all DiffCouples.
        for (int i = 0; i < liSize; i++) {
            DiffCouple dc = (DiffCouple) clearedSet.get(i);
            double log2Ratio = dc.getLog2Ratio();
            double delta = log2Ratio - mean;
            dc.setSignificance(delta / stdevDelta);
        }
        // Fill out result HashMap.
        if (aProgress != null) {
            aProgress.setValue(aProgress.getValue() + 1);
            aProgress.setMessage("Storing final results...");
        }
        aResults.put(MU_HAT, new Double(mean));
        aResults.put(SIGMA_HAT, new Double(stdev));
        aResults.put(INSTRUMENT_STDEV, new Double(iCalibratedStDev));
        aResults.put(COUNT, new Integer(n));
        aResults.put(ITERATIONS, new Integer(iterations));
        aResults.put(DIFFCOUPLES, clearedSet);
        aResults.put(AVERAGING_METHOD, new Integer(iAveragingMethod));
        aResults.putAll(recentering);
        // That's it!
    }

    /**
     * This method reads all identifications from the specified machine that have differential couple data for each
     * specified project from the DB. The Identification instances are transformed into DiffCouple instances and
     * subsequently combined in the aCombined Vector (which is treated as a reference variable)!
     *
     * @param aProjects     DifferentialProject[] with the projects to retrieve the identifications for
     * @param aInstrumentID long with the ID for the instrument selected.
     * @param aCombined     Vector that will contain the combined DiffCouples (reference parameter!)
     * @param aProgress     DefaultProgressBar to display progress on
     * @param aRecenter     Double with the Double to recenter to. Can be 'null' for no recentering.
     * @throws SQLException whenever the data could not be read
     */
    private HashMap combineAllIdentifcations(DifferentialProject[] aProjects, long aInstrumentID, Vector aCombined, DefaultProgressBar aProgress, Double aRecenter) throws SQLException {
        HashMap result = new HashMap(aProjects.length);
        // Cycle all projects.
        for (int i = 0; i < aProjects.length; i++) {
            DifferentialProject lProject = aProjects[i];
            // Adapt progress bar if there is any.
            if (aProgress != null) {
                aProgress.setValue(aProgress.getValue() + 1);
                aProgress.setMessage("Retrieving all differential identifications for project " + lProject.getProjectID() + "...");
            }
            // Read all differential IDs for the current project and the current machine.
            Identification[] ids = Identification.getAllIdentificationsforProjectAndInstrument(iConn, lProject.getProjectID(), aInstrumentID, "i.light_isotope>0 AND i.heavy_isotope>0 AND i.valid >= 0" + iWhereAddition);
            if (ids != null && ids.length > 0) {
                // Report on number of differential IDs read.
                if (aProgress != null) {
                    aProgress.setValue(aProgress.getValue() + 1);
                    aProgress.setMessage("Processing " + ids.length + " ID's from project " + lProject.getProjectID() + "...");
                }
                // Read identifications into temp List.
                ArrayList temp = new ArrayList(ids.length);
                double[] ratios = new double[ids.length];
                for (int j = 0; j < ids.length; j++) {
                    Identification lID = ids[j];
                    DiffCouple dc = null;
                    // See if we need to inverse light and heavy intensities.
                    if (lProject.isInverse()) {
                        // Inverse
                        dc = new DiffCouple(lProject.getProjectID(), 1, lID.getHeavy_isotope().doubleValue(), lID.getLight_isotope().doubleValue(), "" + lID.getL_spectrumid(), lID.getAccession(), (int) lID.getStart(), (int) lID.getEnd(), lID.getEnzymatic(), lID.getSequence(), lID.getModified_sequence(), lID.getDescription());
                    } else {
                        // Normal.
                        dc = new DiffCouple(lProject.getProjectID(), 1, lID.getLight_isotope().doubleValue(), lID.getHeavy_isotope().doubleValue(), "" + lID.getL_spectrumid(), lID.getAccession(), (int) lID.getStart(), (int) lID.getEnd(), lID.getEnzymatic(), lID.getSequence(), lID.getModified_sequence(), lID.getDescription());
                    }
                    ratios[j] = dc.getRatio();
                    temp.add(dc);
                }
                if (iRecenter != null) {
                    // Now calculate the mean/median (depending on the statistics chosen) of the collection of identifications.
                    double center = 0.0;
                    if (iStatType == ROBUST_STATISTICS) {
                        center = BasicStats.median(ratios, false);
                    } else if (iStatType == STANDARD_STATISTICS) {
                        center = BasicStats.mean(ratios);
                    }
                    // Calculate the deviation from the suggested center.
                    double delta = aRecenter.doubleValue() - center;
                    // Apply this correction to all couples.
                    for (Iterator lIterator = temp.iterator(); lIterator.hasNext();) {
                        DiffCouple lDiffCouple = (DiffCouple) lIterator.next();
                        lDiffCouple.setCorrection(delta);
                    }
                    result.put(new Long(lProject.getProjectID()), new Double(delta));
                }
                // Add everything in the combined vector.
                aCombined.addAll(temp);
            }
        }

        return result;
    }

    /**
     * This method cleares the sequence-based redundancy of the DiffCouples.
     *
     * @param aIDs Vector with the DiffCouples to clear sequence redundancy from.
     * @return Vector with non-redundant DiffCouples. All entries that have been merged, retain references to each
     *         merged entry.
     */
    private Vector clearSequenceRedundancy(Vector aIDs) {
        int liSize = aIDs.size();
        HashMap intermed = new HashMap(liSize);
        for (int i = 0; i < liSize; i++) {
            DiffCouple dc = (DiffCouple) aIDs.get(i);
            String key = dc.getSequence();
            // Check for sequence redundancy.
            if (intermed.containsKey(key)) {
                // Redundant entry, add them.
                DiffCouple old = (DiffCouple) intermed.get(key);
                old.addCouple(dc);
            } else {
                // New entry, put it in the Map.
                intermed.put(key, dc);
            }
        }
        // Extract all entries.
        return new Vector(intermed.values());
    }
}
