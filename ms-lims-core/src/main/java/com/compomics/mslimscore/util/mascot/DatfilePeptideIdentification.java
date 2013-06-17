/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.mascot;

import com.compomics.mslimscore.util.interfaces.PeptideIdentification;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class DatfilePeptideIdentification implements PeptideIdentification {
	// Class specific log4j logger for DatfilePeptideIdentification instances.
    private static Logger logger = Logger.getLogger(DatfilePeptideIdentification.class);

   /** This variable represents the contents for the 'datfile_query'. */
   private long iDatfile_query = Long.MIN_VALUE;


   /** This variable represents the contents for the 'accession'. */
   private String iAccession = null;


   /** This variable represents the contents for the 'start'. */
   private long iStart = Long.MIN_VALUE;


   /** This variable represents the contents for the 'end'. */
   private long iEnd = Long.MIN_VALUE;


   /** This variable represents the contents for the 'enzymatic'. */
   private String iEnzymatic = null;


   /** This variable represents the contents for the 'sequence'. */
   private String iSequence = null;


   /** This variable represents the contents for the 'modified_sequence'. */
   private String iModified_sequence = null;


   /** This variable represents the contents for the 'score'. */
   private long iScore = Long.MIN_VALUE;


   /** This variable represents the contents for the 'homology'. */
   private double iHomology = Double.MIN_VALUE;


   /** This variable represents the contents for the 'exp_mass'. */
   private Number iExp_mass = null;


   /** This variable represents the contents for the 'cal_mass'. */
   private Number iCal_mass = null;


   /** This variable represents the contents for the 'valid'. */
   private int iValid = Integer.MIN_VALUE;


   /** This variable represents the contents for the 'Description'. */
   private String iDescription = null;


   /** This variable represents the contents for the 'identitythreshold'. */
   private long iIdentitythreshold = Long.MIN_VALUE;


   /** This variable represents the contents for the 'confidence'. */
   private Number iConfidence = null;


   /** This variable represents the contents for the 'DB'. */
   private String iDb = null;


   /** This variable represents the contents for the 'title'. */
   private String iTitle = null;


   /** This variable represents the contents for the 'precursor'. */
   private Number iPrecursor = null;


   /** This variable represents the contents for the 'charge'. */
   private int iCharge = Integer.MIN_VALUE;


   /** This variable represents the contents for the 'isoforms'. */
   private String iIsoforms = null;


   /** This variable represents the contents for the 'db_filename'. */
   private String iDb_filename = null;


   /** This variable represents the contents for the 'mascot_version'. */
   private String iMascot_version = null;

    /**
     * This is the quantitation type (Light, Heavy, ...) of this identification
     */
    private String iType;

    /**
     * This is the spectrum file name
     */
    private String iSpectrumFileName;


    /**
     * This constructor allows the creation of the 'IdentificationTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param    aParams    HashMap with the parameters to initialize this object with. <i>Please use only constants defined on
     * this class as keys in the HashMap!</i>
     */
    public DatfilePeptideIdentification(HashMap aParams) {
        if (aParams.containsKey("DATFILE_QUERY")) {
            this.iDatfile_query = (Long) aParams.get("DATFILE_QUERY");
        }
        if (aParams.containsKey("ACCESSION")) {
            this.iAccession = (String) aParams.get("ACCESSION");
        }
        if (aParams.containsKey("START")) {
            this.iStart = ((Long) aParams.get("START")).longValue();
        }
        if (aParams.containsKey("END")) {
            this.iEnd = ((Long) aParams.get("END")).longValue();
        }
        if (aParams.containsKey("ENZYMATIC")) {
            this.iEnzymatic = (String) aParams.get("ENZYMATIC");
        }
        if (aParams.containsKey("SEQUENCE")) {
            this.iSequence = (String) aParams.get("SEQUENCE");
        }
        if (aParams.containsKey("MODIFIED_SEQUENCE")) {
            this.iModified_sequence = (String) aParams.get("MODIFIED_SEQUENCE");
        }
        if (aParams.containsKey("SCORE")) {
            this.iScore = ((Long) aParams.get("SCORE")).longValue();
        }
        if (aParams.containsKey("HOMOLOGY")) {
            this.iHomology = ((Double) aParams.get("HOMOLOGY")).doubleValue();
        }
        if (aParams.containsKey("EXP_MASS")) {
            this.iExp_mass = (Number) aParams.get("EXP_MASS");
        }
        if (aParams.containsKey("CAL_MASS")) {
            this.iCal_mass = (Number) aParams.get("CAL_MASS");
        }
        if (aParams.containsKey("VALID")) {
            this.iValid = ((Integer) aParams.get("VALID")).intValue();
        }
        if (aParams.containsKey("DESCRIPTION")) {
            this.iDescription = (String) aParams.get("DESCRIPTION");
        }
        if (aParams.containsKey("IDENTITYTHRESHOLD")) {
            this.iIdentitythreshold = ((Long) aParams.get("IDENTITYTHRESHOLD")).longValue();
        }
        if (aParams.containsKey("CONFIDENCE")) {
            this.iConfidence = (Number) aParams.get("CONFIDENCE");
        }
        if (aParams.containsKey("DB")) {
            this.iDb = (String) aParams.get("DB");
        }
        if (aParams.containsKey("TITLE")) {
            this.iTitle = (String) aParams.get("TITLE");
        }
        if (aParams.containsKey("PRECURSOR")) {
            this.iPrecursor = (Number) aParams.get("PRECURSOR");
        }
        if (aParams.containsKey("CHARGE")) {
            this.iCharge = ((Integer) aParams.get("CHARGE")).intValue();
        }
        if (aParams.containsKey("ISOFORMS")) {
            this.iIsoforms = (String) aParams.get("ISOFORMS");
        }
        if (aParams.containsKey("DB_FILENAME")) {
            this.iDb_filename = (String) aParams.get("DB_FILENAME");
        }
        if (aParams.containsKey("MASCOT_VERSION")) {
            this.iMascot_version = (String) aParams.get("MASCOT_VERSION");
        }

        if (aParams.containsKey("SPECTRUM_FILE_NAME")) {
            this.iSpectrumFileName = (String) aParams.get("SPECTRUM_FILE_NAME");
        }
    }


   //GETTERS AND SETTERS
    public String getType() {
        return iType;
    }


    public void setType(String aType) {
        iType = aType;
    }

    public long getDatfile_query() {
        return iDatfile_query;
    }

    public void setDatfile_query(long iDatfile_query) {
        this.iDatfile_query = iDatfile_query;
    }

    public String getAccession() {
        return iAccession;
    }

    public void setAccession(String iAccession) {
        this.iAccession = iAccession;
    }

    public long getStart() {
        return iStart;
    }

    public void setStart(long iStart) {
        this.iStart = iStart;
    }

    public long getEnd() {
        return iEnd;
    }

    public void setEnd(long iEnd) {
        this.iEnd = iEnd;
    }

    public String getEnzymatic() {
        return iEnzymatic;
    }

    public void setEnzymatic(String iEnzymatic) {
        this.iEnzymatic = iEnzymatic;
    }

    public String getSequence() {
        return iSequence;
    }

    public void setSequence(String iSequence) {
        this.iSequence = iSequence;
    }

    public String getModified_sequence() {
        return iModified_sequence;
    }

    public void setModified_sequence(String iModified_sequence) {
        this.iModified_sequence = iModified_sequence;
    }

    public long getScore() {
        return iScore;
    }

    public void setScore(long iScore) {
        this.iScore = iScore;
    }

    public double getHomology() {
        return iHomology;
    }

    public void setHomology(double iHomology) {
        this.iHomology = iHomology;
    }

    public Number getExp_mass() {
        return iExp_mass;
    }

    public void setExp_mass(Number iExp_mass) {
        this.iExp_mass = iExp_mass;
    }

    public Number getCal_mass() {
        return iCal_mass;
    }

    public void setCal_mass(Number iCal_mass) {
        this.iCal_mass = iCal_mass;
    }

    public int getValid() {
        return iValid;
    }

    public void setValid(int iValid) {
        this.iValid = iValid;
    }

    public String getDescription() {
        return iDescription;
    }

    public void setDescription(String iDescription) {
        this.iDescription = iDescription;
    }

    public long getIdentitythreshold() {
        return iIdentitythreshold;
    }

    public void setIdentitythreshold(long iIdentitythreshold) {
        this.iIdentitythreshold = iIdentitythreshold;
    }

    public Number getConfidence() {
        return iConfidence;
    }

    public void setConfidence(Number iConfidence) {
        this.iConfidence = iConfidence;
    }

    public String getDb() {
        return iDb;
    }

    public void setDb(String iDb) {
        this.iDb = iDb;
    }

    public String getTitle() {
        return iTitle;
    }

    public void setTitle(String iTitle) {
        this.iTitle = iTitle;
    }

    public Number getPrecursor() {
        return iPrecursor;
    }

    public void setPrecursor(Number iPrecursor) {
        this.iPrecursor = iPrecursor;
    }

    public int getCharge() {
        return iCharge;
    }

    public void setCharge(int iCharge) {
        this.iCharge = iCharge;
    }

    public String getIsoforms() {
        return iIsoforms;
    }

    public void setIsoforms(String iIsoforms) {
        this.iIsoforms = iIsoforms;
    }

    public String getDb_filename() {
        return iDb_filename;
    }

    public void setDb_filename(String iDb_filename) {
        this.iDb_filename = iDb_filename;
    }

    public String getMascot_version() {
        return iMascot_version;
    }

    public void setMascot_version(String iMascot_version) {
        this.iMascot_version = iMascot_version;
    }

    public String getSpectrumFileName() {
        return iSpectrumFileName;
    }

    public void setSpectrumFileName(String aFileName) {
        this.iSpectrumFileName = aFileName;
    }
}
