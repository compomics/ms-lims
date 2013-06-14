/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 8-okt-2004
 * Time: 17:53:22
 */
package com.compomics.mslimscore.gui.table;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.gui.table.renderers.ErrorObject;
import com.compomics.mslimscore.util.diff.DiffAnalysisCore;
import com.compomics.mslimscore.util.diff.DiffCouple;
import com.compomics.mslimscore.util.diff.DifferentialProject;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.6 $
 * $Date: 2005/02/23 08:58:41 $
 */

/**
 * This class represents a tablemodel based on differential couples.
 *
 * @author Lennart Martens
 * @version $Id: DiffCoupleTableModel.java,v 1.6 2005/02/23 08:58:41 lennart Exp $
 */
public class DiffCoupleTableModel extends AbstractTableModel {
    // Class specific log4j logger for DiffCoupleTableModel instances.
    private static Logger logger = Logger.getLogger(DiffCoupleTableModel.class);
    /**
     * The Vector with all the Differential couples to display on the GUI.
     */
    private Vector iCouples = null;

    /**
     * The HashMap with projects. These should be keyed by their project IDs.
     */
    private HashMap iProjects = null;

    /**
     * The label for the light isotope sample.
     */
    private String iLightLabel = null;
    /**
     * The label for the heavy isotope sample.
     */
    private String iHeavyLabel = null;

    /**
     * The averaging method.
     */
    private int iAverageMethod = -1;

