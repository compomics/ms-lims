package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA. User: Niklaas Colaert Date: 21-nov-2008 Time: 12:01:22 To change this template use File |
 * Settings | File Templates.
 */

public class Quantitation_file extends Quantitation_fileTableAccessor {
    // Class specific log4j logger for Quantitation_file instances.
    private static Logger logger = Logger.getLogger(Quantitation_file.class);

    /**
     * This key in the HashMap allows the setting of file and filename to be replaced by the fully qualified filename
     * only.
     */
    public static final String FROMFILE = "FROMFILE";

    /**
     * This constructor just maps the superclass constructor.
     *
     * @param aParams HashMap with the values to set.
     */
    public Quantitation_file(HashMap aParams) {
        super(aParams);
        if (aParams.containsKey(FROMFILE)) {
            try {
                this.setFileFromName((String) aParams.get(FROMFILE));
            } catch (IOException ioe) {
                throw new IllegalArgumentException("Unable to process file '" + (String) aParams.get(FROMFILE) + "': " + ioe.getMessage() + "!");
            }
        }
    }

    /**
     * This constructor reads the quantitation file from a resultset. The ResultSet should be positioned such that a
     * single row can be read directly (i.e., without calling the 'next()' method on the ResultSet). The columns should
     * be in this order: <br /> Column 1: distiller_output_fileid <br /> Column 2: filename  <br /> Column 3: type  <br
     * /> Column 4: GZIPped bytes for thefile <br /> Column 5: username <br /> Column 6: creationdate <br /> Column 7:
     * modificationdate <br />
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Quantitation_file(ResultSet aRS) throws SQLException {

        iQuantitation_fileid = aRS.getLong(1);
        iFilename = aRS.getString(2);
        iType = aRS.getString(3);
        InputStream is1 = aRS.getBinaryStream(4);
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
        iUsername = aRS.getString(5);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(6);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(7);
    }

    /**
     * Default constructor.
     */
    public Quantitation_file() {
        super();
    }

    /**
     * This method returns the DAT file as unzipped bytes.
     *
     * @return byte[]  with the unzipped bytes for the DAT file.
     * @throws IOException when the unzipping process goes wrong.
     */
    public byte[] getUnzippedFile() throws IOException {
        byte[] result = null;

        byte[] zipped = super.getFile();
        ByteArrayInputStream bais = new ByteArrayInputStream(zipped);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        int read = -1;
        while ((read = bis.read()) != -1) {
            bos.write(read);
        }
        bos.flush();
        baos.flush();
        result = baos.toByteArray();
        bos.close();
        bis.close();
        bais.close();
        baos.close();

        return result;
    }

    /**
     * This method returns a BufferedReader into the unzipped DAT file. It is up to the caller to close the reader.
     *
     * @return BufferedReader  connected to the unzipped DAT file.
     * @throws IOException when the unzipping process goes wrong.
     */
    public BufferedReader getBufferedReader() throws IOException {
        byte[] zipped = super.getFile();
        ByteArrayInputStream bais = new ByteArrayInputStream(zipped);
        BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
        BufferedReader br = new BufferedReader(new InputStreamReader(bis));
        return br;
    }

    /**
     * This method allows the on-the fly zipping of data that is put in the DB.
     *
     * @param aBytes byte[] with the data for the DAT file. This data will be zipped and subsequently sent to the
     *               superclass.
     * @throws IOException when the zipping process fails.
     */
    public void setUnzippedFile(byte[] aBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        BufferedOutputStream bos = new BufferedOutputStream(gos);
        ByteArrayInputStream bais = new ByteArrayInputStream(aBytes);
        BufferedInputStream bis = new BufferedInputStream(bais);
        int read = -1;
        while ((read = bis.read()) != -1) {
            bos.write(read);
        }
        bos.flush();
        baos.flush();
        gos.finish();
        super.setFile(baos.toByteArray());
        bis.close();
        bos.close();
        gos.close();
        bais.close();
        baos.close();
    }

