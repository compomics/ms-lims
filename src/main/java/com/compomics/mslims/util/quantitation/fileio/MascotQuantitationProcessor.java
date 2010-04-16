package com.compomics.mslims.util.quantitation.fileio;

import org.apache.log4j.Logger;

import com.compomics.mslims.db.accessors.Datfile;
import com.compomics.mslims.gui.tree.MascotSearch;
import com.compomics.mslims.util.interfaces.QuantitationProcessor;
import com.compomics.util.interfaces.Flamable;
import com.compomics.rover.general.quantitation.RatioGroupCollection;
import com.compomics.rover.general.quantitation.source.distiller.DistillerRatioGroup;
import com.compomics.rover.general.enumeration.QuantitationMetaType;
import com.compomics.rover.general.db.accessors.IdentificationExtension;
import com.compomics.rover.general.fileio.readers.QuantitationXmlReader;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas Colaert
 * Date: 20-nov-2008
 * Time: 8:53:00
 * To change this template use File | Settings | File Templates.
 */

/**
 * The MascotQuantitationProcessor class is a QuantitationProcessor. It can iterate over a series of MascotSearch
 * intances, each having a reference to a Mascot Distiller rov file as well as a Mascot results file.
 * <p/>
 * As a QuantitationProcessor RatioGroupCollection is constructed for each MascotSearch.
 */
public class MascotQuantitationProcessor implements QuantitationProcessor {
    // Class specific log4j logger for MascotQuantitationProcessor instances.
    private static Logger logger = Logger.getLogger(MascotQuantitationProcessor.class);
// ------------------------------ FIELDS ------------------------------

    /**
     * The ms_lims connection to tie up quantitation information to ms_lims identifications.
     */
    private Connection iConnection;
    private Flamable iFlamable;

    /**
     * The array of MascotSearches to do.
     */
    private MascotSearch[] iMascotSearches;

    /**
     * An instance counter for the distinct searches.
     */
    private int iSearchCounter = -1;
    private IdentificationExtension[] iIdentificationsForDatfile;

    /**
     * This boolean is used to avoid the database connection during unit testing. If set to false, the Identifications
     * are not matched anymore! They are then set in a hard coded way - so keep this boolean FALSE except FOR TESTING.
     */
    private boolean iJUnitStatus = false;

// --------------------------- CONSTRUCTORS ---------------------------

    /**
     * Constructs a new RovFile instance from the given MascotSearch.
     *
     * @param aConnection     MascotSearch containing the rov file.
     * @param aFlamable       The flamable frame capturing the errors.
     * @param aMascotSearches The array of MascotSearches to do.
     */
    public MascotQuantitationProcessor(Connection aConnection, Flamable aFlamable, MascotSearch[] aMascotSearches) {
        iConnection = aConnection;
        // Parse the datfile url
        // get server, folder and filename
        iFlamable = aFlamable;
        iMascotSearches = aMascotSearches;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface QuantitationProcessor ---------------------


    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        // Returns true as long as the searchcounter (which increases on each call to the 'next()' mehtod.) is
        // less then the number of searches.
        return iSearchCounter < iMascotSearches.length - 1;
    }

