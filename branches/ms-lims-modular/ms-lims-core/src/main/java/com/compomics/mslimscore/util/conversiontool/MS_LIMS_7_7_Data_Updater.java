package com.compomics.mslimscore.util.conversiontool;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Davy
 * Date: 3/8/12
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class MS_LIMS_7_7_Data_Updater {

    private static Logger logger = Logger.getLogger(MS_LIMS_7_7_Data_Updater.class);
    private Connection inConn;
    private int counter = 0;
    private ResultSet quantIdResultSet;
    private int quantfileid;
    JProgressBar progressBar;
    
    public MS_LIMS_7_7_Data_Updater(String aTitle,Connection aConn) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, InterruptedException {
        this.inConn = aConn;
        int nrOfProcessors = Runtime.getRuntime().availableProcessors();
        try{
            PreparedStatement changeScanTable = inConn.prepareStatement("alter table scan CHANGE number number int unsigned DEFAULT '0'");
            changeScanTable.executeUpdate();
            PreparedStatement changeQuantFileTable = inConn.prepareStatement("alter table quantitation_file ADD version_number varchar(15) after file");
            changeQuantFileTable.executeUpdate();
            changeQuantFileTable = inConn.prepareStatement("alter table quantitation_file ADD binary_file longblob after file");
            changeQuantFileTable.executeUpdate();
        } catch (SQLException sqle){
            logger.error("error in updating tables:" + sqle);
        }
        try{
            PreparedStatement changeQuantFileTable = inConn.prepareStatement("alter table quantitation_file ADD binary_file longblob after file");
            changeQuantFileTable.executeUpdate();
        } catch (SQLException sqle){
            logger.error("error in updating tables:" + sqle);
        }
        PreparedStatement getQuantids = inConn.prepareStatement("select quantitation_fileid from quantitation_file");
        ResultSet quantIdResultSet = getQuantids.executeQuery();
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("updating the database");
        progressBar.setStringPainted(true);
        JFrame frame = new JFrame(aTitle);
        frame.add(progressBar);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        while (quantIdResultSet.next()) {
            if (counter < nrOfProcessors) {
                counter++;
                PreparedStatement XMLFetcher = inConn.prepareStatement("select file from quantitation_file where quantitation_fileid = ?");
                quantfileid = quantIdResultSet.getInt(1);
                XMLFetcher.setInt(1,quantfileid);
                ResultSet rs = XMLFetcher.executeQuery();
                rs.next();
                updateWorker udw = new updateWorker();
                udw.construct(rs.getBlob(1),quantfileid);
                XMLFetcher.close();
                udw.execute();
            } else {
                while (counter >= nrOfProcessors) {
                    Thread.sleep(1000);
                }
                counter++;
                PreparedStatement XMLFetcher = inConn.prepareStatement("select file from quantitation_file where quantitation_fileid = ?");
                quantfileid = quantIdResultSet.getInt(1);
                XMLFetcher.setInt(1,quantfileid);
                ResultSet rs = XMLFetcher.executeQuery();
                rs.next();
                updateWorker udw = new updateWorker();
                udw.construct(rs.getBlob(1),quantfileid);
                XMLFetcher.close();
                udw.execute();
            }
        }
        progressBar.setIndeterminate(false);
        progressBar.setString("done updating");
    }

    class updateWorker extends SwingWorker<Void,ResultSet> {
        Blob blob;
        int quantfileid;
        public void construct (Blob blob,int quantfileid){
            this.blob = blob;
            this.quantfileid = quantfileid;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lVersionNumber = "";
            try{
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[2048];

                InputStream in = blob.getBinaryStream();

                int n;
                while ((n = in.read(buf)) >= 0) {
                    baos.write(buf, 0, n);
                }

                in.close();
                byte[] bytes = baos.toByteArray();

                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                baos = new ByteArrayOutputStream();
                BufferedInputStream bis = new BufferedInputStream(new GZIPInputStream(bais));
                int read = -1;
                while ((read = bis.read()) != -1){
                    baos.write(read);
                }
                bytes = baos.toByteArray();
                bais = new ByteArrayInputStream(bytes);
                String lLine = "";

                StringBuilder stringHolderBuffer = new StringBuilder();
                int readChars;
                while ((readChars = bais.read(bytes)) != -1){
                    for (int i = 0; i < readChars; ++i){
                        if(bytes[i] == '\n'){
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
                            stringHolderBuffer.append(new String(new byte[]{bytes[i]}));
                        }
                    }
                }
                stringHolderBuffer = null;
                in.close();
                in = null;
                bis.close();
                bis = null;
                bais.close();
                bais = null;
                baos.flush();
                baos.close();
                baos = null;
                lLine = null;
                bytes = null;
                blob = null;
                buf = null;
            } catch(Exception e){
                logger.error(e);
            }
            PreparedStatement versionInsertStatement = inConn.prepareStatement("update quantitation_file set version_number = (?) where quantitation_fileid = ?");
            if (lVersionNumber.equals("")){
                versionInsertStatement.setObject(1, "error");
            }
            else{
                versionInsertStatement.setObject(1, lVersionNumber);
            }
            versionInsertStatement.setObject(2, quantfileid);
            try {
                versionInsertStatement.execute();
            } catch(SQLException sqle){
                logger.error(sqle);
            }
            versionInsertStatement.close();
            counter--;
            return null;
        }
    }
}
