/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 22-jun-2004
 * Time: 16:43:06
 */
package com.compomics.mslims.gui.table;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.IdentificationTableAccessor;
import com.compomics.mslims.db.accessors.Identification;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2007/10/05 10:12:10 $
 */

/**
 * This class implements a TableModel for IdentificationTableAccessors.
 *
 * @author Lennart Martens
 * @version $Id: IdentificationTableAccessorsTableModel.java,v 1.4 2007/10/05 10:12:10 lennart Exp $
 */
public class IdentificationTableAccessorsTableModel extends AbstractTableModel {
    // Class specific log4j logger for IdentificationTableAccessorsTableModel instances.
    private static Logger logger = Logger.getLogger(IdentificationTableAccessorsTableModel.class);

    /**
     * The data to display.
     */
    private Identification[] iData = null;

    private static final int FILENAME = 0;
    private static final int ACCESSION = 1;
    private static final int SEQUENCE = 2;
    private static final int MODIFIED_SEQUENCE = 3;
    private static final int ION_COVERAGE = 4;
    private static final int START = 5;
    private static final int END = 6;
    private static final int DESCRIPTION = 7;
    private static final int TITLE = 8;
    private static final int SCORE = 9;
    private static final int IDENTITYTHRESHOLD = 10;
    private static final int CONFIDENCE = 11;
    private static final int CAL_MASS = 12;
    private static final int EXP_MASS = 13;
    private static final int ISOFORMS = 14;
    private static final int PRECURSOR = 15;
    private static final int CHARGE = 16;
    private static final int ENZYMATIC = 17;
    private static final int DATFILE = 18;
    private static final int DB_FILENAME = 19;
    private static final int DB = 20;
    private static final int MASCOT_VERSION = 21;

    /**
     * This constructor will automatically read only the IdentificationTableAccessor instances from the Vector with
     * candidates.
     *
     * @param aCandidiates Collection with candidates for retention.
     */
    public IdentificationTableAccessorsTableModel(Vector aCandidiates) {
        // First cycle and count.
        int count = 0;
        int liSize = aCandidiates.size();
        for (int i = 0; i < liSize; i++) {
            Object o = aCandidiates.get(i);
            if (o instanceof IdentificationTableAccessor) {
                count++;
            }
        }
        // Okay, init the array.
        iData = new Identification[count];
        count = 0;
        for (int i = 0; i < liSize; i++) {
            Object o = aCandidiates.get(i);
            if (o instanceof IdentificationTableAccessor) {
                iData[count] = (Identification) o;
                count++;
            }
        }
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
            case FILENAME:
                result = "Filename";
                break;
            case ACCESSION:
                result = "Accession";
                break;
            case SEQUENCE:
                result = "Sequence";
                break;
            case MODIFIED_SEQUENCE:
                result = "Modified Sequence";
                break;
            case ION_COVERAGE:
                result = "Ion Coverage";
                break;
            case START:
                result = "Start";
                break;
            case END:
                result = "End";
                break;
            case DESCRIPTION:
                result = "Description";
                break;
            case TITLE:
                result = "Title";
                break;
            case SCORE:
                result = "Score";
                break;
            case IDENTITYTHRESHOLD:
                result = "Identity threshold";
                break;
            case CONFIDENCE:
                result = "Confidence interval";
                break;
            case CAL_MASS:
                result = "Calculated mass";
                break;
            case EXP_MASS:
                result = "Experimental mass";
                break;
            case ISOFORMS:
                result = "Isoforms";
                break;
            case PRECURSOR:
                result = "Precursor (m/z)";
                break;
            case CHARGE:
                result = "Charge";
                break;
            case ENZYMATIC:
                result = "Enzymatic";
                break;
            case DATFILE:
                result = "Datfile";
                break;
            case DB_FILENAME:
                result = "Search DB filename";
                break;
            case DB:
                result = "Search DB";
                break;
            case MASCOT_VERSION:
                result = "Mascot version";
                break;
        }

