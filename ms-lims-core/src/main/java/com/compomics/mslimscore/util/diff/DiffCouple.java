/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 14-jun-2004
 * Time: 11:31:57
 */
package com.compomics.mslimscore.util.diff;

import org.apache.log4j.Logger;

import com.compomics.statlib.descriptive.BasicStats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.7 $
 * $Date: 2005/05/18 14:30:35 $
 */

/**
 * This class wraps the information about a heavy/light couple.
 *
 * @author Lennart Martens
 * @version $Id: DiffCouple.java,v 1.7 2005/05/18 14:30:35 lennart Exp $
 */
public class DiffCouple {
    // Class specific log4j logger for DiffCouple instances.
    private static Logger logger = Logger.getLogger(DiffCouple.class);
    /**
     * Project reference.
     */
    private long iProjectID = -1;

    /**
     * Intensity for the light ion.
     */
    private double iLightIntensity = 0.0;

    /**
     * Any additional light intensities are collected here.
     */
    private ArrayList iAddedLightIntensities = null;

    /**
     * Any additional heavy intensities are collected here.
     */
    private ArrayList iAddedHeavyIntensities = null;

    /**
     * Intensity for the heavy ion.
     */
    private double iHeavyIntensity = 0.0;

    /**
     * After statistical analysis, this field will hold the result of '(Mu-mean)/stdev'. It can therefore be readily
     * compared to a normal significance interval, since the statistical analysis should yield a Mu and stdev such that
     * N~(Mu, stdev).
     */
    private double iSignificance = 0.0;

    /**
     * Number of spectra that were merged to obtain this result.
     */
    private int iCount = 1;

    /**
     * Original spectrum filename.
     */
    private String iFilename = null;

    /**
     * Accession number of the associated protein.
     */
    private String iAccession = null;

    /**
     * Start location within the parent protein of the peptide.
     */
    private int iStart = 0;

    /**
     * End location within the parent protein of the peptide.
     */
    private int iEnd = 0;

    /**
     * Description of the enzymatic character of the peptide.
     */
    private String iEnzymatic = null;

    /**
     * Sequence of the identified peptide.
     */
    private String iSequence = null;

    /**
     * Modified sequence of the peptide (comma-separated list of mod seqs if more than one spectrum contributed.
     */
    private String iModifiedSequence = null;

    /**
     * Description line associated with the accession number.
     */
    private String iDescription = null;

    /**
     * This Vector will contain all original DiffCouples that were added to this DiffCouple.
     */
    private Vector iAddedCouples = new Vector();

    /**
     * This double will be added to the ratio when calculated.
     */
    private double iCorrection = 0.0;

    /**
     * This constructor takes all necessary arguments to build a DiffCouple.
     *
     * @param aProjectID   long with the link to the original project.
     * @param aCount       int with the number of spectra contributing to this ratio.
     * @param aLight       double with the intensity of the light ion.
     * @param aHeavy       double with the intensity of the heavy ion.
     * @param aFilename    String with the filename of the original spectrum.
     * @param aAccession   String with the accession number.
     * @param aStart       int with the start location.
     * @param aEnd         int with the end location.
     * @param aEnzymatic   String with the enzymatic character of the peptide.
     * @param aSequence    String with the sequence
     * @param aModSeq      String with the modified sequence
     * @param aDescription String with the description
     */
    public DiffCouple(long aProjectID, int aCount, double aLight, double aHeavy, String aFilename, String aAccession, int aStart, int aEnd, String aEnzymatic, String aSequence, String aModSeq, String aDescription) {
        this.iProjectID = aProjectID;
        this.iCount = aCount;
        this.iLightIntensity = aLight;
        this.iHeavyIntensity = aHeavy;
        this.iFilename = aFilename;
        this.iAccession = aAccession;
        this.iStart = aStart;
        this.iEnd = aEnd;
        this.iEnzymatic = aEnzymatic;
        this.iSequence = aSequence;
        this.iModifiedSequence = aModSeq;
        this.iDescription = aDescription;
    }

