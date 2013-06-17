/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.quantitation;

import com.compomics.mslimscore.util.enumeration.MaxQuantScoreType;
import com.compomics.mslimscore.util.enumeration.ProteinDatabaseType;
import com.compomics.mslimscore.util.enumeration.QuantitationSource;
import com.compomics.mslimscore.util.enumeration.ReferenceSetEnum;
import com.compomics.mslimscore.util.interfaces.Ratio;
import com.compomics.mslimscore.util.quantitation.ratios.QuantitativeProtein;
import com.compomics.mslimscore.util.quantitation.ratios.RatioGroupCollection;
import com.compomics.mslimscore.util.quantitation.ratios.RatioType;
import com.compomics.mslimscore.util.quantitation.ratios.ReferenceSet;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.math.MathException;
import org.apache.commons.math.special.Erf;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class QuantitativeValidationSingleton {
    // Class specific log4j logger for QuantitativeValidationSingelton instances.

    private static Logger logger = Logger.getLogger(QuantitativeValidationSingleton.class);
    /**
     * Singelton instance
     */
    private static QuantitativeValidationSingleton ourInstance = new QuantitativeValidationSingleton();
    /**
     * Boolean that says if non valid ratios should be used in the calculation
     * of the protein mean
     */
    private boolean iUseOnlyValidRatioForProteinMean = false;
    /**
     * Boolean that says if only uniquely identified peptide ratios should be
     * used in the calculation of the protein mean
     */
    private boolean iUseOnlyUniqueRatioForProteinMean = false;
    private boolean iUseOriginalRatio = false;
    /**
     * Boolean that says if a ratio or the log2 of that ratio should be given
     */
    private boolean iLog2 = false;
    /**
     * This vector with Ratio stores the validated ratios
     */
    private Vector<Ratio> iValidatedRatios = new Vector<Ratio>();
    /**
     * This vector holds the selected QuantiativeProtein
     */
    private Vector<QuantitativeProtein> iSelectedProteins = new Vector<QuantitativeProtein>();
    /**
     * This vector holds the commented QuantiativeProtein
     */
    private Vector<QuantitativeProtein> iCommentedProteins = new Vector<QuantitativeProtein>();
    /**
     * This vector holds the validated QuantiativeProtein
     */
    private Vector<QuantitativeProtein> iValidatedProteins = new Vector<QuantitativeProtein>();
    /**
     * This vector holds the validated QuantiativeProtein
     */
    private Vector<QuantitativeProtein> iAllProteins = new Vector<QuantitativeProtein>();
    /**
     * This is the database type (Uniprot, ipi, ncbi)
     */
    private ProteinDatabaseType iDatabaseType;
    /**
     * This boolean says if the ratios used in the reference set must be valid.
     * If it's true, the ratios must be valid
     */
    private boolean iRatioValidInReferenceSet = false;
    /**
     * The right border of the graph. This is also the end for the colored
     * gradient in the protein bar
     */
    private int iRightGraphBorder = 2;
    /**
     * The left border of the graph. This is also the start for the colored
     * gradient in the protein bar
     */
    private int iLeftGraphBorder = -2;
    /**
     * The reference set
     */
    private ReferenceSet iReferenceSet;
    /**
     * The calibrated standard deviation for log2 scale ratios for 1/1 ratio
     * mixtures on the mass spectrometer.
     */
    private double iCalibratedStdev = 0.238714;
    /**
     * This vector will hold protein accessions for the not used peptides in the
     * calculation of the protein mean
     */
    private Vector<String> iNotUsedProteins = new Vector<String>();
    /**
     * This vector will hold peptide sequences. This ratios linked to the
     * peptide sequences will not be used in the calculation of the protein
     * mean. (The protein accession are stored in the iNotUsedProteins vector)
     */
    private Vector<String> iNotUsedPeptides = new Vector<String>();
    /**
     * Enum that indicates the reference set type
     */
    private ReferenceSetEnum iProteinsReferenceSetType;
    /**
     * The number of proteins that will be used for the creation of the
     * reference set
     */
    private int iNumberOfProteinsInReferenceSet;
    /**
     * The rover sources
     */
    private Vector<QuantitationSource> iQuantitationSources = new Vector<QuantitationSource>();
    /**
     * The max quant score type
     */
    private MaxQuantScoreType iMaxQuantScoreType = MaxQuantScoreType.RATIO;
    /**
     * The location where the file chooser should open
     */
    private String iFileLocationOpener;
    /**
     * The ratio types
     */
    private Vector<String> iRatioTypes;
    /**
     * The component types
     */
    private Vector<String> iComponentsTypes;
    /**
     * the matched ratio types
     */
    private Vector<RatioType> iMatchedRatioTypes = new Vector<RatioType>();
    /**
     * The rover sources of the original selection of files
     */
    private Vector<QuantitationSource> iOriginalQuantitationSources;
    /**
     * boolean that indicate if multiple sources are used
     */
    private boolean iMultipleSources = false;
    /**
     * Vector with the titles for the different sources
     */
    private Vector<String> iTitlesForDifferentSources;
    /**
     * Map with the protein accessions and the sequences
     */
    private HashMap iSequenceMap = new HashMap();
    /**
     * Vector with the RatioGroupCollection
     */
    private Vector<RatioGroupCollection> iOriginalCollections;
    /**
     * Vector with protein accessions
     */
    private Vector<String> iProteinAccessions;
    /**
     * Vector with booleans that indicate if the corresponding indexes are used
     * in the visualization
     */
    private Vector<Boolean> iSelectedIndexes;
    /**
     * String with the location of the fasta database
     */
    private String iFastaDatabaseLocation;
    /**
     * boolean that indicates if normalization was performed
     */
    private boolean iNormalization = false;
    private boolean iMsLimsPre7_2 = false;
    private boolean iMsLimsPre7_6 = false;
    /**
     * The special accessions for the reference set, these can only be taken to
     * create the reference set, or these can be excluded from the reference
     * set, depending on the reference set enumeration
     */
    private String iReferenceSetSpecialAccessions = "";
    /**
     * boolean that indicates if the peptizer invalid peptide identification
     * should be excluded
     */
    private boolean iExcludePeptizerUnvalid = false;

    public static QuantitativeValidationSingleton getInstance() {
        return ourInstance;
    }

    private QuantitativeValidationSingleton() {
    }

    /**
     * Getter for useOnlyValidRatioForProteinMean
     *
     * @return boolean
     */
    public boolean isUseOnlyValidRatioForProteinMean() {
        return iUseOnlyValidRatioForProteinMean;
    }

    /**
     * Getter for useOnlyUniqueRatioForProteinMean
     *
     * @return boolean
     */
    public boolean isUseOnlyUniqueRatioForProteinMean() {
        return iUseOnlyUniqueRatioForProteinMean;
    }

    /**
     * Setter for useOnlyValidRatioForProteinMean
     *
     * @param aUseOnlyValidRatioForProteinMean
     */
    public void setUseOnlyValidRatioForProteinMean(boolean aUseOnlyValidRatioForProteinMean) {
        this.iUseOnlyValidRatioForProteinMean = aUseOnlyValidRatioForProteinMean;
    }

    /**
     * Setter for useOnlyUniqueRatioForProteinMean
     *
     * @param aUseOnlyUniqueRatioForProteinMean
     */
    public void setUseOnlyUniqueRatioForProteinMean(boolean aUseOnlyUniqueRatioForProteinMean) {
        this.iUseOnlyUniqueRatioForProteinMean = aUseOnlyUniqueRatioForProteinMean;
    }

    /**
     * Getter for log2
     *
     * @return boolean
     */
    public boolean isLog2() {
        return iLog2;
    }

    /**
     * Setter for log2 boolean
     *
     * @param aLog2
     */
    public void setLog2(boolean aLog2) {
        this.iLog2 = aLog2;
    }

    /**
     * Method that indicates if we are connected to ms_lims
     *
     * @return boolean
     */
    public boolean isDatabaseMode() {

        boolean lDatabaseMode = false;
        for (int i = 0; i < iQuantitationSources.size(); i++) {
            QuantitationSource lQuantitationSource = iQuantitationSources.get(i);
            if (lQuantitationSource == QuantitationSource.THERMO_MSF_LIMS || lQuantitationSource == QuantitationSource.ITRAQ_MS_LIMS || lQuantitationSource == QuantitationSource.DISTILLER_QUANT_TOOLBOX_MS_LIMS || lQuantitationSource == QuantitationSource.MAX_QUANT_MS_LIMS) {
                lDatabaseMode = true;
            }
        }
        return lDatabaseMode;
    }

    /**
     * This method will add a validate ratio
     *
     * @param aRatio Ratio to add
     */
    public void addValidatedRatio(Ratio aRatio) {
        if (!iValidatedRatios.contains(aRatio)) {
            iValidatedRatios.add(aRatio);
        }
    }

    /**
     * Getter for the validated ratios
     *
     * @return Vector<Ratio>
     */
    public Vector<Ratio> getValidatedRatios() {
        return iValidatedRatios;
    }

    /**
     * This method will unzip the specified zip entry from the specified zip
     * input stream and store it relative to the specified parent folder. It
     * will handle both folders and files.
     *
     * @param aParent File with the parent folder to place the unzipped files
     * relative to.
     * @param ze ZipEntry to unzip from the stream.
     * @param aZis ZipInputStream to read the unzipped bytes for the entry from.
     * @throws java.io.IOException when the unzipping failed.
     */
    public static void unzipEntry(File aParent, ZipEntry ze, ZipInputStream aZis) throws IOException {
// Get the name for the zip entry. This name contains the relative path.
        String name = ze.getName();
// If the entry is a directory, create it if it does not already exists.
        if (ze.isDirectory()) {
            File dir = new File(aParent, name);
// Only attempt to create it if it doesn't exist already.
            if (!dir.exists()) {
                boolean result = dir.mkdirs();
// Check for successful creation.
                if (!result) {
                    throw new IOException("Creation of directory '" + dir.getAbsolutePath() + "' failed miserably!");
                }
            }
        } else {
            File file = new File(aParent, name);
// If the file already exists, break with an IOException!
            if (file.exists()) {
                throw new IOException("File '" + file.getAbsolutePath() + "' already exists! Aborting unzip operation!");
            }
// Okay, first create the file.
            boolean result = file.createNewFile();
// Check to see if the creation worked.
            if (!result) {
                throw new IOException("Creation of file '" + file.getAbsolutePath() + "' failed miserably!");
            }
// Now write the unzipped contents to the output file.
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            byte[] read = new byte[1024];
            int readBytes = 0;
            while ((readBytes = aZis.read(read)) != -1) {
                if (readBytes < 1024) {
                    bos.write(read, 0, readBytes);
                } else {
                    bos.write(read);
                }
            }
            bos.flush();
            bos.close();
        }
    }

    /**
     * This method will add a selected protein
     *
     * @param aProtein
     */
    public void addSelectedProtein(QuantitativeProtein aProtein) {
        if (!iSelectedProteins.contains(aProtein)) {
            iSelectedProteins.add(aProtein);
        }
    }

    /**
     * Getter for the selected proteins
     *
     * @return Vector<QuantitativeProtein>
     */
    public Vector<QuantitativeProtein> getSelectedProteins() {
        return iSelectedProteins;
    }

    /**
     * This method will remove all the selected proteins
     */
    public void removeAllSelectedProteins() {
        iSelectedProteins.removeAllElements();
    }

    /**
     * This method will add a validated
     *
     * @param aProtein
     */
    public void addValidatedProtein(QuantitativeProtein aProtein) {
        if (!iValidatedProteins.contains(aProtein)) {
            iValidatedProteins.add(aProtein);
        }
    }

    /**
     * Getter for the validated proteins
     *
     * @return Vector<QuantitativeProtein>
     */
    public Vector<QuantitativeProtein> getValidatedProteins() {
        return iValidatedProteins;
    }

    /**
     * Remove a protein from the validated proteins
     *
     * @param aProtein The protein that will be removed from the validated
     * vector
     */
    public void removeValidatedProtein(QuantitativeProtein aProtein) {
        iValidatedProteins.remove(aProtein);
    }

    /**
     * Getter for the commented proteins
     *
     * @return Vector<QuantitativeProtein>
     */
    public Vector<QuantitativeProtein> getAllProteins() {
        return iAllProteins;
    }

    /**
     * Setter for the commented proteins
     */
    public void setAllProteins(Vector<QuantitativeProtein> lAllProteins) {
        iAllProteins = lAllProteins;
    }

    /**
     * Getter for the commented proteins
     *
     * @return Vector<QuantitativeProtein>
     */
    public Vector<QuantitativeProtein> getCommentedProteins() {
        return iCommentedProteins;
    }

    /**
     * Remove a protein from the commented proteins
     *
     * @param aProtein The protein that will be removed from the commented
     * vector
     */
    public void removeCommentedProtein(QuantitativeProtein aProtein) {
        iCommentedProteins.remove(aProtein);
    }

    /**
     * Add a commented protein
     *
     * @param aProtein That will be added
     */
    public void addCommentedProtein(QuantitativeProtein aProtein) {
        if (!iCommentedProteins.contains(aProtein)) {
            iCommentedProteins.add(aProtein);
        }
    }

    /**
     * This method will save a .ROVER file
     *
     * @param aFileLocation Location to save to
     * @throws IOException
     */
    /**
     * Is the loaded data from Mascot Distiller
     *
     * @return boolean that indicates the status
     */
    public boolean isDistillerQuantitation() {
        boolean lDistillerQuantitation = false;
        for (int i = 0; i < iQuantitationSources.size(); i++) {
            QuantitationSource lQuantitationSource = iQuantitationSources.get(i);
            if (lQuantitationSource == QuantitationSource.DISTILLER_QUANT_TOOLBOX_MS_LIMS || lQuantitationSource == QuantitationSource.DISTILLER_QUANT_TOOLBOX_ROV) {
                lDistillerQuantitation = true;
            }
        }
        return lDistillerQuantitation;
    }

    /**
     * Is the loaded data from MaxQuant
     *
     * @return boolean that indicates the status
     */
    public boolean isMaxQuantQuantitation() {
        boolean lMaxQuantQuantitation = false;
        for (int i = 0; i < iQuantitationSources.size(); i++) {
            QuantitationSource lQuantitationSource = iQuantitationSources.get(i);
            if (lQuantitationSource == QuantitationSource.MAX_QUANT || lQuantitationSource == QuantitationSource.MAX_QUANT_NO_SIGN || lQuantitationSource == QuantitationSource.MAX_QUANT_MS_LIMS) {
                lMaxQuantQuantitation = true;
            }
        }
        return lMaxQuantQuantitation;
    }

    /**
     * Is the loaded data from MaxQuant without Sign A and Sign B
     *
     * @return boolean that indicates the status
     */
    public boolean isMaxQuantQuantitationWithoutSign() {
        boolean lMaxQuantQuantitation = false;

        for (int i = 0; i < iQuantitationSources.size(); i++) {
            QuantitationSource lQuantitationSource = iQuantitationSources.get(i);
            if (lQuantitationSource == QuantitationSource.MAX_QUANT_NO_SIGN) {
                lMaxQuantQuantitation = true;
            }
        }
        return lMaxQuantQuantitation;
    }

    /**
     * Getter for the database type
     *
     * @return ProteinDatabaseType
     */
    public ProteinDatabaseType getDatabaseType() {
        return iDatabaseType;
    }

    /**
     * Setter for the database type
     *
     * @param aDatabaseType The database type
     */
    public void setDatabaseType(ProteinDatabaseType aDatabaseType) {
        this.iDatabaseType = aDatabaseType;
    }

    /**
     * Getter for the boolean that indicates if the calculations in the
     * reference set only takes valid ratios
     *
     * @return boolean
     */
    public boolean isRatioValidInReferenceSet() {
        return iRatioValidInReferenceSet;
    }

    /**
     * Setter for the status RatioValidInReferenceSet
     *
     * @param aRatioValidInReferenceSet
     */
    public void setRatioValidInReferenceSet(boolean aRatioValidInReferenceSet) {
        iRatioValidInReferenceSet = aRatioValidInReferenceSet;
    }

    /**
     * Getter for the right graph border
     *
     * @return int with the right border
     */
    public int getRightGraphBorder() {
        return iRightGraphBorder;
    }

    /**
     * Setter for the right graph border
     *
     * @param iRightGraphBorder Int with the border value
     */
    public void setRightGraphBorder(int iRightGraphBorder) {
        this.iRightGraphBorder = iRightGraphBorder;
    }

    /**
     * Getter for the left graph border
     *
     * @return int with the left border
     */
    public int getLeftGraphBorder() {
        return iLeftGraphBorder;
    }

    /**
     * Setter for the left graph border
     *
     * @param iLeftGraphBorder Int with the border value
     */
    public void setLeftGraphBorder(int iLeftGraphBorder) {
        this.iLeftGraphBorder = iLeftGraphBorder;
    }

    /**
     * Getter for the reference set
     *
     * @return ReferenceSet
     */
    public ReferenceSet getReferenceSet() {
        return iReferenceSet;
    }

    /**
     * Setter for the reference set
     *
     * @param aReferenceSet Reference set tot set
     */
    public void setReferenceSet(ReferenceSet aReferenceSet) {
        this.iReferenceSet = aReferenceSet;
    }

    /**
     * Getter for the calibrated stDev
     *
     * @return double with the calibrated stDev
     */
    public double getCalibratedStdev() {
        return iCalibratedStdev;
    }

    /**
     * Setter for the calibrated stDev
     *
     * @param aCalibratedStdev double with the stDev to set
     */
    public void setCalibratedStdev(double aCalibratedStdev) {
        this.iCalibratedStdev = aCalibratedStdev;
    }

    /**
     * This method will delete the protein and peptide from the not used in
     * protein mean calculation vector.
     *
     * @param accession String with the accession
     * @param peptideSequence String with the peptide sequence
     */
    public void deleteNotUsedPeptide(String accession, String peptideSequence) {
        for (int i = 0; i < iNotUsedProteins.size(); i++) {
            if (iNotUsedProteins.get(i).equalsIgnoreCase(accession) && iNotUsedPeptides.get(i).equalsIgnoreCase(peptideSequence)) {
                iNotUsedPeptides.remove(i);
                iNotUsedProteins.remove(i);
            }
        }
    }

    /**
     * This method will add a protein and peptide to the not used in protein
     * mean calculation vector.
     *
     * @param accession String with the accession
     * @param peptideSequence String with the peptide sequence
     */
    public void addNotUsedPeptide(String accession, String peptideSequence) {
        iNotUsedPeptides.add(peptideSequence);
        iNotUsedProteins.add(accession);
    }

    /**
     * Getter for the not used proteins
     *
     * @return Vector<String> with protein accessions
     */
    public Vector<String> getNotUsedProteins() {
        return iNotUsedProteins;
    }

    /**
     * Getter for the not used proteins
     *
     * @return Vector<String> with peptide sequences
     */
    public Vector<String> getNotUsedPeptides() {
        return iNotUsedPeptides;
    }

    /**
     * Setter for the ReferenceSetEnum that indicates the reference set
     *
     * @param lProteinsReferenceSetType ReferenceSetEnum to set
     */
    public void setReferenceSetEnum(ReferenceSetEnum lProteinsReferenceSetType) {
        this.iProteinsReferenceSetType = lProteinsReferenceSetType;
    }

    /**
     * This method gives a ReferenceSetEnum that indicates the reference set
     *
     * @return ReferenceSetEnum
     */
    public ReferenceSetEnum getReferenceSetEnum() {
        return iProteinsReferenceSetType;
    }

    /**
     * Getter for the number of proteins used in the reference set
     *
     * @return int with the number of proteins used
     */
    public int getNumberOfProteinsInReferenceSet() {
        return iNumberOfProteinsInReferenceSet;
    }

    /**
     * Setter for the number of proteins used in the reference set
     *
     * @param aNumberOfProteinsInReferenceSet int with the number to set
     */
    public void setNumberOfProteinsInReferenceSet(int aNumberOfProteinsInReferenceSet) {
        this.iNumberOfProteinsInReferenceSet = aNumberOfProteinsInReferenceSet;
    }

    /**
     * This method will give a boolean that indicates if we are working with
     * iTRAQ data
     *
     * @return boolean that indicates the status
     */
    public boolean isITraqData() {
        boolean lItraq = false;
        for (int i = 0; i < iQuantitationSources.size(); i++) {
            QuantitationSource lQuantitationSource = iQuantitationSources.get(i);
            if (lQuantitationSource == QuantitationSource.ITRAQ_DAT || lQuantitationSource == QuantitationSource.ITRAQ_MS_LIMS || lQuantitationSource == QuantitationSource.ITRAQ_ROV) {
                lItraq = true;
            }
        }
        return lItraq;
    }

    /**
     * Setter for the QuantitationSource
     *
     * @param aQuantitationSource The QuantitationSource to set
     */
    public void setRoverDataType(QuantitationSource aQuantitationSource) {
        this.iQuantitationSources.add(aQuantitationSource);
    }

    public void removeLastRoverDataType() {
        this.iQuantitationSources.removeElementAt(iQuantitationSources.size() - 1);
    }

    /**
     * Getter for the MaxQuant Score type
     *
     * @return MaxQuantScoreType
     */
    public MaxQuantScoreType getMaxQuantScoreType() {
        return iMaxQuantScoreType;
    }

    /**
     * Setter for the MaxQuantScoreType
     *
     * @param aMaxQuantScoreType MaxQuantScoreType to set
     */
    public void setMaxQuantScoreType(MaxQuantScoreType aMaxQuantScoreType) {
        this.iMaxQuantScoreType = aMaxQuantScoreType;
    }

    /**
     * This method calculates the p value for a Z-value Example: From 1.96
     * (Z-score) to 0.95 % (P-value)
     *
     * @param lZvalue the Z-value
     * @return double a p value
     */
    public double calculateOneSidedPvalueForZvalue(double lZvalue) {
        lZvalue = lZvalue / Math.sqrt(2.0);
        double lPvalue = 0.0;
        try {
            lPvalue = Erf.erf(lZvalue);
        } catch (MathException e) {
//e.printStackTrace();
//Maximal number of iterations (10,000) exceeded
//The Z value is to big or to small
// P value will be 0.0, this is ok!
        }
        return Math.round((Math.abs(lPvalue)) * 1000000.0) / 1000000.0;
    }

    /**
     * This method calculates the p value for a Z-value Example: From 1.96
     * (Z-score) to 0.975 % (P-value)
     *
     * @param lZvalue the Z-value
     * @return double a p value
     */
    public double calculateTwoSidedPvalueForZvalue(double lZvalue) {
        lZvalue = lZvalue / Math.sqrt(2.0);
        double lPvalue = 0.0;
        try {
            lPvalue = Erf.erf(lZvalue);
        } catch (MathException e) {
//e.printStackTrace();
//Maximal number of iterations (10,000) exceeded
//The Z value is to big or to small
// P value will be 0.0, this is ok!
        }
        lPvalue = (1 - ((1 - lPvalue) / 2));
        return Math.round((lPvalue) * 1000000.0) / 1000000.0;
    }

    /**
     * This method will give a boolean that indicates if we are working with
     * Census data
     *
     * @return boolean that indicates the status
     */
    public boolean isCensusQuantitation() {
        for (int i = 0; i < iQuantitationSources.size(); i++) {
            QuantitationSource lQuantitationSource = iQuantitationSources.get(i);
            if (lQuantitationSource == QuantitationSource.CENSUS) {
                return true;
            }
        }
        return false;
    }

    public String getFileLocationOpener() {
        return iFileLocationOpener;
    }

    public void setFileLocationOpener(String iFileLocationOpener) {
        this.iFileLocationOpener = iFileLocationOpener;
    }

    public void setRatioTypes(Vector<String> lRatioTypes) {
        this.iRatioTypes = lRatioTypes;
    }

    public Vector<String> getRatioTypes() {
        return iRatioTypes;
    }

    public void setComponentTypes(Vector<String> lComponentsTypes) {
        this.iComponentsTypes = lComponentsTypes;
    }

    public Vector<String> getComponentTypes() {
        return iComponentsTypes;
    }

    public Vector<QuantitationSource> getQuantitationSources() {
        return iQuantitationSources;
    }

    public void setQuantitationSources(Vector<QuantitationSource> iQuantitationSources) {
        this.iQuantitationSources = iQuantitationSources;
    }

    public void setOriginalQuantitationSources(Vector<QuantitationSource> iQuantitationSources) {
        this.iOriginalQuantitationSources = iQuantitationSources;
    }

    public void setMultipleSources(boolean multipleSources) {
        this.iMultipleSources = multipleSources;
    }

    public boolean isMultipleSources() {
        return iMultipleSources;
    }

    public void setTitles(Vector<String> titles) {
        this.iTitlesForDifferentSources = titles;
    }

    public Vector<String> getTitles() {
        return iTitlesForDifferentSources;
    }

    public void addProteinSequence(String aAccession, String aSequence) {
        this.iSequenceMap.put(aAccession, aSequence);
    }

    public String getProteinSequence(String aAccession) {
        String aSequence = (String) iSequenceMap.get(aAccession);
        return aSequence;
    }

    public Vector<QuantitationSource> getOriginalQuantitationSources() {
        return iOriginalQuantitationSources;
    }

    public void setOriginalCollections(Vector<RatioGroupCollection> originalCollections) {
        this.iOriginalCollections = originalCollections;
    }

    public Vector<RatioGroupCollection> getOriginalCollections() {
        return iOriginalCollections;
    }

    public void setProteinAccessions(Vector<String> proteinAccessions) {
        this.iProteinAccessions = proteinAccessions;
    }

    public Vector<String> getProteinAccessions() {
        return iProteinAccessions;
    }

    public void restart() {
        iCommentedProteins = new Vector<QuantitativeProtein>();
        iNotUsedPeptides = new Vector<String>();
        iNotUsedProteins = new Vector<String>();
        iSelectedProteins = new Vector<QuantitativeProtein>();
        iValidatedProteins = new Vector<QuantitativeProtein>();
        iValidatedRatios = new Vector<Ratio>();
    }

    public void setSelectedIndexes(Vector<Boolean> selectedIndexes) {
        this.iSelectedIndexes = selectedIndexes;
    }

    public Vector<Boolean> getSelectedIndexes() {
        if (iSelectedIndexes == null) {
//if it doesn't exist, every index will be used
            Vector<Boolean> lResult = new Vector<Boolean>();
            for (int i = 0; i < iTitlesForDifferentSources.size(); i++) {
                lResult.add(true);
            }
            return lResult;
        }
        return iSelectedIndexes;
    }

    public Vector<RatioType> getMatchedRatioTypes() {
        return iMatchedRatioTypes;
    }

    public void addMatchedRatioTypes(RatioType lMatchedRatioType) {
        this.iMatchedRatioTypes.add(lMatchedRatioType);
    }

    public boolean isUseOriginalRatio() {
        return iUseOriginalRatio;
    }

    public void setUseOriginalRatio(boolean iUseOriginalRatio) {
        this.iUseOriginalRatio = iUseOriginalRatio;
    }

    public void setFastaDatabaseLocation(String fastaDatabaseLocation) {
        this.iFastaDatabaseLocation = fastaDatabaseLocation;
    }

    public String getFastaDatabaseLocation() {
        return iFastaDatabaseLocation;
    }

    public boolean isNormalization() {
        return iNormalization;
    }

    public void setNormalization(boolean iNormalization) {
        this.iNormalization = iNormalization;
    }

    public void setMsLimsPre7_2(boolean msLimsPre7_2) {
        this.iMsLimsPre7_2 = msLimsPre7_2;
    }

    public boolean getMsLimsPre7_2() {
        return iMsLimsPre7_2;
    }

    public void setMsLimsPre7_6(boolean msLimsPre7_6) {
        this.iMsLimsPre7_6 = msLimsPre7_6;
    }

    public boolean getMsLimsPre7_6() {
        return iMsLimsPre7_6;
    }

    public String getReferenceSetSpecialAccessions() {
        return iReferenceSetSpecialAccessions;
    }

    public void setReferenceSetSpecialAccessions(String lSet) {
        this.iReferenceSetSpecialAccessions = lSet;
    }

    public void setExcludePeptizerUnvalid(boolean lExclude) {
        this.iExcludePeptizerUnvalid = lExclude;
    }

    public boolean getExcludePeptizerUnvalid() {
        return iExcludePeptizerUnvalid;
    }
}