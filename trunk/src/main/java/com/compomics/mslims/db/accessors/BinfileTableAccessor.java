/*
 * Created by the DBAccessor generator.
 * Programmer: Lennart Martens
 * Date: 30/12/2005
 * Time: 12:12:01
 */
package com.compomics.mslims.db.accessors;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.compomics.util.db.interfaces.*;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2005/12/31 10:22:52 $
 */

/**
 * This class is a generated accessor for the Binfile table.
 *
 * @author DBAccessor generator class (Lennart Martens).
 */
public class BinfileTableAccessor implements Deleteable, Retrievable, Updateable, Persistable {

	/**
	 * This variable tracks changes to the object.
	 */
	protected boolean iUpdated = false;

	/**
	 * This variable can hold generated primary key columns.
	 */
	protected Object[] iKeys = null;

	/**
	 * This variable represents the contents for the 'binfileid' column.
	 */
	protected long iBinfileid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_projectid' column.
	 */
	protected long iL_projectid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'l_filedescriptionid' column.
	 */
	protected long iL_filedescriptionid = Long.MIN_VALUE;


	/**
	 * This variable represents the contents for the 'file' column.
	 */
	protected byte[] iFile = null;


	/**
	 * This variable represents the contents for the 'filename' column.
	 */
	protected String iFilename = null;


	/**
	 * This variable represents the contents for the 'comments' column.
	 */
	protected String iComments = null;


	/**
	 * This variable represents the contents for the 'originalpath' column.
	 */
	protected String iOriginalpath = null;


	/**
	 * This variable represents the contents for the 'originalhost' column.
	 */
	protected String iOriginalhost = null;


	/**
	 * This variable represents the contents for the 'originaluser' column.
	 */
	protected String iOriginaluser = null;


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
	 * This variable represents the key for the 'binfileid' column.
	 */
	public static final String BINFILEID = "BINFILEID";

	/**
	 * This variable represents the key for the 'l_projectid' column.
	 */
	public static final String L_PROJECTID = "L_PROJECTID";

	/**
	 * This variable represents the key for the 'l_filedescriptionid' column.
	 */
	public static final String L_FILEDESCRIPTIONID = "L_FILEDESCRIPTIONID";

	/**
	 * This variable represents the key for the 'file' column.
	 */
	public static final String FILE = "FILE";

	/**
	 * This variable represents the key for the 'filename' column.
	 */
	public static final String FILENAME = "FILENAME";

	/**
	 * This variable represents the key for the 'comments' column.
	 */
	public static final String COMMENTS = "COMMENTS";

	/**
	 * This variable represents the key for the 'originalpath' column.
	 */
	public static final String ORIGINALPATH = "ORIGINALPATH";

	/**
	 * This variable represents the key for the 'originalhost' column.
	 */
	public static final String ORIGINALHOST = "ORIGINALHOST";

	/**
	 * This variable represents the key for the 'originaluser' column.
	 */
	public static final String ORIGINALUSER = "ORIGINALUSER";

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
	public BinfileTableAccessor() {
	}

