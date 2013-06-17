/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 13/04/2010
 * Time: 15:53:29
 */
package com.compomics.mslimsdb.accessors;

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
 * This class is a generated accessor for the Spectrum_file table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Spectrum_fileTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'l_spectrumid' column.
     */
    protected long iL_spectrumid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'file' column.
     */
    protected byte[] iFile = null;


    /**
     * This variable represents the key for the 'l_spectrumid' column.
     */
    public static final String L_SPECTRUMID = "L_SPECTRUMID";

    /**
     * This variable represents the key for the 'file' column.
     */
    public static final String FILE = "FILE";


    /**
     * Default constructor.
     */
    public Spectrum_fileTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'Spectrum_fileTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public Spectrum_fileTableAccessor(HashMap aParams) {
        if (aParams.containsKey(L_SPECTRUMID)) {
            this.iL_spectrumid = ((Long) aParams.get(L_SPECTRUMID)).longValue();
        }
        if (aParams.containsKey(FILE)) {
            this.iFile = (byte[]) aParams.get(FILE);
        }
        this.iUpdated = true;
    }


    /**
     * This constructor allows the creation of the 'Spectrum_fileTableAccessor' object based on a resultset obtained by
     * a 'select * from Spectrum_file' query.
     *
     * @param aResultSet ResultSet with the required columns to initialize this object with.
     * @throws SQLException when the ResultSet could not be read.
     */
    public Spectrum_fileTableAccessor(ResultSet aResultSet) throws SQLException {
        this.iL_spectrumid = aResultSet.getLong("l_spectrumid");
        InputStream is1 = aResultSet.getBinaryStream("file");
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
        this.iFile = new byte[reading];
        for (int i = 0; i < reading; i++) {
            this.iFile[i] = ((Byte) bytes1.get(i)).byteValue();
        }

        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'L_spectrumid' column
     *
     * @return long    with the value for the L_spectrumid column.
     */
    public long getL_spectrumid() {
        return this.iL_spectrumid;
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
     * This method sets the value for the 'L_spectrumid' column
     *
     * @param aL_spectrumid long with the value for the L_spectrumid column.
     */
    public void setL_spectrumid(long aL_spectrumid) {
        this.iL_spectrumid = aL_spectrumid;
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
     * This method allows the caller to delete the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int delete(Connection aConn) throws SQLException {
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM spectrum_file WHERE l_spectrumid = ?");
        lStat.setLong(1, iL_spectrumid);
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
        if (!aKeys.containsKey(L_SPECTRUMID)) {
            throw new IllegalArgumentException("Primary key field 'L_SPECTRUMID' is missing in HashMap!");
        } else {
            iL_spectrumid = ((Long) aKeys.get(L_SPECTRUMID)).longValue();
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM spectrum_file WHERE l_spectrumid = ?");
        lStat.setLong(1, iL_spectrumid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iL_spectrumid = lRS.getLong("l_spectrumid");
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
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'spectrum_file' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'spectrum_file' table! Object is not initialized correctly!");
        }
    }

    /**
     * This method allows the caller to obtain a basic select for this table.
     *
     * @return String with the basic select statement for this table.
     */
    public static String getBasicSelect() {
        return "select * from spectrum_file";
    }

    /**
     * This method allows the caller to obtain all rows for this table from a persistent store.
     *
     * @param aConn Connection to the persitent store.
     * @return ArrayList<Spectrum_fileTableAccessor>   with all entries for this table.
     */
    public static ArrayList<Spectrum_fileTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
        ArrayList<Spectrum_fileTableAccessor> entities = new ArrayList<Spectrum_fileTableAccessor>();
        Statement stat = aConn.createStatement();
        ResultSet rs = stat.executeQuery(getBasicSelect());
        while (rs.next()) {
            entities.add(new Spectrum_fileTableAccessor(rs));
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE spectrum_file SET l_spectrumid = ?, file = ? WHERE l_spectrumid = ?");
        lStat.setLong(1, iL_spectrumid);
        ByteArrayInputStream bais1 = new ByteArrayInputStream(iFile);
        lStat.setBinaryStream(2, bais1, iFile.length);
        lStat.setLong(3, iL_spectrumid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO spectrum_file (l_spectrumid, file) values(?, ?)");
        if (iL_spectrumid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iL_spectrumid);
        }
        if (iFile == null) {
            lStat.setNull(2, -4);
        } else {
            ByteArrayInputStream bais1 = new ByteArrayInputStream(iFile);
            lStat.setBinaryStream(2, bais1, iFile.length);
        }
        int result = lStat.executeUpdate();
        this.iUpdated = false;
        lStat.close();
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