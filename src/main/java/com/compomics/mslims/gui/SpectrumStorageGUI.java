/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2003
 * Time: 6:58:54
 */
package com.compomics.mslimscore.gui;

import com.compomics.mslimscore.gui.dialogs.ConnectionDialog;
import org.apache.log4j.Logger;

import com.compomics.mslimsdb.accessors.*;
import com.compomics.mslimscore.gui.dialogs.DescriptionDialog;
import com.compomics.mslimscore.gui.dialogs.InstrumentSelectionDialog;
import com.compomics.mslimscore.gui.dialogs.ProjectDialog;
import com.compomics.mslimscore.gui.interfaces.Informable;
import com.compomics.mslimscore.gui.interfaces.ProjectManager;
import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mslimscore.util.fileio.FileExtensionFilter;
import com.compomics.mslimscore.util.fileio.interfaces.SpectrumStorageEngine;
import com.compomics.util.general.CommandLineParser;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.sun.SwingWorker;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.15 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class implements the main GUI for the SpectrumStorage application.
 *
 * @author Lennart Martens
 */
public class SpectrumStorageGUI extends FlamableJFrame implements Connectable, ProjectManager, Informable {
    // Class specific log4j logger for SpectrumStorageGUI instances.
    private static Logger logger = Logger.getLogger(SpectrumStorageGUI.class);

    /**
     * Boolean that indicates whether the tool is ran in stand-alone mode ('true') or not ('false').
     */
    private static boolean iStandAlone = true;

    /**
     * The String with the location for the LC run files.
     */
    private String iParentString = null;

    /**
     * The DB connection for this class.
     */
    private Connection iConn = null;

    /**
     * The identified for the current DB connection.
     */
    private String iDBName = null;

    /**
     * The properties for this instance.
     */
    private Properties iProps = null;

    /**
     * Constant String with the database connection properties file.
     */
    private static final String iConProps = "ms-lims.properties";

    /**
     * The LC run elements that were found in the folder.
     */
    private LCRun[] iLCruns = null;

    /**
     * The LCRun names that were found in the DB.
     */
    private Vector iStoredLCruns = null;


    /**
     * The Projects.
     */
    private Project[] iProjects = null;

    /**
     * The Fragmentations.
     */
    private Fragmentation[] iFragmentations = null;

    /**
     * The Instruments.
     */
    private Instrument[] iInstruments = null;

    /**
     * The selected Instrument (if any).
     */
    private Instrument iSelectedInstrument = null;

    /**
     * This boolean has a lot of effect on the GUI as it makes this application act like a Project management app only
     * (when it is 'true') or as a genuine EsquireSpectrumStorage app (when the boolean is 'false').
     */
    private boolean ibOnlyProjects = false;

    /**
     * This variable holds the effective title for this application.
     */
    private String iCurrentTitle = null;

    /**
     * This boolean indicates whether Mascot Distiller was used for generating the spectrum files.
     */
    private boolean iMascotDistillerProcessing;

    /**
     * Constant with the first part of the Spectrum Storage title.
     */
    private static final String iSSTitle = "SpectrumStorage application";

    /**
     * Constant with the first part of the Project Manager title.
     */
    private static final String iPMTitle = "Project manager application";

    /**
     * Properties file which will be loaded.
     */
    private String iPropsFile = null;

    /**
     * The SpectrumStorageEngine implementation that will do all the real work.
     */
    private SpectrumStorageEngine iEngine = null;

    /**
     * Date-time format String.
     */
    private static final String iDateTimeFormat = "dd/MM/yyyy - HH:mm:ss";

    /**
     * The SimpleDateFormat formatter to display creationdates.
     */
    private static SimpleDateFormat iSDF = new SimpleDateFormat(iDateTimeFormat);

    /**
     * The HashMap that contains the LCRun-project associations.
     */
    private HashMap iAssociations = new HashMap();

    /**
     * The user list.
     */
    private HashMap iUsers = null;

    /**
     * The PROTOCOL types list.
     */
    private HashMap iProtocol = null;

    private JList lstLCruns = null;
    private JComboBox cmbProject = null;
    private JComboBox cmbFragmentation = null;
    private JCheckBox chkProjectSorting = null;
    private JTextField txtResponsible = null;
    private JTextField txtProtocol = null;
    private JTextField txtTitle = null;
    private JTextField txtID = null;
    private JTextField txtUsername = null;
    private JTextField txtCreationDate = null;
    private JTextField txtModificationDate = null;
    private JTextArea txtDescription = null;

    private JTextArea txtSummary = null;
    private JButton btnAssign = null;
    private JButton btnStore = null;
    private JButton btnClear = null;
    private JButton btnNewProject = null;
    private JButton btnModifyProject = null;
    private JButton btnExit = null;
    private Fragmentation iFragmentation;

    /**
     * Default empty constructor made private.
     */
    private SpectrumStorageGUI() {
    }

    /**
     * This constructor takes a title for the frame as its argument. It defaults to the Spectrum Storage GUI!
     *
     * @param aTitle String with the title for the JFrame.
     */
    public SpectrumStorageGUI(String aTitle) {
        this(aTitle, false);
    }

    /**
     * This constructor allows the choice of the display. Options are: Spectrum Storage (boolean parameter should be
     * 'false') or Project Manager (boolean parameter should be 'true').
     *
     * @param aTitle              String with the title for the JFrame.
     * @param aProjectManagerOnly boolean to indicate whether the application is to to be run as a Spectrum Storage app
     *                            ('false'), or a Project Manager ('true').
     */
    public SpectrumStorageGUI(String aTitle, boolean aProjectManagerOnly) {
        this(aTitle, aProjectManagerOnly, null, null);
    }

