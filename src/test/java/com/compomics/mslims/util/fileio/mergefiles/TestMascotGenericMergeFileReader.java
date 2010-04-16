/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 14-dec-2003
 * Time: 12:37:39
 */
package com.compomics.mslims.util.fileio.mergefiles;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.MascotGenericFile;
import com.compomics.mslims.util.fileio.interfaces.MergeFileReader;
import com.compomics.mslims.util.fileio.mergefiles.MascotGenericMergeFileReader;
import com.compomics.mslims.util.mascot.MascotIdentifiedSpectrum;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2007/03/07 17:06:03 $
 */

/**
 * This class implements the full test scenario for the MascotGenricMergeFileReader.
 *
 * @author Lennart Martens
 * @version $Id: TestMascotGenericMergeFileReader.java,v 1.3 2007/03/07 17:06:03 kenny Exp $
 */
public class TestMascotGenericMergeFileReader extends TestCaseLM {
    // Class specific log4j logger for TestMascotGenericMergeFileReader instances.
    private static Logger logger = Logger.getLogger(TestMascotGenericMergeFileReader.class);

    public TestMascotGenericMergeFileReader() {
        this("Test scenario for the MascotGenericMergeFileReader class.");
    }

    public TestMascotGenericMergeFileReader(String aName) {
        super(aName);
    }


