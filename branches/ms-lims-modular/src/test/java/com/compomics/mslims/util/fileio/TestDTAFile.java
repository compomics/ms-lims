/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 25-feb-03
 * Time: 12:54:28
 */
package com.compomics.mslims.util.fileio;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.DTAFile;
import com.compomics.mslims.util.fileio.interfaces.MergeFileReader;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.*;
import java.util.Collections;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2005/03/04 08:36:12 $
 */

/**
 * This class implements the test for the DTAFile class.
 *
 * @author Lennart Martens
 * @see com.compomics.mslims.util.fileio.DTAFile
 */
public class TestDTAFile extends TestCaseLM {
    // Class specific log4j logger for TestDTAFile instances.
    private static Logger logger = Logger.getLogger(TestDTAFile.class);

    public TestDTAFile() {
        this("Test for the PKLFile class");
    }

    public TestDTAFile(String aName) {
        super(aName);
    }

    /**
     * This method test the writing of a DTA file to a file.
     */
    public void testWriting() {
        File input = new File(super.getFullFilePath("testDTA.txt"));
        // First test write with the filename.
        try {
            // Reading original.
            DTAFile file = new DTAFile(input);
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
            fail("IOException during test of writing DTAFile to outputstream: " + ioe.getMessage() + "!");
        }

        try {
            // Reading original.
            DTAFile file = new DTAFile(input);
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
            fail("IOException during test of writing DTAFile to file: " + ioe.getMessage() + "!");
        }

        // Now test write without the filename.
        try {
            // Reading original.
            DTAFile file = new DTAFile(input);
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
            fail("IOException during test of writing DTAFile to outputstream: " + ioe.getMessage() + "!");
        }

        try {
            // Reading original.
            DTAFile file = new DTAFile(input);
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
            fail("IOException during test of writing DTAFile to file: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the exporting of a PKL file into Mascot Generic Format.
     */
    public void testMGFExport() {
        File input = new File(super.getFullFilePath("testDTA.txt"));
        // First test write with the filename.
        try {
            // Reading original.
            DTAFile file = new DTAFile(input);
            // Getting MGF String.
            String mgfContents = file.getMGFContents();
            // Comparing with the control.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testDTA_MGF_control.txt")));
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
            fail("IOException during test of writing DTAFile to outputstream: " + ioe.getMessage() + "!");
        }
    }
}
