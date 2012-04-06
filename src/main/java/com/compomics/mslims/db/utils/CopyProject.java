package com.compomics.mslims.db.utils;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by IntelliJ IDEA.
 * User: Davy
 * Date: 4/10/11
 * Time: 9:35
 */
public class CopyProject extends JFrame {

    private Connection inconn;
    private Connection outconn;
    private long projectNumber;
    private java.sql.Statement stmtout;
    private ResultSet rs;
    private ResultSetMetaData spectrumMetadata;
    private String columnString;
    private Object rowResult;
    private String rowInsertString = "";
    private int batchCounter = 0;
    private ResultSet spectrumResultset;
    private ResultSetMetaData projectMetadata;
    private String lcrunInsertstring = "l_projectid";
    private String projectInsertstring = "";
    private ResultSet lcrunResultset;
    private ResultSetMetaData lcrunMetadata;
    private ResultSet scanResultset;
    private ResultSet spectrum_fileResultset;
    private String identificationKeystring;
    private int insertInt;
    private HashMap<Integer, Integer> oldnewidentificationkeys;
    private PreparedStatement inprep1;
    private PreparedStatement inprep2;
    private PreparedStatement inprep3;
    private PreparedStatement inprep4;
    private PreparedStatement inprep5;
    private PreparedStatement inprep6;
    private PreparedStatement inprep7;
    private PreparedStatement inprep8;
    private PreparedStatement inprep9;
    private PreparedStatement inprep10;
    private JTextArea txtAreaCopyOutput;
    private JProgressBar copyProgressBar;
    private JScrollPane scrollPaneCopy;
    private JButton btnContinue;


    public CopyProject(Connection connFrom, Connection connTo, long projectNumber) throws SQLException, IOException {
        this.projectNumber = projectNumber;
        this.inconn = connFrom;
        this.outconn = connTo;
        this.setTitle("copying project");

        //false so if something should happen we can rollback
        //outconn.setAutoCommit(false);
        stmtout = outconn.createStatement();


        initComponents();
        copyProgressBar.setIndeterminate(true);
        txtAreaCopyOutput.setEnabled(false);
        btnContinue.setEnabled(false);
        this.setVisible(true);
        //prepare the statements we will execute later

        inprep1 = inconn.prepareStatement("select l_userid,l_protocolid,title,description,username,creationdate,modificationdate from project where projectid = " + projectNumber);

        inprep2 = inconn.prepareStatement("select description,filecount,name,dvd_master_number,dvd_secondary_number,primary_fraction,username,creationdate,modificationdate from lcrun where l_projectid =  " + projectNumber + " order by lcrunid ");

        inprep3 = inconn.prepareStatement("select d.filename,d.file,d.server,d.folder,d.username from datfile as d join (select distinct l_datfileid as result from identification as i, spectrum as s where s.spectrumid = i.l_spectrumid and s.l_projectid = " + projectNumber + ") as r on r.result = d.datfileid");

        inprep4 = inconn.prepareStatement("select i.identificationid,i.l_datfileid,i.datfile_query,i.accession,i.start,i.end,i.enzymatic,i.sequence,i.modified_sequence,i.ion_coverage,i.score,i.homology,i.exp_mass,i.cal_mass,i.light_isotope,i.heavy_isotope,i.valid,i.Description,i.identitythreshold," +
                "i.confidence,i.DB,i.title,i.precursor,i.charge,i.isoforms,i.db_filename,i.mascot_version,i.username from identification as i, spectrum as s where s.spectrumid = i.l_spectrumid and s.l_projectid = " + projectNumber + " order by i.l_datfileid,i.identificationid");

        inprep5 = inconn.prepareStatement("select f.l_identificationid,f.iontype,f.ionname,f.l_ionscoringid,f.mz,f.intensity,f.fragmentionnumber,f.massdelta,f.masserrormargin,f.username from fragmention as f, identification as i, spectrum as s where " +
                "s.spectrumid = i.l_spectrumid and i.identificationid=f.l_identificationid and s.l_projectid = " + projectNumber + " order by l_identificationid ");

        inprep6 = inconn.prepareStatement("select v.l_validationtypeid,v.username,v.auto_comment,v.manual_comment from validation as v, identification as i, spectrum as s where " +
                "s.spectrumid = i.l_spectrumid and i.identificationid=v.l_identificationid and s.l_projectid = " + projectNumber);

        inprep7 = inconn.prepareStatement("select distinct q.l_quantitation_fileid from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q where i.l_spectrumid = f.spectrumid and f.l_projectid = " + projectNumber + " and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid");

        inprep8 = inconn.prepareStatement("select g.l_quantitation_fileid, g.file_ref, g.username from quantitation_group as g,(select distinct t.l_quantitation_groupid " +
                " as res from identification_to_quantitation as t , identification as i, spectrum as s where i.l_spectrumid = s.spectrumid and t.l_identificationid = i.identificationid and s.l_projectid = "+projectNumber+" order by l_quantitation_groupid) as result where " +
                "g.quantitation_groupid = result.res order by l_quantitation_fileid");

         inprep10 = inconn.prepareStatement("select t.l_identificationid,t.l_quantitation_groupid,t.type,t.username from identification_to_quantitation as t , identification as i, spectrum as s where i.l_spectrumid = s.spectrumid and t.l_identificationid = i.identificationid and s.l_projectid =" + projectNumber + " order by l_quantitation_groupid");

        inprep9 = inconn.prepareStatement("select q.l_quantitation_groupid,q.ratio,q.standard_error,q.type,q.valid,q.comment,q.username from quantitation_group as g, quantitation as q, identification_to_quantitation as t , identification as i, spectrum as s where" +
                " i.l_spectrumid = s.spectrumid and t.l_identificationid = i.identificationid and g.quantitation_groupid = t. l_quantitation_groupid" +
                " and q.l_quantitation_groupid = g.quantitation_groupid and s.l_projectid =  " + projectNumber + " group by q.quantitationid");


        executeCopy copyExecutor = new executeCopy();
        copyExecutor.startCopyWorker();
    }

