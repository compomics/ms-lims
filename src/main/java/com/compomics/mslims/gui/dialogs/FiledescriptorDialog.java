/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 05-mar-2005
 * Time: 15:01:36
 */
package com.compomics.mslims.gui.dialogs;

import com.compomics.mslims.db.accessors.Filedescriptor;
import com.compomics.mslims.gui.StoreBinaryFileGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/31 12:39:40 $
 */

/**
 * This class implements a generic interface to a Filedescriptor (can be used to add or
 * change a file descriptor).
 *
 * @author Lennart Martens
 */
public class FiledescriptorDialog extends JDialog {

    /**
     * The mode of alteration for which this dialog was opened.
     */
    private int iMode = -1;

    /**
     * The Filedescriptor that has to be altered (if any).
     */
    private Filedescriptor iFiledescriptor = null;

    /**
     * The DB connection on which alterations are to be saved.
     */
    private Connection iConn = null;

    /**
     * If the parent was StoreBinaryFileGUI, it is kept here.
     */
    private StoreBinaryFileGUI iSBF = null;

    /**
     * Constant for the 'new' mode of opening.
     */
    public static final int NEW = 0;

    /**
     * Constant for the 'change' mode of opening.
     */
    public static final int CHANGE = 1;

    private JTextField txtShortLabel = null;
    private JTextField txtUsername = null;
    private JTextField txtCreationDate = null;
    private JTextField txtModificationDate = null;
    private JTextArea txtDescription = null;
    private JButton btnCreate = null;
    private JButton btnSave = null;
    private JButton btnCancel = null;

    /**
     * Date-time format String.
     */
    private static final String iDateTimeFormat = "dd/MM/yyyy - HH:mm:ss";

    /**
     * The SimpleDateFormat formatter to display creationdates.
     */
    private static SimpleDateFormat iSDF = new SimpleDateFormat(iDateTimeFormat);


    /**
     * Wrapper for the superclass constructor that creates a modal JDialog
     * with given parent and title. This Dialog should always be modal, so
     * that's why the boolean is ommitted here. <br />
     * It also takes an int that tells of the mode of editing that is required, along
     * with the project that needs be altered (if any). <br />
     * Valid settings are:
     * <ul>
     *   <li><b>FiledescriptorDialog.NEW</b>: creation of a new file descriptor</li>
     *   <li><b>FiledescriptorDialog.CHANGE</b>: perform some changes to an existing filedescriptor</li>
     * </ul>
     *
     * @param aParent   Frame that is the owner of this JDialog
     * @param aTitle    String with the title for the Dialog
     * @param aMode int with the mode of editing required.
     * @param aFiledescriptor  Filedescriptor to be altered.
     *                  This need NOT be specified for the creation of a new project
     *                  and therefore can be 'null'.
     * @param aConn Connection to store all alterations on.
     */
    public FiledescriptorDialog(Frame aParent, String aTitle, int aMode, Filedescriptor aFiledescriptor, Connection aConn) {
        super(aParent, aTitle, true);

        // Check the parameters.
        if(aMode == FiledescriptorDialog.CHANGE && aFiledescriptor == null) {
            throw new IllegalArgumentException("Attempting to change a file descriptor that is 'null'!");
        }

        if(aParent instanceof StoreBinaryFileGUI) {
            this.iSBF = (StoreBinaryFileGUI)aParent;
        }
        this.iConn = aConn;
        this.iMode = aMode;
        this.iFiledescriptor = aFiledescriptor;

        // GUI stuff.
        this.constructScreen();
    }

