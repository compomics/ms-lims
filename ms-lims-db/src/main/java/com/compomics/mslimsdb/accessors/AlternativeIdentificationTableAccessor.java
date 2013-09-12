package com.compomics.mslimsdb.accessors;

import com.compomics.util.db.interfaces.Deleteable;
import com.compomics.util.db.interfaces.Persistable;
import com.compomics.util.db.interfaces.Retrievable;
import com.compomics.util.db.interfaces.Updateable;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.HashMap;

public class AlternativeIdentificationTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    /*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 16/10/2008
 * Time: 16:01:33
 */
        // Class specific log4j logger for IdentificationTableAccessor instances.
        private static Logger logger = Logger.getLogger(IdentificationTableAccessor.class);

        /**
         * This variable tracks changes to the object.
         */
        protected boolean iUpdated = false;

        /**
         * This variable can hold generated primary key columns.
         */
        protected Object[] iKeys = null;

        /**
         * This variable represents the contents for the 'alternative_identificationid' column.
         */
        protected long iAlternativeIdentificationid = Long.MIN_VALUE;

    /**
     *  This variable represents the contents for the l_identificationid
     */
        protected long iL_Identificationid = Long.MIN_VALUE;

        /**
         * This variable represents the contents for the 'l_spectrumid' column.
         */
        protected long iL_spectrumid = Long.MIN_VALUE;


        /**
         * This variable represents the contents for the 'l_datfileid' column.
         */
        protected long iL_datfileid = Long.MIN_VALUE;


        /**
         * This variable represents the contents for the 'datfile_query' column.
         */
        protected long iDatfile_query = Long.MIN_VALUE;


        /**
         * This variable represents the contents for the 'accession' column.
         */
        protected String iAccession = null;


        /**
         * This variable represents the contents for the 'start' column.
         */
        protected long iStart = Long.MIN_VALUE;


        /**
         * This variable represents the contents for the 'end' column.
         */
        protected long iEnd = Long.MIN_VALUE;


        /**
         * This variable represents the contents for the 'enzymatic' column.
         */
        protected String iEnzymatic = null;


        /**
         * This variable represents the contents for the 'sequence' column.
         */
        protected String iSequence = null;


        /**
         * This variable represents the contents for the 'modified_sequence' column.
         */
        protected String iModified_sequence = null;


        /**
         * This variable represents the contents for the 'ion_coverage' column.
         */
        protected String iIon_coverage = null;


        /**
         * This variable represents the contents for the 'score' column.
         */
        protected long iScore = Long.MIN_VALUE;


        /**
         * This variable represents the contents for the 'homology' column.
         */
        protected double iHomology = Double.MIN_VALUE;


        /**
         * This variable represents the contents for the 'exp_mass' column.
         */
        protected Number iExp_mass = null;


        /**
         * This variable represents the contents for the 'cal_mass' column.
         */
        protected Number iCal_mass = null;


        /**
         * This variable represents the contents for the 'light_isotope' column.
         */
        protected Number iLight_isotope = null;


        /**
         * This variable represents the contents for the 'heavy_isotope' column.
         */
        protected Number iHeavy_isotope = null;


        /**
         * This variable represents the contents for the 'valid' column.
         */
        protected int iValid = Integer.MIN_VALUE;


        /**
         * This variable represents the contents for the 'Description' column.
         */
        protected String iDescription = null;


        /**
         * This variable represents the contents for the 'identitythreshold' column.
         */
        protected long iIdentitythreshold = Long.MIN_VALUE;


        /**
         * This variable represents the contents for the 'confidence' column.
         */
        protected Number iConfidence = null;


        /**
         * This variable represents the contents for the 'DB' column.
         */
        protected String iDb = null;


        /**
         * This variable represents the contents for the 'title' column.
         */
        protected String iTitle = null;


        /**
         * This variable represents the contents for the 'precursor' column.
         */
        protected Number iPrecursor = null;


        /**
         * This variable represents the contents for the 'charge' column.
         */
        protected int iCharge = Integer.MIN_VALUE;


        /**
         * This variable represents the contents for the 'isoforms' column.
         */
        protected String iIsoforms = null;


        /**
         * This variable represents the contents for the 'db_filename' column.
         */
        protected String iDb_filename = null;


        /**
         * This variable represents the contents for the 'mascot_version' column.
         */
        protected String iMascot_version = null;


        /**
         * This variable represents the contents for the 'username' column.
         */
        protected String iUsername = null;


        /**
         * This variable represents the contents for the 'creationdate' column.
         */
        protected java.sql.Timestamp iCreationdate = null;


        /**
         * This variable represents the contents for the 'modificationdate' column.
         */
        protected java.sql.Timestamp iModificationdate = null;


        /**
         * This variable represents the key for the 'alternative_identificationid' column.
         */
        public static final String ALTERNATIVEIDENTIFICATIONID = "ALTERNATIVEIDENTIFICATIONID";

    /**
     * This variable represents the key for the 'l_identification' column 
     */
        public static final String L_IDENTIFICATIONID = "L_IDENTIFICATIONID";

    /**
         * This variable represents the key for the 'l_spectrumid' column.
         */
        public static final String L_SPECTRUMFILEID = "L_SPECTRUMFILEID";

        /**
         * This variable represents the key for the 'l_datfileid' column.
         */
        public static final String L_DATFILEID = "L_DATFILEID";

        /**
         * This variable represents the key for the 'datfile_query' column.
         */
        public static final String DATFILE_QUERY = "DATFILE_QUERY";

        /**
         * This variable represents the key for the 'accession' column.
         */
        public static final String ACCESSION = "ACCESSION";

        /**
         * This variable represents the key for the 'start' column.
         */
        public static final String START = "START";

        /**
         * This variable represents the key for the 'end' column.
         */
        public static final String END = "END";

        /**
         * This variable represents the key for the 'enzymatic' column.
         */
        public static final String ENZYMATIC = "ENZYMATIC";

        /**
         * This variable represents the key for the 'sequence' column.
         */
        public static final String SEQUENCE = "SEQUENCE";

        /**
         * This variable represents the key for the 'modified_sequence' column.
         */
        public static final String MODIFIED_SEQUENCE = "MODIFIED_SEQUENCE";

        /**
         * This variable represents the key for the 'ion_coverage' column.
         */
        public static final String ION_COVERAGE = "ION_COVERAGE";

        /**
         * This variable represents the key for the 'score' column.
         */
        public static final String SCORE = "SCORE";

        /**
         * This variable represents the key for the 'homology' column.
         */
        public static final String HOMOLOGY = "HOMOLOGY";

        /**
         * This variable represents the key for the 'exp_mass' column.
         */
        public static final String EXP_MASS = "EXP_MASS";

        /**
         * This variable represents the key for the 'cal_mass' column.
         */
        public static final String CAL_MASS = "CAL_MASS";

        /**
         * This variable represents the key for the 'light_isotope' column.
         */
        public static final String LIGHT_ISOTOPE = "LIGHT_ISOTOPE";

        /**
         * This variable represents the key for the 'heavy_isotope' column.
         */
        public static final String HEAVY_ISOTOPE = "HEAVY_ISOTOPE";

        /**
         * This variable represents the key for the 'valid' column.
         */
        public static final String VALID = "VALID";

        /**
         * This variable represents the key for the 'Description' column.
         */
        public static final String DESCRIPTION = "DESCRIPTION";

        /**
         * This variable represents the key for the 'identitythreshold' column.
         */
        public static final String IDENTITYTHRESHOLD = "IDENTITYTHRESHOLD";

        /**
         * This variable represents the key for the 'confidence' column.
         */
        public static final String CONFIDENCE = "CONFIDENCE";

        /**
         * This variable represents the key for the 'DB' column.
         */
        public static final String DB = "DB";

        /**
         * This variable represents the key for the 'title' column.
         */
        public static final String TITLE = "TITLE";

        /**
         * This variable represents the key for the 'precursor' column.
         */
        public static final String PRECURSOR = "PRECURSOR";

        /**
         * This variable represents the key for the 'charge' column.
         */
        public static final String CHARGE = "CHARGE";

        /**
         * This variable represents the key for the 'isoforms' column.
         */
        public static final String ISOFORMS = "ISOFORMS";

        /**
         * This variable represents the key for the 'db_filename' column.
         */
        public static final String DB_FILENAME = "DB_FILENAME";

        /**
         * This variable represents the key for the 'mascot_version' column.
         */
        public static final String MASCOT_VERSION = "MASCOT_VERSION";

        /**
         * This variable represents the key for the 'username' column.
         */
        public static final String USERNAME = "USERNAME";

        /**
         * This variable represents the key for the 'creationdate' column.
         */
        public static final String CREATIONDATE = "CREATIONDATE";

        /**
         * This variable represents the key for the 'modificationdate' column.
         */
        public static final String MODIFICATIONDATE = "MODIFICATIONDATE";


        /**
         * Default constructor.
         */
        public AlternativeIdentificationTableAccessor() {
        }

        /**
         * This constructor allows the creation of the 'IdentificationTableAccessor' object based on a set of values in the
         * HashMap.
         *
         * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
         *                on this class as keys in the HashMap!</i>
         */
        public AlternativeIdentificationTableAccessor(HashMap aParams) {
            if (aParams.containsKey(ALTERNATIVEIDENTIFICATIONID)) {
                this.iAlternativeIdentificationid = ((Long) aParams.get(ALTERNATIVEIDENTIFICATIONID)).longValue();
            }
            if(aParams.containsKey(L_IDENTIFICATIONID)){
                this.iL_Identificationid = ((Long)aParams.get(L_IDENTIFICATIONID)).longValue();
            }
            
            if (aParams.containsKey(L_SPECTRUMFILEID)) {
                this.iL_spectrumid = ((Long) aParams.get(L_SPECTRUMFILEID)).longValue();
            }
            if (aParams.containsKey(L_DATFILEID)) {
                this.iL_datfileid = ((Long) aParams.get(L_DATFILEID)).longValue();
            }
            if (aParams.containsKey(DATFILE_QUERY)) {
                this.iDatfile_query = ((Integer) aParams.get(DATFILE_QUERY)).intValue();
            }
            if (aParams.containsKey(ACCESSION)) {
                this.iAccession = (String) aParams.get(ACCESSION);
            }
            if (aParams.containsKey(START)) {
                this.iStart = ((Long) aParams.get(START)).longValue();
            }
            if (aParams.containsKey(END)) {
                this.iEnd = ((Long) aParams.get(END)).longValue();
            }
            if (aParams.containsKey(ENZYMATIC)) {
                this.iEnzymatic = (String) aParams.get(ENZYMATIC);
            }
            if (aParams.containsKey(SEQUENCE)) {
                this.iSequence = (String) aParams.get(SEQUENCE);
            }
            if (aParams.containsKey(MODIFIED_SEQUENCE)) {
                this.iModified_sequence = (String) aParams.get(MODIFIED_SEQUENCE);
            }
            if (aParams.containsKey(ION_COVERAGE)) {
                this.iIon_coverage = (String) aParams.get(ION_COVERAGE);
            }
            if (aParams.containsKey(SCORE)) {
                this.iScore = ((Long) aParams.get(SCORE)).longValue();
            }
            if (aParams.containsKey(HOMOLOGY)) {
                this.iHomology = ((Double) aParams.get(HOMOLOGY)).doubleValue();
            }
            if (aParams.containsKey(EXP_MASS)) {
                this.iExp_mass = (Number) aParams.get(EXP_MASS);
            }
            if (aParams.containsKey(CAL_MASS)) {
                this.iCal_mass = (Number) aParams.get(CAL_MASS);
            }
            if (aParams.containsKey(LIGHT_ISOTOPE)) {
                this.iLight_isotope = (Number) aParams.get(LIGHT_ISOTOPE);
            }
            if (aParams.containsKey(HEAVY_ISOTOPE)) {
                this.iHeavy_isotope = (Number) aParams.get(HEAVY_ISOTOPE);
            }
            if (aParams.containsKey(VALID)) {
                this.iValid = ((Integer) aParams.get(VALID)).intValue();
            }
            if (aParams.containsKey(DESCRIPTION)) {
                this.iDescription = (String) aParams.get(DESCRIPTION);
            }
            if (aParams.containsKey(IDENTITYTHRESHOLD)) {
                this.iIdentitythreshold = ((Long) aParams.get(IDENTITYTHRESHOLD)).longValue();
            }
            if (aParams.containsKey(CONFIDENCE)) {
                this.iConfidence = (Number) aParams.get(CONFIDENCE);
            }
            if (aParams.containsKey(DB)) {
                this.iDb = (String) aParams.get(DB);
            }
            if (aParams.containsKey(TITLE)) {
                this.iTitle = (String) aParams.get(TITLE);
            }
            if (aParams.containsKey(PRECURSOR)) {
                this.iPrecursor = (Number) aParams.get(PRECURSOR);
            }
            if (aParams.containsKey(CHARGE)) {
                this.iCharge = ((Integer) aParams.get(CHARGE)).intValue();
            }
            if (aParams.containsKey(ISOFORMS)) {
                this.iIsoforms = (String) aParams.get(ISOFORMS);
            }
            if (aParams.containsKey(DB_FILENAME)) {
                this.iDb_filename = (String) aParams.get(DB_FILENAME);
            }
            if (aParams.containsKey(MASCOT_VERSION)) {
                this.iMascot_version = (String) aParams.get(MASCOT_VERSION);
            }
            if (aParams.containsKey(USERNAME)) {
                this.iUsername = (String) aParams.get(USERNAME);
            }
            if (aParams.containsKey(CREATIONDATE)) {
                this.iCreationdate = (java.sql.Timestamp) aParams.get(CREATIONDATE);
            }
            if (aParams.containsKey(MODIFICATIONDATE)) {
                this.iModificationdate = (java.sql.Timestamp) aParams.get(MODIFICATIONDATE);
            }
            this.iUpdated = true;
        }


        /**
         * This method returns the value for the 'alternative_identificationid' column
         *
         * @return long    with the value for the alternative_identificationid column.
         */
        public long getiAlternativeIdentificationid() {
            return this.iAlternativeIdentificationid;
        }

    /**
     * This method returns the value for the 'l_identificationid' column
     *
     * @return long with the value for the l_identificationid column
     */
        public long getiL_Identificationid(){
            return this.iL_Identificationid;
        }

        /**
         * This method returns the value for the 'L_spectrumid' column
         *
         * @return long    with the value for the L_spectrumid column.
         */
        public long getL_spectrumid() {
            return this.iL_spectrumid;
        }

        /**
         * This method returns the value for the 'L_datfileid' column
         *
         * @return long    with the value for the L_datfileid column.
         */
        public long getL_datfileid() {
            return this.iL_datfileid;
        }

        /**
         * This method returns the value for the 'Datfile_query' column
         *
         * @return long    with the value for the Datfile_query column.
         */
        public long getDatfile_query() {
            return this.iDatfile_query;
        }

        /**
         * This method returns the value for the 'Accession' column
         *
         * @return String    with the value for the Accession column.
         */
        public String getAccession() {
            return this.iAccession;
        }

        /**
         * This method returns the value for the 'Start' column
         *
         * @return long    with the value for the Start column.
         */
        public long getStart() {
            return this.iStart;
        }

        /**
         * This method returns the value for the 'End' column
         *
         * @return long    with the value for the End column.
         */
        public long getEnd() {
            return this.iEnd;
        }

        /**
         * This method returns the value for the 'Enzymatic' column
         *
         * @return String    with the value for the Enzymatic column.
         */
        public String getEnzymatic() {
            return this.iEnzymatic;
        }

        /**
         * This method returns the value for the 'Sequence' column
         *
         * @return String    with the value for the Sequence column.
         */
        public String getSequence() {
            return this.iSequence;
        }

        /**
         * This method returns the value for the 'Modified_sequence' column
         *
         * @return String    with the value for the Modified_sequence column.
         */
        public String getModified_sequence() {
            return this.iModified_sequence;
        }

        /**
         * This method returns the value for the 'Ion_coverage' column
         *
         * @return String    with the value for the Ion_coverage column.
         */
        public String getIon_coverage() {
            return this.iIon_coverage;
        }

        /**
         * This method returns the value for the 'Score' column
         *
         * @return long    with the value for the Score column.
         */
        public long getScore() {
            return this.iScore;
        }

        /**
         * This method returns the value for the 'Homology' column
         *
         * @return double    with the value for the Homology column.
         */
        public double getHomology() {
            return this.iHomology;
        }

        /**
         * This method returns the value for the 'Exp_mass' column
         *
         * @return Number    with the value for the Exp_mass column.
         */
        public Number getExp_mass() {
            return this.iExp_mass;
        }

        /**
         * This method returns the value for the 'Cal_mass' column
         *
         * @return Number    with the value for the Cal_mass column.
         */
        public Number getCal_mass() {
            return this.iCal_mass;
        }

        /**
         * This method returns the value for the 'Light_isotope' column
         *
         * @return Number    with the value for the Light_isotope column.
         */
        public Number getLight_isotope() {
            return this.iLight_isotope;
        }

        /**
         * This method returns the value for the 'Heavy_isotope' column
         *
         * @return Number    with the value for the Heavy_isotope column.
         */
        public Number getHeavy_isotope() {
            return this.iHeavy_isotope;
        }

        /**
         * This method returns the value for the 'Valid' column
         *
         * @return int    with the value for the Valid column.
         */
        public int getValid() {
            return this.iValid;
        }

        /**
         * This method returns the value for the 'Description' column
         *
         * @return String    with the value for the Description column.
         */
        public String getDescription() {
            return this.iDescription;
        }

        /**
         * This method returns the value for the 'Identitythreshold' column
         *
         * @return long    with the value for the Identitythreshold column.
         */
        public long getIdentitythreshold() {
            return this.iIdentitythreshold;
        }

        /**
         * This method returns the value for the 'Confidence' column
         *
         * @return Number    with the value for the Confidence column.
         */
        public Number getConfidence() {
            return this.iConfidence;
        }

        /**
         * This method returns the value for the 'Db' column
         *
         * @return String    with the value for the Db column.
         */
        public String getDb() {
            return this.iDb;
        }

        /**
         * This method returns the value for the 'Title' column
         *
         * @return String    with the value for the Title column.
         */
        public String getTitle() {
            return this.iTitle;
        }

        /**
         * This method returns the value for the 'Precursor' column
         *
         * @return Number    with the value for the Precursor column.
         */
        public Number getPrecursor() {
            return this.iPrecursor;
        }

        /**
         * This method returns the value for the 'Charge' column
         *
         * @return int    with the value for the Charge column.
         */
        public int getCharge() {
            return this.iCharge;
        }

        /**
         * This method returns the value for the 'Isoforms' column
         *
         * @return String    with the value for the Isoforms column.
         */
        public String getIsoforms() {
            return this.iIsoforms;
        }

        /**
         * This method returns the value for the 'Db_filename' column
         *
         * @return String    with the value for the Db_filename column.
         */
        public String getDb_filename() {
            return this.iDb_filename;
        }

        /**
         * This method returns the value for the 'Mascot_version' column
         *
         * @return String    with the value for the Mascot_version column.
         */
        public String getMascot_version() {
            return this.iMascot_version;
        }

        /**
         * This method returns the value for the 'Username' column
         *
         * @return String    with the value for the Username column.
         */
        public String getUsername() {
            return this.iUsername;
        }

        /**
         * This method returns the value for the 'Creationdate' column
         *
         * @return java.sql.Timestamp    with the value for the Creationdate column.
         */
        public java.sql.Timestamp getCreationdate() {
            return this.iCreationdate;
        }

        /**
         * This method returns the value for the 'Modificationdate' column
         *
         * @return java.sql.Timestamp    with the value for the Modificationdate column.
         */
        public java.sql.Timestamp getModificationdate() {
            return this.iModificationdate;
        }

        /**
         * This method sets the value for the 'Identificationid' column
         *
         * @param aAlternativeIdentificationid long with the value for the alternative_Identificationid column.
         */
        public void setAlternativeIdentificationid(long aAlternativeIdentificationid) {
            this.iAlternativeIdentificationid = aAlternativeIdentificationid;
            this.iUpdated = true;
        }

        public void setiL_Identificationid(long aL_Identificationid){
            this.iL_Identificationid = aL_Identificationid;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'L_spectrumid' column
         *
         * @param aL_spectrumid long with the value for the L_spectrumid column.
         */
        public void setL_spectrumid(long aL_spectrumid) {
            this.iL_spectrumid = aL_spectrumid;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'L_datfileid' column
         *
         * @param aL_datfileid long with the value for the L_datfileid column.
         */
        public void setL_datfileid(long aL_datfileid) {
            this.iL_datfileid = aL_datfileid;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Datfile_query' column
         *
         * @param aDatfile_query long with the value for the Datfile_query column.
         */
        public void setDatfile_query(long aDatfile_query) {
            this.iDatfile_query = aDatfile_query;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Accession' column
         *
         * @param aAccession String with the value for the Accession column.
         */
        public void setAccession(String aAccession) {
            this.iAccession = aAccession;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Start' column
         *
         * @param aStart long with the value for the Start column.
         */
        public void setStart(long aStart) {
            this.iStart = aStart;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'End' column
         *
         * @param aEnd long with the value for the End column.
         */
        public void setEnd(long aEnd) {
            this.iEnd = aEnd;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Enzymatic' column
         *
         * @param aEnzymatic String with the value for the Enzymatic column.
         */
        public void setEnzymatic(String aEnzymatic) {
            this.iEnzymatic = aEnzymatic;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Sequence' column
         *
         * @param aSequence String with the value for the Sequence column.
         */
        public void setSequence(String aSequence) {
            this.iSequence = aSequence;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Modified_sequence' column
         *
         * @param aModified_sequence String with the value for the Modified_sequence column.
         */
        public void setModified_sequence(String aModified_sequence) {
            this.iModified_sequence = aModified_sequence;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Ion_coverage' column
         *
         * @param aIon_coverage String with the value for the Ion_coverage column.
         */
        public void setIon_coverage(String aIon_coverage) {
            this.iIon_coverage = aIon_coverage;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Score' column
         *
         * @param aScore long with the value for the Score column.
         */
        public void setScore(long aScore) {
            this.iScore = aScore;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Homology' column
         *
         * @param aHomology double with the value for the Homology column.
         */
        public void setHomology(double aHomology) {
            this.iHomology = aHomology;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Exp_mass' column
         *
         * @param aExp_mass Number with the value for the Exp_mass column.
         */
        public void setExp_mass(Number aExp_mass) {
            this.iExp_mass = aExp_mass;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Cal_mass' column
         *
         * @param aCal_mass Number with the value for the Cal_mass column.
         */
        public void setCal_mass(Number aCal_mass) {
            this.iCal_mass = aCal_mass;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Light_isotope' column
         *
         * @param aLight_isotope Number with the value for the Light_isotope column.
         */
        public void setLight_isotope(Number aLight_isotope) {
            this.iLight_isotope = aLight_isotope;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Heavy_isotope' column
         *
         * @param aHeavy_isotope Number with the value for the Heavy_isotope column.
         */
        public void setHeavy_isotope(Number aHeavy_isotope) {
            this.iHeavy_isotope = aHeavy_isotope;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Valid' column
         *
         * @param aValid int with the value for the Valid column.
         */
        public void setValid(int aValid) {
            this.iValid = aValid;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Description' column
         *
         * @param aDescription String with the value for the Description column.
         */
        public void setDescription(String aDescription) {
            this.iDescription = aDescription;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Identitythreshold' column
         *
         * @param aIdentitythreshold long with the value for the Identitythreshold column.
         */
        public void setIdentitythreshold(long aIdentitythreshold) {
            this.iIdentitythreshold = aIdentitythreshold;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Confidence' column
         *
         * @param aConfidence Number with the value for the Confidence column.
         */
        public void setConfidence(Number aConfidence) {
            this.iConfidence = aConfidence;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Db' column
         *
         * @param aDb String with the value for the Db column.
         */
        public void setDb(String aDb) {
            this.iDb = aDb;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Title' column
         *
         * @param aTitle String with the value for the Title column.
         */
        public void setTitle(String aTitle) {
            this.iTitle = aTitle;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Precursor' column
         *
         * @param aPrecursor Number with the value for the Precursor column.
         */
        public void setPrecursor(Number aPrecursor) {
            this.iPrecursor = aPrecursor;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Charge' column
         *
         * @param aCharge int with the value for the Charge column.
         */
        public void setCharge(int aCharge) {
            this.iCharge = aCharge;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Isoforms' column
         *
         * @param aIsoforms String with the value for the Isoforms column.
         */
        public void setIsoforms(String aIsoforms) {
            this.iIsoforms = aIsoforms;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Db_filename' column
         *
         * @param aDb_filename String with the value for the Db_filename column.
         */
        public void setDb_filename(String aDb_filename) {
            this.iDb_filename = aDb_filename;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Mascot_version' column
         *
         * @param aMascot_version String with the value for the Mascot_version column.
         */
        public void setMascot_version(String aMascot_version) {
            this.iMascot_version = aMascot_version;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Username' column
         *
         * @param aUsername String with the value for the Username column.
         */
        public void setUsername(String aUsername) {
            this.iUsername = aUsername;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Creationdate' column
         *
         * @param aCreationdate java.sql.Timestamp with the value for the Creationdate column.
         */
        public void setCreationdate(java.sql.Timestamp aCreationdate) {
            this.iCreationdate = aCreationdate;
            this.iUpdated = true;
        }

        /**
         * This method sets the value for the 'Modificationdate' column
         *
         * @param aModificationdate java.sql.Timestamp with the value for the Modificationdate column.
         */
        public void setModificationdate(java.sql.Timestamp aModificationdate) {
            this.iModificationdate = aModificationdate;
            this.iUpdated = true;
        }


        /**
         * This method allows the caller to delete the data represented by this object in a persistent store.
         *
         * @param aConn Connection to the persitent store.
         */
        public int delete(Connection aConn) throws SQLException {
            int result = 0;
            PreparedStatement lStat = null;
            try {
                lStat = aConn.prepareStatement("DELETE FROM alternative_identification WHERE alternative_identificationid = ?");
                lStat.setLong(1, iAlternativeIdentificationid);
                result = lStat.executeUpdate();
                aConn.commit();
                lStat.close();
            } catch (SQLException sqle){
                aConn.rollback();
                logger.error(sqle);
            }
            return result;
        }


        /**
         * This method allows the caller to read data for this object from a persistent store based on the specified keys.
         *
         * @param aConn Connection to the persitent store.
         */
        public void retrieve(Connection aConn, HashMap aKeys) throws SQLException {
            // First check to see whether all PK fields are present.
            if (!aKeys.containsKey(ALTERNATIVEIDENTIFICATIONID)) {
                throw new IllegalArgumentException("Primary key field 'ALTERNATIVEIDENTIFICATIONID' is missing in HashMap!");
            } else {
                iAlternativeIdentificationid = ((Long) aKeys.get(ALTERNATIVEIDENTIFICATIONID)).longValue()
                ;
            }
            // In getting here, we probably have all we need to continue. So let's...
            PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM alternative_identification WHERE alternative_identificationid = ?");
            lStat.setLong(1, iAlternativeIdentificationid);
            ResultSet lRS = lStat.executeQuery();
            int hits = 0;
            while (lRS.next()) {
                hits++;
                iAlternativeIdentificationid = lRS.getLong("alternative_identificationid");
                iL_Identificationid = lRS.getLong("l_identificationid");
                iL_spectrumid = lRS.getLong("l_spectrumid");
                iL_datfileid = lRS.getLong("l_datfileid");
                iDatfile_query = lRS.getLong("datfile_query");
                iAccession = (String) lRS.getObject("accession");
                iStart = lRS.getLong("start");
                iEnd = lRS.getLong("end");
                iEnzymatic = (String) lRS.getObject("enzymatic");
                iSequence = (String) lRS.getObject("sequence");
                iModified_sequence = (String) lRS.getObject("modified_sequence");
                iIon_coverage = (String) lRS.getObject("ion_coverage");
                iScore = lRS.getLong("score");
                iHomology = lRS.getDouble("homology");
                iExp_mass = (Number) lRS.getObject("exp_mass");
                iCal_mass = (Number) lRS.getObject("cal_mass");
                iLight_isotope = (Number) lRS.getObject("light_isotope");
                iHeavy_isotope = (Number) lRS.getObject("heavy_isotope");
                iValid = lRS.getInt("valid");
                iDescription = (String) lRS.getObject("Description");
                iIdentitythreshold = lRS.getLong("identitythreshold");
                iConfidence = (Number) lRS.getObject("confidence");
                iDb = (String) lRS.getObject("DB");
                iTitle = (String) lRS.getObject("title");
                iPrecursor = (Number) lRS.getObject("precursor");
                iCharge = lRS.getInt("charge");
                iIsoforms = (String) lRS.getObject("isoforms");
                iDb_filename = (String) lRS.getObject("db_filename");
                iMascot_version = (String) lRS.getObject("mascot_version");
                iUsername = (String) lRS.getObject("username");
                iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
                iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
            }
            lRS.close();
            lStat.close();
            if (hits > 1) {
                throw new SQLException("More than one hit found for the specified primary keys in the 'identification' table! Object is initialized to last row returned.");
            } else if (hits == 0) {
                throw new SQLException("No hits found for the specified primary keys in the 'identification' table! Object is not initialized correctly!");
            }
        }


        /**
         * This method allows the caller to update the data represented by this object in a persistent store.
         *
         * @param aConn Connection to the persitent store.
         */
        public int update(Connection aConn) throws SQLException {
            if (!this.iUpdated) {
                return 0;
            }
            PreparedStatement lStat = null;
            int result = 0;
            try {
                lStat = aConn.prepareStatement("UPDATE identification SET alternative_identificationid = ?, l_identificationid = ? ,l_spectrumid = ?, l_datfileid = ?, datfile_query = ?, accession = ?, start = ?, end = ?, enzymatic = ?, sequence = ?, modified_sequence = ?, ion_coverage = ?, score = ?, homology = ?, exp_mass = ?, cal_mass = ?, light_isotope = ?, heavy_isotope = ?, valid = ?, Description = ?, identitythreshold = ?, confidence = ?, DB = ?, title = ?, precursor = ?, charge = ?, isoforms = ?, db_filename = ?, mascot_version = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE alternative_identificationid = ?");
                lStat.setLong(1, iAlternativeIdentificationid);
                lStat.setLong(2, iL_Identificationid);
                lStat.setLong(3, iL_spectrumid);
                lStat.setLong(4, iL_datfileid);
                lStat.setLong(5, iDatfile_query);
                lStat.setObject(6, iAccession);
                lStat.setLong(7, iStart);
                lStat.setLong(8, iEnd);
                lStat.setObject(9, iEnzymatic);
                lStat.setObject(10, iSequence);
                lStat.setObject(11, iModified_sequence);
                lStat.setObject(12, iIon_coverage);
                lStat.setLong(13, iScore);
                lStat.setDouble(14, iHomology);
                lStat.setObject(15, iExp_mass);
                lStat.setObject(16, iCal_mass);
                lStat.setObject(17, iLight_isotope);
                lStat.setObject(18, iHeavy_isotope);
                lStat.setInt(19, iValid);
                lStat.setObject(20, iDescription);
                lStat.setLong(21, iIdentitythreshold);
                lStat.setObject(22, iConfidence);
                lStat.setObject(23, iDb);
                lStat.setObject(24, iTitle);
                lStat.setObject(25, iPrecursor);
                lStat.setInt(26, iCharge);
                lStat.setObject(27, iIsoforms);
                lStat.setObject(28, iDb_filename);
                lStat.setObject(29, iMascot_version);
                lStat.setObject(30, iUsername);
                lStat.setObject(31, iCreationdate);
                lStat.setLong(32, iAlternativeIdentificationid);
                result = lStat.executeUpdate();
                lStat.close();
                aConn.commit();
            } catch (SQLException sqle) {
                aConn.rollback();
                logger.error(sqle);
            }
            this.iUpdated = false;
            return result;
        }


        /**
         * This method allows the caller to insert the data represented by this object in a persistent store.
         *
         * @param aConn Connection to the persitent store.
         */
        public int persist(Connection aConn) throws SQLException {
            ResultSet lrsKeys = null;
            PreparedStatement lStat = null;
            int result = 0;
            try {
                lStat = aConn.prepareStatement("INSERT INTO identification (alternative_identificationid,l_identificationid ,l_spectrumid, l_datfileid, datfile_query, accession, start, end, enzymatic, sequence, modified_sequence, ion_coverage, score, homology, exp_mass, cal_mass, light_isotope, heavy_isotope, valid, Description, identitythreshold, confidence, DB, title, precursor, charge, isoforms, db_filename, mascot_version, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",Statement.RETURN_GENERATED_KEYS);
                if (iAlternativeIdentificationid == Long.MIN_VALUE) {
                    lStat.setNull(1, 4);
                } else {
                    lStat.setLong(1, iAlternativeIdentificationid);
                }
                if (iL_Identificationid == Long.MIN_VALUE) {
                    lStat.setNull(2, 4);
                } else {
                    lStat.setLong(2, iL_Identificationid);
                }
                if (iL_spectrumid == Long.MIN_VALUE) {
                    lStat.setNull(3, 4);
                } else {
                    lStat.setLong(3, iL_spectrumid);
                }
                if (iL_datfileid == Long.MIN_VALUE) {
                    lStat.setNull(4, 4);
                } else {
                    lStat.setLong(4, iL_datfileid);
                }
                if (iDatfile_query == Long.MIN_VALUE) {
                    lStat.setNull(5, 4);
                } else {
                    lStat.setLong(5, iDatfile_query);
                }
                if (iAccession == null) {
                    lStat.setNull(6, 12);
                } else {
                    lStat.setObject(6, iAccession);
                }
                if (iStart == Long.MIN_VALUE) {
                    lStat.setNull(7, 4);
                } else {
                    lStat.setLong(7, iStart);
                }
                if (iEnd == Long.MIN_VALUE) {
                    lStat.setNull(8, 4);
                } else {
                    lStat.setLong(8, iEnd);
                }
                if (iEnzymatic == null) {
                    lStat.setNull(9, 1);
                } else {
                    lStat.setObject(9, iEnzymatic);
                }
                if (iSequence == null) {
                    lStat.setNull(10, 12);
                } else {
                    lStat.setObject(10, iSequence);
                }
                if (iModified_sequence == null) {
                    lStat.setNull(11, -1);
                } else {
                    lStat.setObject(11, iModified_sequence);
                }
                if (iIon_coverage == null) {
                    lStat.setNull(12, -1);
                } else {
                    lStat.setObject(12, iIon_coverage);
                }
                if (iScore == Long.MIN_VALUE) {
                    lStat.setNull(13, 4);
                } else {
                    lStat.setLong(13, iScore);
                }
                if (iHomology == Double.MIN_VALUE) {
                    lStat.setNull(14, 8);
                } else {
                    lStat.setDouble(14, iHomology);
                }
                if (iExp_mass == null) {
                    lStat.setNull(15, 3);
                } else {
                    lStat.setObject(15, iExp_mass);
                }
                if (iCal_mass == null) {
                    lStat.setNull(16, 3);
                } else {
                    lStat.setObject(16, iCal_mass);
                }
                if (iLight_isotope == null) {
                    lStat.setNull(17, 3);
                } else {
                    lStat.setObject(17, iLight_isotope);
                }
                if (iHeavy_isotope == null) {
                    lStat.setNull(18, 3);
                } else {
                    lStat.setObject(18, iHeavy_isotope);
                }
                if (iValid == Integer.MIN_VALUE) {
                    lStat.setNull(19, -6);
                } else {
                    lStat.setInt(19, iValid);
                }
                if (iDescription == null) {
                    lStat.setNull(20, -1);
                } else {
                    lStat.setObject(20, iDescription);
                }
                if (iIdentitythreshold == Long.MIN_VALUE) {
                    lStat.setNull(21, 4);
                } else {
                    lStat.setLong(21, iIdentitythreshold);
                }
                if (iConfidence == null) {
                    lStat.setNull(22, 3);
                } else {
                    lStat.setObject(22, iConfidence);
                }
                if (iDb == null) {
                    lStat.setNull(23, 12);
                } else {
                    lStat.setObject(23, iDb);
                }
                if (iTitle == null) {
                    lStat.setNull(24, 12);
                } else {
                    lStat.setObject(24, iTitle);
                }
                if (iPrecursor == null) {
                    lStat.setNull(25, 3);
                } else {
                    lStat.setObject(25, iPrecursor);
                }
                if (iCharge == Integer.MIN_VALUE) {
                    lStat.setNull(26, 5);
                } else {
                    lStat.setInt(26, iCharge);
                }
                if (iIsoforms == null) {
                    lStat.setNull(27, -1);
                } else {
                    lStat.setObject(27, iIsoforms);
                }
                if (iDb_filename == null) {
                    lStat.setNull(28, 12);
                } else {
                    lStat.setObject(28, iDb_filename);
                }
                if (iMascot_version == null) {
                    lStat.setNull(29, 12);
                } else {
                    lStat.setObject(29, iMascot_version);
                }
                result = lStat.executeUpdate();

                aConn.commit();

                // Retrieving the generated keys (if any).
                lrsKeys = lStat.getGeneratedKeys();
                ResultSetMetaData lrsmKeys = lrsKeys.getMetaData();
                int colCount = lrsmKeys.getColumnCount();
                iKeys = new Object[colCount];
                while (lrsKeys.next()) {
                    for (int i = 0; i < iKeys.length; i++) {
                        iKeys[i] = lrsKeys.getObject(i + 1);
                    }
                }
                lrsKeys.close();
                lStat.close();
            } catch (SQLException sqle) {
                aConn.rollback();
                logger.error(sqle);
            }
            this.iUpdated = false;
            return result;
        }

        /**
         * This method will return the automatically generated key for the insert if one was triggered, or 'null'
         * otherwise.
         *
         * @return Object[]    with the generated keys.
         */
        public Object[] getGeneratedKeys() {
            return this.iKeys;
        }

    }
