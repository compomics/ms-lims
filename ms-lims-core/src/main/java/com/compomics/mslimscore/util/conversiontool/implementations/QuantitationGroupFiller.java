package com.compomics.mslimscore.util.conversiontool.implementations;

import com.compomics.mslimscore.util.conversiontool.interfaces.DBConverterStep;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA. User: Niklaas Date: 15-Jan-2010 Time: 11:33:03 To change this template use File | Settings
 * | File Templates.
 */
public class QuantitationGroupFiller implements DBConverterStep {
    // Class specific log4j logger for DbConversionToolGuiEdition instances.
    private static Logger logger = Logger.getLogger(QuantitationGroupFiller.class);


    public boolean performConversionStep(Connection aConn) {
        try {

            // First get all the quantitations  grouped by quantitation_link
            PreparedStatement stat = aConn.prepareStatement("select q.quantitation_link, q.l_quantitation_fileid, q.file_ref from quantitation as q group by q.quantitation_link");
            ResultSet rs = stat.executeQuery();
            Vector<Integer> lQuantitatationLinks = new Vector<Integer>();
            Vector<Integer> lQuantitationFileid = new Vector<Integer>();
            Vector<Integer> lFileRef = new Vector<Integer>();
            while (rs.next()) {
                lQuantitatationLinks.add(rs.getInt(1));
                lQuantitationFileid.add(rs.getInt(2));
                lFileRef.add(rs.getInt(3));
            }
            rs.close();
            stat.close();

            // Now create for every quantitation a new quantitation group
            for (int i = 0; i < lQuantitatationLinks.size(); i++) {
                PreparedStatement lStat = aConn.prepareStatement("INSERT INTO quantitation_group (quantitation_groupid, l_quantitation_fileid, file_ref, username, creationdate, modificationdate) values(?, ?, ?, CURRENT_USER, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
                lStat.setInt(1, lQuantitatationLinks.get(i));
                lStat.setInt(2, lQuantitationFileid.get(i));
                lStat.setInt(3, lFileRef.get(i));
                if (i % 1000 == 0) {
                    System.out.println(i + "/" + lQuantitatationLinks.size());
                }
                int result = lStat.executeUpdate();
                if (result == 0) {
                    System.out.println("error");
                }
                lStat.close();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
