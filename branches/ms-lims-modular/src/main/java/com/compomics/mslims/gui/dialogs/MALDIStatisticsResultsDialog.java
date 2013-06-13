/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 5-okt-2005
 * Time: 11:44:01
 */
package com.compomics.mslims.gui.dialogs;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.workers.MALDIDiffAnalysisWorker;
import com.compomics.mslims.util.interfaces.BrukerCompound;
import com.compomics.mslims.util.fileio.BrukerCompoundCouple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/10/27 12:33:20 $
 */

/**
 * This class implements a dialog that can display the results of a MALDI differential analysis.
 *
 * @author Lennart Martens
 * @version $Id: MALDIStatisticsResultsDialog.java,v 1.3 2005/10/27 12:33:20 lennart Exp $
 */
public class MALDIStatisticsResultsDialog extends JDialog {
    // Class specific log4j logger for MALDIStatisticsResultsDialog instances.
    private static Logger logger = Logger.getLogger(MALDIStatisticsResultsDialog.class);

    /**
     * The name of this analysis.
     */
    private String iName = null;

    /**
     * The median.
     */
    private double iMedian = 0.0;

    /**
     * The (Huber) scale.
     */
    private double iScale = 0.0;

    /**
     * The total number of compounds read.
     */
    private int iCompoundsRead = 0;

    /**
     * The total number of compounds that were singles.
     */
    private int iCompoundsSingle = 0;

    /**
     * The total number of compounds paired.
     */
    private int iPairs = 0;

    /**
     * The total number of compounds skipped.
     */
    private int iCompoundsSkipped = 0;

    /**
     * The total number of files written.
     */
    private int iTotalFilesWritten = -1;

    /**
     * The total number of regulated, non-single compound couples, written to the inclusion lists.
     */
    private int iTotalRegulatedCompounds = -1;

    /**
     * Boolean that indicates whether to use area ('true') or intensity ('false') for the ratio calculation.
     */
    private boolean iUseArea = true;

    /**
     * 95% confidence interval limits in log2.
     */
    private double[] i95Confidence = new double[2];

    /**
     * 95% confidence interval limits in log2.
     */
    private double[] i98Confidence = new double[2];

    /**
     * Number of significantly up- or downregulated compound pairs at the 95% confidence interval.
     */
    private int iRegulatedCount95 = 0;

    /**
     * Number of significantly up- or downregulated compound pairs at the 98% confidence interval.
     */
    private int iRegulatedCount98 = 0;

    /**
     * The hashmap with the detected couples.
     */
    private HashMap iCouples = null;

    private JTextArea txtReadable = null;
    private JTextArea txtCSV = null;
    private JButton btnSaveCSVCouples = null;
    private JButton btnOK = null;

    /**
     * Creates a modal dialog with the specified title and the specified owner. All data members are initialized as
     * well.
     *
     * @param owner                    the Frame from which the dialog is displayed.
     * @param title                    the String to display in the dialog's title bar.
     * @param aName                    String with the name of the analysis.
     * @param aCompoundsRead           int with the total number of compounds read from file.
     * @param aPairs                   int with the number of pairs formed from the read compounds.
     * @param aCompoundsSingle         int with the number of compounds that were single.
     * @param aCompoundsSkipped        int with the number of compounds that were skipped.
     * @param aStats                   double[] with the statistical analysis of the data.
     * @param aCouples                 HashMap with all the couples found.
     * @param aTotalFilesWritten       int with the total number of files written.
     * @param aTotalRegulatedCompounds int with the number of regulated couples, written to the inclusion lists.
     * @param aUseArea                 boolean that indicates use of compound area ('true') or intensity ('false') for
     *                                 ratio calculation.
     */
    public MALDIStatisticsResultsDialog(Frame owner, String title, String aName, int aCompoundsRead, int aPairs, int aCompoundsSingle, int aCompoundsSkipped, double[] aStats, HashMap aCouples, boolean aUseArea, int aTotalFilesWritten, int aTotalRegulatedCompounds) {
        super(owner, title, true);
        iName = aName;
        iCompoundsRead = aCompoundsRead;
        iPairs = aPairs;
        iCompoundsSingle = aCompoundsSingle;
        iCompoundsSkipped = aCompoundsSkipped;
        iMedian = aStats[MALDIDiffAnalysisWorker.MEDIAN];
        iScale = aStats[MALDIDiffAnalysisWorker.SCALE];
        iRegulatedCount95 = (int) aStats[MALDIDiffAnalysisWorker.OUTLIER_COUNT_95];
        iRegulatedCount98 = (int) aStats[MALDIDiffAnalysisWorker.OUTLIER_COUNT_98];
        iTotalFilesWritten = aTotalFilesWritten;
        iTotalRegulatedCompounds = aTotalRegulatedCompounds;
        i95Confidence[0] = aStats[MALDIDiffAnalysisWorker.CONFIDENCE_95_LOWER_LOG2];
        i95Confidence[1] = aStats[MALDIDiffAnalysisWorker.CONFIDENCE_95_UPPER_LOG2];
        i98Confidence[0] = aStats[MALDIDiffAnalysisWorker.CONFIDENCE_98_LOWER_LOG2];
        i98Confidence[1] = aStats[MALDIDiffAnalysisWorker.CONFIDENCE_98_UPPER_LOG2];
        iCouples = aCouples;
        iUseArea = aUseArea;
        this.constructScreen();
        this.initTexts();
        this.pack();
    }

