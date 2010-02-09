/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2003
 * Time: 16:15:36
 */
package com.compomics.mslims.db.accessors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class provides the following enhancements over the ProjectTableAccessor:
 * <ul> <li><i>constructor</i>: to read a single Project from a ResultSet.</li> <li><b>toString()</b>: returns the title
 * of the project.</li> <li><b>hashCode()</b>: returns a hashcode for the project (which is just the Project's ID).</li>
 * <li><b>clone()</b>: returns an identical copy of the project.</li> </ul>
 *
 * @author Lennart Martens
 */
public class Identification_to_quantitation extends Identification_to_quantitationTableAccessor {

    /** Default constructor. */
    public Identification_to_quantitation() {
        super();
    }

    /**
     * Wrapper for the superclass constructor.
     *
     * @param aParams HashMap with the parameters.
     */
    public Identification_to_quantitation(HashMap aParams) {
        super(aParams);
    }

    /**
     * This constructor reads the project from a resultset. The ResultSet should be positioned such that a single row
     * can be read directly (i.e., without calling the 'next()' method on the ResultSet). <br />
     * The columns should be in this order: <br />
     * Column 1: identification_to_quantitationid ID <br />
     * Column 2: l_identificationid <br />
     * Column 3: l_quantitation_groupid <br />
     * Column 4: type <br />
     * Column 5: username <br />
     * Column 6: creationdate <br />
     * Column 8: modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     *
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Identification_to_quantitation(ResultSet aRS) throws SQLException {
        iItqid = aRS.getLong(1);
        iL_identificationid = aRS.getLong(2);
        iL_quantitation_groupid = aRS.getLong(3);
        iType = aRS.getString(4);
        iUsername = aRS.getString(5);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(6);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(7);
    }

    /**
     * Returns a String representation for this Project.
     *
     * @return String  with the String representation for this Project.
     */
    public String toString() {
        return "Quantitation: id-ratio{ " + iL_identificationid + "-" + iL_quantitation_groupid + "}";
    }


    /**
     * This method get all the identification_to_quantitations for a list of identificationids from the database.
     *
     * @param aConn     Connection to load the identification from.
     * @param aIdentificationIds String with the identificationids seperatated by ','
     *
     * @return Identification_to_quantitation array with the linkers.
     *
     * @throws SQLException when the select failed.
     */
    public static Identification_to_quantitation[] getIdentification_to_quantitationForIdentificationIds(Connection aConn, String aIdentificationIds) throws SQLException {
        Identification_to_quantitation[] result = null;
        String query = "select t.* from identification as i, identification_to_quantitation as t where i.identificationid in (" + aIdentificationIds + ") and i.identificationid = t.l_identificationid";
        PreparedStatement ps = aConn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        Vector<Identification_to_quantitation> lLinkers = new Vector<Identification_to_quantitation>();
        while (rs.next()) {
            lLinkers.add(new Identification_to_quantitation(rs));
        }
        rs.close();
        ps.close();
        result = new Identification_to_quantitation[lLinkers.size()];
        lLinkers.toArray(result);
        return result;
    }

    /**
     * Returns a hashcode for the Project. <br /> The hashcode is just the ProjectID, cast to int, which is the PK on
     * the table.
     *
     * @return int with the hashcode
     */
    public int hashCode() {
        return (int) this.iL_quantitation_groupid;
    }
}