/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 14-jun-2004
 * Time: 11:02:00
 */
package com.compomics.mslims.util.diff;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Identification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.12 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class will read the differential data for all the spectra in the presented CSV file and store this data in tha
 * database.
 *
 * @author Lennart Martens
 * @version $Id: StoreDifferentialData.java,v 1.12 2009/03/11 13:57:45 niklaas Exp $
 */
public class StoreDifferentialData {
    // Class specific log4j logger for StoreDifferentialData instances.
    private static Logger logger = Logger.getLogger(StoreDifferentialData.class);

    /**
     * The Vector with the DiffCouples.
     */
    private Vector iCouples = null;

    /**
     * The user for the DB connection.
     */
    private static String iUser = null;

    /**
     * The password for the database user.
     */
    private static String iPwd = null;

    /**
     * The database server and name, e.g.: '//muppet03/projects'.
     */
    private static String iDBName = null;


    /**
     * This constructor creates an instance of this class that uses the specified Vector of DiffCouple instances as the
     * input data to store in the DB.
     *
     * @param aCouples Vector with the DiffCouple instances to store in the DB.
     */
    public StoreDifferentialData(Vector aCouples) {
        this.iCouples = aCouples;
    }

    /**
     * This method stores the differential data in the database.
     */
    public void storeDifferentialData() throws SQLException {

        int liSize = iCouples.size();
        Driver driver = null;
        try {
            driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            throw new SQLException("Unable to load driver class!");
        }
        Properties props = new Properties();
        props.put("user", iUser);
        props.put("password", iPwd);
        Connection conn = driver.connect("jdbc:mysql:" + iDBName, props);
        // Cycle all couples.
        for (int i = 0; i < liSize; i++) {
            DiffCouple dc = (DiffCouple) iCouples.get(i);
            String filename = dc.getFilename();
            Identification id = Identification.getIdentification(conn, filename);
            if (id == null) {
                logger.error("No identification found for filename='" + filename + "'!");
            } else {
                id.setLight_isotope(new Double(dc.getLightIntensity()));
                id.setHeavy_isotope(new Double(dc.getHeavyIntensity()));
                id.update(conn);
            }
        }
        conn.close();
    }

    /**
     * The main method is the entry point in the application.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0 || args.length != 4) {
            printUsage();
        }
        // Set the username & password.
        iUser = args[0];
        iPwd = args[1];
        iDBName = args[2];

        // Okay, let's attempt to read the input file.
        try {
            File file = new File(args[3]);
            if (!file.exists()) {
                throw new IOException("File '" + args[3] + "' could not be found!");
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            int lineCount = 0;
            HashMap allCouples = new HashMap();
            while ((line = br.readLine()) != null) {
                lineCount++;
                line = line.trim();
                // Check fo header line.
                if (line.toLowerCase().startsWith("filename;")) {
                    // Header line found; skip it.
                    continue;
                } else {
                    // Parse this line.
                    try {
                        DiffCouple dc = parseCouple(line);
                        Object removed = allCouples.put(dc.getFilename(), dc);
                        if (removed != null) {
                            // Duplicate entry found.
                            // Take the average.
                            DiffCouple old = (DiffCouple) removed;
                            dc.setLightIntensity((dc.getLightIntensity() + old.getLightIntensity()) / 2);
                            dc.setHeavyIntensity((dc.getHeavyIntensity() + old.getHeavyIntensity()) / 2);
                            logger.error("Averaged for spectrum " + old.getFilename());
                        }
                    } catch (Exception e) {
                        throw new IOException("Unable to parse line nbr. " + lineCount + ": " + e.getMessage());
                    }
                }
            }
            br.close();
            // At this point, we've gathered all data. Start processing it.
            StoreDifferentialData lDa = new StoreDifferentialData(new Vector(allCouples.values()));
            lDa.storeDifferentialData();
            logger.info("\n\nStored differential data for " + allCouples.size() + " spectra in the projects database.");
        } catch (IOException ioe) {
            logger.error("\n\nUnable to parse input file '" + args[3] + "'!" + ioe.getMessage() + "\n");
            System.exit(1);
        } catch (SQLException sqle) {
            logger.error("\n\nUnable to store differential data: " + sqle.getMessage() + "\n");
            System.exit(1);
        }
    }

    /**
     * This method prints class usage information to stderr and exits with error flag raised.
     */
    private static void printUsage() {
        logger.error("\n\nUsage\n\tStoreDifferentialData <db_username> <db_password> <db_URL (e.g.: //muppet03/projects)> <input_csv_file>\n");
        logger.error("\tRemarks:\n\n\t - CSV file format:\n\n\t   <first_line=header>\n\t   Filename;light isotope;heavy isotope");
        System.exit(1);
    }

    /**
     * this method parses an InnerCouple from a line of the CSV file.
     *
     * @param aLine String with the line to parse.
     * @return InnerCouple representing the data in the specified line.
     */
    private static DiffCouple parseCouple(String aLine) {
        DiffCouple dc = null;
        /*
        StringTokenizer st = new StringTokenizer(aLine, ";");
        String filename = st.nextToken();
        String lightValue = st.nextToken();
        String heavyValue = null;
        if(st.hasMoreTokens()) {
            heavyValue = st.nextToken();
        }*/
        int firstSemicolon = aLine.indexOf(";");
        int secondSemicolon = aLine.indexOf(";", firstSemicolon + 1);
        String filename = aLine.substring(0, firstSemicolon).trim();
        String lightValue = aLine.substring(firstSemicolon + 1, secondSemicolon).trim();
        String heavyValue = null;
        if (aLine.length() > secondSemicolon) {
            heavyValue = aLine.substring(secondSemicolon + 1).trim();
            while (heavyValue.endsWith(";")) {
                heavyValue = heavyValue.substring(0, heavyValue.length() - 1);
            }
        }

        if (lightValue == null) {
            lightValue = "";
        }
        if (heavyValue == null) {
            heavyValue = "";
        }
        // Depending on what's in the light and heavy values,
        // we need to process them differently.
        if (lightValue.toLowerCase().indexOf("single") >= 0) {
            lightValue = "1";
            heavyValue = "0";
        } else if (lightValue.toLowerCase().indexOf("c-term") >= 0) {
            lightValue = "-1";
            heavyValue = "0";
        } else if (lightValue.toLowerCase().indexOf("ee") >= 0) {
            lightValue = "-2";
            heavyValue = "0";
        } else if (heavyValue.toLowerCase().indexOf("single") >= 0) {
            lightValue = "0";
            heavyValue = "1";
        } else if (heavyValue.toLowerCase().indexOf("c-term") >= 0) {
            lightValue = "0";
            heavyValue = "-1";
        } else if (heavyValue.toLowerCase().indexOf("ee") >= 0) {
            lightValue = "0";
            heavyValue = "-2";
        }
        double light = Double.parseDouble(lightValue.trim());
        double heavy = Double.parseDouble(heavyValue.trim());
        // Skip rest.
        dc = new DiffCouple(filename, light, heavy);
        // Finis.
        return dc;
    }
}
