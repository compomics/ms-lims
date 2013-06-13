package com.compomics.mslims.db.utils;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: Davy
 * Date: 1/10/12
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class CopyStructure {

    private Statement stmtin;
    private PreparedStatement stmtout;
    private ResultSet inset;

    public CopyStructure(Connection inconn,Connection outconn) throws SQLException {

        stmtin = inconn.createStatement();

        inset = stmtin.executeQuery("select * from filedescriptor");
        stmtout = outconn.prepareStatement("insert into filedescriptor (filedescriptorid,short_label,description,username,creationdate,modificationdate) values (?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        while (inset.next()){
            for (int i = 0; i < 4; i++) {
                stmtout.setObject(i+1,inset.getObject(i + 1));
            }

        stmtout.addBatch();
        }
       stmtout.executeBatch();

       inset = stmtin.executeQuery("select * from fragmentation");
       stmtout = outconn.prepareStatement("insert into fragmentation (fragmentationid,description,username,creationdate,modificationdate) values (?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        while (inset.next()){
            for (int i = 0; i < 3; i++) {
                stmtout.setObject(i + 1,inset.getObject(i + 1));
            }

        stmtout.addBatch();
        }
       stmtout.executeBatch();

       inset = stmtin.executeQuery("select * from id_to_phospho");
       stmtout = outconn.prepareStatement("insert into id_to_phospho (l_id,l_phosphorylationid,conversionid) values (?,?,?)");

        while (inset.next()){
            for (int i = 0; i < 3; i++) {
                stmtout.setObject(i + 1,inset.getObject(i + 1));
            }

        stmtout.addBatch();
        }
       stmtout.executeBatch();


      inset = stmtin.executeQuery("select * from instrument");
       stmtout = outconn.prepareStatement("insert into instrument (instrumentid,name,description,storageclassname,propertiesfilename,differential_calibration,creationdate,modificationdate) values (?,?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        while (inset.next()){
            for (int i = 0; i < 6; i++) {
                stmtout.setObject(i + 1,inset.getObject(i + 1));
            }

        stmtout.addBatch();
        }
       stmtout.executeBatch();

    inset = stmtin.executeQuery("select * from ionscoring");
       stmtout = outconn.prepareStatement("insert into ionscoring (ionscoringid,description,username,creationdate,modificationdate) values (?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        while (inset.next()){
            for (int i = 0; i < 3; i++) {
                stmtout.setObject(i + 1,inset.getObject(i + 1));
            }

        stmtout.addBatch();
        }
       stmtout.executeBatch();

    inset = stmtin.executeQuery("select * from modification_conversion");
       stmtout = outconn.prepareStatement("insert into modification_conversion (modification_conversionid,modification,conversion,username,creationdate,modificationdate) values (?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        while (inset.next()){
            for (int i = 0; i < 4; i++) {
                stmtout.setObject(i + 1,inset.getObject(i + 1));
            }

        stmtout.addBatch();
        }
       stmtout.executeBatch();



inset = stmtin.executeQuery("select * from ms_lims_properties");
       stmtout = outconn.prepareStatement("INSERT INTO ms_lims_properties (ms_lims_propertiesid, key, value, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?)");

        while (inset.next()){
            for (int i = 0; i < 6; i++) {
                stmtout.setObject(i + 1,inset.getObject(i + 1));
            }

        stmtout.addBatch();
        }
       stmtout.executeBatch();


    inset = stmtin.executeQuery("select * from phosphorylation");
       stmtout = outconn.prepareStatement("insert into phosphorylation (phosphorylationid,l_status,residue,location,accession,context,score,threshold,creationdate,description) values (?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        while (inset.next()){
            for (int i = 0; i < 8; i++) {
                stmtout.setObject(i + 1,inset.getObject(i + 1));
            }

        stmtout.addBatch();
        }
       stmtout.executeBatch();


       inset = stmtin.executeQuery("select * from projectanalyzertool");
       stmtout = outconn.prepareStatement("insert into projectanalyzertool (projectanalyzertoolid,toolname,description,toolclassname,toolparameters,username,creationdate,modificationdate) values (?,?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        while (inset.next()){
            for (int i = 0; i < 6; i++) {
                stmtout.setObject(i + 1,inset.getObject(i + 1));
            }

        stmtout.addBatch();
        }
       stmtout.executeBatch();



        inset = stmtin.executeQuery("select * from status");
              stmtout = outconn.prepareStatement("insert into status(statusid,name) values (?,?)");

               while (inset.next()){
                   for (int i = 0; i < 2; i++) {
                       stmtout.setObject(i + 1,inset.getObject(i + 1));
                   }

               stmtout.addBatch();
               }
              stmtout.executeBatch();


    inset = stmtin.executeQuery("select * from user");
              stmtout = outconn.prepareStatement("insert into user (userid,name,username,creationdate,modificationdate) values (?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

               while (inset.next()){
                   for (int i = 0; i < 3; i++) {
                       stmtout.setObject(i + 1,inset.getObject(i + 1));
                   }

               stmtout.addBatch();
               }
              stmtout.executeBatch();



        inset = stmtin.executeQuery("select * from validationtype");
              stmtout = outconn.prepareStatement("insert into validationtype (validationtypeid,name) values (?,?)");

               while (inset.next()){
                   for (int i = 0; i < 2; i++) {
                       stmtout.setObject(i + 1,inset.getObject(i + 1));
                   }

               stmtout.addBatch();
               }
              stmtout.executeBatch();
    }





}
