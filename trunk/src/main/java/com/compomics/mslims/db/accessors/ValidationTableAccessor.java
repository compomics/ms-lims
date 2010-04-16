/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 16/10/2008
 * Time: 16:48:25
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
 * $Revision: 1.5 $
 * $Date: 2009/06/16 12:45:29 $
 */

/**
 * This class is a generated accessor for the Validation table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ValidationTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for ValidationTableAccessor instances.
    private static Logger logger = Logger.getLogger(ValidationTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'validationid' column.
     */
    protected long iValidationid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'l_identificationid' column.
     */
    protected long iL_identificationid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'comment' column.
     */
    protected String iComment = null;


    /**
     * This variable represents the contents for the 'status' column.
     */
    protected boolean iStatus = false;


    /**
     * This variable represents the contents for the 'l_userid' column.
     */
    protected Integer iL_userid = null;


    /**
     * This variable represents the contents for the 'creationdate' column.
     */
    protected java.sql.Timestamp iCreationdate = null;


    /**
     * This variable represents the contents for the 'modificationdate' column.
     */
    protected java.sql.Timestamp iModificationdate = null;


    /**
     * This variable represents the key for the 'validationid' column.
     */
    public static final String VALIDATIONID = "VALIDATIONID";

    /**
     * This variable represents the key for the 'l_identificationid' column.
     */
    public static final String L_IDENTIFICATIONID = "L_IDENTIFICATIONID";

    /**
     * This variable represents the key for the 'comment' column.
     */
    public static final String COMMENT = "COMMENT";

    /**
     * This variable represents the key for the 'status' column.
     */
    public static final String STATUS = "STATUS";

    /**
     * This variable represents the key for the 'l_userid' column.
     */
    public static final String L_USERID = "L_USERID";

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
    public ValidationTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'ValidationTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public ValidationTableAccessor(HashMap aParams) {
        if (aParams.containsKey(VALIDATIONID)) {
            this.iValidationid = ((Long) aParams.get(VALIDATIONID)).longValue();
        }
        if (aParams.containsKey(L_IDENTIFICATIONID)) {
            this.iL_identificationid = ((Long) aParams.get(L_IDENTIFICATIONID)).longValue();
        }
        if (aParams.containsKey(COMMENT)) {
            this.iComment = (String) aParams.get(COMMENT);
        }
        if (aParams.containsKey(STATUS)) {
            this.iStatus = ((Boolean) aParams.get(STATUS));
        }
        if (aParams.containsKey(L_USERID)) {
            this.iL_userid = (Integer) aParams.get(L_USERID);
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
     * This method returns the value for the 'Validationid' column
     *
     * @return long    with the value for the Validationid column.
     */
    public long getValidationid() {
        return this.iValidationid;
    }

    /**
     * This method returns the value for the 'L_identificationid' column
     *
     * @return long    with the value for the L_identificationid column.
     */
    public long getL_identificationid() {
        return this.iL_identificationid;
    }

    /**
     * This method returns the value for the 'Comment' column
     *
     * @return String    with the value for the Comment column.
     */
    public String getComment() {
        return this.iComment;
    }

    /**
     * This method returns the value for the 'Status' column
     *
     * @return boolean    with the value for the Status column.
     */
    public boolean getStatus() {
        return this.iStatus;
    }

    /**
     * This method returns the value for the 'Username' column
     *
     * @return String    with the value for the Username column.
     */
    public int getL_userid() {
        return this.iL_userid;
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
     * This method sets the value for the 'Validationid' column
     *
     * @param aValidationid long with the value for the Validationid column.
     */
    public void setValidationid(long aValidationid) {
        this.iValidationid = aValidationid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_identificationid' column
     *
     * @param aL_identificationid long with the value for the L_identificationid column.
     */
    public void setL_identificationid(long aL_identificationid) {
        this.iL_identificationid = aL_identificationid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Comment' column
     *
     * @param aComment String with the value for the Comment column.
     */
    public void setComment(String aComment) {
        this.iComment = aComment;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Status' column
     *
     * @param aStatus boolean with the value for the Status column.
     */
    public void setStatus(boolean aStatus) {
        this.iStatus = aStatus;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Username' column
     *
     * @param aL_userid String with the value for the Username column.
     */
    public void setL_userid(int aL_userid) {
        this.iL_userid = aL_userid;
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
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM validation WHERE validationid = ?");
        lStat.setLong(1, iValidationid);
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
        if (!aKeys.containsKey(VALIDATIONID)) {
            throw new IllegalArgumentException("Primary key field 'VALIDATIONID' is missing in HashMap!");
        } else {
            iValidationid = ((Long) aKeys.get(VALIDATIONID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM validation WHERE validationid = ?");
        lStat.setLong(1, iValidationid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iValidationid = lRS.getLong("validationid");
            iL_identificationid = lRS.getLong("l_identificationid");
            iComment = (String) lRS.getObject("comment");
            iStatus = lRS.getBoolean("status");
            iL_userid = (Integer) lRS.getObject("l_userid");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'validation' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'validation' table! Object is not initialized correctly!");
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
        PreparedStatement lStat =
                aConn.prepareStatement("UPDATE validation SET validationid = ?, l_identificationid = ?, comment = ?, status = ?, l_userid = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE validationid = ?");
        lStat.setLong(1, iValidationid);
        lStat.setLong(2, iL_identificationid);
        lStat.setObject(3, iComment);
        lStat.setBoolean(4, iStatus);
        lStat.setObject(5, iL_userid);
        lStat.setObject(6, iCreationdate);
        lStat.setLong(7, iValidationid);
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
        PreparedStatement lStat =
                aConn.prepareStatement("INSERT INTO validation (validationid, l_identificationid, comment, status, l_userid, creationdate, modificationdate) values(?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        if (iValidationid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iValidationid);
        }
        if (iL_identificationid == Long.MIN_VALUE) {
            lStat.setNull(2, 4);
        } else {
            lStat.setLong(2, iL_identificationid);
        }
        if (iComment == null) {
            lStat.setNull(3, -1);
        } else {
            lStat.setObject(3, iComment);
        }
        lStat.setBoolean(4, iStatus);
        if (iL_userid == null) {
            lStat.setNull(5, -1);
        } else {
            lStat.setInt(5, iL_userid);
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
     * This method will return the automatically generated key for the insert if one was triggered, or 'null'
     * otherwise.
     *
     * @return Object[]    with the generated keys.
     */
    public Object[] getGeneratedKeys() {
        return this.iKeys;
    }

}
