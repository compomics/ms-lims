/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 20-jan-2004
 * Time: 15:13:18
 */
package com.compomics.mslims.util.fileio;

import junit.TestCaseLM;
import junit.framework.Assert;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2008/11/28 16:10:25 $
 */

/**
 * This class implements the full test scenario for the MascotGenericFile class.
 *
 * @author Lennart Martens
 * @version $Id: TestMascotGenericFile.java,v 1.5 2008/11/28 16:10:25 kenny Exp $
 * @see com.compomics.mslims.util.fileio.MascotGenericFile
 */
public class TestMascotGenericFile extends TestCaseLM {
    // Class specific log4j logger for TestMascotGenericFile instances.
    private static Logger logger = Logger.getLogger(TestMascotGenericFile.class);

    public TestMascotGenericFile() {
        this("test case for the MascotGenericFile class.");
    }

    public TestMascotGenericFile(String aName) {
        super(aName);
    }

    /**
     * this method simply tests the retention time in Mascot generic files.
     */
    public void testRetention() {
        try {
            // No retention time, return 0.
            MascotGenericFile mgf = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_1.mgf")));
            Assert.assertEquals(1, mgf.getRetentionInSeconds().length);
            Assert.assertEquals(6.0, mgf.getRetentionInSeconds()[0]);
        } catch (IOException ioe) {
            fail("IOException thrown when attempting to test the creation of a MascotGenericFile: " + ioe.getMessage());
        }

        try {
            // Sum of scans.
            // Returns the first retention time, track its summed status, and retain all retention times in the embedded properties.
            MascotGenericFile mgf = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_5.mgf")));
            Assert.assertEquals(1207.5892, mgf.getRetentionInSeconds()[0]);
            Assert.assertEquals(1208.5323, mgf.getRetentionInSeconds()[1]);
            Assert.assertEquals(2, mgf.getRetentionInSeconds().length);
            Assert.assertEquals(548, mgf.getScanNumbers()[0]);
            Assert.assertEquals(549, mgf.getScanNumbers()[1]);
            Assert.assertEquals(2, mgf.getScanNumbers().length);
            Assert.assertEquals(true, mgf.isSumOfScans());
            Assert.assertEquals("1207.5892,1208.5323", mgf.getExtraEmbeddedProperty("RTINSECONDS"));
        } catch (IOException ioe) {
            fail("IOException thrown when attempting to test the creation of a MascotGenericFile: " + ioe.getMessage());
        }

        try {
            // Sum of scans.
            // Returns the first retention time, track its summed status, and retain all retention times in the embedded properties.
            MascotGenericFile mgf = new MascotGenericFile(new File(super.getFullFilePath("testRetentionEsquire.mgf")));
            Assert.assertEquals(1608.0, mgf.getRetentionInSeconds()[0]);
        } catch (IOException ioe) {
            fail("IOException thrown when attempting to test the creation of a MascotGenericFile: " + ioe.getMessage());
        }
    }

