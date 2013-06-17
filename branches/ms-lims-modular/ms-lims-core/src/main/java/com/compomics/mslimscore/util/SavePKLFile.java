/*
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 6-feb-03
 * Time: 16:09:59
 */
package com.compomics.mslimscore.util;

import org.apache.log4j.Logger;

import com.compomics.mslimsdb.accessors.PklfilesTableAccessor;

import java.io.*;
import java.sql.Connection;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2007/05/01 13:30:44 $
 */

/**
 * This class can be called by Mascot Daemon to retrieve the filename of the file being processed and storing this file
 * GZIPped in a database.
 *
 * @author Lennart Martens
 */
public class SavePKLFile {
    // Class specific log4j logger for SavePKLFile instances.
    private static Logger logger = Logger.getLogger(SavePKLFile.class);

    private static void tryRead() {
        try {
            // Getting a DB connection.
            Driver d = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
            Properties p = new Properties();
            p.put("user", "martlenn");
            p.put("password", "");
            Connection c = d.connect("jdbc:mysql://cavell.rug.ac.be/demo3", p);

            // Storing results.
            HashMap hm = new HashMap(3);
            hm.put(PklfilesTableAccessor.FILENAME, "caplc1834.341.2.2.pkl");
            PklfilesTableAccessor pta = new PklfilesTableAccessor();
            pta.retrieve(c, hm);
            logger.info("Filename: " + pta.getFilename());
            logger.info("Identified: " + pta.getIdentified());
            logger.info("File: ");
            ByteArrayInputStream bais = new ByteArrayInputStream(pta.getFile());
            BufferedReader br = new BufferedReader(new InputStreamReader(new GZIPInputStream(bais)));
            String line = null;
            while ((line = br.readLine()) != null) {
                logger.info(line);
            }
            br.close();
            bais.close();
            c.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        System.exit(0);
    }

    /**
     * The main method expects 1 and just 1 argument: the fully qualified name of the file to store.
     *
     * @param args String[] with 1 element: the fully qualified file name.
     */
    public static void main(String[] args) {
        //tryRead();
        try {
            // The variables.
            String file = args[0];
            File f = new File(file);
            file = f.getName();
            byte[] bytes = null;
            int ided = 0;

            // Reading the file.
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(baos);
            BufferedOutputStream bos = new BufferedOutputStream(gos);
            int reading = -1;
            while ((reading = bis.read()) > -1) {
                bos.write(reading);
            }
            gos.finish();
            bis.close();
            bytes = baos.toByteArray();
            bos.close();
            baos.close();


            // Getting a DB connection.
            Driver d = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
            Properties p = new Properties();
            p.put("user", "martlenn");
            p.put("password", "");
            Connection c = d.connect("jdbc:mysql://cavell.rug.ac.be/krisdb", p);

            // Storing results.
            HashMap hm = new HashMap(3);
            hm.put(PklfilesTableAccessor.FILENAME, file);
            hm.put(PklfilesTableAccessor.FILE, bytes);
            hm.put(PklfilesTableAccessor.IDENTIFIED, new Integer(ided));
            PklfilesTableAccessor pta = new PklfilesTableAccessor(hm);
            pta.persist(c);

            // C'est fini.
            c.close();
            System.exit(0);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}
