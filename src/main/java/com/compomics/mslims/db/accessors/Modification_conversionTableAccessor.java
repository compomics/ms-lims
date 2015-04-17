package com.compomics.mslims.db.accessors;
/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 29/06/2010
 * Time: 11:31:34
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
 * This class is a generated accessor for the Modification_conversion table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class Modification_conversionTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'modification_conversionid' column.
	 */
	protected long iModification_conversionid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'modification' column.
	 */
	protected String iModification = null;


	/**
	 * This variable represents the contents for the 'conversion' column.
	 */
	protected String iConversion = null;


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
	 * This variable represents the key for the 'modification_conversionid' column.
	 */
	public static final String MODIFICATION_CONVERSIONID = "MODIFICATION_CONVERSIONID";

	/**
	 * This variable represents the key for the 'modification' column.
	 */
	public static final String MODIFICATION = "MODIFICATION";

	/**
	 * This variable represents the key for the 'conversion' column.
	 */
	public static final String CONVERSION = "CONVERSION";

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
	public Modification_conversionTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'Modification_conversionTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public Modification_conversionTableAccessor(HashMap aParams) {
		if(aParams.containsKey(MODIFICATION_CONVERSIONID)) {
			this.iModification_conversionid = ((Long)aParams.get(MODIFICATION_CONVERSIONID)).longValue();
		}
		if(aParams.containsKey(MODIFICATION)) {
			this.iModification = (String)aParams.get(MODIFICATION);
		}
		if(aParams.containsKey(CONVERSION)) {
			this.iConversion = (String)aParams.get(CONVERSION);
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
	 * This constructor allows the creation of the 'Modification_conversionTableAccessor' object based on a resultset
	 * obtained by a 'select * from Modification_conversion' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public Modification_conversionTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iModification_conversionid = aResultSet.getLong("modification_conversionid");
		this.iModification = (String)aResultSet.getObject("modification");
		this.iConversion = (String)aResultSet.getObject("conversion");
		this.iUsername = (String)aResultSet.getObject("username");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Modification_conversionid' column
	 *
	 * @return	long	with the value for the Modification_conversionid column.
	 */
	public long getModification_conversionid() {
		return this.iModification_conversionid;
	}

	/**
	 * This method returns the value for the 'Modification' column
	 *
	 * @return	String	with the value for the Modification column.
	 */
	public String getModification() {
		return this.iModification;
	}

	/**
	 * This method returns the value for the 'Conversion' column
	 *
	 * @return	String	with the value for the Conversion column.
	 */
	public String getConversion() {
		return this.iConversion;
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
	 * This method sets the value for the 'Modification_conversionid' column
	 *
	 * @param	aModification_conversionid	long with the value for the Modification_conversionid column.
	 */
	public void setModification_conversionid(long aModification_conversionid) {
		this.iModification_conversionid = aModification_conversionid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Modification' column
	 *
	 * @param	aModification	String with the value for the Modification column.
	 */
	public void setModification(String aModification) {
		this.iModification = aModification;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Conversion' column
	 *
	 * @param	aConversion	String with the value for the Conversion column.
	 */
	public void setConversion(String aConversion) {
		this.iConversion = aConversion;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM modification_conversion WHERE modification_conversionid = ?");
		lStat.setLong(1, iModification_conversionid);
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
		if(!aKeys.containsKey(MODIFICATION_CONVERSIONID)) {
			throw new IllegalArgumentException("Primary key field 'MODIFICATION_CONVERSIONID' is missing in HashMap!");
		} else {
			iModification_conversionid = ((Long)aKeys.get(MODIFICATION_CONVERSIONID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM modification_conversion WHERE modification_conversionid = ?");
		lStat.setLong(1, iModification_conversionid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iModification_conversionid = lRS.getLong("modification_conversionid");
			iModification = (String)lRS.getObject("modification");
			iConversion = (String)lRS.getObject("conversion");
			iUsername = (String)lRS.getObject("username");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'modification_conversion' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'modification_conversion' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from modification_conversion";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<Modification_conversionTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<Modification_conversionTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<Modification_conversionTableAccessor>  entities = new ArrayList<Modification_conversionTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new Modification_conversionTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE modification_conversion SET modification_conversionid = ?, modification = ?, conversion = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE modification_conversionid = ?");
		lStat.setLong(1, iModification_conversionid);
		lStat.setObject(2, iModification);
		lStat.setObject(3, iConversion);
		lStat.setObject(4, iUsername);
		lStat.setObject(5, iCreationdate);
		lStat.setLong(6, iModification_conversionid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO modification_conversion (modification_conversionid, modification, conversion, username, creationdate, modificationdate) values(?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iModification_conversionid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iModification_conversionid);
		}
		if(iModification == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, iModification);
		}
		if(iConversion == null) {
			lStat.setNull(3, 12);
		} else {
			lStat.setObject(3, iConversion);
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
			iModification_conversionid = ((Number) iKeys[0]).longValue();
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