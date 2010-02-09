/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 04-mrt-05
 * Time: 17:09:23
 */
package com.compomics.mslims.db.accessors;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.*;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2005/12/31 13:11:17 $
 */

/**
 * This class adds XXX useful methods to the BinfileTableAccessor class:
 * <ul>
 *   <li><b>getUnzippedFile()</b>: returns the binfile as a stream of unzipped bytes.</li>
 *   <li><b></b>setUnzippedFile(byte[] aBytes): allows the setting and simultaneous zipping of unzipped bytes.</li>
 *   <li><b></b>setFileFromName(String aFilename): allows the setting and simultaneous zipping of a file
 *      and filename from the fully qualified file name.</li>
 * </ul>
 *
 * @author Lennart Martens
 */
public class Binfile extends BinfileTableAccessor {

    /**
     * This key in the HashMap allows the setting of file and
     * filename to be replaced by the fully qualified filename only.
     */
    public static final String FROMFILE = "FROMFILE";

    /**
     * This constructor just maps the superclass constructor.
     *
     * @param   aParams HashMap with the values to set.
     */
    public Binfile(HashMap aParams) {
        super(aParams);
        if(aParams.containsKey(FROMFILE)) {
            try {
                this.setFileFromName((String)aParams.get(FROMFILE));
            } catch(IOException ioe) {
                throw new IllegalArgumentException("Unable to process file '" + (String)aParams.get(FROMFILE) + "': " + ioe.getMessage() + "!");
            }
        }
    }

    /**
     * This constructor reads the binary file from a resultset. The ResultSet should be positioned such that
     * a single row can be read directly (i.e., without calling the 'next()' method on the ResultSet).
     * The columns should be in this order: <br />
     * Column 1: binfile ID <br />
     * Column 2: l_project ID <br />
     * Column 3: l_filedescription ID <br />
     * Column 4: GZIPped or ZIPped bytes for the file or folder <br />
     * Column 5: filename <br />
     * Column 6: comments <br />
     * Column 7: originalpath <br />
     * Column 8: originalhost <br />
     * Column 9: originaluser <br />
     * Column 10: username <br />
     * Column 11: creationdate <br />
     * Column 12: modificationdate.
     *
     * @param   aRS ResultSet to read the data from.
     * @exception   java.sql.SQLException    when reading the ResultSet failed.
     */
    public Binfile(ResultSet aRS) throws SQLException {
        iBinfileid = aRS.getLong(1);
        iL_projectid = aRS.getLong(2);
        iL_filedescriptionid = aRS.getLong(3);
        // The file. Note that it can be 'null' for
        // lazy caching of the file.
        InputStream is1 = aRS.getBinaryStream(4);
        if(is1 != null) {
            Vector bytes1 = new Vector();
            int reading = -1;
            try {
                while((reading = is1.read()) != -1) {
                    bytes1.add(new Byte((byte)reading));
                }
                is1.close();
            } catch(IOException ioe) {
                bytes1 = new Vector();
            }
            reading = bytes1.size();
            iFile = new byte[reading];
            for(int i=0;i<reading;i++) {
                iFile[i] = ((Byte)bytes1.get(i)).byteValue();
            }
        }
        iFilename = aRS.getString(5);
        iComments = aRS.getString(6);
        iOriginalpath = aRS.getString(7);
        iOriginalhost = aRS.getString(8);
        iOriginaluser = aRS.getString(9);
        iUsername = aRS.getString(10);
        iCreationdate = (java.sql.Timestamp)aRS.getObject(11);
        iModificationdate = (java.sql.Timestamp)aRS.getObject(12);
    }

    /**
     * Default constructor.
     */
    public Binfile() {
        super();
    }


    /**
     * This method writes the unzipped contents of this binfile (whether file or folder)
     * to the specified destination folder. <b>Note</b> that it silently overwrites any
     * pre-existing files! Existence checks should be handled by the caller!
     *
     * @param aDestination   File with the parent folder in which this file will be placed.
     * @throws IOException  when the write failed.
     */
    public void saveBinfileToDisk(File aDestination) throws IOException {
        if(this.isFolder()) {
            writeUnzippedFolder(aDestination);
        } else {
            writeUnzippedFile(aDestination);
        }
    }

