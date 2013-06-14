/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 26-nov-02
 * Time: 16:16:03
 */
package com.compomics.mslimscore.gui.dialogs;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.gui.FTPSatellite;
import com.compomics.util.gui.JLabelAndComponentPanel;
import com.compomics.util.io.FTPClient;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;
import java.util.Vector;


/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2009/07/30 10:19:28 $
 */

/**
 * This class implements the 'Settings' dialog for the FTPSatellite JFrame.
 *
 * @author Lennart Martens
 */
public class SettingsDialog extends JDialog {
    // Class specific log4j logger for SettingsDialog instances.
    private static Logger logger = Logger.getLogger(SettingsDialog.class);

    /**
     * The parent component.
     */
    private FTPSatellite iParent = null;

    /**
     * 'Test FTP settings' button.
     */
    private JButton btnTestFTP = null;

    /**
     * 'Test DB settings' button.
     */
    private JButton btnTestDB = null;

    /**
     * 'OK' button.
     */
    private JButton btnOK = null;

    /**
     * 'Cancel' button.
     */
    private JButton btnCancel = null;

    /**
     * FTP Host textbox.
     */
    private JTextField txtHost = null;

    /**
     * FTP User textbox.
     */
    private JTextField txtUser = null;

    /**
     * FTP Password textbox.
     */
    private JTextField txtPassword = null;

    /**
     * DB driver textbox.
     */
    private JTextField txtDriver = null;

    /**
     * DB URL textbox.
     */
    private JTextField txtUrl = null;

    /**
     * DB user textbox.
     */
    private JTextField txtDBUser = null;

    /**
     * DB password textbox.
     */
    private JTextField txtDBPassword = null;

    /**
     * Folder input textfield.
     */
    private JTextField txtFolder = null;

    /**
     * Folder browse button.
     */
    private JButton btnBrowse = null;

    /**
     * File filter textfield.
     */
    private JTextField txtFilter = null;

    /**
     * Interval textfield.
     */
    private JTextField txtInterval = null;

    /**
     * Checkbox to indicate whether or not to store the pkl files in the DB.
     */
    private JCheckBox chkUSeDB = null;

    /**
     * Checkbox to indicate whether to merge files.
     */
    private JCheckBox chkMergeFiles = null;


