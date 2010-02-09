/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 15-mrt-03
 * Time: 12:52:57
 */
package com.compomics.mslims.util.mascot;

import com.compomics.mslims.util.mascot.MascotHeader;
import com.compomics.mslims.util.mascot.MascotIsoforms;
import junit.TestCaseLM;
import junit.framework.Assert;

/*
 * CVS information:
 *
 * $Revision: 1.5 $
 * $Date: 2007/05/01 13:30:44 $
 */

/**
 * This class implements the test scenario for the MascotIsoforms class.
 *
 * @author Lennart Martens
 * @see com.compomics.mslims.util.mascot.MascotIsoforms
 */
public class TestMascotIsoforms extends TestCaseLM {

    public TestMascotIsoforms() {
        this("Test scenario for the MascotIsoforms class.");
    }

    public TestMascotIsoforms(String aName) {
        super(aName);
    }

    /**
     * This method will test the addition and subsequent processing
     * of headers of isoforms.
     */
    public void testAdditionOfIsoforms() {
        MascotIsoforms mi = new MascotIsoforms();

        mi.addIsoform("P065453", "Isoform 1.");
        mi.addIsoform("P055433", "Isoform 2.");
        mi.addIsoform("P777773", "Isoform 3.");
        mi.addIsoform("108475654", "Isoform 4.");
        Assert.assertEquals(4, mi.getIsoforms().size());

        mi.addIsoform("P065453", "Isoform 1.");
        Assert.assertEquals(4, mi.getIsoforms().size());

        mi.addIsoform("P065453 (66-73)", "Isoform 1.");
        mi.addIsoform("108475654 (1345-1355)", "Isoform 4.");
        Assert.assertEquals(4, mi.getIsoforms().size());
        MascotHeader mh = (MascotHeader)mi.getIsoforms().get("P065453");
        Assert.assertEquals("P065453", mh.getAccession());
        Assert.assertEquals(66, mh.getStart());
        Assert.assertEquals(73, mh.getEnd());
        Assert.assertEquals("Isoform 1.", mh.getDescription());
        mh = (MascotHeader)mi.getIsoforms().get("108475654");
        Assert.assertEquals("108475654", mh.getAccession());
        Assert.assertEquals(1345, mh.getStart());
        Assert.assertEquals(1355, mh.getEnd());
        Assert.assertEquals("Isoform 4.", mh.getDescription());

        mi.addIsoform("P065453 (68-70)", "Isoform 1.");
        mi.addIsoform("108475654 (1342-1356)", "Isoform 4.");
        Assert.assertEquals(4, mi.getIsoforms().size());
        mh = (MascotHeader)mi.getIsoforms().get("P065453");
        Assert.assertEquals("P065453", mh.getAccession());
        Assert.assertEquals(68, mh.getStart());
        Assert.assertEquals(70, mh.getEnd());
        Assert.assertEquals("Isoform 1.", mh.getDescription());
        mh = (MascotHeader)mi.getIsoforms().get("108475654");
        Assert.assertEquals("108475654", mh.getAccession());
        Assert.assertEquals(1345, mh.getStart());
        Assert.assertEquals(1355, mh.getEnd());
        Assert.assertEquals("Isoform 4.", mh.getDescription());

        mi.addIsoform("108475654 (1348-1355)", "Isoform 4.");
        mh = (MascotHeader)mi.getIsoforms().get("108475654");
        Assert.assertEquals("108475654", mh.getAccession());
        Assert.assertEquals(1348, mh.getStart());
        Assert.assertEquals(1355, mh.getEnd());
        Assert.assertEquals("Isoform 4.", mh.getDescription());

        mi.addIsoform("108475654 (1345-1354)", "Isoform 4.");
        mh = (MascotHeader)mi.getIsoforms().get("108475654");
        Assert.assertEquals("108475654", mh.getAccession());
        Assert.assertEquals(1345, mh.getStart());
        Assert.assertEquals(1354, mh.getEnd());
        Assert.assertEquals("Isoform 4.", mh.getDescription());
    }

    /**
     * This method test retrieving an isoform, based on an input list or
     * by natural ordering.
     */
    public void testRetrieving() {
        MascotIsoforms mi = new MascotIsoforms();

        mi.addIsoform("P065453", "Isoform 1.");
        mi.addIsoform("P055433", "Isoform 2.");
        mi.addIsoform("P777773", "Isoform 3.");
        mi.addIsoform("108475654", "Isoform 4.");
        mi.addIsoform("P065453", "Isoform 1.");
        mi.addIsoform("P065453 (66-73)", "Isoform 1.");
        mi.addIsoform("108475654 (1345-1355)", "Isoform 4.");
        mi.addIsoform("P065453 (68-70)", "Isoform 1.");
        mi.addIsoform("108475654 (1342-1356)", "Isoform 4.");
        mi.addIsoform("108475654 (1348-1355)", "Isoform 4.");
        mi.addIsoform("108475654 (1345-1354)", "Isoform 4.");

        MascotHeader mh = mi.getMainHeader(null);
        Assert.assertEquals("108475654 (1345-1354)", mh.getCompoundAccession());
        Assert.assertEquals("P055433^AP065453 (68-70)^AP777773", mh.getIsoformAccessions());

        String[] tests = new String[]{"P08762", "108475654", "P065453"};
        mh = mi.getMainHeader(tests);
        Assert.assertEquals("P065453 (68-70)", mh.getCompoundAccession());
        Assert.assertEquals("108475654 (1345-1354)^AP055433^AP777773", mh.getIsoformAccessions());
    }
}
