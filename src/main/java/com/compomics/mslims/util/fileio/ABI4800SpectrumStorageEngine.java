package com.compomics.mslims.util.fileio;

import com.compomics.mslims.db.accessors.Spectrum;
import com.compomics.mslims.db.accessors.Spectrum_file;
import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.interfaces.SpectrumStorageEngine;
import com.compomics.mslims.util.fileio.interfaces.MergeFileReader;
import com.compomics.mslims.util.fileio.mergefiles.MergeFileReaderFactory;
import com.compomics.mslims.util.workers.Load4800MGFWorker;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.util.interfaces.Flamable;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * Created by IntelliJ IDEA.
 * User: kenny
 * Date: 7-sep-2007
 * Time: 14:52:54
 */

/**
 * This class presents an implementation of the SpectrumStorageEngine that is designed to load and store ms/ms data from
 * an ABI 4800 mass spectrometer.
 * <p/>
 * This class loads all the MGF MS/MS spectra from a ABI 4800 - T2SMascot output specified folder. A given topfolder
 * contains subfolders for distinct lcruns. Directly in these folders resides an stacked .mgf file for that
 * lcrun(multiple spectra in a single file).
 *
 * @author Helsens Kenny 2007-09-05
 */


public class ABI4800SpectrumStorageEngine implements SpectrumStorageEngine {
    // Class specific log4j logger for ABI4800SpectrumStorageEngine instances.
    private static Logger logger = Logger.getLogger(ABI4800SpectrumStorageEngine.class);

    /**
     * This method takes care of loading all ms/ms data from the file system, while displaying a progressbar.
     * <b><i>Please note</i></b> that the 'aFoundLCRuns' Vector is a reference parameter that will contain the Lcrun
     * instances after completion of the method!
     *
     * @param aList         File[]  with the listing of the top-level directory to browse through.
     * @param aStoredLCRuns Vector    with the Lcrun instances that were retrieved from the database. When found on the
     *                      filesystem, these will not be included in the 'aNames' Vector with the results, since they
     *                      are already stored.
     * @param aFoundLCRuns  Vector    that will contain the new (not yet in DB) Lcrun instances found on the local
     *                      harddrive.
     * @param aParent       Flamable    with the parent that will do the error handling.
     * @param aProgress     DefaulProgressBar   to display the progress on.
     */
    public void findAllLCRunsFromFileSystem(File[] aList, Vector aStoredLCRuns, Vector aFoundLCRuns, Flamable aParent, DefaultProgressBar aProgress) {
        Load4800MGFWorker lmw = new Load4800MGFWorker(aList, aStoredLCRuns, aFoundLCRuns, aParent, aProgress);
        lmw.start();
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
        // Get a hanlde to the parent folder.
        File parent = new File(aLCRun.getPathname());

        // Get a handle on all the MGF files included in this LC run, using a MergeFileReader.
        MergeFileReader mfr = MergeFileReaderFactory.getReaderForMergeFile(parent);

        // Cycle through all the MGF files, storing each in the DB with the appropriate
        // links (L_LCRUNID and L_PROJECTID). The SEARCHED and IDENTIFIED flags default to '0'.
        // Filename and data are provided through the MascotGenericFile class, but note that we
        // convert the file contents from a String into a byte[] using the platforms default encoding.
        Vector spectra = mfr.getSpectrumFiles();
        int liSize = spectra.size();
        int counter = 0;
        for (int i = 0; i < liSize; i++) {
            MascotGenericFile lMascotGenericFile = (MascotGenericFile) spectra.elementAt(i);
            HashMap data = new HashMap(9);
            data.put(Spectrum.L_INSTRUMENTID, new Long(aInstrumentid));
            // The links.
            data.put(Spectrum.L_LCRUNID, new Long(aLCRun.getLcrunid()));
            data.put(Spectrum.L_PROJECTID, new Long(aProjectid));
            // The flags.
            data.put(Spectrum.IDENTIFIED, new Long(0));
            data.put(Spectrum.SEARCHED, new Long(0));
            // The filename.
            data.put(Spectrum.FILENAME, lMascotGenericFile.getFilename());
            // The total intensity.
            data.put(Spectrum.TOTAL_SPECTRUM_INTENSITY, lMascotGenericFile.getTotalIntensity());
            // The highest intensity.
            data.put(Spectrum.HIGHEST_PEAK_IN_SPECTRUM, lMascotGenericFile.getHighestIntensity());
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
            byte[] fileContents = lMascotGenericFile.toString().getBytes();
            // Set the byte[].
            lSpectrum_file.setUnzippedFile(fileContents);
            // Create the database object.
            lSpectrum_file.persist(aConn);

            counter++;
        }
        return counter;
    }
}
