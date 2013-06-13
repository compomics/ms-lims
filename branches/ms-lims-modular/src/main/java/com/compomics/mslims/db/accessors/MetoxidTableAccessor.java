/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 25/08/2003
 * Time: 11:42:01
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import com.compomics.util.db.interfaces.Deleteable;
import com.compomics.util.db.interfaces.Persistable;
import com.compomics.util.db.interfaces.Retrievable;
import com.compomics.util.db.interfaces.Updateable;

import java.sql.*;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This class is a generated accessor for the Metoxid table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class MetoxidTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for MetoxidTableAccessor instances.
    private static Logger logger = Logger.getLogger(MetoxidTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'ID' column.
     */
    protected long iId = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'filename' column.
     */
    protected String iFilename = null;


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
     * This variable represents the contents for the 'score' column.
     */
    protected long iScore = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'exp_mass' column.
     */
    protected Number iExp_mass = null;


    /**
     * This variable represents the contents for the 'cal_mass' column.
     */
    protected Number iCal_mass = null;


    /**
     * This variable represents the contents for the 'valid' column.
     */
    protected int iValid = Integer.MIN_VALUE;


    /**
     * This variable represents the contents for the 'Description' column.
     */
    protected String iDescription = null;


    /**
     * This variable represents the contents for the 'creationdate' column.
     */
    protected java.sql.Timestamp iCreationdate = null;


    /**
     * This variable represents the contents for the 'datfile' column.
     */
    protected String iDatfile = null;


    /**
     * This variable represents the contents for the 'server' column.
     */
    protected String iServer = null;


    /**
     * This variable represents the contents for the 'identitythreshold' column.
     */
    protected long iIdentitythreshold = Long.MIN_VALUE;


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
     * This variable represents the key for the 'ID' column.
     */
    public static final String ID = "ID";

    /**
     * This variable represents the key for the 'filename' column.
     */
    public static final String FILENAME = "FILENAME";

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
     * This variable represents the key for the 'score' column.
     */
    public static final String SCORE = "SCORE";

    /**
     * This variable represents the key for the 'exp_mass' column.
     */
    public static final String EXP_MASS = "EXP_MASS";

    /**
     * This variable represents the key for the 'cal_mass' column.
     */
    public static final String CAL_MASS = "CAL_MASS";

    /**
     * This variable represents the key for the 'valid' column.
     */
    public static final String VALID = "VALID";

    /**
     * This variable represents the key for the 'Description' column.
     */
    public static final String DESCRIPTION = "DESCRIPTION";

    /**
     * This variable represents the key for the 'creationdate' column.
     */
    public static final String CREATIONDATE = "CREATIONDATE";

    /**
     * This variable represents the key for the 'datfile' column.
     */
    public static final String DATFILE = "DATFILE";

    /**
     * This variable represents the key for the 'server' column.
     */
    public static final String SERVER = "SERVER";

    /**
     * This variable represents the key for the 'identitythreshold' column.
     */
    public static final String IDENTITYTHRESHOLD = "IDENTITYTHRESHOLD";

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
     * Default constructor.
     */
    public MetoxidTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'MetoxidTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public MetoxidTableAccessor(HashMap aParams) {
        if (aParams.containsKey(ID)) {
            this.iId = ((Long) aParams.get(ID)).longValue();
        }
        if (aParams.containsKey(FILENAME)) {
            this.iFilename = (String) aParams.get(FILENAME);
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
        if (aParams.containsKey(SCORE)) {
            this.iScore = ((Long) aParams.get(SCORE)).longValue();
        }
        if (aParams.containsKey(EXP_MASS)) {
            this.iExp_mass = (Number) aParams.get(EXP_MASS);
        }
        if (aParams.containsKey(CAL_MASS)) {
            this.iCal_mass = (Number) aParams.get(CAL_MASS);
        }
        if (aParams.containsKey(VALID)) {
            this.iValid = ((Integer) aParams.get(VALID)).intValue();
        }
        if (aParams.containsKey(DESCRIPTION)) {
            this.iDescription = (String) aParams.get(DESCRIPTION);
        }
        if (aParams.containsKey(CREATIONDATE)) {
            this.iCreationdate = (java.sql.Timestamp) aParams.get(CREATIONDATE);
        }
        if (aParams.containsKey(DATFILE)) {
            this.iDatfile = (String) aParams.get(DATFILE);
        }
        if (aParams.containsKey(SERVER)) {
            this.iServer = (String) aParams.get(SERVER);
        }
        if (aParams.containsKey(IDENTITYTHRESHOLD)) {
            this.iIdentitythreshold = ((Long) aParams.get(IDENTITYTHRESHOLD)).longValue();
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
        this.iUpdated = true;
    }


    /**
     * This method returns the value for the 'Id' column
     *
     * @return long    with the value for the Id column.
     */
    public long getId() {
        return this.iId;
    }

    /**
     * This method returns the value for the 'Filename' column
     *
     * @return String    with the value for the Filename column.
     */
    public String getFilename() {
        return this.iFilename;
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
     * This method returns the value for the 'Score' column
     *
     * @return long    with the value for the Score column.
     */
    public long getScore() {
        return this.iScore;
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
     * This method returns the value for the 'Creationdate' column
     *
     * @return java.sql.Timestamp    with the value for the Creationdate column.
     */
    public java.sql.Timestamp getCreationdate() {
        return this.iCreationdate;
    }

    /**
     * This method returns the value for the 'Datfile' column
     *
     * @return String    with the value for the Datfile column.
     */
    public String getDatfile() {
        return this.iDatfile;
    }

    /**
     * This method returns the value for the 'Server' column
     *
     * @return String    with the value for the Server column.
     */
    public String getServer() {
        return this.iServer;
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
     * This method sets the value for the 'Id' column
     *
     * @param aId long with the value for the Id column.
     */
    public void setId(long aId) {
        this.iId = aId;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Filename' column
     *
     * @param aFilename String with the value for the Filename column.
     */
    public void setFilename(String aFilename) {
        this.iFilename = aFilename;
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
     * This method sets the value for the 'Score' column
     *
     * @param aScore long with the value for the Score column.
     */
    public void setScore(long aScore) {
        this.iScore = aScore;
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
     * This method sets the value for the 'Creationdate' column
     *
     * @param aCreationdate java.sql.Timestamp with the value for the Creationdate column.
     */
    public void setCreationdate(java.sql.Timestamp aCreationdate) {
        this.iCreationdate = aCreationdate;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Datfile' column
     *
     * @param aDatfile String with the value for the Datfile column.
     */
    public void setDatfile(String aDatfile) {
        this.iDatfile = aDatfile;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Server' column
     *
     * @param aServer String with the value for the Server column.
     */
    public void setServer(String aServer) {
        this.iServer = aServer;
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
     * This method allows the caller to delete the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int delete(Connection aConn) throws SQLException {
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM metoxid WHERE ID = ?");
        lStat.setLong(1, iId);
        int result = lStat.executeUpdate();
        lStat.close();
        return result;
    }


    /**
     * This method allows the caller to read data for this object from a persistent store based on the specified keys.
     *
     * @param aConn Connection to the persitent store.
     */
    public void retrieve(Connection aConn, HashMap aKeys) throws SQLException {
        // First check to see whether all PK fields are present.
        if (!aKeys.containsKey(ID)) {
            throw new IllegalArgumentException("Primary key field 'ID' is missing in HashMap!");
        } else {
            iId = ((Long) aKeys.get(ID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM metoxid WHERE ID = ?");
        lStat.setLong(1, iId);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iId = lRS.getLong("ID");
            iFilename = (String) lRS.getObject("filename");
            iAccession = (String) lRS.getObject("accession");
            iStart = lRS.getLong("start");
            iEnd = lRS.getLong("end");
            iEnzymatic = (String) lRS.getObject("enzymatic");
            iSequence = (String) lRS.getObject("sequence");
            iModified_sequence = (String) lRS.getObject("modified_sequence");
            iScore = lRS.getLong("score");
            iExp_mass = (Number) lRS.getObject("exp_mass");
            iCal_mass = (Number) lRS.getObject("cal_mass");
            iValid = lRS.getInt("valid");
            iDescription = (String) lRS.getObject("Description");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iDatfile = (String) lRS.getObject("datfile");
            iServer = (String) lRS.getObject("server");
            iIdentitythreshold = lRS.getLong("identitythreshold");
            iDb = (String) lRS.getObject("DB");
            iTitle = (String) lRS.getObject("title");
            iPrecursor = (Number) lRS.getObject("precursor");
            iCharge = lRS.getInt("charge");
            iIsoforms = (String) lRS.getObject("isoforms");
            iDb_filename = (String) lRS.getObject("db_filename");
            iMascot_version = (String) lRS.getObject("mascot_version");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'metoxid' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'metoxid' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE metoxid SET ID = ?, filename = ?, accession = ?, start = ?, end = ?, enzymatic = ?, sequence = ?, modified_sequence = ?, score = ?, exp_mass = ?, cal_mass = ?, valid = ?, Description = ?, creationdate = ?, datfile = ?, server = ?, identitythreshold = ?, DB = ?, title = ?, precursor = ?, charge = ?, isoforms = ?, db_filename = ?, mascot_version = ? WHERE ID = ?");
        lStat.setLong(1, iId);
        lStat.setObject(2, iFilename);
        lStat.setObject(3, iAccession);
        lStat.setLong(4, iStart);
        lStat.setLong(5, iEnd);
        lStat.setObject(6, iEnzymatic);
        lStat.setObject(7, iSequence);
        lStat.setObject(8, iModified_sequence);
        lStat.setLong(9, iScore);
        lStat.setObject(10, iExp_mass);
        lStat.setObject(11, iCal_mass);
        lStat.setInt(12, iValid);
        lStat.setObject(13, iDescription);
        lStat.setObject(14, iCreationdate);
        lStat.setObject(15, iDatfile);
        lStat.setObject(16, iServer);
        lStat.setLong(17, iIdentitythreshold);
        lStat.setObject(18, iDb);
        lStat.setObject(19, iTitle);
        lStat.setObject(20, iPrecursor);
        lStat.setInt(21, iCharge);
        lStat.setObject(22, iIsoforms);
        lStat.setObject(23, iDb_filename);
        lStat.setObject(24, iMascot_version);
        lStat.setLong(25, iId);
        int result = lStat.executeUpdate();
        lStat.close();
        this.iUpdated = false;
        return result;
    }


    /**
     * This method allows the caller to insert the data represented by this object in a persistent store.
     *
     * @param aConn Connection to the persitent store.
     */
    public int persist(Connection aConn) throws SQLException {
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO metoxid (ID, filename, accession, start, end, enzymatic, sequence, modified_sequence, score, exp_mass, cal_mass, valid, Description, creationdate, datfile, server, identitythreshold, DB, title, precursor, charge, isoforms, db_filename, mascot_version) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        if (iId == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iId);
        }
        if (iFilename == null) {
            lStat.setNull(2, 12);
        } else {
            lStat.setObject(2, iFilename);
        }
        if (iAccession == null) {
            lStat.setNull(3, 12);
        } else {
            lStat.setObject(3, iAccession);
        }
        if (iStart == Long.MIN_VALUE) {
            lStat.setNull(4, 4);
        } else {
            lStat.setLong(4, iStart);
        }
        if (iEnd == Long.MIN_VALUE) {
            lStat.setNull(5, 4);
        } else {
            lStat.setLong(5, iEnd);
        }
        if (iEnzymatic == null) {
            lStat.setNull(6, 1);
        } else {
            lStat.setObject(6, iEnzymatic);
        }
        if (iSequence == null) {
            lStat.setNull(7, 12);
        } else {
            lStat.setObject(7, iSequence);
        }
        if (iModified_sequence == null) {
            lStat.setNull(8, -1);
        } else {
            lStat.setObject(8, iModified_sequence);
        }
        if (iScore == Long.MIN_VALUE) {
            lStat.setNull(9, 4);
        } else {
            lStat.setLong(9, iScore);
        }
        if (iExp_mass == null) {
            lStat.setNull(10, 3);
        } else {
            lStat.setObject(10, iExp_mass);
        }
        if (iCal_mass == null) {
            lStat.setNull(11, 3);
        } else {
            lStat.setObject(11, iCal_mass);
        }
        if (iValid == Integer.MIN_VALUE) {
            lStat.setNull(12, -6);
        } else {
            lStat.setInt(12, iValid);
        }
        if (iDescription == null) {
            lStat.setNull(13, -1);
        } else {
            lStat.setObject(13, iDescription);
        }
        if (iCreationdate == null) {
            lStat.setNull(14, 93);
        } else {
            lStat.setObject(14, iCreationdate);
        }
        if (iDatfile == null) {
            lStat.setNull(15, 12);
        } else {
            lStat.setObject(15, iDatfile);
        }
        if (iServer == null) {
            lStat.setNull(16, 12);
        } else {
            lStat.setObject(16, iServer);
        }
        if (iIdentitythreshold == Long.MIN_VALUE) {
            lStat.setNull(17, 4);
        } else {
            lStat.setLong(17, iIdentitythreshold);
        }
        if (iDb == null) {
            lStat.setNull(18, 12);
        } else {
            lStat.setObject(18, iDb);
        }
        if (iTitle == null) {
            lStat.setNull(19, 12);
        } else {
            lStat.setObject(19, iTitle);
        }
        if (iPrecursor == null) {
            lStat.setNull(20, 3);
        } else {
            lStat.setObject(20, iPrecursor);
        }
        if (iCharge == Integer.MIN_VALUE) {
            lStat.setNull(21, 5);
        } else {
            lStat.setInt(21, iCharge);
        }
        if (iIsoforms == null) {
            lStat.setNull(22, -1);
        } else {
            lStat.setObject(22, iIsoforms);
        }
        if (iDb_filename == null) {
            lStat.setNull(23, 12);
        } else {
            lStat.setObject(23, iDb_filename);
        }
        if (iMascot_version == null) {
            lStat.setNull(24, 12);
        } else {
            lStat.setObject(24, iMascot_version);
        }
        int result = lStat.executeUpdate();

        // Retrieving the generated keys (if any).
        ResultSet lrsKeys = lStat.getGeneratedKeys();
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
        this.iUpdated = false;
        return result;
    }

    /**
     * This method will return the automatically generated key for the insert if one was triggered, or 'null' otherwise.
     *
     * @return Object[]    with the generated keys.
     */
    public Object[] getGeneratedKeys() {
        return this.iKeys;
	}

}
