/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 4-mrt-2005
 * Time: 20:27:38
 */
package com.compomics.mslims.gui;

import com.compomics.mslims.db.accessors.Binfile;
import com.compomics.mslims.db.accessors.Filedescriptor;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.dialogs.DescriptionDialog;
import com.compomics.mslims.gui.dialogs.FiledescriptorDialog;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.sun.SwingWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.9 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class presents an interface that allows the user to store a binary file or folder
 * in the database in association with a specified folder.
 *
 * @author Lennart Martens
 * @version $Id: StoreBinaryFileGUI.java,v 1.9 2009/07/28 14:48:33 lennart Exp $
 */
public class StoreBinaryFileGUI extends FlamableJFrame implements Connectable {
    /**
     * Boolean that indicates whether the tool is ran in
     * stand-alone mode ('true') or not ('false').
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
     * The Filedescriptor[] that will contain all the file descriptors currently in the DB.
     */
    private Filedescriptor[] iFileDescriptors = null;

    /**
     * The Project[] that will contain all the projects currently in the DB.
     */
    private Project[] iProjects = null;


    /**
     * This HashMap will contain all the associations between projects and files.
     */
    private HashMap iAssociations = new HashMap();

    private static final int FILE_INDEX = 0;
    private static final int FILEDESCRIPTOR_INDEX = 1;
    private static final int FILECOMMENTS_INDEX = 2;

    private JComboBox cmbProjects = null;
    private JCheckBox chkSorting = null;
    private JComboBox cmbFileDescriptors = null;
    private JTextArea txtFileDescriptorDetails = null;
    private JTextField txtFile = null;
    private JTextArea txtSummary = null;
    private JRadioButton rbtFile = null;
    private JRadioButton rbtFolder = null;

    private JButton btnAssign = null;
    private JButton btnStore = null;
    private JButton btnClear = null;
    private JButton btnExit = null;

    private JButton btnModifyFiledescriptor = null;
    private JButton btnNewFiledescriptor = null;



    /**
     * This constructor initializes the program, shows the connection dialog,
     * builds the GUI and lays out the components.
     *
     * @param aTitle    String with the title for the frame. Will be affixed with
     *                         DB connection information.
     */
    public StoreBinaryFileGUI(String aTitle) {
        this(aTitle, null, null);
    }
    /**
     * This constructor allows the choice of the display as well as the
     * specification of the connection to use (and its name).
     *
     * @param aTitle  String with the title for the JFrame.
     * @param aConn   Connection with the database connection
     *                to use. 'null' means no connection specified
     *                so create your own (pops up ConnectionDialog).
     * @param aDBName String with the name for the database connection.
     *                Only read if aConn != null.
     */

    public StoreBinaryFileGUI(String aTitle, Connection aConn, String aDBName) {
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
        // See if we actually have a connection at all.
        // Display the connection dialog.
        if (aConn == null) {
            this.getConnection();
        } else {
            this.passConnection(aConn, aDBName);
        }
        // If we still don't have a connection here, go away.
        if (iConnection == null) {
            close();
        }
        // Set the title.
        this.setTitle(aTitle + " (connected to: '" + iDBName + "')");
        // Read all the filedescriptors.
        this.getFileDescriptors();
        // Read all the projects.
        this.getProjects();
        // Construct the GUI.
        this.constructGUI();
        // Finalize creation.
        this.pack();
        this.setLocation(300, 300);
    }

    /**
     * Default empty constructor made private.
     */
    private StoreBinaryFileGUI() {}

    /**
     * This method will be called by the class actually making the connection.
     * It will pass the connection and an identifier String for that connection
     * (typically the name of the database connected to).
     *
     * @param aConn   Connection with the DB connection.
     * @param aDBName String with an identifier for the connection, typically the
     *                name of the DB connected to.
     */
    public void passConnection(Connection aConn, String aDBName) {
        if(aConn != null) {
            this.iConnection = aConn;
            this.iDBName = aDBName;
        } else {
            this.close();
        }
    }

