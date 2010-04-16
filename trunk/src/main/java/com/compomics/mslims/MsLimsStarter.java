package com.compomics.mslims;

import com.compomics.util.io.StartBrowser;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.io.PropertiesManager;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyKrupp;


import java.io.*;
import java.util.Enumeration;
import java.util.Properties;


import javax.swing.*;


/**
 * A wrapper class used to start the jar file with parameters. The parameters are read from the JavaOptions file in the
 * Properties folder.
 *
 * @author Kenny Helsens
 */
public class MsLimsStarter {
    // Class specific log4j logger for MsLimsStarter instances.
    private static Logger logger = Logger.getLogger(MsLimsStarter.class);

    private boolean debug = false;


    /**
     * Starts the launcher by calling the launch method. Use this as the main class in the jar file.
     */
    public MsLimsStarter() {
        try {
            PlasticLookAndFeel.setPlasticTheme(new SkyKrupp());
            UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            // ignore exception
        }

        try {
            launch();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Launches the jar file with parameters to the jvm.
     *
     * @throws java.lang.Exception
     */
    private void launch() throws Exception {
        // Since we are launching mslims via the starter, mslims is in use for the end user.
        // As such, we want to redirect the log file to the compomics application folder.
        PropertiesManager.getInstance().updateLog4jConfiguration(CompomicsTools.MSLIMS);

        logger.debug("starting ms-lims");
        logger.debug(System.getProperties().getProperty("os.name"));

        // get the version number set in the pom file
        Properties lProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "ms-lims.properties");
        File lApplicationFolder = PropertiesManager.getInstance().getApplicationFolder(CompomicsTools.MSLIMS);

        /**
         * The name of the omssa parser jar file. Must be equal to the name
         * given in the pom file.
         */
        String jarFileName = "ms-lims-" + lProperties.get("version") + ".jar";

        // Get the jarFile path.
        String path;
        path = this.getClass().getResource("MsLimsStarter.class").getPath();
        logger.debug(path);
        path = path.substring(5, path.indexOf(jarFileName));
        logger.debug(path);
        path = path.replace("%20", " ");
        logger.debug(path);

        // Get Java vm options.
        String options = lProperties.get("java").toString();


        String quote = "";
        if (System.getProperty("os.name").lastIndexOf("Windows") != -1) {
            quote = "\"";
        }

        String javaHome = System.getProperty("java.home") + File.separator +
                "bin" + File.separator;

        String cmdLine = javaHome + "java " + options + " -cp " + quote
                + new File(path, jarFileName).getAbsolutePath()
                + quote + " com.compomics.mslims.gui.MS_LIMS";

        if (debug) {
            logger.info(cmdLine);
        }

        try {
            Process p = Runtime.getRuntime().exec(cmdLine);
            int exitVal = p.waitFor();

            String lMessage =
                    "Failed to exit ms-lims appropriately.\n\n" +
                            "For more details see: ms-lims.log\n\n"
                            + "If the error persists, please report the issue at http://ms-lims.googlecode.com/issues/" +
                            "\n\t-describe latest action" +
                            "\n\t-attach screenshot" +
                            "\n\t-attach ms-lims.log file";
            if (exitVal != 0) {

                logger.error(lMessage);
                int lResult = javax.swing.JOptionPane.showOptionDialog(null,
                        lMessage,
                        "ms-lims: unexpected failure",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        UIManager.getIcon("OptionPane.errorIcon"),
                        new Object[]{"Report issue", "Exit"},
                        "Report issue");

                if (lResult == JOptionPane.OK_OPTION) {
                    String lIssuesPage = new String("http://code.google.com/p/ms-lims/issues/list");
                    StartBrowser.start(lIssuesPage);
                }

            }
        } catch (IOException e1) {
            logger.error(e1.getMessage(), e1);
            logger.error(e1.getMessage(), e1);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            logger.error(t.getMessage(), t);
        }
        finally {
            logger.info("Exiting ms-lims");
            System.exit(0);
        }
    }

    /**
     * Starts the launcher by calling the launch method. Use this as the main class in the jar file.
     *
     * @param args
     */
    public static void main(String[] args) {
        new MsLimsStarter();
    }
}