    /**
     * This constructor takes all necessary arguments to build a DiffCouple.
     *
     * @param aProjectID   long with the link to the original project.
     * @param aLight       double with the intensity of the light ion.
     * @param aHeavy       double with the intensity of the heavy ion.
     * @param aFilename    String with the filename of the original spectrum.
     * @param aAccession   String with the accession number.
     * @param aStart       int with the start location.
     * @param aEnd         int with the end location.
     * @param aEnzymatic   String with the enzymatic character of the peptide.
     * @param aSequence    String with the sequence
     * @param aModSeq      String with the modified sequence
     * @param aDescription String with the description
     */
    public DiffCouple(long aProjectID, double aLight, double aHeavy, String aFilename, String aAccession, int aStart, int aEnd, String aEnzymatic, String aSequence, String aModSeq, String aDescription) {
        this.iProjectID = aProjectID;
        this.iCount = 1;
        this.iLightIntensity = aLight;
        this.iHeavyIntensity = aHeavy;
        this.iFilename = aFilename;
        this.iAccession = aAccession;
        this.iStart = aStart;
        this.iEnd = aEnd;
        this.iEnzymatic = aEnzymatic;
        this.iSequence = aSequence;
        this.iModifiedSequence = aModSeq;
        this.iDescription = aDescription;
    }

    /**
     * This constructor builds a minimalist DiffCouple that can be used for data storage. In this case, we only need the
     * filename (primary key) and light and heavy intensities.
     *
     * @param aFilename String with the filename for the spectrum.
     * @param aLight    double with the intensity of the light ion.
     * @param aHeavy    double with the intensity of the heavy ion.
     */
    public DiffCouple(String aFilename, double aLight, double aHeavy) {
        this.iFilename = aFilename;
        this.iLightIntensity = aLight;
        this.iHeavyIntensity = aHeavy;
    }

    /**
     * Use this method to add the specified inner couple to this one, retaining the associated information.
     *
     * @param aCouple DiffCouple to add.
     */
    public void addCouple(DiffCouple aCouple) {
        this.iAddedCouples.add(aCouple);
        this.addCouple(aCouple.getLightIntensity(), aCouple.getHeavyIntensity());
    }

    /**
     * This method adds a couple to this couple, based on the minimum information necessary as well as the modified
     * sequence of the added couple.
     *
     * @param aLight  double with the intensity for the light ion.
     * @param aHeavy  double with the intensity for the heavy ion.
     * @param aModSeq String with teh modified sequence (only added if not present yet).
     */
    private void addCouple(double aLight, double aHeavy, String aModSeq) {
        // See if we already have the modified sequence.
        if (this.iModifiedSequence.indexOf(aModSeq) < 0) {
            this.iModifiedSequence += ", " + aModSeq;
        }
        this.addCouple(aLight, aHeavy);
    }

    /**
     * This method adds a couple to this couple, based on the minimum information necessary.
     *
     * @param aLight double with the intensity for the light ion.
     * @param aHeavy double with the intensity for the heavy ion.
     */
    private void addCouple(double aLight, double aHeavy) {
        if (iAddedLightIntensities == null) {
            iAddedLightIntensities = new ArrayList();
        }
        if (iAddedHeavyIntensities == null) {
            iAddedHeavyIntensities = new ArrayList();
        }
        iAddedLightIntensities.add(new Double(aLight));
        iAddedHeavyIntensities.add(new Double(aHeavy));
        this.iCount++;
    }

    /**
     * This method returns a Vector with all the merged entries that constitute this DiffCouple. The Vector is empty
     * when the count for this DiffCouple is '1'.
     *
     * @return Vector with the DiffCouples that were merged to yield this couple.
     */
    public Vector getMergedEntries() {
        return this.iAddedCouples;
    }

