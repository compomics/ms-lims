/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 30-jul-2003
 * Time: 12:29:44
 */
package com.compomics.mslims.gui;

import com.compomics.mslims.db.accessors.Id_to_phosphoTableAccessor;
import com.compomics.mslims.db.accessors.Identification;
import com.compomics.mslims.db.accessors.Phosphorylation;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.netphos.NetphosOutputReader;
import com.compomics.mslims.util.netphos.NetphosPrediction;
import com.compomics.mslims.util.netphos.PredictedLocation;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.interfaces.Connectable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class allows the user to connect to a 'projects' database and select a project, after
 * which a netphos output file for that project is selected and the class will enter all prediction
 * info into the projects DB.
 *
 * @author Lennart Martens
 */
public class NetphosPredictionsParser extends FlamableJFrame implements Connectable {

    /**
     * Database connection.
     */
    private Connection iConn = null;

    /**
     * Database name
     */
    private String iDBName = null;

    /**
     * The array for the projects.
     */
    private Project[] iProjects = null;

    /**
     * The Netphos output.
     */
    private NetphosOutputReader iReader = null;

    /**
     * A HashMap with the Netphos-reported accession number as key, and the 'real'
     * accession number as value.
     */
    private HashMap iSequences = null;


    private JComboBox cmbProject = null;
    private JTextField txtNetphosOutput = null;
    private JTextField txtSequences = null;
    private JTextField txtThreshold = null;
    private JButton btnBrowseOutput = null;
    private JButton btnBrowseSequences = null;
    private JButton btnParse = null;
    private JButton btnCancel = null;

    /**
     * Constant with the first part of the title.
     */
    private static final String iTitle = "Netphos predictions parser";


