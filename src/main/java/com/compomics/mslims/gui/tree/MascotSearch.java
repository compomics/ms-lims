/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 21-jun-2004
 * Time: 7:44:49
 */
package com.compomics.mslims.gui.tree;

import org.apache.log4j.Logger;

import java.util.Date;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2009/01/30 10:31:05 $
 */

/**
 * This class represents a single Mascot search.
 *
 * @author Lennart Martens
 * @version $Id: MascotSearch.java,v 1.4 2009/01/30 10:31:05 niklaas Exp $
 */
public class MascotSearch {
    // Class specific log4j logger for MascotSearch instances.
    private static Logger logger = Logger.getLogger(MascotSearch.class);

    private String iTitle = null;
    private String iDatfile = null;
    private String iMergefile = null;
    private String iDB = null;
    /**
     * The String description of the Mascot Distiller project file from this search.
     */
    private String iDistiller_project = null;

    private Date iStartDate = null;
    private Date iEndDate = null;
    /**
     * The parent task id
     */
    private int iParentTaskId = 0;

    private int iStatus = 0;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_ERROR = 1;
    public static final int STATUS_WARNING = 2;

    public MascotSearch(int aStatus, String aTitle, String aDB, String aMergefile, String aDatfile, Date aStartDate, Date aEndDate, int aParentTaskId) {
        this.iStatus = aStatus;
        this.iTitle = aTitle;
        this.iDB = aDB;
        this.iMergefile = aMergefile;
        this.iDatfile = aDatfile;
        this.iStartDate = aStartDate;
        this.iEndDate = aEndDate;
        this.iParentTaskId = aParentTaskId;
    }

    /**
     * Constructs a new MascotSearch instance for the given parameters. A MascotSearch instance is a single task
     * launched by Mascot Daemon.
     *
     * @param aStatus         The completion status of the search
     * @param aTitle          The title of the search
     * @param aDB             The database used for the search
     * @param aMergefile      The spectrum mergefile being searched
     * @param aDatfile        The location of the Mascot results file
     * @param aDistiller_file The Mascot Distiller project file wherefrom this search was started.
     * @param aStartDate      The Date of start
     * @param aEndDate        The Dat of end
     * @param aParentTaskId   The parent task id
     */
    public MascotSearch(int aStatus, String aTitle, String aDB, String aMergefile, String aDatfile, String aDistiller_file, Date aStartDate, Date aEndDate, int aParentTaskId) {
        this.iStatus = aStatus;
        this.iTitle = aTitle;
        this.iDB = aDB;
        this.iMergefile = aMergefile;
        this.iDatfile = aDatfile;
        this.iStartDate = aStartDate;
        this.iEndDate = aEndDate;
        this.iDistiller_project = aDistiller_file;
        this.iParentTaskId = aParentTaskId;
    }


    public String getTitle() {
        return iTitle;
    }

    public void setTitle(String aTitle) {
        iTitle = aTitle;
    }

    public String getDistiller_project() {
        return iDistiller_project;
    }

    /**
     * Set the location of the Mascot Distiller project file.
     *
     * @param aDistiller_project String
     */
    public void setDistiller_project(final String aDistiller_project) {
        iDistiller_project = aDistiller_project;
    }

    /**
     * Returns status of this Search whether or not a Distiller project was assigned to it.
     *
     * @return Boolean with distiller status.
     */
    public boolean hasDistillerProject() {
        return iDistiller_project != null;
    }

    public String getDB() {
        return iDB;
    }

    public void setDB(String aDB) {
        iDB = aDB;
    }

    public Date getEndDate() {
        return iEndDate;
    }

    public void setEndDate(Date aEndDate) {
        iEndDate = aEndDate;
    }

    public Date getStartDate() {
        return iStartDate;
    }

    public void setStartDate(Date aStartDate) {
        iStartDate = aStartDate;
    }

    public String getDatfile() {
        return iDatfile;
    }

    public void setDatfile(String aDatfile) {
        iDatfile = aDatfile;
    }

    public String getMergefile() {
        return iMergefile;
    }

    public void setMergefile(String aMergefile) {
        iMergefile = aMergefile;
    }

    public String toString() {
        return this.iMergefile;
    }

    public boolean isError() {
        return (iStatus == STATUS_ERROR);
    }

    public boolean isWarning() {
        return (iStatus == STATUS_WARNING);
    }

    public int getParentTaskId() {
        return iParentTaskId;
    }

    public void setParentTaskId(int iParentTaskId) {
        this.iParentTaskId = iParentTaskId;
    }

    /**
     * This method returns 'true' when this Mascot search is identical in all its components to the MascotSearch
     * specified. It returns 'false' whenever at least one field differs or the Object specified is not a Mascot
     * Search.
     *
     * @param another Object to which this instance should be compared.
     * @return boolean that indicates whether this instance is equal to the one presented.
     */
    @Override
    public boolean equals(Object another) {
        boolean result = false;

        if (another instanceof MascotSearch) {
            MascotSearch ms = (MascotSearch) another;
            // Different comparisons depending on whether it is an erroneous search or
            // a decent one.
            if (this.isError() || ms.isError()) {
                if (this.iStatus == ms.iStatus && this.iTitle.equals(ms.iTitle) && this.iMergefile.equals(ms.iMergefile)
                        && this.iStartDate.equals(ms.iStartDate) && this.iEndDate.equals(ms.iEndDate) && this.iParentTaskId == ms.iParentTaskId) {
                    result = true;
                }
            } else {
                if (this.iStatus == ms.iStatus && this.iTitle.equals(ms.iTitle) && this.iMergefile.equals(ms.iMergefile)
                        && this.iDatfile.equals(ms.iDatfile) && this.iDB.equals(ms.iDB) && this.iStartDate.equals(ms.iStartDate)
                        && this.iEndDate.equals(ms.iEndDate) && this.iParentTaskId == ms.iParentTaskId) {
                    result = true;
                }
            }
        }

        return result;
    }
}
