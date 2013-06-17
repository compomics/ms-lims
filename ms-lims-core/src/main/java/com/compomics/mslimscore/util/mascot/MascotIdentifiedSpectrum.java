/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 30-jul-02
 * Time: 13:37:27
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslimscore.util.mascot;

import org.apache.log4j.Logger;

import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2007/10/05 10:12:10 $
 */

/**
 * This class is a wrapper for the full amount of information Mascot delivers on an identification for a spectrum.
 */
public class MascotIdentifiedSpectrum {
    // Class specific log4j logger for MascotIdentifiedSpectrum instances.
    private static Logger logger = Logger.getLogger(MascotIdentifiedSpectrum.class);

    /**
     * The original file name. No tampering for post-processing here.
     */
    private String original_file = null;

    /**
     * The score this identification received by Mascot.
     */
    private double score;

    /**
     * The homology treshold as calculated for this identification.
     */
    private int homologyTreshold;
    /**
     * The homology treshold as calculated for this identification.
     */
    private int identityTreshold;

    /**
     * The sequence for the identified peptide.
     */
    private String sequence;

    /**
     * The modified sequence for the spectrum.
     */
    private String modified_sequence = null;

    /**
     * The ion coverage for the spectrum.
     */
    private String ion_coverage = null;

    /**
     * The fixed modifications that are present on the identification.
     */
    private String[] fixedMods = null;

    /**
     * The Mascot raw data query number for this identification.
     */
    private int queryNr;

    /**
     * The theoretical mass for this identification.
     */
    private double theoreticalMass;
    /**
     * The experimental mass for this identification.
     */
    private double measuredMass;

    /**
     * The rank of this identification.
     */
    private int rank;

    /**
     * The peaklist file used for identification.
     */
    private String file = null;

    /**
     * The headers for all possible isoforms.
     */
    private MascotIsoforms iHeaders = null;

    /**
     * The identity threshold score.
     */
    private int idThreshold = -1;

    /**
     * The database used for the identification
     */
    private String dbName = null;

    /**
     * The charge state of the precursor.
     */
    private int chargeState = -1;

    /**
     * The precursor M/Z.
     */
    private double precursorMZ = -1.0;

    /**
     * The intensity for the least intense peak in the spectrum.
     */
    private double leastIntense = -1.0;

    /**
     * The intensity for the least intense peak in the spectrum.
     */
    private double mostIntense = -1.0;

    /**
     * The mass of the peak with the lowest mass in the spectrum.
     */
    private double lowestMass = -1.0;

    /**
     * The mass of the peak with the highest mass in the spectrum.
     */
    private double highestMass = -1.0;

    /**
     * The title for the search as stated by the user in Mascot Daemon.
     */
    private String searchTitle = null;

    /**
     * The db file name.
     */
    private String iDBFilename = null;

    /**
     * The Mascot version.
     */
    private String iMascotVersion = null;

    /**
     * The collection for fragmentions.
     */
    private Collection iFragmentIons = null;

    /**
     * The allowed fragment mass error.
     */
    private double iFragmentMassError = -1.0;

    /**
     * Empty constructor.
     */
    public MascotIdentifiedSpectrum() {
    }

    public int getQueryNr() {
        return queryNr;
    }

    public void setQueryNr(int aQueryNr) {
        queryNr = aQueryNr;
    }


    /**
     * This method reports the outcome of the following operation:
     * <pre>experimentalMass - theoreticalMass</pre> and as such allows
     * for negative numbers in the result.
     *
     * @return double with the outcome of the operation:
     *         <pre>experimentalMass - theoreticalMass</pre>
     */
    public double getDelta() {
        return (measuredMass - theoreticalMass);
    }

    /**
     * This method reports the outcome of the following operation:
     * <pre>|experimentalMass - theoreticalMass|</pre> and as such allows
     * for only positive numbers in the result.
     *
     * @return double with the outcome of the operation:
     *         <pre>|experimentalMass - theoreticalMass|</pre>
     */
    public double getAbsoluteDelta() {
        return Math.abs(measuredMass - theoreticalMass);
    }

    public double getTheoreticalMass() {
        return theoreticalMass;
    }

    public void setTheoreticalMass(double aTheoreticalMass) {
        theoreticalMass = aTheoreticalMass;
    }

    public double getMeasuredMass() {
        return measuredMass;
    }

    public void setMeasuredMass(double aMeasuredMass) {
        measuredMass = aMeasuredMass;
    }

    public void setFixedMods(String[] aFixedMods) {
        this.fixedMods = aFixedMods;
    }

    public String[] getFixedMods() {
        return this.fixedMods;
    }

    public String getOriginal_file() {
        return original_file;
    }

    public void setOriginal_file(String original_file) {
        this.original_file = original_file;
    }

    public int getStart(String accession) {
        return this.iHeaders.getHeader(accession).getStart();
    }


    public int getEnd(String accession) {
        return this.iHeaders.getHeader(accession).getEnd();
    }

    public void setIsoforms(MascotIsoforms aHeaders) {
        this.iHeaders = aHeaders;
    }

    public int getIsoformCount() {
        return this.iHeaders.getIsoforms().size();
    }

    public String getAccession(String[] accessions) {
        return this.iHeaders.getMainHeader(accessions).getAccession();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String aFile) {
        file = aFile;
    }

    public String getDescription(String accession) {
        return this.iHeaders.getHeader(accession).getDescription();
    }

    public void setDescription(String aDescription, String aAccession) {
        MascotHeader mh = this.iHeaders.getHeader(aAccession);
        if (mh != null) {
            mh.setDescription(aDescription);
        }
    }

