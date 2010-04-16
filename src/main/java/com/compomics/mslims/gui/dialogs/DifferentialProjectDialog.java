/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 8-okt-2004
 * Time: 11:51:23
 */
package com.compomics.mslims.gui.dialogs;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Project;
import com.compomics.mslims.gui.DiffAnalysisGUI;
import com.compomics.mslims.util.diff.DifferentialProject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2004/10/13 10:05:36 $
 */

/**
 * This class represents a dialog that pops up whenever a user wants to add a project to the differential analysis list.
 * It requests the user to complete relevant, additional information about the project that is necessary to perform te
 * differential analysis.
 *
 * @author Lennart Martens
 * @version $Id: DifferentialProjectDialog.java,v 1.3 2004/10/13 10:05:36 lennart Exp $
 */
public class DifferentialProjectDialog extends JDialog {
    // Class specific log4j logger for DifferentialProjectDialog instances.
    private static Logger logger = Logger.getLogger(DifferentialProjectDialog.class);

    private JTextField txtAlias = null;

    private JRadioButton rbtNormal = null;
    private JRadioButton rbtInverse = null;
    private ButtonGroup rbgRadios = null;

    /**
     * The project that has to be altered (if any).
     */
    private Project iProject = null;

    /**
     * The parent of this window should be DiffAnalysisGUI.
     */
    private DiffAnalysisGUI iParent = null;

    public DifferentialProjectDialog(DiffAnalysisGUI aParent, String aTitle, Project aProject) {
        super(aParent, aTitle, true);
        this.iParent = aParent;
        this.iProject = aProject;
        // GUI stuff.
        this.constructScreen();
        this.pack();
        this.setResizable(false);
    }

    /**
     * This method sets up the GUI components & lays them out.
     */
    private void constructScreen() {
        // Initializing the components.
        txtAlias = new JTextField(15);
        txtAlias.setMaximumSize(txtAlias.getPreferredSize());
        rbtNormal = new JRadioButton("normal");
        rbtNormal.setMaximumSize(rbtNormal.getPreferredSize());
        rbtInverse = new JRadioButton("inverse");
        rbtInverse.setMaximumSize(rbtNormal.getPreferredSize());
        rbgRadios = new ButtonGroup();
        rbgRadios.add(rbtNormal);
        rbgRadios.add(rbtInverse);
        rbtNormal.setSelected(true);

        // The labels and determine spacing with text fields
        JLabel lblAlias = new JLabel("Project alias to use in reports");
        JLabel lblInverse = new JLabel("Labelling of the project");
        JLabel lblSemiColon1 = new JLabel(": ");
        JLabel lblSemiColon2 = new JLabel(": ");
        int aliasSpacer = lblAlias.getMinimumSize().width;
        int inverseSpacer = lblInverse.getMinimumSize().width;
        if (aliasSpacer < inverseSpacer) {
            aliasSpacer = 5 + (inverseSpacer - aliasSpacer);
            inverseSpacer = 5;
        } else {
            inverseSpacer = 5 + (aliasSpacer - inverseSpacer);
            aliasSpacer = 5;
        }

        JPanel jpanButtons = this.getButtonPanel();

        // Laying out the components.
        JPanel jpanText = new JPanel();
        jpanText.setLayout(new BoxLayout(jpanText, BoxLayout.X_AXIS));
        jpanText.add(Box.createHorizontalStrut(5));
        jpanText.add(lblAlias);
        jpanText.add(Box.createHorizontalStrut(aliasSpacer));
        jpanText.add(lblSemiColon1);
        jpanText.add(Box.createHorizontalStrut(5));
        jpanText.add(txtAlias);
        jpanText.add(Box.createHorizontalGlue());
        jpanText.setMaximumSize(new Dimension(jpanText.getMaximumSize().width, jpanText.getPreferredSize().height));

        JPanel jpanCheck = new JPanel();
        jpanCheck.setLayout(new BoxLayout(jpanCheck, BoxLayout.X_AXIS));
        jpanCheck.add(Box.createHorizontalStrut(5));
        jpanCheck.add(lblInverse);
        jpanCheck.add(Box.createHorizontalStrut(inverseSpacer));
        jpanCheck.add(lblSemiColon2);
        jpanCheck.add(Box.createHorizontalStrut(5));
        jpanCheck.add(rbtNormal);
        jpanCheck.add(Box.createHorizontalStrut(5));
        jpanCheck.add(rbtInverse);
        jpanCheck.add(Box.createHorizontalGlue());
        jpanCheck.setMaximumSize(new Dimension(jpanCheck.getMaximumSize().width, jpanCheck.getPreferredSize().height));

        JPanel jpanComponents = new JPanel();
        jpanComponents.setLayout(new BoxLayout(jpanComponents, BoxLayout.Y_AXIS));
        jpanComponents.setBorder(BorderFactory.createTitledBorder("Differential project details"));
        jpanComponents.add(jpanText);
        jpanComponents.add(Box.createVerticalStrut(5));
        jpanComponents.add(jpanCheck);


        JPanel jpanMain = new JPanel();
        jpanMain.setLayout(new BoxLayout(jpanMain, BoxLayout.Y_AXIS));
        jpanMain.add(jpanComponents);
        jpanMain.add(Box.createVerticalStrut(10));
        jpanMain.add(jpanButtons);
        jpanMain.add(Box.createVerticalStrut(5));

        this.getContentPane().add(jpanMain);
    }

    /**
     * This method creates and lays out the button panel.
     *
     * @return JPanel with the buttons.
     */
    private JPanel getButtonPanel() {
        JButton btnOK = new JButton("OK");
        btnOK.setMnemonic(KeyEvent.VK_O);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okPressed();
            }
        });
        btnOK.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    okPressed();
                }
            }
        });

        JButton btnCancel = new JButton("Cancel");
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

        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnOK);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(15));

        return jpanButtons;
    }

    /**
     * This method is invoked when the user presses OK.
     */
    private void okPressed() {
        String alias = txtAlias.getText();
        if (alias == null || alias.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Please enter a short alias for this project.", "No alias given!", JOptionPane.WARNING_MESSAGE);
            txtAlias.requestFocus();
            return;
        }
        DifferentialProject dp = new DifferentialProject(rbtInverse.isSelected(), alias.trim(), iProject.getProjectid(), iProject.getTitle());
        this.iParent.addProjectToTable(dp);
        this.close();
    }

    /**
     * This method is invoked when the user presses cancel.
     */
    private void cancelPressed() {
        this.close();
    }

    /**
     * This method closes the dialog.
     */
    private void close() {
        this.dispose();
    }
}
