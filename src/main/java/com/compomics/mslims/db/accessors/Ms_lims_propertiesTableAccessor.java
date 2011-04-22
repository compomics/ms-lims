package com.compomics.mslims.db.accessors;

/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 29/06/2010
 * Time: 11:31:17
 */

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
 * This class is a generated accessor for the Ms_lims_properties table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Ms_lims_propertiesTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'ms_lims_propertiesid' column.
	 */
	protected long iMs_lims_propertiesid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'key' column.
	 */
	protected String iKey = null;


	/**
	 * This variable represents the contents for the 'value' column.
	 */
	protected String iValue = null;


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
	 * This variable represents the key for the 'ms_lims_propertiesid' column.
	 */
	public static final String MS_LIMS_PROPERTIESID = "MS_LIMS_PROPERTIESID";

	/**
	 * This variable represents the key for the 'key' column.
	 */
	public static final String KEY = "KEY";

	/**
	 * This variable represents the key for the 'value' column.
	 */
	public static final String VALUE = "VALUE";

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
	public Ms_lims_propertiesTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'Ms_lims_propertiesTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public Ms_lims_propertiesTableAccessor(HashMap aParams) {
		if(aParams.containsKey(MS_LIMS_PROPERTIESID)) {
			this.iMs_lims_propertiesid = ((Long)aParams.get(MS_LIMS_PROPERTIESID)).longValue();
		}
		if(aParams.containsKey(KEY)) {
			this.iKey = (String)aParams.get(KEY);
		}
		if(aParams.containsKey(VALUE)) {
			this.iValue = (String)aParams.get(VALUE);
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
	 * This constructor allows the creation of the 'Ms_lims_propertiesTableAccessor' object based on a resultset
	 * obtained by a 'select * from Ms_lims_properties' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Ms_lims_propertiesTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iMs_lims_propertiesid = aResultSet.getLong("ms_lims_propertiesid");
		this.iKey = (String)aResultSet.getObject("key");
		this.iValue = (String)aResultSet.getObject("value");
		this.iUsername = (String)aResultSet.getObject("username");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Ms_lims_propertiesid' column
	 *
	 * @return	long	with the value for the Ms_lims_propertiesid column.
	 */
	public long getMs_lims_propertiesid() {
		return this.iMs_lims_propertiesid;
	}

	/**
	 * This method returns the value for the 'Key' column
	 *
	 * @return	String	with the value for the Key column.
	 */
	public String getKey() {
		return this.iKey;
	}

	/**
	 * This method returns the value for the 'Value' column
	 *
	 * @return	String	with the value for the Value column.
	 */
	public String getValue() {
		return this.iValue;
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
	 * This method sets the value for the 'Ms_lims_propertiesid' column
	 *
	 * @param	aMs_lims_propertiesid	long with the value for the Ms_lims_propertiesid column.
	 */
	public void setMs_lims_propertiesid(long aMs_lims_propertiesid) {
		this.iMs_lims_propertiesid = aMs_lims_propertiesid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Key' column
	 *
	 * @param	aKey	String with the value for the Key column.
	 */
	public void setKey(String aKey) {
		this.iKey = aKey;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Value' column
	 *
	 * @param	aValue	String with the value for the Value column.
	 */
	public void setValue(String aValue) {
		this.iValue = aValue;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM ms_lims_properties WHERE ms_lims_propertiesid = ?");
		lStat.setLong(1, iMs_lims_propertiesid);
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
		if(!aKeys.containsKey(MS_LIMS_PROPERTIESID)) {
			throw new IllegalArgumentException("Primary key field 'MS_LIMS_PROPERTIESID' is missing in HashMap!");
		} else {
			iMs_lims_propertiesid = ((Long)aKeys.get(MS_LIMS_PROPERTIESID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM ms_lims_properties WHERE ms_lims_propertiesid = ?");
		lStat.setLong(1, iMs_lims_propertiesid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iMs_lims_propertiesid = lRS.getLong("ms_lims_propertiesid");
			iKey = (String)lRS.getObject("key");
			iValue = (String)lRS.getObject("value");
			iUsername = (String)lRS.getObject("username");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'ms_lims_properties' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'ms_lims_properties' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from ms_lims_properties";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<Ms_lims_propertiesTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<Ms_lims_propertiesTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<Ms_lims_propertiesTableAccessor>  entities = new ArrayList<Ms_lims_propertiesTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new Ms_lims_propertiesTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE ms_lims_properties SET ms_lims_propertiesid = ?, key = ?, value = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE ms_lims_propertiesid = ?");
		lStat.setLong(1, iMs_lims_propertiesid);
		lStat.setObject(2, iKey);
		lStat.setObject(3, iValue);
		lStat.setObject(4, iUsername);
		lStat.setObject(5, iCreationdate);
		lStat.setLong(6, iMs_lims_propertiesid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO ms_lims_properties (ms_lims_propertiesid, key, value, username, creationdate, modificationdate) values(?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iMs_lims_propertiesid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iMs_lims_propertiesid);
		}
		if(iKey == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, iKey);
		}
		if(iValue == null) {
			lStat.setNull(3, 12);
		} else {
			lStat.setObject(3, iValue);
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
			iMs_lims_propertiesid = ((Number) iKeys[0]).longValue();
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