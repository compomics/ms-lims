package com.compomics.mslims.db.accessors;

import java.util.HashMap;
import java.util.Vector;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas
 * Date: 15-Jan-2010
 * Time: 13:14:56
 * To change this template use File | Settings | File Templates.
 */
public class Quantitation_group  extends Quantitation_groupTableAccessor {

    /**
     * Default constructor.
     */
    public Quantitation_group() {
        super();
    }

    /**
     * Wrapper for the superclass constructor.
     *
     * @param aParams   HashMap with the parameters.
     */
    public Quantitation_group(HashMap aParams) {
        super(aParams);
    }

    /**
     * This constructor reads the project from a resultset. The ResultSet should be positioned such that
     * a single row can be read directly (i.e., without calling the 'next()' method on the ResultSet). <br />
     * The columns should be in this order: <br />
     * Column 1: quantitation_grouid <br />
     * Column 2: l_quantitation_fileid <br />
     * Column 3: file_ref <br />
     * Column 4: username <br />
     * Column 5: creationdate <br />
     * Column 6; modificationdate. <br />
     *
     * @param   aRS ResultSet to read the data from.
     * @exception   java.sql.SQLException    when reading the ResultSet failed.
     */
    public Quantitation_group(ResultSet aRS) throws SQLException {
        iQuantitation_groupid = aRS.getLong(1);
        iL_quantitation_fileid = aRS.getLong(2);
        iFile_ref = aRS.getString(3);
        iUsername = aRS.getString(4);
        iCreationdate = (java.sql.Timestamp)aRS.getObject(5);
        iModificationdate = (java.sql.Timestamp)aRS.getObject(6);
    }





    /**
     * Returns a String representation for this Project.
     *
     * @return String  with the String representation for this Project.
     */
    public String toString() {
        return  String.valueOf(iQuantitation_groupid);
    }


}