/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 31-jul-03
 * Time: 14:01
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.16 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class extends IdentificationTableAccessor.
 *
 * @author Lennart Martens
 */
public class Identification extends IdentificationTableAccessor {
    // Class specific log4j logger for Identification instances.
    private static Logger logger = Logger.getLogger(Identification.class);

    /**
     * Temporary storage of spectrumfilename (primarily for IdentificationGUI stuff)
     */
    private String iSpectrumfilename = null;

    /**
     * Temporary storage of datfilename (primarily for IdentificationGUI stuff)
     */
    private String iDatfilename = null;

    /**
     * Temporary storage of the fragment ions associated with this ID.
     */
    private Collection iFragmentions = null;

    /**
     * Temporary storage of the fragment mass tolerance for this ID.
     */
    private double iFragmentMassTolerance = -1.0;

    private boolean hasAlternative = false;

    /**
     * Default constructor.
     */
    public Identification() {
        super();
    }

    public Identification(Map aHM) {
        super(aHM);

    }

    /**
     * This method reads an Identification from the specified ResultSet. Th ResultSet should be positioned such that it
     * is directly readable, ie.: next should've been called already and should've returned 'true'. The columns should
     * be in this order: <br /> Column 1: identification ID <br /> Column 2: l_spectrumfile ID <br /> Column 3:
     * l_datfile ID <br /> Column 4: accession number <br /> Column 5: start position <br /> Column 6: end position <br
     * /> Column 7: enzymatic character <br /> Column 8: sequence <br /> Column 9: modified sequence <br /> Column 10:
     * ion coverage string <br /> Column 11: score <br /> Column 12: experimental mass <br /> Column 13: calculated mass
     * <br /> Column 14: light isotope intensity <br /> Column 15: heavy isotope intensity <br /> Column 16: valid flag
     * <br /> Column 17: description <br /> Column 18: identity threshold <br /> Column 19: confidence <br /> Column 20:
     * db <br /> Column 21: title <br /> Column 22: precursor mass <br /> Column 23: precursor charge <br /> Column 24:
     * isoforms <br /> Column 25: db filename <br /> Column 26: Mascot version <br /> Column 27: username <br /> Column
     * 28: creationdate <br /> Column 29: modificationdate.
     *
     * @param aRS ResultSet to read the Identification from. It should be directly readable, ie.: next should've been
     *            called already and should've returned 'true'.
     * @throws SQLException when the ResultSet could not be read.
     */
    public Identification(ResultSet aRS) throws SQLException {
        this.iIdentificationid = aRS.getLong(1);
        this.iL_spectrumid = aRS.getLong(2);
        this.iL_datfileid = aRS.getLong(3);
        this.iDatfile_query = aRS.getLong(4);
        this.iAccession = aRS.getString(5);
        this.iStart = aRS.getLong(6);
        this.iEnd = aRS.getLong(7);
        this.iEnzymatic = aRS.getString(8);
        this.iSequence = aRS.getString(9);
        this.iModified_sequence = aRS.getString(10);
        this.iIon_coverage = aRS.getString(11);
        this.iScore = aRS.getLong(12);
        this.iHomology = aRS.getLong(13);
        this.iExp_mass = (Number) aRS.getObject(14);
        this.iCal_mass = (Number) aRS.getObject(15);
        this.iLight_isotope = (Number) aRS.getObject(16);
        this.iHeavy_isotope = (Number) aRS.getObject(17);
        this.iValid = aRS.getInt(18);
        this.iDescription = aRS.getString(19);
        this.iIdentitythreshold = aRS.getLong(20);
        this.iConfidence = (Number) aRS.getObject(21);
        this.iDb = aRS.getString(22);
        this.iTitle = aRS.getString(23);
        this.iPrecursor = (Number) aRS.getObject(24);
        this.iCharge = aRS.getInt(25);
        this.iIsoforms = aRS.getString(26);
        this.iDb_filename = aRS.getString(27);
        this.iMascot_version = aRS.getString(28);
        this.iUsername = aRS.getString(29);
        this.iCreationdate = (java.sql.Timestamp) aRS.getObject(30);
        this.iModificationdate = (java.sql.Timestamp) aRS.getObject(31);

    }

