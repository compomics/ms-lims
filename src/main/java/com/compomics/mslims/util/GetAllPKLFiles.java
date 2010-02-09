/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 21-feb-03
 * Time: 12:35:44
 */
package com.compomics.mslims.util;

import com.compomics.mslims.db.accessors.Pklfiles;
import com.compomics.mslims.db.accessors.Spectrumfile;
import com.compomics.mslims.util.fileio.PKLFile;
import com.compomics.mslims.util.fileio.MascotGenericFile;
import com.compomics.util.general.CommandLineParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2008/08/07 14:13:16 $
 */

/**
 * This class retrieves all PKLFiles from the specified database and writes
 * them to the specified folder.
 *
 * @author Lennart Martens
 */
public class GetAllPKLFiles {

    private static final int ALL = 0;
    private static final int NOT_IDENTIFIED = 1;
    private static final int IDENTIFIED = 2;
    private static final int WHERE_QUERY = 3;

    public static void main(String[] args) {
        GetAllPKLFiles gap = new GetAllPKLFiles();
        if(args == null || args.length == 0) {
            System.err.println("\n\nUsage:\n\tGetAllPKLFiles -(a|n|i|q) [--where <where_clause_use_only_with_q_flag>] --user <username> --password <password> --driver <db_driver> --db <database> <destination_folder>\n\n");
            System.exit(1);
        }
        CommandLineParser clp = new CommandLineParser(args, new String[] {"user", "password", "driver", "db", "where"});

        String user = clp.getOptionParameter("user");
        String password = clp.getOptionParameter("password");
        String driver = clp.getOptionParameter("driver");
        String db = clp.getOptionParameter("db");

        String[] temp = clp.getParameters();
        if(temp == null || temp.length != 1) {
            System.err.println("\n\nYou must specify a destination folder!\n");
            System.err.println("\n\nUsage:\n\tGetAllPKLFiles -(a|n|i|q) [--where <where_clause_use_only_with_q_flag>] --user <username> --password <password> --driver <db_driver> --db <database> <destination_folder>\n\n");
            System.exit(1);
        }
        String output = temp[0];
        File lFile = new File(output);
        if(!lFile.exists() && !lFile.isDirectory()) {
            System.err.println("\n\nYou must specify an output folder that exists!\n\n");
            System.exit(1);
        }

        temp = clp.getFlags();
        if(temp == null || temp.length != 1) {
            System.err.println("\n\nPlease specify -a for all, -n for the not-identified and -i for the identified spectra.\n");
            System.err.println("\n\nUsage:\n\tGetAllPKLFiles -(a|n|i|q) [--where <where_clause_use_only_with_q_flag>] --user <username> --password <password> --driver <db_driver> --db <database> <destination_folder>\n\n");
            System.exit(1);
        }
        String modeString = temp[0].trim();
        String where = null;
        int mode = -1;
        if(modeString.equalsIgnoreCase("a")) {
            mode = ALL;
        } else if(modeString.equalsIgnoreCase("n")) {
            mode = NOT_IDENTIFIED;
        } else if(modeString.equalsIgnoreCase("i")) {
            mode = IDENTIFIED;
        } else if(modeString.equalsIgnoreCase("q")) {
            mode = WHERE_QUERY;
            where = clp.getOptionParameter("where");
            if(where == null || where.trim().equals("")) {
                System.err.println("\n\nPlease specify a 'where'-clause ('--where' paramater) when using the '-q' flag!\n");
                System.err.println("\n\nUsage:\n\tGetAllPKLFiles -(a|n|i|q) [--where <where_clause_use_only_with_q_flag>] --user <username> --password <password> --driver <db_driver> --db <database> <destination_folder>\n\n");
                System.exit(1);
            }
        }

        try {
            gap.putSpectraInFolder(user, password, driver, db, lFile.getCanonicalPath(), mode, where);
        } catch(IOException ioe) {
            System.err.println("\n\nError locating destination folder: " + ioe.getMessage() + "!\n\n");
            System.exit(1);
        }
    }

    /**
     * This method constructs and executes the SQL select statement that will
     * retrieve the desired spectra (all, identified or not identified) and output them
     * to the specified folder.
     *
     * @param   aUser   String with the user name for the DB connection.
     * @param   aPassword   String with the password for the DB connection.
     * @param   aDriverName    String with the database driver class name. 
     * @param   aUrl    String with the database name.
     * @param   aFolder String with the path to the output folder.
     * @param   aMode   int to indicate which PKLFiles need be retrieved
     *                  (all, identified, not identified).
     * @param   aWhereClause    String with the 'where'-clause (can be 'null', should be filled in when the
     *                          aMode == WHERE_QUERY).
     */
    public void putSpectraInFolder(String aUser, String aPassword, String aDriverName, String aUrl, String aFolder, int aMode, String aWhereClause) {
        Connection lConn = null;
        try {
            // Get a driver and a connection ot the DB.
            Driver d = (Driver)Class.forName(aDriverName).newInstance();
            Properties userProps = new Properties();
            userProps.put("user", aUser);
            userProps.put("password", aPassword);
            lConn = d.connect(aUrl, userProps);
            // Construct the query.
            StringBuffer query = new StringBuffer("select spectrumfileid from spectrumfile");
            if(aMode != ALL && aMode != WHERE_QUERY) {
                query.append(" where identified");
                query.append(((aMode==IDENTIFIED)?">":"=") + "0");
            } else if(aMode == WHERE_QUERY) {
                query.append(" " + aWhereClause);
            }

            // Retrieve the data form the db.
            Statement stat = lConn.createStatement();
            PreparedStatement ps = lConn.prepareStatement("select * from spectrumfile where spectrumfileid=?");
            ResultSet rs = stat.executeQuery(query.toString());
            File parentDir = new File(aFolder);
            int counter = 0;
            while(rs.next()) {
                // Get the actual spectrumfile data from the DB.
                ps.setLong(1, rs.getLong(1));
                ResultSet rsSpectrum = ps.executeQuery();
                rsSpectrum.next();
                Spectrumfile sf = new Spectrumfile(rsSpectrum);
                rsSpectrum.close();
                ps.clearParameters();

                // Write the files (slow step in the program).
                MascotGenericFile tempFile = new MascotGenericFile(sf.getFilename(), new String(sf.getUnzippedFile()));
                tempFile.writeToFile(parentDir);
                counter++;
            }
            // Close the resources.
            rs.close();
            ps.close();
            stat.close();
            // That'll be all, thank you.
            System.out.println("\n\nWrote " + counter + " spectrumfiles to folder '" + parentDir.getAbsolutePath() + "'.\n\nAll done!");
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(lConn != null) {
                try {
                    lConn.close();
                } catch(SQLException sqle) {
                }
            }
        }
    }
}
