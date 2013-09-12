/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 15/01/2010
 * Time: 13:19:32
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
 * $Revision: 1.4 $
 * $Date: 2007/07/06 09:41:53 $
 */

/**
 * This class is a generated accessor for the Quantitation_group table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Quantitation_groupTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for Quantitation_groupTableAccessor instances.
    private static Logger logger = Logger.getLogger(Quantitation_groupTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'quantitation_groupid' column.
     */
    protected long iQuantitation_groupid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'l_quantitation_fileid' column.
     */
    protected long iL_quantitation_fileid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'file_ref' column.
     */
    protected String iFile_ref = null;


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
     * This variable represents the key for the 'quantitation_groupid' column.
     */
    public static final String QUANTITATION_GROUPID = "QUANTITATION_GROUPID";

    /**
     * This variable represents the key for the 'l_quantitation_fileid' column.
     */
    public static final String L_QUANTITATION_FILEID = "L_QUANTITATION_FILEID";

    /**
     * This variable represents the key for the 'file_ref' column.
     */
    public static final String FILE_REF = "FILE_REF";

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
    public Quantitation_groupTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'Quantitation_groupTableAccessor' object based on a set of values in
     * the HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public Quantitation_groupTableAccessor(HashMap aParams) {
        if (aParams.containsKey(QUANTITATION_GROUPID)) {
            this.iQuantitation_groupid = ((Long) aParams.get(QUANTITATION_GROUPID)).longValue();
        }
        if (aParams.containsKey(L_QUANTITATION_FILEID)) {
            this.iL_quantitation_fileid = ((Long) aParams.get(L_QUANTITATION_FILEID)).longValue();
        }
        if (aParams.containsKey(FILE_REF)) {
            this.iFile_ref = (String) aParams.get(FILE_REF);
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
     * This constructor allows the creation of the 'Quantitation_groupTableAccessor' object based on a resultset
     * obtained by a 'select * from Quantitation_group' query.
     *
     * @param aResultSet ResultSet with the required columns to initialize this object with.
     * @throws SQLException when the ResultSet could not be read.
     */
    public Quantitation_groupTableAccessor(ResultSet aResultSet) throws SQLException {
        this.iQuantitation_groupid = aResultSet.getLong("quantitation_groupid");
        this.iL_quantitation_fileid = aResultSet.getLong("l_quantitation_fileid");
        this.iFile_ref = (String) aResultSet.getObject("file_ref");
        this.iUsername = (String) aResultSet.getObject("username");
        this.iCreationdate = (java.sql.Timestamp) aResultSet.getObject("creationdate");
        this.iModificationdate = (java.sql.Timestamp) aResultSet.getObject("modificationdate");

        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'Quantitation_groupid' column
     *
     * @return long    with the value for the Quantitation_groupid column.
     */
    public long getQuantitation_groupid() {
        return this.iQuantitation_groupid;
    }

    /**
     * This method returns the value for the 'L_quantitation_fileid' column
     *
     * @return long    with the value for the L_quantitation_fileid column.
     */
    public long getL_quantitation_fileid() {
        return this.iL_quantitation_fileid;
    }

    /**
     * This method returns the value for the 'File_ref' column
     *
     * @return String    with the value for the File_ref column.
     */
    public String getFile_ref() {
        return this.iFile_ref;
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
     * This method sets the value for the 'Quantitation_groupid' column
     *
     * @param aQuantitation_groupid long with the value for the Quantitation_groupid column.
     */
    public void setQuantitation_groupid(long aQuantitation_groupid) {
        this.iQuantitation_groupid = aQuantitation_groupid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_quantitation_fileid' column
     *
     * @param aL_quantitation_fileid long with the value for the L_quantitation_fileid column.
     */
    public void setL_quantitation_fileid(long aL_quantitation_fileid) {
        this.iL_quantitation_fileid = aL_quantitation_fileid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'File_ref' column
     *
     * @param aFile_ref String with the value for the File_ref column.
     */
    public void setFile_ref(String aFile_ref) {
        this.iFile_ref = aFile_ref;
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
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM quantitation_group WHERE quantitation_groupid = ?");
        lStat.setLong(1, iQuantitation_groupid);
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
        if (!aKeys.containsKey(QUANTITATION_GROUPID)) {
            throw new IllegalArgumentException("Primary key field 'QUANTITATION_GROUPID' is missing in HashMap!");
        } else {
            iQuantitation_groupid = ((Long) aKeys.get(QUANTITATION_GROUPID)).longValue();
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM quantitation_group WHERE quantitation_groupid = ?");
        lStat.setLong(1, iQuantitation_groupid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iQuantitation_groupid = lRS.getLong("quantitation_groupid");
            iL_quantitation_fileid = lRS.getLong("l_quantitation_fileid");
            iFile_ref = (String) lRS.getObject("file_ref");
            iUsername = (String) lRS.getObject("username");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'quantitation_group' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'quantitation_group' table! Object is not initialized correctly!");
        }
    }

    /**
     * This method allows the caller to obtain a basic select for this table.
     *
     * @return String with the basic select statement for this table.
     */
    public static String getBasicSelect() {
        return "select * from quantitation_group";
    }

    /**
     * This method allows the caller to obtain all rows for this table from a persistent store.
     *
     * @param aConn Connection to the persitent store.
     * @return ArrayList<Quantitation_groupTableAccessor>   with all entries for this table.
     */
    public static ArrayList<Quantitation_groupTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
        ArrayList<Quantitation_groupTableAccessor> entities = new ArrayList<Quantitation_groupTableAccessor>();
        Statement stat = aConn.createStatement();
        ResultSet rs = stat.executeQuery(getBasicSelect());
        while (rs.next()) {
            entities.add(new Quantitation_groupTableAccessor(rs));
        }
        rs.close();
        stat.close();
        return entities;
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE quantitation_group SET quantitation_groupid = ?, l_quantitation_fileid = ?, file_ref = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE quantitation_groupid = ?");
        lStat.setLong(1, iQuantitation_groupid);
        lStat.setLong(2, iL_quantitation_fileid);
        lStat.setObject(3, iFile_ref);
        lStat.setObject(4, iUsername);
        lStat.setObject(5, iCreationdate);
        lStat.setLong(6, iQuantitation_groupid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO quantitation_group (quantitation_groupid, l_quantitation_fileid, file_ref, username, creationdate, modificationdate) values(?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",Statement.RETURN_GENERATED_KEYS);
        if (iQuantitation_groupid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iQuantitation_groupid);
        }
        if (iL_quantitation_fileid == Long.MIN_VALUE) {
            lStat.setNull(2, 4);
        } else {
            lStat.setLong(2, iL_quantitation_fileid);
        }
        if (iFile_ref == null) {
            lStat.setNull(3, 12);
        } else {
            lStat.setObject(3, iFile_ref);
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
        // Verify that we have a single, generated key.
        if (iKeys != null && iKeys.length == 1) {
            // Since we have exactly one key specified, and only
            // one Primary Key column, we can infer that this was the
            // generated column, and we can therefore initialize it here.
            iQuantitation_groupid = ((Number) iKeys[0]).longValue();
        }
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