    /**
     * This method reports on the light/heavy ratio for the peptide. It uses the default method of weighted ratios for
     * calculating the ratio of a clustered DiffCouple.
     *
     * @return double with the light/heavy ratio.
     */
    public double getRatio() {
        return this.getRatioAsWeightedRatio();
    }

    /**
     * This method reports on the light/heavy ratio for the peptide. When this DiffCouple represents a cluster, the
     * ratio is calculated as the ratio of the summed intensities of the cluster elements.
     *
     * @return double with the light/heavy ratio.
     */
    public double getRatioAsWeightedRatio() {
        double result = 0.0;

        // First calculate all the constituent ratios as weighted ratios.
        int replicates = this.getCount();
        double[] ratios = new double[replicates];

        ratios[0] = (this.iLightIntensity / this.iHeavyIntensity) + iCorrection;
        Collection children = this.getMergedEntries();
        if (children != null && children.size() > 0) {
            int index = 1;
            for (Iterator lIterator = children.iterator(); lIterator.hasNext();) {
                DiffCouple lDiffCouple = (DiffCouple) lIterator.next();
                ratios[index] = lDiffCouple.getRatioAsWeightedRatio();
                index++;
            }
        }

        // Now get the total light and heavy intensity.
        double lightTotal = this.getSummedLightIntensity();
        double heavyTotal = this.getSummedHeavyIntensity();

        // Determine the fraction that each couple contributes to light,
        // resp. heavy total intensity.
        double[] lightPercentage = new double[replicates];
        lightPercentage[0] = this.getLightIntensity() / lightTotal;
        children = this.getMergedEntries();
        if (children != null && children.size() > 0) {
            int index = 1;
            for (Iterator lIterator = children.iterator(); lIterator.hasNext();) {
                DiffCouple lDiffCouple = (DiffCouple) lIterator.next();
                lightPercentage[index] = lDiffCouple.getLightIntensity() / lightTotal;
                index++;
            }
        }
        double[] heavyPercentage = new double[replicates];
        heavyPercentage[0] = this.getHeavyIntensity() / heavyTotal;
        children = this.getMergedEntries();
        if (children != null && children.size() > 0) {
            int index = 1;
            for (Iterator lIterator = children.iterator(); lIterator.hasNext();) {
                DiffCouple lDiffCouple = (DiffCouple) lIterator.next();
                heavyPercentage[index] = lDiffCouple.getHeavyIntensity() / heavyTotal;
                index++;
            }
        }

        // Add the product of the individual ratios and their weight factor for light.
        double lightWeightedRatio = 0.0;
        for (int i = 0; i < ratios.length; i++) {
            lightWeightedRatio += (ratios[i] * lightPercentage[i]);
        }
        // Add the product of the individual ratios and their weight factor for heavy.
        double heavyWeightedRatio = 0.0;
        for (int i = 0; i < ratios.length; i++) {
            heavyWeightedRatio += (ratios[i] * heavyPercentage[i]);
        }

        // Average weighted average for light and weighted average for heavy.
        result = (lightWeightedRatio + heavyWeightedRatio) / 2;

        // Finished.
        return result;
    }

    /**
     * This method reports on the light/heavy ratio for the peptide. When this DiffCouple represents a cluster, the
     * ratio is calculated as the average of the individual ratios.
     *
     * @return double with the light/heavy ratio.
     */
    public double getRatioAsAverageRatio() {
        double result = 0.0;

        int count = 1;
        double sumOfRatios = 0.0;
        // First the ratio for this DiffCouple.
        sumOfRatios += (this.iLightIntensity / this.iHeavyIntensity) + iCorrection;
        // See if there are clustered data as well.
        if (iAddedCouples != null && iAddedCouples.size() > 0) {
            for (int i = 0; i < iAddedCouples.size(); i++) {
                DiffCouple lDiffCouple = (DiffCouple) iAddedCouples.elementAt(i);
                sumOfRatios += lDiffCouple.getRatioAsAverageRatio();
                count++;
            }
        }
        result = sumOfRatios / count;

        return result;
    }

