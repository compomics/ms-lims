package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas
 * Date: 29-Jun-2010
 * Time: 11:50:13
 * To change this template use File | Settings | File Templates.
 */
public class Modification_conversion extends Modification_conversionTableAccessor {
    // Class specific log4j logger for Modification_conversion instances.
    private static Logger logger = Logger.getLogger(LCRun.class);

    /**
     * Constructor
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Modification_conversion(ResultSet aRS) throws SQLException {
        super(aRS);
    }

    /**
     * Constructor
     * @param aMap HashMap to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Modification_conversion(HashMap aMap) throws SQLException {
        super(aMap);
    }

    /**
     * This method will give the version number as a double. If this is a pre 7.5 ms_lims version an error will be thrown
     * @param iConn
     * @return Double with the version
     * @throws SQLException
     */
    public static Vector<Modification_conversion> getAllModificationConversions(Connection iConn) throws SQLException {
        PreparedStatement ps = iConn.prepareStatement("select * from modification_conversion");
        ResultSet rs = ps.executeQuery();
        Vector<Modification_conversion> temp = new Vector<Modification_conversion>();
        while (rs.next()) {
            temp.add(new Modification_conversion(rs));
        }
        rs.close();
        ps.close();
        return temp;
    }

    /**
     * To string method
     */
    public String toString() {
        return this.iModification + ":" + this.iConversion;
    }
}