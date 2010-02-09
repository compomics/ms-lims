/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 31-jul-2003
 * Time: 13:02:32
 */
package com.compomics.mslims.util.netphos;

import com.compomics.mslims.util.netphos.NetphosPrediction;
import com.compomics.mslims.util.netphos.PredictedLocation;
import junit.TestCaseLM;
import junit.framework.Assert;

import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/02/12 13:30:42 $
 */

/**
 * This class implements the test scenario for the NetphosPrediction class.
 *
 * @author Lennart Martens.
 * @see com.compomics.mslims.util.netphos.NetphosPrediction
 */
public class TestNetphosPrediction extends TestCaseLM {

    public TestNetphosPrediction() {
        this("Test scenario for the NetphosPrediction class.");
    }

    public TestNetphosPrediction(String aName) {
        super(aName);
    }

    /**
     * This method test the filtering behaviour of the
     * 'getLocations' methods.
     */
    public void testFiltering() {
        // Set-up.
        Vector controlAll = new Vector(5);
        Vector controlSubset = new Vector(3);

        // The predicted locations.
        PredictedLocation pl1 = new PredictedLocation(2, "ATTYSS", "T", 0.23);
        PredictedLocation pl2 = new PredictedLocation(3, "ATTYSS", "T", 0.93);
        PredictedLocation pl3 = new PredictedLocation(4, "ATTYSS", "Y", 0.83);
        PredictedLocation pl4 = new PredictedLocation(5, "ATTYSS", "S", 0.73);
        PredictedLocation pl5 = new PredictedLocation(6, "ATTYSS", "S", 0.80);

        // controlAll takes all the predicted locations.
        controlAll.add(pl1);
        controlAll.add(pl2);
        controlAll.add(pl3);
        controlAll.add(pl4);
        controlAll.add(pl5);

        // controlSubset takes only the predicted locations that have a score
        // equal to or higher than '0.8'.
        controlSubset.add(pl2);
        controlSubset.add(pl3);
        controlSubset.add(pl5);

        // Okay, set-up nearly complete. Creating a NetphosPrediction for these locations.
        NetphosPrediction np = new NetphosPrediction("P00001");
        np.addPrediction(pl1);
        np.addPrediction(pl2);
        np.addPrediction(pl3);
        np.addPrediction(pl4);
        np.addPrediction(pl5);

        // Testing without threshold.
        Vector temp = np.getLocations();
        int liSize = controlAll.size();
        Assert.assertEquals(liSize, temp.size());
        for(int i = 0; i < liSize; i++) {
            Assert.assertEquals(controlAll.elementAt(i), temp.elementAt(i));
        }

        // Testing with '0' threshold.
        temp = np.getLocations(0.0);
        liSize = controlAll.size();
        Assert.assertEquals(liSize, temp.size());
        for(int i = 0; i < liSize; i++) {
            Assert.assertEquals(controlAll.elementAt(i), temp.elementAt(i));
        }

        // Testing with '0.8' threshold.
        temp = np.getLocations(0.8);
        liSize = controlSubset.size();
        Assert.assertEquals(liSize, temp.size());
        for(int i = 0; i < liSize; i++) {
            Assert.assertEquals(controlSubset.elementAt(i), temp.elementAt(i));
        }
    }
}
