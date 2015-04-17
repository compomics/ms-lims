/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 12-okt-2004
 * Time: 13:13:58
 */
package com.compomics.mslims.gui.frames;

import com.compomics.mslims.gui.table.DiffCoupleTableModel;
import com.compomics.mslims.gui.table.renderers.ErrorCellRenderer;
import com.compomics.mslims.gui.table.renderers.ErrorObject;
import com.compomics.mslims.util.diff.DiffCouple;
import com.compomics.mslims.util.diff.DifferentialProject;
import org.apache.log4j.Logger;

import com.compomics.util.gui.JTableForDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.8 $
 * $Date: 2005/05/18 14:30:35 $
 */

/**
 * This class represents a JFrame that will show the results table from a differential analysis.
 *
 * @author Lennart Martens
 * @version $Id: DifferentialAnalysisResultsFrame.java,v 1.8 2005/05/18 14:30:35 lennart Exp $
 */
public class DifferentialAnalysisResultsFrame extends JFrame {
    // Class specific log4j logger for DifferentialAnalysisResultsFrame instances.
    private static Logger logger = Logger.getLogger(DifferentialAnalysisResultsFrame.class);

    private JTableForDB tblResults = null;

    public DifferentialAnalysisResultsFrame(String aTitle, Vector aResults, final HashMap aProjects, String aLightLabel, String aHeavyLabel, final int aAverageMethod) {
        super(aTitle);
        tblResults = new JTableForDB();
        tblResults.setDefaultRenderer(ErrorObject.class, new ErrorCellRenderer());
        tblResults.setModel(new DiffCoupleTableModel(aResults, aProjects, aLightLabel, aHeavyLabel, aAverageMethod), true);
        tblResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblResults.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            public void mouseClicked(MouseEvent e) {
                // Transform clickpoint to row and column indices +
                // retrieve the renderer at that location.
                Point compLoc = e.getPoint();
                int col = tblResults.columnAtPoint(compLoc);
                int row = tblResults.rowAtPoint(compLoc);
                // If somebody double-clicks with the left mouse-button on a column that
                // contains 'alias' in the column header...
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() > 1 && tblResults.getColumnName(col).toLowerCase().indexOf("alias") > 0) {
                    // Creating the frame with the data from the model.
                    int modelCol = tblResults.convertColumnIndexToModel(col);
                    Object temp = tblResults.getModel().getValueAt(row, DiffCoupleTableModel.REPORT_INSTANCE);
                    if (temp instanceof ErrorObject) {
                        temp = ((ErrorObject) temp).getValue();
                    }
                    DiffCouple dc = (DiffCouple) temp;
                    if (dc.getCount() > 1) {
                        double[] stats = dc.getLocationAndScale();
                        Object[][] data = new Object[dc.getCount()][14];
                        Vector merged = dc.getMergedEntries();
                        int liSize = merged.size();
                        for (int i = 1; i <= liSize; i++) {
                            DiffCouple child = (DiffCouple) merged.get(i - 1);
                            fillDataArray(data[i], aProjects, child, stats[0], stats[1]);
                        }
                        // Now do the 'parent' one.
                        fillDataArray(data[0], aProjects, dc, stats[0], stats[1]);
                        // Create a JTable to hold this stuff.
                        DefaultTableModel dtm = new DefaultTableModel(data, new String[]{"Project alias",
                                "Filename", "Accession",
                                "Description", "Light",
                                "Heavy", "Ratio(light/heavy)", "Ratio correction",
                                "log2(ratio)", "Modified sequence",
                                "Start", "End", "Enzymatic", "Outlier"}) {
                            /**
                             * Returns true regardless of parameter values.
                             *
                             * @param row    the row whose value is to be queried
                             * @param column the column whose value is to be queried
                             * @return true
                             */
                            public boolean isCellEditable(int row, int column) {
                                return false;    //To change body of overridden methods use File | Settings | File Templates.
                            }

                            /**
                             * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
                             *
                             * @param columnIndex the column being queried
                             * @return the Object.class
                             */
                            public Class getColumnClass(int columnIndex) {
                                Class result = null;
                                result = ErrorObject.class;
                                /*
                                switch(columnIndex) {
                                    case 0:
                                        result = String.class;
                                        break;
                                    case 1:
                                        result = String.class;
                                        break;
                                    case 2:
                                        result = String.class;
                                        break;
                                    case 3:
                                        result = String.class;
                                        break;
                                    case 4:
                                        result = Double.class;
                                        break;
                                    case 5:
                                        result = Double.class;
                                        break;
                                    case 6:
                                        result = Double.class;
                                        break;
                                    case 7:
                                        result = Double.class;
                                        break;
                                    case 8:
                                        result = Double.class;
                                        break;
                                    case 9:
                                        result = String.class;
                                        break;
                                    case 10:
                                        result = Integer.class;
                                        break;
                                    case 11:
                                        result = Integer.class;
                                        break;
                                    case 12:
                                        result = String.class;
                                        break;
                                }
                                */
                                return result;
                            }
                        };
                        final JTableForDB tempTable = new JTableForDB();
                        tempTable.setDefaultRenderer(ErrorObject.class, new ErrorCellRenderer());
                        tempTable.setModel(dtm, true);
                        tempTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        // Format the location and scale so as to be manageable.
                        BigDecimal bdLocation = new BigDecimal(stats[0]);
                        bdLocation = bdLocation.setScale(5, BigDecimal.ROUND_HALF_UP);
                        BigDecimal bdScale = new BigDecimal(stats[1]);
                        bdScale = bdScale.setScale(5, BigDecimal.ROUND_HALF_UP);
                        final JFrame tempFrame = new JFrame("Merged couples for " + dc.getSequence() + "  /  location: " + bdLocation + " ;  scale: " + bdScale);
                        // Button/CheckBox panel.
                        JButton btnCopy = new JButton("Copy table");
                        btnCopy.setMnemonic(KeyEvent.VK_O);
                        btnCopy.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                copyTriggered(tempFrame, tempTable);
                            }
                        });
                        btnCopy.addKeyListener(new KeyAdapter() {
                            /**
                             * Invoked when a key has been pressed.
                             */
                            public void keyPressed(KeyEvent e) {
                                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                    copyTriggered(tempFrame, tempTable);
                                }
                            }
                        });
                        final JCheckBox chkSelection = new JCheckBox("Column selection mode", false);
                        chkSelection.addItemListener(new ItemListener() {
                            public void itemStateChanged(ItemEvent e) {
                                if (chkSelection.isSelected()) {
                                    tempTable.setColumnSelectionAllowed(true);
                                    tempTable.setRowSelectionAllowed(false);
                                } else {
                                    tempTable.setColumnSelectionAllowed(false);
                                    tempTable.setRowSelectionAllowed(true);
                                }
                            }
                        });
                        JPanel jpanButtons = new JPanel();
                        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
                        jpanButtons.add(Box.createHorizontalStrut(10));
                        jpanButtons.add(chkSelection);
                        jpanButtons.add(Box.createHorizontalGlue());
                        jpanButtons.add(btnCopy);
                        jpanButtons.add(Box.createHorizontalStrut(10));
                        tempFrame.getContentPane().add(new JScrollPane(tempTable), BorderLayout.CENTER);
                        tempFrame.getContentPane().add(jpanButtons, BorderLayout.SOUTH);
                        tempFrame.addWindowListener(new WindowAdapter() {
                            /**
                             * Invoked when a window is in the process of being closed.
                             * The close operation can be overridden at this point.
                             */
                            public void windowClosing(WindowEvent e) {
                                e.getWindow().setVisible(false);
                                e.getWindow().dispose();
                            }
                        });
                        tempFrame.setBounds(200, 200, 500, 300);
                        tempFrame.setVisible(true);
                    }
                }
            }
        });
        this.constructScreen();
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                e.getWindow().setVisible(false);
                e.getWindow().dispose();
            }
        });
    }

    /**
     * This method creates and lays out the GUI.
     */
    private void constructScreen() {
        JPanel jpanTable = new JPanel(new BorderLayout());
        jpanTable.add(new JScrollPane(tblResults), BorderLayout.CENTER);

        JPanel jpanButtons = this.getButtonPanel();

        this.getContentPane().add(jpanTable, BorderLayout.CENTER);
        this.getContentPane().add(jpanButtons, BorderLayout.SOUTH);
    }

    /**
     * This method creates the button panel.
     *
     * @return JPanel with the button panel.
     */
    private JPanel getButtonPanel() {
        // Create the necessary buttons.
        JButton btnSaveCSV = new JButton("Copy table...");
        btnSaveCSV.setMnemonic(KeyEvent.VK_O);
        btnSaveCSV.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyTriggered(DifferentialAnalysisResultsFrame.this, tblResults);
            }
        });
        btnSaveCSV.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    copyTriggered(DifferentialAnalysisResultsFrame.this, tblResults);
                }
            }
        });
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
        jpanButtons.add(Box.createHorizontalStrut(10));

        return jpanButtons;
    }

    /**
     * This method is called when the user presses 'copy'.
     *
     * @param aParent Component with the parent component.
     * @param aTable  JTable to copy the data from.
     */
    private void copyTriggered(Component aParent, JTable aTable) {
        int nbrCols = aTable.getColumnCount();
        int nbrRows = aTable.getRowCount();

        String data = null;

        StringBuffer allRows = new StringBuffer();
        // Get the headers.
        for (int i = 0; i < nbrCols; i++) {
            allRows.append(aTable.getColumnName(i) + "\t");
        }
        allRows.append("\n");
        // Now the data.
        for (int i = 0; i < nbrRows; i++) {
            for (int j = 0; j < nbrCols; j++) {
                String tempData = aTable.getValueAt(i, j).toString();
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
        JOptionPane.showMessageDialog(aParent, message, "Copy complete.", type);
    }

    /**
     * This method reads the data in the specified DiffCouple, as well as the project name from the HashMap, and stores
     * the relevant data in the specified Object[].
     *
     * @param aData     Object[] to store the data in (reference parameter!)
     * @param aProjects HashMap with the projects (used to read the titles from)
     * @param aCouple   DiffCouple to extract the data from.
     */
    private void fillDataArray(Object[] aData, HashMap aProjects, DiffCouple aCouple, double aLocation, double aScale) {
        aData[0] = ((DifferentialProject) aProjects.get(new Long(aCouple.getProjectID()))).getProjectAlias();
        aData[1] = aCouple.getFilename();
        aData[2] = aCouple.getAccession();
        aData[3] = aCouple.getDescription();
        aData[4] = new Double(aCouple.getLightIntensity());
        aData[5] = new Double(aCouple.getHeavyIntensity());
        if (aCouple.getCount() > 0) {
            aData[6] = new Double(aCouple.getLightIntensity() / aCouple.getHeavyIntensity());
        } else {
            aData[6] = new Double(aCouple.getRatio());
        }
        aData[7] = new Double(aCouple.getCorrection());
        if (aCouple.getCount() > 0) {
            aData[8] = new Double(Math.log(aCouple.getLightIntensity() / aCouple.getHeavyIntensity()) / Math.log(2));
        } else {
            aData[8] = new Double(aCouple.getLog2Ratio());
        }
        aData[9] = aCouple.getModifiedSequence();
        aData[10] = new Integer(aCouple.getStart());
        aData[11] = new Integer(aCouple.getEnd());
        aData[12] = aCouple.getEnzymatic();
        int outlier = aCouple.isOutlier(aLocation, aScale);
        aData[13] = new Integer(outlier);

        // See if we need to convert to ErrorObjects.
        if (outlier > 0) {
            // Decide on the colors to use.
            Color bg = Color.blue;
            if (outlier == 98) {
                bg = Color.red;
            } else if (outlier == 95) {
                bg = Color.yellow;

            }
            for (int i = 0; i < aData.length; i++) {
                Object o = aData[i];
                aData[i] = new ErrorObject(o, "Outlier within this cluster at the " + outlier + " confidence interval!", Color.black, bg);
            }
        }
    }
}
