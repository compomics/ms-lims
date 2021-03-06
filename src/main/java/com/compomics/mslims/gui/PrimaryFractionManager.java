/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 23-jun-2003
 * Time: 18:12:46
 */
package com.compomics.mslims.gui;

import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.mslimscore.gui.dialogs.ConnectionDialog;
import org.apache.log4j.Logger;

import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.io.PropertiesManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class implements a GUI for LCRun-primary fraction management using the 'primary_fraction' column in the LCRun
 * table.
 *
 * @author Lennart Martens
 */
public class PrimaryFractionManager extends JFrame implements Connectable, Flamable {
    // Class specific log4j logger for PrimaryFractionManager instances.
    private static Logger logger = Logger.getLogger(PrimaryFractionManager.class);

    /**
     * This variable holds the connection to the DB.
     */
    private Connection iConn = null;

    /**
     * This array holds all the LCRun which have not been assigned to a primary fraction.
     */
    private LCRun[] iCapLC = null;

    /**
     * This variable holds the identifier for the DB connection, typically the name of the database that has been
     * connected.
     */
    private String iDBName = null;

    /**
     * The HashMap with the assignments.
     */
    private HashMap iAssignments = new HashMap();

    /**
     * This variable holds the maximum age in days that the lcruns can have in order to be selected for display. When it
     * is '-1', no maximum age is used.
     */
    private int iMaximumAgeInDays = -1;

    private static final String propsFile = "PrimaryFractionManager.properties";


    private JList lstCapLC = null;
    private JTextField txtNumber = null;
    private JTextArea txtSummary = null;
    private JButton btnAssign = null;
    private JButton btnStore = null;
    private JButton btnClear = null;
    private JButton btnExit = null;

    /**
     * This constructor serves as a wrapper for the superclass constructor (JFrame).
     *
     * @param aTitle String with the title for the JFrame.
     */
    public PrimaryFractionManager(String aTitle) {
        super(aTitle);
        // Read the props.
        this.readProperties();
        // Connection dialog display.
        this.getConnection();
        // Read LCRun data.
        this.readCapLCData();
        // Fill out the JList.
        this.fillOutCapLCData();
        // Create the GUI.
        this.constructScreen();
        // Allow exit.
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        // Pack.
        this.pack();
        // Display settings.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screen.width / 10), (screen.height / 10));
        //this.setSize(700, 500);
    }

