/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 12-feb-03
 * Time: 10:11:41
 */
package com.compomics.mslimscore.util.fileio.mergers;

import com.compomics.mslimsdb.accessors.Spectrum_file;
import org.apache.log4j.Logger;

import com.compomics.mslimsdb.accessors.Pklfiles;
import com.compomics.mslimsdb.accessors.Spectrum;
import com.compomics.util.general.CommandLineParser;
import com.compomics.util.io.FilenameExtensionFilter;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2005/02/23 08:58:41 $
 */

/**
 * This class can be used to merge pklfiles with the original PKL filename in the header and can store the original
 * files in the DB when desired.
 *
 * @author Lennart Martens
 */
public class PKLMergerAndStorer {
    // Class specific log4j logger for PKLMergerAndStorer instances.
    private static Logger logger = Logger.getLogger(PKLMergerAndStorer.class);

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
     * This boolean indicates whether the individual PKL files should be stored in the DB.
     */
    private boolean iStoreInDB = false;

    /**
     * This constructor creates a default merger that just performs the merge.
     */
    public PKLMergerAndStorer() {
        this(false, null);
    }

    /**
     * This constructor allows the storage of the individual PKL files in the DB before merging.
     *
     * @param aStoreInDB boolean to indicate whether or not to store the individual PKL files in the DB.
     * @param aDBParams  HashMap with the connection parameters for the DB but 'null' can be allowed when aStoreInDB is
     *                   'false'. Required parameters are DRIVER and URL, and all other parameters present in the
     *                   HashMap will be forwarded to the DB at connection time in the same key-value format they have
     *                   in the HashMap.
     */
    public PKLMergerAndStorer(boolean aStoreInDB, HashMap aDBParams) {
        this.iStoreInDB = aStoreInDB;
        // Get the parameters
        if (iStoreInDB) {
            if (aDBParams != null) {
                if (aDBParams.containsKey(URL) && aDBParams.containsKey(DRIVER)) {
                    Iterator iter = aDBParams.keySet().iterator();
                    this.iConnProps = new Properties();
                    while (iter.hasNext()) {
                        String key = (String) iter.next();
                        if (key.equals(DRIVER)) {
                            this.iDriver = (String) aDBParams.get(key);
                        } else if (key.equals(URL)) {
                            this.iUrl = (String) aDBParams.get(key);
                        } else {
                            iConnProps.put(key, aDBParams.get(key));
                        }
                    }
                } else {
                    throw new IllegalArgumentException("When specifying DB storage, the HashMap with parameters should at least contain DRIVER and URL fields!");
                }
            } else {
                throw new IllegalArgumentException("When specifying DB storage, the HashMap with parameters for the DB should be present!");
            }
        }
    }

