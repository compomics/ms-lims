/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 12-feb-2004
 * Time: 9:25:14
 */
package com.compomics.mslims.util.fileio;

import com.compomics.mslims.db.accessors.Spectrum;
import com.compomics.mslims.db.accessors.Spectrum_file;
import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.fileio.interfaces.SpectrumStorageEngine;
import com.compomics.mslims.util.workers.LoadCapLCWorker;
import com.compomics.util.interfaces.Flamable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2009/06/22 09:13:36 $
 */

/**
 * This class presents an implementation of the SpectrumStorageEngine that is designed to load and store CapLC LC runs
 * and corresponding PKL files from a Micromass Q-TOF mass spectrometer.
 *
 * @author Lennart Martens
 * @version $Id: QTOFSpectrumStorageEngine.java,v 1.5 2009/06/22 09:13:36 lennart Exp $
 */
public class QTOFSpectrumStorageEngine implements SpectrumStorageEngine {
    // Class specific log4j logger for QTOFSpectrumStorageEngine instances.
    private static Logger logger = Logger.getLogger(QTOFSpectrumStorageEngine.class);

    /**
     * This hash will contain the mapping of the LCRun to all the constituent files. Keys are Strings with the LCRun
     * name, values are Vectors of files.
     */
    private HashMap iCapLCtoFiles = null;

    /**
     * This method takes care of loading all the LC runs from the file system, while displaying a progressbar.
     * <b><i>Please note</i></b> that the 'aFoundLCRuns' Vector is a reference parameter that will contain the Lcrun
     * instances after completion of the method!
     *
     * @param aList         File[]  with the listing of the top-level directory to browse through.
     * @param aStoredLCRuns Vector    with the Lcrun instances that were retrieved from the database. When found on the
     *                      filesystem, these will not be included in the 'aNames' Vector with the results,s ince they
     *                      are already stored.
     * @param aFoundLCRuns  Vector    that will contain the new (not yet in DB) Lcrun instances found on the local
     *                      harddrive.
     * @param aParent       Flamable    with the paent that will do the error handling.
     * @param aProgress     DefaulProgressBar   to display the progress on.
     */
    public void findAllLCRunsFromFileSystem(File[] aList, Vector aStoredLCRuns, Vector aFoundLCRuns, Flamable aParent, DefaultProgressBar aProgress) {
        // The hashmap that will contain the mapping of all new LCRuns to their respective files.
        iCapLCtoFiles = new HashMap();
        LoadCapLCWorker lcw = new LoadCapLCWorker(aList, aStoredLCRuns, aFoundLCRuns, aParent, aProgress, iCapLCtoFiles);
        lcw.start();
        aProgress.setVisible(true);
    }

    /**
     * This method actually takes care of finding all the spectrumfiles for the indiciated LCRun and transforming these
     * into Spectrum instances for storage in the database over the specified connection.
     *
     * @param aLCRun        LCRun instances for which the spectrumfiles need to be found and stored.
     * @param aProjectid    long with the projectid to associate the Spectrumfiles with.
     * @param aInstrumentid long with the instrumentid to associate the spectrumfiles with.
     * @param aConn         Connection  on which to write the Spectrumfiles.
     * @return int with the number of spectra stored.
     * @throws java.io.IOException   when the filereading goes wrong.
     * @throws java.sql.SQLException when the DB storage goes wrong.
     */
    public int loadAndStoreSpectrumFiles(LCRun aLCRun, long aProjectid, long aInstrumentid, Connection aConn) throws IOException, SQLException {
        // Get the LCRun name.
        String lcName = aLCRun.getName();
        // Find the associated files.
        Vector pklFiles = (Vector) iCapLCtoFiles.get(lcName);
        // Cycle them and process them.
        int counter = 0;
        for (int i = 0; i < pklFiles.size(); i++) {
            File lFile = (File) pklFiles.get(i);
            // Extra check to not store folders by mistake.
            long lCaplcID = aLCRun.getLcrunid();
            String filename = lFile.getName();
            // Load the PKL file.
            PKLFile pklFile = new PKLFile(lFile);
            // Fill out relevant parameters.
            HashMap data = new HashMap(9);
            // The filename has to change extension from 'pkl' to 'mgf'.
            String tempFilename = filename.substring(0, filename.lastIndexOf(".")) + ".mgf";
            data.put(Spectrum.FILENAME, tempFilename);
            // The total intensity.
            data.put(Spectrum.TOTAL_SPECTRUM_INTENSITY, pklFile.getTotalIntensity());
            // The highest intensity.
            data.put(Spectrum.HIGHEST_PEAK_IN_SPECTRUM, pklFile.getHighestIntensity());
            data.put(Spectrum.IDENTIFIED, new Long(0));
            data.put(Spectrum.L_LCRUNID, new Long(lCaplcID));
            data.put(Spectrum.L_PROJECTID, new Long(aProjectid));
            data.put(Spectrum.L_INSTRUMENTID, new Long(aInstrumentid));
            data.put(Spectrum.SEARCHED, new Long(0));
            // The charge - as long for the database accessor.
            Long lCharge = new Long(pklFile.getCharge());
            data.put(Spectrum.CHARGE, lCharge);
                        // The precursorMZ.
            data.put(Spectrum.MASS_TO_CHARGE, pklFile.getPrecursorMZ());


            // Create the database object.
            Spectrum lSpectrum = new Spectrum(data);
            lSpectrum.persist(aConn);

            // Get the spectrumid from the generated keys.
            Long lSpectrumfileID = (Long) lSpectrum.getGeneratedKeys()[0];
            // Create the Spectrum_file instance.
            Spectrum_file lSpectrum_file = new Spectrum_file();
            // Set spectrumid
            lSpectrum_file.setL_spectrumid(lSpectrumfileID);
            // Set the filecontent
            // Read the contents for the file into a byte[].
            byte[] fileContents = pklFile.getMGFContents().getBytes();
            // Set the byte[].
            lSpectrum_file.setUnzippedFile(fileContents);
            // Create the database object.
            lSpectrum_file.persist(aConn);


            counter++;
        }

        return counter;
    }
}