    /**
     * This method constructs and lays out the screen components.
     */
    private void constructScreen() {

        // Components.
        txtShortLabel = new JTextField(20);
        txtShortLabel.setMaximumSize(new Dimension(txtShortLabel.getMaximumSize().width, txtShortLabel.getPreferredSize().height));
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
        txtDescription.setMaximumSize(txtDescription.getPreferredSize());


        // The labels for the project details fields.
        JLabel lblTitle = new JLabel("   Short label: ");
        lblTitle.setPreferredSize(new Dimension(lblTitle.getPreferredSize().width, txtShortLabel.getPreferredSize().height));
        JLabel lblUsername = new JLabel("   Created by: ");
        lblUsername.setPreferredSize(new Dimension(lblUsername.getPreferredSize().width, txtUsername.getPreferredSize().height));
        JLabel lblCreationdate = new JLabel("   Creationdate: ");
        lblCreationdate.setPreferredSize(new Dimension(lblCreationdate.getPreferredSize().width, txtCreationDate.getPreferredSize().height));
        JLabel lblModificationdate = new JLabel("   Modificationdate: ");
        lblModificationdate.setPreferredSize(new Dimension(lblModificationdate.getPreferredSize().width, txtModificationDate.getPreferredSize().height));
        JLabel lblDescription = new JLabel("   Description: ");
        lblDescription.setPreferredSize(new Dimension(lblDescription.getPreferredSize().width, txtCreationDate.getPreferredSize().height));
        JPanel jpanLabels = new JPanel();
        jpanLabels.setLayout(new BoxLayout(jpanLabels, BoxLayout.Y_AXIS));
        jpanLabels.add(lblTitle);
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
        JPanel jpanTitle = new JPanel();
        jpanTitle.setLayout(new BoxLayout(jpanTitle, BoxLayout.X_AXIS));
        jpanTitle.add(txtShortLabel);
        jpanTitle.add(Box.createHorizontalGlue());

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
        jpanFields.add(jpanTitle);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(jpanUsername);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(jpanCreationDate);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(jpanModificationDate);
        jpanFields.add(Box.createVerticalStrut(5));
        jpanFields.add(new JScrollPane(txtDescription));

        // This panel holds the project details labels and textfields.
        JPanel jpanProjectDetails = new JPanel();
        jpanProjectDetails.setLayout(new BoxLayout(jpanProjectDetails, BoxLayout.X_AXIS));
        jpanProjectDetails.setBorder(BorderFactory.createTitledBorder("File descriptor details"));


        // Adding labels and fields to the project details panel.
        jpanProjectDetails.add(jpanLabels);
        jpanProjectDetails.add(Box.createHorizontalStrut(15));
        jpanProjectDetails.add(jpanFields);

        // Combine the project pulldown and the projectdetails.
        JPanel jpanProjects = new JPanel();
        jpanProjects.setLayout(new BoxLayout(jpanProjects, BoxLayout.Y_AXIS));
        jpanProjects.add(Box.createVerticalStrut(5));
        jpanProjects.add(jpanProjectDetails);

        JPanel jpanButtons = this.createButtonPanel();

        JPanel main = new JPanel(new BorderLayout());
        main.add(jpanProjects, BorderLayout.CENTER);
        main.add(jpanButtons, BorderLayout.SOUTH);

        if(iMode == FiledescriptorDialog.CHANGE) {
            this.fillComponents();
        }

        this.getContentPane().add(main, BorderLayout.CENTER);
        this.pack();
    }

    /**
     * This method creates the buttonpanel for this dialog.
     *
     * @return  JPanel  with the buttons.
     */
    private JPanel createButtonPanel() {
        btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic(KeyEvent.VK_C);
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
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cancelPressed();
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());