    /**
     * Default constructor.
     */
    public NetphosPredictionsParser() {
        super(iTitle);
        // Window closing stuff.
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                closeConnection();
                e.getWindow().dispose();
                System.exit(0);
            }
        });

        // Display the connection dialog.
        this.getConnection();
        // Now we have a connection, gather all data next.
        this.gatherData();
        // Some components need to be initialized with the data retrieved.
        // Do this now.
        this.initializeComponents();
        // Build the GUI.
        this.constructScreen();
        // Display settings.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screen.width/10), (screen.height/10));
        this.pack();
    }

    /**
     * This method will be called by the class actually making the connection.
     * It will pass the connection and an identifier String for that connection
     * (typically the name of the database connected to).
     *
     * @param aConn Connection with the DB connection.
     * @param aDBName   String with an identifier for the connection, typically the
     *                  name of the DB connected to.
     */
    public void passConnection(Connection aConn, String aDBName) {
        if(aConn == null) {
            this.dispose();
            System.exit(0);
        }
        this.iConn = aConn;
        this.iDBName = aDBName;
        this.setTitle(iTitle + " (connected to: " + iDBName + ")");
    }

    /**
     * Here we just close the DB connection, and then call
     * the dispose() method of the superclass.
     */
    public void dispose() {
        this.closeConnection();
        super.dispose();
    }

    public static void main(String[] args) {
        NetphosPredictionsParser npp = new NetphosPredictionsParser();
        npp.setVisible(true);
    }

    /**
     * Here we just close the DB connection, and then call
     * the finalize() method of the superclass.
     */
    protected void finalize() throws Throwable {
        this.closeConnection();
        super.finalize();
    }

    /**
     * This method constructs and lays out the GUI.
     */
    private void constructScreen() {
        // Initialize components.
        txtNetphosOutput = new JTextField(30);
        txtNetphosOutput.setMaximumSize(txtNetphosOutput.getPreferredSize());
        txtNetphosOutput.setEditable(false);
        btnBrowseOutput = new JButton("Browse...");
        btnBrowseOutput.setMnemonic(KeyEvent.VK_B);
        btnBrowseOutput.setMaximumSize(btnBrowseOutput.getPreferredSize());
        btnBrowseOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseOutputPressed();
            }
        });
        btnBrowseOutput.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    browseOutputPressed();
                }
            }
        });

        txtSequences = new JTextField(30);
        txtSequences.setMaximumSize(txtSequences.getPreferredSize());
        txtSequences.setEditable(false);
        btnBrowseSequences = new JButton("Browse...");
        btnBrowseSequences.setMnemonic(KeyEvent.VK_R);
        btnBrowseSequences.setMaximumSize(btnBrowseSequences.getPreferredSize());
        btnBrowseSequences.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseSequencesPressed();
            }
        });
        btnBrowseSequences.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    browseSequencesPressed();
                }
            }
        });


        txtThreshold = new JTextField(10);
        txtThreshold.setMaximumSize(txtThreshold.getPreferredSize());

        JLabel lblProjects = new JLabel("Select project: ");
        JLabel lblNetphos = new JLabel("Netphos output file: ");
        JLabel lblSequence = new JLabel("Original sequence file: ");
        JLabel lblThreshold = new JLabel("Threshold for score: ");

        lblProjects.setPreferredSize(lblSequence.getPreferredSize());
        lblNetphos.setPreferredSize(lblSequence.getPreferredSize());
        lblThreshold.setPreferredSize(lblSequence.getPreferredSize());

        JPanel jpanProjects = new JPanel();
        jpanProjects.setLayout(new BoxLayout(jpanProjects, BoxLayout.X_AXIS));
        jpanProjects.add(Box.createHorizontalStrut(5));
        jpanProjects.add(lblProjects);
        jpanProjects.add(Box.createHorizontalStrut(5));
        jpanProjects.add(cmbProject);
        jpanProjects.add(Box.createHorizontalGlue());

        JPanel jpanNetphos = new JPanel();
        jpanNetphos.setLayout(new BoxLayout(jpanNetphos, BoxLayout.X_AXIS));
        jpanNetphos.add(Box.createHorizontalStrut(5));
        jpanNetphos.add(lblNetphos);
        jpanNetphos.add(Box.createHorizontalStrut(5));
        jpanNetphos.add(txtNetphosOutput);
        jpanNetphos.add(Box.createHorizontalStrut(15));
        jpanNetphos.add(btnBrowseOutput);
        jpanNetphos.add(Box.createHorizontalGlue());

        JPanel jpanSequences = new JPanel();
        jpanSequences.setLayout(new BoxLayout(jpanSequences, BoxLayout.X_AXIS));
        jpanSequences.add(Box.createHorizontalStrut(5));
        jpanSequences.add(lblSequence);
        jpanSequences.add(Box.createHorizontalStrut(5));
        jpanSequences.add(txtSequences);
        jpanSequences.add(Box.createHorizontalStrut(15));
        jpanSequences.add(btnBrowseSequences);
        jpanSequences.add(Box.createHorizontalGlue());

        JPanel jpanThreshold = new JPanel();
        jpanThreshold.setLayout(new BoxLayout(jpanThreshold, BoxLayout.X_AXIS));
        jpanThreshold.add(Box.createHorizontalStrut(5));
        jpanThreshold.add(lblThreshold);
        jpanThreshold.add(Box.createHorizontalStrut(5));
        jpanThreshold.add(txtThreshold);
        jpanThreshold.add(Box.createHorizontalGlue());

        JPanel jpanControls = new JPanel();
        jpanControls.setLayout(new BoxLayout(jpanControls, BoxLayout.Y_AXIS));
        jpanControls.setBorder(BorderFactory.createTitledBorder("Netphos parameters"));
        jpanControls.add(jpanProjects);
        jpanControls.add(Box.createVerticalStrut(5));
        jpanControls.add(jpanNetphos);
        jpanControls.add(Box.createVerticalStrut(5));
        jpanControls.add(jpanSequences);
        jpanControls.add(Box.createVerticalStrut(5));
        jpanControls.add(jpanThreshold);
        jpanControls.add(Box.createVerticalStrut(5));

        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanControls);
        jpanMain.add(Box.createVerticalStrut(15));
        jpanMain.add(this.createButtonPanel());
        jpanMain.add(Box.createVerticalGlue());

        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method is called when the user presses the browse button
     * for the Netphos output file.
     */
    private void browseOutputPressed() {
        File toOpen = null;
        while(toOpen == null) {
            JFileChooser jfc = new JFileChooser("/");
            int returnVal = jfc.showOpenDialog(NetphosPredictionsParser.this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                toOpen = jfc.getSelectedFile();
                String path = null;
                try {
                    path = toOpen.getCanonicalPath();
                    iReader = new NetphosOutputReader(toOpen);
                    txtNetphosOutput.setText(path);
                } catch(IOException ioe) {
                    JOptionPane.showMessageDialog(NetphosPredictionsParser.this, new String[]{"Unable to read '" + toOpen.getName() + "' as Netphos output file!", ioe.getMessage()}, "Unable to load Netphos output file!", JOptionPane.ERROR_MESSAGE);
                    toOpen = null;
                }
            } else {
                break;
            }
        }
    }

    /**
     * This method is called when the user presses the browse button
     * for the original sequence file.
     */
    private void browseSequencesPressed() {
        File toOpen = null;
        while(toOpen == null) {
            JFileChooser jfc = new JFileChooser("/");
            int returnVal = jfc.showOpenDialog(NetphosPredictionsParser.this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                toOpen = jfc.getSelectedFile();
                String path = null;
                try {
                    path = toOpen.getCanonicalPath();
                    iSequences = InnerFastaReader.toHashMap(toOpen);
                    txtSequences.setText(path);
                } catch(IOException ioe) {
                    JOptionPane.showMessageDialog(NetphosPredictionsParser.this, new String[]{"Unable to read '" + toOpen.getName() + "' as a FASTA sequences file!", ioe.getMessage()}, "Unable to load Netphos output file!", JOptionPane.ERROR_MESSAGE);
                    toOpen = null;
                }
            } else {
                break;
            }
        }
    }

    /**
     * This method creates the buttonpanel.
     *
     * @return  JPanel with the buttons.
     */
    private JPanel createButtonPanel() {
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));

        btnParse = new JButton("Parse");
        btnParse.setMnemonic(KeyEvent.VK_P);
        btnParse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parsePressed();
            }
        });
        btnParse.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    parsePressed();
                }
            }
        });

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


        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnParse);
        jpanButtons.add(Box.createHorizontalStrut(10));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(5));

        return jpanButtons;
    }

    /**
     * This method is called when the user presses cancel.
     */
    private void cancelPressed() {
        this.dispose();
        System.exit(0);
    }

    /**
     * This method is called when the user presses parse.
     */
    private void parsePressed() {
        // Okay, we should be parsing, but first check whether everything has been filled out correctly.
        String message = null;
        JComponent needsFocus = null;
        boolean fillOutError = false;
        double threshold = -1;

        if(iReader == null) {
            needsFocus = btnBrowseOutput;
            message = "You need to specify a Netphos output file first!";
            fillOutError = true;
        } else if(iSequences == null) {
            needsFocus = btnBrowseSequences;
            message = "You need to specify an original sequence input file first!";
            fillOutError = true;
        } else if(!txtThreshold.getText().trim().equals("")) {
           try {
               threshold = Double.parseDouble(txtThreshold.getText().trim());
               if(threshold < 0) {
                   throw new NumberFormatException("Threshold must be positive!");
               }
           } catch(NumberFormatException nfe) {
               needsFocus = txtThreshold;
               message = "The threshold value must be a positive decimal number or blank for no threshold!";
               fillOutError = true;
           }
        }

        if(fillOutError) {
            JOptionPane.showMessageDialog(this, message, "Not all input fields correctly filled out!", JOptionPane.WARNING_MESSAGE);
            needsFocus.requestFocus();
            return;
        } else {
            // If no threshold was specified, set it to '0.0'.
            if(threshold < 0) {
                threshold = 0.0;
            }

            // Find the project ID.
            long projectid = ((Project)cmbProject.getSelectedItem()).getProjectid();

            // OK, we're all revved up with a place to go!
            // First things first: we should get all the predicted phosphorylation sites,
            // change the accession number to something we can work with,
            // and do not forget to filter for threshold!
            HashMap all = iReader.getPredictions();

            int maximum = all.size();
            DefaultProgressBar progress = new DefaultProgressBar(this, "Parsing and storing Netphos output", 0, maximum);
            Thread t = new Thread(new InnerRunnable(all, threshold, projectid, progress));
            t.start();
            progress.setSize(this.getWidth()/2, progress.getPreferredSize().height);
            progress.setVisible(true);
        }
    }

    /**
     * This inner class implements the working logic behind the 'parsePressed' method. <br />
     * It is a Runnable for progressbar purposes.
     */
    private class InnerRunnable implements Runnable {

        /**
         * All the phosphorylation predictions.
         */
        private HashMap iAllPredictions = null;
        /**
         * The threshold for a good prediction.
         */
        private double iThreshold = -1.0;
        /**
         * Project we're using.
         */
        private long iProjectid = -1l;
        /**
         * The progressbar.
         */
        private DefaultProgressBar iProgress = null;
        /**
         * The constructor takes the necessary parameters.
         *
         * @param aAllPredictions   HashMap with all the phosphorylation predictions.
         * @param aThreshold    double with the threshold.
         * @param aProjectid    long with the link to the project we're using.
         * @param aProgress DefaultProgressBar for indicating the progress on.
         */
        public InnerRunnable(HashMap aAllPredictions, double aThreshold, long aProjectid, DefaultProgressBar aProgress) {
            this.iAllPredictions = aAllPredictions;
            this.iThreshold = aThreshold;
            this.iProjectid = aProjectid;
            this.iProgress = aProgress;
        }
        /**
         * Workhorse method for a Thread.
         */
        public void run() {
            // Accession translation, threshold filtering and DB storage.
            Iterator iter = iAllPredictions.values().iterator();
            int counter = 0;
            while(iter.hasNext()) {
                iProgress.setValue(counter);
                NetphosPrediction lNetphosPrediction = (NetphosPrediction)iter.next();
                // First accession nbr translation.
                String accession = lNetphosPrediction.getAccession();
                // NCBI or swissprot?
                if(accession.startsWith("gi")) {
                    // NCBI. Just remove the leading 'gi_'.
                    int start = accession.indexOf("gi_")+3;
                    accession = accession.substring(start);
                    // See if a trailing '_' is present, as well.
                    if(accession.endsWith("_")) {
                        accession = accession.substring(0, accession.length()-1);
                    }
                } else {
                    // Should be SwissProt.
                    Object temp = iSequences.get(accession);
                    if(temp == null) {
                        System.err.println("\n\nUnidentified accession String '" + accession + "' encountered!\nIgnoring entry!!\n\n");
                        continue;
                    } else {
                        accession = (String)temp;
                    }
                }
                // Reset accession.
                lNetphosPrediction.setAccession(accession);
                iProgress.setMessage("Analyzing predictions for protein " + accession);
                // Get all predictions for this accession nbr.
                Vector temps = lNetphosPrediction.getLocations(iThreshold);
                int liSize = temps.size();
                for(int i=0;i<liSize;i++) {
                    // What we'll need to do now, is find all the identifications that correspond to the
                    // predicted phosphorylation sites, then extract their ID's, and link all to their
                    // respective phosphorylations.
                    PredictedLocation pl = (PredictedLocation)temps.get(i);
                    int location = pl.getLocation();
                    long link = 0l;
                    try {
                        Vector matches = Identification.getIdentifications(accession, location, iProjectid, iConn);
                        int liMatches = matches.size();
                        // See if this prediction has any relevance (ie.: we found at least one match in the DB.
                        if(liMatches > 0) {
                            // Create a new Phosphorylation entry.
                            HashMap hm = new HashMap(7);
                            hm.put(Phosphorylation.ACCESSION, accession);
                            hm.put(Phosphorylation.CONTEXT, pl.getContext());
                            hm.put(Phosphorylation.L_STATUS, new Long(1));
                            hm.put(Phosphorylation.LOCATION, new Long(pl.getLocation()));
                            hm.put(Phosphorylation.RESIDUE, pl.getResidue());
                            hm.put(Phosphorylation.SCORE, new Double(pl.getScore()));
                            hm.put(Phosphorylation.THRESHOLD, new Double(iThreshold));
                            // Effective insert.
                            Phosphorylation p = new Phosphorylation(hm);
                            p.persist(iConn);
                            // Find the PK.
                            Long tempLong = (Long)p.getGeneratedKeys()[0];
                            link = tempLong.longValue();
                        }
                        for(int j=0;j<liMatches;j++) {
                            // All these are identifications corresponding to this phosphorylation.
                            // We need to store the relation in the id_to_phospho lookup table.
                            long idid = ((Identification)matches.get(j)).getIdentificationid();
                            HashMap hm = new HashMap(2);
                            hm.put(Id_to_phosphoTableAccessor.L_ID, new Long(idid));
                            hm.put(Id_to_phosphoTableAccessor.L_PHOSPHORYLATIONID, new Long(link));

                            Id_to_phosphoTableAccessor conv = new Id_to_phosphoTableAccessor(hm);
                            conv.persist(iConn);
                        }
                    } catch(SQLException sqle) {
                        System.err.println("Error retrieving matches for accession '" + accession + "', location '" + location + "' and project '" + cmbProject.getSelectedItem() + "': " + sqle.getMessage());
                    }
                }
                counter++;
            }
            iProgress.setValue(iProgress.getMaximum());
        }
    }

    /**
     * This method fills out the components on the GUI.
     */
    private void initializeComponents() {
        cmbProject = new JComboBox();
        cmbProject.setModel(new DefaultComboBoxModel(iProjects));
    }

    /**
     * This method calls upon a GUI component to handle the connection.
     */
    private void getConnection() {
        ConnectionDialog cd = new ConnectionDialog(this, this, "Database connection for SpectrumManager", "QTOFSpectrumStorage.properties");
        cd.setVisible(true);
    }

    /**
     * This method should be called by the finalize and dispose methods.
     */
    private void closeConnection() {
        try {
            if(iConn != null) {
                iConn.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will attempt to retrieve all relevant data from the
     * local filesystem and the DB connection.
     */
    private void gatherData() {
        this.findProjects();
    }

    /**
     * This method finds all project entries currently stored in the DB
     * and fills out the relevant arrays with info.
     */
    private void findProjects() {
        try {
            this.iProjects = Project.getAllProjects(iConn);
        } catch(SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve project data!");
        }
    }

    private static class InnerFastaReader {

        public static HashMap toHashMap(File aFASTAFile) throws IOException {
            HashMap temp = new HashMap();
            BufferedReader br = new BufferedReader(new FileReader(aFASTAFile));
            String line = null;
            boolean isFASTA = false;
            while((line = br.readLine()) != null) {
                line = line.trim();
                // Skip empty lines.
                if(line.equals("")) {
                    continue;
                } else if(line.startsWith(">")) {
                    if(!isFASTA) {
                        isFASTA = true;
                    }
                    // Remove the leading '>'.
                    line = line.substring(1);
                    // Find the accession number for Netphos, and that for the DB.
                    if(line.startsWith("gi|")) {
                        // Skip these.
                    } else {
                        int start = line.indexOf(" (");
                        int end = line.indexOf(") ");
                        if(start < 0 || end < 0) {
                            System.err.println("Could not find accession number conversion in: " + line + ".");
                            throw new IOException("Unable to parse FASTA entries in sequence file '" + aFASTAFile.getCanonicalPath() + "'!");
                        }
                        temp.put(line.substring(0, start), line.substring(start+2, end));
                    }
                }
            }
            br.close();

            if(!isFASTA) {
                throw new IOException("Probably not a FASTA formatted file!");
            }

            return temp;
        }
    }


    public boolean isStandAlone() {
        return true;
    }
}
