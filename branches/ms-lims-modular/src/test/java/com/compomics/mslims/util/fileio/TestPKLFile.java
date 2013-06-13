/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 25-feb-03
 * Time: 12:54:28
 */
package com.compomics.mslims.util.fileio;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.PKLFile;
import com.compomics.mslims.util.fileio.interfaces.MergeFileReader;
import com.compomics.mslims.util.fileio.mergefiles.MicromassMergeFileReader;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.*;
import java.util.Collections;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/02/12 13:30:42 $
 */

/**
 * This class implements the test for the PKLFile class.
 *
 * @author Lennart Martens
 * @see com.compomics.mslims.util.fileio.PKLFile
 */
public class TestPKLFile extends TestCaseLM {
    // Class specific log4j logger for TestPKLFile instances.
    private static Logger logger = Logger.getLogger(TestPKLFile.class);

    public TestPKLFile() {
        this("Test for the PKLFile class");
    }

    public TestPKLFile(String aName) {
        super(aName);
    }

    /**
     * This method test aberrant creation of a PKL file.
     */
    public void testAberrantCreation() {
        try {
            PKLFile pkl = new PKLFile("1 1 1 caplc3333.666.2.2.pkl");
            Assert.assertEquals("caplc3333.666.2.2.pkl", pkl.getFilename());
            Assert.assertEquals(3333l, pkl.getRunNumber());
            Assert.assertEquals(666, pkl.getScanNumber());
        } catch (Exception e) {
            fail("Unable to create PKLFile instance based on '1 1 1 caplc3333.666.2.2.pkl'.");
        }
    }

