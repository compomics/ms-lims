/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2004
 * Time: 17:58:34
 */
package com.compomics.mslims.gui;

import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.frames.PreviewSearchResultsFrame;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.gui.table.MascotSearchTableModel;
import com.compomics.mslims.gui.table.renderers.ErrorCellRenderer;
import com.compomics.mslims.gui.table.renderers.ErrorObject;
import com.compomics.mslims.gui.tree.MascotSearch;
import com.compomics.mslims.gui.tree.MascotTask;
import com.compomics.mslims.gui.tree.MascotTasksTreeModel;
import com.compomics.mslims.gui.tree.renderers.MascotTasksTreeCellRenderer;
import com.compomics.mslims.util.workers.ReadMascotTaskDBWorker;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.TableSorter;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.14 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class will present the user with a graphical user interface to read search results from the Mascot Daemon Task
 * Database and preview the processed results in the s_lims DB.
 *
 * @author Lennart Martens
 * @version $Id: IdentificationGUI.java,v 1.14 2009/07/28 14:48:33 lennart Exp $
 */
public class IdentificationGUI extends JFrame implements Connectable, Flamable {

    private JTree trSearches = null;
    private JTable tblSummary = null;
    private JTextField txtThreshold = null;
    /**
     * This checkbox identifies whether the data is processed by Mascot Distiller. As then a specific
     * SpectrumStorageEngine is used.
     */
    private JCheckBox chkMascotDistiller = null;

    /**
     * Boolean that indicates whether the tool is ran in stand-alone mode ('true') or not ('false').
     */
    private static boolean iStandAlone = true;

    /**
     * This boolean indicates whether Mascot Distiller was used for generating the spectrum files.
     */
    private boolean iMascotDistillerProcessing;

    /**
     * The database connection to the ms_lims database
     */
    private Connection iConnection = null;

    /**
     * The name of the database we're connected to.
     */
    private String iDBName = null;

    /**
     * This Vector holds all the MascotTasks.
     */
    private Vector iTasks = null;

    /**
     * This constructor takes a single argument with the title for the frame.
     *
     * @param aName String with the title for the frame.
     */
    public IdentificationGUI(String aName) {
        this(aName, null, null);
    }

    /**
     * This constructor takes the title for the frame, the DB connection to use, and a name for this connection.
     *
     * @param aName   String with the title for the frame.
     * @param aConn   Connection with the database connection to use. 'null' means no connection specified so create
     *                your own (pops up ConnectionDialog).
     * @param aDBName String with the name for the database connection. Only read if aConn != null.
     */
    public IdentificationGUI(String aName, Connection aConn, String aDBName) {
        super(aName);
        if (aConn == null) {
            ConnectionDialog cd =
                    new ConnectionDialog(this, this, "Connection for IdentificationGUI", "IdentificationGUI.properties");
            cd.setVisible(true);
        } else {
            passConnection(aConn, aDBName);
        }

        this.readTaskDB();
        super.setTitle("IdentificationGUI (storing results in " + iDBName + ")");
        this.constructScreen();
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        this.pack();
        this.setLocation(100, 100);
    }

    /**
     * This method will be called by the class actually making the connection. It will pass the connection and an
     * identifier String for that connection (typically the name of the database connected to).
     *
     * @param aConn   Connection with the DB connection.
     * @param aDBName String with an identifier for the connection, typically the name of the DB connected to.
     */
    public void passConnection(Connection aConn, String aDBName) {
        if (aConn == null) {
            close();
        } else {
            this.iConnection = aConn;
            this.iDBName = aDBName;
        }
    }

