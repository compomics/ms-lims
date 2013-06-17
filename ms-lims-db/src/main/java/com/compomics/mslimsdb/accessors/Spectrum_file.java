package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by IntelliJ IDEA. User: kenny Date: Apr 9, 2010 Time: 2:49:25 PM
 * <p/>
 * This class
 */


/**
 * This class adds three useful methods to the SpectrumTableAccessor class: <ul> <li><b>getUnzippedFile()</b>: returns
 * the spectrum file as a stream of unzipped bytes.</li> <li><b></b>setUnzippedFile(byte[] aBytes): allows the setting
 * and simultaneous zipping of unzipped bytes.</li> <li><b></b>setFileFromName(String aFilename): allows the setting and
 * simultaneous zipping of a file and filename from the fully qualified file name.</li> </ul> And a constructor which
 * can create a Spectrum from a ResultSet.
 *
 * @author Lennart Martens
 */

public class Spectrum_file extends Spectrum_fileTableAccessor {

    // Class specific log4j logger for Spectrum_file instances.
    private static Logger logger = Logger.getLogger(Spectrum_file.class);

    /**
     * This constructor just maps the superclass constructor.
     *
     * @param aParams HashMap with the values to set.
     */
    public Spectrum_file(HashMap aParams) {
        super(aParams);

    }

    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Spectrum_file(ResultSet aRS) throws SQLException {
        super(aRS);
    }

    /**
     * Default constructor.
     */
    public Spectrum_file() {
        super();
    }

    /**
     * This method returns the actual Spectrum_file instance for this Spectrum.
     *
     * @param aSpectrumfileID The spectrumid of the requested spectrum
     * @param aConn           Connection to read the spectrum File from.
     * @return Spectrum_file with the actual Spectrum.
     */
    public static Spectrum_file findFromID(long aSpectrumfileID, Connection aConn) throws SQLException {
        Spectrum_file temp = null;
        PreparedStatement ps = aConn.prepareStatement(Spectrum_file.getBasicSelect() + " where l_spectrumid=?");
        ps.setLong(1, aSpectrumfileID);
        ResultSet rs = ps.executeQuery();
        int lCounter = 0;
        while (rs.next()) {
            temp = new Spectrum_file(rs);
            lCounter++;
        }
        rs.close();
        ps.close();
        if (lCounter > 1) {
            SQLException sqe = new SQLException("Select based on spectrumfile ID '" + aSpectrumfileID + "' resulted in " + lCounter + " results instead of 1!");
            logger.error(sqe.getMessage(), sqe);
            throw sqe;
        }
        return temp;
    }

    /**
     * This method returns the spectrum file as unzipped bytes.
     *
     * @return byte[]  with the unzipped bytes for the spectrum file.
     * @throws IOException when the unzipping process goes wrong.
     */
    public byte[] getUnzippedFile
            () throws IOException {
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
     * This method returns the specified gzipped byte[] as unzipped bytes.
     *
     * @param aZippedBytes byte[] with the GZIPped bytes.
     * @return byte[]  with the unzipped bytes for the spectrum file.
     * @throws IOException when the unzipping process goes wrong.
     */
    public static byte[] getUnzippedFile
            (
                    byte[] aZippedBytes) throws IOException {
        byte[] result = null;

        ByteArrayInputStream bais = new ByteArrayInputStream(aZippedBytes);
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
     * This method allows the setting of a file (it sets filename and the zipped bytes for the file).
     *
     * @param aFilename String with the FULL filename! After the file has been zipped, the filename is lost.
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
        super.setFile(bytes);
    }


    /**
     * This method returns the basic 'select' statement that includes all columns in the correct sequence in the
     * ResultSet so that the constructor using a ResultSet can be safely called.
     *
     * @return String with the basic select ('select x, y, z, ... from [table]') which can be extended with eg., a
     *         where-clause.
     */
    public static String getBasicSelect() {
        return "select l_spectrumid, file from spectrum_file";
    }

}