    /**
     * This method test the retrieval of the run number and scan number from the pklfile.
     */
    public void testRunAndScanNumbers() {
        Vector scans = new Vector(300, 2);
        Vector runs = new Vector(300, 2);
        // First read all of the responses.

        try {
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath("testRunAndScanNumbers.txt")));
            String line = null;
            while ((line = br.readLine()) != null) {
                runs.add(new Long(line.trim()));
                scans.add(new Long(br.readLine().trim()));
            }
            br.close();
        } catch (IOException ioe) {
            fail("Unable to read control data for testRunANdScanNumbers in TestPKL: " + ioe.getMessage() + "!");
        }

        try {
            String input = super.getFullFilePath("testMicromassMergeFileReader2.txt");
            MergeFileReader mrf = new MicromassMergeFileReader(input);
            Vector pkls = mrf.getSpectrumFiles();

            // Cycle and check them.
            int liSize = pkls.size();
            for (int i = 0; i < liSize; i++) {
                PKLFile pkl = (PKLFile) pkls.get(i);
                Assert.assertTrue(runs.remove(new Long(pkl.getRunNumber())));
                Assert.assertTrue(scans.remove(new Long(pkl.getScanNumber())));
            }
            Assert.assertTrue(runs.size() == 0);
            Assert.assertTrue(scans.size() == 0);
        } catch (IOException ioe) {
            fail("Unable to read mergefile with pkl data in testRunAndScanNumbers: " + ioe.getMessage() + "!");
        }
    }


    /**
     * This method test the implementation of comparable by the PKLFile class.
     */
    public void testOrdering() {
        Vector control = new Vector(300, 2);
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath("testOrdering.txt")));
            String line = null;
            while ((line = br.readLine()) != null) {
                control.add(line);
            }
            br.close();
        } catch (IOException ioe) {
            fail("Unable to read control set for testOrdering: " + ioe.getMessage() + "!");
        }

        try {
            String input = super.getFullFilePath("testMicromassMergeFileReader2.txt");
            MergeFileReader mrf = new MicromassMergeFileReader(input);
            Vector pkls = mrf.getSpectrumFiles();
            Collections.sort(pkls);

            // Cycle and check them.
            int liSize = pkls.size();
            for (int i = 0; i < liSize; i++) {
                PKLFile file = (PKLFile) pkls.get(i);
                Assert.assertEquals(control.elementAt(i), file.getFilename());
            }
        } catch (IOException ioe) {
            fail("Unable to read mergefile with pkl data in testOrdering: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the writing of a PKL file to a file.
     */
    public void testWriting() {
        File input = new File(super.getFullFilePath("testPKL.txt"));
        // First test write with the filename.
        try {
            // Reading original.
            PKLFile file = new PKLFile(input);
            // Writing to outputstream.
            File output = new File(input.getParentFile(), "temp" + file.getFilename());
            output.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(output);
            file.writeToStream(fos, true);
            fos.flush();
            fos.close();

            // Comparing both.
            BufferedReader control = new BufferedReader(new FileReader(input));
            BufferedReader test = new BufferedReader(new FileReader(output));
            String line = null;
            while ((line = control.readLine()) != null) {
                Assert.assertEquals(line, test.readLine());
            }
            Assert.assertTrue(test.readLine() == null);

        } catch (IOException ioe) {
            fail("IOException during test of writing PKLFile to outputstream: " + ioe.getMessage() + "!");
        }

        try {
            // Reading original.
            PKLFile file = new PKLFile(input);
            // Writing to file.
            file.setFilename("temp" + file.getFilename());
            file.writeToFile(input.getParentFile(), true);

            // Comparing with original.
            BufferedReader control = new BufferedReader(new FileReader(input));
            BufferedReader test = new BufferedReader(new FileReader(super.getFullFilePath(file.getFilename())));
            File tempFile = new File(super.getFullFilePath(file.getFilename()));
            tempFile.deleteOnExit();
            String line = null;
            // First line has 'temp' appended to the filename.
            line = test.readLine();
            int spaceLocation = line.lastIndexOf(" ");
            Assert.assertEquals(control.readLine(), line.substring(0, spaceLocation + 1) + line.substring(spaceLocation + 5));
            while ((line = control.readLine()) != null) {
                Assert.assertEquals(line, test.readLine());
            }
            Assert.assertTrue(test.readLine() == null);
        } catch (IOException ioe) {
            fail("IOException during test of writing PKLFile to file: " + ioe.getMessage() + "!");
        }

        // Now test write without the filename.
        try {
            // Reading original.
            PKLFile file = new PKLFile(input);
            // Writing to outputstream.
            File output = new File(input.getParentFile(), "temp" + file.getFilename());
            output.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(output);
            file.writeToStream(fos, false);
            fos.flush();
            fos.close();

            // Comparing both.
            BufferedReader control = new BufferedReader(new FileReader(input));
            BufferedReader test = new BufferedReader(new FileReader(output));
            String line = control.readLine();
            int spaceLocation = line.lastIndexOf(" ");
            Assert.assertEquals(line.substring(0, spaceLocation), test.readLine());
            while ((line = control.readLine()) != null) {
                Assert.assertEquals(line, test.readLine());
            }
            Assert.assertTrue(test.readLine() == null);

        } catch (IOException ioe) {
            fail("IOException during test of writing PKLFile to outputstream: " + ioe.getMessage() + "!");
        }

        try {
            // Reading original.
            PKLFile file = new PKLFile(input);
            // Writing to file.
            file.setFilename("temp" + file.getFilename());
            file.writeToFile(input.getParentFile(), false);

            // Comparing with original.
            BufferedReader control = new BufferedReader(new FileReader(input));
            BufferedReader test = new BufferedReader(new FileReader(super.getFullFilePath(file.getFilename())));
            File tempFile = new File(super.getFullFilePath(file.getFilename()));
            tempFile.deleteOnExit();
            // First line has 'temp' appended to the filename.
            String line = control.readLine();
            int spaceLocation = line.lastIndexOf(" ");
            Assert.assertEquals(line.substring(0, spaceLocation), test.readLine());
            while ((line = control.readLine()) != null) {
                Assert.assertEquals(line, test.readLine());
            }
            Assert.assertTrue(test.readLine() == null);
        } catch (IOException ioe) {
            fail("IOException during test of writing PKLFile to file: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the exporting of a PKL file into Mascot Generic Format.
     */
    public void testMGFExport() {
        File input = new File(super.getFullFilePath("testPKL.txt"));
        // First test write with the filename.
        try {
            // Reading original.
            PKLFile file = new PKLFile(input);
            // Getting MGF String.
            String mgfContents = file.getMGFContents();
            // Comparing with the control.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testPKL_MGF_control.txt")));
            BufferedReader toTest = new BufferedReader(new StringReader(mgfContents));
            String line = null;
            int lineCounter = 0;
            while ((line = control.readLine()) != null) {
                lineCounter++;
                Assert.assertEquals("Error comparing control line " + lineCounter + "!", line, toTest.readLine());
            }
            Assert.assertTrue(toTest.readLine() == null);
            control.close();
            toTest.close();
        } catch (IOException ioe) {
            fail("IOException during test of writing PKLFile to outputstream: " + ioe.getMessage() + "!");
        }
    }
}