    /**
     * Creates a modal dialog with the specified title and the specified owner. All data members apart from the number
     * of files written and differentially regulated couple count are initialized as well. Use this constructor for
     * statistical analysis only.
     *
     * @param owner             the Frame from which the dialog is displayed
     * @param title             the String to display in the dialog's title bar
     * @param aName             String with the name of the analysis.
     * @param aCompoundsRead    int with the total number of compounds read from file.
     * @param aPairs            int with the number of pairs formed from the read compounds.
     * @param aCompoundsSingle  int with the number of compounds that were single.
     * @param aCompoundsSkipped int with the number of compounds that were skipped.
     * @param aStats            double[] with the statistical analysis of the data.
     * @param aCouples          HashMap with all the couples found.
     * @param aUseArea          boolean that indicates use of compound area ('true') or intensity ('false') for ratio
     *                          calculation.
     */
    public MALDIStatisticsResultsDialog(Frame owner, String title, String aName, int aCompoundsRead, int aPairs, int aCompoundsSingle, int aCompoundsSkipped, double[] aStats, HashMap aCouples, boolean aUseArea) {
        this(owner, title, aName, aCompoundsRead, aPairs, aCompoundsSingle, aCompoundsSkipped, aStats, aCouples, aUseArea, -1, -1);
    }

    /**
     * This method initializes the text on the text areas.
     */
    private void initTexts() {
        txtReadable.setText(this.getReadableText());
        txtReadable.setCaretPosition(0);
        txtCSV.setText(this.getCSVText());
        txtCSV.setCaretPosition(0);
    }

