/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 14-jun-2004
 * Time: 11:02:00
 */
package com.compomics.mslimscore.util.diff;

import org.apache.log4j.Logger;

import com.compomics.statlib.descriptive.BasicStats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.6 $
 * $Date: 2004/10/13 10:05:36 $
 */

/**
 * This class will perform a standard differential analysis of all the peptides in the presented CSV file.
 *
 * @author Lennart Martens
 * @version $Id: DiffAnalysis.java,v 1.6 2004/10/13 10:05:36 lennart Exp $
 */
public class DiffAnalysis {
    // Class specific log4j logger for DiffAnalysis instances.
    private static Logger logger = Logger.getLogger(DiffAnalysis.class);

    public static final String STAT_PARAMS = "STAT_PARAMS";

    /**
     * The Vector with the DiffCouples used in the analysis.
     */
    private Vector iCouples = null;

    /**
     * The calibrated standard deviation for log2 scale ratios for 1/1 ratio mixtures on the Q-TOF I mass spectrometer.
     */
    private double iCalibratedStdev = 0.238714;

    /**
     * This constructor creates an instance of this class that uses the specified Vector of DiffCouple instances as its
     * input data for processing.
     *
     * @param aCouples Vector with the DiffCouple instances to perform this analysis on.
     */
    public DiffAnalysis(Vector aCouples) {
        this.iCouples = aCouples;
    }

    /**
     * This method performs the differential analysis and outputs the results as a CSV-formatted String.
     *
     * @return String with the CSV formatted results of the analysis.
     */
    public String getDiffAnalysisAsCSV() {
        // First get the analysis done.
        HashMap stats = this.performDiffAnalysis();
        int liSize = iCouples.size();

        // Next, construct the CSV.
        StringBuffer result = new StringBuffer();

        result.append(";Mu_hat;" + ((double[]) stats.get(STAT_PARAMS))[0] + "\n");
        result.append(";Sigma_hat;" + ((double[]) stats.get(STAT_PARAMS))[1] + "\n");
        result.append(";n;" + liSize + "\n\n");

        result.append(";Filename;16O;18O;Ratio;log2(ratio);Delta;Stdev(delta);Spectrum count;Accession;Start;End;Enzymatic;Sequence;Modified sequence(s);Description\n");
        // Cycle all couples.
        for (int i = 0; i < liSize; i++) {
            DiffCouple dc = (DiffCouple) iCouples.get(i);
            double delta = ((double[]) stats.get(dc.getSequence()))[0];
            double stdevDelta = ((double[]) stats.get(dc.getSequence()))[1];
            result.append(";" + dc.getFilename() + ";" +
                    dc.getLightIntensity() + ";" +
                    dc.getHeavyIntensity() + ";" +
                    dc.getRatio() + ";" +
                    dc.getLog2Ratio() + ";" +
                    delta + ";" +
                    stdevDelta + ";" +
                    dc.getCount() + ";" +
                    dc.getAccession() + ";" +
                    dc.getStart() + ";" +
                    dc.getEnd() + ";" +
                    dc.getEnzymatic() + ";" +
                    dc.getSequence() + ";" +
                    dc.getModifiedSequence() + ";" +
                    dc.getDescription() + ";\n"
            );
        }

        return result.toString();
    }

    /**
     * This HashMap will perform the differential analysis, producing a HashMap (keyed by the sequence from each of the
     * DiffCouples) containing a double[] as value. This double[] holds the delta and stdev[delta] as elements [0] and
     * [1]. <br /> The special key 'STAT_PARAMS' holds a double[] with the mu_hat and sigma_hat used for testing.
     *
     * @return HashMap keyed by the sequence from each of the DiffCouples, with the delta [0] and stdev[delta] [1] as a
     *         double[]. The special key 'STAT_PARAMS' holds a double[] with the mu_hat and sigma_hat used for testing.
     */
    public HashMap performDiffAnalysis() {
        // We'll need log2Ratios for this.
        int liSize = iCouples.size();
        double[] log2Ratios = new double[liSize];
        for (int i = 0; i < liSize; i++) {
            log2Ratios[i] = ((DiffCouple) iCouples.get(i)).getLog2Ratio();
        }
        // Okay, do the stats on the log2 ratios.
        double[] estimators = BasicStats.hubers(log2Ratios, 1e-06, false);
        int n = log2Ratios.length;
        logger.error("Hubers location: " + estimators[0] + " Hubers scale: " + estimators[1] + " Iterations: " + estimators[2]);
        double median = estimators[0];
        double sMedian = estimators[1] * Math.sqrt(Math.PI / (2 * n)) * Math.sqrt(n / (n - 1));

        // This HashMap will store the delta and stdev(delta) for each peptide.
        HashMap stats = new HashMap(liSize);
        // Okay, all set. Cycle each peptide next.
        for (int i = 0; i < liSize; i++) {
            DiffCouple dc = (DiffCouple) iCouples.get(i);
            // Get the peptide average log base 2 ratio
            // and the number of contributing spectra.
            double logRatioPeptide = dc.getLog2Ratio();
            int count = dc.getCount();

            // use the count and the known standard deviation for the mass spec
            // sensitivity to calculate a standard deviation for the log2 ratio.
            //double stdevPeptide = iCalibratedStdev/Math.sqrt(count);

            // Right now: we're just keeping the calibrated standard deviation.
            double stdevPeptide = iCalibratedStdev;

            // Now calculate the delta between the median and this peptide's log2 ratio.
            double delta = logRatioPeptide - median;
            // Now calculate a stdev for this delta (which is the SQRT(stdev[median]^2 + stdev[peptide]^2))
            double stdevDelta = Math.sqrt(Math.pow(sMedian, 2) + Math.pow(stdevPeptide, 2));
            // Store these.
            stats.put(dc.getSequence(), new double[]{delta, stdevDelta});
        }
        // Finally, under the key 'STAT_PARAMS', put mu_hat and sigma_hat.
        stats.put(STAT_PARAMS, new double[]{median, sMedian});
        return stats;
    }

