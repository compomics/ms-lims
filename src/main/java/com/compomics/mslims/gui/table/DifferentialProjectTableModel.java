/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 8-okt-2004
 * Time: 17:53:22
 */
package com.compomics.mslims.gui.table;

import com.compomics.mslims.util.diff.DifferentialProject;
import org.apache.log4j.Logger;


import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2004/10/12 11:54:56 $
 */

/**
 * This class represents a tablemodel based on differential projects.
 *
 * @author Lennart Martens
 * @version $Id: DifferentialProjectTableModel.java,v 1.1 2004/10/12 11:54:56 lennart Exp $
 */
public class DifferentialProjectTableModel extends AbstractTableModel {
    // Class specific log4j logger for DifferentialProjectTableModel instances.
    private static Logger logger = Logger.getLogger(DifferentialProjectTableModel.class);
    /**
     * The Vector with all the Differential projects to display on the GUI.
     */
    private Vector iProjects = null;

    public static final int REPORT_INSTANCE = -1;
    private static final int ID = 0;
    private static final int TITLE = 1;
    private static final int ALIAS = 2;
    private static final int INVERTED = 3;


    /**
     * Returns a default name for the column using spreadsheet conventions: A, B, C, ... Z, AA, AB, etc.  If
     * <code>column</code> cannot be found, returns an empty string.
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName(int column) {
        String result = null;
        switch (column) {
            case ID:
                result = "Project ID";
                break;
            case TITLE:
                result = "Project title";
                break;
            case ALIAS:
                result = "Project alias";
                break;
            case INVERTED:
                result = "Inverted labelling";
                break;
        }
        return result;
    }

    /**
     * Empty default constructor. Creates an empty table model.
     */
    public DifferentialProjectTableModel() {
        this.iProjects = new Vector();
    }

    /**
     * This contructor builds a TableModel based on the DifferentialProject instances provided in the Vector.
     *
     * @param aProjects Vector with the DifferentialProject instances.
     */
    public DifferentialProjectTableModel(Vector aProjects) {
        this.iProjects = aProjects;
    }

    /**
     * This method takes a DifferentialProject and adds it to the model.
     *
     * @param aProject DifferentialProject to add to the model.
     */
    public void addProject(DifferentialProject aProject) {
        this.iProjects.add(aProject);
        this.fireTableDataChanged();
    }

    /**
     * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns
     * it should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        return 4;
    }

    /**
     * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        return iProjects.size();
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>. If the column index is -1,
     * the DifferentialProject instance in this row is returned instead.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        if (this.iProjects != null) {
            switch (columnIndex) {
                case REPORT_INSTANCE:
                    result = iProjects.get(rowIndex);
                    break;
                case ID:
                    result = new Long(((DifferentialProject) iProjects.get(rowIndex)).getProjectID());
                    break;
                case TITLE:
                    result = ((DifferentialProject) iProjects.get(rowIndex)).getProjectTitle();
                    break;
                case ALIAS:
                    result = ((DifferentialProject) iProjects.get(rowIndex)).getProjectAlias();
                    break;
                case INVERTED:
                    result = "normal";
                    if (((DifferentialProject) iProjects.get(rowIndex)).isInverse()) {
                        result = "inverse";
                    }
                    break;
            }
        }
        return result;
    }

    /**
     * This method reports whether a project with the specified ID and title is already present in the model.
     *
     * @param aId    long with the project ID.
     * @param aTitle String with the project title.
     * @return boolean that is 'true' if the model already contains such a project, or 'false' otherwise.
     */
    public boolean containsProject(long aId, String aTitle) {
        boolean present = false;
        for (int i = 0; i < iProjects.size(); i++) {
            DifferentialProject lProject = (DifferentialProject) iProjects.elementAt(i);
            if (lProject.getProjectID() == aId && lProject.getProjectTitle().equals(aTitle)) {
                present = true;
                break;
            }
        }
        return present;
    }

    /**
     * This method removes the element at the specified row.
     *
     * @param aRow int with the index of the row to remove.
     */
    public void removeElement(int aRow) {
        this.iProjects.remove(aRow);
        fireTableDataChanged();
    }
}
