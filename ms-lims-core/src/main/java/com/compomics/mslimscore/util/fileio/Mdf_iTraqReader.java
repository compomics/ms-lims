/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.fileio;

/**
 *
 * @author Davy
 */
import com.compomics.mascotdatfile.util.mascot.*;
import com.compomics.mascotdatfile.util.mascot.enumeration.Mass;
import com.compomics.mascotdatfile.util.mascot.quantitation.Ratio;
import com.compomics.mascotdatfile.util.mascot.quantitation.Component;
import com.compomics.mslimscore.util.quantitation.ratios.RatioGroupCollection;
import com.compomics.mslimscore.util.quantitation.ratios.RatioGroup;
import com.compomics.mslimscore.util.quantitation.ratios.ITraqRatio;
import com.compomics.mslimscore.util.enumeration.QuantitationMetaType;
import com.compomics.mslimscore.util.enumeration.DataType;
import com.compomics.mslimscore.util.IdentificationExtension;
import com.compomics.mslimscore.util.interfaces.PeptideIdentification;
import com.compomics.util.interfaces.Flamable;

import java.io.*;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: niklaas
 * Date: 17-mrt-2009
 * Time: 8:05:27
 */

/**
 * This class will read a mascotdatfile and will create a RatioGroupCollection with iTRAQ ratios found in this mascot datfile.
 */
public class Mdf_iTraqReader {
	// Class specific log4j logger for Mdf_iTraqReader instances.
	 private static Logger logger = Logger.getLogger(Mdf_iTraqReader.class);
    /**
     * Boolean that indicates if the spectra must be found in the mgf merge file.
     */
    private boolean iUseMgfs;
    /**
     * The mgf merge file
     */
    private File iMgf;
    /**
     * Hashmap with spectra parsed from the mgf mergefile.
     * The spectrum title is the key, and an array of Peaks is the value.
     */
    private HashMap iSpectra = new HashMap();
    /**
     * The RatioGroupCollection where all the ratios, hits, ... will be stored in
     */
    private RatioGroupCollection iRatioGroupCollection;
    /**
     * The ratiotypes (115/114, ...)
     */
    private Ratio[] iRatioTypes;
    /**
     * The threshold
     */
    private double iThreshold;
    /**
     * A vector with the DatfilePeptideIdentfications found in the mascot datfile
     */
    private HashMap<Integer,PeptideIdentification> iPeptideIdentifications;
    /**
     * The type of quantitation used (ex. iTRAQ 4plex)
     */
    private String iQuantitationType;
    /**
     * Connection to the ms_lims database to retrieve the peptide identification
     * if the connection is null, the peptide identification wi
     */
    private Connection iConn;
    /**
     * The id from the datfile in the ms_lims database
     */
    private long iDatabaseDatfileid;
    /**
     * Boolean that indicates if the peptide identifications must be retrieved from the ms_lims database.
     */
    private boolean iUseMs_lims;
    /**
     * The flamable
     */
    private Flamable iFlamable;
    /**
     * The components (114, 115, ...)
     */
    private Component[] iComponents;
    /**
     * The datfile
     */
    private DatFile iDatFile;

    /**
     * Constructor
     * @param aMdf A mascot datfile
     * @param aMgf A mgf mergefile (can be null)
     * @param aConn Connection to the ms_lims database
     * @param aDatfileid The id form the datfile in the ms_lims database
     */
    public Mdf_iTraqReader(File aMdf, File aMgf, Flamable aFlamable, Connection aConn, Long aDatfileid){
        this.iFlamable = aFlamable;

        //set the connection to ms_lims
        if(aConn != null){
            iConn = aConn;
            iDatabaseDatfileid = aDatfileid;
            iUseMs_lims = true;
        }
        //create the mascot dat file
        this.iDatFile = new DatFile(aMdf, iFlamable);
        //set the mgf mergefile
        if(aMgf == null){
            //the mgf file will not be used
            this.iUseMgfs = false;
        } else {
            //the mgf file will be used
            this.iUseMgfs = true;
            this.iMgf = aMgf;
        }
    }

