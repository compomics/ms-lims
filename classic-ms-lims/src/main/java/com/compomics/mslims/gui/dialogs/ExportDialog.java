/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 19-dec-02
 * Time: 9:00:17
 */
package com.compomics.mslims.gui.dialogs;

import org.apache.log4j.Logger;

import com.compomics.util.db.DBResultSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class implements a Dialog that allows the user to specify some options for exporting tabular data to a file.
 *
 * @author Lennart Martens
 */
public class ExportDialog extends JDialog {
    // Class specific log4j logger for ExportDialog instances.
    private static Logger logger = Logger.getLogger(ExportDialog.class);

    private JTextField txtFile = null;
    private JButton btnBrowse = null;

    private JRadioButton rbtHTML = null;
    private JRadioButton rbtCSV = null;

    private JComboBox cmbBorder = null;

    private JRadioButton rbtComma = null;
    private JRadioButton rbtSemiColon = null;
    private JRadioButton rbtTab = null;
    private JRadioButton rbtOther = null;
    private JTextField txtOther = null;

    private JButton btnOK = null;
    private JButton btnCancel = null;

    /**
     * The DBResultSet to read the data from.
     */
    private DBResultSet iModel = null;

    /**
     * This constructor allows the specification of a parent, a title for the dialog and a DBResultSet as a data source.
     * It will also lay-out components, but will not make the dialog visible! This is up to the caller.
     *
     * @param aParent JFrame that is the parent of this dialog.
     * @param aTitle  String with the title for this dialog.
     * @param aModel  DBResultSet with the table model to read the data from.
     */
    public ExportDialog(JFrame aParent, String aTitle, DBResultSet aModel) {
        super(aParent, aTitle, true);
        this.iModel = aModel;
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                cancelled();
            }
        });
        this.constructScreen();
        this.pack();
        Point p = aParent.getLocation();
        this.setLocation(p.x + (aParent.getWidth() / 3), p.y + (aParent.getHeight() / 3));
        this.setResizable(false);
    }

    /**
     * This method will construct the screen and lay out the components.
     */
    private void constructScreen() {
        // Components for the file panel.
        txtFile = new JTextField(15);
        txtFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String file = txtFile.getText();
                if (file != null && !file.trim().equals("")) {
                    okPressed();
                }
            }
        });
        btnBrowse = new JButton("Browse...");
        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File startHere = new File("/");
                // Open the filechooser on the root or the folder the user
                // already specified (if it exists).
                if (!txtFile.getText().trim().equals("")) {
                    File f = new File(txtFile.getText().trim());
                    if (f.exists()) {
                        startHere = f;
                    }
                }

                JFileChooser jfc = new JFileChooser(startHere);
                boolean unSure = true;
                String file = null;
                while (unSure) {
                    unSure = false;
                    int returnVal = 0;
                    returnVal = jfc.showSaveDialog(txtFile);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        file = jfc.getSelectedFile().getAbsoluteFile().toString();
                        File f = new File(file);
                        if (f.exists()) {
                            int answer = JOptionPane.showConfirmDialog(ExportDialog.this, new String[]{"File '" + file + "' exists!", "Overwrite?"}, "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (answer == JOptionPane.YES_OPTION) {
                                txtFile.setText(file);
                            } else {
                                file = null;
                                unSure = true;
                            }
                        } else {
                            txtFile.setText(file);
                        }
                    }
                }
            }
        });
        btnBrowse.setMnemonic(KeyEvent.VK_B);
        // The file panel.
        JPanel jpanFile = new JPanel();
        jpanFile.setLayout(new BoxLayout(jpanFile, BoxLayout.X_AXIS));
        jpanFile.setBorder(BorderFactory.createTitledBorder("Output file selection"));
        jpanFile.add(Box.createRigidArea(new Dimension(5, btnBrowse.getHeight())));
        jpanFile.add(new JLabel("Enter output file here: "));
        jpanFile.add(Box.createRigidArea(new Dimension(10, btnBrowse.getHeight())));
        jpanFile.add(txtFile);
        jpanFile.add(Box.createRigidArea(new Dimension(10, btnBrowse.getHeight())));
        jpanFile.add(btnBrowse);
        jpanFile.add(Box.createRigidArea(new Dimension(5, btnBrowse.getHeight())));

        // Components for the HTML panel.
        rbtHTML = new JRadioButton("Output in HTML table format", false);
        rbtHTML.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    cmbBorder.setEnabled(false);
                } else if (e.getStateChange() == ItemEvent.SELECTED) {
                    cmbBorder.setEnabled(true);
                }
            }
        });
        cmbBorder = new JComboBox(new String[]{"0", "1", "2", "3", "4", "5"});
        cmbBorder.setEnabled(false);
        // The HTML panel and subpanels.
        JPanel jpanHTMLRadio = new JPanel();
        jpanHTMLRadio.setLayout(new BoxLayout(jpanHTMLRadio, BoxLayout.X_AXIS));
        jpanHTMLRadio.add(rbtHTML);
        jpanHTMLRadio.add(Box.createHorizontalGlue());

        JPanel jpanBorder = new JPanel();
        jpanBorder.setLayout(new BoxLayout(jpanBorder, BoxLayout.X_AXIS));
        jpanBorder.add(Box.createRigidArea(new Dimension(35, cmbBorder.getHeight())));
        jpanBorder.add(new JLabel("Border style: "));
        jpanBorder.add(Box.createRigidArea(new Dimension(10, cmbBorder.getHeight())));
        jpanBorder.add(cmbBorder);
        jpanBorder.add(Box.createHorizontalGlue());

        JPanel jpanHTML = new JPanel();
        jpanHTML.setLayout(new BoxLayout(jpanHTML, BoxLayout.Y_AXIS));
        jpanHTML.setBorder(BorderFactory.createTitledBorder("HTML output"));
        jpanHTML.add(jpanHTMLRadio);
        jpanHTML.add(Box.createRigidArea(new Dimension(rbtHTML.getWidth(), 5)));
        jpanHTML.add(jpanBorder);

        // Components for the CSV panel.
        rbtCSV = new JRadioButton("Output in CSV format", true);
        rbtCSV.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    rbtComma.setEnabled(false);
                    rbtSemiColon.setEnabled(false);
                    rbtTab.setEnabled(false);
                    rbtOther.setEnabled(false);
                    txtOther.setEnabled(false);
                } else if (e.getStateChange() == ItemEvent.SELECTED) {
                    rbtComma.setEnabled(true);
                    rbtSemiColon.setEnabled(true);
                    rbtTab.setEnabled(true);
                    rbtOther.setEnabled(true);
                    if (rbtOther.isSelected()) {
                        txtOther.setEnabled(true);
                    }
                }
            }
        });
        rbtComma = new JRadioButton("Comma", false);
        rbtSemiColon = new JRadioButton("Semicolon", true);
        rbtTab = new JRadioButton("Tab", false);
        rbtOther = new JRadioButton("Other", false);
        rbtOther.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) {
                    txtOther.setEnabled(false);
                } else if (e.getStateChange() == ItemEvent.SELECTED) {
                    txtOther.setEnabled(true);
                }
            }
        });
        txtOther = new JTextField(10);
        txtOther.setMaximumSize(txtOther.getPreferredSize());
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbtComma);
        bg.add(rbtSemiColon);
        bg.add(rbtTab);
        bg.add(rbtOther);

        ButtonGroup bg2 = new ButtonGroup();
        bg2.add(rbtHTML);
        bg2.add(rbtCSV);
        // CSV panel and subpanels.
        JPanel jpanCSVRadio = new JPanel();
        jpanCSVRadio.setLayout(new BoxLayout(jpanCSVRadio, BoxLayout.X_AXIS));
        jpanCSVRadio.add(rbtCSV);
        jpanCSVRadio.add(Box.createHorizontalGlue());

        JPanel jpanOther = new JPanel();
        jpanOther.setLayout(new BoxLayout(jpanOther, BoxLayout.Y_AXIS));
        JPanel jpanOtherText = new JPanel();
        jpanOtherText.setLayout(new BoxLayout(jpanOtherText, BoxLayout.X_AXIS));
        jpanOtherText.add(txtOther);
        jpanOtherText.add(Box.createHorizontalGlue());
        jpanOther.add(jpanOtherText);

        JPanel jpanRadios = new JPanel();
        jpanRadios.setLayout(new GridLayout(3, 2));
        jpanRadios.add(rbtComma);
        jpanRadios.add(rbtTab);
        jpanRadios.add(rbtSemiColon);
        jpanRadios.add(rbtOther);
        jpanRadios.add(new JPanel());
        jpanRadios.add(jpanOther);

        JPanel jpanRadioWrapper = new JPanel();
        jpanRadioWrapper.setLayout(new BoxLayout(jpanRadioWrapper, BoxLayout.X_AXIS));
        jpanRadioWrapper.add(Box.createRigidArea(new Dimension(35, jpanRadios.getHeight())));
        jpanRadioWrapper.add(jpanRadios);
        jpanRadioWrapper.add(Box.createHorizontalGlue());

        JPanel jpanCSV = new JPanel();
        jpanCSV.setLayout(new BoxLayout(jpanCSV, BoxLayout.Y_AXIS));
        jpanCSV.setBorder(BorderFactory.createTitledBorder("CSV output"));
        jpanCSV.add(jpanCSVRadio);
        jpanCSV.add(Box.createRigidArea(new Dimension(rbtCSV.getWidth(), 5)));
        jpanCSV.add(jpanRadioWrapper);

        // ButtonPanel.
        btnOK = new JButton("Export");
        btnOK.setMnemonic(KeyEvent.VK_E);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okPressed();
            }
        });
        btnCancel = new JButton("Cancel");
        btnCancel.setMnemonic(KeyEvent.VK_C);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelled();
            }
        });
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnOK);
        jpanButtons.add(Box.createRigidArea(new Dimension(10, btnOK.getHeight())));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createRigidArea(new Dimension(10, btnOK.getHeight())));

        // Total Panel.
        JPanel jpanTotal = new JPanel();
        jpanTotal.setLayout(new BoxLayout(jpanTotal, BoxLayout.Y_AXIS));
        jpanTotal.add(jpanFile);
        jpanTotal.add(jpanHTML);
        jpanTotal.add(jpanCSV);
        jpanTotal.add(Box.createRigidArea(new Dimension(jpanCSV.getWidth(), 10)));
        jpanTotal.add(jpanButtons);

        this.getContentPane().add(jpanTotal, BorderLayout.CENTER);
    }

    /**
     * This method is called when the user clicks OK.
     */
    private void okPressed() {
        // See if we have a file.
        String file = txtFile.getText().trim();
        if (!file.equals("")) {
            try {
                PrintWriter out = new PrintWriter(new FileWriter(file));
                // A File was selected for output.
                // Inform the data that it should write to file.
                if (rbtHTML.isSelected()) {
                    out.print("<html><body>");
                    iModel.writeToHTMLTable(out, Integer.parseInt((String) cmbBorder.getSelectedItem()));
                    out.print("</body></html>");
                } else if (rbtCSV.isSelected()) {
                    String separator = null;
                    if (rbtComma.isSelected()) {
                        separator = ",";
                    } else if (rbtSemiColon.isSelected()) {
                        separator = ";";
                    } else if (rbtTab.isSelected()) {
                        separator = "\t";
                    } else if (rbtOther.isSelected()) {
                        separator = txtOther.getText();
                        if (separator.equals("")) {
                            String lMessage = "No custom separator character specified!";
                            logger.error(lMessage);
                            JOptionPane.showMessageDialog(this, lMessage, "No separator specified", JOptionPane.ERROR_MESSAGE);
                            txtOther.requestFocus();
                            return;
                        }
                    }
                    iModel.writeToCSVFile(out, separator);
                }

                out.flush();
                out.close();
                JOptionPane.showMessageDialog(this, "Data successfully written to '" + file + "'!", "Output completed!", JOptionPane.INFORMATION_MESSAGE);
                this.cancelled();
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
                JOptionPane.showMessageDialog(this, new String[]{"Unable to write file '" + file + "'!", ioe.getMessage()}, "Error writing output file!", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            String lMessage = "You need to specify an output file first!";
            logger.error(lMessage);
            JOptionPane.showMessageDialog(this, lMessage, "No output file specified!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called when the dialog was cancelled.
     */
    private void cancelled() {
        this.setVisible(false);
        this.dispose();
    }
}
