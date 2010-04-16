/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 6-mrt-2005
 * Time: 9:50:49
 */
package com.compomics.mslims.gui;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Protocol;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.mslims.db.accessors.Projectanalyzertool;
import com.compomics.mslims.db.accessors.User;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.dialogs.ProjectDialog;
import com.compomics.mslims.gui.interfaces.ProjectAnalyzerTool;
import com.compomics.mslims.gui.interfaces.ProjectManager;
import com.compomics.mslims.gui.tree.ToolTreeModel;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.interfaces.Connectable;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.9 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class presents the main GUI for the projectanalyzer.
 *
 * @author Lennart Martens
 * @version $Id: ProjectAnalyzer.java,v 1.9 2009/07/28 14:48:33 lennart Exp $
 */
public class ProjectAnalyzer extends FlamableJFrame implements Connectable, ProjectManager {
    // Class specific log4j logger for ProjectAnalyzer instances.
    private static Logger logger = Logger.getLogger(ProjectAnalyzer.class);

    /**
     * Boolean that indicates whether the tool is ran in stand-alone mode ('true') or not ('false').
     */
    private static boolean iStandAlone = true;

    /**
     * The database connection to use.
     */
    private Connection iConnection = null;

    /**
     * The name of the DB we're connected to.
     */
    private String iDBName = null;

    /**
     * The projects.
     */
    private Project[] iProjects = null;

    /**
     * The HashMap with the users.
     */
    private HashMap iUsers = null;

    /**
     * The HashMap with the PROTOCOL types.
     */
    private HashMap iProtocol = null;

    /**
     * The known project analyzer tools.
     */
    private Projectanalyzertool[] iProjectAnalyzerTools = null;

    /**
     * The Map with all tool instances that are running.
     */
    private HashMap iTools = null;

    /**
     * Date-time format String.
     */
    private static final String iDateTimeFormat = "dd/MM/yyyy - HH:mm:ss";

    /**
     * The SimpleDateFormat formatter to display creationdates.
     */
    private static SimpleDateFormat iSDF = new SimpleDateFormat(iDateTimeFormat);


    private JComboBox cmbProject = null;
    private JCheckBox chkSorting = null;
    private JTextField txtID = null;
    private JTextField txtTitle = null;
    private JTextField txtResponsible = null;
    private JTextField txtProtocol = null;
    private JTextField txtUsername = null;
    private JTextField txtCreationDate = null;
    private JTextField txtModificationDate = null;
    private JTextArea txtDescription = null;

    private JComboBox cmbTool = null;
    private JTextArea txtToolDetails = null;
    private JButton btnSelectTool = null;


    private JButton btnModifyProject = null;

    private JTree treeSummary = null;

    /**
     * Private default constructor.
     */
    private ProjectAnalyzer() {
    }

    /**
     * This constructor takes a title for the frame and makes sure the DB Connection is set up, the components are
     * initialized and the GUI is layed out.
     *
     * @param aTitle String with the title for the frame. Will be affixed with the database name.
     */
    public ProjectAnalyzer(String aTitle) {
        this(aTitle, null, null);
    }

    /**
     * This constructor takes a title for the frame and a DB Connection to use (along with its name), the components are
     * initialized and the GUI is layed out.
     *
     * @param aTitle  String with the title for the frame. Will be affixed with the database name.
     * @param aConn   Connection with the database connection to use. 'null' means no connection specified so create
     *                your own (pops up ConnectionDialog).
     * @param aDBName String with the name for the database connection. Only read if aConn != null.
     */
    public ProjectAnalyzer(String aTitle, Connection aConn, String aDBName) {
        // Window closing stuff.
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        // Init the Map of tools that are active.
        iTools = new HashMap();
        // Display the connection dialog.
        if (aConn == null) {
            this.getConnection();
        } else {
            this.passConnection(aConn, aDBName);
        }
        if (iConnection == null) {
            close();
        }
        // Set the title.
        this.setTitle(aTitle + " (connected to '" + iDBName + "')");
        // Now we have a connection, gather all data next.
        this.gatherData();
        // Some components need to be initialized with the data retrieved.
        // Do this now.
        this.initializeComponents();
        // Build the GUI.
        this.constructScreen();
        // Display settings.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screen.width / 10), (screen.height / 10));
        this.pack();
    }

