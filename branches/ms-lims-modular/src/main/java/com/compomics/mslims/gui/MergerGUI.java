/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 2-apr-03
 * Time: 15:41:15
 */
package com.compomics.mslims.gui;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Instrument;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.fileio.mergers.MGFMerger;
import com.compomics.mslims.util.fileio.mergers.PKLMergerAndStorer;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.interfaces.Connectable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.13 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class implements the GUI for the settings for the Merger class.
 *
 * @author Lennart Martens
 * @author Thilo Muth
 */
public class MergerGUI extends FlamableJFrame implements Connectable {
    // Class specific log4j logger for MergerGUI instances.
    private static Logger logger = Logger.getLogger(MergerGUI.class);

    /**
     * Boolean that indicates whether the tool is ran in stand-alone mode ('true') or not ('false').
     */
    private static boolean iStandAlone = true;

    /**
     * The DB connection for this class.
     */
    private Connection iConn = null;

    /**
     * The identified for the current DB connection.
     */
    private String iDBName = null;

    /**
     * This variable holds the effective title for this application.
     */
    private String iCurrentTitle = null;

    /**
     * Constant String with the database connection properties file.
     */
    private static final String iConProps = "ms-lims.properties";

    /**
     * The Projects.
     */
    private Project[] iProjects = null;

    /**
     * 'OK' button.
     */
    private JButton btnOK = null;

    /**
     * 'Cancel' button.
     */
    private JButton btnCancel = null;

    /**
     * Destination folder textbox.
     */
    private JTextField txtDestination = null;

    /**
     * Browse button for destination folder.
     */
    private JButton btnBrowseDestination = null;

    /**
     * Number of spectra per mergefile setting.
     */
    private JTextField txtSize = null;

    /**
     * The combobox with the projects (after they've been loaded).
     */
    private JComboBox cmbProjects = null;

    /**
     * Checkbox to switch between numerical and alphabetical sorting of the projects.
     */
    private JCheckBox chkOrdering = null;

    /**
     * Radio Button to indicate that we want to retrieve already searched spectra. <br /> (Only in FROM_DB mode!)
     */
    private JRadioButton rdbSearchedInc = null;

    /**
     * Radio Button to indicate that we do not want to retrieve already searched spectra. <br /> (Only in FROM_DB
     * mode!)
     */
    private JRadioButton rdbSearchedExc = null;

    /**
     * Radio Button to indicate that we do not want to use the 'searched' flag as a query param (thus retrieve all,
     * regardless of searched or not). <br /> 'Only in FROM_DB mode!)
     */
    private JRadioButton rdbSearchedOff = null;

    /**
     * Radio Button to indicate that we want to retrieve already identified spectra. <br /> (Only in FROM_DB mode!)
     */
    private JRadioButton rdbIdentifiedInc = null;

    /**
     * Radio Button to indicate that we do not want to retrieve already identified spectra. <br /> (Only in FROM_DB
     * mode!)
     */
    private JRadioButton rdbIdentifiedExc = null;

    /**
     * Radio Button to indicate that we do not want to use the 'identified' flag as a query param. (thus retrieve all,
     * regardless of identified or not). <br /> (Only in FROM_DB mode!)
     */
    private JRadioButton rdbIdentifiedOff = null;

    /**
     * This combobox allows the user to select only those spectrum files that originate form the specified instrument.
     * (Only in FROM_DB mode!)
     */
    private JComboBox cmbInstruments = null;

    /**
     * This textbox allows the user to select only those spectrum files that match the presented filename. (Only in
     * FROM_DB mode!)
     */
    private JTextField txtFilename = null;

    /**
     * This constructor will initialize the component and construct the GUI.
     */
    public MergerGUI() {
        this("Spectrum Merger GUI", null, null);
    }