    public static final int REPORT_INSTANCE = -1;
    private static final int PROJECTID = 0;
    private static final int PROJECTTITLE = 1;
    private static final int PROJECTALIAS = 2;
    private static final int PROJECTINVERTED = 3;
    private static final int LIGHTINTENSITY = 4;
    private static final int HEAVYINTENSITY = 5;
    private static final int RATIO = 6;
    private static final int LOG2RATIO = 7;
    private static final int SIGNIFICANCE = 8;
    private static final int SIGNIFICANCE_TYPE = 9;
    private static final int COUNT = 10;
    private static final int FILENAME = 11;
    private static final int ACCESSION = 12;
    private static final int START = 13;
    private static final int END = 14;
    private static final int ENZYMATIC = 15;
    private static final int SEQUENCE = 16;
    private static final int MODIFIEDSEQUENCE = 17;
    private static final int DESCRIPTION = 18;
    private static final int OUTLIERS = 19;


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
            case PROJECTID:
                result = "Project ID";
                break;
            case PROJECTTITLE:
                result = "Project title";
                break;
            case PROJECTALIAS:
                result = "Project alias";
                break;
            case PROJECTINVERTED:
                result = "Inverted labelling";
                break;
            case LIGHTINTENSITY:
                result = "Light intensity";
                break;
            case HEAVYINTENSITY:
                result = "Heavy intensity";
                break;
            case RATIO:
                result = "Ratio (light/heavy)";
                break;
            case LOG2RATIO:
                result = "Log2(ratio)";
                break;
            case SIGNIFICANCE:
                result = "Significance";
                break;
            case SIGNIFICANCE_TYPE:
                result = "Significance for ...";
                break;
            case COUNT:
                result = "Mergecount";
                break;
            case FILENAME:
                result = "Filename";
                break;
            case ACCESSION:
                result = "Accession";
                break;
            case START:
                result = "Start";
                break;
            case END:
                result = "End";
                break;
            case ENZYMATIC:
                result = "Enzymatic";
                break;
            case SEQUENCE:
                result = "Sequence";
                break;
            case MODIFIEDSEQUENCE:
                result = "Modified sequence";
                break;
            case DESCRIPTION:
                result = "Description";
                break;
            case OUTLIERS:
                result = "Outliers present";
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
        return ErrorObject.class;
    }

    /**
     * Empty default constructor. Creates an empty table model.
     */
    public DiffCoupleTableModel() {
        this.iCouples = new Vector();
        this.iProjects = new HashMap();
    }


    /**
     * This contructor builds a TableModel based on the DifferentialProject instances provided in the Vector. If a
     * DiffCouple represents a cluster, the ratios is calculated from the weighted ratios.
     *
     * @param aCouples    Vector with the DiffCouples to display.
     * @param aProjects   HashMap with the DifferentialProject instances keyed by a Long representing their Project ID.
     * @param aLightLabel String with the light label description
     * @param aHeavyLabel String with the heavy label description
     */
    public DiffCoupleTableModel(Vector aCouples, HashMap aProjects, String aLightLabel, String aHeavyLabel) {
        this(aCouples, aProjects, aLightLabel, aHeavyLabel, DiffAnalysisCore.WEIGHTED_RATIOS);
    }

    /**
     * This contructor builds a TableModel based on the DifferentialProject instances provided in the Vector.
     *
     * @param aCouples       Vector with the DiffCouples to display.
     * @param aProjects      HashMap with the DifferentialProject instances keyed by a Long representing their Project
     *                       ID.
     * @param aLightLabel    String with the light label description
     * @param aHeavyLabel    String with the heavy label description
     * @param aAverageMethod int with the method for calculating the averages. Please use the constants defined on the
     *                       DiffAnalysisCore class.
     */
    public DiffCoupleTableModel(Vector aCouples, HashMap aProjects, String aLightLabel, String aHeavyLabel, int aAverageMethod) {
        this.iCouples = aCouples;
        this.iProjects = aProjects;
        this.iLightLabel = aLightLabel;
        this.iHeavyLabel = aHeavyLabel;
        this.iAverageMethod = aAverageMethod;
    }

    /**
     * This method takes a DiffCouple and adds it to the model.
     *
     * @param aCouple DiffCouple to add to the model.
     */
    public void addCouple(DiffCouple aCouple) {
        this.iCouples.add(aCouple);
        this.fireTableDataChanged();
    }

    /**
     * This method takes a DifferentialProjectand adds it to the model.
     *
     * @param aProject DifferentialProject to add to the model.
     */
    public void addProject(DifferentialProject aProject) {
        this.iProjects.put(new Long(aProject.getProjectID()), aProject);
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
        return 20;
    }

    /**
     * Returns the number of rows in the model. A <code>JTable</code> uses this method to determine how many rows it
     * should display.  This method should be quick, as it is called frequently during rendering.
     *
     * @return the number of rows in the model
     * @see #getColumnCount
     */
    public int getRowCount() {
        return iCouples.size();
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
        if (this.iCouples != null) {
            DiffCouple dc = (DiffCouple) iCouples.get(rowIndex);
            Long id = new Long(dc.getProjectID());
            // Check the DiffCouple; if it is composite, we will calculate
            // whether it has outliers. If it has, we'll highlight it.
            int outliers = 0;
            outliers = dc.checkOutliers();

            switch (columnIndex) {
                case REPORT_INSTANCE:
                    result = iCouples.get(rowIndex);
                    break;
                case PROJECTID:
                    result = id;
                    break;
                case PROJECTTITLE:
                    result = ((DifferentialProject) iProjects.get(id)).getProjectTitle();
                    break;
                case PROJECTALIAS:
                    StringBuffer alias = new StringBuffer(((DifferentialProject) iProjects.get(id)).getProjectAlias());
                    // See if there are more projects from which this
                    // couple was merged.
                    if (dc.getCount() > 1) {
                        Vector v = dc.getMergedEntries();
                        int liSize = v.size();
                        for (int i = 0; i < liSize; i++) {
                            DiffCouple child = (DiffCouple) v.get(i);
                            alias.append(" " + ((DifferentialProject) iProjects.get(new Long(child.getProjectID()))).getProjectAlias());
                        }
                    }
                    result = alias.toString();
                    break;
                case PROJECTINVERTED:
                    result = "normal";
                    if (((DifferentialProject) iProjects.get(id)).isInverse()) {
                        result = "inverse";
                    }
                    break;
                case LIGHTINTENSITY:
                    result = new Double(dc.getLightIntensity());
                    break;
                case HEAVYINTENSITY:
                    result = new Double(dc.getHeavyIntensity());
                    break;
                case RATIO:
                    switch (iAverageMethod) {
                        case DiffAnalysisCore.WEIGHTED_RATIOS:
                            result = new Double(dc.getRatioAsWeightedRatio());
                            break;
                        case DiffAnalysisCore.AVERAGE_RATIOS:
                            result = new Double(dc.getRatioAsAverageRatio());
                            break;
                        default:
                            result = new Double(dc.getRatio());
                            break;
                    }
                    break;
                case LOG2RATIO:
                    switch (iAverageMethod) {
                        case DiffAnalysisCore.WEIGHTED_RATIOS:
                            result = new Double(dc.getLog2RatioAsWeightedRatio());
                            break;
                        case DiffAnalysisCore.AVERAGE_RATIOS:
                            result = new Double(dc.getLog2RatioAsAverageRatio());
                            break;
                        default:
                            result = new Double(dc.getLog2Ratio());
                            break;
                    }
                    break;
                case SIGNIFICANCE:
                    result = new Double(dc.getSignificance());
                    break;
                case SIGNIFICANCE_TYPE:
                    if (dc.getRatio() > 1) {
                        result = iLightLabel;
                    } else if (dc.getRatio() < 1) {
                        result = iHeavyLabel;
                    } else {
                        result = "1/1";
                    }
                    break;
                case COUNT:
                    result = new Integer(dc.getCount());
                    break;
                case FILENAME:
                    result = dc.getFilename();
                    break;
                case ACCESSION:
                    result = dc.getAccession();
                    break;
                case START:
                    result = new Integer(dc.getStart());
                    break;
                case END:
                    result = new Integer(dc.getEnd());
                    break;
                case ENZYMATIC:
                    result = dc.getEnzymatic();
                    break;
                case SEQUENCE:
                    result = dc.getSequence();
                    break;
                case MODIFIEDSEQUENCE:
                    result = dc.getModifiedSequence();
                    break;
                case DESCRIPTION:
                    result = dc.getDescription();
                    break;
                case OUTLIERS:
                    result = new Integer(outliers);
                    break;
            }
            if (outliers == 95) {
                result = new ErrorObject(result, "Clustered couple with outliers at the 95% confidence interval!", Color.black, Color.yellow);
            } else if (outliers == 98) {
                result = new ErrorObject(result, "Clustered couple with outliers at the 98% confidence interval!", Color.black, Color.red);
            }
        }
        return result;
    }
}
