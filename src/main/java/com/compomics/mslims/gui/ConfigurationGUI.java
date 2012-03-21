package com.compomics.mslims.gui;

import com.compomics.mascotdatfile.util.mascot.ModificationConversion;
import com.compomics.mslims.db.accessors.*;
import com.compomics.mslims.db.conversiontool.MS_LIMS_7_7_Data_Updater;
import com.compomics.mslims.util.fileio.ModificationConversionIO;
import com.compomics.mslims.util.mascot.MascotWebConnector.MascotAuthenticatedConnection;
import org.apache.log4j.Logger;

import com.compomics.mslims.db.conversiontool.DbConversionToolGuiEdition;
import com.compomics.mslims.db.conversiontool.MS_LIMS_6_Data_Updater;
import com.compomics.mslims.db.conversiontool.MS_LIMS_7_Data_Updater;
import com.compomics.mslims.db.factory.InstrumentFactory;
import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.io.PropertiesManager;
import com.compomics.util.sun.SwingWorker;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 28-okt-2008 Time: 9:59:16
 * <p/>
 * The 'ConfigurationGUI ' class was created for setting up and configuration of the ms_lims database scheme.
 */
public class ConfigurationGUI extends FlamableJFrame implements Connectable {
    // Class specific log4j logger for ConfigurationGUI instances.
    private static Logger logger = Logger.getLogger(ConfigurationGUI.class);
// ------------------------------ FIELDS ------------------------------

