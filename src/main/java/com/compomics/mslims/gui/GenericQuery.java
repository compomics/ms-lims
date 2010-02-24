/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 17-dec-02
 * Time: 15:56:21
 */
package com.compomics.mslims.gui;

import com.compomics.mslims.db.accessors.Fragmention;
import com.compomics.mslims.db.accessors.Spectrumfile;
import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.dialogs.ExportDialog;
import com.compomics.mslims.gui.dialogs.QueryCacheDialog;
import com.compomics.mslims.gui.interfaces.Informable;
import com.compomics.mslims.util.fileio.MascotGenericFile;
import com.compomics.mascotdatfile.util.gui.SequenceFragmentationPanel;
import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.peptizer.gui.PeptizerGUI;
import com.compomics.peptizer.gui.dialog.CreateTaskDialog;
import com.compomics.peptizer.util.fileio.ConnectionManager;
import com.compomics.util.db.DBResultSet;
import com.compomics.util.gui.JTableForDB;
import com.compomics.util.gui.renderers.ByteArrayRenderer;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.io.PropertiesManager;
import com.compomics.util.io.StartBrowser;
import com.compomics.util.sun.SwingWorker;
import com.compomics.util.sun.TableSorter;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
 * CVS information:
 *
 * $Revision: 1.28 $
 * $Date: 2009/07/28 14:48:33 $
 */

/**
 * This class allows the user to perform a generic SQL query, visualize the results and optionally export them to a CSV
 * file.
 *
 * @author Lennart Martens
 */
public class GenericQuery extends JFrame implements Connectable, Informable {

    private static final String QUERY_SEPARATOR = "--*-- QUERY SEPARATOR --*--";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

    /**
     * boolean that indicates whether this application is launched as a stand-alone client or as a module form another
     * application.
     */
    private static boolean iStandAlone = true;

    private JTableForDB tblResult = null;
    private JButton btnSubmit = null;
    private JButton btnShowCache = null;
    private JButton btnRemoveQueryFromCache = null;
    private JButton btnClearQueryCache = null;
    private JButton btnExport = null;
    private JButton btnPeptizer = null;
    private JButton btnCopy = null;
    private JTextArea txtQuery = null;
    private JProgressBar progress = null;
    private JLabel lblStatus = null;
    private JCheckBox chkTableScrolls = null;
    private JCheckBox chkSelectionMode = null;

    /**
     * The database connection.
     */
    private Connection iConn = null;

    /**
     * The title for the Dialog.
     */
    private String iTitle = null;

    /**
     * This Vector holds the last queries executed.
     */
    private Vector iQueryCache = null;

    /**
     * This variable holds the query cache max size.
     */
    private int iMaxCacheSize = 40;

    /**
     * This int indicates the position we're at in the cache.
     */
    private int iCurrentLocationInCache = 0;

    /**
     * This constructor takes as a single argument the title for the frame. It also constructs and lays out the
     * components.
     *
     * @param aTitle String with the frame title.
     */
    public GenericQuery(String aTitle) {
        this(aTitle, null, null);
    }

    /**
     * This constructor takes as arguments the title for the frame, the DB connection to read from and a name for this
     * connection. It also constructs and lays out the components.
     *
     * @param aTitle  String with the frame title.
     * @param aConn   Connection with the database connection to use. 'null' means no connection specified so create
     *                your own (pops up ConnectionDialog).
     * @param aDBName String with the name for the database connection. Only read if aConn != null.
     */
    public GenericQuery(String aTitle, Connection aConn, String aDBName) {
        super(aTitle);
        this.iTitle = aTitle;
        iQueryCache = new Vector(iMaxCacheSize, 5);
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        loadQueries();
        this.constructScreen();
        if (aConn == null) {
            this.getConnection();
        } else {
            this.passConnection(aConn, aDBName);
        }
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(50, 50, (int) (d.getWidth() / 2), (int) (d.getHeight() / 1.3));
        txtQuery.requestFocus();
    }

    /**
     * This method accepts an incoming connection to perform all database queries on.
     *
     * @param aConn Connection on which to perform the queries.
     * @param aDB   String with the name of the DB (for display purposes).
     */
    public void passConnection(Connection aConn, String aDB) {
        if (aConn == null) {
            this.close();
        }
        this.iConn = aConn;
        this.lblStatus.setText("DB connection established to '" + aDB + "'!");
        this.setTitle(iTitle + " (connected to '" + aDB + "')");
        this.txtQuery.requestFocus();
    }

    /**
     * Main method to launch the tool. Start-up args are not used.
     *
     * @param args String[] with the start-up args (not used).
     */
    public static void main(String[] args) {
        GenericQuery gq = new GenericQuery("Generic query application");
        gq.setVisible(true);
    }

