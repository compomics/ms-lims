/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compomics.mslimscore.util.modificationconversion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Davy
 */
public class ModificationConversion {

    private static Logger logger = Logger.getLogger(ModificationConversion.class);
    private static ModificationConversion singleton;
    private final Connection iConnection;
    private HashMap iConversionMap;

    public static ModificationConversion getInstance(Connection iConnection) {
        if (singleton == null) {
            singleton = new ModificationConversion(iConnection);
        }
        return singleton;
    }

    private ModificationConversion(Connection iConnection) {
        this.iConnection = iConnection;
    }

    public HashMap getConversionMap() {
        //TODO write tests for ms-lims connection conversionmap
        initModificationConversionMap(iConnection);
        return iConversionMap;
    }

    private void initModificationConversionMap(Connection iConnection) {
        try {
            iConversionMap = new HashMap();
            String lKey = "";
            String lValue = "";
            PreparedStatement stat = iConnection.prepareStatement("select modification,conversion from modification_conversion");
            ResultSet rs = stat.executeQuery();
            while (rs.next()) {
                /* Skip comments and empty lines.
                 if (line.trim().startsWith("#") || line.trim().equals("")) {
                 continue;
                 }
                 StringTokenizer lst = new StringTokenizer(line, "=");
                 String lKey = lst.nextToken().trim();
                 String lValue = lst.nextToken().trim();

                 // Check for illegal characters in the short name.
                 char lIllegalChar = '-';

                 if (lValue.indexOf(lIllegalChar) != -1) {
                 illegalShortName(lKey, lValue, lIllegalChar);
                 }
                 lIllegalChar = '#';
                 if (lValue.indexOf(lIllegalChar) != -1) {
                 illegalShortName(lKey, lValue, lIllegalChar);
                 }
                 */
                lKey = rs.getString("modification");
                lValue = rs.getString("conversion");
                iConversionMap.put(lKey, lValue);
            }
        } catch (SQLException sqle) {
            logger.error(sqle);
        }
    }
}