    /**
     * This method reports on the light/heavy ratio for the peptide, transformed in log2 scale. When this DiffCouple
     * represents a cluster, the ratio is calculated as the ratio of the weighted ratios of the cluster elements.
     *
     * @return double with the log(base 2) light/heavy ratio.
     */
    public double getLog2RatioAsWeightedRatio() {
        return Math.log(this.getRatioAsWeightedRatio()) / Math.log(2);
    }

    /**
     * This method reports on the light/heavy ratio for the peptide, transformed in log2 scale.
     * <p/>
     * When this DiffCouple represents a cluster, the ratio is calculated as the average of the individual ratios.
     *
     * @return double with the log(base 2) light/heavy ratio.
     */
    public double getLog2RatioAsAverageRatio() {
        return Math.log(this.getRatioAsAverageRatio()) / Math.log(2);
    }

    /**
     * This method reports on the light/heavy ratio for the peptide, transformed in log2 scale. It uses the default
     * method of weighted ratios for calculating the ratio of a clustered DiffCouple.
     *
     * @return double with the log(base 2) light/heavy ratio.
     */
    public double getLog2Ratio() {
        return Math.log(this.getRatio()) / Math.log(2);
    }

    /**
     * This method checks whether this DiffCouple has a ratio that is significantly different from the normal
     * distribution detailed by the given location and scale.
     *
     * @return int that is '98' for a ratio that has a different ratio at the 98% confidence interval, '95' for the 95%
     *         confidence interval and '0' when the ratio is not significantly different.
     */
    public int isOutlier(double aLocation, double aScale) {
        int result = 0;
        double significance = Math.abs(((this.getLightIntensity() / getHeavyIntensity()) - aLocation)) / aScale;
        if (significance > 2.33) {
            result = 98;
        } else if (significance > 1.96) {
            result = 95;
        }
        return result;
    }

