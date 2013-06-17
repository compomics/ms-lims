/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 21-jun-2003
 * Time: 16:35:15
 */
package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2005/12/31 12:27:14 $
 */

/**
 * This class provides a wrapper around a LCRunTableAccessor. It defines a comparison (via a Comparable implementation),
 * a toString (for use in listviews), an 'assigned' variable (for use in listviews as well).
 *
 * @author Lennart Martens
 */
public class LCRun extends LcrunTableAccessor implements Comparable {
    // Class specific log4j logger for LCRun instances.
    private static Logger logger = Logger.getLogger(LCRun.class);

    /**
     * This boolean indicates whether this LC run has been assigned to a project already.
     */
    private boolean iAssigned = false;

    /**
     * This String holds the complete pathname for this LC run.
     */
    private String iPathname = null;

    /**
     * This int holds the number of sub-LC's in the folder.
     */
    private int iSubCaplcs = 0;

    /**
     * This constructor takes the LC run name (eg., 'CapLCxxxx' with each 'x' a digit for the Micromass Q-TOF) and the
     * number of spectra (eg., PKL files for the Micromass Q-TOF) in that LC run.
     *
     * @param aName      String with the name for the LC run (typically 'CapLCxxxx' with each 'x' a digit for the
     *                   Micromass Q-TOF).
     * @param aSubLCs    int with the number of sub-LCs in this folder.
     * @param aFileCount long with the number of spectra in the LC run.
     */
    public LCRun(String aName, int aSubLCs, long aFileCount) {
        this.iName = aName;
        this.iSubCaplcs = aSubLCs;
        this.iFilecount = aFileCount;
        this.iAssigned = false;
    }

    /**
     * This constructor reads the LC run from a resultset. The ResultSet should be positioned such that a single row can
     * be read directly (i.e., without calling the 'next()' method on the ResultSet). The columns should be in this
     * order: <br /> Column 1: lcrunid <br /> Column 2: link to projectid <br /> Column 3: description <br /> Column 4:
     * number of files at storage time <br /> Column 5: name for the LC run <br /> Column 6: cd number <br /> Column 7:
     * dvd number <br /> Column 8: primary fraction number <br /> Column 9: username <br /> Column 10: creationdate <br
     * /> Column 11: modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public LCRun(ResultSet aRS) throws SQLException {
        iLcrunid = aRS.getLong(1);
        iL_projectid = aRS.getLong(2);
        ;
        iDescription = aRS.getString(3);
        iFilecount = aRS.getLong(4);
        iName = aRS.getString(5);
        iDvd_master_number = aRS.getLong(6);
        iDvd_secondary_number = aRS.getLong(7);
        iPrimary_fraction = aRS.getLong(8);
        iUsername = aRS.getString(9);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(10);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(11);
    }

    /**
     * This method retrieves all unique LC run names from the database, whose creationdate is no more than the specified
     * number of days in the past. .
     *
     * @param aConn    Connection to read the LC runs from.
     * @param aDaysAgo int with the maximum age (creationdate may not be more then aDaysAgo days ago than today) for the
     *                 LC runs (-1 retrieves all).
     * @return Vector with the names
     * @throws SQLException when the retrieve went wrong.
     */
    public static Vector getUniqueLCRunNames(Connection aConn, int aDaysAgo) throws SQLException {
        String sql = "select distinct(name) from lcrun";
        if (aDaysAgo >= 0) {
            sql += " where TO_DAYS(NOW()) - TO_DAYS(creationdate) <= ?";
        }
        PreparedStatement ps = aConn.prepareStatement(sql);
        if (aDaysAgo >= 0) {
            ps.setInt(1, aDaysAgo);
        }
        ResultSet rs = ps.executeQuery();
        Vector tempVec = new Vector(500, 100);
        while (rs.next()) {
            tempVec.add(rs.getString(1));
        }
        rs.close();
        ps.close();
        // Return result.
        return tempVec;
    }

