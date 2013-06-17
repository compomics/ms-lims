/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 22-jun-2004
 * Time: 15:36:56
 */
package com.compomics.mslimscore.util.mascot;

import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import org.apache.log4j.Logger;

import com.compomics.mslimsdb.accessors.*;
import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mascotdatfile.util.interfaces.MascotDatfileInf;
import com.compomics.mascotdatfile.util.interfaces.QueryToPeptideMapInf;
import com.compomics.mascotdatfile.util.mascot.Header;
import com.compomics.mascotdatfile.util.mascot.MascotDatfile_Index;
import com.compomics.mascotdatfile.util.mascot.Masses;
import com.compomics.mascotdatfile.util.mascot.Parameters;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.PeptideHitAnnotation;
import com.compomics.mascotdatfile.util.mascot.ProteinHit;
import com.compomics.mascotdatfile.util.mascot.ProteinMap;
import com.compomics.mascotdatfile.util.mascot.Query;
import com.compomics.mascotdatfile.util.mascot.fragmentions.FragmentIonImpl;
import com.compomics.mslimscore.util.FragmentionMiddleMan;
import com.compomics.util.db.interfaces.Persistable;
import com.compomics.util.interfaces.Flamable;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/*
* CVS information:
*
* $Revision: 1.33 $
* $Date: 2009/12/17 14:08:39 $
*/

/**
 * This class will process Mascot results.
 *
 * @author Lennart Martens
 * @version $Id: MascotResultsProcessor.java,v 1.33 2009/12/17 14:08:39 kenny Exp $
 */
public class MascotResultsProcessor {
    // Class specific log4j logger for MascotResultsProcessor instances.
    private static Logger logger = Logger.getLogger(MascotResultsProcessor.class);

    /**
     * The database connection from which to retrieve the accessions.
     */
    private Connection iConn = null;

    /**
     * The confidence interval.
     */
    private double iThreshold = 0.0;

    /**
     * This HashMap will cache the datfilenames to datfileids for this MascotResultsProcessor.
     */
    private HashMap iDatfilenameToDatfileid = new HashMap();

    /**
     * HashMap that will hold a datfile as key, and the collection of searched spectra found in that datfile, as
     * values.
     */
    private HashMap iAllSpectraInDatfiles = new HashMap();
    
        private HashMap<String, Object> iMasterIdentificationToIdentificationid;


    /**
     * This constructor takes the DB connection to operate on as its argument.
     *
     * @param aConn      Connection with the database connection.
     * @param aThreshold double with the confidence interval for the parsing (eg. 0.05 for 95%)
     */
    public MascotResultsProcessor(Connection aConn, double aThreshold) {
        this(aConn, aThreshold, false);
    }

    /**
     * This constructor takes the DB connection to operate on as its argument.
     *
     * @param aConn                      Connection with the database connection.
     * @param aThreshold                 double with the confidence interval for the parsing (eg. 0.05 for 95%)
     * @param aMascotDistillerProcessing This boolean indicates whether Mascot Distiller was used for generating the
     *                                   spectrum files.
     */
    public MascotResultsProcessor(Connection aConn, double aThreshold, final boolean aMascotDistillerProcessing) {
        this.iConn = aConn;
        this.iThreshold = aThreshold;
        Query.setDistillerFilenameProcessing(aMascotDistillerProcessing);
    }


    /**
     * This method processes all the ID's and returns a Vector filled with instances of the Persistable elements that
     * have been parsed.
     *
     * @param aMergefile String with the filename for the mergefile
     * @param aDatfile   String with the URL for the datfile.
     */
    public Vector processIDs(String aMergefile, String aDatfile, Flamable aParent) {
        return this.processIDs(aMergefile, aDatfile, aParent, null);
    }

