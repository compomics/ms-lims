package com.compomics.mslims.util.quantitation.fileio;

import com.compomics.mslims.util.interfaces.QuantitationStorageEngine;
import com.compomics.mslims.db.accessors.Quantitation_file;
import com.compomics.mslims.db.accessors.Quantitation;
import com.compomics.mslims.db.accessors.Identification_to_quantitation;
import com.compomics.mslims.db.accessors.Quantitation_group;
import com.compomics.util.interfaces.Flamable;
import com.compomics.rover.general.quantitation.RatioGroupCollection;
import com.compomics.rover.general.quantitation.RatioGroup;
import com.compomics.rover.general.enumeration.QuantitationMetaType;
import com.compomics.rover.general.interfaces.Ratio;
import com.compomics.rover.general.db.accessors.IdentificationExtension;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * User: niklaas
 * Date: 25-mrt-2009
 * Time: 8:06:15
 */
public class Ms_limsiTraqStorageEngine implements QuantitationStorageEngine {
// ------------------------------ FIELDS ------------------------------

    private Flamable iFlamable;
    private Connection iConnection;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Construct a QuantitationStorageEngine for iTraq data from datfiles.
     * @param aFlamable The error handler.
     * @param aConnection The database connnection to store the quantitation.
     */
    public Ms_limsiTraqStorageEngine(final Flamable aFlamable, final Connection aConnection) {
        iFlamable = aFlamable;
        iConnection = aConnection;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface QuantitationStorageEngine ---------------------

    /**
     * The Ms_limsiTraqStorageEngine stores a collection of RatioGroups into the ms_lims system.
     * The dat file will be zipped and persisted and identificationid's are matched from the lims system.
     * <br><br>
     * {@inheritDoc}
     */
    public boolean storeQuantitation(final RatioGroupCollection aRatioGroupCollection) throws IOException, SQLException {

        long lOutputFileId = 0;

        // 1. Store the quantitation file;

        // Return without storage if the distiller file is allready in the database!
        if(Quantitation_file.isStoredInDatabase((String) aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME), iConnection)){
            //iFlamable.passHotPotato(new Throwable("Distiller output xml file '" + aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME) + "' has allready been processed!!"));
            //this distiller rov file is already stored in the database, ask the user if they want to store it again
            int answer = JOptionPane.showConfirmDialog( new JFrame(), "The .dat file with iTraq information ( "+ (String) aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME) +" ) was already stored in the database.\n Do you want to store it again?", "Problem storing .dat file",JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE);
            if (answer == JOptionPane.YES_OPTION){
                //ok store it again
            } else {
                //do not store it
                return false;
            }
        }

