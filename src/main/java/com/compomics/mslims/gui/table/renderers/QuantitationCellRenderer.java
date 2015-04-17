package com.compomics.mslims.gui.table.renderers;

import com.compomics.mslims.gui.interfaces.TableColor;
import org.apache.log4j.Logger;


import com.compomics.rover.general.interfaces.Ratio;
import com.compomics.rover.general.quantitation.source.distiller.DistillerRatio;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 20-nov-2008 Time: 19:24:31 The 'QuantitationCellRenderer ' class was
 * created for
 */
public class QuantitationCellRenderer extends DefaultTableCellRenderer {
    // Class specific log4j logger for QuantitationCellRenderer instances.
    private static Logger logger = Logger.getLogger(QuantitationCellRenderer.class);

    /* The Table makes use of 4 colors.
     * a dark and light shade for alternating rows
     * and a selected and non-selected color folowing the selection of columns.
     */
    private TableColor iTableColor;

    /**
     * This JLabel will be dynamically modified according the content of the cell.
     */
    private JLabel lbl = null;

    /**
     * Construct a new QuantitationCellRenderer
     */
    public QuantitationCellRenderer() {
        lbl = new JLabel();
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 11));
        iTableColor = new DefaultTableColorImpl();
    }

    /**
     * Returns the component used for drawing the cell.  This method is used to configure the renderer appropriately
     * before drawing.
     *
     * @param table      the <code>JTable</code> that is asking the renderer to draw; can be <code>null</code>
     * @param value      the value of the cell to be rendered.  It is up to the specific renderer to interpret and draw
     *                   the value.  For example, if <code>value</code> is the string "true", it could be rendered as a
     *                   string or it could be rendered as a check box that is checked.  <code>null</code> is a valid
     *                   value
     * @param isSelected true if the cell is to be rendered with the selection highlighted; otherwise false
     * @param hasFocus   if true, render cell appropriately.  For example, put a special border on the cell, if the cell
     *                   can be edited, render in the color used to indicate editing
     * @param row        the row index of the cell being drawn.  When drawing the header, the value of <code>row</code>
     *                   is -1
     * @param column     the column index of the cell being drawn
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        lbl.setOpaque(true);

        // 1. Set color.
        setColor(isSelected, row);

        lbl.setForeground(Color.BLACK);

        if (value instanceof Ratio) {
            lbl.setText("" + ((Ratio) value).getRatio(false));

            if (value instanceof DistillerRatio) {
                // 2. Set Valid
                setValid(((DistillerRatio) value).getValid());
            }

        } else {
            if (value == null) {
                lbl.setText("");
            } else {
                lbl.setText(value.toString());

            }
        }
        // 3. Set text to JLabel

        return lbl;
    }

    /**
     * Sets the color of the JLabel According it's selection status and rownumber.
     *
     * @param aSelected boolean on the selection.
     * @param aRow      int rownumber.
     */
    private void setColor(boolean aSelected, int aRow) {
        // Selection Column
        if (aSelected) {
            if (aRow % 2 == 0) {
                // Equal Selected rows (0,2,4,6,..)
                lbl.setBackground(iTableColor.getSelectedLight());
            } else {
                // Unequal Selected rows (1,3,5,7,..)
                lbl.setBackground(iTableColor.getSelectedDark());
            }
        } else {
            // Non Selected Column.
            if (aRow % 2 == 0) {
                // Equal Non-Selected rows (0,2,4,6,..)
                lbl.setBackground(iTableColor.getNonSelectedLight());
            } else {
                // Unequal Non-Selected rows (1,3,5,7,..)
                lbl.setBackground(iTableColor.getNonSelectedDark());
            }
        }
    }

    private void setValid(boolean valid) {
        if (valid) {
            lbl.setForeground(new Color(0, 150, 0));
        } else {
            lbl.setForeground(new Color(180, 0, 0));
        }
    } // End setValid function

    /**
     * Set the TableColor for the Table.
     *
     * @param aTableColor TableColor implementing Object.
     */
    private void setTableColor(TableColor aTableColor) {
        iTableColor = aTableColor;
    }
}
