/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 15-jun-02
 * Time: 14:38:38
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslims.db.utils;

import com.compomics.mslims.db.accessors.Spectrumfile;
import com.compomics.mslims.util.fileio.PKLFile;
import com.compomics.util.general.CommandLineParser;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class can be adapted to migrate databases to next releases.
 */
public class DBTransferTool {

    /**
     * The String with the DB driver.
     */
    private String iDBDriver = null;
    /**
     * The String with the DB URL.
     */
    private String iDBUrl = null;

    /**
     * Properties for the DB connection (eg., user, password).
     */
    private Properties iDBProps = null;

    /**
     * Constant, defining the key in the properties for the database driver.
     */
    public static final String DRIVER = "DRIVER";
    /**
     * Constant, defining the key in the properties for the database URL.
     */
    public static final String URL = "URL";
    /**
     * Constant, defining the key in the properties for the database user.
     */
    public static final String USER = "user";
    /**
     * Constant, defining the key in the properties for the database password.
     */
    public static final String PASSWORD = "password";

    /**
     * Constructor that takes the necessary properties for the DB connection
     * as a parameter.
     *
     * @param aPropFile Properties instance with the connection properties.
     */
    public DBTransferTool(Properties aPropFile) {
        iDBDriver = aPropFile.getProperty(DRIVER);
        iDBUrl = aPropFile.getProperty(URL);
        iDBProps = aPropFile;
    }

    /**
     * This method does the real work.
     */
    public void startConversion() {
        try {
            Driver driver = (Driver)Class.forName(iDBDriver).newInstance();
            Connection lConn = driver.connect(iDBUrl, iDBProps);
            // --------------------------------------------------------
            //      YOUR CODE CAN START HERE
            // --------------------------------------------------------

            // We need to convert existing PKL files into MGF files,
            // change their filenames and change the links to these filenames
            // in the identifications table.
            // The latter is easy enough, since we should be able to do the
            // substitution in a single query.

            // First the PKL file content and filename.
            // We need to select all of them first.
            String query = "select spectrumfileid, filename, searched, identified, l_projectid, creationdate, file, l_lcrunid, l_instrumentid from spectrumfile";
            Statement stat = lConn.createStatement();
            ResultSet rs = stat.executeQuery(query);
            int specFileCounter = 0;
            System.out.print("\n\nUpdating pklfiles to MGF files...");
            while(rs.next()) {
                specFileCounter++;
                // Print a dot every 50 records.
                if(specFileCounter%50 == 0) {
                    System.out.print(".");
                }

                // Create a Spectrumfile.
                Spectrumfile specFile = new Spectrumfile(rs);
                // Get filename and file content.
                String filename = specFile.getFilename();
                String content = new String(specFile.getUnzippedFile());
                // Create a PKLFile.
                PKLFile pkl = new PKLFile(filename, content);
                String newContent = pkl.getMGFContents();
                // Change the filename.
                String newFilename = filename.substring(0, filename.lastIndexOf(".pkl")) + ".mgf";
                // Replace the appropriate fields on the Spectrumfile.
                specFile.setFilename(newFilename);
                specFile.setUnzippedFile(newContent.getBytes());
                specFile.update(lConn);
            }
            rs.close();
            System.out.println("...Done!");

            // Now to run the statement to update all filenames in the identification table.
            System.out.println("\nUpdating filenames in the 'Identification' table...");
            query = "update identification set filename=concat(substring(filename, 1, (length(filename)-4)),'.mgf')";
            int idUpdateCounter = stat.executeUpdate(query);
            System.out.println("Update complete!");

            System.out.println("\n\nUpdated " + specFileCounter + " PKL files to MGF format and new filenames, and");
            System.out.println("updated " + idUpdateCounter + " linked identifications with the new filenames.");

            System.out.println("\n\nJob's done!\n\n");
            stat.close();
            // --------------------------------------------------------
            //      END YOUR CODE HERE
            // --------------------------------------------------------
            if(lConn != null) {
                lConn.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method is the entry point for the application.
     *
     * @param args  String[] with the start-up args.
     */
    public static void main(String[] args) {
        if(args == null || args.length == 0) {
            printUsage();
        }

        CommandLineParser clp = new CommandLineParser(args, new String[]{"user", "password"});
        String user = clp.getOptionParameter("user");
        String password = clp.getOptionParameter("password");
        if(clp.getParameters().length != 2) {
            printUsage();
        }
        String driver = clp.getParameters()[0];
        String url = clp.getParameters()[1];

        Properties props = new Properties();
        props.put(DRIVER, driver);
        props.put(URL, url);
        if(user != null) {
            props.put(USER, user);
            props.put(PASSWORD, password);
        }

        DBTransferTool dbtt = new DBTransferTool(props);
        dbtt.startConversion();
    }

    /**
     * This method prints usage information to the stderr stream and exits the JVM with the status flag raised.
     */
    private static void printUsage() {
        System.err.println("\n\nUsage:\n\tDBTransferTool --user <db_username> --password <db_password> <db_driver> <db_url>\n\n");
        System.exit(1);
    }
}
