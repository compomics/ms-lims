/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 28-feb-03
 * Time: 9:40:22
 */
package com.compomics.mslims.util;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.PKLFile;
import com.compomics.util.general.CommandLineParser;

import java.io.File;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class will allow the recalibration of PKL files based on a ppm error or an absolute (Da) error. Charge state is
 * implicitly taken care of.
 *
 * @author Lennart Martens.
 */
public class RecalibratePKLFiles {
    // Class specific log4j logger for RecalibratePKLFiles instances.
    private static Logger logger = Logger.getLogger(RecalibratePKLFiles.class);

    private static final int ABSOLUTE_ERROR = 0;
    private static final int PPM_ERROR = 1;

    /**
     * Main method that runs the class.
     *
     * @param args start-up arguments. Run program without any to see which apply.
     */
    public static void main(String[] args) {
        try {
            // Check for params.
            if (args == null || args.length == 0) {
                flagError("Usage:\n\tRecalibratePKLFiles (--ppm <error_in_ppm>|--abs <error_in_dalton>) <target_folder>");
            }

            // Parse params.
            CommandLineParser clp = new CommandLineParser(args, new String[]{"ppm", "abs"});
            // Check them.
            String[] tempParams = clp.getParameters();
            // Input folder.
            if (tempParams == null || tempParams.length != 1) {
                flagError("Usage:\n\tRecalibratePKLFiles (--ppm <error_in_ppm>|--abs <error_in_dalton>) <target_folder>");
            }
            File source = new File(tempParams[0]);
            if (!source.exists() || !source.isDirectory()) {
                flagError("Source must be a folder and it must exist!");
            }
            // abs or ppm.
            String type1 = clp.getOptionParameter("ppm");
            String type2 = clp.getOptionParameter("abs");
            if (type1 != null && type2 != null) {
                flagError("You must specify EITHER ppm OR absolute (abs) values!\nNot both!");
            }
            if (type1 == null && type2 == null) {
                flagError("You must specify an error in ppm OR in absolute value!");
            }
            String errorText = ((type1 == null) ? type2 : type1);
            double error = -1.0;
            try {
                error = Double.parseDouble(errorText);
            } catch (Exception e) {
                flagError("The error must be specified as a valid decimal number!");
            }
            int type = ((type1 == null) ? ABSOLUTE_ERROR : PPM_ERROR);

            // Now to read the list of files in the source dir.
            logger.info("Gathering file list to process...");
            File[] allPkls = source.listFiles();
            logger.info("Done.\n\nCycling all files, correcting with " + error + ((type == PPM_ERROR) ? " ppm" : " Da") + ", compensating for precursor charge...");
            int counter = 0;
            for (int i = 0; i < allPkls.length; i++) {
                if (allPkls[i].isDirectory()) {
                    continue;
                }
                PKLFile pkl = new PKLFile(allPkls[i]);
                double precursorMZ = pkl.getPrecursorMZ();
                int chargeState = pkl.getCharge();
                double tempError = error / chargeState;
                // Now calculate the new parent mass.
                if (type == ABSOLUTE_ERROR) {
                    precursorMZ += tempError;
                } else {
                    precursorMZ += ((precursorMZ * tempError) / 1000000);
                }
                pkl.setPrecursorMZ(precursorMZ);
                pkl.writeToFile(source, false);
                counter++;
            }
            logger.info("Done.\nCorrected " + counter + " PKL files.\n\n");

        } catch (Exception e) {
            flagError("Unexpected exception occurrred: " + e.getMessage() + "!");
        }
    }

    /**
     * This method outputs the specified Strign to stderr and then exits the JVM with a non-zero status.
     *
     * @param aMessage String with the error message to display.
     */
    private static void flagError(String aMessage) {
        logger.error("\n\n" + aMessage + "\n\n");
        System.exit(1);
    }
}
