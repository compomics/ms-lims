/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 7-mrt-2005
 * Time: 7:47:11
 */
package com.compomics.mslims.gui.projectanalyzertools;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Protocol;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.mslims.db.utils.ProjectReporter;
import com.compomics.mslims.gui.ProjectAnalyzer;
import com.compomics.mslims.gui.interfaces.ProjectAnalyzerTool;
import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.sun.SwingWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class provides a tool that allows the user to generate some interesting figures about the current project.
 *
 * @author Lennart Martens
 * @version $Id: DescriptiveNumbersTool.java,v 1.4 2009/03/11 13:57:45 niklaas Exp $
 */
public class DescriptiveNumbersTool extends FlamableJFrame implements ProjectAnalyzerTool {
    // Class specific log4j logger for DescriptiveNumbersTool instances.
    private static Logger logger = Logger.getLogger(DescriptiveNumbersTool.class);

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
     * The PROTOCOL type for the current project.
     */
    private Protocol iProtocol = null;

    /**
     * This String holds the name for the tool.
     */
    private String iToolName = null;


    private JProgressBar progress = null;
    private JTextArea txtReport = null;
    private JLabel lblStatus = null;

    /**
     * Explicit default constructor.
     */
    public DescriptiveNumbersTool() {
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
        // Get the PROTOCOL type.
        this.getProtocol();
        // Construct the GUI.
        this.constructScreen();
        this.pack();
        this.setTitle("Descriptive numbers tool for project " + aProject.getProjectid() + " (connected to '" + iDBName + "')");
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
        if (this.getState() == Frame.ICONIFIED) {
            this.setState(Frame.NORMAL);
        }
        this.requestFocus();
    }

    /**
     * Get the protocol entry from the database for this project.
     */
    private void getProtocol() {
        try {
            HashMap keys = new HashMap(1);
            keys.put(Protocol.PROTOCOLID, new Long(iProject.getL_protocolid()));
            iProtocol = new Protocol();
            iProtocol.retrieve(iConnection, keys);
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, new String[]{"Unable to load instruments from the database: " + sqle.getMessage(), "Exiting tool."}, "Unable to load instruments.", JOptionPane.ERROR_MESSAGE);
            this.close();
        }
    }

    /**
     * This method returns the button panel.
     *
     * @return JPanel with the buttons.
     */
    private JPanel getButtonPanel() {
        JButton btnStartReport = new JButton("Generate report");
        btnStartReport.setMnemonic(KeyEvent.VK_G);
        btnStartReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executeReportTriggered();
            }
        });
        btnStartReport.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyTyped(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    executeReportTriggered();
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
        jpanButtons.add(btnStartReport);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnExit);
        jpanButtons.add(Box.createHorizontalStrut(15));

        return jpanButtons;
    }

    /**
     * This method is called when the user clicks the 'execute query button'.
     */
    private void executeReportTriggered() {
        final ProjectReporter reporter = new ProjectReporter(iProject, iProtocol, iConnection);
        progress.setMinimum(0);
        progress.setMaximum(reporter.getNumberOfQueries() + 1);
        progress.setString("Starting up report generation...");
        progress.setIndeterminate(true);
        final long startMillis = System.currentTimeMillis();
        final SwingWorker queryWorker = new SwingWorker() {
            public Object construct() {
                Object result = null;
                try {
                    result = reporter.getReport(progress);
                } catch (SQLException e) {
                    result = e;
                }
                return result;
            }

            public void finished() {
                queryCompleted(this, startMillis);
            }
        };
        queryWorker.start();
    }

    /**
     * This method will be called by the SwingWorker whenever the query completes.
     *
     * @param aQuery       SwingWorker that's handled the query. Since it is calling us, the query is now complete.
     * @param aStartMillis long with the original starting time of the query in milliseconds.
     */
    private void queryCompleted(SwingWorker aQuery, long aStartMillis) {
        if (progress.isIndeterminate()) {
            progress.setIndeterminate(false);
        }
        progress.setValue(progress.getMinimum());
        try {
            Object temp = aQuery.get();
            if (temp instanceof String) {
                long endMillis = System.currentTimeMillis();
                double totalTime = 0.0;
                boolean inSeconds = false;
                totalTime = endMillis - aStartMillis;
                if (totalTime > 1000) {
                    totalTime /= 1000.0;
                    inSeconds = true;
                }
                progress.setValue(progress.getMaximum());
                String duration = new BigDecimal(totalTime).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + (inSeconds ? " seconds" : " milliseconds");
                lblStatus.setForeground(this.getForeground());
                lblStatus.setText("Report generated (generation took " + duration + ").");
                txtReport.setText((String) temp);
                if (txtReport.getText().length() > 1) {
                    txtReport.setCaretPosition(1);
                }
                progress.setString("Report generated (" + duration + ")!");
            } else if (temp instanceof SQLException) {
                throw (SQLException) temp;
            }
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            JOptionPane.showMessageDialog(this, new String[]{"Unfortunately, your report failed, (see below for the error): ", sqle.getMessage()}, "Report failed!", JOptionPane.ERROR_MESSAGE);
            lblStatus.setForeground(Color.red);
            lblStatus.setText("Report failed.");
            progress.setString("Report failed");
        } finally {
            progress.setValue(progress.getMinimum());
        }
    }

    /**
     * This method initializes the components and lays them out on the screen.
     */
    private void constructScreen() {
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
        jpanButtons.setBorder(BorderFactory.createTitledBorder("Report generation"));

        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(Box.createVerticalStrut(15));
        jpanMain.add(jpanButtons);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanProgress);
        jpanMain.add(Box.createVerticalGlue());


        txtReport = new JTextArea(30, 80);
        txtReport.setEditable(false);
        lblStatus = new JLabel("");
        JPanel jpanReport = new JPanel();
        jpanReport.setLayout(new BorderLayout());
        jpanReport.setBorder(BorderFactory.createTitledBorder("Project report"));
        jpanReport.add(new JScrollPane(txtReport), BorderLayout.CENTER);
        jpanReport.add(lblStatus, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jpanMain, jpanReport);

        this.getContentPane().add(splitPane, BorderLayout.CENTER);
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


    public boolean isStandAlone() {
        return false;
    }
}