    /**
     * This method should be called whenever an external application or frame/dialog
     * changes somehting in the filedescriptors table. Calling this method will prompt a
     * reloading of this data here.
     */
    public void fileDescriptorsChanged() {
        this.getFileDescriptors();
        cmbFileDescriptors.setModel(new DefaultComboBoxModel(iFileDescriptors));
        if(iAssociations != null && iAssociations.size() > 0) {
            clearTriggered();
        }
        stateChangedFiledescriptor();
    }

    /**
     * The main method is the entry point for the application.
     *
     * @param args  String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        StoreBinaryFileGUI sbf = null;
        try {
            sbf = new StoreBinaryFileGUI("Binary file storage application");
            sbf.setVisible(true);
        } catch(Throwable t) {
            new StoreBinaryFileGUI().passHotPotato(t);
        }
    }


    /**
     * This method creates the GUI, initializes all the components and lays them out.
     */
    private void constructGUI() {
        // Labels.
        JLabel lblProject = new JLabel("Select project : ");
        JLabel lblFDLabel = new JLabel("Select file descriptor : ");
        JLabel lblFDDetails = new JLabel("File descriptor details : ");
        lblProject.setPreferredSize(lblFDDetails.getPreferredSize());
        lblFDLabel.setPreferredSize(lblFDDetails.getPreferredSize());

        // The projects panel.
        cmbProjects = new JComboBox(iProjects);
        chkSorting = new JCheckBox("Sort projects alphabetically");
        chkSorting.setSelected(false);
        chkSorting.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean alphabetically = false;
                if(chkSorting.isSelected()) {
                    alphabetically = true;
                }
                resortProjects(alphabetically);
            }
        });

        // Panel for the project combobox.
        JPanel jpanProjectsCombo = new JPanel();
        jpanProjectsCombo.setLayout(new BoxLayout(jpanProjectsCombo, BoxLayout.X_AXIS));
        jpanProjectsCombo.add(Box.createHorizontalStrut(15));
        jpanProjectsCombo.add(lblProject);
        jpanProjectsCombo.add(Box.createHorizontalStrut(5));
        jpanProjectsCombo.add(cmbProjects);
        jpanProjectsCombo.add(Box.createHorizontalGlue());

        // A panel for the sorting checkbox.
        JPanel jpanProjectSorting = new JPanel();
        jpanProjectSorting.setLayout(new BoxLayout(jpanProjectSorting, BoxLayout.X_AXIS));
        jpanProjectSorting.add(Box.createHorizontalStrut(15));
        jpanProjectSorting.add(chkSorting);
        jpanProjectSorting.add(Box.createHorizontalGlue());

        // Panel for the whole project.
        JPanel jpanProjects = new JPanel();
        jpanProjects.setBorder(BorderFactory.createTitledBorder("Projects"));
        jpanProjects.setLayout(new BoxLayout(jpanProjects, BoxLayout.Y_AXIS));
        jpanProjects.add(jpanProjectsCombo);
        jpanProjects.add(jpanProjectSorting);

        // The file descriptors panel.
        txtFileDescriptorDetails = new JTextArea(5, 20);
        txtFileDescriptorDetails.setEditable(false);
        cmbFileDescriptors = new JComboBox(iFileDescriptors);
        cmbFileDescriptors.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    stateChangedFiledescriptor();
                }
            }
        });
        stateChangedFiledescriptor();
        btnModifyFiledescriptor = new JButton("Modify file descriptor...");
        btnModifyFiledescriptor.setMnemonic(KeyEvent.VK_M);
        btnModifyFiledescriptor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modifyFiledescriptorTriggered();
            }
        });
        // Create new file descriptor button.
        btnNewFiledescriptor = new JButton("Create new file descriptor...");
        btnNewFiledescriptor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newFiledescriptorTriggered();
            }
        });
        btnNewFiledescriptor.setMnemonic(KeyEvent.VK_N);

        JPanel jpanFileDescriptors = new JPanel();
        jpanFileDescriptors.setLayout(new BoxLayout(jpanFileDescriptors, BoxLayout.X_AXIS));
        jpanFileDescriptors.add(Box.createHorizontalStrut(15));
        jpanFileDescriptors.add(lblFDLabel);
        jpanFileDescriptors.add(Box.createHorizontalStrut(5));
        jpanFileDescriptors.add(cmbFileDescriptors);
        jpanFileDescriptors.add(Box.createHorizontalGlue());

        JPanel jpanFDLabel = new JPanel();
        jpanFDLabel.setLayout(new BoxLayout(jpanFDLabel, BoxLayout.Y_AXIS));
        jpanFDLabel.add(lblFDDetails);
        jpanFDLabel.add(Box.createVerticalGlue());

        JPanel jpanFDDetails = new JPanel();
        jpanFDDetails.setLayout(new BoxLayout(jpanFDDetails, BoxLayout.X_AXIS));
        jpanFDDetails.add(Box.createHorizontalStrut(15));
        jpanFDDetails.add(jpanFDLabel);
        jpanFDDetails.add(Box.createHorizontalStrut(5));
        jpanFDDetails.add(new JScrollPane(txtFileDescriptorDetails));
        jpanFDDetails.add(Box.createHorizontalGlue());

        JPanel jpanFDButtons = new JPanel();
        jpanFDButtons.setLayout(new BoxLayout(jpanFDButtons, BoxLayout.X_AXIS));
        jpanFDButtons.add(Box.createHorizontalGlue());
        jpanFDButtons.add(btnModifyFiledescriptor);
        jpanFDButtons.add(Box.createHorizontalStrut(5));
        jpanFDButtons.add(btnNewFiledescriptor);
        jpanFDButtons.add(Box.createHorizontalStrut(15));

        JPanel jpanFullFD = new JPanel();
        jpanFullFD.setBorder(BorderFactory.createTitledBorder("File descriptors"));
        jpanFullFD.setLayout(new BoxLayout(jpanFullFD, BoxLayout.Y_AXIS));
        jpanFullFD.add(jpanFileDescriptors);
        jpanFullFD.add(Box.createVerticalStrut(5));
        jpanFullFD.add(jpanFDDetails);
        jpanFullFD.add(Box.createVerticalStrut(5));
        jpanFullFD.add(jpanFDButtons);

        // The file panel.
        txtFile = new JTextField(50);
        txtFile.setMaximumSize(new Dimension(txtFile.getMaximumSize().width, txtFile.getPreferredSize().height));
        rbtFile = new JRadioButton("Select a file");
        rbtFolder = new JRadioButton("Select a folder");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbtFile);
        bg.add(rbtFolder);
        rbtFile.setSelected(true);
        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.setMnemonic(KeyEvent.VK_B);
        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectionName = "file";
                boolean file = true;
                if(rbtFolder.isSelected()) {
                    selectionName = "folder";
                    file = false;
                }

                String currentDir = txtFile.getText().trim();
                if(currentDir == null || currentDir.equals("")) {
                    currentDir = "/";
                }
                JFileChooser jfc = new JFileChooser(currentDir);
                jfc.setDialogType(JFileChooser.CUSTOM_DIALOG);
                jfc.setApproveButtonText("Select " + selectionName);
                jfc.setApproveButtonMnemonic(KeyEvent.VK_S);
                jfc.setApproveButtonToolTipText("Select the " + selectionName + " to upload.");
                jfc.setDialogTitle("Select source " + selectionName);
                if(file) {
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                } else {
                    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                }
                int value = jfc.showDialog(StoreBinaryFileGUI.this, "Select " + selectionName);
                if(value == JFileChooser.APPROVE_OPTION) {
                    try {
                        txtFile.setText(jfc.getSelectedFile().getCanonicalPath());
                    } catch(IOException ioe) {
                        JOptionPane.showMessageDialog(StoreBinaryFileGUI.this, new String[]{"Unable to find the " + selectionName + " you've selected!", "\n", ioe.getMessage(), "\n"}, selectionName + " was not found!", JOptionPane.ERROR_MESSAGE);
                        txtFile.setText("");
                    }
                }
            }
        });
        JPanel jpanFile = new JPanel();
        jpanFile.setLayout(new BoxLayout(jpanFile, BoxLayout.X_AXIS));
        jpanFile.add(Box.createHorizontalStrut(10));
        jpanFile.add(txtFile);
        jpanFile.add(Box.createHorizontalStrut(5));
        jpanFile.add(btnBrowse);
        jpanFile.add(Box.createHorizontalStrut(10));
        JPanel jpanRadios = new JPanel();
        jpanRadios.setLayout(new BoxLayout(jpanRadios, BoxLayout.X_AXIS));
        jpanRadios.add(Box.createHorizontalStrut(10));
        jpanRadios.add(rbtFile);
        jpanRadios.add(Box.createHorizontalStrut(5));
        jpanRadios.add(rbtFolder);
        jpanRadios.add(Box.createHorizontalStrut(10));

        JPanel jpanFileAndRadios = new JPanel();
        jpanFileAndRadios.setBorder(BorderFactory.createTitledBorder("Select upload"));
        jpanFileAndRadios.setLayout(new BoxLayout(jpanFileAndRadios, BoxLayout.Y_AXIS));
        jpanFileAndRadios.add(jpanFile);
        jpanFileAndRadios.add(jpanRadios);

        // The summary panel.
        txtSummary = new JTextArea(8, 45);
        txtSummary.setEditable(false);
        txtSummary.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JPanel jpanSummary = new JPanel(new BorderLayout());
        jpanSummary.setBorder(BorderFactory.createTitledBorder("Summary"));
        jpanSummary.add(new JScrollPane(txtSummary), BorderLayout.CENTER);

        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanProjects);
        jpanMain.add(jpanFullFD);
        jpanMain.add(jpanFileAndRadios);
        jpanMain.add(jpanSummary);
        jpanMain.add(this.createButtonPanel());
        // Finishing touch.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method creates and returns a JPanel with the buttons.
     *
     * @return  JPanel  with the buttons.
     */
    private JPanel createButtonPanel() {
        // Assign button.
        btnAssign = new JButton("Assign file/folder to project");
        btnAssign.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignTriggered();
            }
        });
        btnAssign.setMnemonic(KeyEvent.VK_A);
        btnAssign.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    assignTriggered();
                }
            }
        });

        // Store button.
        btnStore = new JButton("Store");
        btnStore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                storeTriggered();
            }
        });
        btnStore.setMnemonic(KeyEvent.VK_S);
        btnStore.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    storeTriggered();
                }
            }
        });

        // Clear button.
        btnClear = new JButton("Clear");
        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearTriggered();
            }
        });
        btnClear.setMnemonic(KeyEvent.VK_C);
        btnClear.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    clearTriggered();
                }
            }
        });

        // Exit button.
        btnExit = new JButton("Exit");
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        btnExit.setMnemonic(KeyEvent.VK_X);
        btnExit.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    close();
                }
            }
        });

        // Button panel itself.
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

        // Restricting the height of the buttonpanel to the preferred height of the buttons.
        jpanButtons.setMaximumSize(new Dimension(jpanButtons.getMaximumSize().width, btnAssign.getPreferredSize().height));

        return jpanButtons;
    }

    /**
     * This method should be called whenever the selection on the 'cmbFileDescriptors'
     * changes. This method will update the detailed description field (txtFileDescriptorDetails)
     * accordingly.
     */
    private void stateChangedFiledescriptor() {
        Object temp = cmbFileDescriptors.getSelectedItem();
        if(temp != null) {
            txtFileDescriptorDetails.setText(((Filedescriptor)temp).getDescription());
        }
    }

    /**
     * This method is called when the 'create new filedescriptor' button is pressed.
     */
    private void newFiledescriptorTriggered() {
        if(iAssociations != null && iAssociations.size() > 0) {
            int result = JOptionPane.showConfirmDialog(this, new String[] {"This operation will clear all associations!", "Do you wish to continue?"}, "Associations will be cleared!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(result == JOptionPane.NO_OPTION) {
                return;
            }
        }
        try {
            FiledescriptorDialog fdd = new FiledescriptorDialog(this, "Create a new file descriptor", FiledescriptorDialog.NEW, null, iConnection);
            Point p = this.getLocation();
            fdd.setLocation((int)(p.getX()) + 50, (int)(p.getY()) + 50);
            fdd.setVisible(true);
        } catch(Throwable t) {
            this.passHotPotato(t);
        }

    }

    /**
     * This method is called when the user clicks the modify project button.
     */
    private void modifyFiledescriptorTriggered() {
        if(iAssociations != null && iAssociations.size() > 0) {
            int result = JOptionPane.showConfirmDialog(this, new String[] {"This operation will clear all associations!", "Do you wish to continue?"}, "Associations will be cleared!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if(result == JOptionPane.NO_OPTION) {
                return;
            }
        }
        if(cmbFileDescriptors.getSelectedItem() != null) {
            try {
                FiledescriptorDialog fdd = new FiledescriptorDialog(this, "Modify existing file descriptor", FiledescriptorDialog.CHANGE, (Filedescriptor)cmbFileDescriptors.getSelectedItem(), iConnection);
                Point p = this.getLocation();
                fdd.setLocation((int)(p.getX()) + 50, (int)(p.getY()) + 50);
                fdd.setVisible(true);
            } catch(Throwable t) {
                this.passHotPotato(t);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No file descriptor selected to modify!", "No file descriptor selected!", JOptionPane.WARNING_MESSAGE);
        }
    }


    /**
     * This method is called when the 'assign' button is pressed.
     */
    private void assignTriggered() {
        // Check whether there is a file and that it exists.
        String filepath = txtFile.getText();
        if(filepath == null || filepath.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "You did not enter a file or folder to assign!", "Nothing to assign!", JOptionPane.WARNING_MESSAGE);
            txtFile.requestFocus();
            return;
        }
        File file = new File(filepath);
        if(!file.exists()) {
            JOptionPane.showMessageDialog(this, "The path you specified ('" + filepath + "') could not be found!!", "Path not found!", JOptionPane.WARNING_MESSAGE);
            txtFile.requestFocus();
            return;
        }
        // See if any comments need to be recorded.
        String comments = DescriptionDialog.getDescriptionDialog(this, "Comments for '" + filepath + "'", null, this.getX()+(this.getWidth()/3), this.getY()+(this.getHeight()/3));
        // OK, we seem to have something decent to assign.
        // See if there is anything associated to the project already,
        // if not, add a new element in the association HashMap.
        Object project = cmbProjects.getSelectedItem();
        Vector tempVec = null;
        if(iAssociations.containsKey(project)) {
            tempVec = (Vector)iAssociations.get(project);
        } else {
            tempVec = new Vector();
        }
        tempVec.add(new Object[]{file, cmbFileDescriptors.getSelectedItem(), comments});
        iAssociations.put(project, tempVec);
        this.txtFile.setText("");
        this.updateSummary();
    }

    /**
     * This method is called when the 'store' button is pressed.
     */
    private void storeTriggered() {
        // First determine the number of files/folders to store.
        int total = 0;
        Iterator itCount = iAssociations.keySet().iterator();
        while(itCount.hasNext()) {
            Project lProject = (Project)itCount.next();
            Vector temp = (Vector)iAssociations.get(lProject);
            total += temp.size();
        }
        final DefaultProgressBar progress = new DefaultProgressBar(this, "Storing zipped binary files in the database", 0, total);
        progress.setSize(this.getWidth()/2, progress.getPreferredSize().height);
        progress.setLocation(this.getLocation().x+((this.getWidth()-progress.getWidth())/2), this.getLocation().y+((this.getHeight()-progress.getHeight())/2));

        SwingWorker sw = new SwingWorker() {
            /**
             * Compute the value to be returned by the <code>get</code> method.
             */
            public Object construct() {
                if(iAssociations.size() == 0) {
                    JOptionPane.showMessageDialog(StoreBinaryFileGUI.this, "First assign some file(s) to (a) project(s)!", "No assignments made!", JOptionPane.WARNING_MESSAGE);
                    return "";
                }
                try {
                    // First, cycle the projects (keys in the HashMap).
                    Iterator it = iAssociations.keySet().iterator();
                    // Counters.
                    int fileCounter = 0;
                    int folderCounter = 0;
                    while(it.hasNext()) {
                        Project lProject = (Project)it.next();
                        // Get the project ID.
                        long projectid = lProject.getProjectid();
                        // Now cycle all associated LC runs.
                        Vector fileVec = (Vector)iAssociations.get(lProject);
                        int liSize = fileVec.size();
                        for(int i = 0; i < liSize; i++) {
                            Object[] objects = (Object[])fileVec.elementAt(i);
                            File file = (File)objects[FILE_INDEX];
                            Filedescriptor fd = (Filedescriptor)objects[FILEDESCRIPTOR_INDEX];
                            String comments = (String)objects[FILECOMMENTS_INDEX];
                            String designation = null;
                            if(file.isDirectory()) {
                                designation = "folder";
                                folderCounter++;
                            } else {
                                designation = "file";
                                fileCounter++;
                            }
                            progress.setMessage("Storing " + designation + " '" + file.getAbsolutePath() + "' in project '" + lProject.getTitle() + "'...");
                            // Create a Binfile.
                            HashMap params = new HashMap(7);
                            params.put(Binfile.FROMFILE, file.getAbsolutePath());
                            params.put(Binfile.L_PROJECTID, new Long(projectid));
                            params.put(Binfile.L_FILEDESCRIPTIONID, new Long(fd.getFiledescriptorid()));
                            params.put(Binfile.FILENAME, file.getName());
                            params.put(Binfile.COMMENTS, comments);
                            String parent = file.getParent();
                            String separator = null;
                            if(parent.indexOf("\\") >= 0) {
                                separator = "\\";
                            } else {
                                separator = "/";
                            }
                            if(!parent.endsWith(separator)) {
                                parent += separator;
                            }
                            params.put(Binfile.ORIGINALPATH,  parent);
                            params.put(Binfile.ORIGINALHOST, InetAddress.getLocalHost().getHostName());
                            params.put(Binfile.ORIGINALUSER, System.getProperty("user.name"));
                            Binfile bf = new Binfile(params);
                            bf.persist(iConnection);
                            // Finding the auto-generated ID for the LCRun.
                            Long l = (Long)bf.getGeneratedKeys()[0];
                            bf.setBinfileid(l.longValue());
                            progress.setValue(progress.getValue()+1);
                        }
                    }
                    StoreBinaryFileGUI.this.iAssociations = new HashMap();
                    txtSummary.setText("");
                    JOptionPane.showMessageDialog(StoreBinaryFileGUI.this, "All files (" + fileCounter + ") and folders (" + folderCounter + ") have been stored!", "Store complete!", JOptionPane.INFORMATION_MESSAGE);
                } catch(Throwable t) {
                    StoreBinaryFileGUI.this.passHotPotato(t, "Unable to store assignments!");
                }
                return "";
            }
        };
        sw.start();
        progress.setVisible(true);
    }

    /**
     * This method is called when the 'clear' button is pressed.
     */
    private void clearTriggered() {
        iAssociations = new HashMap();
        txtSummary.setText("");
    }

    /**
     * This method reads the iAssociations HashMap and updates the
     * txtSummary field according to the data in that HashMap.
     */
    private void updateSummary() {
        StringBuffer sb = new StringBuffer();
        Iterator it = iAssociations.keySet().iterator();
        int count = 0;
        // Cycle each project.
        while(it.hasNext()) {
            Project lProject = (Project)it.next();
            // Endline for all but first element.
            if(count != 0) {
                sb.append("\n");
            }
            sb.append(" " + lProject.toString() + "\n ");
            for(int i=0;i<lProject.toString().length();i++) {
                sb.append("-");
            }
            sb.append("\n");
            // Cycle each LCRun for this project.
            Vector tempVec = (Vector)iAssociations.get(lProject);
            int liSize = tempVec.size();
            for(int i = 0; i < liSize; i++) {
                Object[] objects = (Object[])tempVec.elementAt(i);
                File file = (File)objects[FILE_INDEX];
                Filedescriptor fd = (Filedescriptor)objects[FILEDESCRIPTOR_INDEX];
                sb.append("   + " + file.getAbsolutePath() + " " + (file.isDirectory()?"<dir>":"<file>") +  ": " + fd.getShort_label());
                String comments = (String)objects[FILECOMMENTS_INDEX];
                sb.append((comments==null?"":" @") + "\n");
            }
            count++;
        }
        txtSummary.setText(sb.toString());
    }

    /**
     * This method calls upon a GUI component to handle the connection.
     */
    private void getConnection() {
        ConnectionDialog cd = new ConnectionDialog(this, this, "Database connection for StoreBinaryFile", "StoreBinaryFileGUI.properties");
        cd.setVisible(true);
    }

    /**
     * This method reads all known file descriptors from the DB and stores them in the
     * 'iFileDescriptors' HashMap.
     */
    private void getFileDescriptors() {
        try {
            iFileDescriptors = Filedescriptor.getAllFiledescriptors(iConnection, true);
            if(iFileDescriptors == null) {
                iFileDescriptors = new Filedescriptor[0];
            }
        } catch(SQLException sqle) {
            passHotPotato(sqle, "Unable to read file descriptors from the database: " + sqle.getMessage());
        }
    }

    /**
     * This method reads all known projects from the DB and stores them in the
     * 'iProjects' HashMap.
     */
    private void getProjects() {
        try {
            iProjects = Project.getAllProjects(iConnection);
        } catch(SQLException sqle) {
            passHotPotato(sqle, "Unable to read file descriptors from the database: " + sqle.getMessage());
        }
    }

    /**
     * This method re-sorts the projects in the combobox.
     * If the boolean is 'true', sorting is alphabetically on the project title,
     * otherwise( boolean 'false') it is by project number (project id).
     *
     * @param aAlphabetically boolean to indicate whether sorting should be performed
     *                        alphabetically ('true') or by project number ('false').
     */
    private void resortProjects(boolean aAlphabetically) {
        Comparator comp = null;
        if(aAlphabetically) {
            // Alphabetic ordering of the project title.
            comp = new Comparator() {
                public int compare(Object o, Object o1) {
                    Project p1 = (Project)o;
                    Project p2 = (Project)o1;
                    return p1.getTitle().compareToIgnoreCase(p2.getTitle());
                }
            };
        } else {
            // Ordering on the projectid.
            comp = new Comparator() {
                public int compare(Object o, Object o1) {
                    Project p1 = (Project)o;
                    Project p2 = (Project)o1;
                    return (int)(p2.getProjectid()-p1.getProjectid());
                }
            };
        }
        Arrays.sort(iProjects, comp);
        cmbProjects.setModel(new DefaultComboBoxModel(iProjects));
    }


    /**
     * This method should be called when the application is
     * not launched in stand-alone mode.
     */
    public static void setNotStandAlone() {
        iStandAlone = false;
    }

    public boolean isStandAlone() {
        return iStandAlone;
    }

    /**
     * This method is called when the application is closed.
     */
    private void close() {
        //closeConnection();
        this.setVisible(false);
        this.dispose();
        if (iStandAlone) {
            if (iConnection != null) {
                try {
                    iConnection.close();
                    System.out.println("\nDB connection closed.\n");
                } catch (SQLException sqle) {
                    System.err.println("\nUnable to close DB connection!\n");
                }
            }
            System.exit(0);
        }
    }
}
