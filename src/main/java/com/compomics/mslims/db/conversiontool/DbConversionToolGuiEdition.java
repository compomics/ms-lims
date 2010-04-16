package com.compomics.mslims.db.conversiontool;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;
import com.compomics.mslims.db.conversiontool.implementations.SQLDBConverterStepImpl;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Iterator;
import java.sql.Driver;
import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA. User: niklaas Date: 06-Jul-2009 Time: 08:46:18
 */
public class DbConversionToolGuiEdition {
    // Class specific log4j logger for DbConversionToolGuiEdition instances.
    private static Logger logger = Logger.getLogger(DbConversionToolGuiEdition.class);
    /**
     * The connection to ms_lims
     */
    private Connection iConn;
    /**
     * The cdf file
     */
    private File iCdfFile;
    /**
     * ArrayList with the instructions from the cdf file
     */
    private ArrayList iInstructions;


    public DbConversionToolGuiEdition(Connection aConn, File aCdfFile) {
        //set the database connection and the cdf file
        this.iConn = aConn;
        this.iCdfFile = aCdfFile;
        // Try to load the instruction set.
        iInstructions = new ArrayList();
        //read the file
        try {
            BufferedReader br = new BufferedReader(new FileReader(iCdfFile));
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
                iInstructions.add(step);
            }
            br.close();
        } catch (IOException ioe) {
            logger.info("Unable to read database conversion definition file instructions: " + ioe.getMessage());
        }

    }


    /**
     * This method gives the number of instructions read in the .cdf file
     *
     * @return int with the number of instructions
     */
    public int getNumberOfInstructions() {
        return iInstructions.size();
    }


    /**
     * This method will execute the instructions in the cdf file
     */
    public void doInstructions() {

        try {
            // Alright. Start the processing.
            int counter = 0;
            for (Iterator lIterator = iInstructions.iterator(); lIterator.hasNext();) {
                counter++;
                DBConverterStep step = (DBConverterStep) lIterator.next();
                logger.info("\n   + Step " + counter + "...");
                // Timing.
                long startTime = System.currentTimeMillis();
                // Perform step.
                boolean error = step.performConversionStep(iConn);
                // Timing again.
                long endTime = System.currentTimeMillis();
                // Transform time into seconds (with decimals).
                long longDelta = endTime - startTime;
                double doubleDelta = ((double) longDelta) / 1000.0;
                BigDecimal bdDelta = new BigDecimal(doubleDelta).setScale(3, BigDecimal.ROUND_HALF_UP);
                if (!error) {
                    logger.info("     OK!");
                    logger.info(" (step took " + bdDelta.toString() + " seconds)");
                } else {
                    logger.info("     ... FAILED!");
                    logger.info(" (step took " + bdDelta.toString() + " seconds)");
                    // Skip further steps!!
                    logger.info("(skipping next " + (iInstructions.size() - counter) + " update steps)");
                    break;
                }
            }
            logger.info("\nAll done.");
        } catch (Exception e) {
            logger.error("\n\nUnable to complete conversion: " + e.getMessage());
            logger.error(e.getMessage(), e);
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
                logger.info("The class '" + aFullyQualifiedClassname + "' is not an implementation of DBConverterStep!");
            }
            result = (DBConverterStep) temp;
        } catch (ClassNotFoundException cnfe) {
            logger.info("Unable to find the programmatic conversion step class you specified ('" + aFullyQualifiedClassname + "')!");
        } catch (InstantiationException ie) {
            logger.info("Unable to initialize the programmatic conversion step class you specified ('" + aFullyQualifiedClassname + "')!");
        } catch (IllegalAccessException iae) {
            logger.info("Unable to initialize the programmatic conversion step class you specified ('" + aFullyQualifiedClassname + "')!");
        }
        return result;
    }

}
