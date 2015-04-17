package com.compomics.mslims.util.quantitation.fileio;

import org.apache.log4j.Logger;


import com.compomics.mslims.util.interfaces.QuantitationStorageEngine;
import com.compomics.mslims.db.accessors.Quantitation_file;
import com.compomics.mslims.db.accessors.Quantitation;
import com.compomics.mslims.db.accessors.Identification_to_quantitation;
import com.compomics.mslims.db.accessors.Quantitation_group;
import com.compomics.util.interfaces.Flamable;
import com.compomics.rover.general.quantitation.RatioGroupCollection;
import com.compomics.rover.general.quantitation.RatioGroup;
import com.compomics.rover.general.quantitation.source.distiller.DistillerRatio;
import com.compomics.rover.general.quantitation.source.distiller.DistillerRatioGroup;
import com.compomics.rover.general.enumeration.QuantitationMetaType;
import com.compomics.rover.general.db.accessors.IdentificationExtension;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.zip.GZIPInputStream;

/**
 * Created by IntelliJ IDEA. User: Kenny Date: 25-nov-2008 Time: 11:47:19 The 'QuantitationStorageEngine ' class was
 * created for
 */
public class DistillerQuantitationStorageEngine implements QuantitationStorageEngine {
    // Class specific log4j logger for DistillerQuantitationStorageEngine instances.
    private static Logger logger = Logger.getLogger(DistillerQuantitationStorageEngine.class);
// ------------------------------ FIELDS ------------------------------

    private Flamable iFlamable;
    private Connection iConnection;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Construct a QuantitationStorageEngine for Distiller project files.
     *
     * @param aFlamable   The error handler.
     * @param aConnection The database connnection to store the quantitation.
     */
    public DistillerQuantitationStorageEngine(final Flamable aFlamable, final Connection aConnection) {
        iFlamable = aFlamable;
        iConnection = aConnection;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface QuantitationStorageEngine ---------------------

    /**
     * The DistillerQuantitationStorageEngine stores a collection of DistillerRatioGroups into the ms_lims system. The
     * distiller quantitation file will be zipped and persisted and identificationid's are matched from the lims system.
     * <br><br> {@inheritDoc}
     */
    public boolean storeQuantitation(RatioGroupCollection aRatioGroupCollection) throws IOException, SQLException {
        //only if ratioSoureType is distiller store Distiller output xml files

        // 1. Store the quantitation file;

        // Return without storage if the distiller file is already in the database!
        if (Quantitation_file.isStoredInDatabase((String) aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME), iConnection)) {
            //iFlamable.passHotPotato(new Throwable("Distiller output xml file '" + aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME) + "' has allready been processed!!"));
            //this distiller rov file is already stored in the database, ask the user if they want to store it again
            int answer = JOptionPane.showConfirmDialog(new JFrame(), "The distiller rov file ( " + aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME) + " ) was already stored in the database.\n Do you want to store it again?", "Problem storing rov file", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                //ok store it again
            } else {
                //do not store it
                return false;
            }
        }


