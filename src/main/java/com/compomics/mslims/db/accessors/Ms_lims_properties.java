package com.compomics.mslims.db.accessors;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas
 * Date: 29-Jun-2010
 * Time: 11:33:23
 * To change this template use File | Settings | File Templates.
 */
public class Ms_lims_properties extends Ms_lims_propertiesTableAccessor {
    // Class specific log4j logger for Ms_lims_properties instances.
    private static Logger logger = Logger.getLogger(LCRun.class);

    /**
     * Constructor
     * @param aRS ResultSet to read the data from.
     * @throws java.sql.SQLException when reading the ResultSet failed.
     */
    public Ms_lims_properties(ResultSet aRS) throws SQLException {
        super(aRS);
    }


    /**
     * This method will give the version number as a double. If this is a pre 7.5 ms_lims version an error will be thrown
     * @param iConn
     * @return Double with the version
     * @throws SQLException
     */
    public static double getMs_limsVersion(Connection iConn) throws SQLException {
        PreparedStatement ps = iConn.prepareStatement("select p.value from ms_lims_properties as p where p.key ='version'");
        ResultSet rs = ps.executeQuery();
        String temp = "";
        while (rs.next()) {
            temp = rs.getString(1);
        }
        double result = Double.valueOf(temp) ;
        rs.close();
        ps.close();
        return result;
    }

     /**
     * This method will give the modification conversion version number as an int. If there is no version stored in ms_lims version number 0 will be given
     * @param iConn
     * @return Double with the version
     * @throws SQLException
     */
    public static int getModificationConversionVersion(Connection iConn)  {
         try{
             PreparedStatement ps = iConn.prepareStatement("select p.value from ms_lims_properties as p where p.key ='modification_conversion_version'");
             ResultSet rs = ps.executeQuery();
             String temp = "";
             while (rs.next()) {
                 temp = rs.getString(1);
             }
             int result = Integer.valueOf(temp) ;
             rs.close();
             ps.close();
             return result;
         } catch (SQLException e) {
             //
             return 0;
         }
     }

    /**
     * This method will set the new modification conversion version number.
     * @param iConn
     * @throws SQLException
     */
    public static void setModificationConversionVersion(Connection iConn, int aNewVersion) throws SQLException {
         PreparedStatement ps = iConn.prepareStatement("update ms_lims_properties as p set p.value ='" +aNewVersion + "', p.modificationdate = CURRENT_TIMESTAMP where p.key ='modification_conversion_version'");
         ps.executeUpdate();
     }

    /**
     * This method will give the version number as a double. If this is a pre 7.5 ms_lims version an error will be thrown
     * @param iConn
     * @return Double with the version
     * @throws SQLException
     */
    public static String getMs_lims_propertyForKey(Connection iConn, String lKey) throws SQLException {
        PreparedStatement ps = iConn.prepareStatement("select p.value from ms_lims_properties where p.key ='" + lKey + "'");
        ResultSet rs = ps.executeQuery();
        String temp = "";
        while (rs.next()) {
            temp = rs.getString(1);
        }
        rs.close();
        ps.close();
        return temp;
    }

    /**
     * To string method
     */
    public String toString() {
        return this.iKey + ":" + this.iValue;
    }

}
