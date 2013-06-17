/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2003
 * Time: 16:15:36
 */
package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2009/06/16 11:53:45 $
 */

/**
 * This class provides the following enhancements over the ProjectTableAccessor: <ul> <li><i>constructor</i>: to read a
 * single Project from a ResultSet.</li> <li><b>toString()</b>: returns the title of the project.</li>
 * <li><b>hashCode()</b>: returns a hashcode for the project (which is just the Project's ID).</li> <li><b>clone()</b>:
 * returns an identical copy of the project.</li> </ul>
 *
 * @author Lennart Martens
 */
public class Validationtype extends ValidationtypeTableAccessor {
    // Class specific log4j logger for Validation instances.
    private static Logger logger = Logger.getLogger(Validationtype.class);

    public static final int NOT_VALIDATED = 0;

    public static final int REJECT_AUTO= -1;
    public static final int ACCEPT_AUTO = 1;

    public static final int REJECT_MANUAL= -2;
    public static final int ACCEPT_MANUAL = 2;

    /**
     * Default constructor.
     */
    public Validationtype() {
        super();
    }

    /**
     * Wrapper for the superclass constructor.
     *
     * @param aParams HashMap with the parameters.
     */
    public Validationtype(HashMap aParams) {
        super(aParams);
    }

    /**
     * This constructor reads the project from a resultset. The ResultSet should be positioned such that a single row
     * can be read directly (i.e., without calling the 'next()' method on the ResultSet). <br /> The columns should be
     * in this order: <br /> Column 1: validation ID <br /> Column 2: l_identificationid <br /> Column 3: comment <br />
     * Column 4: status <br /> Column 5: username <br /> Column 6: creationdate <br /> Column 8: modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Validationtype(ResultSet aRS) throws SQLException {
        iValidationtypeid = aRS.getLong(1);
        iName = aRS.getString(2);
    }



    /**
     * Returns a String representation for this Project.
     *
     * @return String  with the String representation for this Project.
     */
    public String toString() {
        return "Validationtype{ " + iValidationtypeid+ "}";
    }

    /**
     * Returns a hashcode for the Project. <br /> The hashcode is just the ProjectID, cast to int, which is the PK on
     * the table.
     *
     * @return int with the hashcode
     */
    public int hashCode() {
        return (int) this.iValidationtypeid;
    }
}
