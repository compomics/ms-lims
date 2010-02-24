/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 28-mrt-03
 * Time: 9:39:18
 */
package com.compomics.mslims.util.fileio.mergefiles;

import com.compomics.mslims.util.fileio.interfaces.MergeFileReader;
import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.io.PropertiesManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/10/12 11:54:56 $
 */

/**
 * This class gets a MergeFileReader, given a File.
 *
 * @author Lennart Martens.
 */
public class MergeFileReaderFactory {

    /**
     * The constant holding the filename for the properties file from the available MergeFileReaders will be read.
     */
    private static final String PROPERTIES_FILE = "mergefilereaders.properties";

    /**
     * The constant String used to separate the properties key from the classname in the HashMap key.
     */
    private static final String SEPARATOR = "$$-*-$$";

    /**
     * This HashMap will contain all the available mergefilereaders. Mappings it will contain are (key, value):
     * ('properties_key + SEPARATOR + classname', testing_instance_of_class).
     */
    private static final HashMap iAvailableMergeFileReaders = new HashMap(15);

    /*
     * We need to find out which readers are available.
     * These should be stored in a properties file ("mergefilereaders.properties")
     * and we'll read them here.
     * If it fails, flag a serious error!
     */

    static {
        Properties props = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS, PROPERTIES_FILE);

        // Cycle all properties.
        Enumeration lEnum = props.keys();
        while (lEnum.hasMoreElements()) {
            // Get the key.
            String key = (String) lEnum.nextElement();
            // Get the classname for this key.
            String classname = props.getProperty(key);
            try {
                // See if we can find the specified class.
                Class cl = Class.forName(classname);
                try {
                    // Try to create a new instance using the default constructor.
                    Object o = cl.newInstance();
                    // Now check to see if it is a MergeFileReader.
                    if (!(o instanceof MergeFileReader)) {
                        System.err.println("\n\nThe '" + classname + "' class is not an implementation of MergeFileReader and will therefore be ignored!\n\n");
                    } else {
                        // Add it to the HashMap.
                        iAvailableMergeFileReaders.put(key + SEPARATOR + classname, o);
                    }
                } catch (InstantiationException e) {
                    System.err.println("\n\nCould not create instance of class '" + classname + "' using the default constructor. Is it abstract or an interface?\n\n");
                } catch (IllegalAccessException e) {
                    System.err.println("\n\nCould not create instance of class '" + classname + "' using the default constructor. Does it have public default (no-argument) constructor?\n\n");
                }
            } catch (ClassNotFoundException cnfe) {
                System.err.println("\n\nUnable to load class '" + classname + "'. This MergeFileReader will not be available!\n\n");
            }
        }
    }

    /**
     * This method finds the indicated file and returns a suitable MergeFileReader implementation.
     *
     * @param aFilename String with the filename.
     * @return MergeFileReader with the MergeFileReader for the file.
     * @throws java.io.IOException when the file was either not found, could not be opened or had an unrecognized
     *                             format.
     */
    public static MergeFileReader getReaderForMergeFile(String aFilename) throws IOException {
        return MergeFileReaderFactory.getReaderForMergeFile(new File(aFilename));
    }

    /**
     * This method parses the indicated file and returns a suitable MergeFileReader implementation.
     *
     * @param aFile File with the pointer to the mergefile.
     * @return MergeFileReader with the MergeFileReader for the file.
     * @throws java.io.IOException when the file was either not found, could not be opened or had an unrecognized
     *                             format.
     */
    public static MergeFileReader getReaderForMergeFile(File aFile) throws IOException {
        MergeFileReader mfr = null;

        // Check if the file exists.
        if (aFile.exists()) {
            // Iterate through all the available MergeFileReaders.
            Iterator iter = iAvailableMergeFileReaders.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                MergeFileReader reader = (MergeFileReader) iAvailableMergeFileReaders.get(key);
                if (reader.canRead(aFile)) {
                    // Okay, our work here is almost done.
                    // We need to create a new instance of this reader, however
                    // and initialize it.
                    String classname = key.substring(key.indexOf(SEPARATOR) + SEPARATOR.length());
                    try {
                        Object o = Class.forName(classname).newInstance();
                        mfr = (MergeFileReader) o;
                        mfr.load(aFile);
                    } catch (InstantiationException e) {
                        throw new IOException("Unable to create new instance of '" + classname + "'! Apparently it is either abstract or an interface.");
                    } catch (IllegalAccessException e) {
                        throw new IOException("Unable to create new instance of '" + classname + "'! No public default (no-arguments) constructor found!");
                    } catch (ClassNotFoundException e) {
                        throw new IOException("Unable to create new instance of '" + classname + "'! Class was not found!");
                    } catch (ClassCastException cce) {
                        throw new IOException("Unable to cast new instance of '" + classname + "' to a MergeFileReader!");
                    }
                }
            }
        } else {
            throw new IOException("File '" + aFile + "' was not found!");
        }
        // If there was no hit earlier, we can't read it.
        if (mfr == null) {
            throw new IOException("Unrecognized merge file type for file '" + aFile.getCanonicalPath() + "'!");
        }
        // Return the reader.
        return mfr;
    }
}
