/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 22-jun-2004
 * Time: 15:03:11
 */
package com.compomics.mslims.gui.frames;

import com.compomics.rover.general.quantitation.RatioGroup;
import org.apache.log4j.Logger;

import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.gui.table.DistillerQuantitationTableModel;
import com.compomics.mslims.gui.table.ITraqQuantitationTableModel;
import com.compomics.mslims.gui.table.renderers.QuantitationCellRenderer;
import com.compomics.mslims.gui.tree.MascotSearch;
import com.compomics.mslims.util.enumeration.RatioSourceType;
import com.compomics.mslims.util.interfaces.QuantitationProcessor;
import com.compomics.mslims.util.interfaces.QuantitationStorageEngine;
import com.compomics.mslims.util.quantitation.fileio.DistillerQuantitationStorageEngine;
import com.compomics.mslims.util.quantitation.fileio.MascotQuantitationProcessor;
import com.compomics.mslims.util.quantitation.fileio.Ms_limsiTraqQuantitationProcessor;
import com.compomics.mslims.util.quantitation.fileio.Ms_limsiTraqStorageEngine;
import com.compomics.mslims.util.workers.QuantitationStorageWorker;
import com.compomics.mslims.util.workers.QuantitationWorker;
import com.compomics.util.interfaces.Flamable;
import com.compomics.rover.general.quantitation.RatioGroupCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.sql.Connection;
import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2009/05/18 08:01:11 $
 */

/**
 * This class shows a preview of the search results which can subsequently be stored in the database.
 *
 * @author Kenny Helsens
 * @author Niklaas Colaert
 */
public class PreviewQuantitationResultsFrame extends JFrame implements Flamable {
    // Class specific log4j logger for PreviewQuantitationResultsFrame instances.
    private static Logger logger = Logger.getLogger(PreviewQuantitationResultsFrame.class);

    /**
     * The Objects to be previewed.
     */
    private Object[] iPreviewData;

    /**
     * This RatioSourceType enum types the source of the quantitation data. See the Enumeration for the distinct types.
     */
    private RatioSourceType iRatioSourceType;

    /**
     * This Vector will hold all the results from the parsing.
     */
    private Vector<RatioGroupCollection> iQuantitationResults = null;

    /**
     * The working horse for processing the PreviewData.
     */
    private QuantitationProcessor iQuantitationProcessor = null;

    private JTable tblResults = null;
    private Connection iConnection;

    /**
     * This constructor shows a preview of the search results.
     *
     * @param aParent          JFrame with the parent frame.
     * @param aPreviewData     Objects to be previewed. The content depends on the RatioSourceType parameter.
     * @param aConnection      Connection to store the search results to.
     * @param aRatioSourceType The source type of the quantitation data.
     */
    public PreviewQuantitationResultsFrame(JFrame aParent, Object[] aPreviewData, Connection aConnection, final RatioSourceType aRatioSourceType) {
        super();
        iPreviewData = aPreviewData;
        iConnection = aConnection;
        iRatioSourceType = aRatioSourceType;
        if (iRatioSourceType == RatioSourceType.DISTILLER_QUANTITATION_TOOLBOX) {
            iQuantitationProcessor = new MascotQuantitationProcessor(aConnection, this, (MascotSearch[]) aPreviewData);
        } else if (iRatioSourceType == RatioSourceType.ITRAQ_MS_LIMS) {
            iQuantitationProcessor = new Ms_limsiTraqQuantitationProcessor(aConnection, this, (Long[]) aPreviewData);
        }
        // If the quantitaion processor was created succesfully, go in!
        if (iQuantitationProcessor != null) {
            this.acquireData();
            this.constructScreen();
            this.pack();
            this.setLocation(aParent.getLocation().x + 50, aParent.getLocation().y + 50);
            this.setTitle("Preview Quantition results");
            this.setVisible(true);
        } else {
            this.passHotPotato(new Throwable("Quantitation processor was not created properly!! "));
        }

        // Verify whether all RatioGroups are linked to identifications!
        for (Iterator<RatioGroupCollection> lRatioGroupCollectionIterator = iQuantitationResults.iterator(); lRatioGroupCollectionIterator.hasNext();) {
            RatioGroupCollection lRatioGroupCollection = lRatioGroupCollectionIterator.next();
            if(!hasIdentificationsInEachRatioGroup(lRatioGroupCollection)){
                JOptionPane.showMessageDialog(new JFrame(), "Some RatioGroups could not be linked to peptide identifications!!\n", "Problem previewing rov file", JOptionPane.WARNING_MESSAGE);
                break;
            };
        }
    }