    /**
     * The main method is the entry point for the application.
     *
     * @param args String[[]   with the start-up arguments.
     */
    public static void main(String[] args) {
        try {
            IdentificationGUI frame = new IdentificationGUI("IdentificationGUI");
            frame.setVisible(true);
        } catch (Throwable t) {
            JFrame frame = new JFrame("You won't see me.");
            JOptionPane.showMessageDialog(frame, new String[]{"An error occurred: ", t.getMessage()}, "Application encountered a fatal error!", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

    }

    /**
     * This method initializes all components on the GUI and lays them out.
     */
    private void constructScreen() {
        TreeModel model = new MascotTasksTreeModel(iTasks);
        trSearches = new JTree(model);
        trSearches.setCellRenderer(new MascotTasksTreeCellRenderer());
        trSearches.setMaximumSize(new Dimension(trSearches.getPreferredSize().width, trSearches.getMaximumSize().height));
        trSearches.setMinimumSize(trSearches.getPreferredSize());
        trSearches.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath[] paths = trSearches.getSelectionPaths();
                if (paths != null) {
                    MascotSearch[] info = null;
                    Vector infoVec = new Vector(25, 10);
                    Vector duplicates = new Vector(25, 10);
                    for (int i = 0; i < paths.length; i++) {
                        TreePath lPath = paths[i];
                        Object lTemp = lPath.getLastPathComponent();
                        if (lTemp instanceof MascotSearch) {
                            MascotSearch ms = (MascotSearch) lTemp;
                            if (!duplicates.contains(ms)) {
                                infoVec.add(ms);
                                duplicates.add(ms);
                            }
                        } else if (lTemp instanceof MascotTask) {
                            MascotTask mt = (MascotTask) lTemp;
                            Vector searches = mt.getSearches();
                            int liSize = searches.size();
                            for (int j = 0; j < liSize; j++) {
                                MascotSearch ms = (MascotSearch) searches.get(j);
                                if (!duplicates.contains(ms)) {
                                    infoVec.add(ms);
                                    duplicates.add(ms);
                                }
                            }
                        }
                    }
                    // Convert the Vector back into a 2D array of Strings.
                    info = new MascotSearch[infoVec.size()];
                    infoVec.toArray(info);
                    tblSummary.setModel(new MascotSearchTableModel(info));
                    tblSummary.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                }
            }
        });

        // The list panel.
        JPanel jpanList = new JPanel(new BorderLayout());
        jpanList.setBorder(BorderFactory.createTitledBorder("Mascot Daemon Task DB"));
        JScrollPane scrList = new JScrollPane(trSearches);
        scrList.setMinimumSize(scrList.getPreferredSize());
        jpanList.add(scrList, BorderLayout.CENTER);
        jpanList.setMinimumSize(new Dimension(jpanList.getPreferredSize().width + 10, jpanList.getPreferredSize().height));

        // The summary.
        TableModel tablemodel = new MascotSearchTableModel();

        tblSummary = new JTable(tablemodel);
        tblSummary.setDefaultRenderer(ErrorObject.class, new ErrorCellRenderer());
        JPanel jpanSummary = new JPanel(new BorderLayout());
        jpanSummary.setBorder(BorderFactory.createTitledBorder("Selected tasks & searches"));
        jpanSummary.add(new JScrollPane(tblSummary), BorderLayout.CENTER);

        TableSorter sorter = new TableSorter(tablemodel);
        sorter.addMouseListenerToHeaderInTable(tblSummary);

