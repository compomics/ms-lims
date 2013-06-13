/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 15-mrt-03
 * Time: 11:52:22
 */
package com.compomics.mslims.util.mascot;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.mascot.MascotHeader;
import junit.TestCaseLM;
import junit.framework.Assert;

/*
 * CVS information:
 *
 * $Revision: 1.4 $
 * $Date: 2005/11/28 11:19:17 $
 */

/**
 * This class implements the test scenario for the MascotHeader class.
 *
 * @author Lennart Martens.
 * @see com.compomics.mslims.util.mascot.MascotHeader
 */
public class TestMascotHeader extends TestCaseLM {
    // Class specific log4j logger for TestMascotHeader instances.
    private static Logger logger = Logger.getLogger(TestMascotHeader.class);

    public TestMascotHeader() {
        this("Test scenario for the MascotHeader class.");
    }

    public TestMascotHeader(String aName) {
        super(aName);
    }

    /**
     * This method test the creation of a header object and the corresponding parsing of the start and end information,
     * if present.
     */
    public void testCreation() {
        // Without localization.
        String accession = "P0864354";
        String description = "This is a test for the MascotHeader.";
        MascotHeader mh = new MascotHeader(accession, description);
        Assert.assertEquals(accession, mh.getAccession());
        Assert.assertEquals(accession, mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == -1);
        Assert.assertTrue(mh.getEnd() == -1);

        accession = "1564883783";
        description = "This is a second test for the MascotHeader.";
        mh = new MascotHeader(accession, description);
        Assert.assertEquals(accession, mh.getAccession());
        Assert.assertEquals(accession, mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == -1);
        Assert.assertTrue(mh.getEnd() == -1);

        // With localization.
        accession = "P66545 (123-135)";
        description = "This is a third test for the MascotHeader.";
        mh = new MascotHeader(accession, description);
        Assert.assertEquals(accession.substring(0, accession.indexOf(" (")), mh.getAccession());
        Assert.assertEquals(accession, mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 123);
        Assert.assertTrue(mh.getEnd() == 135);

        accession = "167467859 (123-135)";
        description = "This is a fourth test for the MascotHeader.";
        mh = new MascotHeader(accession, description);
        Assert.assertEquals(accession.substring(0, accession.indexOf(" (")), mh.getAccession());
        Assert.assertEquals(accession, mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 123);
        Assert.assertTrue(mh.getEnd() == 135);


        accession = "P66545 (1a3-1b5)";
        description = "This is a fifth test for the MascotHeader.";
        mh = new MascotHeader(accession, description);
        Assert.assertEquals(accession, mh.getAccession());
        Assert.assertEquals(accession, mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == -1);
        Assert.assertTrue(mh.getEnd() == -1);


        accession = "P66545 ()77-";
        description = "This is a sixth test for the MascotHeader.";
        mh = new MascotHeader(accession, description);
        Assert.assertEquals(accession, mh.getAccession());
        Assert.assertEquals(accession, mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == -1);
        Assert.assertTrue(mh.getEnd() == -1);
    }

    /**
     * This method test the updating of the start and end locations.
     */
    public void testUpdatingLocation() {
        String accession = "P66545 (123-135)";
        String description = "This is a first test for the MascotHeader location.";
        MascotHeader mh = new MascotHeader(accession, description);
        Assert.assertEquals(accession.substring(0, accession.indexOf(" (")), mh.getAccession());
        Assert.assertEquals(accession, mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 123);
        Assert.assertTrue(mh.getEnd() == 135);

        // Try to update start and header.
        mh.updateLocation(125, 134);
        Assert.assertEquals(accession.substring(0, accession.indexOf(" (")), mh.getAccession());
        Assert.assertEquals(accession.substring(0, accession.indexOf(" (")) + " (125-134)", mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 125);
        Assert.assertTrue(mh.getEnd() == 134);

        // Try to update unsuccessfully.
        mh.updateLocation(119, 144);
        Assert.assertEquals(accession.substring(0, accession.indexOf(" (")), mh.getAccession());
        Assert.assertEquals(accession.substring(0, accession.indexOf(" (")) + " (125-134)", mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 125);
        Assert.assertTrue(mh.getEnd() == 134);

        mh.updateLocation(-1, -1);
        Assert.assertEquals(accession.substring(0, accession.indexOf(" (")), mh.getAccession());
        Assert.assertEquals(accession.substring(0, accession.indexOf(" (")) + " (125-134)", mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 125);
        Assert.assertTrue(mh.getEnd() == 134);

        // Now for one that has no start or end, and then gets some.
        accession = "P66545";
        description = "This is a second test for the MascotHeader location.";
        mh = new MascotHeader(accession, description);
        Assert.assertEquals(accession, mh.getAccession());
        Assert.assertEquals(accession, mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == -1);
        Assert.assertTrue(mh.getEnd() == -1);

        mh.updateLocation(125, 134);
        Assert.assertEquals(accession, mh.getAccession());
        Assert.assertEquals(accession + " (125-134)", mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 125);
        Assert.assertTrue(mh.getEnd() == 134);

        mh.updateLocation(256, 265);
        Assert.assertEquals(accession, mh.getAccession());
        Assert.assertEquals(accession + " (125-134)", mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 125);
        Assert.assertTrue(mh.getEnd() == 134);

        mh.updateLocation(36, 45);
        Assert.assertEquals(accession, mh.getAccession());
        Assert.assertEquals(accession + " (36-45)", mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 36);
        Assert.assertTrue(mh.getEnd() == 45);

        mh.updateLocation(256, 264);
        Assert.assertEquals(accession, mh.getAccession());
        Assert.assertEquals(accession + " (256-264)", mh.getCompoundAccession());
        Assert.assertEquals(description, mh.getDescription());
        Assert.assertTrue(mh.getStart() == 256);
        Assert.assertTrue(mh.getEnd() == 264);
    }

