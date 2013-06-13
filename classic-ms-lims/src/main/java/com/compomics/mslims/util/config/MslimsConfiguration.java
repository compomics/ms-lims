package com.compomics.mslims.util.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.awt.*;

/**
 * This class is a
 */
public class MslimsConfiguration {

    /**
     * Properties instance to manage the properties of the library.
     */
    private static PropertiesConfiguration input;

    /**
     * Static initiatlizer.
     */
    static {
        try {
            input = new PropertiesConfiguration("ms-lims.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }


    // Return the filename for the stockforest output directory.

    /**
     * Return Rectangle object for the PDF output dimensions.
     * @return
     */
    public static Rectangle getPDFOutputDimensions(){
        String[] lDimensionStrings = input.getStringArray("pdf.output.coordinates");
        int[] lDimensionInts = new int[4];

        for (int i = 0; i < lDimensionStrings.length; i++) {
            lDimensionInts[i] = Integer.parseInt(lDimensionStrings[i]);
        }

        return new Rectangle(lDimensionInts[0], lDimensionInts[1], lDimensionInts[2], lDimensionInts[3]);
    }

    /**
     * Return Rectangle object for the PDF output dimensions.
     * @return
     */
    public static double getPDFOutputMaxY(){
        return input.getDouble("pdf.output.max.y");
    }


}

