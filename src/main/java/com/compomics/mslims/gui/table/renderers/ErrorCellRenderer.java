/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-nov-2004
 * Time: 12:23:55
 */
package com.compomics.mslims.gui.table.renderers;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/02/02 10:37:43 $
 */

/**
 * This class presents a TableCellRenderer for cells with errors.
 *
 * @author Lennart Martens
 * @version $Id: ErrorCellRenderer.java,v 1.3 2005/02/02 10:37:43 lennart Exp $
 */
public class ErrorCellRenderer extends DefaultTableCellRenderer {
    // Class specific log4j logger for ErrorCellRenderer instances.
    private static Logger logger = Logger.getLogger(ErrorCellRenderer.class);

    /**
     * The original foreground color when unselected.
     */
    private Color iUnselectedOriginalForeground = null;

    /**
     * The original background color when unselected.
     */
    private Color iUnselectedOriginalBackground = null;

    /**
     * The original font when unselected.
     */
    private Font iUnselectedOriginalFont = null;

    /**
     * The original foreground color when selected.
     */
    private Color iSelectedOriginalForeground = null;

    /**
     * The original font when selected.
     */
    private Font iSelectedOriginalFont = null;

    /**
     * Returns the default table cell renderer.
     *
     * @param table      the <code>JTable</code>
     * @param value      the value to assign to the cell at <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus   true if cell has focus
     * @param row        the row of the cell to render
     * @param column     the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Object temp = null;
        boolean isError = false;
        if (value instanceof ErrorObject) {
            temp = ((ErrorObject) value).getValue();
            isError = true;
        } else {
            temp = value;
        }
        TableCellRenderer component = table.getDefaultRenderer(temp.getClass());
        Component result = component.getTableCellRendererComponent(table, temp, isSelected, hasFocus, row, column);
        if (isSelected) {
            if (isError) {
                if (iSelectedOriginalFont == null) {
                    iSelectedOriginalFont = result.getFont();
                }
                if (iSelectedOriginalForeground == null) {
                    iSelectedOriginalForeground = result.getForeground();
                }
                result.setFont(new Font(iSelectedOriginalFont.getName(), Font.BOLD, iSelectedOriginalFont.getSize()));
                ErrorObject eo = (ErrorObject) value;
                // Set the foreground to the background color here, but keep the selection
                // background color (semi-inversion).
                result.setForeground(eo.getBackGround());
                if (result instanceof JComponent) {
                    ((JComponent) result).setToolTipText(eo.getMessage());
                }
            } else {
                if (iSelectedOriginalFont != null) {
                    result.setFont(iSelectedOriginalFont);
                }
                if (iSelectedOriginalForeground != null) {
                    result.setForeground(iSelectedOriginalForeground);
                }
                if (result instanceof JComponent) {
                    ((JComponent) result).setToolTipText("");
                }
            }
        } else {
            if (isError) {
                if (iUnselectedOriginalFont == null) {
                    iUnselectedOriginalFont = result.getFont();
                }
                if (iUnselectedOriginalBackground == null) {
                    iUnselectedOriginalBackground = result.getBackground();
                }
                if (iUnselectedOriginalForeground == null) {
                    iUnselectedOriginalForeground = result.getForeground();
                }
                result.setFont(new Font(iUnselectedOriginalFont.getName(), Font.BOLD, iUnselectedOriginalFont.getSize()));
                ErrorObject eo = (ErrorObject) value;
                result.setBackground(eo.getBackGround());
                result.setForeground(eo.getForeground());
                if (result instanceof JComponent) {
                    ((JComponent) result).setToolTipText(eo.getMessage());
                }
            } else {
                if (iUnselectedOriginalFont != null) {
                    result.setFont(iUnselectedOriginalFont);
                }
                if (iUnselectedOriginalBackground != null) {
                    result.setBackground(iUnselectedOriginalBackground);
                }
                if (iUnselectedOriginalForeground != null) {
                    result.setForeground(iUnselectedOriginalForeground);
                }
                if (result instanceof JComponent) {
                    ((JComponent) result).setToolTipText("");
                }
            }
        }
        return result;
    }
}
