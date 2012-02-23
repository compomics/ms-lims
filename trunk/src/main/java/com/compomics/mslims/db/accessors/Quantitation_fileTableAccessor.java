/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 20/01/2009
 * Time: 15:15:30
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
 * $Revision: 1.2 $
 * $Date: 2009/01/30 10:31:05 $
 */

/**
 * This class is a generated accessor for the Quantitation_file table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Quantitation_fileTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for Quantitation_fileTableAccessor instances.
    private static Logger logger = Logger.getLogger(Quantitation_fileTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'quantitation_fileid' column.
     */
    protected long iQuantitation_fileid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'filename' column.
     */
    protected String iFilename = null;


    /**
     * This variable represents the contents for the 'type' column.
     */
    protected String iType = null;


    /**
     * This variable represents the contents for the 'file' column.
     */
    protected byte[] iFile = null;


    /**
     * This variable represents the contents for the 'binary' column.
     */
    protected byte[] iBinary = null;

    protected String iVersionNumber;

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
     * This variable represents the key for the 'quantitation_fileid' column.
     */
    public static final String QUANTITATION_FILEID = "QUANTITATION_FILEID";

    /**
     * This variable represents the key for the 'filename' column.
     */
    public static final String FILENAME = "FILENAME";

    /**
     * This variable represents the key for the 'type' column.
     */
    public static final String TYPE = "TYPE";

    /**
     * This variable represents the key for the 'file' column.
     */
    public static final String FILE = "FILE";

    /**
     * This variable represents the key for the 'binary' column.
     */
    public static final String BINARY_FILE = "BINARY_FILE";


    public static final  String VERSION_NUMBER = "VERSION_NUMBER";
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
    public Quantitation_fileTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'Quantitation_fileTableAccessor' object based on a set of values in
     * the HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public Quantitation_fileTableAccessor(HashMap aParams) {
        if (aParams.containsKey(QUANTITATION_FILEID)) {
            this.iQuantitation_fileid = ((Long) aParams.get(QUANTITATION_FILEID)).longValue();
        }
        if (aParams.containsKey(FILENAME)) {
            this.iFilename = (String) aParams.get(FILENAME);
        }
        if (aParams.containsKey(TYPE)) {
            this.iType = (String) aParams.get(TYPE);
        }
        if (aParams.containsKey(FILE)) {
            this.iFile = (byte[]) aParams.get(FILE);
        }
        if (aParams.containsKey(BINARY_FILE)){
            this.iBinary = (byte[]) aParams.get(BINARY_FILE);
        }
        if (aParams.containsKey(VERSION_NUMBER)){
            this.iVersionNumber = (String) aParams.get(VERSION_NUMBER);
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
     * This constructor allows the creation of the 'Quantitation_fileTableAccessor' object based on a resultset obtained
     * by a 'select * from Quantitation_file' query.
     *
     * @param aResultSet ResultSet with the required columns to initialize this object with.
     * @throws SQLException when the ResultSet could not be read.
     */
    public Quantitation_fileTableAccessor(ResultSet aResultSet) throws SQLException {
        this.iQuantitation_fileid = aResultSet.getLong("quantitation_fileid");
        this.iFilename = (String) aResultSet.getObject("filename");
        this.iType = (String) aResultSet.getObject("type");
        InputStream is4 = aResultSet.getBinaryStream("file");
        Vector bytes4 = new Vector();
        int reading = -1;
        try {
            while ((reading = is4.read()) != -1) {
                bytes4.add(new Byte((byte) reading));
            }
            is4.close();
        } catch (IOException ioe) {
            bytes4 = new Vector();
        }
        reading = bytes4.size();
        this.iFile = new byte[reading];
        for (int i = 0; i < reading; i++) {
            this.iFile[i] = ((Byte) bytes4.get(i)).byteValue();
        }
        InputStream is5 = aResultSet.getBinaryStream("binary_file");
        Vector bytes5 = new Vector();
        reading = -1;
        try {
            while ((reading = is5.read()) != -1) {
                bytes4.add(new Byte((byte) reading));
            }
            is5.close();
        } catch (IOException ioe) {
            bytes5 = new Vector();
        }
        reading = bytes5.size();
        this.iBinary = new byte[reading];
        for (int i = 0; i < reading; i++) {
            this.iBinary[i] = (Byte) bytes5.get(i);
        }
        this.iVersionNumber = (String) aResultSet.getObject("version_number");
        this.iUsername = (String) aResultSet.getObject("username");
        this.iCreationdate = (java.sql.Timestamp) aResultSet.getObject("creationdate");
        this.iModificationdate = (java.sql.Timestamp) aResultSet.getObject("modificationdate");

        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'Quantitation_fileid' column
     *
     * @return long    with the value for the Quantitation_fileid column.
     */
    public long getQuantitation_fileid() {
        return this.iQuantitation_fileid;
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
     * This method returns the value for the 'Type' column
     *
     * @return String    with the value for the Type column.
     */
    public String getType() {
        return this.iType;
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
     * This method returns the value for the 'binary' column
     *
     * @return byte[]    with the value for the binary column.
     */
    public byte[] getBinary() {
        return  this.iBinary;
    }

    public String getVersionNumber() {
        return this.iVersionNumber;
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
     * This method sets the value for the 'Quantitation_fileid' column
     *
     * @param aQuantitation_fileid long with the value for the Quantitation_fileid column.
     */
    public void setQuantitation_fileid(long aQuantitation_fileid) {
        this.iQuantitation_fileid = aQuantitation_fileid;
        this.iUpdated = true;
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
     * This method sets the value for the 'Type' column
     *
     * @param aType String with the value for the Type column.
     */
    public void setType(String aType) {
        this.iType = aType;
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
     * This method sets the value for the 'binary' column
     *
     * @param aBinary byte[] with the value for the binary column.
     */
    public void setBinary(byte[] aBinary){
        this.iBinary = aBinary;
        logger.info(iBinary.length);
        this.iUpdated = true;
    }

    public void setVersionNumber(String aVersionNumber){
        this.iVersionNumber = aVersionNumber;
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
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM quantitation_file WHERE quantitation_fileid = ?");
        lStat.setLong(1, iQuantitation_fileid);
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
        if (!aKeys.containsKey(QUANTITATION_FILEID)) {
            throw new IllegalArgumentException("Primary key field 'QUANTITATION_FILEID' is missing in HashMap!");
        } else {
            iQuantitation_fileid = ((Long) aKeys.get(QUANTITATION_FILEID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM quantitation_file WHERE quantitation_fileid = ?");
        lStat.setLong(1, iQuantitation_fileid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iQuantitation_fileid = lRS.getLong("quantitation_fileid");
            iFilename = (String) lRS.getObject("filename");
            iType = (String) lRS.getObject("type");
            InputStream is4 = lRS.getBinaryStream("file");
            Vector bytes4 = new Vector();
            int reading = -1;
            try {
                while ((reading = is4.read()) != -1) {
                    bytes4.add(new Byte((byte) reading));
                }
                is4.close();
            } catch (IOException ioe) {
                bytes4 = new Vector();
            }
            reading = bytes4.size();
            iFile = new byte[reading];
            for (int i = 0; i < reading; i++) {
                iFile[i] = ((Byte) bytes4.get(i)).byteValue();
            }
             InputStream is5 = lRS.getBinaryStream("binary_file");
            Vector bytes5 = new Vector();
            reading = -1;
            try {
                while ((reading = is5.read()) != -1) {
                    bytes5.add((byte) reading);
                }
                is5.close();
            } catch (IOException ioe) {
                bytes4 = new Vector();
            }
            reading = bytes5.size();
            iBinary = new byte[reading];
            for (int i = 0; i < reading; i++) {
                iBinary[i] = (Byte) bytes5.get(i);
            }
            iUsername = (String) lRS.getObject("username");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'quantitation_file' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'quantitation_file' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE quantitation_file SET quantitation_fileid = ?, filename = ?, type = ?, file = ?, binary_file = ?, version_number = ? ,username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE quantitation_fileid = ?");
        lStat.setLong(1, iQuantitation_fileid);
        lStat.setObject(2, iFilename);
        lStat.setObject(3, iType);
        ByteArrayInputStream bais4 = new ByteArrayInputStream(iFile);
        lStat.setBinaryStream(4, bais4, iFile.length);
        ByteArrayInputStream bais5 = new ByteArrayInputStream(iBinary);
        lStat.setBinaryStream(5, bais5, iBinary.length);
        lStat.setObject(6,iVersionNumber);
        lStat.setObject(7, iUsername);
        lStat.setObject(8, iCreationdate);
        lStat.setLong(9, iQuantitation_fileid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO quantitation_file (quantitation_fileid,filename,type,file,binary_file,version_number,username,creationdate,modificationdate) values(?,?,?,?,?,?,CURRENT_USER,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
        if (iQuantitation_fileid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iQuantitation_fileid);
        }
        if (iFilename == null) {
            lStat.setNull(2, 12);
        } else {
            lStat.setObject(2, iFilename);
        }
        if (iType == null) {
            lStat.setNull(3, 12);
        } else {
            lStat.setObject(3, iType);
        }
        if (iFile == null) {
            lStat.setNull(4, -4);
        } else {
            ByteArrayInputStream bais4 = new ByteArrayInputStream(iFile);
            lStat.setBinaryStream(4, bais4, iFile.length);
        }
        if(iBinary == null){
            lStat.setNull(5,-4);
        }
        else {
            ByteArrayInputStream bais5 = new ByteArrayInputStream(iBinary);
            lStat.setBinaryStream(5, bais5, iBinary.length);
        }
        if (iVersionNumber == null){
            lStat.setNull(6,12);
        } else {
            lStat.setObject(6,iVersionNumber);
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
