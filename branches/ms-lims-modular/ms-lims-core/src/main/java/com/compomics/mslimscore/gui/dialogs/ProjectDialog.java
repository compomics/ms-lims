/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 20-jun-2003
 * Time: 15:01:36
 */
package com.compomics.mslimscore.gui.dialogs;

import org.apache.log4j.Logger;

import com.compomics.mslimsdb.accessors.Protocol;
import com.compomics.mslimsdb.accessors.Project;
import com.compomics.mslimsdb.accessors.User;
import com.compomics.mslimscore.gui.interfaces.ProjectManager;

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
 * $Revision: 1.4 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class implements a generic interface to a Project (can be used to add, change or delete a project).
 *
 * @author Lennart Martens
 */
public class ProjectDialog extends JDialog {
    // Class specific log4j logger for ProjectDialog instances.
    private static Logger logger = Logger.getLogger(ProjectDialog.class);

    /**
     * The mode of alteration for which this dialog was opened.
     */
    private int iMode = -1;

    /**
     * The project that has to be altered (if any).
     */
    private Project iProject = null;

    /**
     * The DB connection on which alterations are to be saved.
     */
    private Connection iConn = null;

    /**
     * If the parent was ProjectManager, it is kept here.
     */
    private ProjectManager iSS = null;

    /**
     * The users form the DB.
     */
    private User[] iUsers = null;

    /**
     * The PROTOCOL types from the DB.
     */
    private Protocol[] iProtocol = null;


    /**
     * Constant for the 'new' mode of opening.
     */
    public static final int NEW = 0;

    /**
     * Constant for the 'change' mode of opening.
     */
    public static final int CHANGE = 1;

    private JComboBox cmbUser = null;
    private JComboBox cmbProtocol = null;
    private JTextField txtTitle = null;
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
     * Wrapper for the superclass constructor that creates a modal JDialog with given parent and title. This Dialog
     * should always be modal, so that's why the boolean is ommitted here. <br /> It also takes an int that tells of the
     * mode of editing that is required, along with the project that needs be altered (if any). <br /> Valid settings
     * are: <ul> <li><b>ProjectDialog.NEW</b>: creation of a new project</li> <li><b>ProjectDialog.CHANGE</b>: perform
     * some changes to an existing project</li> </ul>
     *
     * @param aParent  Frame that is the owner of this JDialog
     * @param aTitle   String with the title for the DIalog
     * @param aMode    int with the mode of editing required.
     * @param aProject Project to be altered. This need NOT be specified for the creation of a new project and therefore
     *                 can be 'null'.
     * @param aConn    Connection to store all alterations on.
     */
    public ProjectDialog(Frame aParent, String aTitle, int aMode, Project aProject, Connection aConn) {
        super(aParent, aTitle, true);

        // Check the parameters.
        if (aMode == ProjectDialog.CHANGE && aProject == null) {
            throw new IllegalArgumentException("Attempting to change a project that is 'null'!");
        }

        if (aParent instanceof ProjectManager) {
            this.iSS = (ProjectManager) aParent;
        }
        this.iConn = aConn;
        this.iMode = aMode;
        this.iProject = aProject;

        // Load user data.
        if (iUsers == null) {
            this.loadUsers();
        }

        // Load PROTOCOL data.
        if (iProtocol == null) {
            this.loadProtocol();

        }
        // GUI stuff.
        this.constructScreen();
    }

    /**
     * This method constructs and lays out the screen components.
     */
    private void constructScreen() {

        // Components.
        txtTitle = new JTextField(20);
        txtTitle.setMaximumSize(new Dimension(txtTitle.getMaximumSize().width, txtTitle.getPreferredSize().height));
        cmbUser = new JComboBox(iUsers);
        cmbUser.setMaximumSize(new Dimension(cmbUser.getPreferredSize().width, cmbUser.getPreferredSize().height));
        cmbProtocol = new JComboBox(iProtocol);
        cmbProtocol.setMaximumSize(new Dimension(cmbProtocol.getPreferredSize().width, cmbProtocol.getPreferredSize().height));
        txtUsername = new JTextField(20);
        txtUsername.setEditable(false);
        txtUsername.setMaximumSize(txtUsername.getPreferredSize());
        txtModificationDate = new JTextField(20);
        txtModificationDate.setEditable(false);
        txtModificationDate.setMaximumSize(txtModificationDate.getPreferredSize());
        txtCreationDate = new JTextField(20);
        txtCreationDate.setEditable(false);
        txtCreationDate.setMaximumSize(txtCreationDate.getPreferredSize());
        txtDescription = new JTextArea(8, 20);
        txtDescription.setMaximumSize(txtDescription.getPreferredSize());


        // The labels for the project details fields.
        JLabel lblTitle = new JLabel("   Project title: ");
        lblTitle.setPreferredSize(new Dimension(lblTitle.getPreferredSize().width, txtTitle.getPreferredSize().height));
        JLabel lblResponsible = new JLabel("   Project responsible: ");
        lblResponsible.setPreferredSize(new Dimension(lblResponsible.getPreferredSize().width, cmbUser.getPreferredSize().height));
        JLabel lblProtocol = new JLabel("   PROTOCOL type: ");
        lblProtocol.setPreferredSize(new Dimension(lblProtocol.getPreferredSize().width, cmbProtocol.getPreferredSize().height));
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
        JPanel jpanTitle = new JPanel();
        jpanTitle.setLayout(new BoxLayout(jpanTitle, BoxLayout.X_AXIS));
        jpanTitle.add(txtTitle);
        jpanTitle.add(Box.createHorizontalGlue());

        JPanel jpanResponsible = new JPanel();
        jpanResponsible.setLayout(new BoxLayout(jpanResponsible, BoxLayout.X_AXIS));
        jpanResponsible.add(cmbUser);
        jpanResponsible.add(Box.createHorizontalGlue());

        JPanel jpanProtocol = new JPanel();
        jpanProtocol.setLayout(new BoxLayout(jpanProtocol, BoxLayout.X_AXIS));
        jpanProtocol.add(cmbProtocol);
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

        // This panel holds the project details labels and textfields.
        JPanel jpanProjectDetails = new JPanel();
        jpanProjectDetails.setLayout(new BoxLayout(jpanProjectDetails, BoxLayout.X_AXIS));
        jpanProjectDetails.setBorder(BorderFactory.createTitledBorder("Project details"));


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

        if (iMode == ProjectDialog.CHANGE) {
            this.fillComponents();
        }

        this.getContentPane().add(main, BorderLayout.CENTER);
        this.pack();
    }

