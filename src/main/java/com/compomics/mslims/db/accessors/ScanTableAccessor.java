/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 01/09/2010
 * Time: 15:36:30
 */
package com.compomics.mslimsdb.accessors;

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
 * This class is a generated accessor for the Scan table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class ScanTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'scanid' column.
	 */
	protected long iScanid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_spectrumid' column.
	 */
	protected long iL_spectrumid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'number' column.
	 */
	protected int iNumber = Integer.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'rtsec' column.
	 */
	protected Number iRtsec = null;


	/**
	 * This variable represents the contents for the 'creationdate' column.
	 */
	protected java.sql.Timestamp iCreationdate = null;


	/**
	 * This variable represents the contents for the 'modificationdate' column.
	 */
	protected java.sql.Timestamp iModificationdate = null;


	/**
	 * This variable represents the key for the 'scanid' column.
	 */
	public static final String SCANID = "SCANID";

	/**
	 * This variable represents the key for the 'l_spectrumid' column.
	 */
	public static final String L_SPECTRUMID = "L_SPECTRUMID";

	/**
	 * This variable represents the key for the 'number' column.
	 */
	public static final String NUMBER = "NUMBER";

	/**
	 * This variable represents the key for the 'rtsec' column.
	 */
	public static final String RTSEC = "RTSEC";

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
	public ScanTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'ScanTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public ScanTableAccessor(HashMap aParams) {
		if(aParams.containsKey(SCANID)) {
			this.iScanid = ((Long)aParams.get(SCANID)).longValue();
		}
		if(aParams.containsKey(L_SPECTRUMID)) {
			this.iL_spectrumid = ((Long)aParams.get(L_SPECTRUMID)).longValue();
		}
		if(aParams.containsKey(NUMBER)) {
			this.iNumber = ((Integer)aParams.get(NUMBER)).intValue();
		}
		if(aParams.containsKey(RTSEC)) {
			this.iRtsec = (Number)aParams.get(RTSEC);
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
	 * This constructor allows the creation of the 'ScanTableAccessor' object based on a resultset
	 * obtained by a 'select * from Scan' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public ScanTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iScanid = aResultSet.getLong("scanid");
		this.iL_spectrumid = aResultSet.getLong("l_spectrumid");
		this.iNumber = aResultSet.getInt("number");
		this.iRtsec = (Number)aResultSet.getObject("rtsec");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Scanid' column
	 * 
	 * @return	long	with the value for the Scanid column.
	 */
	public long getScanid() {
		return this.iScanid;
	}

	/**
	 * This method returns the value for the 'L_spectrumid' column
	 * 
	 * @return	long	with the value for the L_spectrumid column.
	 */
	public long getL_spectrumid() {
		return this.iL_spectrumid;
	}

	/**
	 * This method returns the value for the 'Number' column
	 * 
	 * @return	int	with the value for the Number column.
	 */
	public int getNumber() {
		return this.iNumber;
	}

	/**
	 * This method returns the value for the 'Rtsec' column
	 * 
	 * @return	Number	with the value for the Rtsec column.
	 */
	public Number getRtsec() {
		return this.iRtsec;
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
	 * This method sets the value for the 'Scanid' column
	 * 
	 * @param	aScanid	long with the value for the Scanid column.
	 */
	public void setScanid(long aScanid) {
		this.iScanid = aScanid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'L_spectrumid' column
	 * 
	 * @param	aL_spectrumid	long with the value for the L_spectrumid column.
	 */
	public void setL_spectrumid(long aL_spectrumid) {
		this.iL_spectrumid = aL_spectrumid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Number' column
	 * 
	 * @param	aNumber	int with the value for the Number column.
	 */
	public void setNumber(int aNumber) {
		this.iNumber = aNumber;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Rtsec' column
	 * 
	 * @param	aRtsec	Number with the value for the Rtsec column.
	 */
	public void setRtsec(Number aRtsec) {
		this.iRtsec = aRtsec;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM scan WHERE scanid = ?");
		lStat.setLong(1, iScanid);
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
		if(!aKeys.containsKey(SCANID)) {
			throw new IllegalArgumentException("Primary key field 'SCANID' is missing in HashMap!");
		} else {
			iScanid = ((Long)aKeys.get(SCANID)).longValue();
		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM scan WHERE scanid = ?");
		lStat.setLong(1, iScanid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iScanid = lRS.getLong("scanid");
			iL_spectrumid = lRS.getLong("l_spectrumid");
			iNumber = lRS.getInt("number");
			iRtsec = (Number)lRS.getObject("rtsec");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'scan' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'scan' table! Object is not initialized correctly!");
		}
	}
	/**
	 * This method allows the caller to obtain a basic select for this table.
	 *
	 * @return   String with the basic select statement for this table.
	 */
	public static String getBasicSelect(){
		return "select * from scan";
	}

	/**
	 * This method allows the caller to obtain all rows for this
	 * table from a persistent store.
	 *
	 * @param   aConn Connection to the persitent store.
	 * @return   ArrayList<ScanTableAccessor>   with all entries for this table.
	 */
	public static ArrayList<ScanTableAccessor> retrieveAllEntries(Connection aConn) throws SQLException {
		ArrayList<ScanTableAccessor>  entities = new ArrayList<ScanTableAccessor>();
		Statement stat = aConn.createStatement();
		ResultSet rs = stat.executeQuery(getBasicSelect());
		while(rs.next()) {
			entities.add(new ScanTableAccessor(rs));
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE scan SET scanid = ?, l_spectrumid = ?, number = ?, rtsec = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE scanid = ?");
		lStat.setLong(1, iScanid);
		lStat.setLong(2, iL_spectrumid);
		lStat.setInt(3, iNumber);
		lStat.setObject(4, iRtsec);
		lStat.setObject(5, iCreationdate);
		lStat.setLong(6, iScanid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO scan (scanid, l_spectrumid, number, rtsec, creationdate, modificationdate) values(?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iScanid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iScanid);
		}
		if(iL_spectrumid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iL_spectrumid);
		}
		if(iNumber == Integer.MIN_VALUE) {
			lStat.setNull(3, 5);
		} else {
			lStat.setInt(3, iNumber);
		}
		if(iRtsec == null) {
			lStat.setNull(4, 3);
		} else {
			lStat.setObject(4, iRtsec);
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
			iScanid = ((Number) iKeys[0]).longValue();
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