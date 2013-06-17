package com.compomics.mslimsdb.accessors;

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
 * This class is a generated accessor for the Validationtype table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ValidationtypeTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'validationtypeid' column.
	 */
	protected long iValidationtypeid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'name' column.
	 */
	protected String iName = null;


	/**
	 * This variable represents the key for the 'validationtypeid' column.
	 */
	public static final String VALIDATIONTYPEID = "VALIDATIONTYPEID";

	/**
	 * This variable represents the key for the 'name' column.
	 */
	public static final String NAME = "NAME";




	/**
	 * Default constructor.
	 */
	public ValidationtypeTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'ValidationtypeTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public ValidationtypeTableAccessor(HashMap aParams) {
		if(aParams.containsKey(VALIDATIONTYPEID)) {
			this.iValidationtypeid = ((Long)aParams.get(VALIDATIONTYPEID)).longValue();
		}
		if(aParams.containsKey(NAME)) {
			this.iName = (String)aParams.get(NAME);
		}
		this.iUpdated = true;
	}


	/**
	 * This constructor allows the creation of the 'ValidationtypeTableAccessor' object based on a resultset
	 * obtained by a 'select * from Validationtype' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ValidationtypeTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iValidationtypeid = aResultSet.getLong("validationtypeid");
		this.iName = (String)aResultSet.getObject("name");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Validationtypeid' column
	 * 
	 * @return	long	with the value for the Validationtypeid column.
	 */
	public long getValidationtypeid() {
		return this.iValidationtypeid;
	}

	/**
	 * This method returns the value for the 'Name' column
	 * 
	 * @return	String	with the value for the Name column.
	 */
	public String getName() {
		return this.iName;
	}

	/**
	 * This method sets the value for the 'Validationtypeid' column
	 * 
	 * @param	aValidationtypeid	long with the value for the Validationtypeid column.
	 */
	public void setValidationtypeid(long aValidationtypeid) {
		this.iValidationtypeid = aValidationtypeid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Name' column
	 * 
	 * @param	aName	String with the value for the Name column.
	 */
	public void setName(String aName) {
		this.iName = aName;
		this.iUpdated = true;
	}



	/**
	 * This method allows the caller to delete the data represented by this
	 * object in a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 */
	public int delete(Connection aConn) throws SQLException {
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM validationtype WHERE validationtypeid = ?");
		lStat.setLong(1, iValidationtypeid);
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
		if(!aKeys.containsKey(VALIDATIONTYPEID)) {
			throw new IllegalArgumentException("Primary key field 'VALIDATIONTYPEID' is missing in HashMap!");
		} else {
			iValidationtypeid = ((Long)aKeys.get(VALIDATIONTYPEID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM validationtype WHERE validationtypeid = ?");
		lStat.setLong(1, iValidationtypeid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iValidationtypeid = lRS.getLong("validationtypeid");
			iName = (String)lRS.getObject("name");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'validationtype' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'validationtype' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from validationtype";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<ValidationtypeTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<ValidationtypeTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<ValidationtypeTableAccessor>  entities = new ArrayList<ValidationtypeTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new ValidationtypeTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE validationtype SET validationtypeid = ?, name = ? WHERE validationtypeid = ?");
		lStat.setLong(1, iValidationtypeid);
		lStat.setObject(2, iName);
		lStat.setLong(3, iValidationtypeid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO validationtype (validationtypeid, name) values(?, ?)");
		if(iValidationtypeid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iValidationtypeid);
		}
		if(iName == null) {
			lStat.setNull(2, 12);
		} else {
			lStat.setObject(2, iName);
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
			iValidationtypeid = ((Number) iKeys[0]).longValue();
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