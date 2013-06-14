/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-aug-2005
 * Time: 7:37:31
 */
package com.compomics.mslimscore.gui.dialogs;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.gui.interfaces.Informable;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2005/08/24 08:25:24 $
 */

/**
 * This class
 *
 * @author Lennart
 * @version $Id: QueryCacheDialog.java,v 1.1 2005/08/24 08:25:24 lennart Exp $
 */
public class QueryCacheDialog extends JDialog {
    // Class specific log4j logger for QueryCacheDialog instances.
    private static Logger logger = Logger.getLogger(QueryCacheDialog.class);

    /**
     * The String[] with the queries.
     */
    private String[] iQueries = null;

    /**
     * The String[] with the query designations.
     */
    private String[] iQueryNames = null;


    private JList lstQueries = null;
    private JTextArea txtQuery = null;

    private JButton btnUSe = null;
    private JButton btnCancel = null;

    /**
     * This constructor creates a QueryCacheDialog and lays out its components. Note that this dialog is always modal!
     *
     * @param aOwner JFrame with the owner of this dialog
     * @param aTitle String with the title for this dialog.
     */
    public QueryCacheDialog(JFrame aOwner, String aTitle, Vector aQueries) {
        super(aOwner, aTitle + " (" + aQueries.size() + " queries)", true);
        iQueries = new String[aQueries.size()];
        aQueries.toArray(iQueries);
        iQueryNames = new String[aQueries.size()];
        for (int i = 0; i < iQueryNames.length; i++) {
            iQueryNames[i] = "Query " + (i + 1);
        }

        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        this.constructScreen();
        Point parentLoc = aOwner.getLocation();
        int parentWidth = aOwner.getWidth();
        int parentHeight = aOwner.getHeight();
        this.setBounds(parentLoc.x + (int) (parentWidth / 12.5), parentLoc.y + (parentHeight / 4), (int) (parentWidth / 1.25), parentHeight / 2);
    }

    /**
     * This method onstructs and lays out the GUI.
     */
    private void constructScreen() {
        lstQueries = new JList(iQueryNames);
        lstQueries.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selected = lstQueries.getSelectedIndex();
                txtQuery.setText(iQueries[selected]);
                if (!txtQuery.getText().equals("")) {
                    txtQuery.setCaretPosition(1);
                }
            }
        });
        txtQuery = new JTextArea(10, 10);
        txtQuery.setEditable(false);
        txtQuery.setMinimumSize(txtQuery.getPreferredSize());

        JPanel jpanQueries = new JPanel(new BorderLayout());
        jpanQueries.add(new JScrollPane(lstQueries), BorderLayout.CENTER);

        JPanel jpanQuery = new JPanel(new BorderLayout());
        JScrollPane jsp = new JScrollPane(txtQuery);
        jsp.setMinimumSize(txtQuery.getPreferredSize());
        jpanQuery.add(jsp);

        JSplitPane splMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splMain.setOneTouchExpandable(true);
        splMain.add(jpanQueries);
        splMain.add(jpanQuery);

        JPanel jpanMain = new JPanel(new BorderLayout());
        jpanMain.add(splMain, BorderLayout.CENTER);
        jpanMain.add(this.getButtonPanel(), BorderLayout.SOUTH);

        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method creates the button panel.
     *
     * @return JPanel with the buttons.
     */
    private JPanel getButtonPanel() {
        btnUSe = new JButton("Use query");
        btnUSe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                usePressed();
            }
        });
        btnUSe.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    usePressed();
                }
            }
        });

        btnCancel = new JButton("Cancel");
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

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnUSe);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(5));

        return jpanButtons;
    }

    /**
     * This method is called when the user clicks 'use query'.
     */
    private void usePressed() {
        if (lstQueries.getSelectedIndex() != -1) {
            if (this.getParent() instanceof Informable) {
                Informable parent = (Informable) this.getParent();
                parent.inform(new Integer(lstQueries.getSelectedIndex()));
                this.close();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a query from the list first!", "No query selected", JOptionPane.WARNING_MESSAGE);
            lstQueries.requestFocus();
        }
    }

    /**
     * This method is called when the user clicks 'cancel'.
     */
    private void cancelPressed() {
        this.close();
    }

    /**
     * This method closes the dialog.
     */
    private void close() {
        this.setVisible(false);
        this.dispose();
    }
}