    /**
     * {@inheritDoc}
     */
    public RatioGroupCollection next() {
        iSearchCounter++;
        RatioGroupCollection lRatioGroupCollection = null;
        try {

            if (!iJUnitStatus) {
                // Get the identifications that are within ms_lims from the given MascotSearch.
                // Hence, only do not do this during the tests.
                setIdentificationsForActiveSearch();
            }

            // Get the rov file.
            File aRovFile = getRovFile(iMascotSearches[iSearchCounter]);

            if (aRovFile != null) {
                // Ok, the rov file was found.
                // Next, unzip and get the quantation xml file.
                File lDistillerXmlFile = getDistillerXmlFile(aRovFile, iMascotSearches[iSearchCounter]);
                if (lDistillerXmlFile != null) {
                    lRatioGroupCollection = this.parseDistillerXmlFile(lDistillerXmlFile, iMascotSearches[iSearchCounter]);

                    // Return the instance!
                    return lRatioGroupCollection;
                }
            }
        } catch (SQLException e) {
            logger.error("Failing!");
            logger.error(e.getMessage(), e);
        }
        return null;
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * Returns the Distiller Quantitation xml file within the given Distiller Rov file.
     *
     * @param aRovFile File handle to the (zipped) Distiller project file.
     * @return File handle to the distiller xml file within the rov file. <b> can be null!</b>
     */
    private File getDistillerXmlFile(final File aRovFile, MascotSearch aMascotSearch) {
        try {
            File lTempfolder = File.createTempFile("temp", "temp").getParentFile();
            File lTempRovFolder = new File(lTempfolder, "mslims");

            if (lTempRovFolder.exists() == false) {
                lTempRovFolder.mkdir();
            }

            File lTempTaskFolder = new File(lTempRovFolder, String.valueOf(aMascotSearch.getParentTaskId()));

            if (lTempTaskFolder.exists() == false) {
                lTempTaskFolder.mkdir();
            }
            lTempTaskFolder.deleteOnExit();

            File lTempUnzippedRovFileFolder = new File(lTempTaskFolder, aRovFile.getName());

            lTempUnzippedRovFileFolder.deleteOnExit();

            if (!lTempUnzippedRovFileFolder.exists()) {
                // Folder does not exist yet.
                if (!lTempUnzippedRovFileFolder.mkdir()) {
                    // Making of folder failed, quit!
                    iFlamable.passHotPotato(new Throwable("Unable to create temporary directory ' "
                            + lTempUnzippedRovFileFolder.getName()
                            + "' for distiller rov project '" + aRovFile.getName() + "'!!"));
                    // If temporary dir could not be created, return null to stop the process.
                    return null;
                }

                // Unzip the files in the new temp folder

                BufferedOutputStream out = null;
                ZipInputStream in = new ZipInputStream(
                        new BufferedInputStream(
                                new FileInputStream(aRovFile)));
                ZipEntry entry;
                while ((entry = in.getNextEntry()) != null) {
                    //System.out.println("Extracting: " + entry);
                    int count;
                    byte data[] = new byte[1000];

                    // write the files to the disk
                    out = new BufferedOutputStream(
                            new FileOutputStream(lTempUnzippedRovFileFolder.getPath() + "/" + entry.getName()), 1000);

                    while ((count = in.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.flush();
                    out.close();
                }
                in.close();
            }

            // Ok, all files should have been unzipped  in the lTempUnzippedRovFileFolder by now.
            // Try to find the distiller xml file..

            File[] lUnzippedRovFiles = lTempUnzippedRovFileFolder.listFiles();
            for (int i = 0; i < lUnzippedRovFiles.length; i++) {
                File lUnzippedRovFile = lUnzippedRovFiles[i];
                if (lUnzippedRovFile.getName().toLowerCase().indexOf("rover_data+bb8") != -1) {
                    return lUnzippedRovFile;
                }
                if (lUnzippedRovFile.getName().toLowerCase().indexOf("rover_data+bb9") != -1) {
                    return lUnzippedRovFile;
                }
            }

            // If we get here, it means that the distiller quantitation file was not found in the unzipped directory!
            iFlamable.passHotPotato(new Throwable("Distiller quantitation file was not found in the Distiller project file '" + aRovFile.getName() + "' (task: " + aMascotSearch.getParentTaskId() + ")!!"));
            return null;
        } catch (IOException e) {
            logger.error("Failing!");
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Returns an Array Identification instances that are stored into ms_lims from the given MascotSearch.
     *
     * @return Array of Identification instances.
     * @throws SQLException
     */
    private void setIdentificationsForActiveSearch() throws SQLException {
        MascotSearch lMascotSearch = iMascotSearches[iSearchCounter];

        String lDatfile = lMascotSearch.getDatfile();
        String lDatfileServer = lDatfile.substring(0, lDatfile.lastIndexOf("/cgi"));

        String lDatfileFolder = lDatfile.substring(lDatfile.indexOf("file=") + 5, lDatfile.lastIndexOf("/") + 1);
        int dataSection = lDatfile.indexOf("/data/") + 6;
        String lDatfileName = lDatfile.substring(lDatfile.indexOf("/", dataSection) + 1);

        setIdentifications(Datfile.getIdentificationExtensionsForDatfile(lDatfileName, lDatfileServer, lDatfileFolder, iConnection));
    }


    /**
     * Returns the File handle to the Mascot Distiller rov file. Error handling is taken care of as well.
     *
     * @return File The Filehandle to the (zipped!) rov file
     */
    public File getRovFile(MascotSearch aMascotSearch) {
        String aDistillerProject = aMascotSearch.getDistiller_project();

        //get the folder
        File lFolder = new File(aDistillerProject.substring(0, aDistillerProject.lastIndexOf(System.getProperty("file.separator")) + 1));

        File lFile = null;

        //get the .rov file (there's a possibility that the filename is a little bit different near the end)
        int fileCounter = 0;
        for (int i = 0; i < lFolder.listFiles().length; i++) {
            if (lFolder.listFiles()[i].getName().startsWith(aDistillerProject.substring(aDistillerProject.lastIndexOf(System.getProperty("file.separator")) + 1, aDistillerProject.lastIndexOf("."))) && lFolder.listFiles()[i].getName().endsWith(".rov")) {
                lFile = lFolder.listFiles()[i];
                fileCounter = fileCounter + 1;
            }
        }

        if (fileCounter >= 2) {
            iFlamable.passHotPotato(new Throwable("The .rov file ' " + aDistillerProject + "' could be located more than once!\nMaybe you have 2 rov files with the same name."));
            return null;
        }

        if (lFile == null) {
            iFlamable.passHotPotato(new Throwable("The .rov file ' " + aDistillerProject + "' could not be located!!"));
            return null;
        }

        if (!lFile.exists()) {
            iFlamable.passHotPotato(new Throwable("The .rov file ' " + aDistillerProject + "' could not be located!!"));
            return null;
        }

        return lFile;
    }

    // TODO throw error when there is no data in the file

    /**
     * This method will extracted all the information from the distiller xml file. The hits will be stored in the hits
     * Vector.
     *
     * @param aDistillerXmlFile The xml file.
     */
    private RatioGroupCollection parseDistillerXmlFile(File aDistillerXmlFile, MascotSearch aMascotSearch) throws SQLException {

        String lRovFileName = aDistillerXmlFile.getParentFile().getName();
        QuantitationXmlReader lReader = new QuantitationXmlReader(aDistillerXmlFile, iFlamable, lRovFileName);
        RatioGroupCollection lRatioGroupCollection = lReader.getRatioGroupCollection();
        lRatioGroupCollection.putMetaData(QuantitationMetaType.MASCOTTASKID, String.valueOf(aMascotSearch.getParentTaskId()));
        //match the identifications to the RatioGroups
        for (int i = 0; i < lRatioGroupCollection.size(); i++) {
            DistillerRatioGroup lRatioGroup = (DistillerRatioGroup) lRatioGroupCollection.get(i);
            lRatioGroup.linkIdentificationsAndQueries(iIdentificationsForDatfile);
        }

        return lRatioGroupCollection;
    }

    /**
     * THIS METHOD CAN ONLY BE USED FOR TESTING. TO AVOID THE DATABASE CONNECTION, THE IDENTIFICATIONS ARE SET HARD
     * CODED IN THE TESTS.
     */
    public void setJUnitStatus(boolean aStatus) {
        iJUnitStatus = aStatus;
    }


    /**
     * Do not use except for tests.
     */
    public void setIdentifications(final IdentificationExtension[] aIdentificationsForDatfile) {
        iIdentificationsForDatfile = aIdentificationsForDatfile;
    }
}
