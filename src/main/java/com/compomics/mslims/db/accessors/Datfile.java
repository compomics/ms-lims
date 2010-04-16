/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 31-mrt-03
 * Time: 17:09:23
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;


import com.compomics.rover.general.db.accessors.IdentificationExtension;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2009/05/18 08:01:11 $
 */

/**
 * This class adds three useful methods to the DatfilesTableAccessor class: <ul> <li><b>getUnzippedFile()</b>: returns
 * the datfile as a stream of unzipped bytes.</li> <li><b></b>setUnzippedFile(byte[] aBytes): allows the setting and
 * simultaneous zipping of unzipped bytes.</li> <li><b></b>setFileFromName(String aFilename): allows the setting and
 * simultaneous zipping of a file and filename from the fully qualified file name.</li> </ul>
 *
 * @author Lennart Martens
 */
public class Datfile extends DatfileTableAccessor {
    // Class specific log4j logger for Datfile instances.
    private static Logger logger = Logger.getLogger(Datfile.class);

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
    public Datfile(HashMap aParams) {
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
     * This constructor reads the DAT file from a resultset. The ResultSet should be positioned such that a single row
     * can be read directly (i.e., without calling the 'next()' method on the ResultSet). The columns should be in this
     * order: <br /> Column 1: datfileid <br /> Column 2: filename <br /> Column 3: GZIPped bytes for the file <br />
     * Column 4: server <br /> Column 5: folder <br /> Column 6: username <br /> Column 7: creationdate <br /> Column 8:
     * modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Datfile(ResultSet aRS) throws SQLException {

        iDatfileid = aRS.getLong(1);
        iFilename = (String) aRS.getObject(2);
        InputStream is1 = aRS.getBinaryStream(3);
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
        iServer = aRS.getString(4);
        iFolder = aRS.getString(5);
        iUsername = aRS.getString(6);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(7);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(8);
    }

    /**
     * Default constructor.
     */
    public Datfile() {
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
     * This method returns an array with Identifications, containing all Identification entries that could be retrieved
     * for the specified datfile.
     *
     * @param aFilename String with the datfile filename.
     * @param aServer   String with the datfile server.
     * @param aFolder   String with the datfile folder.
     * @param aConn     Connection to read the data from.
     * @return Array with the identifications for the requested datfile
     * @throws SQLException when the retrieve failed.
     */
    public static Identification[] getIdentificationsForDatfile(String aFilename, String aServer, String aFolder, Connection aConn) throws SQLException {
        Vector temp = new Vector();

        PreparedStatement ps =
                aConn.prepareStatement("select i.* from identification as i, datfile as d where d.filename = ? and d.server = ? and d.folder = ? and i.l_datfileid = d.datfileid");
        ps.setString(1, aFilename);
        ps.setString(2, aServer);
        ps.setString(3, aFolder);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Identification(rs));
        }

        rs.close();
        ps.close();
        Identification[] lIdentifications = new Identification[temp.size()];
        temp.toArray(lIdentifications);
        return lIdentifications;
    }

    /**
     * This method returns an array with IdentificationExtension, containing all IdentificationExtension entries that
     * could be retrieved for the specified datfile.
     *
     * @param aFilename String with the datfile filename.
     * @param aServer   String with the datfile server.
     * @param aFolder   String with the datfile folder.
     * @param aConn     Connection to read the data from.
     * @return Array with the IdentificationExtension for the requested datfile
     * @throws SQLException when the retrieve failed.
     */

    public static IdentificationExtension[] getIdentificationExtensionsForDatfile(String aFilename, String aServer, String aFolder, Connection aConn) throws SQLException {
        Vector temp = new Vector();

        PreparedStatement ps =
                aConn.prepareStatement("select i.*, s.filename from identification as i, datfile as d, spectrum as s where d.filename = ? and d.server = ? and d.folder = ? and i.l_datfileid = d.datfileid and i.l_spectrumid = s.spectrumid");
        ps.setString(1, aFilename);
        ps.setString(2, aServer);
        ps.setString(3, aFolder);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new IdentificationExtension(rs));
        }

        rs.close();
        ps.close();
        IdentificationExtension[] lIdentifications = new IdentificationExtension[temp.size()];
        temp.toArray(lIdentifications);
        return lIdentifications;

    }

}
