/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 27-Jun-2008
 * Time: 10:35:34
 */
package com.compomics.mslimscore.gui.dialogs;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.sql.Connection;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2008/06/27 10:45:55 $
 */

/**
 * This class represents a modal dialog that takes a fully qualified classname, attempts to load the class, and
 * subsequently calls its constructor with exactly two arguments, in the following order: a Connection, and a String
 * with the name of the connection.
 *
 * @author Lennart Martens
 * @version $Id: CustomLauncherDialog.java,v 1.1 2008/06/27 10:45:55 lennart Exp $
 */
public class CustomLauncherDialog extends JDialog {
    // Class specific log4j logger for CustomLauncherDialog instances.
    private static Logger logger = Logger.getLogger(CustomLauncherDialog.class);

    /**
     * The database connection to forward.
     */
    private Connection iConn = null;

    /**
     * The database name to forward.
     */
    private String iDBName = null;

    private JTextField txtClassname = null;


    /**
     * This constructor takes the database connection, as well as the database name to pass on.
     *
     * @param aConn   Connection to pass on
     * @param aDBName String with the name of the database connection.
     */
    public CustomLauncherDialog(Connection aConn, String aDBName) {
        this.iConn = aConn;
        this.iDBName = aDBName;
        this.setModal(true);
        this.setTitle("Custom application launcher");
        this.initializeScreen();
        this.pack();
    }

    private void initializeScreen() {
        JLabel lblClassname = new JLabel("Please provide the fully qualified classname");
        lblClassname.setFont(lblClassname.getFont().deriveFont(Font.ITALIC));

        txtClassname = new JTextField(50);
        txtClassname.setMaximumSize(new Dimension(txtClassname.getMaximumSize().width, txtClassname.getPreferredSize().height));

        JPanel jpanClassname = new JPanel();
        jpanClassname.setLayout(new BoxLayout(jpanClassname, BoxLayout.X_AXIS));
        jpanClassname.add(Box.createHorizontalStrut(10));
        jpanClassname.add(txtClassname);
        jpanClassname.add(Box.createHorizontalStrut(10));

        JPanel jpanButtons = this.getButtonPanel();

        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(Box.createVerticalGlue());
        jpanMain.add(lblClassname);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanClassname);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanButtons);
        jpanMain.add(Box.createVerticalStrut(5));

        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    private JPanel getButtonPanel() {

        JButton btnExecute = new JButton("Execute");
        btnExecute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnExecutePressed();
            }
        });
        btnExecute.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnExecutePressed();
                }
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        btnCancel.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    close();
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnExecute);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(15));

        return jpanButtons;
    }

    private void btnExecutePressed() {
        if (txtClassname.getText() == null || txtClassname.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Please provide a fully qualified classname to execute.", "No classname provided!", JOptionPane.WARNING_MESSAGE);
            txtClassname.requestFocus();
            return;
        }
        // Right, we've got a classname.
        String classname = txtClassname.getText().trim();
        // First try to load the class.
        Class classToExecute = null;
        try {
            classToExecute = Class.forName(classname);
        } catch (ClassNotFoundException cnfe) {
            JOptionPane.showMessageDialog(this, new String[]{"Unable to find the class you specified.", "Please verify that the classname is correctly specified,", " that the full package is correctly included,", " and that it can be found on the classpath!"}, "Class not found!", JOptionPane.WARNING_MESSAGE);
            txtClassname.requestFocus();
            return;
        }
        // OK, we've got a class. Now try to invoke the right constructor.
        try {
            Constructor toCall = classToExecute.getConstructor(Connection.class, String.class);
            // Right, we have a constructor, call it!
            Object[] arguments = new Object[]{iConn, iDBName};
            toCall.newInstance(arguments);
            // We're done. Close this dialog.
            close();
        } catch (NoSuchMethodException nsme) {
            this.invocationFailed();
        } catch (InstantiationException ie) {
            this.invocationFailed();
        } catch (InvocationTargetException ite) {
            this.invocationFailed();
        } catch (IllegalAccessException iae) {
            this.invocationFailed();
        }
    }

    private void invocationFailed() {
        String lMessage = "Unable to find or call the correct constructor on the class you specified.";
        logger.error(lMessage);
        JOptionPane.showMessageDialog(this, new String[]{lMessage, "Please verify that a public constructor accepting a:", "   - java.sql.Collection", " and a ", "   - java.lang.String", "as arguments (in that order) is available!"}, "Class not found!", JOptionPane.ERROR_MESSAGE);
        txtClassname.requestFocus();
        return;
    }

    private void close() {
        this.setVisible(false);
        this.dispose();
    }
}
