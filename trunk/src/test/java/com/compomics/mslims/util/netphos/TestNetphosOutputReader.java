/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 20-jul-2003
 * Time: 17:28:01
 */
package com.compomics.mslims.util.netphos;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.netphos.NetphosOutputReader;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.*;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/02/12 13:30:42 $
 */

/**
 * This class implements the test scenario for the NetphosOutputReader. (in extension, it indirectly also test the other
 * netphos package classes).
 *
 * @author Lennart Martens
 * @see com.compomics.mslims.util.netphos.NetphosOutputReader
 */
public class TestNetphosOutputReader extends TestCaseLM {
    // Class specific log4j logger for TestNetphosOutputReader instances.
    private static Logger logger = Logger.getLogger(TestNetphosOutputReader.class);

    public TestNetphosOutputReader() {
        this("Test scenario for the NetphosOutputReader.");
    }

    public TestNetphosOutputReader(String aName) {
        super(aName);
    }

    /**
     * Tests the default functionality of the NetphosOutputReader.
     */
    public void testNetphosOutputReader() {
        // First test something that works.
        try {
            NetphosOutputReader nor = new NetphosOutputReader(new File(super.getFullFilePath("testNetphosReader.txt")));
            String outputString = nor.toString();
            BufferedReader outputReader = new BufferedReader(new StringReader(outputString));
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testNetphosReader_control.txt")));
            String line = null;
            int lineCounter = 0;
            while ((line = control.readLine()) != null) {
                lineCounter++;
                Assert.assertEquals("Assertion failed while reading line number " + lineCounter + ".", line, outputReader.readLine());
            }
            Assert.assertTrue(outputReader.readLine() == null);
            outputReader.close();
            control.close();
        } catch (IOException ioe) {
            fail("IOException occurred while testing the NetphosOutputReader: " + ioe.getMessage());
        }
        // now test exception handling when attempting to load a non-existant file.
        try {
            NetphosOutputReader nor = new NetphosOutputReader(new File("nonexistantfileinfilesystem_withstarngeName.ggg"));
            fail("No IOException thrown when attempting to load a non-existant file in NetphosOutputReader!");
        } catch (IOException ioe) {
            // Perfect. Do nothing.
        }
    }

    /**
     * Test the thresholding output.
     */
    public void testThresholdReading() {
        // First with the threshold.
        try {
            NetphosOutputReader nor = new NetphosOutputReader(new File(super.getFullFilePath("testNetphosThresholdReader.txt")));
            // Output with '0.8' threshold.
            String outputString = nor.toString(0.8);
            BufferedReader outputReader = new BufferedReader(new StringReader(outputString));
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testNetphosThresholdReader_control.txt")));
            String line = null;
            int lineCounter = 0;
            while ((line = control.readLine()) != null) {
                lineCounter++;
                Assert.assertEquals("Assertion failed while reading line number " + lineCounter + ".", line, outputReader.readLine());
            }
            Assert.assertTrue(outputReader.readLine() == null);
            outputReader.close();
            control.close();
        } catch (IOException ioe) {
            fail("IOException occurred while testing the NetphosOutputReader with a threshold of '0.8': " + ioe.getMessage());
        }

        // Now try without the threshold.
        try {
            NetphosOutputReader nor = new NetphosOutputReader(new File(super.getFullFilePath("testNetphosThresholdReader.txt")));
            // Output without threshold.
            String outputString = nor.toString();
            BufferedReader outputReader = new BufferedReader(new StringReader(outputString));
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testNetphosNoThresholdReader_control.txt")));
            String line = null;
            int lineCounter = 0;
            while ((line = control.readLine()) != null) {
                lineCounter++;
                Assert.assertEquals("Assertion failed while reading line number " + lineCounter + ".", line, outputReader.readLine());
            }
            Assert.assertTrue(outputReader.readLine() == null);
            outputReader.close();
            control.close();
        } catch (IOException ioe) {
            fail("IOException occurred while testing the NetphosOutputReader without a threshold: " + ioe.getMessage());
        }

        // Finally, try with a threshold of 0.0, which should be equal to no threshold at all.
        try {
            NetphosOutputReader nor = new NetphosOutputReader(new File(super.getFullFilePath("testNetphosThresholdReader.txt")));
            // Output with a threshold of '0.0'.
            String outputString = nor.toString(0.0);
            BufferedReader outputReader = new BufferedReader(new StringReader(outputString));
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testNetphosNoThresholdReader_control.txt")));
            String line = null;
            int lineCounter = 0;
            while ((line = control.readLine()) != null) {
                lineCounter++;
                Assert.assertEquals("Assertion failed while reading line number " + lineCounter + ".", line, outputReader.readLine());
            }
            Assert.assertTrue(outputReader.readLine() == null);
            outputReader.close();
            control.close();
        } catch (IOException ioe) {
            fail("IOException occurred while testing the NetphosOutputReader with a '0.0' threshold: " + ioe.getMessage());
        }
    }
}