    public String getTemporaryDatfilename() {
        return iDatfilename;
    }

    public void setTemporaryDatfilename(String aDatfilename) {
        iDatfilename = aDatfilename;
    }

    public String getTemporarySpectrumfilename() {
        return iSpectrumfilename;
    }

    public void setTemporarySpectrumfilename(String aSpectrumfilename) {
        iSpectrumfilename = aSpectrumfilename;
    }

    public double getFragmentMassTolerance() {
        return iFragmentMassTolerance;
    }

    public void setFragmentMassTolerance(double aFragmentMassTolerance) {
        iFragmentMassTolerance = aFragmentMassTolerance;
    }

    public Collection getFragmentions() {
        return iFragmentions;
    }

    public void setFragmentions(Collection aFragmentions) {
        iFragmentions = aFragmentions;
    }

    /**
     * This method returns a Vector, containing all Identification entries that could be retrieved, matching the
     * specified accession String and holding the specified location and associated with the correct project.
     *
     * @param aAccession String with the accession String to match.
     * @param aLocation  int with the location that should be >= start and <= end.
     * @param aProjectid long with the link to the project this Identification must belong to.
     * @param aConn      Connection to read the data from.
     * @return Vector with the matches (can be empty when no matches were found).
     * @throws SQLException when the retrieve failed.
     */
    public static Vector getIdentifications(String aAccession, int aLocation, long aProjectid, Connection aConn) throws SQLException {
        Vector temp = new Vector();

        PreparedStatement ps =
                aConn.prepareStatement("select i.* from identification as i, spectrum as f where i.accession = ? and i.start <= ? and i.end >= ? and i.l_spectrumid = f.spectrumid and f.l_projectid=?");
        ps.setString(1, aAccession);
        ps.setInt(2, aLocation);
        ps.setInt(3, aLocation);
        ps.setLong(4, aProjectid);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new Identification(rs));
        }
        rs.close();
        ps.close();

        return temp;
    }

    /**
     * This method returns all the unique accession numbers in Accession column in the Identification table in the
     * database.
     *
     * @param aConn Connection from which to retrieve the accessions.
     * @return String[]    with the unique accessions
     * @throws SQLException when something goes wrong.
     */
    public static String[] getAllUniqueAccessions(Connection aConn) throws SQLException {
        PreparedStatement ps = aConn.prepareStatement("select distinct accession from identificaton");
        ResultSet rs = ps.executeQuery();
        Vector accessions = new Vector(10000, 5000);
        while (rs.next()) {
            accessions.add(rs.getString(1));
        }
        rs.close();
        ps.close();
        String[] result = new String[accessions.size()];
        accessions.toArray(result);

        return result;
    }

    /**
     * This method loads the Identification instance corresponding to the specified filename from the database.
     *
     * @param aConn     Connection to load the identification from.
     * @param aFilename String with the filename to select for.
     * @return Identification with the accessor, or 'null' if no hit was found.
     * @throws SQLException when the select failed.
     */
    public static Identification getIdentification(Connection aConn, String aFilename) throws SQLException {
        Identification result = null;
        PreparedStatement ps =
                aConn.prepareStatement("select i.* from identification as i, spectrum as s where i.l_spectrumid=s.spectrumid and s.filename = ? ORDER BY i.score ASC");
        ps.setString(1, aFilename);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            result = new Identification(rs);
        }
        rs.close();
        ps.close();
        return result;
    }

    /**
     * This method loads the Identification instances corresponding to the specified filenames from the database.
     *
     * @param aConn      Connection to load the identification from.
     * @param aFilenames Collection with Strings containing the filenames to select for.
     * @return HashMap with the spectrum filename as String as key, and an Identification instance as value, or 'null'
     *         if no hit was found.
     * @throws SQLException when the select failed.
     */
    public static HashMap getIdentifications(Connection aConn, Collection aFilenames) throws SQLException {
        HashMap result = new HashMap();
        PreparedStatement ps =
                aConn.prepareStatement("select i.* from identification as i, spectrum as s where i.l_spectrumid=s.spectrumid and s.filename = ? ORDER BY i.score ASC");
        for (Iterator lIterator = aFilenames.iterator(); lIterator.hasNext();) {
            String filename = (String) lIterator.next();
            ps.setString(1, filename);
            ResultSet rs = ps.executeQuery();
            Identification id = null;
            while (rs.next()) {
                id = new Identification(rs);
            }
            rs.close();
            result.put(filename, id);
            ps.clearParameters();
        }
        ps.close();
        return result;
    }

    /**
     * This methods reads all the identifications associated with the specified Project ID, with an optional 'where'
     * clause addition. Note that the two tables you can use are 'identifciation' and 'spectrumfile'. The former is
     * aliased as 'i', the latter as 's'. So you can specify the addition as 'i.score>10 and i.identitithreshold < 30',
     * for instance. The addition will be added to the core query as 'AND (addition)'.
     *
     * @param aConn          Connection to read the identifications from.
     * @param aProjectID     long with the Project ID to select on.
     * @param aWhereAddition String with an optional where-clause addition. Can be 'null'.
     * @return Identification[] with the identifications associated with the specified Project ID.
     * @throws SQLException when the retrieving of the identifications went wrong.
     */
    public static Identification[] getAllIdentificationsforProject(Connection aConn, long aProjectID, String aWhereAddition) throws SQLException {
        String sql =
                "select i.* from identification as i, spectrum as s where i.l_spectrumid = s.spectrumid and s.l_projectid=?";
        if (aWhereAddition != null) {
            sql += " AND " + aWhereAddition;
        }
        PreparedStatement prep = aConn.prepareStatement(sql);
        prep.setLong(1, aProjectID);
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new Identification(rs));
        }
        rs.close();
        prep.close();
        Identification[] lIDs = new Identification[v.size()];
        v.toArray(lIDs);

        return lIDs;
    }

    /**
     * This methods reads all the identifications associated with the specified Project ID and done using spectra from
     * the specified instrument, with an optional 'where' clause addition. Note that the two tables you can use are
     * 'identification' and 'spectrumfile'. The former is aliased as 'i', the latter as 's'. So you can specify the
     * addition as 'i.score>10 and i.identitithreshold < 30', for instance. The addition will be added to the core query
     * as 'AND (addition)'.
     *
     * @param aConn          Connection to read the identifications from.
     * @param aProjectID     long with the Project ID to select on.
     * @param aInstrumentID  long with the Instrument ID to select on.
     * @param aWhereAddition String with an optional where-clause addition. Can be 'null'.
     * @return Identification[] with the identifications associated with the specified Project ID.
     * @throws SQLException when the retrieving of the identifications went wrong.
     */
    public static Identification[] getAllIdentificationsforProjectAndInstrument(Connection aConn, long aProjectID, long aInstrumentID, String aWhereAddition) throws SQLException {
        String sql =
                "select i.* from identification as i, spectrum as s where i.l_spectrumid = s.spectrumid and s.l_projectid=? and s.l_instrumentid=?";
        if (aWhereAddition != null) {
            sql += " AND " + aWhereAddition;
        }
        PreparedStatement prep = aConn.prepareStatement(sql);
        prep.setLong(1, aProjectID);
        prep.setLong(2, aInstrumentID);
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new Identification(rs));
        }
        rs.close();
        prep.close();
        Identification[] lIDs = new Identification[v.size()];
        v.toArray(lIDs);

        return lIDs;
    }


    /**
     * This methods reads all the identifications returned by the query using the specified 'where' clause addition.
     * Note that the two tables you can use are 'identifciation' and 'spectrumfile'. The former is aliased as 'i', the
     * latter as 's'. So you can specify the addition as 'i.score>10 and i.identitithreshold < 30', for instance. The
     * addition will be added to the core query as 'AND (addition)'.
     *
     * @param aConn          Connection to read the identifications from.
     * @param aWhereAddition String with a where-clause addition. Cannot be 'null'!
     * @return Identification[] with the identifications associated with the specified whereclause.
     * @throws SQLException when the retrieving of the identifications went wrong.
     */
    public static Identification[] getAllIdentificationsforWhereclause(Connection aConn, String aWhereAddition) throws SQLException {
        String sql =
                "select i.* from identification as i, spectrum as s where i.l_spectrumid = s.spectrumid";
        // Addition of the whereclause.
        sql += " AND " + aWhereAddition;
        PreparedStatement prep = aConn.prepareStatement(sql);
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new Identification(rs));
        }
        rs.close();
        prep.close();
        Identification[] lIDs = new Identification[v.size()];
        v.toArray(lIDs);

        return lIDs;
    }

        public void setAlternative(boolean hasAnAlternative){
        hasAlternative = hasAnAlternative;
    }

    public boolean getAlternative () {
        return hasAlternative;
    }


    /**
     * Returns the IdentificationID.
     *
     * @return String representing the Identification object.
     */
    public String toString() {
        return "" + iIdentificationid;
    }

    public HashMap getHashMap() {
        HashMap tempMap = new HashMap();
        tempMap.put("IDENTIFICATIONID",this.iIdentificationid);
        tempMap.put("SPECTRUMFILENAME",this.iSpectrumfilename);
        tempMap.put("DATFILENAME",iDatfilename);
        tempMap.put("L_SPECTRUMFILEID",this.iL_spectrumid);
        tempMap.put("L_DATFILEID",this.iL_datfileid);
        tempMap.put("DATFILE_QUERY",(int)this.iDatfile_query);
        tempMap.put("ACCESSION",this.iAccession);
        tempMap.put("START",this.iStart);
        tempMap.put("END",this.iEnd);
        tempMap.put("ENZYMATIC",this.iEnzymatic);
        tempMap.put("SEQUENCE",this.iSequence);
        tempMap.put("MODIFIED_SEQUENCE",this.iModified_sequence);
        tempMap.put("ION_COVERAGE",this.iIon_coverage);
        tempMap.put("SCORE",this.iScore);
        tempMap.put("HOMOLOGY",this.iHomology);
        tempMap.put("EXP_MASS",this.iExp_mass);
        tempMap.put("CAL_MASS",this.iCal_mass);
        tempMap.put("LIGHT_ISOTOPE",this.iLight_isotope);
        tempMap.put("HEAVY_ISOTOPE",this.iHeavy_isotope);
        tempMap.put("VALID",this.iValid);
        tempMap.put("DESCRIPTION",this.iDescription);
        tempMap.put("IDENTITYTHRESHOLD",this.iIdentitythreshold);
        tempMap.put("CONFIDENCE",this.iConfidence);
        tempMap.put("DB",this.iDb);
        tempMap.put("TITLE",this.iTitle);
        tempMap.put("PRECURSOR",this.iPrecursor);
        tempMap.put("CHARGE",this.iCharge);
        tempMap.put("ISOFORMS",this.iIsoforms);
        tempMap.put("DB_FILENAME",this.iDb_filename);
        tempMap.put("MASCOT_VERSION",this.iMascot_version);
        tempMap.put("USERNAME",this.iUsername);
        tempMap.put("CREATIONDATE",this.iCreationdate);
        tempMap.put("MODIFICATIONDATE",this.iModificationdate);
       return tempMap;
    }
}
