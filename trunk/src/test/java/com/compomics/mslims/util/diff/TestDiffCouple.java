/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 7-jan-2005
 * Time: 13:52:59
 */
package com.compomics.mslims.util.diff;

import junit.TestCaseLM;
import junit.framework.Assert;
import com.compomics.mslims.util.diff.DiffCouple;

import java.util.Collection;
import java.util.Iterator;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2005/02/02 10:37:43 $
 */

/**
 * This class test the behaviour of the DiffCouple class.
 *
 * @author Lennart Martens
 * @version $Id: TestDiffCouple.java,v 1.2 2005/02/02 10:37:43 lennart Exp $
 * @see com.compomics.mslims.util.diff.DiffCouple
 */
public class TestDiffCouple extends TestCaseLM {

    public TestDiffCouple() {
        this("This class provides the test scenario for the DiffCouple class.");
    }
    public TestDiffCouple(String aName) {
        super(aName);
    }

    /**
     * This method tests the behaviour of a standard DiffCouple (no adding).
     */
    public void testSingleCouple() {
        DiffCouple dc = new DiffCouple("test.txt", 1235.655, 44675);
        Assert.assertFalse(dc.isSingle());
        Assert.assertEquals(1235.655, dc.getLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(1235.655, dc.getSummedLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getSummedHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(0.02765875769445999, dc.getRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.02765875769445999, dc.getRatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.02765875769445999, dc.getRatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-5.176119831127659, dc.getLog2Ratio(), Double.MIN_VALUE);
        Assert.assertEquals(-5.176119831127659, dc.getLog2RatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-5.176119831127659, dc.getLog2RatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(1, dc.getCount());

        dc = new DiffCouple("test.txt", 0, 2334);
        Assert.assertTrue(dc.isSingle());
        dc = new DiffCouple("test.txt", 0, -2334);
        Assert.assertTrue(dc.isSingle());
        dc = new DiffCouple("test.txt", 1, 0);
        Assert.assertTrue(dc.isSingle());
        dc = new DiffCouple("test.txt", -1, 0);
        Assert.assertTrue(dc.isSingle());
        dc = new DiffCouple("test.txt", 0, 0);
        Assert.assertFalse(dc.isSingle());
    }

    /**
     * This method tests the behaviour of a DiffCouple that was created from different merged couples.
     */
    public void testClusteredCouple() {
        DiffCouple dc = new DiffCouple("test.txt", 1235.655, 44675);
        dc.addCouple(new DiffCouple("", 34.5, 66.89));
        dc.addCouple(new DiffCouple("", 44.9, 73.56));
        dc.addCouple(new DiffCouple("", 56, 80));
        dc.addCouple(new DiffCouple("", 12, 18.9));
        Assert.assertEquals(1235.655, dc.getLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(5, dc.getCount());
        Assert.assertEquals(1383.055, dc.getSummedLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44914.35, dc.getSummedHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(0.06101884797387852, dc.getRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.06101884797387852, dc.getRatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.4977475270518341, dc.getRatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-4.0346012474533979, dc.getLog2Ratio(), Double.MIN_VALUE);
        Assert.assertEquals(-4.0346012474533979, dc.getLog2RatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-1.0065139466340365, dc.getLog2RatioAsAverageRatio(), Double.MIN_VALUE);
    }

    /**
     * This method tests the application of a corrective factor on the ratio.
     */
    public void testCorrectionFactor() {
        DiffCouple dc = new DiffCouple("test.txt", 1235.655, 44675);
        dc.setCorrection(0.0);
        Assert.assertFalse(dc.isSingle());
        Assert.assertEquals(1235.655, dc.getLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(1235.655, dc.getSummedLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getSummedHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(0.02765875769445999, dc.getRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.02765875769445999, dc.getRatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.02765875769445999, dc.getRatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-5.176119831127659, dc.getLog2Ratio(), Double.MIN_VALUE);
        Assert.assertEquals(-5.176119831127659, dc.getLog2RatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-5.176119831127659, dc.getLog2RatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(1, dc.getCount());

        dc.setCorrection(1.0);
        Assert.assertFalse(dc.isSingle());
        Assert.assertEquals(1235.655, dc.getLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(1235.655, dc.getSummedLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getSummedHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(1.02765875769445999, dc.getRatio(), Double.MIN_VALUE);
        Assert.assertEquals(1.02765875769445999, dc.getRatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(1.02765875769445999, dc.getRatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.0393612856306253, dc.getLog2Ratio(), Double.MIN_VALUE);
        Assert.assertEquals(0.0393612856306253, dc.getLog2RatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.0393612856306253, dc.getLog2RatioAsAverageRatio(), Double.MIN_VALUE);

        dc.setCorrection(-0.001);
        Assert.assertFalse(dc.isSingle());
        Assert.assertEquals(1235.655, dc.getLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(1235.655, dc.getSummedLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getSummedHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(0.026658757694459988, dc.getRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.026658757694459988, dc.getRatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.026658757694459988, dc.getRatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-5.229246637772493, dc.getLog2Ratio(), Double.MIN_VALUE);
        Assert.assertEquals(-5.229246637772493, dc.getLog2RatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-5.229246637772493, dc.getLog2RatioAsAverageRatio(), Double.MIN_VALUE);

        DiffCouple dc1 = new DiffCouple("", 34.5, 66.89);
        dc.addCouple(dc1);
        DiffCouple dc2 = new DiffCouple("", 44.9, 73.56);
        dc.addCouple(dc2);
        DiffCouple dc3 = new DiffCouple("", 56, 80);
        dc.addCouple(dc3);
        DiffCouple dc4 = new DiffCouple("", 12, 18.9);
        dc.addCouple(dc4);
        dc.setCorrection(0.0);
        Assert.assertEquals(1235.655, dc.getLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(5, dc.getCount());
        Assert.assertEquals(1383.055, dc.getSummedLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44914.35, dc.getSummedHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(0.06101884797387852, dc.getRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.06101884797387852, dc.getRatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.4977475270518341, dc.getRatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-4.0346012474533979, dc.getLog2Ratio(), Double.MIN_VALUE);
        Assert.assertEquals(-4.0346012474533979, dc.getLog2RatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-1.0065139466340365, dc.getLog2RatioAsAverageRatio(), Double.MIN_VALUE);

        dc.setCorrection(0.03);
        Assert.assertEquals(1235.655, dc.getLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(5, dc.getCount());
        Assert.assertEquals(1383.055, dc.getSummedLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44914.35, dc.getSummedHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(0.08934027759062751, dc.getRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.08934027759062751, dc.getRatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.5037475270518341, dc.getRatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-3.4845454525053987, dc.getLog2Ratio(), Double.MIN_VALUE);
        Assert.assertEquals(-3.4845454525053987, dc.getLog2RatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(-0.9892272435669806, dc.getLog2RatioAsAverageRatio(), Double.MIN_VALUE);

        dc1.setCorrection(0.001);
        dc2.setCorrection(100);
        dc3.setCorrection(-0.0007);
        dc4.setCorrection(0.00045);
        Assert.assertEquals(1235.655, dc.getLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44675, dc.getHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(5, dc.getCount());
        Assert.assertEquals(1383.055, dc.getSummedLightIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(44914.35, dc.getSummedHeavyIntensity(), Double.MIN_VALUE);
        Assert.assertEquals(1.794448107916538, dc.getRatio(), Double.MIN_VALUE);
        Assert.assertEquals(1.794448107916538, dc.getRatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(20.503897527051834, dc.getRatioAsAverageRatio(), Double.MIN_VALUE);
        Assert.assertEquals(0.84354020371463039, dc.getLog2Ratio(), Double.MIN_VALUE);
        Assert.assertEquals(0.84354020371463039, dc.getLog2RatioAsWeightedRatio(), Double.MIN_VALUE);
        Assert.assertEquals(4.35782626844691295, dc.getLog2RatioAsAverageRatio(), Double.MIN_VALUE);
    }

    /**
     * This method tests the outlier detection mechanism and the statistics.
     */
    public void testOutliersAndStats() {
        DiffCouple dc = new DiffCouple("test.txt", 1235.655, 44675);
        Assert.assertEquals(0, dc.checkOutliers());

        dc.addCouple(new DiffCouple("", 34.5, 66.89));
        dc.addCouple(new DiffCouple("", 44.9, 73.56));
        dc.addCouple(new DiffCouple("", 56, 80));
        dc.addCouple(new DiffCouple("", 12, 18.9));
        Assert.assertEquals(98, dc.checkOutliers());
        double[] stats = dc.getLocationAndScale();
        Assert.assertEquals(0.6103860793909733, stats[0], Double.MIN_VALUE);
        Assert.assertEquals(0.12014275329371414, stats[1], Double.MIN_VALUE);
        Assert.assertEquals(98, dc.isOutlier(stats[0], stats[1]));
        Collection children = dc.getMergedEntries();
        for (Iterator lIterator = children.iterator(); lIterator.hasNext();) {
            DiffCouple lDiffCouple = (DiffCouple)lIterator.next();
            Assert.assertEquals(0, lDiffCouple.isOutlier(stats[0], stats[1]));
        }

        dc = new DiffCouple("test.txt", 35.655, 44);
        DiffCouple dc2 = new DiffCouple("", 36, 42);
        dc.addCouple(dc2);
        DiffCouple dc3 = new DiffCouple("", 27, 40);
        dc.addCouple(dc3);
        DiffCouple dc4 = new DiffCouple("", 38, 46);
        dc.addCouple(dc4);
        Assert.assertEquals(95, dc.checkOutliers());
        stats = dc.getLocationAndScale();
        Assert.assertEquals(0.8182139328063242, stats[0], Double.MIN_VALUE);
        Assert.assertEquals(0.07227025599619746, stats[1], Double.MIN_VALUE);
        Assert.assertEquals(0, dc.isOutlier(stats[0], stats[1]));
        Assert.assertEquals(0, dc2.isOutlier(stats[0], stats[1]));
        Assert.assertEquals(95, dc3.isOutlier(stats[0], stats[1]));
        Assert.assertEquals(0, dc4.isOutlier(stats[0], stats[1]));
    }
}
