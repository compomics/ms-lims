/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 17-jun-02
 * Time: 15:49:18
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.compomics.mslimscore.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class represents a generic Zip/Unzip utility, based on the built-in ZipStreams.
 */
public class ZipUtil {
    // Class specific log4j logger for ZipUtil instances.
    private static Logger logger = Logger.getLogger(ZipUtil.class);

    public static final String BYTES = "BYTES";
    public static final String FILENAME = "FILENAME";

    /**
     * Switching this variable on, results in more output being printed.
     */
    private static final boolean debug = false;

    /**
     * This method takes a byte[], zips it and returns the zipped entry as a byte[].
     *
     * @param aSource    byte[] with the bytes that constituted the file.
     * @param asFileName String with the filename of the file (for archiving information).
     * @return byte[]  with the zipped bytes.
     * @throws java.io.IOException when the Zip process fails.
     */
    public static byte[] toZippedBytes(byte[] aSource, String asFileName) throws IOException {
        // InputStream to read from the byte[].
        ByteArrayInputStream bais = new ByteArrayInputStream(aSource);
        byte[] toReturn = ZipUtil.toZippedBytes(bais, asFileName, 2048);
        bais.close();
        return toReturn;
    }

    /**
     * This method takes a filename and a buffersize, reads the file , zips it and returns the zipped entry as a
     * byte[].
     *
     * @param asFileName   String with the filename of the file (for archiving information).
     * @param aiBufferSize int with th size of the buffer to use when reading the file.
     * @return byte[]  with the zipped bytes.
     * @throws java.io.IOException when the Zip process fails.
     */
    public static byte[] toZippedBytes(String asFileName, int aiBufferSize) throws IOException {
        // InputStream to read from the file.
        FileInputStream fis = new FileInputStream(asFileName);
        byte[] toReturn = ZipUtil.toZippedBytes(fis, asFileName, aiBufferSize);
        fis.close();
        return toReturn;
    }

    /**
     * This method takes an InputStream, an original filename and a buffersize, reads the stream, zips it and returns
     * the zipped entry as a byte[].
     *
     * @param is           InputStream from which to read the file contents.
     * @param asFileName   String with the filename of the file (for archiving information).
     * @param aiBufferSize int with th size of the buffer to use when reading the file.
     * @return byte[]  with the zipped bytes.
     * @throws java.io.IOException when the Zip process fails.
     */
    public static byte[] toZippedBytes(InputStream is, String asFileName, int aiBufferSize) throws IOException {
        // Together they allow the output of a zipped set of bytes, representing
        // the compressed form of the files.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ZipOutputStream zos = new ZipOutputStream(baos);

        // Zipping!
        BufferedInputStream origin = new BufferedInputStream(is, aiBufferSize);
        ZipEntry entry = new ZipEntry(asFileName);
        zos.putNextEntry(entry);
        int count;
        byte[] data = new byte[aiBufferSize];
        if (debug) logger.info("Zipping entry.");
        while ((count = origin.read(data, 0, aiBufferSize)) != -1) {
            zos.write(data, 0, count);
        }
        zos.closeEntry();
        zos.finish();
        origin.close();

        // Ok, retrieve zipped bytes.
        byte[] toReturn = baos.toByteArray();
        zos.close();
        baos.close();
        return toReturn;
    }

    /**
     * This method takes a filename, reads the file , zips it and returns the zipped entry as a byte[].
     *
     * @param asFileName String with the filename of the file (for archiving information).
     * @return byte[]  with the zipped bytes.
     * @throws java.io.IOException when the Zip process fails.
     */
    public static byte[] toZippedBytes(String asFileName) throws IOException {
        return ZipUtil.toZippedBytes(asFileName, 2048);
    }

    /**
     * This method takes a byte[] with compressed data and returns a HashMap with the unzipped bytes and the filename.
     *
     * @param aBytes byte[] with the compressed data.
     * @return HashMap with two keys and associated values: the FILENAME and the BYTES
     * @throws java.io.IOException when the Unzip process fails.
     */
    public static HashMap unzipBytesAndFileName(byte[] aBytes) throws IOException {
        // The streams to unzip the zipped bytes.
        ByteArrayInputStream bais = new ByteArrayInputStream(aBytes);
        ZipInputStream zis = new ZipInputStream(bais);

        // Getting the first entry. Only one entry is supported for now.
        ZipEntry ze = zis.getNextEntry();
        // Getting uncompressed size.
        long length = ze.getSize();
        byte[] toReturn = null;
        if (length > 0) {
            // Initializing the return bytearray to the correct size.
            toReturn = new byte[(int) length];
            // Read the unzipped bytes in one go.
            zis.read(toReturn, 0, (int) length);
        } else {
            // Now we don't know the length of the bytearray.
            // So we'll read streams into streams.
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
            int count = 0;
            byte[] data = new byte[2048];
            while ((count = zis.read(data)) != -1) {
                baos.write(data, 0, count);
            }
            toReturn = baos.toByteArray();
            baos.close();
        }
        // Construct the HashMap which will be returned.
        HashMap returnHM = new HashMap(2);
        returnHM.put(BYTES, toReturn);
        returnHM.put(FILENAME, ze.getName());

        // That's it!
        bais.close();
        zis.close();
        return returnHM;
    }

    /**
     * This method takes a byte[] with compressed data and returns the unzipped bytes.
     *
     * @param aBytes byte[] with the compressed data.
     * @return byte[]  with the uncompressed data.
     * @throws java.io.IOException when the Unzip process fails.
     */
    public static byte[] unzipBytes(byte[] aBytes) throws IOException {
        HashMap lhm = ZipUtil.unzipBytesAndFileName(aBytes);
        return (byte[]) lhm.get(BYTES);
    }
}
