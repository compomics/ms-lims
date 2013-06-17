/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 31-jul-2003
 * Time: 14:28:47
 */
package com.compomics.mslimsdb.accessors;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:17 $
 */

/**
 * This class implements a wrapper around the PhosphorylationTableAccessor.
 *
 * @author Lennart Martens
 */
public class Phosphorylation extends PhosphorylationTableAccessor {
    // Class specific log4j logger for Phosphorylation instances.
    private static Logger logger = Logger.getLogger(Phosphorylation.class);

    public Phosphorylation(HashMap aParams) {
        super(aParams);
    }

    /**
     * This method retrieves the ID for this instance form the DB. (Particularly useful when the entry was just created
     * with an automatically generated key).
     *
     * @param aConn Connection to retrieve the PK from.
     * @return long with the key; this key is also filled out in the class itself.
     * @throws SQLException when the retrieving failed.
     */
    public long retrieveID(Connection aConn) throws SQLException {
        long temp = 0l;
        PreparedStatement ps = null;
        if (iScore != null) {
            ps = aConn.prepareStatement("select phosphorylationid from phosphorylation where l_status=? and residue=? and location=? and accession=? and context=? and score=? and threshold=?");
            ps.setLong(1, iL_status);
            ps.setString(2, iResidue);
            ps.setLong(3, iLocation);
            ps.setString(4, iAccession);
            ps.setString(5, iContext);
            ps.setObject(6, iScore);
            ps.setObject(7, iThreshold);
        } else {
            ps = aConn.prepareStatement("select phosphorylationid from phosphorylation where l_status=? and location=? and accession=? and description=?");
            ps.setLong(1, iL_status);
            ps.setLong(2, iLocation);
            ps.setString(3, iAccession);
            ps.setObject(4, iDescription);
        }

        ResultSet rs = ps.executeQuery();

        int counter = 0;
        while (rs.next()) {
            counter++;
            temp = rs.getLong(1);
        }
        rs.close();
        ps.close();
        if (counter != 1) {
            throw new SQLException("Select based on full phosphorylation data (" + iL_status + ", " + iResidue + ", " + iLocation + ", " + iAccession + ", " + iContext + ", " + iScore + ", " + iThreshold + ") resulted in " + counter + " results!");
        }

        this.iPhosphorylationid = temp;
        return temp;
    }
}