    private JPanel jpanContent;
    private JTabbedPane tabMain;
    private JPanel jpanSummary;
    private JPanel jpanDB;
    private JPanel jpanUsers;
    private JPanel jpanProtocol;
    private JPanel jpanInstrument;
    private JPanel jpanTop;
    private JPanel jpanBottom;
    private JButton btnConnect;
    private JButton btnCreateDatabase;
    private JTextField txtDatabaseName;
    private JButton btnSetSQLScheme;
    private JList listUsers;
    private JButton btnAddUser;
    private JTextField txtUser;
    private JButton btnRemoveUser;
    private JButton btnRemoveInstrument;
    private JButton btnAddInstrument;
    private JLabel lblConnectionStatus;
    private JLabel lblDatabaseScheme;
    private JList listProtocols;
    private JTextField txtProtocolDescription;
    private JButton btnAddProtocol;
    private JButton btnRemoveProtocol;
    private JList listAvailableInstruments;
    private JList listDatabaseInstruments;
    private JPanel jpanStatus;
    private JLabel lblStatus;
    private JTextField txtProtocolType;
    private JLabel lblSummaryConnection;
    private JLabel lblSummaryUser;
    private JLabel lblSummaryProtocol;
    private JLabel lblSummaryInstrument;
    private JPanel cdfUpdatePanel;
    private JPanel dataUpdatePanel;
    private JButton loadCdfButton;
    private JButton helpCdfButton;
    private JProgressBar progressBarCdf;
    private JButton startCdfButton;
    private JLabel lblFileName;
    private JLabel lblNoDbCdf;
    private JComboBox cmbDataUpdateTools;
    private JButton launchUpdateToolButton;
    private JTextField txtModification;
    private JTextField txtConversion;
    private JButton storeNewModificationButton;
    private JButton loadModificationsButton;
    private JPanel jpanMascot;
    private JCheckBox chkboxUseMascotHTTPDServer;
    private JTextField txtfieldMascotLoginName;
    private JPasswordField pwdfieldMascotServerPassword;
    private JTextField txtfieldMascotServerLocation;
    private JLabel lblUsername;
    private JLabel lblMascotServerTabDescriptor;
    private JLabel lblPassword;
    private JLabel lblMascotLocation;
    private JTextField txtfieldMascotServerPort;
    private JLabel lblMascotServerPort;
    private JTextField txtfieldMascotLocalLocation;
    private JLabel lblServerFilePath;
    private JButton btnSaveMascotServerSettings;
    private Properties lConnectionProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "IdentificationGUI.properties");

    private JFrame iFrame;
    private Connection iConnection;
    private String iDBName;
    private String iConnectionName;
    private final static Color iColor_ok = new Color(0, 100, 0);
    private static boolean iStandAlone = false;
    private boolean iDatabaseReady = false;
    /**
     * The cdf file
     */
    private File iCdfFile;
    /**
     * The cdf db conversion tool
     */
    private DbConversionToolGuiEdition iCdfUpdater;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Constructs a new ConfigurationGUI instancen,
     */
    public ConfigurationGUI(String aName) {
        this(aName, null, null);
    }

    public boolean isStandAlone() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ConfigurationGUI(String aName, Connection aConnection, String aDBName) {

        super(aName);

        iConnection = aConnection;


        iDBName = aDBName;

        $$$setupUI$$$();

        if (testConnection()) {
            iDatabaseReady = true;
        }

        setListeners();

        updateLists();

        this.getContentPane().add($$$getRootComponent$$$());
        if (lConnectionProperties.containsKey("usemascotauthentication")){
            if(lConnectionProperties.getProperty("usemascotauthentication").equals("1")){
                chkboxUseMascotHTTPDServer.setSelected(true);
                txtfieldMascotLoginName.setEnabled(true);
                txtfieldMascotServerLocation.setEnabled(true);
                pwdfieldMascotServerPassword.setEnabled(true);
                txtfieldMascotServerPort.setEnabled(true);
                txtfieldMascotLocalLocation.setEnabled(true);
                txtfieldMascotLocalLocation.setText(lConnectionProperties.getProperty("mascotserverlocallocation"));
                txtfieldMascotLoginName.setText(lConnectionProperties.getProperty("mascotloginname"));
                txtfieldMascotServerLocation.setText(lConnectionProperties.getProperty("mascotserverlocation"));
                txtfieldMascotServerPort.setText(lConnectionProperties.getProperty("mascotserverport"));
        }}
        super.setTitle(getDialogTitle(aDBName));

        this.pack();
        this.setLocation(100, 100);
        this.setSize(getWidth() + 100, getHeight());
        this.setVisible(true);

        if (iConnection == null) {
            /*   ConnectionDialog cd =
                    new ConnectionDialog(this, this, "Connection for ConfigurationGUI", "ConfigurationGUI.properties");
            cd.setVisible(true);*/
        } else {
            passConnection(iConnection, aDBName);
            lblNoDbCdf.setVisible(false);
        }
    }

    private String getDialogTitle(String aDBName) {
        return "ConfigurationGUI (managing ms_lims database" + aDBName + ")";
    }

    private boolean testConnection() {
        boolean lResult = false;
        if (iConnection != null) {
            String lQuery = "show tables like 'instrument'";
            PreparedStatement lPreparedStatement = null;
            try {
                lPreparedStatement = iConnection.prepareStatement(lQuery);
                ResultSet rs = lPreparedStatement.executeQuery();
                if (rs.next()) {
                    // The database connection has a table called instrument, seems OK!
                    lResult = true;
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return lResult;
    }

    /**
     * This method sets all the listeners upon construction.
     */
    private void setListeners() {

        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        // Connect to a MySQL database.
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Properties lConnectionProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "ms-lims.properties");
                ConnectionDialog lDialog =
                        new ConnectionDialog(iFrame, ConfigurationGUI.this, "Establish DB connection", lConnectionProperties);


                lDialog.setVisible(true);
            }
        });

        // Create a database.
        btnCreateDatabase.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                String aDatabaseName = txtDatabaseName.getText();
                try {
                    // Create the db,
                    execute("create database " + aDatabaseName);
                    // Use it!
                    execute("use " + aDatabaseName);
                    iDatabaseReady = true;
                    iConnectionName = iConnectionName + "/" + aDatabaseName;
                    lblSummaryConnection.setText(iConnectionName);
                    lblDatabaseScheme.setText(iConnectionName);
                    txtDatabaseName.setEditable(false);
                    txtDatabaseName.setForeground(iColor_ok);
                    btnCreateDatabase.setEnabled(false);
                    btnSetSQLScheme.setEnabled(true);
                    updateCurrentTab();
                    status("Created database '" + aDatabaseName + "'");
                    ConfigurationGUI.this.setTitle(getDialogTitle(iConnectionName));

                } catch (SQLException e1) {
                    if (e1.getMessage().indexOf("database exists") > 0) {
                        try {
                            execute("use " + aDatabaseName);
                            status("Database '" + aDatabaseName + "' in use.");
                            ConfigurationGUI.this.setTitle(getDialogTitle(iConnectionName));

                            iDatabaseReady = true;
                        } catch (SQLException e2) {
                            status("Failed to use database '" + aDatabaseName + "'!!");
                            logger.error(e2.getMessage(), e2);  //To change body of catch statement use File | Settings | File Templates.
                        }
                    } else {
                        logger.error(e1.getMessage(), e1);
                        status("Failed to create '" + aDatabaseName + "'");
                        iDatabaseReady = false;
                    }
                }
            }

        });

        // Set the database scheme to a database.
        btnSetSQLScheme.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                try {
                    String[] lDatabaseSchemeQueries = getQueriesForDatabaseScheme("projects.sql");

                    if (lDatabaseSchemeQueries != null) {
                        try {
                            for (int i = 0; i < lDatabaseSchemeQueries.length; i++) {
                                String lQuery = lDatabaseSchemeQueries[i];
                                execute(lQuery);
                            }
                            btnSetSQLScheme.setEnabled(false);
                            lblDatabaseScheme.setForeground(iColor_ok);
                            status("Set database scheme in '" + txtDatabaseName.getText() + "'");
                        } catch (SQLException e1) {
                            status("Failed to Set the database scheme!!");
                            logger.error("Failing!");
                            logger.error(e1.getMessage(), e1);
                        }
                    }

                    String[] lProjectAnalyzerToolQueries = getQueriesForDatabaseScheme("projectanalyzertool.sql");

                    if (lProjectAnalyzerToolQueries != null) {
                        try {
                            for (int i = 0; i < lProjectAnalyzerToolQueries.length; i++) {
                                String lQuery = lProjectAnalyzerToolQueries[i];
                                execute(lQuery);
                            }
                            btnSetSQLScheme.setEnabled(false);
                        } catch (SQLException e1) {
                            status("Failed to apply the projectanalyzer tools!!");
                            logger.error("Failing!");
                            logger.error(e1.getMessage(), e1);
                        }
                    }

                } catch (IOException e1) {
                    logger.error("Failing!");
                    logger.error(e1.getMessage(), e1);
                }
            }
        });

        /**
         * Set the textfield synchronous to selection events in the user list.
         */
        listUsers.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {
                if (listUsers.isSelectionEmpty()) {
                    txtUser.setText("");
                } else {
                    txtUser.setText(String.valueOf(listUsers.getSelectedValue()));
                }
            }
        });

        /**
         * Set the textfields synchronous to selection events in the protocol list.
         */
        listProtocols.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(final ListSelectionEvent e) {

                if (!listProtocols.isSelectionEmpty()) {
                    String s = (String.valueOf(listProtocols.getSelectedValue()));
                    String lType = s.substring(0, s.indexOf('-') - 1).trim();
                    String lDescription = s.substring(s.indexOf('-') + 1).trim();

                    txtProtocolType.setText(lType);
                    txtProtocolDescription.setText(lDescription);
                } else {
                    txtProtocolType.setText("");
                    txtProtocolDescription.setText("");
                }
            }
        });

        /**
         * Adds the new user into the ms_lims database scheme.
         */
        btnAddUser.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                String lUserName = txtUser.getText();
                if (!lUserName.equals("")) {
                    ResultSet rs = executeQuery("select * from user where name = '" + lUserName + "'");
                    try {
                        if (rs.next() == true) {
                            status("User '" + lUserName + "' is allready in the database!");
                        } else {
                            User lNewUser = new User(lUserName);
                            lNewUser.persist(iConnection);
                            status("Added user '" + lNewUser.getName() + "'.");

                        }
                    } catch (SQLException e1) {
                        logger.error("Failing!");
                        logger.error(e1.getMessage(), e1);
                    }
                }
                updateUserList();
            }
        });

        /**
         * Removes the selected user from the ms_lims database scheme.
         */
        btnRemoveUser.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                String lUserName = txtUser.getText();
                ResultSet rs = executeQuery("select * from user where name = '" + lUserName + "'");
                try {
                    if (rs.next() == false) {
                        status("User '" + lUserName + "' is not in the database!");
                    } else {
                        User lUser = new User(lUserName);
                        // Removal of a user relies on the user id, so we need to set this value as it occurs in the db.
                        int lUserID = rs.getInt("userid");
                        lUser.setUserid(lUserID);

                        lUser.delete(iConnection);
                        status("Deleted user '" + lUser.getName() + "'.");

                    }
                } catch (SQLException e1) {
                    logger.error("Failing!");
                    logger.error(e1.getMessage(), e1);
                }
                updateUserList();
            }
        });

        /**
         * Adds the new protocol into the ms_lims database scheme.
         */
        btnAddProtocol.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                String lProtocolDescription = txtProtocolDescription.getText();
                ResultSet rs =
                        executeQuery("select * from protocol where description = '" + lProtocolDescription + "'");
                try {
                    if (rs.next() == true) {
                        status("Protocol '" + lProtocolDescription + "' is allready in the database!");
                    } else {
                        Protocol lProtocolEntry = new Protocol();
                        lProtocolEntry.setDescription(txtProtocolDescription.getText());
                        lProtocolEntry.setType(txtProtocolType.getText());
                        lProtocolEntry.persist(iConnection);
                        status("Added protocol '" + lProtocolEntry.getType() + "'.");

                    }
                } catch (SQLException e1) {
                    logger.error("Failing!");
                    logger.error(e1.getMessage(), e1);
                }
                updateProtocolList();
            }
        });

        /**
         * Removes the selected protocol from the ms_lims database scheme.
         */
        btnRemoveProtocol.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                String lProtocolDescription = txtProtocolDescription.getText();
                ResultSet rs =
                        executeQuery("select * from protocol where description = '" + lProtocolDescription + "'");
                try {
                    if (rs.next() == false) {
                        status("Protocol '" + lProtocolDescription + "' is not in the database!");
                    } else {
                        Protocol lProtocolEntry = new Protocol();
                        // Removal of a protocol relies on the user id, so we need to set this value as it occurs in the db.
                        int lProtocolID = rs.getInt("protocolid");
                        lProtocolEntry.setProtocolid(lProtocolID);

                        lProtocolEntry.delete(iConnection);
                        status("Deleted protocol '" + lProtocolEntry.getType() + "'.");

                    }
                } catch (SQLException e1) {
                    logger.error("Failing!");
                    logger.error(e1.getMessage(), e1);
                }
                updateProtocolList();
            }
        });

        /**
         * Adds the selected Instrument from the 'Available' list, into the ms_lims database scheme.
         */
        btnAddInstrument.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Instrument lInstrument = ((Instrument) listAvailableInstruments.getSelectedValue());
                if (lInstrument != null) {
                    String lInstrumentName = lInstrument.getName();
                    ResultSet rs =
                            executeQuery("select * from instrument where name = '" + lInstrumentName + "'");
                    try {
                        if (rs.next() == true) {
                            status("Instrument '" + lInstrumentName + "' is allready in the database!");
                        } else {
                            lInstrument.persist(iConnection);
                            status("Added instrument '" + lInstrument.getName() + "'.");
                        }
                    } catch (SQLException e1) {
                        logger.error("Failing!");
                        logger.error(e1.getMessage(), e1);
                    }
                }
                updateInstrumentList();
            }
        });

        /**
         * Removes the selected Instrument from the ms_lims database scheme.
         */
        btnRemoveInstrument.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                Instrument lInstrument = ((Instrument) listDatabaseInstruments.getSelectedValue());
                if (lInstrument != null) {
                    String lInstrumentName = lInstrument.getName();
                    ResultSet rs =
                            executeQuery("select * from instrument where name = '" + lInstrumentName + "'");
                    try {
                        if (rs.next() == false) {
                            status("Instrument '" + lInstrumentName + "' is not in the database!");
                        } else {
                            status("Deleted instrument '" + ((Instrument) listDatabaseInstruments.getSelectedValue()).getName() + "'.");
                            ((Instrument) listDatabaseInstruments.getSelectedValue()).delete(iConnection);
                        }
                    } catch (SQLException e1) {
                        logger.error("Failing!");
                        logger.error(e1.getMessage(), e1);
                    }
                }
                updateInstrumentList();
            }
        });


        loadCdfButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //open file chooser
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new CdfFileFilter());
                fc.showOpenDialog(new JFrame());
                //get the selected file
                iCdfFile = fc.getSelectedFile();

                if (iCdfFile != null) {
                    //update the lblFileName
                    lblFileName.setVisible(true);
                    lblFileName.setText(iCdfFile.getName() + "  ");
                    //set the start button enabled
                    startCdfButton.setEnabled(true);
                    //load the updater
                    iCdfUpdater = new DbConversionToolGuiEdition(iConnection, iCdfFile);
                    //set the number of instructions found in the file
                    progressBarCdf.setString("Found " + iCdfUpdater.getNumberOfInstructions() + " instructions in the .cdf file.");
                }
            }
        });

        helpCdfButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showInBrowser("http://genesis.ugent.be/ms_lims/rdbms/conversion.html");
            }
        });

        startCdfButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final long startTime = System.currentTimeMillis();
                progressBarCdf.setIndeterminate(true);
                progressBarCdf.setStringPainted(false);
                SwingWorker lStarter = new SwingWorker() {

                    public Boolean construct() {
                        iCdfUpdater.doInstructions();
                        return true;
                    }

                    public void finished() {
                        progressBarCdf.setIndeterminate(false);
                        progressBarCdf.setStringPainted(true);
                        long endTime = System.currentTimeMillis();
                        // Transform time into seconds (with decimals).
                        long longDelta = endTime - startTime;
                        double doubleDelta = ((double) longDelta) / 1000.0;
                        BigDecimal bdDelta = new BigDecimal(doubleDelta).setScale(3, BigDecimal.ROUND_HALF_UP);
                        progressBarCdf.setString("Update process took " + bdDelta + " s.");
                        startCdfButton.setEnabled(false);
                    }
                };
                lStarter.start();
            }
        });

        launchUpdateToolButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String lSelectedTitle = (String) cmbDataUpdateTools.getSelectedItem();
                if (lSelectedTitle.equalsIgnoreCase("MS_LIMS_6_Data_Updater")) {
                    MS_LIMS_6_Data_Updater lUpdater = new MS_LIMS_6_Data_Updater(iConnection, iDBName);
                } else if (lSelectedTitle.equalsIgnoreCase("MS_LIMS_7_Data_Updater")) {
                    MS_LIMS_7_Data_Updater lUpdater = new MS_LIMS_7_Data_Updater(iConnection, iDBName);
                }  else if (lSelectedTitle.equalsIgnoreCase("MS_LIMS_7_7_Data_Updater")){
                    try{
                    MS_LIMS_7_7_Data_Updater lUpdater = new MS_LIMS_7_7_Data_Updater(iDBName,iConnection);
                    } catch (SQLException sqle){
                        logger.error(sqle);
                    } catch (ClassNotFoundException e1) {
                        logger.error(e1);
                    } catch (InstantiationException e1) {
                        logger.error(e1);
                    } catch (InterruptedException e1) {
                        logger.error(e1);
                    } catch (IllegalAccessException e1) {
                        logger.error(e1);
                    }

                }
            }
        });

        /**
         * Update the lists upon changing the tabs.
         */
        tabMain.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                Component c = tabMain.getSelectedComponent();
                if (c == jpanUsers) {
                    updateUserList();
                } else if (c == jpanInstrument) {
                    updateInstrumentList();
                } else if (c == jpanProtocol) {
                    updateProtocolList();
                } else if (c == jpanSummary) {
                    updateSummaryPanel();
                }
            }
        });
        storeNewModificationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (iConnection != null) {
                    try {
                        String lConversion = txtConversion.getText();
                        String lModification = txtModification.getText();
                        if (lModification.length() != 0) {
                            if (lConversion.length() != 0) {
                                HashMap lMap = new HashMap();
                                lMap.put(Modification_conversion.CONVERSION, lConversion);
                                lMap.put(Modification_conversion.MODIFICATION, lModification);
                                Modification_conversion lMC = new Modification_conversion(lMap);
                                lMC.persist(iConnection);
                                //store the new modification to a new file
                                ModificationConversionIO lLocalMC = new ModificationConversionIO();
                                int lNewestVersion = Ms_lims_properties.getModificationConversionVersion(iConnection);
                                lNewestVersion = lNewestVersion + 1;
                                try {
                                    lLocalMC.writeModificationConversionFile(lNewestVersion, Modification_conversion.getAllModificationConversions(iConnection));
                                    Ms_lims_properties.setModificationConversionVersion(iConnection, lNewestVersion);
                                } catch (SQLException e2) {
                                    logger.error(e2);
                                }
                                JOptionPane.showMessageDialog(new JFrame(), "The conversion was stored.", "Succes", JOptionPane.INFORMATION_MESSAGE);

                            } else {
                                JOptionPane.showMessageDialog(new JFrame(), "The conversion cannot be empty.", "No conversion", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(new JFrame(), "The modification cannot be empty.", "No modification", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException e1) {
                        JOptionPane.showMessageDialog(new JFrame(), "An error occured while storing your modification conversion\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "There is no connection to the ms_lims database.", "No connection", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        loadModificationsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (iConnection != null) {
                    int answer = JOptionPane.showConfirmDialog(new JFrame(), "This will delete all existing modification conversions from the database.\nAll the modification conversions in the modificationConverstion.txt file will be stored in the db.\nAre you sure that you want to continue?", "Load modification conversions from file ", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {

                        try {
                            //1.truncate the modification conversion table
                            PreparedStatement lPreparedStatement = iConnection.prepareStatement("truncate table modification_conversion");
                            lPreparedStatement.execute();
                            //2.store new data
                            ModificationConversion lmc = ModificationConversion.getInstance();
                            HashMap lMap = lmc.getConversionMap();
                            Iterator iterator = lMap.keySet().iterator();
                            int lCounterOk = 0;
                            int lCounterNotOk = 0;
                            String lErrorString = "";

                            while (iterator.hasNext()) {
                                String lModi = iterator.next().toString();
                                String lConversion = lMap.get(lModi).toString();
                                HashMap lPersistMap = new HashMap();
                                lPersistMap.put(Modification_conversion.CONVERSION, lConversion);
                                lPersistMap.put(Modification_conversion.MODIFICATION, lModi);
                                Modification_conversion lMC = new Modification_conversion(lPersistMap);
                                try {
                                    lMC.persist(iConnection);
                                    lCounterOk = lCounterOk + 1;
                                } catch (SQLException e2) {
                                    lCounterNotOk = lCounterNotOk + 1;
                                    lErrorString = lErrorString + "\n" + e2.getMessage();
                                    logger.error(e2);
                                }
                            }
                            //3.store new local file
                            ModificationConversionIO lLocalMC = new ModificationConversionIO();
                            int lNewestVersion = Ms_lims_properties.getModificationConversionVersion(iConnection);
                            lNewestVersion = lNewestVersion + 1;
                            try {
                                lLocalMC.writeModificationConversionFile(lNewestVersion, Modification_conversion.getAllModificationConversions(iConnection));
                                Ms_lims_properties.setModificationConversionVersion(iConnection, lNewestVersion);
                            } catch (SQLException e2) {
                                logger.error(e2);
                            }
                            if (lCounterNotOk == 0) {
                                JOptionPane.showMessageDialog(new JFrame(), "The" + lCounterOk + " conversions were stored.", "Succes", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(new JFrame(), lCounterOk + " conversions were stored.\n" + lCounterNotOk + " conversions not stored!" + lErrorString, "Stored with errors!!!", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (SQLException e1) {
                            logger.error(e1);
                        }


                    } else {
                        //do nothing
                    }
                }
            }
        });
        chkboxUseMascotHTTPDServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (chkboxUseMascotHTTPDServer.isSelected()){
                    txtfieldMascotLoginName.setEnabled(true);
                    txtfieldMascotServerLocation.setEnabled(true);
                    pwdfieldMascotServerPassword.setEnabled(true);
                    txtfieldMascotServerPort.setEnabled(true);
                    txtfieldMascotLocalLocation.setEnabled(true);
                }
                else if (!chkboxUseMascotHTTPDServer.isSelected()){
                    txtfieldMascotLoginName.setEnabled(false);
                    txtfieldMascotServerLocation.setEnabled(false);
                    pwdfieldMascotServerPassword.setEnabled(false);
                    txtfieldMascotServerPort.setEnabled(false);
                    txtfieldMascotLocalLocation.setEnabled(false);
                    lConnectionProperties.put("usemascotauthentication","0");
                    PropertiesManager.getInstance().updateProperties(CompomicsTools.MSLIMS,"IdentificationGUI.properties",lConnectionProperties);
                }
            }
        });

        btnSaveMascotServerSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (chkboxUseMascotHTTPDServer.isSelected()) {
                    if (txtfieldMascotLoginName.getText() != "" && txtfieldMascotServerLocation.getText() != "" && txtfieldMascotLocalLocation.getText() != ""){
                        lConnectionProperties.put("mascotloginname",txtfieldMascotLoginName.getText());
                        lConnectionProperties.put("mascotserverlocation",txtfieldMascotServerLocation.getText());
                        lConnectionProperties.put("mascotserverpassword", pwdfieldMascotServerPassword.getText());
                        lConnectionProperties.put("mascotserverport",txtfieldMascotServerPort.getText());
                        lConnectionProperties.put("mascotserverlocallocation",txtfieldMascotLocalLocation.getText());
                        lConnectionProperties.put("usemascotauthentication","1");
                        PropertiesManager.getInstance().updateProperties(CompomicsTools.MSLIMS,"IdentificationGUI.properties",lConnectionProperties);
                        Properties lConnectionProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "IdentificationGUI.properties");
                        if(lConnectionProperties.containsKey("usemascotauthentication")){
                            if (lConnectionProperties.getProperty("usemascotauthentication").equals("1")) {
                                MascotAuthenticatedConnection mscConnector = new MascotAuthenticatedConnection();
                                if(!mscConnector.areCredentialsPresent()){
                                    try {
                                        mscConnector.login();
                                    } catch (IOException e) {
                                        JOptionPane.showMessageDialog(null,"there has been a problem with logging into the mascot server");
                                        logger.error(e);
                                    }
                                    JOptionPane.showMessageDialog(null,"your settings have been saved and connection has been made to the mascot server");
                                }
                            }
                        }
                        } else {
                        JOptionPane.showMessageDialog(null,"please enter all the needed data.");
                    }
                } else {
                    lConnectionProperties.put("usemascotauthentication","0");
                    PropertiesManager.getInstance().updateProperties(CompomicsTools.MSLIMS,"IdentificationGUI.properties",lConnectionProperties);
                }
            }
        });
    }

    /**
     * Gets the selected tab and calls validate and repaint functions.
     */
    private void updateCurrentTab() {
        tabMain.getSelectedComponent().validate();
        tabMain.getSelectedComponent().repaint();
    }

    /**
     * Returns all queries to construct the ms_lims database scheme.
     *
     * @return String[] The Queries to construct the ms_lims database scheme.
     * @throws java.io.IOException
     */
    private String[] getQueriesForDatabaseScheme(String aFileName) throws IOException {
        // find the 'projects.sql' from the classpath.

        InputStream is = ClassLoader.getSystemResourceAsStream(aFileName);

        if (is == null) {
            is = this.getClass().getClassLoader().getResourceAsStream(aFileName);
            if (is == null) {
                // Leave it at that.
                status("Failed to locate " + aFileName + " in the classpath.\nDatabase scheme was not created.");
                return null;
            }
        }

        StringBuffer sb = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = reader.readLine()) != null) {
            // Get rid of comment and empty lines.
            if (line.indexOf('-') == 0 || line.indexOf('/') == 0) {
                continue;
            }
            if (line.length() == 0) {
                continue;
            }

            sb.append(line);
        }
        // Add the final query.
        StringTokenizer st = new StringTokenizer(sb.toString(), ";");
        String[] lResult = new String[st.countTokens()];

        for (int i = 0; i < lResult.length; i++) {
            lResult[i] = st.nextToken();
        }

        return lResult;
    }

    /**
     * This method performs a Query on the instance Connection without feedback. (database creation is an example)
     *
     * @param aQuery The String to be queried.
     * @throws java.sql.SQLException
     */
    private void execute(final String aQuery) throws SQLException {
        PreparedStatement lPreparedStatement =
                iConnection.prepareStatement(aQuery);
        lPreparedStatement.execute();
    }

    /**
     * Sets a message to the statuspanel in the bottom.
     *
     * @param aString The message.
     */
    public void status(String aString) {
        lblStatus.setText(aString);
        jpanStatus.repaint();
    }

    /**
     * Updates the information on the summary panel.
     */
    private void updateSummaryPanel() {
        if (iConnection != null) {
            lblDatabaseScheme.setText(iConnectionName);
            lblSummaryUser.setText(listUsers.getModel().getSize() + " users");
            lblSummaryProtocol.setText(listProtocols.getModel().getSize() + " protocols");
            lblSummaryInstrument.setText(listDatabaseInstruments.getModel().getSize() + " instruments");
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        jpanContent = new JPanel();
        jpanContent.setLayout(new GridBagLayout());
        tabMain = new JTabbedPane();
        tabMain.setTabLayoutPolicy(1);
        tabMain.setTabPlacement(1);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.01;
        gbc.weighty = 0.01;
        gbc.fill = GridBagConstraints.BOTH;
        jpanContent.add(tabMain, gbc);
        jpanSummary = new JPanel();
        jpanSummary.setLayout(new GridBagLayout());
        tabMain.addTab("Summary", jpanSummary);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanSummary.add(spacer1, gbc);
        jpanTop = new JPanel();
        jpanTop.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanSummary.add(jpanTop, gbc);
        jpanBottom = new JPanel();
        jpanBottom.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        jpanSummary.add(jpanBottom, gbc);
        jpanBottom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), ""));
        final JLabel label1 = new JLabel();
        label1.setText("Connected to:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanBottom.add(label1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanBottom.add(spacer2, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Users:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanBottom.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Protocols:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanBottom.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Instruments:");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanBottom.add(label4, gbc);
        lblSummaryConnection = new JLabel();
        lblSummaryConnection.setText("Database connection message");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanBottom.add(lblSummaryConnection, gbc);
        lblSummaryUser = new JLabel();
        lblSummaryUser.setText("Number of users");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanBottom.add(lblSummaryUser, gbc);
        lblSummaryProtocol = new JLabel();
        lblSummaryProtocol.setText("Number of protocols");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanBottom.add(lblSummaryProtocol, gbc);
        lblSummaryInstrument = new JLabel();
        lblSummaryInstrument.setText("Number of instruments");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanBottom.add(lblSummaryInstrument, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanSummary.add(spacer3, gbc);
        final JPanel spacer4 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 4;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanSummary.add(spacer4, gbc);
        final JPanel spacer5 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanSummary.add(spacer5, gbc);
        jpanDB = new JPanel();
        jpanDB.setLayout(new GridBagLayout());
        tabMain.addTab("Database", jpanDB);
        final JPanel spacer6 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanDB.add(spacer6, gbc);
        btnCreateDatabase = new JButton();
        btnCreateDatabase.setEnabled(false);
        btnCreateDatabase.setHorizontalAlignment(0);
        btnCreateDatabase.setText("Create/Use SQL Database");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanDB.add(btnCreateDatabase, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Enter a database name:");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanDB.add(label5, gbc);
        btnSetSQLScheme = new JButton();
        btnSetSQLScheme.setEnabled(false);
        btnSetSQLScheme.setHorizontalAlignment(0);
        btnSetSQLScheme.setText("Set  SQL Scheme for ms_lims");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanDB.add(btnSetSQLScheme, gbc);
        txtDatabaseName = new JTextField();
        txtDatabaseName.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanDB.add(txtDatabaseName, gbc);
        btnConnect = new JButton();
        btnConnect.setHorizontalAlignment(0);
        btnConnect.setText("Connect");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanDB.add(btnConnect, gbc);
        lblConnectionStatus = new JLabel();
        lblConnectionStatus.setText("No connection");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanDB.add(lblConnectionStatus, gbc);
        final JPanel spacer7 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanDB.add(spacer7, gbc);
        final JPanel spacer8 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 1;
        gbc.gridheight = 5;
        gbc.weightx = 0.05;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanDB.add(spacer8, gbc);
        final JPanel spacer9 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanDB.add(spacer9, gbc);
        final JPanel spacer10 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanDB.add(spacer10, gbc);
        lblDatabaseScheme = new JLabel();
        lblDatabaseScheme.setText("No scheme");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanDB.add(lblDatabaseScheme, gbc);
        final JPanel spacer11 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 6;
        gbc.gridwidth = 6;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanDB.add(spacer11, gbc);
        final JPanel spacer12 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanDB.add(spacer12, gbc);
        final JPanel spacer13 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 8;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanDB.add(spacer13, gbc);
        jpanUsers = new JPanel();
        jpanUsers.setLayout(new GridBagLayout());
        tabMain.addTab("Users", jpanUsers);
        final JPanel spacer14 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanUsers.add(spacer14, gbc);
        btnAddUser = new JButton();
        btnAddUser.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanUsers.add(btnAddUser, gbc);
        txtUser = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanUsers.add(txtUser, gbc);
        btnRemoveUser = new JButton();
        btnRemoveUser.setText("Remove");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanUsers.add(btnRemoveUser, gbc);
        final JPanel spacer15 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weightx = 0.5;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanUsers.add(spacer15, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanUsers.add(panel1, gbc);
        panel1.setBorder(BorderFactory.createTitledBorder("Current users"));
        listUsers = new JList();
        listUsers.setLayoutOrientation(0);
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        defaultListModel1.addElement("An");
        defaultListModel1.addElement("Evy");
        defaultListModel1.addElement("Kenny");
        listUsers.setModel(defaultListModel1);
        listUsers.setSelectionMode(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel1.add(listUsers, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanUsers.add(label6, gbc);
        jpanProtocol = new JPanel();
        jpanProtocol.setLayout(new GridBagLayout());
        tabMain.addTab("Protocol", jpanProtocol);
        txtProtocolDescription = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanProtocol.add(txtProtocolDescription, gbc);
        btnRemoveProtocol = new JButton();
        btnRemoveProtocol.setText("Remove");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanProtocol.add(btnRemoveProtocol, gbc);
        final JPanel spacer16 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 7;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanProtocol.add(spacer16, gbc);
        final JPanel spacer17 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanProtocol.add(spacer17, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 6;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanProtocol.add(panel2, gbc);
        panel2.setBorder(BorderFactory.createTitledBorder("Current protocols"));
        listProtocols = new JList();
        listProtocols.setLayoutOrientation(0);
        final DefaultListModel defaultListModel2 = new DefaultListModel();
        defaultListModel2.addElement("An");
        defaultListModel2.addElement("Evy");
        defaultListModel2.addElement("Kenny");
        listProtocols.setModel(defaultListModel2);
        listProtocols.setSelectionMode(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel2.add(listProtocols, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("Description");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanProtocol.add(label7, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanProtocol.add(label8, gbc);
        btnAddProtocol = new JButton();
        btnAddProtocol.setText("Add");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanProtocol.add(btnAddProtocol, gbc);
        txtProtocolType = new JTextField();
        txtProtocolType.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanProtocol.add(txtProtocolType, gbc);
        jpanInstrument = new JPanel();
        jpanInstrument.setLayout(new GridBagLayout());
        tabMain.addTab("Instrument", jpanInstrument);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 9;
        gbc.gridheight = 5;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanInstrument.add(panel3, gbc);
        panel3.setBorder(BorderFactory.createTitledBorder("Available Instruments"));
        listAvailableInstruments = new JList();
        listAvailableInstruments.setLayoutOrientation(0);
        final DefaultListModel defaultListModel3 = new DefaultListModel();
        listAvailableInstruments.setModel(defaultListModel3);
        listAvailableInstruments.setSelectionMode(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel3.add(listAvailableInstruments, gbc);
        final JPanel spacer18 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanInstrument.add(spacer18, gbc);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 10;
        gbc.gridy = 0;
        gbc.gridwidth = 9;
        gbc.gridheight = 5;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        jpanInstrument.add(panel4, gbc);
        panel4.setBorder(BorderFactory.createTitledBorder("Database Instruments"));
        listDatabaseInstruments = new JList();
        listDatabaseInstruments.setLayoutOrientation(0);
        final DefaultListModel defaultListModel4 = new DefaultListModel();
        defaultListModel4.addElement("An");
        defaultListModel4.addElement("Evy");
        defaultListModel4.addElement("Kenny");
        listDatabaseInstruments.setModel(defaultListModel4);
        listDatabaseInstruments.setSelectionMode(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel4.add(listDatabaseInstruments, gbc);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 9;
        gbc.gridy = 3;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        jpanInstrument.add(panel5, gbc);
        btnAddInstrument = new JButton();
        btnAddInstrument.setText("--->");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel5.add(btnAddInstrument, gbc);
        final JPanel spacer19 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(spacer19, gbc);
        final JPanel spacer20 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel5.add(spacer20, gbc);
        btnRemoveInstrument = new JButton();
        btnRemoveInstrument.setText("<---");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 2;
        gbc.ipady = 2;
        gbc.insets = new Insets(3, 3, 3, 3);
        panel5.add(btnRemoveInstrument, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        tabMain.addTab("Update", panel6);
        cdfUpdatePanel = new JPanel();
        cdfUpdatePanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(cdfUpdatePanel, gbc);
        cdfUpdatePanel.setBorder(BorderFactory.createTitledBorder("Update ms_lims database schema"));
        final JLabel label9 = new JLabel();
        label9.setText("Load a .cdf (conversion definition files) file");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        cdfUpdatePanel.add(label9, gbc);
        loadCdfButton = new JButton();
        loadCdfButton.setText("Load");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        cdfUpdatePanel.add(loadCdfButton, gbc);
        helpCdfButton = new JButton();
        helpCdfButton.setText("?");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        cdfUpdatePanel.add(helpCdfButton, gbc);
        lblFileName = new JLabel();
        lblFileName.setFont(new Font(lblFileName.getFont().getName(), Font.ITALIC, lblFileName.getFont().getSize()));
        lblFileName.setHorizontalAlignment(11);
        lblFileName.setText("Label");
        lblFileName.setVisible(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 40;
        gbc.insets = new Insets(5, 5, 5, 5);
        cdfUpdatePanel.add(lblFileName, gbc);
        progressBarCdf = new JProgressBar();
        progressBarCdf.setString("No .cdf file loaded");
        progressBarCdf.setStringPainted(true);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        cdfUpdatePanel.add(progressBarCdf, gbc);
        startCdfButton = new JButton();
        startCdfButton.setEnabled(false);
        startCdfButton.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        cdfUpdatePanel.add(startCdfButton, gbc);
        lblNoDbCdf = new JLabel();
        lblNoDbCdf.setFont(new Font(lblNoDbCdf.getFont().getName(), Font.BOLD, 14));
        lblNoDbCdf.setForeground(new Color(-65536));
        lblNoDbCdf.setHorizontalAlignment(0);
        lblNoDbCdf.setHorizontalTextPosition(0);
        lblNoDbCdf.setText("Make a connection to a ms_lims database!");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        cdfUpdatePanel.add(lblNoDbCdf, gbc);
        final JPanel spacer21 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        cdfUpdatePanel.add(spacer21, gbc);
        dataUpdatePanel = new JPanel();
        dataUpdatePanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(dataUpdatePanel, gbc);
        dataUpdatePanel.setBorder(BorderFactory.createTitledBorder("Update the data in the ms_lims database"));
        final JLabel label10 = new JLabel();
        label10.setText("Select a data update tool");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        dataUpdatePanel.add(label10, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        dataUpdatePanel.add(cmbDataUpdateTools, gbc);
        launchUpdateToolButton = new JButton();
        launchUpdateToolButton.setText("Launch update tool");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        dataUpdatePanel.add(launchUpdateToolButton, gbc);
        final JPanel spacer22 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dataUpdatePanel.add(spacer22, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridBagLayout());
        tabMain.addTab("Modification conversion", panel7);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel7.add(panel8, gbc);
        panel8.setBorder(BorderFactory.createTitledBorder(null, "Add a modification conversion", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Tahoma", panel8.getFont().getStyle(), 16)));
        final JLabel label11 = new JLabel();
        label11.setHorizontalAlignment(0);
        label11.setHorizontalTextPosition(0);
        label11.setText("Modification");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel8.add(label11, gbc);
        final JLabel label12 = new JLabel();
        label12.setHorizontalAlignment(0);
        label12.setHorizontalTextPosition(0);
        label12.setText("Conversion");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel8.add(label12, gbc);
        txtModification = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel8.add(txtModification, gbc);
        txtConversion = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel8.add(txtConversion, gbc);
        storeNewModificationButton = new JButton();
        storeNewModificationButton.setText("Store");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel8.add(storeNewModificationButton, gbc);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel7.add(panel9, gbc);
        panel9.setBorder(BorderFactory.createTitledBorder(null, "Load modification conversions", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Tahoma", panel9.getFont().getStyle(), 16)));
        loadModificationsButton = new JButton();
        loadModificationsButton.setText("Load");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel9.add(loadModificationsButton, gbc);
        jpanStatus = new JPanel();
        jpanStatus.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        jpanContent.add(jpanStatus, gbc);
        jpanStatus.setBorder(BorderFactory.createTitledBorder(null, "Status", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font(jpanStatus.getFont().getName(), jpanStatus.getFont().getStyle(), 10), new Color(-11711412)));
        final JPanel spacer23 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        jpanStatus.add(spacer23, gbc);
        lblStatus = new JLabel();
        lblStatus.setFont(new Font(lblStatus.getFont().getName(), lblStatus.getFont().getStyle(), 10));
        lblStatus.setForeground(new Color(-11842741));
        lblStatus.setText("Browse the tabs on the left side to configure the ms_lims database");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.ipadx = 3;
        gbc.ipady = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanStatus.add(lblStatus, gbc);
        final JPanel spacer24 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanStatus.add(spacer24, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jpanContent;
    }

    /**
     * A .cdf file filter
     */
    class CdfFileFilter extends FileFilter {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".cdf");
        }

        public String getDescription() {
            return ".cdf files";
        }
    }

    /**
     * Updates all the lists in the tool.
     */
    private void updateLists() {
        updateUserList();
        updateInstrumentList();
        updateProtocolList();
        updateSummaryPanel();
    }

    /**
     * Updates the users list.
     */
    private void updateUserList() {
        try {
            if (iConnection == null | !iDatabaseReady) {
                listUsers.setListData(new String[]{"No connection, no users!"});
            } else {
                ResultSet rs = executeQuery("Select * from user");
                Vector lUsers = new Vector();
                while (rs.next()) {
                    lUsers.add(rs.getString("name"));
                }
                listUsers.setListData(lUsers);

            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
        listUsers.repaint();
    }

    /**
     * This method performs a Query on the instance Connection.
     *
     * @param aQuery The String to be queried.
     * @return The ResultSet of the Query.
     */
    private ResultSet executeQuery(final String aQuery) {
        ResultSet rs = null;
        try {
            PreparedStatement lPreparedStatement =
                    iConnection.prepareStatement(aQuery);
            rs = lPreparedStatement.executeQuery();
        } catch (SQLException e) {
            logger.error("Failing!");
            logger.error(e.getMessage(), e);
        }

        return rs;
    }

    /**
     * updates the instrument list.
     */
    private void updateInstrumentList() {
        listAvailableInstruments.setListData(InstrumentFactory.createAllInstruments());

        try {

            if (iConnection == null | !iDatabaseReady) {
                listDatabaseInstruments.setListData(new String[]{"No connection, no instruments!"});
            } else {
                ResultSet rs = executeQuery("Select * from instrument");
                Vector lInstruments = new Vector();
                while (rs.next()) {
                    lInstruments.add(new Instrument(rs));
                }

                if (lInstruments.size() == 0) {
                    listDatabaseInstruments.setListData(new String[]{"No instruments!"});
                }

                listDatabaseInstruments.setListData(lInstruments);
            }

        } catch (SQLException e) {
            logger.error("Failing upon updating the instrument list!");
            logger.error(e.getMessage(), e);
        }

        listDatabaseInstruments.setSize(listAvailableInstruments.getSize());
        listAvailableInstruments.repaint();
        listDatabaseInstruments.repaint();
    }

    /**
     * Updates the protocol list.
     */
    private void updateProtocolList() {
        try {
            if (iConnection == null | !iDatabaseReady) {
                listProtocols.setListData(new String[]{"No connection, no protocols!"});
            } else {
                ResultSet rs = executeQuery("Select * from protocol");
                Vector lProtocols = new Vector();
                while (rs.next()) {
                    lProtocols.add(rs.getString("type") + " - " + rs.getString("description"));
                }
                listProtocols.setListData(lProtocols);
            }
        } catch (SQLException e) {
            logger.error("Failing!");
            logger.error(e.getMessage(), e);
        }
        listProtocols.repaint();
    }

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface Connectable ---------------------

    /**
     * This method accepts an incoming connection to perform all database queries on.
     *
     * @param aConn           Connection on which to perform the queries.
     * @param aConnectionName String with the name of the DB (for display purposes).
     */
    public void passConnection(Connection aConn, String aConnectionName) {
        if (aConn == null) {
            status("Connection to '" + aConnectionName + "' failed!!");
        } else {
            this.iConnection = aConn;
            this.iConnectionName = aConnectionName;
            this.lblSummaryConnection.setText(aConnectionName);
            lblConnectionStatus.setText(aConnectionName);
            lblConnectionStatus.setForeground(iColor_ok);
            btnConnect.setEnabled(false);
            btnCreateDatabase.setEnabled(true);
            status("Connection made to '" + aConnectionName + "'.");
            updateLists();
            lblNoDbCdf.setVisible(false);
        }
    }

    /**
     * This method is called when the frame is closed. It shuts down the JVM.
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
     * This method should be called when the application is not launched in stand-alone mode.
     */
    public static void setNotStandAlone() {
        iStandAlone = false;
    }


    private void createUIComponents() {

        //A string array with the different data update options
        String[] lDataUpdaters = new String[]{"MS_LIMS_6_Data_Updater", "MS_LIMS_7_Data_Updater","MS_LIMS_7_7_Data_Updater"};
        cmbDataUpdateTools = new JComboBox(lDataUpdaters);
    }

    /**
     * This method opens the default browser on a given webpage
     *
     * @param url String with the url
     * @return boolean False if an error occured
     */
    private boolean showInBrowser(String url) {

        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        try {
            if (os.indexOf("win") >= 0) {
                String[] cmd = new String[4];
                cmd[0] = "cmd.exe";
                cmd[1] = "/C";
                cmd[2] = "start";
                cmd[3] = url;
                rt.exec(cmd);
            } else if (os.indexOf("mac") >= 0) {
                rt.exec("open " + url);
            } else {
                //prioritized 'guess' of users' preference
                String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                        "netscape", "opera", "links", "lynx"};

                StringBuffer cmd = new StringBuffer();
                for (int i = 0; i < browsers.length; i++)
                    cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");

                rt.exec(new String[]{"sh", "-c", cmd.toString()});
                //rt.exec("firefox http://www.google.com");
                //System.out.println(cmd.toString());

            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            JOptionPane.showMessageDialog(new JFrame(), "\n\n The system failed to invoke your default web browser while attempting to access: \n\n " + url + "\n\n", "Browser Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }


// -------------------------- OTHER METHODS --------------------------

    // --------------------------- main() method ---------------------------

    /**
     * Main method starts a JFrame with the ConfigurationGUI.
     *
     * @param args none.
     */
    public static void main(final String[] args) {
        new ConfigurationGUI("ConfigurationGUI");

    }

}