    /**
     * This method simply test the creation of a Mascot generic file.
     */
    public void testCreation() {
        try {
            MascotGenericFile mgf = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_1.mgf")));
            Assert.assertEquals("testMascotGenericFile_1.mgf", mgf.getFilename());
        } catch (IOException ioe) {
            fail("IOException thrown when attempting to test the creation of a MascotGenericFile: " + ioe.getMessage());
        }

        try {
            MascotGenericFile mgf = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_2.mgf")));
            Assert.assertEquals("testMascotGenericFile_2.mgf", mgf.getFilename());
        } catch (IOException ioe) {
            fail("IOException thrown when attempting to test the creation of a MascotGenericFile: " + ioe.getMessage());
        }

        try {
            MascotGenericFile mgf = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_3.mgf")));
            Assert.assertEquals("testMascotGenericFile_3.mgf", mgf.getFilename());
        } catch (IOException ioe) {
            fail("IOException thrown when attempting to test the creation of a MascotGenericFile: " + ioe.getMessage());
        }

        try {
            MascotGenericFile mgf = new MascotGenericFile(new File(super.getFullFilePath("testMascotDistiller~data~L59_Bart_Metox_080530A_forward_p2A01.RAW.-1.mgf")));
            Assert.assertEquals("testMascotDistiller~data~L59_Bart_Metox_080530A_forward_p2A01.RAW.-1.mgf", mgf.getFilename());
        } catch (IOException ioe) {
            fail("IOException thrown when attempting to test the creation of a MascotGenericFile: " + ioe.getMessage());
        }


        try {
            new MascotGenericFile(new File("testMascotGenericFile_IDONOTEXIST.mgf"));
            fail("No IOException thrown when attempting to test the creation of a MascotGenericFile with a non-existant file!");
        } catch (IOException ioe) {
            // This is OK.
        }

        // Now first read the file contents.
        StringBuffer contents = new StringBuffer();
        try {
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath("testMascotGenericFile_1.mgf")));
            String line = null;
            while ((line = br.readLine()) != null) {
                contents.append(line + "\n");
            }
        } catch (IOException ioe) {
            fail("IOException thrown when attempting to read the file 'testMascotGenericFile_1.mgf' to test the creation of a MascotGenericFile: " + ioe.getMessage());
        }
        MascotGenericFile mgf = new MascotGenericFile("testMascotGenericFile_1.mgf", contents.toString());
        Assert.assertEquals("testMascotGenericFile_1.mgf", mgf.getFilename());
    }

    /**
     * This method test the reading of an MGF file into a MascotGenericFile object and the subsequent writing of the
     * instance back to file.
     */
    public void testReadingAndWritingBack() {
        try {
            File input = new File(super.getFullFilePath("testMascotGenericFile_1.mgf"));
            MascotGenericFile mgf = new MascotGenericFile(input);
            // Writing to temporary file.
            File output = new File(input.getParentFile(), "temp" + mgf.getFilename());
            output.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(output);
            mgf.writeToStream(fos);
            fos.flush();
            fos.close();

            // Reading control and written file and comparing.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("TestMascotGenericFile_1_control.mgf")));
            BufferedReader toTest = new BufferedReader(new FileReader(output));

            String line = null;
            while ((line = control.readLine()) != null) {
                Assert.assertEquals(line, toTest.readLine());
            }
            Assert.assertTrue(toTest.readLine() == null);

            control.close();
            toTest.close();
        } catch (IOException ioe) {
            fail("IOException thrown while testing to read and write an MGF file: " + ioe.getMessage());
        }

        try {
            File input = new File(super.getFullFilePath("testMascotGenericFile_2.mgf"));
            MascotGenericFile mgf = new MascotGenericFile(input);
            // Writing to temporary file.
            File output = new File(input.getParentFile(), "temp" + mgf.getFilename());
            output.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(output);
            mgf.writeToStream(fos);
            fos.flush();
            fos.close();

            // Reading control and written file and comparing.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("TestMascotGenericFile_2_control.mgf")));
            BufferedReader toTest = new BufferedReader(new FileReader(output));

            String line = null;
            while ((line = control.readLine()) != null) {
                Assert.assertEquals(line, toTest.readLine());
            }
            Assert.assertTrue(toTest.readLine() == null);

            control.close();
            toTest.close();
        } catch (IOException ioe) {
            fail("IOException thrown while testing to read and write an MGF file: " + ioe.getMessage());
        }

        try {
            File input = new File(super.getFullFilePath("testMascotGenericFile_3.mgf"));
            MascotGenericFile mgf = new MascotGenericFile(input);
            // Writing to temporary file.
            File output = new File(input.getParentFile(), "temp" + mgf.getFilename());
            output.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(output);
            mgf.writeToStream(fos);
            fos.flush();
            fos.close();

            // Reading control and written file and comparing.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("TestMascotGenericFile_3_control.mgf")));
            BufferedReader toTest = new BufferedReader(new FileReader(output));

            String line = null;
            while ((line = control.readLine()) != null) {
                Assert.assertEquals(line, toTest.readLine());
            }
            Assert.assertTrue(toTest.readLine() == null);

            control.close();
            toTest.close();
        } catch (IOException ioe) {
            fail("IOException thrown while testing to read and write an MGF file: " + ioe.getMessage());
        }

        try {
            File input = new File(super.getFullFilePath("testMascotGenericFile_4.mgf"));
            MascotGenericFile mgf = new MascotGenericFile(input);
            // Writing to temporary file.
            File output = new File(input.getParentFile(), "temp" + mgf.getFilename());
            output.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(output);
            mgf.writeToStream(fos);
            fos.flush();
            fos.close();

            // Reading control and written file and comparing.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("TestMascotGenericFile_4_control.mgf")));
            BufferedReader toTest = new BufferedReader(new FileReader(output));

            String line = null;
            while ((line = control.readLine()) != null) {
                Assert.assertEquals(line, toTest.readLine());
            }
            Assert.assertTrue(toTest.readLine() == null);

            control.close();
            toTest.close();
        } catch (IOException ioe) {
            fail("IOException thrown while testing to read and write an MGF file: " + ioe.getMessage());
        }
        // Test 5, this mgf file contains Extra additional paramaters (ex: SCANS, RTINSECONDS, ..)
        try {
            File input = new File(super.getFullFilePath("testMascotGenericFile_5.mgf"));
            MascotGenericFile mgf = new MascotGenericFile(input);
            // Writing to temporary file.
            File output = new File(input.getParentFile(), "temp" + mgf.getFilename());
            output.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(output);
            mgf.writeToStream(fos);
            fos.flush();
            fos.close();

            // Reading control and written file and comparing.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("TestMascotGenericFile_5_control.mgf")));
            BufferedReader toTest = new BufferedReader(new FileReader(output));

            String line = null;
            int lCount = 0;
            while ((line = control.readLine()) != null) {
                lCount++;
                Assert.assertEquals(line, toTest.readLine());
            }
            Assert.assertTrue(toTest.readLine() == null);
            Assert.assertEquals(mgf.getExtraEmbeddedProperty("SCANS"), "548-549");

            // Test the extractCharge() method on MascotGenericFile. This test file has a multiple charge assignement "2+,3+" - the method must return 23.


            control.close();
            toTest.close();
        } catch (IOException ioe) {
            fail("IOException thrown while testing to read and write an MGF file: " + ioe.getMessage());
        }

        // Test 6, this mgf file lacks the + sign folowing the charge state (cfr. proteowizard output!)
        try {

            File input = new File(super.getFullFilePath("testMascotGenericFile_6.mgf"));
            MascotGenericFile mgf = new MascotGenericFile(input);
            // Writing to temporary file.
            Assert.assertEquals(2, mgf.getCharge());
        } catch (IOException ioe) {
            fail("IOException thrown while testing to read and write an MGF file: " + ioe.getMessage());
        }
    }

    /**
     * This method test the sorting of MascotGenricFiles.
     */
    public void testSorting() {
        try {
            MascotGenericFile mgf1 = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_1.mgf")));
            MascotGenericFile mgf2 = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_2.mgf")));

            Vector test = new Vector(2);
            test.add(mgf1);
            test.add(mgf2);
            Collections.sort(test);
            Assert.assertEquals(mgf1, test.get(0));
            Assert.assertEquals(mgf2, test.get(1));

            test = new Vector(2);
            test.add(mgf2);
            test.add(mgf1);
            Collections.sort(test);
            Assert.assertEquals(mgf1, test.get(0));
            Assert.assertEquals(mgf2, test.get(1));

            Assert.assertTrue(mgf1.compareTo(mgf2) < 0);
            Assert.assertTrue(mgf2.compareTo(mgf1) > 0);
            Assert.assertTrue(mgf1.compareTo(mgf1) == 0);
            Assert.assertTrue(mgf2.compareTo(mgf2) == 0);
        } catch (IOException ioe) {
            fail("IOException while attempting to test sorting behaviour of MascotGenericFile: " + ioe.getMessage());
        }
    }

    /**
     * this method test the equals method of the MascotGenericFile.
     */
    public void testEquals() {
        try {
            MascotGenericFile mgf1 = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_1.mgf")));
            MascotGenericFile mgf2 = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_2.mgf")));
            MascotGenericFile mgf3 = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_1.mgf")));
            MascotGenericFile mgf4 = new MascotGenericFile(new File(super.getFullFilePath("testMascotGenericFile_2.mgf")));

            Assert.assertEquals(mgf1, mgf1);
            Assert.assertEquals(mgf2, mgf2);

            Assert.assertEquals(mgf1, mgf3);
            Assert.assertEquals(mgf2, mgf4);

            Assert.assertTrue(!mgf1.equals(null));
            Assert.assertTrue(!mgf1.equals(mgf2));
            Assert.assertTrue(!mgf1.equals(mgf4));
            Assert.assertTrue(!mgf2.equals(null));
            Assert.assertTrue(!mgf2.equals(mgf1));
            Assert.assertTrue(!mgf2.equals(mgf3));

            HashMap temp = mgf1.getPeaks();
            temp.put(new Double(123.4567), new Double(4500));
            mgf1.setPeaks(temp);
            Assert.assertTrue(!mgf1.equals(mgf3));
            Assert.assertTrue(!mgf1.equals(mgf2));
        } catch (IOException ioe) {
            fail("IOException while attempting to test equals method of MascotGenericFile: " + ioe.getMessage());
        }
    }
}
