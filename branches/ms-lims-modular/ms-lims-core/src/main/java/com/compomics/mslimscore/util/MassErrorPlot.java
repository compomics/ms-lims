/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 25-feb-03
 * Time: 16:14:16
 */
package com.compomics.mslimscore.util;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class extracts data concerning mass errors vs. run and scan number.
 *
 * @author Lennart Martens
 */
public class MassErrorPlot {
    // Class specific log4j logger for MassErrorPlot instances.
    private static Logger logger = Logger.getLogger(MassErrorPlot.class);

    /**
     * Main method for this class. Implementation is horribly rudimentary at best.
     *
     * @param args String[] with the start-up arguments.
     */
    public static void main(String[] args) {

        if (args == null || args.length != 2) {
            logger.error("\n\nUsage:\n\tMassErrorPLot <password> <database>\n\n");
            System.exit(1);
        }
        try {
            Driver d = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
            Properties p = new Properties();
            p.put("user", "martlenn");
            p.put("password", args[0]);
            Connection c = d.connect("jdbc:mysql://localhost/" + args[1], p);
            PreparedStatement ps = c.prepareStatement("select filename, abs(exp_mass-cal_mass), (exp_mass-cal_mass), exp_mass, cal_mass from metoxid");
            ResultSet rs = ps.executeQuery();
            HashMap runs = new HashMap();
            HashMap runsDelta = new HashMap();
            while (rs.next()) {
                // Gather the data for this row.
                String filename = rs.getString(1);
                double absDelta = rs.getDouble(2);
                double delta = rs.getDouble(3);
                double exp = rs.getDouble(4);
                double cal = rs.getDouble(5);

                // Extract the run number.
                Long run = new Long(filename.substring(filename.indexOf("caplc") + 5, filename.indexOf('.')));
                Object loTemp = runs.get(run);
                if (loTemp != null) {
                    InnerLowHighAverage ip = (InnerLowHighAverage) loTemp;
                    ip.add(absDelta);
                } else {
                    InnerLowHighAverage ip = new InnerLowHighAverage();
                    ip.add(absDelta);
                    runs.put(run, ip);
                }
                loTemp = runsDelta.get(run);
                if (loTemp != null) {
                    InnerLowHighAverage ip = (InnerLowHighAverage) loTemp;
                    ip.add(delta);
                } else {
                    InnerLowHighAverage ip = new InnerLowHighAverage();
                    ip.add(delta);
                    runsDelta.put(run, ip);
                }
            }
            rs.close();
            ps.close();
            c.close();

            // Now output.
            Iterator iter = runs.keySet().iterator();
            logger.info(";run number;lowest error;average error;highest error;stdev;count");
            while (iter.hasNext()) {
                Long lLong = (Long) iter.next();
                InnerLowHighAverage ip = (InnerLowHighAverage) runs.get(lLong);
                InnerLowHighAverage ipDelta = (InnerLowHighAverage) runsDelta.get(lLong);
                logger.info(";" + lLong + ";" + ip.getLow() + ";" + ip.getAverage() + ";" + ip.getHigh() + ";" + ip.getStandardDev() + ";" + ip.getCount());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static class InnerLowHighAverage {

        private double iLow = Double.MAX_VALUE;
        private double iHigh = Double.MIN_VALUE;
        private Vector iAll = new Vector();

        public InnerLowHighAverage() {
        }

        public double getHigh() {
            return iHigh;
        }

        public double getLow() {
            return iLow;
        }

        public void add(double aDouble) {
            this.iAll.add(new Double(aDouble));
            if (aDouble > this.iHigh) {
                this.iHigh = aDouble;
            }
            if (aDouble < this.iLow) {
                this.iLow = aDouble;
            }
        }

        public double getAverage() {
            int liSize = this.iAll.size();
            double average = 0.0;
            if (liSize > 0) {
                double sum = 0d;
                for (int i = 0; i < liSize; i++) {
                    sum += ((Double) iAll.get(i)).doubleValue();
                }
                average = sum / (double) liSize;
            }
            return average;
        }

        public int getCount() {
            return this.iAll.size();
        }

        public double getStandardDev() {
            int liSize = this.iAll.size();
            double stdev = 0.0;
            if (liSize > 0) {
                double sum = 0d;
                double quadrSum = 0d;
                for (int i = 0; i < liSize; i++) {
                    double temp = ((Double) iAll.get(i)).doubleValue();
                    sum += temp;
                    quadrSum += Math.pow(temp, 2);
                }
                stdev = Math.sqrt(((liSize * quadrSum) - Math.pow(sum, 2)) / ((liSize - 1) * liSize));
            }
            return stdev;
        }
    }
}
