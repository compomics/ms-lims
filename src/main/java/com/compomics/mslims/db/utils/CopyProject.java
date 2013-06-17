package com.compomics.mslimsdb.utils;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA. User: Davy Date: 4/10/11 Time: 9:35
 */
public class CopyProject extends JFrame {

    private static final Logger logger = Logger.getLogger(CopyProject.class);
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
    private int insertInt;
    private HashMap<Integer, Integer> oldnewidentificationkeys = new HashMap<Integer, Integer>();
    private PreparedStatement inprep1;
    private PreparedStatement inprep2;
    private PreparedStatement inprep7;
    private PreparedStatement inprep8;
    private PreparedStatement inprep9;
    private PreparedStatement inprep10;
    private JTextArea txtAreaCopyOutput;
    private JProgressBar copyProgressBar;
    private JScrollPane scrollPaneCopy;
    private JButton btnContinue;
    private HashMap<Integer, Integer> oldSpectrumIdsNewSpectrumIds = new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> oldDatFileIdsNewDatFileIds = new HashMap<Integer, Integer>();

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

        inprep7 = inconn.prepareStatement("select distinct q.l_quantitation_fileid from identification as i, spectrum as f , identification_to_quantitation as t, quantitation_group as q where i.l_spectrumid = f.spectrumid and f.l_projectid = " + projectNumber + " and i.identificationid = t.l_identificationid and t.l_quantitation_groupid = q.quantitation_groupid order by q.l_quantitation_fileid");

        inprep8 = inconn.prepareStatement("select g.l_quantitation_fileid, g.file_ref, g.username from quantitation_group as g,(select distinct t.l_quantitation_groupid "
                + " as res from identification_to_quantitation as t , identification as i, spectrum as s where i.l_spectrumid = s.spectrumid and t.l_identificationid = i.identificationid and s.l_projectid = " + projectNumber + " order by l_quantitation_groupid) as result where "
                + "g.quantitation_groupid = result.res order by l_quantitation_fileid");

        inprep10 = inconn.prepareStatement("select t.l_identificationid,t.l_quantitation_groupid,t.type,t.username from identification_to_quantitation as t , identification as i, spectrum as s where i.l_spectrumid = s.spectrumid and t.l_identificationid = i.identificationid and s.l_projectid =" + projectNumber + " order by l_quantitation_groupid");

        inprep9 = inconn.prepareStatement("select q.l_quantitation_groupid,q.ratio,q.standard_error,q.type,q.valid,q.comment,q.username from quantitation_group as g, quantitation as q, identification_to_quantitation as t , identification as i, spectrum as s where"
                + " i.l_spectrumid = s.spectrumid and t.l_identificationid = i.identificationid and g.quantitation_groupid = t. l_quantitation_groupid"
                + " and q.l_quantitation_groupid = g.quantitation_groupid and s.l_projectid =  " + projectNumber + " group by q.quantitationid order by q.quantitationid");


