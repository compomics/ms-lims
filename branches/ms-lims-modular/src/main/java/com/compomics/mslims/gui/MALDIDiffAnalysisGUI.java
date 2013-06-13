/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 4-okt-2005
 * Time: 10:27:41
 */
package com.compomics.mslims.gui;

import org.apache.log4j.Logger;

import com.compomics.util.gui.FlamableJFrame;
import com.compomics.util.interfaces.Flamable;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.gui.dialogs.MALDIStatisticsResultsDialog;
import com.compomics.mslims.util.workers.MALDIDiffAnalysisWorker;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.Iterator;
import java.util.ArrayList;
/*
 * CVS information:
 *
 * $Revision: 1.7 $
 * $Date: 2007/04/03 11:32:43 $
 */

/**
 * This class will handle the differential analysis of a MALDI LC-MS analysis of a sample. It also allows the export of
 * Q-TOF / Esquire inclusion lists based on the significant peaks.
 *
 * @author Lennart Martens
 * @version $Id: MALDIDiffAnalysisGUI.java,v 1.7 2007/04/03 11:32:43 lennart Exp $
 */
public class MALDIDiffAnalysisGUI extends FlamableJFrame {
    // Class specific log4j logger for MALDIDiffAnalysisGUI instances.
    private static Logger logger = Logger.getLogger(MALDIDiffAnalysisGUI.class);


    private JRadioButton rbtFileInput = null;
    private JTextField txtInputFile = null;
    private JButton btnBrowseInputFile = null;

    private JRadioButton rbtFolderInput = null;
    private JTextField txtInputFolder = null;
    private JButton btnBrowseInputFolder = null;

    private JRadioButton rbtStatistics = null;
    private JRadioButton rbtInclusion = null;

    private JComboBox cmbCalibration = null;

    private JRadioButton rbtIntensity = null;
    private JRadioButton rbtArea = null;

    private JRadioButton rbt95Conf = null;
    private JRadioButton rbt98Conf = null;

    private JCheckBox chkS2nThreshold = null;
    private JTextField txtS2nThreshold = null;

    private JRadioButton rbtQTOF = null;
    private JRadioButton rbtEsquire = null;

    private JTextField txtConeVoltage = null;
    private JTextField txtCollisionEnergy = null;

    private JTextField txtOutputFolder = null;
    private JButton btnBrowseOutput = null;

    private JButton btnAction = null;


    private InnerMALDICalibration[] iCalibrations = null;

    /**
     * This constructor takes a title for this frame.
     */
    public MALDIDiffAnalysisGUI(String aTitle) {
        super(aTitle);
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        this.readCalibrations();
        this.constructScreen();
        this.pack();
    }

    /**
     * This method closes the frame and ends the JVM with exit flag set to '0'.
     */
    public void close() {
        this.setVisible(false);
        this.dispose();
        System.exit(0);
    }

    public static void main(String[] args) {
        MALDIDiffAnalysisGUI mdag = new MALDIDiffAnalysisGUI("Differential analysis for MALDI LC-MS");
        mdag.setLocation(100, 100);
        mdag.setVisible(true);
    }

