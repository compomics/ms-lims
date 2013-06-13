/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 27-jan-2004
 * Time: 11:23:02
 */
package com.compomics.mslims.util.fileio.mergers;

import com.compomics.mslims.db.accessors.Spectrum_file;
import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Spectrum;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.fileio.MascotGenericFile;
import com.compomics.util.interfaces.Flamable;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2005/02/23 08:58:41 $
 */

/**
 * This class merges Mascot Generic Format files from the database. Note that information loss occurs due to replacing
 * the title with the filename.
 *
 * @author Lennart Martens
 * @version $Id: MGFMerger.java,v 1.5 2005/02/23 08:58:41 lennart Exp $
 */
public class MGFMerger extends MergerAncestor {
    // Class specific log4j logger for MGFMerger instances.
    private static Logger logger = Logger.getLogger(MGFMerger.class);

    /**
     * The flamable parent.
     */
    private Flamable iParent = null;

    /**
     * The progress bar. Can be 'null'.
     */
    private DefaultProgressBar iProgress = null;

    /**
     * The database connection.
     */
    private Connection iConn = null;

    /**
     * The destination folder.
     */
    private File iDestinationFolder = null;

    /**
     * The maximum size (in number of merged spectra) per mergefile.
     */
    private int iSize = 0;

    /**
     * The additional where clause.
     */
    private String iWhereClause = null;

    /**
     * The return values (passed in as a reference parameter).
     */
    private HashMap iResults = null;

    /**
     * This constructor will set up a graphical MGFMerger that will display progress..
     *
     * @param aParent            Flamable with the parent.
     * @param aProgress          DefaultProgressBar with the progress bar to display. Can be 'null' for no progressbar.
     * @param aConn              Connection with the database connection.
     * @param aDestinationFolder File with the location for the mergefile.
     * @param aSize              int with the maximum number of PKL files per merged file.
     * @param aWhereClause       String with the 'where' part for the query that will be launched. Can be 'null' in
     *                           which case no 'where' part will be added to the query.
     * @param aResults           HashMap that will contain the results after completion (some number data on operation
     *                           performed). <b>Please note</b> that this is a reference parameter.
     */
    public MGFMerger(Flamable aParent, DefaultProgressBar aProgress, Connection aConn, File aDestinationFolder, int aSize, String aWhereClause, HashMap aResults) throws Exception {
        iParent = aParent;
        this.iProgress = aProgress;
        this.iConn = aConn;
        this.iDestinationFolder = aDestinationFolder;
        this.iSize = aSize;
        this.iWhereClause = aWhereClause;
        this.iResults = aResults;
    }


    /**
     * Compute the value to be returned by the <code>get</code> method.
     */
    public Object construct() {
        return this.mergeFilesFromDBConnectionToFile();
    }

    /**
     * This method will read the inputfiles from the DB connection and write the merged files to the specified
     * destination folder.
     */
    private Object mergeFilesFromDBConnectionToFile() {
        // The stats and container thereof.
        int total = 0;
        int needed = 0;
        try {
            // Construct the query.
            StringBuffer query = new StringBuffer(Spectrum.getBasicSelect());
            if (iWhereClause != null) {
                query.append(" " + iWhereClause.trim());
            }
            query.append(" order by creationdate");
            PreparedStatement ps = iConn.prepareStatement(query.toString());
            ResultSet rs = ps.executeQuery();
            Vector tempVec = new Vector(iSize);
            while (rs.next()) {
                Spectrum mgf = new Spectrum(rs);
                Spectrum_file lSpectrum_file = Spectrum_file.findFromID(mgf.getSpectrumid(), iConn);
                if (iProgress != null) {
                    iProgress.setMessage("Merging spectrum '" + mgf.getFilename() + "'.");
                }
                MascotGenericFile file = new MascotGenericFile(mgf.getFilename(), new String(lSpectrum_file.getUnzippedFile()));
                // Note the use of the 'true' flag, which takes care of substituting the original title with the
                // filename!
                tempVec.add(file.toString(true) + "\n\n");
                total++;
                if (total % iSize == 0) {
                    needed++;
                    String suffix = iSDF.format(new java.util.Date());
                    if (iProgress != null) {
                        iProgress.setMessage("Writing 'mergefile_" + suffix + ".txt'.");
                    }
                    mergeFilesFromString(tempVec, iDestinationFolder, suffix);
                    tempVec = new Vector(iSize);
                }
                if (iProgress != null) {
                    iProgress.setValue(iProgress.getValue() + 1);
                }
            }
            if (tempVec.size() > 0) {
                needed++;
                String suffix = iSDF.format(new java.util.Date());
                mergeFilesFromString(tempVec, iDestinationFolder, suffix);
            }
            rs.close();
            ps.close();

            // Set the stats and return them.
            iResults.put(TOTAL_NUMBER_OF_FILES, new Integer(total));
            iResults.put(TOTAL_NUMBER_OF_MERGEFILES, new Integer(needed));
            if (iProgress != null) {
                iProgress.setValue(iProgress.getMaximum());
            }
        } catch (Exception e) {
            iParent.passHotPotato(e, "Unable to merge files: " + e.getMessage());
        }
        return "";
    }
}
