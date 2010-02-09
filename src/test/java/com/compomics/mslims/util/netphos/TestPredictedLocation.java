/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 31-jul-2003
 * Time: 13:13:29
 */
package com.compomics.mslims.util.netphos;

import com.compomics.mslims.util.netphos.PredictedLocation;
import junit.TestCaseLM;
import junit.framework.Assert;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/02/12 13:30:42 $
 */

/**
 * This class implements the test scenario for the PredictedLocation class.
 *
 * @author Lennart Martens
 * @see com.compomics.mslims.util.netphos.PredictedLocation
 */
public class TestPredictedLocation extends TestCaseLM {

    public TestPredictedLocation() {
        this("Test scenario for the PredictedLocation class.");
    }

    public TestPredictedLocation(String aName) {
        super(aName);
    }

    /**
     * This method test the equals method.
     */
    public void testEquals() {
        PredictedLocation pl1 = new PredictedLocation(1, "ABCD", "A", 0.87);
        PredictedLocation pl2 = new PredictedLocation(1, "BACD", "A", 0.87);
        PredictedLocation pl3 = new PredictedLocation(2, "ABCD", "A", 0.87);
        PredictedLocation pl4 = new PredictedLocation(1, "BACD", "C", 0.87);
        PredictedLocation pl5 = new PredictedLocation(1, "ABCD", "A", 0.88);
        PredictedLocation pl6 = new PredictedLocation(1, "ABCD", "A", 0.87);

        Assert.assertEquals(pl1, pl1);
        Assert.assertEquals(pl1, pl1);

        Assert.assertEquals(pl1, pl6);
        Assert.assertEquals(pl6, pl1);

        Assert.assertTrue(!pl1.equals(pl2));
        Assert.assertTrue(!pl2.equals(pl1));

        Assert.assertTrue(!pl1.equals(pl3));
        Assert.assertTrue(!pl3.equals(pl1));

        Assert.assertTrue(!pl1.equals(pl4));
        Assert.assertTrue(!pl4.equals(pl1));

        Assert.assertTrue(!pl1.equals(pl5));
        Assert.assertTrue(!pl5.equals(pl1));
    }

    /**
     * This method test the clone method.
     */
    public void testClone() {
        PredictedLocation pl1 = new PredictedLocation(1, "ABCD", "A", 0.87);
        PredictedLocation pl2 = (PredictedLocation)pl1.clone();

        Assert.assertEquals(pl1, pl2);
        Assert.assertEquals(pl2, pl1);

        pl2.setLocation(5);

        Assert.assertTrue(!pl1.equals(pl2));
        Assert.assertTrue(!pl2.equals(pl1));

        pl1.setLocation(5);
        Assert.assertEquals(pl1, pl2);
        Assert.assertEquals(pl2, pl1);
    }
}
