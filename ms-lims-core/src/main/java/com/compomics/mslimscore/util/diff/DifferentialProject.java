/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 8-okt-2004
 * Time: 16:56:03
 */
package com.compomics.mslimscore.util.diff;

import org.apache.log4j.Logger;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2004/10/12 11:54:56 $
 */

/**
 * This class represents the data we require for a differential project.
 *
 * @author Lennart Martens
 * @version $Id: DifferentialProject.java,v 1.1 2004/10/12 11:54:56 lennart Exp $
 */
public class DifferentialProject {
    // Class specific log4j logger for DifferentialProject instances.
    private static Logger logger = Logger.getLogger(DifferentialProject.class);

    private long iProjectID = -1;
    private String iProjectTitle = null;
    private String iProjectAlias = null;
    private boolean iInverse = false;

    public DifferentialProject(boolean aInverse, String aProjectAlias, long aProjectID, String aProjectTitle) {
        iInverse = aInverse;
        iProjectAlias = aProjectAlias;
        iProjectID = aProjectID;
        iProjectTitle = aProjectTitle;
    }

    public boolean isInverse() {
        return iInverse;
    }

    public String getProjectAlias() {
        return iProjectAlias;
    }

    public long getProjectID() {
        return iProjectID;
    }

    public String getProjectTitle() {
        return iProjectTitle;
    }

    public String toString() {
        return this.iProjectID + ". " + this.iProjectTitle + " (" + this.iProjectAlias + ", " + (this.iInverse ? "" : "not ") + "inverted) ";
    }
}