    /**
     * This method writes the unzipped contents of this binfile to the specified folder,
     * using the original filename. <b>Note</b> that it silently overwrites any pre-existing files!
     * Existence checks should be handled by the caller!
     *
     * @param aParent   File with the parent folder in which this file will be placed.
     * @throws IOException  when the write failed.
     */
    private void writeUnzippedFile(File aParent) throws IOException {
        // First check to see if we really harbour a file.
        if(this.isFolder()) {
            throw new RuntimeException("Attempt to call 'writeUnzippedFile' on a folder!");
        }
        // Now check whether the File passed in is really a directory.
        if(!aParent.isDirectory()) {
            throw new IllegalArgumentException("The File you specified ('" + aParent.getAbsolutePath() + "') is not a directory!");
        }
        File file = new File(aParent, this.iFilename);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(this.getUnzippedFile());
        bos.flush();
        bos.close();
    }

    /**
     * This method will write the unzipped contents of this ZIPped data stream
     * to the specified folder. If the contents of this binfile do not represent
     * a folder ('isFolder()' returns 'false'), a RuntimeException will be thrown!
     *
     * @param aParentDestination    File with the parent folder to write the
     *                              unZIPped folder to. This File must represent
     *                              a directory!
     * @exception   java.io.IOException when the unzipping process goes wrong.
     */
    private void writeUnzippedFolder(File aParentDestination) throws IOException {
        // First check to see if we really harbour a folder.
        if(this.isFile()) {
            throw new RuntimeException("Attempt to call 'writeUnzippedFolder' on a file!");
        }
        // Now check whether the File passed in is really a directory.
        if(!aParentDestination.isDirectory()) {
            throw new IllegalArgumentException("The File you specified ('" + aParentDestination.getAbsolutePath() + "') is not a directory!");
        }
        // Okay, all seems to be in order.
        // Start the unzipping!
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(iFile));
        ZipEntry ze = null;
        // Cycle the zip entries.
        while((ze = zis.getNextEntry()) != null) {
            // Unzip each.
            unzipEntry(aParentDestination, ze, zis);
        }
        zis.close();
	}

    /**
     * This method will unzip the specified zip entry from the specified zip input stream and store
     * it relative to the specified parent folder. It will handle both folders and files.
     *
     * @param aParent   File with the parent folder to place the unzipped files relative to.
     * @param ze    ZipEntry to unzip from the stream.
     * @param aZis  ZipInputStream to read the unzipped bytes for the entry from.
     * @throws IOException  when the unzipping failed.
     */
	private void unzipEntry(File aParent, ZipEntry ze, ZipInputStream aZis) throws IOException {
        // Get the name for the zip entry. This name contains the relative path.
        String name = ze.getName();
        // If the entry is a directory, create it if it does not already exists.
        if(ze.isDirectory()) {
            File dir = new File(aParent, name);
            // Only attempt to create it if it doesn't exist already.
            if(!dir.exists()) {
				boolean result = dir.mkdirs();
	            // Check for successful creation.
	            if(!result) {
	            	throw new IOException("Creation of directory '" + dir.getAbsolutePath() + "' failed miserably!");
	            }
            }
        } else {
            File file = new File(aParent, name);
            // If the file already exists, break with an IOException!
            if(file.exists()) {
            	throw new IOException("File '" + file.getAbsolutePath() + "' already exists! Aborting unzip operation!");
            }
            // Okay, first create the file.
            boolean result = file.createNewFile();
			// Check to see if the creation worked.
			if(!result) {
            	throw new IOException("Creation of file '" + file.getAbsolutePath() + "' failed miserably!");
            }
            // Now write the unzipped contents to the output file.
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			byte[] read = new byte[1024];
			int readBytes = 0;
			while((readBytes = aZis.read(read)) != -1) {
				if(readBytes < 1024) {
					bos.write(read, 0, readBytes);
				} else {
					bos.write(read);
				}
			}
			bos.flush();
			bos.close();
        }
    }

    /**
     * This method returns the binary file as unzipped bytes.
     *
     * @return  byte[]  with the unzipped bytes for the binary file.
     * @exception   java.io.IOException when the unzipping process goes wrong.
     */
    public byte[] getUnzippedFile() throws IOException {
        byte[] result = null;

        byte[] zipped = super.getFile();
        ByteArrayInputStream bais = new ByteArrayInputStream(zipped);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        int read = -1;
        while((read = bis.read()) != -1) {
            bos.write(read);
        }
        bos.flush();
        baos.flush();
        result = baos.toByteArray();
        bos.close();
        bis.close();
        bais.close();
        baos.close();

        return result;
    }

    /**
     * This method allows the on-the fly zipping of data that is put in the
     * DB.
     *
     * @param   aBytes  byte[] with the data for the binary file. This data
     *                  will be zipped and subsequently sent to the superclass.
     * @exception   java.io.IOException when the zipping process fails.
     */
    public void setUnzippedFile(byte[] aBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        BufferedOutputStream bos = new BufferedOutputStream(gos);
        ByteArrayInputStream bais = new ByteArrayInputStream(aBytes);
        BufferedInputStream bis = new BufferedInputStream(bais);
        int read = -1;
        while((read = bis.read()) != -1) {
            bos.write(read);
        }
        bos.flush();
        baos.flush();
        gos.finish();
        super.setFile(baos.toByteArray());
        bis.close();
        bos.close();
        gos.close();
        bais.close();
        baos.close();
    }

    /**
     * This method allows the setting of a file (it sets filename and
     * the zipped bytes for the file).
     *
     * @param   aFilename   String with the FULL filename!
     * @exception java.io.IOException   whenever the file could not be found,
     *                          could not be read or could not be zipped.
     */
    public void setFileFromName(String aFilename) throws IOException {
        byte[] bytes = null;
        String name = null;

        File f = new File(aFilename);
        if(!f.exists()) {
            throw new IOException("File '" + aFilename + "' does not exist!");
        }
        // File seems to exist.
        // See if it is a file or directory.
        // Get the filename.
        name = f.getName();
        if(f.isDirectory()) {
            bytes = this.zipFolder(f);
            String path = f.getAbsolutePath();
            String separator = "/";
            if(path.indexOf("\\") >= 0) {
                separator = "\\";
            }
            name += separator;
        } else {
            // Get the GZIPped bytes.
            bytes = this.zipFile(f);

        }
        super.setFilename(name);
        super.setFile(bytes);
    }

    public String toString() {
        return this.iFilename;
    }

    /**
     * This methods reads all binary files for the specified project from the Binfile table,
     * without loading the BLOB itself. The BLOB can be loaded by calling 'loadBLOB' on the
     * instance for which the file needs to be loaded.
     *
     * @param aConn Connection to read the binary files from.
     * @param aProjectid    long with the projectid for the project we should collect the binary files for.
     * @return  Binfile[] with the binary file entries (without the BLOB!) in the 'binfile' table.
     * @throws SQLException when the retrieving of the binary files went wrong.
     */
    public static Binfile[] getAllBinfilesLazy(Connection aConn, long aProjectid) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select binfileid, l_projectid, l_filedescriptionid, null, filename, comments, originalpath, originalhost, originaluser, username, creationdate, modificationdate from binfile where l_projectid=? order by binfileid ASC");
        prep.setLong(1, aProjectid);
        ResultSet rs = prep.executeQuery();
        Vector v = new Vector();
        while(rs.next()) {
            v.add(new Binfile(rs));
        }
        rs.close();
        prep.close();
        Binfile[] binfiles = new Binfile[v.size()];
        v.toArray(binfiles);

        return binfiles;
    }

    /**
     * This method initializes the 'iFile' property of this Binfile, provided that the 'iBinfileid'
     * property is correctly initialized. It is best used in conjunction with a lazy loading method.
     *
     * @param aConn Connection with the DB connection to retrieve the BLOB from.
     * @throws SQLException when the retrieve failed.
     */
    public void loadBLOB(Connection aConn) throws SQLException {
        PreparedStatement prep = aConn.prepareStatement("select file from binfile where binfileid=?");
        prep.setLong(1, this.iBinfileid);
        ResultSet rs = prep.executeQuery();
        if(!rs.next()) {
            throw new SQLException("No binary file found for primary key '" + iBinfileid + "'!");
        }
        InputStream is1 = rs.getBinaryStream(1);
        Vector bytes1 = new Vector();
        int reading = -1;
        try {
            while((reading = is1.read()) != -1) {
                bytes1.add(new Byte((byte)reading));
            }
            is1.close();
        } catch(IOException ioe) {
            bytes1 = new Vector();
        }
        reading = bytes1.size();
        iFile = new byte[reading];
        for(int i=0;i<reading;i++) {
            iFile[i] = ((Byte)bytes1.get(i)).byteValue();
        }
        rs.close();
        prep.close();
    }

    /**
     * This method (pk)ZIPs up a folder and returns the byte[] that results.
     *
     * @return  byte[] with the (pk)ZIPped folder and its contents.
     * @throws IOException  when the (pk)ZIPping failed.
     */
    private byte[] zipFolder(File aFolder) throws IOException {
        byte[] bytes = null;
        // Create the output streams.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        zos.setComment("This zipfile was automatically created.\nCreation by the MS_LIMS software suite by Lennart Martens.\nContains the folder '" + aFolder.getName() + "'.");
        // Now recurse through the directories.
        zipFolderContents(aFolder, aFolder.getName() + "/", zos);
        // Finish the zip stream.
        zos.flush();
        zos.finish();
        // Extract the bytes.
        baos.flush();
        bytes = baos.toByteArray();
        // Close the output streams.
        zos.close();
        baos.close();
        return bytes;
    }

    /**
     * This method recursively zips up the specified folder and all its contents.
     *
     * @param aParent   File with the folder to zip up recursively.
     * @param aRelativePath String with the relative path of each folder.
     * @param aZos  ZipOutputStream to write the contents to.
     * @throws IOException  when the zipping failed.
     */
    private void zipFolderContents(File aParent, String aRelativePath, ZipOutputStream aZos) throws IOException {
        // Create a new ZipEntry for the folder itself.
        ZipEntry ze = new ZipEntry(aRelativePath);
        aZos.putNextEntry(ze);
        aZos.closeEntry();
        // Now list the folder.
        File[] files = aParent.listFiles();
        for (int i = 0; i < files.length; i++) {
            File lFile = files[i];
            if(lFile.isDirectory()) {
                // If we have a folder, submit it to the recursion. Note the update to the relative path.
                zipFolderContents(lFile, aRelativePath + lFile.getName() + "/", aZos);
            } else {
                // If we have a file, create a ZipEntry and unzip it.
                ZipEntry zeFile = new ZipEntry(aRelativePath + lFile.getName());
                aZos.putNextEntry(zeFile);
                // Now write the file.
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(lFile));
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while((bytesRead = bis.read(buffer)) != -1) {
                    if(bytesRead < 1024) {
                        aZos.write(buffer, 0, bytesRead);
                    } else {
                        aZos.write(buffer);
                    }
                }
                bis.close();
                // Close the ZipEntry.
                aZos.closeEntry();
            }
        }
    }

    /**
     * This method loads and zips the file data.
     *
     * @param   aFile  File with the data.
     * @return  byte[]  with the GZIPped data.
     * @exception   java.io.IOException whenever the GZIPping process fails.
     */
    private byte[] zipFile(File aFile) throws IOException {
        byte[] bytes = null;

        // Read it, and write the bytes to a GZIPped outputstream.
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(aFile));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        int reading = -1;
        while((reading = bis.read()) != -1) {
            gos.write(reading);
        }
        gos.finish();
        bis.close();
        baos.flush();
        bytes = baos.toByteArray();
        gos.close();
        baos.close();

        return bytes;
    }

    /**
     * This method will return 'true' if the stored binary data represents a folder.
     *
     * @return  boolean that is 'true' when the stored data represents a folder,
     *                  or 'false' when it represents a single file.
     */
    private boolean isFolder() {
        boolean folder = false;
        if(iFile.length > 4) {
            byte[] firstFour = new byte[4];
            for (int i = 0; i < firstFour.length; i++) {
                firstFour[i] = iFile[i];
            }
            // Okay, let's check these with the magic numbers.
            if(firstFour[0] == (byte)0x1F && firstFour[1] == (byte)0x8b) {
                // GZIP! Therefore it is a file.
                folder = false;
            } else if(firstFour[0] == (byte)0x50 && firstFour[1] == (byte)0x4b && firstFour[2] == (byte)0x03 && firstFour[3] == (byte)0x04) {
                // (pk)ZIP! Therefore it is a folder.
                folder = true;
            }
        } else {
            throw new RuntimeException("Stored file was less than four bytes which should be impossible!");
        }

        return folder;
    }

    /**
     * This method will return 'true' if the stored binary data represents a single file.
     *
     * @return  boolean that is 'true' when the stored data represents a single file,
     *                  or 'false' when it represents a folder.
     */
    private boolean isFile() {
        return !isFolder();
    }
}