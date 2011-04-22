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
 * Date: 30-Nov-2010
 * Time: 14:05:55
 */

/**
 * This class implements a wrapper around the FragmentationTableAccessor.
 */

public class Fragmentation extends FragmentationTableAccessor {
    // Class specific log4j logger for Fragmentation instances.
    private static Logger logger = Logger.getLogger(Fragmentation.class);

    /**
     * Default constructor.
     */
    public Fragmentation() {
    }

    /**
     * The constructor
     *
     * @param aHm HashMap with the parameters.
     * @see FragmentationTableAccessor
     */
    public Fragmentation(HashMap aHm) {
        super(aHm);
    }

    /**
     * The constructor
     *
     * @param aRS ResultSet with the parameters.
     * @see FragmentationTableAccessor
     */
    public Fragmentation(ResultSet aRS) throws SQLException {
        super(aRS);
    }



    /**
     * This methods reads all fragmentations.
     *
     * @param aConn Connection to read the fragmentations from.
     * @return Vector<Fragmentation> with the fragmentations in the 'fragmentation' table.
     * @throws SQLException when the retrieving of the fragmentations went wrong.
     */
    public static Fragmentation[] getFragmentations(Connection aConn) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select * from fragmentation");
        ResultSet rs = prep.executeQuery();
        Vector<Fragmentation> v = new Vector<Fragmentation>();
        while (rs.next()) {
            v.add(new Fragmentation(rs));
        }
        rs.close();
        prep.close();
        Fragmentation[] lFragmentation = new Fragmentation[v.size()];
        v.toArray(lFragmentation);

        return lFragmentation;
    }


    public String toString(){
        return iDescription; 
    }
}