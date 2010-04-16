/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 04-mar-2005
 * Time: 18:48:10
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.mascotdatfile.util.mascot.fragmentions.FragmentIonImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2009/11/06 09:19:49 $
 */

/**
 * This class implements a wrapper around the FragmentionTableAccessor.
 *
 * @author Lennart Martens
 */
public class Fragmention extends FragmentionTableAccessor {
    // Class specific log4j logger for Fragmention instances.
    private static Logger logger = Logger.getLogger(Fragmention.class);

    /**
     * Default constructor.
     */
    public Fragmention() {
    }

    /**
     * This constructor maps directly to the equivalent parent precursor.
     *
     * @param aHm HashMap with the parameters.
     * @see FragmentionTableAccessor
     */
    public Fragmention(HashMap aHm) {
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
    public Fragmention(ResultSet aRS) throws SQLException {
        this.iFragmentionid = aRS.getLong(1);
        this.iL_identificationid = aRS.getLong(2);
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
     * @param aIdentificationID long with the identificationid to retrieve the fragmentions for.
     * @return Collection with the Fragmention instances.
     * @throws java.sql.SQLException when the retrieve failed.
     */
    public static Collection getAllFragmentions(Connection aConn, long aIdentificationID) throws SQLException {
        Collection result = new Vector();

        String sql = "select fragmentionid, l_identificationid, iontype, ionname, l_ionscoringid, mz, intensity,  fragmentionnumber, massdelta, masserrormargin, username, creationdate, modificationdate from fragmention where l_identificationid = ?";
        PreparedStatement prep = aConn.prepareStatement(sql);
        prep.setLong(1, aIdentificationID);
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
            result.add(new Fragmention(rs));
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
    public FragmentIon asFragmentIonImpl() {
        FragmentIonImpl fii = new FragmentIonImpl(getMz().doubleValue(), getIntensity(), getMasserrormargin().doubleValue(), (int) getIontype(), (int) getFragmentionnumber(), getIonname());
        fii.setImportance((int) getL_ionscoringid());
        return fii;
    }

    /**
     * This method finds all fragmentions from the DB and stores them in a Collection of MascotDatfile FragmentIonImpl
     * instances.
     *
     * @param aConn             Connection from which to read the fragmentions.
     * @param aIdentificationID long with the identificationid to retrieve the fragmentions for.
     * @return Collection with the MascotDatfile FragmentIonImpl instances.
     * @throws java.sql.SQLException when the retrieve failed.
     */
    public static Vector getAllMascotDatfileFragmentIonImpl(Connection aConn, long aIdentificationID) throws SQLException {
        Collection temp = getAllFragmentions(aConn, aIdentificationID);
        Vector result = new Vector(temp.size());
        for (Iterator lIterator = temp.iterator(); lIterator.hasNext();) {
            Fragmention fi = (Fragmention) lIterator.next();
            result.add(fi.asFragmentIonImpl());
        }

        return result;
    }

    /**
     * This method finds all fragmentions from the DB and stores them in a Collection of MascotDatfile FragmentIonImpl
     * instances.
     *
     * @param aConn             Connection from which to read the fragmentions.
     * @param aIdentificationID long with the identificationid to retrieve the fragmentions for.
     * @param aIonType          long with the ion type.
     * @return Collection with the MascotDatfile FragmentIonImpl instances.
     * @throws java.sql.SQLException when the retrieve failed.
     */
    public static Collection getAllFragmentions(Connection aConn, long aIdentificationID, long aIonType) throws SQLException {
        Vector result = new Vector();

        String sql = "select fragmentionid, l_identificationid, iontype, ionname, l_ionscoringid, mz, intensity,  fragmentionnumber, massdelta, masserrormargin, username, creationdate, modificationdate from fragmention where l_identificationid = ? and iontype = ?";
        PreparedStatement prep = aConn.prepareStatement(sql);
        prep.setLong(1, aIdentificationID);
        prep.setLong(2, aIonType);
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
            result.add(new Fragmention(rs));
        }
        rs.close();
        prep.close();

        return result;
    }


    /**
     * This method finds all fragmentions from the DB and stores them in a Collection of MascotDatfile FragmentIonImpl
     * instances for the selected ion type.
     *
     * @param aConn             Connection from which to read the fragmentions.
     * @param aIdentificationID long with the identificationid to retrieve the fragmentions for.
     * @param aIonType          long with the ion type.
     * @return Collection with the MascotDatfile FragmentIonImpl instances.
     * @throws java.sql.SQLException when the retrieve failed.
     */
    public static Vector getAllMascotDatfileFragmentIonImpl(Connection aConn, long aIdentificationID, long aIonType) throws SQLException {
        Collection temp = getAllFragmentions(aConn, aIdentificationID);
        Vector result = new Vector(temp.size());
        for (Iterator lIterator = temp.iterator(); lIterator.hasNext();) {
            Fragmention fi = (Fragmention) lIterator.next();
            result.add(fi.asFragmentIonImpl());
        }

        return result;
    }

    /**
     * Returns a toString value for the Fragmention values.
     *
     * @return That contains the ionname 'y', the number '3' and the mz '300.0'
     */
    public String toString() {
        return iIonname + iFragmentionnumber + "_" + iMz;
    }

}