    /**
     * This method test the reading of a mergefile.
     */
    public void testMergeFileReader() {
        String control1 = null;
        String control2 = null;
        try {
            // Read the controls.
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath("testMascotGenericMergeFileReader_Control1.txt")));
            StringBuffer lsb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                lsb.append(line + "\n");
            }
            br.close();
            control1 = lsb.toString();

            br = new BufferedReader(new FileReader(super.getFullFilePath("testMascotGenericMergeFileReader_Control2.txt")));
            lsb = new StringBuffer();
            line = null;
            while ((line = br.readLine()) != null) {
                lsb.append(line + "\n");
            }
            br.close();
            control2 = lsb.toString();
        } catch (IOException ioe) {
            fail("Unable to read control files for test of MascotGenericsMergeFileReader: " + ioe.getMessage() + "!");
        }

        // The first merge file.

        try {
            // First just the String with the filename.
            String file = super.getFullFilePath("testMascotGenericMergeFileReader1.txt");
            MascotGenericMergeFileReader mrf = new MascotGenericMergeFileReader(file);
            Assert.assertEquals("testMascotGenericMergeFileReader1", mrf.getRunName());
            Assert.assertEquals(new File(file).getName(), mrf.getFilename());
            Assert.assertEquals(control1, mrf.toString());

            // Now try the file instead of the filename.
            File f = new File(file);
            mrf = new MascotGenericMergeFileReader(f);
            Assert.assertEquals("testMascotGenericMergeFileReader1", mrf.getRunName());
            Assert.assertEquals(f.getName(), mrf.getFilename());
            Assert.assertEquals(control1, mrf.toString());
        } catch (IOException ioe) {
            fail("IOException while testing the first Mascot Generic mergefilereader test: " + ioe.getMessage() + "!");
        }


        // The second merge file.
        try {
            // First just the String with the filename.
            String file = super.getFullFilePath("testMascotGenericMergeFileReader2.txt");
            MascotGenericMergeFileReader mrf = new MascotGenericMergeFileReader(file);
            Assert.assertEquals("Cyt C 1pmol5 ul MS031211MSMS", mrf.getRunName());
            Assert.assertEquals(new File(file).getName(), mrf.getFilename());
            Assert.assertEquals(control2, mrf.toString());

            // Now try the file instead of the filename.
            File f = new File(file);
            mrf = new MascotGenericMergeFileReader(f);
            Assert.assertEquals("Cyt C 1pmol5 ul MS031211MSMS", mrf.getRunName());
            Assert.assertEquals(new File(file).getName(), mrf.getFilename());
            Assert.assertEquals(control2, mrf.toString());
        } catch (IOException ioe) {
            fail("IOException while testing the second Mascot Generic mergefilereader test: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the matching with a contained MGF file.
     */
    public void testFindMatchingMGFFile() {
        try {
            MergeFileReader mrf = new MascotGenericMergeFileReader(super.getFullFilePath("testMascotGenericMergeFileReader2.txt"));
            MascotIdentifiedSpectrum mis = new MascotIdentifiedSpectrum();
            mis.setSearchTitle(" Cmpd 5, +MSn(354.37) 0.9 min");
            MascotGenericFile result = (MascotGenericFile) mrf.findMatchingSpectrumFile(mis);
            Assert.assertTrue(result != null);
            String filename = mrf.getCorrespondingSpectrumFilename(mis);
            Assert.assertEquals("testMascotGenericMergeFileReader2_5.txt", filename);
            Assert.assertEquals(filename, result.getFilename());

            // Second pass, using filename instead of title this time.
            mis.setSearchTitle("testMascotGenericMergeFileReader2_5.txt");
            result = (MascotGenericFile) mrf.findMatchingSpectrumFile(mis);
            Assert.assertTrue(result != null);
            Assert.assertEquals(" Cmpd 5, +MSn(354.37) 0.9 min", result.getTitle());
            filename = mrf.getCorrespondingSpectrumFilename(mis);
            Assert.assertEquals("testMascotGenericMergeFileReader2_5.txt", filename);
            Assert.assertEquals(filename, result.getFilename());

        } catch (IOException ioe) {
            fail("IOException while testing the matching of a PKL file in a MascotGenericMergeFileReader vs. a MIS: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the reporting of all the MGF files in the mergefile.
     */
    public void testMGFFilenameReporting() {
        try {
            MergeFileReader mrf = new MascotGenericMergeFileReader(super.getFullFilePath("testMascotGenericMergeFileReader1.txt"));
            String[] names = mrf.getAllSpectrumFilenames();
            // Length check.
            Assert.assertEquals(141, names.length);

            // In-deep check, using the knowledge about how the individual MGF's are named.
            String prefix = mrf.getFilename().substring(0, mrf.getFilename().lastIndexOf("."));
            String affix = mrf.getFilename().substring(mrf.getFilename().lastIndexOf("."));
            // (Note that this check relies on the MergeFileReader to store the spectrum files in the order they
            // were read from the mergefile!)
            for (int i = 0; i < names.length; i++) {
                Assert.assertEquals(prefix + "_" + (i + 1) + affix, names[i]);
            }
        } catch (IOException ioe) {
            fail("IOException while testing the reporting of the PKL filenames by a MascotGenericMergeFileReader: " + ioe.getMessage() + "!");
        }

        try {
            MergeFileReader mrf = new MascotGenericMergeFileReader(super.getFullFilePath("testMascotGenericMergeFileReader2.txt"));
            String[] names = mrf.getAllSpectrumFilenames();
            // Length check.
            Assert.assertEquals(15, names.length);

            // In-deep check, using the knowledge about how the individual MGF's are named.
            String prefix = mrf.getFilename().substring(0, mrf.getFilename().lastIndexOf("."));
            String affix = mrf.getFilename().substring(mrf.getFilename().lastIndexOf("."));
            // (Note that this check relies on the MergeFileReader to store the spectrum files in the order they
            // were read from the mergefile!)
            for (int i = 0; i < names.length; i++) {
                Assert.assertEquals(prefix + "_" + (i + 1) + affix, names[i]);
            }
        } catch (IOException ioe) {
            fail("IOException while testing the reporting of the PKL filenames by a MascotGenericMergeFileReader: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the reporting of all the titles of the MGF files in the mergefile.
     */
    public void testMGFTitleReporting() {
        try {
            File input = new File(super.getFullFilePath("testMascotGenericMergeFileReader1.txt"));
            MascotGenericMergeFileReader mrf = new MascotGenericMergeFileReader(input);
            String[] titles = mrf.getAllSpectrumTitles();
            // Length check.
            Assert.assertEquals(141, titles.length);

            // In-deep check, using the control file.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testMascotGenericMergeFileReader1_control_titles.txt")));
            for (int i = 0; i < titles.length; i++) {
                Assert.assertEquals(control.readLine(), titles[i]);
            }
            Assert.assertTrue(control.readLine() == null);
            control.close();
        } catch (IOException ioe) {
            fail("IOException while testing the reporting of the PKL filenames by a MascotGenericMergeFileReader: " + ioe.getMessage() + "!");
        }

        try {
            File input = new File(super.getFullFilePath("testMascotGenericMergeFileReader2.txt"));
            MascotGenericMergeFileReader mrf = new MascotGenericMergeFileReader(input);
            String[] titles = mrf.getAllSpectrumTitles();
            // Length check.
            Assert.assertEquals(15, titles.length);

            // In-deep check, using the control file.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testMascotGenericMergeFileReader2_control_titles.txt")));
            for (int i = 0; i < titles.length; i++) {
                Assert.assertEquals(control.readLine(), titles[i]);
            }
            Assert.assertTrue(control.readLine() == null);
            control.close();
        } catch (IOException ioe) {
            fail("IOException while testing the reporting of the PKL filenames by a MascotGenericMergeFileReader: " + ioe.getMessage() + "!");
        }
    }
}
