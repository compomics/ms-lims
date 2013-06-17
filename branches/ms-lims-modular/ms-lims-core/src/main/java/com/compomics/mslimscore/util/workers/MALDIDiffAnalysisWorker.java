/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 4-okt-2005
 * Time: 13:55:13
 */
package com.compomics.mslimscore.util.workers;

import org.apache.log4j.Logger;

import com.compomics.util.interfaces.Flamable;
import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mslimscore.util.interfaces.BrukerCompound;
import com.compomics.mslimscore.util.fileio.BrukerCompoundListReader;
import com.compomics.statlib.descriptive.BasicStats;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
/*
 * CVS information:
 *
 * $Revision: 1.7 $
 * $Date: 2006/03/08 13:09:44 $
 */

/**
 * This class implements a Runnable that actually does the differential analysis (and optional inclusion list writing)
 * for the MALDI LC-MS approach.
 *
 * @author Lennart Martens
 * @version $Id: MALDIDiffAnalysisWorker.java,v 1.7 2006/03/08 13:09:44 lennart Exp $
 */
public class MALDIDiffAnalysisWorker implements Runnable {
    // Class specific log4j logger for MALDIDiffAnalysisWorker instances.
    private static Logger logger = Logger.getLogger(MALDIDiffAnalysisWorker.class);

    /**
     * The parent for this runnable.
     */
    private Flamable iParent = null;

    /**
     * The name of the analysis. Will be prefixed to the individual inclusion list names.
     */
    private String iName = null;

    /**
     * The progress bar.
     */
    private DefaultProgressBar iProgress = null;

    /**
     * Input file.
     */
    private File iInput = null;

    /**
     * Boolean that indicates whether to use peak intensity ('false'), or peak area ('true') for the ratio calculation.
     */
    private boolean iUseArea = true;

    /**
     * Double with the desired calibration.
     */
    private double iCalibration = 0.0;

    /**
     * Output folder.
     */
    private File iOutput = null;

    /**
     * Confidence interval (use constants).
     */
    private int iConfidence = -1;

    /**
     * Signal-to-noise threshold.
     */
    private double iS2nThreshold = -1;

    /**
     * Instrument selected (use constants).
     */
    private int iInstrument = -1;

    /**
     * Cone voltage (only used for Q-TOF).
     */
    private int iConeVoltage = 0;

    /**
     * Collision energy (only used for Q-TOF).
     */
    private int iCollisionEnergy = 0;

    /**
     * The results of the statistical analysis. <br /> Indexes by the constants defined below.
     */
    private double[] iStatisticalResults = new double[12];

    /**
     * The total number of compounds read, number of singles, number of couples and number of skipped compounds. <br />
     * Indexes by the constants defined below.
     */
    private int[] iCompoundCounts = null;

    /**
     * File counter.
     */
    private int iFileCount = 0;

    /**
     * This hashmap will hold the couples after analysis.
     */
    private HashMap iCouples = null;

    /**
     * The recentering value, if defined.
     */
    private Double iRecenteringValue = null;

    /**
     * Compound counter for those that pass the differential requirements.
     */
    private int iDifferentialCompoundCount = 0;

    public static final int CONFIDENCE_95 = 0;
    public static final int CONFIDENCE_98 = 1;
    public static final int QTOF = 0;
    public static final int ESQUIRE = 1;

    public static final int MEDIAN = 0;
    public static final int SCALE = 1;
    public static final int CONFIDENCE_95_LOWER_LOG2 = 2;
    public static final int CONFIDENCE_95_UPPER_LOG2 = 3;
    public static final int CONFIDENCE_98_LOWER_LOG2 = 4;
    public static final int CONFIDENCE_98_UPPER_LOG2 = 5;
    public static final int CONFIDENCE_95_LOWER_RATIO = 6;
    public static final int CONFIDENCE_95_UPPER_RATIO = 7;
    public static final int CONFIDENCE_98_LOWER_RATIO = 8;
    public static final int CONFIDENCE_98_UPPER_RATIO = 9;
    public static final int OUTLIER_COUNT_95 = 10;
    public static final int OUTLIER_COUNT_98 = 11;

