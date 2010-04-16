/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2003
 * Time: 16:15:36
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

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
 * $Date: 2009/01/30 10:31:05 $
 */

/**
 * This class provides the following enhancements over the ProjectTableAccessor:
 * <p/>
 * <ul> <li><i>constructor</i>: to read a single Project from a ResultSet.</li> <li><b>toString()</b>: returns the title
 * of the project.</li> <li><b>hashCode()</b>: returns a hashcode for the project (which is just the Project's ID).</li>
 * <li><b>clone()</b>: returns an identical copy of the project.</li> </ul>
 *
 * @author Lennart Martens
 */
public class Quantitation extends QuantitationTableAccessor {
    // Class specific log4j logger for Quantitation instances.
    private static Logger logger = Logger.getLogger(Quantitation.class);

    /**
     * Temporary storage of quantitation file name (primarily for QuantitationValidation stuff)
     */
    private String iQuantitatioFileName;

    /**
     * Default constructor.
     */
    public Quantitation() {
        super();
    }

    /**
     * Wrapper for the superclass constructor.
     *
     * @param aParams HashMap with the parameters.
     */
    public Quantitation(HashMap aParams) {
        super(aParams);
    }

    /**
     * This constructor reads the project from a resultset. The ResultSet should be positioned such that a single row
     * can be read directly (i.e., without calling the 'next()' method on the ResultSet). <br /> The columns should be
     * in this order: <br /> Column 1: quantitationid <br /> Column 2: l_quantitation_groupid <br /> Column 3: ratio <br
     * /> Column 4: standard_error <br /> Column 5: type <br /> Column 6: valid <br /> Column 7: comment <br /> Column
     * 8: username <br /> Column 9: creationdate <br /> Column 19; modificationdate. <br />
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Quantitation(ResultSet aRS) throws SQLException {
        iQuantitationid = aRS.getLong(1);
        iL_quantitation_groupid = aRS.getLong(2);
        iRatio = aRS.getDouble(3);
        iStandard_error = aRS.getDouble(4);
        iType = aRS.getString(5);
        iValid = aRS.getBoolean(6);
        iComment = aRS.getString(7);
        iUsername = aRS.getString(8);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(9);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(10);
    }


    /**
     * This constructor reads the project from a resultset. The ResultSet should be positioned such that a single row
     * can be read directly (i.e., without calling the 'next()' method on the ResultSet). <br /> The columns should be
     * in this order: <br /> Column 1: quantitationid <br /> Column 2: l_quantitation_groupid <br /> Column 3: ratio <br
     * /> Column 4: standard_error <br /> Column 5: type <br /> Column 6: valid <br /> Column 7: comment <br /> Column
     * 8: username <br /> Column 9: creationdate <br /> Column 19; modificationdate. <br />
     *
     * @param aRS                   ResultSet to read the data from.
     * @param aQuantitationFileName String with the quantitation_file name
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Quantitation(ResultSet aRS, String aQuantitationFileName) throws SQLException {
        iQuantitationid = aRS.getLong(1);
        iL_quantitation_groupid = aRS.getLong(2);
        iRatio = aRS.getDouble(3);
        iStandard_error = aRS.getDouble(4);
        iType = aRS.getString(5);
        iValid = aRS.getBoolean(6);
        iComment = aRS.getString(7);
        iUsername = aRS.getString(8);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(9);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(10);
        this.iQuantitatioFileName = aQuantitationFileName;
    }


    /**
     * This method allows the caller to update the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int updateLowPriority(Connection aConn) throws SQLException {
        if (!this.iUpdated) {
            return 0;
        }
        PreparedStatement lStat = aConn.prepareStatement("UPDATE LOW_PRIORITY quantitation SET quantitationid = ?, l_quantitation_groupid = ?, ratio = ?, standard_error = ?, type = ?, valid = ?, comment = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE quantitationid = ?");
        lStat.setLong(1, iQuantitationid);
        lStat.setLong(2, iL_quantitation_groupid);
        lStat.setDouble(3, iRatio);
        lStat.setDouble(4, iStandard_error);
        lStat.setObject(5, iType);
        lStat.setBoolean(6, iValid);
        lStat.setObject(7, iComment);
        lStat.setObject(8, iUsername);
        lStat.setObject(9, iCreationdate);
        lStat.setLong(10, iQuantitationid);
        int result = lStat.executeUpdate();
        lStat.close();
        this.iUpdated = false;
        return result;
    }

    /**
     * This method returns a Vector, containing all quantitation entries that could be retrieved, matching the specified
     * project.
     *
     * @param aProjectid long with the link to the project this Identification must belong to.
     * @param aConn      Connection to read the data from.
     * @return Vector with the matches (can be empty when no matches were found).
     * @throws SQLException when the retrieve failed.
     */
    public static Vector getQuantitationForProject(long aProjectid, Connection aConn) throws SQLException {
        Vector temp = new Vector();

        PreparedStatement ps = aConn.prepareStatement("select q.*, e.filename from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q, quantitation_file as e, quantitation as u where e.quantitation_fileid = q.l_quantitation_fileid and i.l_spectrumid = f.spectrumid and f.l_projectid=? and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid and u.l_quantitation_groupid = q.quantitation_groupid group by u.quantitationid");
        ps.setLong(1, aProjectid);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Quantitation(rs, rs.getString("filename")));
        }
        rs.close();
        ps.close();

        return temp;
    }

    /**
     * This method returns a Vector, containing all quantitation entries that could be retrieved, matching the specified
     * ids.
     *
     * @param aIds  String with comma seperated ids ( 325322,243292,294302,234904,200432 )
     * @param aConn Connection to read the data from.
     * @return Vector with the matches (can be empty when no matches were found).
     * @throws SQLException when the retrieve failed.
     */
    public static Vector getQuantitationForIdentifications(String aIds, Connection aConn) throws SQLException {
        Vector temp = new Vector();
        String query = "select q.*, e.filename from identification_to_quantitation as t, quantitation as q, quantitation_file as e, quantitation_group as g where t.l_identificationid in (" + aIds + ") and t.l_quantitation_groupid = q.l_quantitation_groupid and g.quantitation_groupid = q.l_quantitation_groupid and e.quantitation_fileid = g.l_quantitation_fileid group by q.quantitationid";

        PreparedStatement ps = aConn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Quantitation(rs, rs.getString("filename")));
        }
        rs.close();
        ps.close();

        return temp;
    }

    /**
     * Returns a String representation for this Project.
     *
     * @return String  with the String representation for this Project.
     */
    public String toString() {
        return "Quantitation: id-ratio{ " + iQuantitationid + "-" + iRatio + "}";
    }

    /**
     * This methods gives the quantitiationfile name
     *
     * @return String with the name of the quantitation file
     */
    public String getQuantitationFileName() {
        return iQuantitatioFileName;
    }


    /**
     * Returns a hashcode for the Project. <br /> The hashcode is just the ProjectID, cast to int, which is the PK on
     * the table.
     *
     * @return int with the hashcode
     */
    public int hashCode() {
        return (int) this.iQuantitationid;
    }
}