    /**
     * This method reports an LCRun[] with all LCRuns that have not been assigned a primary fraction yet, and that are
     * younger than the specified maximum age in days (comparing CURRENT_TIMESTAMP to creationdate).
     *
     * @param aConn             Connection to read the LCRuns from.
     * @param aMaximumAgeInDays int with the maximum age in days.
     * @return LCRun[] with the relevant LCRun rows.
     * @throws SQLException when the retrieval failed.
     */
    public static LCRun[] getLCRunsWithoutPrimFractionNotOlderThan(Connection aConn, int aMaximumAgeInDays) throws SQLException {
        LCRun[] result = null;
        StringBuffer query = new StringBuffer("select lcrunid, l_projectid, description, filecount, name, dvd_master_number, dvd_secondary_number, primary_fraction, username, creationdate, modificationdate from lcrun where ");
        if (aMaximumAgeInDays >= 0) {
            query.append("((to_days(CURRENT_TIMESTAMP)-to_days(creationdate)) <= " + aMaximumAgeInDays + ") AND ");
        }
        query.append("(primary_fraction is null OR primary_fraction = 0) order by name");
        PreparedStatement ps = aConn.prepareStatement(query.toString());
        ResultSet rs = ps.executeQuery();
        Vector temp = new Vector(300, 100);
        while (rs.next()) {
            temp.add(new LCRun(rs));
        }
        result = new LCRun[temp.size()];
        temp.toArray(result);
        rs.close();
        ps.close();
        return result;
    }

    /**
     * This method reports an LCRun[] with all LCRuns that are selected using the specified whereclause.
     *
     * @param aConn        Connection to read the LCRuns from.
     * @param aWhereClause String with the whereclause.
     * @return LCRun[] with the relevant LCRun rows.
     * @throws SQLException when the retrieval failed.
     */
    public static LCRun[] getLCRunsWithWhereClause(Connection aConn, String aWhereClause) throws SQLException {
        if (aWhereClause == null) {
            throw new IllegalArgumentException("You specified a 'null' whereclause!");
        }
        LCRun[] result = null;
        StringBuffer query = new StringBuffer("select lcrunid, l_projectid, description, filecount, name, dvd_master_number, dvd_secondary_number, primary_fraction, username, creationdate, modificationdate from lcrun where " + aWhereClause);
        PreparedStatement ps = aConn.prepareStatement(query.toString());
        ResultSet rs = ps.executeQuery();
        Vector temp = new Vector(300, 100);
        while (rs.next()) {
            temp.add(new LCRun(rs));
        }
        result = new LCRun[temp.size()];
        temp.toArray(result);
        rs.close();
        ps.close();
        return result;
    }

    public boolean isAssigned() {
        return iAssigned;
    }

    public void setAssigned(boolean aAssigned) {
        iAssigned = aAssigned;
    }

    /**
     * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive
     * integer as this object is less than, equal to, or greater than the specified object.<p>
     * <p/>
     * The implementor must ensure <tt>sgn(x.compareTo(y)) == -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and
     * <tt>y</tt>.  (This implies that <tt>x.compareTo(y)</tt> must throw an exception iff <tt>y.compareTo(x)</tt>
     * throws an exception.)<p>
     * <p/>
     * The implementor must also ensure that the relation is transitive: <tt>(x.compareTo(y)&gt;0 &amp;&amp;
     * y.compareTo(z)&gt;0)</tt> implies <tt>x.compareTo(z)&gt;0</tt>.<p>
     * <p/>
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt> implies that <tt>sgn(x.compareTo(z)) ==
     * sgn(y.compareTo(z))</tt>, for all <tt>z</tt>.<p>
     * <p/>
     * It is strongly recommended, but <i>not</i> strictly required that <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.
     * Generally speaking, any class that implements the <tt>Comparable</tt> interface and violates this condition
     * should clearly indicate this fact.  The recommended language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this Object.
     */
    public int compareTo(Object o) {
        LCRun other = (LCRun) o;
        int result = this.iName.compareTo(other.iName);
        if (result == 0) {
            result = (int) this.iFilecount - (int) other.iFilecount;
        }

        return result;
    }

    /**
     * Returns a string representation of the object. In general, the <code>toString</code> method returns a string that
     * "textually represents" this object. The result should be a concise but informative representation that is easy
     * for a person to read. It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code> returns a string consisting of the name of the
     * class of which the object is an instance, the at-sign character `<code>@</code>', and the unsigned hexadecimal
     * representation of the hash code of the object. In other words, this method returns a string equal to the value
     * of: <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return this.iName + " (" + this.iSubCaplcs + ", " + this.iFilecount + ")" + ((this.iDescription == null) ? "" : " @");
    }

    public void setPathname(String aPathname) {
        this.iPathname = aPathname;
    }

    public String getPathname() {
        return this.iPathname;
    }
}