    /**
     * This method constructs the GUI.
     */
    private void constructScreen() {
        // Query panel.
        txtQuery = new JTextArea(5, 15);
        txtQuery.setMinimumSize(txtQuery.getPreferredSize());
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!txtQuery.getText().trim().equals("")) {
                    querySubmitted();
                }
            }
        };
        txtQuery.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
                    txtQuery.setText("");
                } else {
                    super.keyTyped(e);
                }
            }

            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if ((e.getModifiers() == KeyEvent.CTRL_MASK) && (code == KeyEvent.VK_UP || code == KeyEvent.VK_KP_UP)) {
                    cacheBrowser(true);
                } else if ((e.getModifiers() == KeyEvent.CTRL_MASK) && (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_KP_DOWN)) {
                    cacheBrowser(false);
                } else if ((e.getModifiers() == KeyEvent.CTRL_MASK) && (e.getKeyChar() == KeyEvent.VK_ENTER)) {
                    if (!txtQuery.getText().trim().equals("")) {
                        querySubmitted();
                    }
                } else {
                    super.keyPressed(e);
                }
            }
        });
        // Submit button.
        btnSubmit = new JButton("Submit query");
        btnSubmit.setMnemonic(KeyEvent.VK_Q);
        btnSubmit.addActionListener(al);
        btnSubmit.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (!txtQuery.getText().trim().equals("")) {
                        querySubmitted();
                    }
                }
            }
        });

        // Show query cache button.
        btnShowCache = new JButton("Show query cache");
        btnShowCache.setMnemonic(KeyEvent.VK_S);
        btnShowCache.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCache();
            }
        });
        btnShowCache.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    showCache();
                }
            }
        });

        // Clear current query from cache button.
        btnRemoveQueryFromCache = new JButton("Remove query from cache");
        btnRemoveQueryFromCache.setMnemonic(KeyEvent.VK_R);
        btnRemoveQueryFromCache.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!txtQuery.getText().trim().equals("")) {
                    removeQueryFromCache(txtQuery.getText().trim());
                }
            }
        });
        btnRemoveQueryFromCache.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (!txtQuery.getText().trim().equals("")) {
                        removeQueryFromCache(txtQuery.getText().trim());
                    }
                }
            }
        });

        // Clear full query cache button.
        btnClearQueryCache = new JButton("Clear cache");
        btnClearQueryCache.setMnemonic(KeyEvent.VK_C);
        btnClearQueryCache.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearQueryCache();
            }
        });
        btnClearQueryCache.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    clearQueryCache();
                }
            }
        });

        JPanel jpanSubmit = new JPanel();
        jpanSubmit.setLayout(new BoxLayout(jpanSubmit, BoxLayout.X_AXIS));
        jpanSubmit.add(btnSubmit);
        jpanSubmit.add(Box.createHorizontalGlue());
        jpanSubmit.add(btnRemoveQueryFromCache);
        jpanSubmit.add(Box.createHorizontalStrut(5));
        jpanSubmit.add(btnClearQueryCache);
        jpanSubmit.add(Box.createHorizontalStrut(5));
        jpanSubmit.add(btnShowCache);

        jpanSubmit.setMaximumSize(new Dimension(jpanSubmit.getMaximumSize().width, btnSubmit.getPreferredSize().height));

        progress = new JProgressBar(JProgressBar.HORIZONTAL);
        progress.setStringPainted(true);
        progress.setString("");
        progress.setMaximumSize(new Dimension(progress.getMaximumSize().width, progress.getPreferredSize().height));
        JPanel jpanProgress = new JPanel();
        jpanProgress.setLayout(new BoxLayout(jpanProgress, BoxLayout.X_AXIS));
        jpanProgress.setBorder(BorderFactory.createTitledBorder("Progress bar"));
        jpanProgress.add(Box.createHorizontalGlue());
        jpanProgress.add(progress);
        jpanProgress.add(Box.createHorizontalGlue());
        jpanProgress.setMaximumSize(new Dimension(jpanProgress.getMaximumSize().width, progress.getPreferredSize().height));

        JPanel jpanQuery = new JPanel();
        jpanQuery.setLayout(new BoxLayout(jpanQuery, BoxLayout.Y_AXIS));
        jpanQuery.setBorder(BorderFactory.createTitledBorder("Query"));
        JScrollPane jsp = new JScrollPane(txtQuery);
        jsp.setMinimumSize(txtQuery.getPreferredSize());
        jpanQuery.add(jsp);
        jpanQuery.add(Box.createRigidArea(new Dimension(btnSubmit.getWidth(), 5)));
        jpanQuery.add(jpanSubmit);
        jpanQuery.add(Box.createRigidArea(new Dimension(btnSubmit.getWidth(), 5)));
        jpanQuery.add(jpanProgress);

        // Table panel.
        tblResult = new JTableForDB();
        tblResult.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            public void mouseClicked(MouseEvent e) {
                // Transform clickpoint to row and column indices +
                // retrieve the renderer at that location.
                Point compLoc = e.getPoint();
                int col = tblResult.columnAtPoint(compLoc);
                int row = tblResult.rowAtPoint(compLoc);
                TableCellRenderer comp = tblResult.getCellRenderer(row, col);

                if ((e.getModifiers() == MouseEvent.BUTTON3_MASK || e.getModifiers() == MouseEvent.BUTTON2_MASK) && (tblResult.getColumnName(col) != null) && tblResult.getColumnName(col).trim().equalsIgnoreCase("l_datfileid")) {
                    // Get the data from the 'datfile' table.
                    try {
                        Statement stat = iConn.createStatement();
                        ResultSet rs = stat.executeQuery("select server, folder, filename from datfile where datfileid=" + tblResult.getValueAt(row, col));
                        // Only one row expected.
                        rs.next();
                        String server = rs.getString(1);
                        String folder = rs.getString(2);
                        String filename = rs.getString(3);
                        rs.close();
                        stat.close();
                        // The URL will be stored here.
                        String url = server + "/cgi/master_results.pl?file=" + folder + filename;
                        // The process.
                        StartBrowser.start(url);


                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        JOptionPane.showMessageDialog((Component) comp, "Unable to load data for selected datfile (ID=" + tblResult.getValueAt(row, col) + "): " + sqle.getMessage() + ".", "Unable to load datfile data!", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        JOptionPane.showMessageDialog((Component) comp, "Unable to open internet view of selected entry: " + exc.getMessage() + ".", "Unable to open browser window", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ((e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON2) && (comp instanceof ByteArrayRenderer || tblResult.getColumnName(col).trim().equalsIgnoreCase("l_spectrumfileid"))) {
                    byte[] result = null;
                    String filename = "Spectrum";
                    try {
                        if (tblResult.getColumnName(col).trim().equalsIgnoreCase("l_spectrumfileid")) {
                            try {
                                Spectrumfile specFile = Spectrumfile.findFromID(((Number) tblResult.getValueAt(row, col)).longValue(), iConn);
                                result = specFile.getUnzippedFile();
                                filename = specFile.getFilename();
                            } catch (SQLException sqle) {
                                sqle.printStackTrace();
                                JOptionPane.showMessageDialog((Component) comp, "Unable to load data for selected spectrumfile (ID=" + tblResult.getValueAt(row, col) + "): " + sqle.getMessage() + ".", "Unable to load spectrumfile data!", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } else {
                            // Creating the frame with the data from the model.
                            int modelCol = tblResult.convertColumnIndexToModel(col);
                            byte[] spectrumZipped = (byte[]) tblResult.getModel().getValueAt(row, modelCol);
                            result = Spectrumfile.getUnzippedFile(spectrumZipped);
                        }
                        MascotGenericFile mgf = new MascotGenericFile(filename, new String(result));
                        if (mgf.getPeaks() == null || mgf.getPeaks().size() == 0) {
                            JOptionPane.showMessageDialog(GenericQuery.this, "This spectrum contains no peaks and can not be visualized!", "No peaks found in spectrum!", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        // See if we have a sensible filename, else try the title.
                        if (mgf.getFilename() == null || mgf.getFilename().indexOf(".") < 0) {
                            String title = mgf.getTitle();
                            if (title != null && title.indexOf(".") > 0) {
                                mgf.setFilename(title);
                            }
                        }
                        // Get all the fragment ions for this identification.
                        long idid = -1;
                        Vector fragments = new Vector();
                        try {
                            int idColumn = -1;
                            for (int i = 0; i < tblResult.getModel().getColumnCount(); i++) {
                                if (tblResult.getModel().getColumnName(i).trim().toLowerCase().equals("identificationid")) {
                                    idColumn = i;
                                }
                            }
                            if (idColumn > -1) {
                                idid = ((Number) tblResult.getModel().getValueAt(row, idColumn)).longValue();

                                Vector temp = Fragmention.getAllMascotDatfileFragmentIonImpl(iConn, idid);
                                if (temp.size() == 0) {
                                    JOptionPane.showMessageDialog((Component) comp, "No fragment ions were stored for the selected identification (ID=" + idid + ").", "No fragment ions found!", JOptionPane.WARNING_MESSAGE);
                                }
                                for (Iterator lIterator = temp.iterator(); lIterator.hasNext();) {
                                    FragmentIon lIon = (FragmentIon) lIterator.next();
                                    if (lIon.getID() == FragmentIon.Y_ION || lIon.getID() == FragmentIon.B_ION ||
                                            lIon.getID() == FragmentIon.PRECURSOR ||
                                            lIon.getID() == FragmentIon.IMMONIUM) {
                                        fragments.add(lIon);
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(GenericQuery.this, new String[]{"Unable to locate identification id in the current result set.", "Could not locate fragment ions."}, "Identification id not found!", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (SQLException sqle) {
                            sqle.printStackTrace();
                            JOptionPane.showMessageDialog((Component) comp, "Unable to load fragment ions for selected identification (ID=" + idid + "): " + sqle.getMessage() + ".", "Unable to load fragment ions!", JOptionPane.ERROR_MESSAGE);
                        }

                        SpectrumPanel specPanel = new SpectrumPanel(mgf);
                        specPanel.setAnnotations(fragments);
                        JFrame frame = new JFrame("Spectrum for " + mgf.getTitle());
                        frame.getContentPane().add(specPanel);
                        frame.addWindowListener(new WindowAdapter() {
                            /**
                             * Invoked when a window is in the process of being closed.
                             * The close operation can be overridden at this point.
                             */
                            public void windowClosing(WindowEvent e) {
                                e.getWindow().dispose();
                            }
                        });
                        frame.setBounds(100, 100, 450, 300);
                        frame.setVisible(true);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                } else if (e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK && comp instanceof ByteArrayRenderer) {
                    // Creating the frame with the data from the model.
                    int modelCol = tblResult.convertColumnIndexToModel(col);
                    byte[] data = (byte[]) tblResult.getModel().getValueAt(row, modelCol);
                    // Get the output location.
                    try {
                        FileDialog fd = new FileDialog(GenericQuery.this, "Save byte[] to disk...", FileDialog.SAVE);
                        fd.setVisible(true);
                        String select = fd.getFile();
                        if (select == null) {
                            return;
                        } else {
                            select = fd.getDirectory() + select;
                            File output = new File(select);
                            if (!output.exists()) {
                                output.createNewFile();
                            }
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output));
                            bos.write(data);
                            bos.flush();
                            bos.close();
                            JOptionPane.showMessageDialog(GenericQuery.this, "Output written to " + select + ".", "Output written!", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(GenericQuery.this, "Unable to save data to file: " + ioe.getMessage(), "Unable to write data to file!", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (e.getClickCount() >= 2 && (tblResult.getColumnName(col) != null) && tblResult.getColumnName(col).trim().equalsIgnoreCase("l_datfileid")) {
                    // Get the data from the 'datfile' table.
                    try {
                        Statement stat = iConn.createStatement();
                        ResultSet rs = stat.executeQuery("select server, folder, filename from datfile where datfileid=" + tblResult.getValueAt(row, col));
                        // Only one row expected.
                        rs.next();
                        String server = rs.getString(1);
                        String folder = rs.getString(2);
                        String filename = rs.getString(3);
                        rs.close();
                        stat.close();
                        // The URL will be stored here.
                        String url = server + "/x-cgi/ms-showtext.exe?" + folder + filename;
                        // The process.
                        StartBrowser.start(url);

                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        JOptionPane.showMessageDialog((Component) comp, "Unable to load data for selected datfile (ID=" + tblResult.getValueAt(row, col) + "): " + sqle.getMessage() + ".", "Unable to load datfile data!", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        JOptionPane.showMessageDialog((Component) comp, "Unable to open internet view of selected entry: " + exc.getMessage() + ".", "Unable to open browser window", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (e.getClickCount() >= 2 && (tblResult.getColumnName(col) != null) && tblResult.getColumnName(col).trim().equalsIgnoreCase("ion_coverage")) {
                    // Get all the fragment ions for this identification.
                    long idid = -1;
                    String modSeq = null;
                    Vector fragments = new Vector();
                    try {
                        int idColumn = -1;
                        for (int i = 0; i < tblResult.getModel().getColumnCount(); i++) {
                            String colHeader = tblResult.getModel().getColumnName(i).trim().toLowerCase();
                            if (colHeader.equals("identificationid")) {
                                idColumn = i;
                            } else if (colHeader.equals("modified_sequence")) {
                                modSeq = tblResult.getModel().getValueAt(row, i).toString();
                            }
                        }
                        if (idColumn > -1 && modSeq != null) {
                            idid = ((Number) tblResult.getModel().getValueAt(row, idColumn)).longValue();

                            Vector temp = Fragmention.getAllMascotDatfileFragmentIonImpl(iConn, idid);
                            if (temp.size() == 0) {
                                JOptionPane.showMessageDialog((Component) comp, "No fragment ions were stored for the selected identification (ID=" + idid + ").", "No fragment ions found!", JOptionPane.WARNING_MESSAGE);
                            }
                            for (Iterator lIterator = temp.iterator(); lIterator.hasNext();) {
                                FragmentIon lIon = (FragmentIon) lIterator.next();
                                if (lIon.getID() == FragmentIon.Y_ION || lIon.getID() == FragmentIon.B_ION ||
                                        lIon.getID() == FragmentIon.PRECURSOR ||
                                        lIon.getID() == FragmentIon.IMMONIUM) {
                                    fragments.add(lIon);
                                }
                            }
                            SequenceFragmentationPanel sfp = new SequenceFragmentationPanel(modSeq, fragments);
                            JDialog dialog = new JDialog(GenericQuery.this, "Fragment peak annotation", false);
                            dialog.addWindowListener(new WindowAdapter() {
                                /**
                                 * Invoked when a window is in the process of being closed.
                                 * The close operation can be overridden at this point.
                                 */
                                public void windowClosing(WindowEvent e) {
                                    e.getWindow().setVisible(false);
                                    e.getWindow().dispose();
                                }
                            });
                            dialog.getContentPane().add(sfp, BorderLayout.CENTER);
                            dialog.setLocation(100, 100);
                            dialog.pack();
                            dialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(GenericQuery.this, new String[]{"Unable to locate identification id or modified sequence in the current result set.", "Could not locate fragment ions or modified sequence."}, "Identification id or modified sequence not found!", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (SQLException sqle) {
                        sqle.printStackTrace();
                        JOptionPane.showMessageDialog((Component) comp, "Unable to load fragment ions for selected identification (ID=" + idid + "): " + sqle.getMessage() + ".", "Unable to load fragment ions!", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        tblResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane pane = new JScrollPane(tblResult);
        chkTableScrolls = new JCheckBox("Table can be larger than viewport", true);
        chkTableScrolls.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (chkTableScrolls.isSelected()) {
                    tblResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                } else {
                    tblResult.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                }
            }
        });
        chkSelectionMode = new JCheckBox("Column selection mode", false);
        chkSelectionMode.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (chkSelectionMode.isSelected()) {
                    tblResult.setColumnSelectionAllowed(true);
                    tblResult.setRowSelectionAllowed(false);
                } else {
                    tblResult.setColumnSelectionAllowed(false);
                    tblResult.setRowSelectionAllowed(true);
                }
            }
        });
        JPanel jpanCheckboxes = new JPanel();
        jpanCheckboxes.setLayout(new BoxLayout(jpanCheckboxes, BoxLayout.X_AXIS));
        jpanCheckboxes.add(chkTableScrolls);
        jpanCheckboxes.add(chkSelectionMode);
        jpanCheckboxes.add(Box.createHorizontalGlue());

        JPanel jpanTable = new JPanel();
        jpanTable.setBorder(BorderFactory.createTitledBorder("Results"));
        jpanTable.setLayout(new BorderLayout());
        jpanTable.add(pane, BorderLayout.CENTER);
        jpanTable.add(jpanCheckboxes, BorderLayout.SOUTH);

        // Status & output panel.
        lblStatus = new JLabel();
        btnCopy = new JButton("Copy selection");
        btnCopy.setMnemonic(KeyEvent.VK_C);
        btnCopy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyTriggered();
            }
        });

        btnExport = new JButton("Export data...");
        btnExport.setEnabled(false);
        btnExport.setMnemonic(KeyEvent.VK_E);
        btnExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportTriggered();
            }
        });


        btnPeptizer = new JButton("Peptizer");
        btnPeptizer.setEnabled(false);
        btnPeptizer.setMnemonic(KeyEvent.VK_E);
        btnPeptizer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                peptizerTriggered();
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnPeptizer);
        jpanButtons.add(Box.createRigidArea(new Dimension(5, btnCopy.getHeight())));
        jpanButtons.add(btnCopy);
        jpanButtons.add(Box.createRigidArea(new Dimension(5, btnCopy.getHeight())));
        jpanButtons.add(btnExport);


        JPanel jpanStatus = new JPanel();
        jpanStatus.setBorder(BorderFactory.createTitledBorder("Status"));
        jpanStatus.setLayout(new BorderLayout());
        jpanStatus.add(jpanButtons, BorderLayout.EAST);
        jpanStatus.add(lblStatus, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jpanQuery, jpanTable);
        split.setOneTouchExpandable(true);

        this.getContentPane().add(split, BorderLayout.CENTER);
        this.getContentPane().add(jpanStatus, BorderLayout.SOUTH);
    }

    private void peptizerTriggered() {
        DBResultSet rs = (DBResultSet) ((TableSorter) tblResult.getModel()).getModel();
        int lColumnIndex = rs.findColumn("identificationid");
        int lRowCount = rs.getRowCount();
        ArrayList<Long> list = new ArrayList<Long>();

        for (int i = 0; i < lRowCount; i++) {
            Long identificationid = (Long) rs.getValueAt(i, lColumnIndex);
            list.add(identificationid);
        }

        ConnectionManager.getInstance().setConnection(iConn);
        PeptizerGUI peptizer = new PeptizerGUI();
        peptizer.setEnclosedByLims(true);
        CreateTaskDialog dialog = new CreateTaskDialog(peptizer);
        dialog.setMs_lims_identification_id_selected(list);

    }

    /**
     * Shows the query cache dialog.
     */
    private void showCache() {
        QueryCacheDialog qcd = new QueryCacheDialog(this, "Query cache", iQueryCache);
        qcd.setVisible(true);
        txtQuery.requestFocus();
    }

    /**
     * This method can be called by a child component (typically a dialog) that wants to inform the parent class of a
     * certain event.
     *
     * @param o Object with the information to transfer.
     */
    public void inform(Object o) {
        // If we receive an Integer, it means this query from the cache should be
        // selected.
        if (o instanceof Integer) {
            this.cacheSelector(((Integer) o).intValue());
        }
    }

    /**
     * This method is called whenever a query was submitted.
     */
    private void querySubmitted() {
        final String query = txtQuery.getText().trim();
        if (!query.equals("") && iConn != null) {
            // Cache!
            this.checkQueryCache(query);
            // Okay, we need to go ahead with this.

            // NEW STUFF
            tblResult.setModel(new DBResultSet());
            btnSubmit.setEnabled(false);
            progress.setIndeterminate(true);
            final long startMillis = System.currentTimeMillis();

            progress.setString("Executing query (started at " + SDF.format(new Date(startMillis)) + ")...");

            final SwingWorker queryWorker = new SwingWorker() {
                private ResultSet rs = null;
                private Statement s = null;

                public Object construct() {
                    Object result = null;
                    try {
                        // Create the statement.
                        s = iConn.createStatement();
                        // Execute the query and see if there is a resultset.
                        boolean resultset = s.execute(query);
                        if (resultset) {
                            // We've got a resultset, implying a select query.
                            rs = s.getResultSet();
                            result = rs;
                        } else {
                            // We got an update count.
                            result = new Integer(s.getUpdateCount());
                        }
                    } catch (SQLException e) {
                        result = e;
                    }
                    return result;
                }

                public void finished() {
                    queryCompleted(this, query, startMillis, s, rs);
                }

            };
            queryWorker.start();
            // END NEW STUFF
            return;
        }
    }

    /**
     * This method will be called by the SwingWorker whenever the query completes.
     *
     * @param aQuery       SwingWorker that's handled the query. Since it is calling us, the query is now complete.
     * @param aSQL         String with the actual SQL executed.
     * @param aStartMillis long with the original starting time of the query in milliseconds.
     * @param aStatement   Statement that needs to be closed at the end.
     * @param aResultSet   ResultSet that needs to be closed. Can be 'null' for update-like queries.
     */
    private void queryCompleted(SwingWorker aQuery, String aSQL, long aStartMillis, Statement aStatement, ResultSet aResultSet) {
        progress.setIndeterminate(false);
        progress.setValue(progress.getMinimum());
        btnSubmit.setEnabled(true);
        try {
            Object temp = aQuery.get();
            long endMillis = System.currentTimeMillis();
            double totalTime = 0.0;
            boolean inSeconds = false;
            totalTime = endMillis - aStartMillis;
            if (totalTime > 1000) {
                totalTime /= 1000.0;
                inSeconds = true;
            }
            String duration = new BigDecimal(totalTime).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + (inSeconds ? " seconds" : " milliseconds");
            if (temp instanceof ResultSet) {
                ResultSet result = (ResultSet) temp;
                DBResultSet dbr = new DBResultSet(result);
                tblResult.setModel(dbr);
                if (chkSelectionMode.isSelected()) {
                    tblResult.setColumnSelectionAllowed(true);
                    tblResult.setRowSelectionAllowed(false);
                } else {
                    tblResult.setColumnSelectionAllowed(false);
                    tblResult.setRowSelectionAllowed(true);
                }
                result.close();
                lblStatus.setForeground(this.getForeground());
                lblStatus.setText("Query returned " + dbr.getRowCount() + " rows (query took " + duration + ").");
                progress.setString("Query complete (" + duration + ")!");
                btnExport.setEnabled(true);
                int identificationid = dbr.findColumn("identificationid");
                if (identificationid != -1) {
                    btnPeptizer.setEnabled(true);
                } else {
                    btnPeptizer.setEnabled(false);
                }
            } else if (temp instanceof Integer) {
                int update = ((Integer) temp).intValue();
                if (update < 0) {
                    lblStatus.setForeground(Color.red);
                    lblStatus.setText("No reply from database!");
                    btnExport.setEnabled(false);
                    btnPeptizer.setEnabled(false);
                } else {
                    lblStatus.setForeground(this.getForeground());
                    lblStatus.setText("Statement completed. " + update + " row" + ((update == 1) ? "" : "s") + " affected.");
                    tblResult.setModel(new DBResultSet(), true);
                    progress.setString("Statement complete (" + duration + ")!");
                    btnExport.setEnabled(false);
                    btnPeptizer.setEnabled(false);
                }
            } else if (temp instanceof SQLException) {
                throw (SQLException) temp;
            }
            // If the statement was a 'use' statement, change the title.
            if (aSQL.toUpperCase().startsWith("USE")) {
                String tempTitle = this.getTitle();
                String tempDB = aSQL.substring(4).trim();
                if (tempDB.endsWith(";")) {
                    tempDB = tempDB.substring(0, tempDB.length() - 1);
                }
                this.setTitle(tempTitle.substring(0, tempTitle.lastIndexOf('/') + 1) + tempDB + tempTitle.substring(tempTitle.lastIndexOf('\'')));
                progress.setString("Changed database to '" + tempDB + "'");
                lblStatus.setForeground(this.getForeground());
                lblStatus.setText("Changed database to '" + tempDB + "'");
                btnExport.setEnabled(false);
            }
        } catch (SQLException sqle) {
            JOptionPane.showMessageDialog(this, new String[]{"Unfortunately, your query failed, (see below for query): " + sqle.getMessage(), aSQL}, "Query failed!", JOptionPane.ERROR_MESSAGE);
            lblStatus.setForeground(Color.red);
            lblStatus.setText("Query failed: " + sqle.getMessage());
            progress.setString("Query failed!");
            btnExport.setEnabled(false);
            txtQuery.requestFocus();
        } finally {
            // Try to close the result set (if any).
            if (aResultSet != null) {
                try {
                    aResultSet.close();
                } catch (SQLException sqle) {
                    System.err.println("Failed to close resultset!");
                }
            }
            // Try to close the statement (if any).
            if (aStatement != null) {
                try {
                    aStatement.close();
                } catch (SQLException sqle) {
                    System.err.println("Failed to close statement!");
                }
            }
        }
    }


    /**
     * This method is called whenever the user clicked the button to export data.
     */
    private void exportTriggered() {
        ExportDialog ed = new ExportDialog(this, "Export data to file", (DBResultSet) ((TableSorter) tblResult.getModel()).getModel());
        ed.setVisible(true);
    }

    /**
     * This method is called when the user clicks the 'copy selection' button.
     */
    private void copyTriggered() {
        int[] cols = this.tblResult.getSelectedColumns();
        int[] rows = this.tblResult.getSelectedRows();

        int nbrCols = this.tblResult.getColumnCount();
        int nbrRows = this.tblResult.getRowCount();

        String data = null;

        if (tblResult.getRowSelectionAllowed() && rows != null && rows.length > 0) {
            StringBuffer allRows = new StringBuffer();
            for (int i = 0; i < rows.length; i++) {
                for (int j = 0; j < nbrCols; j++) {
                    Object tempValue = this.tblResult.getValueAt(rows[i], j);
                    String tempData = null;
                    if (tempValue != null) {
                        tempData = tempValue.toString();
                    } else {
                        tempData = "";
                    }
                    // Remove possible HTML tags.
                    if (tempData.indexOf("<html>") >= 0 && tempData.indexOf("</html>") > 0) {
                        // Remove 'html' tags.
                        int start = -1;
                        while ((start = tempData.indexOf("<html>")) >= 0) {
                            tempData = tempData.substring(0, start) + tempData.substring(start + 6);
                        }
                        while ((start = tempData.indexOf("</html>")) >= 0) {
                            tempData = tempData.substring(0, start) + tempData.substring(start + 7);
                        }
                    }
                    allRows.append(tempData + "\t");
                }
                allRows.append("\n");
            }
            data = allRows.toString();
        } else if (tblResult.getColumnSelectionAllowed() && cols != null && cols.length > 0) {
            StringBuffer allCols = new StringBuffer();
            for (int i = 0; i < nbrRows; i++) {
                for (int j = 0; j < cols.length; j++) {
                    Object tempValue = this.tblResult.getValueAt(i, cols[j]);
                    String tempData = null;
                    if (tempValue != null) {
                        tempData = tempValue.toString();
                    } else {
                        tempData = "";
                    }
                    // Remove possible HTML tags.
                    if (tempData.indexOf("<html>") >= 0 && tempData.indexOf("</html>") > 0) {
                        // Remove 'html' tags.
                        int start = -1;
                        while ((start = tempData.indexOf("<html>")) >= 0) {
                            tempData = tempData.substring(0, start) + tempData.substring(start + 6);
                        }
                        while ((start = tempData.indexOf("</html>")) >= 0) {
                            tempData = tempData.substring(0, start) + tempData.substring(start + 7);
                        }
                    }
                    allCols.append(tempData + "\t");
                }
                allCols.append("\n");
            }
            data = allCols.toString();
        } else {
            JOptionPane.showMessageDialog(this, "No rows or columns selected!", "No data selected!", JOptionPane.ERROR_MESSAGE);
        }

        if (data != null) {
            Object tempObject = new StringSelection(data);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents((Transferable) tempObject, (ClipboardOwner) tempObject);
        }
    }

    /**
     * This method creates a dialog which handles the DB connection.
     */
    private void getConnection() {
        Properties lConnectionProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "ms_lims.properties");
        ConnectionDialog cd = new ConnectionDialog(this, this, "Establish DB connection for GenericQuery application", lConnectionProperties);
        cd.setVisible(true);
    }

    /**
     * This method is called when the application needs to close.
     */
    private void close() {
        this.setVisible(false);
        if (iConn != null && iStandAlone) {
            try {
                iConn.close();
                System.out.println("DB Connection closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveQueries();
        this.dispose();
        if (iStandAlone) {
            System.exit(0);
        }
    }

    /**
     * This method checks the querycache for the presence of the query. If it is present already, it is moved to the
     * last position, else it is added. If the query size after addition grows over the maximum query cache size, the
     * first element is deleted.
     *
     * @param aQuery String with the executed query.
     */
    private void checkQueryCache(String aQuery) {
        // See if it is in there somewhere.
        if (iQueryCache.contains(aQuery)) {
            // Remove it from its current location.
            iQueryCache.remove(aQuery);
        }
        // Add it to the end.
        iQueryCache.add(aQuery);

        // See if we're not exceeding the maximum cache size.
        while (iQueryCache.size() > iMaxCacheSize) {
            iQueryCache.remove(0);
        }
        iCurrentLocationInCache = iQueryCache.size() - 1;
    }

    /**
     * This method clears the full query cache. It will pop-up a confirmation dialog.
     */
    private void clearQueryCache() {
        int userDecision = JOptionPane.showConfirmDialog(this, "Do you want to clear the entire query cache?", "Clear query cache", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (userDecision == JOptionPane.YES_OPTION) {
            iQueryCache = new Vector(iMaxCacheSize, 5);
            txtQuery.setText("");
            JOptionPane.showMessageDialog(this, "Query cache has been cleared.", "Query cache cleared", JOptionPane.INFORMATION_MESSAGE);
            txtQuery.requestFocus();
        }
    }

    /**
     * This method removes the specified query from the cache.
     *
     * @param aQuery String with the query to remove.
     */
    private void removeQueryFromCache(String aQuery) {
        // See if it is in there somewhere.
        if (iQueryCache.contains(aQuery)) {
            // Remove it from its current location.
            iQueryCache.remove(aQuery);
            txtQuery.setText("");
            txtQuery.requestFocus();
        }
    }

    /**
     * This method triggers a checking of the cache, scrolling in the direction indicated by the boolean parameter.
     *
     * @param up boolean to indicate upward ('true') or downward ('false') scrolling.
     */
    private void cacheBrowser(boolean up) {
        int liSize = iQueryCache.size();
        if (liSize == 0) {
            return;
        } else {
            // Downward
            int lCurrentLocationInCache = iCurrentLocationInCache;
            if (!up) {
                if (lCurrentLocationInCache < (liSize - 1)) {
                    lCurrentLocationInCache++;
                } else {
                    lCurrentLocationInCache = liSize - 1;
                }
                cacheSelector(lCurrentLocationInCache);
            } else {
                // Upward
                if (lCurrentLocationInCache > 0) {
                    lCurrentLocationInCache--;
                }
                cacheSelector(lCurrentLocationInCache);
            }
        }
    }

    /**
     * This method selects the query at the specified index in the query cache and writes it on the query text area.
     *
     * @param aSelectedIndex int with the selected index.
     */
    private void cacheSelector(int aSelectedIndex) {
        if (aSelectedIndex < 0 || aSelectedIndex > (iQueryCache.size() - 1)) {
            throw new IllegalArgumentException("The query index you specified (" + aSelectedIndex + ") is out of the query cache size range!");
        }
        iCurrentLocationInCache = aSelectedIndex;
        txtQuery.setText((String) iQueryCache.get(iCurrentLocationInCache));
        txtQuery.setCaretPosition(0);
        txtQuery.requestFocus();
    }

    /**
     * This method saves the queries in memory to the user home folder as 'queries.txt'.
     */
    private void saveQueries() {
        try {
            // Output file proper.
            File output = new File(PropertiesManager.getInstance().getApplicationFolder(CompomicsTools.MSLIMS), "queries.txt.gz");
            // Just start outputting to the 'queries.txt' file. Silent overwrite!
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(output))));
            for (Iterator lIterator = iQueryCache.iterator(); lIterator.hasNext();) {
                String query = (String) lIterator.next();
                bw.write(query + "\n");
                bw.write("\n" + QUERY_SEPARATOR + "\n\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, new String[]{"Unable to load queries:", ioe.getMessage()}, "Unable to load queries!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * This method loads the queries in memory from the 'queries.txt' file in the user home folder.
     */
    private void loadQueries() {
        try {
            // Input file proper.
            File lApplicationFolder = PropertiesManager.getInstance().getApplicationFolder(CompomicsTools.MSLIMS);
            File lQueriesFile = new File(lApplicationFolder, "queries.txt.gz");
            // Convert pre-existing 'queries.txt' into new 'queries.txt.gz'
            if (!lQueriesFile.exists()) {
                File lOldFile = new File(lApplicationFolder, "queries.txt");
                if (lOldFile.exists()) {
                    // Conversion needs to be done.
                    JOptionPane.showMessageDialog(this, new String[]{"Found an old version of the query file.", "This file will now be automatically converted."}, "Old query file found.", JOptionPane.INFORMATION_MESSAGE);
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(lOldFile));
                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(lQueriesFile))));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            bw.write(line + "\n");
                        }
                        br.close();
                        bw.flush();
                        bw.close();
                        boolean gone = false;
                        while (!gone) {
                            gone = lOldFile.delete();
                        }
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(this, new String[]{"Unable to convert existing query file. Cache will be empty", ioe.getMessage()}, "Unable to convert queries!", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(lQueriesFile))));
            parseIncomingQueries(br);
            br.close();
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this, new String[]{"Unable to load queries, query cache will be empty", ioe.getMessage()}, "Unable to load queries!", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * This method reads the number of cached queries from the specified BufferedReader. <br /> If there are more
     * queries read from the buffer than the cache currently supports, only the last read queries will be cached until
     * the cache is full.
     *
     * @param aQuerySource BufferedReader to read the queries from.
     * @throws IOException when the reading failed.
     */
    private void parseIncomingQueries(BufferedReader aQuerySource) throws IOException {
        String line = null;
        ArrayList queries = new ArrayList();
        StringBuffer query = new StringBuffer();
        while ((line = aQuerySource.readLine()) != null) {
            // Skip empty lines.
            if (line.trim().equals("")) {
                continue;
            }
            // See if we found a separator.
            if (line.trim().equals(QUERY_SEPARATOR)) {
                queries.add(query.toString().trim());
                query = new StringBuffer();
            } else {
                // Just add the current line to the buffer.
                query.append(line + "\n");
            }
        }
        // Now read the arraylist of queries, making sure we include the last element
        // yet do not overflow the max number of queries in the cache (eg., if we've
        // read 12 queries, but the cache only allows for 10, we'll read queries 3-12).
        int startPos = queries.size() - iMaxCacheSize;
        if (startPos < 0) {
            startPos = 0;
        }
        for (int i = startPos; i < queries.size(); i++) {
            iQueryCache.add(queries.get(i));
        }
        iCurrentLocationInCache = iQueryCache.size();
    }


    /**
     * Set the cursor image to a specified cursor.
     *
     * @param cursor One of the constants defined by the <code>Cursor</code> class. If this parameter is null then the
     *               cursor for this window will be set to the type Cursor.DEFAULT_CURSOR.
     * @see java.awt.Component#getCursor
     * @see java.awt.Cursor
     * @since JDK1.1
     */
    public void setCursor(Cursor cursor) {
        int count = this.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component c = this.getComponent(i);
            c.setCursor(cursor);
        }
        super.setCursor(cursor);
    }

    /**
     * This method needs to be called if this class is not running in standalone mode.
     */
    public static void setNotStandAlone() {
        iStandAlone = false;
    }
}