        if(iMode == FiledescriptorDialog.NEW) {
            btnCreate = new JButton("Create");
            btnCreate.setMnemonic(KeyEvent.VK_R);
            btnCreate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createPressed();
                }
            });
            btnCreate.addKeyListener(new KeyAdapter() {
                /**
                 * Invoked when a key has been pressed.
                 */
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        createPressed();
                    }
                }
            });
            jpanButtons.add(btnCreate);
        } else {
            btnSave = new JButton("Save");
            btnSave.setMnemonic(KeyEvent.VK_S);
            btnSave.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    savePressed();
                }
            });
            btnSave.addKeyListener(new KeyAdapter() {
                /**
                 * Invoked when a key has been pressed.
                 */
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        savePressed();
                    }
                }
            });
            jpanButtons.add(btnSave);
        }

        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(10));

        return jpanButtons;
    }

    /**
     * This method is called when the user presses 'cancel'.
     */
    private void cancelPressed() {
        this.dispose();
    }

    /**
     * This method is called when the user presses 'create'.
     */
    private void createPressed() {
        try {
            // Get all the data + do validations.
            HashMap hm = this.getDataFromScreen();
            // See if the validations passed.
            if(hm == null) {
                // Apparently not.
                return;
            }
            // Create the Project object.
            Filedescriptor fd = new Filedescriptor(hm);
            // Persist it.
            fd.persist(iConn);
        } catch(SQLException sqle) {
            JOptionPane.showMessageDialog(this, new String[]{"Unable to create file descriptor:  ", sqle.getMessage()}, "Unable to create file descriptor!", JOptionPane.ERROR_MESSAGE);
            return;
        } catch(Throwable t) {
            if(iSBF != null) {
                iSBF.passHotPotato(t, "Unable to create new file descriptor: " + t.getMessage());
            } else {
                t.printStackTrace();
            }
        }
        // Confirm creation of new project to user.
        JOptionPane.showMessageDialog(this, "Created file descriptor '" + txtShortLabel.getText().trim() + "'.", "Create successufl!", JOptionPane.INFORMATION_MESSAGE);
        // Notify StoreBinaryFileGUI (if any).
        if(iSBF != null) {
            iSBF.fileDescriptorsChanged();
        }
        // Begone!
        this.dispose();
    }

    /**
     * This method is called when the user presses 'save'.
     */
    private void savePressed() {
        boolean error = true;
        try {
            // Get all the data + do validations.
            HashMap hm = this.getDataFromScreen();
            // See if the validations passed.
            if(hm == null) {
                // Apparently not.
                return;
            }
            // Change the existing project.
            iFiledescriptor.setShort_label((String)hm.get(Filedescriptor.SHORT_LABEL));
            iFiledescriptor.setDescription((String)hm.get(Filedescriptor.DESCRIPTION));
            iFiledescriptor.update(iConn);
            error = false;
        } catch(SQLException sqle) {
            JOptionPane.showMessageDialog(this, new String[]{"Unable to save modified file descriptor:  ", sqle.getMessage()}, "Unable to save modified file descriptor!", JOptionPane.ERROR_MESSAGE);
        } catch(Throwable t) {
             if(iSBF != null) {
                iSBF.passHotPotato(t, "Unable to save modified file descriptor: " + t.getMessage());
            } else {
                t.printStackTrace();
            }
        }
        // Confirm save to user.
        if(!error) {
            JOptionPane.showMessageDialog(this, "Saved modified file descriptor '" + txtShortLabel.getText().trim() + "'.", "Save successufl!", JOptionPane.INFORMATION_MESSAGE);
        }
        // Notify StoreBinaryFileGUI (if any).
        if(iSBF != null) {
            iSBF.fileDescriptorsChanged();
        }
        // Begone!
        this.dispose();
    }

    /**
     * This method fills out the information in the iFiledescriptor into the textfields.
     */
    private void fillComponents() {
        txtShortLabel.setText(iFiledescriptor.getShort_label());
        txtUsername.setText(iFiledescriptor.getUsername());
        txtCreationDate.setText(iSDF.format(iFiledescriptor.getCreationdate()));
        txtModificationDate.setText(iSDF.format(iFiledescriptor.getModificationdate()));
        txtDescription.setText(iFiledescriptor.getDescription());
    }

    /**
     * This method gets all data from the GUI components and does the validations.
     * If it returns 'null', a validation has failed and the operation should be aborted.
     *
     * @return  HashMap with the filled-out parameters from the GUI, or 'null' if a
     *                  validation failed.
     */
    private HashMap getDataFromScreen() {
        HashMap hm = null;
        // Get the data in the textfields.
        String shortLabel = txtShortLabel.getText().trim();
        String description = txtDescription.getText();


        // See if the not-NULL fields are filled out.
        if(shortLabel.equals("")) {
            JOptionPane.showMessageDialog(this, "File descriptor short label must be filled out!", "Short label has to be filled out!", JOptionPane.WARNING_MESSAGE);
            txtShortLabel.requestFocus();
        } else {
            // Creation of the param HashMap.
            hm = new HashMap(3);
            hm.put(Filedescriptor.SHORT_LABEL, shortLabel);
            hm.put(Filedescriptor.DESCRIPTION, description);
        }

        return hm;
    }
}
