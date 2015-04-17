/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 22-jun-2004
 * Time: 15:03:11
 */
package com.compomics.mslims.gui.frames;

import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.gui.projectanalyzertools.IdentificationSwitcherGUI;
import com.compomics.mslims.gui.table.IdentificationTableAccessorsTableModel;
import com.compomics.mslims.gui.tree.MascotSearch;
import com.compomics.mslims.util.mascot.MascotResultsProcessor;
import com.compomics.mslims.util.workers.MascotResultsProcessorWorker;
import com.compomics.mslims.util.workers.MascotResultsStorageWorker;
import com.compomics.mslimsdb.accessors.AlternativeIdentification;
import com.compomics.util.gui.JTableForDB;
import com.compomics.util.interfaces.Flamable;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.apache.log4j.Logger;

/*
 * CVS information:
 *
 * $Revision: 1.10 $
 * $Date: 2008/11/28 16:07:18 $
 */

/**
 * This class shows a preview of the search results which can subsequently be stored in the database.
 *
 * @author Lennart Martens
 * @version $Id: PreviewSearchResultsFrame.java,v 1.10 2008/11/28 16:07:18 kenny Exp $
 */
public class PreviewSearchResultsFrame extends JFrame implements Flamable {
    // Class specific log4j logger for PreviewSearchResultsFrame instances.
    private static Logger logger = Logger.getLogger(PreviewSearchResultsFrame.class);

    IdentificationSwitcherGUI identificationSwitcher;

    /**
     * The searches to process.
     */
    private MascotSearch[] iSearches = null;

    /**
     * This Vector will hold all the results from the parsing.
     */
    private Vector iResults = null;

    /**
     * the working horse for this display.
     */
    private MascotResultsProcessor iMRP = null;


    JTableForDB tblResults = null;

    private Vector<AlternativeIdentification> iAlternativeResults;
    private boolean identificationSwitcherUsed;

    /**
     * This constructor shows a preview of the search results.
     *
     * @param aParent                    JFrame with the parent frame.
     * @param aSearches                  MascotSearch[] with the searches to process.
     * @param aConnection                Connection to store the search results to.
     * @param aThreshold                 double with the confidence interval to use.
     * @param aMascotDistillerProcessing boolean whether Mascot Distiller is used for Processing.
     */
    public PreviewSearchResultsFrame(JFrame aParent, MascotSearch[] aSearches, Connection aConnection, double aThreshold, final boolean aMascotDistillerProcessing) {
        super();
        this.iSearches = aSearches;
        iMRP = new MascotResultsProcessor(aConnection, aThreshold, aMascotDistillerProcessing);
        this.acquireData();
        this.constructScreen();
        this.pack();
        this.setLocation(aParent.getLocation().x + 50, aParent.getLocation().y + 50);
        this.setTitle("Preview search results at " + ((1 - aThreshold) * 100) + "% confidence interval  --  " + tblResults.getRowCount() + " spectra identified");
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

    private void constructScreen() {
        // The results table.
        tblResults = new JTableForDB();
        tblResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblResults.setModel(new IdentificationTableAccessorsTableModel(iResults), true);
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
/*
        JButton btnChangeMaster = new JButton("Change master ...");
        if (iAlternativeResults != null) {
            btnChangeMaster.setMnemonic(KeyEvent.VK_M);
            btnChangeMaster.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {
                    editMasterTriggered();
                }
            });
            btnChangeMaster.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                 editMasterTriggered();
            }
            });
        } else {
            btnChangeMaster.setEnabled(false);
        }
        */
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
        //jpanButtons.add(btnChangeMaster);
        //jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnSaveCSV);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnStore);
        jpanButtons.add(Box.createHorizontalStrut(10));

        return jpanButtons;
    }
        private void editMasterTriggered() {
        identificationSwitcher = new IdentificationSwitcherGUI("master and alternative peptide identification overview",iResults,iAlternativeResults);
        identificationSwitcherUsed = true;
    }

    /**
     * This method is called when the user presses 'store'.
     */
    private void storeTriggered() {
        DefaultProgressBar progress =
                new DefaultProgressBar(this, "Processing search results...", 0, iResults.size() + 2);
        progress.setSize(350, 100);
        progress.setMessage("Starting up...");
        if(identificationSwitcherUsed){
            iResults = identificationSwitcher.getMastersToStore();
            Vector<AlternativeIdentification> iAlternativeResultsToStore = identificationSwitcher.getAlternativesToStore();
            if(iAlternativeResultsToStore == null){
                iResults.addAll(iAlternativeResults);
             } else {
                iResults.addAll(iAlternativeResultsToStore);
            }
            MascotResultsStorageWorker worker =
                    new MascotResultsStorageWorker(this.iMRP, iResults, iSearches, this, progress);
            worker.start();
            progress.setVisible(true);
            JOptionPane.showMessageDialog(this, "Storage of identifications complete!", "Storage complete", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } else {
            MascotResultsStorageWorker worker =
                    new MascotResultsStorageWorker(this.iMRP, iResults, iSearches, this, progress);
            worker.start();
            progress.setVisible(true);
            JOptionPane.showMessageDialog(this, "Storage of identifications complete!", "Storage complete", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }
    }
    /**
     * This method is called when the user presses 'cancel'.
     */
    private void cancelTriggered() {
        this.close();
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
                String tempData = this.tblResults.getValueAt(i, j).toString();
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
        this.dispose();
    }

    /**
     * This method collects the information needed for the preview from the MascotResultsProcessor.
     */
    private void acquireData() {
        iResults = new Vector(3000, 750);
        iAlternativeResults = new Vector<AlternativeIdentification>();
        DefaultProgressBar progress =
                new DefaultProgressBar(this, "Processing search results...", 0, (iSearches.length * 2) + 3);
        progress.setSize(350, 100);
        progress.setMessage("Starting up...");
        MascotResultsProcessorWorker worker =
                new MascotResultsProcessorWorker(iMRP, iSearches,iAlternativeResults, iResults, this, progress);
        worker.start();
        progress.setVisible(true);
    }
}