    private void initComponents() {

        copyProgressBar = new JProgressBar();
        scrollPaneCopy = new JScrollPane();
        txtAreaCopyOutput = new JTextArea();
        btnContinue = new JButton();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        txtAreaCopyOutput.setColumns(20);
        txtAreaCopyOutput.setRows(5);
        scrollPaneCopy.setViewportView(txtAreaCopyOutput);

        btnContinue.setText("Continue");
        txtAreaCopyOutput.setText("");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(scrollPaneCopy, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(19, 19, 19)
                                                .addComponent(copyProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(55, 55, 55)
                                                .addComponent(btnContinue)))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(copyProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(scrollPaneCopy, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnContinue)
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        DefaultCaret caret = (DefaultCaret)txtAreaCopyOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        btnContinue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
             close();
            }
        });
                pack();
    }

    private void close() {
        dispose();
    }


    private int copyProject(PreparedStatement statement) throws SQLException {
        txtAreaCopyOutput.append("copying project \n");
        rs = statement.executeQuery();
        projectMetadata = rs.getMetaData();
        insertInt = projectMetadata.getColumnCount();


        //first we get the column names from the result of the query we did so we can create an insert into
        for (int i = 0; i < insertInt; i++) {
            columnString = projectMetadata.getColumnName(i + 1);
            projectInsertstring = projectInsertstring + "," + columnString;
        }
        projectInsertstring = projectInsertstring.replaceFirst(",", "");

        //next we add every resultrow from the query to the update batch
        while (rs.next()) {
            if (batchCounter == 500) {
                //execute the batch if batch is the size indicated
                stmtout.executeBatch();
                batchCounter = 0;
            }

            //for each row we create the values part of the insert
            for (int i = 0; i < insertInt; i++) {
                rowResult = rs.getObject(i + 1);
                if(rowResult instanceof String){rowResult = ((String) rowResult).replaceAll("'","");}
                rowInsertString = rowInsertString + ",'" + rowResult + "'";
            }
            rowInsertString = rowInsertString.replaceFirst(",", "");
            //add to the batch
            stmtout.addBatch("insert into project (" + projectInsertstring + ") values (" + rowInsertString + ");");
            batchCounter++;
        }

        if (batchCounter != 0) {
            //execute leftover which is less than buffer
            stmtout.executeBatch();
        }
        ResultSet temp = stmtout.getGeneratedKeys();
        temp.next();
        projectInsertstring = null;
        projectMetadata = null;
        statement.close();
        return temp.getInt(1);
    }


    private ResultSet copyLcrun(PreparedStatement lcrunstatement, int newProjectNumber) throws SQLException {
        txtAreaCopyOutput.append("copying lcrun \n");
        lcrunResultset = lcrunstatement.executeQuery();
        lcrunMetadata = lcrunResultset.getMetaData();
        insertInt = lcrunMetadata.getColumnCount();

        for (int i = 0; i < insertInt; i++) {
            columnString = lcrunMetadata.getColumnName(i + 1);
            lcrunInsertstring = lcrunInsertstring + "," + columnString;
        }

        //next we add every resultrow from the query to the update batch
        batchCounter = 0;
        while (lcrunResultset.next()) {
            if (batchCounter == 500) {
                //execute the batch if batch is the size indicated
                stmtout.executeBatch();
                batchCounter = 0;
            }
            //for each row we create the values part of the insert

            rowInsertString = String.valueOf(newProjectNumber);
            for (int i = 0; i < insertInt; i++) {
                rowResult = lcrunResultset.getObject(i + 1);
                if (lcrunResultset.wasNull()) {
                    rowResult = "NULL";
                    rowInsertString = rowInsertString + "," + rowResult;
                } else {
                    rowInsertString = rowInsertString + ",'" + rowResult + "'";
                }
            }

            //add to the batch
            stmtout.addBatch("insert into lcrun (" + lcrunInsertstring + ") values (" + rowInsertString + ");");
            batchCounter++;
        }
        if (batchCounter != 0) {
            //execute leftover which is less than buffer
            stmtout.executeBatch();
        }
        lcrunResultset.close();
        lcrunInsertstring = null;
        lcrunMetadata = null;
        lcrunstatement.close();
        return stmtout.executeQuery("select lcrunid from lcrun where l_projectid =" + newProjectNumber + " order by lcrunid");
    }


    private ArrayList<Integer> copySpectrum(ResultSet lcrunKeyResultset, int newProjectNumber) throws SQLException, IOException {
        txtAreaCopyOutput.append("copying spectrum \n");
        PreparedStatement spectrumfileInsertStatement = outconn.prepareStatement("insert into spectrum_file (l_spectrumid,file) values (?,?)");

        Blob spfBlob;
        ArrayList<Integer> lcrunkeys = new ArrayList<Integer>();
        ArrayList<Integer> spectrumInsertedkeysList = new ArrayList<Integer>();
        Iterator<Integer> spectrumInsertedkeysIter;
        int spectrumInstertedKey;

        //add all the lcrun keys to a list because mysql statements can only hold one resultset TODO move to other worker to close statement/resultset
        while (lcrunKeyResultset.next()) {
            lcrunkeys.add(lcrunKeyResultset.getInt(1));
        }
        Iterator<Integer> lcrunkeysiter = lcrunkeys.iterator();

        int lcrunFetchedKey;
        int lcrunGeneratedKey = 0;
        batchCounter = 0;
        String spectrumInsertString;
        String spectrumTableString;

        //get everything from scan
        PreparedStatement scanStatement = inconn.prepareStatement("select scan.number,scan.rtsec from scan,spectrum where scan.l_spectrumid = spectrum.spectrumid and spectrum.l_projectid = " + projectNumber + " order by l_spectrumid");
        scanResultset = scanStatement.executeQuery();
        ResultSetMetaData scanmetadata = scanResultset.getMetaData();
        int scanrowint = scanmetadata.getColumnCount();
        boolean emptyResultSet = false;
        if(!scanResultset.isBeforeFirst()){
            emptyResultSet = true;
        }

        //get everything from spectrum_file
        PreparedStatement spectrum_fileStatement = inconn.prepareStatement("select file from spectrum_file,spectrum where spectrum_file.l_spectrumid = spectrum.spectrumid and spectrum.l_projectid = " + projectNumber + "  order by l_spectrumid ");
        spectrum_fileResultset = spectrum_fileStatement.executeQuery();

        // first we fetch all the lcrun keys that are associated with a project
        PreparedStatement lcrunkeyStatement = inconn.prepareStatement("select lcrunid from lcrun where l_projectid = " + projectNumber + " order by lcrunid");
        ResultSet lcrunFetchedKeySet = lcrunkeyStatement.executeQuery();


        //per lcrun id in the original database we select and insert every spectrumid associated with it and everything associated with that spectrumid
        ResultSet spectrumInsertedKeysResultSet;
        while (lcrunFetchedKeySet.next()) {

            //fetch all the spectra to be inserted per lcrun
            lcrunFetchedKey = lcrunFetchedKeySet.getInt(1);
            PreparedStatement spectrumStatement = inconn.prepareStatement("select l_fragmentationid,l_instrumentid,searched,identified,filename,charge,mass_to_charge,total_spectrum_intensity,highest_peak_in_spectrum,username,creationdate,modificationdate from spectrum where l_projectid  = " + projectNumber + " and l_lcrunid = " + lcrunFetchedKey + " order by spectrumid ");
            spectrumResultset = spectrumStatement.executeQuery();
            spectrumMetadata = spectrumResultset.getMetaData();
            insertInt = spectrumMetadata.getColumnCount();
            spectrumTableString = "l_lcrunid,l_projectid";
            //we get the column names from the result of the query we did so we can create an insert into
            for (int i = 0; i < insertInt; i++) {
                columnString = spectrumMetadata.getColumnName(i + 1);
                spectrumTableString = spectrumTableString + "," + columnString;
            }
            if (lcrunkeysiter.hasNext()) {
                lcrunGeneratedKey = lcrunkeysiter.next();
            }
            while (spectrumResultset.next()) {
                spectrumInsertString = String.valueOf(lcrunGeneratedKey) + "," + newProjectNumber;
                if (batchCounter == 500) {
                    //execute the batch if batch is the size indicated
                    stmtout.executeBatch();
                    batchCounter = 0;
                }
                //for each row we create the values part of the insert

                for (int i = 0; i < insertInt; i++) {
                    rowResult = spectrumResultset.getObject(i + 1);
                    spectrumInsertString = spectrumInsertString + ",'" + rowResult + "'";
                }

                //add to the batch
                stmtout.addBatch("insert into spectrum (" + spectrumTableString + ") values (" + spectrumInsertString + ")");
                batchCounter++;
            }
            if (batchCounter != 0) {
                //execute leftover which is less than buffer
                stmtout.executeBatch();
            }
            txtAreaCopyOutput.append("copying scans and spectrum files \n");
            //after inserting the spectra we insert the files and scans associated with it
            spectrumInsertedKeysResultSet = stmtout.executeQuery("select spectrumid from spectrum where l_projectid = " + newProjectNumber + " and l_lcrunid = " + lcrunGeneratedKey + " order by spectrumid");
            batchCounter = 0;

            //get everything from scan
            spectrumInsertedkeysList.clear();
            while (spectrumInsertedKeysResultSet.next()) {

                spectrumInsertedkeysList.add(spectrumInsertedKeysResultSet.getInt(1));
            }

            spectrumInsertedkeysIter = spectrumInsertedkeysList.iterator();

            while (spectrumInsertedkeysIter.hasNext()) {
                rowInsertString = "";
                if (batchCounter == 500) {
                    stmtout.executeBatch();
                    spectrumfileInsertStatement.executeBatch();
                    batchCounter = 0;
                }
                spectrumInstertedKey = spectrumInsertedkeysIter.next();
                scanResultset.next();
                    rowInsertString = "";
                //batch for scan
                if(!emptyResultSet){
                    for (int j = 0; j < scanrowint; j++) {
                        rowResult = scanResultset.getObject(j + 1);
                        if (rowResult != null){
                            rowInsertString = rowInsertString + ",'" + rowResult + "'";
                        } else {
                            rowInsertString = rowInsertString + ",NULL";
                        }
                    }
                    stmtout.addBatch("insert into scan (l_spectrumid,number,rtsec,creationdate,modificationdate) values (" + spectrumInstertedKey + rowInsertString + ",CURRENT_TIMESTAMP,CURRENT_TIMESTAMP ) ");
                }
                //next we insert into spectrum_file
                //create inserts for every spectrum id we generated
                spectrum_fileResultset.next();
                spfBlob = spectrum_fileResultset.getBlob(1);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];

                InputStream in = spfBlob.getBinaryStream();

                int n;
                while ((n = in.read(buf)) >= 0) {
                    baos.write(buf, 0, n);
                }

                in.close();
                byte[] bytes = baos.toByteArray();
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                spectrumfileInsertStatement.setInt(1, spectrumInstertedKey);
                spectrumfileInsertStatement.setBinaryStream(2,bais,bytes.length);
                spectrumfileInsertStatement.addBatch();
                batchCounter++;
                bais.close();
                baos.close();
            }
            if (batchCounter != 0) {
                stmtout.executeBatch();
                spectrumfileInsertStatement.executeBatch();
            }
        }
        spectrum_fileResultset.close();
        scanStatement.close();
        spectrum_fileStatement.close();
        spectrumResultset.close();
        spectrumMetadata = null;
        spectrumInsertedkeysList.clear();
        spfBlob = null;

        spectrumResultset = stmtout.executeQuery("select spectrumid from spectrum where l_projectid = " + newProjectNumber + " order by spectrumid");

        while (spectrumResultset.next()) {
            spectrumInsertedkeysList.add(spectrumResultset.getInt(1));
        }
        spectrumfileInsertStatement.close();
        lcrunFetchedKeySet.close();
        lcrunkeyStatement.close();
        spectrumResultset.close();
        return spectrumInsertedkeysList;
    }

    private ArrayList<Integer> copyDatfile(PreparedStatement datfilestatement) throws SQLException, IOException {
        ByteArrayInputStream bais;
        ArrayList<Integer> datfileKeys = new ArrayList<Integer>();
        ResultSet datfileGeneratedKey;
        ResultSet datfileResultset = datfilestatement.executeQuery();
        PreparedStatement preparedDatcopyStatement = outconn.prepareStatement("insert into datfile (filename,file,server,folder,username,creationdate,modificationdate) values (?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
        try{
            while (datfileResultset.next()) {
                txtAreaCopyOutput.append("copying dat file \n");
                //impossible to do this in batch because of the size of datfiles
                Blob tempblob = datfileResultset.getBlob(2);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];

                InputStream in = tempblob.getBinaryStream();

                int n;
                while ((n = in.read(buf)) >= 0) {
                    baos.write(buf, 0, n);
                }

                in.close();
                byte[] bytes = baos.toByteArray();
                bais = new ByteArrayInputStream(bytes);

                preparedDatcopyStatement.setString(1, datfileResultset.getString(1));
                preparedDatcopyStatement.setBinaryStream(2, bais, bytes.length);
                preparedDatcopyStatement.setString(3, datfileResultset.getString(3));
                preparedDatcopyStatement.setString(4, datfileResultset.getString(4));
                preparedDatcopyStatement.setString(5, datfileResultset.getString(5));
                preparedDatcopyStatement.executeUpdate();
                bais.close();
                datfileGeneratedKey = preparedDatcopyStatement.getGeneratedKeys();
                datfileGeneratedKey.next();
                datfileKeys.add(datfileGeneratedKey.getInt(1));
            }
        } catch (SQLException sqle){
            sqle.printStackTrace();
        }
        datfileResultset.close();
        datfilestatement.close();
        return datfileKeys;
    }


    private void copyIdentification(PreparedStatement identificationstatement, ArrayList<Integer> spectrumkeys, ArrayList<Integer> datfilekeys) throws SQLException {
        txtAreaCopyOutput.append("copying identifications \n");
        ResultSet identificationResultset = identificationstatement.executeQuery();
        ResultSetMetaData identificationMetadata = identificationResultset.getMetaData();
        ResultSet newidentificationkey;
        oldnewidentificationkeys = new HashMap<Integer, Integer>();
        insertInt = identificationMetadata.getColumnCount();
        Iterator<Integer> datfileIter = datfilekeys.iterator();
        Iterator<Integer> spectrumKeysIter = spectrumkeys.iterator();
        PreparedStatement identificationInsertStatement = outconn.prepareStatement("INSERT INTO identification (l_spectrumid, l_datfileid, datfile_query, accession, start, end, enzymatic, sequence, modified_sequence, ion_coverage, score, homology, exp_mass, cal_mass, light_isotope, heavy_isotope, valid, Description, identitythreshold, confidence, DB, title, precursor, charge, isoforms, db_filename, mascot_version, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        batchCounter = 0;
        int datfileidChecker = 0;
        int datfileCurrent = 0;
        identificationMetadata.getColumnCount();

        while (identificationResultset.next()) {

            identificationInsertStatement.setInt(1, spectrumKeysIter.next());


            if (datfileidChecker == identificationResultset.getInt(2)) {
                identificationInsertStatement.setInt(2, datfileCurrent);
            } else {
                datfileidChecker = identificationResultset.getInt(2);
                datfileCurrent = datfileIter.next();
                identificationInsertStatement.setInt(2, datfileCurrent);
            }

            for (int i = 2; i < insertInt; i++) {
                identificationInsertStatement.setObject(i + 1, identificationResultset.getObject(i + 1));
            }
            try {
                identificationInsertStatement.executeUpdate();
            } catch(SQLException sqle) {
               sqle.printStackTrace();
            }
            newidentificationkey = identificationInsertStatement.getGeneratedKeys();
            newidentificationkey.next();
            oldnewidentificationkeys.put(identificationResultset.getInt(1), newidentificationkey.getInt(1));
        }
        spectrumKeysIter = null;
        identificationstatement.close();
        identificationInsertStatement.close();
        identificationResultset.close();
    }


    private void copyValidation(PreparedStatement validationstatement, ArrayList<Integer> identificationKeys) throws SQLException {
        txtAreaCopyOutput.append("copying validations \n");
        ResultSet validationResultset = validationstatement.executeQuery();
        ResultSetMetaData validationMetadata = validationResultset.getMetaData();
        insertInt = validationMetadata.getColumnCount();
        batchCounter = 0;
        PreparedStatement validationInsertStatement = outconn.prepareStatement("INSERT INTO validation (l_identificationid, l_validationtypeid, username, auto_comment, manual_comment, creationdate, modificationdate) values (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        Iterator<Integer> identificationKeysIter = identificationKeys.iterator();

        while (validationResultset.next()) {
            if (batchCounter == 500) {
                validationInsertStatement.executeBatch();
                batchCounter = 0;
            }

            validationInsertStatement.setInt(1, identificationKeysIter.next());

            for (int i = 0; i < insertInt; i++) {
                validationInsertStatement.setObject(i + 2, validationResultset.getObject(i + 1));
            }
            validationInsertStatement.addBatch();
            batchCounter++;

        }
        if (batchCounter != 0) {
            //execute leftover which is less than buffer
            validationInsertStatement.executeBatch();
        }
        identificationKeysIter = null;
        validationstatement.close();
        validationInsertStatement.close();
        validationResultset.close();
    }


    private void copyFragmention(PreparedStatement fragmentionstatement, ArrayList<Integer> identificationKeys) throws SQLException {
        txtAreaCopyOutput.append("copying fragmention \n");
        ResultSet fragmentionResultset = fragmentionstatement.executeQuery();
        ResultSetMetaData validationMetadata = fragmentionResultset.getMetaData();
        insertInt = validationMetadata.getColumnCount();
        batchCounter = 0;
        int fragmentionChecker = 0;
        int fragmentionCurrent = 0;
        PreparedStatement fragmentionInsertStatement = outconn.prepareStatement("insert into fragmention (l_identificationid, iontype,ionname,l_ionscoringid,mz,intensity,fragmentionnumber,massdelta,masserrormargin,username,creationdate,modificationdate) values (?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        Iterator<Integer> identificationKeysIter = identificationKeys.iterator();

        while (fragmentionResultset.next()) {
            if (batchCounter == 500) {
                fragmentionInsertStatement.executeBatch();
                batchCounter = 0;
            }

            if (fragmentionChecker == fragmentionResultset.getInt(1)) {
                fragmentionInsertStatement.setInt(1, fragmentionCurrent);
            } else {
                fragmentionChecker = fragmentionResultset.getInt(1);
                fragmentionCurrent = identificationKeysIter.next();
                fragmentionInsertStatement.setInt(1, fragmentionCurrent);
            }

            for (int i = 1; i < insertInt; i++) {
                fragmentionInsertStatement.setObject(i + 1, fragmentionResultset.getObject(i + 1));
            }
            fragmentionInsertStatement.addBatch();
            batchCounter++;
        }
        if (batchCounter != 0) {
            //execute leftover which is less than buffer
            fragmentionInsertStatement.executeBatch();
        }
        fragmentionInsertStatement.close();
        identificationKeysIter = null;
        fragmentionstatement.close();
        fragmentionResultset.close();
    }

    private ArrayList<Integer> copyQuantitationFile(PreparedStatement quantitationfilestatement) throws SQLException {
        int selectid;
        ByteArrayInputStream bais;
        ResultSet quantitationFileIdResultset;
        ArrayList<Integer> quantitationFileIds = new ArrayList<Integer>();
        txtAreaCopyOutput.append("fetching quantitation files \n");
        try{
            ResultSet quantitationfileResultset = quantitationfilestatement.executeQuery();
            ArrayList<Integer> selectids = new ArrayList<Integer>();
            while (quantitationfileResultset.next()){
                selectids.add(quantitationfileResultset.getInt(1));
            }
            PreparedStatement quantitationfilePreparedStatement = outconn.prepareStatement("insert into quantitation_file (filename,type,file,binary_file,version_number,username,creationdate,modificationdate) values (?,?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
            Iterator<Integer> selectidsiter = selectids.iterator();
            while (selectidsiter.hasNext()) {
                selectid = selectidsiter.next();
                txtAreaCopyOutput.append("copying quantitation file \n");
                PreparedStatement quantitationfilefetcher = inconn.prepareStatement("select f.filename,f.type,f.file,f.binary_file,f.version_number,f.username from quantitation_file as f where f.quantitation_fileid = "+selectid );
                quantitationfileResultset = quantitationfilefetcher.executeQuery();
                quantitationfileResultset.next();
                quantitationfilePreparedStatement.setString(1, quantitationfileResultset.getString(1));
                quantitationfilePreparedStatement.setString(2, quantitationfileResultset.getString(2));
                Blob tempblob = quantitationfileResultset.getBlob(3);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];

                InputStream in = tempblob.getBinaryStream();

                int n;
                while ((n = in.read(buf)) >= 0) {
                    baos.write(buf, 0, n);
                }

                in.close();
                byte[] bytes = baos.toByteArray();
                bais = new ByteArrayInputStream(bytes);
                quantitationfilePreparedStatement.setBinaryStream(3,bais,bytes.length);
                baos = new ByteArrayOutputStream();
                buf = new byte[1024];

                in = tempblob.getBinaryStream();


                while ((n = in.read(buf)) >= 0) {
                    baos.write(buf, 0, n);
                }

                in.close();
                bytes = baos.toByteArray();
                bais = new ByteArrayInputStream(bytes);
                quantitationfilePreparedStatement.setBinaryStream(4, bais,bytes.length);
                quantitationfilePreparedStatement.setString(5, quantitationfileResultset.getString(5));
                quantitationfilePreparedStatement.setString(6, quantitationfileResultset.getString(6));
                quantitationfilePreparedStatement.executeUpdate();
                quantitationFileIdResultset = quantitationfilePreparedStatement.getGeneratedKeys();
                quantitationFileIdResultset.next();
                quantitationFileIds.add(quantitationFileIdResultset.getInt(1));
            }
            }catch(SQLException sqle) {
            sqle.printStackTrace();
            } catch (IOException e) {
            e.printStackTrace();
        }
        quantitationfilestatement.close();
        return quantitationFileIds;
    }


    private ArrayList<Integer> copyQuantitationgroup(PreparedStatement quantitationgroupstatement, ArrayList<Integer> quantitationfilekeys) throws SQLException {
        txtAreaCopyOutput.append("copying quantitation group \n");
        ResultSet quantitationgroupInsertedIds;
        ResultSet quantitationgroupResultset = quantitationgroupstatement.executeQuery();
        ResultSetMetaData validationMetadata = quantitationgroupResultset.getMetaData();
        insertInt = validationMetadata.getColumnCount();
        int quantitationfileIdInserter = -1;
        int quantitationfileChecker = -1;
        Iterator<Integer> quantitationfileIter = quantitationfilekeys.iterator();

        ArrayList<Integer> quantitationgroupInsertedIdsList = new ArrayList<Integer>();
        try{
            while (quantitationgroupResultset.next()) {
                rowInsertString = "";
                for (int i = 1; i < insertInt; i++) {
                    rowResult = quantitationgroupResultset.getObject(i + 1);
                    rowInsertString = rowInsertString + ",'" + rowResult + "'";
                }

                if (quantitationgroupResultset.getInt(1) == quantitationfileChecker) {
                    rowInsertString = quantitationfileIdInserter + rowInsertString;
                } else {
                    quantitationfileChecker = quantitationgroupResultset.getInt(1);
                    quantitationfileIdInserter = quantitationfileIter.next();
                    rowInsertString = quantitationfileIdInserter + rowInsertString;
                }
                stmtout.executeUpdate("insert into quantitation_group (l_quantitation_fileid,file_ref,username,creationdate,modificationdate) values (" + rowInsertString + ",CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
                quantitationgroupInsertedIds = stmtout.getGeneratedKeys();
                while (quantitationgroupInsertedIds.next()) {
                    quantitationgroupInsertedIdsList.add(quantitationgroupInsertedIds.getInt(1));
                }
            }
        } catch (SQLException sqle) {
                sqle.printStackTrace();
        }
        quantitationfileIter = null;
        quantitationgroupResultset.close();
        quantitationgroupstatement.close();
        return quantitationgroupInsertedIdsList;
    }


    private void copyIdentificationToQuantitation(PreparedStatement identificationtoquantitation, HashMap<Integer, Integer> identificationkeys, ArrayList<Integer> quantitationgroupKeys) throws SQLException {
        txtAreaCopyOutput.append("copying identification to quantitation \n");
        batchCounter = 0;
        ResultSet identificationToQuantitationResultset = identificationtoquantitation.executeQuery();
        Iterator<Integer> quantitationgroupKeysIter = quantitationgroupKeys.iterator();
        int identificationtoquantitationChecker = -1;
        int identificationtoquantitationIdInserter = -1;
        try{
            while (identificationToQuantitationResultset.next()) {
                if (batchCounter == 500) {
                    stmtout.executeBatch();
                    batchCounter = 0;
                }
                rowInsertString = "";
                for (int i = 2; i < 4; i++) {
                    rowResult = identificationToQuantitationResultset.getObject(i + 1);
                    rowInsertString = rowInsertString + ",'" + rowResult + "'";
                }
                if (identificationToQuantitationResultset.getInt(2) == identificationtoquantitationChecker) {
                    rowInsertString = identificationtoquantitationIdInserter + rowInsertString;
                } else {
                    identificationtoquantitationChecker = identificationToQuantitationResultset.getInt(2);
                    identificationtoquantitationIdInserter = quantitationgroupKeysIter.next();
                    rowInsertString = identificationtoquantitationIdInserter + rowInsertString;
                }

                rowInsertString = identificationkeys.get(identificationToQuantitationResultset.getInt(1)) + "," + rowInsertString;
                stmtout.addBatch("insert into identification_to_quantitation (l_identificationid,l_quantitation_groupid,type,username,creationdate,modificationdate) values (" + rowInsertString + ",CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
                batchCounter++;
            }
            if (batchCounter != 0) {
                stmtout.executeBatch();
            }
            identificationtoquantitation.close();
            identificationToQuantitationResultset.close();
            quantitationgroupKeysIter = null;
        } catch (Exception e ) {
            e.printStackTrace();
        }
    }


    private void copyQuantitation(PreparedStatement quantitationstatement, ArrayList<Integer> quantitationgroupKeys) throws SQLException {
        txtAreaCopyOutput.append("copying quantitation \n");
        batchCounter = 0;
        int quantitationChecker = -1;
        int quantitationIdInserter = -1;
        ResultSet quantitationResultset = quantitationstatement.executeQuery();
        ResultSetMetaData quantitationMetadata = quantitationResultset.getMetaData();
        insertInt = quantitationMetadata.getColumnCount();
        Iterator<Integer> quantitationgroupKeysIter = quantitationgroupKeys.iterator();
        PreparedStatement quantitationPreparedStatement = outconn.prepareStatement("insert into quantitation (l_quantitation_groupid,ratio,standard_error,type,valid,comment,username,creationdate,modificationdate) values (?,?,?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");

        while (quantitationResultset.next()) {
            if (batchCounter == 500) {
                quantitationPreparedStatement.executeBatch();
                batchCounter = 0;
            }
            rowInsertString = "";
            for (int i = 1; i < insertInt; i++) {
                quantitationPreparedStatement.setObject(i + 1,quantitationResultset.getObject(i + 1));
            }
            if (quantitationResultset.getInt(1) == quantitationChecker) {
                quantitationPreparedStatement.setObject(1,quantitationIdInserter);
            } else {
                quantitationChecker = quantitationResultset.getInt(1);
                quantitationIdInserter = quantitationgroupKeysIter.next();
                quantitationPreparedStatement.setObject(1,quantitationIdInserter);
            }
            quantitationPreparedStatement.addBatch();
            batchCounter++;
        }
        if (batchCounter != 0) {
            //execute leftover which is less than buffer
            quantitationPreparedStatement.executeBatch();
        }
        quantitationgroupKeysIter = null;
        quantitationPreparedStatement.close();
        quantitationstatement.close();
        quantitationResultset.close();
    }

private SwingWorker copyWorker = new SwingWorker<Void, Void>() {


            @Override
            protected Void doInBackground() throws SQLException, IOException {
                try{
                       int newProjectNumber = copyProject(inprep1);

        ResultSet lcrunInsertedKeysResultSet = copyLcrun(inprep2, newProjectNumber);

        ArrayList<Integer> spectrumkeys = copySpectrum(lcrunInsertedKeysResultSet, newProjectNumber);

                lcrunInsertedKeysResultSet = null;

        ArrayList<Integer> returnedDatfileKeys = copyDatfile(inprep3);
                    copyIdentification(inprep4, spectrumkeys, returnedDatfileKeys);

        spectrumkeys = null;
        returnedDatfileKeys = null;
        ResultSet identificationidResultSet = stmtout.executeQuery("select identificationid from identification,spectrum where spectrumid = l_spectrumid and l_projectid = " + newProjectNumber);

        ArrayList<Integer> identitykeyList = new ArrayList<Integer>();
        while (identificationidResultSet.next()) {
            identitykeyList.add(identificationidResultSet.getInt(1));
        }

        copyValidation(inprep6, identitykeyList);

        copyFragmention(inprep5, identitykeyList);

        identitykeyList = null;

        ArrayList<Integer> quantitationfilekeys = copyQuantitationFile(inprep7);

        ArrayList<Integer> quantitationgroupkeys = copyQuantitationgroup(inprep8, quantitationfilekeys);

        quantitationfilekeys = null;

        copyIdentificationToQuantitation(inprep10, oldnewidentificationkeys, quantitationgroupkeys);

        copyQuantitation(inprep9, quantitationgroupkeys);

                quantitationgroupkeys = null;

                JOptionPane.showMessageDialog(CopyProject.this,"done copying");

                btnContinue.setEnabled(true);
                copyProgressBar.setMaximum(1);
                copyProgressBar.setValue(1);
                copyProgressBar.setIndeterminate(false);
                System.gc();
            } catch(SQLException sqle)
        {
           sqle.printStackTrace();
        }
            return null;}
        };

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
            java.util.logging.Logger.getLogger(CopyProject.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CopyProject.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CopyProject.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CopyProject.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
    }
      private class executeCopy extends Thread {

        public void startCopyWorker()
        {
            copyWorker.execute();
        }
    }
}