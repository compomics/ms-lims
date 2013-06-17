/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 2-jan-2006
 * Time: 13:58:38
 */
package com.compomics.mslimscore.util.conversiontool;

import com.compomics.mslimscore.util.conversiontool.implementations.SQLDBConverterStepImpl;
import com.compomics.mslimscore.util.conversiontool.interfaces.DBConverterStep;
import org.apache.log4j.Logger;


import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.sql.Driver;
import java.sql.Connection;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Iterator;
import java.math.BigDecimal;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class
 *
 * @author Lennart
 * @version $Id: DBConversionTool.java,v 1.3 2009/03/11 13:57:45 niklaas Exp $
 */
public class DBConversionTool {
    // Class specific log4j logger for DBConversionTool instances.
    private static Logger logger = org.apache.log4j.Logger.getLogger(DBConversionTool.class);

    /**
     * The main method is the entry point for the application.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        // Check start-up arguments.
        if (args == null || args.length != 5) {
            printUsage();
        }
        // Check the input file.
        File conversionFile = new File(args[4]);
        if (!conversionFile.exists()) {
            printError("The database conversion definition file you specified ('" + args[4] + "') does not exist!");
        }
        // Try to load the instruction set.
        ArrayList instructions = new ArrayList();
        logger.info("\n\nReading conversion instructions from '" + args[4] + "'...");
        try {
            BufferedReader br = new BufferedReader(new FileReader(conversionFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Skip empty lines and comments.
                if (line.startsWith("#") || line.equals("")) {
                    continue;
                }
                // Okay, so we have a converter step. Can be SQL or programmatic.
                // If we find a programmatic step, immediately check whether we can find the class,
                // instantiate it and whether it is really an implementation of the DBConverterStep
                // interface. If it is an SQL command, create a step for that.
                DBConverterStep step = null;
                if (line.startsWith("!")) {
                    line = line.substring(1);
                    step = loadClass(line);
                } else {
                    // SQL step.
                    step = new SQLDBConverterStepImpl(line);
                }
                instructions.add(step);
            }
            br.close();
        } catch (IOException ioe) {
            printError("Unable to read database conversion definition file instructions: " + ioe.getMessage());
        }
        logger.info("Done. Read " + instructions.size() + " conversion steps.");
        logger.info("\nLoading DB driver (" + args[0] + ")...");
        Driver driver = null;
        // Try to load the driver.
        try {
            driver = (Driver) Class.forName(args[0]).newInstance();
        } catch (ClassNotFoundException cnfe) {
            printError("Unable to find the driver class you specified ('" + args[0] + "')!");
        } catch (InstantiationException ie) {
            printError("Unable to initialize the driver class you specified ('" + args[0] + "')!");
        } catch (IllegalAccessException iae) {
            printError("Unable to initialize the driver class you specified ('" + args[0] + "')!");
        }
        logger.info("Done.");
        logger.info("\nConnecting to DB (" + args[1] + ") as " + args[2] + "...");
        Connection conn = null;
        // Try to connect to the DB.
        try {
            Properties props = new Properties();
            props.put("user", args[2]);
            props.put("username", args[2]);
            props.put("password", args[3]);
            conn = driver.connect(args[1], props);
            logger.info("Done.");
            logger.info("\nStarting database processing...");
            // Alright. Start the processing.
            int counter = 0;
            for (Iterator lIterator = instructions.iterator(); lIterator.hasNext();) {
                counter++;
                DBConverterStep step = (DBConverterStep) lIterator.next();
                logger.info("\n   + Step " + counter + "...");
                // Timing.
                long startTime = System.currentTimeMillis();
                // Perform step.
                boolean error = step.performConversionStep(conn);
                // Timing again.
                long endTime = System.currentTimeMillis();
                // Transform time into seconds (with decimals).
                long longDelta = endTime - startTime;
                double doubleDelta = ((double) longDelta) / 1000.0;
                BigDecimal bdDelta = new BigDecimal(doubleDelta).setScale(3, BigDecimal.ROUND_HALF_UP);
                if (!error) {
                    logger.info("     OK!");
                } else {
                    logger.info("     ... FAILED!");
                    // Skip further steps!!
                    logger.info("(cancelling the remaining " + (instructions.size() - counter) + " update steps)");
                    break;
                }
                logger.info(" (step took " + bdDelta.toString() + " seconds)");
            }
            logger.info("\nAll done.");
        } catch (SQLException sqle) {
            logger.error("\n\nUnable to complete conversion: " + sqle.getMessage());
            logger.error(sqle.getMessage(), sqle);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                logger.info("\n\nClosed database connection.\n\n");
            } catch (SQLException sqle) {
                logger.error("\n\nUnable to close database connection!\n\n");
            }
        }
    }

    /**
     * This method attempts to load the specified class from the current classpath and instantiate it using the default
     * constructor. It then checks whether this object is an instance of 'DBConverterStep'. If there is any problem, it
     * will print an error message and exit the JVM with the error flag raised.
     *
     * @param aFullyQualifiedClassname String with the fully qualified classname of the class to load.
     * @return DBConverterStep with the instance of the class.
     */
    private static DBConverterStep loadClass(String aFullyQualifiedClassname) {
        DBConverterStep result = null;
        try {
            Object temp = Class.forName(aFullyQualifiedClassname).newInstance();
            if (!(temp instanceof DBConverterStep)) {
                printError("The class '" + aFullyQualifiedClassname + "' is not an implementation of DBConverterStep!");
            }
            result = (DBConverterStep) temp;
        } catch (ClassNotFoundException cnfe) {
            printError("Unable to find the programmatic conversion step class you specified ('" + aFullyQualifiedClassname + "')!");
        } catch (InstantiationException ie) {
            printError("Unable to initialize the programmatic conversion step class you specified ('" + aFullyQualifiedClassname + "')!");
        } catch (IllegalAccessException iae) {
            printError("Unable to initialize the programmatic conversion step class you specified ('" + aFullyQualifiedClassname + "')!");
        }
        return result;
    }

    /**
     * This method prints the usage information for this class to the standard error stream and exits with the error
     * flag raised to '1'.
     */
    private static void printUsage() {
        printError("Usage:\n\n\tDBConversionTool <db_driver> <db_url> <db_user> <db_password> <conversion_definition_file>");
    }

    /**
     * This method prints two blank lines followed by the the specified error message and another two empty lines to the
     * standard error stream and exits with the error flag raised to '1'.
     *
     * @param aMsg String with the message to print.
     */
    private static void printError(String aMsg) {
        logger.error("\n\n" + aMsg + "\n\n");
        System.exit(1);
    }
}
