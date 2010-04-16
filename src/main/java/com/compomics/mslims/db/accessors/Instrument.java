/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2003
 * Time: 16:15:36
 */
package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2005/12/31 10:22:52 $
 */

/**
 * This class provides the following enhancements over the InstrumentTableAccessor:
 * <p/>
 * <ul> <li><i>constructor</i>: to read a single Instrument from a ResultSet.</li> <li><b>toString()</b>: returns the
 * name of the Instrument.</li> <li><b>hashCode()</b>: returns a hashcode for the Instrument (which is just the
 * Instrument's ID).</li> </ul>
 *
 * @author Lennart Martens
 */
public class Instrument extends InstrumentTableAccessor {
    // Class specific log4j logger for Instrument instances.
    private static Logger logger = Logger.getLogger(Instrument.class);

    /**
     * Default constructor.
     */
    public Instrument() {
        super();
    }

    /**
     * Wrapper for the superclass constructor.
     *
     * @param aParams HashMap with the parameters.
     */
    public Instrument(HashMap aParams) {
        super(aParams);
    }

    /**
     * This constructor reads the instrument from a resultset. The ResultSet should be positioned such that a single row
     * can be read directly (i.e., without calling the 'next()' method on the ResultSet). <br /> The columns should be
     * in this order: <br /> Column 1: instrument ID <br /> Column 2: name of the instrument <br /> Column 3: the
     * description for the instrument <br /> Column 4: the storageclassname for the instrument <br /> Column 5: the
     * propertiesfilename for the instrument <br /> Column 6: the differential calibration for the instrument <br />
     * Column 7: username <br /> Column 8: creationdate <br /> Column 9: modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws SQLException when reading the ResultSet failed.
     */
    public Instrument(ResultSet aRS) throws SQLException {
        iInstrumentid = aRS.getLong(1);
        iName = aRS.getString(2);
        iDescription = aRS.getString(3);
        iStorageclassname = aRS.getString(4);
        iPropertiesfilename = aRS.getString(5);
        iDifferential_calibration = (Number) aRS.getObject(6);
        iUsername = aRS.getString(7);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(8);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(9);
    }

    /**
     * This methods reads all instruments from the Instrument table.
     *
     * @param aConn Connection to read the instruments from.
     * @return Instrument[] with the instruments in the 'Instrument' table.
     * @throws SQLException when the retrieving of the instruments went wrong.
     */
    public static Instrument[] getAllInstruments(Connection aConn) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select instrumentid, name, description, storageclassname, propertiesfilename, differential_calibration, username, creationdate, modificationdate from instrument order by instrumentid ASC");
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new Instrument(rs));
        }
        rs.close();
        prep.close();
        Instrument[] lInstruments = new Instrument[v.size()];
        v.toArray(lInstruments);

        return lInstruments;
    }

    /**
     * This methods reads all instrumentss from the Instrument table that have a calibrated differential standard
     * deviation.
     *
     * @param aConn Connection to read the instruments from.
     * @return Instrument[] with the instruments in the 'Instrument' table.
     * @throws SQLException when the retrieving of the instruments went wrong.
     */
    public static Instrument[] getAllDifferentialCalibratedInstruments(Connection aConn) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select instrumentid, name, description, storageclassname, propertiesfilename, differential_calibration, username, creationdate, modificationdate from instrument where differential_calibration is not null and differential_calibration > 0 order by instrumentid ASC");
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while (rs.next()) {
            v.add(new Instrument(rs));
        }
        rs.close();
        prep.close();
        Instrument[] lInstruments = new Instrument[v.size()];
        v.toArray(lInstruments);

        return lInstruments;
    }

    /**
     * Returns a String representation for this Instrument.
     *
     * @return String  with the String representation for this Instrument.
     */
    public String toString() {
        return this.iName;
    }

    /**
     * Returns a hashcode for the Instrument. <br /> The hashcode is just the InstrumentID, cast to int, which is the PK
     * on the table.
     *
     * @return int with the hashcode
     */
    public int hashCode() {
        return (int) this.iInstrumentid;
    }
}
