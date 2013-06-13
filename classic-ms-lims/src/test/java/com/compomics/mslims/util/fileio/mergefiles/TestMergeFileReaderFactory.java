/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 28-mrt-03
 * Time: 10:55:23
 */
package com.compomics.mslims.util.fileio.mergefiles;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.fileio.mergefiles.MascotGenericMergeFileReader;
import com.compomics.mslims.util.fileio.mergefiles.MergeFileReaderFactory;
import com.compomics.mslims.util.fileio.mergefiles.MicromassMergeFileReader;
import com.compomics.mslims.util.fileio.mergefiles.MascotDistillerMergeFileReader;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.io.File;
import java.io.IOException;

/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2009/01/30 10:31:05 $
 */

/**
 * This class implements the test scenario for the MergeFileReaderFactory.
 *
 * @author Lennart Martens
 * @see com.compomics.mslims.util.fileio.mergefiles.MergeFileReaderFactory
 */
public class TestMergeFileReaderFactory extends TestCaseLM {
    // Class specific log4j logger for TestMergeFileReaderFactory instances.
    private static Logger logger = Logger.getLogger(TestMergeFileReaderFactory.class);

    public TestMergeFileReaderFactory() {
        this("This is the test scenario for the MergeFileReaderFactory.");
    }

    public TestMergeFileReaderFactory(String aName) {
        super(aName);
    }

    /**
     * This method test the choosing of the appropriate factory.
     */
    public void testFactory() {
        try {
            String file = super.getFullFilePath("testMicromassMergeFileReader1.txt");
            Assert.assertTrue(MergeFileReaderFactory.getReaderForMergeFile(file) instanceof MicromassMergeFileReader);
            Assert.assertTrue(MergeFileReaderFactory.getReaderForMergeFile(new File(file)) instanceof MicromassMergeFileReader);
        } catch (IOException ioe) {
            fail("IOException while testing the MergeFailReaderFactory for a MicromassMergeFile: " + ioe.getMessage() + "!");
        }

        try {
            String file = super.getFullFilePath("testMascotGenericMergeFileReader1.txt");
            Assert.assertTrue(MergeFileReaderFactory.getReaderForMergeFile(file) instanceof MascotGenericMergeFileReader);
            Assert.assertTrue(MergeFileReaderFactory.getReaderForMergeFile(new File(file)) instanceof MascotGenericMergeFileReader);
        } catch (IOException ioe) {
            fail("IOException while testing the MergeFailReaderFactory for a Mascot Generic Format MergeFile: " + ioe.getMessage() + "!");
        }

        try {
            String file = super.getFullFilePath("testMascotDistiller~data~L59_Bart_Metox_080530A_forward_p2A01.RAW.-1.mgf");
            Assert.assertTrue(MergeFileReaderFactory.getReaderForMergeFile(file) instanceof MascotDistillerMergeFileReader);
            Assert.assertTrue(MergeFileReaderFactory.getReaderForMergeFile(new File(file)) instanceof MascotDistillerMergeFileReader);
        } catch (IOException ioe) {
            fail("IOException while testing the MergeFailReaderFactory for a Mascot Distiller MergeFile: " + ioe.getMessage() + "!");
        }


        try {
            String file = super.getFullFilePath("testDB.properties");
            MergeFileReaderFactory.getReaderForMergeFile(file);
            fail("No IOException thrown while testing the MergeFailReaderFactory for an inrecognizable file!");
        } catch (IOException ioe) {
            // this is as it should be!
        }

        try {
            String file = super.getFullFilePath("testDB.properties");
            MergeFileReaderFactory.getReaderForMergeFile(new File(file));
            fail("No IOException thrown while testing the MergeFailReaderFactory for an inrecognizable file!");
        } catch (IOException ioe) {
            // This is as it should be!
        }
    }
}
