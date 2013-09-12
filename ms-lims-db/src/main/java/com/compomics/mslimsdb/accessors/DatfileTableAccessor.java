/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 30/12/2005
 * Time: 12:41:47
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
 * $Revision: 1.1 $
 * $Date: 2005/12/31 10:22:52 $
 */

/**
 * This class is a generated accessor for the Datfile table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class DatfileTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for DatfileTableAccessor instances.
    private static Logger logger = Logger.getLogger(DatfileTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'datfileid' column.
     */
    protected long iDatfileid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'filename' column.
     */
    protected String iFilename = null;


    /**
     * This variable represents the contents for the 'file' column.
     */
    protected byte[] iFile = null;


    /**
     * This variable represents the contents for the 'server' column.
     */
    protected String iServer = null;


    /**
     * This variable represents the contents for the 'folder' column.
     */
    protected String iFolder = null;


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
     * This variable represents the key for the 'datfileid' column.
     */
    public static final String DATFILEID = "DATFILEID";

    /**
     * This variable represents the key for the 'filename' column.
     */
    public static final String FILENAME = "FILENAME";

    /**
     * This variable represents the key for the 'file' column.
     */
    public static final String FILE = "FILE";

    /**
     * This variable represents the key for the 'server' column.
     */
    public static final String SERVER = "SERVER";

    /**
     * This variable represents the key for the 'folder' column.
     */
    public static final String FOLDER = "FOLDER";

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
    public DatfileTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'DatfileTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public DatfileTableAccessor(HashMap aParams) {
        if (aParams.containsKey(DATFILEID)) {
            this.iDatfileid = ((Long) aParams.get(DATFILEID)).longValue();
        }
        if (aParams.containsKey(FILENAME)) {
            this.iFilename = (String) aParams.get(FILENAME);
        }
        if (aParams.containsKey(FILE)) {
            this.iFile = (byte[]) aParams.get(FILE);
        }
        if (aParams.containsKey(SERVER)) {
            this.iServer = (String) aParams.get(SERVER);
        }
        if (aParams.containsKey(FOLDER)) {
            this.iFolder = (String) aParams.get(FOLDER);
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
     * This method returns the value for the 'Datfileid' column
     *
     * @return long    with the value for the Datfileid column.
     */
    public long getDatfileid() {
        return this.iDatfileid;
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
     * This method returns the value for the 'Server' column
     *
     * @return String    with the value for the Server column.
     */
    public String getServer() {
        return this.iServer;
    }

    /**
     * This method returns the value for the 'Folder' column
     *
     * @return String    with the value for the Folder column.
     */
    public String getFolder() {
        return this.iFolder;
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
     * This method sets the value for the 'Datfileid' column
     *
     * @param aDatfileid long with the value for the Datfileid column.
     */
    public void setDatfileid(long aDatfileid) {
        this.iDatfileid = aDatfileid;
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
     * This method sets the value for the 'File' column
     *
     * @param aFile byte[] with the value for the File column.
     */
    public void setFile(byte[] aFile) {
        this.iFile = aFile;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Server' column
     *
     * @param aServer String with the value for the Server column.
     */
    public void setServer(String aServer) {
        this.iServer = aServer;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Folder' column
     *
     * @param aFolder String with the value for the Folder column.
     */
    public void setFolder(String aFolder) {
        this.iFolder = aFolder;
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
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM datfile WHERE datfileid = ?");
        lStat.setLong(1, iDatfileid);
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
        if (!aKeys.containsKey(DATFILEID)) {
            throw new IllegalArgumentException("Primary key field 'DATFILEID' is missing in HashMap!");
        } else {
            iDatfileid = ((Long) aKeys.get(DATFILEID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM datfile WHERE datfileid = ?");
        lStat.setLong(1, iDatfileid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iDatfileid = lRS.getLong("datfileid");
            iFilename = (String) lRS.getObject("filename");
            InputStream is2 = lRS.getBinaryStream("file");
            Vector bytes2 = new Vector();
            int reading = -1;
            try {
                while ((reading = is2.read()) != -1) {
                    bytes2.add(new Byte((byte) reading));
                }
                is2.close();
            } catch (IOException ioe) {
                bytes2 = new Vector();
            }
            reading = bytes2.size();
            iFile = new byte[reading];
            for (int i = 0; i < reading; i++) {
                iFile[i] = ((Byte) bytes2.get(i)).byteValue();
            }
            iServer = (String) lRS.getObject("server");
            iFolder = (String) lRS.getObject("folder");
            iUsername = (String) lRS.getObject("username");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'datfile' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'datfile' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE datfile SET datfileid = ?, filename = ?, file = ?, server = ?, folder = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE datfileid = ?");
        lStat.setLong(1, iDatfileid);
        lStat.setObject(2, iFilename);
        ByteArrayInputStream bais2 = new ByteArrayInputStream(iFile);
        lStat.setBinaryStream(3, bais2, iFile.length);
        lStat.setObject(4, iServer);
        lStat.setObject(5, iFolder);
        lStat.setObject(6, iUsername);
        lStat.setObject(7, iCreationdate);
        lStat.setLong(8, iDatfileid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO datfile (datfileid, filename, file, server, folder, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",Statement.RETURN_GENERATED_KEYS);
        if (iDatfileid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iDatfileid);
        }
        if (iFilename == null) {
            lStat.setNull(2, 12);
        } else {
            lStat.setObject(2, iFilename);
        }
        if (iFile == null) {
            lStat.setNull(3, -4);
        } else {
            ByteArrayInputStream bais2 = new ByteArrayInputStream(iFile);
            lStat.setBinaryStream(3, bais2, iFile.length);
        }
        if (iServer == null) {
            lStat.setNull(4, 12);
        } else {
            lStat.setObject(4, iServer);
        }
        if (iFolder == null) {
            lStat.setNull(5, 12);
        } else {
            lStat.setObject(5, iFolder);
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
