/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2004
 * Time: 17:58:34
 */
package com.compomics.mslims.gui;

import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.io.PropertiesManager;
import com.healthmarketscience.jackcess.Database;
import org.apache.log4j.Logger;

import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.frames.PreviewQuantitationResultsFrame;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.gui.table.MascotQuantitationSearchTableModel;
import com.compomics.mslims.gui.table.renderers.ErrorCellRenderer;
import com.compomics.mslims.gui.table.renderers.ErrorObject;
import com.compomics.mslims.gui.tree.MascotSearch;
import com.compomics.mslims.gui.tree.MascotTask;
import com.compomics.mslims.gui.tree.MascotTasksTreeModel;
import com.compomics.mslims.gui.tree.renderers.MascotTasksTreeCellRenderer;
import com.compomics.mslims.util.workers.ReadMascotTaskDBWorker;
import com.compomics.mslims.util.enumeration.RatioSourceType;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.interfaces.Flamable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class will present the user with a graphical user interface to read search results from the Mascot Daemon Task
 * Database and preview the processed results in the s_lims DB.
 *
 * @author Lennart Martens
 * @version $Id: QuantitationGUI.java,v 1.3 2009/07/28 14:48:33 lennart Exp $
 */
public class QuantitationGUI extends JFrame implements Connectable, Flamable {
    // Class specific log4j logger for QuantitationGUI instances.
    private static Logger logger = Logger.getLogger(QuantitationGUI.class);

