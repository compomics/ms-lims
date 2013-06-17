/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 15/01/2010
 * Time: 13:34:22
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
 * This class is a generated accessor for the Quantitation table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class QuantitationTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for QuantitationTableAccessor instances.
    private static Logger logger = Logger.getLogger(QuantitationTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'quantitationid' column.
     */
    protected long iQuantitationid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'l_quantitation_groupid' column.
     */
    protected long iL_quantitation_groupid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'ratio' column.
     */
    protected double iRatio = Double.MIN_VALUE;


    /**
     * This variable represents the contents for the 'standard_error' column.
     */
    protected double iStandard_error = Double.MIN_VALUE;


    /**
     * This variable represents the contents for the 'type' column.
     */
    protected String iType = null;


    /**
     * This variable represents the contents for the 'valid' column.
     */
    protected boolean iValid = false;


    /**
     * This variable represents the contents for the 'comment' column.
     */
    protected String iComment = null;


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
     * This variable represents the key for the 'quantitationid' column.
     */
    public static final String QUANTITATIONID = "QUANTITATIONID";

    /**
     * This variable represents the key for the 'l_quantitation_groupid' column.
     */
    public static final String L_QUANTITATION_GROUPID = "L_QUANTITATION_GROUPID";

    /**
     * This variable represents the key for the 'ratio' column.
     */
    public static final String RATIO = "RATIO";

    /**
     * This variable represents the key for the 'standard_error' column.
     */
    public static final String STANDARD_ERROR = "STANDARD_ERROR";

    /**
     * This variable represents the key for the 'type' column.
     */
    public static final String TYPE = "TYPE";

    /**
     * This variable represents the key for the 'valid' column.
     */
    public static final String VALID = "VALID";

    /**
     * This variable represents the key for the 'comment' column.
     */
    public static final String COMMENT = "COMMENT";

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
    public QuantitationTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'QuantitationTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public QuantitationTableAccessor(HashMap aParams) {
        if (aParams.containsKey(QUANTITATIONID)) {
            this.iQuantitationid = ((Long) aParams.get(QUANTITATIONID)).longValue();
        }
        if (aParams.containsKey(L_QUANTITATION_GROUPID)) {
            this.iL_quantitation_groupid = ((Long) aParams.get(L_QUANTITATION_GROUPID)).longValue();
        }
        if (aParams.containsKey(RATIO)) {
            this.iRatio = ((Double) aParams.get(RATIO)).doubleValue();
        }
        if (aParams.containsKey(STANDARD_ERROR)) {
            this.iStandard_error = ((Double) aParams.get(STANDARD_ERROR)).doubleValue();
        }
        if (aParams.containsKey(TYPE)) {
            this.iType = (String) aParams.get(TYPE);
        }
        if (aParams.containsKey(VALID)) {
            this.iValid = ((Boolean) aParams.get(VALID)).booleanValue();
        }
        if (aParams.containsKey(COMMENT)) {
            this.iComment = (String) aParams.get(COMMENT);
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
     * This constructor allows the creation of the 'QuantitationTableAccessor' object based on a resultset obtained by a
     * 'select * from Quantitation' query.
     *
     * @param aResultSet ResultSet with the required columns to initialize this object with.
     * @throws SQLException when the ResultSet could not be read.
     */
    public QuantitationTableAccessor(ResultSet aResultSet) throws SQLException {
        this.iQuantitationid = aResultSet.getLong("quantitationid");
        this.iL_quantitation_groupid = aResultSet.getLong("l_quantitation_groupid");
        this.iRatio = aResultSet.getDouble("ratio");
        this.iStandard_error = aResultSet.getDouble("standard_error");
        this.iType = (String) aResultSet.getObject("type");
        this.iValid = aResultSet.getBoolean("valid");
        this.iComment = (String) aResultSet.getObject("comment");
        this.iUsername = (String) aResultSet.getObject("username");
        this.iCreationdate = (java.sql.Timestamp) aResultSet.getObject("creationdate");
        this.iModificationdate = (java.sql.Timestamp) aResultSet.getObject("modificationdate");

        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'Quantitationid' column
     *
     * @return long    with the value for the Quantitationid column.
     */
    public long getQuantitationid() {
        return this.iQuantitationid;
    }

    /**
     * This method returns the value for the 'L_quantitation_groupid' column
     *
     * @return long    with the value for the L_quantitation_groupid column.
     */
    public long getL_quantitation_groupid() {
        return this.iL_quantitation_groupid;
    }

    /**
     * This method returns the value for the 'Ratio' column
     *
     * @return double    with the value for the Ratio column.
     */
    public double getRatio() {
        return this.iRatio;
    }

    /**
     * This method returns the value for the 'Standard_error' column
     *
     * @return double    with the value for the Standard_error column.
     */
    public double getStandard_error() {
        return this.iStandard_error;
    }

    /**
     * This method returns the value for the 'Type' column
     *
     * @return String    with the value for the Type column.
     */
    public String getType() {
        return this.iType;
    }

    /**
     * This method returns the value for the 'Valid' column
     *
     * @return boolean    with the value for the Valid column.
     */
    public boolean getValid() {
        return this.iValid;
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
     * This method sets the value for the 'Quantitationid' column
     *
     * @param aQuantitationid long with the value for the Quantitationid column.
     */
    public void setQuantitationid(long aQuantitationid) {
        this.iQuantitationid = aQuantitationid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_quantitation_groupid' column
     *
     * @param aL_quantitation_groupid long with the value for the L_quantitation_groupid column.
     */
    public void setL_quantitation_groupid(long aL_quantitation_groupid) {
        this.iL_quantitation_groupid = aL_quantitation_groupid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Ratio' column
     *
     * @param aRatio double with the value for the Ratio column.
     */
    public void setRatio(double aRatio) {
        this.iRatio = aRatio;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Standard_error' column
     *
     * @param aStandard_error double with the value for the Standard_error column.
     */
    public void setStandard_error(double aStandard_error) {
        this.iStandard_error = aStandard_error;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Type' column
     *
     * @param aType String with the value for the Type column.
     */
    public void setType(String aType) {
        this.iType = aType;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Valid' column
     *
     * @param aValid boolean with the value for the Valid column.
     */
    public void setValid(boolean aValid) {
        this.iValid = aValid;
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
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM quantitation WHERE quantitationid = ?");
        lStat.setLong(1, iQuantitationid);
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
        if (!aKeys.containsKey(QUANTITATIONID)) {
            throw new IllegalArgumentException("Primary key field 'QUANTITATIONID' is missing in HashMap!");
        } else {
            iQuantitationid = ((Long) aKeys.get(QUANTITATIONID)).longValue();
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM quantitation WHERE quantitationid = ?");
        lStat.setLong(1, iQuantitationid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iQuantitationid = lRS.getLong("quantitationid");
            iL_quantitation_groupid = lRS.getLong("l_quantitation_groupid");
            iRatio = lRS.getDouble("ratio");
            iStandard_error = lRS.getDouble("standard_error");
            iType = (String) lRS.getObject("type");
            iValid = lRS.getBoolean("valid");
            iComment = (String) lRS.getObject("comment");
            iUsername = (String) lRS.getObject("username");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'quantitation' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'quantitation' table! Object is not initialized correctly!");
        }
    }

    /**
     * This method allows the caller to obtain a basic select for this table.
     *
     * @return String with the basic select statement for this table.
     */
    public static String getBasicSelect() {
        return "select * from quantitation";
    }

    /**
     * This method allows the caller to obtain all rows for this table from a persistent store.
     *
     * @param aConn Connection to the persitent store.
     * @return ArrayList<QuantitationTableAccessor>   with all entries for this table.
     */
    public static ArrayList<QuantitationTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
        ArrayList<QuantitationTableAccessor> entities = new ArrayList<QuantitationTableAccessor>();
        Statement stat = aConn.createStatement();
        ResultSet rs = stat.executeQuery(getBasicSelect());
        while (rs.next()) {
            entities.add(new QuantitationTableAccessor(rs));
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE quantitation SET quantitationid = ?, l_quantitation_groupid = ?, ratio = ?, standard_error = ?, type = ?, valid = ?, comment = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE quantitationid = ?");
        lStat.setLong(1, iQuantitationid);
        lStat.setLong(2, iL_quantitation_groupid);
        lStat.setDouble(3, iRatio);
        lStat.setDouble(4, iStandard_error);
        lStat.setObject(5, iType);
        lStat.setBoolean(6, iValid);
        lStat.setObject(7, iComment);
        lStat.setObject(8, iUsername);
        lStat.setObject(9, iCreationdate);
        lStat.setLong(10, iQuantitationid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO quantitation (quantitationid, l_quantitation_groupid, ratio, standard_error, type, valid, comment, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        if (iQuantitationid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iQuantitationid);
        }
        if (iL_quantitation_groupid == Long.MIN_VALUE) {
            lStat.setNull(2, 4);
        } else {
            lStat.setLong(2, iL_quantitation_groupid);
        }
        if (iRatio == Double.MIN_VALUE) {
            lStat.setNull(3, 8);
        } else {
            lStat.setDouble(3, iRatio);
        }
        if (iStandard_error == Double.MIN_VALUE) {
            lStat.setNull(4, 8);
        } else {
            lStat.setDouble(4, iStandard_error);
        }
        if (iType == null) {
            lStat.setNull(5, 12);
        } else {
            lStat.setObject(5, iType);
        }
        lStat.setBoolean(6, iValid);
        if (iComment == null) {
            lStat.setNull(7, 12);
        } else {
            lStat.setObject(7, iComment);
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
            iQuantitationid = ((Number) iKeys[0]).longValue();
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