    public String getIsoformAccessions(String accession) {
        String result = null;
        MascotHeader mh = this.iHeaders.getHeader(accession);
        if (mh != null) {
            result = mh.getIsoformAccessions();
        }
        return result;
    }

    public double getHighestMass() {
        return highestMass;
    }

    public void setHighestMass(double aHighestMass) {
        highestMass = aHighestMass;
    }

    public double getLeastIntense() {
        return leastIntense;
    }

    public void setLeastIntense(double aLeastIntense) {
        leastIntense = aLeastIntense;
    }

    public double getLowestMass() {
        return lowestMass;
    }

    public void setLowestMass(double aLowestMass) {
        lowestMass = aLowestMass;
    }

    public double getMostIntense() {
        return mostIntense;
    }

    public void setMostIntense(double aMostIntense) {
        mostIntense = aMostIntense;
    }

    public String getSearchTitle() {
        return searchTitle;
    }

    public void setSearchTitle(String aSearchTitle) {
        searchTitle = aSearchTitle;
    }

    public String getDBFilename() {
        return iDBFilename;
    }

    public void setDBFilename(String aDBFilename) {
        iDBFilename = aDBFilename;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getMascotVersion() {
        return iMascotVersion;
    }

    public void setMascotVersion(String aMascotVersion) {
        iMascotVersion = aMascotVersion;
    }

    /**
     * This method returns the sequence, 'tagged' with all variable modifications. <br /> The formatting of this String
     * is the brainchild of Dr. Grégoire Thomas.
     *
     * @return String  with the sequence for this identification, annotated with modification tags, as concocted by Dr.
     *         Grégoire Thomas.
     */
    public String getModifiedSequence() {
        return modified_sequence;
    }

    public void setModifiedSequence(String aModified_sequence) {
        modified_sequence = aModified_sequence;
    }

    public String getIon_coverage() {
        return ion_coverage;
    }

    public void setIon_coverage(String aIon_coverage) {
        ion_coverage = aIon_coverage;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String aSequence) {
        sequence = aSequence;
    }

    public int getScore() {
        return (int) (this.score + 0.5);
    }

    public double getRealScore() {
        return this.score;
    }

    public void setScore(double aiScore) {
        this.score = aiScore;
    }

    public int getHomologyTreshold() {
        return homologyTreshold;
    }

    public void setHomologyTreshold(int aiHT) {
        this.homologyTreshold = aiHT;
    }

    public int getIdentityTreshold() {
        return identityTreshold;
    }

    public void setIdentityTreshold(int aiIT) {
        this.identityTreshold = aiIT;
    }

    public String getDBName() {
        return this.dbName;
    }

    public void setDBName(String aDB) {
        this.dbName = aDB;
    }

    public double getPrecursorMZ() {
        return precursorMZ;
    }

    public void setPrecursorMZ(double aPrecursorMZ) {
        precursorMZ = aPrecursorMZ;
    }

    public int getChargeState() {
        return chargeState;
    }

    public void setChargeState(int aChargeState) {
        chargeState = aChargeState;
    }

    public Collection getFragmentIons() {
        return iFragmentIons;
    }

    public void setFragmentIons(Collection aFragmentIons) {
        iFragmentIons = aFragmentIons;
    }

    public double getFragmentMassError() {
        return iFragmentMassError;
    }

    public void setFragmentMassError(double aFragmentMassError) {
        iFragmentMassError = aFragmentMassError;
    }

    /**
     * This method generates a String representation of this identification.
     *
     * @return String  with the String representation of this object.
     */
    public String toString() {
        StringBuffer temp = new StringBuffer("\n");
        String accession = this.getAccession(null);

        temp.append("Spectrum with: \n");
        temp.append("\t- sequence: " + this.sequence + "\tModified sequence: " + this.getModifiedSequence() + "\n");
        temp.append("\t- matching: " + accession + ((this.getStart(accession) >= 0) ? "(" + this.getStart(accession) + "-" + this.getEnd(accession) + ")\n" : "\n"));
        temp.append("\t\t\t" + this.getDescription(accession) + "\n");
        temp.append("\t- score: " + this.getScore() + "\n");
        temp.append("\t- Mr(cal): " + this.theoreticalMass + "\n");
        temp.append("\t- Mr(exp): " + this.measuredMass + "\n");
        temp.append("\t- ID treshold: " + this.identityTreshold + "\n");
        temp.append("\t- homology treshold: " + this.homologyTreshold + "\n");
        temp.append("\t- rank: " + this.rank + "\n");
        temp.append("\t- from file: " + this.file + "\n");
        temp.append("\t- precursor mass and charge: " + this.precursorMZ + " ");
        temp.append(this.chargeState + "\n");
        temp.append("\t- identified in: " + this.dbName + ".\n");
        temp.append("\t- In this spectrum (lowMass, highMass, lowIntensity, highIntensity): (" + this.lowestMass + ", " + this.highestMass + ", " + this.leastIntense + ", " + this.mostIntense + ")\n");
        temp.append("\t- this was query number " + this.queryNr + "\n");
        temp.append("\t- Search title: " + this.searchTitle + "\n");

        return temp.toString();
    }

    /**
     * The hashcode generated here results from combining the following (key, value) pairs in a HashMap and then
     * exracting that HashMap's hashcode: <ul> <li>"1" : query (Integer)</li> <li>"2" : rank (Integer)</li> <li>"3" :
     * original filename (String)</li> </ul>
     *
     * @return int with the hashcode for this instance.
     */
    public int hashCode() {
        HashMap temp = new HashMap(3);

        temp.put("1", new Integer(this.queryNr));
        temp.put("2", new Integer(this.rank));
        temp.put("3", this.original_file);

        return temp.hashCode();
    }
}
