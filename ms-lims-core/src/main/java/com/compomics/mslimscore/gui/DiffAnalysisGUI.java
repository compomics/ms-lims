/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 8-okt-2004
 * Time: 7:27:57
 */
package com.compomics.mslimscore.gui;

import com.compomics.mslimscore.gui.dialogs.ConnectionDialog;
import org.apache.log4j.Logger;

import com.compomics.mslimsdb.accessors.Instrument;
import com.compomics.mslimsdb.accessors.Project;
import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.mslimscore.gui.dialogs.DifferentialProjectDialog;
import com.compomics.mslimscore.gui.frames.DifferentialAnalysisResultsFrame;
import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mslimscore.gui.table.DifferentialProjectTableModel;
import com.compomics.mslimscore.util.diff.DiffAnalysisCore;
import com.compomics.mslimscore.util.diff.DifferentialProject;
import com.compomics.mslimscore.util.workers.DiffAnalysisWorker;
import com.compomics.util.general.CommandLineParser;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.io.PropertiesManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.11 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class presents a graphical user interface to perform a differential analyse within one project, or accross
 * different projects.
 *
 * @author Lennart Martens
 * @version $Id: DiffAnalysisGUI.java,v 1.11 2009/07/28 14:48:33 lennart Exp $
 */
public class DiffAnalysisGUI extends JFrame implements Connectable, Flamable {
    // Class specific log4j logger for DiffAnalysisGUI instances.
    private static Logger logger = Logger.getLogger(DiffAnalysisGUI.class);

    private JComboBox cmbCalibratedStDev = null;
    private JTextField txtLightLabel = null;
    private JTextField txtHeavyLabel = null;

    private JList lstProjects = null;
    private JTable tblSelectedProjects = null;

    private JRadioButton rbtRobustStats = null;
    private JRadioButton rbtStandardStats = null;

    private JRadioButton rbtSumIntensities = null;
    private JRadioButton rbtAverageRatio = null;
    private JTextField txtWhereAddition = null;

    private JCheckBox chkRecenterProjects = null;
    private JTextField txtRecenterProjects = null;

    /**
     * The database connection to the ms_lims database
     */
    private Connection iConnection = null;

    /**
     * The name of the database we're connected to.
     */
    private String iDBName = null;

    /**
     * All the projects that were retrieved from the DB.
     */
    private Project[] iProjects = null;

    /**
     * All the differentially calibrated instruments from the DB.
     */
    private Instrument[] iInstruments = null;

    /**
     * The TableModel that contains all the selected projects.
     */
    private DifferentialProjectTableModel iModel = null;

    /**
     * When this boolean is 'true', expert mode is enabled and more choices of configuration are available to the user.
     */
    private boolean iExpert = false;

    /**
     * This constructor takes a boolean indicating the mode as parameter. The expert mode (enabled when the aExpert
     * boolean is 'true') allows two additional configuration parameters: the type of averaging used and an optional
     * 'wehere' clause to select specific identifications only.
     *
     * @param aExpert boolean that is 'true' for expert mode (more options), 'false' for default mode.
     */
    public DiffAnalysisGUI(boolean aExpert) {
        this.iExpert = aExpert;
        Properties lConnectionProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "ms-lims.properties");

