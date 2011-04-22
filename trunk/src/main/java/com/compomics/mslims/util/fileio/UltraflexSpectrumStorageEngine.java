/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 12-feb-2004
 * Time: 9:33:29
 */
package com.compomics.mslims.util.fileio;

import com.compomics.mslims.db.accessors.Fragmentation;
import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.mslims.db.accessors.Spectrum;
import com.compomics.mslims.db.accessors.Spectrum_file;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.fileio.interfaces.SpectrumStorageEngine;
import com.compomics.mslims.util.workers.LoadUltraflexXMLWorker;
import com.compomics.util.interfaces.Flamable;
import org.apache.log4j.Logger;

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
 * This class presents an implementation of the SpectrumStorageEngine that is designed to load and store ms/ms data from
 * a Bruker Ultraflex mass spectrometer.
 *
 * @author Lennart Martens
 * @version $Id: UltraflexSpectrumStorageEngine.java,v 1.5 2009/06/22 09:13:36 lennart Exp $
 */
public class UltraflexSpectrumStorageEngine implements SpectrumStorageEngine {
    // Class specific log4j logger for UltraflexSpectrumStorageEngine instances.
    private static Logger logger = Logger.getLogger(UltraflexSpectrumStorageEngine.class);

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
        LoadUltraflexXMLWorker lmw = new LoadUltraflexXMLWorker(aList, aStoredLCRuns, aFoundLCRuns, aParent, aProgress);
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
    public int loadAndStoreSpectrumFiles(LCRun aLCRun, long aProjectid, long aInstrumentid, Connection aConn, Fragmentation aFragmentation) throws IOException, SQLException {
        int counter = 0;

        // Get a hanlde to the parent folder.
        File parent = new File(aLCRun.getPathname());
        String prefix = aLCRun.getName();

        // Get a handle on all the peaklists in this LCRun.
        Vector spectra = new Vector(10, 5);
        HashMap foundOnes = new HashMap();
        this.browseRunRecursively(parent, spectra, prefix, foundOnes);

        // Cycle through all the files, storing each in the DB with the appropriate
        // links (L_LCRUNID and L_PROJECTID). The SEARCHED and IDENTIFIED flags default to '0'.
        // Filename and data are provided through the MascotGenericFile class, but note that we
        // convert the file contents from a String into a byte[] using the platforms default encoding.
        int liSize = spectra.size();

        for (int i = 0; i < liSize; i++) {
            UltraflexXMLFile lFile = (UltraflexXMLFile) spectra.elementAt(i);
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
            byte[] fileContents = lFile.getMGFFormat().getBytes();
            // Set the byte[].
            lSpectrum_file.setUnzippedFile(fileContents);
            // Create the database object.
            lSpectrum_file.persist(aConn);

            counter++;
        }
        return counter;
    }

    /**
     * This method finds all the LIFT folders in a certain run.
     *
     * @param aParent  File with the run top folder.
     * @param aSpectra Vector with the spectra found (reference parameter).
     * @param aPrefix  String with the prefix for the filename of the spectra found.
     * @throws IOException when the recursive browsing fails.
     */
    private void browseRunRecursively(File aParent, Vector aSpectra, String aPrefix, HashMap aFoundOnes) throws IOException {
        File[] list = aParent.listFiles();
        for (int i = 0; i < list.length; i++) {
            File lFile = list[i];
            if (lFile.isDirectory()) {
                if (LoadUltraflexXMLWorker.isLiftFolder(lFile)) {
                    // Include the name of the next-to-previous (parent's parent)
                    // folder as well.
                    File parent = lFile.getParentFile();
                    File grandParent = parent.getParentFile();
                    String newPrefix = aPrefix + "_" + grandParent.getName() + "_" + lFile.getName();
                    newPrefix = newPrefix.replace(' ', '_');
                    if (!aFoundOnes.containsKey(newPrefix)) {
                        Object o = aFoundOnes.put(newPrefix, new Integer(0));
                    }
                    this.loadSpectra(lFile, aSpectra, newPrefix, aFoundOnes);
                } else {
                    this.browseRunRecursively(lFile, aSpectra, aPrefix, aFoundOnes);
                }
            }
        }
    }

    /**
     * This method finds all the LIFT spectra in a certain LIFT folder.
     *
     * @param aParent   File with the LIFT top folder.
     * @param aSpectra  Vector with the spectra found (reference parameter).
     * @param aPrefix   String with the prefix for the filename of the spectra found.
     * @param aCounters HashMap that holds the counter for each possible filename.
     * @throws IOException when the recursive browsing fails.
     */
    private void loadSpectra(File aParent, Vector aSpectra, String aPrefix, HashMap aCounters) throws IOException {
        File[] files = aParent.listFiles();
        for (int i = 0; i < files.length; i++) {
            File lFile = files[i];
            if (lFile.isDirectory()) {
                loadSpectra(lFile, aSpectra, aPrefix, aCounters);
            } else if (lFile.getName().equals("peaklist.xml")) {
                UltraflexXMLFile xml = new UltraflexXMLFile(lFile);
                int counter = ((Integer) aCounters.get(aPrefix)).intValue();
                counter++;
                xml.setFilename(aPrefix + "_" + counter + ".mgf");
                aCounters.put(aPrefix, new Integer(counter));
                aSpectra.add(xml);
            }
        }
    }
}
