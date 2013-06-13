/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 2-jan-2006
 * Time: 14:47:49
 */
package com.compomics.mslims.db.conversiontool.implementations;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.conversiontool.interfaces.DBConverterStep;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2007/10/12 19:33:03 $
 */

/**
 * This class handles the conversion of 'Pyr-%' modified sequences.
 *
 * @author Lennart
 * @version $Id: Modified_sequence_correctionStepImpl.java,v 1.1 2007/10/12 19:33:03 lennart Exp $
 */
public class Modified_sequence_correctionStepImpl implements DBConverterStep {
    // Class specific log4j logger for Modified_sequence_correctionStepImpl instances.
    private static Logger logger = Logger.getLogger(Modified_sequence_correctionStepImpl.class);

    /**
     * Default constructor.
     */
    public Modified_sequence_correctionStepImpl() {
        // Wee need to ensure there is always a default public constructor!
    }

    /**
     * This method will be called whenever this step should be executed.
     *
     * @param aConn Connection on which to perform the step.
     * @return boolean that indicates success ('false') or failure ('true').
     */
    public boolean performConversionStep(Connection aConn) {
        boolean error = false;

        // Alright. Having been called to perform our part of the conversion, we need to
        //  - locate any 'Pyr-%' modified sequences, and replace them with 'NH2-X<Pyr>'
        //    Note that Pyr-Q<Dam> can occur, which needs to become 'NH2-Q<Pyr,Dam>' 
        try {
            // We need to do the following:
            // - Find all current identifications with modified_sequence 'Pyr-%',
            // - find the identificationid,
            // - update the modified_sequence
            // - store the change, setting the modificationdate.
            // Note that we will make use of the ORIGINAL identification table for this.
            Statement stat = aConn.createStatement();
            ResultSet rs = stat.executeQuery("select identificationid, modified_sequence from identification where modified_sequence like 'Pyr-%'");
            HashMap data = new HashMap();
            while (rs.next()) {
                data.put(new Long(rs.getLong(1)), rs.getString(2));
            }
            rs.close();
            stat.close();
            // User-friendly output.
            if (data.size() > 0) {
                logger.info("\t Found " + data.size() + " 'Pyr-%' modified_sequence instances...");
                logger.info("\t Fixing them....");
                // Okay, now find the server info for these, and convert the filename into
                // filename and foldername.
                PreparedStatement psModSeq = aConn.prepareStatement("update identification set modified_sequence=?, modificationdate=CURRENT_TIMESTAMP where identificationid=?");
                Iterator iter = data.keySet().iterator();
                int mod_seqs_done_count = 0;
                while (iter.hasNext()) {
                    Long id = (Long) iter.next();
                    String updatedModSeq = convertModSeq((String) data.get(id));
                    psModSeq.setString(1, updatedModSeq);
                    psModSeq.setLong(2, id.longValue());
                    int changed = psModSeq.executeUpdate();
                    if (changed != 1) {
                        logger.error(" * Unable to find a match when trying to update identificationid " + id + "!");
                    }
                    psModSeq.clearParameters();
                    mod_seqs_done_count++;
                }
                psModSeq.close();
                logger.info("\t Fixed " + mod_seqs_done_count + " modified sequences.");
            } else {
                logger.info("\t All entries checked and found to be OK, no fix needed.");
            }
            // All done.
        } catch (SQLException sqle) {
            logger.error("\n\nError converting modified sequences: ");
            logger.error(sqle.getMessage());
            logger.error(sqle.getMessage(), sqle);
            error = true;
        }
        return error;
    }

    private String convertModSeq(String aOriginal) {
        String temp = aOriginal.substring(aOriginal.indexOf("-") + 1);
        String start = "NH2-" + temp.charAt(0) + "<";
        temp = temp.substring(1);
        // If there is a 'Q<Dam>' at the start, we need to extend the
        // 'start' bit a bit.
        if (temp.charAt(0) == '<') {
            temp = "," + temp.substring(1);
        } else {
            temp = ">" + temp;
        }
        return start + "Pyr" + temp;
    }
}
