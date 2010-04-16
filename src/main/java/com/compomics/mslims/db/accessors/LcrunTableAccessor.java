/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 30/12/2005
 * Time: 12:13:12
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
 * $Date: 2005/12/31 10:22:52 $
 */

/**
 * This class is a generated accessor for the Lcrun table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class LcrunTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for LcrunTableAccessor instances.
    private static Logger logger = Logger.getLogger(LcrunTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'lcrunid' column.
     */
    protected long iLcrunid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'l_projectid' column.
     */
    protected long iL_projectid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'description' column.
     */
    protected String iDescription = null;


    /**
     * This variable represents the contents for the 'filecount' column.
     */
    protected long iFilecount = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'name' column.
     */
    protected String iName = null;


    /**
     * This variable represents the contents for the 'dvd_master_number' column.
     */
    protected long iDvd_master_number = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'dvd_secondary_number' column.
     */
    protected long iDvd_secondary_number = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'primary_fraction' column.
     */
    protected long iPrimary_fraction = Long.MIN_VALUE;


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
     * This variable represents the key for the 'lcrunid' column.
     */
    public static final String LCRUNID = "LCRUNID";

    /**
     * This variable represents the key for the 'l_projectid' column.
     */
    public static final String L_PROJECTID = "L_PROJECTID";

    /**
     * This variable represents the key for the 'description' column.
     */
    public static final String DESCRIPTION = "DESCRIPTION";

    /**
     * This variable represents the key for the 'filecount' column.
     */
    public static final String FILECOUNT = "FILECOUNT";

    /**
     * This variable represents the key for the 'name' column.
     */
    public static final String NAME = "NAME";

    /**
     * This variable represents the key for the 'dvd_master_number' column.
     */
    public static final String DVD_MASTER_NUMBER = "DVD_MASTER_NUMBER";

    /**
     * This variable represents the key for the 'dvd_secondary_number' column.
     */
    public static final String DVD_SECONDARY_NUMBER = "DVD_SECONDARY_NUMBER";

    /**
     * This variable represents the key for the 'primary_fraction' column.
     */
    public static final String PRIMARY_FRACTION = "PRIMARY_FRACTION";

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
    public LcrunTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'LcrunTableAccessor' object based on a set of values in the HashMap.
     *
     * @param    aParams    HashMap with the parameters to initialize this object with. <i>Please use only constants defined on
     * this class as keys in the HashMap!</i>
     */
    public LcrunTableAccessor(HashMap aParams) {
        if (aParams.containsKey(LCRUNID)) {
            this.iLcrunid = ((Long) aParams.get(LCRUNID)).longValue();
        }
        if (aParams.containsKey(L_PROJECTID)) {
            this.iL_projectid = ((Long) aParams.get(L_PROJECTID)).longValue();
        }
        if (aParams.containsKey(DESCRIPTION)) {
            this.iDescription = (String) aParams.get(DESCRIPTION);
        }
        if (aParams.containsKey(FILECOUNT)) {
            this.iFilecount = ((Long) aParams.get(FILECOUNT)).longValue();
        }
        if (aParams.containsKey(NAME)) {
            this.iName = (String) aParams.get(NAME);
        }
        if (aParams.containsKey(DVD_MASTER_NUMBER)) {
            this.iDvd_master_number = ((Long) aParams.get(DVD_MASTER_NUMBER)).longValue();
        }
        if (aParams.containsKey(DVD_SECONDARY_NUMBER)) {
            this.iDvd_secondary_number = ((Long) aParams.get(DVD_SECONDARY_NUMBER)).longValue();
        }
        if (aParams.containsKey(PRIMARY_FRACTION)) {
            this.iPrimary_fraction = ((Long) aParams.get(PRIMARY_FRACTION)).longValue();
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
     * This method returns the value for the 'Lcrunid' column
     *
     * @return long    with the value for the Lcrunid column.
     */
    public long getLcrunid() {
        return this.iLcrunid;
    }

    /**
     * This method returns the value for the 'L_projectid' column
     *
     * @return long    with the value for the L_projectid column.
     */
    public long getL_projectid() {
        return this.iL_projectid;
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
     * This method returns the value for the 'Filecount' column
     *
     * @return long    with the value for the Filecount column.
     */
    public long getFilecount() {
        return this.iFilecount;
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
     * This method returns the value for the 'Dvd_master_number' column
     *
     * @return long    with the value for the Dvd_master_number column.
     */
    public long getDvd_master_number() {
        return this.iDvd_master_number;
    }

    /**
     * This method returns the value for the 'Dvd_secondary_number' column
     *
     * @return long    with the value for the Dvd_secondary_number column.
     */
    public long getDvd_secondary_number() {
        return this.iDvd_secondary_number;
    }

    /**
     * This method returns the value for the 'Primary_fraction' column
     *
     * @return long    with the value for the Primary_fraction column.
     */
    public long getPrimary_fraction() {
        return this.iPrimary_fraction;
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
     * This method sets the value for the 'Lcrunid' column
     *
     * @param    aLcrunid    long with the value for the Lcrunid column.
     */
    public void setLcrunid(long aLcrunid) {
        this.iLcrunid = aLcrunid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_projectid' column
     *
     * @param    aL_projectid    long with the value for the L_projectid column.
     */
    public void setL_projectid(long aL_projectid) {
        this.iL_projectid = aL_projectid;
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
     * This method sets the value for the 'Filecount' column
     *
     * @param    aFilecount    long with the value for the Filecount column.
     */
    public void setFilecount(long aFilecount) {
        this.iFilecount = aFilecount;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Name' column
     *
     * @param    aName    String with the value for the Name column.
     */
    public void setName(String aName) {
        this.iName = aName;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Dvd_master_number' column
     *
     * @param    aDvd_master_number    long with the value for the Dvd_master_number column.
     */
    public void setDvd_master_number(long aDvd_master_number) {
        this.iDvd_master_number = aDvd_master_number;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Dvd_secondary_number' column
     *
     * @param    aDvd_secondary_number    long with the value for the Dvd_secondary_number column.
     */
    public void setDvd_secondary_number(long aDvd_secondary_number) {
        this.iDvd_secondary_number = aDvd_secondary_number;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Primary_fraction' column
     *
     * @param    aPrimary_fraction    long with the value for the Primary_fraction column.
     */
    public void setPrimary_fraction(long aPrimary_fraction) {
        this.iPrimary_fraction = aPrimary_fraction;
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
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM lcrun WHERE lcrunid = ?");
        lStat.setLong(1, iLcrunid);
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
        if (!aKeys.containsKey(LCRUNID)) {
            throw new IllegalArgumentException("Primary key field 'LCRUNID' is missing in HashMap!");
        } else {
            iLcrunid = ((Long) aKeys.get(LCRUNID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM lcrun WHERE lcrunid = ?");
        lStat.setLong(1, iLcrunid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iLcrunid = lRS.getLong("lcrunid");
            iL_projectid = lRS.getLong("l_projectid");
            iDescription = (String) lRS.getObject("description");
            iFilecount = lRS.getLong("filecount");
            iName = (String) lRS.getObject("name");
            iDvd_master_number = lRS.getLong("dvd_master_number");
            iDvd_secondary_number = lRS.getLong("dvd_secondary_number");
            iPrimary_fraction = lRS.getLong("primary_fraction");
            iUsername = (String) lRS.getObject("username");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'lcrun' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'lcrun' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE lcrun SET lcrunid = ?, l_projectid = ?, description = ?, filecount = ?, name = ?, dvd_master_number = ?, dvd_secondary_number = ?, primary_fraction = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE lcrunid = ?");
        lStat.setLong(1, iLcrunid);
        lStat.setLong(2, iL_projectid);
        lStat.setObject(3, iDescription);
        lStat.setLong(4, iFilecount);
        lStat.setObject(5, iName);
        lStat.setLong(6, iDvd_master_number);
        lStat.setLong(7, iDvd_secondary_number);
        lStat.setLong(8, iPrimary_fraction);
        lStat.setObject(9, iUsername);
        lStat.setObject(10, iCreationdate);
        lStat.setLong(11, iLcrunid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO lcrun (lcrunid, l_projectid, description, filecount, name, dvd_master_number, dvd_secondary_number, primary_fraction, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        if (iLcrunid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iLcrunid);
        }
        if (iL_projectid == Long.MIN_VALUE) {
            lStat.setNull(2, 4);
        } else {
            lStat.setLong(2, iL_projectid);
        }
        if (iDescription == null) {
            lStat.setNull(3, -1);
        } else {
            lStat.setObject(3, iDescription);
        }
        if (iFilecount == Long.MIN_VALUE) {
            lStat.setNull(4, 4);
        } else {
            lStat.setLong(4, iFilecount);
        }
        if (iName == null) {
            lStat.setNull(5, 12);
        } else {
            lStat.setObject(5, iName);
        }
        if (iDvd_master_number == Long.MIN_VALUE) {
            lStat.setNull(6, 4);
        } else {
            lStat.setLong(6, iDvd_master_number);
        }
        if (iDvd_secondary_number == Long.MIN_VALUE) {
            lStat.setNull(7, 4);
        } else {
            lStat.setLong(7, iDvd_secondary_number);
        }
        if (iPrimary_fraction == Long.MIN_VALUE) {
            lStat.setNull(8, 4);
        } else {
            lStat.setLong(8, iPrimary_fraction);
        }
        int result = lStat.executeUpdate();

        // Retrieving the generated keys (if any).
        ResultSet lrsKeys = lStat.getGeneratedKeys();
        ResultSetMetaData lrsmKeys = lrsKeys.getMetaData();
        int colCount = lrsmKeys.getColumnCount();
        iKeys = new Object[colCount];
        while(lrsKeys.next()) {
			for(int i=0;i<iKeys.length;i++) {
				iKeys[i] = lrsKeys.getObject(i+1);
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
