package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Davy
 * Date: 7/10/12
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class AlternativeFragmentIon extends AlternativeFragmentIonTableAccessor {

    private static Logger logger = Logger.getLogger(Fragmention.class);

    /**
     * Default constructor.
     */
    public AlternativeFragmentIon() {
    }

    /**
     * This constructor maps directly to the equivalent parent precursor.
     *
     * @param aHm HashMap with the parameters.
     * @see FragmentionTableAccessor
     */
    public AlternativeFragmentIon(HashMap aHm) {
        super(aHm);
    }

    /**
     * This constructor reads the fragmention from a resultset. The ResultSet should be positioned such that a single
     * row can be read directly (i.e., without calling the 'next()' method on the ResultSet). The columns should be in
     * this order: <br />
     * <p/>
     * Column 1: fragmentionid <br /> Column 2: l_identificationid <br /> Column 3: iontype <br /> Column 4: ionname <br
     * /> Column 5: l_ionscoringid <br /> Column 6: mz <br /> Column 7: intensity <br /> Column 8: fragmentionnumber <br
     * /> Column 9: massdelta <br /> Column 10: masserrormargin <br /> Column 11: username <br /> Column 12:
     * creationdate <br /> Column 13: modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public AlternativeFragmentIon(ResultSet aRS) throws SQLException {
        this.iAlternativeFragmentionid = aRS.getLong(1);
        this.iL_alternativeidentificationid = aRS.getLong(2);
        this.iIontype = aRS.getLong(3);
        iIonname = aRS.getString(4);
        iL_ionscoringid = aRS.getLong(5);
        iMz = aRS.getDouble(6);
        iIntensity = aRS.getLong(7);
        iFragmentionnumber = aRS.getLong(8);
        iMassdelta = aRS.getDouble(9);
        iMasserrormargin = aRS.getDouble(10);
        iUsername = aRS.getString(11);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(12);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(13);
    }

    /**
     * This method finds all fragmentions from the DB and stores them in a Collection of Fragmention instances.
     *
     * @param aConn             Connection from which to read the fragmentions.
     * @param anAlternativeIdentificationID long with the identificationid to retrieve the fragmentions for.
     * @return Collection with the Fragmention instances.
     * @throws java.sql.SQLException when the retrieve failed.
     */
    public static Collection getAllAlternativeFragmentions(Connection aConn, long anAlternativeIdentificationID) throws SQLException {
        Collection result = new Vector();

        String sql = "select fragmentionid, l_alternativeidentificationid, iontype, ionname, l_ionscoringid, mz, intensity,  fragmentionnumber, massdelta, masserrormargin, username, creationdate, modificationdate from fragmention where l_alternativeidentificationid = ?";
        PreparedStatement prep = aConn.prepareStatement(sql);
        prep.setLong(1, anAlternativeIdentificationID);
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
            result.add(new AlternativeFragmentIon(rs));
        }
        rs.close();
        prep.close();

        return result;
    }

    /**
     * This method returns the MascotDatfile FragmentIon representation for this fragment ion
     *
     * @return FragmentIonImpl  for this fragment ion.
     */

    /**
     * This method finds all fragmentions from the DB and stores them in a Collection of MascotDatfile FragmentIonImpl
     * instances.
     *
     * @param aConn             Connection from which to read the fragmentions.
     * @param anAlternativeIdentificationID long with the identificationid to retrieve the fragmentions for.
     * @return Collection with the MascotDatfile FragmentIonImpl instances.
     * @throws java.sql.SQLException when the retrieve failed.
     */

    /**
     * This method finds all fragmentions from the DB and stores them in a Collection of MascotDatfile FragmentIonImpl
     * instances.
     *
     * @param aConn             Connection from which to read the fragmentions.
     * @param anAlternativeIdentificationID long with the identificationid to retrieve the fragmentions for.
     * @param aIonType          long with the ion type.
     * @return Collection with the MascotDatfile FragmentIonImpl instances.
     * @throws java.sql.SQLException when the retrieve failed.
     */

    /**
     * This method finds all fragmentions from the DB and stores them in a Collection of MascotDatfile FragmentIonImpl
     * instances for the selected ion type.
     *
     * @param aConn             Connection from which to read the fragmentions.
     * @param anAlternativeIdentificationID long with the identificationid to retrieve the fragmentions for.
     * @param aIonType          long with the ion type.
     * @return Collection with the MascotDatfile FragmentIonImpl instances.
     * @throws java.sql.SQLException when the retrieve failed.
     */

    /**
     * Returns a toString value for the Fragmention values.
     *
     * @return That contains the ionname 'y', the number '3' and the mz '300.0'
     */
    public String toString() {
        return iIonname + iFragmentionnumber + "_" + iMz;
    }
}
