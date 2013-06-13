/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 4-feb-2004
 * Time: 17:57:32
 */
package com.compomics.mslims.gui;

import org.apache.log4j.Logger;


import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/06/30 08:46:22 $
 */

/**
 * This class represents a GUI application which can be used to apply various XSL sheets to XML files.
 *
 * @author Lennart Martens
 * @version $Id: XSLTransformer.java,v 1.2 2004/06/30 08:46:22 lennart Exp $
 */
public class XSLTransformer extends JFrame {
    // Class specific log4j logger for XSLTransformer instances.
    private static Logger logger = Logger.getLogger(XSLTransformer.class);

    private static final String XML_FOLDER = "XML_FOLDER";
    private static final String XSL_FiLE = "XSL_FILE";
    private static final String OUTPUT_FILE = "OUTPUT_FILE";
    private static final String APPEND = "APPEND";

    private JLabel lblXMLFile = null;
    private JLabel lblXSLFile = null;
    private JLabel lblOutputFile = null;

    private JTextField txtXMLFile = null;
    private JTextField txtXSLFile = null;
    private JTextField txtOutputFile = null;

    private JCheckBox chkAppend = null;

    private JButton btnBrowseXML = null;
    private JButton btnBrowseXSL = null;
    private JButton btnBrowseOutput = null;
    private JButton btnView = null;
    private JButton btnWrite = null;