        return result;
    }

    /**
     * Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    public Class getColumnClass(int columnIndex) {
        Class result = null;

        switch (columnIndex) {
            case FILENAME:
                result = String.class;
                break;
            case ACCESSION:
                result = String.class;
                break;
            case SEQUENCE:
                result = String.class;
                break;
            case MODIFIED_SEQUENCE:
                result = String.class;
                break;
            case ION_COVERAGE:
                result = String.class;
                break;
            case START:
                result = Long.class;
                break;
            case END:
                result = Long.class;
                break;
            case DESCRIPTION:
                result = String.class;
                break;
            case TITLE:
                result = String.class;
                break;
            case SCORE:
                result = Double.class;
                break;
            case IDENTITYTHRESHOLD:
                result = Long.class;
                break;
            case CONFIDENCE:
                result = Double.class;
                break;
            case CAL_MASS:
                result = Double.class;
                break;
            case EXP_MASS:
                result = Double.class;
                break;
            case ISOFORMS:
                result = String.class;
                break;
            case PRECURSOR:
                result = Double.class;
                break;
            case CHARGE:
                result = Integer.class;
                break;
            case ENZYMATIC:
                result = String.class;
                break;
            case DATFILE:
                result = String.class;
                break;
            case DB_FILENAME:
                result = String.class;
                break;
            case DB:
                result = String.class;
                break;
            case MASCOT_VERSION:
                result = String.class;
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
        return 21;
    }

    /**
     * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        int result = 0;
        if (iData != null) {
            result = iData.length;
        }
        return result;
    }

    /**
     * Returns the value for the cell at <code>columnIndex</code> and <code>rowIndex</code>.
     *
     * @param rowIndex    the row whose value is to be queried
     * @param columnIndex the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;
        if (iData != null) {
            switch (columnIndex) {
                case FILENAME:
                    result = iData[rowIndex].getTemporarySpectrumfilename();
                    break;
                case ACCESSION:
                    result = iData[rowIndex].getAccession();
                    break;
                case SEQUENCE:
                    result = iData[rowIndex].getSequence();
                    break;
                case MODIFIED_SEQUENCE:
                    result = iData[rowIndex].getModified_sequence();
                    break;
                case ION_COVERAGE:
                    result = iData[rowIndex].getIon_coverage();
                    break;
                case START:
                    result = new Long(iData[rowIndex].getStart());
                    break;
                case END:
                    result = new Long(iData[rowIndex].getEnd());
                    break;
                case DESCRIPTION:
                    result = iData[rowIndex].getDescription();
                    break;
                case TITLE:
                    result = iData[rowIndex].getTitle();
                    break;
                case SCORE:
                    result = new Long(iData[rowIndex].getScore());
                    break;
                case IDENTITYTHRESHOLD:
                    result = new Long(iData[rowIndex].getIdentitythreshold());
                    break;
                case CONFIDENCE:
                    result = iData[rowIndex].getConfidence();
                    break;
                case CAL_MASS:
                    result = iData[rowIndex].getCal_mass();
                    break;
                case EXP_MASS:
                    result = iData[rowIndex].getExp_mass();
                    break;
                case ISOFORMS:
                    result = iData[rowIndex].getIsoforms();
                    break;
                case PRECURSOR:
                    result = iData[rowIndex].getPrecursor();
                    break;
                case CHARGE:
                    result = new Integer(iData[rowIndex].getCharge());
                    break;
                case ENZYMATIC:
                    result = iData[rowIndex].getEnzymatic();
                    break;
                case DATFILE:
                    result = iData[rowIndex].getTemporaryDatfilename();
                    break;
                case DB_FILENAME:
                    result = iData[rowIndex].getDb_filename();
                    break;
                case DB:
                    result = iData[rowIndex].getDb();
                    break;
                case MASCOT_VERSION:
                    result = iData[rowIndex].getMascot_version();
                    break;
            }
        }
        return result;
    }
}