    /**
     * This method processes all the ID's and returns a Vector filled with instances of the Persistable elements that
     * have been parsed.
     *
     * @param aMergefile String with the filename for the mergefile
     * @param aDatfile   String with the URL for the datfile.
     * @param aProgress  DefaultProgressBar to show progress on. Can be 'null', in which case it is ignored.
     */
    public Vector processIDs(String aMergefile, String aDatfile, Flamable aParent, DefaultProgressBar aProgress) {

        // The Vector that will hold all identifications.
        Vector result = new Vector(1000, 250);
        // The Mascot '.dat' file.
        boolean isURL = false;
        // Find out if we have an older version of Mascot on the other side, or a more recent one.
        // The older versions will use 'ms-showtext.exe' in the 'x-cgi' folder, the newer ones require
        // the use of the 'ms-status.exe' application in the same folder.
        boolean useLegacy = true;
        String serverURL = aDatfile.substring(0, aDatfile.lastIndexOf("/cgi"));
        try {
            URL test = new URL(serverURL + "/x-cgi/ms-showtext.exe");
            URLConnection conn = test.openConnection();
            InputStream is = conn.getInputStream();
            is.close();
        } catch (IOException ioe) {
            useLegacy = false;
        }
        // These three variables (filename, folder and datedir) are only used in
        // NON-LEGACY mode (ie. useLegacy == false).
        String filename = null;
        String folder = null;
        String datedir = null;
        if (useLegacy) {
            serverURL = aDatfile.substring(0, aDatfile.lastIndexOf("/cgi")) + "/x-cgi/ms-showtext.exe?";
            if (aDatfile.startsWith("http")) {
                aDatfile = serverURL + aDatfile.substring(aDatfile.indexOf("file=") + 5, aDatfile.length());
                isURL = true;
            }
        } else {
            int dataSection = aDatfile.indexOf("/data/") + 6;
            datedir = aDatfile.substring(dataSection, aDatfile.indexOf("/", dataSection));
            folder = aDatfile.substring(aDatfile.indexOf("file=") + 5, aDatfile.lastIndexOf("/") + 1);
            filename = aDatfile.substring(aDatfile.indexOf("/", dataSection) + 1);
            serverURL = aDatfile.substring(0, aDatfile.lastIndexOf("/cgi")) + "/x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir=" + datedir + "&ResJob=" + filename;
            aDatfile = serverURL;
            isURL = true;
        }

        Vector tempAccessions = new Vector();

        // Try to get all accessions from a preferences list.
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("accessionPreferences.properties");
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String tempLine = null;
                while ((tempLine = br.readLine()) != null) {
                    tempLine = tempLine.trim();
                    // Only uniques, please.
                    if (!tempAccessions.contains(tempLine)) {
                        tempAccessions.add(tempLine);
                    }
                }
                br.close();
                is.close();
            }
        } catch (IOException ioe) {
            logger.error("Unable to retrieve list of accession numbers from preferences list: " + ioe.getMessage() + "!");
            logger.error(ioe.getMessage(), ioe);
        }

        // Get all known accession numbers from the db (if any).
        try {
            tempAccessions.add(Identification.getAllUniqueAccessions(iConn));
        } catch (Exception e) {
            // No real harm done.
        }

        // Progress if required.
        int startLoc = aDatfile.lastIndexOf("/") + 1;
        int endLoc = aDatfile.indexOf(".dat") + 4;
        if (aProgress != null) {
            aProgress.setMessage("Downloading datfile '" + aDatfile.substring(startLoc, endLoc) + "'...");
        }
        // Okay, first we need to retrieve a stream to the Mascot
        // '.dat' file, then we need to feed that stream to the rawparser.
        try {
            // The buffer to hold the datfile.
            StringBuffer all = new StringBuffer();
            BufferedReader input = null;
            if (isURL) {
                // URL connection.
                URL url = new URL(aDatfile);
                URLConnection conn = url.openConnection();
                input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                // Local file.
                input = new BufferedReader(new FileReader(aDatfile));
            }

            // Reading the .dat file and clearing HTML codes in the proces.
            String line = null;
            boolean started = false;
            if (useLegacy) {
                while ((line = input.readLine()) != null) {
                    if ((line.indexOf("</PRE>") >= 0) || (line.indexOf("</pre>") >= 0)) {
                        started = false;
                        break;
                    } else if (started) {
                        all.append(line + "\n");
                    } else if ((line.indexOf("<PRE>") >= 0) || (line.indexOf("<pre>") >= 0)) {
                        started = true;
                    }
                }
            } else {
                while ((line = input.readLine()) != null) {
                    if (started) {
                        all.append(line + "\n");
                    } else if (line.indexOf("MIME-Version") >= 0) {
                        all.append(line + "\n");
                        started = true;
                    }
                }
            }
            // Stream read, closing.
            input.close();
            String datContent = all.toString();
            HashMap lDatFile = new HashMap(4);
            if (useLegacy) {
                filename = aDatfile.substring(startLoc, endLoc);
                lDatFile.put(Datfile.FILENAME, filename);
                lDatFile.put(Datfile.SERVER, aDatfile.substring(0, aDatfile.indexOf("/x-cgi")));
                lDatFile.put(Datfile.FOLDER, aDatfile.substring(aDatfile.lastIndexOf("ms-showtext.exe?") + 16, startLoc));
            } else {
                lDatFile.put(Datfile.FILENAME, filename);
                lDatFile.put(Datfile.SERVER, aDatfile.substring(0, aDatfile.indexOf("/x-cgi")));
                lDatFile.put(Datfile.FOLDER, folder);
            }
            Datfile lDf = new Datfile(lDatFile);
            byte[] datfileBytes = null;
            ArrayList subset = null;
            // Since for big files (and correspondingly big Strings),
            // the getBytes() method fails due to limited range
            // (float range; breaks at 16,777,216 bytes),
            // we split the String here if necessary.
            String temp = datContent;
            subset = new ArrayList();
            while (temp.length() > 10000000) {
                subset.add(temp.substring(0, 10000000));
                temp = temp.substring(10000000);
            }
            // Now to process everything using a ByteArrayOutputStream.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (subset != null) {
                for (Iterator lIterator = subset.iterator(); lIterator.hasNext();) {
                    String s = (String) lIterator.next();
                    baos.write(s.getBytes());
                }
            }
            subset = null;
            baos.write(temp.getBytes());
            baos.flush();
            datfileBytes = baos.toByteArray();
            baos.flush();
            baos.close();
            System.gc();
            lDf.setUnzippedFile(datfileBytes);

            if (aProgress != null) {
                aProgress.setValue(aProgress.getValue() + 1);
                aProgress.setMessage("Parsing datfile '" + filename + "'...");
            }

            // Parsing the results.
            BufferedReader br = new BufferedReader(new StringReader(datContent));
            MascotDatfileInf mdf = new MascotDatfile_Index(br, filename);
            this.addSearchedSpectra(mdf);
            Vector v = this.extractIDs(mdf);

            // Store the datfile in the results Vector.
            result.add(lDf);

            // First cycle all to retrieve accession numbers from unique identifications.
            int liSize = v.size();
            for (int i = 0; i < liSize; i++) {
                // Get the identified spectrum.
                MascotIdentifiedSpectrum lSpectrum = (MascotIdentifiedSpectrum) v.elementAt(i);
                if (lSpectrum.getIsoformCount() == 1) {
                    String lAccession = lSpectrum.getAccession(null);
                    if (!tempAccessions.contains(lAccession)) {
                        tempAccessions.add(lAccession);
                    }
                }
            }

            // Store all retrieved accession numbers.
            String[] accessions = new String[tempAccessions.size()];
            tempAccessions.toArray(accessions);
            Arrays.sort(accessions);
            // Invert the array.
            String[] accessionsInv = new String[accessions.length];
            for (int i = 0; i < accessions.length; i++) {
                accessionsInv[i] = accessions[accessions.length - (i + 1)];
            }

            // Cycle all and store them.
            for (int i = 0; i < liSize; i++) {
                // Get the identified spectrum.
                MascotIdentifiedSpectrum mis = (MascotIdentifiedSpectrum) v.elementAt(i);

                // We have to check the file stuff!
                String specFile = mis.getFile().trim();

                // Get the accession number.
                String accession = mis.getAccession(accessionsInv);

                // Isolate the enzymatic part.
                String descr = mis.getDescription(accession);
                if (descr == null) {
                    descr = "No description found.";
                    mis.setDescription(descr, accession);
                } else if (descr.indexOf(";") >= 0) {
                    descr = descr.replace(';', '*');
                    mis.setDescription(descr, accession);
                }

                int start = descr.indexOf("(*") + 2;
                int end = descr.indexOf("*)");
                if (start < 0 || end < 0) {
                    descr = "FE";
                } else {
                    mis.setDescription(descr.substring(end + 2), accession);
                    descr = descr.substring(start, end);
                }

                // See if there are any isoforms in the description.
                String tempDesc = mis.getDescription(accession);
                String isoforms = mis.getIsoformAccessions(accession);
                int startIsoforms = -1;
                if ((startIsoforms = tempDesc.indexOf("^A")) >= 0) {
                    String tempDesc2 = tempDesc.substring(0, startIsoforms);
                    mis.setDescription(tempDesc2, accession);
                    if (isoforms == null) {
                        isoforms = tempDesc.substring(startIsoforms + 2);
                    } else {
                        isoforms += tempDesc.substring(startIsoforms);
                    }
                }
                // Remove all 'xx|' or 'xxx|' (for IPI) Strings from the isoforms.
                int startPipe = -1;
                while (isoforms != null && (startPipe = isoforms.indexOf("|")) > 0) {
                    if (startPipe >= 3 && isoforms.substring(startPipe - 3, startPipe).equalsIgnoreCase("ipi")) {
                        isoforms = isoforms.substring(0, startPipe - 3) + isoforms.substring(startPipe + 1);
                    } else {
                        isoforms = isoforms.substring(0, startPipe - 2) + isoforms.substring(startPipe + 1);
                    }
                }
                // Put all params in a HashMap with the correct keys.
                HashMap hm = new HashMap();
                hm.put(IdentificationTableAccessor.ACCESSION, accession);
                hm.put(IdentificationTableAccessor.CAL_MASS, new BigDecimal(mis.getTheoreticalMass()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                hm.put(IdentificationTableAccessor.END, new Long(mis.getEnd(accession)));
                hm.put(IdentificationTableAccessor.ENZYMATIC, descr);
                hm.put(IdentificationTableAccessor.EXP_MASS, new Double(mis.getMeasuredMass()));
                hm.put(IdentificationTableAccessor.MODIFIED_SEQUENCE, mis.getModifiedSequence());
                hm.put(IdentificationTableAccessor.ION_COVERAGE, mis.getIon_coverage());
                hm.put(IdentificationTableAccessor.SCORE, new Long(mis.getScore()));
                hm.put(IdentificationTableAccessor.HOMOLOGY, new Double(mis.getHomologyTreshold()));
                hm.put(IdentificationTableAccessor.SEQUENCE, mis.getSequence());
                hm.put(IdentificationTableAccessor.START, new Long(mis.getStart(accession)));
                hm.put(IdentificationTableAccessor.VALID, new Integer(1));
                hm.put(IdentificationTableAccessor.IDENTITYTHRESHOLD, new Long(mis.getIdentityTreshold()));
                hm.put(IdentificationTableAccessor.CONFIDENCE, new Double(this.iThreshold));
                hm.put(IdentificationTableAccessor.DESCRIPTION, mis.getDescription(accession));
                hm.put(IdentificationTableAccessor.DB, mis.getDBName());
                hm.put(IdentificationTableAccessor.PRECURSOR, new Double(mis.getPrecursorMZ()));
                hm.put(IdentificationTableAccessor.CHARGE, new Integer(mis.getChargeState()));
                hm.put(IdentificationTableAccessor.TITLE, mis.getSearchTitle());
                hm.put(IdentificationTableAccessor.ISOFORMS, isoforms);
                hm.put(IdentificationTableAccessor.DB_FILENAME, mis.getDBFilename());
                hm.put(IdentificationTableAccessor.MASCOT_VERSION, mis.getMascotVersion());
                hm.put(IdentificationTableAccessor.DATFILE_QUERY, mis.getQueryNr());

                // Temporary storage of future dependent rows.
                Identification mo = new Identification(hm);
                mo.setTemporaryDatfilename(filename);
                mo.setTemporarySpectrumfilename(specFile);
                mo.setFragmentions(mis.getFragmentIons());
                mo.setFragmentMassTolerance(mis.getFragmentMassError());

                // Adding it to the result Vector.
                result.add(mo);
            }

            if (aProgress != null) {
                aProgress.setValue(aProgress.getValue() + 1);
            }
        } catch (Exception e) {
            aProgress.dispose();
            aParent.passHotPotato(e, "Could not parse " + aDatfile + ": " + e.getMessage());
            result = new Vector();
        }
        return result;
    }

    /**
     * This method will store all the specified persistables (expected are Datfile and IdentificationTableAccessors) to
     * the database and updates the corresponding spectrumfiles as well.
     *
     * @param aPersistables Vector with the persistables to store.
     * @param aParent       Flamable with the instance to notify when something goes wrong.
     */
    public void storeData(Vector aPersistables, Flamable aParent) {
        storeData(aPersistables, aParent, null);
    }

    /**
     * This method will store all the specified persistables (expected are Datfile and IdentificationTableAccessors) to
     * the database and updates the corresponding spectrumfiles as well, displaying a progressbar on screen if one is
     * specified.
     *
     * @param aPersistables Vector with the persistables to store.
     * @param aParent       Flamable with the instance to notify when something goes wrong.
     * @param aProgress     DefaultProgressBar with the progressbar to use; can be 'null' for no progressbar.
     */
    public void storeData(Vector aPersistables, Flamable aParent, DefaultProgressBar aProgress) {
        if (aProgress != null) {
            aProgress.setMessage("Filtering data...");
        }

        // Start off by filtering out only what is Persistable.
        Vector persistable = new Vector(aPersistables.size());
        int liSize = aPersistables.size();
        for (int i = 0; i < liSize; i++) {
            Object o = aPersistables.get(i);
            if (o instanceof Persistable) {
                persistable.add(o);
            } else {
                // This is one we do not need to process, so mark it.
                if (aProgress != null) {
                    aProgress.setValue(aProgress.getValue() + 1);
                }
            }
        }
        // All set!
        // Let the beast go!
        liSize = persistable.size();
        if (aProgress != null) {
            aProgress.setMessage("Processing identified spectra...");
        }
        // We'll need to store the changed stuff later in case a rollback becomes necessary.
        try {
            for (int i = 0; i < liSize; i++) {
                Persistable ps = (Persistable) persistable.get(i);
                // See if we have:
                //  - identification: update spectrumfile + l_spectrumid + l_datfileid.
                if (ps instanceof Identification) {
                    Identification ita = (Identification) ps;
                    logger.debug("Start persisting identification of spectrumid " + ita.getL_spectrumid());
                    // We need to update the spectrumfile as well.
                    Spectrum lSpectrum = Spectrum.findFromName(ita.getTemporarySpectrumfilename(), iConn);
                    if (lSpectrum.getIdentified() > 0) {
                        lSpectrum.setIdentified(lSpectrum.getIdentified() + 1);
                    } else {
                        lSpectrum.setIdentified(1);
                    }
                    // Update it.
                    lSpectrum.update(iConn);
                    logger.debug("Persisted identification of spectrumid " + ita.getL_spectrumid());
                    ita.setL_spectrumid(lSpectrum.getSpectrumid());
                    // Now to find the datfile ID.
                    Object l_datfileid = iDatfilenameToDatfileid.get(ita.getTemporaryDatfilename());
                    iMasterIdentificationToIdentificationid.put(ita.getTemporarySpectrumfilename(), ps.getGeneratedKeys()[0]);
                    if (l_datfileid == null) {
                        throw new SQLException("No datfile link found for datfile with filename '" + ita.getTemporaryDatfilename() + "'!");
                    }
                    ita.setL_datfileid(((Number) l_datfileid).longValue());
                } else if(ps instanceof AlternativeIdentification) {
                    AlternativeIdentification ita = (AlternativeIdentification) ps;
                    ita.setL_spectrumid(Spectrum.findFromName(ita.getTemporarySpectrumfilename(), iConn).getSpectrumid());
                    Object l_datfileid = iDatfilenameToDatfileid.get(ita.getTemporaryDatfilename());
                    if (l_datfileid == null) {
                        throw new SQLException("No datfile link found for datfile with filename '" + ita.getTemporaryDatfilename() + "'!");
                    }
                    ita.setL_datfileid(((Number) l_datfileid).longValue());
                }
                ps.persist(iConn);
                
                // See if we have:
                //  - Datfile: in this case, we need to retrieve the generated key and store it in
                //             a mapping.
                if (ps instanceof Datfile) {
                    Datfile datfile = (Datfile) ps;
                    iDatfilenameToDatfileid.put(datfile.getFilename(), ps.getGeneratedKeys()[0]);
                }
                //  - Identification: in this case, we still need to store the fragment ions
                //      & a neutral Validation entry (version 7.6).
                else if (ps instanceof Identification) {
                    Identification id = (Identification) ps;
                    double tol = id.getFragmentMassTolerance();
                    Iterator<com.compomics.mslimsdb.accessors.Fragmention> iter = id.getFragmentions().iterator();
                    while (iter.hasNext()) {
                        FragmentIonImpl fi = (FragmentIonImpl) FragmentionMiddleMan.asFragmentIonImpl(iter.next());

                        HashMap hm = new HashMap();
                        hm.put(Fragmention.FRAGMENTIONNUMBER, new Long(fi.getNumber()));
                        hm.put(Fragmention.INTENSITY, new Long(new Double(fi.getIntensity()).longValue()));
                        hm.put(Fragmention.IONNAME, fi.getType());
                        hm.put(Fragmention.IONTYPE, new Long(fi.getID()));
                        hm.put(Fragmention.L_IDENTIFICATIONID, (Long)(id.getGeneratedKeys()[0]));
                        hm.put(Fragmention.L_IONSCORINGID, new Long(fi.getImportance()));
                        hm.put(Fragmention.MASSDELTA, new Double(new BigDecimal(fi.getTheoreticalExperimantalMassError()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()));
                        hm.put(Fragmention.MASSERRORMARGIN, new Double(id.getFragmentMassTolerance()));
                        hm.put(Fragmention.MZ, new Double(new BigDecimal(fi.getMZ()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()));

                        Fragmention fi_db = new Fragmention(hm);
                        fi_db.persist(iConn);
                    }

                    // Create and persist Validation for the new Identification.
                    HashMap lValidationMap = new HashMap();
                    lValidationMap.put(Validation.L_IDENTIFICATIONID, (Long) id.getGeneratedKeys()[0]);
                    lValidationMap.put(Validation.L_VALIDATIONTYPEID, new Long(Validationtype.NOT_VALIDATED));

                    Validation lValidation = new Validation(lValidationMap);
                    lValidation.persist(iConn);

                } else if (ps instanceof AlternativeIdentification) {
                    AlternativeIdentification id = (AlternativeIdentification) ps;
                    double tol = id.getFragmentMassTolerance();
                    Iterator iter = id.getFragmentions().iterator();
                    while (iter.hasNext()) {
                        FragmentIonImpl fi = (FragmentIonImpl) iter.next();

                        HashMap hm = new HashMap();
                        hm.put(Fragmention.FRAGMENTIONNUMBER, new Long(fi.getNumber()));
                        hm.put(Fragmention.INTENSITY, new Long(new Double(fi.getIntensity()).longValue()));
                        hm.put(Fragmention.IONNAME, fi.getType());
                        hm.put(Fragmention.IONTYPE, new Long(fi.getID()));
                        hm.put(Fragmention.L_IDENTIFICATIONID, (Long)(id.getGeneratedKeys()[0]));
                        hm.put(Fragmention.L_IONSCORINGID, new Long(fi.getImportance()));
                        hm.put(Fragmention.MASSDELTA, new Double(new BigDecimal(fi.getTheoreticalExperimantalMassError()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()));
                        hm.put(Fragmention.MASSERRORMARGIN, new Double(id.getFragmentMassTolerance()));
                        hm.put(Fragmention.MZ, new Double(new BigDecimal(fi.getMZ()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()));

                        AlternativeFragmentIon fi_db = new AlternativeFragmentIon(hm);
                        fi_db.persist(iConn);
                    }
                                        // Create and persist Validation for the new Identification.
                    HashMap lValidationMap = new HashMap();
                    lValidationMap.put(Validation.L_IDENTIFICATIONID, (Long) id.getGeneratedKeys()[0]);
                    lValidationMap.put(Validation.L_VALIDATIONTYPEID, new Long(Validationtype.NOT_VALIDATED));
                if (aProgress != null) {
                    aProgress.setValue(aProgress.getValue() + 1);
                }
            }
            // Now do all the updates for the spectrumfiles.
            // Add the information about having been searched to the PKLfiles in the DB.
            if (aProgress != null) {
                aProgress.setMessage("Updating 'searched' flag on all spectra in the datfiles...");
            }
            Iterator iter = iAllSpectraInDatfiles.values().iterator();
            while (iter.hasNext()) {
                Set names = (Set) iter.next();
                String[] filenames = new String[names.size()];
                names.toArray(filenames);
                Spectrum.addOneToSearchedFlag(filenames, iConn);
            }
            if (aProgress != null) {
                aProgress.setValue(aProgress.getValue() + 1);
            }
            }
        } catch (Exception e) {
            // Do a rollback if possible.
            String message = "Encountered an error while trying to insert data: " + e.getMessage();
            String rollbackStatus = "Rollback not yet implemented!";
            if (aProgress != null) {
                aProgress.dispose();
            }
            aParent.passHotPotato(e, message + " " + rollbackStatus);
        }
    }

    private Vector extractIDs(MascotDatfileInf aMDF) throws IllegalArgumentException {
        // Vector that will contain the MascotIdentifiedSpectrum instances.
        Vector result = new Vector();

        // Get the generic parameters for the search,
        // Extract the db filename and the Mascot version.
        Header header = aMDF.getHeaderSection();
        String version = header.getVersion();
        String dbfilename = header.getRelease();
        Parameters parameters = aMDF.getParametersSection();
        String searchTitle = parameters.getCom();
        if (searchTitle == null) {
            searchTitle = "!No title specified";
        } else {
            int location = searchTitle.indexOf("|");
            if (location >= 0) {
                searchTitle = searchTitle.substring(0, location).trim();
            }
        }
        String inputfile = parameters.getFile();
        String dbName = parameters.getDatabase();
        ProteinMap proteinMap = aMDF.getProteinMap();
        Masses masses = aMDF.getMasses();

        // Rank of the hit (only highest ranking hits
        // (i.e.: rank = 1)) are considered,
        int rank = 1;

        // check if the mdf is from a multifile
        boolean lIsMultiFile = parameters.isDistillerMultiFile();
        String[] lMultiFileNames = parameters.getDistillerMultiFileNames();


        // Get all the queries...
        // Map to transfer query ID into peptidehits.
        QueryToPeptideMapInf queryToPepMap = aMDF.getQueryToPeptideMap(iConn);
        Iterator iter = aMDF.getQueryIterator();
        int lQueryCounter = 0;
        while (iter.hasNext()) {
            // Get the query.
            Query query = (Query) iter.next();
            lQueryCounter++;
            // Get the first ranking peptide hit, if any.
            PeptideHit ph = queryToPepMap.getPeptideHitOfOneQuery(query.getQueryNumber(), rank);
            if (ph != null && ph.scoresAboveIdentityThreshold(iThreshold)) {
                // We have a peptide hit for this query that scores equal
                // to or above the threshold. Parse it and create a
                // MascotIdentifiedSpectrum.
                MascotIdentifiedSpectrum mis = new MascotIdentifiedSpectrum();
                // Generic stuff, already parsed in advance.
                mis.setDBFilename(dbfilename);
                mis.setMascotVersion(version);
                mis.setSearchTitle(searchTitle);
                mis.setOriginal_file(inputfile);
                mis.setDBName(dbName);
                mis.setQueryNr(lQueryCounter);

                // Query title.


                //if it is a multifile get the scans for the query
                mis.setFile(query.getTitle());

                // Additional query info.
                if (mis.getFile() == null && aMDF.getNumberOfQueries() == 1) {
                    // In this case, a single query was performed using a file that did not contain
                    // 'merge' (regardless of case). This is indicative of a search with a single spectrum.
                    // Therefore we just keep the name of the spectrum as reported by Mascot.
                    mis.setFile(inputfile);
                } else if (mis.getFile() == null && aMDF.getNumberOfQueries() > 1) {
                    // Mergefile.
                    // We omit the filename (set it to '*').
                    mis.setFile("*");
                }

                // Query m/z and charge.
                double mz = new BigDecimal(query.getPrecursorMZ()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                String chargeString = query.getChargeString();
                boolean isNegative = false;
                int chargeLoc = chargeString.indexOf('+');
                if (chargeLoc < 0) {
                    chargeLoc = chargeString.indexOf('-');
                    isNegative = true;
                }
                chargeString = chargeString.substring(0, chargeLoc);
                int charge = Integer.parseInt(chargeString);
                if (isNegative) {
                    charge = -charge;
                }
                mis.setChargeState(charge);
                mis.setPrecursorMZ(mz);

                // PeptideHit stuff.
                // Thresholds and rank.
                mis.setHomologyTreshold((int) ph.getHomologyThreshold());
                mis.setIdentityTreshold((int) ph.calculateIdentityThreshold(iThreshold));
                mis.setRank(rank);

                mis.setTheoreticalMass(ph.getPeptideMr());
                mis.setMeasuredMass(ph.getPeptideMr() + ph.getDeltaMass());
                mis.setSequence(ph.getSequence());
                String lModifiedSequence = ph.getModifiedSequence();
                // If a modified sequence contain's a '#' character, this means the modification was not included in the modificationConversion.txt file.
                // Throw an error since we don't want to have multiple names for identical modifications.
                if (lModifiedSequence.indexOf('#') != -1) {
                    throw new IllegalArgumentException("\n\nModificationConversion.txt does not contain enough information to parse the following identification:\n\t" + lModifiedSequence + "\nPlease add the modification into modificationcoverions.txt. ");
                }
                mis.setModifiedSequence(lModifiedSequence);
                mis.setScore(ph.getIonsScore());

                // Protein stuff.
                MascotIsoforms mifs = new MascotIsoforms();
                Iterator iter2 = ph.getProteinHits().iterator();
                while (iter2.hasNext()) {
                    ProteinHit protein = (ProteinHit) iter2.next();
                    // Hold the original accession to access 
                    String originalAccession = protein.getAccession();
                    String trimmedAccession = originalAccession;
                    int startLoc = trimmedAccession.indexOf('(');
                    int endLoc = trimmedAccession.indexOf(')');
                    int tempStart = -1;
                    int tempEnd = -1;
                    if ((startLoc >= 0) && (endLoc >= 0)) {
                        String tempLocalization = trimmedAccession.substring(startLoc + 1, endLoc);
                        StringTokenizer lst = new StringTokenizer(tempLocalization, "-");
                        try {
                            tempStart = Integer.parseInt(lst.nextToken().trim());
                            tempEnd = Integer.parseInt(lst.nextToken().trim());
                            trimmedAccession = trimmedAccession.substring(0, startLoc).trim();
                        } catch (Exception e) {
                            // Do nothing.
                            // It's probably just not a location String.
                        }
                    }
                    // If no start and end location found, take those from the
                    // protein information supplied by Mascot.
                    if (tempStart < 0) {
                        tempStart = protein.getStart();
                        tempEnd = protein.getStop();
                    }
                    mifs.addIsoform(trimmedAccession, proteinMap.getProteinDescription(originalAccession), tempStart, tempEnd);
                }
                mis.setIsoforms(mifs);
                // Add the ion coverage String.
                PeptideHitAnnotation pha = ph.getPeptideHitAnnotation(masses, parameters, query.getPrecursorMZ(), query.getChargeString());
                String ion_coverage = getIonCoverage(ph, query, pha);
                mis.setIon_coverage(ion_coverage);
                // Calling this method will initialize all mass deltas between matched peaks.
                pha.getMatchedBYions(query.getPeakList());
                // Calling this method will initialize the ion importance as determined by Mascot.
                Collection fragmentions = pha.getFusedMatchedIons(query.getPeakList(), ph.getPeaksUsedFromIons1(), query.getMaxIntensity(), 0.10);
                mis.setFragmentIons(fragmentions);
                double fragmentError = Double.parseDouble(parameters.getITOL());
                String fragmentErrorUnit = parameters.getITOLU();
                if (fragmentErrorUnit.trim().toLowerCase().equals("ppm")) {
                    fragmentError = query.getPrecursorMZ() * fragmentError * 1e-6;
                }
                mis.setFragmentMassError(fragmentError);
                // Add mis to vector.
                result.add(mis);
            }
        }
        return result;
    }

    private String getIonCoverage(PeptideHit ph, Query query, PeptideHitAnnotation pha) {
        // Match Mascot ions.
        Vector<FragmentIon> ions = pha.getMatchedIonsByMascot(query.getPeakList(), ph.getPeaksUsedFromIons1());
        // Peptide sequence + length.
        String sequence = ph.getSequence();
        int length = sequence.length();
        // Create Y and B boolean arrays.
        boolean[] yIons = new boolean[length];
        boolean[] bIons = new boolean[length];
        // Fill out arrays.
        for (int i = 0; i < ions.size(); i++) {
            FragmentIon lFragmentIon = (FragmentIon) ions.elementAt(i);
            switch (lFragmentIon.getID()) {
                case FragmentIon.Y_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    if (yIons.length == lFragmentIon.getNumber() + 1) {
                        yIons[yIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.Y_DOUBLE_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    if (yIons.length == lFragmentIon.getNumber() + 1) {
                        yIons[yIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.B_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    if (bIons.length == lFragmentIon.getNumber() + 1) {
                        bIons[bIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.B_DOUBLE_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    if (bIons.length == lFragmentIon.getNumber() + 1) {
                        bIons[bIons.length - 1] = true;
                    }
                    break;

                default:
                    // Skip other fragmentions.
            }
        }
        // Now simply add formatting.
        String[] modifiedAA = ph.getModifiedSequenceComponents();
        StringBuffer formattedSequence = new StringBuffer("<html>");
        // Cycle the amino acids (using b-ions indexing here).
        for (int i = 0; i < bIons.length; i++) {
            boolean italic = false;
            boolean bold = false;
            // First and last one only have 50% coverage anyway
            if (i == 0) {
                if (bIons[i]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    if (yIons[yIons.length - (i + 3)]) {
                        bold = true;
                    }
                }
            } else if (i == (length - 1)) {
                if (bIons[i] && bIons[i - 1]) {
                    if (bIons[i - 2]) {
                        italic = true;
                    }
                }
                if (yIons[yIons.length - (i + 1)]) {
                    bold = true;
                }
            } else {
                // Aha, two ions needed here.
                if (bIons[i] && bIons[i - 1]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    bold = true;
                }
            }
            // Actually add the next char.
            formattedSequence.append(
                    (italic ? "<u>" : "") +
                            (bold ? "<font color=\"red\">" : "") +
                            modifiedAA[i].replaceAll("<", "&lt;").replaceAll(">", "&gt;") +
                            (italic ? "</u>" : "") +
                            (bold ? "</font>" : "")
            );
        }
        // Finalize HTML'ized label text.
        formattedSequence.append("</html>");

        return formattedSequence.toString();
    }

    /**
     * This method adds all the searched spectra from this datfile to the 'iAllSpectraInDatfiles' map, if the spectra
     * are already present, it will update their count.
     *
     * @param aMDF MascotDatfile from which the spectra are read.
     */
    private void addSearchedSpectra(MascotDatfileInf aMDF) {
        Iterator iter = aMDF.getQueryIterator();
        HashSet filenames = new HashSet(aMDF.getNumberOfQueries());

        Parameters parameters = aMDF.getParametersSection();
        // check if the mdf is from a multifile
        while (iter.hasNext()) {
            Query lQuery = (Query) iter.next();
            String title = lQuery.getTitle();
            filenames.add(title);
        }
        Object result = iAllSpectraInDatfiles.put(aMDF.getFileName(), filenames);
        if (result != null) {
            logger.error("\n\nFound duplicate processed datfilename: '" + aMDF.getFileName() + "'!");
        }
    }


}
