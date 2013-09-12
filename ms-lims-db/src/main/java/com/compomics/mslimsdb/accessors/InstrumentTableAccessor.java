/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 30/12/2005
 * Time: 12:13:03
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
 * $Revision: 1.6 $
 * $Date: 2005/12/31 10:22:52 $
 */

/**
 * This class is a generated accessor for the Instrument table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class InstrumentTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for InstrumentTableAccessor instances.
    private static Logger logger = Logger.getLogger(InstrumentTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'instrumentid' column.
     */
    protected long iInstrumentid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'name' column.
     */
    protected String iName = null;


    /**
     * This variable represents the contents for the 'description' column.
     */
    protected String iDescription = null;


    /**
     * This variable represents the contents for the 'storageclassname' column.
     */
    protected String iStorageclassname = null;


    /**
     * This variable represents the contents for the 'propertiesfilename' column.
     */
    protected String iPropertiesfilename = null;


    /**
     * This variable represents the contents for the 'differential_calibration' column.
     */
    protected Number iDifferential_calibration = null;


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
     * This variable represents the key for the 'instrumentid' column.
     */
    public static final String INSTRUMENTID = "INSTRUMENTID";

    /**
     * This variable represents the key for the 'name' column.
     */
    public static final String NAME = "NAME";

    /**
     * This variable represents the key for the 'description' column.
     */
    public static final String DESCRIPTION = "DESCRIPTION";

    /**
     * This variable represents the key for the 'storageclassname' column.
     */
    public static final String STORAGECLASSNAME = "STORAGECLASSNAME";

    /**
     * This variable represents the key for the 'propertiesfilename' column.
     */
    public static final String PROPERTIESFILENAME = "PROPERTIESFILENAME";

    /**
     * This variable represents the key for the 'differential_calibration' column.
     */
    public static final String DIFFERENTIAL_CALIBRATION = "DIFFERENTIAL_CALIBRATION";

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
    public InstrumentTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'InstrumentTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public InstrumentTableAccessor(HashMap aParams) {
        if (aParams.containsKey(INSTRUMENTID)) {
            this.iInstrumentid = ((Long) aParams.get(INSTRUMENTID)).longValue();
        }
        if (aParams.containsKey(NAME)) {
            this.iName = (String) aParams.get(NAME);
        }
        if (aParams.containsKey(DESCRIPTION)) {
            this.iDescription = (String) aParams.get(DESCRIPTION);
        }
        if (aParams.containsKey(STORAGECLASSNAME)) {
            this.iStorageclassname = (String) aParams.get(STORAGECLASSNAME);
        }
        if (aParams.containsKey(PROPERTIESFILENAME)) {
            this.iPropertiesfilename = (String) aParams.get(PROPERTIESFILENAME);
        }
        if (aParams.containsKey(DIFFERENTIAL_CALIBRATION)) {
            this.iDifferential_calibration = (Number) aParams.get(DIFFERENTIAL_CALIBRATION);
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
     * This method returns the value for the 'Instrumentid' column
     *
     * @return long    with the value for the Instrumentid column.
     */
    public long getInstrumentid() {
        return this.iInstrumentid;
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
     * This method returns the value for the 'Description' column
     *
     * @return String    with the value for the Description column.
     */
    public String getDescription() {
        return this.iDescription;
    }

    /**
     * This method returns the value for the 'Storageclassname' column
     *
     * @return String    with the value for the Storageclassname column.
     */
    public String getStorageclassname() {
        return this.iStorageclassname;
    }

    /**
     * This method returns the value for the 'Propertiesfilename' column
     *
     * @return String    with the value for the Propertiesfilename column.
     */
    public String getPropertiesfilename() {
        return this.iPropertiesfilename;
    }

    /**
     * This method returns the value for the 'Differential_calibration' column
     *
     * @return Number    with the value for the Differential_calibration column.
     */
    public Number getDifferential_calibration() {
        return this.iDifferential_calibration;
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
     * This method sets the value for the 'Instrumentid' column
     *
     * @param aInstrumentid long with the value for the Instrumentid column.
     */
    public void setInstrumentid(long aInstrumentid) {
        this.iInstrumentid = aInstrumentid;
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
     * This method sets the value for the 'Description' column
     *
     * @param aDescription String with the value for the Description column.
     */
    public void setDescription(String aDescription) {
        this.iDescription = aDescription;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Storageclassname' column
     *
     * @param aStorageclassname String with the value for the Storageclassname column.
     */
    public void setStorageclassname(String aStorageclassname) {
        this.iStorageclassname = aStorageclassname;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Propertiesfilename' column
     *
     * @param aPropertiesfilename String with the value for the Propertiesfilename column.
     */
    public void setPropertiesfilename(String aPropertiesfilename) {
        this.iPropertiesfilename = aPropertiesfilename;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Differential_calibration' column
     *
     * @param aDifferential_calibration Number with the value for the Differential_calibration column.
     */
    public void setDifferential_calibration(Number aDifferential_calibration) {
        this.iDifferential_calibration = aDifferential_calibration;
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
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM instrument WHERE instrumentid = ?");
        lStat.setLong(1, iInstrumentid);
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
        if (!aKeys.containsKey(INSTRUMENTID)) {
            throw new IllegalArgumentException("Primary key field 'INSTRUMENTID' is missing in HashMap!");
        } else {
            iInstrumentid = ((Long) aKeys.get(INSTRUMENTID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM instrument WHERE instrumentid = ?");
        lStat.setLong(1, iInstrumentid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iInstrumentid = lRS.getLong("instrumentid");
            iName = (String) lRS.getObject("name");
            iDescription = (String) lRS.getObject("description");
            iStorageclassname = (String) lRS.getObject("storageclassname");
            iPropertiesfilename = (String) lRS.getObject("propertiesfilename");
            iDifferential_calibration = (Number) lRS.getObject("differential_calibration");
            iUsername = (String) lRS.getObject("username");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'instrument' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'instrument' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE instrument SET instrumentid = ?, name = ?, description = ?, storageclassname = ?, propertiesfilename = ?, differential_calibration = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE instrumentid = ?");
        lStat.setLong(1, iInstrumentid);
        lStat.setObject(2, iName);
        lStat.setObject(3, iDescription);
        lStat.setObject(4, iStorageclassname);
        lStat.setObject(5, iPropertiesfilename);
        lStat.setObject(6, iDifferential_calibration);
        lStat.setObject(7, iUsername);
        lStat.setObject(8, iCreationdate);
        lStat.setLong(9, iInstrumentid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO instrument (instrumentid, name, description, storageclassname, propertiesfilename, differential_calibration, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",Statement.RETURN_GENERATED_KEYS);
        if (iInstrumentid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iInstrumentid);
        }
        if (iName == null) {
            lStat.setNull(2, 12);
        } else {
            lStat.setObject(2, iName);
        }
        if (iDescription == null) {
            lStat.setNull(3, -1);
        } else {
            lStat.setObject(3, iDescription);
        }
        if (iStorageclassname == null) {
            lStat.setNull(4, 12);
        } else {
            lStat.setObject(4, iStorageclassname);
        }
        if (iPropertiesfilename == null) {
            lStat.setNull(5, 12);
        } else {
            lStat.setObject(5, iPropertiesfilename);
        }
        if (iDifferential_calibration == null) {
            lStat.setNull(6, 3);
        } else {
            lStat.setObject(6, iDifferential_calibration);
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