    /**
     * This method initializes and lays out the components on the GUI.
     */
    private void constructScreen() {
        // Readable text area.
        txtReadable = new JTextArea(8, 40);
        txtReadable.setEditable(false);
        // The panel for the CSV text area.
        JPanel jpanReadable = new JPanel(new BorderLayout());
        jpanReadable.setBorder(BorderFactory.createTitledBorder("Human readable text"));
        jpanReadable.add(new JScrollPane(txtReadable), BorderLayout.CENTER);

        // CSV text area.
        txtCSV = new JTextArea(8, 40);
        txtCSV.setEditable(false);
        // The panel for the CSV text area.
        JPanel jpanCSV = new JPanel(new BorderLayout());
        jpanCSV.setBorder(BorderFactory.createTitledBorder("CSV text"));
        jpanCSV.add(new JScrollPane(txtCSV), BorderLayout.CENTER);

        // The split pane for the two text areas.
        JSplitPane spltTextAreas = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        spltTextAreas.add(jpanReadable);
        spltTextAreas.add(jpanCSV);
        spltTextAreas.setOneTouchExpandable(true);
        spltTextAreas.setDividerLocation(0.6);
        spltTextAreas.setResizeWeight(0.6);
        JPanel jpanDummy = new JPanel(new BorderLayout());
        jpanDummy.add(spltTextAreas, BorderLayout.CENTER);

        // Save couples to CSV file button.
        btnSaveCSVCouples = new JButton("Save couples...");
        btnSaveCSVCouples.setMnemonic(KeyEvent.VK_S);
        btnSaveCSVCouples.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCouples();
            }
        });
        btnSaveCSVCouples.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveCouples();
                }
            }
        });
        // OK button.
        btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        btnOK.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    close();
                }
            }
        });
        // Button panel.
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnSaveCSVCouples);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnOK);
        jpanButtons.add(Box.createHorizontalStrut(10));

        // Main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanDummy);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanButtons);
        jpanMain.add(Box.createVerticalStrut(5));
        // Add the main panel to the Dialog.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method closes the dialog.
     */
    public void close() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * This method is called when the user clicks the 'save couples as CSV file' button.
     */
    private void saveCouples() {
        boolean lbContinue = true;
        String folder = "/";
        File output = null;
        while (lbContinue) {
            JFileChooser jfc = new JFileChooser(folder);
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);
            // In case of 'Cancel' or 'Error', just return.
            if (jfc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            // Check whether file exists and if overwrite is desired.
            output = jfc.getSelectedFile();
            if (output.exists()) {
                int result = JOptionPane.showConfirmDialog(this, "File '" + output.getName() + "' exists, do you wish to overwrite?", "File exists. Overwrite?", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    // Set the starting folder to the current one and continue the loop.
                    folder = output.getParentFile().getAbsolutePath();
                } else {
                    // We'll overwrite, so exit the loop.
                    lbContinue = false;
                }
            } else {
                // File is new, so exit loop.
                lbContinue = false;
            }
        }
        try {
            // Write output.
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            // Header line.
            bw.write("Ratio (light/heavy);Light mass;Heavy mass;Light S/N;Heavy S/N;\r\n");
            // Now cycle all couples and write them out.
            Iterator iter = iCouples.keySet().iterator();
            while (iter.hasNext()) {
                StringBuffer csvLine = new StringBuffer();
                Object key = iter.next();
                BrukerCompoundCouple lCompound = (BrukerCompoundCouple) iCouples.get(key);
                csvLine.append(lCompound.getRegulation(iUseArea) + ";");
                csvLine.append(lCompound.getLightMass() + ";");
                csvLine.append(lCompound.getHeavyMass() + ";");
                csvLine.append(lCompound.getLightS2n() + ";");
                csvLine.append(lCompound.getHeavyS2n() + "\r\n");
                bw.write(csvLine.toString());
            }
            bw.flush();
            bw.close();
            JOptionPane.showMessageDialog(this, "Written " + iCouples.size() + " couples to CSV output file '.", "CSV output file written.", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            JOptionPane.showMessageDialog(this, new String[]{"Writing output file '" + output.getName() + "' failed.", ioe.getMessage()}, "Unable to write output file!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method will create the human readable report.
     *
     * @return String with the human readable report.
     */
    private String getReadableText() {
        StringBuffer sb = new StringBuffer();

        sb.append("Report for " + iName + ":\n");
        sb.append("\nRead " + iCompoundsRead + " compounds from file, of which");
        sb.append("\n\t- " + iCompoundsSingle + " were single,");
        sb.append("\n\t- " + (iPairs * 2) + " were paired into " + iPairs + " pairs, and");
        sb.append("\n\t- " + iCompoundsSkipped + " were skipped due to the ambiguity of the couples.");
        sb.append("\n\n");
        sb.append("Statistics for the couples:");
        sb.append("\n\t- n: " + iPairs);
        sb.append("\n\t- median: " + new BigDecimal(iMedian).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + " (log2), or " + new BigDecimal(Math.pow(2, iMedian)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + " (ratio)");
        sb.append("\n\t- Corrected Huber scale estimate: " + new BigDecimal(iScale).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
        sb.append("\n\n");
        sb.append("Confidence intervals:");
        sb.append("\n\t- 95%: [" + new BigDecimal(i95Confidence[0]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ", " + new BigDecimal(i95Confidence[1]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + "] (log2), or [" + new BigDecimal(Math.pow(2, i95Confidence[0])).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ", " + new BigDecimal(Math.pow(2, i95Confidence[1])).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + "] (ratio) - yielding " + iRegulatedCount95 + " significantly up-or downregulated compound pairs.");
        sb.append("\n\t- 98%: [" + new BigDecimal(i98Confidence[0]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ", " + new BigDecimal(i98Confidence[1]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + "] (log2), or [" + new BigDecimal(Math.pow(2, i98Confidence[0])).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ", " + new BigDecimal(Math.pow(2, i98Confidence[1])).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + "] (ratio) - yielding " + iRegulatedCount98 + " significantly up-or downregulated compound pairs.");
        if (iTotalFilesWritten > 0) {
            sb.append("\n\nWrote " + iTotalFilesWritten + " inclusion lists, ");
            sb.append("containing " + iTotalRegulatedCompounds + " significantly up- or down regulated couples");
            sb.append(" and " + iCompoundsSingle + " single compounds.");
        }

        return sb.toString();
    }

    /**
     * This method will create the report in CSV.
     *
     * @return String with the report in CSV.
     */
    private String getCSVText() {
        StringBuffer sb = new StringBuffer();

        sb.append("Report for " + iName);
        sb.append("\nCompounds read;" + iCompoundsRead);
        sb.append("\nSingle compounds;" + iCompoundsSingle);
        sb.append("\nCouples found;" + iPairs);
        sb.append("\nSkipped compounds;" + iCompoundsSkipped);
        sb.append("\n\nNumber;data points;median;corrected Huber scale;lower limit (95%);upper limit (95%);significantly regulated count (95%);lower limit (98%);upper limit (98%);significantly regulated count (98%)");
        sb.append("\nlog2;" + iPairs + ";" + new BigDecimal(iMedian).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + new BigDecimal(iScale).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + new BigDecimal(i95Confidence[0]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + new BigDecimal(i95Confidence[1]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + iRegulatedCount95 + ";" + new BigDecimal(i98Confidence[0]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + new BigDecimal(i98Confidence[1]).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + iRegulatedCount98);
        sb.append("\nratio;" + iPairs + ";" + new BigDecimal(Math.pow(2, iMedian)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + "N/A" + ";" + new BigDecimal(Math.pow(2, i95Confidence[0])).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + new BigDecimal(Math.pow(2, i95Confidence[1])).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + iRegulatedCount95 + ";" + new BigDecimal(Math.pow(2, i98Confidence[0])).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + new BigDecimal(Math.pow(2, i98Confidence[1])).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue() + ";" + iRegulatedCount98);
        if (iTotalFilesWritten > 0) {
            sb.append("\n\ninclusion list files written;" + iTotalFilesWritten);
            sb.append("\nregulated couples in list;" + iTotalRegulatedCompounds);
            sb.append("\nsingles in list;" + iCompoundsSingle);
        }

        return sb.toString();
    }
}
