/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 30-jul-2003
 * Time: 12:29:44
 */
package com.compomics.mslims.gui;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Id_to_phosphoTableAccessor;
import com.compomics.mslims.db.accessors.Identification;
import com.compomics.mslims.db.accessors.Phosphorylation;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.mascot.MascotSequenceRetriever;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.interfaces.Connectable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class allows the user to connect to a 'projects' database and select a project, after which an accession numbers
 * file is indicated, which will be used to search SwissProt for the possible phosphorylations.
 *
 * @author Lennart Martens
 */
public class SwissProtPhosphoReader extends FlamableJFrame implements Connectable {
    // Class specific log4j logger for SwissProtPhosphoReader instances.
    private static Logger logger = Logger.getLogger(SwissProtPhosphoReader.class);

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
     * This Vector will contain all the accession numbers to request and parse.
     */
    private Vector iAccessions = null;

    private JComboBox cmbProject = null;
    private JTextField txtAccessions = null;
    private JButton btnBrowseAccessions = null;
    private JButton btnParse = null;
    private JButton btnCancel = null;

    /**
     * Constant with the first part of the title.
     */
    private static final String iTitle = "SwissProt phospho annotations parser";


    /**
     * Default constructor.
     */
    public SwissProtPhosphoReader() {
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
        this.setLocation((screen.width / 10), (screen.height / 10));
        this.pack();
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
            this.dispose();
            System.exit(0);
        }
        this.iConn = aConn;
        this.iDBName = aDBName;
        this.setTitle(iTitle + " (connected to: " + iDBName + ")");
    }

    /**
     * Here we just close the DB connection, and then call the dispose() method of the superclass.
     */
    public void dispose() {
        this.closeConnection();
        super.dispose();
    }

    public static void main(String[] args) {
        SwissProtPhosphoReader npp = new SwissProtPhosphoReader();
        npp.setVisible(true);
    }

    /**
     * Here we just close the DB connection, and then call the finalize() method of the superclass.
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
        txtAccessions = new JTextField(30);
        txtAccessions.setMaximumSize(txtAccessions.getPreferredSize());
        txtAccessions.setEditable(false);
        btnBrowseAccessions = new JButton("Browse...");
        btnBrowseAccessions.setMnemonic(KeyEvent.VK_B);
        btnBrowseAccessions.setMaximumSize(btnBrowseAccessions.getPreferredSize());
        btnBrowseAccessions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseAccessionsPressed();
            }
        });
        btnBrowseAccessions.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    browseAccessionsPressed();
                }
            }
        });

        JLabel lblProjects = new JLabel("Select project: ");
        JLabel lblAccessions = new JLabel("Accession numbers file: ");

        lblProjects.setPreferredSize(lblAccessions.getPreferredSize());

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
        jpanNetphos.add(lblAccessions);
        jpanNetphos.add(Box.createHorizontalStrut(5));
        jpanNetphos.add(txtAccessions);
        jpanNetphos.add(Box.createHorizontalStrut(15));
        jpanNetphos.add(btnBrowseAccessions);
        jpanNetphos.add(Box.createHorizontalGlue());

        JPanel jpanControls = new JPanel();
        jpanControls.setLayout(new BoxLayout(jpanControls, BoxLayout.Y_AXIS));
        jpanControls.setBorder(BorderFactory.createTitledBorder("Database and input file"));
        jpanControls.add(jpanProjects);
        jpanControls.add(Box.createVerticalStrut(5));
        jpanControls.add(jpanNetphos);
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
     * This method is called when the user presses the browse button for the Netphos output file.
     */
    private void browseAccessionsPressed() {
        File toOpen = null;
        while (toOpen == null) {
            JFileChooser jfc = new JFileChooser("/");
            int returnVal = jfc.showOpenDialog(SwissProtPhosphoReader.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                toOpen = jfc.getSelectedFile();
                String path = null;
                try {
                    path = toOpen.getCanonicalPath();
                    Vector tempVec = new Vector();
                    BufferedReader br = new BufferedReader(new FileReader(toOpen));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (!line.equals("")) {
                            tempVec.add(line);
                        }
                    }
                    br.close();
                    iAccessions = tempVec;
                    txtAccessions.setText(path);
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage(), ioe);
                    JOptionPane.showMessageDialog(SwissProtPhosphoReader.this, new String[]{"Unable to read '" + toOpen.getName() + "' as accession numbers file!", ioe.getMessage()}, "Unable to load Netphos output file!", JOptionPane.ERROR_MESSAGE);
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
     * @return JPanel with the buttons.
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
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
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

        if (iAccessions == null) {
            needsFocus = btnBrowseAccessions;
            message = "You need to specify an accession numbers file first!";
            fillOutError = true;
        }

        if (fillOutError) {
            JOptionPane.showMessageDialog(this, message, "Not all input fields correctly filled out!", JOptionPane.WARNING_MESSAGE);
            needsFocus.requestFocus();
            return;
        } else {
            // Find the project ID.
            long projectid = ((Project) cmbProject.getSelectedItem()).getProjectid();

            // OK, we're all revved up with a place to go!
            // Start processing!
            int maximum = iAccessions.size();
            DefaultProgressBar progress = new DefaultProgressBar(this, "Parsing known SwissProt phosphorylation sites", 0, maximum);
            Thread t = new Thread(new InnerRunnable(iAccessions, projectid, progress));
            t.start();
            progress.setSize(this.getWidth() / 2, progress.getPreferredSize().height);
            progress.setVisible(true);
        }
    }

    /**
     * This inner class implements the working logic behind the 'parsePressed' method. <br /> It is a Runnable for
     * progressbar purposes.
     */
    private class InnerRunnable implements Runnable {

        /**
         * All the accession numbers.
         */
        private Vector iAccessions = null;
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
         * @param aAccessions vector with all the accession numbers.
         * @param aProjectid  long with the link to the project we're using.
         * @param aProgress   DefaultProgressBar for indicating the progress on.
         */
        public InnerRunnable(Vector aAccessions, long aProjectid, DefaultProgressBar aProgress) {
            this.iAccessions = aAccessions;
            this.iProjectid = aProjectid;
            this.iProgress = aProgress;
        }

        /**
         * Workhorse method for a Thread.
         */
        public void run() {
            // Accession translation, threshold filtering and DB storage.
            Iterator iter = iAccessions.iterator();
            int counter = 0;
            // MascotSequenceRetriever.
            MascotSequenceRetriever msr = new MascotSequenceRetriever("MDMetOx.properties");
            // Loop all accessions.
            while (iter.hasNext()) {
                iProgress.setValue(counter);
                // First accession nbr translation.
                String accession = (String) iter.next();
                String upper = accession.toUpperCase();
                if (!(upper.startsWith("P") || upper.startsWith("Q") || upper.startsWith("O"))) {
                    logger.info("Skipped accession '" + accession + "' because it didn't start with 'P', 'Q' or 'O'.");
                    continue;
                }
                // Change message on progressbar.
                iProgress.setMessage("Retrieving entry for protein " + accession);
                try {
                    // Get the full-text report.
                    String fullText = msr.getRawSequence(accession);
                    // Get all phosphorylation annotations for this accession nbr.
                    iProgress.setMessage("Parsing raw entry for protein " + accession);
                    Vector temps = parseFullTextReport(fullText);
                    int liSize = temps.size();
                    for (int i = 0; i < liSize; i++) {
                        // What we'll need to do now, is find all the identifications that correspond to the
                        // predicted phosphorylation sites, then extract their ID's, and link all to their
                        // respective phosphorylations.
                        Object[] tempObj = (Object[]) temps.get(i);
                        int location = ((Integer) tempObj[1]).intValue();
                        long link = 0l;
                        try {
                            Vector matches = Identification.getIdentifications(accession, location, iProjectid, iConn);
                            int liMatches = matches.size();
                            // See if this prediction has any relevance (ie.: we found at least one match in the DB).
                            if (liMatches > 0) {
                                // Create a new Phosphorylation entry.
                                HashMap hm = new HashMap(7);
                                hm.put(Phosphorylation.ACCESSION, accession);
                                hm.put(Phosphorylation.L_STATUS, new Long(2));
                                hm.put(Phosphorylation.LOCATION, new Long(location));
                                hm.put(Phosphorylation.DESCRIPTION, tempObj[0]);
                                // Effective insert.
                                Phosphorylation p = new Phosphorylation(hm);
                                p.persist(iConn);
                                // Find the PK.
                                Long tempLong = (Long) p.getGeneratedKeys()[0];
                                link = tempLong.longValue();
                            }
                            for (int j = 0; j < liMatches; j++) {
                                // All these are identifications corresponding to this phosphorylation.
                                // We need to store the relation in the id_to_phospho lookup table.
                                long idid = ((Identification) matches.get(j)).getIdentificationid();
                                HashMap hm = new HashMap(2);
                                hm.put(Id_to_phosphoTableAccessor.L_ID, new Long(idid));
                                hm.put(Id_to_phosphoTableAccessor.L_PHOSPHORYLATIONID, new Long(link));

                                Id_to_phosphoTableAccessor conv = new Id_to_phosphoTableAccessor(hm);
                                conv.persist(iConn);
                            }
                        } catch (SQLException sqle) {
                            logger.error("Error retrieving matches for accession '" + accession + "', location '" + location + "' and project '" + cmbProject.getSelectedItem() + "': " + sqle.getMessage());
                        }
                    }
                    counter++;
                } catch (IOException ioe) {
                    logger.error("IOException retrieving fulltext report via Mascot for '" + accession + "': " + ioe.getMessage());
                }
            }
            iProgress.setValue(iProgress.getMaximum());
        }

        /**
         * This method parses a SwissProt full-text report for phosphorylation annotations.
         *
         * @param aText String with the full-text report.
         * @return Vector with the known phosphorylations (each element an Object[] with first the description, then the
         *         location as Integer).
         */
        private Vector parseFullTextReport(String aText) {
            Vector results = new Vector();
            try {
                BufferedReader br = new BufferedReader(new StringReader(aText));
                String line = null;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    String upper = line.toUpperCase();
                    if (upper.startsWith("FT   MOD_RES") && upper.indexOf("  PHOSPHORYLATION") > 0) {
                        // We found a phosphorylation annotation.
                        // Should be:
                        // FT   MOD_RES  xxx(start)  xxx(end)  PHOSPHORYLATION...
                        StringTokenizer st = new StringTokenizer(line, " ");
                        // 'FT'
                        st.nextToken();
                        // 'MOD_RES'
                        st.nextToken();
                        // Start location.
                        int start = Integer.parseInt(st.nextToken());
                        // End location (should be the same).
                        int end = Integer.parseInt(st.nextToken());
                        if (start != end) {
                            logger.error("Found start location " + start + " and end location " + end + "!");
                        } else {
                            results.add(new Object[]{line.substring(upper.indexOf("PHOSPHORYLATION")), new Integer(start)});
                        }
                    }
                }
            } catch (IOException ioe) {
                logger.error("IOException while reading a String: " + ioe.getMessage());
                logger.error(ioe.getMessage(), ioe);
            }
            return results;
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
            if (iConn != null) {
                iConn.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * This method will attempt to retrieve all relevant data from the local filesystem and the DB connection.
     */
    private void gatherData() {
        this.findProjects();
    }

    /**
     * This method finds all project entries currently stored in the DB and fills out the relevant arrays with info.
     */
    private void findProjects() {
        try {
            this.iProjects = Project.getAllProjects(iConn);
        } catch (SQLException sqle) {
            this.passHotPotato(sqle, "Unable to retrieve project data!");
        }
    }

    private static class InnerFastaReader {

        public static HashMap toHashMap(File aFASTAFile) throws IOException {
            HashMap temp = new HashMap();
            BufferedReader br = new BufferedReader(new FileReader(aFASTAFile));
            String line = null;
            boolean isFASTA = false;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Skip empty lines.
                if (line.equals("")) {
                    continue;
                } else if (line.startsWith(">")) {
                    if (!isFASTA) {
                        isFASTA = true;
                    }
                    // Remove the leading '>'.
                    line = line.substring(1);
                    // Find the accession number for Netphos, and that for the DB.
                    if (line.startsWith("gi|")) {
                        // Skip these.
                    } else {
                        int start = line.indexOf(" (");
                        int end = line.indexOf(") ");
                        if (start < 0 || end < 0) {
                            logger.error("Could not find accession number conversion in: " + line + ".");
                            throw new IOException("Unable to parse FASTA entries in sequence file '" + aFASTAFile.getCanonicalPath() + "'!");
                        }
                        temp.put(line.substring(0, start), line.substring(start + 2, end));
                    }
                }
            }
            br.close();

            if (!isFASTA) {
                throw new IOException("Probably not a FASTA formatted file!");
            }

            return temp;
        }
    }


    public boolean isStandAlone() {
        return true;
    }
}
