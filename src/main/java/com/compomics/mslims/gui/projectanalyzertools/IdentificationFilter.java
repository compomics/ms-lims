package com.compomics.mslims.gui.projectanalyzertools;

import com.compomics.mslims.db.accessors.Identification;
import com.compomics.mslims.db.accessors.Project;
import com.compomics.util.gui.dialogs.ConnectionDialog;
import com.compomics.mslims.gui.interfaces.ProjectAnalyzerTool;
import com.compomics.mslims.gui.ProjectAnalyzer;
import com.compomics.rover.general.enumeration.ProteinDatabaseType;
import com.compomics.rover.general.sequenceretriever.UniprotSequenceRetriever;
import com.compomics.rover.general.sequenceretriever.NcbiSequenceRetriever;
import com.compomics.rover.general.sequenceretriever.IpiSequenceRetriever;
import com.compomics.util.interfaces.Connectable;
import com.compomics.util.sun.SwingWorker;

import javax.swing.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.sql.Connection;
import java.sql.SQLException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: niklaas
 * Date: 25-feb-2009
 * Time: 8:24:18
 * To change this template use File | Settings | File Templates.
 */
public class IdentificationFilter extends JFrame implements ProjectAnalyzerTool {

    //gui stuff
    private JPanel jpanContent;
    private JTextArea txtResult;
    private JTextField txtNtermMod;
    private JTextField txtStart;
    private JTextField txtEnds;
    private JTextField txtNotEnds;
    private JRadioButton uniprotRadioButton;
    private JRadioButton IPIRadioButton;
    private JRadioButton NCBIRadioButton;
    private JRadioButton nonOfTheAboveRadioButton;
    private JTextField txtAfter;
    private JTextField txtNotAfter;
    private JTextField txtBefore;
    private JTextField txtNotBefore;
    private JCheckBox chbNStartSmaller;
    private JSpinner spinner1;
    private JCheckBox chbNStartLarger;
    private JCheckBox chbCEndSmaller;
    private JCheckBox chbCEndlarger;
    private JButton startButton;
    private JProgressBar progressBar;
    private JSpinner spinner2;
    private JSpinner spinner3;
    private JSpinner spinner4;
    private JCheckBox chbUseIsoforms;

    /**
     * The connection to ms_lims
     */
    private Connection iConn;
    /**
     * The project to do the analysis on
     */
    private Project iProject;
    /**
     * The database type
     */
    private ProteinDatabaseType iDatabaseType;
    /**
     * The identifications
     */
    private Identification[] iIdentifications = null;
    /**
     * Vector with the different protein accessions
     */
    private Vector<String> iProteinAccessions = null;
    /**
     * Vector with the protein sequences.
     * The sequence found in this Vector on a specific position is linked to the protein accession in the iProteinAccession Vector on the same position.
     */
    private Vector<String> iProteinSequences = null;
    /**
     * The ProjectAnalyzer parent
     */
    private ProjectAnalyzer iParent;
    /**
     * The database name
     */
    private String iDBName;
    /**
     * Parameters for this ProjectAnalyzerTool implementation
     */
    private String iParameters;
    /**
     * Toolname of this ProjectAnalyzerTool implementation
     */
    private String iToolName;


