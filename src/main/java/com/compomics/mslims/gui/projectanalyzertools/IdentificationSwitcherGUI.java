/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * IdentificationSwitcherGUI.java
 *
 * Created on Feb 2, 2012, 12:04:52 PM
 */
package com.compomics.mslimscore.gui.projectanalyzertools;

import com.compomics.mslimsdb.accessors.AlternativeIdentification;
import com.compomics.mslimsdb.accessors.AlternativeIdentificationTableAccessor;
import com.compomics.mslimsdb.accessors.Identification;
import com.compomics.mslimsdb.accessors.Project;
import com.compomics.mslimscore.gui.ProjectAnalyzer;
import com.compomics.mslimscore.gui.interfaces.ProjectAnalyzerTool;
import com.compomics.mslimscore.gui.table.IdentificationTableAccessorsTableModel;
import com.compomics.mslimscore.gui.table.renderers.AlternativeIdentificationAccessorsTableModel;
import com.compomics.mslimscore.gui.table.renderers.MasterAlternativeRenderer;
import com.compomics.util.gui.JTableForDB;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Davy Maddelein
 */
public class IdentificationSwitcherGUI extends javax.swing.JFrame implements ProjectAnalyzerTool {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(IdentificationSwitcherGUI.class);

    private javax.swing.JButton btnSwitch;
    private javax.swing.JButton btnCommit;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private JTableForDB alternativesTable = new JTableForDB();
    private JTableForDB masterTable = new JTableForDB();


    private Connection iConn;
    private boolean iStandAlone = true;
    Vector<AlternativeIdentification> alternativesVector;
    HashMap<Identification, Vector<AlternativeIdentification>> mastersToAlternativesMap = new HashMap<Identification, Vector<AlternativeIdentification>>();
    private Vector masterVector = new Vector<Identification>();
    private ProjectAnalyzer iParent;
    private String iParameters;
    private Project iProject;
    private String iDBName;
    private String iToolName;
    private boolean dbUsed;
    private int numberOfDatfiles = 0;

    /** Creates new form IdentificationSwitcherGUI
     * @param aTitle the title of the pane
     * @param aResults vector of all results to compare
     * @param aAlternativeResults vector for all the results that scored equal
     **/

    public IdentificationSwitcherGUI(String aTitle,Vector aResults, Vector<AlternativeIdentification> aAlternativeResults){
        super(aTitle);
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        //TODO switch out logic to own class
        this.masterVector = aResults;
        this.alternativesVector = aAlternativeResults;
        this.dbUsed = false;
        mapMastersToAlternatives(masterVector, alternativesVector);
        initComponents();
        masterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        masterTable.setModel(new IdentificationTableAccessorsTableModel(aResults));
        alternativesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        alternativesTable.setModel(new DefaultTableModel());
        jScrollPane2.setVisible(true);
        jLabel1.setVisible(false);
        jComboBox1.setVisible(false);
        this.setVisible(true);

    }

    public IdentificationSwitcherGUI(ResultSet identificationSelection){
        dbUsed = true;
        this.setVisible(true);

    }

    public IdentificationSwitcherGUI()  {
        this.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window is in the process of being closed.
             * The close operation can be overridden at this point.
             */
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
    }

    public void engageTool(ProjectAnalyzer aParent, String aToolName, String aParameters, Connection aConn, String aDBName, Project aProject) {
        this.iParent = aParent;
        this.iToolName = aToolName + " (" + aProject.getProjectid() + ". " + aProject.getTitle() + ")";
        this.iParameters = aParameters;
        this.iConn = aConn;
        this.iDBName = aDBName;
        this.iProject = aProject;
        dbUsed= true;
        PreparedStatement identification;
        PreparedStatement alternativeIdentification;
        ResultSet identificationResultSet = null;
        ResultSet alternativeIdentificationResultSet = null;
        try {
            identification = iConn.prepareStatement("select identification.* from identification,spectrum where l_spectrumid = identificationid and l_projectid = "+ iProject.getProjectid());
            alternativeIdentification = iConn.prepareStatement("select alternative_identification.* from alternative_identification,identification,spectrum where alternative_identification.l_identificationid = identification.identificationid and l_spectrumid = spectrumid and l_projectid = "+iProject.getProjectid());
            identificationResultSet = identification.executeQuery();
            alternativeIdentificationResultSet = alternativeIdentification.executeQuery();
            mapAlternativesToMaster(identificationResultSet,alternativeIdentificationResultSet);
        } catch (SQLException e) {
            logger.error(e);
        }
        initComponents();
        this.setVisible(true);
        masterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        masterTable.setModel(new ResultSetTableModel(identificationResultSet));
        alternativesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        alternativesTable.setModel(new ResultSetTableModel(alternativeIdentificationResultSet));
    }