    public static final int TOTAL_COUNT = 0;
    public static final int SINGLE_COUNT = 1;
    public static final int COUPLE_COUNT = 2;
    public static final int SKIPPED_COUNT = 3;

    public MALDIDiffAnalysisWorker(Flamable aParent, String aName, DefaultProgressBar aProgress, File aInput, boolean aUseArea, double aCalibration, File aOutput, int aConfidence, double aS2nThreshold, int aInstrument, int aConeVoltage, int aCollisionEnergy, Double aRecenteringValue) {
        this.iParent = aParent;
        this.iName = aName;
        this.iProgress = aProgress;
        this.iInput = aInput;
        this.iUseArea = aUseArea;
        this.iCalibration = aCalibration;
        this.iOutput = aOutput;
        this.iConfidence = aConfidence;
        this.iS2nThreshold = aS2nThreshold;
        this.iInstrument = aInstrument;
        this.iConeVoltage = aConeVoltage;
        this.iCollisionEnergy = aCollisionEnergy;
        this.iRecenteringValue = aRecenteringValue;
    }

    public void run() {
        try {
            // 0. See if we have an input file, or an input folder.
            BrukerCompoundListReader bclr = new BrukerCompoundListReader();
            if (!iInput.isDirectory()) {
                // File; process accordingly.
                // 1.a. Read input file.
                if (iProgress != null) {
                    iProgress.setMessage("Reading components...");
                }
                bclr.readList(iInput);
                if (iProgress != null) {
                    iProgress.setValue(iProgress.getValue() + 1);
                }
            } else {
                // Folder; process accordingly.
                // 1.b. Find all files, adapt progress bar and read all of them.
                if (iProgress != null) {
                    iProgress.setMessage("Finding all compoundlists in '" + iInput.getName() + "'");
                    iProgress.pack();
                    iProgress.setIndeterminate(true);
                }
                File[] compoundListFiles = this.getAllCompoundListsForFolder(iInput, iProgress);
                if (iProgress != null) {
                    iProgress.setMessage("Found " + compoundListFiles.length + " compoundlists to parse.");
                    iProgress.setIndeterminate(false);
                }
                for (int i = 0; i < compoundListFiles.length; i++) {
                    File lCompoundListFile = compoundListFiles[i];
                    if (iProgress != null) {
                        iProgress.setMessage("Reading compoundlist for '" + lCompoundListFile.getParentFile().getName() + "'...");
                    }
                    bclr.readList(lCompoundListFile);
                }
                if (iProgress != null) {
                    iProgress.setValue(iProgress.getValue() + 1);
                }
            }

            // 2. Perform statistics.
            if (iProgress != null) {
                iProgress.setMessage("Performing statistical analysis...");
            }
            // First init the compound counts.
            iCompoundCounts = new int[4];
            iCompoundCounts[TOTAL_COUNT] = bclr.getTotalCompoundsRead();
            iCompoundCounts[SINGLE_COUNT] = bclr.getTotalSingles();
            iCompoundCounts[COUPLE_COUNT] = bclr.getTotalPairs();
            iCompoundCounts[SKIPPED_COUNT] = bclr.getSkippedCompounds();

            // Get all the ratios for the couples in log2 scale.
            iCouples = bclr.getCouples();
            double[] log2Ratios = new double[iCouples.size()];
            Iterator iter = iCouples.keySet().iterator();
            int count = 0;
            while (iter.hasNext()) {
                Object key = iter.next();
                BrukerCompound lCompound = (BrukerCompound) iCouples.get(key);
                log2Ratios[count] = Math.log(lCompound.getRegulation(iUseArea)) / Math.log(2);
                count++;
            }
            // See if we need to recenter the couples.
            if (iRecenteringValue != null) {
                double center = iRecenteringValue.doubleValue();
                // Find the median of the log2 ratios.
                double median = BasicStats.median(log2Ratios, false);
                double delta = center - median;
                // Correct all log2 ratios.
                for (int i = 0; i < log2Ratios.length; i++) {
                    log2Ratios[i] = log2Ratios[i] + delta;
                }
            }

            // All right, do a robust statistical analysis.
            double[] huber = BasicStats.hubers(log2Ratios, 1e-06, false);
            // Add the median to the final stat results.
            iStatisticalResults[MEDIAN] = huber[MEDIAN];
            // Add the corrected scale (corrected with MALDI machine-specific standard deviation).
            iStatisticalResults[SCALE] = Math.sqrt(Math.pow(huber[SCALE], 2) + Math.pow(iCalibration, 2));
            // Calculate the lower and upper thresholds for the 95% and 98% confidence intervals
            // as well as the number of couples that are significantly regulated for each.
            calculateAdditionalStats(log2Ratios);
            if (iProgress != null) {
                iProgress.setValue(iProgress.getValue() + 1);
            }
            // 3. If output of inclusion lists is required, generate these.
            if (iOutput != null) {
                if (iProgress != null) {
                    iProgress.setMessage("Writing inclusion lists...");
                }
                writeInclusionLists(bclr);
                if (iProgress != null) {
                    iProgress.setValue(iProgress.getValue() + 1);
                    iProgress.setMessage("Writing lookup lists...");
                    iProgress.setValue(iProgress.getValue() + 1);
                }
            }
            if (iProgress != null) {
                iProgress.setValue(iProgress.getMaximum());
            }
        } catch (Exception e) {
            iParent.passHotPotato(e, "Unable to complete processing");
        }
    }

