/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 08-Mar-2007
 * Time: 09:27:03
 */
package com.compomics.mslims.gui;

import com.compomics.mslims.gui.dialogs.AboutDialog;
import com.compomics.mslims.gui.dialogs.CustomLauncherDialog;
import com.compomics.mslims.gui.quantitation.QuantitationTypeChooser;
import be.proteomics.mat.gui.PeptizerGUI;
import be.proteomics.mat.gui.dialog.CreateTaskDialog;
import be.proteomics.mat.util.fileio.ConnectionManager;
import com.compomics.rover.gui.wizard.WizardFrameHolder;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.util.interfaces.Connectable;
import com.jgoodies.looks.FontPolicies;
import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import com.jgoodies.looks.FontSets;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.Silver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
/*
 * CVS information:
 *
 * $Revision: 1.20 $
 * $Date: 2009/12/17 14:08:39 $
 */

/**
 * This class presents a main GUI for the whole ms_lims suite of
 * applications.
 *
 * @author Thilo Muth
 * @author Lennart Martens
 * @version $Id: MS_LIMS.java,v 1.20 2009/12/17 14:08:39 kenny Exp $
 */
public class MS_LIMS extends JFrame implements Connectable {

    /**
     * The Database connection to forward to launched components.
     */
    private Connection iConn = null;

    /**
     * The name of the DB connection.
     */
    private String iDBName = null;

    // GUI variables.
    private JButton projectManagerBtn = new JButton();
    private JButton spectrumStorageBtn = new JButton();
    private JButton mergerBtn = new JButton();
    private JButton mascotBtn = new JButton();
    private JButton identificationBtn = new JButton();
    private JButton quantitationBtn = new JButton();
    private JButton peptizerBtn = new JButton();
    private JButton genericQueryBtn = new JButton();
    private JButton storeBinaryFileBtn = new JButton();
    private JButton projectAnalyzerBtn = new JButton();
    private JButton quantitationValidationBtn = new JButton();
    private JButton customBtn = new JButton();

    private JButton exitBtn = new JButton();
    private JLabel projectManagerLbl = new JLabel();
    private JLabel spectrumStorageLbl = new JLabel();
    private JLabel spectrumStorageLbl2 = new JLabel();
    private JLabel mergerLbl = new JLabel();
    private JLabel mergerLbl2 = new JLabel();
    private JLabel identificationLbl = new JLabel();
    private JLabel quantitationLbl = new JLabel();
    private JLabel peptizerBtnLbl = new JLabel();
    private JLabel genericQueryLbl = new JLabel();
    private JLabel storeBinaryFileLbl = new JLabel();
    private JLabel projectAnalyzerLbl = new JLabel();
    private JLabel quantitationValidationLbl = new JLabel();
    String curDir;
    private JMenuBar menuBar;
    private JMenu menu, submenu;
    private JMenuItem menuItem;
    // Create a file chooser
    JFrame frame = new JFrame();
    String filename = System.getProperty("user.dir") + File.separator;//File.separator+"tmp";
    JFileChooser fc = new JFileChooser(new File(filename));
    Action openAction = new OpenFileAction(frame, fc);
    private JButton sourceBrow = new JButton(openAction); // JButton opens the action
    JLabel sourceLabel = new JLabel("Source: ");
    private JTextField sourceField = new JTextField(12);
    String sourceName;


