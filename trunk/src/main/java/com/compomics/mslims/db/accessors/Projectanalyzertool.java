/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 04-mar-2005
 * Time: 18:48:10
 */
package com.compomics.mslimsdb.accessors;

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
 * $Revision: 1.4 $
 * $Date: 2005/12/31 10:22:52 $
 */

/**
 * This class implements a wrapper around the ProjectanalyzertoolTableAccessor.
 *
 * @author Lennart Martens
 */
public class Projectanalyzertool extends ProjectanalyzertoolTableAccessor {
    // Class specific log4j logger for Projectanalyzertool instances.
    private static Logger logger = Logger.getLogger(Projectanalyzertool.class);

    /**
     * Default constructor.
     */
    public Projectanalyzertool() {
    }

    /**
     * This constructor maps directly to the equivalent parent precursor.
     *
     * @param aHm HashMap with the parameters.
     * @see ProjectanalyzertoolTableAccessor
     */
    public Projectanalyzertool(HashMap aHm) {
        super(aHm);
    }

    /**
     * This constructor reads the projectanalyzertool from a resultset. The ResultSet should be positioned such that a
     * single row can be read directly (i.e., without calling the 'next()' method on the ResultSet). The columns should
     * be in this order: <br />
     * <p/>
     * Column 1: projectanalyzertoolid <br /> Column 2: tool name <br /> Column 3: description <br /> Column 4:
     * toolclassname <br /> Column 5: toolparameters <br /> Column 6: username <br /> Column 7: creationdate <br />
     * Column 8: modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Projectanalyzertool(ResultSet aRS) throws SQLException {
        this.iProjectanalyzertoolid = aRS.getLong(1);
        this.iToolname = aRS.getString(2);
        this.iDescription = aRS.getString(3);
        this.iToolclassname = aRS.getString(4);
        this.iToolparameters = aRS.getString(5);
        iUsername = aRS.getString(6);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(7);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(8);
    }

    /**
     * This method finds all projectanalyzertools from the DB and stores them in a Projectanalyzertool[].
     *
     * @param aConn             Connection from which to read the projectanalyzertool.
     * @param aSortedByToolName boolean to indicate whether the returned array should be sorted alphabetically by the
     *                          tool name. Sorts when 'true', and returns default DB ordering (typically 'creationdate')
     *                          when 'false'.
     * @return Projectanalyzertool[] with the projectanalyzertools.
     * @throws java.sql.SQLException when the retrieve failed.
     */
    public static Projectanalyzertool[] getAllProjectanalyzertools(Connection aConn, boolean aSortedByToolName) throws SQLException {
        Projectanalyzertool[] projectanalyzertools = null;

        String sql = "select projectanalyzertoolid, toolname, description, toolclassname, toolparameters, username, creationdate, modificationdate from projectanalyzertool";
        if (aSortedByToolName) {
            sql += " order by toolname";
        }
        PreparedStatement prep = aConn.prepareStatement(sql);
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new Projectanalyzertool(rs));
        }
        rs.close();
        prep.close();
        projectanalyzertools = new Projectanalyzertool[v.size()];
        v.toArray(projectanalyzertools);

        return projectanalyzertools;
    }

    /**
     * This method returns a String representation for this projectanalyzertool (the tool name).
     *
     * @return String  with the String description for this projectanalyzertool.
     */
    public String toString() {
        return this.iToolname;
    }
}