    /**
     * Method that will start this ProjectAnalyzerTool.
     */
    public void start() {

        SwingWorker start = new SwingWorker() {
            public Object construct() {
                try {
                    progressBar.setIndeterminate(true);
                    txtResult.setText("");

                    //0. test all the inputs
                    if (!checkAminoacidInput(txtBefore.getText())) {
                        //error
                        progressBar.setIndeterminate(false);
                        txtResult.append("Problem with the input. The amino acids must be comma separated and the modfication must be after the amino acid. Ex. \"R\" or \"R,R<C13>\" ");
                        return false;
                    }
                    if (!checkAminoacidInput(txtNotBefore.getText())) {
                        //error
                        progressBar.setIndeterminate(false);
                        txtResult.append("Problem with the input. The amino acids must be comma separated and the modfication must be after the amino acid. Ex. \"R\" or \"R,R<C13>\" ");
                        return false;
                    }
                    if (!checkAminoacidInput(txtAfter.getText())) {
                        //error
                        progressBar.setIndeterminate(false);
                        txtResult.append("Problem with the input. The amino acids must be comma separated and the modfication must be after the amino acid. Ex. \"R\" or \"R,R<C13>\" ");
                        return false;
                    }
                    if (!checkAminoacidInput(txtNotAfter.getText())) {
                        //error
                        progressBar.setIndeterminate(false);
                        txtResult.append("Problem with the input. The amino acids must be comma separated and the modfication must be after the amino acid. Ex. \"R\" or \"R,R<C13>\" ");
                        return false;
                    }
                    if (!checkAminoacidInput(txtStart.getText())) {
                        //error
                        progressBar.setIndeterminate(false);
                        txtResult.append("Problem with the input. The amino acids must be comma separated and the modfication must be after the amino acid. Ex. \"R\" or \"R,R<C13>\" ");
                        return false;
                    }
                    if (!checkAminoacidInput(txtNotEnds.getText())) {
                        //error
                        progressBar.setIndeterminate(false);
                        txtResult.append("Problem with the input. The amino acids must be comma separated and the modfication must be after the amino acid. Ex. \"R\" or \"R,R<C13>\" ");
                        return false;
                    }
                    if (!checkAminoacidInput(txtEnds.getText())) {
                        //error
                        progressBar.setIndeterminate(false);
                        txtResult.append("Problem with the input. The amino acids must be comma separated and the modfication must be after the amino acid. Ex. \"R\" or \"R,R<C13>\" ");
                        return false;
                    }

                    //1. get all the identification for the project
                    if (iIdentifications == null) {
                        iIdentifications = Identification.getAllIdentificationsforProject(iConn, iProject.getProjectid(), null);
                    }

                    //2. get all the accessions of the identified proteins (+ isoforms)
                    if (iProteinAccessions == null) {
                        iProteinAccessions = getProteinAccessions(iIdentifications);
                    }

                    //3. download the proteins sequences
                    //3.1 Set the database type
                    if (uniprotRadioButton.isSelected()) {
                        iDatabaseType = ProteinDatabaseType.UNIPROT;
                    }
                    if (IPIRadioButton.isSelected()) {
                        iDatabaseType = ProteinDatabaseType.IPI;
                    }
                    if (NCBIRadioButton.isSelected()) {
                        iDatabaseType = ProteinDatabaseType.NCBI;
                    }
                    if (nonOfTheAboveRadioButton.isSelected()) {
                        iDatabaseType = ProteinDatabaseType.UNKNOWN;
                    }
                    //3.2 get the sequences
                    if (iProteinSequences == null) {
                        iProteinSequences = getProteinSequences(iProteinAccessions, progressBar);
                    }
                    progressBar.setString("");
                    progressBar.setIndeterminate(true);

                    //4. check every identification
                    txtResult.append("identificationid,accession,start,end,sequence length,found start,found end,modified sequence,pre,sequence,post,filtered,reasons\n");
                    int lAcceptedCounter = 0;
                    boolean lIdentificationFiltered = false;

                    for (int i = 0; i < iIdentifications.length; i++) {
                        Identification[] lId = new Identification[1];
                        lId[0] = iIdentifications[i];
                        lIdentificationFiltered = false;

                        //get the N- and C- terminal modification
                        String lNterModification = iIdentifications[i].getModified_sequence().substring(0, iIdentifications[i].getModified_sequence().indexOf("-"));
                        String lCterModification = iIdentifications[i].getModified_sequence().substring(iIdentifications[i].getModified_sequence().lastIndexOf("-") + 1);

                        //get the modified sequence
                        String[] lModSequence = sequenceToArray(iIdentifications[i].getModified_sequence());

                        //get the accessions linked to this identification
                        Vector<String> lPeptideAccessions = getProteinAccessions(lId);

                        //all the proteins linked to this peptide identifications will be checked
                        for (int j = 0; j < lPeptideAccessions.size(); j++) {
                            String lAccession = lPeptideAccessions.get(j);
                            String lSequence = null;
                            //The result string that can be printed
                            String lResult = iIdentifications[i].getIdentificationid() + "," + lAccession + "," + iIdentifications[i].getStart() + "," + iIdentifications[i].getEnd();
                            //get the protein sequence linked to this accession;
                            for (int k = 0; k < iProteinAccessions.size(); k++) {
                                if (lAccession.equalsIgnoreCase(iProteinAccessions.get(k))) {
                                    lSequence = iProteinSequences.get(k);
                                    k = iProteinAccessions.size();
                                }
                            }
                            if (lSequence == null) {
                                System.err.println("Did not find protein sequence for: " + lAccession);
                            } else {
                                //get the start and the end position
                                int lStart = lSequence.indexOf(iIdentifications[i].getSequence()) + 1;
                                int lEnd = lStart + iIdentifications[i].getSequence().length() - 1;

                                //check if we could find this peptide in the seqeuence
                                if (lStart == -1 || lStart == 0) {
                                    System.out.println(iIdentifications[i].getSequence() + " could not be found in " + lPeptideAccessions.get(j));
                                } else {

                                    //get the region before and after the peptide
                                    int lStartRegion = lStart - 1 - 20;
                                    if (lStartRegion < 0) {
                                        lStartRegion = 0;
                                    }
                                    int lEndRegion = lEnd + 20;
                                    if (lEndRegion > lSequence.length()) {
                                        lEndRegion = lSequence.length();
                                    }
                                    String lBefore = lSequence.substring(lStartRegion, lStart - 1);
                                    if (lBefore.length() != 20) {
                                        int xTimes = 20 - lBefore.length();
                                        for (int k = 0; k < xTimes; k++) {
                                            lBefore = "X" + lBefore;
                                        }
                                    }
                                    String lBeforeChar = lBefore.substring(lBefore.length() - 1);
                                    String lAfter = lSequence.substring(lEnd, lEndRegion);
                                    if (lAfter.length() != 20) {
                                        int xTimes = 20 - lAfter.length();
                                        for (int k = 0; k < xTimes; k++) {
                                            lAfter = lAfter + "X";
                                        }
                                    }
                                    String lAfterChar = lAfter.substring(0, 1);

                                    lResult = lResult + "," + lSequence.length() + "," + lStart + "," + lEnd + "," + iIdentifications[i].getModified_sequence() + "," + lBefore + "," + iIdentifications[i].getSequence() + "," + lAfter;

                                    //    --------------------------
                                    //    |now do the filtering    |
                                    //    --------------------------

                                    boolean lFilter = true;
                                    boolean lFirstFilter = true;
                                    String lNotInFilterReason = "";
                                    String lFilterBefore = txtBefore.getText();
                                    String lFilterNotBefore = txtNotBefore.getText();
                                    String lFilterAfter = txtAfter.getText();
                                    String lFilterNotAfter = txtNotAfter.getText();
                                    String lFilterNterMod = txtNtermMod.getText();
                                    String lFilterStart = txtStart.getText();
                                    String lFilterEnd = txtEnds.getText();
                                    String lFilterNotEnd = txtNotEnds.getText();

                                    //filter the AA before the peptide, the AA must be one in the lBefore text
                                    if (lFilterBefore.length() != 0) {
                                        StringTokenizer lTok = new StringTokenizer(lFilterBefore, ",");
                                        int lBeforeOkCounter = 0;
                                        while (lTok.hasMoreElements()) {
                                            //one of the tokens must be the same to the lBeforeChar
                                            String aa = lTok.nextToken();
                                            if (aa.equalsIgnoreCase(lBeforeChar)) {
                                                lBeforeOkCounter = lBeforeOkCounter + 1;
                                            }
                                        }
                                        if (lBeforeOkCounter >= 1) {
                                            //a token is the same as the lBeforeChar
                                            if (lFirstFilter) {
                                                lFilter = true;
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }

                                            }
                                        } else {
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the AA '" + lBeforeChar + "' before the peptide was not in " + lFilterBefore;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the AA'" + lBeforeChar + "' before the peptide was not in " + lFilterBefore;
                                            }
                                        }

                                    }

                                    //filter the AA before the peptide, the AA must not be one in the lNotBefore text
                                    if (lFilterNotBefore.length() != 0) {
                                        StringTokenizer lTok = new StringTokenizer(lFilterNotBefore, ",");
                                        int lBeforeOkCounter = 0;
                                        while (lTok.hasMoreElements()) {
                                            //one of the tokens must be the same to the lBeforeChar
                                            String aa = lTok.nextToken();
                                            if (aa.equalsIgnoreCase(lBeforeChar)) {
                                                lBeforeOkCounter = lBeforeOkCounter + 1;
                                            }
                                        }
                                        if (lBeforeOkCounter > 0) {
                                            //a token is the same as the lBeforeChar
                                            //this is not good
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the AA '" + lBeforeChar + "' before the peptide was in " + lFilterNotBefore;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the AA '" + lBeforeChar + "' before the peptide was in " + lFilterNotBefore;
                                            }
                                        } else {
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }
                                        }

                                    }

                                    //filter the AA after the peptide, the AA must be one in the lAfter text
                                    if (lFilterAfter.length() != 0) {
                                        StringTokenizer lTok = new StringTokenizer(lFilterAfter, ",");
                                        int lAfterOkCounter = 0;
                                        while (lTok.hasMoreElements()) {
                                            //one of the tokens must be the same to the lAfterChar
                                            String aa = lTok.nextToken();
                                            if (aa.equalsIgnoreCase(lAfterChar)) {
                                                lAfterOkCounter = lAfterOkCounter + 1;
                                            }
                                        }
                                        if (lAfterOkCounter >= 1) {
                                            //a token is the same as the lAfterChar
                                            if (lFirstFilter) {
                                                lFilter = true;
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }

                                            }
                                        } else {
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the AA '" + lAfterChar + "' after the peptide was not in " + lFilterAfter;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the AA '" + lAfterChar + "' after the peptide was not in " + lFilterAfter;
                                            }
                                        }

                                    }

                                    //filter the AA after the peptide, the AA must not be one in the lNotAfter text
                                    if (lFilterNotAfter.length() != 0) {
                                        StringTokenizer lTok = new StringTokenizer(lFilterNotAfter, ",");
                                        int lAfterOkCounter = 0;
                                        while (lTok.hasMoreElements()) {
                                            //one of the tokens must be the same to the lAfterChar
                                            String aa = lTok.nextToken();
                                            if (aa.equalsIgnoreCase(lAfterChar)) {
                                                lAfterOkCounter = lAfterOkCounter + 1;
                                            }
                                        }
                                        if (lAfterOkCounter > 0) {
                                            //a token is the same as the lAfterChar
                                            //this is not good
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the AA '" + lAfterChar + "' after the peptide was in " + lFilterNotAfter;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the AA '" + lAfterChar + "' after the peptide was in " + lFilterNotAfter;
                                            }
                                        } else {
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }
                                        }

                                    }