    /**
     * This method merges the specified PKL files into a String. It adds a few blanks (three) between each spectrum and
     * adds the original filename to the first line (header), separated by a space.
     *
     * @param aInputFiles File[] with the PKL files to merge.
     * @return String  with the merged content
     * @throws java.io.IOException whenever the input files could not be read. SQLExceptions are mapped to IOException
     *                             in these!
     */
    public String mergeFilesToString(File[] aInputFiles) throws IOException {
        StringBuffer result = new StringBuffer();
        Connection lConn = null;

        // See if we should create a DB connection.
        if (iStoreInDB) {
            try {
                Driver lDriver = (Driver) Class.forName(iDriver).newInstance();
                lConn = lDriver.connect(iUrl, iConnProps);
            } catch (Exception e) {
                throw new IOException("Unable to connect to database: " + e.getMessage() + "!");
            }
        }

        // Cycle all files.
        for (int i = 0; i < aInputFiles.length; i++) {
            File lFile = aInputFiles[i];

            // DB storage (when necessary).
            if (iStoreInDB) {
                try {
                    HashMap params = new HashMap(2);
                    params.put(Pklfiles.FROMFILE, lFile.getCanonicalPath());
                    params.put(Pklfiles.IDENTIFIED, new Integer(0));
                    Pklfiles db = new Pklfiles(params);
                    db.persist(lConn);
                } catch (SQLException sqle) {
                    throw new IOException("Unable to store file '" + lFile.getName() + "' in db: " + sqle.getMessage() + "!");
                }
            }

            BufferedReader br = new BufferedReader(new FileReader(lFile));
            // Read the first line.
            String line = br.readLine();
            // Append a space and the filename
            line += " " + lFile.getName();
            result.append(line + "\n");
            while ((line = br.readLine()) != null) {
                result.append(line + "\n");
            }
            br.close();
            result.append("\n\n");
        }

        // Check for a live DB connection and terminate it, if necessary.
        if (lConn != null) {
            try {
                lConn.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return result.toString();
    }

    /**
     * This method will read the inputfiles from the DB connection and write the merged files to the specified
     * destination folder.
     *
     * @param aDestinationFolder File with the location for the mergefile.
     * @param aSize              int with the maximum number of PKL files per merged file.
     * @param aParams            Properties with DB connection parameters.
     * @param aWhereClause       String with the 'where' part for the query that will be launched. Can be 'null' in
     *                           which case no 'where' part will be added to the query.
     * @return HashMap with some output stats for the curious caller.
     * @throws java.lang.Exception whenever the output files could not be written (IOException), database problems occur
     *                             (SQLException) or the DB driver cannot be found (various).
     */
    public static HashMap mergeFilesFromDBConnectionToFile(File aDestinationFolder, int aSize, Properties aParams, String aWhereClause) throws Exception {
        // the stats and container thereof.
        HashMap stats = new HashMap(3);
        int total = 0;
        int needed = 0;

        StringBuffer query = new StringBuffer("select pklfileid, filename, searched, identified, l_projectid, creationdate, file, l_caplcid from pklfile");
        if (aWhereClause != null) {
            query.append(" " + aWhereClause.trim());
        }
        query.append(" order by creationdate");
        Driver d = (Driver) Class.forName(aParams.getProperty(PKLMergerAndStorer.DRIVER)).newInstance();
        Connection lConn = d.connect(aParams.getProperty(PKLMergerAndStorer.URL), aParams);
        PreparedStatement ps = lConn.prepareStatement(query.toString());
        ResultSet rs = ps.executeQuery();
        Vector tempVec = new Vector(aSize);
        while (rs.next()) {
            Spectrum pkl = new Spectrum(rs);
            Spectrum_file lSpectrum_file = Spectrum_file.findFromID(pkl.getSpectrumid(), lConn);
            StringBuffer tempSB = new StringBuffer(new String(lSpectrum_file.getUnzippedFile()));
            int location = tempSB.indexOf("\n");
            tempSB.insert(location - 1, " " + pkl.getFilename());
            tempVec.add(tempSB.toString());
            total++;
            if (total % aSize == 0) {
                needed++;
                String suffix = iSDF.format(new java.util.Date());
                mergeFilesFromString(tempVec, aDestinationFolder, suffix);
                tempVec = new Vector(aSize);
            }
        }
        if (tempVec.size() > 0) {
            needed++;
            String suffix = iSDF.format(new java.util.Date());
            mergeFilesFromString(tempVec, aDestinationFolder, suffix);
        }
        rs.close();
        ps.close();

        stats.put(TOTAL_NUMBER_OF_FILES, new Integer(total));
        stats.put(TOTAL_NUMBER_OF_MERGEFILES, new Integer(needed));
        return stats;
    }

    /**
     * This method merges the specified PKL files into a new physical file. It adds a few blanks (three) between each
     * spectrum and adds the original filename to the first line (header), separated by a space.
     *
     * @param aInputFiles File[] with the PKL files to merge.
     * @param aFilename   String with the desired filename for the merged outputfile.
     * @return File  with the pointer to the physical file with the merged content
     * @throws java.io.IOException whenever the input files could not be read, or the output file could not be written.
     *                             SQLExceptions are mapped to IOException in these!
     */
    public File mergeFilesToFile(File[] aInputFiles, String aFilename) throws IOException {
        String merge = this.mergeFilesToString(aInputFiles);
        File output = new File(aFilename);
        output.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write(merge + "\n");
        bw.flush();
        bw.close();
        return output;
    }

    /**
     * This method allows the caller to specify an input folder and an output folder as well as a size. The program will
     * then take all files currently in the folder and merge them.
     *
     * @param aSourceFolder      File with the location of the source folder (must point to a directory!)
     * @param aDestinationFolder File with the location of the destination folder (must point to a directory!)
     * @param aSize              int with the maximum number of PKL files per merge file.
     * @param aRecursive         boolean to indicate whether the sourcefolder should be searched recursively.
     * @return HashMap with some output stats for the curious caller.
     * @throws java.io.IOException when the merging was not possible.
     */
    public HashMap mergeAllFilesFromFolderToFolder(File aSourceFolder, File aDestinationFolder, int aSize, boolean aRecursive) throws IOException {
        return this.mergeAllFilesFromFolderToFolder(aSourceFolder, aDestinationFolder, aSize, null, aRecursive);
    }

    /**
     * This method allows the caller to specify an input folder and an output folder as well as a size. The program will
     * then take all files currently in the folder (taking into account a possible file filter) and merge them.
     *
     * @param aSourceFolder      File with the location of the source folder (must point to a directory!)
     * @param aDestinationFolder File with the location of the destination folder (must point to a directory!)
     * @param aSize              int with the maximum number of PKL files per merge file.
     * @param aFileFilter        String with the file extension filter (must be '*.ext'!!) can be 'null'.
     * @param aRecursive         boolean to indicate whether the sourcefolder should be searched recursively.
     * @return HashMap with some output stats for the curious caller.
     * @throws java.io.IOException when the merging was not possible.
     */
    public HashMap mergeAllFilesFromFolderToFolder(File aSourceFolder, File aDestinationFolder, int aSize, String aFileFilter, boolean aRecursive) throws IOException {
        // Stat collector.
        HashMap stats = new HashMap(3);

        // Array to store all the (filtered) files.
        File[] all = null;

        all = this.getFileList(aSourceFolder, aFileFilter, aRecursive);


        Vector allClean = new Vector(all.length);
        for (int i = 0; i < all.length; i++) {
            File lFile = all[i];
            if (!lFile.isDirectory()) {
                allClean.add(lFile);
            }
        }
        int total = allClean.size();
        stats.put(TOTAL_NUMBER_OF_FILES, new Integer(total));
        // See how many merge files we'll need.
        int needed = total / aSize;
        int rest = total % aSize;
        if (rest > 0) {
            needed++;
        }
        stats.put(TOTAL_NUMBER_OF_MERGEFILES, new Integer(needed));
        // Generate the needed merge files.
        for (int i = 0; i < needed; i++) {
            String currentTime = Long.toString(System.currentTimeMillis());
            File output = new File(aDestinationFolder, "merge" + currentTime + ".txt");
            int arraysize = aSize;
            if (((i + 1) == needed) && (rest > 0)) {
                arraysize = rest;
            }
            File[] sendFiles = new File[arraysize];
            for (int j = 0; j < sendFiles.length; j++) {
                int index = j + (i * aSize);
                sendFiles[j] = (File) allClean.get(index);
            }
            this.mergeFilesToFile(sendFiles, output.getCanonicalPath());
        }
        // Done.
        return stats;
    }

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
    public static void mergeFilesFromString(Vector aStrings, File aDestinationFolder, String aSuffix) throws IOException {
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

    /**
     * Main method for this class.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            printError("Usage:\n\tPKLMergerAndStorer [-r] [-s --user <DB_username> --password <DB_password> --url <DB_url> --driver <DB_driver>] [--extension <file_extension>] --count <number_of_pkls_per_merge> --output <output_folder> <input_folder>");
        }
        CommandLineParser clp = new CommandLineParser(args, new String[]{"user", "password", "url", "driver", "count", "output", "extension"});
        String[] tempArray = clp.getFlags();
        String dbDriver = null;
        String dbUrl = null;
        String dbUser = null;
        String dbPassword = null;
        boolean recursive = false;

        // See if we should store stuff in the db.
        if (tempArray != null && tempArray.length >= 1) {
            if (tempArray[0].trim().equals("s") || (tempArray.length == 2 && tempArray[1].trim().equals("s"))) {
                dbDriver = clp.getOptionParameter("driver");
                dbUrl = clp.getOptionParameter("url");
                dbUser = clp.getOptionParameter("user");
                dbPassword = clp.getOptionParameter("password");
                if (dbDriver == null || dbUrl == null) {
                    printError("Both database driver AND database URL are required for a connection!\n\nUsage:\n\tPKLMergerAndStorer [-r] [-s --user <DB_username> --password <DB_password> --url <DB_url> --driver <DB_driver>] [--extension <file_extension>] --count <number_of_pkls_per_merge> --output <output_folder> <input_folder>");
                }
            }
            if (tempArray[0].trim().equals("r") || (tempArray.length == 2 && tempArray[1].trim().equals("r"))) {
                recursive = true;
            }
        }

        String countString = clp.getOptionParameter("count");
        String outString = clp.getOptionParameter("output");
        String extension = clp.getOptionParameter("extension");
        tempArray = clp.getParameters();
        if (tempArray == null || tempArray.length != 1) {
            printError("You need to specify a single input folder!\n\nUsage:\n\tPKLMergerAndStorer [-r] [-s --user <DB_username> --password <DB_password> --url <DB_url> --driver <DB_driver>] [--extension <file_extension>] --count <number_of_pkls_per_merge> --output <output_folder> <input_folder>");
        }
        String inputString = tempArray[0];

        if (outString == null) {
            printError("You need to specify a single output folder!\n\nUsage:\n\tPKLMergerAndStorer [-r] [-s --user <DB_username> --password <DB_password> --url <DB_url> --driver <DB_driver>] [--extension <file_extension>] --count <number_of_pkls_per_merge> --output <output_folder> <input_folder>");
        }
        if (countString == null) {
            printError("You need to specify a maximum number of PKLFiles to merge per mergefile!\n\nUsage:\n\tPKLMergerAndStorer [-r] [-s --user <DB_username> --password <DB_password> --url <DB_url> --driver <DB_driver>] [--extension <file_extension>] --count <number_of_pkls_per_merge> --output <output_folder> <input_folder>");
        }

        // We've got everything! See if the files are correct and the number is correctly formed.
        try {
            File outFile = new File(outString);
            File inFile = new File(inputString);

            if (!outFile.exists() || !outFile.isDirectory()) {
                printError("The output folder does not exist or is not a directory!");
            }
            if (!inFile.exists() || !inFile.isDirectory()) {
                printError("The input folder does not exist or is not a directory!");
            }
            int count = -1;
            try {
                count = Integer.parseInt(countString);
                if (count <= 0) {
                    throw new NumberFormatException("Is less than or equal to zero, yet should be non-zero positive!");
                }
            } catch (Exception e) {
                printError("The count specified is NOT a positive whole number, greater than zero!");
            }

            // Okay, everything checks out until now!
            // Let's split for a second, depending on whether we want to store in db or not.
            PKLMergerAndStorer merger = null;
            if (dbDriver != null) {
                HashMap params = new HashMap(4);
                params.put(DRIVER, dbDriver);
                params.put(URL, dbUrl);
                if (dbUser != null) {
                    params.put("user", dbUser);
                }
                if (dbPassword != null) {
                    params.put("password", dbPassword);
                }
                merger = new PKLMergerAndStorer(true, params);
            } else {
                merger = new PKLMergerAndStorer();
            }

            // Start the merge itself!
            merger.mergeAllFilesFromFolderToFolder(inFile, outFile, count, extension, recursive);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            printError("Unable to complete merging: \n\n" + e.getMessage());
        }
    }


    /**
     * This method prints the specified message to stderr and then exits the JVM, setting the status flag to '1'.
     *
     * @param aMessage String with the error message to write to stdout.
     */
    private static void printError(String aMessage) {
        logger.error("\n\n" + aMessage + "\n\n");
        System.exit(1);
    }

    /**
     * This method will find all files in the specified folder, corresponding to the specified filename filter (if any)
     * and the search can be recursive.
     *
     * @param aSourceFolder File with the sourcefolder.
     * @param aFileFilter   String with the filename filter to apply (can be 'null' for no filter).
     * @param aRecursive    boolean to indicate whether searching should be recursive.
     * @return File[]  with the complete filelist.
     */
    private File[] getFileList(File aSourceFolder, String aFileFilter, boolean aRecursive) {
        File[] all = null;
        // Possible filenamefilter.
        if (aRecursive) {
            // Recursion required.
            // This takes some extra effort.
            Vector files = new Vector(300, 100);
            // Get all the files in the source folder.
            this.readFolderRecursively(files, aSourceFolder, aFileFilter);

            all = new File[files.size()];
            files.toArray(all);
        } else {
            // No recursion. Just list this folder.
            if (aFileFilter != null) {
                // Get the filtered files in the 'in' folder.
                all = aSourceFolder.listFiles(new FilenameExtensionFilter(aFileFilter));
            } else {
                // Get all the files in the 'in' folder.
                all = aSourceFolder.listFiles();
            }
        }

        return all;
    }

    /**
     * This method will allow recursive 'mining' of a folder for (filtered) files.
     *
     * @param aStorage      Vector to hold the complete list of files. Found files are added to this Vector.
     * @param aSourceFolder File with the source folder.
     * @param aFileFilter   String with the filename filter to apply.
     */
    private void readFolderRecursively(Vector aStorage, File aSourceFolder, String aFileFilter) {
        File[] temp = aSourceFolder.listFiles();

        for (int i = 0; i < temp.length; i++) {
            File file = temp[i];
            if (file.isDirectory()) {
                readFolderRecursively(aStorage, file, aFileFilter);
            } else if (aFileFilter != null) {
                // We should apply a filter here.
                FilenameExtensionFilter fef = new FilenameExtensionFilter(aFileFilter);
                if (fef.accept(file.getParentFile(), file.getName())) {
                    aStorage.add(file);
                }
            } else {
                aStorage.add(file);
            }
        }
    }
}