    /**
     * This method is meant to be called when the user has changed or added a project<br /> Changes will then be read
     * from the DB (along with all the other projects). Calling this method will result in a DB SQL statement being
     * executed.
     */
    public void projectsChanged() {
        this.findProjects();
        resortProjects(chkSorting.isSelected());
        this.fillProjectPulldown();
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
        }
        iConnection = aConn;
        iDBName = aDBName;
    }

    /**
     * This method will be called by a tool that is closing.
     *
     * @param aTool ProjectAnalyzerTool that is closing.
     */
    public void toolClosing(ProjectAnalyzerTool aTool) {
        String toolClass = aTool.getClass().getName();
        toolClass = toolClass.substring(toolClass.lastIndexOf(".") + 1);
        Vector temp = (Vector) this.iTools.get(toolClass);
        temp.remove(aTool);
        // If we just removed the last instance,
        // also delete the key.
        if (temp.size() == 0) {
            this.iTools.remove(toolClass);
        }
        updateTree();
    }

    /**
     * This method will take care of organizing and laying out the GUI of the application.
     */
    private void constructScreen() {

        // The labels for the project details fields.
        JLabel lblID = new JLabel("   Project ID: ");
        lblID.setPreferredSize(new Dimension(lblID.getPreferredSize().width, txtTitle.getPreferredSize().height));
        JLabel lblTitle = new JLabel("   Project title: ");
        lblTitle.setPreferredSize(new Dimension(lblTitle.getPreferredSize().width, txtTitle.getPreferredSize().height));
        JLabel lblResponsible = new JLabel("   Project responsible: ");
        lblResponsible.setPreferredSize(new Dimension(lblResponsible.getPreferredSize().width, txtResponsible.getPreferredSize().height));
        JLabel lblProtocol = new JLabel("   PROTOCOL type: ");
        lblProtocol.setPreferredSize(new Dimension(lblProtocol.getPreferredSize().width, txtProtocol.getPreferredSize().height));
        JLabel lblUsername = new JLabel("   Project created by: ");
        lblUsername.setPreferredSize(new Dimension(lblUsername.getPreferredSize().width, txtUsername.getPreferredSize().height));
        JLabel lblCreationdate = new JLabel("   Project creationdate: ");
        lblCreationdate.setPreferredSize(new Dimension(lblCreationdate.getPreferredSize().width, txtCreationDate.getPreferredSize().height));
        JLabel lblModificationdate = new JLabel("   Project modificationdate: ");
        lblModificationdate.setPreferredSize(new Dimension(lblModificationdate.getPreferredSize().width, txtModificationDate.getPreferredSize().height));
        JLabel lblDescription = new JLabel("   Project description: ");
        lblDescription.setPreferredSize(new Dimension(lblDescription.getPreferredSize().width, txtCreationDate.getPreferredSize().height));
        JPanel jpanLabels = new JPanel();
        jpanLabels.setLayout(new BoxLayout(jpanLabels, BoxLayout.Y_AXIS));
        jpanLabels.add(lblID);
        jpanLabels.add(Box.createVerticalStrut(5));
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
        JPanel jpanID = new JPanel();
        jpanID.setLayout(new BoxLayout(jpanID, BoxLayout.X_AXIS));
        jpanID.add(txtID);
        jpanID.add(Box.createHorizontalGlue());

        JPanel jpanTitle = new JPanel();
        jpanTitle.setLayout(new BoxLayout(jpanTitle, BoxLayout.X_AXIS));
        jpanTitle.add(txtTitle);
        jpanTitle.add(Box.createHorizontalGlue());

        JPanel jpanResponsible = new JPanel();
        jpanResponsible.setLayout(new BoxLayout(jpanResponsible, BoxLayout.X_AXIS));
        jpanResponsible.add(txtResponsible);
        jpanResponsible.add(Box.createHorizontalGlue());

        JPanel jpanProtocol = new JPanel();
        jpanProtocol.setLayout(new BoxLayout(jpanProtocol, BoxLayout.X_AXIS));
        jpanProtocol.add(txtProtocol);
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
        jpanFields.add(jpanID);
        jpanFields.add(Box.createVerticalStrut(5));
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

        btnModifyProject = new JButton("Modify project...");
        btnModifyProject.setMnemonic(KeyEvent.VK_M);
        btnModifyProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyProjectTriggered();
            }
        });

        JPanel jpanProjectButton = new JPanel();
        jpanProjectButton.setLayout(new BoxLayout(jpanProjectButton, BoxLayout.X_AXIS));
        jpanProjectButton.add(Box.createHorizontalGlue());
        jpanProjectButton.add(btnModifyProject);
        jpanProjectButton.add(Box.createHorizontalStrut(10));
        jpanProjectButton.setMaximumSize(new Dimension(jpanProjectButton.getMaximumSize().width, btnModifyProject.getPreferredSize().height));

        // This panel holds the project details labels and textfields.
        JPanel jpanProjectDetails = new JPanel();
        jpanProjectDetails.setLayout(new BoxLayout(jpanProjectDetails, BoxLayout.X_AXIS));
        jpanProjectDetails.setBorder(BorderFactory.createTitledBorder("Project details"));
        jpanProjectDetails.add(jpanLabels);
        jpanProjectDetails.add(Box.createHorizontalStrut(15));
        jpanProjectDetails.add(jpanFields);

        // A panel for the pull-down box.
        JPanel jpanProjectCombo = new JPanel();
        jpanProjectCombo.setLayout(new BoxLayout(jpanProjectCombo, BoxLayout.X_AXIS));
        jpanProjectCombo.add(cmbProject);
        jpanProjectCombo.add(Box.createHorizontalGlue());

        // A panel for the sorting checkbox.
        JPanel jpanProjectSorting = new JPanel();
        jpanProjectSorting.setLayout(new BoxLayout(jpanProjectSorting, BoxLayout.X_AXIS));
        jpanProjectSorting.add(chkSorting);
        jpanProjectSorting.add(Box.createHorizontalGlue());

        // Combine the project pulldown and the projectdetails.
        JPanel jpanProjects = new JPanel();
        jpanProjects.setLayout(new BoxLayout(jpanProjects, BoxLayout.Y_AXIS));
        jpanProjects.setBorder(BorderFactory.createTitledBorder("Project selection"));
        jpanProjects.add(jpanProjectCombo);
        jpanProjects.add(jpanProjectSorting);
        jpanProjects.add(Box.createVerticalStrut(5));
        jpanProjects.add(jpanProjectDetails);
        jpanProjects.add(Box.createVerticalStrut(5));
        jpanProjects.add(jpanProjectButton);
        jpanProjects.add(Box.createVerticalStrut(5));

        // Tools panel.
        btnSelectTool = new JButton("Engage tool...");
        btnSelectTool.setMnemonic(KeyEvent.VK_T);
        btnSelectTool.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toolEngaged();
            }
        });
        btnSelectTool.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    toolEngaged();
                }
            }
        });
        JPanel jpanToolCombo = new JPanel();
        jpanToolCombo.setLayout(new BoxLayout(jpanToolCombo, BoxLayout.X_AXIS));
        jpanToolCombo.add(cmbTool);
        jpanToolCombo.add(Box.createHorizontalStrut(15));
        jpanToolCombo.add(btnSelectTool);
        jpanToolCombo.add(Box.createHorizontalGlue());

        JLabel lblToolDetails = new JLabel("   Tool details: ");
        lblToolDetails.setPreferredSize(new Dimension(jpanLabels.getPreferredSize().width, txtTitle.getPreferredSize().height));

        JPanel jpanToolLabel = new JPanel();
        jpanToolLabel.setLayout(new BoxLayout(jpanToolLabel, BoxLayout.Y_AXIS));
        jpanToolLabel.add(lblToolDetails);
        jpanToolLabel.add(Box.createVerticalGlue());

        JPanel jpanToolDetails = new JPanel();
        jpanToolDetails.setLayout(new BoxLayout(jpanToolDetails, BoxLayout.X_AXIS));
        jpanToolDetails.setBorder(BorderFactory.createTitledBorder("Tool details"));
        jpanToolDetails.add(jpanToolLabel);
        jpanToolDetails.add(Box.createHorizontalStrut(15));
        jpanToolDetails.add(new JScrollPane(txtToolDetails));

        JPanel jpanTools = new JPanel();
        jpanTools.setLayout(new BoxLayout(jpanTools, BoxLayout.Y_AXIS));
        jpanTools.setBorder(BorderFactory.createTitledBorder("Project analysis tools"));
        jpanTools.add(jpanToolCombo);
        jpanTools.add(Box.createVerticalStrut(10));
        jpanTools.add(jpanToolDetails);

        // Top panel.
        JPanel jpanTop = new JPanel();
        jpanTop.setLayout(new BoxLayout(jpanTop, BoxLayout.Y_AXIS));
        jpanTop.add(jpanProjects);
        jpanTop.add(Box.createVerticalStrut(5));
        jpanTop.add(jpanTools);

        // Tool summary panel.
        treeSummary = new JTree(new ToolTreeModel(iTools));
        treeSummary.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath selPath = treeSummary.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        Object clicked = selPath.getLastPathComponent();
                        if (clicked instanceof ProjectAnalyzerTool) {
                            ((ProjectAnalyzerTool) clicked).setActive();
                        }
                    }
                }
            }
        });
        JPanel jpanToolSummary = new JPanel(new BorderLayout());
        jpanToolSummary.add(new JScrollPane(treeSummary), BorderLayout.CENTER);
        jpanToolSummary.setPreferredSize(new Dimension(jpanToolSummary.getPreferredSize().width, jpanTop.getPreferredSize().height / 5));

        // Splitpane between the top splitpane and the summary panel.
        JSplitPane splitPaneDown = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPaneDown.setResizeWeight(0.8);
        splitPaneDown.setTopComponent(jpanTop);
        splitPaneDown.setBottomComponent(jpanToolSummary);
        splitPaneDown.setDividerLocation(0.8);


        // The main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(splitPaneDown);
        jpanMain.add(Box.createVerticalStrut(10));
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);

        // Set maximum heigths on comboboxes.
        cmbProject.setMaximumSize(new Dimension(cmbProject.getMaximumSize().width, btnModifyProject.getPreferredSize().height));
        cmbTool.setMaximumSize(new Dimension(cmbTool.getMaximumSize().width, btnSelectTool.getPreferredSize().height));
    }

    /**
     * This method is called when the user clicks the 'modify project' button.
     */
    private void modifyProjectTriggered() {
        if (cmbProject.getSelectedItem() != null) {
            try {
                ProjectDialog pd = new ProjectDialog(this, "Modify existing project", ProjectDialog.CHANGE, (Project) cmbProject.getSelectedItem(), iConnection);
                Point p = this.getLocation();
                pd.setLocation((int) (p.getX()) + 50, (int) (p.getY()) + 50);
                pd.setVisible(true);
            } catch (Throwable t) {
                this.passHotPotato(t);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No project selected to modify!", "No project selected!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * This method is called when the user clicks the 'engage project' button.
     */
    private void toolEngaged() {
        if (cmbTool.getSelectedItem() != null) {
            Projectanalyzertool pat = (Projectanalyzertool) cmbTool.getSelectedItem();
            try {
                Object temp = Class.forName(pat.getToolclassname()).newInstance();
                if (temp instanceof ProjectAnalyzerTool) {
                    ProjectAnalyzerTool tool = (ProjectAnalyzerTool) temp;
                    String toolClass = tool.getClass().getName();
                    toolClass = toolClass.substring(toolClass.lastIndexOf(".") + 1);
                    if (iTools.containsKey(toolClass)) {
                        Vector tempVec = (Vector) iTools.get(toolClass);
                        tempVec.add(tool);
                    } else {
                        Vector tempVec = new Vector();
                        tempVec.add(tool);
                        iTools.put(toolClass, tempVec);
                    }
                    tool.engageTool(this, pat.getToolname(), pat.getToolparameters(), iConnection, iDBName, (Project) cmbProject.getSelectedItem());
                    updateTree();
                } else {
                    String lMessage = "The tool is not a correct implementation!";
                    logger.error(lMessage);
                    JOptionPane.showMessageDialog(this, lMessage, "Cannot start tool!", JOptionPane.ERROR_MESSAGE);
                }
            } catch (ClassNotFoundException cnfe) {
                logger.error(cnfe.getMessage(), cnfe);
                JOptionPane.showMessageDialog(this, "Tool '" + pat.getToolclassname() + "' could not be found in your classpath!", "Tool not found!", JOptionPane.ERROR_MESSAGE);
            } catch (Throwable t) {
                this.passHotPotato(t);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No tool selected to execute!", "No tool selected!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * The main method is the entry point for the application.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        ProjectAnalyzer pa = null;
        try {
            pa = new ProjectAnalyzer("Project analyzer application");
            pa.setVisible(true);
        } catch (Throwable t) {
            new ProjectAnalyzer().passHotPotato(t);
        }
    }

    public Point getLocationForChild() {
        int newY = this.getY() + 50;
        int newX = this.getX() + 50;
        return new Point(newX, newY);
    }

    /**
     * This method will attempt to retrieve all relevant data from the local filesystem and the DB connection.
     */
    private void gatherData() {
        this.findProjects();
        this.findUsers();
        this.findProtocol();
        this.findProjectAnalyzerTools();
    }

    /**
     * This method will use the data retrieved in 'gatherData()' to fill out a few components.
     */
    private void initializeComponents() {
        cmbProject = new JComboBox();
        cmbProject.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Project selected = (Project) e.getItem();
                    stateChangedProject(selected);
                }
            }
        });
        chkSorting = new JCheckBox("Sort projects alphabetically");
        chkSorting.setSelected(false);
        chkSorting.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean alphabetically = false;
                if (chkSorting.isSelected()) {
                    alphabetically = true;
                }
                resortProjects(alphabetically);
            }
        });
        // We also initialize the relevant txtComponents at this point.
        txtTitle = new JTextField(20);
        txtTitle.setEditable(false);
        txtTitle.setMaximumSize(txtTitle.getPreferredSize());
        txtID = new JTextField(20);
        txtID.setEditable(false);
        txtID.setMaximumSize(txtID.getPreferredSize());
        txtResponsible = new JTextField(20);
        txtResponsible.setEditable(false);
        txtResponsible.setMaximumSize(txtResponsible.getPreferredSize());
        txtProtocol = new JTextField(20);
        txtProtocol.setEditable(false);
        txtProtocol.setMaximumSize(txtProtocol.getPreferredSize());
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
        txtDescription.setMinimumSize(txtDescription.getPreferredSize());
        txtDescription.setEditable(false);

        fillProjectPulldown();

        cmbTool = new JComboBox();
        txtToolDetails = new JTextArea(8, 20);
        txtToolDetails.setMinimumSize(txtToolDetails.getPreferredSize());
        txtToolDetails.setEditable(false);

        fillToolPulldown();
    }

    /**
     * This method calls upon a GUI component to handle the connection.
     */
    private void getConnection() {
        ConnectionDialog cd = new ConnectionDialog(this, this, "Database connection for ProjectAnalyzer", "ProjectAnalyzer.properties");
        cd.setVisible(true);
    }

    /**
     * This method fills out the cmbProject with the data in the iProjects Project[].
     */
    private void fillProjectPulldown() {
        cmbProject.setModel(new DefaultComboBoxModel(iProjects));
        stateChangedProject((Project) cmbProject.getSelectedItem());
    }

    /**
     * This method is called whenever the user selects another element in the project combobox (cmbProject).
     *
     * @param aProject Project that was selected in the combobox.
     */
    private void stateChangedProject(Project aProject) {
        if (aProject != null) {
            txtID.setText(Long.toString(aProject.getProjectid()));
            txtTitle.setText(aProject.getTitle());
            Long key = new Long(aProject.getL_userid());
            User userValue = (User) iUsers.get(key);
            txtResponsible.setText(userValue.getName());
            key = new Long(aProject.getL_protocolid());
            Protocol cofValue = (Protocol) iProtocol.get(key);
            txtProtocol.setText(cofValue.getType());
            txtUsername.setText(aProject.getUsername());
            txtCreationDate.setText(iSDF.format(aProject.getCreationdate()));
            txtModificationDate.setText(iSDF.format(aProject.getModificationdate()));
            txtDescription.setText(aProject.getDescription());
            if (!txtDescription.getText().equals("")) {
                txtDescription.setCaretPosition(1);
            }
        }
    }

    /**
     * This method fills out the cmbTools with the data in the iProjects Project[].
     */
    private void fillToolPulldown() {
        cmbTool.setModel(new DefaultComboBoxModel(iProjectAnalyzerTools));
        cmbTool.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    Projectanalyzertool selected = (Projectanalyzertool) e.getItem();
                    stateChangedProjectanalyzertool(selected);
                }
            }
        });
        stateChangedProjectanalyzertool((Projectanalyzertool) cmbTool.getSelectedItem());
    }

    /**
     * This method is called whenever the user selects another element in the projectanalyzertool combobox (cmbTool).
     *
     * @param aTool Projectanalyzertool that was selected in the combobox.
     */
    private void stateChangedProjectanalyzertool(Projectanalyzertool aTool) {
        if (aTool != null) {
            txtToolDetails.setText(aTool.getDescription());
            if (!txtToolDetails.getText().equals("")) {
                txtToolDetails.setCaretPosition(1);
            }
        }
    }

    /**
     * This method will shut down this application, triggering a JVM exit with status flag set to '0'.
     */
    private void close() {
        Collection temp = this.iTools.values();
        Vector[] vectors = new Vector[temp.size()];
        temp.toArray(vectors);
        for (int i = 0; i < vectors.length; i++) {
            Vector lVector = vectors[i];
            ProjectAnalyzerTool[] tools = new ProjectAnalyzerTool[lVector.size()];
            lVector.toArray(tools);
            for (int j = 0; j < tools.length; j++) {
                ProjectAnalyzerTool tool = (ProjectAnalyzerTool) tools[j];
                tool.close();
            }
        }
        this.setVisible(false);
        this.dispose();
        if (iStandAlone) {
            if (iConnection != null) {
                try {
                    iConnection.close();
                    logger.info("DB connection closed.");
                } catch (Exception e) {
                    logger.error("\n\nUnable to close DB connection: " + e.getMessage() + "\n\n");
                }
            }
            System.exit(0);
        }
    }

    /**
     * This method finds all project entries currently stored in the DB and fills out the relevant arrays with info.
     */
    private void findProjects() {
        try {
            iProjects = Project.getAllProjects(iConnection);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve project data!");
        }
    }

    /**
     * This method collects all user information. It fills the 'iUsers' cache.
     */
    private void findUsers() {
        try {
            iUsers = User.getAllUsersAsMap(iConnection);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve user data!");
        }
    }

    /**
     * This method collects all protocol information. It fills the 'iProtocol' cache.
     */
    private void findProtocol() {
        try {
            iProtocol = Protocol.getAllProtocolsAsMap(iConnection);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve PROTOCOL data!");
        }
    }

    /**
     * This method collects all tools from the DB. It fills the 'iTools' array.
     */
    private void findProjectAnalyzerTools() {
        try {
            iProjectAnalyzerTools = Projectanalyzertool.getAllProjectanalyzertools(iConnection, true);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve project analyzer tools data!");
        }
    }

    private void updateTree() {
        treeSummary.setModel(new ToolTreeModel(iTools));
    }

    /**
     * This method re-sorts the projects in the combobox. If the boolean is 'true', sorting is alphabetically on the
     * project title, otherwise( boolean 'false') it is by project number (project id).
     *
     * @param aAlphabetically boolean to indicate whether sorting should be performed alphabetically ('true') or by
     *                        project number ('false').
     */
    private void resortProjects(boolean aAlphabetically) {
        Comparator comp = null;
        if (aAlphabetically) {
            // Alphabetic ordering of the project title.
            comp = new Comparator() {
                public int compare(Object o, Object o1) {
                    Project p1 = (Project) o;
                    Project p2 = (Project) o1;
                    return p1.getTitle().compareToIgnoreCase(p2.getTitle());
                }
            };
        } else {
            // Ordering on the projectid.
            comp = new Comparator() {
                public int compare(Object o, Object o1) {
                    Project p1 = (Project) o;
                    Project p2 = (Project) o1;
                    return (int) (p2.getProjectid() - p1.getProjectid());
                }
            };
        }
        Arrays.sort(iProjects, comp);
        this.fillProjectPulldown();
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
