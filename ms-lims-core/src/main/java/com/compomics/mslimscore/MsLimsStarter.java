package com.compomics.mslimscore;

import com.compomics.software.CompomicsWrapper;
import java.io.*;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

/**
 * A wrapper class used to start the jar file with parameters. The parameters
 * are read from the JavaOptions file in the Properties folder.
 *
 * After launching MS_LIMS, this process stops immediately.
 *
 * @author Kenny Helsens
 */
public class MsLimsStarter extends CompomicsWrapper {

    private static final Logger logger = Logger.getLogger(MsLimsStarter.class);
    private static File iTempFolder;

    /**
     * Starts the launcher by calling the launch method. Use this as the main
     * class in the jar file.
     */
    public MsLimsStarter(String[] args) {
        while (1 == 1) {
            try {
                cleanOldFiles();
                JOptionPane.showConfirmDialog(null, "relaunch");
                launch(args);
            } catch (Exception e) {
                logger.error(e);
            }
        }


    }

    /**
     * Launches the jar file with parameters to the jvm.
     *
     * @throws java.lang.Exception
     */
    private void launch(String[] args) throws Exception {
        try {
            File jarFile = new File(MsLimsStarter.class
                    .getProtectionDomain().getCodeSource().getLocation().toURI());
            String mainClass = "com.compomics.mslimscore.gui.MS_LIMS";

            launchTool(
                    "Ms Lims", jarFile, null, mainClass, args);
        } catch (URISyntaxException ex) {
            logger.error(ex);
        }
    }

    /**
     * Starts the launcher by calling the launch method. Use this as the main
     * class in the jar file.
     *
     * @param args
     */
    public static void main(String[] args) {
        new MsLimsStarter(args);
    }

    private static File getTempDirectory() {
        try {
            File lTempFile = File.createTempFile("anchor", "tmp");
            iTempFolder = new File(lTempFile.getParent(), "mascotdatfile_raf");
            lTempFile.delete();
        } catch (IOException e) {
            logger.error(e);
        }
        return iTempFolder;
    }

    public static void cleanOldFiles() {
        File lTempDirectory = getTempDirectory();
        File[] lFiles = lTempDirectory.listFiles();
        for (File lFile : lFiles) {
            lFile.delete();
        }
    }
}
