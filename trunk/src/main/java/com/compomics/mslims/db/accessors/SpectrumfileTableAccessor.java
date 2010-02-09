/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 19/06/2009
 * Time: 16:48:21
 */
package com.compomics.mslims.db.accessors;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.compomics.util.db.interfaces.*;

/*
 * CVS information:
 *
 * $Revision: 1.6 $
 * $Date: 2009/06/22 09:13:36 $
 */

/**
 * This class is a generated accessor for the Spectrumfile table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class SpectrumfileTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'spectrumfileid' column.
	 */
	protected long iSpectrumfileid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_lcrunid' column.
	 */
	protected long iL_lcrunid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_projectid' column.
	 */
	protected long iL_projectid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_instrumentid' column.
	 */
	protected long iL_instrumentid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'searched' column.
	 */
	protected long iSearched = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'identified' column.
	 */
	protected long iIdentified = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'file' column.
	 */
	protected byte[] iFile = null;


	/**
	 * This variable represents the contents for the 'filename' column.
	 */
	protected String iFilename = null;


	/**
	 * This variable represents the contents for the 'total_spectrum_intensity' column.
	 */
	protected Number iTotal_spectrum_intensity = null;


	/**
	 * This variable represents the contents for the 'highest_peak_in_spectrum' column.
	 */
	protected Number iHighest_peak_in_spectrum = null;


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
	 * This variable represents the key for the 'spectrumfileid' column.
	 */
	public static final String SPECTRUMFILEID = "SPECTRUMFILEID";

	/**
	 * This variable represents the key for the 'l_lcrunid' column.
	 */
	public static final String L_LCRUNID = "L_LCRUNID";

	/**
	 * This variable represents the key for the 'l_projectid' column.
	 */
	public static final String L_PROJECTID = "L_PROJECTID";

	/**
	 * This variable represents the key for the 'l_instrumentid' column.
	 */
	public static final String L_INSTRUMENTID = "L_INSTRUMENTID";

	/**
	 * This variable represents the key for the 'searched' column.
	 */
	public static final String SEARCHED = "SEARCHED";

	/**
	 * This variable represents the key for the 'identified' column.
	 */
	public static final String IDENTIFIED = "IDENTIFIED";

	/**
	 * This variable represents the key for the 'file' column.
	 */
	public static final String FILE = "FILE";

	/**
	 * This variable represents the key for the 'filename' column.
	 */
	public static final String FILENAME = "FILENAME";

	/**
	 * This variable represents the key for the 'total_spectrum_intensity' column.
	 */
	public static final String TOTAL_SPECTRUM_INTENSITY = "TOTAL_SPECTRUM_INTENSITY";

	/**
	 * This variable represents the key for the 'highest_peak_in_spectrum' column.
	 */
	public static final String HIGHEST_PEAK_IN_SPECTRUM = "HIGHEST_PEAK_IN_SPECTRUM";

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
	public SpectrumfileTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'SpectrumfileTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public SpectrumfileTableAccessor(HashMap aParams) {
		if(aParams.containsKey(SPECTRUMFILEID)) {
			this.iSpectrumfileid = ((Long)aParams.get(SPECTRUMFILEID)).longValue();
		}
		if(aParams.containsKey(L_LCRUNID)) {
			this.iL_lcrunid = ((Long)aParams.get(L_LCRUNID)).longValue();
		}
		if(aParams.containsKey(L_PROJECTID)) {
			this.iL_projectid = ((Long)aParams.get(L_PROJECTID)).longValue();
		}
		if(aParams.containsKey(L_INSTRUMENTID)) {
			this.iL_instrumentid = ((Long)aParams.get(L_INSTRUMENTID)).longValue();
		}
		if(aParams.containsKey(SEARCHED)) {
			this.iSearched = ((Long)aParams.get(SEARCHED)).longValue();
		}
		if(aParams.containsKey(IDENTIFIED)) {
			this.iIdentified = ((Long)aParams.get(IDENTIFIED)).longValue();
		}
		if(aParams.containsKey(FILE)) {
			this.iFile = (byte[])aParams.get(FILE);
		}
		if(aParams.containsKey(FILENAME)) {
			this.iFilename = (String)aParams.get(FILENAME);
		}
		if(aParams.containsKey(TOTAL_SPECTRUM_INTENSITY)) {
			this.iTotal_spectrum_intensity = (Number)aParams.get(TOTAL_SPECTRUM_INTENSITY);
		}
		if(aParams.containsKey(HIGHEST_PEAK_IN_SPECTRUM)) {
			this.iHighest_peak_in_spectrum = (Number)aParams.get(HIGHEST_PEAK_IN_SPECTRUM);
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
	 * This constructor allows the creation of the 'SpectrumfileTableAccessor' object based on a resultset
	 * obtained by a 'select * from Spectrumfile' query.
	 *
	 * @param	aResultSet	ResultSet with the required columns to initialize this object with.
	 * @exception	SQLException	when the ResultSet could not be read.
	 */
	public SpectrumfileTableAccessor(ResultSet aResultSet) throws SQLException {
		this.iSpectrumfileid = aResultSet.getLong("spectrumfileid");
		this.iL_lcrunid = aResultSet.getLong("l_lcrunid");
		this.iL_projectid = aResultSet.getLong("l_projectid");
		this.iL_instrumentid = aResultSet.getLong("l_instrumentid");
		this.iSearched = aResultSet.getLong("searched");
		this.iIdentified = aResultSet.getLong("identified");
		InputStream is6 = aResultSet.getBinaryStream("file");
		Vector bytes6 = new Vector();
		int reading = -1;
		try {
			while((reading = is6.read()) != -1) {
				bytes6.add(new Byte((byte)reading));
			}
			is6.close();
		} catch(IOException ioe) {
			bytes6 = new Vector();
		}
		reading = bytes6.size();
		this.iFile = new byte[reading];
		for(int i=0;i<reading;i++) {
			this.iFile[i] = ((Byte)bytes6.get(i)).byteValue();
		}
		this.iFilename = (String)aResultSet.getObject("filename");
		this.iTotal_spectrum_intensity = (Number)aResultSet.getObject("total_spectrum_intensity");
		this.iHighest_peak_in_spectrum = (Number)aResultSet.getObject("highest_peak_in_spectrum");
		this.iUsername = (String)aResultSet.getObject("username");
		this.iCreationdate = (java.sql.Timestamp)aResultSet.getObject("creationdate");
		this.iModificationdate = (java.sql.Timestamp)aResultSet.getObject("modificationdate");

		this.iUpdated = true;
	}


	/**
	 * This method returns the value for the 'Spectrumfileid' column
	 * 
	 * @return	long	with the value for the Spectrumfileid column.
	 */
	public long getSpectrumfileid() {
		return this.iSpectrumfileid;
	}

	/**
	 * This method returns the value for the 'L_lcrunid' column
	 * 
	 * @return	long	with the value for the L_lcrunid column.
	 */
	public long getL_lcrunid() {
		return this.iL_lcrunid;
	}

	/**
	 * This method returns the value for the 'L_projectid' column
	 * 
	 * @return	long	with the value for the L_projectid column.
	 */
	public long getL_projectid() {
		return this.iL_projectid;
	}

	/**
	 * This method returns the value for the 'L_instrumentid' column
	 * 
	 * @return	long	with the value for the L_instrumentid column.
	 */
	public long getL_instrumentid() {
		return this.iL_instrumentid;
	}

	/**
	 * This method returns the value for the 'Searched' column
	 * 
	 * @return	long	with the value for the Searched column.
	 */
	public long getSearched() {
		return this.iSearched;
	}

	/**
	 * This method returns the value for the 'Identified' column
	 * 
	 * @return	long	with the value for the Identified column.
	 */
	public long getIdentified() {
		return this.iIdentified;
	}

	/**
	 * This method returns the value for the 'File' column
	 * 
	 * @return	byte[]	with the value for the File column.
	 */
	public byte[] getFile() {
		return this.iFile;
	}

	/**
	 * This method returns the value for the 'Filename' column
	 * 
	 * @return	String	with the value for the Filename column.
	 */
	public String getFilename() {
		return this.iFilename;
	}

	/**
	 * This method returns the value for the 'Total_spectrum_intensity' column
	 * 
	 * @return	Number	with the value for the Total_spectrum_intensity column.
	 */
	public Number getTotal_spectrum_intensity() {
		return this.iTotal_spectrum_intensity;
	}

	/**
	 * This method returns the value for the 'Highest_peak_in_spectrum' column
	 * 
	 * @return	Number	with the value for the Highest_peak_in_spectrum column.
	 */
	public Number getHighest_peak_in_spectrum() {
		return this.iHighest_peak_in_spectrum;
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
	 * This method sets the value for the 'Spectrumfileid' column
	 * 
	 * @param	aSpectrumfileid	long with the value for the Spectrumfileid column.
	 */
	public void setSpectrumfileid(long aSpectrumfileid) {
		this.iSpectrumfileid = aSpectrumfileid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'L_lcrunid' column
	 * 
	 * @param	aL_lcrunid	long with the value for the L_lcrunid column.
	 */
	public void setL_lcrunid(long aL_lcrunid) {
		this.iL_lcrunid = aL_lcrunid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'L_projectid' column
	 * 
	 * @param	aL_projectid	long with the value for the L_projectid column.
	 */
	public void setL_projectid(long aL_projectid) {
		this.iL_projectid = aL_projectid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'L_instrumentid' column
	 * 
	 * @param	aL_instrumentid	long with the value for the L_instrumentid column.
	 */
	public void setL_instrumentid(long aL_instrumentid) {
		this.iL_instrumentid = aL_instrumentid;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Searched' column
	 * 
	 * @param	aSearched	long with the value for the Searched column.
	 */
	public void setSearched(long aSearched) {
		this.iSearched = aSearched;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Identified' column
	 * 
	 * @param	aIdentified	long with the value for the Identified column.
	 */
	public void setIdentified(long aIdentified) {
		this.iIdentified = aIdentified;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'File' column
	 * 
	 * @param	aFile	byte[] with the value for the File column.
	 */
	public void setFile(byte[] aFile) {
		this.iFile = aFile;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Filename' column
	 * 
	 * @param	aFilename	String with the value for the Filename column.
	 */
	public void setFilename(String aFilename) {
		this.iFilename = aFilename;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Total_spectrum_intensity' column
	 * 
	 * @param	aTotal_spectrum_intensity	Number with the value for the Total_spectrum_intensity column.
	 */
	public void setTotal_spectrum_intensity(Number aTotal_spectrum_intensity) {
		this.iTotal_spectrum_intensity = aTotal_spectrum_intensity;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Highest_peak_in_spectrum' column
	 * 
	 * @param	aHighest_peak_in_spectrum	Number with the value for the Highest_peak_in_spectrum column.
	 */
	public void setHighest_peak_in_spectrum(Number aHighest_peak_in_spectrum) {
		this.iHighest_peak_in_spectrum = aHighest_peak_in_spectrum;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM spectrumfile WHERE spectrumfileid = ?");
		lStat.setLong(1, iSpectrumfileid);
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
		if(!aKeys.containsKey(SPECTRUMFILEID)) {
			throw new IllegalArgumentException("Primary key field 'SPECTRUMFILEID' is missing in HashMap!");
		} else {
			iSpectrumfileid = ((Long)aKeys.get(SPECTRUMFILEID)).longValue()
;		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM spectrumfile WHERE spectrumfileid = ?");
		lStat.setLong(1, iSpectrumfileid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iSpectrumfileid = lRS.getLong("spectrumfileid");
			iL_lcrunid = lRS.getLong("l_lcrunid");
			iL_projectid = lRS.getLong("l_projectid");
			iL_instrumentid = lRS.getLong("l_instrumentid");
			iSearched = lRS.getLong("searched");
			iIdentified = lRS.getLong("identified");
			InputStream is6 = lRS.getBinaryStream("file");
			Vector bytes6 = new Vector();
			int reading = -1;
			try {
				while((reading = is6.read()) != -1) {
					bytes6.add(new Byte((byte)reading));
				}
				is6.close();
			} catch(IOException ioe) {
				bytes6 = new Vector();
			}
			reading = bytes6.size();
			iFile = new byte[reading];
			for(int i=0;i<reading;i++) {
				iFile[i] = ((Byte)bytes6.get(i)).byteValue();
			}
			iFilename = (String)lRS.getObject("filename");
			iTotal_spectrum_intensity = (Number)lRS.getObject("total_spectrum_intensity");
			iHighest_peak_in_spectrum = (Number)lRS.getObject("highest_peak_in_spectrum");
			iUsername = (String)lRS.getObject("username");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'spectrumfile' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'spectrumfile' table! Object is not initialized correctly!");
		}
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE spectrumfile SET spectrumfileid = ?, l_lcrunid = ?, l_projectid = ?, l_instrumentid = ?, searched = ?, identified = ?, file = ?, filename = ?, total_spectrum_intensity = ?, highest_peak_in_spectrum = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE spectrumfileid = ?");
		lStat.setLong(1, iSpectrumfileid);
		lStat.setLong(2, iL_lcrunid);
		lStat.setLong(3, iL_projectid);
		lStat.setLong(4, iL_instrumentid);
		lStat.setLong(5, iSearched);
		lStat.setLong(6, iIdentified);
		ByteArrayInputStream bais6 = new ByteArrayInputStream(iFile);
		lStat.setBinaryStream(7, bais6, iFile.length);
		lStat.setObject(8, iFilename);
		lStat.setObject(9, iTotal_spectrum_intensity);
		lStat.setObject(10, iHighest_peak_in_spectrum);
		lStat.setObject(11, iUsername);
		lStat.setObject(12, iCreationdate);
		lStat.setLong(13, iSpectrumfileid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO spectrumfile (spectrumfileid, l_lcrunid, l_projectid, l_instrumentid, searched, identified, file, filename, total_spectrum_intensity, highest_peak_in_spectrum, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iSpectrumfileid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iSpectrumfileid);
		}
		if(iL_lcrunid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iL_lcrunid);
		}
		if(iL_projectid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iL_projectid);
		}
		if(iL_instrumentid == Long.MIN_VALUE) {
			lStat.setNull(4, 4);
		} else {
			lStat.setLong(4, iL_instrumentid);
		}
		if(iSearched == Long.MIN_VALUE) {
			lStat.setNull(5, 4);
		} else {
			lStat.setLong(5, iSearched);
		}
		if(iIdentified == Long.MIN_VALUE) {
			lStat.setNull(6, 4);
		} else {
			lStat.setLong(6, iIdentified);
		}
		if(iFile == null) {
			lStat.setNull(7, -4);
		} else {
			ByteArrayInputStream bais6 = new ByteArrayInputStream(iFile);
			lStat.setBinaryStream(7, bais6, iFile.length);
		}
		if(iFilename == null) {
			lStat.setNull(8, 12);
		} else {
			lStat.setObject(8, iFilename);
		}
		if(iTotal_spectrum_intensity == null) {
			lStat.setNull(9, 3);
		} else {
			lStat.setObject(9, iTotal_spectrum_intensity);
		}
		if(iHighest_peak_in_spectrum == null) {
			lStat.setNull(10, 3);
		} else {
			lStat.setObject(10, iHighest_peak_in_spectrum);
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
			iSpectrumfileid = ((Number) iKeys[0]).longValue();
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