        ExecuteCopy copyExecutor = new ExecuteCopy();
        copyExecutor.startCopyWorker();
    }

    private void initComponents() {

        copyProgressBar = new JProgressBar();
        scrollPaneCopy = new JScrollPane();
        txtAreaCopyOutput = new JTextArea();
        btnContinue = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

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
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(copyProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scrollPaneCopy, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnContinue)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        DefaultCaret caret = (DefaultCaret) txtAreaCopyOutput.getCaret();
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
        return stmtout.executeQuery("select lcrunid from lcrun where l_projectid =" + newProjectNumber + " order by lcrunid");
    }

    private ArrayList<Integer> copySpectrum(ResultSet lcrunKeyResultset, int newProjectNumber) throws SQLException, FileNotFoundException {
        txtAreaCopyOutput.append("copying spectrum \n");
        PreparedStatement spectrumfileInsertStatement = outconn.prepareStatement("insert into spectrum_file (l_spectrumid,file) values (?,?)");

        ArrayList<Integer> lcrunkeys = new ArrayList<Integer>();
        ArrayList<Integer> spectrumInsertedkeysList = new ArrayList<Integer>();
        Iterator<Integer> spectrumInsertedkeysIter;
        int spectrumInstertedKey;

        //add all the lcrun keys to a list because mysql statements can only hold one resultset
        while (lcrunKeyResultset.next()) {
            lcrunkeys.add(lcrunKeyResultset.getInt(1));
        }
        Iterator<Integer> lcrunkeysiter = lcrunkeys.iterator();

        int lcrunFetchedKey;
        int lcrunGeneratedKey = 0;
        batchCounter = 0;
        String spectrumInsertString;
        String spectrumTableString;

        // first we fetch all the lcrun keys that are associated with a project
        PreparedStatement lcrunkeyStatement = inconn.prepareStatement("select lcrunid from lcrun where l_projectid = " + projectNumber + " order by lcrunid");
        ResultSet lcrunFetchedKeySet = lcrunkeyStatement.executeQuery();


        //per lcrun id in the original database we select and insert every spectrumid associated with it and everything associated with that spectrumid
        ResultSet spectrumInsertedKeysResultSet;
        while (lcrunFetchedKeySet.next()) {

            //fetch all the spectrums to be inserted per lcrun
            lcrunFetchedKey = lcrunFetchedKeySet.getInt(1);
            PreparedStatement spectrumStatement = inconn.prepareStatement("select spectrumid,l_fragmentationid,l_instrumentid,searched,identified,filename,charge,mass_to_charge,total_spectrum_intensity,highest_peak_in_spectrum,username,creationdate,modificationdate from spectrum where l_projectid  = " + projectNumber + " and l_lcrunid = " + lcrunFetchedKey + " order by spectrumid ");
            spectrumResultset = spectrumStatement.executeQuery();
            spectrumMetadata = spectrumResultset.getMetaData();
            insertInt = spectrumMetadata.getColumnCount();
            spectrumTableString = "l_lcrunid,l_projectid";
            //we get the column names from the result of the query we did so we can create an insert into
            for (int i = 1; i < insertInt; i++) {
                columnString = spectrumMetadata.getColumnName(i + 1);
                spectrumTableString = spectrumTableString + "," + columnString;
            }
            if (lcrunkeysiter.hasNext()) {
                lcrunGeneratedKey = lcrunkeysiter.next();
            }
            while (spectrumResultset.next()) {
                spectrumInsertString = String.valueOf(lcrunGeneratedKey) + "," + newProjectNumber;

                //for each row we create the values part of the insert

                for (int i = 1; i < insertInt; i++) {
                    rowResult = spectrumResultset.getObject(i + 1);
                    spectrumInsertString = spectrumInsertString + ",'" + rowResult + "'";
                }

                //add to the batch
                stmtout.execute("insert into spectrum (" + spectrumTableString + ") values (" + spectrumInsertString + ")", Statement.RETURN_GENERATED_KEYS);
                ResultSet newSpectrumKeys = stmtout.getGeneratedKeys();
                newSpectrumKeys.next();
                oldSpectrumIdsNewSpectrumIds.put(spectrumResultset.getInt(1), newSpectrumKeys.getInt(1));
            }

            txtAreaCopyOutput.append("coyping scans and spectrum files \n");
            //after inserting the spectra we insert the files and scans associated with it
            spectrumInsertedKeysResultSet = stmtout.executeQuery("select spectrumid from spectrum where l_projectid = " + newProjectNumber + " and l_projectid = " + lcrunGeneratedKey + " order by spectrumid");
            batchCounter = 0;

            //get everything from scan
            PreparedStatement scanStatement = inconn.prepareStatement("select scan.number,scan.rtsec from scan,spectrum where scan.l_spectrumid = spectrum.spectrumid and spectrum.l_projectid = " + projectNumber + " order by l_spectrumid");
            scanResultset = scanStatement.executeQuery();
            ResultSetMetaData scanmetadata = scanResultset.getMetaData();
            int scanrowint = scanmetadata.getColumnCount();

            //get everything from spectrum_file
            PreparedStatement spectrum_fileStatement = inconn.prepareStatement("select file from spectrum_file,spectrum where spectrum_file.l_spectrumid = spectrum.spectrumid and spectrum.l_projectid = " + projectNumber + "  order by l_spectrumid");
            spectrum_fileResultset = spectrum_fileStatement.executeQuery();
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
                //batch for scan
                for (int j = 0; j < scanrowint; j++) {
                    rowResult = scanResultset.getObject(j + 1);
                    rowInsertString = rowInsertString + ",'" + rowResult + "'";
                }
                this.stmtout.addBatch("insert into scan (l_spectrumid,number,rtsec,creationdate,modificationdate) values (" + spectrumInstertedKey + rowInsertString + ",CURRENT_TIMESTAMP,CURRENT_TIMESTAMP )");

                //next we insert into spectrum_file
                //create inserts for every spectrum id we generated
                spectrum_fileResultset.next();
                spectrumfileInsertStatement.setInt(1, spectrumInstertedKey);
                spectrumfileInsertStatement.setBlob(2, spectrum_fileResultset.getBlob(1));
                spectrumfileInsertStatement.addBatch();
                batchCounter++;
            }
            if (batchCounter != 0) {
                this.stmtout.executeBatch();
                spectrumfileInsertStatement.executeBatch();
            }
            spectrum_fileResultset.close();
            spectrumStatement.close();
        }
        scanResultset.close();
        spectrumResultset.close();
        spectrumMetadata = null;
        spectrumInsertedkeysList.clear();

        spectrumResultset = stmtout.executeQuery("select spectrumid from spectrum where l_projectid = " + newProjectNumber + " order by spectrumid");

        while (spectrumResultset.next()) {
            spectrumInsertedkeysList.add(spectrumResultset.getInt(1));
        }
        spectrumfileInsertStatement.close();
        lcrunFetchedKeySet.close();
        spectrumResultset.close();
        lcrunkeyStatement.close();
        return spectrumInsertedkeysList;
    }

    private void copyDatfile() throws SQLException, IOException {
        ByteArrayInputStream bais;
        ResultSet datfileGeneratedKey;
        PreparedStatement datFileSelectStatement = inconn.prepareStatement("select d.datfileid,d.filename,d.file,d.server,d.folder,d.username from datfile as d join (select distinct l_datfileid as result from identification as i, spectrum as s where s.spectrumid = i.l_spectrumid and s.l_projectid = " + projectNumber + ") as r on r.result = d.datfileid group by d.datfileid order by d.datfileid");
        ResultSet datfileResultset = datFileSelectStatement.executeQuery();
        PreparedStatement preparedDatcopyStatement = outconn.prepareStatement("insert into datfile (filename,file,server,folder,username,creationdate,modificationdate) values (?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
        while (datfileResultset.next()) {
            txtAreaCopyOutput.append("copying dat file \n");
            //impossible to do this in batch because of the huge size of datfiles
            Blob tempblob = datfileResultset.getBlob(3);

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

            preparedDatcopyStatement.setString(1, datfileResultset.getString(2));
            preparedDatcopyStatement.setBinaryStream(2, bais, bytes.length);
            preparedDatcopyStatement.setString(3, datfileResultset.getString(4));
            preparedDatcopyStatement.setString(4, datfileResultset.getString(5));
            preparedDatcopyStatement.setString(5, datfileResultset.getString(6));
            preparedDatcopyStatement.executeUpdate();
            bais.close();
            datfileGeneratedKey = preparedDatcopyStatement.getGeneratedKeys();
            datfileGeneratedKey.next();
            oldDatFileIdsNewDatFileIds.put(datfileResultset.getInt(1), datfileGeneratedKey.getInt(1));
        }

        preparedDatcopyStatement.close();
        datfileResultset.close();
    }

    private int insertIdentification(ResultSet rs, int columnCount, int newSpectrumId, int newDatFileId) throws SQLException {
        PreparedStatement identificationInsertStatement = outconn.prepareStatement("INSERT INTO identification (l_spectrumid, l_datfileid, datfile_query, accession, start, end, enzymatic, sequence, modified_sequence, ion_coverage, score, homology, exp_mass, cal_mass, light_isotope, heavy_isotope, valid, Description, identitythreshold, confidence, DB, title, precursor, charge, isoforms, db_filename, mascot_version, username, creationdate, modificationdate) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);

        identificationInsertStatement.setInt(1, newSpectrumId);
        identificationInsertStatement.setInt(2, newDatFileId);

        for (int i = 3; i < columnCount; i++) {
            identificationInsertStatement.setObject(i, rs.getObject(i + 1));
        }

        identificationInsertStatement.execute();
        ResultSet identificationGeneratedKeys = identificationInsertStatement.getGeneratedKeys();
        identificationGeneratedKeys.next();
        int newIdentificationId = identificationGeneratedKeys.getInt(1);
        identificationInsertStatement.close();
        identificationGeneratedKeys.close();
        return newIdentificationId;
    }

    private void insertFragmentIon(int oldIdentificationid, int newIdentificationid) throws SQLException {
        //TODO insert in batch together with some validations
        PreparedStatement fragmentionInsertStatement = outconn.prepareStatement("insert into fragmention (l_identificationid, iontype,ionname,l_ionscoringid,mz,intensity,fragmentionnumber,massdelta,masserrormargin,username,creationdate,modificationdate) values (?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
        PreparedStatement fragmentionSelectStatement = inconn.prepareStatement("select iontype,ionname,l_ionscoringid,mz,intensity,fragmentionnumber,massdelta,masserrormargin,username from fragmention where l_identificationid = ?");
        fragmentionSelectStatement.setInt(1, oldIdentificationid);
        ResultSet fragmentionResultset = fragmentionSelectStatement.executeQuery();
        try {
            while (fragmentionResultset.next()) {
                fragmentionInsertStatement.setInt(1, newIdentificationid);
                fragmentionInsertStatement.setInt(2, fragmentionResultset.getInt("iontype"));
                fragmentionInsertStatement.setString(3, fragmentionResultset.getString("ionname"));
                fragmentionInsertStatement.setInt(4, fragmentionResultset.getInt("l_ionscoringid"));
                fragmentionInsertStatement.setDouble(5, fragmentionResultset.getDouble("mz"));
                fragmentionInsertStatement.setInt(6, fragmentionResultset.getInt("intensity"));
                fragmentionInsertStatement.setInt(7, fragmentionResultset.getInt("fragmentionnumber"));
                fragmentionInsertStatement.setDouble(8, fragmentionResultset.getDouble("massdelta"));
                fragmentionInsertStatement.setDouble(9, fragmentionResultset.getDouble("masserrormargin"));
                fragmentionInsertStatement.setString(10, fragmentionResultset.getString("username"));
                fragmentionInsertStatement.execute();
            }
        } catch (SQLException sqle) {
            logger.error(sqle);
        }
        fragmentionResultset.close();
        fragmentionInsertStatement.close();
        fragmentionSelectStatement.close();
    }

    private void insertValidation(int oldIdentificationId, int newIdentificationid) throws SQLException {
        PreparedStatement getValidationForIdentification = inconn.prepareStatement("select l_validationtypeid,username,auto_comment,manual_comment from validation where l_identificationid = " + oldIdentificationId);
        PreparedStatement insertValidationstatement = outconn.prepareStatement("insert into validation (l_identificationid, l_validationtypeid, username, auto_comment, manual_comment, creationdate, modificationdate) values (?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
        ResultSet validationResultSet = getValidationForIdentification.executeQuery();

        while (validationResultSet.next()) {
            insertValidationstatement.setInt(1, newIdentificationid);
            insertValidationstatement.setInt(2, validationResultSet.getInt(1));
            insertValidationstatement.setString(3, validationResultSet.getString(2));
            insertValidationstatement.setString(4, validationResultSet.getString(3));
            insertValidationstatement.setString(5, validationResultSet.getString(4));
            insertValidationstatement.execute();
        }
        validationResultSet.close();
        insertValidationstatement.close();
        getValidationForIdentification.close();
    }

    private void copyIdentification() throws SQLException {
        PreparedStatement getIdentificationsForDatFileId = inconn.prepareStatement("select identificationid,l_spectrumid, l_datfileid, datfile_query, accession, start, end, enzymatic, sequence, modified_sequence, ion_coverage, score, homology, exp_mass, cal_mass, light_isotope, heavy_isotope, valid, Description, identitythreshold, confidence, DB, title, precursor, charge, isoforms, db_filename, mascot_version, username from identification where l_datfileid = ? order by l_spectrumid asc");

        for (Integer datFileKey : oldDatFileIdsNewDatFileIds.keySet()) {
            txtAreaCopyOutput.append("copying identifications for datfile: " + datFileKey + "\n");

            getIdentificationsForDatFileId.setInt(1, datFileKey);
            ResultSet identificationResult = getIdentificationsForDatFileId.executeQuery();
            //only needed for cross version support
            ResultSetMetaData identificationMetadata = identificationResult.getMetaData();
            int numberOfColums = identificationMetadata.getColumnCount();

            while (identificationResult.next()) {
                int newIdentificationId = insertIdentification(identificationResult, numberOfColums, oldSpectrumIdsNewSpectrumIds.get(identificationResult.getInt("l_spectrumid")), oldDatFileIdsNewDatFileIds.get(datFileKey));
                insertFragmentIon(identificationResult.getInt(1), newIdentificationId);
                insertValidation(identificationResult.getInt(1), newIdentificationId);
                oldnewidentificationkeys.put(identificationResult.getInt(1), newIdentificationId);
            }
            identificationResult.close();

        }
        getIdentificationsForDatFileId.close();
    }

    private ArrayList<Integer> copyQuantitationFile(PreparedStatement quantitationfilestatement) throws SQLException, IOException {
        int selectid;
        ByteArrayInputStream bais;
        ResultSet quantitationFileIdResultset;
        ArrayList<Integer> quantitationFileIds = new ArrayList<Integer>();
        txtAreaCopyOutput.append("fetching quantitation files \n");
        ResultSet quantitationfileResultset = quantitationfilestatement.executeQuery();
        ArrayList<Integer> selectids = new ArrayList<Integer>();
        while (quantitationfileResultset.next()) {
            selectids.add(quantitationfileResultset.getInt(1));
        }
        PreparedStatement quantitationfilePreparedStatement = outconn.prepareStatement("insert into quantitation_file (filename,type,file,username,creationdate,modificationdate) values (?,?,?,?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
        Iterator<Integer> selectidsiter = selectids.iterator();
        while (selectidsiter.hasNext()) {
            selectid = selectidsiter.next();
            txtAreaCopyOutput.append("copying quantitation file \n");
            PreparedStatement quantitationfilefetcher = inconn.prepareStatement("select f.filename,f.type,f.file,f.username from quantitation_file as f where f.quantitation_fileid = " + selectid + " order by f.quantitation_fileid");
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
            quantitationfilePreparedStatement.setBinaryStream(3, bais, bytes.length);
            quantitationfilePreparedStatement.setString(4, quantitationfileResultset.getString(4));
            quantitationfilePreparedStatement.executeUpdate();
            quantitationFileIdResultset = quantitationfilePreparedStatement.getGeneratedKeys();
            quantitationFileIdResultset.next();
            quantitationFileIds.add(quantitationFileIdResultset.getInt(1));
            quantitationfilefetcher.close();
        }
        quantitationfilePreparedStatement.close();
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
            stmtout.executeUpdate("insert into quantitation_group (l_quantitation_fileid,file_ref,username,creationdate,modificationdate) values (" + rowInsertString + ",CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)", Statement.RETURN_GENERATED_KEYS);
            quantitationgroupInsertedIds = stmtout.getGeneratedKeys();
            while (quantitationgroupInsertedIds.next()) {
                quantitationgroupInsertedIdsList.add(quantitationgroupInsertedIds.getInt(1));
            }
        }
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
                quantitationPreparedStatement.setObject(i + 1, quantitationResultset.getObject(i + 1));
            }
            if (quantitationResultset.getInt(1) == quantitationChecker) {
                quantitationPreparedStatement.setObject(1, quantitationIdInserter);
            } else {
                quantitationChecker = quantitationResultset.getInt(1);
                quantitationIdInserter = quantitationgroupKeysIter.next();
                quantitationPreparedStatement.setObject(1, quantitationIdInserter);
            }
            quantitationPreparedStatement.addBatch();
            batchCounter++;
        }
        if (batchCounter != 0) {
            //execute leftover which is less than buffer
            quantitationPreparedStatement.executeBatch();
        }
        quantitationPreparedStatement.close();
        quantitationResultset.close();
    }
    private SwingWorker copyWorker = new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() {
            try {
                int newProjectNumber = copyProject(inprep1);

                ResultSet lcrunInsertedKeysResultSet = copyLcrun(inprep2, newProjectNumber);

                copySpectrum(lcrunInsertedKeysResultSet, newProjectNumber);

                copyDatfile();

                copyIdentification();

                ArrayList<Integer> quantitationfilekeys = copyQuantitationFile(inprep7);

                ArrayList<Integer> quantitationgroupkeys = copyQuantitationgroup(inprep8, quantitationfilekeys);

                copyIdentificationToQuantitation(inprep10, oldnewidentificationkeys, quantitationgroupkeys);

                copyQuantitation(inprep9, quantitationgroupkeys);

                JOptionPane.showMessageDialog(CopyProject.this, "done copying");

                btnContinue.setEnabled(true);
                copyProgressBar.setMaximum(1);
                copyProgressBar.setValue(1);
                copyProgressBar.setIndeterminate(false);
                copyProgressBar.repaint();
            } catch (SQLException ex) {
                logger.error(ex);
            } catch (IOException ex) {
                logger.error(ex);
            }
            return null;
        }
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

    private class ExecuteCopy extends Thread {

        public void startCopyWorker() {
            copyWorker.execute();
        }
    }
}