/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 25/08/2003
 * Time: 11:42:25
 */
package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import com.compomics.util.db.interfaces.Deleteable;
import com.compomics.util.db.interfaces.Persistable;
import com.compomics.util.db.interfaces.Retrievable;
import com.compomics.util.db.interfaces.Updateable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class is a generated accessor for the Pklfiles table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class PklfilesTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for PklfilesTableAccessor instances.
    private static Logger logger = Logger.getLogger(PklfilesTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'filename' column.
     */
    protected String iFilename = null;


    /**
     * This variable represents the contents for the 'file' column.
     */
    protected byte[] iFile = null;


    /**
     * This variable represents the contents for the 'identified' column.
     */
    protected int iIdentified = Integer.MIN_VALUE;


    /**
     * This variable represents the contents for the 'creationdate' column.
     */
    protected java.sql.Timestamp iCreationdate = null;


    /**
     * This variable represents the key for the 'filename' column.
     */
    public static final String FILENAME = "FILENAME";

    /**
     * This variable represents the key for the 'file' column.
     */
    public static final String FILE = "FILE";

    /**
     * This variable represents the key for the 'identified' column.
     */
    public static final String IDENTIFIED = "IDENTIFIED";

    /**
     * This variable represents the key for the 'creationdate' column.
     */
    public static final String CREATIONDATE = "CREATIONDATE";


    /**
     * Default constructor.
     */
    public PklfilesTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'PklfilesTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public PklfilesTableAccessor(HashMap aParams) {
        if (aParams.containsKey(FILENAME)) {
            this.iFilename = (String) aParams.get(FILENAME);
        }
        if (aParams.containsKey(FILE)) {
            this.iFile = (byte[]) aParams.get(FILE);
        }
        if (aParams.containsKey(IDENTIFIED)) {
            this.iIdentified = ((Integer) aParams.get(IDENTIFIED)).intValue();
        }
        if (aParams.containsKey(CREATIONDATE)) {
            this.iCreationdate = (java.sql.Timestamp) aParams.get(CREATIONDATE);
        }
        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'Filename' column
     *
     * @return String    with the value for the Filename column.
     */
    public String getFilename() {
        return this.iFilename;
    }

    /**
     * This method returns the value for the 'File' column
     *
     * @return byte[]    with the value for the File column.
     */
    public byte[] getFile() {
        return this.iFile;
    }

    /**
     * This method returns the value for the 'Identified' column
     *
     * @return int    with the value for the Identified column.
     */
    public int getIdentified() {
        return this.iIdentified;
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
     * This method sets the value for the 'Filename' column
     *
     * @param aFilename String with the value for the Filename column.
     */
    public void setFilename(String aFilename) {
        this.iFilename = aFilename;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'File' column
     *
     * @param aFile byte[] with the value for the File column.
     */
    public void setFile(byte[] aFile) {
        this.iFile = aFile;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Identified' column
     *
     * @param aIdentified int with the value for the Identified column.
     */
    public void setIdentified(int aIdentified) {
        this.iIdentified = aIdentified;
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
     * This method allows the caller to delete the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int delete(Connection aConn) throws SQLException {
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM pklfiles WHERE filename = ?");
        lStat.setObject(1, iFilename);
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
        if (!aKeys.containsKey(FILENAME)) {
            throw new IllegalArgumentException("Primary key field 'FILENAME' is missing in HashMap!");
        } else {
            iFilename = (String) aKeys.get(FILENAME);
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM pklfiles WHERE filename = ?");
        lStat.setObject(1, iFilename);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iFilename = (String) lRS.getObject("filename");
            InputStream is1 = lRS.getBinaryStream("file");
            Vector bytes1 = new Vector();
            int reading = -1;
            try {
                while ((reading = is1.read()) != -1) {
                    bytes1.add(new Byte((byte) reading));
                }
                is1.close();
            } catch (IOException ioe) {
                bytes1 = new Vector();
            }
            reading = bytes1.size();
            iFile = new byte[reading];
            for (int i = 0; i < reading; i++) {
                iFile[i] = ((Byte) bytes1.get(i)).byteValue();
            }
            iIdentified = lRS.getInt("identified");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'pklfiles' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'pklfiles' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE pklfiles SET filename = ?, file = ?, identified = ?, creationdate = ? WHERE filename = ?");
        lStat.setObject(1, iFilename);
        ByteArrayInputStream bais1 = new ByteArrayInputStream(iFile);
        lStat.setBinaryStream(2, bais1, iFile.length);
        lStat.setInt(3, iIdentified);
        lStat.setObject(4, iCreationdate);
        lStat.setObject(5, iFilename);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO pklfiles (filename, file, identified, creationdate) values(?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
        if (iFilename == null) {
            lStat.setNull(1, 12);
        } else {
            lStat.setObject(1, iFilename);
        }
        if (iFile == null) {
            lStat.setNull(2, -4);
        } else {
            ByteArrayInputStream bais1 = new ByteArrayInputStream(iFile);
            lStat.setBinaryStream(2, bais1, iFile.length);
        }
        if (iIdentified == Integer.MIN_VALUE) {
            lStat.setNull(3, 5);
        } else {
            lStat.setInt(3, iIdentified);
        }
        if (iCreationdate == null) {
            lStat.setNull(4, 93);
        } else {
            lStat.setObject(4, iCreationdate);
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