    /**
     * This method reports on the results of the statistical analysis. These can be accessed by using the constants
     * defined on this class (MEDIAN, HUBER_SCALE).
     *
     * @return double[] with the results of the statistical analysis.
     */
    public double[] getStatisticsResults() {
        return this.iStatisticalResults;
    }

    /**
     * This method returns an array with the total number of compounds read from file, the number of singles amongst
     * those, the number of couples and the number of skipped compounds. These counts can be accessed by using the
     * constants defined on this class (TOTAL_COUNT, SINGLE_COUNT, COUPLE_COUNT, SKIPPED_COUNT).
     *
     * @return int[] with the compound counts.
     */
    public int[] getCompoundCounts() {
        return iCompoundCounts;
    }

    /**
     * This method returns the number of inclusion lists written.
     *
     * @return int with the number of inclusion lists written.
     */
    public int getFileCount() {
        return iFileCount;
    }

    /**
     * This method returns the number of non-single, significantly up- or downrregulated couples that were written to
     * the inclusion lists.
     *
     * @return int with the count of differentially regulated couples.
     */
    public int getDifferentialCompoundCount() {
        return iDifferentialCompoundCount;
    }

    /**
     * This method reports on the name of the analysis.
     *
     * @return String  with the name of the analysis.
     */
    public String getName() {
        return iName;
    }

    /**
     * This method reports on the couples picked up during the data parsing.
     *
     * @return HashMap with the couples.
     */
    public HashMap getCouples() {
        return this.iCouples;
    }

