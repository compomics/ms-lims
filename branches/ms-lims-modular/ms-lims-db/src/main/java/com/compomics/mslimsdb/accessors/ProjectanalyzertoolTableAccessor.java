/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 30/12/2005
 * Time: 12:13:43
 */
package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import java.sql.*;
import java.io.*;
import java.util.*;

import com.compomics.util.db.interfaces.*;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/31 10:22:52 $
 */

/**
 * This class is a generated accessor for the Projectanalyzertool table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ProjectanalyzertoolTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for ProjectanalyzertoolTableAccessor instances.
    private static Logger logger = Logger.getLogger(ProjectanalyzertoolTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'projectanalyzertoolid' column.
     */
    protected long iProjectanalyzertoolid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'toolname' column.
     */
    protected String iToolname = null;


    /**
     * This variable represents the contents for the 'description' column.
     */
    protected String iDescription = null;


    /**
     * This variable represents the contents for the 'toolclassname' column.
     */
    protected String iToolclassname = null;


    /**
     * This variable represents the contents for the 'toolparameters' column.
     */
    protected String iToolparameters = null;


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
     * This variable represents the key for the 'projectanalyzertoolid' column.
     */
    public static final String PROJECTANALYZERTOOLID = "PROJECTANALYZERTOOLID";

    /**
     * This variable represents the key for the 'toolname' column.
     */
    public static final String TOOLNAME = "TOOLNAME";

    /**
     * This variable represents the key for the 'description' column.
     */
    public static final String DESCRIPTION = "DESCRIPTION";

    /**
     * This variable represents the key for the 'toolclassname' column.
     */
    public static final String TOOLCLASSNAME = "TOOLCLASSNAME";

    /**
     * This variable represents the key for the 'toolparameters' column.
     */
    public static final String TOOLPARAMETERS = "TOOLPARAMETERS";

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
    public ProjectanalyzertoolTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'ProjectanalyzertoolTableAccessor' object based on a set of values in
     * the HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public ProjectanalyzertoolTableAccessor(HashMap aParams) {
        if (aParams.containsKey(PROJECTANALYZERTOOLID)) {
            this.iProjectanalyzertoolid = ((Long) aParams.get(PROJECTANALYZERTOOLID)).longValue();
        }
        if (aParams.containsKey(TOOLNAME)) {
            this.iToolname = (String) aParams.get(TOOLNAME);
        }
        if (aParams.containsKey(DESCRIPTION)) {
            this.iDescription = (String) aParams.get(DESCRIPTION);
        }
        if (aParams.containsKey(TOOLCLASSNAME)) {
            this.iToolclassname = (String) aParams.get(TOOLCLASSNAME);
        }
        if (aParams.containsKey(TOOLPARAMETERS)) {
            this.iToolparameters = (String) aParams.get(TOOLPARAMETERS);
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
     * This method returns the value for the 'Projectanalyzertoolid' column
     *
     * @return long    with the value for the Projectanalyzertoolid column.
     */
    public long getProjectanalyzertoolid() {
        return this.iProjectanalyzertoolid;
    }

    /**
     * This method returns the value for the 'Toolname' column
     *
     * @return String    with the value for the Toolname column.
     */
    public String getToolname() {
        return this.iToolname;
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
     * This method returns the value for the 'Toolclassname' column
     *
     * @return String    with the value for the Toolclassname column.
     */
    public String getToolclassname() {
        return this.iToolclassname;
    }

    /**
     * This method returns the value for the 'Toolparameters' column
     *
     * @return String    with the value for the Toolparameters column.
     */
    public String getToolparameters() {
        return this.iToolparameters;
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
     * This method sets the value for the 'Projectanalyzertoolid' column
     *
     * @param aProjectanalyzertoolid long with the value for the Projectanalyzertoolid column.
     */
    public void setProjectanalyzertoolid(long aProjectanalyzertoolid) {
        this.iProjectanalyzertoolid = aProjectanalyzertoolid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Toolname' column
     *
     * @param aToolname String with the value for the Toolname column.
     */
    public void setToolname(String aToolname) {
        this.iToolname = aToolname;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Description' column
     *
     * @param aDescription String with the value for the Description column.
     */
    public void setDescription(String aDescription) {
        this.iDescription = aDescription;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Toolclassname' column
     *
     * @param aToolclassname String with the value for the Toolclassname column.
     */
    public void setToolclassname(String aToolclassname) {
        this.iToolclassname = aToolclassname;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Toolparameters' column
     *
     * @param aToolparameters String with the value for the Toolparameters column.
     */
    public void setToolparameters(String aToolparameters) {
        this.iToolparameters = aToolparameters;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Username' column
     *
     * @param aUsername String with the value for the Username column.
     */
    public void setUsername(String aUsername) {
        this.iUsername = aUsername;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Creationdate' column
     *
     * @param aCreationdate java.sql.Timestamp with the value for the Creationdate column.
     */
    public void setCreationdate(java.sql.Timestamp aCreationdate) {
        this.iCreationdate = aCreationdate;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Modificationdate' column
     *
     * @param aModificationdate java.sql.Timestamp with the value for the Modificationdate column.
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
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM projectanalyzertool WHERE projectanalyzertoolid = ?");
        lStat.setLong(1, iProjectanalyzertoolid);
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
        if (!aKeys.containsKey(PROJECTANALYZERTOOLID)) {
            throw new IllegalArgumentException("Primary key field 'PROJECTANALYZERTOOLID' is missing in HashMap!");
        } else {
            iProjectanalyzertoolid = ((Long) aKeys.get(PROJECTANALYZERTOOLID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM projectanalyzertool WHERE projectanalyzertoolid = ?");
        lStat.setLong(1, iProjectanalyzertoolid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iProjectanalyzertoolid = lRS.getLong("projectanalyzertoolid");
            iToolname = (String) lRS.getObject("toolname");
            iDescription = (String) lRS.getObject("description");
            iToolclassname = (String) lRS.getObject("toolclassname");
            iToolparameters = (String) lRS.getObject("toolparameters");
            iUsername = (String) lRS.getObject("username");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'projectanalyzertool' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'projectanalyzertool' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE projectanalyzertool SET projectanalyzertoolid = ?, toolname = ?, description = ?, toolclassname = ?, toolparameters = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE projectanalyzertoolid = ?");
        lStat.setLong(1, iProjectanalyzertoolid);
        lStat.setObject(2, iToolname);
        lStat.setObject(3, iDescription);
        lStat.setObject(4, iToolclassname);
        lStat.setObject(5, iToolparameters);
        lStat.setObject(6, iUsername);
        lStat.setObject(7, iCreationdate);
        lStat.setLong(8, iProjectanalyzertoolid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO projectanalyzertool (projectanalyzertoolid, toolname, description, toolclassname, toolparameters, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",Statement.RETURN_GENERATED_KEYS);
        if (iProjectanalyzertoolid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iProjectanalyzertoolid);
        }
        if (iToolname == null) {
            lStat.setNull(2, 12);
        } else {
            lStat.setObject(2, iToolname);
        }
        if (iDescription == null) {
            lStat.setNull(3, -1);
        } else {
            lStat.setObject(3, iDescription);
        }
        if (iToolclassname == null) {
            lStat.setNull(4, 12);
        } else {
            lStat.setObject(4, iToolclassname);
        }
        if (iToolparameters == null) {
            lStat.setNull(5, -1);
        } else {
            lStat.setObject(5, iToolparameters);
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