    /**
     * This method checks if the specified DiffCouple (which should be representing a cluster!) has a set of ratios that
     * contains an outlier on the 95% confidence interval. Location is the median, scale the Huber scale estimator. The
     * 95% confidence interval results in a test Z-value of 1.96.
     *
     * @return int that is '98' for a clustered diffcouple that has outlying ratios in its composition at the 98%
     *         confidence interval, '95' for the 95% confidence interval and '0' when no outliers could be detected.
     */
    public int checkOutliers() {
        int result = 0;
        int count = this.getCount();
        if (count > 1) {
            // Calculate the median (for location) and Huber sclae estimate (for scale)
            // for this cluster.
            double[] stats = this.getLocationAndScale();
            // Now see if any ratios lie outside of a 95% confidence interval
            // based on these two values.
            int parentScore = this.isOutlier(stats[0], stats[1]);
            if (parentScore > 0) {
                result = parentScore;
            } else {
                Collection children = this.getMergedEntries();
                for (Iterator lIterator = children.iterator(); lIterator.hasNext();) {
                    DiffCouple dc = (DiffCouple) lIterator.next();
                    int temp = dc.isOutlier(stats[0], stats[1]);
                    if (temp > 0) {
                        result = temp;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * This method calculates a robust location (median) and scale (Huber scale) estimate for this clustered couple.
     *
     * @return double[] with the location at index '0' and the scale at index '1'.
     */
    public double[] getLocationAndScale() {
        double[] ratios = new double[this.getCount()];
        ratios[0] = (this.getLightIntensity() / this.getHeavyIntensity()) + this.getCorrection();
        Vector couples = this.getMergedEntries();
        for (int i = 0; i < couples.size(); i++) {
            DiffCouple lCouple = (DiffCouple) couples.elementAt(i);
            ratios[i + 1] = lCouple.getRatio();
        }
        // Calculate the median and the Huber scale estimate.
        double[] huber = BasicStats.hubers(ratios, 1e-6, false);

        return huber;
    }

    /**
     * When a DiffCouple represents a cluster, this method will return the sum of all heavy intensities. If it is not a
     * cluster, the result will be identical to the result of getHeavyIntensity().
     *
     * @return double with the summed heavy intensity.
     */
    public double getSummedHeavyIntensity() {
        double result = iHeavyIntensity;
        if (iAddedCouples != null && iAddedCouples.size() > 0) {
            for (Iterator lIterator = iAddedCouples.iterator(); lIterator.hasNext();) {
                double d = ((DiffCouple) lIterator.next()).getSummedHeavyIntensity();
                result += d;
            }
        }
        return result;
    }

    /**
     * When a DiffCouple represents a cluster, this method will return the sum of all light intensities. If it is not a
     * cluster, the result will be identical to the result of getLightIntensity().
     *
     * @return double with the summed light intensity.
     */
    public double getSummedLightIntensity() {
        double result = iLightIntensity;
        if (iAddedCouples != null && iAddedCouples.size() > 0) {
            for (Iterator lIterator = iAddedCouples.iterator(); lIterator.hasNext();) {
                double d = ((DiffCouple) lIterator.next()).getSummedLightIntensity();
                result += d;
            }
        }
        return result;
    }

    /**
     * This method returns 'true' only if the ratio can not be calculated (one of the intensities is zero, the other is
     * non-zero). It uses the default method of weighted ratios for calculating the ratio of a clustered DiffCouple.
     *
     * @return boolean that indicates whether this is a single ('true') peak, or a couple ('false').
     */
    public boolean isSingle() {
        boolean result = false;
        // Only return 'true' (stating that it is a single) if ONLY one intensity is zero, and the OTHER
        // intensity is non-zero.
        if ((iLightIntensity <= 0.0 && iHeavyIntensity != 0.0) || (iLightIntensity != 0.0 && iHeavyIntensity <= 0.0)) {
            result = true;
        }
        return result;
    }

    public String getAccession() {
        return iAccession;
    }

    public void setAccession(String aAccession) {
        iAccession = aAccession;
    }

    public int getCount() {
        return iCount;
    }

    public void setCount(int aCount) {
        iCount = aCount;
    }

    public String getDescription() {
        return iDescription;
    }

    public void setDescription(String aDescription) {
        iDescription = aDescription;
    }

    public int getEnd() {
        return iEnd;
    }

    public void setEnd(int aEnd) {
        iEnd = aEnd;
    }

    public String getEnzymatic() {
        return iEnzymatic;
    }

    public void setEnzymatic(String aEnzymatic) {
        iEnzymatic = aEnzymatic;
    }

    public String getFilename() {
        return iFilename;
    }

    public void setFilename(String aFilename) {
        iFilename = aFilename;
    }

    public double getHeavyIntensity() {
        return iHeavyIntensity;
    }

    public void setHeavyIntensity(double aHeavyIntensity) {
        iHeavyIntensity = aHeavyIntensity;
    }

    public double getLightIntensity() {
        return iLightIntensity;
    }

    public void setLightIntensity(double aLightIntensity) {
        iLightIntensity = aLightIntensity;
    }

    public String getModifiedSequence() {
        return iModifiedSequence;
    }

    public void setModifiedSequence(String aModifiedSequence) {
        iModifiedSequence = aModifiedSequence;
    }

    public String getSequence() {
        return iSequence;
    }

    public void setSequence(String aSequence) {
        iSequence = aSequence;
    }

    public int getStart() {
        return iStart;
    }

    public void setStart(int aStart) {
        iStart = aStart;
    }

    public void setSignificance(double aSignificance) {
        iSignificance = aSignificance;
    }

    public double getSignificance() {
        return this.iSignificance;
    }

    public long getProjectID() {
        return iProjectID;
    }

    public void setProjectID(long aProjectID) {
        iProjectID = aProjectID;
    }

    public double getCorrection() {
        return iCorrection;
    }

    public void setCorrection(double aCorrection) {
        iCorrection = aCorrection;
    }
}