    /**
     * This method test the natural ordering of the headers.
     */
    public void testCompare() {
        MascotHeader mh1 = new MascotHeader("P00001", "test");
        MascotHeader mh2 = new MascotHeader("P65533", "test");
        Assert.assertTrue(mh1.compareTo(mh2) < 0);
        Assert.assertTrue(mh2.compareTo(mh1) > 0);

        mh1 = new MascotHeader("P65532", "test");
        mh2 = new MascotHeader("P65533", "test");
        Assert.assertTrue(mh1.compareTo(mh2) < 0);
        Assert.assertTrue(mh2.compareTo(mh1) > 0);

        mh1 = new MascotHeader("10874645", "test");
        mh2 = new MascotHeader("P65533", "test");
        Assert.assertTrue(mh1.compareTo(mh2) < 0);
        Assert.assertTrue(mh2.compareTo(mh1) > 0);

        mh1 = new MascotHeader("P65532", "test");
        mh2 = new MascotHeader("P65533 (32-39)", "test");
        Assert.assertTrue(mh1.compareTo(mh2) < 0);
        Assert.assertTrue(mh2.compareTo(mh1) > 0);

        mh1 = new MascotHeader("P65532 (34-41)", "test");
        mh2 = new MascotHeader("P65533", "test");
        Assert.assertTrue(mh1.compareTo(mh2) < 0);
        Assert.assertTrue(mh2.compareTo(mh1) > 0);

        mh1 = new MascotHeader("P65532 (34-41)", "test");
        mh2 = new MascotHeader("P65533 (32-39)", "test");
        Assert.assertTrue(mh1.compareTo(mh2) < 0);
        Assert.assertTrue(mh2.compareTo(mh1) > 0);

        mh1 = new MascotHeader("P65532 (34-41)", "test");
        mh2 = new MascotHeader("P65532 (32-39)", "test");
        Assert.assertTrue(mh2.compareTo(mh1) < 0);
        Assert.assertTrue(mh1.compareTo(mh2) > 0);

        mh1 = new MascotHeader("P65532", "test");
        mh2 = new MascotHeader("P65532 (33-43)", "test");
        Assert.assertTrue(mh2.compareTo(mh1) < 0);
        Assert.assertTrue(mh1.compareTo(mh2) > 0);

        mh1 = new MascotHeader("P65532 (144-156)", "test");
        mh2 = new MascotHeader("P65532", "test");
        Assert.assertTrue(mh1.compareTo(mh2) < 0);
        Assert.assertTrue(mh2.compareTo(mh1) > 0);

        mh1 = new MascotHeader("P65532", "test");
        mh2 = new MascotHeader("P65532", "test");
        Assert.assertTrue(mh1.compareTo(mh2) == 0);
        Assert.assertTrue(mh2.compareTo(mh1) == 0);
    }

    /**
     * This method tests the scoring of the Mascot Header class.
     */
    public void testScoring() {
        MascotHeader mh = new MascotHeader("ipi123454332", "UniProt/Swiss-Prot:Q9UHI6|REFSEQ_NP:NP_009135|UniProt/TrEMBL:Q9H4N4*Q8NEH0*Q8IYV2*Q8TDR3|ENSEMBL:ENSP00000181534 Tax_I");
        Assert.assertEquals(2, mh.getScore());
        mh = new MascotHeader("IPI0000001", " REFSEQ_NP:NP_006399|UniProt/TrEMBL:O95994|ENSEMBL:ENSP00000223274 Tax_I");
        Assert.assertEquals(1, mh.getScore());
        mh = new MascotHeader("IPI0000001", " REFSEQ_NP:NP_006399|ENSEMBL:ENSP00000223274 Tax_I");
        Assert.assertEquals(1, mh.getScore());
        mh = new MascotHeader("IPI0000001", " REFSEQ_XP:XP_006399|ENSEMBL:ENSP00000223274 Tax_I");
        Assert.assertEquals(0, mh.getScore());
        mh = new MascotHeader("P65532 (144-156)", "test");
        Assert.assertEquals(0, mh.getScore());
    }
}