        // Total upper panel.
        JSplitPane spltUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jpanList, jpanSummary);
        JPanel jpanUpper = new JPanel();
        jpanUpper.setLayout(new BorderLayout());
        jpanUpper.add(spltUpper, BorderLayout.CENTER);

        // The settings.
        JPanel jpanSettings = new JPanel();
        jpanSettings.setLayout(new BoxLayout(jpanSettings, BoxLayout.X_AXIS));
        jpanSettings.setBorder(BorderFactory.createTitledBorder("Settings"));

        // Threshold parameter.
        txtThreshold = new JTextField(15);
        txtThreshold.setMaximumSize(txtThreshold.getPreferredSize());
        txtThreshold.setText("0.05");

        JPanel jpanThreshold = new JPanel();
        jpanThreshold.setLayout(new BoxLayout(jpanThreshold, BoxLayout.X_AXIS));
        jpanThreshold.add(new JLabel("Identity threshold (0-1; default is 0.05 for 95% confidence): "));
        jpanThreshold.add(Box.createHorizontalStrut(5));
        jpanThreshold.add(txtThreshold);
        jpanThreshold.add(Box.createHorizontalGlue());

        // Mascot Distiller parameter.
        // Checkbox indicating that the spectrum files where generated by Mascot Distiller.
        chkMascotDistiller = new JCheckBox("");
        chkMascotDistiller.setSelected(false);
        chkMascotDistiller.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                iMascotDistillerProcessing = chkMascotDistiller.isSelected();
            }
        });
        JLabel lblMascotDistiller = new JLabel("Mascot Distiller workflow:");

        JPanel jpanMascotDistiller = new JPanel();
        jpanMascotDistiller.setLayout(new BoxLayout(jpanMascotDistiller, BoxLayout.X_AXIS));
        jpanMascotDistiller.add(lblMascotDistiller);
        jpanThreshold.add(Box.createHorizontalStrut(5));
        jpanMascotDistiller.add(chkMascotDistiller);

        // Add to the settings panel.
        jpanSettings.add(jpanThreshold);
        jpanSettings.add(Box.createHorizontalGlue());
        jpanSettings.add(jpanMascotDistiller);

        // The main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanUpper);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanSettings);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(this.getButtonPanel());
        jpanMain.add(Box.createVerticalStrut(5));

        // Add everything to the main gui.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method creates the panel with the buttons.
     *
     * @return JPanel with the buttons.
     */
    private JPanel getButtonPanel() {
        JButton btnExit = new JButton("Exit");
        btnExit.setMnemonic(KeyEvent.VK_E);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitPressed();
            }
        });
        btnExit.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    exitPressed();
                }
            }
        });

        JButton btnClear = new JButton("Clear");
        btnClear.setMnemonic(KeyEvent.VK_C);
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearPressed();
            }
        });
        btnClear.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    clearPressed();
                }
            }
        });

        JButton btnPreview = new JButton("Preview");
        btnPreview.setMnemonic(KeyEvent.VK_P);
        btnPreview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                previewPressed();
            }
        });
        btnPreview.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    previewPressed();
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));

        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnExit);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnClear);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnPreview);
        jpanButtons.add(Box.createHorizontalStrut(10));

        return jpanButtons;
    }

    /**
     * This method is called whenever the user presses 'preview'.
     */
    private void previewPressed() {
        int selectionCount = tblSummary.getSelectedRowCount();
        if (selectionCount == 0) {
            JOptionPane.showMessageDialog(this, "You need to select one or more rows in the table to process.", "No rows selected!", JOptionPane.ERROR_MESSAGE);
            tblSummary.requestFocus();
            return;
        }
        // Okay, something was selected.
        // First get the identification threshold!
        String threshold = txtThreshold.getText();
        if (threshold == null || threshold.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "You need to specify an identity threshold!", "No threshold specified!", JOptionPane.ERROR_MESSAGE);
            txtThreshold.requestFocus();
            return;
        }
        double thresh = 0.0;
        try {
            thresh = Double.parseDouble(threshold);
            if (thresh <= 0 || thresh >= 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "You need to specify a positive number between 0 and 1 for the threshold!", "Incorrect threshold specified!", JOptionPane.ERROR_MESSAGE);
            txtThreshold.requestFocus();
            return;
        }
        // Get the instances!
        int[] selections = tblSummary.getSelectedRows();
        ArrayList errorFree = new ArrayList(selectionCount);
        for (int i = 0; i < selections.length; i++) {
            MascotSearch temp = (MascotSearch) tblSummary.getValueAt(selections[i], -1);
            if (!temp.isError()) {
                errorFree.add(temp);
            }
        }
        MascotSearch[] todo = new MascotSearch[errorFree.size()];
        errorFree.toArray(todo);
        // Now display the selection for the user to verify.
        PreviewSearchResultsFrame psrd =
                new PreviewSearchResultsFrame(this, todo, iConnection, thresh, iMascotDistillerProcessing);
        psrd.setVisible(true);
    }

    /**
     * This method is called whenever the user presses 'clear'.
     */
    private void clearPressed() {
        tblSummary.setModel(new DefaultTableModel(null, new String[]{}));
        trSearches.setSelectionPaths(null);
    }

    /**
     * This method is called whenever the user clicks 'exit'.
     */
    private void exitPressed() {
        this.close();
    }


    /**
     * This method closes down the application, taking care of the database connection in the process.
     */
    private void close() {
        try {
            if (iConnection != null && iStandAlone) {
                iConnection.close();
            }
        } catch (SQLException sqle) {
            // Do nothing.
        }
        this.setVisible(false);
        this.dispose();
        if (iStandAlone) {
            System.exit(0);
        }
    }

    /**
     * This method attempts to connect to the Mascot Task DB via ODBC and read the relevant information.
     */
    private void readTaskDB() {
        try {
            Properties props = new Properties();
            try {
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("IdentificationGUI.properties");
                if (is == null) {
                    throw new IOException();
                }
                props.load(is);
                String driver = props.getProperty("TASKDRIVER");
                String url = props.getProperty("TASKURL");
                if (driver == null) {
                    throw new SQLException("Key 'TASKDRIVER' in file 'IdentificationGUI.properties' was not defined or NULL!");
                }
                if (url == null) {
                    throw new SQLException("Key 'TASKURL' in file 'IdentificationGUI.properties' was not defined or NULL!");
                }
                Driver d = null;
                try {
                    d = (Driver) Class.forName(driver).newInstance();
                } catch (Exception e) {
                    throw new SQLException("Unable to load Mascot Dameon TaskDB database driver: " + e.getMessage());
                }
                Connection conn = null;
                try {
                    conn = d.connect(url, new Properties());
                } catch (SQLException sqle) {
                    throw new SQLException("Unable to connect to the Mascot Dameon TaskDB database: " + sqle.getMessage());
                }
                try {
                    // Okay, connected.
                    // Get the list of tasks.
                    Statement stat = conn.createStatement();
                    ResultSet rs = stat.executeQuery("select count(*) from Mascot_Daemon_Tasks");
                    rs.next();
                    int count = rs.getInt(1);
                    rs.close();
                    stat.close();
                    DefaultProgressBar dpb =
                            new DefaultProgressBar(this, "Reading Mascot Daemon Task DB...", 0, count + 2);
                    dpb.setResizable(false);
                    dpb.setSize(350, 100);
                    dpb.setMessage("Connecting...");
                    iTasks = new Vector(count, 5);
                    ReadMascotTaskDBWorker worker = new ReadMascotTaskDBWorker(conn, iTasks, this, dpb);
                    worker.start();
                    dpb.setVisible(true);
                    conn.close();
                    // Better exception handling for the ODBC/JDBC bridge driver!!
                } catch (SQLException sqle) {
                    throw new SQLException("Unable to read data from the Mascot Daemon TaskDB '" + sqle.getMessage() + "'!");
                }
            } catch (IOException ioe) {
                throw new SQLException("Could not find configurationfile 'IdentificationGUI.properties' in the classpath!");
            }
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, new String[]{"There were fatal errors trying to access the Mascot Daemon Task DB:", sqle.getMessage()}, "Unable to retrieve data from TaskDB!", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
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
        JOptionPane.showMessageDialog(this, new String[]{"An error occurred while attempting to read the data:", aMessage}, "Error occurred!", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method should be called when the application is not launched in stand-alone mode.
     */
    public static void setNotStandAlone() {
        iStandAlone = false;
    }

    public boolean isStandAlone() {
        return iStandAlone;
    }
}