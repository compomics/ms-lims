package com.compomics.mslimscore.util.conversiontool.implementations;


import com.compomics.mslimscore.util.conversiontool.interfaces.DBConverterStep;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: Niklaas Date: 15-Jan-2010 Time: 12:43:34 To change this template use File | Settings
 * | File Templates.
 */
public class QuantitationGroupIncrementSetter implements DBConverterStep {
    // Class specific log4j logger for DbConversionToolGuiEdition instances.
    private static Logger logger = Logger.getLogger(QuantitationGroupIncrementSetter.class);


    public boolean performConversionStep(Connection aConn) {
        //get the maximum in the quantitation_group table
        try {
            // First get all the quantitations  grouped by quantitation_link
            PreparedStatement stat = aConn.prepareStatement("select MAX(q.quantitation_groupid) from quantitation_group q;");
            ResultSet rs = stat.executeQuery();
            Integer lMax = 0;
            while (rs.next()) {
                lMax = rs.getInt(1);
            }
            rs.close();
            stat.close();
            if (lMax != null && lMax > 0) {
                //we have to reset it
                PreparedStatement stat1 = aConn.prepareStatement("ALTER TABLE `quantitation_group` AUTO_INCREMENT = ?");
                stat1.setInt(1, (lMax + 1));
                stat1.executeUpdate();
                stat1.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
