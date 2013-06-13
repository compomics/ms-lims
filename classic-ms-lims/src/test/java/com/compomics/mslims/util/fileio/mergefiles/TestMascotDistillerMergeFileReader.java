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
import com.compomics.mslims.util.fileio.mergefiles.MascotDistillerMergeFileReader;
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
 * $Revision: 1.2 $
 * $Date: 2009/05/18 08:01:10 $
 */

/**
 * This class implements the full test scenario for the MascotDistillerGenricMergeFileReader.
 *
 * @author Kenny Helsens
 * @version $Id: TestMascotDistillerMergeFileReader.java,v 1.2 2009/05/18 08:01:10 niklaas Exp $
 */
public class TestMascotDistillerMergeFileReader extends TestCaseLM {
    // Class specific log4j logger for TestMascotDistillerMergeFileReader instances.
    private static Logger logger = Logger.getLogger(TestMascotDistillerMergeFileReader.class);

    public TestMascotDistillerMergeFileReader() {
        this("Test scenario for the MascotDistillerMergeFileReader class.");
    }

    public TestMascotDistillerMergeFileReader(String aName) {
        super(aName);
    }


    /**
     * This method test the reading of a mergefile.
     */
    public void testMergeFileReader() {
        String controll = null;
        try {
            // Read the controls.
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath("testMascotDistillerMergeFileReader_Control.txt")));
            StringBuffer lsb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                lsb.append(line + "\n");
            }
            br.close();
            controll = lsb.toString();

        } catch (IOException ioe) {
            fail("Unable to read control file for test of MascotGenericsMergeFileReader: " + ioe.getMessage() + "!");
        }

        try {
            // First just the String with the filename.
            String file = super.getFullFilePath("testMascotDistiller~data~L59_Bart_Metox_080530A_forward_p2A01.RAW.-1.mgf");
            MascotDistillerMergeFileReader mrf = new MascotDistillerMergeFileReader(file);
            Assert.assertEquals("L59_Bart_Metox_080530A_forward_p2A01", mrf.getRunName());
            Assert.assertEquals(new File(file).getName(), mrf.getFilename());
            Assert.assertEquals(controll, mrf.toString());

            // Now try the file instead of the filename.
            File f = new File(file);
            mrf = new MascotDistillerMergeFileReader(f);
            Assert.assertEquals("L59_Bart_Metox_080530A_forward_p2A01", mrf.getRunName());
            Assert.assertEquals(f.getName(), mrf.getFilename());
            Assert.assertEquals(controll, mrf.toString());
        } catch (IOException ioe) {
            fail("IOException while testing the first Mascot Generic mergefilereader test: " + ioe.getMessage() + "!");
        }

    }

    /**
     * This method test the matching with a contained MGF file.
     */
    public void testFindMatchingMGFFile() {
        try {
            MergeFileReader mrf = new MascotDistillerMergeFileReader(super.getFullFilePath("testMascotDistiller~data~L59_Bart_Metox_080530A_forward_p2A01.RAW.-1.mgf"));
            MascotIdentifiedSpectrum mis = new MascotIdentifiedSpectrum();
            // Title 84!
            mis.setSearchTitle("84: Scan 618 (rt=12.3077) [C:\\XCalibur\\data\\data_linda\\L59_Bart_Metox_080530A_forward_p2A01.RAW]");
            MascotGenericFile result = (MascotGenericFile) mrf.findMatchingSpectrumFile(mis);
            Assert.assertTrue(result != null);
            String filename = mrf.getCorrespondingSpectrumFilename(mis);
            Assert.assertEquals("L59_Bart_Metox_080530A_forward_p2A01_84_618_1_3.mgf", filename);
            Assert.assertEquals(filename, result.getFilename());

            // Second pass, using filename instead of title this time.
            mis.setSearchTitle("L59_Bart_Metox_080530A_forward_p2A01_84_618_1_3.mgf");
            result = (MascotGenericFile) mrf.findMatchingSpectrumFile(mis);
            Assert.assertTrue(result != null);
            Assert.assertEquals("84: Scan 618 (rt=12.3077) [C:\\XCalibur\\data\\data_linda\\L59_Bart_Metox_080530A_forward_p2A01.RAW]", result.getTitle());
            filename = mrf.getCorrespondingSpectrumFilename(mis);
            Assert.assertEquals("L59_Bart_Metox_080530A_forward_p2A01_84_618_1_3.mgf", filename);
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
            MergeFileReader mrf = new MascotDistillerMergeFileReader(super.getFullFilePath("testMascotDistiller~data~L59_Bart_Metox_080530A_forward_p2A01.RAW.-1.mgf"));
            String[] names = mrf.getAllSpectrumFilenames();
            // Length check.
            Assert.assertEquals(100, names.length);

            // In-deep check, using the control file.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testMascotDistillerMergeFileReader_Control_filenames.txt")));
            for (int i = 0; i < names.length; i++) {
                Assert.assertEquals(control.readLine(), names[i]);
            }
            Assert.assertTrue(control.readLine() == null);
            control.close();

        } catch (IOException ioe) {
            fail("IOException while testing the reporting of the PKL filenames by a MascotGenericMergeFileReader: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the reporting of all the titles of the MGF files in the mergefile.
     */
    public void testMGFTitleReporting() {
        try {
            File input = new File(super.getFullFilePath("testMascotDistiller~data~L59_Bart_Metox_080530A_forward_p2A01.RAW.-1.mgf"));
            MascotDistillerMergeFileReader mrf = new MascotDistillerMergeFileReader(input);
            String[] titles = mrf.getAllSpectrumTitles();
            // Length check.
            Assert.assertEquals(100, titles.length);

            // In-deep check, using the control file.
            BufferedReader control = new BufferedReader(new FileReader(super.getFullFilePath("testMascotDistillerMergeFileReader_Control_titles.txt")));
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
