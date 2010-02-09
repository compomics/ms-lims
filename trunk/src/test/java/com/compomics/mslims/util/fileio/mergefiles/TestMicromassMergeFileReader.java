/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 25-feb-03
 * Time: 12:12:09
 */
package com.compomics.mslims.util.fileio.mergefiles;

import com.compomics.mslims.util.fileio.PKLFile;
import com.compomics.mslims.util.fileio.interfaces.MergeFileReader;
import com.compomics.mslims.util.fileio.mergefiles.MicromassMergeFileReader;
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
 * $Date: 2004/02/12 13:30:42 $
 */

/**
 * This class implements the test scenario for the MicromassMergeFileReader class.
 *
 * @author Lennart Martens
 * @see com.compomics.mslims.util.fileio.mergefiles.MicromassMergeFileReader
 */
public class TestMicromassMergeFileReader extends TestCaseLM {

    public TestMicromassMergeFileReader() {
        this("Test for the MicromassMergeFileReader class.");
    }

    public TestMicromassMergeFileReader(String aName) {
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
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath("testMicromassMergeFileReader_Control1.txt")));
            StringBuffer lsb = new StringBuffer();
            String line = null;
            while((line = br.readLine()) != null) {
                lsb.append(line + "\n");
            }
            br.close();
            control1 = lsb.toString();

            br = new BufferedReader(new FileReader(super.getFullFilePath("testMicromassMergeFileReader_Control2.txt")));
            lsb = new StringBuffer();
            line = null;
            while((line = br.readLine()) != null) {
                lsb.append(line + "\n");
            }
            br.close();
            control2 = lsb.toString();
        } catch(IOException ioe) {
            fail("Unable to read control files for test of MicromassMergeFileReader: " + ioe.getMessage() + "!");
        }

        // The first merge file.
        try {
            // First just the String with the filename.
            String file = super.getFullFilePath("testMicromassMergeFileReader1.txt");
            MergeFileReader mrf = new MicromassMergeFileReader(file);
            Assert.assertEquals(new File(file).getName(), mrf.getFilename());
            Assert.assertEquals(control1, mrf.toString());

            // Now try the file instead of the filename.
            File f = new File(file);
            mrf = new MicromassMergeFileReader(f);
            Assert.assertEquals(f.getName(), mrf.getFilename());
            Assert.assertEquals(control1, mrf.toString());
        } catch(IOException ioe) {
            fail("IOException while testing the first mergefilereader test: " + ioe.getMessage() + "!");
        }

        // The second merge file.
        try {
            // First just the String with the filename.
            String file = super.getFullFilePath("testMicromassMergeFileReader2.txt");
            MergeFileReader mrf = new MicromassMergeFileReader(file);
            Assert.assertEquals(control2, mrf.toString());

            // Now try the file instead of the filename.
            File f = new File(file);
            mrf = new MicromassMergeFileReader(f);
            Assert.assertEquals(control2, mrf.toString());
        } catch(IOException ioe) {
            fail("IOException while testing the second mergefilereader test: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the matching with a contained PKLfile.
     */
    public void testFindMatchingPKLFile() {
        try {
            MergeFileReader mrf = new MicromassMergeFileReader(super.getFullFilePath("testMicromassMergeFileReader2.txt"));
            MascotIdentifiedSpectrum mis = new MascotIdentifiedSpectrum();
            mis.setPrecursorMZ(1038.4587);
            mis.setChargeState(2);
            mis.setLowestMass(101.09);
            mis.setHighestMass(1931.03);
            mis.setLeastIntense(0.0091);
            mis.setMostIntense(1279.26);
            PKLFile result = (PKLFile)mrf.findMatchingSpectrumFile(mis);
            Assert.assertTrue(result != null);
            String filename = mrf.getCorrespondingSpectrumFilename(mis);
            Assert.assertEquals("caplc1709.032.2.2.pkl", filename);
            Assert.assertEquals(filename, result.getFilename());

            // Second pass.
            mis.setPrecursorMZ(456.7132);
            mis.setChargeState(2);
            mis.setLowestMass(301.0921);
            mis.setHighestMass(1268.0670);
            mis.setLeastIntense(0.1925);
            mis.setMostIntense(111.9731);
            result = (PKLFile)mrf.findMatchingSpectrumFile(mis);
            Assert.assertTrue(result != null);
            filename = mrf.getCorrespondingSpectrumFilename(mis);
            Assert.assertEquals("caplc1708.102.2.2.pkl", filename);
            Assert.assertEquals(filename, result.getFilename());

        } catch(IOException ioe) {
            fail("IOException while testing the matching of a PKL file in a MicromassMergeFileReader vs. a MIS: " + ioe.getMessage() + "!");
        }
    }

    /**
     * This method test the reporing of all the PKL files in the mergefile.
     */
    public void testPKLFilenameReporting() {
        try {
            MergeFileReader mrf = new MicromassMergeFileReader(super.getFullFilePath("testMicromassMergeFileReader2.txt"));
            String[] names = mrf.getAllSpectrumFilenames();
            // Length check.
            Assert.assertEquals(299, names.length);

            // Independantly reading the file.
            BufferedReader br = new BufferedReader(new FileReader(super.getFullFilePath("testMicromassMergeFileReader2.txt")));
            String line = null;
            String[] control = new String[299];
            int counter = 0;
            while((line = br.readLine()) != null) {
                int start = -1;
                if((start = line.indexOf("caplc")) >= 0) {
                    control[counter] = line.substring(start).trim();
                    counter++;
                }
            }
            br.close();
            // Check them all.
            // (Note that this check relies on the MergeFileReader to store the PKL files in the order they
            // were read from the mergefile!)
            for(int i = 0; i < names.length; i++) {
                Assert.assertEquals(control[i], names[i]);
            }
        } catch(IOException ioe) {
            fail("IOException while testing the reporting of the PKL filenames by a MicromassMergeFileReader: " + ioe.getMessage() + "!");
        }
    }
}