    /**
     * This method allows the setting of a file (it sets filename and the zipped bytes for the file).
     *
     * @param aFilename String with the FULL filename!
     * @throws IOException whenever the file could not be found, could not be read or could not be zipped.
     */
    public void setFileFromName(String aFilename) throws IOException {
        byte[] bytes = null;
        String name = null;

        File f = new File(aFilename);
        if (!f.exists()) {
            throw new IOException("File '" + aFilename + "' does not exist!");
        }
        // File seems to exist.
        // Get the filename.
        name = f.getName();
        // Get the GZIPped bytes.
        bytes = this.zipFile(f);
        super.setFilename(name);
        super.setFile(bytes);
    }

    /**
     * This method loads and zips the file data.
     *
     * @param aFile File with the data.
     * @return byte[]  with the GZIPped data.
     * @throws IOException whenever the GZIPping process fails.
     */
    private byte[] zipFile(File aFile) throws IOException {
        byte[] bytes = null;

        // Read it, and write the bytes to a GZIPped outputstream.
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(aFile));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        int reading = -1;
        while ((reading = bis.read()) != -1) {
            gos.write(reading);
        }
        gos.finish();
        bis.close();
        baos.flush();
        bytes = baos.toByteArray();
        gos.close();
        baos.close();

        return bytes;
    }

    /**
     * This static method verifies whether the given Quantitation file allready has an entry in the ms_lims system of
     * the given connection.
     *
     * @param aFilename   The 'quantitation_file' filename.
     * @param aConnection The Connection where to the database.
     * @return boolean status whether the file was found in the given database
     * @throws SQLException Throwed when an SQL error occurs.
     */
    public static boolean isStoredInDatabase(String aFilename, Connection aConnection) throws SQLException {
        PreparedStatement prep =
                aConnection.prepareStatement("select filename from quantitation_file where filename=? ");
        prep.setString(1, aFilename);
        ResultSet rs = prep.executeQuery();
        // Any row that returns indicates the distiller file has allready been processed!!
        return rs.next();
    }

    /**
     * This method get all the quantitation_files for a list of identificationids from the database.
     *
     * @param aConn              Connection to load the identification from.
     * @param aIdentificationIds String with the identificationids seperatated by ','
     * @return Quantitation_file The requested quantitationfiles
     * @throws SQLException when the select failed.
     */
    public static Quantitation_file[] getQuantitation_fileForIdentificationIds(Connection aConn, String aIdentificationIds) throws SQLException {
        Quantitation_file[] result = null;
        String query = "select f.* from identification as i, identification_to_quantitation as t, quantitation_group as g, quantitation_file as f where i.identificationid in (" + aIdentificationIds + ") and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = g.quantitation_groupid and q.L_quantitation_fileid  = f.quantitation_fileid group by filename ";
        PreparedStatement ps = aConn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        Vector<Quantitation_file> lLinkers = new Vector<Quantitation_file>();
        while (rs.next()) {
            lLinkers.add(new Quantitation_file(rs));
        }
        rs.close();
        ps.close();
        result = new Quantitation_file[lLinkers.size()];
        lLinkers.toArray(result);
        return result;
    }

    /**
     * This method gets the quantitation_file for a quantitation_file id from the database.
     *
     * @param aConn   Connection to load the identification from.
     * @param aFileId The id for the quantitation file
     * @return Quantitation_file The requested quantitationfile
     * @throws SQLException when the select failed.
     */
    public static Quantitation_file getQuantitation_fileForId(Connection aConn, Long aFileId) throws SQLException {
        Quantitation_file result = null;
        PreparedStatement ps = aConn.prepareStatement("select * from quantitation_file as f where f.quantitation_fileid = ?");
        ps.setLong(1, aFileId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        result = new Quantitation_file(rs);
        ps.close();
        rs.close();
        return result;
    }
}
