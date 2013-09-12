/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 25/08/2003
 * Time: 11:31:54
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
 * This class is a generated accessor for the Id_to_phospho table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Id_to_phosphoTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for Id_to_phosphoTableAccessor instances.
    private static Logger logger = Logger.getLogger(Id_to_phosphoTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'l_id' column.
     */
    protected long iL_id = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'l_phosphorylationid' column.
     */
    protected long iL_phosphorylationid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'conversionid' column.
     */
    protected long iConversionid = Long.MIN_VALUE;


    /**
     * This variable represents the key for the 'l_id' column.
     */
    public static final String L_ID = "L_ID";

    /**
     * This variable represents the key for the 'l_phosphorylationid' column.
     */
    public static final String L_PHOSPHORYLATIONID = "L_PHOSPHORYLATIONID";

    /**
     * This variable represents the key for the 'conversionid' column.
     */
    public static final String CONVERSIONID = "CONVERSIONID";


    /**
     * Default constructor.
     */
    public Id_to_phosphoTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'Id_to_phosphoTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public Id_to_phosphoTableAccessor(HashMap aParams) {
        if (aParams.containsKey(L_ID)) {
            this.iL_id = ((Long) aParams.get(L_ID)).longValue();
        }
        if (aParams.containsKey(L_PHOSPHORYLATIONID)) {
            this.iL_phosphorylationid = ((Long) aParams.get(L_PHOSPHORYLATIONID)).longValue();
        }
        if (aParams.containsKey(CONVERSIONID)) {
            this.iConversionid = ((Long) aParams.get(CONVERSIONID)).longValue();
        }
        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'L_id' column
     *
     * @return long    with the value for the L_id column.
     */
    public long getL_id() {
        return this.iL_id;
    }

    /**
     * This method returns the value for the 'L_phosphorylationid' column
     *
     * @return long    with the value for the L_phosphorylationid column.
     */
    public long getL_phosphorylationid() {
        return this.iL_phosphorylationid;
    }

    /**
     * This method returns the value for the 'Conversionid' column
     *
     * @return long    with the value for the Conversionid column.
     */
    public long getConversionid() {
        return this.iConversionid;
    }

    /**
     * This method sets the value for the 'L_id' column
     *
     * @param aL_id long with the value for the L_id column.
     */
    public void setL_id(long aL_id) {
        this.iL_id = aL_id;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_phosphorylationid' column
     *
     * @param aL_phosphorylationid long with the value for the L_phosphorylationid column.
     */
    public void setL_phosphorylationid(long aL_phosphorylationid) {
        this.iL_phosphorylationid = aL_phosphorylationid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Conversionid' column
     *
     * @param aConversionid long with the value for the Conversionid column.
     */
    public void setConversionid(long aConversionid) {
        this.iConversionid = aConversionid;
        this.iUpdated = true;
    }


    /**
     * This method allows the caller to delete the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int delete(Connection aConn) throws SQLException {
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM id_to_phospho WHERE conversionid = ?");
        lStat.setLong(1, iConversionid);
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
        if (!aKeys.containsKey(CONVERSIONID)) {
            throw new IllegalArgumentException("Primary key field 'CONVERSIONID' is missing in HashMap!");
        } else {
            iConversionid = ((Long) aKeys.get(CONVERSIONID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM id_to_phospho WHERE conversionid = ?");
        lStat.setLong(1, iConversionid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iL_id = lRS.getLong("l_id");
            iL_phosphorylationid = lRS.getLong("l_phosphorylationid");
            iConversionid = lRS.getLong("conversionid");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'id_to_phospho' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'id_to_phospho' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE id_to_phospho SET l_id = ?, l_phosphorylationid = ?, conversionid = ? WHERE conversionid = ?");
        lStat.setLong(1, iL_id);
        lStat.setLong(2, iL_phosphorylationid);
        lStat.setLong(3, iConversionid);
        lStat.setLong(4, iConversionid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO id_to_phospho (l_id, l_phosphorylationid, conversionid) values(?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
        if (iL_id == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iL_id);
        }
        if (iL_phosphorylationid == Long.MIN_VALUE) {
            lStat.setNull(2, 4);
        } else {
            lStat.setLong(2, iL_phosphorylationid);
        }
        if (iConversionid == Long.MIN_VALUE) {
            lStat.setNull(3, 4);
        } else {
            lStat.setLong(3, iConversionid);
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
