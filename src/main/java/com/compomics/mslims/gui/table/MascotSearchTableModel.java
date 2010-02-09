/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 22-jun-2004
 * Time: 7:55:22
 */
package com.compomics.mslims.gui.table;

import com.compomics.mslims.gui.table.renderers.ErrorObject;
import com.compomics.mslims.gui.tree.MascotSearch;

import javax.swing.table.AbstractTableModel;
import java.awt.*;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2005/02/03 14:27:45 $
 */

/**
 * This class provides a TableModel implementation that is based on
 * Mascot Searches.
 * 
 * @author Lennart Martens
 * @version $Id: MascotSearchTableModel.java,v 1.4 2005/02/03 14:27:45 lennart Exp $
 */
public class MascotSearchTableModel extends AbstractTableModel {

    /**
     * The MascotSearch[] with all the Mascot searches to display on the GUI.
     */
    private MascotSearch[] iSearches = null;

    private static final int REPORT_INSTANCE = -1;
    private static final int TITLE = 0;
    private static final int MERGEFILE = 1;
    private static final int DATFILE = 2;
    private static final int SEARCH_DATABASE = 3;
    private static final int STARTED = 4;
    private static final int ENDED = 5;

    private Color ERROR_FOREGROUND = Color.BLACK;
    private Color ERROR_BACKGROUND = Color.RED;

    private Color WARNING_FOREGROUND = Color.BLACK;
    private Color WARNING_BACKGROUND = Color.YELLOW;


    /**
     * Returns a default name for the column using spreadsheet conventions:
     * A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     * returns an empty string.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName(int column) {
        String result = null;
        switch(column) {
            case TITLE:
                result = "Title";
                break;
            case MERGEFILE:
                result = "Mergefile";
                break;
            case DATFILE:
                result = "Datfile";
                break;
            case SEARCH_DATABASE:
                result = "Search DB";
                break;
            case STARTED:
                result = "Started";
                break;
            case ENDED:
                result = "Ended";
                break;
        }
        return result;
    }

    /**
     * Empty default constructor. Creates an empty table model.
     */
    public MascotSearchTableModel() {
    }

    /**
     * This contructor builds a TableModel based on the MascotSearch instances
     * provided.
     *
     * @param aSearches MascotSearch[] with the searches.
     */
    public MascotSearchTableModel(MascotSearch[] aSearches) {
        this.iSearches = aSearches;
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    public Class getColumnClass(int columnIndex) {
        return ErrorObject.class;    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Returns the number of columns in the model. A
     * <code>JTable</code> uses this method to determine how many columns it
     * should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        return 6;
    }

    /**
     * Returns the number of rows in the model. A
     * <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it
     * is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        int colCount = 0;
        if(iSearches != null) {
            colCount = this.iSearches.length;
        }
        return colCount;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and
     * <code>rowIndex</code>. If the column index is -1, the MascotSearch instance in this
     * row is returned instead.
     *
     * @param	rowIndex	the row whose value is to be queried
     * @param	columnIndex the column whose value is to be queried
     * @return	the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        if(this.iSearches != null) {
            switch(columnIndex) {
                case REPORT_INSTANCE:
                    result = iSearches[rowIndex];
                    break;
                case TITLE:
                    result = iSearches[rowIndex].getTitle();
                    break;
                case MERGEFILE:
                    result = iSearches[rowIndex].getMergefile();
                    break;
                case DATFILE:
                    result = iSearches[rowIndex].getDatfile();
                    break;
                case SEARCH_DATABASE:
                    result = iSearches[rowIndex].getDB();
                    break;
                case STARTED:
                    result = iSearches[rowIndex].getStartDate();
                    break;
                case ENDED:
                    result = iSearches[rowIndex].getEndDate();
                    break;
            }
        }
        // Correct for 'null' or empty String values.
        if(result == null || result.equals("")) {
            result = "<No data>";
        }
        // Error rows need to become ErrorObjects.
        if(columnIndex != REPORT_INSTANCE && iSearches[rowIndex].isError()) {
            result = new ErrorObject("" + result, "This search produced an error and will not be parsed by IdentificationGUI!", ERROR_FOREGROUND, ERROR_BACKGROUND);
        } else if(columnIndex != REPORT_INSTANCE && iSearches[rowIndex].isWarning()) {
            result = new ErrorObject("" + result, "A warning was generated for this search. It will be parsed but certain data may be missing.", WARNING_FOREGROUND, WARNING_BACKGROUND);
        }
        return result;
    }
}
