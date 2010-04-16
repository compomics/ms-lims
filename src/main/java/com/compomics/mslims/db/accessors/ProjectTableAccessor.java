/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 30/12/2005
 * Time: 12:13:31
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import java.sql.*;
import java.io.*;
import java.util.*;

import com.compomics.util.db.interfaces.*;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class is a generated accessor for the Project table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ProjectTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for ProjectTableAccessor instances.
    private static Logger logger = Logger.getLogger(ProjectTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'projectid' column.
     */
    protected long iProjectid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'l_userid' column.
     */
    protected long iL_userid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'l_protocolid' column.
     */
    protected long iL_protocolid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'title' column.
     */
    protected String iTitle = null;


    /**
     * This variable represents the contents for the 'description' column.
     */
    protected String iDescription = null;


    /**
     * This variable represents the contents for the 'username' column.
     */
    protected String iUsername = null;


    /**
     * This variable represents the contents for the 'creationdate' column.
     */
    protected java.sql.Timestamp iCreationdate = null;


    /**
     * This variable represents the contents for the 'modificationdate' column.
     */
    protected java.sql.Timestamp iModificationdate = null;


    /**
     * This variable represents the key for the 'projectid' column.
     */
    public static final String PROJECTID = "PROJECTID";

    /**
     * This variable represents the key for the 'l_userid' column.
     */
    public static final String L_USERID = "L_USERID";

    /**
     * This variable represents the key for the 'l_protocolid' column.
     */
    public static final String L_PROTOCOLID = "L_PROTOCOLID";

    /**
     * This variable represents the key for the 'title' column.
     */
    public static final String TITLE = "TITLE";

    /**
     * This variable represents the key for the 'description' column.
     */
    public static final String DESCRIPTION = "DESCRIPTION";

    /**
     * This variable represents the key for the 'username' column.
     */
    public static final String USERNAME = "USERNAME";

    /**
     * This variable represents the key for the 'creationdate' column.
     */
    public static final String CREATIONDATE = "CREATIONDATE";

    /**
     * This variable represents the key for the 'modificationdate' column.
     */
    public static final String MODIFICATIONDATE = "MODIFICATIONDATE";


    /**
     * Default constructor.
     */
    public ProjectTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'ProjectTableAccessor' object based on a set of values in the HashMap.
     *
     * @param    aParams    HashMap with the parameters to initialize this object with. <i>Please use only constants defined on
     * this class as keys in the HashMap!</i>
     */
    public ProjectTableAccessor(HashMap aParams) {
        if (aParams.containsKey(PROJECTID)) {
            this.iProjectid = ((Long) aParams.get(PROJECTID)).longValue();
        }
        if (aParams.containsKey(L_USERID)) {
            this.iL_userid = ((Long) aParams.get(L_USERID)).longValue();
        }
        if (aParams.containsKey(L_PROTOCOLID)) {
            this.iL_protocolid = ((Long) aParams.get(L_PROTOCOLID)).longValue();
        }
        if (aParams.containsKey(TITLE)) {
            this.iTitle = (String) aParams.get(TITLE);
        }
        if (aParams.containsKey(DESCRIPTION)) {
            this.iDescription = (String) aParams.get(DESCRIPTION);
        }
        if (aParams.containsKey(USERNAME)) {
            this.iUsername = (String) aParams.get(USERNAME);
        }
        if (aParams.containsKey(CREATIONDATE)) {
            this.iCreationdate = (java.sql.Timestamp) aParams.get(CREATIONDATE);
        }
        if (aParams.containsKey(MODIFICATIONDATE)) {
            this.iModificationdate = (java.sql.Timestamp) aParams.get(MODIFICATIONDATE);
        }
        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'Projectid' column
     *
     * @return long    with the value for the Projectid column.
     */
    public long getProjectid() {
        return this.iProjectid;
    }

    /**
     * This method returns the value for the 'L_userid' column
     *
     * @return long    with the value for the L_userid column.
     */
    public long getL_userid() {
        return this.iL_userid;
    }

    /**
     * This method returns the value for the 'L_protocolid' column
     *
     * @return long    with the value for the L_protocolid column.
     */
    public long getL_protocolid() {
        return this.iL_protocolid;
    }

    /**
     * This method returns the value for the 'Title' column
     *
     * @return String    with the value for the Title column.
     */
    public String getTitle() {
        return this.iTitle;
    }

    /**
     * This method returns the value for the 'Description' column
     *
     * @return String    with the value for the Description column.
     */
    public String getDescription() {
        return this.iDescription;
    }

    /**
     * This method returns the value for the 'Username' column
     *
     * @return String    with the value for the Username column.
     */
    public String getUsername() {
        return this.iUsername;
    }

    /**
     * This method returns the value for the 'Creationdate' column
     *
     * @return java.sql.Timestamp    with the value for the Creationdate column.
     */
    public java.sql.Timestamp getCreationdate() {
        return this.iCreationdate;
    }

    /**
     * This method returns the value for the 'Modificationdate' column
     *
     * @return java.sql.Timestamp    with the value for the Modificationdate column.
     */
    public java.sql.Timestamp getModificationdate() {
        return this.iModificationdate;
    }

    /**
     * This method sets the value for the 'Projectid' column
     *
     * @param    aProjectid    long with the value for the Projectid column.
     */
    public void setProjectid(long aProjectid) {
        this.iProjectid = aProjectid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_userid' column
     *
     * @param    aL_userid    long with the value for the L_userid column.
     */
    public void setL_userid(long aL_userid) {
        this.iL_userid = aL_userid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_protocolid' column
     *
     * @param    aL_protocolid    long with the value for the L_protocolid column.
     */
    public void setL_protocolid(long aL_protocolid) {
        this.iL_protocolid = aL_protocolid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Title' column
     *
     * @param    aTitle    String with the value for the Title column.
     */
    public void setTitle(String aTitle) {
        this.iTitle = aTitle;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Description' column
     *
     * @param    aDescription    String with the value for the Description column.
     */
    public void setDescription(String aDescription) {
        this.iDescription = aDescription;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Username' column
     *
     * @param    aUsername    String with the value for the Username column.
     */
    public void setUsername(String aUsername) {
        this.iUsername = aUsername;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Creationdate' column
     *
     * @param    aCreationdate    java.sql.Timestamp with the value for the Creationdate column.
     */
    public void setCreationdate(java.sql.Timestamp aCreationdate) {
        this.iCreationdate = aCreationdate;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Modificationdate' column
     *
     * @param    aModificationdate    java.sql.Timestamp with the value for the Modificationdate column.
     */
    public void setModificationdate(java.sql.Timestamp aModificationdate) {
        this.iModificationdate = aModificationdate;
        this.iUpdated = true;
    }


    /**
     * This method allows the caller to delete the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int delete(Connection aConn) throws SQLException {
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM project WHERE projectid = ?");
        lStat.setLong(1, iProjectid);
        int result = lStat.executeUpdate();
        lStat.close();
        return result;
    }


    /**
     * This method allows the caller to read data for this object from a persistent store based on the specified keys.
     *
     * @param aConn Connection to the persitent store.
     */
    public void retrieve(Connection aConn, HashMap aKeys) throws SQLException {
        // First check to see whether all PK fields are present.
        if (!aKeys.containsKey(PROJECTID)) {
            throw new IllegalArgumentException("Primary key field 'PROJECTID' is missing in HashMap!");
        } else {
            iProjectid = ((Long) aKeys.get(PROJECTID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM project WHERE projectid = ?");
        lStat.setLong(1, iProjectid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iProjectid = lRS.getLong("projectid");
            iL_userid = lRS.getLong("l_userid");
            iL_protocolid = lRS.getLong("l_protocolid");
            iTitle = (String) lRS.getObject("title");
            iDescription = (String) lRS.getObject("description");
            iUsername = (String) lRS.getObject("username");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'project' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'project' table! Object is not initialized correctly!");
        }
    }


    /**
     * This method allows the caller to update the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int update(Connection aConn) throws SQLException {
        if (!this.iUpdated) {
            return 0;
        }
        PreparedStatement lStat = aConn.prepareStatement("UPDATE project SET projectid = ?, l_userid = ?, l_protocolid = ?, title = ?, description = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE projectid = ?");
        lStat.setLong(1, iProjectid);
        lStat.setLong(2, iL_userid);
        lStat.setLong(3, iL_protocolid);
        lStat.setObject(4, iTitle);
        lStat.setObject(5, iDescription);
        lStat.setObject(6, iUsername);
        lStat.setObject(7, iCreationdate);
        lStat.setLong(8, iProjectid);
        int result = lStat.executeUpdate();
        lStat.close();
        this.iUpdated = false;
        return result;
    }


    /**
     * This method allows the caller to insert the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int persist(Connection aConn) throws SQLException {
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO project (projectid, l_userid, l_protocolid, title, description, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        if (iProjectid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iProjectid);
        }
        if (iL_userid == Long.MIN_VALUE) {
            lStat.setNull(2, 4);
        } else {
            lStat.setLong(2, iL_userid);
        }
        if (iL_protocolid == Long.MIN_VALUE) {
            lStat.setNull(3, 4);
        } else {
            lStat.setLong(3, iL_protocolid);
        }
        if (iTitle == null) {
            lStat.setNull(4, 12);
        } else {
            lStat.setObject(4, iTitle);
        }
        if (iDescription == null) {
            lStat.setNull(5, -1);
        } else {
            lStat.setObject(5, iDescription);
        }
        int result = lStat.executeUpdate();

        // Retrieving the generated keys (if any).
        ResultSet lrsKeys = lStat.getGeneratedKeys();
        ResultSetMetaData lrsmKeys = lrsKeys.getMetaData();
        int colCount = lrsmKeys.getColumnCount();
        iKeys = new Object[colCount];
        while (lrsKeys.next()) {
            for (int i = 0; i < iKeys.length; i++) {
                iKeys[i] = lrsKeys.getObject(i + 1);
            }
		}
		lrsKeys.close();
		lStat.close();
		this.iUpdated = false;
		return result;
	}

	/**
     * This method will return the automatically generated key for the insert if one was triggered, or 'null' otherwise.
     *
     * @return Object[]    with the generated keys.
     */
	public Object[] getGeneratedKeys() {
		return this.iKeys;
	}

}