                                    //check if the wanted N-term modification is present
                                    if (lFilterNterMod.length() != 0) {
                                        StringTokenizer lTok = new StringTokenizer(lFilterNterMod, ",");
                                        int lModCounter = 0;
                                        while (lTok.hasMoreElements()) {
                                            //one of the tokens must be the same to the lNterModification
                                            String mod = lTok.nextToken();
                                            if (mod.equalsIgnoreCase(lNterModification)) {
                                                lModCounter = lModCounter + 1;
                                            }
                                        }
                                        if (lModCounter > 0) {
                                            //a token is the same as the lNterModification
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }

                                        } else {
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the N terminal modification " + lNterModification + " was not in " + lFilterNterMod;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the N terminal modification " + lNterModification + " was not in " + lFilterNterMod;
                                            }
                                        }
                                    }

                                    //check if the peptide start with wanted aa
                                    if (lFilterStart.length() != 0) {
                                        StringTokenizer lTok = new StringTokenizer(lFilterStart, ",");
                                        int lStartCounter = 0;
                                        while (lTok.hasMoreElements()) {
                                            //one of the tokens must be the same to the peptide start
                                            String start = lTok.nextToken();
                                            if (start.equalsIgnoreCase(lModSequence[0].substring(lModSequence[0].indexOf("-") + 1))) {
                                                lStartCounter = lStartCounter + 1;
                                            }
                                        }
                                        if (lStartCounter > 0) {
                                            //a token is the same as the start
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }

                                        } else {
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the start AA " + lModSequence[0] + " was not in " + lFilterStart;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the start AA " + lModSequence[0] + " was not in " + lFilterStart;
                                            }
                                        }
                                    }

                                    //check if the peptide ends with the wanted aa
                                    if (lFilterEnd.length() != 0) {
                                        StringTokenizer lTok = new StringTokenizer(lFilterEnd, ",");
                                        int lEndCounter = 0;
                                        while (lTok.hasMoreElements()) {
                                            //one of the tokens must be the same to the peptide end
                                            String end = lTok.nextToken();
                                            if (end.equalsIgnoreCase(lModSequence[lModSequence.length - 1].substring(0, lModSequence[lModSequence.length - 1].indexOf("-")))) {
                                                lEndCounter = lEndCounter + 1;
                                            }
                                        }
                                        if (lEndCounter > 0) {
                                            //a token is the same as the end
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }

                                        } else {
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the end AA " + lModSequence[lModSequence.length - 1] + " was not in " + lFilterEnd;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the end AA " + lModSequence[lModSequence.length - 1] + " was not in " + lFilterEnd;
                                            }
                                        }
                                    }

                                    //check if the peptide does not end with the not wanted aa
                                    if (lFilterNotEnd.length() != 0) {
                                        StringTokenizer lTok = new StringTokenizer(lFilterNotEnd, ",");
                                        int lEndCounter = 0;
                                        while (lTok.hasMoreElements()) {
                                            //one of the tokens must be the same to the peptide end
                                            String end = lTok.nextToken();
                                            if (end.equalsIgnoreCase(lModSequence[lModSequence.length - 1].substring(0, lModSequence[lModSequence.length - 1].indexOf("-")))) {
                                                lEndCounter = lEndCounter + 1;
                                            }
                                        }
                                        if (lEndCounter < 0) {
                                            //a token is not the same as the lFilterNotEnd
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }

                                        } else {
                                            //a token is the same as the lFilterNotEnd
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the end AA " + lModSequence[lModSequence.length - 1] + " was in " + lFilterNotEnd;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the end AA " + lModSequence[lModSequence.length - 1] + " was in " + lFilterNotEnd;
                                            }
                                        }
                                    }

                                    //check the start position
                                    if (chbNStartSmaller.isSelected()) {
                                        int lStartSpin = (Integer) spinner1.getValue();
                                        if (lStart < lStartSpin) {
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }

                                        } else {
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the start position " + lStart + " was larger than " + lStartSpin;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the start position " + lStart + " was larger than " + lStartSpin;
                                            }
                                        }
                                    }

                                    //check the start position
                                    if (chbNStartLarger.isSelected()) {
                                        int lStartSpin = (Integer) spinner2.getValue();
                                        if (lStart > lStartSpin) {
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }

                                        } else {
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the start position " + lStart + " was smaller than " + lStartSpin;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the start position " + lStart + " was smaller than " + lStartSpin;
                                            }
                                        }
                                    }

                                    //check the end position
                                    if (chbCEndSmaller.isSelected()) {
                                        int lEndSpin = (Integer) spinner3.getValue();
                                        if (lEnd < lSequence.length() - lEndSpin) {
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }

                                        } else {
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the end position " + lEnd + " was larger than " + lEndSpin;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the end position " + lEnd + " was larger than " + lEndSpin;
                                            }
                                        }
                                    }

                                    //check the end position
                                    if (chbCEndlarger.isSelected()) {
                                        int lEndSpin = (Integer) spinner4.getValue();
                                        if (lEnd > lSequence.length() - lEndSpin) {
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            } else {
                                                if (lFilter) {
                                                    // leave it true
                                                } else {
                                                    //leave it false
                                                }
                                            }

                                        } else {
                                            lFilter = false;
                                            if (lFirstFilter) {
                                                lFirstFilter = false;
                                            }
                                            if (lNotInFilterReason.length() == 0) {
                                                lNotInFilterReason = "Not in filter: the end position " + lEnd + " was smaller than " + lEndSpin;
                                            } else {
                                                lNotInFilterReason = lNotInFilterReason + ", " + "the end position " + lEnd + " was smaller than " + lEndSpin;
                                            }
                                        }
                                    }

                                    lResult = lResult + "," + lFilter + "," + lNotInFilterReason;
                                    if (lFilter) {
                                        lIdentificationFiltered = true;
                                        txtResult.append(lResult + "\n");
                                    }
                                    //System.out.println(lResult);
                                }
                            }

                        }
                        if (lIdentificationFiltered) {
                            lAcceptedCounter = lAcceptedCounter + 1;
                        }
                    }

                    //print some info
                    progressBar.setIndeterminate(false);
                    progressBar.setStringPainted(true);
                    progressBar.setString("Identifications found : " + iIdentifications.length + ", selected : " + lAcceptedCounter);

                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return true;
            }

            public void finished() {
                // done
            }

        };
        start.start();


    }

    /**
     * This method will split the modified sequence in aa.
     *
     * @param sequence The modified sequence to split
     * @return String[] Array with (modified) aminoacids
     */
    public String[] sequenceToArray(String sequence) {
        Vector<String> sequenceVector = new Vector<String>();
        String sub1 = "";
        if (sequence.indexOf("<", sequence.indexOf("-") + 2) == 1) {
            sequenceVector.add(sequence.substring(0, sequence.indexOf(">") + 1));
            sub1 = sequence.substring(sequence.indexOf(">") + 1);
        } else {
            sequenceVector.add(sequence.substring(0, sequence.indexOf("-") + 2));
            sub1 = sequence.substring(sequence.indexOf("-") + 2);
        }
        for (int i = 0; i < sub1.length(); i++) {
            if (sub1.charAt(i + 1) == '<') {
                sequenceVector.add(sub1.substring(i, sub1.indexOf(">", i) + 1));
                i = sub1.indexOf(">", i);
                if (sub1.charAt(i + 1) == '-') {
                    sequenceVector.set(sequenceVector.size() - 1, sequenceVector.get(sequenceVector.size() - 1) + sub1.substring(i + 1));
                    i = sub1.length() - 1;
                }
            } else {
                if (sub1.charAt(i + 1) == '-') {
                    sequenceVector.add(sub1.substring(i));
                    i = sub1.length() - 1;
                } else {
                    sequenceVector.add(String.valueOf(sub1.charAt(i)));
                }
            }
        }
        String[] sequenceArray = new String[sequenceVector.size()];
        sequenceVector.toArray(sequenceArray);
        return sequenceArray;
    }

    /**
     * This method will download all the protein sequences for the protein accessions in the given vector.
     *
     * @param lProteinAccessions Vector with protein accession
     * @param progressBar        The progress bar to show the progress
     * @return Vector with protein sequences linked to the accession by their position in the vector.
     */
    private Vector<String> getProteinSequences(Vector<String> lProteinAccessions, JProgressBar progressBar) {
        Vector<String> lSequences = new Vector<String>();
        progressBar.setIndeterminate(false);
        progressBar.setStringPainted(true);
        progressBar.setString("Downloading " + lProteinAccessions.size() + " protein sequences");
        progressBar.setMaximum(lProteinAccessions.size());
        for (int i = 0; i < lProteinAccessions.size(); i++) {
            String lAccession = lProteinAccessions.get(i);
            progressBar.setValue(i + 1);
            try {
                if (iDatabaseType == ProteinDatabaseType.UNIPROT) {
                    UniprotSequenceRetriever lRetriever = new UniprotSequenceRetriever(lAccession);
                    lSequences.add(lRetriever.getSequence());
                } else if (iDatabaseType == ProteinDatabaseType.NCBI) {
                    NcbiSequenceRetriever lRetriever = new NcbiSequenceRetriever(lAccession);
                    lSequences.add(lRetriever.getSequence());
                } else if (iDatabaseType == ProteinDatabaseType.IPI) {
                    IpiSequenceRetriever lRetriever = new IpiSequenceRetriever(lAccession);
                    lSequences.add(lRetriever.getSequence());
                }
            } catch (Exception e) {
                lSequences.add(null);
            }
        }
        progressBar.setMaximum(0);
        progressBar.setValue(0);
        return lSequences;
    }

    /**
     * This method will get the different protein accession (including isoforms!) linked the given identifications
     *
     * @param lIdentifications Array with identifications
     * @return Vector with all the different protein accessions
     */
    private Vector<String> getProteinAccessions(Identification[] lIdentifications) {
        //create an vector to store the accessions in
        Vector<String> lAccession = new Vector<String>();
        for (int i = 0; i < lIdentifications.length; i++) {
            //find the proteins for every identification linked to this ratio group
            String lProtein = lIdentifications[i].getAccession();
            String lIsoforms = lIdentifications[i].getIsoforms();
            //check if the protein is already found
            boolean lNewProtein = true;
            for (int j = 0; j < lAccession.size(); j++) {
                if (lAccession.get(j).equalsIgnoreCase(lProtein)) {
                    lNewProtein = false;
                }
            }
            if (lNewProtein) {
                lAccession.add(lProtein);
            }
            //check for every isoform if the isoform is already found
            if (lIsoforms != null && chbUseIsoforms.isSelected()) {
                int lStartIndex = lIsoforms.indexOf("^A");
                StringTokenizer lTok = new StringTokenizer(lIsoforms, "^A");
                while (lStartIndex > 0) {
                    int lEndIndex = lIsoforms.indexOf("^A", lStartIndex + 2);
                    if (lEndIndex == -1) {
                        lEndIndex = lIsoforms.length() - 1;
                    }
                    String lIsoform = lIsoforms.substring(lStartIndex + 2, lEndIndex);
                    lIsoform = lIsoform.substring(0, lIsoforms.indexOf(" "));
                    boolean lNewIsoform = true;
                    for (int j = 0; j < lAccession.size(); j++) {
                        if (lAccession.get(j).equalsIgnoreCase(lIsoform)) {
                            lNewIsoform = false;
                        }
                    }
                    if (lNewIsoform) {
                        lAccession.add(lIsoform);
                    }
                    lStartIndex = lIsoforms.indexOf("^A", lStartIndex + 2);
                }
            }

        }
        return lAccession;
    }

    /**
     * This method will check the amino acid input
     * Rules: - The amino acids must be seperated by a comma
     * - The amino acids must be in the one letter form
     * - If the amino acids are modified it must be like : "K<Ace>"
     *
     * @param aInput String with aminoacids
     * @return boolean if the check was ok or not
     */
    private boolean checkAminoacidInput(String aInput) {
        boolean lCheck = true;
        StringTokenizer lTok = new StringTokenizer(aInput, ",");
        while (lTok.hasMoreElements()) {
            //read token by token
            String lAa = (String) lTok.nextElement();
            if (lAa.length() > 1) {
                //check if there is a modification like: "K<Ace>"
                if (lAa.charAt(1) != '<') {
                    //the should be a modification
                    lCheck = false;
                }
                if (lAa.charAt(lAa.length() - 1) != '>') {
                    lCheck = false;
                }
            }
        }
        return lCheck;
    }

    /**
     * Pass the connection
     *
     * @param connection The connection
     * @param s          The database name
     */
    public void passConnection(Connection connection, String s) {
        iConn = connection;
        iDBName = s;
    }

    /**
     * Engage tool
     *
     * @param aParent     ProjectAnalyzer with the parent that launched this tool.
     * @param aToolName   String with the name for the tool.
     * @param aParameters String with the parameters as stored in the database for this tool.
     * @param aConn       Connection with the DB connection to use.
     * @param aDBName     String with the name of the database we're connected to via 'aConn'.
     * @param aProject    Project with the project we should be analyzing.
     */
    public void engageTool(ProjectAnalyzer aParent, String aToolName, String aParameters, Connection aConn, String aDBName, Project aProject) {
        this.iParent = aParent;
        this.iToolName = aToolName + " (" + aProject.getProjectid() + ". " + aProject.getTitle() + ")";
        this.iParameters = aParameters;
        this.iConn = aConn;
        this.iDBName = aDBName;
        this.iProject = aProject;

        $$$setupUI$$$();

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });

        //create JFrame parameters
        this.setTitle("Filter the identifications for : " + iProject.getTitle());
        this.setContentPane(jpanContent);
        this.setSize(1100, 900);
        this.setLocation(100, 100);
        this.setVisible(true);
    }

    /**
     * Getter for the tool name
     *
     * @return String with the tool name
     */
    public String getToolName() {
        return iToolName;
    }

    /**
     * This method will be called when the tool should show itself on the foreground and request the focus.
     */
    public void setActive() {
        if (this.getState() == Frame.ICONIFIED) {
            this.setState(Frame.NORMAL);
        }

        this.requestFocus();
    }

    /**
     * This method should be called whenever this tool closes down.
     */
    public void close() {
        // Notify the parent.
        iParent.toolClosing(this);
        this.setVisible(false);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        jpanContent = new JPanel();
        jpanContent.setLayout(new GridBagLayout());
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        jpanContent.add(panel1, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Before peptide");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("peptide");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("After peptide");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("N-terminal modification : ");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label4, gbc);
        txtNtermMod = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(txtNtermMod, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("peptide start with");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("peptide ends with");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label6, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("peptide ends not with");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label7, gbc);
        txtStart = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(txtStart, gbc);
        txtEnds = new JTextField();
        txtEnds.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(txtEnds, gbc);
        txtNotEnds = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(txtNotEnds, gbc);
        uniprotRadioButton = new JRadioButton();
        uniprotRadioButton.setSelected(true);
        uniprotRadioButton.setText("Uniprot");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 21;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(uniprotRadioButton, gbc);
        IPIRadioButton = new JRadioButton();
        IPIRadioButton.setText("IPI");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 22;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(IPIRadioButton, gbc);
        NCBIRadioButton = new JRadioButton();
        NCBIRadioButton.setText("NCBI");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 23;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(NCBIRadioButton, gbc);
        nonOfTheAboveRadioButton = new JRadioButton();
        nonOfTheAboveRadioButton.setText("Non of the above");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 24;
        gbc.gridwidth = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(nonOfTheAboveRadioButton, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("Database type");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 21;
        gbc.gridwidth = 4;
        gbc.gridheight = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label8, gbc);
        final JSeparator separator1 = new JSeparator();
        separator1.setOrientation(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.gridheight = 9;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(separator1, gbc);
        final JSeparator separator2 = new JSeparator();
        separator2.setOrientation(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 11;
        gbc.gridheight = 9;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(separator2, gbc);
        txtAfter = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(txtAfter, gbc);
        txtNotAfter = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(txtNotAfter, gbc);
        txtNotBefore = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(txtNotBefore, gbc);
        txtBefore = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(txtBefore, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("After");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label9, gbc);
        final JLabel label10 = new JLabel();
        label10.setText("Not after");
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label10, gbc);
        final JLabel label11 = new JLabel();
        label11.setText("Before");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label11, gbc);
        final JLabel label12 = new JLabel();
        label12.setText("Not before");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(label12, gbc);
        chbNStartSmaller = new JCheckBox();
        chbNStartSmaller.setText("N-term peptide start position <");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(chbNStartSmaller, gbc);
        spinner1 = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 16;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(spinner1, gbc);
        chbNStartLarger = new JCheckBox();
        chbNStartLarger.setText("N-term peptide start position >");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(chbNStartLarger, gbc);
        spinner2 = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 17;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(spinner2, gbc);
        chbCEndSmaller = new JCheckBox();
        chbCEndSmaller.setText("C-term peptide end position < protein sequence lenght -");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(chbCEndSmaller, gbc);
        spinner3 = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 18;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(spinner3, gbc);
        chbCEndlarger = new JCheckBox();
        chbCEndlarger.setText("C-term peptide end position > protein sequence lenght -");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(chbCEndlarger, gbc);
        spinner4 = new JSpinner();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 19;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(spinner4, gbc);
        final JSeparator separator3 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 20;
        gbc.gridwidth = 9;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(separator3, gbc);
        final JSeparator separator4 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 11;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(separator4, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 11;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 8;
        gbc.gridy = 11;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer2, gbc);
        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer3, gbc);
        chbUseIsoforms = new JCheckBox();
        chbUseIsoforms.setSelected(true);
        chbUseIsoforms.setText("Use isoforms");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 25;
        gbc.gridwidth = 9;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel1.add(chbUseIsoforms, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        jpanContent.add(panel2, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel2.add(scrollPane1, gbc);
        txtResult = new JTextArea();
        txtResult.setRows(5);
        scrollPane1.setViewportView(txtResult);
        startButton = new JButton();
        startButton.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel2.add(startButton, gbc);
        progressBar = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel2.add(progressBar, gbc);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(uniprotRadioButton);
        buttonGroup.add(IPIRadioButton);
        buttonGroup.add(NCBIRadioButton);
        buttonGroup.add(nonOfTheAboveRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jpanContent;
    }
}
