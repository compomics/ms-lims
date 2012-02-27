package com.compomics.mslims.util.quantitation.fileio;

import com.compomics.util.enumeration.CompomicsTools;
import com.compomics.util.io.PropertiesManager;
import org.apache.commons.io.FileUtils;
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

import javax.swing.*;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
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
                //copy original rov file to temporary file
            }
            File copiedRovFile = new File(aRovFile.getParent()+"/"+ aRovFile.getName()+"_copy");
            FileUtils.copyFile(aRovFile,copiedRovFile);


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

            // Ok, all files should have been unzipped  in the lTempUnzippedRovFileFolder by now.
            // Try to find the distiller xml file..

            File[] lUnzippedRovFiles = lTempUnzippedRovFileFolder.listFiles();
            boolean twoDotFour = false;
            for (int i = 0; lUnzippedRovFiles.length > i; i++) {
                if ((lUnzippedRovFiles[i].getName()).contains("1f40")) {
                    //check if new version if old just continue
                    twoDotFour = true;
                }
            }
        if (twoDotFour) {
                    //after unzipping we launch mascot distiller to change binary to xml
            String mascotLocation = mcdChecker();
            int exitval = editRovFile(lTempUnzippedRovFileFolder.getAbsoluteFile(),aRovFile.getAbsoluteFile(),mascotLocation);
                    //if editing succeeds rezip the archive and add file
                    if (exitval == 0) {
                        return new File(lTempUnzippedRovFileFolder.getAbsoluteFile()+"/rover_data+bb8_edited");
                    }
            else { logger.error("there was a problem with mascot distiller processing the rov files");}
        } else {

            // Potential buggy!!
            // We assume this file is always named 'rover_data+bb8'.
            for (int i = 0; lUnzippedRovFiles.length > i; i++) {
                File lUnzippedRovFile = lUnzippedRovFiles[i];
                    if (lUnzippedRovFile.getName().toLowerCase().indexOf("rover_data+bb8") != -1) {
                        //distiller xml file found!
                        return lUnzippedRovFile;
                    }
                        // or this file is always named 'rover_data+bb8'.
                    if (lUnzippedRovFile.getName().toLowerCase().indexOf("rover_data+bb9") != -1) {
                        //distiller xml file found!
                        return lUnzippedRovFile;
                    }
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

        if (!lFolder.exists()) {
            iFlamable.passHotPotato(new Throwable("The folder with the .rov files ' " + aDistillerProject + "' could not be located!!\nMaybe you are not working on the computer where the raw data is located."));
            return null;
        }

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

    private int editRovFile(File lTempUnzippedRovFileFolder, File aRovFile, String distillerlocation) {
        int exitval = -1;
        try{
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("\"" + distillerlocation + "\" \"" + aRovFile.getAbsolutePath() + "\" -batch -saveQuantXml -quantout \""  + lTempUnzippedRovFileFolder.getAbsolutePath() + "\\rover_data+bb8_edited\"");
            streamGobbler errorGobbler = new streamGobbler(proc.getErrorStream(), "ERROR");

            // any output
            streamGobbler outputGobbler = new streamGobbler(proc.getInputStream(), "OUTPUT");

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            // any error
            exitval = proc.waitFor();
        } catch (Throwable t) {
         java.util.logging.Logger.getLogger(MascotQuantitationProcessor.class.getName()).log(Level.SEVERE, null, t);
        }
    return exitval;
    }

    private String mcdChecker(){
        Properties props = PropertiesManager.getInstance().getProperties(CompomicsTools.MSLIMS,"ms-lims.properties");
        boolean checkChosen = false;
        JFileChooser fc = new JFileChooser("C:\\Program Files\\Matrix Science\\Mascot Distiller");
        mcdFileFilter mcdfilter = new mcdFileFilter();
        fc.setFileFilter(mcdfilter);
        if(props.getProperty("distillerlocation") == null) {
                while (!checkChosen) {
                    JOptionPane.showMessageDialog(null, "It seems you are using Mascot Distiller 2.4. \n To work with the new files, please select the location of the Mascot Distiller executable");
                    fc.showOpenDialog(new JFrame());
                        if (fc.getSelectedFile().exists()) {
                            props.put("distillerlocation", fc.getSelectedFile().getAbsolutePath());
                            PropertiesManager.getInstance().updateProperties(CompomicsTools.MSLIMS,"ms-lims.properties",props);
                            checkChosen = true;
                            return fc.getSelectedFile().getAbsolutePath();
                        } else {
                            int ClosePane = JOptionPane.showConfirmDialog(null, "Do you want to stop selecting Mascot Distiller?", "warning", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
                            if (ClosePane == JOptionPane.NO_OPTION) {
                            } else if (ClosePane == JOptionPane.YES_OPTION) {
                                return null;
                            }
                        }
                    }
                } else {
                String value = props.getProperty("distillerlocation");
                if (!(new File(value)).exists()) {
                    JOptionPane.showMessageDialog(null,"the location of Mascot Distiller does not seem to exist anymore, please select the new location of Mascot Distiller");
                    if (fc.getSelectedFile().exists()) {
                        props.put("distillerlocation",fc.getSelectedFile().getAbsolutePath());
                        PropertiesManager.getInstance().updateProperties(CompomicsTools.MSLIMS,"ms-lims.properties",props);
                        return fc.getSelectedFile().getAbsolutePath();
                    }
                }
            }
        return props.getProperty("distillerlocation");
    }



    //inner class designed to gobble up the errors and output of the system call for mascot distiller
    private class streamGobbler extends Thread {

       private InputStream is;
       private String type;
       private OutputStream os;

        streamGobbler(InputStream is, String type) {
            this(is, type, null);
        }

        streamGobbler(InputStream is, String type, OutputStream redirect) {
            this.is = is;
            this.type = type;
            this.os = redirect;
        }
    }
    private class mcdFileFilter extends javax.swing.filechooser.FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".exe");
        }

        @Override
        public String getDescription() {
            return ".exe files";
        }
    }
}
