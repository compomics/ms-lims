package com.compomics.mslims.gui;

import antlr.collections.impl.*;
import com.compomics.mascotdatfile.util.interfaces.FragmentIon;
import com.compomics.mascotdatfile.util.interfaces.MascotDatfileInf;
import com.compomics.mascotdatfile.util.interfaces.QueryToPeptideMapInf;
import com.compomics.mascotdatfile.util.mascot.*;
import com.compomics.mascotdatfile.util.mascot.fragmentions.FragmentIonImpl;
import com.compomics.mslims.db.accessors.*;
import com.compomics.mslims.db.accessors.Quantitation;
import com.compomics.mslims.db.accessors.Spectrum;
import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.util.fileio.MascotGenericFile;
import com.compomics.mslims.util.mascot.MascotIdentifiedSpectrum;
import com.compomics.mslims.util.mascot.MascotIsoforms;
import com.compomics.rover.general.db.accessors.IdentificationExtension;
import com.compomics.rover.general.enumeration.QuantitationMetaType;
import com.compomics.rover.general.quantitation.RatioGroup;
import com.compomics.rover.general.quantitation.RatioGroupCollection;
import com.compomics.rover.general.quantitation.source.distiller.DistillerRatio;
import com.compomics.rover.general.quantitation.source.distiller.DistillerRatioGroup;
import com.compomics.thermo_msf_parser.Parser;
import com.compomics.thermo_msf_parser.gui.Thermo_msf_parserGUI;
import com.compomics.thermo_msf_parser.msf.*;
import com.compomics.thermo_msf_parser.msf.Event;
import com.compomics.thermo_msf_parser.msf.Peak;
import com.compomics.util.db.interfaces.Persistable;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.interfaces.Flamable;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Niklaas
 * Date: 23/03/11
 * Time: 13:48
 * To change this template use File | Settings | File Templates.
 */
public class MsfStorer extends JFrame {
    // Class specific log4j logger for LCRun instances.
    private static Logger logger = Logger.getLogger(MsfStorer.class);

    private JPanel jpanContent;
    private JComboBox cmbProjects;
    private JButton selectMsfFilesButton;
    private JProgressBar progressBar;
    private JComboBox cmbInstrument;
    private JComboBox cmbFragmentationMethods;
    private JLabel msfLabel;
    private JCheckBox chbHighConfident;
    private JCheckBox chbMediumConfident;
    private JCheckBox chbLowConfidence;
    private JLabel lbl1;
    private JLabel lbl2;
    private JLabel lbl3;
    private JButton previewMsfFilesButton;
    private JCheckBox chbCombine;


    /**
     * HashMap that will hold a datfile as key, and the vector of searched spectra found in that datfile, as
     * values.
     */
    private HashMap iAllSpectraInDatfiles = new HashMap();
    /**
     * This HashMap will cache the datfilenames to datfileids for this MascotResultsProcessor.
     */
    private HashMap iDatfilenameToDatfileid = new HashMap();
    //the connection to the ms_lims database
    private Connection iConn;
    private Project[] iProjects;
    private Instrument[] iInstruments;
    private Fragmentation[] iFragmentations;
    private Vector<Parser> iParsedMsfs = new Vector<Parser>();
    private Vector<String> iMsfFileLocations = new Vector<String>();
    private double iThreshold = 0.05;
    private Vector<ScoreType> iMajorScoreType = new Vector<ScoreType>();
    private Vector<Peptide> iPeptidesToStore = new Vector<Peptide>();
    private Vector<String> iStoredRawFileNames = new Vector<String>();
    private HashMap<String, Long> iSpectrumIdMap = new HashMap<String, Long>();
    private HashMap<String, Integer> iSpectrumScoreMap = new HashMap<String, Integer>();
    private HashMap<String, Boolean> iSpectrumIdentificationStored = new HashMap<String, Boolean>();


