/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 5-sep-02
 * Time: 8:59:34
 */
package com.compomics.mslims;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.TestZipUtil;
import com.compomics.mslims.util.diff.TestDiffCouple;
import com.compomics.mslims.util.netphos.TestNetphosOutputReader;
import com.compomics.mslims.util.netphos.TestNetphosPrediction;
import com.compomics.mslims.util.netphos.TestPredictedLocation;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/*
 * CVS information:
 *
 * $Revision: 1.14 $
 * $Date: 2009/03/11 13:57:45 $
 */

/**
 * This class represents the full test suite for the LIMS project.
 *
 * @author Lennart Martens
 */
public class FullSuite extends TestCase {
    // Class specific log4j logger for FullSuite instances.
    private static Logger logger = Logger.getLogger(FullSuite.class);

    public FullSuite() {
        this("Full suite of test for LIMS project.");
    }

    public FullSuite(String aName) {
        super(aName);
    }

    public static Test suite() {
        TestSuite ts = new TestSuite();

        // The utils.
        ts.addTest(new TestSuite(TestZipUtil.class));
        // The TableAccessors.

        ts.addTest(new TestSuite(com.compomics.mslims.util.fileio.mergefiles.TestMicromassMergeFileReader.class));
        ts.addTest(new TestSuite(com.compomics.mslims.util.fileio.mergefiles.TestMascotGenericMergeFileReader.class));
        ts.addTest(new TestSuite(com.compomics.mslims.util.fileio.mergefiles.TestMascotDistillerMergeFileReader.class));
        ts.addTest(new TestSuite(com.compomics.mslims.util.fileio.mergefiles.TestMergeFileReaderFactory.class));
        ts.addTest(new TestSuite(com.compomics.mslims.util.fileio.TestPKLFile.class));
        ts.addTest(new TestSuite(com.compomics.mslims.util.fileio.TestDTAFile.class));
        ts.addTest(new TestSuite(com.compomics.mslims.util.fileio.TestMascotGenericFile.class));
        ts.addTest(new TestSuite(com.compomics.mslims.util.fileio.TestUltraflexXMLFile.class));
        ts.addTest(new TestSuite(com.compomics.mslims.util.mascot.TestMascotHeader.class));
        ts.addTest(new TestSuite(com.compomics.mslims.util.mascot.TestMascotIsoforms.class));
        ts.addTest(new TestSuite(TestPredictedLocation.class));
        ts.addTest(new TestSuite(TestNetphosPrediction.class));
        ts.addTest(new TestSuite(TestNetphosOutputReader.class));
        ts.addTest(new TestSuite(TestDiffCouple.class));


        return ts;
    }
}
