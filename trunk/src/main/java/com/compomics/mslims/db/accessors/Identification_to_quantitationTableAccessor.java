/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 15/01/2010
 * Time: 13:39:05
 */
package com.compomics.mslims.db.accessors;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.compomics.util.db.interfaces.*;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2007/07/06 09:41:53 $
 */

/**
 * This class is a generated accessor for the Identification_to_quantitation table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Identification_to_quantitationTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'itqid' column.
	 */
	protected long iItqid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_identificationid' column.
	 */
	protected long iL_identificationid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_quantitation_groupid' column.
	 */
	protected long iL_quantitation_groupid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'type' column.
	 */
	protected String iType = null;


	/**
	 * This variable represents the contents for the 'username' column.
	 */
	protected String iUsername = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'itqid' column.
	 */
	public static final String ITQID = "ITQID";

	/**
	 * This variable represents the key for the 'l_identificationid' column.
	 */
	public static final String L_IDENTIFICATIONID = "L_IDENTIFICATIONID";

	/**
	 * This variable represents the key for the 'l_quantitation_groupid' column.
	 */
	public static final String L_QUANTITATION_GROUPID = "L_QUANTITATION_GROUPID";

	/**
	 * This variable represents the key for the 'type' column.
	 */
	public static final String TYPE = "TYPE";

	/**
	 * This variable represents the key for the 'username' column.
	 */
	public static final String USERNAME = "USERNAME";

	/**
	 * This variable represents the key for the 'creationdate' column.
	 */
	public static final String CREATIONDATE = "CREATIONDATE";

	/**
	 * This variable represents the key for the 'modificationdate' column.
	 */
	public static final String MODIFICATIONDATE = "MODIFICATIONDATE";




	/**
	 * Default constructor.
	 */
	public Identification_to_quantitationTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'Identification_to_quantitationTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public Identification_to_quantitationTableAccessor(HashMap aParams) {
		if(aParams.containsKey(ITQID)) {
			this.iItqid = ((Long)aParams.get(ITQID)).longValue();
		}
		if(aParams.containsKey(L_IDENTIFICATIONID)) {
			this.iL_identificationid = ((Long)aParams.get(L_IDENTIFICATIONID)).longValue();
		}
		if(aParams.containsKey(L_QUANTITATION_GROUPID)) {
			this.iL_quantitation_groupid = ((Long)aParams.get(L_QUANTITATION_GROUPID)).longValue();
		}
		if(aParams.containsKey(TYPE)) {
			this.iType = (String)aParams.get(TYPE);
		}
		if(aParams.containsKey(USERNAME)) {
			this.iUsername = (String)aParams.get(USERNAME);
		}
		if(aParams.containsKey(CREATIONDATE)) {
			this.iCreationdate = (java.sql.Timestamp)aParams.get(CREATIONDATE);
		}
		if(aParams.containsKey(MODIFICATIONDATE)) {
			this.iModificationdate = (java.sql.Timestamp)aParams.get(MODIFICATIONDATE);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'Identification_to_quantitationTableAccessor' object based on a resultset
	 * obtained by a 'select * from Identification_to_quantitation' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Identification_to_quantitationTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iItqid = aResultSet.getLong("itqid");
		this.iL_identificationid = aResultSet.getLong("l_identificationid");
		this.iL_quantitation_groupid = aResultSet.getLong("l_quantitation_groupid");
		this.iType = (String)aResultSet.getObject("type");
		this.iUsername = (String)aResultSet.getObject("username");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Itqid' column
	 * 
	 * @return	long	with the value for the Itqid column.
	 */
	public long getItqid() {
		return this.iItqid;
	}

	/**
	 * This method returns the value for the 'L_identificationid' column
	 * 
	 * @return	long	with the value for the L_identificationid column.
	 */
	public long getL_identificationid() {
		return this.iL_identificationid;
	}

	/**
	 * This method returns the value for the 'L_quantitation_groupid' column
	 * 
	 * @return	long	with the value for the L_quantitation_groupid column.
	 */
	public long getL_quantitation_groupid() {
		return this.iL_quantitation_groupid;
	}

	/**
	 * This method returns the value for the 'Type' column
	 * 
	 * @return	String	with the value for the Type column.
	 */
	public String getType() {
		return this.iType;
	}

	/**
	 * This method returns the value for the 'Username' column
	 * 
	 * @return	String	with the value for the Username column.
	 */
	public String getUsername() {
		return this.iUsername;
	}

	/**
	 * This method returns the value for the 'Creationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Creationdate column.
	 */
	public java.sql.Timestamp getCreationdate() {
		return this.iCreationdate;
	}

	/**
	 * This method returns the value for the 'Modificationdate' column
	 * 
	 * @return	java.sql.Timestamp	with the value for the Modificationdate column.
	 */
	public java.sql.Timestamp getModificationdate() {
		return this.iModificationdate;
	}

	/**
	 * This method sets the value for the 'Itqid' column
	 * 
	 * @param	aItqid	long with the value for the Itqid column.
	 */
	public void setItqid(long aItqid) {
		this.iItqid = aItqid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'L_identificationid' column
	 * 
	 * @param	aL_identificationid	long with the value for the L_identificationid column.
	 */
	public void setL_identificationid(long aL_identificationid) {
		this.iL_identificationid = aL_identificationid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'L_quantitation_groupid' column
	 * 
	 * @param	aL_quantitation_groupid	long with the value for the L_quantitation_groupid column.
	 */
	public void setL_quantitation_groupid(long aL_quantitation_groupid) {
		this.iL_quantitation_groupid = aL_quantitation_groupid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Type' column
	 * 
	 * @param	aType	String with the value for the Type column.
	 */
	public void setType(String aType) {
		this.iType = aType;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Username' column
	 * 
	 * @param	aUsername	String with the value for the Username column.
	 */
	public void setUsername(String aUsername) {
		this.iUsername = aUsername;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Creationdate' column
	 * 
	 * @param	aCreationdate	java.sql.Timestamp with the value for the Creationdate column.
	 */
	public void setCreationdate(java.sql.Timestamp aCreationdate) {
		this.iCreationdate = aCreationdate;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Modificationdate' column
	 * 
	 * @param	aModificationdate	java.sql.Timestamp with the value for the Modificationdate column.
	 */
	public void setModificationdate(java.sql.Timestamp aModificationdate) {
		this.iModificationdate = aModificationdate;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM identification_to_quantitation WHERE itqid = ?");
		lStat.setLong(1, iItqid);
		int result = lStat.executeUpdate();
		lStat.close();
		return result;
	}


	/**
	 * This method allows the caller to read data for this
	 * object from a persistent store based on the specified keys.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public void retrieve(Connection aConn, HashMap aKeys) throws SQLException {
		// First check to see whether all PK fields are present.
		if(!aKeys.containsKey(ITQID)) {
			throw new IllegalArgumentException("Primary key field 'ITQID' is missing in HashMap!");
		} else {
			iItqid = ((Long)aKeys.get(ITQID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM identification_to_quantitation WHERE itqid = ?");
		lStat.setLong(1, iItqid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iItqid = lRS.getLong("itqid");
			iL_identificationid = lRS.getLong("l_identificationid");
			iL_quantitation_groupid = lRS.getLong("l_quantitation_groupid");
			iType = (String)lRS.getObject("type");
			iUsername = (String)lRS.getObject("username");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'identification_to_quantitation' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'identification_to_quantitation' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from identification_to_quantitation";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<Identification_to_quantitationTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<Identification_to_quantitationTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<Identification_to_quantitationTableAccessor>  entities = new ArrayList<Identification_to_quantitationTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new Identification_to_quantitationTableAccessor(rs));
		}
		rs.close();
		stat.close();
		return entities;
	}



	/**
	 * This method allows the caller to update the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int update(Connection aConn) throws SQLException {
		if(!this.iUpdated) {
			return 0;
		}
		PreparedStatement lStat = aConn.prepareStatement("UPDATE identification_to_quantitation SET itqid = ?, l_identificationid = ?, l_quantitation_groupid = ?, type = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE itqid = ?");
		lStat.setLong(1, iItqid);
		lStat.setLong(2, iL_identificationid);
		lStat.setLong(3, iL_quantitation_groupid);
		lStat.setObject(4, iType);
		lStat.setObject(5, iUsername);
		lStat.setObject(6, iCreationdate);
		lStat.setLong(7, iItqid);
		int result = lStat.executeUpdate();
		lStat.close();
		this.iUpdated = false;
		return result;
	}


	/**
	 * This method allows the caller to insert the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int persist(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO identification_to_quantitation (itqid, l_identificationid, l_quantitation_groupid, type, username, creationdate, modificationdate) values(?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iItqid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iItqid);
		}
		if(iL_identificationid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iL_identificationid);
		}
		if(iL_quantitation_groupid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iL_quantitation_groupid);
		}
		if(iType == null) {
			lStat.setNull(4, 12);
		} else {
			lStat.setObject(4, iType);
		}
		int result = lStat.executeUpdate();

		// Retrieving the generated keys (if any).
		ResultSet lrsKeys = lStat.getGeneratedKeys();
		ResultSetMetaData lrsmKeys = lrsKeys.getMetaData();
		int colCount = lrsmKeys.getColumnCount();
		iKeys = new Object[colCount];
		while(lrsKeys.next()) {
			for(int i=0;i<iKeys.length;i++) {
				iKeys[i] = lrsKeys.getObject(i+1);
			}
		}
		lrsKeys.close();
		lStat.close();
		// Verify that we have a single, generated key.
		if(iKeys != null && iKeys.length == 1) {
			// Since we have exactly one key specified, and only
			// one Primary Key column, we can infer that this was the
			// generated column, and we can therefore initialize it here.
			iItqid = ((Number) iKeys[0]).longValue();
		}
		this.iUpdated = false;
		return result;
	}

	/**
	 * This method will return the automatically generated key for the insert if 
	 * one was triggered, or 'null' otherwise.
	 *
	 * @return	Object[]	with the generated keys.
	 */
	public Object[] getGeneratedKeys() {
		return this.iKeys;
	}

}