        //First things first: store the distiller.xml file
        //find the temporary folder
        File lTempfolder = File.createTempFile("temp", "temp").getParentFile();
        File lTempRovFolder = new File(lTempfolder, "mslims");
        File lTempTaskFolder = new File(lTempRovFolder, (String) aRatioGroupCollection.getMetaData(QuantitationMetaType.MASCOTTASKID));
        File lTempUnzippedRovFileFolder = new File(lTempTaskFolder, (String) aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME));
        // Does the ms_lims temporary folder exists anyway?
        if (!lTempRovFolder.exists()) {
            iFlamable.passHotPotato(new Throwable("Tempory folder '" + lTempTaskFolder.getName() + "' for distiller output xml files could not be found!"));
            return false;
        }

        // Does the rov file folder exists?
        if (!lTempUnzippedRovFileFolder.exists()) {
            iFlamable.passHotPotato(new Throwable("Tempory file folder with distiller output xml file could not be found!"));
            return false;
        }

        File lQuantitationFile = null;
        File[] lUnzippedRovFileArray = lTempUnzippedRovFileFolder.listFiles();
        boolean twoDotFour = false;
        for (int i = 0; lUnzippedRovFileArray.length > i; i++) {
            if ((lUnzippedRovFileArray[i].getName()).contains("1f40")) {
                //check if new version if old just continue
                twoDotFour = true;
            }
        }
        if (twoDotFour) {
                        lQuantitationFile = new File(lTempUnzippedRovFileFolder.getAbsoluteFile()+"/rover_data+bb8_edited");
                        addQuantitationToDB(aRatioGroupCollection,lQuantitationFile,lQuantitationFile.getParent()+"/rover_data+bb8");
        } else {
            for (int i = 0; lUnzippedRovFileArray.length > i; i++) {
                File lUnzippedRovFile = lUnzippedRovFileArray[i];
                // Potential buggy!!
                // We assume this file is always named 'rover_data+bb8'.
                if (lUnzippedRovFile.getName().toLowerCase().indexOf("rover_data+bb8") != -1) {
                    //distiller xml file found!
                    lQuantitationFile = lUnzippedRovFile;
            }
                    // or this file is always named 'rover_data+bb8'.
                if (lUnzippedRovFile.getName().toLowerCase().indexOf("rover_data+bb9") != -1) {
                    //distiller xml file found!
                    lQuantitationFile = lUnzippedRovFile;
                }
            }
            addQuantitationToDB(aRatioGroupCollection,lQuantitationFile);
         }
    return true;
    }


    private boolean addQuantitationToDB(RatioGroupCollection aRatioGroupCollection,File lQuantitationXML) throws IOException, SQLException {

        long lDistillerOutputFileId = 0;

        // The buffer to hold the distiller xml file.
        StringBuffer all = new StringBuffer();
        BufferedReader input = null;
        // Local file.
        input = new BufferedReader(new FileReader(lQuantitationXML));


        // Reading the quantitation file and clearing empty lines.
        String line = null;
        while ((line = input.readLine()) != null) {
            all.append(line + "\n");
        }

        // Stream read, closing.
        input.close();
        String lQuantitationFileContent = all.toString();

        HashMap lQuantitation_Accessor_Map = new HashMap(4);
        lQuantitation_Accessor_Map.put(Quantitation_file.FILENAME, aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME));
        lQuantitation_Accessor_Map.put(Quantitation_file.TYPE, "distiller");

        Quantitation_file lQuantitation_file = new Quantitation_file(lQuantitation_Accessor_Map);

        ArrayList subset = null;
        // Since for big files (and correspondingly big Strings),
        // the getBytes() method fails due to limited range
        // (float range; breaks at 16,777,216 bytes),
        // we split the String here if necessary.
        String temp = lQuantitationFileContent;
        subset = new ArrayList();
        while (temp.length() > 10000000) {
            subset.add(temp.substring(0, 10000000));
            temp = temp.substring(10000000);
        }
        // Now to process everything using a ByteArrayOutputStream.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (subset != null) {
            for (Iterator lIterator = subset.iterator(); lIterator.hasNext();) {
                String s = (String) lIterator.next();
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
        lDistillerOutputFileId = Long.valueOf(lGeneratedKeys[0].toString());

        //Store the ratios
        int lNumberOfHits = aRatioGroupCollection.size();
        for (int i = 0; i < lNumberOfHits; i++) {
            RatioGroup lRatioGroup = aRatioGroupCollection.get(i);
            //Check if we need to store these ratios
            if (lRatioGroup.getNumberOfIdentifications() > 0) {
                //Identifications linked to the ratios, store them


                //first store the file ref and file link in the quantitation group table
                HashMap hmQuantitationGroup = new HashMap();
                hmQuantitationGroup.put(Quantitation_group.L_QUANTITATION_FILEID, lDistillerOutputFileId);
                hmQuantitationGroup.put(Quantitation_group.FILE_REF, String.valueOf(((DistillerRatioGroup) lRatioGroup).getReferenceOfParentHit()));
                Quantitation_group quant_group = new Quantitation_group(hmQuantitationGroup);
                quant_group.persist(iConnection);

                long lL_quantitationGroupid = quant_group.getQuantitation_groupid();

                //now store the ratios
                int lNumberOfRatios = lRatioGroup.getNumberOfRatios();
                for (int r = 0; r < lNumberOfRatios; r++) {
                    DistillerRatio lRatio = (DistillerRatio) lRatioGroup.getRatio(r);

                    HashMap hm = new HashMap();
                    hm.put(Quantitation.TYPE, lRatio.getType());
                    hm.put(Quantitation.L_QUANTITATION_GROUPID, lL_quantitationGroupid);
                    BigDecimal lBigDecimal = new BigDecimal(lRatio.getRatio(false));
                    lBigDecimal = lBigDecimal.setScale(5, BigDecimal.ROUND_HALF_DOWN);
                    hm.put(Quantitation.RATIO, lBigDecimal.doubleValue());
                    //only if ratioSoureType is distiller store Distiller specific ratio information
                    // cast
                    hm.put(Quantitation.STANDARD_ERROR, lRatio.getQuality());
                    hm.put(Quantitation.VALID, lRatio.getValid());
                    Quantitation quant = new Quantitation(hm);
                    quant.persist(iConnection);
                }
                // now store the quantitation to identification links

                int lNumberOfIdentifications = lRatioGroup.getNumberOfIdentifications();
                for (int k = 0; k < lNumberOfIdentifications; k++) {
                    IdentificationExtension lIdentification = (IdentificationExtension) lRatioGroup.getIdentification(k);
                    if (lIdentification != null) {
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

    private boolean addQuantitationToDB(RatioGroupCollection aRatioGroupCollection,File lQuantitationXML,String lQuantitationBinaryPath) throws IOException, SQLException {

         long lDistillerOutputFileId = 0;

        // The buffer to hold the distiller xml file.
        StringBuffer all = new StringBuffer();
        BufferedReader input = null;
        // Local file.
        input = new BufferedReader(new FileReader(lQuantitationXML));


        // Reading the quantitation file and clearing empty lines.
        String line = null;
        while ((line = input.readLine()) != null) {
            all.append(line + "\n");
        }

        // Stream read, closing.
        input.close();
        String lQuantitationFileContent = all.toString();

        HashMap lQuantitation_Accessor_Map = new HashMap(4);
        lQuantitation_Accessor_Map.put(Quantitation_file.FILENAME, aRatioGroupCollection.getMetaData(QuantitationMetaType.FILENAME));
        lQuantitation_Accessor_Map.put(Quantitation_file.TYPE, "distiller");

        Quantitation_file lQuantitation_file = new Quantitation_file(lQuantitation_Accessor_Map);

        ArrayList subset = null;
        // Since for big files (and correspondingly big Strings),
        // the getBytes() method fails due to limited range
        // (float range; breaks at 16,777,216 bytes),
        // we split the String here if necessary.
        String temp = lQuantitationFileContent;
        subset = new ArrayList();
        while (temp.length() > 10000000) {
            subset.add(temp.substring(0, 10000000));
            temp = temp.substring(10000000);
        }
        // Now to process everything using a ByteArrayOutputStream.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (subset != null) {
            for (Iterator lIterator = subset.iterator(); lIterator.hasNext();) {
                String s = (String) lIterator.next();
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

            ByteArrayInputStream bais = new ByteArrayInputStream(lQuantitationFileBytes);
            baos = new ByteArrayOutputStream();
            BufferedInputStream bis = new BufferedInputStream(bais);
            int read = -1;
            while ((read = bis.read()) != -1){
                baos.write(read);
            }
            lQuantitationFileBytes = baos.toByteArray();
            bais = new ByteArrayInputStream(lQuantitationFileBytes);
            String lLine = "";

            StringBuilder stringHolderBuffer = new StringBuilder();
            String lVersionNumber = "";
            int readChars;
            while ((readChars = bais.read(lQuantitationFileBytes)) != -1){
                for (int i = 0; i < readChars; ++i){
                    if(lQuantitationFileBytes[i] == '\n'){
                        lLine = stringHolderBuffer.toString();
                        if (lLine.trim().startsWith("<info name=\"DISTILLERVERSION\"")) {
                            lVersionNumber = lLine.substring(lLine.indexOf("val")+5, lLine.indexOf("\"/>"));
                            break;
                        } else if(lLine.trim().startsWith("</header") || lLine.trim().startsWith("<header/>")){
                            lVersionNumber = "not found";
                            break;

                        } else {
                            stringHolderBuffer.delete(0,stringHolderBuffer.length());
                        }
                    } else {
                        stringHolderBuffer.append(new String(new byte[]{lQuantitationFileBytes[i]}));
                    }
                }
            }

        lQuantitation_file.setVersionNumber(lVersionNumber);

        //next up is the binary

        ArrayList<Byte> binaryFileArrayList = new ArrayList<Byte>();
        FileInputStream fis = new FileInputStream(new File(lQuantitationBinaryPath));
        bis = new BufferedInputStream(fis);
        read = -1;
        while ((read =bis.read()) != -1){
            binaryFileArrayList.add((byte)read);
        }
        bis.close();
        fis.close();
        byte[] lQuantitationBinaryBytes = new byte[binaryFileArrayList.size()];
        int counter = 0;
        for (Iterator<Byte> iterator = binaryFileArrayList.iterator(); iterator.hasNext(); ) {

            byte next = iterator.next();
            lQuantitationBinaryBytes[counter] = next;
            counter++;
        }
        lQuantitation_file.setBinaryFile(lQuantitationBinaryBytes);

        lQuantitation_file.persist(iConnection);

        // Get the generated keys of the QuantitationFile
        Object[] lGeneratedKeys = lQuantitation_file.getGeneratedKeys();
        lDistillerOutputFileId = Long.valueOf(lGeneratedKeys[0].toString());

        //Store the ratios

        int lNumberOfHits = aRatioGroupCollection.size();
        for (int i = 0; i < lNumberOfHits; i++) {
            RatioGroup lRatioGroup = aRatioGroupCollection.get(i);
            //Check if we need to store these ratios
            if (lRatioGroup.getNumberOfIdentifications() > 0) {
                //Identifications linked to the ratios, store them


                //first store the file ref and file link in the quantitation group table
                HashMap hmQuantitationGroup = new HashMap();
                hmQuantitationGroup.put(Quantitation_group.L_QUANTITATION_FILEID, lDistillerOutputFileId);
                hmQuantitationGroup.put(Quantitation_group.FILE_REF, String.valueOf(((DistillerRatioGroup) lRatioGroup).getReferenceOfParentHit()));
                Quantitation_group quant_group = new Quantitation_group(hmQuantitationGroup);
                quant_group.persist(iConnection);

                long lL_quantitationGroupid = quant_group.getQuantitation_groupid();

                //now store the ratios
                int lNumberOfRatios = lRatioGroup.getNumberOfRatios();
                for (int r = 0; r < lNumberOfRatios; r++) {
                    DistillerRatio lRatio = (DistillerRatio) lRatioGroup.getRatio(r);

                    HashMap hm = new HashMap();
                    hm.put(Quantitation.TYPE, lRatio.getType());
                    hm.put(Quantitation.L_QUANTITATION_GROUPID, lL_quantitationGroupid);
                    BigDecimal lBigDecimal = new BigDecimal(lRatio.getRatio(false));
                    lBigDecimal = lBigDecimal.setScale(5, BigDecimal.ROUND_HALF_DOWN);
                    hm.put(Quantitation.RATIO, lBigDecimal.doubleValue());
                    //only if ratioSoureType is distiller store Distiller specific ratio information
                    // cast
                    hm.put(Quantitation.STANDARD_ERROR, lRatio.getQuality());
                    hm.put(Quantitation.VALID, lRatio.getValid());
                    Quantitation quant = new Quantitation(hm);
                    quant.persist(iConnection);
                }
                // now store the quantitation to identification links

                int lNumberOfIdentifications = lRatioGroup.getNumberOfIdentifications();
                for (int k = 0; k < lNumberOfIdentifications; k++) {
                    IdentificationExtension lIdentification = (IdentificationExtension) lRatioGroup.getIdentification(k);
                    if (lIdentification != null) {
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
