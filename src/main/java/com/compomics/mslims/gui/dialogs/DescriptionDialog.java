/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 21-jun-2003
 * Time: 18:11:12
 */
package com.compomics.mslims.gui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class allows the editing of a description (freetext) field.
 *
 * @author Lennart Martens
 */
public class DescriptionDialog extends JDialog {

    /**
     * The description text.
     */
    private String iText = null;

    /**
     * The new text.
     */
    private String iNewText = null;

    private JTextArea txtText = null;
    private JButton btnSave = null;
    private JButton btnCancel = null;
    private JButton btnNoDescription = null;


    /**
     * Private constructor.
     *
     * @param aParent   Frame with the parent for this dialog.
     * @param aTitle    String with a title for this dialog.
     * @param aText String with the text to edit. Can be 'null'.
     */
    private DescriptionDialog(Frame aParent, String aTitle, String aText) {
        super(aParent, aTitle, true);
        this.iText = aText;
        this.constructScreen();
        this.pack();
    }

    /**
     * This method will pop-up a model dialog that allows the user to edit the description value.
     * It will return the edited value.
     *
     * @param aParent   Frame with the parent for this dialog.
     * @param aTitle    String with a title for this dialog.
     * @param aText String with the text to edit. Can be 'null'.
     * @param   x   int with the x-coordinate for the location of the dialog.
     * @param   y   int with the y-coordinate for the location of the dialog.
     * @return  String  with the edited text, note that this String can be 'null'!
     */
    public static String getDescriptionDialog(Frame aParent, String aTitle, String aText, int x, int y) {
        String result = null;

        DescriptionDialog dd = new DescriptionDialog(aParent, aTitle, aText);
        dd.setLocation(x, y);
        dd.setVisible(true);
        result = dd.getText();

        return result;
    }

    /**
     * This method will return the String after the editing process.
     * If the String was not edited, the original is returned.
     *
     * @return  String with the edited String.
     */
    private String getText() {
        return iNewText;
    }

    /**
     * This method will construct the GUI.
     */
    private void constructScreen() {
        // The textarea.
        txtText = new JTextArea(15, 45);
        if(iText != null) {
            txtText.setText(iText);
        } else {
            txtText.setText("");
        }

        // Buttons.
        btnSave = new JButton("Save");
        btnSave.setMnemonic(KeyEvent.VK_S);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                savePressed();
            }
        });
        btnSave.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    savePressed();
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

        btnNoDescription = new JButton("No description");
        btnNoDescription.setMnemonic(KeyEvent.VK_N);
        btnNoDescription.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                noDescriptionPressed();
            }
        });
        btnNoDescription.addKeyListener(new KeyAdapter() {
            /**
             * Invoked when a key has been pressed.
             */
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    noDescriptionPressed();
                }
            }
        });

        // Buttonpanel.
        JPanel jpanButtons = new JPanel();
        jpanButtons.setLayout(new BoxLayout(jpanButtons, BoxLayout.X_AXIS));
        jpanButtons.add(Box.createHorizontalGlue());
        jpanButtons.add(btnSave);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnCancel);
        jpanButtons.add(Box.createHorizontalStrut(5));
        jpanButtons.add(btnNoDescription);
        jpanButtons.add(Box.createHorizontalStrut(10));

        // Adding it all to the main frame.
        this.getContentPane().add(new JScrollPane(txtText), BorderLayout.CENTER);
        this.getContentPane().add(jpanButtons, BorderLayout.SOUTH);
    }

    /**
     * This method is called when the user presses cancel.
     */
    private void savePressed() {
        String text = txtText.getText();
        if(iText == null && text.equals("")) {
            this.iNewText = this.iText;
        } else {
            this.iNewText = text;
        }
        this.dispose();
    }

    /**
     * This method is called when the user presses cancel.
     */
    private void cancelPressed() {
        this.iNewText = this.iText;
        this.dispose();
    }

    /**
     * This method is called when the user clicks the no description button
     */
    private void noDescriptionPressed() {
        this.iNewText = null;
        this.dispose();
    }
}