    /**
     * This constructor allows the choice of the display as well as the specification of the connection to use (and its
     * name). Options are: Spectrum Storage (boolean parameter should be 'false') or Project Manager (boolean parameter
     * should be 'true').
     *
     * @param aTitle              String with the title for the JFrame.
     * @param aProjectManagerOnly boolean to indicate whether the application is to to be run as a Spectrum Storage app
     *                            ('false'), or a Project Manager ('true').
     * @param aConn               Connection with the database connection to use. 'null' means no connection specified
     *                            so create your own (pops up ConnectionDialog).
     * @param aDBName             String with the name for the database connection. Only read if aConn != null.
     */
    public SpectrumStorageGUI(String aTitle, boolean aProjectManagerOnly, Connection aConn, String aDBName) {
        super(aTitle);
        this.iCurrentTitle = aTitle;
        this.setProjectManagerOnly(aProjectManagerOnly);

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
        // If we are not in Project Manager mode, load the instruments
        // and present the user with the selection choices.
        if (!ibOnlyProjects) {
            // Load all instruments.
            this.getInstrumentsFromDB();
            // Display the instrument selection dialog.
            this.instrumentChooserAndInitializer();
            // Load the properties.
            this.loadProperties();
        }
        // Now we have a connection, gather all data next.
        this.gatherData();
        // Some components need to be initialized with the data retrieved.
        // Do this now.
        this.initializeComponents();
        // Build the GUI.
        this.constructScreen();
        // Display settings.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screen.width / 10), (screen.height / 10));
        this.pack();
        //this.setSize(800, 700);
    }

    /**
     * This method will attempt to retrieve all relevant data from the local filesystem and the DB connection.
     */
    private void gatherData() {
        if (!ibOnlyProjects) {
            this.getCaplcFromDB();
            this.findLCrunNames();
        }
        this.findProjects();
        this.findFragmentations();
        this.findUsers();
        this.findProtocol();
    }

