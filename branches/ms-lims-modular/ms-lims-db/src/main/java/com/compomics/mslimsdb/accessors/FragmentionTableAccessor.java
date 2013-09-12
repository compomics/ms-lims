/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 27/09/2007
 * Time: 10:55:04
 */
package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import java.sql.*;
import java.io.*;
import java.util.*;

import com.compomics.util.db.interfaces.*;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2007/10/05 10:12:10 $
 */

/**
 * This class is a generated accessor for the Fragmention table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class FragmentionTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {
    // Class specific log4j logger for FragmentionTableAccessor instances.
    private static Logger logger = Logger.getLogger(FragmentionTableAccessor.class);

    /**
     * This variable tracks changes to the object.
     */
    protected boolean iUpdated = false;

    /**
     * This variable can hold generated primary key columns.
     */
    protected Object[] iKeys = null;

    /**
     * This variable represents the contents for the 'fragmentionid' column.
     */
    protected long iFragmentionid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'l_identificationid' column.
     */
    protected long iL_identificationid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'iontype' column.
     */
    protected long iIontype = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'ionname' column.
     */
    protected String iIonname = null;


    /**
     * This variable represents the contents for the 'l_ionscoringid' column.
     */
    protected long iL_ionscoringid = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'mz' column.
     */
    protected Number iMz = null;


    /**
     * This variable represents the contents for the 'intensity' column.
     */
    protected long iIntensity = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'fragmentionnumber' column.
     */
    protected long iFragmentionnumber = Long.MIN_VALUE;


    /**
     * This variable represents the contents for the 'massdelta' column.
     */
    protected Number iMassdelta = null;


    /**
     * This variable represents the contents for the 'masserrormargin' column.
     */
    protected Number iMasserrormargin = null;


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
     * This variable represents the key for the 'fragmentionid' column.
     */
    public static final String FRAGMENTIONID = "FRAGMENTIONID";

    /**
     * This variable represents the key for the 'l_identificationid' column.
     */
    public static final String L_IDENTIFICATIONID = "L_IDENTIFICATIONID";

    /**
     * This variable represents the key for the 'iontype' column.
     */
    public static final String IONTYPE = "IONTYPE";

    /**
     * This variable represents the key for the 'ionname' column.
     */
    public static final String IONNAME = "IONNAME";

    /**
     * This variable represents the key for the 'l_ionscoringid' column.
     */
    public static final String L_IONSCORINGID = "L_IONSCORINGID";

    /**
     * This variable represents the key for the 'mz' column.
     */
    public static final String MZ = "MZ";

    /**
     * This variable represents the key for the 'intensity' column.
     */
    public static final String INTENSITY = "INTENSITY";

    /**
     * This variable represents the key for the 'fragmentionnumber' column.
     */
    public static final String FRAGMENTIONNUMBER = "FRAGMENTIONNUMBER";

    /**
     * This variable represents the key for the 'massdelta' column.
     */
    public static final String MASSDELTA = "MASSDELTA";

    /**
     * This variable represents the key for the 'masserrormargin' column.
     */
    public static final String MASSERRORMARGIN = "MASSERRORMARGIN";

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
    public FragmentionTableAccessor() {
    }

    /**
     * This constructor allows the creation of the 'FragmentionTableAccessor' object based on a set of values in the
     * HashMap.
     *
     * @param aParams HashMap with the parameters to initialize this object with. <i>Please use only constants defined
     *                on this class as keys in the HashMap!</i>
     */
    public FragmentionTableAccessor(HashMap aParams) {
        if (aParams.containsKey(FRAGMENTIONID)) {
            this.iFragmentionid = ((Long) aParams.get(FRAGMENTIONID)).longValue();
        }
        if (aParams.containsKey(L_IDENTIFICATIONID)) {
            this.iL_identificationid = ((Long) aParams.get(L_IDENTIFICATIONID)).longValue();
        }
        if (aParams.containsKey(IONTYPE)) {
            this.iIontype = ((Long) aParams.get(IONTYPE)).longValue();
        }
        if (aParams.containsKey(IONNAME)) {
            this.iIonname = (String) aParams.get(IONNAME);
        }
        if (aParams.containsKey(L_IONSCORINGID)) {
            this.iL_ionscoringid = ((Long) aParams.get(L_IONSCORINGID)).longValue();
        }
        if (aParams.containsKey(MZ)) {
            this.iMz = (Number) aParams.get(MZ);
        }
        if (aParams.containsKey(INTENSITY)) {
            this.iIntensity = ((Long) aParams.get(INTENSITY)).longValue();
        }
        if (aParams.containsKey(FRAGMENTIONNUMBER)) {
            this.iFragmentionnumber = ((Long) aParams.get(FRAGMENTIONNUMBER)).longValue();
        }
        if (aParams.containsKey(MASSDELTA)) {
            this.iMassdelta = (Number) aParams.get(MASSDELTA);
        }
        if (aParams.containsKey(MASSERRORMARGIN)) {
            this.iMasserrormargin = (Number) aParams.get(MASSERRORMARGIN);
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
     * This method returns the value for the 'Fragmentionid' column
     *
     * @return long    with the value for the Fragmentionid column.
     */
    public long getFragmentionid() {
        return this.iFragmentionid;
    }

    /**
     * This method returns the value for the 'L_identificationid' column
     *
     * @return long    with the value for the L_identificationid column.
     */
    public long getL_identificationid() {
        return this.iL_identificationid;
    }

    /**
     * This method returns the value for the 'Iontype' column
     *
     * @return long    with the value for the Iontype column.
     */
    public long getIontype() {
        return this.iIontype;
    }

    /**
     * This method returns the value for the 'Ionname' column
     *
     * @return String    with the value for the Ionname column.
     */
    public String getIonname() {
        return this.iIonname;
    }

    /**
     * This method returns the value for the 'L_ionscoringid' column
     *
     * @return long    with the value for the L_ionscoringid column.
     */
    public long getL_ionscoringid() {
        return this.iL_ionscoringid;
    }

    /**
     * This method returns the value for the 'Mz' column
     *
     * @return Number    with the value for the Mz column.
     */
    public Number getMz() {
        return this.iMz;
    }

    /**
     * This method returns the value for the 'Intensity' column
     *
     * @return long    with the value for the Intensity column.
     */
    public long getIntensity() {
        return this.iIntensity;
    }

    /**
     * This method returns the value for the 'Fragmentionnumber' column
     *
     * @return long    with the value for the Fragmentionnumber column.
     */
    public long getFragmentionnumber() {
        return this.iFragmentionnumber;
    }

    /**
     * This method returns the value for the 'Massdelta' column
     *
     * @return Number    with the value for the Massdelta column.
     */
    public Number getMassdelta() {
        return this.iMassdelta;
    }

    /**
     * This method returns the value for the 'Masserrormargin' column
     *
     * @return Number    with the value for the Masserrormargin column.
     */
    public Number getMasserrormargin() {
        return this.iMasserrormargin;
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
     * This method sets the value for the 'Fragmentionid' column
     *
     * @param aFragmentionid long with the value for the Fragmentionid column.
     */
    public void setFragmentionid(long aFragmentionid) {
        this.iFragmentionid = aFragmentionid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_identificationid' column
     *
     * @param aL_identificationid long with the value for the L_identificationid column.
     */
    public void setL_identificationid(long aL_identificationid) {
        this.iL_identificationid = aL_identificationid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Iontype' column
     *
     * @param aIontype long with the value for the Iontype column.
     */
    public void setIontype(long aIontype) {
        this.iIontype = aIontype;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Ionname' column
     *
     * @param aIonname String with the value for the Ionname column.
     */
    public void setIonname(String aIonname) {
        this.iIonname = aIonname;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'L_ionscoringid' column
     *
     * @param aL_ionscoringid long with the value for the L_ionscoringid column.
     */
    public void setL_ionscoringid(long aL_ionscoringid) {
        this.iL_ionscoringid = aL_ionscoringid;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Mz' column
     *
     * @param aMz Number with the value for the Mz column.
     */
    public void setMz(Number aMz) {
        this.iMz = aMz;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Intensity' column
     *
     * @param aIntensity long with the value for the Intensity column.
     */
    public void setIntensity(long aIntensity) {
        this.iIntensity = aIntensity;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Fragmentionnumber' column
     *
     * @param aFragmentionnumber long with the value for the Fragmentionnumber column.
     */
    public void setFragmentionnumber(long aFragmentionnumber) {
        this.iFragmentionnumber = aFragmentionnumber;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Massdelta' column
     *
     * @param aMassdelta Number with the value for the Massdelta column.
     */
    public void setMassdelta(Number aMassdelta) {
        this.iMassdelta = aMassdelta;
        this.iUpdated = true;
    }

    /**
     * This method sets the value for the 'Masserrormargin' column
     *
     * @param aMasserrormargin Number with the value for the Masserrormargin column.
     */
    public void setMasserrormargin(Number aMasserrormargin) {
        this.iMasserrormargin = aMasserrormargin;
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
        PreparedStatement lStat = aConn.prepareStatement("DELETE FROM fragmention WHERE fragmentionid = ?");
        lStat.setLong(1, iFragmentionid);
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
        if (!aKeys.containsKey(FRAGMENTIONID)) {
            throw new IllegalArgumentException("Primary key field 'FRAGMENTIONID' is missing in HashMap!");
        } else {
            iFragmentionid = ((Long) aKeys.get(FRAGMENTIONID)).longValue()
                    ;
        }
        // In getting here, we probably have all we need to continue. So let's...
        PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM fragmention WHERE fragmentionid = ?");
        lStat.setLong(1, iFragmentionid);
        ResultSet lRS = lStat.executeQuery();
        int hits = 0;
        while (lRS.next()) {
            hits++;
            iFragmentionid = lRS.getLong("fragmentionid");
            iL_identificationid = lRS.getLong("l_identificationid");
            iIontype = lRS.getLong("iontype");
            iIonname = (String) lRS.getObject("ionname");
            iL_ionscoringid = lRS.getLong("l_ionscoringid");
            iMz = (Number) lRS.getObject("mz");
            iIntensity = lRS.getLong("intensity");
            iFragmentionnumber = lRS.getLong("fragmentionnumber");
            iMassdelta = (Number) lRS.getObject("massdelta");
            iMasserrormargin = (Number) lRS.getObject("masserrormargin");
            iUsername = (String) lRS.getObject("username");
            iCreationdate = (java.sql.Timestamp) lRS.getObject("creationdate");
            iModificationdate = (java.sql.Timestamp) lRS.getObject("modificationdate");
        }
        lRS.close();
        lStat.close();
        if (hits > 1) {
            throw new SQLException("More than one hit found for the specified primary keys in the 'fragmention' table! Object is initialized to last row returned.");
        } else if (hits == 0) {
            throw new SQLException("No hits found for the specified primary keys in the 'fragmention' table! Object is not initialized correctly!");
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
        PreparedStatement lStat = aConn.prepareStatement("UPDATE fragmention SET fragmentionid = ?, l_identificationid = ?, iontype = ?, ionname = ?, l_ionscoringid = ?, mz = ?, intensity = ?, fragmentionnumber = ?, massdelta = ?, masserrormargin = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE fragmentionid = ?");
        lStat.setLong(1, iFragmentionid);
        lStat.setLong(2, iL_identificationid);
        lStat.setLong(3, iIontype);
        lStat.setObject(4, iIonname);
        lStat.setLong(5, iL_ionscoringid);
        lStat.setObject(6, iMz);
        lStat.setLong(7, iIntensity);
        lStat.setLong(8, iFragmentionnumber);
        lStat.setObject(9, iMassdelta);
        lStat.setObject(10, iMasserrormargin);
        lStat.setObject(11, iUsername);
        lStat.setObject(12, iCreationdate);
        lStat.setLong(13, iFragmentionid);
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
        PreparedStatement lStat = aConn.prepareStatement("INSERT INTO fragmention (fragmentionid, l_identificationid, iontype, ionname, l_ionscoringid, mz, intensity, fragmentionnumber, massdelta, masserrormargin, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",Statement.RETURN_GENERATED_KEYS);
        if (iFragmentionid == Long.MIN_VALUE) {
            lStat.setNull(1, 4);
        } else {
            lStat.setLong(1, iFragmentionid);
        }
        if (iL_identificationid == Long.MIN_VALUE) {
            lStat.setNull(2, 4);
        } else {
            lStat.setLong(2, iL_identificationid);
        }
        if (iIontype == Long.MIN_VALUE) {
            lStat.setNull(3, 4);
        } else {
            lStat.setLong(3, iIontype);
        }
        if (iIonname == null) {
            lStat.setNull(4, 12);
        } else {
            lStat.setObject(4, iIonname);
        }
        if (iL_ionscoringid == Long.MIN_VALUE) {
            lStat.setNull(5, 4);
        } else {
            lStat.setLong(5, iL_ionscoringid);
        }
        if (iMz == null) {
            lStat.setNull(6, 3);
        } else {
            lStat.setObject(6, iMz);
        }
        if (iIntensity == Long.MIN_VALUE) {
            lStat.setNull(7, 4);
        } else {
            lStat.setLong(7, iIntensity);
        }
        if (iFragmentionnumber == Long.MIN_VALUE) {
            lStat.setNull(8, 4);
        } else {
            lStat.setLong(8, iFragmentionnumber);
        }
        if (iMassdelta == null) {
            lStat.setNull(9, 3);
        } else {
            lStat.setObject(9, iMassdelta);
        }
        if (iMasserrormargin == null) {
            lStat.setNull(10, 3);
        } else {
            lStat.setObject(10, iMasserrormargin);
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
