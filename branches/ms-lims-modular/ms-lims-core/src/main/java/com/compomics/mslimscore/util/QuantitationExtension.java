/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util;

import com.compomics.mslimscore.util.quantitation.QuantitativeValidationSingleton;
import com.compomics.mslimsdb.accessors.Quantitation;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Davy
 */
public class QuantitationExtension extends Quantitation{
	// Class specific log4j logger for QuantitationExtension instances.
	 private static Logger logger = Logger.getLogger(QuantitationExtension.class);

    /**
     * The file name of the quantitation file where this Quantitation could be found in.
     */
    private String iQuantitationFileName;
    private String iFile_ref;

    /**
     * This constructor reads the project from a resultset. The ResultSet should be positioned such that
     * a single row can be read directly (i.e., without calling the 'next()' method on the ResultSet). <br />
     * The columns should be in this order: <br />
     * Column 1: quantitationid <br />
     * Column 2: l_quantitation_groupid <br />
     * Column 3: ratio <br />
     * Column 4: standard_error <br />
     * Column 5: type <br />
     * Column 6: valid <br />
     * Column 7: comment <br />
     * Column 8: username <br />
     * Column 9: creationdate <br />
     * Column 10; modificationdate. <br />
     *
     * @param   aRS ResultSet to read the data from.
     * @param   aQuantitationFileName String with the quantitation_file name
     * @exception   java.sql.SQLException    when reading the ResultSet failed.
     */
    public QuantitationExtension(ResultSet aRS, String aQuantitationFileName, String aFile_ref) throws SQLException {
        QuantitativeValidationSingleton iQuantitationSingelton = QuantitativeValidationSingleton.getInstance();
         if(iQuantitationSingelton.getMsLimsPre7_2()){
             iQuantitationid = aRS.getLong(1);
            iL_quantitation_groupid = aRS.getLong(4);
            iRatio = aRS.getDouble(5);
            iStandard_error = aRS.getDouble(6);
            iType = aRS.getString(7);
            iValid = aRS.getBoolean(8);
            iComment = aRS.getString(9);
            iUsername = aRS.getString(10);
            iCreationdate = (java.sql.Timestamp)aRS.getObject(11);
            iModificationdate = (java.sql.Timestamp)aRS.getObject(12);
            this.iQuantitationFileName = aQuantitationFileName;
            this.iFile_ref = aFile_ref;
         } else {
             iQuantitationid = aRS.getLong(1);
             iL_quantitation_groupid = aRS.getLong(2);
             iRatio = aRS.getDouble(3);
             iStandard_error = aRS.getDouble(4);
             iType = aRS.getString(5);
             iValid = aRS.getBoolean(6);
             iComment = aRS.getString(7);
             iUsername = aRS.getString(8);
             iCreationdate = (java.sql.Timestamp)aRS.getObject(9);
             iModificationdate = (java.sql.Timestamp)aRS.getObject(10);
             this.iQuantitationFileName = aQuantitationFileName;
             this.iFile_ref = aFile_ref;
         }

    }

    /**
     * Getter for iQuantitationFileName
     */
    public String getQuantitationFileName(){
        return iQuantitationFileName;
    }

    public String getFile_ref(){
        return iFile_ref;
    }

    /**
	 * This method allows the caller to update the data (with low priority) represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int updateLowPriority(Connection aConn) throws SQLException {
		if(!this.iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE LOW_PRIORITY quantitation SET quantitationid = ?, l_quantitation_groupid = ?, ratio = ?, standard_error = ?, type = ?, valid = ?, comment = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE quantitationid = ?");
		lStat.setLong(1, iQuantitationid);
		lStat.setLong(2, iL_quantitation_groupid);
		lStat.setDouble(3, iRatio);
		lStat.setDouble(4, iStandard_error);
		lStat.setObject(5, iType);
		lStat.setBoolean(6, iValid);
		lStat.setObject(7, iComment);
		lStat.setObject(8, iUsername);
		lStat.setObject(9, iCreationdate);
		lStat.setLong(10, iQuantitationid);
		int result = lStat.executeUpdate();
		lStat.close();
		this.iUpdated = false;
		return result;
	}

     /**
     * This method returns a Vector, containing all quantitation entries that could be retrieved, matching the
     * specified identificationids.
     *
     *
      * @param aIds      String with comma seperated ids ( 325322,243292,294302,234904,200432 )
      * @param aConn     Connection to read the data from.
      *
      * @return Vector with the matches (can be empty when no matches were found).
     *
     * @throws SQLException when the retrieve failed.
     */
    public static ArrayList<Quantitation> getQuantitationForIdentifications(String aIds, Connection aConn) throws SQLException {
        ArrayList<Quantitation> temp = new ArrayList<Quantitation>();
        String query;
        QuantitativeValidationSingleton iQuantitationSingelton = QuantitativeValidationSingleton.getInstance();
         if(iQuantitationSingelton.getMsLimsPre7_2()){
             query = "select q.*, e.filename from identification_to_quantitation as t, quantitation as q, quantitation_file as e where t.l_identificationid in (" + aIds + ") and t.quantitation_link = q.quantitation_link and e.quantitation_fileid = q.l_quantitation_fileid group by q.quantitationid";
         } else {
             query = "select q.*, e.filename, g.file_ref from identification_to_quantitation as t, quantitation as q, quantitation_file as e, quantitation_group as g where t.l_identificationid in (" + aIds + ") and t.l_quantitation_groupid = q.l_quantitation_groupid and q.l_quantitation_groupid = g.quantitation_groupid and e.quantitation_fileid = g.l_quantitation_fileid group by q.quantitationid";
         }

        PreparedStatement ps = aConn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            temp.add(new QuantitationExtension(rs, rs.getString("filename"), rs.getString("file_ref")));
        }
        rs.close();
        ps.close();

        return temp;
    }

}
