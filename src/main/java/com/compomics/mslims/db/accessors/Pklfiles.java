/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 11-feb-03
 * Time: 9:57:22
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:17 $
 */

/**
 * This class adds three useful methods to the PklfilesTableAccessor class: <ul> <li><b>getUnzippedFile()</b>: returns
 * the pklfile as a stream of unzipped bytes.</li> <li><b></b>setUnzippedFile(byte[] aBytes): allows the setting and
 * simultaneous zipping of unzipped bytes.</li> <li><b></b>setFileFromName(String aFilename): allows the setting and
 * simultaneous zipping of a file and filename from the fully qualified file name.</li> </ul> And a constructor which
 * can create a pklfile from a ResultSet.
 *
 * @author Lennart Martens
 */
public class Pklfiles extends PklfilesTableAccessor {
    // Class specific log4j logger for Pklfiles instances.
    private static Logger logger = Logger.getLogger(Pklfiles.class);

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
    public Pklfiles(HashMap aParams) {
        super(aParams);
        if (aParams.containsKey(FROMFILE)) {
            try {
                this.setFileFromName((String) aParams.get(FROMFILE));
            } catch (IOException ioe) {
                throw new IllegalArgumentException("Unable to process file '" + aParams.get(FROMFILE) + "': " + ioe.getMessage() + "!");
            }
        }
    }

    /**
     * This constructor reads the PKL file from a resultset. The ResultSet should be positioned such that a single row
     * can be read directly (i.e., without calling the 'next()' method on the ResultSet). The columns should be in this
     * order: <br /> Column 1: filename <br /> Column 2: GZIPped bytes for the file. <br /> Column 3: number of
     * identifications. <br /> Column 4: creationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Pklfiles(ResultSet aRS) throws SQLException {

        iFilename = (String) aRS.getObject(1);
        InputStream is1 = aRS.getBinaryStream(2);
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
        iIdentified = aRS.getInt(3);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(4);
    }

    /**
     * Default constructor.
     */
    public Pklfiles() {
        super();
    }

    /**
     * This method returns the PKL file as unzipped bytes.
     *
     * @return byte[]  with the unzipped bytes for the PKL file.
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
     * This method allows the on-the fly zipping of data that is put in the DB.
     *
     * @param aBytes byte[] with the data for the PKL file. This data will be zipped and subsequently sent to the
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
}
