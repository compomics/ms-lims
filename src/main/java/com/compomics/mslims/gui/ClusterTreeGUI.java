package com.compomics.mslims.gui;

/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 9-feb-2005
 * Time: 17:38:40
 */

/*
 * CVS information:
 *
 * $Revision: 1.10 $
 * $Date: 2009/07/28 14:48:33 $
 */

import com.compomics.mslims.db.accessors.Identification;
import com.compomics.mslims.db.accessors.Spectrumfile;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.gui.table.IdentificationTableModel;
import com.compomics.mslims.gui.tree.ClusterTreeModel;
import com.compomics.mslims.gui.tree.renderers.ClusterTreeCellRenderer;
import com.compomics.mslims.util.fileio.MascotGenericFile;
import com.compomics.mslims.util.workers.LoadDBDataWorker;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.gui.JTableForDB;
import com.compomics.util.gui.events.RescalingEvent;
import com.compomics.util.gui.interfaces.SpectrumPanelListener;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.compomics.util.interfaces.Connectable;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.util.*;

/**
 * This class represents clustered spectra using a JTree.
 *
 * @author Lennart Martens
 * @version $Id: ClusterTreeGUI.java,v 1.10 2009/07/28 14:48:33 lennart Exp $
 */
public class ClusterTreeGUI extends FlamableJFrame implements Connectable, SpectrumPanelListener {

    /**
     * The DB connection, if any. 'null' when not connected.
     */
    private Connection iConn = null;
    /**
     * The DB name we are connected to, or 'null' in case of no connection.
     */
    private String iDBName = null;

    /**
     * This HashMap will contain the mapping of spectrum filenames (String, key) to
     * identifications (Identification instances, value) - or is 'null' in the absence of
     * a DB connection.
     */
    private HashMap iSpectrumIDMappings = null;

    private JTree trClusters = null;
    private ClusterTreeModel ctmClusters = null;
    private JTableForDB tblIdentifications = null;
    private JPanel jpanSpectra = null;
    private JCheckBox chkRescaleAll = null;
    private JButton btnAlignSpectra = null;
    private JButton btnRemoveSpectra = null;
    private JTextField txtSearchSpectrum = null;
    private JButton btnSearchSpectrum = null;