    /**
     * Constructor
     * @param aMdf A mascot datfile
     * @param aMgf A mgf mergefile (can be null)
     */
    public Mdf_iTraqReader(File aMdf, File aMgf, Flamable aFlamable){
        this(aMdf, aMgf, aFlamable, null, null);
    }


    /**
     * This method creates the ratiogroupcollection and set the necissary elements in this ratiogroupcollection
     */
    public void createRatioGroupCollection(){
        //create the ratiogroupcollection
        iRatioGroupCollection = new RatioGroupCollection(DataType.ITRAQ_DAT);
        //get the component types
        iComponents = this.getComponents();
        if(iComponents == null){
            //no qaunt data
            iRatioGroupCollection = null;
            return;
        }
        Vector<String> lCompToSet = new Vector<String>();
        for(int i = 0; i< iComponents.length; i ++){
            lCompToSet.add(iComponents[i].getName());
        }
        //set the components in the ratiogroupcollection
        iRatioGroupCollection.setComponentTypes(lCompToSet);
        //get the ratio types
        iRatioTypes = this.getRatioTypes();
        Vector<String> lRatioToSet = new Vector<String>();
        for(int i = 0; i< iRatioTypes.length; i ++){
            lRatioToSet.add(iRatioTypes[i].getName());
        }
        //set the ratiotypes in the ratiogroupcollection
        iRatioGroupCollection.setRatioTypes(lRatioToSet);
        //if we are connected to ms_lims we will set the datfileid
        if(iUseMs_lims){
            iRatioGroupCollection.putMetaData(QuantitationMetaType.DATFILEID, iDatabaseDatfileid);
        }
        //add the filename to the collection
        iRatioGroupCollection.putMetaData(QuantitationMetaType.FILENAME, iDatFile.getMascotDatFile().getFileName());
    }


    /**
     * This method will get the ratiotypes from the mdf
     * @return Ratio[] with the ratiotypes
     */
    public Ratio[] getRatioTypes(){
        Quantitation lQuant;
        try{
            lQuant = iDatFile.getMascotDatFile().getQuantitation();
        } catch (NullPointerException e){
            iFlamable.passHotPotato(new Throwable("No quantitative information was found in the datfile '" + iDatFile.getMascotDatFile().getFileName() + "'."));
            logger.error(e.getMessage(), e);
            return null;
        }
        Ratio[] lRatios = lQuant.getRatios();
        return lRatios;
    }

    /**
     * This method will get the components from the mdf
     * @return Component[] with the components
     */
    public Component[] getComponents(){
        Quantitation lQuant;
        try{
            lQuant = iDatFile.getMascotDatFile().getQuantitation();
        } catch (NullPointerException e){
            iFlamable.passHotPotato(new Throwable("No quantitative information was found in the datfile '" + iDatFile.getMascotDatFile().getFileName() + "'."));
            logger.error(e.getMessage(), e);
            return null;
        }
        Component[] lComp = lQuant.getComponents();
        return lComp;
    }

