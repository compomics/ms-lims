/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 06/08/2003
 * Time: 16:48:15
 */
package com.compomics.mslimsdb.accessors;

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
 * This class is a generated accessor for the Phosphorylation table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class PhosphorylationTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for PhosphorylationTableAccessor instances.
    private static Logger logger = Logger.getLogger(PhosphorylationTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'phosphorylationid' column.
     */
    protected long iPhosphorylationid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'l_status' column.
     */
    protected long iL_status = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'residue' column.
     */
    protected String iResidue = null;


    /**
     * This variable represents the contents for the 'location' column.
     */
    protected long iLocation = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'accession' column.
     */
    protected String iAccession = null;


    /**
     * This variable represents the contents for the 'context' column.
     */
    protected String iContext = null;


    /**
     * This variable represents the contents for the 'score' column.
     */
    protected Number iScore = null;


    /**
     * This variable represents the contents for the 'threshold' column.
     */
    protected Number iThreshold = null;


    /**
     * This variable represents the contents for the 'creationdate' column.
     */
    protected java.sql.Timestamp iCreationdate = null;


    /**
     * This variable represents the contents for the 'description' column.
     */
    protected String iDescription = null;


    /**
     * This variable represents the key for the 'phosphorylationid' column.
     */
    public static final String PHOSPHORYLATIONID = "PHOSPHORYLATIONID";

    /**
     * This variable represents the key for the 'l_status' column.
     */
    public static final String L_STATUS = "L_STATUS";

    /**
     * This variable represents the key for the 'residue' column.
     */
    public static final String RESIDUE = "RESIDUE";

    /**
     * This variable represents the key for the 'location' column.
     */
    public static final String LOCATION = "LOCATION";

    /**
     * This variable represents the key for the 'accession' column.
     */
    public static final String ACCESSION = "ACCESSION";

    /**
     * This variable represents the key for the 'context' column.
     */
    public static final String CONTEXT = "CONTEXT";

    /**
     * This variable represents the key for the 'score' column.
     */
    public static final String SCORE = "SCORE";

    /**
     * This variable represents the key for the 'threshold' column.
     */
    public static final String THRESHOLD = "THRESHOLD";

    /**
     * This variable represents the key for the 'creationdate' column.
     */
    public static final String CREATIONDATE = "CREATIONDATE";

    /**
     * This variable represents the key for the 'description' column.
     */
    public static final String DESCRIPTION = "DESCRIPTION";


    /**
     * Default constructor.
     */
    public PhosphorylationTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'PhosphorylationTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public PhosphorylationTableAccessor(HashMap aParams) {
        if (aParams.containsKey(PHOSPHORYLATIONID)) {
            this.iPhosphorylationid = ((Long) aParams.get(PHOSPHORYLATIONID)).longValue();
        }
        if (aParams.containsKey(L_STATUS)) {
            this.iL_status = ((Long) aParams.get(L_STATUS)).longValue();
        }
        if (aParams.containsKey(RESIDUE)) {
            this.iResidue = (String) aParams.get(RESIDUE);
        }
        if (aParams.containsKey(LOCATION)) {
            this.iLocation = ((Long) aParams.get(LOCATION)).longValue();
        }
        if (aParams.containsKey(ACCESSION)) {
            this.iAccession = (String) aParams.get(ACCESSION);
        }
        if (aParams.containsKey(CONTEXT)) {
            this.iContext = (String) aParams.get(CONTEXT);
        }
        if (aParams.containsKey(SCORE)) {
            this.iScore = (Number) aParams.get(SCORE);
        }
        if (aParams.containsKey(THRESHOLD)) {
            this.iThreshold = (Number) aParams.get(THRESHOLD);
        }
        if (aParams.containsKey(CREATIONDATE)) {
            this.iCreationdate = (java.sql.Timestamp) aParams.get(CREATIONDATE);
        }
        if (aParams.containsKey(DESCRIPTION)) {
            this.iDescription = (String) aParams.get(DESCRIPTION);
        }
        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'Phosphorylationid' column
     *
     * @return long    with the value for the Phosphorylationid column.
     */
    public long getPhosphorylationid() {
        return this.iPhosphorylationid;
    }

    /**
     * This method returns the value for the 'L_status' column
     *
     * @return long    with the value for the L_status column.
     */
    public long getL_status() {
        return this.iL_status;
    }

    /**
     * This method returns the value for the 'Residue' column
     *
     * @return String    with the value for the Residue column.
     */
    public String getResidue() {
        return this.iResidue;
    }

    /**
     * This method returns the value for the 'Location' column
     *
     * @return long    with the value for the Location column.
     */
    public long getLocation() {
        return this.iLocation;
    }

    /**
     * This method returns the value for the 'Accession' column
     *
     * @return String    with the value for the Accession column.
     */
    public String getAccession() {
        return this.iAccession;
    }

    /**
     * This method returns the value for the 'Context' column
     *
     * @return String    with the value for the Context column.
     */
    public String getContext() {
        return this.iContext;
    }

    /**
     * This method returns the value for the 'Score' column
     *
     * @return Number    with the value for the Score column.
     */
    public Number getScore() {
        return this.iScore;
    }

    /**
     * This method returns the value for the 'Threshold' column
     *
     * @return Number    with the value for the Threshold column.
     */
    public Number getThreshold() {
        return this.iThreshold;
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
     * This method returns the value for the 'Description' column
     *
     * @return String    with the value for the Description column.
     */
    public String getDescription() {
        return this.iDescription;
    }

    /**
     * This method sets the value for the 'Phosphorylationid' column
     *
     * @param aPhosphorylationid long with the value for the Phosphorylationid column.
     */
    public void setPhosphorylationid(long aPhosphorylationid) {
        this.iPhosphorylationid = aPhosphorylationid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_status' column
     *
     * @param aL_status long with the value for the L_status column.
     */
    public void setL_status(long aL_status) {
        this.iL_status = aL_status;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Residue' column
     *
     * @param aResidue String with the value for the Residue column.
     */
    public void setResidue(String aResidue) {
        this.iResidue = aResidue;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Location' column
     *
     * @param aLocation long with the value for the Location column.
     */
    public void setLocation(long aLocation) {
        this.iLocation = aLocation;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Accession' column
     *
     * @param aAccession String with the value for the Accession column.
     */
    public void setAccession(String aAccession) {
        this.iAccession = aAccession;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Context' column
     *
     * @param aContext String with the value for the Context column.
     */
    public void setContext(String aContext) {
        this.iContext = aContext;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Score' column
     *
     * @param aScore Number with the value for the Score column.
     */
    public void setScore(Number aScore) {
        this.iScore = aScore;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Threshold' column
     *
     * @param aThreshold Number with the value for the Threshold column.
     */
    public void setThreshold(Number aThreshold) {
        this.iThreshold = aThreshold;
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
     * This method sets the value for the 'Description' column
     *
     * @param aDescription String with the value for the Description column.
     */
    public void setDescription(String aDescription) {
        this.iDescription = aDescription;
        this.iUpdated = true;
    }


    /**
     * This method allows the caller to delete the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int delete(Connection aConn) throws SQLException {
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM phosphorylation WHERE phosphorylationid = ?");
        lStat.setLong(1, iPhosphorylationid);
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
        if (!aKeys.containsKey(PHOSPHORYLATIONID)) {
            throw new IllegalArgumentException("Primary key field 'PHOSPHORYLATIONID' is missing in HashMap!");
        } else {
            iPhosphorylationid = ((Long) aKeys.get(PHOSPHORYLATIONID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM phosphorylation WHERE phosphorylationid = ?");
        lStat.setLong(1, iPhosphorylationid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iPhosphorylationid = lRS.getLong("phosphorylationid");
            iL_status = lRS.getLong("l_status");
            iResidue = (String) lRS.getObject("residue");
            iLocation = lRS.getLong("location");
            iAccession = (String) lRS.getObject("accession");
            iContext = (String) lRS.getObject("context");
            iScore = (Number) lRS.getObject("score");
            iThreshold = (Number) lRS.getObject("threshold");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iDescription = (String) lRS.getObject("description");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'phosphorylation' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'phosphorylation' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE phosphorylation SET phosphorylationid = ?, l_status = ?, residue = ?, location = ?, accession = ?, context = ?, score = ?, threshold = ?, creationdate = ?, description = ? WHERE phosphorylationid = ?");
        lStat.setLong(1, iPhosphorylationid);
        lStat.setLong(2, iL_status);
        lStat.setObject(3, iResidue);
        lStat.setLong(4, iLocation);
        lStat.setObject(5, iAccession);
        lStat.setObject(6, iContext);
        lStat.setObject(7, iScore);
        lStat.setObject(8, iThreshold);
        lStat.setObject(9, iCreationdate);
        lStat.setObject(10, iDescription);
        lStat.setLong(11, iPhosphorylationid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO phosphorylation (phosphorylationid, l_status, residue, location, accession, context, score, threshold, creationdate, description) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
        if (iPhosphorylationid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iPhosphorylationid);
        }
        if (iL_status == Long.MIN_VALUE) {
            lStat.setNull(2, 4);
        } else {
            lStat.setLong(2, iL_status);
        }
        if (iResidue == null) {
            lStat.setNull(3, 12);
        } else {
            lStat.setObject(3, iResidue);
        }
        if (iLocation == Long.MIN_VALUE) {
            lStat.setNull(4, 4);
        } else {
            lStat.setLong(4, iLocation);
        }
        if (iAccession == null) {
            lStat.setNull(5, 12);
        } else {
            lStat.setObject(5, iAccession);
        }
        if (iContext == null) {
            lStat.setNull(6, 12);
        } else {
            lStat.setObject(6, iContext);
        }
        if (iScore == null) {
            lStat.setNull(7, 3);
        } else {
            lStat.setObject(7, iScore);
        }
        if (iThreshold == null) {
            lStat.setNull(8, 3);
        } else {
            lStat.setObject(8, iThreshold);
        }
        if (iCreationdate == null) {
            lStat.setNull(9, 93);
        } else {
            lStat.setObject(9, iCreationdate);
        }
        if (iDescription == null) {
            lStat.setNull(10, 12);
        } else {
            lStat.setObject(10, iDescription);
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
