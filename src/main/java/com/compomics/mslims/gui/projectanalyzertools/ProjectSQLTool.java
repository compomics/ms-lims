/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 7-mrt-2005
 * Time: 7:47:11
 */
package com.compomics.mslims.gui.projectanalyzertools;

import com.compomics.mascotdatfile.util.gui.SequenceFragmentationPanel;
import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.mslims.db.accessors.Instrument;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.mslims.db.accessors.Spectrum;
import com.compomics.mslims.db.accessors.Spectrum_file;
import com.compomics.mslims.gui.ProjectAnalyzer;
import com.compomics.mslims.gui.interfaces.ProjectAnalyzerTool;
import com.compomics.mslims.util.fileio.MascotGenericFile;
import org.apache.log4j.Logger;

import com.compomics.mslimscore.gui.dialogs.ExportDialog;
import com.compomics.util.db.DBResultSet;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.gui.JTableForDB;
import com.compomics.util.gui.renderers.ByteArrayRenderer;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.compomics.util.io.StartBrowser;
import com.compomics.util.sun.SwingWorker;
import com.compomics.util.sun.TableSorter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.28 $
 * $Date: 2009/05/18 08:01:10 $
 */

/**
 * This class provides a tool that allows the user to run a set of SQL statements against the database.
 *
 * @author Lennart Martens
 * @version $Id: ProjectSQLTool.java,v 1.28 2009/05/18 08:01:10 niklaas Exp $
 */
public class ProjectSQLTool extends FlamableJFrame implements ProjectAnalyzerTool {
    // Class specific log4j logger for ProjectSQLTool instances.
    private static Logger logger = Logger.getLogger(ProjectSQLTool.class);

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
     * The lazily cached statement.
     */
    private Statement iStatement = null;

    /**
     * The name for the DB connection.
     */
    private String iDBName = null;

    /**
     * The project we should be analysing.
     */
    private Project iProject = null;

    /**
     * The array of instruments that were retrieved from the DB.
     */
    private Object[] iInstruments = null;

    /**
     * This String holds the name for the tool.
     */
    private String iToolName = null;

    /**
     * Formats a date/time to HH:mm:ss (eg. "14:57:23")
     */
    private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

    private JRadioButton rbtAllPeptides = null;
    private JRadioButton rbtUniquePeptides = null;
    private JCheckBox chkHighestScorePeptide = null;
    private JRadioButton rbtSeq = null;
    private JTextField txtSeq = null;
    private JRadioButton rbtModSeq = null;
    private JTextField txtModSeq = null;
    private JRadioButton rbtTitle = null;
    private JTextField txtTitle = null;
    private JRadioButton rbtUniqueProteins = null;
    private JCheckBox chkOmitIPIXRefs = null;
    private JRadioButton rbtSingles = null;
    private JRadioButton rbtSingleLight = null;
    private JRadioButton rbtSingleHeavy = null;
    private JRadioButton rbtSingleBoth = null;
    private JCheckBox chkIncludeFile = null;

    private JComboBox cmbInstrument = null;
    private JProgressBar progress = null;

    private JTableForDB tblResults = null;
    private JLabel lblStatus = null;
    private JButton btnExport = null;
    private JButton btnCopy = null;
    private JCheckBox btnSelectionMode = null;

    JButton btnExecuteQuery = null;


    /**
     * Explicit default constructor.
     */
    public ProjectSQLTool() {
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
     * This method represents the 'command-pattern' design of the ProjectAnalyzerTool. It will actually allow the tool
     * to run.
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
        this.getInstruments();
        // Construct the GUI.
        this.constructScreen();
        this.pack();
        this.setTitle("Project query tool for project " + aProject.getProjectid() + " (connected to '" + iDBName + "')");
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
        if (this.getState() == java.awt.Frame.ICONIFIED) {
            this.setState(java.awt.Frame.NORMAL);
        }

        this.requestFocus();
    }

