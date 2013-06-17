/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.fileio;

import com.compomics.mascotdatfile.util.mascot.Header;
import com.compomics.mascotdatfile.util.mascot.MascotDatfile_Index;
import com.compomics.mascotdatfile.util.mascot.Masses;
import com.compomics.mascotdatfile.util.mascot.Parameters;
import com.compomics.mascotdatfile.util.mascot.PeptideHit;
import com.compomics.mascotdatfile.util.mascot.ProteinHit;
import com.compomics.mascotdatfile.util.mascot.ProteinMap;
import com.compomics.mascotdatfile.util.mascot.Query;
import com.compomics.mascotdatfile.util.mascot.enumeration.MascotDatfileType;
import com.compomics.mascotdatfile.util.mascot.factory.MascotDatfileFactory;
import com.compomics.mascotdatfile.util.mascot.index.QueryToPeptideMap_Index;
import com.compomics.mslimscore.util.interfaces.PeptideIdentification;
import com.compomics.mslimscore.util.mascot.DatfilePeptideIdentification;
import com.compomics.mslimscore.util.quantitation.ratios.RatioGroupCollection;
import com.compomics.util.interfaces.Flamable;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class DatFile {
	// Class specific log4j logger for DatFile instances.
	 private static Logger logger = Logger.getLogger(DatFile.class);
    /**
     * Original dat file
     */
    private File iOriginalDatFile;
    /**
     * File path of the original dat file
     */
    private String iFilePath;
    /**
     * The mergefile
     */
    private File iMergeFile = null;
    /**
     * The (indexed) mascot dat file
     */
    private MascotDatfile_Index iMascotDatFile;
    /**
     * The flamable
     */
    private Flamable iFlamable;

    /**
     * The constructor
     * @param aOriginalDatFile The dat file
     * @param aFlamable The Flamable
     */
    public DatFile(File aOriginalDatFile, Flamable aFlamable) {
        this.iFlamable = aFlamable;
        this.iOriginalDatFile = aOriginalDatFile;
        this.iFilePath = aOriginalDatFile.getAbsolutePath();
    }

    /**
     * Constructor. This constructor will take datfiles and mgf mergefiles. The identifications will be linked to spectr in the mergefile
     * @param aDatFile The dat file
     * @param aMergeFile The mergefile with the spectra
     * @param aFlamable The Flamable
     */
    public DatFile(File aDatFile, File aMergeFile, Flamable aFlamable) {
        this.iFlamable = aFlamable;
        this.iFilePath = aDatFile.getAbsolutePath();
        this.iOriginalDatFile = aDatFile;
        this.iMergeFile = aMergeFile;
    }


    /**
     * Getter for the MascotDatfile
     * @return MascotDatfile_Index
     */
    public MascotDatfile_Index getMascotDatFile() {
        if(iMascotDatFile == null){
            this.readMascotDatFile();
        }
        return iMascotDatFile;
    }

    /**
     * This method reads the mascot dat file.
     */
    public void readMascotDatFile(){
        iMascotDatFile  = (MascotDatfile_Index) MascotDatfileFactory.create(iFilePath, MascotDatfileType.INDEX);
    }

    /**
     * Getter for the RatioGroupCollection
     * @param aFlamable Flamable
     * @param aThreshold The threshold
     * @return RatioGroupCollection
     */
    public RatioGroupCollection getITraqRatioGroupCollection(Flamable aFlamable, double aThreshold){
        Mdf_iTraqReader lReader = new Mdf_iTraqReader(iOriginalDatFile, iMergeFile, aFlamable);
        lReader.setThreshold(aThreshold);
        return lReader.getRatioGroupCollection();
    }

    /**
     * Getter for the RatioGroupCollection
     * @param aFlamable Flamable
     * @param aConn Connection to ms_lims
     * @param aDatFileid The datfile id in ms_lims
     * @param aThreshold The threshold
     * @return RatioGroupCollecion
     */
    public RatioGroupCollection getITraqRatioGroupCollection(Flamable aFlamable, Connection aConn, Long aDatFileid, double aThreshold){
        Mdf_iTraqReader lReader = new Mdf_iTraqReader(iOriginalDatFile, iMergeFile, aFlamable, aConn, aDatFileid);
        lReader.setThreshold(aThreshold);
        return lReader.getRatioGroupCollection();
    }

    /**
     * This method extract the DatfilePeptideIdentification from the MascotDatfile
     * @param lThreshold The threshold
     * @return Vector<DatfilePeptideIdentification> Vector with DatfilePeptideIdentification
     */
    public HashMap<Integer, PeptideIdentification> extractDatfilePeptideIdentification(double lThreshold){
        //check if the datfile has been parsed
        if(iMascotDatFile == null){
            this.readMascotDatFile();
        }

        // Vector that will contain the DatfilePeptideIdentification instances.
        HashMap<Integer, PeptideIdentification> result = new HashMap<Integer, PeptideIdentification>();
        try{

            // Get the generic parameters for the search,
            // Extract the db filename and the Mascot version.
            Header header = iMascotDatFile.getHeaderSection();
            String version = header.getVersion();
            String dbfilename = header.getRelease();
            Parameters parameters = iMascotDatFile.getParametersSection();
            String searchTitle = parameters.getCom();
            if(searchTitle == null) {
                searchTitle = "!No title specified";
            } else {
                int location = searchTitle.indexOf("|");
                if(location >= 0) {
                    searchTitle = searchTitle.substring(0, location).trim();
                }
            }
            String inputfile = parameters.getFile();
            String dbName = parameters.getDatabase();
            ProteinMap proteinMap = iMascotDatFile.getProteinMap();
            Masses masses = iMascotDatFile.getMasses();

            // Rank of the hit (only highest ranking hits
            // (i.e.: rank = 1)) are considered,
            int rank = 1;

            // Get all the queries...
            Vector queries = iMascotDatFile.getQueryList();
            // Map to transfer query ID into peptidehits.
            QueryToPeptideMap_Index queryToPepMap = iMascotDatFile.getQueryToPeptideMap();
            Iterator iter = queries.iterator();
            int lQueryCounter = 0;
            while(iter.hasNext()) {
                // Get the query.
                Query query = (Query)iter.next();
                lQueryCounter++;
                // Get the first ranking peptide hit, if any.
                PeptideHit ph = queryToPepMap.getPeptideHitOfOneQuery(query.getQueryNumber(), rank);
                if(ph != null && ph.scoresAboveIdentityThreshold(lThreshold)) {
                    // We have a peptide hit for this query that scores equal
                    // to or above the threshold. Parse it and create a
                    // MascotIdentifiedSpectrum.
                    // Put all params in a HashMap with the correct keys.
                    // Query m/z and charge.
                    double mz = new BigDecimal(query.getPrecursorMZ()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                    String chargeString = query.getChargeString();
                    boolean isNegative = false;
                    int chargeLoc = chargeString.indexOf('+');
                    if(chargeLoc < 0) {
                        chargeLoc = chargeString.indexOf('-');
                        isNegative = true;
                    }
                    chargeString = chargeString.substring(0, chargeLoc);
                    int charge = Integer.parseInt(chargeString);
                    if(isNegative) {
                        charge = -charge;
                    }

                    String lModifiedSequence = ph.getModifiedSequence();
                    // If a modified sequence contain's a '#' character, this means the modification was not included in the modificationConversion.txt file.
                    // Throw an error since we don't want to have multiple names for identical modifications.
                    if(lModifiedSequence.indexOf('#') != -1){
                        //iFlamable.passHotPotato(new Throwable("ModificationConversion.txt does not contain enough information to parse the following identification:\n\t" + lModifiedSequence +"\nPlease add the modification into modificationconversions.txt. "));
                        //it's not such a big problem, the modified sequence will not look nice, but that is ok
                    }

                    HashMap hm = new HashMap();

                    hm.put("SCORE", new Long((long) ph.getIonsScore()));
                    hm.put("MODIFIED_SEQUENCE", new String(lModifiedSequence));
                    hm.put("CAL_MASS", ph.getPeptideMr());
                    hm.put("EXP_MASS", ph.getPeptideMr() + ph.getDeltaMass());
                    hm.put("SEQUENCE", new String(ph.getSequence()));
                    hm.put("HOMOLOGY", ph.getHomologyThreshold());
                    hm.put("VALID", new Integer(1));
                    hm.put("IDENTITYTHRESHOLD", new Long((long) ph.calculateIdentityThreshold(lThreshold)));
                    hm.put("CONFIDENCE", new Double(lThreshold));
                    hm.put("PRECURSOR",  mz);
                    hm.put("DB", new String(dbName));
                    hm.put("CHARGE",  charge);
                    hm.put("TITLE", new String(searchTitle));
                    hm.put("DB_FILENAME", new String(dbfilename));
                    hm.put("MASCOT_VERSION", new String(version));
                    hm.put("DATFILE_QUERY", new Long(lQueryCounter));
                    hm.put("SPECTRUM_FILE_NAME", new String(query.getTitle()));

                    // Protein stuff.

                    boolean firstProtein = true;
                    String lIsoforms = "";
                    Iterator iter2 = ph.getProteinHits().iterator();
                    while(iter2.hasNext()) {
                        ProteinHit protein = (ProteinHit)iter2.next();
                        // Hold the original accession to access
                        String originalAccession = protein.getAccession();
                        String trimmedAccession = originalAccession;
                        int startLoc = trimmedAccession.indexOf('(');
                        int endLoc = trimmedAccession.indexOf(')');
                        int tempStart = -1;
                        int tempEnd = -1;
                        if((startLoc >= 0) && (endLoc >= 0)) {
                            String tempLocalization = trimmedAccession.substring(startLoc+1, endLoc);
                            StringTokenizer lst = new StringTokenizer(tempLocalization, "-");
                            try {
                                tempStart = Integer.parseInt(lst.nextToken().trim());
                                tempEnd = Integer.parseInt(lst.nextToken().trim());
                                trimmedAccession = trimmedAccession.substring(0, startLoc).trim();
                            } catch(Exception e) {
                                // Do nothing.
                                // It's probably just not a location String.
                            }
                        }
                        // If no start and end location found, take those from the
                        // protein information supplied by Mascot.
                        if(tempStart < 0) {
                            tempStart = protein.getStart();
                            tempEnd = protein.getStop();
                        }
                        if(firstProtein){
                            hm.put("ACCESSION", new String(trimmedAccession));
                            hm.put("END", new Long(tempEnd));
                            hm.put("START", new Long(tempStart));
                            String lDescription = proteinMap.getProteinDescription(originalAccession);
                            hm.put("DESCRIPTION", new String(lDescription));
                            // Isolate the enzymatic part.
                            String descr = lDescription;
                            if(descr == null) {
                                descr = "No description found.";
                            } else if(descr.indexOf(";") >= 0) {
                                descr = descr.replace(';', '*');
                            }

                            int start = descr.indexOf("(*") + 2;
                            int end = descr.indexOf("*)");
                            if(start < 0 || end < 0) {
                                descr = "FE";
                            } else {
                                descr = descr.substring(start, end);
                            }
                            hm.put("ENZYMATIC", new String(descr));
                            firstProtein = false;
                        } else {
                            lIsoforms = lIsoforms + "^A" + trimmedAccession + " (" + tempStart + "-" + tempEnd + ")";
                        }

                    }
                    hm.put("ISOFORMS", new String(lIsoforms));

                    //add to the results
                    result.put(lQueryCounter, new DatfilePeptideIdentification(hm));
                }
            }
        } catch (Exception e){
            iFlamable.passHotPotato(new Throwable("Problem reading the dat file"));
            logger.error(e.getMessage(), e);
        }
        iMascotDatFile = null;
        //System.out.println("read file");
        System.gc();
        System.gc();
    	return result;
    }

}