    /**
     * This constructor takes the title of the frame.
     *
     * @param title String with the name of the frame.
     */
    public MS_LIMS(String title) {
        // Initialize frame.
        super(title);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                close();
            }
        });
        int frameWidth = 600;
        int frameHeight = 670;
        setSize(frameWidth, frameHeight);

        //Before we start we will delete all the files and folders in temp/mslims. Files could 
        //still be there if the quantitation storage gui was not quited correctly.
        try {
            File lTempfolder = File.createTempFile("temp", "temp").getParentFile();
            File lTempRovFolder = new File(lTempfolder, "mslims");

            if (lTempRovFolder.exists() == true) {
                deleteDir(lTempRovFolder);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        Container cp = getContentPane();
        cp.setLayout(null);
        curDir = System.getProperty("user.dir") + File.separator;

        // Look n feel.
        try {
            FontSet fontSet = FontSets.createDefaultFontSet(
                    new Font("Tahoma", Font.PLAIN, 11),    // control font
                    new Font("Tahoma", Font.PLAIN, 11),    // menu font
                    new Font("Tahoma", Font.BOLD, 11)     // title font
            );
            FontPolicy fixedPolicy = FontPolicies.createFixedPolicy(fontSet);
            PlasticLookAndFeel.setFontPolicy(fixedPolicy);
            PlasticLookAndFeel.setPlasticTheme(new Silver());
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // GUI components.
        // menuBar at the top of the GUI.
        menuBar = new JMenuBar();
        // Build the first menu.
        menu = new JMenu("Menu");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        // submenu
        menu.addSeparator();
        submenu = new JMenu("Start");
        submenu.setMnemonic(KeyEvent.VK_S);
        menu.add(submenu);
        menuItem = new JMenuItem("ProjectManager");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                projectManagerBtnActionPerformed(evt);
            }
        });
        submenu.add(menuItem);

        menuItem = new JMenuItem("Spectrum Storage");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                spectrumStorageBtnActionPerformed(evt);
            }
        });
        submenu.add(menuItem);

        menuItem = new JMenuItem("MergerGUI");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_3, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mergerBtnActionPerformed(evt);
            }
        });
        submenu.add(menuItem);

        menuItem = new JMenuItem("IdentificationGUI");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_4, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                identificationBtnActionPerformed(evt);
            }
        });
        submenu.add(menuItem);

        menuItem = new JMenuItem("QuantitatinGUI");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_5, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                identificationBtnActionPerformed(evt);
            }
        });
        submenu.add(menuItem);

        menuItem = new JMenuItem("GenericQuery");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_6, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                genericQueryBtnActionPerformed(evt);
            }
        });
        submenu.add(menuItem);

        menuItem = new JMenuItem("Store Binary File(s)");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_7, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                storeBinaryFileBtnActionPerformed(evt);
            }
        });
        submenu.add(menuItem);

        menuItem = new JMenuItem("ProjectAnalyzer");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_8, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                projectAnalyzerBtnActionPerformed(evt);
            }
        });
        submenu.add(menuItem);


        menuItem = new JMenuItem("Database Configuration", KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F2, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Configure the ms_lims database");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                configurationMenuActionPerformed();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "Exit the Ms_Lims GUI");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });
        menu.add(menuItem);

        menu = new JMenu("Mascot");
        menu.setMnemonic(KeyEvent.VK_C);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu opens the Mascot daemon");
        menuItem = new JMenuItem("Start Daemon");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_M, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mascotBtnActionPerformed(evt);
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);

        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription(
                "Help menu");
        menuItem = new JMenuItem("About");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_A, ActionEvent.ALT_MASK));
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                AboutDialog ad = new AboutDialog(MS_LIMS.this, "About ms_lims");
                ad.setLocation(MS_LIMS.this.getLocation());
                ad.setVisible(true);
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);

        // Buttons
        projectManagerBtn.setBounds(40, 20, 150, 40);
        projectManagerBtn.setText("ProjectManager");
        cp.add(projectManagerBtn);
        projectManagerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                projectManagerBtnActionPerformed(evt);
            }
        });

        spectrumStorageBtn.setBounds(40, 70, 150, 40);
        spectrumStorageBtn.setText("Spectrum Storage");
        cp.add(spectrumStorageBtn);
        spectrumStorageBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                spectrumStorageBtnActionPerformed(evt);
            }
        });

        mergerBtn.setBounds(40, 120, 150, 40);
        mergerBtn.setText("MergerGUI");
        cp.add(mergerBtn);
        mergerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mergerBtnActionPerformed(evt);
            }
        });

        mascotBtn.setBounds(40, 170, 150, 40);
        mascotBtn.setText("Mascot Daemon");
        cp.add(mascotBtn);
        mascotBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mascotBtnActionPerformed(evt);
            }
        });

        identificationBtn.setBounds(40, 220, 150, 40);
        identificationBtn.setText("IdentificationGUI");
        cp.add(identificationBtn);
        identificationBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                identificationBtnActionPerformed(evt);
            }
        });

        quantitationBtn.setBounds(40, 270, 150, 40);
        quantitationBtn.setText("QuantitationGUI");
        cp.add(quantitationBtn);
        quantitationBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quantitationBtnActionPerformed(evt);
            }
        });

        peptizerBtn.setBounds(40, 320, 150, 40);
        peptizerBtn.setText("Peptizer");
        cp.add(peptizerBtn);
        peptizerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                peptizerBtnActionPerformed(evt);
            }
        });


        genericQueryBtn.setBounds(40, 370, 150, 40);
        genericQueryBtn.setText("GenericQuery");
        cp.add(genericQueryBtn);
        genericQueryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                genericQueryBtnActionPerformed(evt);
            }
        });

        storeBinaryFileBtn.setBounds(40, 420, 150, 40);
        storeBinaryFileBtn.setText("Store Binary File(s)");
        cp.add(storeBinaryFileBtn);
        storeBinaryFileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                storeBinaryFileBtnActionPerformed(evt);
            }
        });

        projectAnalyzerBtn.setBounds(40, 470, 150, 40);
        projectAnalyzerBtn.setText("ProjectAnalyzer");
        cp.add(projectAnalyzerBtn);
        projectAnalyzerBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                projectAnalyzerBtnActionPerformed(evt);
            }
        });

        quantitationValidationBtn.setBounds(40, 520, 150, 40);
        quantitationValidationBtn.setText("Rover");
        cp.add(quantitationValidationBtn);
        quantitationValidationBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                quantitationValidationBtnActionPerformed(evt);
            }
        });


        customBtn.setBounds(360, 570, 90, 30);
        customBtn.setText("Custom...");
        cp.add(customBtn);
        customBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                customBtnActionPerformed(evt);
            }
        });

        exitBtn.setBounds(470, 570, 90, 30);
        exitBtn.setText("Exit");
        cp.add(exitBtn);
        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                close();
            }
        });

        // Labels to the right of the buttons.
        projectManagerLbl.setBounds(220, 20, 350, 40);
        projectManagerLbl.setText("Create a new project");
        projectManagerLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(projectManagerLbl);
        spectrumStorageLbl.setBounds(220, 70, 350, 20);
        spectrumStorageLbl.setText("Store mass spectra from local folder and assign to project");
        spectrumStorageLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(spectrumStorageLbl);
        spectrumStorageLbl2.setBounds(220, 90, 350, 20);
        spectrumStorageLbl2.setText("(Only mgf-files can be stored)");
        spectrumStorageLbl2.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(spectrumStorageLbl2);
        mergerLbl.setBounds(220, 120, 350, 20);
        mergerLbl.setText("Merge stored mass spectra for Mascot search");
        mergerLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(mergerLbl);
        mergerLbl2.setBounds(220, 140, 350, 20);
        mergerLbl2.setText("(Choose 1000 spectra to be merged)");
        mergerLbl2.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(mergerLbl2);
        // get the mascot daemon file before showing the sourcfield
        getMascotDaemonFile();
        sourceBrow.setBounds(455, 180, 90, 25);
        sourceField.setBounds(220, 180, 230, 25);
        sourceField.setEnabled(false);
        sourceField.setText(sourceName);
        cp.add(sourceField);
        cp.add(sourceBrow);
        identificationLbl.setBounds(220, 220, 350, 40);
        identificationLbl.setText("Get identifications out of dat-file");
        identificationLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(identificationLbl);
        quantitationLbl.setBounds(220, 270, 350, 40);
        quantitationLbl.setText("Get Quantitations out of quantitation files");
        quantitationLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(quantitationLbl);
        peptizerBtnLbl.setBounds(220, 320, 350, 40);
        peptizerBtnLbl.setText("Launch peptizer for manual validation");
        peptizerBtnLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(peptizerBtnLbl);
        genericQueryLbl.setBounds(220, 370, 350, 40);
        genericQueryLbl.setText("Start own query");
        genericQueryLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(genericQueryLbl);
        storeBinaryFileLbl.setBounds(220, 420, 350, 40);
        storeBinaryFileLbl.setText("Store binary file(s) with a project");
        storeBinaryFileLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(storeBinaryFileLbl);
        projectAnalyzerLbl.setBounds(220, 470, 350, 40);
        projectAnalyzerLbl.setText("Start Projectanalyzer");
        projectAnalyzerLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(projectAnalyzerLbl);
        quantitationValidationLbl.setBounds(220, 520, 350, 40);
        quantitationValidationLbl.setText("Start quantitation validation");
        quantitationValidationLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        cp.add(quantitationValidationLbl);

        setJMenuBar(menuBar);
        setResizable(false);
        setVisible(true);
        getConnection();
    }

    private void configurationMenuActionPerformed() {
        // Try to verify permissions!
        // Perform a query on the current database connection and
        // try to find 'delete' or 'grant all' permissions on the selected database.

        // Note this is only very basic dummy-protection.
        boolean boolIsAllowed = false;
        String lUser = "NA";

        if (iConn != null) {
            try {
                // Username.
                lUser = iConn.getMetaData().getUserName();
                String lCatalog = iConn.getCatalog();


                int index = -1;
                if ((index = lUser.indexOf('@')) != -1) {
                    lUser = lUser.substring(0, index).toLowerCase();
                }


                if (lUser.equals("root")) {
                    boolIsAllowed = true;
                } else {

                    PreparedStatement lStatement = iConn.prepareStatement("show grants for " + lUser);
                    ResultSet rs = lStatement.executeQuery();
                    while (rs.next()) {
                        boolean boolGrant = false;
                        boolean boolUser = false;
                        boolean boolCatalog = false;

                        // Get the current grant.
                        String grant = rs.getString(1).toLowerCase();

                        // Verify the user.
                        int userIndex = grant.indexOf("to '" + lUser + "'");
                        if (userIndex > -1) {
                            boolUser = true;
                        } else {
                            continue;
                        }

                        // Verify the grant.
                        int grantIndex1 = grant.indexOf("delete");
                        int grantIndex2 = grant.indexOf("all");
                        if (grantIndex1 > -1 || grantIndex2 > -1) {
                            boolGrant = true;
                        } else {
                            continue;
                        }

                        // Verify the catalog.
                        int indexCatalog1 = grant.indexOf("on '" + lCatalog + "'");
                        int indexCatalog2 = grant.indexOf("on *.*");
                        if (indexCatalog1 > -1 || indexCatalog2 > -1) {
                            boolCatalog = true;
                        } else {
                            continue;
                        }

                        if (boolGrant & boolCatalog & boolUser) {
                            boolIsAllowed = true;
                            break;
                        }

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        if (boolIsAllowed) {
            new ConfigurationGUI("ConfigurationGUI", iConn, iDBName);
        } else {
            JOptionPane.showMessageDialog(this.getRootPane(), "User '" + lUser + "' is not allowed to configure the ms_lims database system. \nPlease contact the system administrator.");
        }

    }

    /**
     * This method creates a dialog which handles the DB connection.
     */
    private void getConnection() {
        ConnectionDialog cd = new ConnectionDialog(this, this, "Establish DB connection for ms_lims", "queryengine.properties");
        cd.setVisible(true);
    }

    // Event procedures

    public void projectManagerBtnActionPerformed(ActionEvent evt) {
        SpectrumStorageGUI.setNotStandAlone();
        SpectrumStorageGUI specStore = new SpectrumStorageGUI("Project manager", true, iConn, iDBName);
        specStore.setVisible(true);
    }

    public void spectrumStorageBtnActionPerformed(ActionEvent evt) {
        SpectrumStorageGUI.setNotStandAlone();
        SpectrumStorageGUI specStore = new SpectrumStorageGUI("Spectrum storage", false, iConn, iDBName);
        specStore.setVisible(true);
    }

    public void quantitationValidationBtnActionPerformed(ActionEvent evt) {
        WizardFrameHolder valQaunt = new WizardFrameHolder(false, iConn);
        valQaunt.setVisible(true);
    }

    public void mergerBtnActionPerformed(ActionEvent evt) {
        MergerGUI.setNotStandAlone();
        MergerGUI merger = new MergerGUI("MergerGUI", iConn, iDBName);
        merger.setVisible(true);
    }

    public void mascotBtnActionPerformed(ActionEvent evt) {
        try {
            ProcessBuilder builder = new ProcessBuilder(sourceName);
            builder.start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, new String[]{"Unable to load the file 'Daemon.exe'", ex.getMessage()}, "Daemon.exe", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void identificationBtnActionPerformed(ActionEvent evt) {
        IdentificationGUI.setNotStandAlone();
        IdentificationGUI id = new IdentificationGUI("Identification GUI", iConn, iDBName);
        id.setVisible(true);
    }

    public void quantitationBtnActionPerformed(ActionEvent evt) {
        QuantitationTypeChooser lChooser = new QuantitationTypeChooser(iConn, iDBName);
    }

    private void peptizerBtnActionPerformed(final ActionEvent aEvt) {
        ConnectionManager.getInstance().setConnection(iConn);
        PeptizerGUI peptizer = new PeptizerGUI();
        peptizer.setEnclosedByLims(true);
        CreateTaskDialog dialog = new CreateTaskDialog(peptizer);
        dialog.setMs_lims_project_selected();
    }

    public void genericQueryBtnActionPerformed(ActionEvent evt) {
        GenericQuery.setNotStandAlone();
        GenericQuery query = new GenericQuery("GenericQuery", iConn, iDBName);
        query.setVisible(true);
    }

    public void storeBinaryFileBtnActionPerformed(ActionEvent evt) {
        StoreBinaryFileGUI.setNotStandAlone();
        StoreBinaryFileGUI sbfGUI = new StoreBinaryFileGUI("Store binary files(s)", iConn, iDBName);
        sbfGUI.setVisible(true);
    }

    public void projectAnalyzerBtnActionPerformed(ActionEvent evt) {
        ProjectAnalyzer.setNotStandAlone();
        ProjectAnalyzer pa = new ProjectAnalyzer("Project Analyzer", iConn, iDBName);
        pa.setVisible(true);
    }

    public void customBtnActionPerformed(ActionEvent evt) {
        CustomLauncherDialog cd = new CustomLauncherDialog(iConn, iDBName);
        cd.setLocationRelativeTo(this);
        cd.setVisible(true);
    }


    /**
     * This method accepts an incoming connection to
     * perform all database queries on.
     *
     * @param aConn Connection on which to perform the queries.
     * @param aDB   String with the name of the DB (for display purposes).
     */
    public void passConnection(Connection aConn, String aDB) {
        if (aConn == null) {
            this.close();
        }
        this.iConn = aConn;
        this.iDBName = aDB;
    }

    /**
     * This method is called when the frame is closed. It shuts down the JVM.
     */
    private void close() {
        if (iConn != null) {
            try {
                iConn.close();
                System.out.println("\n\nDB connection closed.\n\n");
            } catch (SQLException sqle) {
                System.err.println("\n\nUnable to close DB connection!\n\n");
            }
        }
        System.exit(0);
    }

    /**
     * This method reads the location of the Mascot Daemon file from a property-file
     */
    private void getMascotDaemonFile() {
        try {
            Properties props = new Properties();
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("mascotdaemon.properties");
            if (is == null) {
                throw new IOException("Unable to locate 'mascotdaemon.properties' file in the classpath.");
            }
            props.load(is);
            sourceName = props.getProperty("MASCOTDAEMONFILE");
            if (sourceName == null) {
                throw new IOException("Key 'MASCOTDAEMONFILE' in file 'mascotdaemon.properties' was not defined or NULL!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, new String[]{ex.getMessage(), "Please select the program location manually when required."}, "Mascot Daemon location not specified!", JOptionPane.WARNING_MESSAGE);
        }
    }

    // This action creates and shows an open-file dialog.

    public class OpenFileAction extends AbstractAction {

        JFrame frame;
        JFileChooser chooser;
        InnerDaemonFileFilter filter = new InnerDaemonFileFilter();
        File file = null;

        OpenFileAction(JFrame frame, JFileChooser chooser) {
            super("Browse...");
            this.chooser = chooser;
            this.frame = frame;
        }

        public void actionPerformed(ActionEvent evt) {

            chooser.setFileFilter(filter);
            chooser.showOpenDialog(frame);

            // Get the selected file
            file = chooser.getSelectedFile();
            // Only get the path, if a file was selected
            if (file != null) {
                String name = file.getAbsolutePath();
                sourceName = name;
                sourceField.setText(name);
                sourceField.setEnabled(true);
                sourceField.setEditable(false);

            }
        }
    }

    ;

    public static void main(String[] args) {
        new MS_LIMS("ms_lims (version " + AboutDialog.getLastVersion() + ")");
    }

    /**
     * This method will delete every file in a directory
     *
     * @param dir Directory to delete
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * This class represents a file filter that looks specifically for the mascot daemon
     * executable ('daemon.exe').
     */
    private static class InnerDaemonFileFilter extends javax.swing.filechooser.FileFilter {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith("daemon.exe");
        }

        public String getDescription() {
            return "Daemon.exe";
        }
    }
}