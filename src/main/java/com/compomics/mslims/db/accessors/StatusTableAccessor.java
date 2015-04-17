/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 20/08/2003
 * Time: 16:20:00
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import com.compomics.util.db.interfaces.Deleteable;
import com.compomics.util.db.interfaces.Persistable;
import com.compomics.util.db.interfaces.Retrievable;
import com.compomics.util.db.interfaces.Updateable;

import java.sql.*;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class is a generated accessor for the Status table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class StatusTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for StatusTableAccessor instances.
    private static Logger logger = Logger.getLogger(StatusTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'statusid' column.
     */
    protected long iStatusid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'name' column.
     */
    protected String iName = null;


    /**
     * This variable represents the key for the 'statusid' column.
     */
    public static final String STATUSID = "STATUSID";

    /**
     * This variable represents the key for the 'name' column.
     */
    public static final String NAME = "NAME";


    /**
     * Default constructor.
     */
    public StatusTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'StatusTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public StatusTableAccessor(HashMap aParams) {
        if (aParams.containsKey(STATUSID)) {
            this.iStatusid = ((Long) aParams.get(STATUSID)).longValue();
        }
        if (aParams.containsKey(NAME)) {
            this.iName = (String) aParams.get(NAME);
        }
        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'Statusid' column
     *
     * @return long    with the value for the Statusid column.
     */
    public long getStatusid() {
        return this.iStatusid;
    }

    /**
     * This method returns the value for the 'Name' column
     *
     * @return String    with the value for the Name column.
     */
    public String getName() {
        return this.iName;
    }

    /**
     * This method sets the value for the 'Statusid' column
     *
     * @param aStatusid long with the value for the Statusid column.
     */
    public void setStatusid(long aStatusid) {
        this.iStatusid = aStatusid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Name' column
     *
     * @param aName String with the value for the Name column.
     */
    public void setName(String aName) {
        this.iName = aName;
        this.iUpdated = true;
    }


    /**
     * This method allows the caller to delete the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int delete(Connection aConn) throws SQLException {
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM status WHERE statusid = ?");
        lStat.setLong(1, iStatusid);
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
        if (!aKeys.containsKey(STATUSID)) {
            throw new IllegalArgumentException("Primary key field 'STATUSID' is missing in HashMap!");
        } else {
            iStatusid = ((Long) aKeys.get(STATUSID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM status WHERE statusid = ?");
        lStat.setLong(1, iStatusid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iStatusid = lRS.getLong("statusid");
            iName = (String) lRS.getObject("name");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'status' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'status' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE status SET statusid = ?, name = ? WHERE statusid = ?");
        lStat.setLong(1, iStatusid);
        lStat.setObject(2, iName);
        lStat.setLong(3, iStatusid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO status (statusid, name) values(?, ?)");
        if (iStatusid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iStatusid);
        }
        if (iName == null) {
            lStat.setNull(2, 12);
        } else {
            lStat.setObject(2, iName);
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
