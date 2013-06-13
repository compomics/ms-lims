/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 04-mar-2005
 * Time: 18:48:10
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/31 10:22:52 $
 */

/**
 * This class implements a wrapper around the FiledescriptorTableAccessor.
 *
 * @author Lennart Martens
 */
public class Filedescriptor extends FiledescriptorTableAccessor {
    // Class specific log4j logger for Filedescriptor instances.
    private static Logger logger = Logger.getLogger(Filedescriptor.class);

    /**
     * Default constructor.
     */
    public Filedescriptor() {
    }

    /**
     * This constructor maps directly to the equivalent parent precursor.
     *
     * @param aHm HashMap with the parameters.
     * @see com.compomics.mslims.db.accessors.FiledescriptorTableAccessor
     */
    public Filedescriptor(HashMap aHm) {
        super(aHm);
    }

    /**
     * This constructor reads the filedescriptor from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet). The columns should be in
     * this order: <br />
     * <p/>
     * Column 1: filedescriptorid <br /> Column 2: short label <br /> Column 3: description <br /> Column 4: username
     * <br /> Column 5: creationdate <br /> Column 6: modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Filedescriptor(ResultSet aRS) throws SQLException {
        this.iFiledescriptorid = aRS.getLong(1);
        this.iShort_label = aRS.getString(2);
        this.iDescription = aRS.getString(3);
        iUsername = aRS.getString(4);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(5);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(6);
    }

    /**
     * This method finds all filedescriptors from the DB and stores them in a Filedescriptor[].
     *
     * @param aConn               Connection from which to read the filedescriptors.
     * @param aSortedByShortLabel boolean to indicate whether the returned array should be sorted alphabetically by the
     *                            short labels. Sorts when 'true', and returns default DB ordering (typically
     *                            'creationdate') when 'false'.
     * @return Filedescriptor[] with the filedescriptors.
     * @throws java.sql.SQLException when the retrieve failed.
     */
    public static Filedescriptor[] getAllFiledescriptors(Connection aConn, boolean aSortedByShortLabel) throws SQLException {
        Filedescriptor[] filedescriptors = null;

        String sql = "select filedescriptorid, short_label, description, username, creationdate, modificationdate from filedescriptor";
        if (aSortedByShortLabel) {
            sql += " order by short_label";
        }
        PreparedStatement prep = aConn.prepareStatement(sql);
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new Filedescriptor(rs));
        }
        rs.close();
        prep.close();
        filedescriptors = new Filedescriptor[v.size()];
        v.toArray(filedescriptors);

        return filedescriptors;
    }

    /**
     * This method returns a String representation for this filedescriptor (the short label).
     *
     * @return String  with the String description for this filedescriptor.
     */
    public String toString() {
        return this.iShort_label;
    }
}