    /**
     * This method calculates the thresholds for the 95% and 98% confidence interval (both in log2 and ratio scale) and
     * also calculates how many compound pairs are significantly up- or downregulated for each.
     *
     * @param aLog2Ratios with the log2 of the ratios of the compound pairs.
     */
    private void calculateAdditionalStats(double[] aLog2Ratios) {
        // 95% confidence interval.
        iStatisticalResults[CONFIDENCE_95_LOWER_LOG2] = iStatisticalResults[MEDIAN] - (1.96 * iStatisticalResults[SCALE]);
        iStatisticalResults[CONFIDENCE_95_UPPER_LOG2] = iStatisticalResults[MEDIAN] + (1.96 * iStatisticalResults[SCALE]);
        iStatisticalResults[CONFIDENCE_95_LOWER_RATIO] = Math.pow(2, iStatisticalResults[CONFIDENCE_95_LOWER_LOG2]);
        iStatisticalResults[CONFIDENCE_95_UPPER_RATIO] = Math.pow(2, iStatisticalResults[CONFIDENCE_95_UPPER_LOG2]);

        // 98% confidence interval.
        iStatisticalResults[CONFIDENCE_98_LOWER_LOG2] = iStatisticalResults[MEDIAN] - (2.33 * iStatisticalResults[SCALE]);
        iStatisticalResults[CONFIDENCE_98_UPPER_LOG2] = iStatisticalResults[MEDIAN] + (2.33 * iStatisticalResults[SCALE]);
        iStatisticalResults[CONFIDENCE_98_LOWER_RATIO] = Math.pow(2, iStatisticalResults[CONFIDENCE_98_LOWER_LOG2]);
        iStatisticalResults[CONFIDENCE_98_UPPER_RATIO] = Math.pow(2, iStatisticalResults[CONFIDENCE_98_UPPER_LOG2]);

        // Count the outliers.
        int count95 = 0;
        int count98 = 0;
        for (int i = 0; i < aLog2Ratios.length; i++) {
            double log2Ratio = aLog2Ratios[i];
            if (log2Ratio < iStatisticalResults[CONFIDENCE_98_LOWER_LOG2] || log2Ratio > iStatisticalResults[CONFIDENCE_98_UPPER_LOG2]) {
                count95++;
                count98++;
            } else if (log2Ratio < iStatisticalResults[CONFIDENCE_95_LOWER_LOG2] || log2Ratio > iStatisticalResults[CONFIDENCE_95_UPPER_LOG2]) {
                count95++;
            }
        }
        iStatisticalResults[OUTLIER_COUNT_95] = count95;
        iStatisticalResults[OUTLIER_COUNT_98] = count98;
    }

