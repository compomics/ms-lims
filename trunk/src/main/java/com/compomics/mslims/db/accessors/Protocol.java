/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-jun-2003
 * Time: 18:48:10
 */
package com.compomics.mslimsdb.accessors;

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
 * $Revision: 1.1 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class implements a wrapper around the ProtocolTableAccessor.
 *
 * @author Lennart Martens
 */
public class Protocol extends ProtocolTableAccessor {
    // Class specific log4j logger for Protocol instances.
    private static Logger logger = Logger.getLogger(Protocol.class);

    /**
     * Default constructor.
     */
    public Protocol() {
    }

    /**
     * This constructor reads the PKL file from a resultset. The ResultSet should be positioned such that a single row
     * can be read directly (i.e., without calling the 'next()' method on the ResultSet). The columns should be in this
     * order: <br /> Column 1: protocolid <br /> Column 2: type <br /> Column 3: description <br /> Column 4: username
     * <br /> Column 5: creationdate <br /> Column 6: modificationdate.
     *
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Protocol(ResultSet aRS) throws SQLException {
        this.iProtocolid = aRS.getLong(1);
        this.iType = aRS.getString(2);
        this.iDescription = aRS.getString(3);
        iUsername = aRS.getString(4);
        iCreationdate = (java.sql.Timestamp) aRS.getObject(5);
        iModificationdate = (java.sql.Timestamp) aRS.getObject(6);
    }

    /**
     * This method finds all Protocol types from the DB and stores them in a HashMap, with their ID number as key (Long
     * type).
     *
     * @param aConn Connection from which to read the Protocol types.
     * @return HashMap with the Protocol types as values, and their respective ID's as keys (Long type).
     * @throws SQLException when the retrieve failed.
     */
    public static HashMap getAllProtocolsAsMap(Connection aConn) throws SQLException {
        HashMap lProtocol = new HashMap();

        PreparedStatement prep = aConn.prepareStatement("select protocolid, type, description, username, creationdate, modificationdate from protocol");
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
            Protocol temp = new Protocol(rs);
            lProtocol.put(new Long(temp.getProtocolid()), temp);
        }
        rs.close();
        prep.close();

        return lProtocol;
    }

    /**
     * This method finds all Protocol types from the DB and stores them in a Protocol[].
     *
     * @param aConn Connection from which to read the Protocol types.
     * @return Protocol[] with the Protocol types as values.
     * @throws SQLException when the retrieve failed.
     */
    public static Protocol[] getAllProtocols(Connection aConn) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select protocolid, type, description, username, creationdate, modificationdate from protocol");
        ResultSet rs = prep.executeQuery();
        Vector temp = new Vector();
        while (rs.next()) {
            temp.add(new Protocol(rs));
        }
        Protocol[] lProtocol = new Protocol[temp.size()];
        temp.toArray(lProtocol);
        rs.close();
        prep.close();

        return lProtocol;
    }

    /**
     * This method returns a String representation for this Protocol.
     *
     * @return String  with the String description for this protocol.
     */
    public String toString() {
        return this.iType;
    }
}
