/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 12-feb-2004
 * Time: 9:04:18
 */
package com.compomics.mslimscore.util.fileio.interfaces;

import com.compomics.mslimsdb.accessors.Fragmentation;
import com.compomics.mslimsdb.accessors.LCRun;
import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.util.interfaces.Flamable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This interface describes the behaviour for a SpectrumStorage implementation. The required functionality can be summed
 * up as allowing Lcrun instances to be read from the filesystem, and to allow the reading of spectrumfiles from the
 * harddrive and transforming these into correct SpectrumFile instances for DB storage.
 *
 * @author Lennart Martens
 * @version $Id: SpectrumStorageEngine.java,v 1.3 2004/07/08 13:14:19 lennart Exp $
 */
public interface SpectrumStorageEngine {

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
    public abstract void findAllLCRunsFromFileSystem(File[] aList, Vector aStoredLCRuns, Vector aFoundLCRuns, Flamable aParent, DefaultProgressBar aProgress);

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
    public abstract int
    loadAndStoreSpectrumFiles(LCRun aLCRun, long aProjectid, long aInstrumentid, Connection aConn, Fragmentation aFragmentation) throws IOException, SQLException;
}
