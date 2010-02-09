/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 21-jun-03
 * Time: 17:21:13
 */
package com.compomics.mslims.db.accessors;


import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
 * CVS information:
 *
 * $Revision: 1.12 $
 * $Date: 2009/06/22 09:13:36 $
 */

/**
 * This class adds three useful methods to the SpectrumfileTableAccessor class:
 * <ul>
 *   <li><b>getUnzippedFile()</b>: returns the spectrum file as a stream of unzipped bytes.</li>
 *   <li><b></b>setUnzippedFile(byte[] aBytes): allows the setting and simultaneous zipping of unzipped bytes.</li>
 *   <li><b></b>setFileFromName(String aFilename): allows the setting and simultaneous zipping of a file
 *      and filename from the fully qualified file name.</li>
 * </ul>
 * And a constructor which can create a Spectrumfile from a ResultSet.
 *
 * @author Lennart Martens
 */
public class Spectrumfile extends SpectrumfileTableAccessor {

    /**
     * This key in the HashMap allows the setting of file and
     * filename to be replaced by the fully qualified filename only.
     */
    public static final String FROMFILE = "FROMFILE";

    /**
     * This constructor just maps the superclass constructor.
     *
     * @param   aParams HashMap with the values to set.
     */
    public Spectrumfile(HashMap aParams) {
        super(aParams);
        if(aParams.containsKey(FROMFILE)) {
            try {
                this.setFileFromName((String)aParams.get(FROMFILE));
            } catch(IOException ioe) {
                throw new IllegalArgumentException("Unable to process file '" + (String)aParams.get(FROMFILE) + "': " + ioe.getMessage() + "!");
            }
        }
    }

    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that
     * a single row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param   aRS ResultSet to read the data from.
     * @exception   SQLException    when reading the ResultSet failed.
     */
    public Spectrumfile(ResultSet aRS) throws SQLException {
        super(aRS);
    }

    /**
     * Default constructor.
     */
    public Spectrumfile() {
        super();
    }

