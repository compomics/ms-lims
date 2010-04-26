/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 21-jun-03
 * Time: 17:21:13
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;


import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
 * CVS information:
 *
 * $Revision: 1.12 $
 * $Date: 2009/06/22 09:13:36 $
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
public class Spectrum extends SpectrumTableAccessor {
    // Class specific log4j logger for Spectrum instances.
    private static Logger logger = Logger.getLogger(Spectrum.class);


    /**
     * This constructor just maps the superclass constructor.
     *
     * @param aParams HashMap with the values to set.
     */
    public Spectrum(HashMap aParams) {
        super(aParams);
    }

    /**
     * This constructor reads the spectrum file from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Spectrum(ResultSet aRS) throws SQLException {
        super(aRS);
    }

    /**
     * Default constructor.
     */
    public Spectrum() {
        super();
    }

    /**
     * This method will find a spectrum file from the current connection, based on the filename.
     *
     * @param aFileName String with the filename of the spectrum file to find.
     * @param aConn     Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Spectrum findFromName(String aFileName, Connection aConn) throws SQLException {
        Spectrum temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where filename = ?");
        ps.setString(1, aFileName);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Spectrum(rs);
        }
        rs.close();
        ps.close();
        if (counter != 1) {
            throw new SQLException("Select based on spectrum name '" + aFileName + "' resulted in " + counter + " results!");
        }

        return temp;
    }


    /**
     * This method will find a spectrum file from the current connection, based on the specified spectrumid.
     *
     * @param aSpectrumID long with the spectrumid of the spectrum file to find.
     * @param aConn           Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Spectrum findFromID(long aSpectrumID, Connection aConn) throws SQLException {
        Spectrum temp = null;
        PreparedStatement ps = aConn.prepareStatement(getBasicSelect() + " where spectrumid = ?");
        ps.setLong(1, aSpectrumID);
        ResultSet rs = ps.executeQuery();
        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = new Spectrum(rs);
        }
        rs.close();
        ps.close();
        if (counter != 1) {
            SQLException sqe = new SQLException("Select based on spectrum ID '" + aSpectrumID + "' resulted in " + counter + " results instead of 1!");
            logger.error(sqe.getMessage(), sqe);
            throw sqe;
        }
        return temp;
    }


    /**
     * This method will find a spectrum file from the current connection, based on the filename.
     *
     * @param aFileName String with the filename of the sepctrum file to find.
     * @param aConn     Connection to read the spectrum File from.
     * @return Spectrumfile with the data.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Spectrum getFromName(String aFileName, Connection aConn) throws SQLException {
        return findFromName(aFileName, aConn);
    }

    /**
     * This method will find all spectrum files that link to the specified project from the current connection.
     *
     * @param aProjectID long with the ID of the project to find all spectra for.
     * @param aConn      Connection to read the spectrum File from.
     * @return Spectrumfile[] with the spectra.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Spectrum[] getAllSpectraForProject(long aProjectID, Connection aConn) throws SQLException {
        return getAllSpectraForProject(aProjectID, aConn, null);
    }

    /**
     * This method will find all spectrum files that link to the specified project under the specified conditions from
     * the current connection.
     *
     * @param aProjectID             long with the ID of the project to find all spectra for.
     * @param aConn                  Connection to read the spectrum File from.
     * @param aAdditionalWhereClause String with the additional whereclause that will be affixed right after '... AND
     *                               '(can be 'null').
     * @return Spectrumfile[] with the spectra.
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Spectrum[] getAllSpectraForProject(long aProjectID, Connection aConn, String aAdditionalWhereClause) throws SQLException {
        Spectrum[] result = null;
        ArrayList temp = new ArrayList();
        String query = getBasicSelect() + " where l_projectid = ?";
        if (aAdditionalWhereClause != null) {
            query += " AND " + aAdditionalWhereClause;
        }
        PreparedStatement ps = aConn.prepareStatement(query);
        ps.setLong(1, aProjectID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Spectrum(rs));
        }
        rs.close();
        ps.close();

        // Transform arraylist into array.
        result = new Spectrum[temp.size()];
        temp.toArray(result);

        return result;
    }

    /**
     * This method will find the spectrumid, filename and identification count for all spectrum files that link to the
     * specified project from the connection provided.
     *
     * @param aProjectID long with the ID of the project to find all spectra for.
     * @param aConn      Connection to read the spectrum data from.
     * @return Object[][] with the spectrumid for the spectra in [x][0], the String filenames for the spectra in [x][1],
     *         and the Integer for identified in [x][2].
     * @throws SQLException when the retrieval did not succeed.
     */
    public static Object[][] getFilenameAndIdentifiedStatusForAllSpectraForProject(long aProjectID, Connection aConn) throws SQLException {
        Object[][] result = null;
        ArrayList temp = new ArrayList();
        String query = "select spectrumid, filename, identified from spectrum where l_projectid = ?";

        PreparedStatement ps = aConn.prepareStatement(query);
        ps.setLong(1, aProjectID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
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
     * This method returns the basic 'select' statement that includes all columns in the correct sequence in the
     * ResultSet so that the constructor using a ResultSet can be safely called.
     *
     * @return String with the basic select ('select x, y, z, ... from [table]') which can be extended with eg., a
     *         where-clause.
     */
    public static String getBasicSelect() {
        return "select spectrumid, l_lcrunid, l_projectid, l_instrumentid, searched, identified, filename, total_spectrum_intensity, highest_peak_in_spectrum, username, creationdate, modificationdate from spectrum";
    }

    /**
     * This method increases the 'searched' flag with '1' for all the spectrumfiles whose names are listed.
     *
     * @param aFilenames String[] with the filenames of the spectrumfiles to update.
     */
    public static void addOneToSearchedFlag(String[] aFilenames, Connection aConn) throws SQLException {
        // The Prepped stat.
        Statement stat = aConn.createStatement();
        String query = "update spectrum set searched = searched+1, modificationdate=CURRENT_TIMESTAMP where filename in ";
        StringBuffer sb = new StringBuffer("(");
        for (int i = 0; i < aFilenames.length; i++) {
            String lFilename = aFilenames[i];
            if (i != 0) {
                sb.append(",");
            }
            sb.append("'" + lFilename + "'");
        }
        sb.append(")");
        query += sb.toString();
        int resultUpdate = stat.executeUpdate(query);
        stat.close();
        if (resultUpdate != aFilenames.length) {
            throw new SQLException("Error while updating Spectrum table for filenames.\nNumber of updated rows was " + resultUpdate + " instead of the expected " + aFilenames.length + "!");
        }
    }

}