	/**
	 * This constructor allows the creation of the 'BinfileTableAccessor' object based on a set of values in the HashMap.
	 *
	 * @param	aParams	HashMap with the parameters to initialize this object with.
	 *		<i>Please use only constants defined on this class as keys in the HashMap!</i>
	 */
	public BinfileTableAccessor(HashMap aParams) {
		if(aParams.containsKey(BINFILEID)) {
			this.iBinfileid = ((Long)aParams.get(BINFILEID)).longValue();
		}
		if(aParams.containsKey(L_PROJECTID)) {
			this.iL_projectid = ((Long)aParams.get(L_PROJECTID)).longValue();
		}
		if(aParams.containsKey(L_FILEDESCRIPTIONID)) {
			this.iL_filedescriptionid = ((Long)aParams.get(L_FILEDESCRIPTIONID)).longValue();
		}
		if(aParams.containsKey(FILE)) {
			this.iFile = (byte[])aParams.get(FILE);
		}
		if(aParams.containsKey(FILENAME)) {
			this.iFilename = (String)aParams.get(FILENAME);
		}
		if(aParams.containsKey(COMMENTS)) {
			this.iComments = (String)aParams.get(COMMENTS);
		}
		if(aParams.containsKey(ORIGINALPATH)) {
			this.iOriginalpath = (String)aParams.get(ORIGINALPATH);
		}
		if(aParams.containsKey(ORIGINALHOST)) {
			this.iOriginalhost = (String)aParams.get(ORIGINALHOST);
		}
		if(aParams.containsKey(ORIGINALUSER)) {
			this.iOriginaluser = (String)aParams.get(ORIGINALUSER);
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
	 * This method returns the value for the 'Binfileid' column
	 * 
	 * @return	long	with the value for the Binfileid column.
	 */
	public long getBinfileid() {
		return this.iBinfileid;
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
	 * This method returns the value for the 'L_filedescriptionid' column
	 * 
	 * @return	long	with the value for the L_filedescriptionid column.
	 */
	public long getL_filedescriptionid() {
		return this.iL_filedescriptionid;
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
	 * This method returns the value for the 'Comments' column
	 * 
	 * @return	String	with the value for the Comments column.
	 */
	public String getComments() {
		return this.iComments;
	}

	/**
	 * This method returns the value for the 'Originalpath' column
	 * 
	 * @return	String	with the value for the Originalpath column.
	 */
	public String getOriginalpath() {
		return this.iOriginalpath;
	}

	/**
	 * This method returns the value for the 'Originalhost' column
	 * 
	 * @return	String	with the value for the Originalhost column.
	 */
	public String getOriginalhost() {
		return this.iOriginalhost;
	}

	/**
	 * This method returns the value for the 'Originaluser' column
	 * 
	 * @return	String	with the value for the Originaluser column.
	 */
	public String getOriginaluser() {
		return this.iOriginaluser;
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
	 * This method sets the value for the 'Binfileid' column
	 * 
	 * @param	aBinfileid	long with the value for the Binfileid column.
	 */
	public void setBinfileid(long aBinfileid) {
		this.iBinfileid = aBinfileid;
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
	 * This method sets the value for the 'L_filedescriptionid' column
	 * 
	 * @param	aL_filedescriptionid	long with the value for the L_filedescriptionid column.
	 */
	public void setL_filedescriptionid(long aL_filedescriptionid) {
		this.iL_filedescriptionid = aL_filedescriptionid;
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
	 * This method sets the value for the 'Comments' column
	 * 
	 * @param	aComments	String with the value for the Comments column.
	 */
	public void setComments(String aComments) {
		this.iComments = aComments;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Originalpath' column
	 * 
	 * @param	aOriginalpath	String with the value for the Originalpath column.
	 */
	public void setOriginalpath(String aOriginalpath) {
		this.iOriginalpath = aOriginalpath;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Originalhost' column
	 * 
	 * @param	aOriginalhost	String with the value for the Originalhost column.
	 */
	public void setOriginalhost(String aOriginalhost) {
		this.iOriginalhost = aOriginalhost;
		this.iUpdated = true;
	}

	/**
	 * This method sets the value for the 'Originaluser' column
	 * 
	 * @param	aOriginaluser	String with the value for the Originaluser column.
	 */
	public void setOriginaluser(String aOriginaluser) {
		this.iOriginaluser = aOriginaluser;
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
		PreparedStatement lStat = aConn.prepareStatement("DELETE FROM binfile WHERE binfileid = ?");
		lStat.setLong(1, iBinfileid);
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
		if(!aKeys.containsKey(BINFILEID)) {
			throw new IllegalArgumentException("Primary key field 'BINFILEID' is missing in HashMap!");
		} else {
			iBinfileid = ((Long)aKeys.get(BINFILEID)).longValue()
;		}
		// In getting here, we probably have all we need to continue. So let's...
		PreparedStatement lStat = aConn.prepareStatement("SELECT * FROM binfile WHERE binfileid = ?");
		lStat.setLong(1, iBinfileid);
		ResultSet lRS = lStat.executeQuery();
		int hits = 0;
		while(lRS.next()) {
			hits++;
			iBinfileid = lRS.getLong("binfileid");
			iL_projectid = lRS.getLong("l_projectid");
			iL_filedescriptionid = lRS.getLong("l_filedescriptionid");
			InputStream is3 = lRS.getBinaryStream("file");
			Vector bytes3 = new Vector();
			int reading = -1;
			try {
				while((reading = is3.read()) != -1) {
					bytes3.add(new Byte((byte)reading));
				}
				is3.close();
			} catch(IOException ioe) {
				bytes3 = new Vector();
			}
			reading = bytes3.size();
			iFile = new byte[reading];
			for(int i=0;i<reading;i++) {
				iFile[i] = ((Byte)bytes3.get(i)).byteValue();
			}
			iFilename = (String)lRS.getObject("filename");
			iComments = (String)lRS.getObject("comments");
			iOriginalpath = (String)lRS.getObject("originalpath");
			iOriginalhost = (String)lRS.getObject("originalhost");
			iOriginaluser = (String)lRS.getObject("originaluser");
			iUsername = (String)lRS.getObject("username");
			iCreationdate = (java.sql.Timestamp)lRS.getObject("creationdate");
			iModificationdate = (java.sql.Timestamp)lRS.getObject("modificationdate");
		}
		lRS.close();
		lStat.close();
		if(hits>1) {
			throw new SQLException("More than one hit found for the specified primary keys in the 'binfile' table! Object is initialized to last row returned.");
		} else if(hits == 0) {
			throw new SQLException("No hits found for the specified primary keys in the 'binfile' table! Object is not initialized correctly!");
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
		PreparedStatement lStat = aConn.prepareStatement("UPDATE binfile SET binfileid = ?, l_projectid = ?, l_filedescriptionid = ?, file = ?, filename = ?, comments = ?, originalpath = ?, originalhost = ?, originaluser = ?, username = ?, creationdate = ?, modificationdate = CURRENT_TIMESTAMP WHERE binfileid = ?");
		lStat.setLong(1, iBinfileid);
		lStat.setLong(2, iL_projectid);
		lStat.setLong(3, iL_filedescriptionid);
		ByteArrayInputStream bais3 = new ByteArrayInputStream(iFile);
		lStat.setBinaryStream(4, bais3, iFile.length);
		lStat.setObject(5, iFilename);
		lStat.setObject(6, iComments);
		lStat.setObject(7, iOriginalpath);
		lStat.setObject(8, iOriginalhost);
		lStat.setObject(9, iOriginaluser);
		lStat.setObject(10, iUsername);
		lStat.setObject(11, iCreationdate);
		lStat.setLong(12, iBinfileid);
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
		PreparedStatement lStat = aConn.prepareStatement("INSERT INTO binfile (binfileid, l_projectid, l_filedescriptionid, file, filename, comments, originalpath, originalhost, originaluser, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
		if(iBinfileid == Long.MIN_VALUE) {
			lStat.setNull(1, 4);
		} else {
			lStat.setLong(1, iBinfileid);
		}
		if(iL_projectid == Long.MIN_VALUE) {
			lStat.setNull(2, 4);
		} else {
			lStat.setLong(2, iL_projectid);
		}
		if(iL_filedescriptionid == Long.MIN_VALUE) {
			lStat.setNull(3, 4);
		} else {
			lStat.setLong(3, iL_filedescriptionid);
		}
		if(iFile == null) {
			lStat.setNull(4, -4);
		} else {
			ByteArrayInputStream bais3 = new ByteArrayInputStream(iFile);
			lStat.setBinaryStream(4, bais3, iFile.length);
		}
		if(iFilename == null) {
			lStat.setNull(5, 12);
		} else {
			lStat.setObject(5, iFilename);
		}
		if(iComments == null) {
			lStat.setNull(6, -1);
		} else {
			lStat.setObject(6, iComments);
		}
		if(iOriginalpath == null) {
			lStat.setNull(7, 12);
		} else {
			lStat.setObject(7, iOriginalpath);
		}
		if(iOriginalhost == null) {
			lStat.setNull(8, 12);
		} else {
			lStat.setObject(8, iOriginalhost);
		}
		if(iOriginaluser == null) {
			lStat.setNull(9, 12);
		} else {
			lStat.setObject(9, iOriginaluser);
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