    /**
     * This method will use the data retrieved in 'gatherData()' to fill out a few components.
     */
    private void initializeComponents() {
        if (!ibOnlyProjects) {
            lstLCruns = new JList();
            lstLCruns.addMouseListener(new MouseAdapter() {
                /**
                 * Invoked when the mouse has been clicked on a component.
                 */
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        listClick();
                    }
                }
            });
            fillLCrunList();
        }
        cmbProject = new JComboBox();
        cmbProject.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Project selected = (Project) e.getItem();
                    stateChangedProject(selected);
                }
            }
        });
        cmbFragmentation = new JComboBox();
        cmbFragmentation.setPreferredSize(new Dimension(50,20));
        cmbFragmentation.setMaximumSize(new Dimension(50,20));
        cmbFragmentation.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Fragmentation selected = (Fragmentation) e.getItem();
                    stateChangedFragmentation(selected);
                }
            }
        });
        chkProjectSorting = new JCheckBox("Sort projects alphabetically");
        chkProjectSorting.setSelected(false);
        chkProjectSorting.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean alphabetically = false;
                if (chkProjectSorting.isSelected()) {
                    alphabetically = true;
                }
                resortProjects(alphabetically);
            }
        });
        // We also initialize the relevant txtComponents at this point.
        txtTitle = new JTextField(20);
        txtTitle.setEditable(false);
        txtTitle.setMaximumSize(txtTitle.getPreferredSize());
        txtID = new JTextField(20);
        txtID.setEditable(false);
        txtID.setMaximumSize(txtID.getPreferredSize());
        txtResponsible = new JTextField(20);
        txtResponsible.setEditable(false);
        txtResponsible.setMaximumSize(txtResponsible.getPreferredSize());
        txtProtocol = new JTextField(20);
        txtProtocol.setEditable(false);
        txtProtocol.setMaximumSize(txtProtocol.getPreferredSize());
        txtUsername = new JTextField(20);
        txtUsername.setEditable(false);
        txtUsername.setMaximumSize(txtUsername.getPreferredSize());
        txtCreationDate = new JTextField(20);
        txtCreationDate.setEditable(false);
        txtCreationDate.setMaximumSize(txtCreationDate.getPreferredSize());
        txtModificationDate = new JTextField(20);
        txtModificationDate.setEditable(false);
        txtModificationDate.setMaximumSize(txtModificationDate.getPreferredSize());
        txtDescription = new JTextArea(8, 20);
        txtDescription.setMinimumSize(txtDescription.getPreferredSize());
        txtDescription.setEditable(false);

        fillProjectPulldown();
        fillFragmentationPulldown();
    }


    /**
     * This method will take care of organizing and laying out the GUI of the application.
     */
    private void constructScreen() {

        // This panel holds the list view of the LC runs.
        JPanel jpanList = new JPanel();
        jpanList.setLayout(new BoxLayout(jpanList, BoxLayout.Y_AXIS));
        jpanList.setBorder(BorderFactory.createTitledBorder("LC run list"));
        jpanList.add(new JScrollPane(lstLCruns));

        // The labels for the project details fields.
        JLabel lblID = new JLabel("   Project ID: ");
        lblID.setPreferredSize(new Dimension(lblID.getPreferredSize().width, txtTitle.getPreferredSize().height));
        JLabel lblTitle = new JLabel("   Project title: ");
        lblTitle.setPreferredSize(new Dimension(lblTitle.getPreferredSize().width, txtTitle.getPreferredSize().height));
        JLabel lblResponsible = new JLabel("   Project responsible: ");
        lblResponsible.setPreferredSize(new Dimension(lblResponsible.getPreferredSize().width, txtResponsible.getPreferredSize().height));
        JLabel lblProtocol = new JLabel("   PROTOCOL type: ");
        lblProtocol.setPreferredSize(new Dimension(lblProtocol.getPreferredSize().width, txtProtocol.getPreferredSize().height));
        JLabel lblUsername = new JLabel("   Created by: ");
        lblUsername.setPreferredSize(new Dimension(lblUsername.getPreferredSize().width, txtUsername.getPreferredSize().height));
        JLabel lblCreationdate = new JLabel("   Project creationdate: ");
        lblCreationdate.setPreferredSize(new Dimension(lblCreationdate.getPreferredSize().width, txtCreationDate.getPreferredSize().height));
        JLabel lblModificationdate = new JLabel("   Project modificationdate: ");
        lblModificationdate.setPreferredSize(new Dimension(lblModificationdate.getPreferredSize().width, txtModificationDate.getPreferredSize().height));
        JLabel lblDescription = new JLabel("   Project description: ");
        lblDescription.setPreferredSize(new Dimension(lblDescription.getPreferredSize().width, txtCreationDate.getPreferredSize().height));
        JPanel jpanLabels = new JPanel();
        jpanLabels.setLayout(new BoxLayout(jpanLabels, BoxLayout.Y_AXIS));
        jpanLabels.add(lblID);
        jpanLabels.add(Box.createVerticalStrut(5));
        jpanLabels.add(lblTitle);
        jpanLabels.add(Box.createVerticalStrut(5));
        jpanLabels.add(lblResponsible);
        jpanLabels.add(Box.createVerticalStrut(5));
        jpanLabels.add(lblProtocol);
        jpanLabels.add(Box.createVerticalStrut(5));
        jpanLabels.add(lblUsername);
        jpanLabels.add(Box.createVerticalStrut(5));
        jpanLabels.add(lblCreationdate);
        jpanLabels.add(Box.createVerticalStrut(5));
        jpanLabels.add(lblModificationdate);
        jpanLabels.add(Box.createVerticalStrut(5));
        jpanLabels.add(lblDescription);
        jpanLabels.add(Box.createVerticalGlue());
        jpanLabels.setMaximumSize(new Dimension(lblCreationdate.getPreferredSize().width, jpanLabels.getMaximumSize().height));

        // The project details fields.
        JPanel jpanID = new JPanel();
        jpanID.setLayout(new BoxLayout(jpanID, BoxLayout.X_AXIS));
        jpanID.add(txtID);
        jpanID.add(Box.createHorizontalGlue());

        JPanel jpanTitle = new JPanel();
        jpanTitle.setLayout(new BoxLayout(jpanTitle, BoxLayout.X_AXIS));
        jpanTitle.add(txtTitle);
        jpanTitle.add(Box.createHorizontalGlue());

        JPanel jpanResponsible = new JPanel();
        jpanResponsible.setLayout(new BoxLayout(jpanResponsible, BoxLayout.X_AXIS));
        jpanResponsible.add(txtResponsible);
        jpanResponsible.add(Box.createHorizontalGlue());

        JPanel jpanProtocol = new JPanel();
        jpanProtocol.setLayout(new BoxLayout(jpanProtocol, BoxLayout.X_AXIS));
        jpanProtocol.add(txtProtocol);
        jpanProtocol.add(Box.createHorizontalGlue());

        JPanel jpanUsername = new JPanel();
        jpanUsername.setLayout(new BoxLayout(jpanUsername, BoxLayout.X_AXIS));
        jpanUsername.add(txtUsername);
        jpanUsername.add(Box.createHorizontalGlue());

        JPanel jpanCreationDate = new JPanel();
        jpanCreationDate.setLayout(new BoxLayout(jpanCreationDate, BoxLayout.X_AXIS));
        jpanCreationDate.add(txtCreationDate);
        jpanCreationDate.add(Box.createHorizontalGlue());

        JPanel jpanModificationDate = new JPanel();
        jpanModificationDate.setLayout(new BoxLayout(jpanModificationDate, BoxLayout.X_AXIS));
        jpanModificationDate.add(txtModificationDate);
        jpanModificationDate.add(Box.createHorizontalGlue());

        JPanel jpanFields = new JPanel();
        jpanFields.setLayout(new BoxLayout(jpanFields, BoxLayout.Y_AXIS));
        jpanFields.add(jpanID);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(jpanTitle);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(jpanResponsible);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(jpanProtocol);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(jpanUsername);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(jpanCreationDate);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(jpanModificationDate);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(new JScrollPane(txtDescription));

        btnModifyProject = new JButton("Modify project...");
        btnModifyProject.setMnemonic(KeyEvent.VK_M);
        btnModifyProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyProjectTriggered();
            }
        });

        JPanel jpanProjectButton = new JPanel();
        jpanProjectButton.setLayout(new BoxLayout(jpanProjectButton, BoxLayout.X_AXIS));
        jpanProjectButton.add(Box.createHorizontalGlue());
        jpanProjectButton.add(btnModifyProject);
        jpanProjectButton.add(Box.createHorizontalStrut(10));
        jpanProjectButton.setMaximumSize(new Dimension(jpanProjectButton.getMaximumSize().width, btnModifyProject.getPreferredSize().height));

        // This panel holds the project details labels and textfields.
        JPanel jpanProjectDetails = new JPanel();
        jpanProjectDetails.setLayout(new BoxLayout(jpanProjectDetails, BoxLayout.X_AXIS));
        jpanProjectDetails.setBorder(BorderFactory.createTitledBorder("Project details"));
        jpanProjectDetails.add(jpanLabels);
        jpanProjectDetails.add(Box.createHorizontalStrut(15));
        jpanProjectDetails.add(jpanFields);

        // Create new projects button.
        btnNewProject = new JButton("Create new project...");
        btnNewProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newProjectTriggered();
            }
        });
        btnNewProject.setMnemonic(KeyEvent.VK_N);
        // This allows the component to catch 'tab' keystrokes.
        btnNewProject.setFocusTraversalKeysEnabled(false);
        btnNewProject.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    newProjectTriggered();
                } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    btnAssign.requestFocus();
                }
            }
        });

        // A panel for the pull-down box.
        JPanel jpanCombo = new JPanel();
        jpanCombo.setLayout(new BoxLayout(jpanCombo, BoxLayout.X_AXIS));
        jpanCombo.add(cmbProject);
        jpanCombo.add(Box.createHorizontalStrut(15));
        jpanCombo.add(btnNewProject);
        jpanCombo.add(Box.createHorizontalGlue());

        // Restricting the height of the combopanel to the preferred height of the new project button.
        jpanCombo.setMaximumSize(new Dimension(jpanCombo.getMaximumSize().width, btnNewProject.getPreferredSize().height));

        // A panel for the sorting checkbox.
        JPanel jpanSort = new JPanel();
        jpanSort.setLayout(new BoxLayout(jpanSort, BoxLayout.X_AXIS));
        jpanSort.add(chkProjectSorting);
        jpanSort.add(Box.createHorizontalGlue());

        // A panel for the combobox + the sorting checkbox.
        JPanel jpanComboSort = new JPanel();
        jpanComboSort.setLayout(new BoxLayout(jpanComboSort, BoxLayout.Y_AXIS));
        jpanComboSort.setBorder(BorderFactory.createTitledBorder("Project selection"));
        jpanComboSort.add(jpanCombo);
        jpanComboSort.add(jpanSort);

        // Combine the project pulldown and the projectdetails.
        JPanel jpanProjects = new JPanel();
        jpanProjects.setLayout(new BoxLayout(jpanProjects, BoxLayout.Y_AXIS));
        jpanProjects.add(jpanComboSort);
        jpanProjects.add(Box.createVerticalStrut(5));
        jpanProjects.add(jpanProjectDetails);
        jpanProjects.add(Box.createVerticalStrut(5));
        jpanProjects.add(jpanProjectButton);
        jpanProjects.add(Box.createVerticalStrut(5));

        // Create a split pane with the two scroll panes in it.
        if (!ibOnlyProjects) {
            JScrollPane jscrProjects = new JScrollPane(jpanProjects);
            JSplitPane splitPaneTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jpanList, jscrProjects);
            splitPaneTop.setDividerLocation(jpanList.getPreferredSize().width + 25);

            // The top panel.
            JPanel jpanTop = new JPanel();
            jpanTop.setLayout(new BoxLayout(jpanTop, BoxLayout.X_AXIS));
            jpanTop.add(splitPaneTop);

            // The summary panel.
            txtSummary = new JTextArea(8, 45);
            txtSummary.setEditable(false);
            txtSummary.setFont(new Font("Monospaced", Font.PLAIN, 14));
            JPanel jpanSummary = new JPanel(new BorderLayout());
            jpanSummary.setBorder(BorderFactory.createTitledBorder("Summary"));
            jpanSummary.add(new JScrollPane(txtSummary), BorderLayout.CENTER);

            // Splitpane between the top splitpane and the summary panel.
            JSplitPane splitPaneDown = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jpanTop, jpanSummary);
            splitPaneDown.setResizeWeight(0.3);

            // Panel for the bottom splitpane.
            JPanel jpanBottom = new JPanel();
            jpanBottom.setLayout(new BoxLayout(jpanBottom, BoxLayout.X_AXIS));
            jpanBottom.add(splitPaneDown);

            // The button panel.
            JPanel jpanButtons = this.createButtonPanel();

            // The main panel.
            JPanel jpanMain = new JPanel();
            jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
            jpanMain.add(jpanBottom);
            jpanMain.add(Box.createVerticalStrut(10));
            jpanMain.add(jpanButtons);
            jpanMain.add(Box.createVerticalStrut(5));
            this.getContentPane().add(jpanMain, BorderLayout.CENTER);
            this.setJMenuBar(this.createMenuBar());
        } else {
            JPanel jpanMain = new JPanel();
            jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
            jpanMain.add(jpanProjects);
            this.getContentPane().add(jpanMain, BorderLayout.CENTER);
        }
    }

    /**
     * The main method is the entry point for the application.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        SpectrumStorageGUI ss = null;
        CommandLineParser clp = new CommandLineParser(args);
        boolean projectsOnly = false;
        String title = iSSTitle;
        if (clp.hasFlag("p")) {
            projectsOnly = true;
            title = iPMTitle;
        }
        try {
            ss = new SpectrumStorageGUI(title, projectsOnly);
            ss.setVisible(true);
        } catch (Throwable t) {
            new SpectrumStorageGUI().passHotPotato(t);
        }
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
     * This method is meant to be called when the user has changed or added a project<br /> Changes will then be read
     * from the DB (along with all the other projects). Calling this method will result in a DB SQL statement being
     * executed.
     */
    public void projectsChanged() {
        this.findProjects();
        resortProjects(chkProjectSorting.isSelected());
        this.fillProjectPulldown();
    }

    /**
     * This method is called when the user selects 'cancel' in the summary screen. It will then prompt the user about
     * the option to reset all assignments at that point. If the user selects 'yes', the boolean here should be 'true'.
     *
     * @param aReset boolean to indicate whether all assignments should be reset.
     */
    public void assignmentCancelled(boolean aReset) {
        // If we need to reset all assignments...
        if (aReset) {
            // Set all LC runs to unassigned.
            for (int i = 0; i < iLCruns.length; i++) {
                iLCruns[i].setAssigned(false);
            }
            // Clear the association HashMap.
            iAssociations = new HashMap();
        }
        this.fillLCrunList();
    }

    /**
     * This method can be called by a child component (typically a dialog) that wants to inform the parent class of a
     * certain event.
     *
     * @param o Object with the information to transfer.
     */
    public void inform(Object o) {
        if (o instanceof Instrument) {
            this.iSelectedInstrument = (Instrument) o;
        } else if (o instanceof Boolean) {
            this.iMascotDistillerProcessing = (Boolean) o;
        }
    }

    /**
     * This method calls upon a GUI component to handle the connection.
     */
    private void getConnection() {
        ConnectionDialog cd = new ConnectionDialog(this, this, "Database connection for SpectrumManager", iConProps);
        cd.setVisible(true);
    }

    /**
     * This method loads the properties from a properties file.
     */
    private void loadProperties() {
        iProps = new Properties();
        try {
            if (iMascotDistillerProcessing) {
                // If MascotDistiller is used for processing, then always use the MascotDastiller properties to locate
                // the Distiller Storage Engine.
                iProps.load(this.getClass().getClassLoader().getResourceAsStream("mascotdistiller.properties"));
            } else {
                iProps.load(this.getClass().getClassLoader().getResourceAsStream(iPropsFile));
            }
        } catch (Exception e) {
            this.passHotPotato(e, "Failed to locate properties file (" + iPropsFile + ")!");
        }
    }

    /**
     * This method should be called by the finalize and dispose methods.
     */
    private void closeConnection() {
        try {
            if (iConn != null && iStandAlone) {
                iConn.close();
                logger.info("\nClosed DB connection.\n");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * This method searches the path defined in the properties file as the 'pklfilepath' for all subfolders. Each of
     * these is considered a LCRun run and the name and filecount of each is used to create a LCRun object.
     */
    private void findLCrunNames() {
        Vector names = new Vector(10, 5);

        if (iParentString == null) {
            iParentString = iProps.getProperty("spectrumfilepath");

            if (iMascotDistillerProcessing) {
                this.changeDirRequested(false);
            }
        }
        if (iParentString == null) {
            JOptionPane.showMessageDialog(this, new String[]{"Variable 'spectrumfilepath' was not defined in the " + iPropsFile + " properties file!", "\n", "Please select a new folder"}, "Folder not found!", JOptionPane.WARNING_MESSAGE);
            this.changeDirRequested(false);
        }
        iParentString = iParentString.trim();
        if (iParentString.equals("")) {
            JOptionPane.showMessageDialog(this, new String[]{"Variable 'spectrumfilepath' was blank in the " + iPropsFile + " properties file!", "\n", "Please select a new folder"}, "Folder not found!", JOptionPane.WARNING_MESSAGE);
            this.changeDirRequested(false);
        }
        File parent = null;
        boolean found = false;
        while (!found) {
            parent = new File(iParentString);
            if (!parent.exists()) {
                JOptionPane.showMessageDialog(this, new String[]{"Folder '" + iParentString + "', as defined in the " + iPropsFile + " properties file, does not exist!", "\n", "Please select a new folder"}, "Folder not found!", JOptionPane.WARNING_MESSAGE);
                this.changeDirRequested(false);
            } else if (!parent.isDirectory()) {
                JOptionPane.showMessageDialog(this, new String[]{"Folder '" + iParentString + "', as defined in the " + iPropsFile + " properties file, is not a directory!", "\n", "Please select a new folder"}, "Folder not found!", JOptionPane.WARNING_MESSAGE);
                this.changeDirRequested(false);
            } else {
                found = true;
            }
        }


        File[] list;
        if (iMascotDistillerProcessing) {
            list = filterForMGFFiles(parent.listFiles());
        } else {
            list = parent.listFiles();
        }

        // Show loading progress.
        DefaultProgressBar progress =
                new DefaultProgressBar(this, "Loading LC run folders", 0, list.length, "Loading LC run folders from filesystem.");
        iEngine.findAllLCRunsFromFileSystem(list, iStoredLCruns, names, this, progress);
        iLCruns = new LCRun[names.size()];
        names.toArray(iLCruns);
        Arrays.sort(iLCruns);
        this.setTitle(this.iCurrentTitle + "  (" + iLCruns.length + " new LC runs loaded)");
    }

    /**
     * This method filters the .mgf files from the given array of files.
     *
     * @param aFiles The file list (including some .mgf files).
     * @return The filtered .mgf files.
     */
    private File[] filterForMGFFiles(final File[] aFiles) {

        FileFilter lFilter = new FileExtensionFilter(".mgf");

        ArrayList<File> list = new ArrayList<File>();

        for (int i = 0; i < aFiles.length; i++) {
            File lFile = aFiles[i];
            if (lFilter.accept(lFile)) {
                list.add(lFile);
            }
        }

        File[] lResult = new File[list.size()];

        return list.toArray(lResult);
    }

    /**
     * This method finds all project entries currently stored in the DB and fills out the relevant arrays with info.
     */
    private void findProjects() {
        try {
            iProjects = Project.getAllProjects(iConn);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve project data!");
        }
    }

    /**
     * This method finds all project entries currently stored in the DB and fills out the relevant arrays with info.
     */
    private void findFragmentations() {
        try {
            iFragmentations = Fragmentation.getFragmentations(iConn);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve fragmentations!");
        }
    }

    /**
     * This method collects all user information. It fills the 'iUsers' cache.
     */
    private void findUsers() {
        try {
            iUsers = User.getAllUsersAsMap(iConn);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve user data!");
        }
    }

    /**
     * This method collects all protocol information. It fills the 'iProtocol' cache.
     */
    private void findProtocol() {
        try {
            iProtocol = Protocol.getAllProtocolsAsMap(iConn);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve user data!");
        }
    }

    /**
     * This method takes the information in the LCRun[] iLCruns and uses it to fill out the JList lstLCruns.
     */
    private void fillLCrunList() {
        Vector temp = new Vector();
        for (int i = 0; i < iLCruns.length; i++) {
            LCRun lrun = iLCruns[i];
            if (!lrun.isAssigned()) {
                temp.add(lrun);
            }
        }

        Object[] tempData = new Object[temp.size()];
        temp.toArray(tempData);
        lstLCruns.setListData(tempData);
        lstLCruns.ensureIndexIsVisible(0);
        lstLCruns.clearSelection();
    }

    /**
     * This method fills out the cmbProject with the data in the iProjects Project[].
     */
    private void fillProjectPulldown() {
        cmbProject.setModel(new DefaultComboBoxModel(iProjects));
        stateChangedProject((Project) cmbProject.getSelectedItem());
    }

    /**
     * This method fills out the cmbProject with the data in the iProjects Project[].
     */
    private void fillFragmentationPulldown() {
        cmbFragmentation.setModel(new DefaultComboBoxModel(iFragmentations));
        stateChangedFragmentation((Fragmentation) cmbFragmentation.getSelectedItem());
    }

    /**
     * This method is called whenever the user selects another element in the project combobox (cmbProject).
     *
     * @param aProject Project that was selected in the combobox.
     */
    private void stateChangedProject(Project aProject) {
        if (aProject != null) {
            txtID.setText(Long.toString(aProject.getProjectid()));
            txtTitle.setText(aProject.getTitle());
            Long key = new Long(aProject.getL_userid());
            User userValue = (User) iUsers.get(key);
            txtResponsible.setText(userValue.getName());
            key = new Long(aProject.getL_protocolid());
            Protocol cofValue = (Protocol) iProtocol.get(key);
            txtProtocol.setText(cofValue.getType());
            txtUsername.setText(aProject.getUsername());
            txtCreationDate.setText(iSDF.format(aProject.getCreationdate()));
            txtModificationDate.setText(iSDF.format(aProject.getModificationdate()));
            txtDescription.setText(aProject.getDescription());
            if (!txtDescription.getText().equals("")) {
                txtDescription.setCaretPosition(1);
            }
        }
    }

    /**
     * This method is called whenever the user selects another element in the project combobox (cmbFragmentation).
     *
     * @param aFragmentation Fragmentation that was selected in the combobox.
     */
    private void stateChangedFragmentation(Fragmentation aFragmentation) {
        if (aFragmentation != null) {
            iFragmentation = aFragmentation;
        }
    }

    /**
     * This method creates and returns a JPanel with the buttons.
     *
     * @return JPanel  with the buttons.
     */
    private JPanel createButtonPanel() {
        // Assign button.
        btnAssign = new JButton("Assign LC run(s) to project");
        btnAssign.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignTriggered();
            }
        });
        btnAssign.setMnemonic(KeyEvent.VK_A);
        btnAssign.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    assignTriggered();
                }
            }
        });

        // Store button.
        btnStore = new JButton("Store");
        btnStore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeTriggered();
            }
        });
        btnStore.setMnemonic(KeyEvent.VK_S);
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

        // Clear button.
        btnClear = new JButton("Clear");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearTriggered();
            }
        });
        btnClear.setMnemonic(KeyEvent.VK_C);
        btnClear.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    clearTriggered();
                }
            }
        });

        // Exit button.
        btnExit = new JButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        btnExit.setMnemonic(KeyEvent.VK_X);
        btnExit.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dispose();
                }
            }
        });

        // Button panel itself.
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(cmbFragmentation);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnAssign);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnStore);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnClear);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnExit);
        jpanButtons.add(Box.createHorizontalStrut(10));

        // Restricting the height of the buttonpanel to the preferred height of the buttons.
        jpanButtons.setMaximumSize(new Dimension(jpanButtons.getMaximumSize().width, btnAssign.getPreferredSize().height));

        return jpanButtons;
    }

    /**
     * This method is called when the user presses the store button.
     */
    private void storeTriggered() {
        // First determine the number of LC runs to store.
        int total = 0;
        Iterator itCount = iAssociations.keySet().iterator();
        while (itCount.hasNext()) {
            Project lProject = (Project) itCount.next();
            Vector caplc = (Vector) iAssociations.get(lProject);
            total += caplc.size();
        }
        final DefaultProgressBar progress = new DefaultProgressBar(this, "Storing LC runs in the database", 0, total);
        progress.setSize(this.getWidth() / 2, progress.getPreferredSize().height);

        SwingWorker sw = new SwingWorker() {
            /**
             * Compute the value to be returned by the <code>get</code> method.
             */
            public Object construct() {
                if (iAssociations.size() == 0) {
                    JOptionPane.showMessageDialog(SpectrumStorageGUI.this, "First assign some LC run(s) to (a) project(s)!", "No assignments made!", JOptionPane.WARNING_MESSAGE);
                    return "";
                }
                Cursor tempCursor = SpectrumStorageGUI.this.getCursor();
                SpectrumStorageGUI.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                try {
                    // First, cycle the projects (keys in the HashMap).
                    Iterator it = iAssociations.keySet().iterator();
                    // Counters.
                    int caplcCounter = 0;
                    int pklCounter = 0;
                    while (it.hasNext()) {
                        Project lProject = (Project) it.next();
                        // Get the project ID.
                        long projectid = lProject.getProjectid();
                        // Now cycle all associated LC runs.
                        Vector caplcVec = (Vector) iAssociations.get(lProject);
                        int liSize = caplcVec.size();
                        for (int i = 0; i < liSize; i++) {
                            LCRun run = (LCRun) caplcVec.elementAt(i);
                            progress.setMessage("Storing LC run '" + run.getName() + "' in project '" + lProject.getTitle() + "'...");
                            // Setting the l_projectid field.
                            run.setL_projectid(projectid);
                            // Inserting.
                            run.persist(iConn);
                            // Finding the auto-generated ID for the LCRun.
                            Long l = (Long) run.getGeneratedKeys()[0];
                            run.setLcrunid(l.longValue());
                            // Now get all spectra and store these as well.
                            pklCounter +=
                                    iEngine.loadAndStoreSpectrumFiles(run, projectid, iSelectedInstrument.getInstrumentid(), iConn, iFragmentation);
                            caplcCounter++;
                            progress.setValue(caplcCounter);
                        }
                    }
                    SpectrumStorageGUI.this.setCursor(tempCursor);
                    SpectrumStorageGUI.this.iAssociations = new HashMap();
                    txtSummary.setText("");
                    SpectrumStorageGUI.this.gatherData();
                    SpectrumStorageGUI.this.fillLCrunList();
                    JOptionPane.showMessageDialog(SpectrumStorageGUI.this, "All LC runs (" + caplcCounter + ") and spectrum files (" + pklCounter + ") have been stored!", "Store complete!", JOptionPane.INFORMATION_MESSAGE);
                } catch (Throwable t) {
                    SpectrumStorageGUI.this.passHotPotato(t, "Unable to store assignments!");
                }
                return "";
            }
        };
        sw.start();
        progress.setVisible(true);
    }

    /**
     * This method is called when the user clicks the clear button.
     */
    private void clearTriggered() {
        iAssociations = new HashMap();
        for (int i = 0; i < iLCruns.length; i++) {
            LCRun lCapLC = iLCruns[i];
            lCapLC.setAssigned(false);
        }
        txtSummary.setText("");
        fillLCrunList();

    }

    /**
     * This method creates the menubar for this application.
     *
     * @return JMenuBar    with the menubar for this application.
     */
    private JMenuBar createMenuBar() {
        JMenu jmFile = new JMenu("File");
        jmFile.setMnemonic(KeyEvent.VK_F);
        JMenuItem jmiChangeDir = new JMenuItem("Change LC run source directory...", KeyEvent.VK_H);
        jmiChangeDir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeDirRequested(true);
            }
        });
        jmFile.add(jmiChangeDir);
        JMenuBar jbar = new JMenuBar();
        jbar.add(jmFile);

        return jbar;
    }

    /**
     * This method is called when the user clicks the assign button.
     */
    private void assignTriggered() {
        // First get all selected items of the list.
        Object[] selection = lstLCruns.getSelectedValues();

        // See if we have any at all; if not: inform and return.
        if (selection == null || selection.length == 0) {
            JOptionPane.showMessageDialog(this, "Cannot assign LC runs since no items are selected in the list!", "Unable to assign empty selection", JOptionPane.WARNING_MESSAGE);
            lstLCruns.requestFocus();
            return;
        }

        // OK, we seem to have some candidates.
        // See if there is anything associated to the project already,
        // if not, add a new element in the association HashMap.
        Object project = cmbProject.getSelectedItem();
        Vector tempVec = null;
        if (iAssociations.containsKey(project)) {
            tempVec = (Vector) iAssociations.get(project);
        } else {
            tempVec = new Vector(selection.length, 10);
        }
        for (int i = 0; i < selection.length; i++) {
            tempVec.add(selection[i]);
            ((LCRun) (selection[i])).setAssigned(true);
        }
        iAssociations.put(project, tempVec);
        this.fillLCrunList();
        this.updateSummary();
    }

    /**
     * This method is called when the user clicks the create new project button.
     */
    private void newProjectTriggered() {
        try {
            ProjectDialog pd = new ProjectDialog(this, "Create a new project", ProjectDialog.NEW, null, iConn);
            Point p = this.getLocation();
            pd.setLocation((int) (p.getX()) + 50, (int) (p.getY()) + 50);
            pd.setVisible(true);
        } catch (Throwable t) {
            this.passHotPotato(t);
        }
    }

    /**
     * This method is called when the user clicks the modify project button.
     */
    private void modifyProjectTriggered() {
        if (cmbProject.getSelectedItem() != null) {
            try {
                ProjectDialog pd =
                        new ProjectDialog(this, "Modify existing project", ProjectDialog.CHANGE, (Project) cmbProject.getSelectedItem(), iConn);
                Point p = this.getLocation();
                pd.setLocation((int) (p.getX()) + 50, (int) (p.getY()) + 50);
                pd.setVisible(true);
            } catch (Throwable t) {
                this.passHotPotato(t);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No project selected to modify!", "No project selected!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * This method is called when the user doubleclicks on an item in the list.
     */
    private void listClick() {
        LCRun run = (LCRun) lstLCruns.getSelectedValue();
        // See if anything was selected at all.
        if (run == null) {
            return;
        }
        Point p = this.getLocation();

        run.setDescription(DescriptionDialog.getDescriptionDialog(this, "Edit LC run description", run.getDescription(), (int) (p.getX()) + 50, (int) (p.getY()) + 50));
    }

    /**
     * Method that takes care of showing the GUI for entering the new source dir for the PKL files.
     *
     * @param aUpdateGUI
     */
    private void changeDirRequested(boolean aUpdateGUI) {
        try {
            String s = "";
            if (this.iParentString != null) {
                s = this.iParentString;
            }
            JFileChooser jfc = new JFileChooser(s);
            jfc.setDialogTitle("Select the LC run source folder");
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int choice = jfc.showOpenDialog(this);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File f = jfc.getSelectedFile();
                this.iParentString = f.getCanonicalPath();
                iProps.put("spectrumfilepath",f.getAbsolutePath());
                iProps.store(new FileOutputStream("mascotdistiller.properties"), null);
                if (aUpdateGUI) {
                    this.findLCrunNames();
                    this.fillLCrunList();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(this, new String[]{"Unable to open folder!", e.getMessage()}, "Unable to open folder!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method reads the iAssociations HashMap and updates the txtSummary field according to the data in that
     * HashMap.
     */
    private void updateSummary() {
        StringBuffer sb = new StringBuffer();
        Iterator it = iAssociations.keySet().iterator();
        int count = 0;
        // Cycle each project.
        while (it.hasNext()) {
            Project lProject = (Project) it.next();
            // Endline for all but first element.
            if (count != 0) {
                sb.append("\n");
            }
            sb.append(" " + lProject.toString() + "\n ");
            for (int i = 0; i < lProject.toString().length(); i++) {
                sb.append("-");
            }
            sb.append("\n");
            // Cycle each LCRun for this project.
            Vector tempVec = (Vector) iAssociations.get(lProject);
            int liSize = tempVec.size();
            for (int i = 0; i < liSize; i++) {
                LCRun lCapLC = (LCRun) tempVec.elementAt(i);
                sb.append("   + " + lCapLC.toString().toLowerCase() + "\n");
            }
            count++;
        }
        txtSummary.setText(sb.toString());
    }

    /**
     * This method gets all LCRun names from the DB. These are stored in an array for reference against the LCRun names
     * found in the working LCRun dir. Those that occur in both are omitted from the listview!
     */
    private void getCaplcFromDB() {
        try {
            this.iStoredLCruns = LCRun.getUniqueLCRunNames(iConn, 30);
        } catch (Throwable t) {
            this.passHotPotato(t, "Unable to read LC run names from the DB!");
        }
    }

    /**
     * This method gets all Instruments from the DB. These are stored in the iInstruments Instrument array.
     */
    private void getInstrumentsFromDB() {
        try {
            iInstruments = Instrument.getAllInstruments(iConn);
            if (iInstruments == null || iInstruments.length == 0) {
                this.passHotPotato(new SQLException("No instruments stored in DB!", "Unable to retrieve the list of supported instruments!"));
            }
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve the list of supported instruments!");
        }
    }

    /**
     * This method allows the caller to set the 'Project Manager only' boolean. When it is set, the application behaves
     * like a Project Manager only, else it looks and behaves like a spectrum storage app.
     *
     * @param aProjectManagerOnly boolean to indicate whether this app chould be run as a Project manager only ('true')
     *                            or a QTOFSpectrumStorage app ('false'). This is the default setting.
     */
    private void setProjectManagerOnly(boolean aProjectManagerOnly) {
        this.ibOnlyProjects = aProjectManagerOnly;
    }

    /**
     * This method will display a dialog with known instruments so that the user can select the appropriate instrument.
     * Based on this decision, the corresponding 'storageclassname' and 'propertiesfilename' will be read and
     * initialized.
     */
    private void instrumentChooserAndInitializer() {
        // First create and initialize the dialog.
        InstrumentSelectionDialog isd = new InstrumentSelectionDialog(this, iInstruments);
        isd.setVisible(true);
        // Okay, so far so good.
        // Now, if the selected instrument is 'null', we should exit, since that would have been
        // a 'cancel' press.
        if (iSelectedInstrument == null) {
            close();
        }
        // In getting here, an instrument selection has been made.
        // So now try to find the corresponding classes.
        String converterClassName = null;
        if (iMascotDistillerProcessing) {
            // Hard coded class reference in case of Mascot Distiller processing.
            // Independent from instrument selection!
            converterClassName = "com.compomics.mslimscore.util.fileio.MascotDistillerStorageEngine";
        } else {
            converterClassName = iSelectedInstrument.getStorageclassname();
        }
        if (converterClassName == null || converterClassName.trim().equals("")) {
            passHotPotato(new SQLException("No storage classname defined for the '" + iSelectedInstrument + "' instrument!", "Unable to load storage class!"));
        }
        // Try to load the storage class.
        Object instance = null;
        try {
            instance = Class.forName(converterClassName.trim()).newInstance();
        } catch (ClassNotFoundException cnfe) {
            passHotPotato(cnfe, "Unable to load class '" + converterClassName + "' from the current classpath!");
        } catch (InstantiationException ie) {
            passHotPotato(ie, "The specified class ('" + converterClassName + "') appears to be abstract and cannot be instantiated!");
        } catch (IllegalAccessException iae) {
            passHotPotato(iae, "Apparently, no public default constructor is available on the specified class ('" + converterClassName + "')!");
        }
        // Storage class should be loaded, but check anyway.
        if (instance == null) {
            passHotPotato(new Exception("Could not instantiate storage class!"), "Unable to create a new instance for class '" + converterClassName + "'!");
        }
        // Here we can be sure class loading went OK, so check whether the loaded class is a SpectrumStorageEngine
        // implementation.
        if (!(instance instanceof SpectrumStorageEngine)) {
            passHotPotato(new Exception("Storage class incorrect!"), "Class '" + converterClassName + "' is not an implementation of SpectrumStorageEngine!");
        }
        if (!iMascotDistillerProcessing) {
            String propsFile = null;
            // Check the properties file.
            propsFile = iSelectedInstrument.getPropertiesfilename();
            if (propsFile == null || propsFile.trim().equals("")) {
                passHotPotato(new SQLException("No properties file defined for the '" + iSelectedInstrument + "' instrument!", "Unable to load properties!"));
            }
            // Init the props file.
            this.iPropsFile = propsFile;
        }
        // Very well, all checks passed - set the spectrumstorage engine class.
        this.iEngine = (SpectrumStorageEngine) instance;
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
        fillProjectPulldown();
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
        closeConnection();
        this.setVisible(false);
        this.dispose();
        if (iStandAlone) {
            System.exit(0);
        }
    }
}