    /**
     * This constructor will initialize the component and construct the GUI.
     *
     * @param aParent FTPSatellite, which is the parent component for this dialog.
     */
    public SettingsDialog(FTPSatellite aParent) {
        super(aParent, "Settings", true);
        this.iParent = aParent;

        this.constructScreen();
        this.pack();
        this.setResizable(false);
        Point p = aParent.getLocation();
        this.setLocation(p.x + (aParent.getWidth() / 2), p.y + (aParent.getHeight() / 2));
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    /**
     * This method will initialize all components and lay them out.
     */
    private void constructScreen() {
        // The panel for the FTP settings.
        /*JPanel jpanFTP = new JPanel();
        jpanFTP.setLayout(new BoxLayout(jpanFTP, BoxLayout.X_AXIS));
        jpanFTP.setBorder(BorderFactory.createTitledBorder("FTP settings"));

        */

        // Initialize the FTP components.
        txtHost = new JTextField(20);
        txtUser = new JTextField(20);
        txtPassword = new JPasswordField(20);

        chkMergeFiles = new JCheckBox("Merge pklfiles before FTP.", true);
        chkMergeFiles.setEnabled(false);

        // Place the FTP components + a label for each.
        /*JPanel jpanHostComp = new JPanel();
        jpanHostComp.setLayout(new BoxLayout(jpanHostComp, BoxLayout.Y_AXIS));
        JPanel jpanHostLabel = new JPanel();
        jpanHostLabel.setLayout(new BoxLayout(jpanHostLabel, BoxLayout.Y_AXIS));

        jpanHostLabel.add(new JLabel("Hostname: "));
        jpanHostLabel.add(Box.createRigidArea(new Dimension(txtHost.getWidth(), 5)));
        jpanHostLabel.add(new JLabel("Username: "));
        jpanHostLabel.add(Box.createRigidArea(new Dimension(txtHost.getWidth(), 5)));
        jpanHostLabel.add(new JLabel("Password: "));

        jpanHostComp.add(txtHost);
        jpanHostComp.add(Box.createRigidArea(new Dimension(txtHost.getWidth(), 5)));
        jpanHostComp.add(txtUser);
        jpanHostComp.add(Box.createRigidArea(new Dimension(txtHost.getWidth(), 5)));
        jpanHostComp.add(txtPassword);

        jpanFTP.add(jpanHostLabel);
        jpanFTP.add(Box.createRigidArea(new Dimension(10, jpanHostLabel.getHeight())));
        jpanFTP.add(jpanHostComp);
        */
        JPanel jpanFTP = new JPanel(new BorderLayout());
        jpanFTP.setBorder(BorderFactory.createTitledBorder("FTP settings"));
        JPanel jpanSubFTP = new JLabelAndComponentPanel(new JLabel[]{new JLabel("Hostname"), new JLabel("Username"), new JLabel("Password")}, new JTextField[]{txtHost, txtUser, txtPassword});
        jpanFTP.add(jpanSubFTP, BorderLayout.WEST);
        jpanFTP.add(chkMergeFiles, BorderLayout.SOUTH);

        // Next on the list are the DB components.
        JPanel jpanDB = new JPanel(new BorderLayout());
        //jpanDB.setLayout(new BoxLayout(jpanDB, BoxLayout.X_AXIS));
        jpanDB.setBorder(BorderFactory.createTitledBorder("Database settings"));

        txtDriver = new JTextField(20);
        txtUrl = new JTextField(20);
        txtDBUser = new JTextField(20);
        txtDBPassword = new JPasswordField(20);

        final JLabelAndComponentPanel jpanLT = new JLabelAndComponentPanel(new JLabel[]{new JLabel("DB driver"), new JLabel("DB URL"), new JLabel("DB user"), new JLabel("DB password")}, new JTextField[]{txtDriver, txtUrl, txtDBUser, txtDBPassword});
        chkUSeDB = new JCheckBox("Store files in database (also performs merging)", true);
        chkUSeDB.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (chkUSeDB.isSelected()) {
                    txtDriver.setEnabled(true);
                    txtUrl.setEnabled(true);
                    txtDBUser.setEnabled(true);
                    txtDBPassword.setEnabled(true);
                    chkMergeFiles.setSelected(true);
                    chkMergeFiles.setEnabled(false);
                    btnTestDB.setEnabled(true);
                } else {
                    txtDriver.setEnabled(false);
                    txtUrl.setEnabled(false);
                    txtDBUser.setEnabled(false);
                    txtDBPassword.setEnabled(false);
                    chkMergeFiles.setEnabled(true);
                    btnTestDB.setEnabled(false);
                }
            }
        });

        // Placing the DB components and their respective labels.
        jpanDB.add(chkUSeDB, BorderLayout.NORTH);
        jpanDB.add(jpanLT, BorderLayout.WEST);
        //jpanDB.add(Box.createHorizontalGlue());

        // The panel for the folder settings.
        JPanel jpanFolder = new JPanel();
        jpanFolder.setLayout(new BoxLayout(jpanFolder, BoxLayout.X_AXIS));
        jpanFolder.setBorder(BorderFactory.createTitledBorder("Target folder"));

        // Now for the target folder panel's components.
        txtFolder = new JTextField(20);
        btnBrowse = new JButton("Browse...");
        btnBrowse.setMnemonic(KeyEvent.VK_B);
        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String currentDir = txtFolder.getText().trim();
                if (currentDir == null || currentDir.equals("")) {
                    currentDir = "/";
                }
                JFileChooser jfc = new JFileChooser(currentDir);
                jfc.setDialogType(JFileChooser.CUSTOM_DIALOG);
                jfc.setApproveButtonText("Select folder");
                jfc.setApproveButtonMnemonic(KeyEvent.VK_S);
                jfc.setApproveButtonToolTipText("Select the folder you want to monitor.");
                jfc.setDialogTitle("Select target folder to monitor");
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int value = jfc.showDialog(SettingsDialog.this, "Select folder");

                if (value == JFileChooser.APPROVE_OPTION) {
                    try {
                        txtFolder.setText(jfc.getSelectedFile().getCanonicalPath());
                    } catch (IOException ioe) {
                        logger.error(ioe.getMessage(), ioe);
                        JOptionPane.showMessageDialog(SettingsDialog.this, new String[]{"Unable to find the folder you've selected!", "\n", ioe.getMessage(), "\n"}, "Folder was not found!", JOptionPane.ERROR_MESSAGE);
                        txtFolder.setText("");
                    }
                }
            }
        });
        txtFilter = new JTextField(10);
        txtInterval = new JTextField(10);

        JPanel jpanFolderComp = new JPanel();
        jpanFolderComp.setLayout(new BoxLayout(jpanFolderComp, BoxLayout.Y_AXIS));
        JPanel jpanFolderLabel = new JPanel();
        jpanFolderLabel.setLayout(new BoxLayout(jpanFolderLabel, BoxLayout.Y_AXIS));

        jpanFolderLabel.add(new JLabel("Select folder: "));
        jpanFolderLabel.add(Box.createRigidArea(new Dimension(txtFilter.getWidth(), (int) (btnBrowse.getPreferredSize().getHeight() / 2))));
        jpanFolderLabel.add(new JLabel("File filter: "));
        jpanFolderLabel.add(Box.createRigidArea(new Dimension(txtFilter.getWidth(), 5)));
        jpanFolderLabel.add(new JLabel("Checking interval: "));


        JPanel jpanFile = new JPanel();
        jpanFile.setLayout(new BoxLayout(jpanFile, BoxLayout.X_AXIS));
        jpanFile.add(txtFolder);
        jpanFile.add(Box.createRigidArea(new Dimension(10, txtFolder.getHeight())));
        jpanFile.add(btnBrowse);

        JPanel jpanIntComp = new JPanel();
        jpanIntComp.setLayout(new BoxLayout(jpanIntComp, BoxLayout.X_AXIS));
        jpanIntComp.add(txtInterval);
        jpanIntComp.add(Box.createRigidArea(new Dimension(5, txtInterval.getHeight())));
        jpanIntComp.add(new JLabel("milliseconds"));

        jpanFolderComp.add(jpanFile);
        jpanFolderComp.add(Box.createRigidArea(new Dimension(txtFilter.getWidth(), 5)));
        jpanFolderComp.add(txtFilter);
        jpanFolderComp.add(Box.createRigidArea(new Dimension(txtFilter.getWidth(), 5)));
        jpanFolderComp.add(jpanIntComp);

        jpanFolder.add(jpanFolderLabel);
        jpanFolder.add(Box.createRigidArea(new Dimension(10, jpanFolderLabel.getHeight())));
        jpanFolder.add(jpanFolderComp);


        // The buttonpanel.
        JPanel jpanButtons = this.getButtonPanel();

        // The main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanFTP);
        jpanMain.add(Box.createRigidArea(new Dimension(jpanFolder.getWidth(), 5)));
        jpanMain.add(jpanDB);
        jpanMain.add(Box.createRigidArea(new Dimension(jpanFolder.getWidth(), 5)));
        jpanMain.add(jpanFolder);
        jpanMain.add(Box.createRigidArea(new Dimension(jpanFolder.getWidth(), 10)));
        jpanMain.add(jpanButtons);

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

        btnTestFTP = new JButton("Test FTP settings");
        btnTestFTP.setMnemonic(KeyEvent.VK_T);
        btnTestFTP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testFTPPressed();
            }
        });

        btnTestDB = new JButton("Test DB settings");
        btnTestDB.setMnemonic(KeyEvent.VK_D);
        btnTestDB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testDBPressed();
            }
        });

        btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okPressed();
            }
        });

        btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelPressed();
            }
        });

        buttons.add(Box.createHorizontalGlue());
        buttons.add(btnTestDB);
        buttons.add(Box.createRigidArea(new Dimension(10, btnOK.getHeight())));
        buttons.add(btnTestFTP);
        buttons.add(Box.createRigidArea(new Dimension(10, btnOK.getHeight())));
        buttons.add(btnOK);
        buttons.add(Box.createRigidArea(new Dimension(10, btnOK.getHeight())));
        buttons.add(btnCancel);

        return buttons;
    }

    /**
     * This method is called when the user presses the 'Test FTP settings' button.
     */
    private void testFTPPressed() {
        Vector messages = new Vector(3, 1);
        int messageType = JOptionPane.INFORMATION_MESSAGE;

        // Do validations on FTP sepcific components.
        String host = txtHost.getText().trim();
        if (host == null || host.equals("")) {
            this.fillOutComponentWarning("hostname");
            txtHost.requestFocus();
            return;
        }
        String user = txtUser.getText().trim();
        if (user == null || user.equals("")) {
            this.fillOutComponentWarning("username");
            txtUser.requestFocus();
            return;
        }
        String password = txtPassword.getText().trim();
        if (password == null || password.equals("")) {
            this.fillOutComponentWarning("password");
            txtPassword.requestFocus();
            return;
        }

        // Try the connection.
        FTPClient ftp = new FTPClient(host, user, password);
        String main = "Test for host '" + host + "' with user '" + user + "' ";
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            ftp.testFTPConnection();
            messages.add(main + "was successful!");
            messages.add("FTP server settings are correct.");
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            messageType = JOptionPane.ERROR_MESSAGE;
            messages.add(main + "failed!");
            messages.add("Error message was '" + ioe.getMessage() + "'");
            messages.add("FTP server settings are INCORRECT!");
        }
        messages.add("\n");
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        // Inform the user.
        String[] msg = new String[messages.size()];
        messages.toArray(msg);
        JOptionPane.showMessageDialog(this, msg, "FTP test results", messageType);

        // Clean-up
        messages = null;
        msg = null;
    }

    /**
     * This method tests the DB connection.
     */
    private void testDBPressed() {
        Vector messages = new Vector(4, 1);
        int messageType = JOptionPane.INFORMATION_MESSAGE;

        // Do validations on DB sepcific components.
        String driver = txtDriver.getText().trim();
        if (driver == null || driver.equals("")) {
            this.fillOutComponentWarning("Driver");
            txtDriver.requestFocus();
            return;
        }
        String url = txtUrl.getText().trim();
        if (url == null || url.equals("")) {
            this.fillOutComponentWarning("URL");
            txtUrl.requestFocus();
            return;
        }
        String dbuser = txtDBUser.getText().trim();
        String dbpassword = txtDBPassword.getText().trim();

        // Try the connection.
        String main = "Test for database '" + url + (dbuser.equals("") ? "' " : "' with user '" + dbuser + "' ");
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            Driver d = (Driver) Class.forName(driver).newInstance();
            Properties props = new Properties();
            if (!dbuser.equals("")) {
                props.put("user", dbuser);
                props.put("password", dbpassword);
            }
            Connection c = d.connect(url, props);
            c.close();
            messages.add(main + "was successful!");
            messages.add("DB connection settings are correct.");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            messageType = JOptionPane.ERROR_MESSAGE;
            messages.add(main + "failed!");
            messages.add("Error message was '" + e.getMessage() + "'");
            messages.add("DB connection settings are INCORRECT!");
        }
        messages.add("\n");
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        // Inform the user.
        String[] msg = new String[messages.size()];
        messages.toArray(msg);
        JOptionPane.showMessageDialog(this, msg, "DB test results", messageType);

        // Clean-up
        messages = null;
        msg = null;
    }

    /**
     * This method is called when the 'OK' button is pressed.
     */
    private void okPressed() {
        // Do validations on all components.
        String host = txtHost.getText().trim();
        if (host == null || host.equals("")) {
            this.fillOutComponentWarning("hostname");
            txtHost.requestFocus();
            return;
        }
        String user = txtUser.getText().trim();
        if (user == null || user.equals("")) {
            this.fillOutComponentWarning("username");
            txtUser.requestFocus();
            return;
        }
        String password = txtPassword.getText().trim();
        if (password == null || password.equals("")) {
            this.fillOutComponentWarning("password");
            txtPassword.requestFocus();
            return;
        }
        String driver = txtDriver.getText().trim();
        if (chkUSeDB.isSelected() && (driver == null || driver.equals(""))) {
            this.fillOutComponentWarning("Driver");
            txtDriver.requestFocus();
            return;
        }
        String url = txtUrl.getText().trim();
        if (chkUSeDB.isSelected() && (url == null || url.equals(""))) {
            this.fillOutComponentWarning("URL");
            txtUrl.requestFocus();
            return;
        }
        String folder = txtFolder.getText().trim();
        File f = null;
        if (folder == null || folder.equals("")) {
            this.fillOutComponentWarning("target folder");
            txtFolder.requestFocus();
            return;
        } else {
            // Check for existance, and see that it is in fact a folder!
            f = new File(folder);
            if (!f.exists()) {
                String lMessage = "The folder you specified (" + folder + ") does not exist!";
                logger.error(lMessage);
                JOptionPane.showMessageDialog(this, lMessage, "Folder does not exist!", JOptionPane.ERROR_MESSAGE);
                txtFolder.requestFocus();
                return;
            } else if (!f.isDirectory()) {
                String lMessage = "The folder you specified (" + folder + ") is not a directory!";
                logger.error(lMessage);
                JOptionPane.showMessageDialog(this, lMessage, "Folder is not a directory!", JOptionPane.ERROR_MESSAGE);
                txtFolder.requestFocus();
                return;
            }
        }
        String filter = txtFilter.getText().trim();
        if (filter.equals("")) {
            filter = null;
        }
        String interval = txtInterval.getText().trim();
        long check = 0l;
        if (interval == null || interval.equals("")) {
            this.fillOutComponentWarning("checking interval");
            txtInterval.requestFocus();
            return;
        } else {
            try {
                check = Long.parseLong(interval);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                JOptionPane.showMessageDialog(this, "The interval you specified (" + interval + ") is not a whole number!", "Checking interval is not a whole number!", JOptionPane.ERROR_MESSAGE);
                txtInterval.requestFocus();
                return;
            }
        }

        // All components survived their validations.
        // Now set these components on the main GUI.
        iParent.setFTPParams(host, user, password, chkMergeFiles.isSelected());
        if (chkUSeDB.isSelected()) {
            iParent.setDBParams(driver, url, txtDBUser.getText().trim(), txtDBPassword.getText().trim());
        }
        iParent.setMonitoringParams(f, filter, check);
        iParent.setInitialized(true);
        this.setVisible(false);
        this.dispose();
    }

    /**
     * This method is called when the 'Cancel' button is pressed.
     */
    private void cancelPressed() {
        this.close();
    }

    /**
     * This method closes the window without any other action.
     */
    private void close() {
        this.setVisible(false);
        iParent.setInitialized(false);
        this.dispose();
    }

    /**
     * This method displays an error message about the necessity of filling out the specified component first.
     *
     * @param aComponent String with the description of the data for the component that needs to be filled out.
     */
    private void fillOutComponentWarning(String aComponent) {
        String lMessage = "You need to fill out the " + aComponent + " first!";
        logger.error(lMessage);
        JOptionPane.showMessageDialog(this, lMessage, aComponent + " not filled out!", JOptionPane.ERROR_MESSAGE);
    }
}