    /**
     * Get all the instruments from the database and init the Instrument[] iInstruments'.
     */
    private void getInstruments() {
        try {
            Instrument[] temp = Instrument.getAllInstruments(iConnection);
            // We need to have a 'All instruments' setting in the front of the list
            // so prefix it here.
            // I use a String object, instead of an Instrument, for ease of creation and
            // because I can now check for instrument filtering at submission time based on
            // 'instanceof Instrument'.
            Object[] instruments = new Object[temp.length + 1];
            instruments[0] = "All instruments";
            for (int i = 0; i < temp.length; i++) {
                instruments[i + 1] = temp[i];
            }
            iInstruments = instruments;
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, new String[]{"Unable to load instruments from the database: " + sqle.getMessage(), "Exiting SQLTool."}, "Unable to load instruments.", JOptionPane.ERROR_MESSAGE);
            this.close();
        }
    }

    /**
     * This method returns the button panel.
     *
     * @return JPanel with the buttons.
     */
    private JPanel getButtonPanel() {
        btnExecuteQuery = new JButton("Execute query");
        btnExecuteQuery.setMnemonic(KeyEvent.VK_E);
        btnExecuteQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeQueryTriggered();
            }
        });
        btnExecuteQuery.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    executeQueryTriggered();
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
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    close();
                }
            }
        });

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnExecuteQuery);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnExit);
        jpanButtons.add(Box.createHorizontalStrut(15));

        return jpanButtons;
    }

    /**
     * This method is called when the user clicks the 'execute query button'.
     */
    private void executeQueryTriggered() {
        final String query = this.constructQuery();
        // See if something sensible was returned by the
        // query constructor. If not, just bail.
        if (query == null) {
            return;
        }
        // Okay, we need to go ahead with this.
        try {
            if (iStatement == null) {
                iStatement = iConnection.createStatement();
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, new String[]{"Unfortunately, your query could not be created, (see below for query): " + sqle.getMessage(), query}, "Query failed!", JOptionPane.ERROR_MESSAGE);
            lblStatus.setForeground(Color.red);
            lblStatus.setText("Query creation failed.");
        }
        btnExecuteQuery.setEnabled(false);
        progress.setIndeterminate(true);
        final long startMillis = System.currentTimeMillis();
        String startTime = SDF.format(new Date(startMillis));
        progress.setString("Executing query (started at " + startTime + ")...");
        final SwingWorker queryWorker = new SwingWorker() {
            public Object construct() {
                Object result = null;
                try {
                    result = iStatement.executeQuery(query);
                } catch (SQLException e) {
                    result = e;
                }
                return result;
            }

            public void finished() {
                queryCompleted(this, query, startMillis);
            }

        };
        queryWorker.start();
    }

    /**
     * This method will be called by the SwingWorker whenever the query completes.
     *
     * @param aQuery       SwingWorker that's handled the query. Since it is calling us, the query is now complete.
     * @param aSQL         String with the actual SQL executed.
     * @param aStartMillis long with the original starting time of the query in milliseconds.
     */
    private void queryCompleted(SwingWorker aQuery, String aSQL, long aStartMillis) {
        progress.setIndeterminate(false);
        progress.setValue(progress.getMinimum());
        btnExecuteQuery.setEnabled(true);
        try {
            Object temp = aQuery.get();
            if (temp instanceof ResultSet) {
                ResultSet result = (ResultSet) temp;
                long endMillis = System.currentTimeMillis();
                double totalTime = 0.0;
                boolean inSeconds = false;
                totalTime = endMillis - aStartMillis;
                if (totalTime > 1000) {
                    totalTime /= 1000.0;
                    inSeconds = true;
                }
                DBResultSet dbr = new DBResultSet(result);
                tblResults.setModel(dbr);
                result.close();
                String duration = new BigDecimal(totalTime).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + (inSeconds ? " seconds" : " milliseconds");
                lblStatus.setForeground(this.getForeground());
                lblStatus.setText("Query returned " + dbr.getRowCount() + " rows (query took " + duration + ").");
                progress.setString("Query complete (" + duration + ")!");
            } else if (temp instanceof SQLException) {
                throw (SQLException) temp;
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, new String[]{"Unfortunately, your query failed, (see below for query): " + sqle.getMessage(), aSQL}, "Query failed!", JOptionPane.ERROR_MESSAGE);
            lblStatus.setForeground(Color.red);
            lblStatus.setText("Query failed.");
            progress.setString("Query failed");
        }
    }

    /**
     * This method initializes the components and lays them out on the screen.
     */
    private void constructScreen() {
        chkIncludeFile = new JCheckBox("Include spectrum file in select");
        rbtAllPeptides = new JRadioButton("Show all peptides");
        rbtAllPeptides.setMaximumSize(new Dimension(rbtAllPeptides.getMaximumSize().width, rbtAllPeptides.getPreferredSize().height));
        rbtAllPeptides.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtAllPeptides.isSelected()) {
                    chkIncludeFile.setEnabled(true);
                }
            }
        });
        rbtUniquePeptides = new JRadioButton("Show only unique peptides");
        rbtUniquePeptides.setMaximumSize(new Dimension(rbtUniquePeptides.getMaximumSize().width, rbtUniquePeptides.getPreferredSize().height));
        rbtUniquePeptides.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtUniquePeptides.isSelected()) {
                    chkIncludeFile.setEnabled(true);
                    chkHighestScorePeptide.setEnabled(true);
                } else {
                    chkHighestScorePeptide.setEnabled(false);
                }
            }
        });
        chkHighestScorePeptide = new JCheckBox("Show highest scoring peptide");
        chkHighestScorePeptide.setMaximumSize(new Dimension(chkHighestScorePeptide.getMaximumSize().width, chkHighestScorePeptide.getPreferredSize().height));
        chkHighestScorePeptide.setEnabled(false);

        JPanel jpanAllPeptides = new JPanel();
        jpanAllPeptides.setLayout(new BoxLayout(jpanAllPeptides, BoxLayout.X_AXIS));
        jpanAllPeptides.add(Box.createHorizontalStrut(5));
        jpanAllPeptides.add(rbtAllPeptides);
        jpanAllPeptides.add(Box.createHorizontalStrut(15));
        jpanAllPeptides.add(rbtUniquePeptides);
        jpanAllPeptides.add(Box.createHorizontalStrut(15));
        jpanAllPeptides.add(chkHighestScorePeptide);
        jpanAllPeptides.add(Box.createHorizontalGlue());

        rbtModSeq = new JRadioButton("Only peptides with modified sequences containing: ");
        rbtSeq = new JRadioButton("Only peptides with sequences containing: ");
        rbtSeq.setMaximumSize(new Dimension(rbtSeq.getMaximumSize().width, rbtSeq.getPreferredSize().height));
        rbtSeq.setPreferredSize(new Dimension(rbtModSeq.getPreferredSize().width, rbtSeq.getPreferredSize().height));
        rbtSeq.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtSeq.isSelected()) {
                    txtSeq.setEnabled(true);
                    chkIncludeFile.setEnabled(true);
                } else {
                    txtSeq.setEnabled(false);
                }
            }
        });
        txtSeq = new JTextField(10);
        txtSeq.setMaximumSize(new Dimension(txtSeq.getPreferredSize().width, txtSeq.getPreferredSize().height));
        txtSeq.setEnabled(false);
        txtSeq.setToolTipText("Use '%' signs before and after for wildcards.");
        JPanel jpanSeq = new JPanel();
        jpanSeq.setLayout(new BoxLayout(jpanSeq, BoxLayout.X_AXIS));
        jpanSeq.add(Box.createHorizontalStrut(5));
        jpanSeq.add(rbtSeq);
        jpanSeq.add(Box.createHorizontalStrut(5));
        jpanSeq.add(txtSeq);
        jpanSeq.add(Box.createHorizontalGlue());

        rbtModSeq.setMaximumSize(new Dimension(rbtModSeq.getMaximumSize().width, rbtModSeq.getPreferredSize().height));
        rbtModSeq.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtModSeq.isSelected()) {
                    txtModSeq.setEnabled(true);
                    chkIncludeFile.setEnabled(true);
                } else {
                    txtModSeq.setEnabled(false);
                }
            }
        });
        txtModSeq = new JTextField(10);
        txtModSeq.setMaximumSize(new Dimension(txtModSeq.getPreferredSize().width, txtModSeq.getPreferredSize().height));
        txtModSeq.setEnabled(false);
        txtModSeq.setToolTipText("Use '%' signs before and after for wildcards.");
        JPanel jpanModSeq = new JPanel();
        jpanModSeq.setLayout(new BoxLayout(jpanModSeq, BoxLayout.X_AXIS));
        jpanModSeq.add(Box.createHorizontalStrut(5));
        jpanModSeq.add(rbtModSeq);
        jpanModSeq.add(Box.createHorizontalStrut(5));
        jpanModSeq.add(txtModSeq);
        jpanModSeq.add(Box.createHorizontalGlue());

        rbtTitle = new JRadioButton("Only identifications with titles containing: ");
        rbtTitle.setMaximumSize(new Dimension(rbtTitle.getMaximumSize().width, rbtTitle.getPreferredSize().height));
        rbtTitle.setPreferredSize(new Dimension(rbtModSeq.getPreferredSize().width, rbtTitle.getPreferredSize().height));
        rbtTitle.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtTitle.isSelected()) {
                    txtTitle.setEnabled(true);
                    chkIncludeFile.setEnabled(true);
                } else {
                    txtTitle.setEnabled(false);
                }
            }
        });
        txtTitle = new JTextField(10);
        txtTitle.setMaximumSize(new Dimension(txtTitle.getPreferredSize().width, txtTitle.getPreferredSize().height));
        txtTitle.setEnabled(false);
        txtTitle.setToolTipText("Use '%' signs before and after for wildcards.");
        JPanel jpanTitle = new JPanel();
        jpanTitle.setLayout(new BoxLayout(jpanTitle, BoxLayout.X_AXIS));
        jpanTitle.add(Box.createHorizontalStrut(5));
        jpanTitle.add(rbtTitle);
        jpanTitle.add(Box.createHorizontalStrut(5));
        jpanTitle.add(txtTitle);
        jpanTitle.add(Box.createHorizontalGlue());

        chkOmitIPIXRefs = new JCheckBox("Omit IPI database Xrefs from description");
        chkOmitIPIXRefs.setSelected(false);
        chkOmitIPIXRefs.setEnabled(false);
        rbtUniqueProteins = new JRadioButton("Show only unique proteins");
        rbtUniqueProteins.setMaximumSize(new Dimension(rbtUniqueProteins.getMaximumSize().width, rbtUniqueProteins.getPreferredSize().height));
        rbtUniqueProteins.setPreferredSize(new Dimension(rbtModSeq.getPreferredSize().width, rbtUniqueProteins.getPreferredSize().height));
        rbtUniqueProteins.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtUniqueProteins.isSelected()) {
                    chkOmitIPIXRefs.setEnabled(true);
                    chkIncludeFile.setEnabled(false);
                } else {
                    chkOmitIPIXRefs.setEnabled(false);
                }
            }
        });

        JPanel jpanOnlyProteins = new JPanel();
        jpanOnlyProteins.setLayout(new BoxLayout(jpanOnlyProteins, BoxLayout.X_AXIS));
        jpanOnlyProteins.add(Box.createHorizontalStrut(5));
        jpanOnlyProteins.add(rbtUniqueProteins);
        jpanOnlyProteins.add(chkOmitIPIXRefs);
        jpanOnlyProteins.add(Box.createHorizontalGlue());

        rbtSingles = new JRadioButton("Show only peptides detected as single");
        rbtSingles.setMaximumSize(new Dimension(rbtSingles.getMaximumSize().width, rbtSingles.getPreferredSize().height));
        rbtSingles.setPreferredSize(new Dimension(rbtModSeq.getPreferredSize().width, rbtSingles.getPreferredSize().height));
        rbtSingles.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtSingles.isSelected()) {
                    chkIncludeFile.setEnabled(true);
                    rbtSingleLight.setEnabled(true);
                    rbtSingleHeavy.setEnabled(true);
                    rbtSingleBoth.setEnabled(true);
                } else {
                    rbtSingleLight.setEnabled(false);
                    rbtSingleHeavy.setEnabled(false);
                    rbtSingleBoth.setEnabled(false);
                }
            }
        });
        rbtSingleLight = new JRadioButton("light");
        rbtSingleLight.setMaximumSize(new Dimension(rbtSingleLight.getMaximumSize().width, rbtSingleLight.getPreferredSize().height));
        rbtSingleLight.setEnabled(false);
        rbtSingleHeavy = new JRadioButton("heavy");
        rbtSingleHeavy.setMaximumSize(new Dimension(rbtSingleHeavy.getMaximumSize().width, rbtSingleHeavy.getPreferredSize().height));
        rbtSingleHeavy.setEnabled(false);
        rbtSingleBoth = new JRadioButton("both");
        rbtSingleBoth.setMaximumSize(new Dimension(rbtSingleBoth.getMaximumSize().width, rbtSingleBoth.getPreferredSize().height));
        rbtSingleBoth.setEnabled(false);
        ButtonGroup bgSingle = new ButtonGroup();
        bgSingle.add(rbtSingleLight);
        bgSingle.add(rbtSingleHeavy);
        bgSingle.add(rbtSingleBoth);
        rbtSingleBoth.setSelected(true);
        JPanel jpanSingles = new JPanel();
        jpanSingles.setLayout(new BoxLayout(jpanSingles, BoxLayout.X_AXIS));
        jpanSingles.add(Box.createHorizontalStrut(5));
        jpanSingles.add(rbtSingles);
        jpanSingles.add(rbtSingleLight);
        jpanSingles.add(Box.createHorizontalStrut(5));
        jpanSingles.add(rbtSingleHeavy);
        jpanSingles.add(Box.createHorizontalStrut(5));
        jpanSingles.add(rbtSingleBoth);
        jpanSingles.add(Box.createHorizontalStrut(15));
        jpanSingles.add(Box.createHorizontalGlue());

        ButtonGroup bg = new ButtonGroup();
        bg.add(rbtAllPeptides);
        bg.add(rbtUniquePeptides);
        bg.add(rbtSeq);
        bg.add(rbtModSeq);
        bg.add(rbtTitle);
        bg.add(rbtUniqueProteins);
        bg.add(rbtSingles);
        rbtAllPeptides.setSelected(true);

        chkIncludeFile.setSelected(false);
        JPanel jpanIncludeFile = new JPanel();
        jpanIncludeFile.setLayout(new BoxLayout(jpanIncludeFile, BoxLayout.X_AXIS));
        jpanIncludeFile.add(Box.createHorizontalStrut(5 + rbtModSeq.getPreferredSize().width));
        jpanIncludeFile.add(chkIncludeFile);
        jpanIncludeFile.add(Box.createHorizontalGlue());

        JPanel jpanRadios = new JPanel();
        jpanRadios.setLayout(new BoxLayout(jpanRadios, BoxLayout.Y_AXIS));
        jpanRadios.setBorder(BorderFactory.createTitledBorder("Selection options"));
        jpanRadios.add(jpanAllPeptides);
        jpanRadios.add(Box.createVerticalStrut(5));
        jpanRadios.add(jpanSeq);
        jpanRadios.add(Box.createVerticalStrut(5));
        jpanRadios.add(jpanModSeq);
        jpanRadios.add(Box.createVerticalStrut(5));
        jpanRadios.add(jpanTitle);
        jpanRadios.add(Box.createVerticalStrut(5));
        jpanRadios.add(jpanOnlyProteins);
        jpanRadios.add(Box.createVerticalStrut(5));
        jpanRadios.add(jpanSingles);
        jpanRadios.add(Box.createVerticalStrut(5));
        jpanRadios.add(jpanIncludeFile);
        jpanRadios.add(Box.createVerticalGlue());

        cmbInstrument = new JComboBox(iInstruments);
        cmbInstrument.setMaximumSize(new Dimension(cmbInstrument.getMaximumSize().width, cmbInstrument.getPreferredSize().height));

        JPanel jpanInstruments = new JPanel();
        jpanInstruments.setLayout(new BoxLayout(jpanInstruments, BoxLayout.X_AXIS));
        jpanInstruments.setBorder(BorderFactory.createTitledBorder("Instrument selection"));
        jpanInstruments.add(Box.createHorizontalGlue());
        jpanInstruments.add(cmbInstrument);
        jpanInstruments.add(Box.createHorizontalGlue());

        JPanel jpanControls = new JPanel();
        jpanControls.setLayout(new BoxLayout(jpanControls, BoxLayout.Y_AXIS));
        jpanControls.add(jpanRadios);
        jpanControls.add(Box.createVerticalStrut(5));
        jpanControls.add(jpanInstruments);

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

        JPanel jpanButtons = this.getButtonPanel();

        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanControls);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanProgress);
        jpanMain.add(Box.createVerticalStrut(15));
        jpanMain.add(jpanButtons);
        jpanMain.add(Box.createVerticalGlue());

        tblResults = new JTableForDB(null);
        tblResults.addMouseListener(new MouseAdapter() {
            /**
             * Invoked when the mouse has been clicked on a component.
             */
            public void mouseClicked(MouseEvent e) {
                // Transform clickpoint to row and column indices +
                // retrieve the renderer at that location.
                Point compLoc = e.getPoint();
                int col = tblResults.columnAtPoint(compLoc);
                int row = tblResults.rowAtPoint(compLoc);
                TableCellRenderer comp = tblResults.getCellRenderer(row, col);

                if ((e.getModifiers() == MouseEvent.BUTTON3_MASK || e.getModifiers() == MouseEvent.BUTTON2_MASK) && (tblResults.getColumnName(col) != null) && tblResults.getColumnName(col).trim().equalsIgnoreCase("l_datfileid")) {
                    // Get the data from the 'datfile' table.
                    try {
                        Statement stat = iConnection.createStatement();
                        ResultSet rs = stat.executeQuery("select server, folder, filename from datfile where datfileid=" + tblResults.getValueAt(row, col));
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
                        logger.error(sqle.getMessage(), sqle);
                        JOptionPane.showMessageDialog((Component) comp, "Unable to load data for selected datfile (ID=" + tblResults.getValueAt(row, col) + "): " + sqle.getMessage() + ".", "Unable to load datfile data!", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception exc) {
                        logger.error(exc.getMessage(), exc);
                        JOptionPane.showMessageDialog((Component) comp, "Unable to open internet view of selected entry: " + exc.getMessage() + ".", "Unable to open browser window", JOptionPane.ERROR_MESSAGE);
                    }
                } else if ((e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON2) && (comp instanceof ByteArrayRenderer || tblResults.getColumnName(col).trim().equalsIgnoreCase("l_spectrumid"))) {
                    byte[] result = null;
                    String filename = "Spectrum";
                    try {
                        if (tblResults.getColumnName(col).trim().equalsIgnoreCase("l_spectrumid")) {
                            try {
                                Spectrum lSpectrum = Spectrum.findFromID(((Number) tblResults.getValueAt(row, col)).longValue(), iConnection);
                                Spectrum_file lSpectrum_file = Spectrum_file.findFromID(lSpectrum.getSpectrumid(), iConnection);
                                result = lSpectrum_file.getUnzippedFile();
                                filename = lSpectrum.getFilename();
                            } catch (SQLException sqle) {
                                logger.error(sqle.getMessage(), sqle);
                                JOptionPane.showMessageDialog((Component) comp, "Unable to load data for selected spectrumfile (ID=" + tblResults.getValueAt(row, col) + "): " + sqle.getMessage() + ".", "Unable to load spectrumfile data!", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        } else {
                            // Creating the frame with the data from the model.
                            int modelCol = tblResults.convertColumnIndexToModel(col);
                            byte[] spectrumZipped = (byte[]) tblResults.getModel().getValueAt(row, modelCol);
                            result = Spectrum_file.getUnzippedFile(spectrumZipped);
                        }
                        MascotGenericFile mgf = new MascotGenericFile(filename, new String(result));
                        if (mgf.getPeaks() == null || mgf.getPeaks().size() == 0) {
                            JOptionPane.showMessageDialog(ProjectSQLTool.this, "This spectrum contains no peaks and can not be visualized!", "No peaks found in spectrum!", JOptionPane.WARNING_MESSAGE);
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
                            for (int i = 0; i < tblResults.getModel().getColumnCount(); i++) {
                                if (tblResults.getModel().getColumnName(i).trim().toLowerCase().equals("identificationid")) {
                                    idColumn = i;
                                }
                            }
                            if (idColumn > -1) {
                                idid = ((Number) tblResults.getModel().getValueAt(row, idColumn)).longValue();

                                Vector temp = FragmentionMiddleMan.getAllMascotDatfileFragmentIonImpl(iConnection, idid);
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
                                JOptionPane.showMessageDialog(ProjectSQLTool.this, new String[]{"Unable to locate identification id in the current result set.", "Could not locate fragment ions."}, "Identification id not found!", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (SQLException sqle) {
                            logger.error(sqle.getMessage(), sqle);
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
                        logger.error(ioe.getMessage(), ioe);
                    }
                } else if (e.getModifiersEx() == MouseEvent.CTRL_DOWN_MASK && comp instanceof ByteArrayRenderer) {
                    // Creating the frame with the data from the model.
                    int modelCol = tblResults.convertColumnIndexToModel(col);
                    byte[] data = (byte[]) tblResults.getModel().getValueAt(row, modelCol);
                    // Get the output location.
                    try {
                        FileDialog fd = new FileDialog(ProjectSQLTool.this, "Save byte[] to disk...", FileDialog.SAVE);
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
                            JOptionPane.showMessageDialog(ProjectSQLTool.this, "Output written to " + select + ".", "Output written!", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (IOException ioe) {
                        logger.error(ioe.getMessage(), ioe);
                        JOptionPane.showMessageDialog(ProjectSQLTool.this, "Unable to save data to file: " + ioe.getMessage(), "Unable to write data to file!", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (e.getClickCount() >= 2 && (tblResults.getColumnName(col) != null) && tblResults.getColumnName(col).trim().equalsIgnoreCase("l_datfileid")) {
                    // Get the data from the 'datfile' table.
                    try {
                        Statement stat = iConnection.createStatement();
                        ResultSet rs = stat.executeQuery("select server, folder, filename from datfile where datfileid=" + tblResults.getValueAt(row, col));
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
                        logger.error(sqle.getMessage(), sqle);
                        JOptionPane.showMessageDialog((Component) comp, "Unable to load data for selected datfile (ID=" + tblResults.getValueAt(row, col) + "): " + sqle.getMessage() + ".", "Unable to load datfile data!", JOptionPane.ERROR_MESSAGE);
                    } catch (Exception exc) {
                        logger.error(exc.getMessage(), exc);
                        JOptionPane.showMessageDialog((Component) comp, "Unable to open internet view of selected entry: " + exc.getMessage() + ".", "Unable to open browser window", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (e.getClickCount() >= 2 && (tblResults.getColumnName(col) != null) && tblResults.getColumnName(col).trim().equalsIgnoreCase("ion_coverage")) {
                    // Get all the fragment ions for this identification.
                    long idid = -1;
                    String modSeq = null;
                    Vector fragments = new Vector();
                    try {
                        int idColumn = -1;
                        for (int i = 0; i < tblResults.getModel().getColumnCount(); i++) {
                            String colHeader = tblResults.getModel().getColumnName(i).trim().toLowerCase();
                            if (colHeader.equals("identificationid")) {
                                idColumn = i;
                            } else if (colHeader.equals("modified_sequence")) {
                                modSeq = tblResults.getModel().getValueAt(row, i).toString();
                            }
                        }
                        if (idColumn > -1 && modSeq != null) {
                            idid = ((Number) tblResults.getModel().getValueAt(row, idColumn)).longValue();

                            Vector temp = FragmentionMiddleMan.getAllMascotDatfileFragmentIonImpl(iConnection, idid);
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
                            SequenceFragmentationPanel sfp = new SequenceFragmentationPanel(modSeq, fragments, true);
                            JDialog dialog = new JDialog(ProjectSQLTool.this, "Fragment peak annotation", false);
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
                            JOptionPane.showMessageDialog(ProjectSQLTool.this, new String[]{"Unable to locate identification id or modified sequence in the current result set.", "Could not locate fragment ions or modified sequence."}, "Identification id or modified sequence not found!", JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (SQLException sqle) {
                        logger.error(sqle.getMessage(), sqle);
                        JOptionPane.showMessageDialog((Component) comp, "Unable to load fragment ions for selected identification (ID=" + idid + "): " + sqle.getMessage() + ".", "Unable to load fragment ions!", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        tblResults.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JPanel jpanTable = new JPanel(new BorderLayout());
        jpanTable.add(new JScrollPane(tblResults), BorderLayout.CENTER);

        btnSelectionMode = new JCheckBox("Column selection mode", false);
        btnSelectionMode.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (btnSelectionMode.isSelected()) {
                    tblResults.setColumnSelectionAllowed(true);
                    tblResults.setRowSelectionAllowed(false);
                } else {
                    tblResults.setColumnSelectionAllowed(false);
                    tblResults.setRowSelectionAllowed(true);
                }
            }
        });
        JPanel jpanCheckboxes = new JPanel();
        jpanCheckboxes.setLayout(new BoxLayout(jpanCheckboxes, BoxLayout.X_AXIS));
        jpanCheckboxes.add(Box.createHorizontalStrut(10));
        jpanCheckboxes.add(btnSelectionMode);
        jpanCheckboxes.add(Box.createHorizontalGlue());

        JPanel jpanTableAndCheck = new JPanel();
        jpanTableAndCheck.setLayout(new BorderLayout());
        jpanTableAndCheck.add(jpanTable, BorderLayout.CENTER);
        jpanTableAndCheck.add(jpanCheckboxes, BorderLayout.SOUTH);


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
        btnExport.setMnemonic(KeyEvent.VK_X);
        btnExport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportTriggered();
            }
        });
        JPanel jpanTableButtons = new JPanel();
        jpanTableButtons.setLayout(new BoxLayout(jpanTableButtons, BoxLayout.X_AXIS));
        jpanTableButtons.add(Box.createHorizontalGlue());
        jpanTableButtons.add(btnCopy);
        jpanTableButtons.add(Box.createRigidArea(new Dimension(5, btnCopy.getHeight())));
        jpanTableButtons.add(btnExport);

        JPanel jpanStatus = new JPanel();
        jpanStatus.setBorder(BorderFactory.createTitledBorder("Status"));
        jpanStatus.setLayout(new BorderLayout());
        jpanStatus.add(jpanTableButtons, BorderLayout.EAST);
        jpanStatus.add(lblStatus, BorderLayout.CENTER);

        JPanel jpanTableFull = new JPanel();
        jpanTableFull.setBorder(BorderFactory.createTitledBorder("Query results"));
        jpanTableFull.setLayout(new BorderLayout());
        jpanTableFull.add(jpanTableAndCheck, BorderLayout.CENTER);
        jpanTableFull.add(jpanStatus, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jpanMain, jpanTableFull);
        splitPane.setOneTouchExpandable(true);

        this.getContentPane().add(splitPane, BorderLayout.CENTER);
    }

    /**
     * This method is called whenever the user clicked the button to export data.
     */
    private void exportTriggered() {
        if (tblResults.getModel() == null || tblResults.getModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data in table!", "No data to export!", JOptionPane.WARNING_MESSAGE);
            return;
        }
        ExportDialog ed = new ExportDialog(this, "Export data to file", (DBResultSet) ((TableSorter) tblResults.getModel()).getModel());
        ed.setVisible(true);
    }

    /**
     * This method is called when the user clicks the 'copy selection' button.
     */
    private void copyTriggered() {
        int[] cols = this.tblResults.getSelectedColumns();
        int[] rows = this.tblResults.getSelectedRows();

        int nbrCols = this.tblResults.getColumnCount();
        int nbrRows = this.tblResults.getRowCount();

        String data = null;

        if (tblResults.getRowSelectionAllowed() && rows != null && rows.length > 0) {
            StringBuffer allRows = new StringBuffer();
            for (int i = 0; i < rows.length; i++) {
                for (int j = 0; j < nbrCols; j++) {
                    Object tempValue = this.tblResults.getValueAt(rows[i], j);
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
        } else if (tblResults.getColumnSelectionAllowed() && cols != null && cols.length > 0) {
            StringBuffer allCols = new StringBuffer();
            for (int i = 0; i < nbrRows; i++) {
                for (int j = 0; j < cols.length; j++) {
                    Object tempValue = this.tblResults.getValueAt(i, cols[j]);
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
            String lMessage = "No rows or columns selected!";
            logger.error(lMessage);
            JOptionPane.showMessageDialog(this, lMessage, "No data selected!", JOptionPane.ERROR_MESSAGE);
        }

        if (data != null) {
            Object tempObject = new StringSelection(data);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents((Transferable) tempObject, (ClipboardOwner) tempObject);
        }
    }

    /**
     * This method creates a query that corresponds with the selections made by the user on th GUI.
     */
    private String constructQuery() {
        StringBuffer query = new StringBuffer();
        String prefix = "select ";
        boolean boolIncludeFile = chkIncludeFile.isSelected();
        if (boolIncludeFile) {
            prefix += "sf.file as Spectrum, ";
        }
        Object temp = cmbInstrument.getSelectedItem();
        long instrumentID = -1;
        if (temp instanceof Instrument) {
            instrumentID = ((Instrument) temp).getInstrumentid();
        }
        if (rbtAllPeptides.isSelected()) {
            query.append(prefix + "i.identificationid, i.l_datfileid, i.l_spectrumid, s.filename, i.accession, i.start, i.end, i.enzymatic, i.sequence, i.modified_sequence, i.ion_coverage, i.score, i.exp_mass,\n" +
                    "i.cal_mass, i.light_isotope, i.heavy_isotope, i.valid, trim(i.Description) as Description, i.creationdate, i.identitythreshold, i.confidence,\n" +
                    "i.DB, i.title, i.precursor, i.charge, i.isoforms, i.db_filename, i.mascot_version" +
                    " from identification as i, validation as v, spectrum as s" + (chkIncludeFile.isSelected() ? ", spectrum_file as sf" : "") + " where l_validationtypeid >= 0 and v.l_identificationid = i.identificationid and s.spectrumid = i.l_spectrumid and s.l_projectid=" + iProject.getProjectid() + (chkIncludeFile.isSelected() ? " and s.spectrumid = sf.l_spectrumid " : ""));
            // See if there is an instrument selection.
            if (instrumentID >= 0) {
                query.append(" and s.l_instrumentid=" + instrumentID);
            }

        } else if (rbtUniquePeptides.isSelected()) {
            if (chkHighestScorePeptide.isSelected()) {
                // This query requires subselects. So it wil redefine the query StringBuffer completely.
                // NOTE that there is no real guarantee that it will work outisde of MySQL 4.1 - I do know it works there.
                query.append("select count(*) as 'nbr. sequences', sub.* from (select " + (chkIncludeFile.isSelected() ? "sf.file as Spectrum, " : "") +
                        "i.* from identification as i, validation as v, spectrum as s, spectrum_file as sf where i.l_spectrumid=s.spectrumid and " +
                        "s.l_projectid=" + iProject.getProjectid() + (chkIncludeFile.isSelected() ? " and s.spectrumid = sf.l_spectrumid " : "") + " " + (instrumentID >= 0 ? "and s.l_instrumentid=" + instrumentID + " " : "") + "and l_validationtypeid >= 0 and v.l_identificationid = i.identificationid order by score DESC) as sub group by sequence");
            } else {
                query.append(prefix + "i.identificationid, i.l_datfileid, i.l_spectrumid, s.filename, i.accession, i.start, i.end, i.enzymatic, i.sequence, i.modified_sequence, i.ion_coverage, i.score, i.exp_mass,\n" +
                        "i.cal_mass, i.light_isotope, i.heavy_isotope, i.valid, trim(i.Description) as Description, i.creationdate, i.identitythreshold, i.confidence,\n" +
                        "i.DB, i.title, i.precursor, i.charge, i.isoforms, i.db_filename, i.mascot_version, count(*) as 'Number of spectra' " +
                        "from identification as i, validation as v, spectrum as s" + (chkIncludeFile.isSelected() ? ", spectrum_file as sf" : "") + " where l_validationtypeid >= 0 and v.l_identificationid = i.identificationid and s.spectrumid = i.l_spectrumid " + (chkIncludeFile.isSelected() ? " and s.spectrumid = sf.l_spectrumid " : "") + " and s.l_projectid=" + iProject.getProjectid() + (instrumentID >= 0 ? " and s.l_instrumentid=" + instrumentID : "") + " group by i.sequence");
            }
        } else if (rbtSeq.isSelected()) {
            String param = txtSeq.getText();
            if (param != null && !param.trim().equals("")) {
                param = param.trim();
            } else {
                JOptionPane.showMessageDialog(this, "You need to specify a sequence element to query by!", "No sequence element provided!", JOptionPane.WARNING_MESSAGE);
                txtSeq.requestFocus();
                return null;
            }
            query.append(prefix + "i.identificationid, i.l_datfileid, i.l_spectrumid, s.filename, i.accession, i.start, i.end, i.enzymatic, i.sequence, i.modified_sequence, i.ion_coverage, i.score, i.exp_mass,\n" +
                    "i.cal_mass, i.light_isotope, i.heavy_isotope, i.valid, trim(i.Description) as Description, i.creationdate, i.identitythreshold, i.confidence,\n" +
                    "i.DB, i.title, i.precursor, i.charge, i.isoforms, i.db_filename, i.mascot_version " +
                    "from identification as i, validation as v, spectrum as s" + (chkIncludeFile.isSelected() ? ", spectrum_file as sf" : "") + " where l_validationtypeid >= 0 and v.l_identificationid = i.identificationid and s.spectrumid = i.l_spectrumid and s.l_projectid=" + iProject.getProjectid() + (chkIncludeFile.isSelected() ? " and s.spectrumid = sf.l_spectrumid " : "") + " and i.sequence like '" + param + "'" + (instrumentID >= 0 ? " and s.l_instrumentid=" + instrumentID : ""));
        } else if (rbtModSeq.isSelected()) {
            String param = txtModSeq.getText();
            if (param != null && !param.trim().equals("")) {
                param = param.trim();
            } else {
                JOptionPane.showMessageDialog(this, "You need to specify a modified sequence element to query by!", "No modified sequence element provided!", JOptionPane.WARNING_MESSAGE);
                txtModSeq.requestFocus();
                return null;
            }
            query.append(prefix + "i.identificationid, i.l_datfileid, i.l_spectrumid, s.filename, i.accession, i.start, i.end, i.enzymatic, i.sequence, i.modified_sequence, i.ion_coverage, i.score, i.exp_mass,\n" +
                    "i.cal_mass, i.light_isotope, i.heavy_isotope, i.valid, trim(i.Description) as Description, i.creationdate, i.identitythreshold, i.confidence,\n" +
                    "i.DB, i.title, i.precursor, i.charge, i.isoforms, i.db_filename, i.mascot_version " +
                    "from identification as i, validation as v, spectrum as s" + (chkIncludeFile.isSelected() ? ", spectrum_file as sf" : "") + " where l_validationtypeid >= 0 and v.l_identificationid = i.identificationid and s.spectrumid = i.l_spectrumid and s.l_projectid=" + iProject.getProjectid() + (chkIncludeFile.isSelected() ? " and s.spectrumid = sf.l_spectrumid " : "") + " and i.modified_sequence like '" + param + "'" + (instrumentID >= 0 ? " and s.l_instrumentid=" + instrumentID : ""));
        } else if (rbtTitle.isSelected()) {
            String param = txtTitle.getText();
            if (param != null && !param.trim().equals("")) {
                param = param.trim();
            } else {
                JOptionPane.showMessageDialog(this, "You need to specify a title part to query by!", "No title part provided!", JOptionPane.WARNING_MESSAGE);
                txtTitle.requestFocus();
                return null;
            }
            query.append(prefix + "i.identificationid, i.l_datfileid, i.l_spectrumid, s.filename, i.accession, i.start, i.end, i.enzymatic, i.sequence, i.modified_sequence, i.ion_coverage, i.score, i.exp_mass,\n" +
                    "i.cal_mass, i.light_isotope, i.heavy_isotope, i.valid, trim(i.Description) as Description, i.creationdate, i.identitythreshold, i.confidence,\n" +
                    "i.DB, i.title, i.precursor, i.charge, i.isoforms, i.db_filename, i.mascot_version " +
                    "from identification as i, validation as v, spectrum as s" + (chkIncludeFile.isSelected() ? ", spectrum_file as sf" : "") + " where l_validationtypeid >= 0 and v.l_identificationid = i.identificationid and s.spectrumid = i.l_spectrumid and s.l_projectid=" + iProject.getProjectid() + (chkIncludeFile.isSelected() ? " and s.spectrumid = sf.l_spectrumid " : "") + " and i.title like '" + param + "'" + (instrumentID >= 0 ? " and s.l_instrumentid=" + instrumentID : ""));
        } else if (rbtUniqueProteins.isSelected()) {
            query.append("select substring(i.accession, 1, if((locate('.', i.accession)-1) >0, locate('.', i.accession)-1, length(i.accession))) as versionless_accession, ");
            if (chkOmitIPIXRefs.isSelected()) {
                query.append("trim(substring(i.description, locate(' ', i.description, locate('Tax_ID=', i.description))+1)) as 'description with IPI XRefs removed', ");
            } else {
                query.append("trim(i.description) as Description, ");
            }
            // XRef extractor.
            query.append("cast(\n" +
                    "\tif (\n" +
                    "\t\tlocate('SWISS-PROT:',i.description) > 0,\n" +
                    "\t\tsubstring(\n" +
                    "\t\t\ti.description, \n" +
                    "\t\t\tlocate('SWISS-PROT:',i.description) + 11, \n" +
                    "\t\t\tif (\n" +
                    "\t\t\t\tlocate('|', i.description, locate('SWISS-PROT:',i.description)) > 0,\n" +
                    "\t\t\t\tlocate('|', i.description, locate('SWISS-PROT:',i.description)) - locate('SWISS-PROT:',i.description) - 11,\n" +
                    "\t\t\t\tlocate(' ', i.description, locate('SWISS-PROT:',i.description)) - locate('SWISS-PROT:',i.description) - 11\n" +
                    "\t\t\t)\n" +
                    "\t\t),\n" +
                    "\t\tif (\n" +
                    "\t\t\tlocate('TREMBL:',i.description) > 0,\n" +
                    "\t\t\tsubstring(\n" +
                    "\t\t\t\ti.description, \n" +
                    "\t\t\t\tlocate('TREMBL:',i.description) + 7, \n" +
                    "\t\t\t\tif (\n" +
                    "\t\t\t\t\tlocate('|', i.description, locate('TREMBL:',i.description)) > 0,\n" +
                    "\t\t\t\t\tlocate('|', i.description, locate('TREMBL:',i.description)) - locate('TREMBL:',i.description) - 7,\n" +
                    "\t\t\t\t\tlocate(' ', i.description, locate('TREMBL:',i.description)) - locate('TREMBL:',i.description) - 7\n" +
                    "\t\t\t\t)\n" +
                    "\t\t\t),\n" +
                    "\t\t\tif (\n" +
                    "\t\t\t\tlocate('REFSEQ',i.description) > 0,\n" +
                    "\t\t\t\tsubstring(\n" +
                    "\t\t\t\t\ti.description, \n" +
                    "\t\t\t\t\tlocate('REFSEQ',i.description) + 10, \n" +
                    "\t\t\t\t\tif (\n" +
                    "\t\t\t\t\t\tlocate('|', i.description, locate('REFSEQ',i.description)) > 0,\n" +
                    "\t\t\t\t\t\tlocate('|', i.description, locate('REFSEQ',i.description)) - locate('REFSEQ',i.description) - 10,\n" +
                    "\t\t\t\t\t\tlocate(' ', i.description, locate('REFSEQ',i.description)) - locate('REFSEQ',i.description) - 10\n" +
                    "\t\t\t\t\t)\n" +
                    "\t\t\t\t),\n" +
                    "\t\t\t\tif (\n" +
                    "\t\t\t\t\tlocate('ENSEMBL:',i.description) > 0,\n" +
                    "\t\t\t\t\tsubstring(\n" +
                    "\t\t\t\t\t\ti.description, \n" +
                    "\t\t\t\t\t\tlocate('ENSEMBL:',i.description) + 8, \n" +
                    "\t\t\t\t\t\tif (\n" +
                    "\t\t\t\t\t\t\tlocate('|', i.description, locate('ENSEMBL:',i.description)) > 0,\n" +
                    "\t\t\t\t\t\t\tlocate('|', i.description, locate('ENSEMBL:',i.description)) - locate('ENSEMBL:',i.description) - 8,\n" +
                    "\t\t\t\t\t\t\tlocate(' ', i.description, locate('ENSEMBL:',i.description)) - locate('ENSEMBL:',i.description) - 8\n" +
                    "\t\t\t\t\t\t)\n" +
                    "\t\t\t\t\t),\n" +
                    "\t\t\t\t\t''\n" +
                    "\t\t\t\t)\n" +
                    "\t\t\t)\n" +
                    "\t\t)\n" +
                    "\t) as CHAR) as XRef,");
            query.append("count(*) as 'Number of spectra', count(distinct sequence) as 'Number of unique peptides' from identification as i, validation as v, spectrum as s where l_validationtypeid >= 0 and v.l_identificationid = i.identificationid and s.spectrumid = i.l_spectrumid and s.l_projectid=" + iProject.getProjectid() + (instrumentID >= 0 ? " and s.l_instrumentid=" + instrumentID : ""));
        } else if (rbtSingles.isSelected()) {
            // Query basis.
            query.append(prefix + "i.identificationid, i.l_datfileid, i.l_spectrumid, s.filename, i.accession, i.start, i.end, i.enzymatic, i.sequence, i.modified_sequence, i.ion_coverage, i.score, i.exp_mass,\n" +
                    "i.cal_mass, i.light_isotope, i.heavy_isotope, i.valid, trim(i.Description) as Description, i.creationdate, i.identitythreshold, i.confidence,\n" +
                    "i.DB, i.title, i.precursor, i.charge, i.isoforms, i.db_filename, i.mascot_version " +
                    "from identification as i, validation as v, spectrum as s" + (chkIncludeFile.isSelected() ? ", spectrum_file as sf" : "") + " where l_validationtypeid >= 0 and v.l_identificationid = i.identificationid and s.spectrumid = i.l_spectrumid and s.l_projectid=" + iProject.getProjectid() + (chkIncludeFile.isSelected() ? " and s.spectrumid = sf.l_spectrumid " : "") + " and ");
            // See if we need light, heavy or both.
            String singleHeavy = "(i.light_isotope = 0 and i.heavy_isotope>0)";
            String singleLight = "(i.light_isotope>0 and i.heavy_isotope=0)";
            if (rbtSingleBoth.isSelected()) {
                query.append("(" + singleHeavy + " or " + singleLight + ")");
            } else if (rbtSingleLight.isSelected()) {
                query.append(singleLight);
            } else if (rbtSingleHeavy.isSelected()) {
                query.append(singleHeavy);
            }
            if (instrumentID >= 0) {
                query.append(" and s.l_instrumentid=" + instrumentID);
            }
        }
        if (rbtUniqueProteins.isSelected()) {
            query.append(" group by versionless_accession");
        }
        return query.toString();
    }

    /**
     * This method should be called whenever this tool closes down.
     */
    public void close() {
        // Notify the parent.
        iParent.toolClosing(this);
        if (iStatement != null) {
            try {
                iStatement.close();
            } catch (SQLException sqle) {
                logger.error(sqle.getMessage(), sqle);
            }
        }
        this.setVisible(false);
        this.dispose();
    }


    public boolean isStandAlone() {
        return false;
    }
}
