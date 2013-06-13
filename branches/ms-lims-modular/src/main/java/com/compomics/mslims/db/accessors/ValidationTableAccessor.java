package com.compomics.mslims.db.accessors;


import com.compomics.util.db.interfaces.Deleteable;
import com.compomics.util.db.interfaces.Persistable;
import com.compomics.util.db.interfaces.Retrievable;
import com.compomics.util.db.interfaces.Updateable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2007/07/06 09:41:53 $
 */

/**
 * This class is a generated accessor for the Validation table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ValidationTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'validationid' column.
	 */
	protected long iValidationid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_identificationid' column.
	 */
	protected long iL_identificationid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_validationtypeid' column.
	 */
	protected long iL_validationtypeid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'auto_comment' column.
	 */
	protected String iAuto_comment = null;


	/**
	 * This variable represents the contents for the 'manual_comment' column.
	 */
	protected String iManual_comment = null;


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
	 * This variable represents the key for the 'validationid' column.
	 */
	public static final String VALIDATIONID = "VALIDATIONID";

	/**
	 * This variable represents the key for the 'l_identificationid' column.
	 */
	public static final String L_IDENTIFICATIONID = "L_IDENTIFICATIONID";

	/**
	 * This variable represents the key for the 'l_validationtypeid' column.
	 */
	public static final String L_VALIDATIONTYPEID = "L_VALIDATIONTYPEID";

	/**
	 * This variable represents the key for the 'auto_comment' column.
	 */
	public static final String AUTO_COMMENT = "AUTO_COMMENT";

	/**
	 * This variable represents the key for the 'manual_comment' column.
	 */
	public static final String MANUAL_COMMENT = "MANUAL_COMMENT";

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
	public ValidationTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'ValidationTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public ValidationTableAccessor(HashMap aParams) {
		if(aParams.containsKey(VALIDATIONID)) {
			this.iValidationid = ((Long)aParams.get(VALIDATIONID)).longValue();
		}
		if(aParams.containsKey(L_IDENTIFICATIONID)) {
			this.iL_identificationid = ((Long)aParams.get(L_IDENTIFICATIONID)).longValue();
		}
		if(aParams.containsKey(L_VALIDATIONTYPEID)) {
			this.iL_validationtypeid = ((Long)aParams.get(L_VALIDATIONTYPEID)).longValue();
		}
		if(aParams.containsKey(AUTO_COMMENT)) {
			this.iAuto_comment = (String)aParams.get(AUTO_COMMENT);
		}
		if(aParams.containsKey(MANUAL_COMMENT)) {
			this.iManual_comment = (String)aParams.get(MANUAL_COMMENT);
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
	 * This constructor allows the creation of the 'ValidationTableAccessor' object based on a resultset
	 * obtained by a 'select * from Validation' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ValidationTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iValidationid = aResultSet.getLong("validationid");
		this.iL_identificationid = aResultSet.getLong("l_identificationid");
		this.iL_validationtypeid = aResultSet.getLong("l_validationtypeid");
		this.iAuto_comment = (String)aResultSet.getObject("auto_comment");
		this.iManual_comment = (String)aResultSet.getObject("manual_comment");
		this.iUsername = (String)aResultSet.getObject("username");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Validationid' column
	 * 
	 * @return	long	with the value for the Validationid column.
	 */
	public long getValidationid() {
		return this.iValidationid;
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
	 * This method returns the value for the 'L_validationtypeid' column
	 * 
	 * @return	long	with the value for the L_validationtypeid column.
	 */
	public long getL_validationtypeid() {
		return this.iL_validationtypeid;
	}

	/**
	 * This method returns the value for the 'Auto_comment' column
	 * 
	 * @return	String	with the value for the Auto_comment column.
	 */
	public String getAuto_comment() {
		return this.iAuto_comment;
	}

	/**
	 * This method returns the value for the 'Manual_comment' column
	 * 
	 * @return	String	with the value for the Manual_comment column.
	 */
	public String getManual_comment() {
		return this.iManual_comment;
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
	 * This method sets the value for the 'Validationid' column
	 * 
	 * @param	aValidationid	long with the value for the Validationid column.
	 */
	public void setValidationid(long aValidationid) {
		this.iValidationid = aValidationid;
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
	 * This method sets the value for the 'L_validationtypeid' column
	 * 
	 * @param	aL_validationtypeid	long with the value for the L_validationtypeid column.
	 */
	public void setL_validationtypeid(long aL_validationtypeid) {
		this.iL_validationtypeid = aL_validationtypeid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Auto_comment' column
	 * 
	 * @param	aAuto_comment	String with the value for the Auto_comment column.
	 */
	public void setAuto_comment(String aAuto_comment) {
		this.iAuto_comment = aAuto_comment;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Manual_comment' column
	 * 
	 * @param	aManual_comment	String with the value for the Manual_comment column.
	 */
	public void setManual_comment(String aManual_comment) {
		this.iManual_comment = aManual_comment;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM validation WHERE validationid = ?");
		lStat.setLong(1, iValidationid);
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
		if(!aKeys.containsKey(VALIDATIONID)) {
			throw new IllegalArgumentException("Primary key field 'VALIDATIONID' is missing in HashMap!");
		} else {
			iValidationid = ((Long)aKeys.get(VALIDATIONID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM validation WHERE validationid = ?");
		lStat.setLong(1, iValidationid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iValidationid = lRS.getLong("validationid");
			iL_identificationid = lRS.getLong("l_identificationid");
			iL_validationtypeid = lRS.getLong("l_validationtypeid");
			iAuto_comment = (String)lRS.getObject("auto_comment");
			iManual_comment = (String)lRS.getObject("manual_comment");
			iUsername = (String)lRS.getObject("username");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'validation' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'validation' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from validation";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<ValidationTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<ValidationTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<ValidationTableAccessor>  entities = new ArrayList<ValidationTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new ValidationTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE validation SET validationid = ?, l_identificationid = ?, l_validationtypeid = ?, auto_comment = ?, manual_comment = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE validationid = ?");
		lStat.setLong(1, iValidationid);
		lStat.setLong(2, iL_identificationid);
		lStat.setLong(3, iL_validationtypeid);
		lStat.setObject(4, iAuto_comment);
		lStat.setObject(5, iManual_comment);
		lStat.setObject(6, iUsername);
		lStat.setObject(7, iCreationdate);
		lStat.setLong(8, iValidationid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO validation (validationid, l_identificationid, l_validationtypeid, auto_comment, manual_comment, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iValidationid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iValidationid);
		}
		if(iL_identificationid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iL_identificationid);
		}
		if(iL_validationtypeid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iL_validationtypeid);
		}
		if(iAuto_comment == null) {
			lStat.setNull(4, -1);
		} else {
			lStat.setObject(4, iAuto_comment);
		}
		if(iManual_comment == null) {
			lStat.setNull(5, -1);
		} else {
			lStat.setObject(5, iManual_comment);
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
		if(iKeys != null && iKeys.length == 1 && iKeys[0] != null) {
			// Since we have exactly one key specified, and only
			// one Primary Key column, we can infer that this was the
			// generated column, and we can therefore initialize it here.
			iValidationid = ((Number) iKeys[0]).longValue();
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