    /**
     * This constructor allows the choice of the display as well as the specification of the connection to use (and its
     * name).
     *
     * @param aTitle  String with the title for the JFrame.
     * @param aConn   Connection with the database connection to use. 'null' means no connection specified so create
     *                your own (pops up ConnectionDialog).
     * @param aDBName String with the name for the database connection. Only read if aConn != null.
     */

    public MergerGUI(String aTitle, Connection aConn, String aDBName) {
        super(aTitle);
        this.iCurrentTitle = aTitle;

        // Window closing stuff.
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        // Display the connection dialog.
        if (aConn == null) {
            this.getConnection();
        } else {
            this.passConnection(aConn, aDBName);
        }
        if (iConn == null) {
            close();
        }
        this.constructScreen();
        this.pack();
        loadProjectsTriggered();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) (d.getWidth() / 4), (int) (d.getHeight() / 3));
    }

    /**
     * This method will initialize all components and lay them out.
     */
    private void constructScreen() {

        // Initialize the Folder components.
        txtDestination = new JTextField(20);
        txtDestination.setMaximumSize(new Dimension(txtDestination
                .getMaximumSize().width,
                txtDestination.getPreferredSize().height));
        txtSize = new JTextField(20);
        txtSize.setMaximumSize(new Dimension(txtSize.getMaximumSize().width,
                txtSize.getPreferredSize().height));
        cmbProjects = new JComboBox();
        chkOrdering = new JCheckBox("Sort projects alphabetically");
        chkOrdering.setSelected(false);
        chkOrdering.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean alphabetically = false;
                if (chkOrdering.isSelected()) {
                    alphabetically = true;
                }
                resortProjects(alphabetically);
            }
        });

        // Browse button.
        btnBrowseDestination = new JButton("Browse...");
        btnBrowseDestination.setMnemonic(KeyEvent.VK_R);
        btnBrowseDestination.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String currentDir = txtDestination.getText().trim();
                if (currentDir == null || currentDir.equals("")) {
                    currentDir = "/";
                }
                JFileChooser jfc = new JFileChooser(currentDir);
                jfc.setDialogType(JFileChooser.CUSTOM_DIALOG);
                jfc.setApproveButtonText("Select folder");
                jfc.setApproveButtonMnemonic(KeyEvent.VK_S);
                jfc
                        .setApproveButtonToolTipText("Select the folder you want to put the merge files in.");
                jfc.setDialogTitle("Select destination folder");
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int value = jfc.showDialog(MergerGUI.this, "Select folder");

                if (value == JFileChooser.APPROVE_OPTION) {
                    try {
                        txtDestination.setText(jfc.getSelectedFile()
                                .getCanonicalPath());
                    } catch (IOException ioe) {
                        logger.error(ioe.getMessage(), ioe);
                        JOptionPane.showMessageDialog(
                                MergerGUI.this,
                                new String[]{
                                        "Unable to find the folder you've selected!",
                                        "\n", ioe.getMessage(), "\n"},
                                "Folder was not found!",
                                JOptionPane.ERROR_MESSAGE);
                        txtDestination.setText("");
                    }
                }
            }
        });

        // Laying these out...
        JPanel jpanFolders = new JPanel();
        jpanFolders.setLayout(new BoxLayout(jpanFolders, BoxLayout.X_AXIS));
        jpanFolders.setBorder(BorderFactory.createTitledBorder("Output settings"));

        JPanel jpanFolderComp = new JPanel();
        jpanFolderComp.setLayout(new BoxLayout(jpanFolderComp, BoxLayout.Y_AXIS));
        JPanel jpanFolderLabel = new JPanel();
        jpanFolderLabel.setLayout(new BoxLayout(jpanFolderLabel, BoxLayout.Y_AXIS));

        jpanFolderLabel.add(new JLabel("Select destination folder: "));
        jpanFolderLabel.add(Box.createRigidArea(new Dimension(txtDestination.getWidth(), (int) (btnBrowseDestination.getPreferredSize().getHeight() / 2))));
        jpanFolderLabel.add(new JLabel("Number of spectrum files per mergefile: "));

        JPanel jpanProjectsCombo = new JPanel();
        jpanProjectsCombo.setLayout(new BoxLayout(jpanProjectsCombo, BoxLayout.X_AXIS));
        jpanProjectsCombo.add(cmbProjects);
        jpanProjectsCombo.add(Box.createHorizontalGlue());

        JPanel jpanProjectsOrdering = new JPanel();
        jpanProjectsOrdering.setLayout(new BoxLayout(jpanProjectsOrdering, BoxLayout.X_AXIS));
        jpanProjectsOrdering.add(chkOrdering);
        jpanProjectsOrdering.add(Box.createHorizontalGlue());

        JPanel jpanProjects = new JPanel();
        jpanProjects.setLayout(new BoxLayout(jpanProjects, BoxLayout.Y_AXIS));
        jpanProjects.setBorder(BorderFactory.createTitledBorder("Project selection"));
        jpanProjects.add(jpanProjectsCombo);
        jpanProjects.add(jpanProjectsOrdering);

        JPanel jpanDestination = new JPanel();
        jpanDestination.setLayout(new BoxLayout(jpanDestination, BoxLayout.X_AXIS));
        jpanDestination.add(txtDestination);
        jpanDestination.add(Box.createRigidArea(new Dimension(10, txtDestination.getHeight())));
        jpanDestination.add(btnBrowseDestination);

        JPanel jpanIntComp = new JPanel();
        jpanIntComp.setLayout(new BoxLayout(jpanIntComp, BoxLayout.X_AXIS));
        jpanIntComp.add(txtSize);
        jpanIntComp.add(Box.createRigidArea(new Dimension(5, txtSize.getHeight())));
        jpanIntComp.add(new JLabel("files"));

        jpanFolderComp.add(jpanDestination);
        jpanFolderComp.add(Box.createRigidArea(new Dimension(jpanProjects.getWidth(), 5)));
        jpanFolderComp.add(jpanIntComp);

        jpanFolders.add(jpanFolderLabel);
        jpanFolders.add(Box.createRigidArea(new Dimension(10, jpanFolderLabel.getHeight())));
        jpanFolders.add(jpanFolderComp);

        // Next on the list are the DB components.
        // Search parameters radiobuttons.
        rdbSearchedInc = new JRadioButton("searched", false);
        rdbSearchedExc = new JRadioButton("NOT searched", true);
        rdbSearchedOff = new JRadioButton("ignore 'searched'", false);
        rdbIdentifiedInc = new JRadioButton("identified", false);
        rdbIdentifiedExc = new JRadioButton("NOT identified", true);
        rdbIdentifiedOff = new JRadioButton("ignore 'identified'", false);

        // Instrument combobox.
        cmbInstruments = new JComboBox();
        // Filename textbox.
        txtFilename = new JTextField(20);
        txtFilename.setToolTipText("Include '%' for the like match.");
        txtFilename.setMaximumSize(new Dimension(txtFilename.getPreferredSize().width, txtFilename.getPreferredSize().height));

        // Sizes.
        int prefSize_inc = Math.max(rdbSearchedInc.getPreferredSize().width, rdbIdentifiedInc.getPreferredSize().width);
        int prefSize_exc = Math.max(rdbSearchedExc.getPreferredSize().width, rdbIdentifiedExc.getPreferredSize().width);
        int prefSize_off = Math.max(rdbSearchedOff.getPreferredSize().width, rdbIdentifiedOff.getPreferredSize().width);

        rdbSearchedInc.setPreferredSize(new Dimension(prefSize_inc, rdbSearchedInc.getPreferredSize().height));
        rdbSearchedExc.setPreferredSize(new Dimension(prefSize_exc, rdbSearchedExc.getPreferredSize().height));
        rdbSearchedOff.setPreferredSize(new Dimension(prefSize_off, rdbSearchedOff.getPreferredSize().height));

        rdbIdentifiedInc.setPreferredSize(new Dimension(prefSize_inc, rdbIdentifiedInc.getPreferredSize().height));
        rdbIdentifiedExc.setPreferredSize(new Dimension(prefSize_exc, rdbIdentifiedExc.getPreferredSize().height));
        rdbIdentifiedOff.setPreferredSize(new Dimension(prefSize_off, rdbIdentifiedOff.getPreferredSize().height));

        ButtonGroup bgSearched = new ButtonGroup();
        bgSearched.add(rdbSearchedInc);
        bgSearched.add(rdbSearchedExc);
        bgSearched.add(rdbSearchedOff);

        ButtonGroup bgIdentified = new ButtonGroup();
        bgIdentified.add(rdbIdentifiedInc);
        bgIdentified.add(rdbIdentifiedExc);
        bgIdentified.add(rdbIdentifiedOff);

        JPanel jpanSearched = new JPanel();
        jpanSearched.setLayout(new BoxLayout(jpanSearched, BoxLayout.X_AXIS));
        jpanSearched.add(Box.createHorizontalStrut(25));
        jpanSearched.add(rdbSearchedInc);
        jpanSearched.add(Box.createHorizontalStrut(5));
        jpanSearched.add(rdbSearchedExc);
        jpanSearched.add(Box.createHorizontalStrut(5));
        jpanSearched.add(rdbSearchedOff);
        jpanSearched.add(Box.createHorizontalGlue());

        JPanel jpanIdentified = new JPanel();
        jpanIdentified.setLayout(new BoxLayout(jpanIdentified, BoxLayout.X_AXIS));
        jpanIdentified.add(Box.createHorizontalStrut(25));
        jpanIdentified.add(rdbIdentifiedInc);
        jpanIdentified.add(Box.createHorizontalStrut(5));
        jpanIdentified.add(rdbIdentifiedExc);
        jpanIdentified.add(Box.createHorizontalStrut(5));
        jpanIdentified.add(rdbIdentifiedOff);
        jpanIdentified.add(Box.createHorizontalGlue());

        JLabel lblInstrument = new JLabel("Select instrument: ");
        JLabel lblFilename = new JLabel("Optional filename-filter: ");

        int delta = lblFilename.getPreferredSize().width - lblInstrument.getPreferredSize().width;

        JPanel jpanInstrument = new JPanel();
        jpanInstrument.setLayout(new BoxLayout(jpanInstrument, BoxLayout.X_AXIS));
        jpanInstrument.add(Box.createHorizontalStrut(33));
        jpanInstrument.add(lblInstrument);
        jpanInstrument.add(Box.createHorizontalStrut(15 + delta));
        jpanInstrument.add(cmbInstruments);
        jpanInstrument.add(Box.createHorizontalGlue());

        JPanel jpanFilename = new JPanel();
        jpanFilename.setLayout(new BoxLayout(jpanFilename, BoxLayout.X_AXIS));
        jpanFilename.add(Box.createHorizontalStrut(33));
        jpanFilename.add(lblFilename);
        jpanFilename.add(Box.createHorizontalStrut(15));
        jpanFilename.add(txtFilename);
        jpanFilename.add(Box.createHorizontalGlue());

        JPanel jpanSearchOptions = new JPanel();
        jpanSearchOptions.setLayout(new BoxLayout(jpanSearchOptions, BoxLayout.Y_AXIS));
        jpanSearchOptions.setBorder(BorderFactory.createTitledBorder("Spectrum selection options"));
        jpanSearchOptions.add(jpanSearched);
        jpanSearchOptions.add(Box.createVerticalStrut(5));
        jpanSearchOptions.add(jpanIdentified);
        jpanSearchOptions.add(Box.createVerticalStrut(5));
        jpanSearchOptions.add(jpanInstrument);
        jpanSearchOptions.add(Box.createVerticalStrut(5));
        jpanSearchOptions.add(jpanFilename);
        jpanSearchOptions.add(Box.createVerticalStrut(5));

        // The buttonpanel.
        JPanel jpanButtons = this.getButtonPanel();

        // The main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanProjects);
        jpanMain.add(Box.createRigidArea(new Dimension(jpanFolders.getWidth(), 5)));
        jpanMain.add(jpanSearchOptions);
        jpanMain.add(Box.createRigidArea(new Dimension(jpanFolders.getWidth(), 5)));
        jpanMain.add(jpanFolders);
        jpanMain.add(Box.createRigidArea(new Dimension(jpanFolders.getWidth(), 10)));
        jpanMain.add(jpanButtons);

        // By default, disable all but the DB settings when in from DB mode.
        cmbProjects.setMaximumSize(new Dimension(cmbProjects.getMaximumSize().width, cmbProjects.getPreferredSize().height));
        cmbInstruments.setMaximumSize(new Dimension(cmbInstruments.getMaximumSize().width, cmbInstruments.getPreferredSize().height));

        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method will construct the buttonpanel.
     *
     * @return JPanel  with the buttons.
     */
    private JPanel getButtonPanel() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

        btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okPressedDB();
            }
        });
        btnOK.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    okPressedDB();
                }
            }
        });

        btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelPressed();
            }
        });
        btnCancel.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cancelPressed();
                }
            }
        });

        buttons.add(Box.createRigidArea(new Dimension(10, btnOK.getHeight())));
        buttons.add(btnOK);
        buttons.add(Box.createRigidArea(new Dimension(10, btnOK.getHeight())));
        buttons.add(btnCancel);

        return buttons;
    }

    /**
     * This method is called when the user clicks the OK button in FROM_DB mode.
     */
    private void okPressedDB() {
        String destination = txtDestination.getText().trim();
        if (destination == null || destination.equals("")) {
            this.fillOutComponentWarning("destination folder");
            txtDestination.requestFocus();
            return;
        }
        String lSize = txtSize.getText().trim();
        boolean maxSize = false;
        if (lSize == null || lSize.equals("")) {
            // This check is changed in favor of substituting 'no number' with '1000000'.
            /*
                this.fillOutComponentWarning("Spectrum files per merge file");
                txtSize.requestFocus();
                return;
                */
            maxSize = true;
        }
        int size = -1;
        if (!maxSize) {
            try {

                size = Integer.parseInt(lSize);
                if (size <= 0) {
                    throw new Exception("Zero or less.");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(this,
                        "The number of spectrum files per merge file you specified ("
                                + lSize + ") is not a positive whole number!",
                        "Checking interval is not a whole number!",
                        JOptionPane.ERROR_MESSAGE);
                txtSize.requestFocus();
                return;
            }
        } else {
            // Set the number of spectra per mergefile to 1.000.000. That seems reasonably large.
            size = 1000000;
        }

        File fDestination = new File(destination);
        if (!fDestination.exists()) {

            String lMessage = "The destination folder you specified (" + destination
                    + ") does not exist!";
            logger.error(lMessage);
            JOptionPane.showMessageDialog(this,
                    lMessage, "Folder does not exist!",
                    JOptionPane.ERROR_MESSAGE);
            txtDestination.requestFocus();
            return;
        } else if (!fDestination.isDirectory()) {
            JOptionPane.showMessageDialog(this,
                    "The destination folder you specified (" + destination
                            + ") is not a directory!",
                    "Folder is not a directory!", JOptionPane.ERROR_MESSAGE);
            txtDestination.requestFocus();
            return;
        }

        // Go to a wait cursor at this point.
        Cursor curs = this.getCursor();
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // All components survived their validations.
        // Now to run the merger.
        int allFiles = 0;
        int mergeFiles = 0;
        int result = 0;
        try {
            String whereClause = "where l_projectid="
                    + ((Project) cmbProjects.getSelectedItem()).getProjectid();
            // Now to generate a 'where' clause...
            if (!rdbSearchedOff.isSelected()) {
                whereClause += " and searched";
                if (rdbSearchedInc.isSelected()) {
                    whereClause += ">0";
                } else if (rdbSearchedExc.isSelected()) {
                    whereClause += "=0";
                } else {
                    String lMessage = "No 'Searched' radio button selected!";
                    logger.error(lMessage);
                    JOptionPane.showMessageDialog(this,
                            lMessage,
                            "No 'searched' radio button selected!",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (!rdbIdentifiedOff.isSelected()) {
                whereClause += " and identified";
                if (rdbIdentifiedInc.isSelected()) {
                    whereClause += ">0";
                } else if (rdbIdentifiedExc.isSelected()) {
                    whereClause += "=0";
                } else {
                    String lMessage = "No 'Identified' radio button selected!";
                    logger.error(lMessage);
                    JOptionPane.showMessageDialog(this,
                            lMessage,
                            "No 'identified' radio button selected!",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (cmbInstruments.getSelectedItem() instanceof Instrument) {
                // Okay, selection should include an instrument.
                whereClause += " and l_instrumentid = "
                        + ((Instrument) cmbInstruments.getSelectedItem())
                        .getInstrumentid();
            }
            String filename = txtFilename.getText().trim();
            if (!filename.equals("")) {
                whereClause += " and filename ";
                // See if there is an '%' present; if so, do a like.
                // If not, do an exact match.
                if (filename.indexOf("%") >= 0) {
                    // Check for inversion.
                    if (filename.startsWith("!")) {
                        whereClause += "not ";
                        // Don't forget to remove the '!'...
                        filename = filename.substring(1);
                    }
                    whereClause += "like '";
                } else {
                    whereClause += "= '";
                }
                whereClause += filename + "'";
            }
            logger.info("Where clause: " + whereClause);
            Statement stat = iConn.createStatement();
            ResultSet rs = stat
                    .executeQuery("select count(*) from spectrum "
                            + whereClause);
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            stat.close();
            if (count == 0) {
                result = JOptionPane.showConfirmDialog(this, new String[]{
                        "No files selected by this query!",
                        "Do you want to merge another set of spectra?",
                        "\n"}, "Finished processing files.",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                DefaultProgressBar dpb = new DefaultProgressBar(this,
                        "Merging " + count + " mergefiles to '" + destination
                                + "'...", 0, count + 1,
                        "Starting up merging to '" + destination + "'...");
                dpb.setLocation(this.getLocation().x + (this.getWidth() / 3),
                        this.getLocation().y + (this.getHeight() / 3));
                HashMap stats = new HashMap();
                MGFMerger merger = new MGFMerger(this, dpb, iConn,
                        fDestination, size, whereClause, stats);
                merger.start();
                dpb.setVisible(true);
                allFiles = ((Integer) stats
                        .get(MGFMerger.TOTAL_NUMBER_OF_FILES)).intValue();
                mergeFiles = ((Integer) stats
                        .get(MGFMerger.TOTAL_NUMBER_OF_MERGEFILES)).intValue();
                result = JOptionPane.showConfirmDialog(this, new String[]{
                        "Merged " + allFiles + " files into " + mergeFiles
                                + " file(s).",
                        "Do you want to merge another set of spectrum files?",
                        "\n"}, "Finished processing files.",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,
                    "Unable to merge spectrum files: " + e.getMessage() + "!",
                    "Unable to perform merging!", JOptionPane.ERROR_MESSAGE);
            logger.error(e.getMessage(), e);
        }
        this.setCursor(curs);
        if (result == JOptionPane.NO_OPTION) {
            this.close();
        }
    }

    /**
     * This method is called when the 'Cancel' button is pressed.
     */
    private void cancelPressed() {
        this.close();
    }

    /**
     * This method is called when the user presses the load projects button.
     */
    private void loadProjectsTriggered() {
        // Do data validations.
        boolean error = true;

        // Connect to the DB to load projects and instruments.
        String troubleString = "project";
        try {
            // Select all projects.
            iProjects = Project.getAllProjects(iConn);
            // Initialize the project combobox.
            cmbProjects.setModel(new DefaultComboBoxModel(iProjects));

            // Now we enter the instrument-retrieval phase.
            troubleString = "instrument";
            Instrument[] temp = Instrument.getAllInstruments(iConn);
            // We need to have a 'All instruments' setting in the front of the list
            // so prefix it here.
            // I use a String object, instead of an Instrument, for ease of creation and
            // because I can now check for instrument filtering at submission time based on
            // 'instanceof Instrument'.
            Object[] instruments = new Object[temp.length + 1];
            instruments[0] = "All instruments";
            for (int i = 0; i < temp.length; i++) {
                instruments[i + 1] = temp[i];
            }
            cmbInstruments.setModel(new DefaultComboBoxModel(instruments));

            // Close connection and set 'all clear' flag.
            // iConn.close();
            error = false;
            this.pack();
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve " + troubleString + " data!");
        }
    }

    /**
     * This method displays an error message about the necessity of filling out the specified component first.
     *
     * @param aComponent String with the description of the data for the component that needs to be filled out.
     */
    private void fillOutComponentWarning(String aComponent) {
        String lMessage = "You need to fill out the "
                + aComponent + " first!";
        logger.error(lMessage);
        JOptionPane.showMessageDialog(this, lMessage, aComponent + " not filled out!",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method re-sorts the projects in the combobox. If the boolean is 'true', sorting is alphabetically on the
     * project title, otherwise( boolean 'false') it is by project number (project id).
     *
     * @param aAlphabetically boolean to indicate whether sorting should be performed alphabetically ('true') or by
     *                        project number ('false').
     */
    private void resortProjects(boolean aAlphabetically) {
        Comparator comp = null;
        if (aAlphabetically) {
            // Alphabetic ordering of the project title.
            comp = new Comparator() {
                public int compare(Object o, Object o1) {
                    Project p1 = (Project) o;
                    Project p2 = (Project) o1;
                    return p1.getTitle().compareToIgnoreCase(p2.getTitle());
                }
            };
        } else {
            // Ordering on the projectid.
            comp = new Comparator() {
                public int compare(Object o, Object o1) {
                    Project p1 = (Project) o;
                    Project p2 = (Project) o1;
                    return (int) (p2.getProjectid() - p1.getProjectid());
                }
            };
        }
        Arrays.sort(iProjects, comp);
        cmbProjects.setModel(new DefaultComboBoxModel(iProjects));
    }

    /**
     * This method calls upon a GUI component to handle the connection.
     */
    private void getConnection() {
        ConnectionDialog cd = new ConnectionDialog(this, this,
                "Database connection for MergerGUI", iConProps);
        cd.setVisible(true);
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
        }
        this.iConn = aConn;
        this.iDBName = aDBName;
        this.setTitle(iCurrentTitle + " (connected to: " + iDBName + ")");
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

    /**
     * This method is called when the application is closed.
     */
    private void close() {
        //closeConnection();
        this.setVisible(false);
        this.dispose();
        if (iStandAlone) {
            if (iConn != null) {
                try {
                    iConn.close();
                    logger.info("\nDB connection closed.\n");
                } catch (SQLException sqle) {
                    logger.error("\nUnable to close DB connection!\n");
                }
            }
            System.exit(0);
        }
    }

    /**
     * Main method is the entry point for the application. Start-up parameters are not used.
     *
     * @param args String[] with the start-up paremeters.
     */
    public static void main(String[] args) {
        MergerGUI mg = new MergerGUI();
        mg.setVisible(true);
    }
}