    /**
     * This method returns the spectrum file as unzipped bytes.
     *
     * @return  byte[]  with the unzipped bytes for the spectrum file.
     * @exception   IOException when the unzipping process goes wrong.
     */
    public byte[] getUnzippedFile() throws IOException {
        byte[] result = null;

        byte[] zipped = super.getFile();
        ByteArrayInputStream bais = new ByteArrayInputStream(zipped);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        int read = -1;
        while((read = bis.read()) != -1) {
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
     * @param   aZippedBytes byte[] with the GZIPped bytes.
     * @return  byte[]  with the unzipped bytes for the spectrum file.
     * @exception   IOException when the unzipping process goes wrong.
     */
    public static byte[] getUnzippedFile(byte[] aZippedBytes) throws IOException {
        byte[] result = null;

        ByteArrayInputStream bais = new ByteArrayInputStream(aZippedBytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        int read = -1;
        while((read = bis.read()) != -1) {
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
     * This method allows the on-the fly zipping of data that is put in the
     * DB.
     *
     * @param   aBytes  byte[] with the data for the PKL file. This data
     *                  will be zipped and subsequently sent to the superclass.
     * @exception   IOException when the zipping process fails.
     */
    public void setUnzippedFile(byte[] aBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        BufferedOutputStream bos = new BufferedOutputStream(gos);
        ByteArrayInputStream bais = new ByteArrayInputStream(aBytes);
        BufferedInputStream bis = new BufferedInputStream(bais);
        int read = -1;
        while((read = bis.read()) != -1) {
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
     * This method allows the setting of a file (it sets filename and
     * the zipped bytes for the file).
     *
     * @param   aFilename   String with the FULL filename!
     * @exception IOException   whenever the file could not be found,
     *                          could not be read or could not be zipped.
     */
    public void setFileFromName(String aFilename) throws IOException {
        byte[] bytes = null;
        String name = null;

        File f = new File(aFilename);
        if(!f.exists()) {
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
     * This method will find a spectrum file from the current connection, based on the filename.
     *
     * @param aFileName  String with the filename of the spectrum file to find.
     * @param aConn Connection to read the spectrum File from.
     * @return  Spectrumfile with the data.
     * @exception   SQLException    when the retrieval did not succeed.
     */
    public static Spectrumfile findFromName(String aFileName, Connection aConn) throws SQLException {
        Spectrumfile temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where filename = ?");
        ps.setString(1, aFileName);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while(rs.next()) {
            counter++;
            temp = new Spectrumfile(rs);
        }
        rs.close();
        ps.close();
        if(counter != 1) {
            throw new SQLException("Select based on spectrumfile name '" + aFileName + "' resulted in " + counter + " results!");
        }

        return temp;
    }


    /**
     * This method will find a spectrum file from the current connection, based on the specified spectrumfileid.
     *
     * @param aSpectrumfileid  long with the spectrumfileid of the spectrum file to find.
     * @param aConn Connection to read the spectrum File from.
     * @return  Spectrumfile with the data.
     * @exception   SQLException    when the retrieval did not succeed.
     */
    public static Spectrumfile findFromID(long aSpectrumfileid, Connection aConn) throws SQLException {
        Spectrumfile temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where spectrumfileid = ?");
        ps.setLong(1, aSpectrumfileid);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while(rs.next()) {
            counter++;
            temp = new Spectrumfile(rs);
        }
        rs.close();
        ps.close();
        if(counter != 1) {
            throw new SQLException("Select based on spectrumfile ID '" + aSpectrumfileid + "' resulted in " + counter + " results instead of 1!");
        }
        return temp;
    }

    /**
     * This method will find a spectrum file from the current connection, based on the filename.
     *
     * @param aFileName  String with the filename of the sepctrum file to find.
     * @param aConn Connection to read the spectrum File from.
     * @return  Spectrumfile with the data.
     * @exception   SQLException    when the retrieval did not succeed.
     */
    public static Spectrumfile getFromName(String aFileName, Connection aConn) throws SQLException {
        return findFromName(aFileName, aConn);
    }

    /**
     * This method will find all spectrum files that link to the specified project
     * from the current connection.
     *
     * @param aProjectID  long with the ID of the project to find all spectra for.
     * @param aConn Connection to read the spectrum File from.
     * @return  Spectrumfile[] with the spectra.
     * @exception   SQLException    when the retrieval did not succeed.
     */
    public static Spectrumfile[] getAllSpectraForProject(long aProjectID, Connection aConn) throws SQLException {
        return getAllSpectraForProject(aProjectID, aConn, null);
    }

    /**
     * This method will find all spectrum files that link to the specified project
     * under the specified conditions from the current connection.
     *
     * @param aProjectID  long with the ID of the project to find all spectra for.
     * @param aConn Connection to read the spectrum File from.
     * @param aAdditionalWhereClause  String with the additional whereclause that
     *                                will be affixed right after '... AND '(can be 'null').
     * @return  Spectrumfile[] with the spectra.
     * @exception   SQLException    when the retrieval did not succeed.
     */
    public static Spectrumfile[] getAllSpectraForProject(long aProjectID, Connection aConn, String aAdditionalWhereClause) throws SQLException {
        Spectrumfile[] result = null;
        ArrayList temp = new ArrayList();
        String query = getBasicSelect() + " where l_projectid = ?";
        if(aAdditionalWhereClause != null) {
            query += " AND " + aAdditionalWhereClause;
        }
        PreparedStatement ps = aConn.prepareStatement(query);
        ps.setLong(1, aProjectID);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            temp.add(new Spectrumfile(rs));
        }
        rs.close();
        ps.close();

        // Transform arraylist into array.
        result = new Spectrumfile[temp.size()];
        temp.toArray(result);

        return result;
    }

    /**
     * This method will find the spectrumfileid, filename and identification count for all
     * spectrum files that link to the specified project from the connection provided.
     *
     * @param aProjectID  long with the ID of the project to find all spectra for.
     * @param aConn Connection to read the spectrum data from.
     * @return  Object[][] with the spectrumfileid for the spectra in [x][0],
     *                     the String filenames for the spectra in [x][1], and
     *                     the Integer for identified in [x][2].
     * @exception   SQLException    when the retrieval did not succeed.
     */
    public static Object[][] getFilenameAndIdentifiedStatusForAllSpectraForProject(long aProjectID, Connection aConn) throws SQLException {
        Object[][] result = null;
        ArrayList temp = new ArrayList();
        String query = "select spectrumfileid, filename, identified from spectrumfile where l_projectid = ?";

        PreparedStatement ps = aConn.prepareStatement(query);
        ps.setLong(1, aProjectID);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            temp.add(new Object[]{new Long(rs.getLong(1)), rs.getString(2), new Integer(rs.getInt(3))});
        }
        rs.close();
        ps.close();

        // Transform arraylist into array.
        result = new Object[temp.size()][];
        temp.toArray(result);

        return result;
    }

    /**
     * This method returns the basic 'select' statement that includes all columns in the correct sequence
     * in the ResultSet so that the constructor using a ResultSet can be safely called.
     *
     * @return  String with the basic select ('select x, y, z, ... from [table]') which can be extended
     *                  with eg., a where-clause.
     */
    public static String getBasicSelect() {
        return "select spectrumfileid, l_lcrunid, l_projectid, l_instrumentid, searched, identified, file, filename, total_spectrum_intensity, highest_peak_in_spectrum, username, creationdate, modificationdate from spectrumfile";
    }

    /**
     * This method increases the 'searched' flag with '1' for all the spectrumfiles whose names
     * are listed.
     *
     * @param aFilenames    String[] with the filenames of the spectrumfiles to update.
     */
    public static void addOneToSearchedFlag(String[] aFilenames, Connection aConn) throws SQLException {
        // The Prepped stat.
        Statement stat = aConn.createStatement();
        String query = "update spectrumfile set searched = searched+1, modificationdate=CURRENT_TIMESTAMP where filename in ";
        StringBuffer sb = new StringBuffer("(");
        for(int i = 0; i < aFilenames.length; i++) {
            String lFilename = aFilenames[i];
            if(i != 0) {
                sb.append(",");
            }
            sb.append("'" + lFilename + "'");
        }
        sb.append(")");
        query += sb.toString();
        int resultUpdate = stat.executeUpdate(query);
        stat.close();
        if(resultUpdate != aFilenames.length) {
            throw new SQLException("Error while updating SpectrumFile table for filenames.\nNumber of updated rows was " + resultUpdate + " instead of the expected " + aFilenames.length + "!");
        }
    }

    /**
     * This method loads and zips the file data.
     *
     * @param   aFile  File with the data.
     * @return  byte[]  with the GZIPped data.
     * @exception   IOException whenever the GZIPping process fails.
     */
    private byte[] zipFile(File aFile) throws IOException {
        byte[] bytes = null;

        // Read it, and write the bytes to a GZIPped outputstream.
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(aFile));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        int reading = -1;
        while((reading = bis.read()) != -1) {
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
