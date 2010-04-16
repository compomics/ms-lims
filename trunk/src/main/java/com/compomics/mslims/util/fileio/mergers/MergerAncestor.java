/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 27-jan-2004
 * Time: 11:32:09
 */
package com.compomics.mslims.util.fileio.mergers;

import org.apache.log4j.Logger;

import com.compomics.util.sun.SwingWorker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2005/02/23 08:58:41 $
 */

/**
 * This class groups some of the variables and constants typically used by Mergers.
 *
 * @author Lennart Martens
 * @version $Id: MergerAncestor.java,v 1.2 2005/02/23 08:58:41 lennart Exp $
 */
public abstract class MergerAncestor extends SwingWorker {
    /**
     * Parameter for the DB connection, if any.
     */
    protected String iDriver = null;
    /**
     * Parameter for the DB connection, if any.
     */
    protected String iUrl = null;
    /**
     * Other parameters for the DB connection.
     */
    protected Properties iConnProps = null;
    /**
     * Key for the DB driver.
     */
    public static final String DRIVER = "DRIVER";
    /**
     * Key for the DB URL.
     */
    public static final String URL = "URL";
    /**
     * Date & time formatter.
     */
    public static SimpleDateFormat iSDF = new SimpleDateFormat("ddMMyyyy_HHmmssSS");
    /**
     * Key for the total number of PKL files processed. This key is present in the HashMap with the stats, returned by
     * 'mergeAllFilesFromFolderToFolder.
     */
    public static final String TOTAL_NUMBER_OF_FILES = "TOTAL_NUMBER_OF_FILES";
    /**
     * Key for the total number of merge files produced. This key is present in the HashMap with the stats, returned by
     * 'mergeAllFilesFromFolderToFolder.
     */
    public static final String TOTAL_NUMBER_OF_MERGEFILES = "TOTAL_NUMBER_OF_MERGEFILES";

    /**
     * This method merges the Strings in the specified Vector to a mergefile in the specified output folder, where the
     * filename is: merge_[suffix].txt. <br /> Note that ALL the Strings in the Vector are merged, regardless of their
     * number.
     *
     * @param aStrings           Vector with the Strings to merge.
     * @param aDestinationFolder File with the outputfolder (this folder should exist!)
     * @param aSuffix            String with the name suffix for the resultant mergefile (filename: merge_[suffix].txt)
     * @throws java.io.IOException when the output of the mergefile failed.
     */
    public void mergeFilesFromString(Vector aStrings, File aDestinationFolder, String aSuffix) throws IOException {
        File output = new File(aDestinationFolder, "mergefile_" + aSuffix + ".txt");
        if (!output.exists()) {
            output.createNewFile();
        }
        BufferedWriter bos = new BufferedWriter(new FileWriter(output));
        int liSize = aStrings.size();
        for (int i = 0; i < liSize; i++) {
            bos.write((String) aStrings.get(i));
        }
        bos.flush();
        bos.close();
    }
}
