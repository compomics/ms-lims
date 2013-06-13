/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2003
 * Time: 16:15:36
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
 * $Revision: 1.5 $
 * $Date: 2009/03/11 13:57:45 $
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
public class Project extends ProjectTableAccessor {
    // Class specific log4j logger for Project instances.
    private static Logger logger = Logger.getLogger(Project.class);

    /**
     * Default constructor.
     */
    public Project() {
        super();
    }

    /**
     * Wrapper for the superclass constructor.
     *
     * @param aParams HashMap with the parameters.
     */
    public Project(HashMap aParams) {
        super(aParams);
    }

    /**
     * This constructor reads the project from a resultset. The ResultSet should be positioned such that a single row
     * can be read directly (i.e., without calling the 'next()' method on the ResultSet). <br /> The columns should be
     * in this order: <br /> Column 1: project ID <br /> Column 2: link to the person responsible for the project <br />
     * Column 3: link to the PROTOCOL type used for the project <br /> Column 4: title of the project <br /> Column 5:
     * the description for the project Column 6: username <br /> Column 7: creationdate <br /> Column 8:
     * modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Project(ResultSet aRS) throws SQLException {
        iProjectid = aRS.getLong(1);
        iL_userid = aRS.getLong(2);
        iL_protocolid = aRS.getLong(3);
        iTitle = aRS.getString(4);
        iDescription = aRS.getString(5);
        iUsername = aRS.getString(6);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(7);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(8);
    }

    /**
     * This methods reads all projects from the Project table.
     *
     * @param aConn Connection to read the projects from.
     * @return Project[] with the projects in the 'Project' table.
     * @throws SQLException when the retrieving of the projects went wrong.
     */
    public static Project[] getAllProjects(Connection aConn) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select projectid, l_userid, l_protocolid, title, description, username, creationdate, modificationdate from project order by creationdate desc, title asc");
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new Project(rs));
        }
        rs.close();
        prep.close();
        Project[] lProjects = new Project[v.size()];
        v.toArray(lProjects);

        return lProjects;
    }

    /**
     * This methods reads all projects with differential data from the Project table.
     *
     * @param aConn Connection to read the projects from.
     * @return Project[] with the projects in the 'Project' table.
     * @throws SQLException when the retrieving of the projects went wrong.
     */
    public static Project[] getAllDifferentialProjects(Connection aConn) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select distinct p.projectid, p.l_userid, p.l_protocolid, p.title, p.description, p.username, p.creationdate, p.modificationdate from project as p, spectrum as s, identification as i where p.projectid=s.l_projectid and s.spectrumid=i.l_spectrumid and i.light_isotope>0 and i.heavy_isotope>0 order by p.creationdate desc, p.title asc");
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new Project(rs));
        }
        rs.close();
        prep.close();
        Project[] lProjects = new Project[v.size()];
        v.toArray(lProjects);

        return lProjects;
    }

    /**
     * Returns a String representation for this Project.
     *
     * @return String  with the String representation for this Project.
     */
    public String toString() {
        return this.iProjectid + ". " + this.iTitle;
    }

    /**
     * Returns a hashcode for the Project. <br /> The hashcode is just the ProjectID, cast to int, which is the PK on
     * the table.
     *
     * @return int with the hashcode
     */
    public int hashCode() {
        return (int) this.iProjectid;
    }
}
