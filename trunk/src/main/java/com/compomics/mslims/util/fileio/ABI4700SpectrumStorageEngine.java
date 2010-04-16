/**
 * Created by IntelliJ IDEA.
 * User: martlenn
 * Date: 04-May-2007
 * Time: 14:48:52
 */
package com.compomics.mslims.util.fileio;

import com.compomics.mslims.db.accessors.Spectrum;
import com.compomics.mslims.db.accessors.Spectrum_file;
import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.interfaces.SpectrumStorageEngine;
import com.compomics.mslims.util.workers.Load4700MGFWorker;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.util.interfaces.Flamable;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.SQLException;
/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2009/06/22 09:13:36 $
 */

/**
 * This class presents an implementation of the SpectrumStorageEngine that is designed to load and store ms/ms data from
 * an ABI 4700 mass spectrometer.
 *
 * @author Lennart Martens
 * @version $Id: ABI4700SpectrumStorageEngine.java,v 1.2 2009/06/22 09:13:36 lennart Exp $
 */
public class ABI4700SpectrumStorageEngine implements SpectrumStorageEngine {
    // Class specific log4j logger for ABI4700SpectrumStorageEngine instances.
    private static Logger logger = Logger.getLogger(ABI4700SpectrumStorageEngine.class);

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
        Load4700MGFWorker lmw = new Load4700MGFWorker(aList, aStoredLCRuns, aFoundLCRuns, aParent, aProgress);
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
        int counter = 0;

        // Get a hanlde to the parent folder.
        File parent = new File(aLCRun.getPathname());

        // Get a handle on all the peaklists in this LCRun.
        Vector spectra = new Vector(10, 5);
        this.browseRunRecursively(parent, spectra);
        if (spectra.size() != aLCRun.getFilecount()) {
            throw new IOException("Found only " + spectra.size() + " MS/MS MGF files in " + aLCRun.getPathname() + " instead of the expected " + aLCRun.getFilecount() + "!");
        }

        // Cycle through all the files, storing each in the DB with the appropriate
        // links (L_LCRUNID and L_PROJECTID). The SEARCHED and IDENTIFIED flags default to '0'.
        // Filename and data are provided through the MascotGenericFile class, but note that we
        // convert the file contents from a String into a byte[] using the platforms default encoding.
        int liSize = spectra.size();

        for (int i = 0; i < liSize; i++) {
            T2Extractor_MascotGenericFile lFile = (T2Extractor_MascotGenericFile) spectra.elementAt(i);
            HashMap data = new HashMap(9);
            data.put(Spectrum.L_INSTRUMENTID, new Long(aInstrumentid));
            // The links.
            data.put(Spectrum.L_LCRUNID, new Long(aLCRun.getLcrunid()));
            data.put(Spectrum.L_PROJECTID, new Long(aProjectid));
            // The flags.
            data.put(Spectrum.IDENTIFIED, new Long(0));
            data.put(Spectrum.SEARCHED, new Long(0));
            // The filename.
            data.put(Spectrum.FILENAME, lFile.getFilename());
            // The total intensity.
            data.put(Spectrum.TOTAL_SPECTRUM_INTENSITY, lFile.getTotalIntensity());
            // The highest intensity.
            data.put(Spectrum.HIGHEST_PEAK_IN_SPECTRUM, lFile.getHighestIntensity());
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
            byte[] fileContents = lFile.toString().getBytes();
            // Set the byte[].
            lSpectrum_file.setUnzippedFile(fileContents);
            // Create the database object.
            lSpectrum_file.persist(aConn);

            counter++;
        }
        return counter;
    }

    /**
     * This method finds all the MS/MS files for a certain run.
     *
     * @param aParent  File with the run top folder.
     * @param aSpectra Vector with the spectra found (reference parameter).
     * @throws java.io.IOException when the recursive browsing fails.
     */
    private void browseRunRecursively(File aParent, Vector aSpectra) throws IOException {
        File[] list = aParent.listFiles();
        for (int i = 0; i < list.length; i++) {
            File lFile = list[i];
            if (lFile.isDirectory()) {
                this.browseRunRecursively(lFile, aSpectra);
            } else if (lFile.getName().toUpperCase().indexOf("_MSMS_") > 0 && lFile.getName().toUpperCase().endsWith(".MGF")) {
                loadSpectrum(lFile, aSpectra);
            }
        }
    }

    /**
     * This method finds all the MGF spectra in a certain folder.
     *
     * @param aFile    File to load.
     * @param aSpectra Vector with the spectra found (reference parameter).
     * @throws java.io.IOException when the recursive browsing fails.
     */
    private void loadSpectrum(File aFile, Vector aSpectra) throws IOException {
        T2Extractor_MascotGenericFile t2_mgf = new T2Extractor_MascotGenericFile(aFile);
        t2_mgf.setFilename(aFile.getParentFile().getName() + "_" + t2_mgf.getFilename());
        aSpectra.add(t2_mgf);
    }
}