    /**
     * This constructor takes a title for the JFrame as its only argument.
     *
     * @param aTitle String with a title for the JFrame.
     */
    public XSLTransformer(String aTitle) {
        super(aTitle);

        this.constructScreen();
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                XSLTransformer.this.cleanUp();
                System.exit(0);
            }
        });
        this.attemptToLocateProperties();
        this.setLocation(100, 100);
        this.pack();
    }

    /**
     * The main method is the entry point for the application.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        XSLTransformer xslt = new XSLTransformer("XML/XSL transformation application");
        xslt.setVisible(true);
    }

    /**
     * This method initializes and lays out the screen components.
     */
    private void constructScreen() {
        // The labels.
        lblXMLFile = new JLabel("XML file : ");
        lblXSLFile = new JLabel("XSL file : ");
        lblOutputFile = new JLabel("Output file : ");
        // the longest of the labels.
        int length = Math.max(lblOutputFile.getPreferredSize().width, lblXMLFile.getPreferredSize().width);
        if (lblXSLFile.getPreferredSize().width > length) {
            length = lblXSLFile.getPreferredSize().width;
        }
        // Delta's.
        int deltaXML = length - lblXMLFile.getPreferredSize().width;
        int deltaXSL = length - lblXSLFile.getPreferredSize().width;
        int deltaOutput = length - lblOutputFile.getPreferredSize().width;

        // XML label, XML textfield, XML browse button.
        txtXMLFile = new JTextField(20);
        btnBrowseXML = new JButton("Browse...");
        btnBrowseXML.setMnemonic(KeyEvent.VK_B);
        btnBrowseXML.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    browseXMLTriggered();
                }
            }
        });
        btnBrowseXML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseXMLTriggered();
            }
        });
        // The XML panel.
        JPanel jpanXML = new JPanel();
        jpanXML.setLayout(new BoxLayout(jpanXML, BoxLayout.X_AXIS));
        jpanXML.add(Box.createHorizontalStrut(5));
        jpanXML.add(lblXMLFile);
        jpanXML.add(Box.createHorizontalStrut(5 + deltaXML));
        jpanXML.add(txtXMLFile);
        jpanXML.add(Box.createHorizontalStrut(5));
        jpanXML.add(btnBrowseXML);
        jpanXML.add(Box.createHorizontalStrut(5));

        // XSL label, XSL textfield, XSL browse button.
        txtXSLFile = new JTextField(20);
        btnBrowseXSL = new JButton("Browse...");
        btnBrowseXSL.setMnemonic(KeyEvent.VK_R);
        btnBrowseXSL.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    browseXSLTriggered();
                }
            }
        });
        btnBrowseXSL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseXSLTriggered();
            }
        });
        // The XSL panel.
        JPanel jpanXSL = new JPanel();
        jpanXSL.setLayout(new BoxLayout(jpanXSL, BoxLayout.X_AXIS));
        jpanXSL.add(Box.createHorizontalStrut(5));
        jpanXSL.add(lblXSLFile);
        jpanXSL.add(Box.createHorizontalStrut(5 + deltaXSL));
        jpanXSL.add(txtXSLFile);
        jpanXSL.add(Box.createHorizontalStrut(5));
        jpanXSL.add(btnBrowseXSL);
        jpanXSL.add(Box.createHorizontalStrut(5));

        // Output label, output textfield, output browse button.
        txtOutputFile = new JTextField(20);
        btnBrowseOutput = new JButton("Browse...");
        btnBrowseOutput.setMnemonic(KeyEvent.VK_O);
        btnBrowseOutput.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    browseOutputTriggered();
                }
            }
        });
        btnBrowseOutput.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                browseOutputTriggered();
            }
        });
        // The XSL panel.
        JPanel jpanOutput = new JPanel();
        jpanOutput.setLayout(new BoxLayout(jpanOutput, BoxLayout.X_AXIS));
        jpanOutput.add(Box.createHorizontalStrut(5));
        jpanOutput.add(lblOutputFile);
        jpanOutput.add(Box.createHorizontalStrut(5 + deltaOutput));
        jpanOutput.add(txtOutputFile);
        jpanOutput.add(Box.createHorizontalStrut(5));
        jpanOutput.add(btnBrowseOutput);
        jpanOutput.add(Box.createHorizontalStrut(5));

        // The append label, checkbox and panel.
        chkAppend = new JCheckBox("Append", false);
        JPanel jpanAppend = new JPanel();
        jpanAppend.setLayout(new BoxLayout(jpanAppend, BoxLayout.X_AXIS));
        jpanAppend.add(Box.createHorizontalStrut(length + 15));
        jpanAppend.add(chkAppend);
        jpanAppend.add(Box.createHorizontalGlue());

        // Set label minimum sizes.
        lblOutputFile.setMinimumSize(new Dimension(length, lblOutputFile.getPreferredSize().height));
        lblXMLFile.setMinimumSize(new Dimension(length, lblXMLFile.getPreferredSize().height));
        lblXSLFile.setMinimumSize(new Dimension(length, lblXSLFile.getPreferredSize().height));

        // View button.
        btnView = new JButton("View...");
        btnView.setMnemonic(KeyEvent.VK_V);
        btnView.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    viewTriggered();
                }
            }
        });
        btnView.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewTriggered();
            }
        });
        // Write button.
        btnWrite = new JButton("Write!");
        btnWrite.setMnemonic(KeyEvent.VK_W);
        btnWrite.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been typed.
             * This event occurs when a key press is followed by a key release.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    writeTriggered();
                }
            }
        });
        btnWrite.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                writeTriggered();
            }
        });

        // The buttonpanel.
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnView);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnWrite);
        jpanButtons.add(Box.createHorizontalStrut(10));

        // Main panel.
        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanXML);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanXSL);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanOutput);
        jpanMain.add(Box.createVerticalStrut(5));
        jpanMain.add(jpanAppend);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanButtons);
        jpanMain.add(Box.createVerticalGlue());

        // Add the main panel to the frame.
        this.getContentPane().add(jpanMain, BorderLayout.CENTER);
    }

    /**
     * This method will be called when the user clicks 'browse XML'.
     */
    private void browseXMLTriggered() {
        this.getFile(txtXMLFile, "XML", ".xml");
    }

    /**
     * This method will be called when the user clicks 'browse XSL'.
     */
    private void browseXSLTriggered() {
        this.getFile(txtXSLFile, "XSL", ".xsl");
    }

    /**
     * This method will be called when the user clicks 'browse output'.
     */
    private void browseOutputTriggered() {
        this.getFile(txtOutputFile, "Output file", null);
    }

    /**
     * This method presents a generic way to load a file location using a JFileChooser component and feed the filename
     * into the specified JTextComponent. Note that if a path has been entered in the textfield and it exists, it is
     * automatically set as the starting location on the JFileChooser.
     *
     * @param aTextField JTextField with the recipient of the retrieved information.
     * @param aContent   String with a description for the type of file that needs to be localized.
     * @param aFilter    String with an (optional) extension to filter for (can be 'null' for no filter).
     */
    private void getFile(JTextField aTextField, String aContent, String aFilter) {
        File toOpen = null;
        while (toOpen == null) {
            // Default directory location is the root of this drive.
            String root = "/";
            // See if there is something stipulated in the recipient textfield already.
            String tempRoot = aTextField.getText();
            if (tempRoot != null && !tempRoot.trim().equals("")) {
                // It seems that something is already specified.
                File test = new File(tempRoot.trim());
                // See if it exists.
                if (test.exists()) {
                    // It exists! If it is a file, get the parent directory.
                    if (!test.isDirectory()) {
                        test = test.getParentFile();
                    }
                    // try to get the path, and if this succeeds, set it as the
                    // starting location for browsing. Remember that if it fails,
                    // we default to the root of the drive, so no exception handling
                    // here.
                    try {
                        root = test.getCanonicalPath();
                    } catch (IOException ioe) {
                        // If this fails, we default to the root of this drive anyway.
                    }
                }
            }
            JFileChooser jfc = new JFileChooser(root);
            // Set a file filter if required.
            if (aFilter != null) {
                jfc.setFileFilter(new FilenameFilter(aFilter));
            }
            int returnVal = jfc.showOpenDialog(XSLTransformer.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                toOpen = jfc.getSelectedFile();
                if (toOpen.exists()) {
                    try {
                        aTextField.setText(toOpen.getCanonicalPath());
                    } catch (IOException ioe) {
                        logger.error(ioe.getMessage(), ioe);
                        JOptionPane.showMessageDialog(XSLTransformer.this, new String[]{"Unable to read '" + toOpen.getName() + "' as " + aContent + " file!", ioe.getMessage()}, "Unable to load " + aContent + " file!", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    String lMessage = "The '" + toOpen.getName() + "' " + aContent + " file was not found!";
                    logger.error(lMessage);
                    JOptionPane.showMessageDialog(XSLTransformer.this, new String[]{lMessage}, aContent + " file was not found!", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    /**
     * This method is called when the user clicks 'View...'
     */
    private void viewTriggered() {
        // Retrieve the contents of the textfields.
        String lXMLFile = txtXMLFile.getText();
        String lXSLFile = txtXSLFile.getText();

        // First validate the presence of both input files (XML and XSL).
        if (lXMLFile == null || lXMLFile.trim().equals("")) {
            String lMessage = "No XML file selected!";
            logger.error(lMessage);
            JOptionPane.showMessageDialog(this, "You need to select an XML input file first!", lMessage, JOptionPane.ERROR_MESSAGE);
            txtXMLFile.requestFocus();
        } else if (lXSLFile == null || lXSLFile.trim().equals("")) {
            String lMessage = "No XSL file selected!";
            logger.error(lMessage);
            JOptionPane.showMessageDialog(this, "You need to select an XSL input file first!", lMessage, JOptionPane.ERROR_MESSAGE);
            txtXSLFile.requestFocus();
        } else {
            // Now see if we can actually locate the files.
            File xml = new File(lXMLFile);
            File xsl = new File(lXSLFile);
            if (!xml.exists()) {
                String lMessage = "The XML input file you specified does not exist!";
                logger.error(lMessage);
                JOptionPane.showMessageDialog(this, lMessage, "XML file not found!", JOptionPane.ERROR_MESSAGE);
                txtXMLFile.requestFocus();
            } else if (!xsl.exists()) {
                String lMessage = "The XSL input file you specified does not exist!";
                logger.error(lMessage);
                JOptionPane.showMessageDialog(this, lMessage, "XSL file not found!", JOptionPane.ERROR_MESSAGE);
                txtXSLFile.requestFocus();
            } else {
                // All clear!
                // The frame to display the results in.
                final JFrame display = new JFrame(xml.getName() + " transformed by " + xsl.getName());
                // The text area which will hold the data.
                final JTextArea area = new JTextArea();
                area.setFont(new Font("Monospaced", Font.PLAIN, 12));
                // The 'save file' button.
                JButton btnSave = new JButton("Save file...");
                btnSave.setMnemonic(KeyEvent.VK_S);
                btnSave.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        XSLTransformer.this.saveFileTriggered(display, area.getText());
                    }
                });
                // The panel for the button.
                JPanel jpanButtons = new JPanel();
                jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
                jpanButtons.add(Box.createHorizontalGlue());
                jpanButtons.add(btnSave);
                jpanButtons.add(Box.createHorizontalStrut(10));

                display.getContentPane().add(new JScrollPane(area), BorderLayout.CENTER);
                display.getContentPane().add(jpanButtons, BorderLayout.SOUTH);
                display.addWindowListener(new WindowAdapter() {
                    /**
                     * Invoked when a window is in the process of being closed.
                     * The close operation can be overridden at this point.
                     */
                    public void windowClosing(WindowEvent e) {
                        e.getWindow().setVisible(false);
                        e.getWindow().dispose();
                    }
                });
                try {
                    // Get the transformer.
                    Transformer transformer = XSLTransformer.this.getTransformer(xsl);
                    StringWriter writer = new StringWriter();
                    FileReader reader = new FileReader(xml);
                    try {
                        transformer.transform(new StreamSource(reader), new StreamResult(writer));
                        writer.flush();
                        area.setText(writer.toString());
                        writer.close();
                        display.setLocation((int) XSLTransformer.this.getLocation().getX() + 50, (int) XSLTransformer.this.getLocation().getY() + 50);
                        if (area.getText().length() > 1) {
                            area.setCaretPosition(1);
                        }
                        display.setSize(400, 300);
                        display.setVisible(true);
                    } catch (TransformerException tfe) {
                        logger.error(tfe.getMessage(), tfe);
                        JOptionPane.showMessageDialog(XSLTransformer.this, new String[]{"Unable to execute your XML/XSL transformation!", tfe.getMessage()}, "Transformation failure!", JOptionPane.ERROR_MESSAGE);
                    }
                    // Start the transformation.
                } catch (TransformerConfigurationException tce) {
                    logger.error(tce.getMessage(), tce);
                    JOptionPane.showMessageDialog(XSLTransformer.this, new String[]{"Unable to transform XSL file: ", tce.getMessage()}, "XSL error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage(), ioe);
                    JOptionPane.showMessageDialog(XSLTransformer.this, new String[]{"Unable to read file: ", ioe.getMessage()}, "I/O error", JOptionPane.ERROR_MESSAGE);
                }

            }
        }
    }

    /**
     * This method is called when the user clicks 'Write...'
     */
    private void writeTriggered() {
        // Retrieve the contents of the textfields.
        String lXMLFile = txtXMLFile.getText();
        String lXSLFile = txtXSLFile.getText();
        String lOutputFile = txtOutputFile.getText();

        // First validate the presence of both input files (XML and XSL).
        if (lXMLFile == null || lXMLFile.trim().equals("")) {
            String lMessage = "No XML file selected!";
            logger.error(lMessage);
            JOptionPane.showMessageDialog(this, "You need to select an XML input file first!", lMessage, JOptionPane.ERROR_MESSAGE);
            txtXMLFile.requestFocus();
        } else if (lXSLFile == null || lXSLFile.trim().equals("")) {
            String lMessage = "You need to select an XSL input file first!";
            logger.error(lMessage);
            JOptionPane.showMessageDialog(this, lMessage, "No XSL file selected!", JOptionPane.ERROR_MESSAGE);
            txtXSLFile.requestFocus();
        } else if (lOutputFile == null || lOutputFile.trim().equals("")) {
            String lMessage = "You need to select an output file first!";
            logger.error(lMessage);
            JOptionPane.showMessageDialog(this, lMessage, "No output file selected!", JOptionPane.ERROR_MESSAGE);
            txtOutputFile.requestFocus();
        } else {
            // Now see if we can actually locate the files.
            File xml = new File(lXMLFile);
            File xsl = new File(lXSLFile);
            if (!xml.exists()) {
                String lMessage = "The XML input file you specified does not exist!";
                logger.error(lMessage);
                JOptionPane.showMessageDialog(this, lMessage, "XML file not found!", JOptionPane.ERROR_MESSAGE);
                txtXMLFile.requestFocus();
            } else if (!xsl.exists()) {
                String lMessage = "The XSL input file you specified does not exist!";
                logger.error(lMessage);
                JOptionPane.showMessageDialog(this, lMessage, "XSL file not found!", JOptionPane.ERROR_MESSAGE);
                txtXSLFile.requestFocus();
            } else {
                // All clear so far!
                // See if we are NOT in append mode, if so, ask for confirmation.
                boolean append = chkAppend.isSelected();
                if (!append) {
                    int result = JOptionPane.showConfirmDialog(this, new String[]{"You choose to overwrite file '" + lOutputFile + "'!", "Do you want to continue?"}, "Overwrite selected!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                }
                // First transform.
                String result = null;
                try {
                    // Get the transformer.
                    Transformer transformer = XSLTransformer.this.getTransformer(xsl);
                    StringWriter writer = new StringWriter();
                    FileReader reader = new FileReader(xml);
                    try {
                        transformer.transform(new StreamSource(reader), new StreamResult(writer));
                        writer.flush();
                        result = writer.toString();
                        writer.close();
                    } catch (TransformerException tfe) {
                        logger.error(tfe.getMessage(), tfe);
                        JOptionPane.showMessageDialog(XSLTransformer.this, new String[]{"Unable to execute your XML/XSL transformation!", tfe.getMessage()}, "Transformation failure!", JOptionPane.ERROR_MESSAGE);
                    }
                    // Start the transformation.
                } catch (TransformerConfigurationException tce) {
                    logger.error(tce.getMessage(), tce);
                    JOptionPane.showMessageDialog(XSLTransformer.this, new String[]{"Unable to transform XSL file: ", tce.getMessage()}, "XSL error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage(), ioe);
                    JOptionPane.showMessageDialog(XSLTransformer.this, new String[]{"Unable to read file: ", ioe.getMessage()}, "I/O error", JOptionPane.ERROR_MESSAGE);
                }
                // Convert all ',' in the String to '_' for maximum compatibility with 'CSV' format.
                result = result.replace(',', '_');

                // All that's left now is to write.
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(lOutputFile, append));
                    bw.write(result);
                    bw.flush();
                    bw.close();
                    String start = "Appended";
                    if (!append) {
                        start = "Wrote";
                    }
                    JOptionPane.showMessageDialog(this, start + " contents to file '" + lOutputFile + "'.", "Output complete!", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage(), ioe);

                    JOptionPane.showMessageDialog(XSLTransformer.this, new String[]{"Unable to write to file '" + lOutputFile + "': ", ioe.getMessage()}, "Write error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * This method discards the graphical parts of the application upon system exit.
     */
    private void cleanUp() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * This method creates and initializes an XML transformer based on the selected XSL.
     *
     * @throws javax.xml.transform.TransformerConfigurationException
     *          when the transformer could not be created.
     */
    private Transformer getTransformer(File aXSLFile) throws TransformerConfigurationException, IOException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        StreamSource stylesource = new StreamSource(new FileInputStream(aXSLFile));
        return tFactory.newTransformer(stylesource);
    }

    /**
     * This method will attempt to read the 'xml_xsl.properties' file from the classpath containing the following
     * (self-evident) keys: <ul> <li>XML_FOLDER</li> <li>XSL_FILE</li> <li>OUTPUT_FILE</li> <li>APPEND</li> </ul>
     */
    private void attemptToLocateProperties() {
        try {
            Properties p = new Properties();
            p.load(this.getClass().getClassLoader().getResourceAsStream("xml_xsl.properties"));
            if (p.containsKey(XML_FOLDER)) {
                String temp = p.getProperty(XML_FOLDER);
                if (temp != null && !temp.trim().equals("")) {
                    txtXMLFile.setText(temp.trim());
                }
            }
            if (p.containsKey(XSL_FiLE)) {
                String temp = p.getProperty(XSL_FiLE);
                if (temp != null && !temp.trim().equals("")) {
                    txtXSLFile.setText(temp.trim());
                }
            }
            if (p.containsKey(OUTPUT_FILE)) {
                String temp = p.getProperty(OUTPUT_FILE);
                if (temp != null && !temp.trim().equals("")) {
                    txtOutputFile.setText(temp.trim());
                }
            }
            if (p.containsKey(APPEND)) {
                String temp = p.getProperty(APPEND);
                if (temp != null && !temp.trim().equals("")) {
                    chkAppend.setSelected(new Boolean(temp).booleanValue());
                }
            }
        } catch (Exception e) {
            // Whatever.
        }
    }

    /**
     * This method is called whenever the user tries to save a file from a display window.
     *
     * @param aSource   component that called this method.
     * @param aContents String with the contents to be saved to file.
     */
    private void saveFileTriggered(Component aSource, String aContents) {
        // First get the filename and location.
        boolean lbContinue = true;
        File output = null;
        while (lbContinue) {
            JFileChooser jfc = new JFileChooser("/");
            int returnVal = jfc.showSaveDialog(XSLTransformer.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lbContinue = false;
                output = jfc.getSelectedFile();
                if (output.exists()) {
                    int result = JOptionPane.showConfirmDialog(aSource, new String[]{"File '" + output.getName() + "' exists!", "Do you want to overwrite?"}, "File exists!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.NO_OPTION) {
                        lbContinue = true;
                    } else if (result == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                }
            }
        }
        // Okay, output the String to the specified file.
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            bw.write(aContents);
            bw.flush();
            bw.close();
            JOptionPane.showMessageDialog(aSource, "Written contents to file '" + output.getName() + "'.", "Output complete!", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            JOptionPane.showMessageDialog(aSource, new String[]{"Unable to write contents to file '" + output.getName() + "':", ioe.getMessage()}, "Error writing file!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class FilenameFilter extends FileFilter {

        /**
         * The String to filter on.
         */
        private String iString = null;

        /**
         * This constructor takes the String to filter on. It ignores whether or not htis String starts with a '.'.
         *
         * @param aString String to filter on.
         */
        public FilenameFilter(String aString) {
            this.iString = aString;
            if (iString.startsWith(".")) {
                iString = iString.substring(1);
            }
        }

        /**
         * Whether the given file is accepted by this filter.
         */
        public boolean accept(File f) {
            boolean result = f.isDirectory();
            String name = f.getName();
            if (name.endsWith("." + iString)) {
                result = true;
            }
            return result;
        }

        /**
         * The description of this filter. For example: "JPG and GIF Images"
         *
         * @see FileView#getName
         */
        public String getDescription() {
            return iString + " Files";
        }
    }
}