    public String getToolName() {
        return "Identification Switcher";
    }

    public void setActive() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void close() {
            this.setVisible(false);
            this.dispose();
    }

    //mapping for database results
    private void mapAlternativesToMaster(ResultSet masterResultSet,ResultSet alternativeResultSet) throws SQLException {
        int keyChecker = -1;
        Vector<AlternativeIdentification> alternativesList = new Vector<AlternativeIdentification>();
        while(alternativeResultSet.next()) {
            if (alternativeResultSet.getInt("l_identificationid") != keyChecker){
                mastersToAlternativesMap.put(new Identification(masterResultSet),alternativesList);
                keyChecker = alternativeResultSet.getInt("l_identificationid");
                alternativesList.add(new AlternativeIdentification((alternativeResultSet)));
            } else {alternativesList.add(new AlternativeIdentification(alternativeResultSet));
            }
        }
        if (!alternativesList.isEmpty()){
            mastersToAlternativesMap.put(new Identification(masterResultSet),alternativesList);
        }
    }

    //TODO mapping for selected database results

    //mapping for file results
    private void mapMastersToAlternatives(Vector resultsVector,Vector<AlternativeIdentification> alternativesVector) {
        int counter = 0;
        for (Object aResultsVector : resultsVector) {
            if (aResultsVector instanceof Identification) {
                Vector<AlternativeIdentification> alternativesList = new Vector<AlternativeIdentification>();
                for (AlternativeIdentification anAlternativesVector : alternativesVector) {
                    if (anAlternativesVector.getTemporarySpectrumfilename().equals(((Identification) aResultsVector).getTemporarySpectrumfilename())) {
                        ((Identification) aResultsVector).setAlternative(true);
                        alternativesList.add(anAlternativesVector);

                    }
                }
                if (!alternativesList.isEmpty()) {
                    mastersToAlternativesMap.put((Identification) aResultsVector, alternativesList);
                }
            } else {
                numberOfDatfiles++;
            }
            counter++;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        btnSwitch = new JButton();
        btnCommit = new JButton();
        jComboBox1 = new JComboBox();
        jLabel1 = new JLabel();
        jScrollPane1 = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        masterTable = new JTableForDB();
        jScrollPane2 = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        alternativesTable = new JTableForDB();
        setMaximizedBounds(new Rectangle(100, 50, 50, 100));

        btnSwitch.setText("Switch selected identifications");

        btnCommit.setText("finish editing");

        jLabel1.setText("Choose project");

        masterTable.setModel(new DefaultTableModel());
        alternativesTable.setModel(new DefaultTableModel());

        masterTable.setMaximumSize(new Dimension(75, 64));
        jScrollPane1.setViewportView(masterTable);
        alternativesTable.setMaximumSize(new Dimension(75, 64));
        jScrollPane2.setViewportView(alternativesTable);
        alternativesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        masterTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        masterTable.setDefaultRenderer(Identification.class, new MasterAlternativeRenderer());
        alternativesTable.setDefaultRenderer(Identification.class,new MasterAlternativeRenderer());

        masterTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                //check to ensure only the correct alternative identifications are switched
                if (mastersToAlternativesMap.containsKey(masterVector.get(masterTable.getSelectedRow()+ numberOfDatfiles))) {
                    alternativesTable.setModel(new AlternativeIdentificationAccessorsTableModel(mastersToAlternativesMap.get(masterVector.get(masterTable.getSelectedRow()+ numberOfDatfiles))));
                    jScrollPane2.setVisible(true);
                } else {
                    alternativesTable.setModel(new DefaultTableModel());
                }
            }
        });

        btnSwitch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Identification masterRowToChange = (Identification) masterVector.get(masterTable.convertRowIndexToModel(masterTable.getSelectedRow())+ numberOfDatfiles);
                String masterFilename = masterRowToChange.getTemporarySpectrumfilename();
                String masterDatFile = masterRowToChange.getTemporaryDatfilename();
                AlternativeIdentification alternativesRowToChange = (mastersToAlternativesMap.get(masterVector.get(masterTable.getSelectedRow()+ numberOfDatfiles)).get(alternativesTable.getSelectedRow()));
                String alternativeFileName = alternativesRowToChange.getTemporarySpectrumfilename();
                HashMap masterHashMap = masterRowToChange.getHashMap();
                masterRowToChange= new Identification(alternativesRowToChange.getHashMap());
                masterRowToChange.setTemporarySpectrumfilename(masterFilename);
                masterRowToChange.setTemporaryDatfilename(masterDatFile);
                alternativesRowToChange = new AlternativeIdentification(masterHashMap);
                alternativesRowToChange.setTemporarySpectrumfilename(alternativeFileName);
                alternativesTable.setModel(new AlternativeIdentificationAccessorsTableModel(mastersToAlternativesMap.get((masterVector.get(masterTable.getSelectedRow()+ numberOfDatfiles)))));
                masterVector.set(masterTable.getSelectedRow()+ numberOfDatfiles, masterRowToChange);

                mastersToAlternativesMap.get(masterRowToChange).set(alternativesTable.getSelectedRow(), alternativesRowToChange);
                masterTable.setModel(new IdentificationTableAccessorsTableModel(masterVector));
                }
        });

        btnCommit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //TODO return to resultframe if db not used?
                if(dbUsed){
                    try {
                        Collection<Vector<AlternativeIdentification>> alternativeIdentificationCollection = mastersToAlternativesMap.values();
                        for (Vector<AlternativeIdentification> anAlternativeIdentificationVector : alternativeIdentificationCollection) {
                                for(AlternativeIdentification anAlternativeIdentification : anAlternativeIdentificationVector){
                                    AlternativeIdentificationTableAccessor lAlternativeIdentificationTableAccessor = new AlternativeIdentificationTableAccessor();
                                    lAlternativeIdentificationTableAccessor.persist(iConn);
                                }

                        }
                    } catch (SQLException e) {
                        logger.error(e);
                    }
                } else {
                    close();
                }

            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(1301, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addContainerGap(1284, Short.MAX_VALUE))))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 567, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(btnCommit)
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGap(62, 62, 62)
                                                .addComponent(btnSwitch)
                                                .addGap(53, 53, 53)
                                                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 562, GroupLayout.PREFERRED_SIZE)))
                                .addGap(35, 35, 35))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(jLabel1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnCommit)
                                                .addGap(26, 26, 26))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(183, 183, 183)
                                .addComponent(btnSwitch)
                                .addContainerGap(355, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IdentificationSwitcherGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IdentificationSwitcherGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IdentificationSwitcherGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IdentificationSwitcherGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        IdentificationSwitcherGUI guiframe = new IdentificationSwitcherGUI();
        guiframe.setVisible(true);

    }

    public Vector<AlternativeIdentification> getAlternativesToStore(){
        return alternativesVector;
    }

    public Vector getMastersToStore(){
        return masterVector;
    }

    public boolean isStandAlone() {
        return iStandAlone;
    }

    public void setNotStandAlone() {
        iStandAlone = false;
    }

    //TODO to separate TableModel

    private class ResultSetTableModel extends AbstractTableModel {
        private Vector<Identification> rows;

        public ResultSetTableModel(ResultSet rs) {
            try {
                while (rs.next()){
                    rows.add(new Identification(rs));
                }
            } catch (SQLException e) {
                logger.error(e);
            }
        }
        public int getRowCount() {
            return rows.size();
        }

        public int getColumnCount() {
            return 10;
        }

        public Object getValueAt(int i, int i1) {
            return null;
        }
    }
}
