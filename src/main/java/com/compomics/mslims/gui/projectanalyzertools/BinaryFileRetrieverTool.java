/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 21-mrt-2005
 * Time: 10:23:56
 */
package com.compomics.mslims.gui.projectanalyzertools;

import com.compomics.mslims.db.accessors.Binfile;
import com.compomics.mslims.db.accessors.Filedescriptor;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.mslims.gui.ProjectAnalyzer;
import com.compomics.mslims.gui.interfaces.ProjectAnalyzerTool;
import com.compomics.util.gui.FlamableJFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2007/04/03 11:32:43 $
 */

/**
 * This class implements a ProjectAnalyzerTool that allows retrieval of the stored binary files associated with
 * a specified project.
 *
 * @author Lennart Martens
 * @version $Id: BinaryFileRetrieverTool.java,v 1.3 2007/04/03 11:32:43 lennart Exp $
 */
public class BinaryFileRetrieverTool extends FlamableJFrame implements ProjectAnalyzerTool {

    /**
     * The parent that started this application.
     */
    private ProjectAnalyzer iParent = null;

    /**
     * The parameters that were passed to this application.
     */
    private String iParameters = null;

    /**
     * The database connection that was passed to this application.
     */
    private Connection iConnection = null;

    /**
     * The name for the DB connection.
     */
    private String iDBName = null;

    /**
     * The project we should be analysing.
     */
    private Project iProject = null;

    /**
     * The file descriptors we retrieved from the DB, keyed by their ID.
     */
    private HashMap iFiledescriptors = null;

    /**
     * The binary files we retrieved from the project under analysis.
     */
    private Binfile[] iBinfiles = null;

    /**
     * This String holds the name for the tool.
     */
    private String iToolName = null;

    /**
     * Simple date format for this class.
     */
    private static final SimpleDateFormat iSDF = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");

    private JComboBox cmbBinfile = null;

    private JTextField txtFDLabel = null;
    private JTextArea txtFDDescription = null;

    private JTextField txtFilename = null;
    private JTextField txtPath = null;
    private JTextField txtHost = null;
    private JTextField txtUser = null;
    private JTextField txtUsername = null;
    private JTextField txtCreationdate = null;
    private JTextField txtModificationdate = null;
    private JTextArea txtComments = null;

