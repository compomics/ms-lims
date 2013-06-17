package com.compomics.mslimscore.util.fileio;

import com.compomics.mslimsdb.accessors.Fragmentation;
import com.compomics.mslimsdb.accessors.LCRun;
import com.compomics.mslimsdb.accessors.Spectrum;
import com.compomics.mslimsdb.accessors.Spectrum_file;
import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mslimscore.util.fileio.filters.MascotGenericFileFilter;
import com.compomics.mslimscore.util.fileio.interfaces.SpectrumStorageEngine;
import com.compomics.mslimscore.util.fileio.mergefiles.MascotGenericMergeFileReader;
import com.compomics.mslimscore.util.workers.LoadMicroTOFQWorker;
import com.compomics.util.interfaces.Flamable;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: kennyhelsens
 * Date: Oct 25, 2010
 * Time: 12:56:04 PM
 */
public class MicroTOFQStorageEngine implements SpectrumStorageEngine {
// Class specific log4j logger for UltraflexSpectrumStorageEngine instances.
    private static Logger logger = Logger.getLogger(MicroTOFQStorageEngine.class);

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
        LoadMicroTOFQWorker lmtofqw = new LoadMicroTOFQWorker(aList, aStoredLCRuns, aFoundLCRuns, aParent, aProgress);
        lmtofqw.start();
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
    public int loadAndStoreSpectrumFiles(LCRun aLCRun, long aProjectid, long aInstrumentid, Connection aConn, Fragmentation aFragmentation) throws IOException, SQLException {
        int counter = 0;

        // Get a hanlde to the parent folder.
        File parent = new File(aLCRun.getPathname());

        // Filter for child .mgf files.
        FileFilter lMGFFilter = new MascotGenericFileFilter();
        File[] lMGFFiles = parent.listFiles(lMGFFilter);
        if (lMGFFiles.length > 0) {
            int lCounter = 0;
            for (int j = 0; j < lMGFFiles.length; j++) {
                MascotGenericMergeFileReader lMascotGenericMergeFileReader = new MascotGenericMergeFileReader(lMGFFiles[j]);
                Vector lSpectrumFiles = lMascotGenericMergeFileReader.getSpectrumFiles();
                // Cycle through all the files, storing each in the DB with the appropriate
                // links (L_LCRUNID and L_PROJECTID). The SEARCHED and IDENTIFIED flags default to '0'.
                // Filename and data are provided through the MascotGenericFile class, but note that we
                // convert the file contents from a String into a byte[] using the platforms default encoding.

                for (int i = 0; i < lSpectrumFiles.size(); i++) {
                    MascotGenericFile lFile = (MascotGenericFile) lSpectrumFiles.elementAt(i);

                    HashMap data = new HashMap(9);
                    data.put(Spectrum.L_INSTRUMENTID, new Long(aInstrumentid));
                    // The links.
                    data.put(Spectrum.L_LCRUNID, new Long(aLCRun.getLcrunid()));
                    data.put(Spectrum.L_PROJECTID, new Long(aProjectid));
                    data.put(Spectrum.L_FRAGMENTATIONID, aFragmentation.getFragmentationid());
                    // The flags.
                    data.put(Spectrum.IDENTIFIED, new Long(0));
                    data.put(Spectrum.SEARCHED, new Long(0));
                    // The filename.
                    data.put(Spectrum.FILENAME, lFile.getFilename());
                    // The total intensity.
                    data.put(Spectrum.TOTAL_SPECTRUM_INTENSITY, lFile.getTotalIntensity());
                    // The highest intensity.
                    data.put(Spectrum.HIGHEST_PEAK_IN_SPECTRUM, lFile.getHighestIntensity());
                    // The charge - as long for the database accessor.
                    Long lCharge = new Long(lFile.getCharge());
                    data.put(Spectrum.CHARGE, lCharge);
                    // The precursorMZ.
                    data.put(Spectrum.MASS_TO_CHARGE, lFile.getPrecursorMZ());

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

            }
        }
        return counter;
    }
}