    /**
     * This method takes care of any unrecoverable exception or error, thrown by a child thread.
     *
     * @param aThrowable Throwable that represents the unrecoverable error or exception.
     */
    public void passHotPotato(Throwable aThrowable) {
        this.passHotPotato(aThrowable, aThrowable.getMessage());
    }

    /**
     * This method takes care of any unrecoverable exception or error, thrown by a child thread.
     *
     * @param aThrowable Throwable that represents the unrecoverable error or exception.
     * @param aMessage   String with an extra message to display.
     */
    public void passHotPotato(Throwable aThrowable, String aMessage) {
        logger.error(aThrowable.getMessage(), aThrowable);
        JOptionPane.showMessageDialog(this, new String[]{"An error occurred while attempting to process your data:", aMessage}, "Error occurred!", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Create the GUI components.
     */
    private void constructScreen() {
        // The results table.
        tblResults = new JTable();
        tblResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        if (iRatioSourceType == RatioSourceType.DISTILLER_QUANTITATION_TOOLBOX) {
            tblResults.setModel(new DistillerQuantitationTableModel(iQuantitationResults));
        } else if (iRatioSourceType == RatioSourceType.ITRAQ_MS_LIMS) {
            tblResults.setModel(new ITraqQuantitationTableModel(iQuantitationResults));
        }
        tblResults.setDefaultRenderer(Object.class, new QuantitationCellRenderer());
        JPanel jpanResults = new JPanel(new BorderLayout());
        jpanResults.setBorder(BorderFactory.createTitledBorder("Preview results"));
        jpanResults.add(new JScrollPane(tblResults), BorderLayout.CENTER);

        // The main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanResults);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(this.getButtonPanel());
        jpanMain.add(Box.createVerticalStrut(5));

        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method creates the button panel.
     *
     * @return JPanel with the button panel.
     */
    private JPanel getButtonPanel() {
        // Create the necessary buttons.
        JButton btnStore = new JButton("Store");
        btnStore.setMnemonic(KeyEvent.VK_S);
        btnStore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeTriggered();
            }
        });
        btnStore.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    storeTriggered();
                }
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelTriggered();
            }
        });
        btnCancel.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cancelTriggered();
                }
            }
        });

        JButton btnSaveCSV = new JButton("Copy table...");
        btnSaveCSV.setMnemonic(KeyEvent.VK_O);
        btnSaveCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyTriggered();
            }
        });
        btnSaveCSV.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    copyTriggered();
                }
            }
        });
        // The column-selection checkbox.
        // Create the checkbox.
        final JCheckBox chkSelection = new JCheckBox("Column selection mode", false);
        chkSelection.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (chkSelection.isSelected()) {
                    tblResults.setColumnSelectionAllowed(true);
                    tblResults.setRowSelectionAllowed(false);
                } else {
                    tblResults.setColumnSelectionAllowed(false);
                    tblResults.setRowSelectionAllowed(true);
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalStrut(10));
        jpanButtons.add(chkSelection);
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnSaveCSV);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnStore);
        jpanButtons.add(Box.createHorizontalStrut(10));

        return jpanButtons;
    }

    /**
     * This method is called when the user presses 'store'.
     */
    private void storeTriggered() {

        DefaultProgressBar progress =
                new DefaultProgressBar(this, "Processing quantitation preview results...", 0, iQuantitationResults.size() + 2);
        progress.setSize(350, 100);
        progress.setMessage("Starting up...");

        
        // This HashMap will save report information succes or failure rate of storage of the RatioGroupCollections
        HashMap<String, Boolean> lWorkerReport = new HashMap<String, Boolean>();

        QuantitationStorageEngine lEngine = null;
        if (iRatioSourceType == RatioSourceType.DISTILLER_QUANTITATION_TOOLBOX) {
            // Ratio's come from Mascot Distiller, create the corresponding storage engine;
            lEngine = new DistillerQuantitationStorageEngine(this, iConnection);
        } else if (iRatioSourceType == RatioSourceType.ITRAQ_MS_LIMS) {
            // Ratio's come from iTraq data
            lEngine = new Ms_limsiTraqStorageEngine(this, iConnection);
        } else {
            // Other engines?!
        }

        QuantitationStorageWorker worker =
                new QuantitationStorageWorker(lEngine, iQuantitationResults, this, progress, lWorkerReport);
        worker.start();
        progress.setVisible(true);

        // Make report.
        String lMessage = createWorkerReport(lWorkerReport);

        JOptionPane.showMessageDialog(this, lMessage, "Storage complete", JOptionPane.INFORMATION_MESSAGE);
        this.dispose();
    }

    /**
     * This method is called when the user presses 'cancel'.
     */
    private void cancelTriggered() {
        this.close();
    }

    /**
     * Creates a String report from the HashMap created upon the worker's actions.
     *
     * @param aWorkerReport The HashMap with reports for each RatioGroupCollection.
     * @return The report String
     */
    private String createWorkerReport(HashMap<String, Boolean> aWorkerReport) {
        StringBuffer sb = new StringBuffer();
        int lSuccesCounter = 0;
        int lFailureCounter = 0;
        ArrayList listFailures = new ArrayList();

        // Iterate over all status reports.
        Collection<Boolean> lStatusCollection = aWorkerReport.values();
        int index = 0;
        for (Iterator<Boolean> lBooleanIterator = lStatusCollection.iterator(); lBooleanIterator.hasNext();) {
            if (lBooleanIterator.next()) {
                lSuccesCounter++;
            } else {
                // In case of failure, increase the failure counter and keep track of the filename.
                lFailureCounter++;
                listFailures.add(aWorkerReport.keySet().toArray()[index]);
            }
            index++;
        }

        if (lFailureCounter == 0) {
            // Good news!
            sb.append(lSuccesCounter + " distiller quantitation xml files were processed correctly!");
        } else {
            sb.append(lSuccesCounter + " distiller quantitation xml files were processed correctly");
            sb.append("\n");
            sb.append(lFailureCounter + " distiller quantitation xml files were processed incorrectly");
            sb.append("\n");
            for (int i = 0; i < listFailures.size(); i++) {
                Object o = listFailures.get(i);
                sb.append(o);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * This method is called when the user presses 'copy'.
     */
    private void copyTriggered() {
        int nbrCols = this.tblResults.getColumnCount();
        int nbrRows = this.tblResults.getRowCount();

        String data = null;

        StringBuffer allRows = new StringBuffer();
        // Get the headers.
        for (int i = 0; i < nbrCols; i++) {
            allRows.append(this.tblResults.getColumnName(i) + "\t");
        }
        allRows.append("\n");
        // Now the data.
        for (int i = 0; i < nbrRows; i++) {
            for (int j = 0; j < nbrCols; j++) {
                Object o = this.tblResults.getValueAt(i, j);
                String tempData;
                if (o == null) {
                    tempData = "";
                } else {
                    tempData = o.toString();
                }
                // Remove possible HTML tags.
                if (tempData.indexOf("<html>") >= 0 && tempData.indexOf("</html>") > 0) {
                    // Remove 'html' tags.
                    int start = -1;
                    while ((start = tempData.indexOf("<html>")) >= 0) {
                        tempData = tempData.substring(0, start) + tempData.substring(start + 6);
                    }
                    while ((start = tempData.indexOf("</html>")) >= 0) {
                        tempData = tempData.substring(0, start) + tempData.substring(start + 7);
                    }
                }
                allRows.append(tempData + "\t");
            }
            allRows.append("\n");
        }
        data = allRows.toString();

        String message = null;
        int type = 0;
        if (nbrRows > 0 && data != null) {
            Object tempObject = new StringSelection(data);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents((Transferable) tempObject, (ClipboardOwner) tempObject);
            message = nbrRows + " rows copied to clipboard!";
            type = JOptionPane.INFORMATION_MESSAGE;
        } else {
            message = "No data to copy!";
            type = JOptionPane.WARNING_MESSAGE;
        }
        JOptionPane.showMessageDialog(this, message, "Copy complete.", type);
    }

    /**
     * This method disposes the dialog. It does not close the DB connection, though!
     */
    private void close() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * This method collects the information needed for the preview from the MascotResultsProcessor.
     */
    private void acquireData() {
        iQuantitationResults = new Vector<RatioGroupCollection>();

        DefaultProgressBar progress =
                new DefaultProgressBar(this, "Processing quantitation...", 0, (iPreviewData.length) + 1);
        progress.setSize(350, 100);
        progress.setMessage("Starting up...");
        QuantitationWorker worker =
                new QuantitationWorker(iQuantitationProcessor, iQuantitationResults, this, progress);
        worker.start();
        progress.setVisible(true);
    }


    /**
     * Verify whether each ratioGroup has an identification linked to it.
     * @return Boolean on the
     * @param aRatioGroupCollection
     */
    private boolean hasIdentificationsInEachRatioGroup(final RatioGroupCollection aRatioGroupCollection) {
        int lCounter = 0;
        for (Iterator lIterator = aRatioGroupCollection.iterator(); lIterator.hasNext();) {
            RatioGroup lRatioGroup = (RatioGroup) lIterator.next();
            if(lRatioGroup.getNumberOfIdentifications() > 0){
                // Ok, this ratiogroup has a linked peptide identification.
                lCounter++;
            }
        }
        return lCounter == aRatioGroupCollection.size();
    }
}