    /**
     * This method actually writes the inclusion lists.
     *
     * @param aReader BrukerCompoundListReader to read the data from.
     */
    private void writeInclusionLists(BrukerCompoundListReader aReader) throws IOException {
        // Calculate the thresholds.
        double lowerThresh = 0.0;
        double upperThresh = 0.0;
        double factor = 0.0;
        if (iConfidence == CONFIDENCE_95) {
            lowerThresh = iStatisticalResults[CONFIDENCE_95_LOWER_RATIO];
            upperThresh = iStatisticalResults[CONFIDENCE_95_UPPER_RATIO];
        } else if (iConfidence == CONFIDENCE_98) {
            lowerThresh = iStatisticalResults[CONFIDENCE_98_LOWER_RATIO];
            upperThresh = iStatisticalResults[CONFIDENCE_98_UPPER_RATIO];
        } else {
            throw new IllegalArgumentException("Incorrect confidence interval specified!");
        }
        // Rearrange the data so that it is keyed by the fraction letter in the position.
        HashMap rekeyed = new HashMap();
        // First the singles.
        Iterator iter = aReader.getSingles().iterator();
        while (iter.hasNext()) {
            BrukerCompound compound = (BrukerCompound) iter.next();
            String key = compound.getPosition().substring(0, 1);
            checkHashForKey(rekeyed, key, compound);
        }
        // Next the couples.
        iter = aReader.getCouples().values().iterator();
        while (iter.hasNext()) {
            BrukerCompound compound = (BrukerCompound) iter.next();
            String key = compound.getPosition().substring(0, 1);
            checkHashForKey(rekeyed, key, compound);
        }
        // Done.
        // Now cycle each key and write a file for each.
        iter = rekeyed.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            Collection value = (Collection) rekeyed.get(key);
            writeInclusionList(key, value, lowerThresh, upperThresh);
        }
    }

    /**
     * Method that either creates a new ArrayList with this value and adds it under the specified key (if the key is new
     * to the hash) or that adds the specified value to a pre-existing ArrayList (if the key already existed in the
     * hash).
     *
     * @param aHash  HashMap to perform the operation on.
     * @param aKey   Object with the key to check for.
     * @param aValue Object to store.
     */
    private void checkHashForKey(HashMap aHash, Object aKey, Object aValue) {
        if (aHash.containsKey(aKey)) {
            ArrayList temp = (ArrayList) aHash.get(aKey);
            temp.add(aValue);
        } else {
            ArrayList temp = new ArrayList();
            temp.add(aValue);
            aHash.put(aKey, temp);
        }
    }

    /**
     * This method writes a single inclusion list for the specified collection, as well as a 'lookuplist' for this
     * inclusion list.
     *
     * @param aKey   String with the name oof the key (affixed to the inclusion list name).
     * @param aValue Collection with the compounds to write the inclusion list for.
     */
    private void writeInclusionList(String aKey, Collection aValue, double aLowerThresh, double aUpperThresh) throws IOException {
        // Increment the file counter.
        iFileCount++;
        // The writer.
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File(iOutput, "inclusionList_" + iName + "_" + aKey + ".csv")));
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File(iOutput, "lookupList_" + iName + "_" + aKey + ".csv")));
        // The lookuplist has a header.
        bw2.write("mass;M/z (1+);M/z (2+);M/z (3+);Ratio (light/heavy);significance;Position\n");

        // Cycle all compounds.
        Iterator iter = aValue.iterator();
        while (iter.hasNext()) {
            BrukerCompound compound = (BrukerCompound) iter.next();
            // First see if there is a signal-to-noise threshold (ie. 'iS2nThreshold' >=0).
            // If there is one, see if the compound matches, otherwise let it go.
            if (iS2nThreshold < 0 || compound.passesS2nFilter(iS2nThreshold)) {
                // Test significance.
                if (compound.isSingle() || compound.getRegulation(iUseArea) < aLowerThresh || compound.getRegulation(iUseArea) > aUpperThresh) {
                    if (!compound.isSingle()) {
                        iDifferentialCompoundCount++;
                    }
                    // Write the two charge states for the inclusion list.
                    bw.write(compound.getMZForCharge(2) + "");
                    if (iInstrument == QTOF) {
                        bw.write(";2;" + iConeVoltage + ";" + iCollisionEnergy);
                    }
                    bw.write("\n");
                    bw.write(compound.getMZForCharge(3) + "");
                    if (iInstrument == QTOF) {
                        bw.write(";3;" + iConeVoltage + ";" + iCollisionEnergy);
                    }
                    bw.write("\n");
                    // Calculate significance and write the lookup list.
                    double significance = 0.0;
                    if (!compound.isSingle()) {
                        significance = ((Math.log(compound.getRegulation(iUseArea)) / Math.log(2)) - iStatisticalResults[MEDIAN]) / iStatisticalResults[SCALE];
                    }
                    bw2.write(compound.getMass() + ";" + compound.getMZForCharge(1) + ";" +
                            compound.getMZForCharge(2) + ";" + compound.getMZForCharge(3) +
                            ";" + compound.getRegulation(iUseArea) + ";" + significance +
                            ";" + compound.getPosition() + "\n");
                }
            } else {
                continue;
            }
        }
        // Done.
        bw.flush();
        bw.close();
        bw2.flush();
        bw2.close();
    }

    /**
     * This method recurses the specified folder for all files called 'CompoundList.xml'.
     *
     * @param aSourceFolder File with the source folder to start descending from.
     * @param aProgress     DefaultProgressBar with the progressbar to annotate progress on.
     * @return File[] with all the located compound list files.
     */
    private static File[] getAllCompoundListsForFolder(File aSourceFolder, DefaultProgressBar aProgress) {
        ArrayList files = new ArrayList();
        // Actually fill out the list.
        recurseFolder(aSourceFolder, files, aProgress);
        File[] result = new File[files.size()];
        files.toArray(result);
        return result;
    }

    /**
     * This method recurses the specified folder for all files called 'CompoundList.xml'.
     *
     * @param aSourceFolder File with the source folder to start descending from.
     * @param aList         ArrayList which will contain the files. NB: this is a reference parameter.
     * @param aProgress     DefaultProgressBar with the progressbar to annotate progress on.
     */
    private static void recurseFolder(File aSourceFolder, ArrayList aList, DefaultProgressBar aProgress) {
        File[] files = aSourceFolder.listFiles();
        for (int i = 0; i < files.length; i++) {
            File lFile = files[i];
            if (lFile.isDirectory()) {
                if (aProgress != null) {
                    aProgress.setMessage("Scanning " + lFile.getName());
                }
                recurseFolder(lFile, aList, aProgress);
            } else if (lFile.getName().equals("CompoundList.xml")) {
                aList.add(lFile);
            }
        }
    }

    /**
     * The main method is the entry point for the application.
     *
     * @param args string[] with the start-up arguments.
     */
    public static void main(String[] args) {
        try {
            // Check start-up arguments.
            if (args == null || args.length != 1) {
                printUsage();
            }
            // Check input folder.
            File inputFolder = new File(args[0]);
            if (!inputFolder.exists()) {
                printError("Unable to locate the folder you specified ('" + args[0] + "')!");
            }
            if (!inputFolder.isDirectory()) {
                printError("The folder you specified ('" + args[0] + "') is not a directory!");
            }
            // OK, all should be well.
            // Recurse to find all compoundlists.
            File[] allCompoundLists = MALDIDiffAnalysisWorker.getAllCompoundListsForFolder(inputFolder, null);
            HashMap results = new HashMap(allCompoundLists.length);
            // Cycle each list to find the average median.
            double totalMedian = 0.0;
            for (int i = 0; i < allCompoundLists.length; i++) {
                File lAllCompoundList = allCompoundLists[i];
                MALDIDiffAnalysisWorker worker = new MALDIDiffAnalysisWorker(null, null, null, lAllCompoundList, false, 0.2766416, null, MALDIDiffAnalysisWorker.CONFIDENCE_95, 10, 0, 0, 0, null);
                worker.run();
                double[] stats = worker.getStatisticsResults();
                totalMedian += stats[MALDIDiffAnalysisWorker.MEDIAN];
            }
            double avgMedian = totalMedian / allCompoundLists.length;

            //
            // CHOOOSE ONE BLOCK!
            //

            /*
            // Analyze each list, recentering to the average median.
            for (int i = 0; i < allCompoundLists.length; i++) {
                File lAllCompoundList = allCompoundLists[i];
                MALDIDiffAnalysisWorker worker = new MALDIDiffAnalysisWorker(null, null, null, lAllCompoundList, false, null, MALDIDiffAnalysisWorker.CONFIDENCE_95, 10, 0, 0, 0, new Double(avgMedian));
                worker.run();
                double[] stats = worker.getStatisticsResults();
                results.put(lAllCompoundList.getAbsolutePath(), new double[]{stats[MALDIDiffAnalysisWorker.MEDIAN], stats[MALDIDiffAnalysisWorker.SCALE]});
            }
            */

            // Analyse all lists
            MALDIDiffAnalysisWorker worker = new MALDIDiffAnalysisWorker(null, null, null, inputFolder, false, 0.2766416, null, MALDIDiffAnalysisWorker.CONFIDENCE_95, 10, 0, 0, 0, new Double(avgMedian));
            worker.run();
            double[] stats = worker.getStatisticsResults();
            results.put(inputFolder.getAbsolutePath(), new double[]{stats[MALDIDiffAnalysisWorker.MEDIAN], stats[MALDIDiffAnalysisWorker.SCALE]});

            //
            // END CHOOOSE ONE BLOCK!
            //

            // Print the results.
            Iterator iter = results.keySet().iterator();

            logger.info("File;Median;Scale");
            while (iter.hasNext()) {
                String key = (String) iter.next();
                double[] tempStats = (double[]) results.get(key);
                double median = tempStats[0];
                double scale = tempStats[1];
                logger.info(key + ";" + median + ";" + scale);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void printUsage() {
        printError("MALDIDiffAnalysisWorker <source_folder>");
    }

    private static void printError(String aMessage) {
        logger.error("\n\n" + aMessage + "\n\n");
        System.exit(1);
    }
}
