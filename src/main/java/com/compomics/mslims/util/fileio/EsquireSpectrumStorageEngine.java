/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 12-feb-2004
 * Time: 9:33:29
 */
package com.compomics.mslims.util.fileio;

import com.compomics.mslims.db.accessors.ScanTableAccessor;
import com.compomics.mslims.db.accessors.Spectrum;
import com.compomics.mslims.db.accessors.Spectrum_file;
import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.fileio.interfaces.MergeFileReader;
import com.compomics.mslims.util.fileio.interfaces.SpectrumStorageEngine;
import com.compomics.mslims.util.fileio.mergefiles.MergeFileReaderFactory;
import com.compomics.mslims.util.workers.LoadMGFWorker;
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
 * $Revision: 1.4 $
 * $Date: 2009/06/22 09:13:36 $
 */

/**
 * This class presents an implementation of the SpectrumStorageEngine that is designed to load and store LC runs and
 * corresponding MGF files from a Bruker Esquire HCT mass spectrometer.
 *
 * @author Lennart Martens
 * @version $Id: EsquireSpectrumStorageEngine.java,v 1.4 2009/06/22 09:13:36 lennart Exp $
 */
public class EsquireSpectrumStorageEngine implements SpectrumStorageEngine {
    // Class specific log4j logger for EsquireSpectrumStorageEngine instances.
    private static Logger logger = Logger.getLogger(EsquireSpectrumStorageEngine.class);

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
        LoadMGFWorker lmw = new LoadMGFWorker(aList, aStoredLCRuns, aFoundLCRuns, aParent, aProgress);
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
            // The charge - as long for the database accessor.
            Long lCharge = new Long(lMascotGenericFile.getCharge());
            data.put(Spectrum.CHARGE, lCharge);
                        // The precursorMZ.
            data.put(Spectrum.MASS_TO_CHARGE, lMascotGenericFile.getPrecursorMZ());


            // Create the database object.
            Spectrum lSpectrum = new Spectrum(data);
            lSpectrum.persist(aConn);

            // Get the spectrumid from the generated keys.
            Long lSpectrumid = (Long) lSpectrum.getGeneratedKeys()[0];
            // Create the Spectrum_file instance.
            Spectrum_file lSpectrum_file = new Spectrum_file();
            // Set spectrumid
            lSpectrum_file.setL_spectrumid(lSpectrumid);
            // Set the filecontent
            // Read the contents for the file into a byte[].
            byte[] fileContents = lMascotGenericFile.toString().getBytes();
            // Set the byte[].
            lSpectrum_file.setUnzippedFile(fileContents);
            // Create the database object.
            lSpectrum_file.persist(aConn);

            // Now persist the scan information, if any.
            if(lMascotGenericFile.getRetentionInSeconds() != null){
                double[] lRTInSeconds = lMascotGenericFile.getRetentionInSeconds();
                int[] lScanNumbers = lMascotGenericFile.getScanNumbers();
                for (int j = 0; j < lRTInSeconds.length; j++) {
                    double lRTInSecond = lRTInSeconds[j];

                    ScanTableAccessor lScanTableAccessor = new ScanTableAccessor();
                    lScanTableAccessor.setL_spectrumid(lSpectrumid);
                    lScanTableAccessor.setRtsec(lRTInSecond);
                    if(lScanNumbers != null){
                        lScanTableAccessor.setNumber(lScanNumbers[j]);
                    }
                    lScanTableAccessor.persist(aConn);
                }
            }
            counter++;
        }
        return counter;
    }

    /**
     * This method attempts to parse the retiontion time from the title of esquire mgf files.
     * @param aTitle TITLE variable in the mgf files produced by the Esquire IT.
     * @return Retention time in seconds.
     */
    private double parseRetentionTimeInSecondsFromTitle(String aTitle) {
        aTitle = aTitle.toLowerCase();
        int lIndexStop = aTitle.indexOf(" min");
        int lIndexStart = aTitle.lastIndexOf("( ");
        double result = 0;

        if(lIndexStop != -1){
            String s = aTitle.substring(lIndexStart + 2, lIndexStop);
            result = Double.parseDouble(s); // In minutes!!
            result = result * 60; // To seconds!!
        }

        return result;
    }
}
