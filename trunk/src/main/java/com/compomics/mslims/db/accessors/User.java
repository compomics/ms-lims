/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 24-jun-2003
 * Time: 16:09:07
 */
package com.compomics.mslims.db.accessors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2005/12/31 10:22:52 $
 */

/**
 * This class provides the following enhancements over the UserTableAccessor:
 *
 * <ul>
 *   <li><i>constructor</i>: to read a single User from a ResultSet.</li>
 *   <li><b>toString()</b>: returns the name of the user.</li>
 * </ul>
 *
 * @author Lennart Martens
 */
public class User extends UserTableAccessor {

    /**
     * Simple wrapper for the superclass constructor.
     *
     * @param aParams   HashMap with the parameters.
     */
    public User(HashMap aParams) {
        super(aParams);
    }

    /**
     * This constructor sets the only 'settable' field: username.
     *
     * @param aName String with the full name for the user.
     */
    public User(String aName) {
        super.setName(aName);
    }

    /**
     * This constructor reads a User from a ResultSet. The ResultSet should be positioned such that
     * a single row can be read directly (i.e., without calling the 'next()' method on the ResultSet). <br />
     * The columns should be in this order: <br />
     * Column 1: user ID <br />
     * Column 2: name <br />
     * Column 3: username <br />
     * Column 4: creationdate <br />
     * Column 5: modificationdate.
     *
     * @param   aRS ResultSet to read the data from.
     * @exception   java.sql.SQLException    when reading the ResultSet failed.
     */
    public User(ResultSet aRS) throws SQLException {
        this.iUserid = aRS.getLong(1);
        this.iName = aRS.getString(2);
        iUsername = aRS.getString(3);
        iCreationdate = (java.sql.Timestamp)aRS.getObject(4);
        iModificationdate = (java.sql.Timestamp)aRS.getObject(5);
    }

    /**
     * This method retrieves all users from the connection and stores them in a HashMap. <br />
     * The userid is the key (Long type) and the User object is the value.
     *
     * @param aConn Connection to retrieve the users from.
     * @return  HashMap with the users, userid is the key (Long type) and User objects are the values.
     * @throws SQLException when the retrieve failed.
     */
    public static HashMap getAllUsersAsMap(Connection aConn) throws SQLException {
        HashMap lUsers = new HashMap();
        PreparedStatement prep = aConn.prepareStatement("select userid, name, username, creationdate, modificationdate from user");
        ResultSet rs = prep.executeQuery();
        while(rs.next()) {
            User temp = new User(rs);
            lUsers.put(new Long(temp.getUserid()),temp);
        }
        rs.close();
        prep.close();

        return lUsers;
    }

    /**
     * This method retrieves all users from the connection and stores them in a User[].
     *
     * @param aConn Connection to retrieve the users from.
     * @return  User[] with the users.
     * @throws SQLException when the retrieve failed.
     */
    public static User[] getAllUsers(Connection aConn) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select userid, name, username, creationdate, modificationdate from user");
        ResultSet rs = prep.executeQuery();
        Vector temp = new Vector();
        while(rs.next()) {
            temp.add(new User(rs));
        }
        User[] result = new User[temp.size()];
        temp.toArray(result);
        rs.close();
        prep.close();
        return result;
    }


    /**
     * This method returns a String representation of the User, ie.: the name.
     *
     * @return  String  with the name of the user.
     */
    public String toString() {
        return this.iName;
    }
}
