package com.compomics.mslims.gui.table.renderers;

import com.compomics.mslims.db.accessors.Identification;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Davy
 * Date: 6/29/12
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class MasterAlternativeRenderer extends DefaultTableCellRenderer {

    private Font iSelectedOriginalFont;

    /**
     * Construct a new masterAlternativeSwitcherCellRenderer
     */

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

    Object temp;
    boolean hasAlternative = false;
    if (value instanceof Identification) {
        temp = value;
        hasAlternative = ((Identification) value).getAlternative();
    } else {
        temp = value;
    }

        TableCellRenderer component = table.getDefaultRenderer(temp.getClass());
        Component result = component.getTableCellRendererComponent(table, temp, isSelected, hasFocus, row, column);
        if (hasAlternative){
            if (iSelectedOriginalFont == null) {
                iSelectedOriginalFont = result.getFont();
            }
            result.setFont(new Font(iSelectedOriginalFont.getName(), Font.BOLD, iSelectedOriginalFont.getSize()));
        }

        return result;
    }
}