    private JTree trSearches = null;
    private JTable tblSummary = null;
    /**
     * Boolean that indicates whether the tool is ran in stand-alone mode ('true') or not ('false').
     */
    private static boolean iStandAlone = true;


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
    public QuantitationGUI(String aName) {
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
    public QuantitationGUI(String aName, Connection aConn, String aDBName) {
        super(aName);
        if (aConn == null) {
            ConnectionDialog cd = new ConnectionDialog(this, this, "Connection for QuantitationGUI", "QuantitationGUI.properties");
            cd.setVisible(true);
        } else {
            passConnection(aConn, aDBName);
        }

        this.readTaskDB();
        super.setTitle("QuantitationGUI (storing results in " + iDBName + ")");
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
        this.setVisible(true);
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
            QuantitationGUI frame = new QuantitationGUI("QuantitationGUI");
            frame.setVisible(true);
        } catch (Throwable t) {
            JFrame frame = new JFrame("You won't see me.");
            logger.error(t.getMessage(), t);
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
                    tblSummary.setModel(new MascotQuantitationSearchTableModel(info));
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
        tblSummary = new JTable(new MascotQuantitationSearchTableModel());
        tblSummary.setDefaultRenderer(ErrorObject.class, new ErrorCellRenderer());

        tblSummary.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {

                    // Locate the renderer under the event location
                    int colIndex = tblSummary.getSelectedColumn();
                    int rowIndex = tblSummary.getSelectedRow();

                    String hint = tblSummary.getValueAt(rowIndex, colIndex).toString();

                    JOptionPane.showMessageDialog(QuantitationGUI.this, hint, "cell description", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JPanel jpanSummary = new JPanel(new BorderLayout());
        jpanSummary.setBorder(BorderFactory.createTitledBorder("Selected tasks & searches"));
        jpanSummary.add(new JScrollPane(tblSummary), BorderLayout.CENTER);

        // Total upper panel.
        JSplitPane spltUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jpanList, jpanSummary);
        JPanel jpanUpper = new JPanel();
        jpanUpper.setLayout(new BorderLayout());
        jpanUpper.add(spltUpper, BorderLayout.CENTER);

        // The main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanUpper);
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
            String lMessage = "You need to select one or more rows in the table to process.";
            logger.error(lMessage);
            JOptionPane.showMessageDialog(this, lMessage, "No rows selected!", JOptionPane.ERROR_MESSAGE);
            tblSummary.requestFocus();
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
        // Now display the selected quantiation information into the previewpane.
        new PreviewQuantitationResultsFrame(this, todo, iConnection, RatioSourceType.DISTILLER_QUANTITATION_TOOLBOX);

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
            // Try to close the connection
            if (iConnection != null && iStandAlone) {
                iConnection.close();
            }

            // Clear temporary data.
            File lTempDirectory = File.createTempFile("temp", "temp").getParentFile();
            File lTempMslimsDirectory = new File(lTempDirectory, "mslims");
            if (lTempMslimsDirectory.exists()) {
                File[] lTopFiles = lTempMslimsDirectory.listFiles();
                for (File lMiddleFile : lTopFiles) {
                    if (lMiddleFile.isDirectory()) {
                        File[] lMiddleFiles = lMiddleFile.listFiles();
                        for (File lBottomFile : lMiddleFiles) {
                            lBottomFile.delete();
                        }
                    }
                    lMiddleFile.delete();
                }
            }

        } catch (SQLException sqle) {
            // Do nothing.
        } catch (IOException e) {
            logger.error("Failing!");
            logger.error(e.getMessage(), e);
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
        Properties props = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "QuantitationGUI.properties");
        Boolean useAccess = new Boolean(props.getProperty("USE_ACCESS"));
        if (!useAccess) {
            try {
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("QuantitationGUI.properties");
                if (is == null) {
                    throw new IOException();
                }
                props.load(is);
                String driver = props.getProperty("TASKDRIVER");
                String url = props.getProperty("TASKURL");
                if (driver == null) {
                    throw new SQLException("Key 'TASKDRIVER' in file 'QuantitationGUI.properties' was not defined or NULL!");
                }
                if (url == null) {
                    throw new SQLException("Key 'TASKURL' in file 'QuantitationGUI.properties' was not defined or NULL!");
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
                    DefaultProgressBar dpb = new DefaultProgressBar(this, "Reading Mascot Daemon Task DB...", 0, count + 2);
                    dpb.setResizable(false);
                    dpb.setSize(350, 100);
                    dpb.setMessage("Connecting...");
                    Vector lTasks = new Vector(count, 5);
                    ReadMascotTaskDBWorker worker = new ReadMascotTaskDBWorker(conn, lTasks, this, dpb);
                    worker.start();
                    dpb.setVisible(true);
                    conn.close();

                    //ToDo
                    iTasks = new Vector();
                    for (int i = 0; i < lTasks.size(); i++) {
                        // As seen as there is a child with quantitation, we must display the task itself.
                        MascotTask lMascotTask = (MascotTask) lTasks.elementAt(i);
                        int lNumberOfSearches = lMascotTask.countSearches();

                        for (int j = 0; j < lNumberOfSearches; j++) {
                            MascotSearch lMascotSearch = lMascotTask.getSearch(j);
                            if (lMascotSearch.hasDistillerProject()) {
                                iTasks.add(lMascotTask);
                                break;
                            }
                        }
                    }


                    // @TODO
                    // Better exception handling for the ODBC/JDBC bridge driver!!
                } catch (SQLException sqle) {
                    throw new SQLException("Unable to read data from the Mascot Daemon TaskDB '" + sqle.getMessage() + "'!");
                }
            }catch (SQLException sqle){
                logger.error(sqle.getMessage(), sqle);
                JOptionPane.showMessageDialog(this, new String[]{"There were fatal errors trying to access the Mascot Daemon Task DB:", sqle.getMessage()}, "Unable to retrieve data from TaskDB!", JOptionPane.ERROR_MESSAGE);
                this.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(this, new String[]{"There were fatal errors trying to access the Mascot Daemon Task DB:", e.getMessage()}, "Unable to read the properties!", JOptionPane.ERROR_MESSAGE);
                this.close();
            }
        }else{
            // Retrieve from MS Access file.
            try {
                String fileLocation = props.getProperty("MS_ACCESS_FILE");
                if (fileLocation == null) {
                    throw new IOException("Key 'MS_ACCESS_FILE' in file 'QuantitationGUI.properties' was not defined or NULL!");
                }

                File taskDBFile = null;
                while (taskDBFile == null) {
                    // Default directory location is the root of this drive.
                    String root = fileLocation;
                    File test = new File(root.trim());
                    // See if it exists.
                    if (!test.exists()) {
                        // Just go to the user home folder.
                        root = System.getProperty("user.dir") + File.separator;
                    }
                    JFileChooser jfc = new JFileChooser(root);
                    jfc.setDialogTitle("Open Mascot Daemon TaskDB file (.mdb file)");
                    // Set the mdb file name filter.
                    jfc.setFileFilter(new FileNameExtensionFilter("MS Access files", "mdb"));
                    // Select file.
                    int returnVal = jfc.showOpenDialog(QuantitationGUI.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        taskDBFile = jfc.getSelectedFile();
                        if (!taskDBFile.exists()) {
                            String lMessage = "The '" + taskDBFile.getName() + " file was not found!";
                            logger.error(lMessage);
                            JOptionPane.showMessageDialog(QuantitationGUI.this, new String[]{lMessage}, " file was not found!", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        break;
                    }
                }

                // Should have a file now!
                Database taskDB = Database.open(taskDBFile, true);
                int rowCount = taskDB.getTable("Mascot_Daemon_Results").getRowCount();
                rowCount += taskDB.getTable("Mascot_Daemon_Tasks").getRowCount();

                DefaultProgressBar dpb = new DefaultProgressBar(this, "Reading Mascot Daemon Task DB Access file...", 0, rowCount+2);
                dpb.setResizable(false);
                dpb.setSize(350, 100);
                dpb.setMessage("Connecting...");
                iTasks = new Vector();
                ReadMascotTaskDBWorker worker = new ReadMascotTaskDBWorker(taskDB, iTasks, this, dpb);
                worker.start();
                dpb.setVisible(true);
                taskDB.close();

            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
                JOptionPane.showMessageDialog(this, new String[]{"There were fatal errors trying to access the Mascot Daemon Task DB Access file:", ioe.getMessage()}, "Unable to retrieve data from TaskDB!", JOptionPane.ERROR_MESSAGE);
                this.close();
            }
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
        logger.error(aMessage, aThrowable);
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
