/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 12-feb-2004
 * Time: 9:33:29
 */
package com.compomics.mslims.util.fileio;

import com.compomics.mslims.db.accessors.LCRun;
import com.compomics.mslims.db.accessors.Spectrumfile;
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
 * This class presents an implementation of the SpectrumStorageEngine that is designed
 * to load and store LC runs and corresponding MGF files from a Bruker Esquire HCT mass spectrometer.
 *
 * @author Lennart Martens
 * @version $Id: EsquireSpectrumStorageEngine.java,v 1.4 2009/06/22 09:13:36 lennart Exp $
 */
public class EsquireSpectrumStorageEngine implements SpectrumStorageEngine {

    /**
     * This method takes care of loading all the LC runs from the file system, while displaying a progressbar.
     * <b><i>Please note</i></b> that the 'aFoundLCRuns' Vector is a reference parameter that will contain the Lcrun
     * instances after completion of the method!
     *
     * @param aList File[]  with the listing of the top-level directory to browse through.
     * @param aStoredLCRuns   Vector    with the Lcrun instances that were retrieved from the database.
     *                                  When found on the filesystem, these will not be included in the 'aNames'
     *                                  Vector with the results,s ince they are already stored.
     * @param aFoundLCRuns    Vector    that will contain the new (not yet in DB) Lcrun instances found on
     *                                  the local harddrive.
     * @param aParent   Flamable    with the paent that will do the error handling.
     * @param aProgress DefaulProgressBar   to display the progress on.
     */
    public void findAllLCRunsFromFileSystem(File[] aList, Vector aStoredLCRuns, Vector aFoundLCRuns, Flamable aParent, DefaultProgressBar aProgress) {
        LoadMGFWorker lmw = new LoadMGFWorker(aList, aStoredLCRuns, aFoundLCRuns, aParent, aProgress);
        lmw.start();
        aProgress.setVisible(true);
    }

    /**
     * This method actually takes care of finding all the spectrumfiles for the indiciated LCRun and
     * transforming these into Spectrumfile instances for storage in the database over the specified connection.
     *
     * @param aLCRun    LCRun instances for which the spectrumfiles need to be found and stored.
     * @param aProjectid    long with the projectid to associate the Spectrumfiles with.
     * @param aInstrumentid long with the instrumentid to associate the spectrumfiles with.
     * @param aConn Connection  on which to write the Spectrumfiles.
     * @return  int with the number of spectra stored.
     * @throws java.io.IOException  when the filereading goes wrong.
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
        for(int i = 0; i < liSize; i++) {
            MascotGenericFile lMascotGenericFile = (MascotGenericFile)spectra.elementAt(i);
            HashMap data = new HashMap(9);
            data.put(Spectrumfile.L_INSTRUMENTID, new Long(aInstrumentid));
            // The links.
            data.put(Spectrumfile.L_LCRUNID, new Long(aLCRun.getLcrunid()));
            data.put(Spectrumfile.L_PROJECTID, new Long(aProjectid));
            // The flags.
            data.put(Spectrumfile.IDENTIFIED, new Long(0));
            data.put(Spectrumfile.SEARCHED, new Long(0));
            // The filename.
            data.put(Spectrumfile.FILENAME, lMascotGenericFile.getFilename());
            // The total intensity.
            data.put(Spectrumfile.TOTAL_SPECTRUM_INTENSITY, lMascotGenericFile.getTotalIntensity());
            // The highest intensity.
            data.put(Spectrumfile.HIGHEST_PEAK_IN_SPECTRUM, lMascotGenericFile.getHighestIntensity());
            // Create the database object.
            Spectrumfile dbObject = new Spectrumfile(data);
            // Read the contents for the file into a byte[].
            byte[] fileContents = lMascotGenericFile.toString().getBytes();
            // Set the byte[].
            dbObject.setUnzippedFile(fileContents);
            dbObject.persist(aConn);
            counter++;
        }
        return counter;
    }
}