    /**
     * The main method is the entry point in the application.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            printUsage();
        }
        // Okay, let's attempt to read the input file.
        try {
            File file = new File(args[0]);
            if (!file.exists()) {
                throw new IOException("File '" + args[0] + "' could not be found!");
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            int lineCount = 0;
            HashMap allCouples = new HashMap();
            int mergeCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                line = line.trim();
                // Check fo header line.
                if (line.toLowerCase().startsWith("filename;")) {
                    // Header line found; skip it.
                    continue;
                } else {
                    // Parse this line.
                    try {
                        DiffCouple dc = parseCouple(line);
                        if (allCouples.containsKey(dc.getSequence())) {
                            DiffCouple original = (DiffCouple) allCouples.get(dc.getSequence());
                            original.addCouple(dc);
                            mergeCount++;
                        } else {
                            allCouples.put(dc.getSequence(), dc);
                        }
                    } catch (Exception e) {
                        throw new IOException("Unable to parse line nbr. " + lineCount + ": " + e.getMessage());
                    }
                }
            }
            logger.error(" + Merged spectra for " + mergeCount + " sequences.");
            br.close();
            Vector averagedSpectra = new Vector(allCouples.size());
            Iterator iter = allCouples.keySet().iterator();
            while (iter.hasNext()) {
                DiffCouple dc = (DiffCouple) allCouples.get(iter.next());
                if (dc.getCount() > 1) {
                    logger.error(dc.getSequence() + ";" + dc.getCount());
                }
                averagedSpectra.add(dc);
            }
            // At this point, we've gathered all data. Start processing it.
            DiffAnalysis da = new DiffAnalysis(averagedSpectra);
            String output = da.getDiffAnalysisAsCSV();
            logger.info(output);
        } catch (IOException ioe) {
            logger.error("\n\nUnable to parse input file '" + args[0] + "'!" + ioe.getMessage() + "\n");
            System.exit(1);
        }
    }

    /**
     * This method prints class usage information to stderr and exits with error flag raised.
     */
    private static void printUsage() {
        logger.error("\n\nUsage\n\tDiffAnalysis <input_csv_file>\n");
        logger.error("\tRemarks:\n\n\t - CSV file format:\n\t   <first_line=header>\n\t   Filename;16O;18O;Ratio;Spectrum count;Accession;Start;End;Enzymatic;Sequence;Modified sequence(s);Description");
    }

    /**
     * this method parses an InnerCouple from a line of the CSV file.
     *
     * @param aLine String with the line to parse.
     * @return InnerCouple representing the data in the specified line.
     */
    private static DiffCouple parseCouple(String aLine) {
        DiffCouple dc = null;

        StringTokenizer st = new StringTokenizer(aLine, ";");
        String filename = st.nextToken().trim();
        double light = Double.parseDouble(st.nextToken().trim());
        double heavy = Double.parseDouble(st.nextToken().trim());
        // Ratio is skipped, since it will be recalculated anyway.
        st.nextToken();
        int count = Integer.parseInt(st.nextToken().trim());
        String accession = st.nextToken().trim();
        int start = Integer.parseInt(st.nextToken().trim());
        int end = Integer.parseInt(st.nextToken().trim());
        String enzymatic = st.nextToken().trim();
        String sequence = st.nextToken().trim();
        String modSeq = st.nextToken().trim();
        String description = st.nextToken().trim();
        // Skip rest.
        dc = new DiffCouple(0, count, light, heavy, filename, accession, start, end, enzymatic, sequence, modSeq, description);
        // Finis.
        return dc;
    }
}
