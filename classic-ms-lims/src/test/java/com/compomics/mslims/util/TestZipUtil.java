/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 6-sep-02
 * Time: 16:34:47
 */
package com.compomics.mslims.util;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.ZipUtil;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/02/12 13:30:42 $
 */

/**
 * This class implements the testscenario for the ZipUtil class.
 *
 * @author Lennart Martens
 * @see com.compomics.mslims.util.ZipUtil
 */
public class TestZipUtil extends TestCaseLM {
    // Class specific log4j logger for TestZipUtil instances.
    private static Logger logger = Logger.getLogger(TestZipUtil.class);

    public TestZipUtil() {
        this("Test for the ZipUtil class.");
    }

    public TestZipUtil(String aName) {
        super(aName);
    }

    /**
     * This method test the zipping behaviour of the component.
     */
    public void testZip() {
        // This test uses the test.pkl file in the classpath for testing.
        final String filename = super.getFullFilePath("test.pkl");
        final String filename2 = "test.fnm";
        final String testMe = "Test me";
        final byte[] tempBytes = testMe.getBytes();
        // Test the little bugger!
        // First all the zipping.
        byte[] bytes1 = null;
        byte[] bytes2 = null;
        byte[] bytes3 = null;
        byte[] bytes4 = null;
        try {
            bytes1 = ZipUtil.toZippedBytes(filename);
            bytes2 = ZipUtil.toZippedBytes(filename, 256);
            FileInputStream fis = new FileInputStream(filename);
            bytes3 = ZipUtil.toZippedBytes(fis, filename, 512);
            fis.close();
            bytes4 = ZipUtil.toZippedBytes(tempBytes, filename2);
        } catch (IOException ioe) {
            fail("IOException occurred while attempting to access file '" + filename + "': " + ioe.getMessage());
        }

        // Now unzip and compare.
        byte[] result = null;
        try {
            File lFile = new File(filename);
            long length = lFile.length();
            FileInputStream fis = new FileInputStream(lFile);
            result = new byte[(int) length];
            fis.read(result);
            fis.close();
        } catch (IOException ioe) {
            fail("Unable to load file for checking correct zipping behaviour! " + ioe.getMessage());
        }
        String resultStr = new String(result);
        try {
            Assert.assertEquals(resultStr, new String(ZipUtil.unzipBytes(bytes1)));
            Assert.assertEquals(resultStr, new String(ZipUtil.unzipBytes(bytes2)));
            Assert.assertEquals(resultStr, new String(ZipUtil.unzipBytes(bytes3)));

            HashMap hm = ZipUtil.unzipBytesAndFileName(bytes4);
            Assert.assertEquals(testMe, new String((byte[]) hm.get(ZipUtil.BYTES)));
            Assert.assertEquals(filename2, (String) hm.get(ZipUtil.FILENAME));
        } catch (IOException ioe) {
            fail("Unable to unzipp zipped bytes. " + ioe.getMessage());
        }
    }
}