    /**
     * This method will get the peptide identifications from the datfile or the database
     * @return Vector<PeptideIdentification>
     */
    public HashMap<Integer, PeptideIdentification> getPeptideIdentifiations(){
        //create the vector with peptide identifications
        HashMap<Integer, PeptideIdentification> lIdentifications = null;

        //if we use ms_lims we will get the identifications from the database, otherwise we will get them from the datfile
        if(iUseMs_lims){
            try {
                lIdentifications = IdentificationExtension.getIdentificationExtensions(iConn, " i.l_datfileid = " + iDatabaseDatfileid);
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            lIdentifications =  iDatFile.extractDatfilePeptideIdentification(iThreshold);
        }
        return lIdentifications;
    }


    /**
     * This method will get the data from the mdf and store the RatioGroups in the RatioGroupCollection
     */
    public void readMdf(){

        //get all the peptide identifications for the datfile
        iPeptideIdentifications = this.getPeptideIdentifiations();
        //get the quantitationtype from the parameter section in the mascot datfile
        Parameters lParam = iDatFile.getMascotDatFile().getParametersSection();
        iQuantitationType = lParam.getQuantiation();

        //check if the quantitation type is itraq
        if(iQuantitationType.toLowerCase().indexOf("tmt") == -1 && iQuantitationType.toLowerCase().indexOf("itraq") == -1){
            iFlamable.passHotPotato(new Throwable("No correct quantification information could be extracted from the datfile '" + iRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME) + "'! The quantitation type was: " + iQuantitationType + "."));
            return;
        }

        //get the mass type
        String lMassString = lParam.getMass();
        Mass lMass = null;
        for (Mass lMassEnum : Mass.values()) {
            if(lMassEnum.toString().equalsIgnoreCase(lMassString)){
                lMass = lMassEnum;
            }
        }
        //get the fragment ion tolerance
        double lMassTolerance = Double.valueOf(lParam.getITOL());

        // Get all the queries
        Vector queries = iDatFile.getMascotDatFile().getQueryList();
        // Create the iterator
        Iterator iter = queries.iterator();
        while (iter.hasNext()) {
            // Get the query.
            Query lQuery = (Query) iter.next();
            if(iUseMgfs){
                //the peaks are not in the mdf, get them from the mergefile
                // we will search in the spectra hashmap for the peaks that are linked to the same component as this query
                Peak[] lPeaks = (Peak[]) iSpectra.get(this.getComponentNumberFromTitle(lQuery.getTitle()));
                lQuery.setPeakList(lPeaks);
            }

            //create the ratiogroup
            RatioGroup lRatioGroup = new RatioGroup(iRatioGroupCollection);
            //add an peptide identification to the group

            PeptideIdentification lIdent = iPeptideIdentifications.get(lQuery.getQueryNumber());
            if(lIdent != null){
                if(lQuery.getQueryNumber() == lIdent.getDatfile_query()){
                    //the query number matches the query number from the peptide identification
                    //set the quantitation type
                    lIdent.setType(iQuantitationType);
                    //add the identificatoin to the ratiogroup
                    lRatioGroup.addIdentification(lIdent, iQuantitationType);
                    //add the different ratios
                    for(int j = 0; j<iRatioTypes.length; j ++){
                        Ratio lMDFRatio = iRatioTypes[j];
                        double lRatioDouble = lMDFRatio.calculate(lQuery.getPeakList(), lMassTolerance , lMass);
                        if(lRatioDouble >= 0.0){
                            ITraqRatio lRatio = new ITraqRatio(lRatioDouble, lMDFRatio.getName(), true, lRatioGroup);
                            lRatioGroup.addRatio(lRatio);
                        }
                    }
                    //set the peptide sequence
                    lRatioGroup.setPeptideSequence(lIdent.getSequence());
                    //add the ratiogroup to the ratiogroup collection
                    if(lRatioGroup.getNumberOfRatios() > 0){
                        iRatioGroupCollection.add(lRatioGroup);
                    }
                }
            }

        }
        //check if we have added anything
        if(iRatioGroupCollection.size() == 0){
            iFlamable.passHotPotato(new Throwable("No information could be extracted from the datfile '" + iRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME) + "' !"));
        }
    }

    /**
     * This method gets the component number from a Query parsed title.
     * @return
     */
    public int getComponentNumberFromTitle(String aTitle){
        String lTemp = aTitle.substring(aTitle.indexOf("_") + 1 , aTitle.indexOf("_", aTitle.indexOf("_") + 1));
        return Integer.valueOf(lTemp);
    }

    /**
     * This method will read the mgf mergefile. It will store the find spectra in a hashmap.
     * In that hashmap the key is the spectrum title and the value is an array of Peaks.
     */
    public void readMergeFile(){
        try {
            String iCurrentSpectrumTitle = null;
            String iCurrentSpectrumScans = null;
            String iCurrentCharge = null;
            Vector<Peak> lPeaks = new Vector<Peak>();

            if(!iMgf.exists()) {
                throw new IOException("Mergefile '" + iMgf.getCanonicalPath() + "' could not be found!");
            } else {

                BufferedReader br = new BufferedReader(new FileReader(iMgf));

                // First parse the header.
                // First (non-empty?) line can be CHARGE= --> omit it if present.
                // Next up are comment blocks.
                // First non-empty comment line is raw filename (fully qualified).
                // Second non-empty comment line holds the run title.
                // Rest holds additional info which will be stored in the 'iComments' variable.
                // First comment line without spaces after the '###' is part of the first spectrum.
                String line = null;
                int commentLineCounter = 0;
                int lineCounter = 0;
                boolean inSpectrum = false;
                boolean lMultiFile = false;
                Vector<String> lMultiRawFileNames = new Vector<String>();
                StringBuffer tempComments = new StringBuffer();

                // Cycle the file.
                boolean runnameNotYetFound = true;
                while((line = br.readLine()) != null) {
                    lineCounter++;
                    line = line.trim();
                    // Skip empty lines and file-level charge statement.
                    if(line.equals("") || (lineCounter == 1 && line.startsWith("CHARGE"))) {
                        continue;
                    }
                    // Comment lines.
                    else if(line.startsWith("#") && !inSpectrum) {
                        // First strip off the comment markings in a new String ('cleanLine').
                        String cleanLine = cleanCommentMarks(line);
                        // If cleanLine trimmed is empty String, it's an empty comment line
                        // and therefore skipped without counting.
                        String cleanLineTrimmed = cleanLine.trim();
                        if(cleanLineTrimmed.equals("")) {
                            continue;
                        }
                        // If it is not empty String, yet starts with a space (note that we verify
                        // using the untrimmed cleanLine!), it is a header
                        // comment, so we start by counting it!
                        else if(cleanLine.startsWith(" ") || cleanLine.startsWith("\t")) {
                            commentLineCounter++;
                            // Every non-empty comment line is added to the tempComments
                            // StringBuffer, the contents of which are afterwards copied into
                            // the 'iComments' variable.
                            tempComments.append(line + "\n");
                        }
                        // Spectrum comment. Start a new Spectrum!
                        else {
                            inSpectrum = true;
                        }
                    }
                    // It could that multiple raw files were used to create the mergefile. If there are different lines with something like
                    //'_DISTILLER_RAWFILE[0]={1}C:\Users\mascot\...\QstarE04494.wiff' this is the case
                    else if(line.startsWith("_DISTILLER_RAWFILE")){
                        lMultiRawFileNames.add(line.substring(line.lastIndexOf("}")));
                    }
                    
                    // Not an empty line, not an initial charge line, not a comment line and inside a spectrum.
                    else if(inSpectrum) {

                        // Keep track of the 'TITLE' value for further usage in the filename creation.
                        // Note that this is the only difference with the parent.
                        if(line.indexOf("TITLE") >= 0){
                            iCurrentSpectrumTitle = line;
                            if(lMultiFile){
                                //check if this is linked to two files
                                String lTemp = iCurrentSpectrumTitle.substring(iCurrentSpectrumTitle.lastIndexOf("[") + 1, iCurrentSpectrumTitle.lastIndexOf("]"));
                                if(lTemp.indexOf(",") > 0){
                                    //linked to multiple files
                                    iCurrentSpectrumTitle = iCurrentSpectrumTitle.substring(0,iCurrentSpectrumTitle.lastIndexOf(lTemp) + (lTemp.substring(0, lTemp.indexOf(","))).length() )  + "]";
                                }
                            }
                        }
                        // Keep track of the 'SCANS' value for further usage in the filename creation.
                        if(line.indexOf("SCANS") >= 0){
                            iCurrentSpectrumScans = line;
                        }

                        // Keep track of the 'CHARGE' value for further usage in the filename creation.
                        if(line.indexOf("CHARGE") >= 0){
                            iCurrentCharge = line.substring(line.indexOf("=") + 1);
                        }

                        //Try to find the peaks
                        if(line.indexOf("\t")>0){
                            //we found a tab, we are in the peaklist
                            //get the mass
                            double lMass = Double.valueOf(line.substring(0, line.indexOf("\t")));
                            //get the intensity
                            double lIntensity = Double.valueOf(line.substring(line.indexOf("\t") + 1));
                            //add a new peak to the peak list
                            lPeaks.add(new Peak(lMass, lIntensity));
                        }

                        // See if it was an 'END IONS', in which case we stop being in a spectrum.
                        if(line.indexOf("END IONS") >= 0) {
                            // End detected. Much to do!
                            // Reset boolean.
                            inSpectrum = false;
                            String[] lMultiFileNameArray = new String[lMultiRawFileNames.size()];
                            lMultiRawFileNames.toArray(lMultiFileNameArray);

                            if(lMultiFileNameArray.length > 1){
                                lMultiFile = true;
                            }

                            // Create a filename for the spectrum, based on the filename of the mergefile, with
                            // an '_[spectrumCounter]' before the extension (eg., myParent.mgf --> myParent_1.mgf).
                            String spectrumFilename;
                            //the spectrum is fully parsed
                            //create a peak array
                            Peak[] lPeakArray = new Peak[lPeaks.size()];
                            lPeaks.toArray(lPeakArray);
                            try{
                                spectrumFilename = Query.processMGFTitleToFilename(iCurrentSpectrumTitle, lMultiFile, lMultiFileNameArray, iCurrentSpectrumScans, iCurrentCharge);
                                iSpectra.put(this.getComponentNumberFromTitle(spectrumFilename), lPeakArray);
                            } catch(Exception e){
                                spectrumFilename = iCurrentSpectrumTitle;
                                iSpectra.put(spectrumFilename.substring(spectrumFilename.indexOf("=") + 1, spectrumFilename.indexOf(":")), lPeakArray);
                            }
                            //add a new spectrum to the hashmap
                            //remove everything from the vector and delete the title
                            iCurrentCharge = null;
                            iCurrentSpectrumScans = null;
                            iCurrentSpectrumTitle = null;
                            lPeaks.removeAllElements();
                            inSpectrum = false;
                        }
                    }
                    // If we're not in a spectrum, see if the line is 'BEGIN IONS', which marks the begin of a spectrum!
                    else if(line.indexOf("BEGIN IONS") >= 0){
                        inSpectrum = true;
                    }
                }

                br.close();
            }
        } catch (FileNotFoundException e) {
            iFlamable.passHotPotato(new Throwable("Problem reading '" + iMgf.getAbsolutePath() + "' !"));
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            iFlamable.passHotPotato(new Throwable("Problem reading '" + iMgf.getAbsolutePath() + "' !"));
            logger.error(e.getMessage(), e);
        }
    }


    public String cleanCommentMarks(String aCommentLine) {
        StringBuffer result = new StringBuffer(aCommentLine);
        while (result.length() > 0 && result.charAt(0) == '#') {
            result.deleteCharAt(0);
        }
        return result.toString();
    }

    /**
     * This method sets the threshold
     * @param aThreshold double with the threshold
     */
    public void setThreshold(double aThreshold){
        iThreshold = aThreshold;
    }

    /**
     * The getter for the RatioGroupCollection
     * @return RatioGroupCollection
     */
    public RatioGroupCollection getRatioGroupCollection() {
        if(iRatioGroupCollection == null){
            if(iUseMgfs){
                this.readMergeFile();
            }
            this.createRatioGroupCollection();
            if(iRatioGroupCollection == null){
                return null;
            }
            this.readMdf();
        }
        return iRatioGroupCollection;
    }
}
