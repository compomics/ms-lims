/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2004
 * Time: 18:21:25
 */
package com.compomics.mslimscore.gui.tree;

import org.apache.log4j.Logger;

import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2008/11/28 16:07:18 $
 */

/**
 * This class represents a Mascot Task.
 *
 * @author Lennart Martens
 * @version $Id: MascotTask.java,v 1.3 2008/11/28 16:07:18 kenny Exp $
 */
public class MascotTask {
    // Class specific log4j logger for MascotTask instances.
    private static Logger logger = Logger.getLogger(MascotTask.class);

    /**
     * This field holds the title for the task.
     */
    private String iTitle = null;

    /**
     * This field holds the parameters for the task.
     */
    private String iParams = null;

    /**
     * This field holds the schedule type for the task.
     */
    private String iSchedule = null;

    /**
     * This field holds the status for the task.
     */
    private String iStatus = null;

    /**
     * This Vector holds all the associated MascotSearches.
     */
    private Vector iSearches = null;

    /**
     * The number of this task.
     */
    private int iNumber = 0;

    /**
     * This constructor takes the title of the task as well as the list of datfiles that have resulted from the searches
     * in this task.
     *
     * @param aTitle    String with the title for the task.
     * @param aSearches Vector with all the MascotSearches for this task
     * @param aNumber   int with the number for this task.
     */
    public MascotTask(String aTitle, String aParams, String aSchedule, String aStatus, Vector aSearches, int aNumber) {
        iTitle = aTitle;
        iParams = aParams;
        iSchedule = aSchedule;
        iStatus = aStatus;
        iSearches = aSearches;
        iNumber = aNumber;
    }

    /**
     * This method returns how many searches there are in this task.
     *
     * @return int with the number of searches in this task.
     */
    public int countSearches() {
        return this.iSearches.size();
    }

    public int getNumber() {
        return iNumber;
    }

    public void setNumber(int aNumber) {
        iNumber = aNumber;
    }

    public String getParams() {
        return iParams;
    }

    public void setParams(String aParams) {
        iParams = aParams;
    }

    public String getSchedule() {
        return iSchedule;
    }

    public void setSchedule(String aSchedule) {
        iSchedule = aSchedule;
    }

    public String getStatus() {
        return iStatus;
    }

    public void setStatus(String aStatus) {
        iStatus = aStatus;
    }

    public Vector getSearches() {
        return iSearches;
    }

    public void setSearches(Vector aSearches) {
        iSearches = aSearches;
    }

    public String getTitle() {
        return iTitle;
    }

    public void setTitle(String aTitle) {
        iTitle = aTitle;
    }

    /**
     * This method returns a the String representation of this instance.
     *
     * @return String with the number + ". " + title.
     */
    public String toString() {
        return this.iNumber + ". " + this.iTitle;
    }

    /**
     * This method returns the datfile associated with this index.
     *
     * @param aIndex int with the index for the datfile.
     * @return String with the name of the datfile for this search
     */
    public String getSearchDatfile(int aIndex) {
        return ((MascotSearch) this.iSearches.get(aIndex)).getDatfile();
    }

    /**
     * This method returns the mergefile associated with this index.
     *
     * @param aIndex int with the index for the mergefile.
     * @return String with the name of the mergefile for this search
     */
    public String getSearchMergefile(int aIndex) {
        return ((MascotSearch) this.iSearches.get(aIndex)).getMergefile();
    }

    /**
     * This method returns the MascotSearch at the specified index.
     *
     * @param aIndex int with the index to retrieve the Mascot Search for.
     * @return MascotSearch    with the relevant information.
     */
    public MascotSearch getSearch(int aIndex) {
        return (MascotSearch) this.iSearches.get(aIndex);
    }
}
