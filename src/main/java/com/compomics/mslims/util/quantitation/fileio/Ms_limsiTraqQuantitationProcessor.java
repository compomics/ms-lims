package com.compomics.mslims.util.quantitation.fileio;

import org.apache.log4j.Logger;

import com.compomics.mslims.util.interfaces.QuantitationProcessor;
import com.compomics.rover.general.quantitation.RatioGroupCollection;
import com.compomics.rover.general.fileio.readers.Mdf_iTraqReader;
import com.compomics.util.interfaces.Flamable;

import java.sql.*;
import java.util.zip.GZIPInputStream;
import java.io.*;

/**
 * Created by IntelliJ IDEA. User: niklaas Date: 16-mrt-2009 Time: 12:25:22 To change this template use File | Settings
 * | File Templates.
 */
public class Ms_limsiTraqQuantitationProcessor implements QuantitationProcessor {
    // Class specific log4j logger for Ms_limsiTraqQuantitationProcessor instances.
    private static Logger logger = Logger.getLogger(Ms_limsiTraqQuantitationProcessor.class);

    /**
     * The ms_lims connection to tie up quantitation information to ms_lims identifications.
     */
    private Connection iConnection;
    /**
     * The Flamable
     */
    private Flamable iFlamable;
    /**
     * Vector with datfileids
     */
    private Long[] iDatFileIds;
    /**
     * An instance counter for the distinct searches.
     */
    private int iSearchCounter = -1;

    /**
     * Constructor
     *
     * @param aConnection MascotSearch containing the rov file.
     * @param aFlamable   The flamable frame capturing the errors.
     * @param aDatFileIds The array with Datfileids to do.
     */
    public Ms_limsiTraqQuantitationProcessor(Connection aConnection, Flamable aFlamable, Long[] aDatFileIds) {
        iConnection = aConnection;
        // Parse the datfile url
        // get server, folder and filename
        iFlamable = aFlamable;
        iDatFileIds = aDatFileIds;
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        // Returns true as long as the searchcounter (which increases on each call to the 'next()' mehtod.) is
        // less then the number of searches.
        return iSearchCounter < iDatFileIds.length - 1;
    }

    /**
     * {@inheritDoc}
     */
    public RatioGroupCollection next() {
        iSearchCounter++;
        RatioGroupCollection lRatioGroupCollection = null;
        Long lDatfileid = null;
        try {

            //create a temp folder
            File lTempfolder = File.createTempFile("temp", "temp").getParentFile();
            File lTempMs_limsFolder = new File(lTempfolder, "mslims");

            if (lTempMs_limsFolder.exists() == false) {
                lTempMs_limsFolder.mkdir();
            }

            //save the datfile to a temp folder
            //the datfile
            File lDatfile = null;
            //get the datfile from the database
            PreparedStatement prepDat = null;
            prepDat = iConnection.prepareStatement("select * from datfile where datfileid = ?");
            Long id = iDatFileIds[iSearchCounter];
            prepDat.setLong(1, id);
            ResultSet rsDat = prepDat.executeQuery();
            while (rsDat.next()) {
                byte[] zipped = rsDat.getBytes("file");
                ByteArrayInputStream bais = new ByteArrayInputStream(zipped);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
                BufferedOutputStream bos = new BufferedOutputStream(baos);
                int read = -1;
                while ((read = bis.read()) != -1) {
                    bos.write(read);
                }
                bos.flush();
                baos.flush();
                byte[] result = baos.toByteArray();
                bos.close();
                bis.close();
                bais.close();
                baos.close();

                lDatfileid = rsDat.getLong("datfileid");

                lDatfile = new File(lTempMs_limsFolder.getAbsolutePath(), rsDat.getString("filename"));
                PrintWriter out = new PrintWriter(lDatfile);
                out.write(new String(result));
                out.flush();
                out.close();
            }
            prepDat.close();
            rsDat.close();

            //now create a mdf_itraqreader and get the ratiogroupcollection
            Mdf_iTraqReader lReader = new Mdf_iTraqReader(lDatfile, null, iFlamable, iConnection, lDatfileid);
            lRatioGroupCollection = lReader.getRatioGroupCollection();

        } catch (IOException e) {
            iFlamable.passHotPotato(new Throwable("Error saving the datfile with datfileid '" + iDatFileIds[iSearchCounter] + "' to the temp folder!"));
        } catch (SQLException e) {
            iFlamable.passHotPotato(new Throwable("Error retrieving the datfile with datfileid '" + iDatFileIds[iSearchCounter] + "' from the database!"));
        }
        return lRatioGroupCollection;
    }

}
