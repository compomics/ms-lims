package com.compomics.mslims.db.utils;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author Davy Maddelein
 */

public class dbDeleteQueryGenerator extends JFrame implements Runnable {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(dbDeleteQueryGenerator.class);


    //declaration of variables
    private Connection iConn = null;
    private long iProjectid;
    private int iSelection;
    private String iDBName;
    JScrollPane deletePane;
    private JButton btnContinue;
    private JProgressBar progressBar;
    private static boolean iStandAlone = true;
    private JTextArea txtareaDelete;
    private HashMap<String, String> resultsetMap = new HashMap<String, String>();
    private String deleteString = "do you want to delete these entries? \n";
    private int levelExecute = 0;
    private Mapping mapper = new Mapping();
    private String removedLastComma;
    private HashMap<String,Integer> summedRows = new HashMap<String, Integer>();
    private CountDownLatch doneLatch;


    public dbDeleteQueryGenerator(Connection aConnection, String aDBName, long aProjectid, int aSelection, CountDownLatch doneLatch) {
        //iConn.setAutoCommit(false);
        this.doneLatch = doneLatch;
        this.setTitle("clean project " + aProjectid);
        this.iConn = aConnection;
        this.iDBName = aDBName;
        this.iProjectid = aProjectid;
        this.iSelection = aSelection;

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screen.width / 10), (screen.height / 10));
        initComponents();
        progressBar.setIndeterminate(true);
        this.setVisible(true);
    }


    private void initComponents() {

        deletePane = new JScrollPane();
        txtareaDelete = new JTextArea();
        btnContinue = new JButton();
        progressBar = new JProgressBar();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        txtareaDelete.setColumns(20);
        txtareaDelete.setRows(5);
        txtareaDelete.setEnabled(false);
        deletePane.setViewportView(txtareaDelete);

        btnContinue.setText("close");
        btnContinue.setEnabled(false);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(btnContinue))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(deletePane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addComponent(deletePane, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                                .addComponent(btnContinue)
                                .addContainerGap())
        );
        DefaultCaret caret = (DefaultCaret)txtareaDelete.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        btnContinue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                close();
            }
        });
    pack();
    }

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(dbDeleteQueryGenerator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dbDeleteQueryGenerator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dbDeleteQueryGenerator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dbDeleteQueryGenerator.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private void close() {
        this.setVisible(false);
        this.dispose();
    }

    private void closeConnection() {
        try {
            if (iConn != null) {
                iConn.close();
                logger.info("\nClosed DB connection.\n");
            }
        } catch (SQLException e) {
            logger.error(e);
        }
    }

    public boolean isStandAlone() {
        return iStandAlone;
    }

    public static void setNotStandAlone() {
        iStandAlone = false;
    }

    public void run() {
        CountDownLatch deletedLatch = new CountDownLatch(1);
        ExecuteDeletes deleteExecutor = new ExecuteDeletes();
        deleteExecutor.startDeleteWorker(deletedLatch);
        try {
            deletedLatch.await();
        } catch (InterruptedException e) {
            logger.error(e);
        }
        doneLatch.countDown();
    }

    //convert resultset to string of values for delete
    private class Mapping extends Thread {

        private void put(ResultSet rs, HashMap<String, String> hash, String key) throws SQLException {
            rs.beforeFirst();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append(",");
                sb.append(rs.getString(1));
            }
            removedLastComma = sb.toString();
            removedLastComma = removedLastComma.replaceFirst(",", "");
            hash.put(key, removedLastComma);
            rs.last();
            deleteString = deleteString + key + ": " + rs.getRow() + " rows \n";
            summedRows.put(key,rs.getRow());
        }
    }

    //easy override access
    private class ExecuteDeletes extends Thread {

        public void startDeleteWorker(CountDownLatch deletedLatch)
        {
            deleteWorker.execute();
            deletedLatch.countDown();
        }
    }
    //swingworker where we do all the database work
    private SwingWorker deleteWorker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws SQLException {
               try {

                   Statement stmt = iConn.createStatement();
                   //precaution/check if there isn't something horribly wrong with the db, or a force from empty
                   ResultSet testResult = stmt.executeQuery("select projectid from project where projectid =" +iProjectid);
                   testResult.last();
                   if (testResult.getRow() == 0)
                   {
                       JOptionPane.showMessageDialog(dbDeleteQueryGenerator.this,"could not find the project id in the database, please check your database.");
                       close();
                   }
                   txtareaDelete.append("fetching the entries \n");

                   for (int i = 0; i < iSelection; i++) {
                   levelExecute = previewDeletes(i);
                   }

                   int finalChoice = JOptionPane.showConfirmDialog(dbDeleteQueryGenerator.this, deleteString);
                   if (finalChoice == JOptionPane.YES_OPTION) {
                       executeDeletes();
                   } else {
                    close();
                   }
               } catch (SQLException e) {
                logger.error(e);
               }
            return null;
            }


            private int previewDeletes(int levelcheck) throws SQLException {
                Statement stmt = iConn.createStatement();
                ResultSet returnedResult = null;

                //check if level has values if lazy or unsure user

                if (levelcheck == 0) {
                    returnedResult = stmt.executeQuery("select t.l_quantitation_groupid from  identification_to_quantitation as t , identification as i, spectrum as s where" +
                        " i.l_spectrumid = s.spectrumid and t.l_identificationid = i.identificationid and s.l_projectid = " + iProjectid);

                    if (returnedResult.isBeforeFirst()) {
                        mapper.put(returnedResult, resultsetMap, "quantitation_group");
                        mapper.put(returnedResult, resultsetMap, "identification_to_quantitation");

                        returnedResult = stmt.executeQuery("select distinct q.l_quantitation_fileid from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q where i.l_spectrumid = f.spectrumid and f.l_projectid = " + iProjectid + " and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid");
                        mapper.put(returnedResult, resultsetMap, "quantitation_file");


                        returnedResult = stmt.executeQuery("select distinct u.quantitationid from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q, quantitation_file as e, quantitation as u where e.quantitation_fileid = q.l_quantitation_fileid and i.l_spectrumid = f.spectrumid and f.l_projectid=" + iProjectid + " and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid and u.l_quantitation_groupid = q.quantitation_groupid");
                        mapper.put(returnedResult, resultsetMap, "quantitation");
                        levelExecute = 1;
                    } else {
                        levelExecute = 2;
                    }
                } else if (levelcheck == 1) {
                    returnedResult = stmt.executeQuery("select i.identificationid from identification as i,spectrum as s where l_spectrumid = spectrumid and l_projectid = " + iProjectid);
                    if (returnedResult.isBeforeFirst()) {
                        mapper.put(returnedResult, resultsetMap, "identification");

                        returnedResult = stmt.executeQuery("select i.l_datfileid from identification as i,spectrum as s where l_spectrumid = spectrumid and l_projectid =" + iProjectid + " group by l_datfileid");
                        mapper.put(returnedResult, resultsetMap, "datfile");

                        returnedResult = stmt.executeQuery("select v.validationid from validation as v, identification as i,spectrum as s where v.l_identificationid =i.identificationid and i.l_spectrumid = s.spectrumid and s.l_projectid =  " + iProjectid);
                        mapper.put(returnedResult, resultsetMap, "validation");

                        returnedResult = stmt.executeQuery("select f.fragmentionid from fragmention as f,identification as i, spectrum as s where f.l_identificationid = i.identificationid and i.l_spectrumid = s.spectrumid and s.l_projectid =" + iProjectid);
                        mapper.put(returnedResult, resultsetMap, "fragmention");
                        levelExecute = 2;
                    } else {
                        levelExecute = 3;
                    }
                } else if (levelcheck == 2) {
                    returnedResult = stmt.executeQuery("select spectrumid from spectrum where l_projectid = " + iProjectid);
                    if (returnedResult.isBeforeFirst()) {
                        mapper.put(returnedResult, resultsetMap, "spectrum");
                        mapper.put(returnedResult, resultsetMap, "spectrum_file");

                        returnedResult = stmt.executeQuery("select s.scanid from scan as s, spectrum as sp where s.l_spectrumid = sp.spectrumid and sp.l_projectid =" + iProjectid);
                        mapper.put(returnedResult, resultsetMap, "scan");

                        levelExecute = 3;
                    }
                } else if (levelcheck == 3) {
                    returnedResult = stmt.executeQuery("select lcrunid from lcrun where l_projectid = " + iProjectid);
                    mapper.put(returnedResult, resultsetMap, "lcrun");
                    levelExecute = 4;
                }
                if (returnedResult != null) {
                    returnedResult.close();
                }
            return levelExecute;
            }

            private void executeDeletes() throws SQLException {

                Collection<Integer> summedRowsValues = summedRows.values();

                int sum = 0;
                for (int v : summedRowsValues){
                    sum += v;
                }
                summedRowsValues=null;
                progressBar.setMaximum(sum + 1);
                progressBar.setValue(1);
                progressBar.setIndeterminate(false);
                Statement stmt;
                stmt = iConn.createStatement();
                for (int i = 0; i < levelExecute; i++) {

                    if (i == 3) {
                        try{
                            txtareaDelete.append("deleting lcrun \n");
                            stmt.executeUpdate("delete l.* from lcrun as l where l.lcrunid in (" + resultsetMap.get("lcrun") + ")");
                            updateProgressBar("lcrun");
                            txtareaDelete.append("deleted lcrun \n");
                        } catch (SQLException e) {
                            logger.error(e);}
                    }
                    if (i == 2) {
                        try{
                            txtareaDelete.append("deleting scan \n");
                            stmt.executeUpdate("delete s.* from scan as s where s.scanid in (" + resultsetMap.get("scan") + ")");
                            updateProgressBar("scan");
                            txtareaDelete.append("deleted scan \n");
                        } catch (SQLException e) {
                            logger.error(e);}

                        try{
                            txtareaDelete.append("deleting spectrum file \n");
                            stmt.executeUpdate("delete sf.* from spectrum_file as sf where sf.l_spectrumid in (" + resultsetMap.get("spectrum_file") + ")");
                            updateProgressBar("spectrum_file");
                            txtareaDelete.append("deleted spectrum file \n");
                        } catch (SQLException e) {
                            logger.error(e);}

                        try{
                            txtareaDelete.append("deleting spectrum \n");
                            stmt.executeUpdate("delete s.* from spectrum as s where s.spectrumid in (" + resultsetMap.get("spectrum") + ")");
                            updateProgressBar("spectrum");
                            txtareaDelete.append("deleted spectrum \n");
                        } catch (SQLException e) {
                            logger.error(e);
                            /*if (iConnection != null) {
                            try {
                                iConnection.rollback();
                            } catch (SQLException ex1) {
                                System.out.println(ex1.getMessage());
                            }}*/
                        }
                    }
                    if (i == 1) {
                        try {
                            txtareaDelete.append("deleting validation \n");
                            stmt.executeUpdate("delete v.* from validation as v where v.validationid in (" + resultsetMap.get("validation") + ")");
                            updateProgressBar("validation");
                            txtareaDelete.append("deleted validation \n");
                        } catch (SQLException e) {
                            logger.error(e);
                        }
                            txtareaDelete.append("deleting fragmention \n");
                            stmt.executeUpdate("delete f.* from fragmention as f where f.fragmentionid in (" + resultsetMap.get("fragmention") + ")");
                            updateProgressBar("fragmention");
                            txtareaDelete.append("deleted fragmention \n");
                        try {
                            txtareaDelete.append("deleting datfile \n");
                            stmt.executeUpdate("delete d.* from datfile as d where d.datfileid in (" + resultsetMap.get("datfile") + ")");
                            updateProgressBar("datfile");
                            txtareaDelete.append("deleted datfile \n");
                        } catch (SQLException e) {
                            logger.error(e);
                        }
                        try {
                            txtareaDelete.append("deleting identification \n");
                            stmt.executeUpdate("delete i.* from identification as i where i.identificationid in (" + resultsetMap.get("identification") + ")");
                            updateProgressBar("identification");
                            txtareaDelete.append("deleted identification \n");
                        } catch (SQLException e) {
                            logger.error(e);
                            /*if (iConnection != null) {
                                try {
                                    iConnection.rollback();
                                } catch (SQLException ex1) {
                                    System.out.println(ex1.getMessage());
                                }
                            }*/
                        }
                    }
                    if (i == 0) {
                        try {
                            txtareaDelete.append("deleting quantitation \n");
                            stmt.executeUpdate("delete q.* from quantitation as q where q.quantitationid in (" + resultsetMap.get("quantitation") + ")");
                            updateProgressBar("quantitation");
                            txtareaDelete.append("deleted quantitation \n");
                        } catch (SQLException e) {
                            logger.error(e);}
                        try {
                            txtareaDelete.append("deleting quantitation file \n");
                            stmt.executeUpdate("delete qf.* from quantitation_file as qf where qf.quantitation_fileid in (" + resultsetMap.get("quantitation_file") + ")");
                            updateProgressBar("quantitation_file");
                            txtareaDelete.append("deleted quantitation file \n");
                        } catch (SQLException e) {
                            logger.error(e);}
                        try {
                            txtareaDelete.append("deleting quantitation group \n");
                            stmt.executeUpdate("delete qg.* from quantitation_group as qg where qg.quantitation_groupid in (" + resultsetMap.get("quantitation_group") + ")");
                            updateProgressBar("quantitation_group");
                            txtareaDelete.append("deleted quantitation group \n");
                        } catch (SQLException e) {
                            logger.error(e);}
                        try {
                            txtareaDelete.append("deleting identification to quantitation \n");
                            stmt.executeUpdate("delete itq.* from identification_to_quantitation as itq where itq.l_quantitation_groupid in (" + resultsetMap.get("identification_to_quantitation") + ")");
                            updateProgressBar("identification_to_quantitation");
                            txtareaDelete.append("deleted identification to quantitation \n");
                        } catch (SQLException e) {
                            logger.error(e);
                            /*if (iConnection != null) {
                            try {
                                iConnection.rollback();
                            } catch (SQLException ex1) {
                                System.out.println(ex1.getMessage());
                            }}*/
                        }
                    }
                }
            //iConn.commit();
                stmt.executeUpdate("update project set modificationdate = CURRENT_TIMESTAMP where projectid ="+iProjectid);
                JOptionPane.showMessageDialog(null,"done deleting project " +iProjectid);
                btnContinue.setEnabled(true);
            }
    private void updateProgressBar(String key)
    {
        if (summedRows.containsKey(key))
        {
            progressBar.setValue(progressBar.getValue() + summedRows.get(key));
        }
    }

    };
}