        ConnectionDialog cd = new ConnectionDialog(this, this, "Connection for DiffAnalysisGUI" + (iExpert ? " (expert mode)" : ""), lConnectionProperties);
        cd.setVisible(true);
        // Frame to show the user that something is indeed happening.
        JFrame tempFrame = new JFrame("Loading data...");
        tempFrame.getContentPane().add(new JLabel("Loading project data...", JLabel.CENTER));
        tempFrame.setBounds(200, 200, 200, 150);
        tempFrame.setVisible(true);
        tempFrame.setEnabled(false);
        this.readProjects();
        tempFrame.getContentPane().add(new JLabel("Loading instrument data...", JLabel.CENTER));
        tempFrame.setVisible(false);
        tempFrame.dispose();
        this.readInstruments();
        super.setTitle("Differential Analysis " + (iExpert ? "- expert mode " : "") + " (reading projects from " + iDBName + ")");
        iModel = new DifferentialProjectTableModel();
        this.constructScreen();
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close(0);
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
            System.exit(0);
        } else {
            this.iConnection = aConn;
            this.iDBName = aDBName;
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
        logger.error(aThrowable.getMessage(), aThrowable);
        this.close(1);
    }

    /**
     * This method is called when a project should be added to the selected projects table.
     *
     * @param aProject DifferentialProject to add to the table.
     */
    public void addProjectToTable(DifferentialProject aProject) {
        iModel.addProject(aProject);
    }

    /**
     * The main method is the entry point for the application.
     *
     * @param args String[[]   with the start-up arguments.
     */
    public static void main(String[] args) {
        try {
            CommandLineParser clp = new CommandLineParser(args);
            String[] flags = clp.getFlags();
            boolean expert = false;
            if (flags != null && flags.length > 0) {
                for (int i = 0; i < flags.length; i++) {
                    String lFlag = flags[i];
                    if ("e".equals(lFlag)) {
                        expert = true;
                    }
                }
            }
            DiffAnalysisGUI frame = new DiffAnalysisGUI(expert);
            frame.setVisible(true);
        } catch (Throwable t) {
            JFrame frame = new JFrame("You won't see me.");
            JOptionPane.showMessageDialog(frame, new String[]{"A start-up error occurred: ", t.getMessage()}, "Application encountered a fatal error!", JOptionPane.ERROR_MESSAGE);
            logger.error(t.getMessage(), t);
            frame.dispose();
            System.exit(1);
        }
    }

    /**
     * This method initializes and lays out screen components.
     */
    private void constructScreen() {

        // Initialize components.
        JLabel lblCalibratedStDev = new JLabel("Select the instrument on which the analysis was performed");
        JLabel lblLightLabel = new JLabel("Enter description for light label here");
        JLabel lblHeavyLabel = new JLabel("Enter description for heavy label here");
        JLabel lblSemiColon1 = new JLabel(": ");
        JLabel lblSemiColon2 = new JLabel(": ");
        JLabel lblSemiColon3 = new JLabel(": ");

        cmbCalibratedStDev = new JComboBox(iInstruments);
        cmbCalibratedStDev.setMaximumSize(cmbCalibratedStDev.getPreferredSize());

        txtLightLabel = new JTextField(30);
        txtLightLabel.setMaximumSize(txtLightLabel.getPreferredSize());

        txtHeavyLabel = new JTextField(30);
        txtHeavyLabel.setMaximumSize(txtHeavyLabel.getPreferredSize());

        cmbCalibratedStDev.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtLightLabel.requestFocus();
                }
            }
        });
        txtLightLabel.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtHeavyLabel.requestFocus();
                }
            }
        });
        txtHeavyLabel.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    lstProjects.requestFocus();
                }
            }
        });


        lstProjects = new JList(iProjects);
        lstProjects.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstProjects.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    projectListClicked();
                }
            }
        });
        lstProjects.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    projectListClicked();
                }
            }
        });
        JButton btnList = new JButton("Add project...");
        btnList.setMnemonic(KeyEvent.VK_A);
        btnList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                projectListClicked();
            }
        });
        btnList.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    projectListClicked();
                }
            }
        });
        tblSelectedProjects = new JTable(iModel);
        tblSelectedProjects.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteRowRequested();
                }
            }
        });
        JButton btnTable = new JButton("Unselect project");
        btnTable.setMnemonic(KeyEvent.VK_U);
        btnTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRowRequested();
            }
        });
        btnTable.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    deleteRowRequested();
                }
            }
        });

        rbtRobustStats = new JRadioButton("Robust statistics");
        rbtStandardStats = new JRadioButton("Standard statistics");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbtRobustStats);
        bg.add(rbtStandardStats);
        rbtRobustStats.setSelected(true);

        rbtSumIntensities = new JRadioButton("Weighted ratios");
        rbtAverageRatio = new JRadioButton("Average ratios");
        ButtonGroup bg2 = new ButtonGroup();
        bg2.add(rbtSumIntensities);
        bg2.add(rbtAverageRatio);
        rbtSumIntensities.setSelected(true);

        txtWhereAddition = new JTextField(80);
        txtWhereAddition.setMaximumSize(txtWhereAddition.getPreferredSize());

        chkRecenterProjects = new JCheckBox("Center project data on  ");
        txtRecenterProjects = new JTextField(10);
        txtRecenterProjects.setMaximumSize(txtRecenterProjects.getPreferredSize());
        txtRecenterProjects.setEnabled(false);
        chkRecenterProjects.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (chkRecenterProjects.isSelected()) {
                    txtRecenterProjects.setEnabled(true);
                } else {
                    txtRecenterProjects.setEnabled(false);
                }
            }
        });
        chkRecenterProjects.setSelected(false);


        // The button panel.
        JPanel jpanButtons = this.getButtonPanel();
        jpanButtons.setMaximumSize(new Dimension(jpanButtons.getMaximumSize().width, jpanButtons.getPreferredSize().height));

        // Lay out components.
        // Correct distance between label and textbox (the xWidth variables)
        // in such a way that the textboxes are equally far from the
        // labels (add more space for the shorter label).
        int calibratedWidth = lblCalibratedStDev.getMinimumSize().width;
        int lightWidth = lblLightLabel.getMinimumSize().width;
        int heavyWidth = lblHeavyLabel.getMinimumSize().width;
        heavyWidth = 5 + (calibratedWidth - heavyWidth);
        lightWidth = 5 + (calibratedWidth - lightWidth);
        calibratedWidth = 5;

        JPanel jpanTopCalib = new JPanel();
        jpanTopCalib.setLayout(new BoxLayout(jpanTopCalib, BoxLayout.X_AXIS));
        jpanTopCalib.setBorder(BorderFactory.createTitledBorder("Instrument selection"));
        jpanTopCalib.add(Box.createHorizontalStrut(5));
        jpanTopCalib.add(lblCalibratedStDev);
        jpanTopCalib.add(Box.createHorizontalStrut(calibratedWidth));
        jpanTopCalib.add(lblSemiColon1);
        jpanTopCalib.add(Box.createHorizontalStrut(5));
        jpanTopCalib.add(cmbCalibratedStDev);
        jpanTopCalib.add(Box.createHorizontalGlue());

        JPanel jpanTopLight = new JPanel();
        jpanTopLight.setLayout(new BoxLayout(jpanTopLight, BoxLayout.X_AXIS));
        jpanTopLight.add(Box.createHorizontalStrut(5));
        jpanTopLight.add(lblLightLabel);
        jpanTopLight.add(Box.createHorizontalStrut(lightWidth));
        jpanTopLight.add(lblSemiColon2);
        jpanTopLight.add(Box.createHorizontalStrut(5));
        jpanTopLight.add(txtLightLabel);
        jpanTopLight.add(Box.createHorizontalGlue());

        JPanel jpanTopHeavy = new JPanel();
        jpanTopHeavy.setLayout(new BoxLayout(jpanTopHeavy, BoxLayout.X_AXIS));
        jpanTopHeavy.add(Box.createHorizontalStrut(5));
        jpanTopHeavy.add(lblHeavyLabel);
        jpanTopHeavy.add(Box.createHorizontalStrut(heavyWidth));
        jpanTopHeavy.add(lblSemiColon3);
        jpanTopHeavy.add(Box.createHorizontalStrut(5));
        jpanTopHeavy.add(txtHeavyLabel);
        jpanTopHeavy.add(Box.createHorizontalGlue());

        JPanel jpanTop = new JPanel();
        jpanTop.setLayout(new BoxLayout(jpanTop, BoxLayout.Y_AXIS));
        jpanTop.setBorder(BorderFactory.createTitledBorder("Sample label descriptions"));
        jpanTop.add(jpanTopLight);
        jpanTop.add(jpanTopHeavy);
        jpanTop.setMaximumSize(new Dimension(jpanTop.getMaximumSize().width, jpanTop.getPreferredSize().height));

        JPanel jpanTotalTop = new JPanel();
        jpanTotalTop.setLayout(new BoxLayout(jpanTotalTop, BoxLayout.Y_AXIS));
        jpanTotalTop.add(jpanTopCalib);
        jpanTotalTop.add(Box.createVerticalStrut(5));
        jpanTotalTop.add(jpanTop);
        jpanTotalTop.setMaximumSize(new Dimension(jpanTotalTop.getMaximumSize().width, jpanTotalTop.getPreferredSize().height));

        JPanel jpanBottomListButton = new JPanel();
        jpanBottomListButton.setLayout(new BoxLayout(jpanBottomListButton, BoxLayout.X_AXIS));
        jpanBottomListButton.add(Box.createHorizontalGlue());
        jpanBottomListButton.add(btnList);
        jpanBottomListButton.add(Box.createHorizontalStrut(5));
        jpanBottomListButton.setMaximumSize(new Dimension(jpanBottomListButton.getMaximumSize().width, jpanBottomListButton.getPreferredSize().height));

        JPanel jpanBottomList = new JPanel();
        jpanBottomList.setLayout(new BoxLayout(jpanBottomList, BoxLayout.Y_AXIS));
        jpanBottomList.setBorder(BorderFactory.createTitledBorder("Project list"));
        jpanBottomList.add(new JScrollPane(lstProjects));
        jpanBottomList.add(Box.createVerticalStrut(5));
        jpanBottomList.add(jpanBottomListButton);

        JPanel jpanBottomTableButton = new JPanel();
        jpanBottomTableButton.setLayout(new BoxLayout(jpanBottomTableButton, BoxLayout.X_AXIS));
        jpanBottomTableButton.add(Box.createHorizontalGlue());
        jpanBottomTableButton.add(btnTable);
        jpanBottomTableButton.add(Box.createHorizontalStrut(5));
        jpanBottomTableButton.setMaximumSize(new Dimension(jpanBottomTableButton.getMaximumSize().width, jpanBottomTableButton.getPreferredSize().height));

        JPanel jpanBottomTable = new JPanel();
        jpanBottomTable.setLayout(new BoxLayout(jpanBottomTable, BoxLayout.Y_AXIS));
        jpanBottomTable.setBorder(BorderFactory.createTitledBorder("Selected projects"));
        jpanBottomTable.add(new JScrollPane(tblSelectedProjects));
        jpanBottomTable.add(Box.createVerticalStrut(5));
        jpanBottomTable.add(jpanBottomTableButton);

        JPanel jpanBottom = new JPanel();
        jpanBottom.setLayout(new BoxLayout(jpanBottom, BoxLayout.X_AXIS));
        jpanBottom.add(jpanBottomList);
        jpanBottom.add(Box.createHorizontalStrut(5));
        jpanBottom.add(jpanBottomTable);

        JPanel jpanStats = new JPanel();
        jpanStats.setLayout(new BoxLayout(jpanStats, BoxLayout.X_AXIS));
        jpanStats.setBorder(BorderFactory.createTitledBorder("Statistical analysis method"));
        jpanStats.add(Box.createHorizontalStrut(10));
        jpanStats.add(rbtRobustStats);
        jpanStats.add(Box.createHorizontalStrut(5));
        jpanStats.add(rbtStandardStats);
        jpanStats.add(Box.createHorizontalGlue());

        JPanel jpanAverage = new JPanel();
        jpanAverage.setLayout(new BoxLayout(jpanAverage, BoxLayout.X_AXIS));
        jpanAverage.setBorder(BorderFactory.createTitledBorder("Averaging method for merged couples"));
        jpanAverage.add(Box.createHorizontalStrut(10));
        jpanAverage.add(rbtSumIntensities);
        jpanAverage.add(Box.createHorizontalStrut(5));
        jpanAverage.add(rbtAverageRatio);
        jpanAverage.add(Box.createHorizontalGlue());

        JPanel jpanWhereAddition = new JPanel();
        jpanWhereAddition.setLayout(new BoxLayout(jpanWhereAddition, BoxLayout.Y_AXIS));
        jpanWhereAddition.setBorder(BorderFactory.createTitledBorder("Optional addition to where clause"));
        JLabel lblWhereAddition = new JLabel("Prefix identification columns with 'i.' and spectrum columns with 's.'! ");
        Font oldFont = lblWhereAddition.getFont();
        lblWhereAddition.setFont(new Font(oldFont.getName(), Font.BOLD | Font.ITALIC, oldFont.getSize()));
        if (this.getBackground() != Color.red) {
            lblWhereAddition.setForeground(Color.red);
        } else {
            lblWhereAddition.setForeground(Color.blue);
        }
        jpanWhereAddition.add(lblWhereAddition);
        jpanWhereAddition.add(Box.createVerticalStrut(5));
        jpanWhereAddition.add(txtWhereAddition);

        JPanel temp = new JPanel();
        temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
        temp.add(jpanWhereAddition);
        temp.add(Box.createHorizontalGlue());

        JPanel jpanRecenter = new JPanel();
        jpanRecenter.setLayout(new BoxLayout(jpanRecenter, BoxLayout.X_AXIS));
        jpanRecenter.setBorder(BorderFactory.createTitledBorder("Centering of the individual projects"));
        jpanRecenter.add(Box.createHorizontalStrut(10));
        jpanRecenter.add(chkRecenterProjects);
        jpanRecenter.add(Box.createHorizontalStrut(5));
        jpanRecenter.add(txtRecenterProjects);
        jpanRecenter.add(Box.createHorizontalGlue());

        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanTotalTop);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanBottom);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanStats);
        jpanMain.add(Box.createVerticalStrut(10));
        if (iExpert) {
            jpanMain.add(jpanAverage);
            jpanMain.add(Box.createVerticalStrut(10));
            jpanMain.add(temp);
            jpanMain.add(Box.createVerticalStrut(10));
        }
        jpanMain.add(jpanRecenter);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanButtons);
        jpanMain.add(Box.createVerticalStrut(5));
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method creates and lays out the buttonpanel, which is returned.
     *
     * @return JPanel with the buttons.
     */
    private JPanel getButtonPanel() {

        JButton btnAnalyze = new JButton("Analyze differential data!");
        btnAnalyze.setMnemonic(KeyEvent.VK_D);
        btnAnalyze.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    analyzePressed();
                }
            }
        });
        btnAnalyze.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                analyzePressed();
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnAnalyze);
        jpanButtons.add(Box.createHorizontalStrut(10));

        return jpanButtons;
    }

    /**
     * This method closes down the application, taking care of the database connection in the process.
     *
     * @param aStatus int with the status to set the status flag to.
     */
    private void close(int aStatus) {
        try {
            if (iConnection != null) {
                iConnection.close();
            }
        } catch (SQLException sqle) {
            // Do nothing.
        }
        this.setVisible(false);
        this.dispose();
        System.exit(aStatus);
    }

    /**
     * This method reads all available differential projects from the database.
     */
    private void readProjects() {
        try {
            iProjects = Project.getAllDifferentialProjects(iConnection);
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, "Unable to read projects: " + sqle.getMessage(), "Unable to read project data", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method reads all differentially calibrated instruments from the database.
     */
    private void readInstruments() {
        try {
            iInstruments = Instrument.getAllDifferentialCalibratedInstruments(iConnection);
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, "Unable to read instruments: " + sqle.getMessage(), "Unable to read instrument data", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called when the user presses the 'analyze' button and will perform the differential analysis.
     */
    private void analyzePressed() {
        // Get the calibrated standard deviation for the instrument used, as well as its ID.
        Instrument instrument = (Instrument) cmbCalibratedStDev.getSelectedItem();
        double calibratedStDev = instrument.getDifferential_calibration().doubleValue();
        long instrumentID = instrument.getInstrumentid();
        // Get the labels.
        String lightLabel = txtLightLabel.getText();
        String heavyLabel = txtHeavyLabel.getText();
        // Get the number of rows in the table.
        int projectCount = tblSelectedProjects.getRowCount();

        // Validate the projectCount (see if there are any projects selected at all).
        if (projectCount <= 0) {
            JOptionPane.showMessageDialog(this, "Please select one or more projects to analyze first", "No projects selected!", JOptionPane.WARNING_MESSAGE);
            lstProjects.requestFocus();
            return;
        }

        // Validate the labels.
        if (lightLabel == null || lightLabel.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Please specify a short description for the light isotope", "No 'light label' given!", JOptionPane.WARNING_MESSAGE);
            txtLightLabel.requestFocus();
            return;
        } else {
            lightLabel = lightLabel.trim();
        }
        if (heavyLabel == null || heavyLabel.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Please specify a short description for the heavy isotope", "No 'heavy label' given!", JOptionPane.WARNING_MESSAGE);
            txtHeavyLabel.requestFocus();
            return;
        } else {
            heavyLabel = heavyLabel.trim();
        }
        // Validate potential centering.
        Double recenter = null;
        String centered = "not centered";
        if (chkRecenterProjects.isSelected()) {
            String text = txtRecenterProjects.getText();
            try {
                recenter = new Double(text);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please specify a valid decimal number for the centering value", "Invalid number for centering!", JOptionPane.WARNING_MESSAGE);
                txtRecenterProjects.requestFocus();
                return;
            }
            centered = "on " + text;
        }
        // Get the DifferentialProjects from the table.
        DifferentialProject[] diffProjects = new DifferentialProject[projectCount];
        for (int i = 0; i < projectCount; i++) {
            diffProjects[i] = (DifferentialProject) tblSelectedProjects.getValueAt(i, DifferentialProjectTableModel.REPORT_INSTANCE);
        }
        // Determine the averaging method.
        int averaging = -1;
        String averagingString = "weighted ratios";
        if (rbtSumIntensities.isSelected()) {
            averaging = DiffAnalysisCore.WEIGHTED_RATIOS;
        } else if (rbtAverageRatio.isSelected()) {
            averaging = DiffAnalysisCore.AVERAGE_RATIOS;
            averagingString = "average ratios";
        }
        // See if we have a where clause addition.
        String whereAddition = txtWhereAddition.getText();
        if (whereAddition == null || whereAddition.trim().equals("")) {
            whereAddition = null;
        }
        // Start the processing!
        HashMap results = new HashMap();
        int statType = -1;
        String location = null;
        String scale = null;
        if (rbtRobustStats.isSelected()) {
            statType = DiffAnalysisCore.ROBUST_STATISTICS;
            location = "median";
            scale = "Huber scale";
        } else {
            statType = DiffAnalysisCore.STANDARD_STATISTICS;
            location = "mean";
            scale = "standard deviation";
        }
        DiffAnalysisCore core = new DiffAnalysisCore(iConnection, calibratedStDev, statType, averaging, recenter, whereAddition);
        DefaultProgressBar progress = new DefaultProgressBar(this, "Processing search results...", 0, (diffProjects.length * 2) + 4);
        progress.setSize(350, 100);
        progress.setMessage("Starting up...");
        DiffAnalysisWorker worker = new DiffAnalysisWorker(core, this, progress, diffProjects, instrumentID, results);
        worker.start();
        progress.setVisible(true);
        if (results.size() == 0) {
            JOptionPane.showMessageDialog(this, "No differential identifications were found in the database using your settings!", "No identifications found!", JOptionPane.WARNING_MESSAGE);
        } else {
            double mean = new BigDecimal(((Double) results.get(DiffAnalysisCore.MU_HAT)).doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            double stdev = new BigDecimal(((Double) results.get(DiffAnalysisCore.SIGMA_HAT)).doubleValue()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
            int count = ((Integer) results.get(DiffAnalysisCore.COUNT)).intValue();
            String type = null;
            if (statType == DiffAnalysisCore.ROBUST_STATISTICS) {
                type = "Robust";
            } else {
                type = "Standard";
            }
            String[] messages = null;
            if (chkRecenterProjects.isSelected()) {
                messages = new String[5 + diffProjects.length];
            } else {
                messages = new String[5];
            }
            messages[0] = type + " differential analysis complete!";
            messages[1] = location + " [log2(ratio)]:  " + mean;
            messages[2] = scale + " [log2(ratio)]:  " + stdev;
            messages[3] = "Count:  " + count;
            messages[4] = " ";
            if (chkRecenterProjects.isSelected()) {
                for (int i = 5; i < messages.length; i++) {
                    DifferentialProject dp = diffProjects[i - 5];
                    BigDecimal bd = new BigDecimal(((Double) results.get(new Long(dp.getProjectID()))).doubleValue());
                    messages[i] = "Correction for project " + dp.getProjectID() + ":  " + bd.setScale(4, BigDecimal.ROUND_HALF_UP);
                }
            }
            JOptionPane.showMessageDialog(this, messages, "Analysis complete!", JOptionPane.INFORMATION_MESSAGE);

            // Transform the Projects into a HashMap, keyed by their ProjectID.
            HashMap projectsHash = new HashMap(diffProjects.length);
            for (int i = 0; i < diffProjects.length; i++) {
                DifferentialProject lDiffProject = diffProjects[i];
                projectsHash.put(new Long(lDiffProject.getProjectID()), lDiffProject);
            }

            // Pop-up a new frame with the results.
            JFrame resultFrame = new DifferentialAnalysisResultsFrame("Results from " + type.toLowerCase() + " differential analysis (" + location + ": " + mean + "  " + scale + ": " + stdev + "  count: " + count + "  averaging: " + averagingString + "  centered: " + centered + ")", (Vector) (results.get(DiffAnalysisCore.DIFFCOUPLES)), projectsHash, lightLabel, heavyLabel, averaging);
            resultFrame.setBounds(200, 200, 600, 500);
            resultFrame.setVisible(true);
        }
    }

    /**
     * This method is called when th suer double-clicks a project in the list. It will take care of acquiring the
     * necessary details for this project.
     */
    private void projectListClicked() {
        Object selection = lstProjects.getSelectedValue();
        if (selection == null) {
            JOptionPane.showMessageDialog(this, "Please select a project to add from the list!", "No project selected", JOptionPane.WARNING_MESSAGE);
            lstProjects.requestFocus();
            return;
        }
        // Okay, a project was selected.
        Project project = (Project) lstProjects.getSelectedValue();
        // See if it is not already present in the selected list. No duplicate selections allowed!
        if (iModel.containsProject(project.getProjectid(), project.getTitle())) {
            JOptionPane.showMessageDialog(this, new String[]{"This project was already selected!", "You are not allowed to perform duplicate selections."}, "Project already selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Everything checks out, proceed!
        DifferentialProjectDialog dpd = new DifferentialProjectDialog(this, "Settings for project no. " + project.getProjectid() + ": '" + project.getTitle() + "'", project);
        Point p = this.getLocation();
        dpd.setLocation((int) (p.getX()) + 150, (int) (p.getY()) + 150);
        dpd.setVisible(true);
    }

    /**
     * This method is triggered whenever the user attempts to delete an entry from the 'selected projects' table.
     */
    private void deleteRowRequested() {
        // First see if anything was in fact selected.
        int selectedRow = tblSelectedProjects.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "You need to select the row you wish to delete", "No row selected!", JOptionPane.WARNING_MESSAGE);
            tblSelectedProjects.requestFocus();
            return;
        }
        // OK, proceed with the delete.
        iModel.removeElement(selectedRow);
    }
}