    /**
     * This method creates the buttonpanel for this dialog.
     *
     * @return JPanel  with the buttons.
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
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cancelPressed();
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());

        if (iMode == ProjectDialog.NEW) {
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
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
            if (hm == null) {
                // Apparently not.
                return;
            }
            // Create the Project object.
            Project p = new Project(hm);
            // Persist it.
            p.persist(iConn);
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, new String[]{"Unable to create project:  ", sqle.getMessage()}, "Unable to create project!", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (Throwable t) {

            if (iSS != null) {
                iSS.passHotPotato(t, "Unable to create new project!");
            } else {
                logger.error(t.getMessage(), t);
            }
        }
        // Confirm creation of new project to user.
        JOptionPane.showMessageDialog(this, "Created project '" + txtTitle.getText().trim() + "'.", "Create successufl!", JOptionPane.INFORMATION_MESSAGE);
        // Notify QTOFSpectrumStorage (if any).
        if (iSS != null) {
            iSS.projectsChanged();
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
            if (hm == null) {
                // Apparently not.
                return;
            }
            // Change the existing project.
            iProject.setTitle((String) hm.get(Project.TITLE));
            iProject.setL_userid(((Long) hm.get(Project.L_USERID)).longValue());
            iProject.setL_protocolid(((Long) hm.get(Project.L_PROTOCOLID)).longValue());
            iProject.setDescription((String) hm.get(Project.DESCRIPTION));
            iProject.update(iConn);
            error = false;
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, new String[]{"Unable to save modified project:  ", sqle.getMessage()}, "Unable to save modified project!", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable t) {
            if (iSS != null) {
                iSS.passHotPotato(t, "Unable to save modified project!");
            } else {
                logger.error(t.getMessage(), t);
            }
        }
        // Confirm save to user.
        if (!error) {
            JOptionPane.showMessageDialog(this, "Saved modified project '" + txtTitle.getText().trim() + "'.", "Save successufl!", JOptionPane.INFORMATION_MESSAGE);
        }
        // Notify QTOFSpectrumStorage (if any).
        if (iSS != null) {
            iSS.projectsChanged();
        }
        // Begone!
        this.dispose();
    }

    /**
     * This method fills out the information in the iProject into the textfields.
     */
    private void fillComponents() {
        txtTitle.setText(iProject.getTitle());
        long temp = iProject.getL_userid();
        int index = 0;
        for (int i = 0; i < iUsers.length; i++) {
            User lUser = iUsers[i];
            if (lUser.getUserid() == temp) {
                index = i;
            }
        }
        cmbUser.setSelectedIndex(index);
        temp = iProject.getL_protocolid();
        index = 0;
        for (int i = 0; i < iProtocol.length; i++) {
            Protocol lCof = iProtocol[i];
            if (lCof.getProtocolid() == temp) {
                index = i;
            }
        }
        cmbProtocol.setSelectedIndex(index);
        txtUsername.setText(iProject.getUsername());
        txtCreationDate.setText(iSDF.format(iProject.getCreationdate()));
        txtModificationDate.setText(iSDF.format(iProject.getModificationdate()));
        txtDescription.setText(iProject.getDescription());
    }

    /**
     * This method gets all data from the GUI components and does the validations. If it returns 'null', a validation
     * has failed and the operation should be aborted.
     *
     * @return HashMap with the filled-out parameters from the GUI, or 'null' if a validation failed.
     */
    private HashMap getDataFromScreen() {
        HashMap hm = null;
        // Get the data in the textfields.
        String title = txtTitle.getText().trim();
        long userid = ((User) cmbUser.getSelectedItem()).getUserid();
        long protocolid = ((Protocol) cmbProtocol.getSelectedItem()).getProtocolid();
        String description = txtDescription.getText();


        // See if the not-NULL fields are filled out.
        if (title.equals("")) {
            JOptionPane.showMessageDialog(this, "Project title must be filled out!", "Title has to be filled out!", JOptionPane.WARNING_MESSAGE);
            txtTitle.requestFocus();
        } else {
            // Creation of the param HashMap.
            hm = new HashMap(3);
            hm.put(Project.TITLE, title);
            hm.put(Project.L_USERID, new Long(userid));
            hm.put(Project.L_PROTOCOLID, new Long(protocolid));
            hm.put(Project.DESCRIPTION, description);
        }

        return hm;
    }

    /**
     * This method attempts to load the users from the DB.
     */
    private void loadUsers() {
        try {
            iUsers = User.getAllUsers(iConn);
        } catch (SQLException sqle) {
            iSS.passHotPotato(sqle, "Unable to load users from DB!");
        }
    }

    /**
     * This method attempts to load the protocol types from the DB.
     */
    private void loadProtocol() {
        try {
            iProtocol = Protocol.getAllProtocols(iConn);
        } catch (SQLException sqle) {
            iSS.passHotPotato(sqle, "Unable to load protocol types from DB!");
        }
    }

}