        //First things first: store the distiller.xml file
         //find the temporary folder
        File lTempfolder = File.createTempFile("temp", "temp").getParentFile();
        File lTempDatFolder = new File(lTempfolder, "mslims");
        File lTempDatFile = new File(lTempDatFolder, (String) aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME));

        // Does the ms_lims temporary folder exists anyway?
        if(!lTempDatFolder.exists()){
            iFlamable.passHotPotato(new Throwable("Tempory folder '" + lTempDatFolder.getName() + "' for .dat files could not be found!"));
            return false;
        }

        // Does the rov file folder exists?
        if(!lTempDatFile.exists()){
            iFlamable.passHotPotato(new Throwable("Tempory file '" + aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME) + "' could not be found!"));
            return false;
        }

         // The buffer to hold the distiller xml file.
        StringBuffer all = new StringBuffer();
        BufferedReader input = null;
        // Local file.
        input = new BufferedReader(new FileReader(lTempDatFile));


        // Reading the quantitation file and clearing empty lines.
        String line = null;
        while((line = input.readLine()) != null) {
            all.append(line + "\n");
        }

        // Stream read, closing.
        input.close();
        String lQuantitationFileContent = all.toString();

        HashMap lQuantitation_Accessor_Map = new HashMap(4);
        lQuantitation_Accessor_Map.put(Quantitation_file.FILENAME, aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME));
        lQuantitation_Accessor_Map.put(Quantitation_file.TYPE, "iTraq");

        Quantitation_file lQuantitation_file = new Quantitation_file(lQuantitation_Accessor_Map);

        ArrayList subset = null;
        // Since for big files (and correspondingly big Strings),
        // the getBytes() method fails due to limited range
        // (float range; breaks at 16,777,216 bytes),
        // we split the String here if necessary.
        String temp = lQuantitationFileContent;
        subset = new ArrayList();
        while(temp.length() > 10000000) {
            subset.add(temp.substring(0, 10000000));
            temp = temp.substring(10000000);
        }
        // Now to process everything using a ByteArrayOutputStream.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(subset != null) {
            for (Iterator lIterator = subset.iterator(); lIterator.hasNext();) {
                String s = (String)lIterator.next();
                baos.write(s.getBytes());
            }
        }
        subset = null;
        baos.write(temp.getBytes());
        baos.flush();

        byte[] lQuantitationFileBytes = baos.toByteArray();

        baos.close();
        System.gc();

        lQuantitation_file.setUnzippedFile(lQuantitationFileBytes);
        lQuantitation_file.persist(iConnection);

        // Get the generated keys of the QuantitationFile
        Object[] lGeneratedKeys = lQuantitation_file.getGeneratedKeys();
        lOutputFileId = Long.valueOf(lGeneratedKeys[0].toString());


        //Store the ratios

        int lNumberOfHits = aRatioGroupCollection.size();
        for(int i = 0; i<lNumberOfHits; i ++){
            RatioGroup lRatioGroup = (RatioGroup) aRatioGroupCollection.get(i);
            //Check if we need to store these ratios
            if(lRatioGroup.getNumberOfIdentifications() > 0){
                //Identifications linked to the ratios, store them

                //first store the file ref and file link in the quantitation group table
                HashMap hmQuantitationGroup = new HashMap();
                hmQuantitationGroup.put(Quantitation_group.L_QUANTITATION_FILEID, lOutputFileId);
                hmQuantitationGroup.put(Quantitation_group.FILE_REF, String.valueOf(lRatioGroup.getIdentification(0).getDatfile_query()));
                Quantitation_group quant_group = new Quantitation_group(hmQuantitationGroup);
                quant_group.persist(iConnection);

                long lL_quantitationGroupid = quant_group.getQuantitation_groupid();

                //now store the ratios
                int lNumberOfRatios = lRatioGroup.getNumberOfRatios();
                for(int r = 0; r<lNumberOfRatios; r ++){
                    Ratio lRatio = (Ratio) lRatioGroup.getRatio(r);

                    HashMap hm = new HashMap();
                    hm.put(Quantitation.TYPE, lRatio.getType());
                    hm.put(Quantitation.L_QUANTITATION_GROUPID, lL_quantitationGroupid);
                    BigDecimal lBigDecimal = new BigDecimal(lRatio.getRatio(false));
                    lBigDecimal = lBigDecimal.setScale(5, BigDecimal.ROUND_HALF_DOWN);
                    hm.put(Quantitation.RATIO, lBigDecimal.doubleValue());
                    hm.put(Quantitation.VALID, lRatio.getValid());
                    Quantitation quant = new Quantitation(hm);
                    quant.persist(iConnection);
                }
                // now store the quantitation to identification links

                int lNumberOfIdentifications = lRatioGroup.getNumberOfIdentifications();
                for(int k = 0 ; k<lNumberOfIdentifications; k++){
                    IdentificationExtension lIdentification = (IdentificationExtension)lRatioGroup.getIdentification(k);
                    if(lIdentification != null){
                            HashMap hm = new HashMap();
                            hm.put(Identification_to_quantitation.TYPE, lRatioGroup.getPeptideType(k));
                            hm.put(Identification_to_quantitation.L_QUANTITATION_GROUPID, lL_quantitationGroupid);
                            hm.put(Identification_to_quantitation.L_IDENTIFICATIONID, lIdentification.getIdentificationid());
                            Identification_to_quantitation aItQ = new Identification_to_quantitation(hm);
                            aItQ.persist(iConnection);
                        }
                    }
                }
            }
        return true;
    }

}