    public MsfStorer(Connection lConn) {
        //set the connection
        this.iConn = lConn;
        //get the projects
        try {
            this.iProjects = Project.getAllProjects(iConn);
            this.iInstruments = Instrument.getAllInstruments(iConn);
            this.iFragmentations = Fragmentation.getFragmentations(iConn);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        $$$setupUI$$$();

        //set the frame parameters
        this.setTitle("Thermo MSF data storer");
        this.setContentPane(jpanContent);
        this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(700, 400));
        this.setPreferredSize(new Dimension(700, 400));
        this.setMaximumSize(new Dimension(700, 400));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        msfLabel.setVisible(false);


        selectMsfFilesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startTheStore();
            }
        });
        previewMsfFilesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thermo_msf_parserGUI(false);
            }
        });
    }

    public void startTheStore() {
        //open a new thread to parse the files found by the file chooser
        com.compomics.util.sun.SwingWorker lParser = new com.compomics.util.sun.SwingWorker() {
            boolean lLoaded = false;

            public Boolean construct() {
                try {
                    selectMsfFilesButton.setEnabled(false);
                    cmbInstrument.setEnabled(false);
                    cmbProjects.setEnabled(false);
                    cmbFragmentationMethods.setEnabled(false);
                    chbHighConfident.setEnabled(false);
                    chbMediumConfident.setEnabled(false);
                    chbLowConfidence.setEnabled(false);
                    previewMsfFilesButton.setEnabled(false);
                    chbCombine.setEnabled(false);

                    //open file chooser
                    JFileChooser fc = new JFileChooser();
                    fc.setMultiSelectionEnabled(true);
                    //create the file filter to choose
                    FileFilter lFilter = new MsfFileFilter();
                    fc.setFileFilter(lFilter);
                    int returnVal = fc.showOpenDialog(getFrame());
                    File[] lFiles = null;
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        lFiles = fc.getSelectedFiles();
                        for (int i = 0; i < lFiles.length; i++) {
                            iMsfFileLocations.add(lFiles[i].getAbsolutePath());
                        }
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "Open command cancelled by user.", "Info", JOptionPane.INFORMATION_MESSAGE);
                        return true;
                    }

                    progressBar.setVisible(true);
                    progressBar.setStringPainted(true);
                    progressBar.setMaximum(iMsfFileLocations.size() + 1);
                    //parse the msf files
                    Project lSelectedProject = (Project) cmbProjects.getSelectedItem();
                    Instrument lSelectedInstrument = (Instrument) cmbInstrument.getSelectedItem();
                    Fragmentation lSelectedFragmentationMethod = (Fragmentation) cmbFragmentationMethods.getSelectedItem();

                    msfLabel.setVisible(true);

                    Vector lDataToStore = new Vector();

                    for (int i = 0; i < iMsfFileLocations.size(); i++) {
                        System.gc();
                        if (!chbCombine.isSelected()) {
                            iPeptidesToStore.removeAllElements();
                        }
                        try {
                            msfLabel.setText("Msf file " + (i + 1) + "/" + iMsfFileLocations.size());
                            progressBar.setValue(i + 1);
                            progressBar.setString("Parsing: " + iMsfFileLocations.get(i));
                            //progressBar.updateUI();
                            Parser lParser = new Parser(iMsfFileLocations.get(i), true);
                            if (chbCombine.isSelected()) {
                                iParsedMsfs.add(lParser);
                            }
                            progressBar.setString("Parsed: " + iMsfFileLocations.get(i));


                            //set the major score type
                            for (int s = 0; s < lParser.getScoreTypes().size(); s++) {
                                if (lParser.getScoreTypes().get(s).getIsMainScore() == 1) {
                                    iMajorScoreType.add(lParser.getScoreTypes().get(s));
                                }
                            }

                            Vector<RawFile> lRawFiles = lParser.getRawFiles();
                            Vector<com.compomics.thermo_msf_parser.msf.Spectrum> lSpectra = lParser.getSpectra();
                            HashMap<String, com.compomics.thermo_msf_parser.msf.Spectrum> lSpectraMap = new HashMap<String, com.compomics.thermo_msf_parser.msf.Spectrum>();
                            for (int s = 0; s < lSpectra.size(); s++) {
                                lSpectraMap.put(lSpectra.get(s).getSpectrumTitle(), lSpectra.get(s));
                            }


                            //check if the mgf file exists
                            String lMgfLocation = iMsfFileLocations.get(i).replace(".msf", ".mgf");
                            File lMgf = new File(lMgfLocation);
                            HashMap<String, MascotGenericFile> lSpectraFromMgf = null;
                            boolean lUseMgf = false;
                            if (lMgf.exists()) {
                                lUseMgf = true;
                                lSpectraFromMgf = readMgfFile(lMgf);
                            }

                            for (int j = 0; j < lRawFiles.size(); j++) {
                                RawFile lRaw = lRawFiles.get(j);

                                //check if we still need to store the spectra for this raw file
                                //this only happens in complex scenarios where multiple msf files for the same raw data is stored
                                boolean lAreadyStored = false;
                                for (int r = 0; r < iStoredRawFileNames.size(); r++) {
                                    if (lRaw.getFileName().equalsIgnoreCase(iStoredRawFileNames.get(r))) {
                                        lAreadyStored = true;
                                    }
                                }
                                if (!lAreadyStored) {
                                    iStoredRawFileNames.add(lRaw.getFileName());
                                    Vector<com.compomics.thermo_msf_parser.msf.Spectrum> lLinkedSpectra = new Vector<com.compomics.thermo_msf_parser.msf.Spectrum>();
                                    for (int s = 0; s < lSpectra.size(); s++) {
                                        if (lSpectra.get(s).getFileId() == lRaw.getFileId()) {
                                            lLinkedSpectra.add(lSpectra.get(s));
                                        }
                                    }


                                    LCRun run = new LCRun(lRaw.getFileName().substring(lRaw.getFileName().lastIndexOf("\\") + 1), 0, lLinkedSpectra.size());
                                    run.setL_projectid(lSelectedProject.getProjectid());
                                    run.persist(iConn);
                                    Long l = (Long) run.getGeneratedKeys()[0];
                                    run.setLcrunid(l.longValue());

                                    progressBar.setString("Storing spectra for: " + run.getName());
                                    progressBar.setMaximum(lLinkedSpectra.size());


                                    //store the spectra
                                    for (int k = 0; k < lLinkedSpectra.size(); k++) {
                                        progressBar.setValue(k);
                                        if (lUseMgf) {
                                            MascotGenericFile lMgfSpectrum = lSpectraFromMgf.get(lLinkedSpectra.get(k).getSpectrumTitle());
                                            lSpectraFromMgf.put(lLinkedSpectra.get(k).getSpectrumTitle(), null);
                                            if (lMgfSpectrum == null) {
                                                //did not find this spectrum in the mgf file
                                                JOptionPane.showMessageDialog(getFrame(), "Did not find the spectrum " + lLinkedSpectra.get(k).getSpectrumTitle() + " in the mgf file!", "Spectrum not stored!", JOptionPane.INFORMATION_MESSAGE);
                                            } else {

                                                HashMap data = new HashMap(9);
                                                data.put(Spectrum.L_INSTRUMENTID, lSelectedInstrument.getInstrumentid());
                                                // The links.
                                                data.put(Spectrum.L_LCRUNID, run.getLcrunid());
                                                data.put(Spectrum.L_PROJECTID, lSelectedProject.getProjectid());
                                                data.put(Spectrum.L_FRAGMENTATIONID, lSelectedFragmentationMethod.getFragmentationid());
                                                // The flags.
                                                data.put(Spectrum.IDENTIFIED, new Long(0));
                                                data.put(Spectrum.SEARCHED, new Long(0));
                                                // The filename.
                                                data.put(Spectrum.FILENAME, lLinkedSpectra.get(k).getSpectrumTitle());
                                                // The total intensity.
                                                data.put(Spectrum.TOTAL_SPECTRUM_INTENSITY, lMgfSpectrum.getTotalIntensity());
                                                // The highest intensity.
                                                data.put(Spectrum.HIGHEST_PEAK_IN_SPECTRUM, lMgfSpectrum.getHighestIntensity());
                                                // The charge - as long for the database accessor.
                                                Long lCharge = new Long(lMgfSpectrum.getCharge());
                                                data.put(Spectrum.CHARGE, lCharge);
                                                // The precursorMZ.
                                                data.put(Spectrum.MASS_TO_CHARGE, lMgfSpectrum.getPrecursorMZ());

                                                // Create the database object.
                                                // logger.debug("Creating Spectrum instance for " + lMascotGenericFile.getFilename());
                                                Spectrum lSpectrumDb = new Spectrum(data);
                                                lSpectrumDb.persist(iConn);


                                                // Get the spectrumid from the generated keys.
                                                Long lSpectrumid = (Long) lSpectrumDb.getGeneratedKeys()[0];
                                                // Create the Spectrum_file instance.
                                                Spectrum_file lSpectrum_file = new Spectrum_file();
                                                // Set spectrumid
                                                lSpectrum_file.setL_spectrumid(lSpectrumid);
                                                // Set the byte[].
                                                lSpectrum_file.setUnzippedFile(lMgfSpectrum.toString().getBytes());
                                                // Create the database object.
                                                lSpectrum_file.persist(iConn);


                                                ScanTableAccessor lScanTableAccessor = new ScanTableAccessor();
                                                lScanTableAccessor.setL_spectrumid(lSpectrumid);
                                                lScanTableAccessor.setRtsec((lMgfSpectrum.getRetentionInSeconds()[0]) / 60.0);
                                                lScanTableAccessor.setNumber(lMgfSpectrum.getScanNumbers()[0]);
                                                lScanTableAccessor.persist(iConn);
                                            }
                                        } else {

                                            com.compomics.thermo_msf_parser.msf.Spectrum lSpectrum = lLinkedSpectra.get(k);

                                            // Read the contents for the file into a byte[].
                                            String lSpectrumLine = "BEGIN IONS\nTITLE=" + lSpectrum.getSpectrumTitle() + "\n";
                                            Peak lMono = lSpectrum.getFragmentedMsPeak();
                                            lSpectrumLine = lSpectrumLine + "PEPMASS=" + lMono.getX() + "\t" + lMono.getY() + "\n";
                                            lSpectrumLine = lSpectrumLine + "CHARGE=" + lSpectrum.getCharge() + "+\n";
                                            lSpectrumLine = lSpectrumLine + "RTINSECONDS=" + (lSpectrum.getRetentionTime() / 60.0) + "\n";
                                            if (lSpectrum.getFirstScan() != lSpectrum.getFirstScan()) {
                                                lSpectrumLine = lSpectrumLine + "SCANS=" + lSpectrum.getFirstScan() + "." + lSpectrum.getLastScan() + "\n";
                                            } else {
                                                lSpectrumLine = lSpectrumLine + "SCANS=" + lSpectrum.getFirstScan() + "\n";
                                            }
                                            Vector<Peak> lMSMS = lSpectrum.getMSMSPeaks();
                                            double lSum = 0.0;
                                            double lMax = 0.0;
                                            for (int s = 0; s < lMSMS.size(); s++) {
                                                lSum = lSum + lMSMS.get(s).getY();
                                                if (lMSMS.get(s).getY() > lMax) {
                                                    lMax = lMSMS.get(s).getY();
                                                }
                                                lSpectrumLine = lSpectrumLine + lMSMS.get(s).getX() + "\t" + lMSMS.get(s).getY() + "\n";
                                            }
                                            lSpectrumLine = lSpectrumLine + "END IONS\n\n";

                                            byte[] fileContents = lSpectrumLine.getBytes();

                                            HashMap data = new HashMap(9);
                                            data.put(Spectrum.L_INSTRUMENTID, lSelectedInstrument.getInstrumentid());
                                            // The links.
                                            data.put(Spectrum.L_LCRUNID, run.getLcrunid());
                                            data.put(Spectrum.L_PROJECTID, lSelectedProject.getProjectid());
                                            data.put(Spectrum.L_FRAGMENTATIONID, lSelectedFragmentationMethod.getFragmentationid());
                                            // The flags.
                                            data.put(Spectrum.IDENTIFIED, new Long(0));
                                            data.put(Spectrum.SEARCHED, new Long(0));
                                            // The filename.
                                            data.put(Spectrum.FILENAME, lSpectrum.getSpectrumTitle());
                                            // The total intensity.
                                            data.put(Spectrum.TOTAL_SPECTRUM_INTENSITY, lSum);
                                            // The highest intensity.
                                            data.put(Spectrum.HIGHEST_PEAK_IN_SPECTRUM, lMax);
                                            // The charge - as long for the database accessor.
                                            Long lCharge = new Long(lSpectrum.getCharge());
                                            data.put(Spectrum.CHARGE, lCharge);
                                            // The precursorMZ.
                                            data.put(Spectrum.MASS_TO_CHARGE, lSpectrum.getMz());

                                            // Create the database object.
                                            // logger.debug("Creating Spectrum instance for " + lMascotGenericFile.getFilename());
                                            Spectrum lSpectrumDb = new Spectrum(data);
                                            lSpectrumDb.persist(iConn);


                                            // Get the spectrumid from the generated keys.
                                            Long lSpectrumid = (Long) lSpectrumDb.getGeneratedKeys()[0];
                                            // Create the Spectrum_file instance.
                                            Spectrum_file lSpectrum_file = new Spectrum_file();
                                            // Set spectrumid
                                            lSpectrum_file.setL_spectrumid(lSpectrumid);
                                            // Set the byte[].
                                            lSpectrum_file.setUnzippedFile(fileContents);
                                            // Create the database object.
                                            lSpectrum_file.persist(iConn);


                                            ScanTableAccessor lScanTableAccessor = new ScanTableAccessor();
                                            lScanTableAccessor.setL_spectrumid(lSpectrumid);
                                            lScanTableAccessor.setRtsec(lSpectrum.getRetentionTime() / 60.0);
                                            lScanTableAccessor.setNumber(lSpectrum.getFirstScan());
                                            lScanTableAccessor.persist(iConn);
                                        }
                                    }
                                }
                            }
                            lSpectraFromMgf = new HashMap<String, MascotGenericFile>();
                            System.gc();
                            System.gc();


                            //get dat files
                            WorkflowInfo lInfo = lParser.getWorkFlowInfo();
                            String lServer = null;
                            Vector<String> lDatFiles = new Vector<String>();
                            for (int m = 0; m < lInfo.getWorkflowMessages().size(); m++) {
                                if (lInfo.getWorkflowMessages().get(m).getMessage().startsWith("Use mascot server")) {
                                    String lMessage = lInfo.getWorkflowMessages().get(m).getMessage();
                                    lServer = lMessage.substring(lMessage.indexOf("http"), lMessage.lastIndexOf("/") + 1);
                                }
                                if (lInfo.getWorkflowMessages().get(m).getMessage().startsWith("Mascot result on ")) {
                                    String lMessage = lInfo.getWorkflowMessages().get(m).getMessage();
                                    lDatFiles.add(lMessage.substring(lMessage.indexOf("..") + 3, lMessage.lastIndexOf(".dat") + 4));
                                }
                            }

                            //collect the information off peptide identifications to store later on
                            for (int m = 0; m < lDatFiles.size(); m++) {
                                String lUrl = lServer + "cgi/master_results_2.pl?file=../" + lDatFiles.get(m);
                                Vector lResult = processIDs(lUrl, progressBar, lSpectraMap);
                                for (int r = 0; r < lResult.size(); r++) {
                                    lDataToStore.add(lResult.get(r));
                                }

                                //store the data if it is not combined
                                if (!chbCombine.isSelected()) {
                                    storeData(lDataToStore, progressBar);
                                    if (lParser.getQuantificationMethod() != null) {
                                        progressBar.setIndeterminate(false);
                                        progressBar.setString("Storing quantifications");
                                        storeQuantitation(lParser, progressBar);
                                    }
                                    lDataToStore = new Vector();
                                }
                            }

                        } catch (SQLException e) {
                            logger.debug(e.getMessage(), e);
                        } catch (ClassNotFoundException e) {
                            logger.debug(e.getMessage(), e);
                        }
                        System.gc();
                    }

                    //now store the data
                    if (chbCombine.isSelected()) {
                        storeData(lDataToStore, progressBar);
                        progressBar.setMaximum(iParsedMsfs.size());
                        for (int p = 0; p < iParsedMsfs.size(); p++) {
                            Parser lParser = iParsedMsfs.get(p);
                            if (lParser.getQuantificationMethod() != null) {
                                progressBar.setIndeterminate(false);
                                progressBar.setValue(p);
                                progressBar.setString("Storing quantifications");
                                storeQuantitation(lParser, progressBar);
                            }
                        }
                    }

                    // Now do all the updates for the spectrumfiles.
                    // Add the information about having been searched to the PKLfiles in the DB.
                    if (progressBar != null) {
                        progressBar.setString("Updating 'searched' flag on all spectra in the datfiles...");
                    }
                    Iterator iter = iAllSpectraInDatfiles.values().iterator();
                    while (iter.hasNext()) {
                        Vector<String> names = (Vector<String>) iter.next();
                        String[] filenames = new String[names.size()];

                        names.toArray(filenames);
                        Spectrum.addOneToSearchedFlag(filenames, iConn);
                    }

                    progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);
                    progressBar.setString("");
                    progressBar.setStringPainted(false);
                    lLoaded = true;

                } catch (Exception e1) {
                    lLoaded = false;
                    logger.debug(e1.getMessage(), e1);
                    progressBar.setVisible(false);
                    JOptionPane.showMessageDialog(new JFrame(), "There was a problem storing your data!", "Problem storing", JOptionPane.ERROR_MESSAGE);
                }
                return true;
            }

            public void finished() {
                selectMsfFilesButton.setEnabled(true);
                cmbInstrument.setEnabled(true);
                cmbProjects.setEnabled(true);
                cmbFragmentationMethods.setEnabled(true);
                chbHighConfident.setEnabled(true);
                chbMediumConfident.setEnabled(true);
                chbLowConfidence.setEnabled(true);
                previewMsfFilesButton.setEnabled(true);
                chbCombine.setEnabled(true);

                if (lLoaded) {
                    //give a message to the user that everything is loaded
                    JOptionPane.showMessageDialog(new JFrame(), "All data was stored", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        };
        lParser.start();
    }

    public JFrame getFrame() {
        return this;
    }

    public HashMap<String, MascotGenericFile> readMgfFile(File aFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(aFile));

        String line = null;
        HashMap<String, MascotGenericFile> lSpectra = new HashMap<String, MascotGenericFile>();
        int lineCounter = 0;
        int spectrumCounter = 0;
        boolean inSpectrum = false;
        String lCurrentSpectrumTitle = null;
        Integer lCurrentSpectrumCharge;

        StringBuffer spectrum = new StringBuffer();
        // Cycle the file.
        boolean runnameNotYetFound = true;
        while ((line = br.readLine()) != null) {
            lineCounter++;
            line = line.trim();
            // Skip empty lines and file-level charge statement.
            if (line.equals("") || (lineCounter == 1 && line.startsWith("CHARGE"))) {
                continue;
            }

            // Not an empty line, not an initial charge line, not a comment line and inside a spectrum.
            // It could be 'BEGIN IONS', 'END IONS', 'TITLE=...', 'PEPMASS=...',
            // in-spectrum 'CHARGE=...' or, finally, a genuine peak line.
            // Whatever it is, add it to the spectrum StringBuffer.
            else if (inSpectrum) {


                // Keep track of the 'TITLE' value for further usage in the filename creation.
                // Note that this is the only difference with the parent.
                if (line.startsWith("TITLE")) {
                    lCurrentSpectrumTitle = line;
                } else {
                    // Adding this line to the spectrum StringBuffer.
                    spectrum.append(line + "\n");
                }
                if (line.startsWith("CHARGE")) {
                    lCurrentSpectrumCharge = Integer.valueOf(line.substring(7, 8));
                    lCurrentSpectrumTitle = aFile.getName().substring(0, aFile.getName().lastIndexOf(".")) + "_" + lCurrentSpectrumTitle.substring(lCurrentSpectrumTitle.indexOf("Spectrum") + 8, lCurrentSpectrumTitle.indexOf(" ")).trim() + "_" + lCurrentSpectrumTitle.substring(lCurrentSpectrumTitle.indexOf(":") + 1, lCurrentSpectrumTitle.indexOf(",")).trim() + "_" + lCurrentSpectrumCharge;
                }


                // See if it was an 'END IONS', in which case we stop being in a spectrum.
                if (line.startsWith("END IONS")) {
                    // End detected. Much to do!
                    // Reset boolean.
                    inSpectrum = false;
                    // Parse the contents of the spectrum StringBuffer into a MascotGenericFile.
                    MascotGenericFile mgf = new MascotGenericFile(lCurrentSpectrumTitle, "BEGIN IONS\nTITLE=" + lCurrentSpectrumTitle + "\n" + spectrum.toString());
                    lSpectra.put(lCurrentSpectrumTitle, mgf);
                    // Reset the spectrum StringBuffer.
                    spectrum = new StringBuffer();
                    lCurrentSpectrumTitle = null;

                }
            }
            // If we're not in a spectrum, see if the line is 'BEGIN IONS', which marks the begin of a spectrum!
            else if (line.startsWith("BEGIN IONS")) {
                inSpectrum = true;
            }
        }
        return lSpectra;
    }

    public boolean storeQuantitation(Parser lParsedMsfFile, JProgressBar progressBar) throws IOException, SQLException {
        //only if ratioSoureType is distiller store Distiller output xml files
        long lMsfFileId = 0;

        HashMap<Integer, com.compomics.thermo_msf_parser.msf.Spectrum> lSpectrumMap = lParsedMsfFile.getSpectraMapByUniqueSpectrumId();

        // 1. Store the quantitation file;

        // Return without storage if the msf file is allready in the database!
        if (Quantitation_file.isStoredInDatabase(lParsedMsfFile.getFileName() + "_" + lParsedMsfFile.getQuantificationMethodName(), iConn)) {
            //this msf file is already stored in the database, ask the user if they want to store it again
            int answer = JOptionPane.showConfirmDialog(new JFrame(), "The msf file ( " + lParsedMsfFile.getFileName() + " ) was already stored in the database.\n Do you want to store it again?", "Problem storing rov file", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                //ok store it again
            } else {
                //do not store it
                return false;
            }
        }


        HashMap lQuantitation_Accessor_Map = new HashMap(4);
        lQuantitation_Accessor_Map.put(Quantitation_file.FILENAME, lParsedMsfFile.getFileName() + "_" + lParsedMsfFile.getQuantificationMethodName());
        lQuantitation_Accessor_Map.put(Quantitation_file.TYPE, "msf");

        Quantitation_file lQuantitation_file = new Quantitation_file(lQuantitation_Accessor_Map);

        //create for every quantspectrum a spectrum
        String lQuanFile = "";
        lQuanFile = lQuanFile + "FILE=" + lParsedMsfFile.getFileName() + "_" + lParsedMsfFile.getQuantificationMethodName();
        lQuanFile = lQuanFile + "\nRATIO=";
        for (int r = 0; r < lParsedMsfFile.getRatioTypes().size(); r++) {
            lQuanFile = lQuanFile + lParsedMsfFile.getRatioTypes().get(r).getRatioType() + "\t";
        }
        lQuanFile = lQuanFile + "\nCOMPONENTS=";
        for (int r = 0; r < lParsedMsfFile.getComponents().size(); r++) {
            lQuanFile = lQuanFile + lParsedMsfFile.getComponents().get(r) + "\t";
        }

        //filter only the wanted quan results (only for the peptides stored in ms_lims)
        Vector<QuanResult> lQuanResults = new Vector<QuanResult>();
        Vector<Integer> lFileIds = new Vector<Integer>();
        for (int i = 0; i < iPeptidesToStore.size(); i++) {
            QuanResult lQuanResult = iPeptidesToStore.get(i).getParentSpectrum().getQuanResult();
            if (lQuanResult != null) {
                if (lQuanResult.getSpectrumIds().size() == 0) {
                    //System.out.println(";");
                }
                if (!lQuanResults.contains(lQuanResult)) {
                    lQuanResults.add(lQuanResult);
                    lFileIds.add(iPeptidesToStore.get(i).getParentSpectrum().getFileId());
                }
            }
        }
        progressBar.setMaximum(lQuanResults.size() + 1);
        for (int i = 0; i < lQuanResults.size(); i++) {

            progressBar.setValue(i);
            QuanResult lQuan = lQuanResults.get(i);
            int lFileId = lFileIds.get(i);

            //get the quan events
            Vector<Event> lQuanEvents = new Vector<Event>();
            Vector<Integer> lQuanEventsIds = new Vector<Integer>();
            Vector<Vector<Event>> lQuanEventsByPattern = new Vector<Vector<Event>>();
            for (int l = 0; l < lQuan.getIsotopePatterns().size(); l++) {
                Vector<Event> lIsotopePatternEvents = lQuan.getIsotopePatterns().get(l).getEventsWithQuanResult(lParsedMsfFile.getConnection());
                lQuanEventsByPattern.add(lIsotopePatternEvents);
                for (int j = 0; j < lIsotopePatternEvents.size(); j++) {
                    lQuanEvents.add(lIsotopePatternEvents.get(j));
                    lQuanEventsIds.add(lIsotopePatternEvents.get(j).getEventId());
                }
            }

            //get the quan events
            Vector<Vector<Event>> lQuanEventsByPatternWithoutQuanChannel = new Vector<Vector<Event>>();
            for (int j = 0; j < lQuan.getIsotopePatterns().size(); j++) {
                Vector<Event> lIsotopePatternEvents = lQuan.getIsotopePatterns().get(j).getEventsWithoutQuanResult(lParsedMsfFile.getConnection());
                lQuanEventsByPatternWithoutQuanChannel.add(lIsotopePatternEvents);
                for (int l = 0; l < lIsotopePatternEvents.size(); l++) {
                    lQuanEvents.add(lIsotopePatternEvents.get(l));
                    lQuanEventsIds.add(lIsotopePatternEvents.get(l).getEventId());
                }
            }

            //get the min and max retention and mass
            double lMinMass = Double.MAX_VALUE;
            double lMinRT = Double.MAX_VALUE;
            double lMaxMass = Double.MIN_VALUE;
            double lMaxRT = Double.MIN_VALUE;

            for (int j = 0; j < lQuanEvents.size(); j++) {
                if (lMinMass > lQuanEvents.get(j).getMass()) {
                    lMinMass = lQuanEvents.get(j).getMass();
                }
                if (lMaxMass < lQuanEvents.get(j).getMass()) {
                    lMaxMass = lQuanEvents.get(j).getMass();
                }
                if (lMinRT > lQuanEvents.get(j).getRetentionTime()) {
                    lMinRT = lQuanEvents.get(j).getRetentionTime();
                }
                if (lMaxRT < lQuanEvents.get(j).getRetentionTime()) {
                    lMaxRT = lQuanEvents.get(j).getRetentionTime();
                }
            }
            //calculate the borders
            double lMassDiff = Math.abs(lMaxMass - lMinMass);
            if (lMassDiff == 0) {
                lMassDiff = 15.0;
            }
            lMinMass = lMinMass - (lMassDiff / 3.0);
            lMaxMass = lMaxMass + (lMassDiff / 3.0);
            lMinRT = lMinRT - 0.5;
            lMaxRT = lMaxRT + 0.5;

            Vector<Event> lBackgroundEvents = Event.getEventByRetentionTimeLimitMassLimitAndFileIdExcludingIds(lMinRT, lMaxRT, lMinMass, lMaxMass, lQuanEventsIds, lFileId, lParsedMsfFile.getConnection());

            lQuanFile = lQuanFile + "\nBEGIN IONS\nQuanResultId=" + lQuan.getQuanResultId() + "\n";
            for (int e = 0; e < lBackgroundEvents.size(); e++) {
                lQuanFile = lQuanFile + lBackgroundEvents.get(e).getMass() + "_" + lBackgroundEvents.get(e).getIntensity() + "\n";
            }

            for (int e = 0; e < lQuan.getIsotopePatterns().size(); e++) {
                double[] lQuanPatternMzValues = new double[lQuanEventsByPattern.get(e).size()];
                double[] lQuanPatternIntensityValues = new double[lQuanEventsByPattern.get(e).size()];
                for (int j = 0; j < lQuanEventsByPattern.get(e).size(); j++) {
                    lQuanPatternMzValues[j] = lQuanEventsByPattern.get(e).get(j).getMass();
                    lQuanPatternIntensityValues[j] = lQuanEventsByPattern.get(e).get(j).getIntensity();
                    for (int k = 0; k < lQuan.getIsotopePatterns().get(e).getEventAnnotations().size(); k++) {
                        if (lQuanEventsByPattern.get(e).get(j).getEventId() == lQuan.getIsotopePatterns().get(e).getEventAnnotations().get(k).getEventId()) {
                            if (lQuan.getIsotopePatterns().get(e).getEventAnnotations().get(k).getQuanChannelId() != -1) {
                                lQuanFile = lQuanFile + lQuanEventsByPattern.get(e).get(j).getMass() + "_" + lQuanEventsByPattern.get(e).get(j).getIntensity() + "_GREEN_" + lParsedMsfFile.getQuanChannelNameById(lQuan.getIsotopePatterns().get(e).getEventAnnotations().get(k).getQuanChannelId()) + "\n";
                            } else {
                                lQuanFile = lQuanFile + lQuanEventsByPattern.get(e).get(j).getMass() + "_" + lQuanEventsByPattern.get(e).get(j).getIntensity() + "_GREEN" + "\n";
                            }
                        }
                    }
                }
            }

            for (int e = 0; e < lQuan.getIsotopePatterns().size(); e++) {
                double[] lQuanPatternMzValues = new double[lQuanEventsByPatternWithoutQuanChannel.get(e).size()];
                double[] lQuanPatternIntensityValues = new double[lQuanEventsByPatternWithoutQuanChannel.get(e).size()];
                for (int j = 0; j < lQuanEventsByPatternWithoutQuanChannel.get(e).size(); j++) {
                    lQuanPatternMzValues[j] = lQuanEventsByPatternWithoutQuanChannel.get(e).get(j).getMass();
                    lQuanPatternIntensityValues[j] = lQuanEventsByPatternWithoutQuanChannel.get(e).get(j).getIntensity();
                }
                for (int g = 0; g < lQuanPatternMzValues.length; g++) {
                    lQuanFile = lQuanFile + lQuanPatternMzValues[g] + "_" + lQuanPatternIntensityValues[g] + "_BLUE" + "\n";
                }
            }
            lQuanFile = lQuanFile + "END IONS\n";
        }

        progressBar.setIndeterminate(true);

        ArrayList subset = null;
        // Since for big files (and correspondingly big Strings),
        // the getBytes() method fails due to limited range
        // (float range; breaks at 16,777,216 bytes),
        // we split the String here if necessary.
        String temp = lQuanFile;
        subset = new ArrayList();
        while (temp.length() > 10000000) {
            subset.add(temp.substring(0, 10000000));
            temp = temp.substring(10000000);
        }
        // Now to process everything using a ByteArrayOutputStream.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (subset != null) {
            for (Iterator lIterator = subset.iterator(); lIterator.hasNext(); ) {
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
        lQuantitation_file.persist(iConn);

        // Get the generated keys of the QuantitationFile
        Object[] lGeneratedKeys = lQuantitation_file.getGeneratedKeys();
        lMsfFileId = Long.valueOf(lGeneratedKeys[0].toString());

        Vector<RatioType> lRatioTypes = lParsedMsfFile.getRatioTypes();

        //Store the ratios
        int lNumberOfHits = lQuanResults.size();
        for (int i = 0; i < lNumberOfHits; i++) {
            QuanResult lQuan = lQuanResults.get(i);
            //first store the file ref and file link in the quantitation group table
            HashMap hmQuantitationGroup = new HashMap();
            hmQuantitationGroup.put(Quantitation_group.L_QUANTITATION_FILEID, lMsfFileId);
            hmQuantitationGroup.put(Quantitation_group.FILE_REF, String.valueOf(lQuan.getQuanResultId()));
            Quantitation_group quant_group = new Quantitation_group(hmQuantitationGroup);
            boolean lQuanGroupPersisted = false;

            boolean lRatioAdded = false;
            for (int r = 0; r < lRatioTypes.size(); r++) {
                Double lRatio = lQuan.getRatioByRatioType(lRatioTypes.get(r));

                if (lRatio != null) {
                    HashMap hm = new HashMap();
                    hm.put(Quantitation.TYPE, lRatioTypes.get(r).getRatioType());
                    if (!lQuanGroupPersisted) {
                        quant_group.persist(iConn);
                    }
                    long lL_quantitationGroupid = quant_group.getQuantitation_groupid();
                    hm.put(Quantitation.L_QUANTITATION_GROUPID, lL_quantitationGroupid);
                    BigDecimal lBigDecimal = new BigDecimal(lRatio);
                    lBigDecimal = lBigDecimal.setScale(5, BigDecimal.ROUND_HALF_DOWN);
                    hm.put(Quantitation.RATIO, lBigDecimal.doubleValue());
                    hm.put(Quantitation.VALID, true);
                    Quantitation quant = new Quantitation(hm);
                    quant.persist(iConn);
                    lRatioAdded = true;
                }
            }

            if (lRatioAdded) {
                for (int p = 0; p < iPeptidesToStore.size(); p++) {
                    if (iPeptidesToStore.get(p).getParentSpectrum().getQuanResult() != null && iPeptidesToStore.get(p).getParentSpectrum().getQuanResult().getQuanResultId() == lQuan.getQuanResultId()) {
                        if (iSpectrumIdMap.get(iPeptidesToStore.get(p).getParentSpectrum().getSpectrumTitle()) != null) {

                            HashMap hm = new HashMap();
                            if (iPeptidesToStore.get(p).getChannelId() == 0) {
                                hm.put(Identification_to_quantitation.TYPE, "Not defined");
                            } else {
                                hm.put(Identification_to_quantitation.TYPE, lParsedMsfFile.getQuanChannelNameById(iPeptidesToStore.get(p).getChannelId()));
                            }
                            long lL_quantitationGroupid = quant_group.getQuantitation_groupid();
                            hm.put(Identification_to_quantitation.L_QUANTITATION_GROUPID, lL_quantitationGroupid);
                            Long lIdentificationId = iSpectrumIdMap.get(iPeptidesToStore.get(p).getParentSpectrum().getSpectrumTitle());
                            hm.put(Identification_to_quantitation.L_IDENTIFICATIONID, lIdentificationId);
                            Identification_to_quantitation aItQ = new Identification_to_quantitation(hm);
                            aItQ.persist(iConn);
                        } else {
                            //System.out.println("fsdf");
                        }
                    }
                }
            }
        }
        progressBar.setIndeterminate(false);
        return true;
    }


    /**
     * This method processes all the ID's and returns a Vector filled with instances of the Persistable elements that
     * have been parsed.
     *
     * @param aDatfile String with the URL for the datfile.
     */
    public Vector processIDs(String aDatfile, JProgressBar aProgress, HashMap<String, com.compomics.thermo_msf_parser.msf.Spectrum> lSpectra) {

        // The Vector that will hold all identifications.
        Vector result = new Vector(1000, 250);
        // The Mascot '.dat' file.
        boolean isURL = false;
        // Find out if we have an older version of Mascot on the other side, or a more recent one.
        // The older versions will use 'ms-showtext.exe' in the 'x-cgi' folder, the newer ones require
        // the use of the 'ms-status.exe' application in the same folder.
        boolean useLegacy = true;
        String serverURL = aDatfile.substring(0, aDatfile.lastIndexOf("/cgi"));
        try {
            URL test = new URL(serverURL + "/x-cgi/ms-showtext.exe");
            URLConnection conn = test.openConnection();
            InputStream is = conn.getInputStream();
            is.close();
        } catch (IOException ioe) {
            useLegacy = false;
        }
        // These three variables (filename, folder and datedir) are only used in
        // NON-LEGACY mode (ie. useLegacy == false).
        String filename = null;
        String folder = null;
        String datedir = null;
        if (useLegacy) {
            serverURL = aDatfile.substring(0, aDatfile.lastIndexOf("/cgi")) + "/x-cgi/ms-showtext.exe?";
            if (aDatfile.startsWith("http")) {
                aDatfile = serverURL + aDatfile.substring(aDatfile.indexOf("file=") + 5, aDatfile.length());
                isURL = true;
            }
        } else {
            int dataSection = aDatfile.indexOf("/data/") + 6;
            datedir = aDatfile.substring(dataSection, aDatfile.indexOf("/", dataSection));
            folder = aDatfile.substring(aDatfile.indexOf("file=") + 5, aDatfile.lastIndexOf("/") + 1);
            filename = aDatfile.substring(aDatfile.indexOf("/", dataSection) + 1);
            serverURL = aDatfile.substring(0, aDatfile.lastIndexOf("/cgi")) + "/x-cgi/ms-status.exe?Autorefresh=false&Show=RESULTFILE&DateDir=" + datedir + "&ResJob=" + filename;
            aDatfile = serverURL;
            isURL = true;
        }

        Vector tempAccessions = new Vector();

        // Try to get all accessions from a preferences list.
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("accessionPreferences.properties");
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String tempLine = null;
                while ((tempLine = br.readLine()) != null) {
                    tempLine = tempLine.trim();
                    // Only uniques, please.
                    if (!tempAccessions.contains(tempLine)) {
                        tempAccessions.add(tempLine);
                    }
                }
                br.close();
                is.close();
            }
        } catch (IOException ioe) {
            logger.error("Unable to retrieve list of accession numbers from preferences list: " + ioe.getMessage() + "!");
            logger.error(ioe.getMessage(), ioe);
        }

        // Get all known accession numbers from the db (if any).
        try {
            tempAccessions.add(Identification.getAllUniqueAccessions(iConn));
        } catch (Exception e) {
            // No real harm done.
        }

        // Progress if required.
        int startLoc = aDatfile.lastIndexOf("/") + 1;
        int endLoc = aDatfile.indexOf(".dat") + 4;
        if (aProgress != null) {
            aProgress.setString("Downloading datfile '" + filename + "'...");
        }
        // Okay, first we need to retrieve a stream to the Mascot
        // '.dat' file, then we need to feed that stream to the rawparser.
        try {
            // The buffer to hold the datfile.
            StringBuffer all = new StringBuffer();
            BufferedReader input = null;
            if (isURL) {
                // URL connection.
                URL url = new URL(aDatfile);
                URLConnection conn = url.openConnection();
                input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                // Local file.
                input = new BufferedReader(new FileReader(aDatfile));
            }

            // Reading the .dat file and clearing HTML codes in the proces.
            String line = null;
            boolean started = false;
            if (useLegacy) {
                while ((line = input.readLine()) != null) {
                    if ((line.indexOf("</PRE>") >= 0) || (line.indexOf("</pre>") >= 0)) {
                        started = false;
                        break;
                    } else if (started) {
                        all.append(line + "\n");
                    } else if ((line.indexOf("<PRE>") >= 0) || (line.indexOf("<pre>") >= 0)) {
                        started = true;
                    }
                }
            } else {
                while ((line = input.readLine()) != null) {
                    if (started) {
                        all.append(line + "\n");
                    } else if (line.indexOf("MIME-Version") >= 0) {
                        all.append(line + "\n");
                        started = true;
                    }
                }
            }
            // Stream read, closing.
            input.close();
            String datContent = all.toString();


            System.gc();

            HashMap lDatFile = new HashMap(4);
            if (useLegacy) {
                filename = aDatfile.substring(startLoc, endLoc);
                lDatFile.put(Datfile.FILENAME, filename);
                lDatFile.put(Datfile.SERVER, aDatfile.substring(0, aDatfile.indexOf("/x-cgi")));
                lDatFile.put(Datfile.FOLDER, aDatfile.substring(aDatfile.lastIndexOf("ms-showtext.exe?") + 16, startLoc));
            } else {
                lDatFile.put(Datfile.FILENAME, filename);
                lDatFile.put(Datfile.SERVER, aDatfile.substring(0, aDatfile.indexOf("/x-cgi")));
                lDatFile.put(Datfile.FOLDER, folder);
            }
            Datfile lDf = new Datfile(lDatFile);
            byte[] datfileBytes = null;
            ArrayList subset = null;
            // Since for big files (and correspondingly big Strings),
            // the getBytes() method fails due to limited range
            // (float range; breaks at 16,777,216 bytes),
            // we split the String here if necessary.
            String temp = datContent;
            subset = new ArrayList();
            while (temp.length() > 10000000) {
                subset.add(temp.substring(0, 10000000));
                temp = temp.substring(10000000);
            }
            // Now to process everything using a ByteArrayOutputStream.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (subset != null) {
                for (Iterator lIterator = subset.iterator(); lIterator.hasNext(); ) {
                    String s = (String) lIterator.next();
                    baos.write(s.getBytes());
                }
            }
            subset = null;
            baos.write(temp.getBytes());
            baos.flush();
            datfileBytes = baos.toByteArray();
            baos.flush();
            baos.close();
            System.gc();
            System.gc();
            System.gc();
            lDf.setUnzippedFile(datfileBytes);
            System.gc();
            System.gc();
            lDf.persist(iConn);
            iDatfilenameToDatfileid.put(lDf.getFilename(), lDf.getGeneratedKeys()[0]);
            lDf = null;
            temp = null;
            datfileBytes = new byte[0];


            if (aProgress != null) {
                aProgress.setValue(aProgress.getValue() + 1);
                aProgress.setString("Parsing datfile '" + filename + "'...");
            }

            // Parsing the results.
            //String[] lLines = datContent.split("\n");
            /*for (int k = 0; k < 1000; k++) {
               System.out.println(lLines[k]);
           } */
            //System.out.println(datContent);

            // Create temp file.
            File tempDatFile = File.createTempFile("datFileTempMsfStore", ".dat");

            // Delete temp file when program exits.
            tempDatFile.deleteOnExit();

            // Write to temp file
            BufferedWriter out = new BufferedWriter(new FileWriter(tempDatFile));
            out.write(datContent);
            out.close();
            datContent = null;
            System.gc();

            BufferedReader br = new BufferedReader(new FileReader(tempDatFile));

            MascotDatfileInf mdf = new MascotDatfile_Index(br, filename);
            Vector v = this.extractIDs(mdf, lSpectra);

            // Store the datfile in the results Vector.
            //result.add(lDf);

            // First cycle all to retrieve accession numbers from unique identifications.
            int liSize = v.size();
            for (int i = 0; i < liSize; i++) {
                // Get the identified spectrum.
                if (v.get(i) instanceof MascotIdentifiedSpectrum) {
                    MascotIdentifiedSpectrum lSpectrum = (MascotIdentifiedSpectrum) v.elementAt(i);
                    if (lSpectrum.getIsoformCount() == 1) {
                        String lAccession = lSpectrum.getAccession(null);
                        if (!tempAccessions.contains(lAccession)) {
                            tempAccessions.add(lAccession);
                        }
                    }
                }
            }

            // Store all retrieved accession numbers.
            String[] accessions = new String[tempAccessions.size()];
            tempAccessions.toArray(accessions);
            Arrays.sort(accessions);
            // Invert the array.
            String[] accessionsInv = new String[accessions.length];
            for (int i = 0; i < accessions.length; i++) {
                accessionsInv[i] = accessions[accessions.length - (i + 1)];
            }

            // Cycle all and store them.
            for (int i = 0; i < liSize; i++) {
                if (v.get(i) instanceof MascotIdentifiedSpectrum) {

                    // Get the identified spectrum.
                    MascotIdentifiedSpectrum mis = (MascotIdentifiedSpectrum) v.elementAt(i);

                    // We have to check the file stuff!
                    String specFile = mis.getFile().trim();

                    // Get the accession number.
                    String accession = mis.getAccession(accessionsInv);

                    // Isolate the enzymatic part.
                    String descr = mis.getDescription(accession);
                    if (descr == null) {
                        descr = "No description found.";
                        mis.setDescription(descr, accession);
                    } else if (descr.indexOf(";") >= 0) {
                        descr = descr.replace(';', '*');
                        mis.setDescription(descr, accession);
                    }

                    int start = descr.indexOf("(*") + 2;
                    int end = descr.indexOf("*)");
                    if (start < 0 || end < 0) {
                        descr = "FE";
                    } else {
                        mis.setDescription(descr.substring(end + 2), accession);
                        descr = descr.substring(start, end);
                    }

                    // See if there are any isoforms in the description.
                    String tempDesc = mis.getDescription(accession);
                    String isoforms = mis.getIsoformAccessions(accession);
                    int startIsoforms = -1;
                    if ((startIsoforms = tempDesc.indexOf("^A")) >= 0) {
                        String tempDesc2 = tempDesc.substring(0, startIsoforms);
                        mis.setDescription(tempDesc2, accession);
                        if (isoforms == null) {
                            isoforms = tempDesc.substring(startIsoforms + 2);
                        } else {
                            isoforms += tempDesc.substring(startIsoforms);
                        }
                    }
                    // Remove all 'xx|' or 'xxx|' (for IPI) Strings from the isoforms.
                    int startPipe = -1;
                    while (isoforms != null && (startPipe = isoforms.indexOf("|")) > 0) {
                        if (startPipe >= 3 && isoforms.substring(startPipe - 3, startPipe).equalsIgnoreCase("ipi")) {
                            isoforms = isoforms.substring(0, startPipe - 3) + isoforms.substring(startPipe + 1);
                        } else {
                            isoforms = isoforms.substring(0, startPipe - 2) + isoforms.substring(startPipe + 1);
                        }
                    }
                    // Put all params in a HashMap with the correct keys.
                    HashMap hm = new HashMap();
                    hm.put(IdentificationTableAccessor.ACCESSION, accession);
                    hm.put(IdentificationTableAccessor.CAL_MASS, new BigDecimal(mis.getTheoreticalMass()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    hm.put(IdentificationTableAccessor.END, new Long(mis.getEnd(accession)));
                    hm.put(IdentificationTableAccessor.ENZYMATIC, descr);
                    hm.put(IdentificationTableAccessor.EXP_MASS, new Double(mis.getMeasuredMass()));
                    hm.put(IdentificationTableAccessor.MODIFIED_SEQUENCE, mis.getModifiedSequence());
                    hm.put(IdentificationTableAccessor.ION_COVERAGE, mis.getIon_coverage());
                    hm.put(IdentificationTableAccessor.SCORE, new Long(mis.getScore()));
                    hm.put(IdentificationTableAccessor.HOMOLOGY, new Double(mis.getHomologyTreshold()));
                    hm.put(IdentificationTableAccessor.SEQUENCE, mis.getSequence());
                    hm.put(IdentificationTableAccessor.START, new Long(mis.getStart(accession)));
                    hm.put(IdentificationTableAccessor.VALID, new Integer(1));
                    hm.put(IdentificationTableAccessor.IDENTITYTHRESHOLD, new Long(mis.getIdentityTreshold()));
                    hm.put(IdentificationTableAccessor.CONFIDENCE, new Double(this.iThreshold));
                    hm.put(IdentificationTableAccessor.DESCRIPTION, mis.getDescription(accession));
                    hm.put(IdentificationTableAccessor.DB, mis.getDBName());
                    hm.put(IdentificationTableAccessor.PRECURSOR, new Double(mis.getPrecursorMZ()));
                    hm.put(IdentificationTableAccessor.CHARGE, new Integer(mis.getChargeState()));
                    hm.put(IdentificationTableAccessor.TITLE, mis.getSearchTitle());
                    hm.put(IdentificationTableAccessor.ISOFORMS, isoforms);
                    hm.put(IdentificationTableAccessor.DB_FILENAME, mis.getDBFilename());
                    hm.put(IdentificationTableAccessor.MASCOT_VERSION, mis.getMascotVersion());
                    hm.put(IdentificationTableAccessor.DATFILE_QUERY, mis.getQueryNr());

                    // Temporary storage of future dependent rows.
                    Identification mo = new Identification(hm);
                    mo.setTemporaryDatfilename(filename);
                    mo.setTemporarySpectrumfilename(specFile);
                    mo.setFragmentions(mis.getFragmentIons());
                    mo.setFragmentMassTolerance(mis.getFragmentMassError());

                    // Adding it to the result Vector.
                    result.add(mo);
                }
            }
            Vector<String> lSpectraSearchedForThisDatFile = new Vector<String>();
            for (int i = 0; i < liSize; i++) {
                if (v.get(i) instanceof String) {
                    lSpectraSearchedForThisDatFile.add((String) v.get(i));
                }
            }
            iAllSpectraInDatfiles.put(filename, lSpectraSearchedForThisDatFile);

            if (aProgress != null) {
                aProgress.setValue(aProgress.getValue() + 1);
            }
        } catch (Exception e) {
            result = new Vector();
            e.printStackTrace();
        }
        return result;
    }

    /**
     * This method will store all the specified persistables (expected are Datfile and IdentificationTableAccessors) to
     * the database and updates the corresponding spectrumfiles as well, displaying a progressbar on screen if one is
     * specified.
     *
     * @param aPersistables Vector with the persistables to store.
     * @param aProgress     DefaultProgressBar with the progressbar to use; can be 'null' for no progressbar.
     */
    public void storeData(Vector aPersistables, JProgressBar aProgress) throws SQLException {
        if (aProgress != null) {
            aProgress.setString("Filtering data...");
        }

        // Start off by filtering out only what is Persistable.
        Vector persistable = new Vector(aPersistables.size());
        int liSize = aPersistables.size();
        for (int i = 0; i < liSize; i++) {
            Object o = aPersistables.get(i);
            if (o instanceof Persistable) {
                persistable.add(o);
            } else {
                // This is one we do not need to process, so mark it.
                if (aProgress != null) {
                    aProgress.setValue(aProgress.getValue() + 1);
                }
            }
        }
        // All set!
        // Let the beast go!
        liSize = persistable.size();
        if (aProgress != null) {
            aProgress.setString("Processing identified spectra...");
            aProgress.setMaximum(liSize);
            aProgress.setValue(0);
        }
        // We'll need to store the changed stuff later in case a rollback becomes necessary.
        for (int i = 0; i < liSize; i++) {
            Persistable ps = (Persistable) persistable.get(i);
            // See if we have:
            //  - identification: update spectrumfile + l_spectrumid + l_datfileid.
            if (ps instanceof Identification) {
                Identification id = (Identification) ps;
                if (iSpectrumScoreMap.get(id.getTemporarySpectrumfilename()) == id.getScore()) {
                    //check in no identification with the same score is already stored
                    if (!iSpectrumIdentificationStored.get(id.getTemporarySpectrumfilename())) {

                        //we need to store this identification
                        // We need to update the spectrumfile as well.
                        Spectrum lSpectrum = Spectrum.findFromName(id.getTemporarySpectrumfilename(), iConn);
                        if (lSpectrum != null) {
                            if (lSpectrum.getIdentified() > 0) {
                                lSpectrum.setIdentified(lSpectrum.getIdentified() + 1);
                            } else {
                                lSpectrum.setIdentified(1);
                            }
                            // Update it.
                            lSpectrum.update(iConn);
                            id.setL_spectrumid(lSpectrum.getSpectrumid());
                            // Now to find the datfile ID.
                            Object l_datfileid = iDatfilenameToDatfileid.get(id.getTemporaryDatfilename());
                            if (l_datfileid == null) {
                                throw new SQLException("No datfile link found for datfile with filename '" + id.getTemporaryDatfilename() + "'!");
                            }
                            id.setL_datfileid(((Number) l_datfileid).longValue());
                            id.persist(iConn);
                            iSpectrumIdentificationStored.put(id.getTemporarySpectrumfilename(), true);

                            //  - Identification: in this case, we still need to store the fragment ions.
                            if (id.getGeneratedKeys()[0] != null) {
                                iSpectrumIdMap.put(new String(id.getTemporarySpectrumfilename()), (Long) id.getGeneratedKeys()[0]);
                                double tol = id.getFragmentMassTolerance();
                                Iterator iter = id.getFragmentions().iterator();
                                while (iter.hasNext()) {
                                    FragmentIonImpl fi = (FragmentIonImpl) iter.next();

                                    HashMap hm = new HashMap();
                                    hm.put(Fragmention.FRAGMENTIONNUMBER, new Long(fi.getNumber()));
                                    hm.put(Fragmention.INTENSITY, new Long(new Double(fi.getIntensity()).longValue()));
                                    hm.put(Fragmention.IONNAME, fi.getType());
                                    hm.put(Fragmention.IONTYPE, new Long(fi.getID()));
                                    hm.put(Fragmention.L_IDENTIFICATIONID, id.getGeneratedKeys()[0]);
                                    hm.put(Fragmention.L_IONSCORINGID, new Long(fi.getImportance()));
                                    hm.put(Fragmention.MASSDELTA, new Double(new BigDecimal(fi.getTheoreticalExperimantalMassError()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()));
                                    hm.put(Fragmention.MASSERRORMARGIN, new Double(id.getFragmentMassTolerance()));
                                    hm.put(Fragmention.MZ, new Double(new BigDecimal(fi.getMZ()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue()));

                                    Fragmention fi_db = new Fragmention(hm);
                                    fi_db.persist(iConn);
                                }

                                // Create and persist Validation for the new Identification.
                                HashMap lValidationMap = new HashMap();
                                lValidationMap.put(Validation.L_IDENTIFICATIONID, (Long) id.getGeneratedKeys()[0]);
                                lValidationMap.put(Validation.L_VALIDATIONTYPEID, new Long(Validationtype.NOT_VALIDATED));

                                Validation lValidation = new Validation(lValidationMap);
                                lValidation.persist(iConn);

                            }
                        }
                    }
                }
            }


            // See if we have:
            //  - Datfile: in this case, we need to retrieve the generated key and store it in
            //             a mapping.
            if (ps instanceof Datfile) {
                ps.persist(iConn);
                Datfile datfile = (Datfile) ps;
                iDatfilenameToDatfileid.put(datfile.getFilename(), ps.getGeneratedKeys()[0]);
            }
            if (aProgress != null) {
                aProgress.setValue(aProgress.getValue() + 1);
            }
        }

        if (aProgress != null) {
            aProgress.setValue(aProgress.getValue() + 1);
        }
    }


    private Vector extractIDs(MascotDatfileInf aMDF, HashMap<String, com.compomics.thermo_msf_parser.msf.Spectrum> lSpectra) throws IllegalArgumentException {
        // Vector that will contain the MascotIdentifiedSpectrum instances.
        Vector result = new Vector();

        // Get the generic parameters for the search,
        // Extract the db filename and the Mascot version.
        Header header = aMDF.getHeaderSection();
        String version = header.getVersion();
        String dbfilename = header.getRelease();
        Parameters parameters = aMDF.getParametersSection();
        String lRawName = parameters.getFile();
        lRawName = lRawName.substring(lRawName.indexOf(":") + 2, lRawName.toLowerCase().lastIndexOf(".raw"));
        String searchTitle = parameters.getCom();
        if (searchTitle == null) {
            searchTitle = "!No title specified";
        } else {
            int location = searchTitle.indexOf("|");
            if (location >= 0) {
                searchTitle = searchTitle.substring(0, location).trim();
            }
        }
        String inputfile = parameters.getFile();
        String dbName = parameters.getDatabase();
        ProteinMap proteinMap = aMDF.getProteinMap();
        Masses masses = aMDF.getMasses();

        // Rank of the hit (only highest ranking hits
        // (i.e.: rank = 1)) are considered,
        int rank = 1;


        // Get all the queries...
        // Map to transfer query ID into peptidehits.
        QueryToPeptideMapInf queryToPepMap = aMDF.getQueryToPeptideMap();
        Iterator iter = aMDF.getQueryIterator();
        int lQueryCounter = 0;
        while (iter.hasNext()) {
            // Get the query.
            Query query = (Query) iter.next();
            lQueryCounter++;


            String lTitle = query.getTitle();
            String chargeString = query.getChargeString();
            boolean isNegative = false;
            int chargeLoc = chargeString.indexOf('+');
            if (chargeLoc < 0) {
                chargeLoc = chargeString.indexOf('-');
                isNegative = true;
            }
            chargeString = chargeString.substring(0, chargeLoc);
            int charge = Integer.parseInt(chargeString);
            if (isNegative) {
                charge = -charge;
            }
            lTitle = lRawName + "_" + lTitle.substring(lTitle.indexOf("Spectrum") + 8, lTitle.indexOf(" ")).trim() + "_" + lTitle.substring(lTitle.indexOf(":") + 1, lTitle.indexOf(",")).trim() + "_" + charge;
            //add the title to show that it was searched
            result.add(lTitle);
            //process title
            com.compomics.thermo_msf_parser.msf.Spectrum lSpectrum = lSpectra.get(lTitle);
            if (lSpectrum != null) {

                Vector<Peptide> lPeptides = lSpectrum.getPeptides();
                for (int i = 0; i < lPeptides.size(); i++) {
                    Peptide lPeptide = lPeptides.get(i);
                    int lConfidenceLevel = lPeptide.getConfidenceLevel();
                    boolean lUse = false;
                    if (chbHighConfident.isSelected() && lConfidenceLevel == 3) {
                        lUse = true;
                    }
                    if (chbMediumConfident.isSelected() && lConfidenceLevel == 2) {
                        lUse = true;
                    }
                    if (chbLowConfidence.isSelected() && lConfidenceLevel == 1) {
                        lUse = true;
                    }
                    if (!lPeptide.getParentSpectrum().isHighestScoring(lPeptide, iMajorScoreType)) {
                        lUse = false;
                    }
                    if (lUse) {


                        // Get the first ranking peptide hit, if any.
                        PeptideHit ph = queryToPepMap.getPeptideHitOfOneQuery(query.getQueryNumber(), rank);
                        if (ph != null && ph.getSequence().equalsIgnoreCase(lPeptide.getSequence())) {

                            //check if this is the highest scoring peptide for this spectrum
                            boolean lHighest = true;
                            if (iSpectrumScoreMap.get(lTitle) != null) {
                                // we found already an identification for this spectrum
                                if (iSpectrumScoreMap.get(lTitle) > ph.getIonsScore()) {
                                    //the old one is the highest scoring one
                                    //this one will not be added
                                    lHighest = false;
                                } else {
                                    //the current peptide identification has the highest score
                                    //delete the old peptide from the iPeptidesToStore vector
                                    Peptide lPeptideToDelete = null;
                                    for (int p = 0; p < iPeptidesToStore.size(); p++) {
                                        if (iPeptidesToStore.get(p).getParentSpectrum().getSpectrumTitle().equalsIgnoreCase(lTitle)) {
                                            lPeptideToDelete = iPeptidesToStore.get(p);
                                            p = iPeptidesToStore.size();
                                        }
                                    }
                                    iPeptidesToStore.remove(lPeptideToDelete);
                                }
                            }

                            if (lHighest) {
                                iSpectrumScoreMap.put(lTitle, (int) ph.getIonsScore());
                                iSpectrumIdentificationStored.put(lTitle, false);
                                iPeptidesToStore.add(lPeptide);

                                // We have a peptide hit for this query that scores equal
                                // to or above the threshold. Parse it and create a
                                // MascotIdentifiedSpectrum.
                                MascotIdentifiedSpectrum mis = new MascotIdentifiedSpectrum();
                                // Generic stuff, already parsed in advance.
                                mis.setDBFilename(dbfilename);
                                mis.setMascotVersion(version);
                                mis.setSearchTitle(searchTitle);
                                mis.setOriginal_file(inputfile);
                                mis.setDBName(dbName);
                                mis.setQueryNr(lQueryCounter);

                                // Query title.


                                //if it is a multifile get the scans for the query
                                mis.setFile(lTitle);

                                // Additional query info.
                                if (mis.getFile() == null && aMDF.getNumberOfQueries() == 1) {
                                    // In this case, a single query was performed using a file that did not contain
                                    // 'merge' (regardless of case). This is indicative of a search with a single spectrum.
                                    // Therefore we just keep the name of the spectrum as reported by Mascot.
                                    mis.setFile(inputfile);
                                } else if (mis.getFile() == null && aMDF.getNumberOfQueries() > 1) {
                                    // Mergefile.
                                    // We omit the filename (set it to '*').
                                    mis.setFile("*");
                                }

                                // Query m/z and charge.
                                double mz = new BigDecimal(query.getPrecursorMZ()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

                                mis.setChargeState(charge);
                                mis.setPrecursorMZ(mz);

                                // PeptideHit stuff.
                                // Thresholds and rank.
                                mis.setHomologyTreshold((int) ph.getHomologyThreshold());
                                mis.setIdentityTreshold((int) ph.calculateIdentityThreshold(iThreshold));
                                mis.setRank(rank);

                                mis.setTheoreticalMass(ph.getPeptideMr());
                                mis.setMeasuredMass(ph.getPeptideMr() + ph.getDeltaMass());
                                mis.setSequence(ph.getSequence());
                                String lModifiedSequence = ph.getModifiedSequence();
                                // If a modified sequence contain's a '#' character, this means the modification was not included in the modificationConversion.txt file.
                                // Throw an error since we don't want to have multiple names for identical modifications.
                                if (lModifiedSequence.indexOf('#') != -1) {
                                    throw new IllegalArgumentException("\n\nModificationConversion.txt does not contain enough information to parse the following identification:\n\t" + lModifiedSequence + "\nPlease add the modification into modificationcoverions.txt. ");
                                }
                                mis.setModifiedSequence(lModifiedSequence);
                                mis.setScore((int) ph.getIonsScore());

                                // Protein stuff.
                                MascotIsoforms mifs = new MascotIsoforms();
                                Iterator iter2 = ph.getProteinHits().iterator();
                                while (iter2.hasNext()) {
                                    ProteinHit protein = (ProteinHit) iter2.next();
                                    // Hold the original accession to access
                                    String originalAccession = protein.getAccession();
                                    String trimmedAccession = originalAccession;
                                    int startLoc = trimmedAccession.indexOf('(');
                                    int endLoc = trimmedAccession.indexOf(')');
                                    int tempStart = -1;
                                    int tempEnd = -1;
                                    if ((startLoc >= 0) && (endLoc >= 0)) {
                                        String tempLocalization = trimmedAccession.substring(startLoc + 1, endLoc);
                                        StringTokenizer lst = new StringTokenizer(tempLocalization, "-");
                                        try {
                                            tempStart = Integer.parseInt(lst.nextToken().trim());
                                            tempEnd = Integer.parseInt(lst.nextToken().trim());
                                            trimmedAccession = trimmedAccession.substring(0, startLoc).trim();
                                        } catch (Exception e) {
                                            // Do nothing.
                                            // It's probably just not a location String.
                                        }
                                    }
                                    // If no start and end location found, take those from the
                                    // protein information supplied by Mascot.
                                    if (tempStart < 0) {
                                        tempStart = protein.getStart();
                                        tempEnd = protein.getStop();
                                    }
                                    mifs.addIsoform(trimmedAccession, proteinMap.getProteinDescription(originalAccession), tempStart, tempEnd);
                                }
                                mis.setIsoforms(mifs);
                                // Add the ion coverage String.
                                PeptideHitAnnotation pha = ph.getPeptideHitAnnotation(masses, parameters, query.getPrecursorMZ(), query.getChargeString());
                                String ion_coverage = getIonCoverage(ph, query, pha);
                                mis.setIon_coverage(ion_coverage);
                                // Calling this method will initialize all mass deltas between matched peaks.
                                pha.getMatchedBYions(query.getPeakList());
                                // Calling this method will initialize the ion importance as determined by Mascot.
                                Collection fragmentions = pha.getFusedMatchedIons(query.getPeakList(), ph.getPeaksUsedFromIons1(), query.getMaxIntensity(), 0.10);
                                mis.setFragmentIons(fragmentions);
                                double fragmentError = Double.parseDouble(parameters.getITOL());
                                String fragmentErrorUnit = parameters.getITOLU();
                                if (fragmentErrorUnit.trim().toLowerCase().equals("ppm")) {
                                    fragmentError = query.getPrecursorMZ() * fragmentError * 1e-6;
                                }
                                mis.setFragmentMassError(fragmentError);
                                // Add mis to vector.
                                result.add(mis);
                            }
                        }
                    }
                }
            }

        }
        return result;
    }

    private String getIonCoverage(PeptideHit ph, Query query, PeptideHitAnnotation pha) {
        // Match Mascot ions.
        Vector ions = pha.getMatchedIonsByMascot(query.getPeakList(), ph.getPeaksUsedFromIons1());
        // Peptide sequence + length.
        String sequence = ph.getSequence();
        int length = sequence.length();
        // Create Y and B boolean arrays.
        boolean[] yIons = new boolean[length];
        boolean[] bIons = new boolean[length];
        // Fill out arrays.
        for (int i = 0; i < ions.size(); i++) {
            FragmentIon lFragmentIon = (FragmentIon) ions.elementAt(i);
            switch (lFragmentIon.getID()) {
                case FragmentIon.Y_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    if (yIons.length == lFragmentIon.getNumber() + 1) {
                        yIons[yIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.Y_DOUBLE_ION:
                    yIons[lFragmentIon.getNumber() - 1] = true;
                    if (yIons.length == lFragmentIon.getNumber() + 1) {
                        yIons[yIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.B_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    if (bIons.length == lFragmentIon.getNumber() + 1) {
                        bIons[bIons.length - 1] = true;
                    }
                    break;

                case FragmentIon.B_DOUBLE_ION:
                    bIons[lFragmentIon.getNumber() - 1] = true;
                    if (bIons.length == lFragmentIon.getNumber() + 1) {
                        bIons[bIons.length - 1] = true;
                    }
                    break;

                default:
                    // Skip other fragmentions.
            }
        }
        // Now simply add formatting.
        String[] modifiedAA = ph.getModifiedSequenceComponents();
        StringBuffer formattedSequence = new StringBuffer("<html>");
        // Cycle the amino acids (using b-ions indexing here).
        for (int i = 0; i < bIons.length; i++) {
            boolean italic = false;
            boolean bold = false;
            // First and last one only have 50% coverage anyway
            if (i == 0) {
                if (bIons[i]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    if (yIons[yIons.length - (i + 3)]) {
                        bold = true;
                    }
                }
            } else if (i == (length - 1)) {
                if (bIons[i] && bIons[i - 1]) {
                    if (bIons[i - 2]) {
                        italic = true;
                    }
                }
                if (yIons[yIons.length - (i + 1)]) {
                    bold = true;
                }
            } else {
                // Aha, two ions needed here.
                if (bIons[i] && bIons[i - 1]) {
                    italic = true;
                }
                if (yIons[yIons.length - (i + 1)] && yIons[yIons.length - (i + 2)]) {
                    bold = true;
                }
            }
            // Actually add the next char.
            formattedSequence.append(
                    (italic ? "<u>" : "") +
                            (bold ? "<font color=\"red\">" : "") +
                            modifiedAA[i].replaceAll("<", "&lt;").replaceAll(">", "&gt;") +
                            (italic ? "</u>" : "") +
                            (bold ? "</font>" : "")
            );
        }
        // Finalize HTML'ized label text.
        formattedSequence.append("</html>");

        return formattedSequence.toString();
    }


    /**
     * This method fills out the cmbProject with the data in the iProjects Project[].
     */
    private void fillPulldown() {
        cmbProjects.setModel(new DefaultComboBoxModel(iProjects));
        cmbInstrument.setModel(new DefaultComboBoxModel(iInstruments));
        cmbFragmentationMethods.setModel(new DefaultComboBoxModel(iFragmentations));
    }

    private void createUIComponents() {
        cmbProjects = new JComboBox();
        cmbInstrument = new JComboBox();
        cmbFragmentationMethods = new JComboBox();
        fillPulldown();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        jpanContent = new JPanel();
        jpanContent.setLayout(new GridBagLayout());
        final JLabel label1 = new JLabel();
        label1.setText("Select a project: ");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Select an instrument: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Select a fragmentation method: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(label3, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(cmbProjects, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(cmbInstrument, gbc);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(cmbFragmentationMethods, gbc);
        selectMsfFilesButton = new JButton();
        selectMsfFilesButton.setText("Select and store .msf files");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(selectMsfFilesButton, gbc);
        progressBar = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(progressBar, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Peptide Confidence Level: ");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Combine identifications from different msf files ?");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(label5, gbc);
        chbHighConfident = new JCheckBox();
        chbHighConfident.setMaximumSize(new Dimension(130, 22));
        chbHighConfident.setMinimumSize(new Dimension(130, 22));
        chbHighConfident.setPreferredSize(new Dimension(130, 22));
        chbHighConfident.setSelected(true);
        chbHighConfident.setText("High");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(chbHighConfident, gbc);
        chbMediumConfident = new JCheckBox();
        chbMediumConfident.setMaximumSize(new Dimension(130, 22));
        chbMediumConfident.setMinimumSize(new Dimension(130, 22));
        chbMediumConfident.setPreferredSize(new Dimension(130, 22));
        chbMediumConfident.setSelected(false);
        chbMediumConfident.setText("Medium");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(chbMediumConfident, gbc);
        chbLowConfidence = new JCheckBox();
        chbLowConfidence.setMaximumSize(new Dimension(130, 22));
        chbLowConfidence.setMinimumSize(new Dimension(130, 22));
        chbLowConfidence.setPreferredSize(new Dimension(130, 22));
        chbLowConfidence.setText("Low");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(chbLowConfidence, gbc);
        msfLabel = new JLabel();
        msfLabel.setFont(new Font(msfLabel.getFont().getName(), Font.ITALIC, msfLabel.getFont().getSize()));
        msfLabel.setText("Label");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 4;
        jpanContent.add(msfLabel, gbc);
        previewMsfFilesButton = new JButton();
        previewMsfFilesButton.setText("Preview .msf files");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        jpanContent.add(previewMsfFilesButton, gbc);
        lbl3 = new JLabel();
        lbl3.setFont(new Font(lbl3.getFont().getName(), Font.ITALIC, lbl3.getFont().getSize()));
        lbl3.setForeground(new Color(-65536));
        lbl3.setText("This only works for .msf files with a filename equal to the raw file name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 5;
        jpanContent.add(lbl3, gbc);
        lbl2 = new JLabel();
        lbl2.setFont(new Font(lbl2.getFont().getName(), Font.ITALIC, lbl2.getFont().getSize()));
        lbl2.setForeground(new Color(-65536));
        lbl2.setText("This only works for .msf created for one run");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 5;
        jpanContent.add(lbl2, gbc);
        lbl1 = new JLabel();
        lbl1.setFont(new Font(lbl1.getFont().getName(), Font.ITALIC, lbl1.getFont().getSize()));
        lbl1.setForeground(new Color(-65536));
        lbl1.setText("This only works for .msf files identified by Mascot");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 5;
        jpanContent.add(lbl1, gbc);
        chbCombine = new JCheckBox();
        chbCombine.setText("(This will load everything in memory, so don't store to many files in one time)");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        jpanContent.add(chbCombine, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jpanContent;
    }


    /**
     * A .msf file filter
     */
    class MsfFileFilter extends FileFilter {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".msf");
        }

        public String getDescription() {
            return ".msf files";
        }
    }


    public static void main(String[] args) {


        Driver d = null;
        String aDriver = "com.mysql.jdbc.Driver";
        String aUser = "root";
        String aPassword = "niklaas,13*";
        String aUrl = "jdbc:mysql://localhost/ms_lims3";
        // Instantiate the Driver.
        try {
            d = (Driver) Class.forName(aDriver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Connect (with credentials, if supplied).
        Properties lProps = new Properties();
        if (aUser != null && aPassword != null) {
            lProps.put("user", aUser);
            lProps.put("password", aPassword);
        }
        try {
            Connection Conn = d.connect(aUrl, lProps);
            if (Conn == null) {
                throw new SQLException("Connection was 'null'; perhaps USER and PASSWORD required?!");
            }
            MsfStorer lS = new MsfStorer(Conn);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }


    }


}
