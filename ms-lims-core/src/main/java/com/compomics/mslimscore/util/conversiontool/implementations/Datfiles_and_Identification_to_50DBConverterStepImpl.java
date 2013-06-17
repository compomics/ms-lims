/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 2-jan-2006
 * Time: 14:47:49
 */
package com.compomics.mslimscore.util.conversiontool.implementations;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.util.conversiontool.interfaces.DBConverterStep;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
/*
 * CVS information:
 *
 * $Revision: 1.3 $
 * $Date: 2006/01/04 19:44:02 $
 */

/**
 * This class
 *
 * @author Lennart
 * @version $Id: Datfiles_and_Identification_to_50DBConverterStepImpl.java,v 1.3 2006/01/04 19:44:02 lennart Exp $
 */
public class Datfiles_and_Identification_to_50DBConverterStepImpl implements DBConverterStep {
    // Class specific log4j logger for Datfiles_and_Identification_to_50DBConverterStepImpl instances.
    private static Logger logger = Logger.getLogger(Datfiles_and_Identification_to_50DBConverterStepImpl.class);

    /**
     * Default constructor.
     */
    public Datfiles_and_Identification_to_50DBConverterStepImpl() {
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
        //  - update datfile
        //  - update identification.
        try {
            // Firstly: datfile.
            // We need to do the following:
            // - Find all current datfile filenames,
            // - find the server for these,
            // - extract the folder from these and
            // - convert the filenames into the correct form.
            // Note that we will make use of the ORIGINAL identification table for this.
            Statement stat = aConn.createStatement();
            ResultSet rs = stat.executeQuery("select filename, datfileid from datfile");
            HashMap data = new HashMap();
            while (rs.next()) {
                data.put(rs.getString(1), new Long(rs.getLong(2)));
            }
            rs.close();
            // Speed optimization.
            // We can dramatically improve performance for the following section by creating an index for
            // 'datfile' on identification. Note that, at the end of this section, the index is dropped again.
            logger.info("\t Creating index on identification(datfile)...");
            stat.execute("create index datfilename on identification(datfile)");
            logger.info("\t Done.");
            stat.close();
            // Okay, now find the server info for these, and convert the filename into
            // filename and foldername.
            PreparedStatement psGetDataFromIdentification = aConn.prepareStatement("select distinct server from identification where datfile = ?");
            PreparedStatement psUpdateDatfile = aConn.prepareStatement("update datfile set filename=?, folder=?, server=?, modificationdate=CURRENT_TIMESTAMP where datfileid=?");
            PreparedStatement psUpdateIdentification = aConn.prepareStatement("update identification set l_datfileid=?, modificationdate=CURRENT_TIMESTAMP where datfile=?");
            Iterator iter = data.keySet().iterator();
            int datfilesDoneCount = 0;
            while (iter.hasNext()) {
                String key = (String) iter.next();
                long datfileid = ((Long) data.get(key)).longValue();
                psGetDataFromIdentification.setString(1, key);
                rs = psGetDataFromIdentification.executeQuery();
                int counter = 0;
                String server = null;
                while (rs.next()) {
                    counter++;
                    server = rs.getString(1);
                }
                rs.close();
                if (counter == 0) {
                    logger.error("\t # Unable to find an identification for datfile with filename '" + key + "' and datfileid " + datfileid + "! (" + datfilesDoneCount + " datfiles done already)");
                } else if (counter > 1) {
                    logger.error("\t # Found more than one an server for datfile with filename '" + key + "' and datfileid " + datfileid + "! (" + datfilesDoneCount + " datfiles done already)");
                } else {
                    // Update the datfile.
                    InnerData id = new InnerData(key, server);
                    psUpdateDatfile.setString(1, id.getFilename());
                    psUpdateDatfile.setString(2, id.getFolder());
                    psUpdateDatfile.setString(3, id.getServer());
                    psUpdateDatfile.setLong(4, datfileid);
                    int affected = psUpdateDatfile.executeUpdate();
                    if (affected != 1) {
                        logger.error("\t # Updated " + affected + " rows instead of one for original datfile filename '" + key + "'!");
                    }
                    psUpdateDatfile.clearParameters();
                    // Update the identifications.
                    psUpdateIdentification.setLong(1, datfileid);
                    psUpdateIdentification.setString(2, key);
                    psUpdateIdentification.executeUpdate();
                    psUpdateIdentification.clearParameters();
                }
                psGetDataFromIdentification.clearParameters();
                datfilesDoneCount++;
            }
            psGetDataFromIdentification.close();
            psUpdateDatfile.close();
            psUpdateIdentification.close();
            // Datfiles part is now done.
            stat = aConn.createStatement();
            // Here we'll drop the index that we created above since it is no longer of any use.
            logger.info("\t Dropping index 'datfilename' on identification(datfile)...");
            stat.execute("drop index datfilename on identification");
            logger.info("\t Done.");
            // Continue with the link from identification to spectrum.
            // Here, we'll need to find all spectrum filenames for all identifications.
            // Subsequently, we update the identifications after retrieving the corresponding spectrum id.
            data = null;
            ArrayList filenames = new ArrayList();
            rs = stat.executeQuery("select distinct filename from identification");
            while (rs.next()) {
                filenames.add(rs.getString(1));
            }
            rs.close();
            stat.close();
            // Okay, we've got the spectrum filenames.
            // Now to the identifications and the updates thereof.
            PreparedStatement psFindSpectrumfileid = aConn.prepareStatement("select spectrumid from spectrumwhere filename=?");
            psUpdateIdentification = aConn.prepareStatement("update identification set l_spectrumid=?, modificationdate=CURRENT_TIMESTAMP where filename=?");
            for (Iterator lIterator = filenames.iterator(); lIterator.hasNext();) {
                String filename = (String) lIterator.next();
                psFindSpectrumfileid.setString(1, filename);
                rs = psFindSpectrumfileid.executeQuery();
                boolean specFile = rs.next();
                if (specFile) {
                    long spectrumid = rs.getLong(1);
                    rs.close();
                    psFindSpectrumfileid.clearParameters();
                    psUpdateIdentification.setLong(1, spectrumid);
                    psUpdateIdentification.setString(2, filename);
                    psUpdateIdentification.executeUpdate();
                    psUpdateIdentification.clearParameters();
                } else {
                    rs.close();
                    logger.error("\t # Did not find a spectrumid for spectrum filename '" + filename + "'!");
                }
            }
            psFindSpectrumfileid.close();
            psUpdateIdentification.close();
            // All done.
        } catch (SQLException sqle) {
            logger.error("\n\nError converting datfile(s) or identification(s):");
            logger.error(sqle.getMessage());
            logger.error(sqle.getMessage(), sqle);
            error = true;
        }

        return error;
    }

    private class InnerData {
        /**
         * The server field for the datfile table.
         */
        private String iServer = null;
        /**
         * The folder field for the datfile table.
         */
        private String iFolder = null;
        /**
         * The filename field for the datfile table.
         */
        private String iFilename = null;

        /**
         * This constructor takes the original data (filename and server) for the datfile and parses these into a
         * filename, folder and server.
         *
         * @param aOriginalFilename String with the original datfile filename column value.
         * @param aOriginalServer   String with the original identification server column value.
         */
        public InnerData(String aOriginalFilename, String aOriginalServer) {
            int folderEnd = aOriginalFilename.lastIndexOf("/") + 1;
            iFolder = aOriginalFilename.substring(0, folderEnd).trim();
            iFilename = aOriginalFilename.substring(folderEnd).trim();
            iServer = aOriginalServer.substring(0, aOriginalServer.indexOf("/x-cgi")).trim();
        }

        public String getFilename() {
            return iFilename;
        }

        public String getFolder() {
            return iFolder;
        }

        public String getServer() {
            return iServer;
        }
    }
}