    /**
     * Explicit default constructor.
     */
    public BinaryFileRetrieverTool() {
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
     * This method represents the 'command-pattern' design of the ProjectAnalyzerTool.
     * It will actually allow the tool to run.
     *
     * @param aParent     ProjectAnalyzer with the parent that launched this tool.
     * @param aToolName   String with the name for the tool.
     * @param aParameters String with the parameters as stored in the database for this tool.
     * @param aConn       Connection with the DB connection to use.
     * @param aDBName     String with the name of the database we're connected to via 'aConn'.
     * @param aProject    Project with the project we should be analyzing.
     */
    public void engageTool(ProjectAnalyzer aParent, String aToolName, String aParameters, Connection aConn, String aDBName, Project aProject) {
        this.iParent = aParent;
        this.iToolName = aToolName + " (" + aProject.getProjectid() + ". " + aProject.getTitle() + ")";
        this.iParameters = aParameters;
        this.iConnection = aConn;
        this.iDBName = aDBName;
        this.iProject = aProject;
        // Get the data.
        this.getFileDescriptors();
        this.getBinfiles();
        // See if we have anything to display at all.
        if(iBinfiles == null || iBinfiles.length == 0) {
            JOptionPane.showMessageDialog(this, new String[] {"No binary files in the database for this project.", "Exiting Binary file retrieval Tool."}, "No binary files found.", JOptionPane.WARNING_MESSAGE);
            this.close();
            return;
        }
        // Construct the GUI.
        this.constructScreen();
        // Init the details fields.
        updateDetails();
        this.pack();
        this.setTitle("Binary file retrieval tool for project " + aProject.getProjectid() + " (connected to '" + iDBName + "')");
        // Set the screen location and make it visible.
        this.setLocation(aParent.getLocationForChild());
        this.setVisible(true);
    }

    /**
     * This method should return a meaningful name for the tool.
     *
     * @return String with a meaningful name for the tool.
     */
    public String getToolName() {
        return this.iToolName;
    }

    public String toString() {
        return this.getToolName();
    }

    /**
     * This method will be called when the tool should show itself on the foreground and request the focus.
     */
    public void setActive() {
        if(this.getState() == java.awt.Frame.ICONIFIED){
            this.setState(java.awt.Frame.NORMAL);
        }

        this.requestFocus();
    }

    /**
     * This method should be called whenever this tool closes down.
     */
    public void close() {
        // Notify the parent.
        iParent.toolClosing(this);
        this.setVisible(false);
        this.dispose();
    }

    /**
     * This method initializes and lays out the GUI components.
     */
    private void constructScreen() {

        JLabel lblCreationdate = new JLabel("Original creation date : ");
        JLabel lblFile = new JLabel("Select file : ");
        lblFile.setPreferredSize(lblCreationdate.getPreferredSize());
        JLabel lblFDLabel = new JLabel("File descriptor : ");
        lblFDLabel.setPreferredSize(lblCreationdate.getPreferredSize());
        JLabel lblFDDescription = new JLabel("Details : ");
        lblFDDescription.setPreferredSize(lblCreationdate.getPreferredSize());
        JLabel lblFilename = new JLabel("Filename : ");
        lblFilename.setPreferredSize(lblCreationdate.getPreferredSize());
        JLabel lblPath = new JLabel("Original path : ");
        lblPath.setPreferredSize(lblCreationdate.getPreferredSize());
        JLabel lblHost = new JLabel("Original host : ");
        lblHost.setPreferredSize(lblCreationdate.getPreferredSize());
        JLabel lblUser = new JLabel("Original user : ");
        lblUser.setPreferredSize(lblCreationdate.getPreferredSize());
        JLabel lblUsername = new JLabel("Created by : ");
        lblUsername.setPreferredSize(lblCreationdate.getPreferredSize());
        JLabel lblModificationdate = new JLabel("Modification date : ");
        lblModificationdate.setPreferredSize(lblCreationdate.getPreferredSize());

        JLabel lblComments = new JLabel("Comments : ");
        lblComments.setPreferredSize(lblCreationdate.getPreferredSize());

        cmbBinfile = new JComboBox(iBinfiles);
        cmbBinfile.setMaximumSize(new Dimension(cmbBinfile.getPreferredSize().width, cmbBinfile.getPreferredSize().height));
        cmbBinfile.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateDetails();
            }
        });

        JPanel jpanBinfile = new JPanel();
        jpanBinfile.setLayout(new BoxLayout(jpanBinfile, BoxLayout.X_AXIS));
        jpanBinfile.add(Box.createHorizontalStrut(15));
        jpanBinfile.add(lblFile);
        jpanBinfile.add(Box.createHorizontalStrut(5));
        jpanBinfile.add(cmbBinfile);
        jpanBinfile.add(Box.createHorizontalGlue());

        txtFDLabel = new JTextField(50);
        txtFDLabel.setEditable(false);
        txtFDLabel.setMaximumSize(new Dimension(txtFDLabel.getPreferredSize().width, txtFDLabel.getPreferredSize().height));

        JPanel jpanLabel = new JPanel();
        jpanLabel.setLayout(new BoxLayout(jpanLabel, BoxLayout.X_AXIS));
        jpanLabel.add(Box.createHorizontalStrut(15));
        jpanLabel.add(lblFDLabel);
        jpanLabel.add(Box.createHorizontalStrut(5));
        jpanLabel.add(txtFDLabel);
        jpanLabel.add(Box.createHorizontalGlue());

        txtFDDescription = new JTextArea(8, 20);
        txtFDDescription.setMinimumSize(txtFDDescription.getPreferredSize());
        txtFDDescription.setEditable(false);

        JPanel jpanDetailsLabel = new JPanel();
        jpanDetailsLabel.setLayout(new BoxLayout(jpanDetailsLabel, BoxLayout.Y_AXIS));
        jpanDetailsLabel.add(lblFDDescription);
        jpanDetailsLabel.add(Box.createVerticalGlue());

        JPanel jpanDetails = new JPanel();
        jpanDetails.setLayout(new BoxLayout(jpanDetails, BoxLayout.X_AXIS));
        jpanDetails.add(Box.createHorizontalStrut(15));
        jpanDetails.add(jpanDetailsLabel);
        jpanDetails.add(Box.createHorizontalStrut(5));
        jpanDetails.add(new JScrollPane(txtFDDescription));
        jpanDetails.add(Box.createHorizontalGlue());

        JPanel jpanFD = new JPanel();
        jpanFD.setLayout(new BoxLayout(jpanFD, BoxLayout.Y_AXIS));
        jpanFD.setBorder(BorderFactory.createTitledBorder("File descriptor"));
        jpanFD.add(jpanLabel);
        jpanFD.add(Box.createVerticalStrut(5));
        jpanFD.add(jpanDetails);

        txtFilename = new JTextField(50);
        txtFilename.setEditable(false);
        txtFilename.setMaximumSize(new Dimension(txtFilename.getPreferredSize().width, txtFilename.getPreferredSize().height));

        JPanel jpanFilename = new JPanel();
        jpanFilename.setLayout(new BoxLayout(jpanFilename, BoxLayout.X_AXIS));
        jpanFilename.add(Box.createHorizontalStrut(15));
        jpanFilename.add(lblFilename);
        jpanFilename.add(Box.createHorizontalStrut(5));
        jpanFilename.add(txtFilename);
        jpanFilename.add(Box.createHorizontalGlue());

        txtPath = new JTextField(50);
        txtPath.setEditable(false);
        txtPath.setMaximumSize(new Dimension(txtPath.getPreferredSize().width, txtPath.getPreferredSize().height));

        JPanel jpanPath = new JPanel();
        jpanPath.setLayout(new BoxLayout(jpanPath, BoxLayout.X_AXIS));
        jpanPath.add(Box.createHorizontalStrut(15));
        jpanPath.add(lblPath);
        jpanPath.add(Box.createHorizontalStrut(5));
        jpanPath.add(txtPath);
        jpanPath.add(Box.createHorizontalGlue());

        txtHost = new JTextField(50);
        txtHost.setEditable(false);
        txtHost.setMaximumSize(new Dimension(txtHost.getPreferredSize().width, txtHost.getPreferredSize().height));

        JPanel jpanHost = new JPanel();
        jpanHost.setLayout(new BoxLayout(jpanHost, BoxLayout.X_AXIS));
        jpanHost.add(Box.createHorizontalStrut(15));
        jpanHost.add(lblHost);
        jpanHost.add(Box.createHorizontalStrut(5));
        jpanHost.add(txtHost);
        jpanHost.add(Box.createHorizontalGlue());

        txtUser = new JTextField(50);
        txtUser.setEditable(false);
        txtUser.setMaximumSize(new Dimension(txtUser.getPreferredSize().width, txtUser.getPreferredSize().height));

        JPanel jpanUser = new JPanel();
        jpanUser.setLayout(new BoxLayout(jpanUser, BoxLayout.X_AXIS));
        jpanUser.add(Box.createHorizontalStrut(15));
        jpanUser.add(lblUser);
        jpanUser.add(Box.createHorizontalStrut(5));
        jpanUser.add(txtUser);
        jpanUser.add(Box.createHorizontalGlue());

        txtUsername = new JTextField(50);
        txtUsername.setEditable(false);
        txtUsername.setMaximumSize(new Dimension(txtUsername.getPreferredSize().width, txtUsername.getPreferredSize().height));

        JPanel jpanUsername = new JPanel();
        jpanUsername.setLayout(new BoxLayout(jpanUsername, BoxLayout.X_AXIS));
        jpanUsername.add(Box.createHorizontalStrut(15));
        jpanUsername.add(lblUsername);
        jpanUsername.add(Box.createHorizontalStrut(5));
        jpanUsername.add(txtUsername);
        jpanUsername.add(Box.createHorizontalGlue());

        txtCreationdate = new JTextField(50);
        txtCreationdate.setEditable(false);
        txtCreationdate.setMaximumSize(new Dimension(txtCreationdate.getPreferredSize().width, txtCreationdate.getPreferredSize().height));

        JPanel jpanCreationDate = new JPanel();
        jpanCreationDate.setLayout(new BoxLayout(jpanCreationDate, BoxLayout.X_AXIS));
        jpanCreationDate.add(Box.createHorizontalStrut(15));
        jpanCreationDate.add(lblCreationdate);
        jpanCreationDate.add(Box.createHorizontalStrut(5));
        jpanCreationDate.add(txtCreationdate);
        jpanCreationDate.add(Box.createHorizontalGlue());

        txtModificationdate = new JTextField(50);
        txtModificationdate.setEditable(false);
        txtModificationdate.setMaximumSize(new Dimension(txtModificationdate.getPreferredSize().width, txtModificationdate.getPreferredSize().height));

        JPanel jpanModificationDate = new JPanel();
        jpanModificationDate.setLayout(new BoxLayout(jpanModificationDate, BoxLayout.X_AXIS));
        jpanModificationDate.add(Box.createHorizontalStrut(15));
        jpanModificationDate.add(lblModificationdate);
        jpanModificationDate.add(Box.createHorizontalStrut(5));
        jpanModificationDate.add(txtModificationdate);
        jpanModificationDate.add(Box.createHorizontalGlue());

        txtComments = new JTextArea(8, 20);
        txtComments.setMinimumSize(txtComments.getPreferredSize());
        txtComments.setEditable(false);

        JPanel jpanCommentsLabel = new JPanel();
        jpanCommentsLabel.setLayout(new BoxLayout(jpanCommentsLabel, BoxLayout.Y_AXIS));
        jpanCommentsLabel.add(lblComments);
        jpanCommentsLabel.add(Box.createVerticalGlue());

        JPanel jpanComments = new JPanel();
        jpanComments.setLayout(new BoxLayout(jpanComments, BoxLayout.X_AXIS));
        jpanComments.add(Box.createHorizontalStrut(15));
        jpanComments.add(jpanCommentsLabel);
        jpanComments.add(Box.createHorizontalStrut(5));
        jpanComments.add(new JScrollPane(txtComments));
        jpanComments.add(Box.createHorizontalGlue());

        JPanel jpanFileDetails = new JPanel();
        jpanFileDetails.setLayout(new BoxLayout(jpanFileDetails, BoxLayout.Y_AXIS));
        jpanFileDetails.setBorder(BorderFactory.createTitledBorder("File details"));
        jpanFileDetails.add(jpanFilename);
        jpanFileDetails.add(Box.createVerticalStrut(5));
        jpanFileDetails.add(jpanPath);
        jpanFileDetails.add(Box.createVerticalStrut(5));
        jpanFileDetails.add(jpanHost);
        jpanFileDetails.add(Box.createVerticalStrut(5));
        jpanFileDetails.add(jpanUser);
        jpanFileDetails.add(Box.createVerticalStrut(5));
        jpanFileDetails.add(jpanUsername);
        jpanFileDetails.add(Box.createVerticalStrut(5));
        jpanFileDetails.add(jpanCreationDate);
        jpanFileDetails.add(Box.createVerticalStrut(5));
        jpanFileDetails.add(jpanModificationDate);
        jpanFileDetails.add(Box.createVerticalStrut(5));
        jpanFileDetails.add(jpanComments);

        JPanel jpanTop = new JPanel();
        jpanTop.setLayout(new BoxLayout(jpanTop, BoxLayout.Y_AXIS));
        jpanTop.setBorder(BorderFactory.createTitledBorder("File selection menu"));
        jpanTop.add(jpanBinfile);
        jpanTop.add(Box.createVerticalStrut(5));
        jpanTop.add(jpanFD);
        jpanTop.add(Box.createVerticalStrut(5));
        jpanTop.add(jpanFileDetails);
        jpanTop.add(Box.createVerticalGlue());


        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanTop);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(this.getButtonPanel());

        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method retrieves all binfile entries for the current project,
     * albeit lazily cached (actual bytes are not yet retrieved).
     */
    private void getBinfiles() {
        try {
            iBinfiles = Binfile.getAllBinfilesLazy(iConnection, iProject.getProjectid());
        } catch(SQLException sqle) {
            JOptionPane.showMessageDialog(this, new String[] {"Unable to load binary files from the database: " + sqle.getMessage(), "Exiting Binary file retrieval Tool."}, "Unable to load binary files.", JOptionPane.ERROR_MESSAGE);
            this.close();
        }
    }

    /**
     * This method reads all known file descriptors from the DB and stores them in the
     * 'iFileDescriptors' HashMap.
     */
    private void getFileDescriptors() {
        try {
            Filedescriptor[] temp = Filedescriptor.getAllFiledescriptors(iConnection, true);
            iFiledescriptors = new HashMap(temp.length);
            for (int i = 0; i < temp.length; i++) {
                Filedescriptor lFiledescriptor = temp[i];
                iFiledescriptors.put(new Long(lFiledescriptor.getFiledescriptorid()), lFiledescriptor);
            }
        } catch(SQLException sqle) {
            passHotPotato(sqle, "Unable to read file descriptors from the database: " + sqle.getMessage());
        }
    }

    /**
     * This method provides the button panel.
     *
     * @return JPanel with the buttons.
     */
    private JPanel getButtonPanel() {
        JButton btnSave = new JButton("Save file...");
        btnSave.setMnemonic(KeyEvent.VK_S);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTriggered();
            }
        });
        btnSave.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    saveTriggered();
                }
            }
        });

        JButton btnExit = new JButton("Exit");
        btnExit.setMnemonic(KeyEvent.VK_X);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        btnExit.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    close();
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnSave);
        jpanButtons.add(Box.createHorizontalStrut(10));
        jpanButtons.add(btnExit);
        jpanButtons.add(Box.createHorizontalStrut(15));

        return jpanButtons;
    }

    /**
     * This method will be called when the user clicks 'save file'.
     */
    private void saveTriggered() {
        // Firstly, load the actual binary file from the DB (remember that we
        //  were displaying a lazily cached version).
        Binfile bf = (Binfile)cmbBinfile.getSelectedItem();
        JFrame tempFrame = new JFrame("Loading file...");
        JLabel label = new JLabel("Loading the file from the database...", JLabel.CENTER);
        JPanel jpanLabel = new JPanel(new BorderLayout());
        jpanLabel.add(label, BorderLayout.CENTER);
        tempFrame.getContentPane().add(jpanLabel, BorderLayout.CENTER);
        tempFrame.setSize((int)(label.getPreferredSize().width*1.5), label.getPreferredSize().height*5);
        tempFrame.setLocation(this.getLocation().x+((this.getWidth()-tempFrame.getWidth())/2), this.getLocation().y+((this.getHeight()-tempFrame.getHeight())/2));
        tempFrame.setEnabled(false);
        tempFrame.setVisible(true);
        // Force-paint the label.
        label.paint(jpanLabel.getGraphics());
        try {
            bf.loadBLOB(iConnection);
        } catch(SQLException sqle) {
            JOptionPane.showMessageDialog(this, new String[] {"Unable to retrieve file from the database:", sqle.getMessage(), "Could not complete save operation!"}, "File retrieval failed!", JOptionPane.ERROR_MESSAGE);
            return;
        } finally {
            tempFrame.setVisible(false);
            tempFrame.dispose();
        }
        // Okay, we have the BLOB.
        // Now find out where to put it.
        boolean lbContinue = true;
        String selectedLocation = "/";
        while(lbContinue) {
            JFileChooser jfc = new JFileChooser(selectedLocation);
            jfc.setDialogType(JFileChooser.CUSTOM_DIALOG);
            jfc.setApproveButtonText("Select output location");
            jfc.setApproveButtonMnemonic(KeyEvent.VK_S);
            jfc.setApproveButtonToolTipText("Select the output location.");
            jfc.setDialogTitle("Select output location");
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int value = jfc.showDialog(BinaryFileRetrieverTool.this, "Select output location");
            if(value == JFileChooser.APPROVE_OPTION) {
                try {
                    File selection = jfc.getSelectedFile();
                    selectedLocation = selection.getAbsolutePath();
                    String filename = bf.getFilename();
                    File output = new File(selection, filename);
                    if(output.exists()) {
                        JOptionPane.showMessageDialog(this, new String[] {"Output object '" + output.getAbsolutePath() + "' exists,", "please provide a new output location!"}, "Output object exists!", JOptionPane.WARNING_MESSAGE);
                        continue;
                    } else {
                        bf.saveBinfileToDisk(selection);
                        JOptionPane.showMessageDialog(this, "Wrote unzipped contents of binary file to: " + output.getAbsolutePath(), "File written.", JOptionPane.INFORMATION_MESSAGE);
                        lbContinue = false;
                    }
                } catch(IOException ioe) {
                    JOptionPane.showMessageDialog(this, new String[] {"Unable to write file to '" + selectedLocation + "':", ioe.getMessage(), "Please provide a new output location!"}, "Unable to write object!", JOptionPane.ERROR_MESSAGE);
                }
            } else if(value == JFileChooser.CANCEL_OPTION) {
                lbContinue = false;
            }
        }
    }

    /**
     * This method initiliazes all the detailed fields on the GUI.
     */
    private void updateDetails() {
        Binfile bf = (Binfile)cmbBinfile.getSelectedItem();
        // The file descriptor stuff.
        long fdID = bf.getL_filedescriptionid();
        Filedescriptor fd = (Filedescriptor)iFiledescriptors.get(new Long(fdID));
        txtFDLabel.setText(fd.getShort_label());
        txtFDDescription.setText(fd.getDescription());
        if(txtFDDescription.getText() != null && txtFDDescription.getText().length() > 0) {
            txtFDDescription.setCaretPosition(1);
        }
        // The binfile details.
        txtFilename.setText(bf.getFilename());
        txtPath.setText(bf.getOriginalpath());
        txtHost.setText(bf.getOriginalhost());
        txtUser.setText(bf.getOriginaluser());
        txtUsername.setText(bf.getUsername());
        txtCreationdate.setText(iSDF.format(bf.getCreationdate()));
        txtModificationdate.setText(iSDF.format(bf.getModificationdate()));
        txtComments.setText(bf.getComments());
        if(txtComments.getText() != null && txtComments.getText().length() > 0) {
            txtComments.setCaretPosition(1);
        }
    }


    public boolean isStandAlone() {
        return false;
    }
}