    /**
     * This method initialized and lays out the components on the screen.
     */
    private void constructScreen() {
        // First some labels (note the width equalling).
        final JLabel lblCollision = new JLabel("Collision energy: ");
        lblCollision.setMaximumSize(new Dimension(lblCollision.getMaximumSize().width, lblCollision.getPreferredSize().height));
        final JLabel lblOutputFolder = new JLabel("Output folder: ");
        lblOutputFolder.setPreferredSize(new Dimension(lblCollision.getPreferredSize().width, lblCollision.getPreferredSize().height));
        lblOutputFolder.setMaximumSize(new Dimension(lblOutputFolder.getMaximumSize().width, lblOutputFolder.getPreferredSize().height));
        final JLabel lblInputFolder = new JLabel("Input folder: ");
        lblInputFolder.setPreferredSize(new Dimension(lblCollision.getPreferredSize().width, lblInputFolder.getPreferredSize().height));
        lblInputFolder.setMaximumSize(new Dimension(lblInputFolder.getMaximumSize().width, lblInputFolder.getPreferredSize().height));
        final JLabel lblInputFile = new JLabel("Input file: ");
        lblInputFile.setPreferredSize(new Dimension(lblCollision.getPreferredSize().width, lblInputFile.getPreferredSize().height));
        lblInputFile.setMaximumSize(new Dimension(lblInputFile.getMaximumSize().width, lblInputFile.getPreferredSize().height));
        final JLabel lblConeVoltage = new JLabel("Cone voltage: ");
        lblConeVoltage.setPreferredSize(new Dimension(lblCollision.getPreferredSize().width, lblConeVoltage.getPreferredSize().height));
        lblConeVoltage.setMaximumSize(new Dimension(lblConeVoltage.getMaximumSize().width, lblConeVoltage.getPreferredSize().height));

        // Radiobuttons. Also equalized in width.
        rbtStatistics = new JRadioButton("Statistical analysis");
        rbtInclusion = new JRadioButton("Generate inclusion lists");
        rbtIntensity = new JRadioButton("Use peak intensity");
        rbtIntensity.setPreferredSize(new Dimension(rbtStatistics.getPreferredSize().width, rbtIntensity.getPreferredSize().height));
        rbtArea = new JRadioButton("Use peak area");
        rbtArea.setPreferredSize(new Dimension(rbtInclusion.getPreferredSize().width, rbtArea.getPreferredSize().height));
        rbtQTOF = new JRadioButton("Q-TOF");
        rbtQTOF.setPreferredSize(new Dimension(rbtStatistics.getPreferredSize().width, rbtQTOF.getPreferredSize().height));
        rbtEsquire = new JRadioButton("Esquire");
        rbtEsquire.setPreferredSize(new Dimension(rbtInclusion.getPreferredSize().width, rbtEsquire.getPreferredSize().height));
        rbt95Conf = new JRadioButton("95% confidence");
        rbt95Conf.setPreferredSize(new Dimension(rbtStatistics.getPreferredSize().width, rbt95Conf.getPreferredSize().height));
        rbt98Conf = new JRadioButton("98% confidence");
        rbt98Conf.setPreferredSize(new Dimension(rbtInclusion.getPreferredSize().width, rbt98Conf.getPreferredSize().height));
        rbtFileInput = new JRadioButton("File input");
        rbtFileInput.setPreferredSize(new Dimension(rbtStatistics.getPreferredSize().width, rbtFileInput.getPreferredSize().height));
        rbtFolderInput = new JRadioButton("Folder input");
        rbtFolderInput.setPreferredSize(new Dimension(rbtInclusion.getPreferredSize().width, rbtFolderInput.getPreferredSize().height));

        // The button panel.
        JPanel jpanButtons = this.getButtonPanel();
        jpanButtons.setMaximumSize(new Dimension(jpanButtons.getMaximumSize().width, jpanButtons.getPreferredSize().height));

        // The file input components.
        txtInputFile = new JTextField(35);
        txtInputFile.setMaximumSize(new Dimension(txtInputFile.getMaximumSize().width, txtInputFile.getPreferredSize().height));
        btnBrowseInputFile = new JButton("Browse...");
        btnBrowseInputFile.setMnemonic(KeyEvent.VK_B);
        btnBrowseInputFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseFileInputPressed();
            }
        });
        // The file input panel
        JPanel jpanFileInput = new JPanel();
        jpanFileInput.setLayout(new BoxLayout(jpanFileInput, BoxLayout.X_AXIS));
        jpanFileInput.setBorder(BorderFactory.createTitledBorder("File input"));
        jpanFileInput.add(Box.createHorizontalStrut(15));
        jpanFileInput.add(lblInputFile);
        jpanFileInput.add(Box.createHorizontalStrut(5));
        jpanFileInput.add(txtInputFile);
        jpanFileInput.add(Box.createHorizontalStrut(5));
        jpanFileInput.add(btnBrowseInputFile);
        jpanFileInput.add(Box.createHorizontalGlue());
        jpanFileInput.setMaximumSize(new Dimension(jpanFileInput.getMaximumSize().width, jpanFileInput.getPreferredSize().height));

        // The folder input components.
        txtInputFolder = new JTextField(35);
        txtInputFolder.setMaximumSize(new Dimension(txtInputFolder.getMaximumSize().width, txtInputFolder.getPreferredSize().height));
        btnBrowseInputFolder = new JButton("Browse...");
        btnBrowseInputFolder.setMnemonic(KeyEvent.VK_B);
        btnBrowseInputFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseFolderInputPressed();
            }
        });
        // The folder input panel
        JPanel jpanFolderInput = new JPanel();
        jpanFolderInput.setLayout(new BoxLayout(jpanFolderInput, BoxLayout.X_AXIS));
        jpanFolderInput.setBorder(BorderFactory.createTitledBorder("Folder input"));
        jpanFolderInput.add(Box.createHorizontalStrut(15));
        jpanFolderInput.add(lblInputFolder);
        jpanFolderInput.add(Box.createHorizontalStrut(5));
        jpanFolderInput.add(txtInputFolder);
        jpanFolderInput.add(Box.createHorizontalStrut(5));
        jpanFolderInput.add(btnBrowseInputFolder);
        jpanFolderInput.add(Box.createHorizontalGlue());
        jpanFolderInput.setMaximumSize(new Dimension(jpanFolderInput.getMaximumSize().width, jpanFolderInput.getPreferredSize().height));

        // Input select radio button group.
        ButtonGroup bgInput = new ButtonGroup();
        bgInput.add(rbtFileInput);
        bgInput.add(rbtFolderInput);
        rbtFileInput.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtFileInput.isSelected()) {
                    lblInputFolder.setEnabled(false);
                    txtInputFolder.setEnabled(false);
                    btnBrowseInputFolder.setEnabled(false);
                    lblInputFile.setEnabled(true);
                    txtInputFile.setEnabled(true);
                    btnBrowseInputFile.setEnabled(true);
                    txtInputFile.requestFocus();
                }
            }
        });
        rbtFolderInput.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtFolderInput.isSelected()) {
                    lblInputFolder.setEnabled(true);
                    txtInputFolder.setEnabled(true);
                    btnBrowseInputFolder.setEnabled(true);
                    lblInputFile.setEnabled(false);
                    txtInputFile.setEnabled(false);
                    btnBrowseInputFile.setEnabled(false);
                    txtInputFolder.requestFocus();
                }
            }
        });
        rbtFileInput.setSelected(true);

        // Input select panel.
        JPanel jpanSelectInput = new JPanel();
        jpanSelectInput.setLayout(new BoxLayout(jpanSelectInput, BoxLayout.X_AXIS));
        jpanSelectInput.add(Box.createHorizontalGlue());
        jpanSelectInput.add(rbtFileInput);
        jpanSelectInput.add(Box.createHorizontalStrut(30));
        jpanSelectInput.add(rbtFolderInput);
        jpanSelectInput.add(Box.createHorizontalGlue());
        jpanSelectInput.setMaximumSize(new Dimension(jpanSelectInput.getMaximumSize().width, jpanSelectInput.getPreferredSize().height));

        // The whole input panel.
        JPanel jpanInput = new JPanel();
        jpanInput.setLayout(new BoxLayout(jpanInput, BoxLayout.Y_AXIS));
        jpanInput.setBorder(BorderFactory.createTitledBorder("Data input selection"));
        jpanInput.add(jpanSelectInput);
        jpanInput.add(Box.createVerticalStrut(5));
        jpanInput.add(jpanFileInput);
        jpanInput.add(Box.createVerticalStrut(5));
        jpanInput.add(jpanFolderInput);

        // The intensity/area components.
        ButtonGroup bgIntArea = new ButtonGroup();
        bgIntArea.add(rbtIntensity);
        bgIntArea.add(rbtArea);
        rbtArea.setSelected(true);
        JPanel jpanPeak = new JPanel();
        jpanPeak.setLayout(new BoxLayout(jpanPeak, BoxLayout.X_AXIS));
        jpanPeak.setBorder(BorderFactory.createTitledBorder("Ratio calculation input"));
        jpanPeak.add(Box.createHorizontalGlue());
        jpanPeak.add(rbtIntensity);
        jpanPeak.add(Box.createHorizontalStrut(30));
        jpanPeak.add(rbtArea);
        jpanPeak.add(Box.createHorizontalGlue());

        // The calibration selection component.
        cmbCalibration = new JComboBox(iCalibrations);
        cmbCalibration.setMaximumSize(new Dimension(cmbCalibration.getPreferredSize().width, cmbCalibration.getPreferredSize().height));
        JPanel jpanCalibration = new JPanel();
        jpanCalibration.setBorder(BorderFactory.createTitledBorder("Select instrument calibration"));
        jpanCalibration.add(cmbCalibration);

        // The confidence interval components.
        rbt95Conf.setSelected(true);
        ButtonGroup bgConfidence = new ButtonGroup();
        bgConfidence.add(rbt95Conf);
        bgConfidence.add(rbt98Conf);
        // The confidence interval panel.
        JPanel jpanConfidence = new JPanel();
        jpanConfidence.setLayout(new BoxLayout(jpanConfidence, BoxLayout.X_AXIS));
        jpanConfidence.setBorder(BorderFactory.createTitledBorder("Confidence level selection"));
        jpanConfidence.add(Box.createHorizontalGlue());
        jpanConfidence.add(rbt95Conf);
        jpanConfidence.add(Box.createHorizontalStrut(30));
        jpanConfidence.add(rbt98Conf);
        jpanConfidence.add(Box.createHorizontalGlue());
        jpanConfidence.setMaximumSize(new Dimension(jpanConfidence.getMaximumSize().width, jpanConfidence.getPreferredSize().height));

        // The signal-to-noise filter components.
        txtS2nThreshold = new JTextField(5);
        txtS2nThreshold.setMaximumSize(new Dimension(txtS2nThreshold.getPreferredSize().width, txtS2nThreshold.getPreferredSize().height));
        chkS2nThreshold = new JCheckBox("Use signal-to-noise filtering at a ratio of ");
        chkS2nThreshold.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (chkS2nThreshold.isSelected()) {
                    txtS2nThreshold.setEnabled(true);
                } else {
                    txtS2nThreshold.setEnabled(false);
                }
            }
        });

        // The signal-to-noise filter panel.
        JPanel jpanS2n = new JPanel();
        jpanS2n.setLayout(new BoxLayout(jpanS2n, BoxLayout.X_AXIS));
        jpanS2n.setBorder(BorderFactory.createTitledBorder("Signal-to-noise filter"));
        jpanS2n.add(Box.createHorizontalStrut(15));
        jpanS2n.add(chkS2nThreshold);
        jpanS2n.add(Box.createHorizontalStrut(5));
        jpanS2n.add(txtS2nThreshold);
        jpanS2n.add(Box.createHorizontalGlue());
        jpanS2n.setMaximumSize(new Dimension(jpanS2n.getMaximumSize().width, jpanS2n.getPreferredSize().height));

        // The output folder components
        txtOutputFolder = new JTextField(35);
        txtOutputFolder.setMaximumSize(new Dimension(txtOutputFolder.getMaximumSize().width, txtOutputFolder.getPreferredSize().height));
        btnBrowseOutput = new JButton("Browse...");
        btnBrowseOutput.setMnemonic(KeyEvent.VK_R);
        btnBrowseOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseOutputPressed();
            }
        });
        // The output folder panel
        JPanel jpanFolderOutput = new JPanel();
        jpanFolderOutput.setLayout(new BoxLayout(jpanFolderOutput, BoxLayout.X_AXIS));
        jpanFolderOutput.setBorder(BorderFactory.createTitledBorder("Output folder"));
        jpanFolderOutput.add(Box.createHorizontalStrut(15));
        jpanFolderOutput.add(lblOutputFolder);
        jpanFolderOutput.add(Box.createHorizontalStrut(5));
        jpanFolderOutput.add(txtOutputFolder);
        jpanFolderOutput.add(Box.createHorizontalStrut(5));
        jpanFolderOutput.add(btnBrowseOutput);
        jpanFolderOutput.add(Box.createHorizontalGlue());
        jpanFolderOutput.setMaximumSize(new Dimension(jpanFolderOutput.getMaximumSize().width, jpanFolderOutput.getPreferredSize().height));

        // The cone voltage and collision energy settings for the Q-TOF.
        txtConeVoltage = new JTextField(10);
        txtCollisionEnergy = new JTextField(10);
        txtConeVoltage.setMaximumSize(new Dimension(txtConeVoltage.getPreferredSize().width, txtConeVoltage.getPreferredSize().height));
        txtCollisionEnergy.setMaximumSize(new Dimension(txtCollisionEnergy.getPreferredSize().width, txtCollisionEnergy.getPreferredSize().height));

        // Panel for the cone voltage.
        JPanel jpanConeVoltage = new JPanel();
        jpanConeVoltage.setLayout(new BoxLayout(jpanConeVoltage, BoxLayout.X_AXIS));
        jpanConeVoltage.add(Box.createHorizontalStrut(15));
        jpanConeVoltage.add(lblConeVoltage);
        jpanConeVoltage.add(Box.createHorizontalStrut(5));
        jpanConeVoltage.add(txtConeVoltage);
        jpanConeVoltage.add(Box.createHorizontalGlue());

        // Panel for the collision energy.
        JPanel jpanCollision = new JPanel();
        jpanCollision.setLayout(new BoxLayout(jpanCollision, BoxLayout.X_AXIS));
        jpanCollision.add(Box.createHorizontalStrut(15));
        jpanCollision.add(lblCollision);
        jpanCollision.add(Box.createHorizontalStrut(5));
        jpanCollision.add(txtCollisionEnergy);
        jpanCollision.add(Box.createHorizontalGlue());

        // Panel for the Q-TOF settings.
        JPanel jpanQTOF = new JPanel();
        jpanQTOF.setLayout(new BoxLayout(jpanQTOF, BoxLayout.Y_AXIS));
        jpanQTOF.setBorder(BorderFactory.createTitledBorder("Q-TOF specific settings"));
        jpanQTOF.add(jpanConeVoltage);
        jpanQTOF.add(Box.createVerticalStrut(5));
        jpanQTOF.add(jpanCollision);
        jpanQTOF.setMaximumSize(new Dimension(jpanQTOF.getMaximumSize().width, jpanQTOF.getPreferredSize().height));

        // The instrument selection radiobuttons.
        rbtQTOF.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtQTOF.isSelected() && rbtInclusion.isSelected()) {
                    txtConeVoltage.setEnabled(true);
                    txtCollisionEnergy.setEnabled(true);
                }
            }
        });
        rbtQTOF.setSelected(true);
        rbtEsquire.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtEsquire.isSelected()) {
                    txtConeVoltage.setEnabled(false);
                    txtCollisionEnergy.setEnabled(false);
                }
            }
        });
        ButtonGroup bgInstruments = new ButtonGroup();
        bgInstruments.add(rbtQTOF);
        bgInstruments.add(rbtEsquire);

        JPanel jpanInstruments = new JPanel();
        jpanInstruments.setLayout(new BoxLayout(jpanInstruments, BoxLayout.X_AXIS));
        jpanInstruments.setBorder(BorderFactory.createTitledBorder("Instrument selection for inclusion list"));
        jpanInstruments.add(Box.createHorizontalGlue());
        jpanInstruments.add(rbtQTOF);
        jpanInstruments.add(Box.createHorizontalStrut(30));
        jpanInstruments.add(rbtEsquire);
        jpanInstruments.add(Box.createHorizontalGlue());
        jpanInstruments.setMaximumSize(new Dimension(jpanInstruments.getMaximumSize().width, jpanInstruments.getPreferredSize().height));

        // The panel that will be hold the components for inclusion lists output.
        final JPanel jpanOutput = new JPanel();
        jpanOutput.setLayout(new BoxLayout(jpanOutput, BoxLayout.Y_AXIS));
        jpanOutput.setBorder(BorderFactory.createTitledBorder("Inclusion list output"));
        jpanOutput.add(jpanConfidence);
        jpanOutput.add(Box.createVerticalStrut(5));
        jpanOutput.add(jpanS2n);
        jpanOutput.add(Box.createVerticalStrut(5));
        jpanOutput.add(jpanInstruments);
        jpanOutput.add(Box.createVerticalStrut(5));
        jpanOutput.add(jpanQTOF);
        jpanOutput.add(Box.createVerticalStrut(5));
        jpanOutput.add(jpanFolderOutput);

        // The process selection components.
        rbtStatistics.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtStatistics.isSelected()) {
                    rbt95Conf.setEnabled(false);
                    rbt98Conf.setEnabled(false);
                    chkS2nThreshold.setEnabled(false);
                    txtS2nThreshold.setEnabled(false);
                    txtOutputFolder.setEnabled(false);
                    lblOutputFolder.setEnabled(false);
                    btnBrowseOutput.setEnabled(false);
                    rbtQTOF.setEnabled(false);
                    rbtEsquire.setEnabled(false);
                    txtConeVoltage.setEnabled(false);
                    lblConeVoltage.setEnabled(false);
                    txtCollisionEnergy.setEnabled(false);
                    lblCollision.setEnabled(false);
                    btnAction.setText("Analyze");
                }
            }
        });
        rbtStatistics.setMaximumSize(new Dimension(rbtStatistics.getMaximumSize().width, rbtStatistics.getPreferredSize().height));
        rbtStatistics.setSelected(true);
        rbtInclusion.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (rbtInclusion.isSelected()) {
                    rbt95Conf.setEnabled(true);
                    rbt98Conf.setEnabled(true);
                    chkS2nThreshold.setEnabled(true);
                    if (chkS2nThreshold.isSelected()) {
                        txtS2nThreshold.setEnabled(true);
                    }
                    txtOutputFolder.setEnabled(true);
                    lblOutputFolder.setEnabled(true);
                    btnBrowseOutput.setEnabled(true);
                    rbtQTOF.setEnabled(true);
                    rbtEsquire.setEnabled(true);
                    if (rbtQTOF.isSelected()) {
                        txtConeVoltage.setEnabled(true);
                        lblConeVoltage.setEnabled(true);
                        txtCollisionEnergy.setEnabled(true);
                        lblCollision.setEnabled(true);
                    }
                    btnAction.setText("Generate lists");
                }
            }
        });
        rbtInclusion.setMaximumSize(new Dimension(rbtInclusion.getMaximumSize().width, rbtInclusion.getPreferredSize().height));
        // The button group.
        ButtonGroup bgProcSelect = new ButtonGroup();
        bgProcSelect.add(rbtStatistics);
        bgProcSelect.add(rbtInclusion);
        // The panel for the process selection.
        JPanel jpanProcSelect = new JPanel();
        jpanProcSelect.setLayout(new BoxLayout(jpanProcSelect, BoxLayout.X_AXIS));
        jpanProcSelect.setBorder(BorderFactory.createTitledBorder("Select processing"));
        jpanProcSelect.add(Box.createHorizontalGlue());
        jpanProcSelect.add(rbtStatistics);
        jpanProcSelect.add(Box.createHorizontalStrut(30));
        jpanProcSelect.add(rbtInclusion);
        jpanProcSelect.add(Box.createHorizontalGlue());
        jpanProcSelect.setMaximumSize(new Dimension(jpanProcSelect.getMaximumSize().width, jpanProcSelect.getPreferredSize().height));

        // Create and add the main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanInput);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanProcSelect);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanPeak);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanCalibration);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanOutput);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanButtons);
        jpanMain.add(Box.createVerticalStrut(5));
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method creates the button panel for this application.
     *
     * @return JPanel with the buttons.
     */
    private JPanel getButtonPanel() {
        // The buttons.
        btnAction = new JButton("Analyze");
        btnAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionPressed();
            }
        });
        btnAction.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionPressed();
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
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    close();
                }
            }
        });

        // The panel.
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnAction);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnExit);
        jpanButtons.add(Box.createHorizontalStrut(15));

        return jpanButtons;
    }

    /**
     * This method is called whenever the user presses the 'browse' button for the file input field.
     */
    private void browseFileInputPressed() {
        // File dialog for input, taking into account the starting
        // folder in the 'txtInputFile', if correct. Otherwise, use the
        // system root.
        File startHere = new File("/");
        if (!txtInputFile.getText().trim().equals("")) {
            File f = new File(txtInputFile.getText().trim());
            if (f.exists()) {
                startHere = f;
            }
        }
        // Filechooser that only shows folders and xml files.
        JFileChooser jfc = new JFileChooser(startHere);
        int returnVal = 0;
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                boolean result = false;
                if (f.isDirectory() || f.getName().endsWith(".xml") || f.getName().endsWith(".XML")) {
                    result = true;
                }
                return result;
            }

            public String getDescription() {
                return "XML files";
            }
        });
        returnVal = jfc.showOpenDialog(txtInputFile);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txtInputFile.setText(jfc.getSelectedFile().getAbsoluteFile().toString());
        }
    }

    /**
     * This method is called whenever the user presses the 'browse' button for the folder input field.
     */
    private void browseFolderInputPressed() {
        // File dialog for input, taking into account the starting
        // folder in the 'txtInputFolder', if correct. Otherwise, use the
        // system root.
        File startHere = new File("/");
        if (!txtInputFolder.getText().trim().equals("")) {
            File f = new File(txtInputFolder.getText().trim());
            if (f.exists()) {
                startHere = f;
            }
        }
        // Filechooser that only shows folders and xml files.
        JFileChooser jfc = new JFileChooser(startHere);
        int returnVal = 0;
        jfc.setDialogType(JFileChooser.CUSTOM_DIALOG);
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        returnVal = jfc.showDialog(txtInputFolder, "Select folder");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txtInputFolder.setText(jfc.getSelectedFile().getAbsoluteFile().toString());
        }
    }

    /**
     * This method is called whenever the user presses the 'browse' button for the output field.
     */
    private void browseOutputPressed() {
        // File dialog for output, taking into account the starting
        // folder in the 'txtOutputFolder', if correct. Otherwise, use the
        // system root.
        File startHere = new File("/");
        if (!txtOutputFolder.getText().trim().equals("")) {
            File f = new File(txtOutputFolder.getText().trim());
            if (f.exists()) {
                startHere = f;
            }
        }
        // Filechooser that only shows folders and xml files.
        JFileChooser jfc = new JFileChooser(startHere);
        int returnVal = 0;
        jfc.setDialogType(JFileChooser.CUSTOM_DIALOG);
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        returnVal = jfc.showDialog(txtOutputFolder, "Select output folder");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            txtOutputFolder.setText(jfc.getSelectedFile().getAbsoluteFile().toString());
        }
    }

    /**
     * This method is called when someone presses the main action button.
     */
    private void actionPressed() {
        File input = null;
        if (rbtFileInput.isSelected()) {
            // Shared check for inputfile.
            String inputFile = txtInputFile.getText();
            if (inputFile == null || inputFile.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "Please specify an input file to open!", "No file specified", JOptionPane.WARNING_MESSAGE);
                txtInputFile.requestFocus();
                return;
            }
            // See if the file exists.
            input = new File(inputFile);
            if (!input.exists()) {
                JOptionPane.showMessageDialog(this, "Could not find the file you specified: '" + inputFile + "'!", "File not found!", JOptionPane.WARNING_MESSAGE);
                txtInputFile.requestFocus();
                return;
            }
            // See whether it is not a directory.
            if (input.isDirectory()) {
                JOptionPane.showMessageDialog(this, "The file you specified: '" + inputFile + "' is a folder, not a file!", "Folder specified instead of file!", JOptionPane.WARNING_MESSAGE);
                txtInputFile.requestFocus();
                return;
            }
        } else if (rbtFolderInput.isSelected()) {
            // Shared check for inputfolder.
            String inputFile = txtInputFolder.getText();
            if (inputFile == null || inputFile.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "Please specify an input folder to open!", "No folder specified", JOptionPane.WARNING_MESSAGE);
                txtInputFolder.requestFocus();
                return;
            }
            // See if the file exists.
            input = new File(inputFile);
            if (!input.exists()) {
                JOptionPane.showMessageDialog(this, "Could not find the folder you specified: '" + inputFile + "'!", "Folder not found!", JOptionPane.WARNING_MESSAGE);
                txtInputFolder.requestFocus();
                return;
            }
            // See whether it is not a directory.
            if (!input.isDirectory()) {
                JOptionPane.showMessageDialog(this, "The folder you specified: '" + inputFile + "' is a file, not a folder!", "File specified instead of folder!", JOptionPane.WARNING_MESSAGE);
                txtInputFolder.requestFocus();
                return;
            }
        }
        // Find the correct name for the project.
        String name = null;
        if (input.isDirectory()) {
            // For a folder, take the folder name.
            name = input.getName();
        } else {
            // For a file, take the name of the parent folder.
            name = input.getParentFile().getName();
        }
        // See whether we should use area or intensity.
        boolean area = true;
        if (rbtIntensity.isSelected()) {
            area = false;
        }
        // Find the calibration.
        InnerMALDICalibration calib = (InnerMALDICalibration) cmbCalibration.getSelectedItem();
        double calibration = calib.getCalibration();
        logger.info("Selected calibration: " + calibration);
        // Forward to the correct action.
        if (rbtStatistics.isSelected()) {
            doStatistics(input, name, area, calibration);
        } else if (rbtInclusion.isSelected()) {
            doInclusionLists(input, name, area, calibration);
        }
    }

    /**
     * This method will be called when the robust statistics need to be calculated.
     *
     * @param aInput       File with the input file.
     * @param aName        String with the name for the analysis.
     * @param aUseArea     boolean that indicates whether to use compound area ('true') or intensity ('false') for the
     *                     ratio calculation.
     * @param aCalibration double with the instrument calibration.
     */
    private void doStatistics(File aInput, String aName, boolean aUseArea, double aCalibration) {
        MALDIDiffAnalysisWorker results = this.doCompoundProcessing("Analyzing '" + aInput.getName() + "'...", this, aName, aInput, aUseArea, aCalibration, null, -1, 0.0, 0, 0, 0);
        // Okay, in getting here, all the work should be done.
        // Visualize some results for the user.
        showResults(results, false);
    }

    /**
     * This method will be called when the inclusion lists need to be generated.
     *
     * @param aInput       File with the input file.
     * @param aName        String with the name for the analysis.
     * @param aUseArea     boolean that indicates whether to use compound area ('true') or intensity ('false') for the
     *                     ratio calculation.
     * @param aCalibration double with the instrument calibration.
     */
    private void doInclusionLists(File aInput, String aName, boolean aUseArea, double aCalibration) {
        // Check the confidence interval.
        int confidence = -1;
        if (rbt95Conf.isSelected()) {
            confidence = MALDIDiffAnalysisWorker.CONFIDENCE_95;
        } else if (rbt98Conf.isSelected()) {
            confidence = MALDIDiffAnalysisWorker.CONFIDENCE_98;
        }
        // Check the signal-to-noise threshold.
        double s2nThreshold = -1.0;
        if (chkS2nThreshold.isSelected()) {
            String s2nString = txtS2nThreshold.getText();
            if (s2nString == null || s2nString.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "You need to specify a signal-to-noise threshold!", "No signal-to-noise threshold specified!", JOptionPane.WARNING_MESSAGE);
                txtS2nThreshold.requestFocus();
                return;
            }
            try {
                s2nThreshold = Double.parseDouble(s2nString);
                if (s2nThreshold <= 0.0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "You need to specify a positive, non-zero decimal number!", "Incorrect signal-to-noise threshold specified!", JOptionPane.WARNING_MESSAGE);
                txtS2nThreshold.requestFocus();
                return;
            }
        }
        // Check the instrument selection.
        int instrument = -1;
        if (rbtQTOF.isSelected()) {
            instrument = MALDIDiffAnalysisWorker.QTOF;
        } else if (rbtEsquire.isSelected()) {
            instrument = MALDIDiffAnalysisWorker.ESQUIRE;
        }
        // Check the additional fields and gather data.
        int coneVoltage = -1;
        int collisionEnergy = -1;
        if (rbtQTOF.isSelected()) {
            String text = txtConeVoltage.getText();
            if (text == null || text.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "You need to specify a cone voltage for the Q-TOF!", "No cone voltage specified!", JOptionPane.WARNING_MESSAGE);
                txtConeVoltage.requestFocus();
                return;
            }
            try {
                coneVoltage = Integer.parseInt(text);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "The cone voltage must be a non-zero, positive whole number!", "Incorrect cone voltage specified!", JOptionPane.WARNING_MESSAGE);
                txtConeVoltage.requestFocus();
                return;
            }
            if (coneVoltage <= 0) {
                JOptionPane.showMessageDialog(this, "The cone voltage must be a non-zero, positive whole number!", "Incorrect cone voltage specified!", JOptionPane.WARNING_MESSAGE);
                txtConeVoltage.requestFocus();
                return;
            }
            // Collision energy.
            text = txtCollisionEnergy.getText();
            if (text == null || text.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "You need to specify a collision energy for the Q-TOF!", "No collision energy specified!", JOptionPane.WARNING_MESSAGE);
                txtCollisionEnergy.requestFocus();
                return;
            }
            try {
                collisionEnergy = Integer.parseInt(text);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "The collision energy must be a non-zero, positive whole number!", "Incorrect collision energy specified!", JOptionPane.WARNING_MESSAGE);
                txtCollisionEnergy.requestFocus();
                return;
            }
            if (collisionEnergy <= 0) {
                JOptionPane.showMessageDialog(this, "The collision energy must be a non-zero, positive whole number!", "Incorrect collision energy specified!", JOptionPane.WARNING_MESSAGE);
                txtCollisionEnergy.requestFocus();
                return;
            }
        }
        // Output folder checks.
        String outString = txtOutputFolder.getText();
        if (outString == null || outString.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "You need to specify an output folder for the inclusion lists!", "No output folder specified!", JOptionPane.WARNING_MESSAGE);
            txtOutputFolder.requestFocus();
            return;
        }
        File output = new File(outString);
        if (!output.exists()) {
            JOptionPane.showMessageDialog(this, "The output folder you specified does not exist!", "Output folder does not exist!", JOptionPane.WARNING_MESSAGE);
            txtOutputFolder.requestFocus();
            return;
        }
        if (!output.isDirectory()) {
            JOptionPane.showMessageDialog(this, "The output location you specified is not a folder!", "Output location is not a folder!", JOptionPane.WARNING_MESSAGE);
            txtOutputFolder.requestFocus();
            return;
        }
        // Do the actual processing.
        MALDIDiffAnalysisWorker results = this.doCompoundProcessing("Generating inclusion lists for '" + aInput.getName() + "'...", this, aName, aInput, aUseArea, aCalibration, output, confidence, s2nThreshold, instrument, coneVoltage, collisionEnergy);
        // Okay, in getting here, all the work should be done.
        // Visualize some results for the user.
        showResults(results, true);
    }

    /**
     * This method actually makes sure the required data is read and parsed.
     *
     * @param aProgressBarTitle String with a title for the progressbar that will be displayed.
     * @param aWorkerParent     Flamable with the parent for the actual workerthread.
     * @param aProcessingName   String with a name for the processing (project name for instance).
     * @param aInput            File with the input file or folder.
     * @param aUseArea          boolean that indicates whether to use compound area ('true') or intensity ('false') for
     *                          the ratio calculation.
     * @param aCalibration      double with the instrument calibration.
     * @param aOutput           File with the (optional) output file. Can be 'null' for no file.
     * @param aConfidence       int with the (optional) confidence interval selected.
     * @param aS2nThreshold     double with the (optional) signal-to-noise threshold selected.
     * @param aInstrument       int with the (optional) output instrument selected.
     * @param aConeVoltage      int with the (optional) cone voltage constant to output.
     * @param aCollisionEnergy  int with the (optional) collision energy to output.
     * @return MALDIDiffAnalysisWorker with the results of the parsing and processing.
     */
    private MALDIDiffAnalysisWorker doCompoundProcessing(String aProgressBarTitle, Flamable aWorkerParent, String aProcessingName, File aInput, boolean aUseArea, double aCalibration, File aOutput, int aConfidence, double aS2nThreshold, int aInstrument, int aConeVoltage, int aCollisionEnergy) {
        // The progress bar.
        DefaultProgressBar progress = new DefaultProgressBar(this, aProgressBarTitle, 0, 4);
        progress.setSize(this.getWidth() / 2, progress.getHeight());
        // The worker.
        MALDIDiffAnalysisWorker mdaw = new MALDIDiffAnalysisWorker(this, aProcessingName, progress, aInput, aUseArea, aCalibration, aOutput, aConfidence, aS2nThreshold, aInstrument, aConeVoltage, aCollisionEnergy, null);
        // Start it!
        Thread t = new Thread(mdaw);
        t.start();
        progress.setVisible(true);
        // If we get here, we're through. Return the results and be done with it.
        return mdaw;
    }

    /**
     * This method will init and pop-up a dialog with the results of the analysis.
     *
     * @param aWorker                MALDIDiffAnalysisWorker which performed the work and will therefore be interrogated
     *                               for the results.
     * @param aInclusionListsWritten boolean to indicate whether inclusion lists were written.
     */
    private void showResults(MALDIDiffAnalysisWorker aWorker, boolean aInclusionListsWritten) {
        MALDIStatisticsResultsDialog msrd = null;
        int[] counts = aWorker.getCompoundCounts();
        double[] stats = aWorker.getStatisticsResults();
        String aOrI = (rbtArea.isSelected()) ? "area" : "intensity";
        if (aInclusionListsWritten) {
            msrd = new MALDIStatisticsResultsDialog(this, "Results for " + aWorker.getName() + "(using " + aOrI + " for ratio calculation)", aWorker.getName(), counts[MALDIDiffAnalysisWorker.TOTAL_COUNT], counts[MALDIDiffAnalysisWorker.COUPLE_COUNT], counts[MALDIDiffAnalysisWorker.SINGLE_COUNT], counts[MALDIDiffAnalysisWorker.SKIPPED_COUNT], stats, aWorker.getCouples(), rbtArea.isSelected(), aWorker.getFileCount(), aWorker.getDifferentialCompoundCount());
        } else {
            msrd = new MALDIStatisticsResultsDialog(this, "Results for " + aWorker.getName() + "(using " + aOrI + " for ratio calculation)", aWorker.getName(), counts[MALDIDiffAnalysisWorker.TOTAL_COUNT], counts[MALDIDiffAnalysisWorker.COUPLE_COUNT], counts[MALDIDiffAnalysisWorker.SINGLE_COUNT], counts[MALDIDiffAnalysisWorker.SKIPPED_COUNT], stats, aWorker.getCouples(), rbtArea.isSelected());
        }
        msrd.setLocation(this.getX() + 100, this.getY() + 100);
        msrd.setVisible(true);
    }

    /**
     * This method will attempt to read the 'MALDIDiffAnalysisGUI.properties' file from the classpath and read the
     * calibrations from there.
     */
    private void readCalibrations() {
        try {
            Properties p = new Properties();
            InputStream is = ClassLoader.getSystemResourceAsStream("MALDIDiffAnalysisGUI.properties");
            if (is == null) {
                is = this.getClass().getClassLoader().getResourceAsStream("MALDIDiffAnalysisGUI.properties");
                if (is == null) {
                    // Leave it at that.
                    String lMessage = "Could not find file 'MALDIDiffAnalysisGUI.properties' in the classpath!";
                    logger.error(lMessage);
                    JOptionPane.showMessageDialog(this, new String[]{lMessage, "No calibrations loaded, please fix and restart application!", "Exiting application."}, "Unable to load calibration data!", JOptionPane.ERROR_MESSAGE);
                    this.close();
                }
            }
            p.load(is);
            Iterator iter = p.keySet().iterator();
            iCalibrations = new InnerMALDICalibration[p.size()];
            int counter = 0;
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = p.getProperty(key);
                double calibration = 0.0;
                try {
                    calibration = Double.parseDouble(value);
                } catch (NumberFormatException nfe) {
                    logger.error(nfe.getMessage(), nfe);

                    JOptionPane.showMessageDialog(this, new String[]{"Could not read the calibration value for the '" + key + "' labelling!", "Value '" + value + "' was not a decimal number", "This calibration is set to '0.0'!"}, "Problem loading calibration data for '" + key + "'!", JOptionPane.ERROR_MESSAGE);
                }
                iCalibrations[counter] = new InnerMALDICalibration(key, calibration);
                counter++;
            }
            is.close();
        } catch (Exception e) {
            // Do nothing.
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * This class simply holds the two-piece calibration data (String with a labelling technique and double with the
     * actual calibration value).
     */
    private class InnerMALDICalibration {
        /**
         * The labelling technique.
         */
        private String iLabelling = null;
        /**
         * The calibration value.
         */
        private double iCalibration = 0.0;

        /**
         * This constructor takes the String with the labelling and the double with the corresponding calibration
         * value.
         *
         * @param aLabelling   String with the labelling.
         * @param aCalibration double with the calibration value.
         */
        public InnerMALDICalibration(String aLabelling, double aCalibration) {
            this.iLabelling = aLabelling;
            this.iCalibration = aCalibration;
        }

        public String toString() {
            return this.iLabelling + "   (stdev: " + iCalibration + ")";
        }

        /**
         * This method reports on the calibration value.
         *
         * @return double  with the calibrated standard deviation for this labelling technique.
         */
        public double getCalibration() {
            return iCalibration;
        }
    }


    public boolean isStandAlone() {
        return true;
    }
}