    /**
     * The main method defines the entry point for the application.
     *
     * @param args String[] with the start-up parameters.
     */
    public static void main(String[] args) {
        PrimaryFractionManager bm = new PrimaryFractionManager("Primary fraction manager.");
        bm.setVisible(true);
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
            this.close();
        }
        this.iConn = aConn;
        this.iDBName = aDBName;
    }

    /**
     * This method takes care of any unrecoverable exception or error, thrown by a child thread.
     *
     * @param aThrowable Throwable that represents the unrecoverable error or exception.
     */
    public void passHotPotato(Throwable aThrowable) {
        this.passHotPotato(aThrowable, null);
    }

    /**
     * This method takes care of any unrecoverable exception or error, thrown by a child thread.
     *
     * @param aThrowable Throwable that represents the unrecoverable error or exception.
     * @param aMessage   String with an extra message to display.
     */
    public void passHotPotato(Throwable aThrowable, String aMessage) {
        String[] messages = null;
        if (aMessage != null) {
            messages = new String[]{"Fatal error encountered in application!", aMessage, aThrowable.getMessage(), "\n"};
        } else {
            messages = new String[]{"Fatal error encountered in application!", aThrowable.getMessage(), "\n"};
        }
        logger.error(aThrowable.getMessage(), aThrowable);
        JFrame tempFrame = new JFrame();
        JOptionPane.showMessageDialog(tempFrame, messages, "Application unexpectedly terminated!", JOptionPane.ERROR_MESSAGE);
        tempFrame.dispose();
        System.exit(1);
    }

    /**
     * This method initializes and lays-out all GUI components on the screen.
     */
    private void constructScreen() {
        // Initialize components.
        txtNumber = new JTextField(12);
        txtNumber.setMaximumSize(txtNumber.getPreferredSize());
        txtNumber.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignTriggered();
            }
        });
        txtSummary = new JTextArea(15, 45);
        txtSummary.setEditable(false);
        txtSummary.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Panel for the list.
        JPanel jpanCapLC = new JPanel(new BorderLayout());
        jpanCapLC.setBorder(BorderFactory.createTitledBorder("LC run list"));
        jpanCapLC.add(new JScrollPane(lstCapLC), BorderLayout.CENTER);
        int width = lstCapLC.getPreferredSize().width;
        if (width > 10) {
            jpanCapLC.setPreferredSize(new Dimension(width + 50, jpanCapLC.getPreferredSize().height));
        }

        // Medium designation.
        String primFract = "Primary fraction";

        // Panel for the number textfield.
        JPanel jpanNumber = new JPanel();
        jpanNumber.setLayout(new BoxLayout(jpanNumber, BoxLayout.X_AXIS));
        jpanNumber.setBorder(BorderFactory.createTitledBorder(primFract + " number"));
        jpanNumber.add(Box.createHorizontalStrut(15));
        jpanNumber.add(new JLabel(primFract + " number: "));
        jpanNumber.add(Box.createHorizontalStrut(5));
        jpanNumber.add(txtNumber);
        jpanNumber.add(Box.createHorizontalGlue());
        jpanNumber.setMaximumSize(new Dimension(jpanNumber.getMaximumSize().width, txtNumber.getPreferredSize().height));

        // Panel for the summary text area.
        JPanel jpanSummary = new JPanel(new BorderLayout());
        jpanSummary.setBorder(BorderFactory.createTitledBorder("Summary"));
        jpanSummary.add(new JScrollPane(txtSummary), BorderLayout.CENTER);

        // Panel to group summary and number panels.
        JPanel jpanRight = new JPanel();
        jpanRight.setLayout(new BoxLayout(jpanRight, BoxLayout.Y_AXIS));
        jpanRight.add(jpanNumber);
        jpanRight.add(Box.createVerticalStrut(15));
        jpanRight.add(jpanSummary);

        this.getContentPane().add(jpanCapLC, BorderLayout.WEST);
        this.getContentPane().add(this.createButtonPanel(), BorderLayout.SOUTH);
        this.getContentPane().add(jpanRight, BorderLayout.CENTER);
    }

    /**
     * This method calls upon a GUI component to handle the connection.
     */
    private void getConnection() {

        ConnectionDialog cd = new ConnectionDialog(this, this, "Database connection for PrimaryFractionManager", propsFile);
        cd.setVisible(true);
    }

    /**
     * This method creates a panel with the buttons for this application.
     *
     * @return JPanel  with the buttons.
     */
    private JPanel createButtonPanel() {
        btnAssign = new JButton("Assign LC run(s) to primary fractions");
        btnAssign.setMnemonic(KeyEvent.VK_A);
        btnAssign.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignTriggered();
            }
        });
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

        btnStore = new JButton("Store");
        btnStore.setMnemonic(KeyEvent.VK_S);
        btnStore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeTriggered();
            }
        });
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

        btnClear = new JButton("Clear");
        btnClear.setMnemonic(KeyEvent.VK_C);
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearTriggered();
            }
        });
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

        btnExit = new JButton("Exit");
        btnExit.setMnemonic(KeyEvent.VK_X);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitTriggered();
            }
        });
        btnExit.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    exitTriggered();
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnAssign);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnStore);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnClear);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnExit);
        jpanButtons.add(Box.createHorizontalStrut(10));

        return jpanButtons;
    }

    /**
     * This method si called when the user clicks the assign button.
     */
    private void assignTriggered() {
        // Get the data from the GUI.
        Object[] selection = lstCapLC.getSelectedValues();
        String numberString = txtNumber.getText().trim();
        // See if anything is selected.
        if (selection == null || selection.length == 0) {
            JOptionPane.showMessageDialog(this, "No items selected in the LC run list to assign!", "Nothing selected!", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String primFract = "primary fraction";
        // See if a number has been typed.
        if (numberString.equals("")) {
            JOptionPane.showMessageDialog(this, "You have to type a " + primFract + " number to assign the LC run files to!", "No " + primFract + " number entered!", JOptionPane.WARNING_MESSAGE);
            txtNumber.requestFocus();
            return;
        }
        Long number = null;
        try {
            number = new Long(numberString);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "The " + primFract + " number has to be a positive integer number, you typed '" + numberString + "' instead!", "Incorrect " + primFract + " number!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Vector tempVec = new Vector(selection.length);
        for (int i = 0; i < selection.length; i++) {
            LCRun tempCaplc = (LCRun) selection[i];
            tempCaplc.setAssigned(true);
            tempVec.add(tempCaplc);
        }
        // Add the assignments.
        if (iAssignments.containsKey(number)) {
            ((Vector) iAssignments.get(number)).addAll(tempVec);
        } else {
            iAssignments.put(number, tempVec);
        }
        this.fillOutCapLCData();
        this.updateSummary();
        txtNumber.setText("");
    }

    /**
     * This method is called when the user clicks the store button.
     */
    private void storeTriggered() {
        try {
            Iterator it = iAssignments.keySet().iterator();
            int count = 0;
            while (it.hasNext()) {
                Long number = (Long) it.next();
                long nr = number.longValue();
                Vector tempVec = (Vector) iAssignments.get(number);
                for (int i = 0; i < tempVec.size(); i++) {
                    LCRun lCapLC = (LCRun) tempVec.elementAt(i);
                    lCapLC.setPrimary_fraction(nr);
                    lCapLC.update(iConn);
                    count++;
                }
            }
            JOptionPane.showMessageDialog(this, "Completed primary fraction assignments for " + count + " LC runs.", "Store successfully completed!", JOptionPane.INFORMATION_MESSAGE);
            txtSummary.setText("");
            iAssignments = new HashMap();
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, new String[]{"Unable to store the assignments in the DB:", sqle.getMessage()}, "Unable to store primary fraction information!", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable t) {
            this.passHotPotato(t, "Unable to store primary fraction information!");
        }
    }

    /**
     * This method is called when the user clicks the clear button.
     */
    private void clearTriggered() {
        iAssignments = new HashMap();
        for (int i = 0; i < iCapLC.length; i++) {
            LCRun lCapLC = iCapLC[i];
            if (lCapLC.getPrimary_fraction() <= 0) {
                lCapLC.setAssigned(false);
            }
        }
        txtSummary.setText("");
        txtNumber.setText("");
        fillOutCapLCData();
    }

    /**
     * This method is called when the user clicks the exit button.
     */
    private void exitTriggered() {
        this.close();
    }

    /**
     * This method creates and displays the summary.
     */
    private void updateSummary() {
        StringBuffer sb = new StringBuffer();
        Iterator it = iAssignments.keySet().iterator();
        int counter = 0;
        String primFraction = "Primary fraction";

        while (it.hasNext()) {
            if (counter != 0) {
                sb.append("\n");
            }
            Long key = (Long) it.next();
            String number = key.toString();
            sb.append(" " + primFraction + " nr. " + number + ":\n ");
            int length = 6 + primFraction.length() + number.length();
            for (int i = 0; i < length; i++) {
                sb.append("-");
            }
            sb.append("\n");
            Vector tempVec = (Vector) iAssignments.get(key);
            for (int i = 0; i < tempVec.size(); i++) {
                sb.append("   + " + tempVec.elementAt(i).toString() + "\n");
            }
            counter++;
        }
        txtSummary.setText(sb.toString());
    }

    /**
     * This method should be called to close this application.
     */
    private void close() {
        if (this.iConn != null) {
            try {
                this.iConn.close();
                logger.info("DB connection closed.");
            } catch (SQLException sqle) {
                logger.error(sqle.getMessage(), sqle);
            }
        }
        this.dispose();
        System.exit(0);
    }

    /**
     * This method reads all LCRun entries from the iConn connection that have 'NULL' for DVD or CD number (depending on
     * storage type selected by user at start-up) fields.
     */
    private void readCapLCData() {
        if (iConn != null) {
            try {
                iCapLC = LCRun.getLCRunsWithoutPrimFractionNotOlderThan(iConn, iMaximumAgeInDays);
            } catch (SQLException sqle) {
                this.passHotPotato(sqle, "Unable to retrieve LC run list!");
            }
        }
    }

    /**
     * This method sets the LCRun data in iCapLC on the JList 'lstCapLC'.
     */
    private void fillOutCapLCData() {
        if (lstCapLC == null) {
            lstCapLC = new JList();
        }
        Vector temp = new Vector();
        for (int i = 0; i < iCapLC.length; i++) {
            LCRun lCapLC = iCapLC[i];
            if (!lCapLC.isAssigned()) {
                temp.add(lCapLC);
            }
        }
        Object[] tempData = new Object[temp.size()];
        temp.toArray(tempData);
        lstCapLC.setListData(tempData);
        lstCapLC.clearSelection();
    }

    /**
     * This method attempts to read the maximum age property from the props file.
     */
    private void readProperties() {
        Properties props = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "IdentificationGUI.properties");
        String value = props.getProperty("pastdays");
        if (value != null) {
            value = value.trim();
            iMaximumAgeInDays = Integer.parseInt(value);
        }
    }
}
