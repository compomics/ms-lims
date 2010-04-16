package com.compomics.mslims.gui.table;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 17-feb-2005
 * Time: 12:22:06
 */

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2005/12/31 10:22:52 $
 */

import com.compomics.mslims.db.accessors.Identification;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class represents a table model for a List of Identification instances.
 *
 * @author Lennart Martens
 * @version $Id: IdentificationTableModel.java,v 1.2 2005/12/31 10:22:52 lennart Exp $
 */
public class IdentificationTableModel extends AbstractTableModel {
    // Class specific log4j logger for IdentificationTableModel instances.
    private static Logger logger = Logger.getLogger(IdentificationTableModel.class);

    /**
     * The List with the identifications.
     */
    private List iIdentifications = null;

    /**
     * The constructor simply accepts the List of Identifications to display.
     *
     * @param aIdentifications List with the Identification instances to display.
     */
    public IdentificationTableModel(List aIdentifications) {
        this.iIdentifications = aIdentifications;
        // Sorted array of cluster names.
        Collections.sort(iIdentifications, new Comparator() {
            public int compare(Object o1, Object o2) {
                long l1 = ((Identification) o1).getL_spectrumid();
                long l2 = ((Identification) o2).getL_spectrumid();
                return (int) (l1 - l2);
            }
        });
    }

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
            case 0:
                result = "IdentificationID";
                break;
            case 1:
                result = "L_spectrumfileID";
                break;
            case 2:
                result = "L_datfileID";
                break;
            case 3:
                result = "Accession";
                break;
            case 4:
                result = "Start";
                break;
            case 5:
                result = "End";
                break;
            case 6:
                result = "Enzymatic";
                break;
            case 7:
                result = "Sequence";
                break;
            case 8:
                result = "Modified Sequence";
                break;
            case 9:
                result = "Score";
                break;
            case 10:
                result = "Experimental Mass";
                break;
            case 11:
                result = "Calculated Mass";
                break;
            case 12:
                result = "Light Isotope";
                break;
            case 13:
                result = "Heavy Isotope";
                break;
            case 14:
                result = "Valid";
                break;
            case 15:
                result = "Description";
                break;
            case 16:
                result = "Creationdate";
                break;
            case 17:
                result = "Identity Threshold";
                break;
            case 18:
                result = "Confidence";
                break;
            case 19:
                result = "DB";
                break;
            case 20:
                result = "Title";
                break;
            case 21:
                result = "precursor";
                break;
            case 22:
                result = "Charge";
                break;
            case 23:
                result = "Isoforms";
                break;
            case 24:
                result = "DB Filename";
                break;
            case 25:
                result = "Mascot Version";
                break;
        }
        return result;
    }

    /**
     * Returns the number of columns in the model. A <code>JTable</code> uses this method to determine how many columns
     * it should create and display by default.
     *
     * @return the number of columns in the model
     * @see #getRowCount
     */
    public int getColumnCount() {
        return 25;
    }

    /**
     * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        return iIdentifications.size();
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Identification id = (Identification) iIdentifications.get(rowIndex);
        Object result = null;
        switch (columnIndex) {
            case 0:
                result = new Long(id.getIdentificationid());
                break;
            case 1:
                result = new Long(id.getL_spectrumid());
                break;
            case 2:
                result = new Long(id.getL_datfileid());
                break;
            case 3:
                result = id.getAccession();
                break;
            case 4:
                result = new Long(id.getStart());
                break;
            case 5:
                result = new Long(id.getEnd());
                break;
            case 6:
                result = id.getEnzymatic();
                break;
            case 7:
                result = id.getSequence();
                break;
            case 8:
                result = id.getModified_sequence();
                break;
            case 9:
                result = new Long(id.getScore());
                break;
            case 10:
                result = id.getExp_mass();
                break;
            case 11:
                result = id.getCal_mass();
                break;
            case 12:
                result = id.getLight_isotope();
                break;
            case 13:
                result = id.getHeavy_isotope();
                break;
            case 14:
                result = new Integer(id.getValid());
                break;
            case 15:
                result = id.getDescription();
                break;
            case 16:
                result = id.getCreationdate();
                break;
            case 17:
                result = new Long(id.getIdentitythreshold());
                break;
            case 18:
                result = id.getConfidence();
                break;
            case 19:
                result = id.getDb();
                break;
            case 20:
                result = id.getTitle();
                break;
            case 21:
                result = id.getPrecursor();
                break;
            case 22:
                result = new Integer(id.getCharge());
                break;
            case 23:
                result = id.getIsoforms();
                break;
            case 24:
                result = id.getDb_filename();
                break;
            case 25:
                result = id.getMascot_version();
                break;
        }
        return result;
    }
}