    /**
     * The main method is the entry point for the application.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        
        // Check the arguments.
        if(args == null  || args.length != 1) {
            printUsage();
        }
        // Check the file.
        File input = new File(args[0]);
        if(!input.exists()) {
            printError("The file you specified ('" + args[0] + "') does not exist!");
        }
        // Okay, let's go!
        try {
            // Load the data in the file.
            HashMap clusters = new HashMap();
            String line = null;
            BufferedReader br = new BufferedReader(new FileReader(input));
            String currentCluster = null;
            Vector currentSpectra = null;
            while((line = br.readLine()) != null) {
                line = line.trim();
                // Skip empty lines.
                if(!line.equals("")) {
                    // See if it is a new cluster, or data for the current one.
                    if(line.toLowerCase().indexOf("cluster number:") >= 0) {
                        // Start of a new cluster.
                        // See if we already have a cluster we're working with, in which
                        // case we store it.
                        if(currentCluster != null && currentSpectra != null) {
                            Collections.sort(currentSpectra);
                            clusters.put(currentCluster, currentSpectra);
                        }
                        currentSpectra = new Vector();
                        currentCluster = "Cluster " + line.substring(line.indexOf(":")+1).trim();
                    } else {
                        // We are in the currentCluster and should add the spectrum name to the
                        // currentSpectra list.
                        // Note that we transform legacy '.pkl' into '.mgf'.
                        int start = line.indexOf(".pkl");
                        if(start >= 0) {
                            line = line.substring(0, start) + ".mgf";
                        }
                        currentSpectra.add(line);
                    }
                }
            }
            br.close();
            // We mustn't forget to add the last cluster.
            clusters.put(currentCluster, currentSpectra);
            // Okay, so now we have a mapping of clusters to spectra.
            // Let's construct a GUI instance to display this stuff on.
            ClusterTreeGUI ctg = new ClusterTreeGUI(clusters);
            ctg.setBounds(250, 250, 550, 450);
            ctg.setVisible(true);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This constructor initializes a ClusterTreeGUI and attempts to tie it in with a database.
     * If this succeeds, it will not only show the tree itself, but also a panel on which spectra
     * can be displayed as well as a table in which identifications can be visualized.
     * In the case the connection fails (typically because the user cancels the connection),
     * the panel with spectra and the table will not be displayed.
     *
     * @param aClusters HashMap with the name of the cluster (as String) as key, and the
     *                  Vector of associated spectra (each a String) as value.
     */
    public ClusterTreeGUI(HashMap aClusters) {
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        // First, let's extract all the individual spectra from the HashMap into a List.
        ArrayList allSpectra = new ArrayList();
        Iterator iter = aClusters.values().iterator();
        while (iter.hasNext()) {
            Vector v = (Vector)iter.next();
            for (int i = 0; i < v.size(); i++) {
                allSpectra.add(v.elementAt(i));
            }
        }
        // Now see if we can create a DB connection.
        ConnectionDialog cd = new ConnectionDialog(this, this, "Connection for the Cluster tree viewer", "ClusterTreeGUI.properties");
        cd.setLocation(100, 100);
        cd.setVisible(true);
        int idCount = -1;
        if(iConn != null) {
            DefaultProgressBar progress = new DefaultProgressBar(this, "Reading identifications from DB '" + iDBName + "'", 0, allSpectra.size()+1, "Initializing database connection to '" + iDBName + "'...");
            iSpectrumIDMappings = new HashMap();
            LoadDBDataWorker worker = new LoadDBDataWorker(this, iConn, iSpectrumIDMappings, allSpectra, progress);
            worker.start();
            progress.setVisible(true);
            Iterator iter2 = iSpectrumIDMappings.values().iterator();
            while (iter2.hasNext()) {
                if(iter2.next() != null) {
                    idCount++;
                }
            }
        }
        // Create and lay out the GUI.
        constructScreen("Cluster tree viewer", aClusters, allSpectra.size(), idCount);
    }

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
        this.iConn = aConn;
        this.iDBName = aDBName;
    }

    /**
     * This method will be called whenever the SpectrumPanel is rescaled.
     *
     * @param aSe ResizinEvent with the details of the rescaling.
     */
    public void rescaled(RescalingEvent aSe) {
        if(chkRescaleAll.isSelected()) {
            // Rescale all listed specpanels, except for the one who threw the event
            // as this one is already OK.
            JPanel source = aSe.getSource();
            double minMass = aSe.getMinMass();
            double maxMass = aSe.getMaxMass();
            Component[] components = jpanSpectra.getComponents();
            for (int i = 0; i < components.length; i++) {
                Component lComponent = components[i];
                if(lComponent != source) {
                    ((SpectrumPanel)lComponent).rescale(minMass, maxMass, false);
                }
            }
            jpanSpectra.repaint();
        }
    }

    /**
     * This method lays out and initializes the GUI.
     *
     * @param aTitle    String with the start of the frame title (some info appended here).
     * @param aClusters HashMap with the cluster to spectra mappings. Cluster name (String) is key,
     *                  Vector of spectra names (Strings) is value.
     * @param aSpecCount    int with the total number of spectra counted.
     * @param aIDCount  int with the total number of identified spectra counted.
     */
    private void constructScreen(String aTitle, HashMap aClusters, int aSpecCount, int aIDCount) {
        // Prepare the title.
        String affix = " (" + aClusters.size() + " clusters; " + aSpecCount + " spectra";

        JPanel jpanMain = new JPanel(new BorderLayout());

        // The text field for searches.
        txtSearchSpectrum = new JTextField(20);
        txtSearchSpectrum.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if((!btnSearchSpectrum.isEnabled()) && txtSearchSpectrum.getText() != null && (!txtSearchSpectrum.getText().trim().equals(""))) {
                    btnSearchSpectrum.setEnabled(true);
                } else if(btnSearchSpectrum.isEnabled() && (txtSearchSpectrum.getText() == null || txtSearchSpectrum.getText().trim().equals(""))) {
                    btnSearchSpectrum.setEnabled(false);
                }
            }
        });
        txtSearchSpectrum.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(txtSearchSpectrum.getText() != null && (!txtSearchSpectrum.getText().trim().equals(""))) {
                        searchPressed();
                    }
                }
            }
        });
        // The button to search for a spectrum.
        btnSearchSpectrum = new JButton("Search");
        btnSearchSpectrum.setMnemonic(KeyEvent.VK_E);
        btnSearchSpectrum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchPressed();
            }
        });
        btnSearchSpectrum.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchPressed();
                }
            }
        });
        btnSearchSpectrum.setEnabled(false);
        JPanel jpanSearch = new JPanel();
        jpanSearch.setLayout(new BoxLayout(jpanSearch, BoxLayout.X_AXIS));
        jpanSearch.add(txtSearchSpectrum);
        jpanSearch.add(Box.createHorizontalStrut(5));
        jpanSearch.add(btnSearchSpectrum);


        ctmClusters = new ClusterTreeModel(aClusters);
        trClusters = new JTree(ctmClusters);

        trClusters.setCellRenderer(new ClusterTreeCellRenderer(iSpectrumIDMappings));

        // Layout changes dramatically if we are connected to the database and therefore have
        // auxillary information.
        if(iConn == null) {
            jpanMain.add(new JScrollPane(trClusters));
        } else {
            jpanSpectra = new JPanel();
            jpanSpectra.setLayout(new BoxLayout(jpanSpectra, BoxLayout.Y_AXIS));

            tblIdentifications = new JTableForDB(null);
            tblIdentifications.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            chkRescaleAll = new JCheckBox("Link spectra rescaling");
            chkRescaleAll.setSelected(false);
            chkRescaleAll.setEnabled(false);
            chkRescaleAll.setMnemonic(KeyEvent.VK_L);

            JButton btnTable = new JButton("Display in table");
            btnTable.setMnemonic(KeyEvent.VK_T);
            btnTable.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    displayOnTablePressed();
                }
            });
            btnTable.addKeyListener(new KeyAdapter() {
                /**
                 * Invoked when a key has been pressed.
                 */
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        displayOnTablePressed();
                    }
                }
            });

            JButton btnSpectra = new JButton("Display spectra");
            btnSpectra.setMnemonic(KeyEvent.VK_S);
            btnSpectra.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    displaySpectraPressed();
                }
            });
            btnSpectra.addKeyListener(new KeyAdapter() {
                /**
                 * Invoked when a key has been pressed.
                 */
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        displaySpectraPressed();
                    }
                }
            });

            btnAlignSpectra = new JButton("Align spectra");
            btnAlignSpectra.setMnemonic(KeyEvent.VK_A);
            btnAlignSpectra.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    alignSpectraPressed();
                }
            });
            btnAlignSpectra.addKeyListener(new KeyAdapter() {
                /**
                 * Invoked when a key has been pressed.
                 */
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        alignSpectraPressed();
                    }
                }
            });
            btnAlignSpectra.setEnabled(false);

            btnRemoveSpectra = new JButton("Remove all spectra");
            btnRemoveSpectra.setMnemonic(KeyEvent.VK_R);
            btnRemoveSpectra.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeSpectraPressed();
                }
            });
            btnRemoveSpectra.addKeyListener(new KeyAdapter() {
                /**
                 * Invoked when a key has been pressed.
                 */
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                        removeSpectraPressed();
                    }
                }
            });
            btnRemoveSpectra.setEnabled(false);

            JPanel jpanTreeButtons = new JPanel();
            jpanTreeButtons.setLayout(new BoxLayout(jpanTreeButtons, BoxLayout.X_AXIS));
            jpanTreeButtons.add(btnTable);
            jpanTreeButtons.add(Box.createHorizontalStrut(2));
            jpanTreeButtons.add(Box.createHorizontalGlue());
            jpanTreeButtons.add(Box.createHorizontalStrut(2));
            jpanTreeButtons.add(btnSpectra);


            JPanel jpanSpectrumButtons = new JPanel();
            jpanSpectrumButtons.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            jpanSpectrumButtons.setLayout(new BoxLayout(jpanSpectrumButtons, BoxLayout.X_AXIS));

            jpanSpectrumButtons.add(Box.createHorizontalStrut(15));
            jpanSpectrumButtons.add(chkRescaleAll);
            jpanSpectrumButtons.add(Box.createHorizontalGlue());
            jpanSpectrumButtons.add(btnAlignSpectra);
            jpanSpectrumButtons.add(Box.createHorizontalStrut(5));
            jpanSpectrumButtons.add(btnRemoveSpectra);
            jpanSpectrumButtons.add(Box.createHorizontalStrut(10));

            JPanel jpanSpectraAndButtons = new JPanel(new BorderLayout());
            jpanSpectraAndButtons.add(jpanSpectra, BorderLayout.CENTER);
            jpanSpectraAndButtons.add(jpanSpectrumButtons, BorderLayout.SOUTH);

            JPanel jpanTree = new JPanel(new BorderLayout());
            jpanTree.add(jpanSearch, BorderLayout.NORTH);
            jpanTree.add(new JScrollPane(trClusters), BorderLayout.CENTER);
            jpanTree.add(jpanTreeButtons, BorderLayout.SOUTH);

            JPanel jpanTable = new JPanel(new BorderLayout());
            jpanTable.add(new JScrollPane(tblIdentifications), BorderLayout.CENTER);

            JSplitPane splPaneTreeSpectra = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            splPaneTreeSpectra.add(jpanTree);
            splPaneTreeSpectra.add(jpanSpectraAndButtons);

            JSplitPane splPaneTableTree = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            splPaneTableTree.setResizeWeight(0.8);
            splPaneTableTree.add(splPaneTreeSpectra);
            splPaneTableTree.add(jpanTable);

            jpanMain.add(splPaneTableTree, BorderLayout.CENTER);

            // Affix the title with additional info.
            affix += "; " + aIDCount + " identifications; connected to " + iDBName;
        }

        this.setTitle(aTitle + affix+")");
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
        this.setLocation(300, 300);
        this.pack();
    }

    /**
     * This method is called when the user presses the 'display on table' button.
     */
    private void displayOnTablePressed() {
        if(iSpectrumIDMappings != null) {
            TreePath[] paths = trClusters.getSelectionPaths();
            if(paths != null && paths.length > 0) {
                Vector ids = new Vector(paths.length);
                for (int i = 0; i < paths.length; i++) {
                    TreePath lPath = paths[i];
                    Object o = lPath.getLastPathComponent();
                    if(ctmClusters.isLeaf(o)) {
                        Identification temp = (Identification)iSpectrumIDMappings.get(o);
                        if(temp != null) {
                            ids.add(temp);
                        }
                    }
                }
                tblIdentifications.setModel(new IdentificationTableModel(ids));
                repaint();
            }
        }
    }

    /**
     * This method is called when the user presses the 'align spectra' button.
     */
    private void alignSpectraPressed() {
        // Get all components.
        Component[] components = jpanSpectra.getComponents();
        double min = 1000.0;
        double max = 0.0;
        // Find the smalles and largest mass across all spectra.
        for (int i = 0; i < components.length; i++) {
            Component lComponent = components[i];
            if(lComponent instanceof SpectrumPanel) {
                double tempMin = ((SpectrumPanel)lComponent).getMinMass();
                double tempMax = ((SpectrumPanel)lComponent).getMaxMass();
                if(tempMin < min) {
                    min = tempMin;
                }
                if(tempMax > max) {
                    max = tempMax;
                }
            }
        }
        // Rescale all to the largest and smallest masses across all spetcra.
        for (int i = 0; i < components.length; i++) {
            Component lComponent = components[i];
            if(lComponent instanceof SpectrumPanel) {
                ((SpectrumPanel)lComponent).rescale(min, max, false);
            }
        }
        // Repaint 'em.
        jpanSpectra.repaint();
    }

    /**
     * This method is called when the user presses the 'search' button.
     */
    private void searchPressed() {
        String searchString = txtSearchSpectrum.getText().trim().toLowerCase();
        Vector foundOnes = new Vector();
        // Traverse the tree.
        Object root = trClusters.getModel().getRoot();
        Vector path = new Vector();
        path.add(root);
        recurseTree(searchString, root, path, foundOnes);
        if(foundOnes.size() > 0) {
            // We now have all the matching clusters with the individual spectra.
            collapseTree();
            // Now expand all the matching ones.
            TreePath[] tpArray = new TreePath[foundOnes.size()];
            foundOnes.toArray(tpArray);
            for (int i = 0; i < tpArray.length; i++) {
                TreePath lTreePath = tpArray[i];
                trClusters.expandPath(lTreePath);
            }
            // Now select all matches.
            trClusters.setSelectionPaths(tpArray);
            JOptionPane.showMessageDialog(this, new String[] {"Found " + foundOnes.size() + " hits in the tree for query '" + searchString + "'.", "They have been expanded and highlighted."}, foundOnes.size() + " hits found!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No hits found in the tree for query '" + searchString + "'.", "No hits found!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * This method completely collapses the tree on the GUI.
     */
    private void collapseTree() {
        int row = trClusters.getRowCount() - 1;
        while (row > 0) {
            trClusters.collapseRow(row);
            row--;
        }
    }

    /**
     * Recurse the tree, looking for the specified search String, constructing
     * TreePaths along the way if they are in fact found (only in the leafs, mind you).
     *
     * @param aSearch   String to search for (match is case insensitive and is done
     *                  on substring match, ie 'oba' matches 'FoObAr').
     * @param aParent   Object with the parent node to explore. This parent should be
     *                  the last element in the aPath Vector as well.
     * @param aPath     Vector with the complete path so far.
     * @param aFound    Vector in which to store the TreePath instances that were found.
     *                  <b>Please note</b> that this is a reference parameter.
     */
    private void recurseTree(String aSearch, Object aParent, Vector aPath, Vector aFound) {
        if(trClusters.getModel().isLeaf(aParent)) {
            // See if it contains the search String.
            if(aParent.toString().toLowerCase().indexOf(aSearch) >= 0) {
                // Okay, reconstruct the TreePath.
                Object[] path = new Object[aPath.size()];
                aPath.toArray(path);
                aFound.add(new TreePath(path));
            }
            aPath.remove(aParent);
        } else {
            int childCount = trClusters.getModel().getChildCount(aParent);
            for(int i=0;i<childCount;i++) {
                Object child = trClusters.getModel().getChild(aParent, i);
                aPath.add(child);
                recurseTree(aSearch, child, aPath, aFound);
            }
            aPath.remove(aParent);
        }
    }

    /**
     * This method is called when the user presses the 'remove all spectra' button.
     */
    private void removeSpectraPressed() {
        jpanSpectra.removeAll();
        btnAlignSpectra.setEnabled(false);
        jpanSpectra.validate();
        jpanSpectra.repaint();
        btnRemoveSpectra.setEnabled(false);
        chkRescaleAll.setEnabled(false);
    }

    /**
     * This method is called when the user presses the 'display spectra' button.
     */
    private void displaySpectraPressed() {
        TreePath[] nodes = trClusters.getSelectionPaths();
        if(nodes != null && nodes.length <= 10) {
            for (int i = 0; i < nodes.length; i++) {
                TreePath lNode = nodes[i];
                Object selectObject = lNode.getLastPathComponent();
                if(ctmClusters.isLeaf(selectObject)) {
                    String filename = (String)selectObject;
                    try {
                        Spectrumfile sf = Spectrumfile.findFromName(filename, iConn);
                        Color filenameColor = null;
                        if(iSpectrumIDMappings.get(filename) == null) {
                            filenameColor = Color.red;
                        }
                        final SpectrumPanel temp = new SpectrumPanel(new MascotGenericFile(filename, new String(sf.getUnzippedFile())));
                        temp.setSpectrumFilenameColor(filenameColor);
                        temp.setLayout(new BoxLayout(temp, BoxLayout.Y_AXIS));
                        temp.addSpectrumPanelListener(this);
                        JButton btnDelete = new JButton("Delete");
                        btnDelete.setMaximumSize(new Dimension(btnDelete.getPreferredSize().width, btnDelete.getPreferredSize().height));
                        final JPanel jpanButton = new JPanel();
                        jpanButton.setMaximumSize(new Dimension(jpanButton.getMaximumSize().width, btnDelete.getPreferredSize().height));
                        jpanButton.setLayout(new BoxLayout(jpanButton, BoxLayout.X_AXIS));
                        jpanButton.add(Box.createHorizontalGlue());
                        jpanButton.add(btnDelete);
                        btnDelete.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                Container parent = temp.getParent();
                                temp.remove(jpanButton);
                                parent.remove(temp);
                                if(parent.getComponentCount() == 0) {
                                    btnAlignSpectra.setEnabled(false);
                                    btnRemoveSpectra.setEnabled(false);
                                    chkRescaleAll.setEnabled(false);
                                }
                                parent.validate();
                                parent.repaint();
                            }
                        });
                        temp.add(Box.createVerticalGlue());
                        temp.add(jpanButton);
                        jpanSpectra.add(temp);
                        if(jpanSpectra.getComponentCount() > 0) {
                            btnAlignSpectra.setEnabled(true);
                            btnRemoveSpectra.setEnabled(true);
                            chkRescaleAll.setEnabled(true);
                        }
                        jpanSpectra.validate();
                        jpanSpectra.repaint();
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else if(nodes.length > 10) {
            JOptionPane.showMessageDialog(jpanSpectra, new String[] {"A maximum of 10 selections applies to visualising spectra!", "You selected " + nodes.length + "."}, "Too many spectra selected!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * This method prints the usage information for this class to the standard
     * error stream and exits with the error flag raised to '1'.
     */
    private static void printUsage() {
        printError("Usage:\n\n\tClusterTreeGUI <cluster_file>");
    }

    /**
     * This method prints two blank lines followed by the the specified error message and another two empty lines
     * to the standard error stream and exits with the error flag raised to '1'.
     *
     * @param aMsg String with the message to print.
     */
    private static void printError(String aMsg) {
        System.err.println("\n\n" + aMsg + "\n\n");
        System.exit(1);
    }

    /**
     * This method is called when the application is closing.
     */
    private void close() {
        if(iConn != null) {
            try {
                iConn.close();
                System.out.println("Database connection closed.");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        this.setVisible(false);
        this.dispose();
        System.exit(0);
    }


    public boolean isStandAlone() {
        return true;
    }
}