/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 26-nov-02
 * Time: 15:00:19
 */
package com.compomics.mslimscore.gui;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.gui.dialogs.SettingsDialog;
import com.compomics.mslimscore.util.fileio.mergers.PKLMergerAndStorer;
import com.compomics.util.interfaces.PickUp;
import com.compomics.util.io.FTPClient;
import com.compomics.util.io.FolderMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class implements a GUI to interface with a FolderMonitor that will FTP all new files to a specified
 * destination.
 *
 * @author Lennart Martens
 */
public class FTPSatellite extends JFrame implements com.compomics.util.interfaces.Logger, PickUp {
    // Class specific log4j logger for FTPSatellite instances.
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FTPSatellite.class);

    /**
     * The Thread that will wrap the FolderMonitor.
     */
    private Thread iThread = null;

    /**
     * The SimpleDateFormat converter class.
     */
    private SimpleDateFormat iSDF = null;

    /**
     * The base title for the JFrame.
     */
    private String iTitle = null;

    /**
     * The text area on which messages can be logged.
     */
    private JTextArea txtText = null;

    /**
     * The 'Start' button.
     */
    private JButton btnStart = null;

    /**
     * The 'Stop' button.
     */
    private JButton btnStop = null;

    /**
     * The 'Settings' button.
     */
    private JButton btnSettings = null;

    /**
     * The 'Write log' button.
     */
    private JButton btnWriteLog = null;

    /**
     * The 'Clear log' button.
     */
    private JButton btnClearLog = null;

    /**
     * The boolean that indicates whether settings have been transferred.
     */
    private boolean iSettingsTransferred = false;

    /**
     * The FTP host name to connect to.
     */
    private String iHost = null;

    /**
     * The FTP user name to use.
     */
    private String iUser = null;

    /**
     * The FTP password to use.
     */
    private String iPassword = null;

    /**
     * The boolean to indicate whether a merge is required.
     */
    private boolean iMerge = false;

    /**
     * The DB driver.
     */
    private String iDriver = null;

    /**
     * The DB URL.
     */
    private String iUrl = null;

    /**
     * The DB user.
     */
    private String iDBUser = null;

    /**
     * The DB user's password.
     */
    private String iDBPassword = null;

    /**
     * The folder to monitor.
     */
    private File iFolder = null;

    /**
     * The file filter to use.
     */
    private String iFilter = null;

    /**
     * The interval at which to check the folder.
     */
    private long iInterval = 0l;

    /**
     * The FolderMonitor that will do all the hard work.
     */
    private FolderMonitor iFM = null;

    /**
     * The object that will allow us to merge and store PKL files.
     */
    private PKLMergerAndStorer iMerger = null;


    /**
     * This constructor takes a title for the JFrame. It also constructs the screen.
     *
     * @param aTitle String with the title for the JFrame.
     */
    public FTPSatellite(String aTitle) {
        super(aTitle + " (stopped)");

        this.iTitle = aTitle;
        iSDF = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

        this.constructScreen();
    }

    /**
     * This method is called by the SettingsDialog to intialize the FTP parameters.
     *
     * @param aHost     String with the hostname of the FTP server.
     * @param aUser     String with the username.
     * @param aPassword String with the password.
     * @param aMerge    boolean to indicate whether the files should be merged before sending.
     */
    public void setFTPParams(String aHost, String aUser, String aPassword, boolean aMerge) {
        this.iHost = aHost;
        this.iUser = aUser;
        this.iPassword = aPassword;
        this.iMerge = aMerge;
    }

    /**
     * This method is called by the SettingsDialog to initialize the DB parameters.
     *
     * @param aDriver   String with the DB driver.
     * @param aUrl      String with the DB URL.
     * @param aUser     String with the user for the DB.
     * @param aPassword String with the password for the DB user.
     */
    public void setDBParams(String aDriver, String aUrl, String aUser, String aPassword) {
        if (aUser.equals("")) {
            aUser = null;
            aPassword = null;
        }
        this.iDriver = aDriver;
        this.iUrl = aUrl;
        this.iDBUser = aUser;
        this.iDBPassword = aPassword;

        HashMap params = new HashMap(4);
        params.put(PKLMergerAndStorer.DRIVER, iDriver);
        params.put(PKLMergerAndStorer.URL, iUrl);
        if (iDBUser != null) {
            params.put("user", iDBUser);
            params.put("password", iDBPassword);
        }

        this.iMerger = new PKLMergerAndStorer(true, params);
    }

    /**
     * This method sets all the monitoring parameters. It is called by the SettingsDialog.
     *
     * @param aFolder   File which points to the directory that has to be monitored.
     * @param aFilter   String with the filename extensions to filter (can be 'null' whenever no filter is desired).
     * @param aInterval long with the checking interval.
     */
    public void setMonitoringParams(File aFolder, String aFilter, long aInterval) {
        this.iFolder = aFolder;
        this.iFilter = aFilter;
        this.iInterval = aInterval;
    }

    /**
     * This method signals that all necessary variables have been set.
     *
     * @param aInitialized boolean to indicate whether the settings have been entered by the user and have been
     *                     transferred.
     */
    public void setInitialized(boolean aInitialized) {
        this.iSettingsTransferred = aInitialized;
    }

    /**
     * This message logs the current time to the console + a message.
     *
     * @param aMessage String with the message to display after the timestamp.
     */
    public void logTime(String aMessage) {
        long time = System.currentTimeMillis();
        String timeString = iSDF.format(new Date(time));
        txtText.append("# " + timeString + " : " + aMessage + "\n");
        txtText.setCaretPosition(txtText.getText().length());
    }

    /**
     * This method allows the logging of a 'normal' event.
     *
     * @param aMessage String with a normal operation message.
     */
    public void logNormalEvent(String aMessage) {
        txtText.append("  " + aMessage + "\n");
        txtText.setCaretPosition(txtText.getText().length());
    }

    /**
     * This method allows the logging of an exceptional event.
     *
     * @param aMessage String with the exceptional message to log.
     */
    public void logExceptionalEvent(String aMessage) {
        long time = System.currentTimeMillis();
        String timeString = iSDF.format(new Date(time));
        txtText.append("*! " + timeString + aMessage + "\n");
        txtText.setCaretPosition(txtText.getText().length());
    }

    /**
     * This method should be called by the notifier when appropriate.
     *
     * @param aObject Object with the data that should be sent.
     */
    public void sendIncoming(Object aObject) {
        // We should have a File[].
        if ((aObject instanceof File[]) && (aObject != null)) {
            File[] allFiles = (File[]) aObject;
            if (allFiles.length > 0) {
                try {
                    File result = iMerger.mergeFilesToFile(allFiles, "mergeFile_" + System.currentTimeMillis() + ".txt");

                    FTPClient ftp = new FTPClient(this.iHost, this.iUser, this.iPassword);
                    ftp.sendTextFile(result.getCanonicalPath());

                    result.deleteOnExit();
                } catch (IOException ioe) {
                    logTime("Error occurred!\n  " + ioe.getMessage());
                }
            }
        }
    }

    /**
     * This method will initialize and lay-out all GUI components.
     */
    private void constructScreen() {
        txtText = new JTextArea(10, 20);
        txtText.setEditable(false);
        txtText.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane textPane = new JScrollPane(txtText);

        JPanel buttonPanel = this.getButtonPanel();

        this.getContentPane().add(textPane, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((int) (dim.getWidth() / 6), (int) (dim.getHeight() / 4));
        this.setSize(this.getWidth() + 100, this.getHeight());
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });
    }

    /**
     * This method will create the buttonpanel.
     *
     * @return JPanel  with the buttons for the application.
     */
    private JPanel getButtonPanel() {
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

        btnStart = new JButton("Start");
        btnStart.setMnemonic(KeyEvent.VK_S);
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startPressed();
            }
        });
        btnStart.setEnabled(false);

        btnStop = new JButton("Stop");
        btnStop.setMnemonic(KeyEvent.VK_P);
        btnStop.setEnabled(false);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopPressed();
            }
        });

        btnSettings = new JButton("Settings...");
        btnSettings.setMnemonic(KeyEvent.VK_T);
        btnSettings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                settingsPressed();
            }
        });

        btnWriteLog = new JButton("Write log to file...");
        btnWriteLog.setMnemonic(KeyEvent.VK_W);
        btnWriteLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtText.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(FTPSatellite.this, "The log is empty; there is no output to be written.", "No output to be written", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String folder = "/";
                boolean sure = false;
                File f = null;
                while (!sure) {
                    sure = true;
                    f = null;
                    // A JFileChooser to handle the saving-file name and location.
                    JFileChooser jfc = new JFileChooser(folder);
                    int operation = jfc.showSaveDialog(FTPSatellite.this);
                    if (operation == JFileChooser.APPROVE_OPTION) {
                        // Get the selected file.
                        f = jfc.getSelectedFile();
                        if (f.exists()) {
                            if (f.isDirectory()) {
                                String lMessage = "You selected a folder, not a file!";
                                logger.error(lMessage);
                                JOptionPane.showMessageDialog(FTPSatellite.this, lMessage, "Folder selected instead of file!", JOptionPane.ERROR_MESSAGE);
                                sure = false;
                            } else {
                                int result = JOptionPane.showConfirmDialog(FTPSatellite.this, new String[]{"The file you selected (" + f.getName() + ") already exists!", "Are you SURE you want to overwrite the existing file?", "\n"}, "File already exists!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                                if (result == JOptionPane.NO_OPTION) {
                                    sure = false;
                                    folder = f.getParent();
                                }
                            }
                        } else {
                            try {
                                f.createNewFile();
                            } catch (IOException ioe) {
                                f = null;
                                String lMessage = "Unable to create the file you requested!";
                                logger.error(lMessage);
                                JOptionPane.showMessageDialog(FTPSatellite.this, lMessage, "Unable to create file!", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
                // See if we have a file...
                if (f != null) {
                    try {
                        PrintWriter pw = new PrintWriter(new FileWriter(f));
                        pw.print(txtText.getText());
                        pw.flush();
                        pw.close();
                        JOptionPane.showMessageDialog(FTPSatellite.this, "Written log to file '" + f.getCanonicalPath() + "'.", "Log written successfully", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ioe) {
                        logger.error(ioe.getMessage(), ioe);
                        JOptionPane.showMessageDialog(FTPSatellite.this, new String[]{"Unable to write the log to file!", ioe.getMessage(), "\n"}, "Unable to write log!", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        btnWriteLog.setEnabled(false);

        btnClearLog = new JButton("Clear log");
        btnClearLog.setMnemonic(KeyEvent.VK_C);
        btnClearLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int answer = JOptionPane.showConfirmDialog(FTPSatellite.this, new String[]{"Are you sure you want to clear the log?", "\n"}, "Confirm clearing of log", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    txtText.setText("");
                    txtText.setCaretPosition(0);
                    System.gc();
                }
            }
        });

        buttons.add(Box.createHorizontalGlue());
        buttons.add(btnStart);
        buttons.add(Box.createRigidArea(new Dimension(5, btnStart.getHeight())));
        buttons.add(btnStop);
        buttons.add(Box.createRigidArea(new Dimension(5, btnStart.getHeight())));
        buttons.add(btnSettings);
        buttons.add(Box.createRigidArea(new Dimension(5, btnStart.getHeight())));
        buttons.add(btnWriteLog);
        buttons.add(Box.createRigidArea(new Dimension(5, btnStart.getHeight())));
        buttons.add(btnClearLog);

        return buttons;
    }

    /**
     * This method is called when the user presses 'Start'.
     */
    private void startPressed() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        logTime("Start pressed.");
        btnStop.setEnabled(true);
        btnStart.setEnabled(false);
        btnSettings.setEnabled(false);
        btnWriteLog.setEnabled(false);

        if (this.iDriver == null) {
            HashMap params = new HashMap(4);
            params.put(FolderMonitor.TEXTMODE, "true");


            if (iMerge) {
                iMerger = new PKLMergerAndStorer();
                params.put(FolderMonitor.PICKUP, this);
                params.put(FolderMonitor.LIMIT, new Integer(299));

                if (iFilter == null) {
                    iFM = new FolderMonitor(iFolder, iInterval, FolderMonitor.GATHER_FILES_FOR_PICKUP, params, logger);
                } else {
                    iFM = new FolderMonitor(iFolder, iInterval, iFilter, FolderMonitor.GATHER_FILES_FOR_PICKUP, params, logger);
                }
            } else {
                params.put(FolderMonitor.HOST, iHost);
                params.put(FolderMonitor.USER, iUser);
                params.put(FolderMonitor.PASSWORD, iPassword);

                if (iFilter == null) {
                    iFM = new FolderMonitor(iFolder, iInterval, FolderMonitor.FTP_TO_SPECIFIED_DESTINATION, params, logger);
                } else {
                    iFM = new FolderMonitor(iFolder, iInterval, iFilter, FolderMonitor.FTP_TO_SPECIFIED_DESTINATION, params, logger);
                }
            }
        } else {

            HashMap params = new HashMap(3);
            params.put(FolderMonitor.PICKUP, this);
            params.put(FolderMonitor.LIMIT, new Integer(299));
            params.put(FolderMonitor.TEXTMODE, "true");

            if (iFilter == null) {
                iFM = new FolderMonitor(iFolder, iInterval, FolderMonitor.GATHER_FILES_FOR_PICKUP, params, logger);
            } else {
                iFM = new FolderMonitor(iFolder, iInterval, iFilter, FolderMonitor.GATHER_FILES_FOR_PICKUP, params, logger);
            }
        }

        iThread = new Thread(iFM);
        iThread.start();

        this.setTitle(iTitle + " (running...)");
        txtText.append("  Monitoring of folder '" + iFolder + "' started.\n");
        txtText.setCaretPosition(txtText.getText().length());
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the user presses 'Stop'.
     */
    private void stopPressed() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        logTime("Stop pressed.");
        btnStop.setEnabled(false);
        btnStart.setEnabled(true);
        btnSettings.setEnabled(true);
        btnWriteLog.setEnabled(true);

        iFM.signalStop();
        iThread.interrupt();

        while (iFM.isRunning()) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }

        this.setTitle(iTitle + " (stopped)");
        txtText.append("  Monitoring of folder '" + iFolder + "' stopped.\n");
        txtText.setCaretPosition(txtText.getText().length());
        iFM = null;
        iThread = null;
        System.gc();
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * This method is called when the user presses 'Settings'.
     */
    private void settingsPressed() {
        SettingsDialog sd = new SettingsDialog(this);
        sd.setVisible(true);
        if (this.iSettingsTransferred) {
            logTime("Settings transferred.");
            try {
                txtText.append("  * FTP settings:\n");
                txtText.append("    - Hostname: '" + iHost + "'\n    - Username: '" + iUser + "'\n  * Monitoring settings:\n    - Folder: '" + iFolder.getCanonicalPath() + "'\n    - Filter: ");
                if (iFilter == null) {
                    txtText.append("(none)\n");
                } else {
                    txtText.append("'" + (iFilter.startsWith(".") ? "" : ".") + iFilter + "'\n");
                }
                txtText.append("    - Checking interval: " + iInterval + " milliseconds\n");
            } catch (IOException ioe) {
                iFolder = null;
                logTime("Error occurred!\n  " + ioe.getMessage());
            }
            btnStart.setEnabled(true);
        }
    }

    /**
     * Main method. Start-up arguments are not used.
     *
     * @param args String[] with the start-up arguments (not used).
     */
    public static void main(String[] args) {
        FTPSatellite fs = new FTPSatellite("FTP monitor satellite");
        fs.setVisible(true);
    }
}
