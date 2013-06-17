/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2003
 * Time: 16:15:36
 */
package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class Validation extends ValidationTableAccessor {
    // Class specific log4j logger for Validation instances.
    private static Logger logger = Logger.getLogger(Validation.class);

    /**
     * Default constructor.
     */
    public Validation() {
        super();
    }

    /**
     * Wrapper for the superclass constructor.
     *
     * @param aParams HashMap with the parameters.
     */
    public Validation(HashMap aParams) {
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
    public Validation(ResultSet aRS) throws SQLException {
        iValidationid = aRS.getLong(1);
        iL_identificationid = aRS.getLong(2);
        iL_validationtypeid= aRS.getLong(3);
        iAuto_comment = aRS.getString(4);
        iManual_comment = aRS.getString(5);
        iUsername = aRS.getString(6);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(7);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(8);
    }

    /**
     * Returns the validation instance for the given identificationud.
     *
     * @param l_identificationid The identificationid of the identified MS/MS spectrum.
     * @param aConnection        The ms-lims database connection.
     * @return The Validation object for the identificationid, or null if no validation was found for this identificaitonid.
     * @throws SQLException
     */
    public static Validation getValidation(long l_identificationid, Connection aConnection) throws SQLException {
        String lQuery = "select * from validation where l_identificationid = " + l_identificationid;
        PreparedStatement ps = aConnection.prepareStatement(lQuery);
        ResultSet lResultSet = ps.executeQuery();
        if (lResultSet.next() == true) {
            // Ok, Validation row is found. Return!
            Validation lValidation = new Validation(lResultSet);
            return lValidation;
        } else {
            // No validation is found for this identificationid! Return null.
            return null;
        }
    }

    /**
     * Returns a String representation for this Project.
     *
     * @return String  with the String representation for this Project.
     */
    public String toString() {
        return "Validation{ " + iValidationid + "}";
    }

    /**
     * Returns a hashcode for the Project. <br /> The hashcode is just the ProjectID, cast to int, which is the PK on
     * the table.
     *
     * @return int with the hashcode
     */
    public int hashCode() {
        return (int) this.iValidationid;
    }
}
