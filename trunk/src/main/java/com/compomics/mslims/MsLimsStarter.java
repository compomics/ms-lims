package com.compomics.mslims;

import com.compomics.util.io.StartBrowser;

import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.io.PropertiesManager;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyKrupp;


import java.io.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;


import javax.swing.*;


/**
 * A wrapper class used to start the jar file with parameters.
 * The parameters are read from the JavaOptions file in the
 * Properties folder.
 *
 * After launching MS_LIMS, this process stops immediately.
 *
 * @author Kenny Helsens
 */
public class MsLimsStarter {


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
            System.err.println(e.getMessage());
        }
    }

    /**
     * Launches the jar file with parameters to the jvm.
     *
     * @throws java.lang.Exception
     */
    private void launch() throws Exception {

        // get the version number set in the pom file
        Properties lProperties = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, "ms-lims.properties");

        /**
         * The name of the ms-lims parser jar file. Must be equal to the name
         * given in the pom file.
         */
        String jarFileName = "ms-lims-" + lProperties.get("version") + ".jar";

        // Get the jarFile path.
        String path;
        path = this.getClass().getResource("MsLimsStarter.class").getPath();
        //logger.debug(path);
        path = path.substring(5, path.indexOf(jarFileName));
        //logger.debug(path);
        path = path.replace("%20", " ");
        //logger.debug(path);

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

        System.out.println(cmdLine);

        if (debug) {
            //logger.info(cmdLine);
        }

        try {
            // Run the process!
            Runtime.getRuntime().exec(cmdLine);

        } catch (IOException e1) {
            System.err.println(e1.getMessage());
            e1.printStackTrace();
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            t.printStackTrace();
        }

        finally {
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
