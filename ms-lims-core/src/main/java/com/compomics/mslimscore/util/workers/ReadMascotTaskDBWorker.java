/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 16-jul-2003
 * Time: 16:06:27
 */
package com.compomics.mslimscore.util.workers;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import org.apache.log4j.Logger;

import com.compomics.mslimscore.gui.progressbars.DefaultProgressBar;
import com.compomics.mslimscore.gui.tree.MascotSearch;
import com.compomics.mslimscore.gui.tree.MascotTask;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.SwingWorker;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/*
 * CVS information:
 *
 * $Revision: 1.6 $
 * $Date: 2009/01/30 10:31:05 $
 */

/**
 * This class loads all the information from the Mascot TaskDB.
 *
 * @author Lennart Martens
 */
public class ReadMascotTaskDBWorker extends SwingWorker {
    // Class specific log4j logger for ReadMascotTaskDBWorker instances.
    private static Logger logger = Logger.getLogger(ReadMascotTaskDBWorker.class);

    /**
     * Boolean to indicate whether the connection is to an MS Access file,
     * or to a relational database.
     */
    private boolean iFromDatabase = false;

    /**
     * Access database object for the Mascot Daemon Task DB.
     */
    private Database iTaskDB = null;

    /**
     * This is the Connection to the Mascot Task DB.
     */
    private Connection iConn = null;

    /**
     * This is the reference to the Vector that will hold all tasks.
     */
    private Vector iAllTasks = null;

    /**
     * This variable contains a reference to the caller.
     */
    private Flamable iFlamable = null;

    /**
     * Th progress bar.
     */
    private DefaultProgressBar iProgress = null;

    /**
     * This constructor allows the creation and initialization of this Runner. By specifying a DB connection,
     * the data will be loaded from a relational database. It takes the necessary arguments to
     * create a workable runner.
     *
     * @param aConn     Connection to the Mascot Task DB.
     * @param aAllTasks Vector to hold the tasks after completion. Note that this is a reference parameter!
     * @param aParent   Flamable instance that called this worker.
     * @param aProgress DefaultProgressBar to show the progress on.
     */
    public ReadMascotTaskDBWorker(Connection aConn, Vector aAllTasks, Flamable aParent, DefaultProgressBar aProgress) {
        iConn = aConn;
        iAllTasks = aAllTasks;
        iFlamable = aParent;
        iProgress = aProgress;
        iFromDatabase = true;
    }

    /**
     * This constructor allows the creation and initialization of this Runner. By specifying an MS Access Database object,
     * the data will be loaded directly from the '.mdb' file. It takes the necessary arguments to
     * create a workable runner.
     *
     * @param aTaskDB   Database with the MS Access file for the Mascot Daemon Task DB.
     * @param aAllTasks Vector to hold the tasks after completion. Note that this is a reference parameter!
     * @param aParent   Flamable instance that called this worker.
     * @param aProgress DefaultProgressBar to show the progress on.
     */
    public ReadMascotTaskDBWorker(Database aTaskDB, Vector aAllTasks, Flamable aParent, DefaultProgressBar aProgress) {
        iTaskDB = aTaskDB;
        iAllTasks = aAllTasks;
        iFlamable = aParent;
        iProgress = aProgress;
        iFromDatabase = false;
    }

    public Object construct() {
        if (iFromDatabase) {
            return retrieveFromDatabase();
        } else {
            return retrieveFromAccessFile();
        }
    }

    /**
     * This method reads all the data from the Mascot Task DB via ODBC (using the Sun JDBC-ODBC bridge driver).
     */
    public Object retrieveFromDatabase() {
        try {
            Statement stat = iConn.createStatement();
            PreparedStatement ps =
                    iConn.prepareStatement("select seq_database, input_data_file, result_url, submitted, returned, result_comment, accession, distiller_project from Mascot_Daemon_Results where task_UID=? order by submitted desc");
            ResultSet rs =
                    stat.executeQuery("select task_UID, task_label, parameter_set, schedule_type, task_status from Mascot_Daemon_Tasks order by task_UID DESC");
            while (rs.next()) {
                int id = rs.getInt(1);
                String title = rs.getString(2);
                String params = rs.getString(3);
                String schedule = rs.getString(4);
                String status = rs.getString(5);
                // Change the message on the progress bar.
                iProgress.setMessage("Reading task '" + title + "'...");
                // Now select all the corresponding searches for this task.
                ps.setInt(1, id);
                ResultSet rs2 = ps.executeQuery();
                Vector searches = new Vector(15, 5);
                while (rs2.next()) {
                    // Read each search.
                    String db = rs2.getString(1);
                    String mergefile = rs2.getString(2);
                    String datfile = rs2.getString(3);
                    Timestamp startDate = rs2.getTimestamp(4);
                    Timestamp endDate = rs2.getTimestamp(5);
                    String searchTitle = rs2.getString(6);
                    String accession = rs2.getString(7);
                    // Distiller project can be null!
                    String distiller_project = rs2.getString(8);
                    // First see if there is a datfile.
                    int searchStatus = MascotSearch.STATUS_NORMAL;
                    // If there is none, no identifications were made!
                    if (datfile == null || !(datfile.indexOf(".dat") >= 0) || accession == null || (accession.toLowerCase().indexOf("<no result") >= 0)) {
                        // This search is skipped.
                        searchStatus = MascotSearch.STATUS_ERROR;
                    } else if (searchTitle == null) {
                        searchTitle = "<No title>";
                        searchStatus = MascotSearch.STATUS_WARNING;
                    }

                    MascotSearch lMascotSearch =
                            new MascotSearch(searchStatus, searchTitle, db, mergefile, datfile, startDate, endDate, id);

                    // If there is a distiller project together with this search, then add it to the search.
                    if (distiller_project != null) {
                        lMascotSearch.setDistiller_project(distiller_project);
                    }

                    searches.add(lMascotSearch);
                }
                rs2.close();
                ps.clearParameters();
                // Init the task now.
                iAllTasks.add(new MascotTask(title, params, schedule, status, searches, id));
                iProgress.setValue(iProgress.getValue() + 1);
            }
            rs.close();
            stat.close();
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
            iProgress.dispose();
            iFlamable.passHotPotato(sqle);
        }
        iProgress.setValue(iProgress.getMaximum());
        return "";
    }

    public Object retrieveFromAccessFile() {
        try {
            Table table = iTaskDB.getTable("Mascot_Daemon_Results");
            Iterator<Map<String, Object>> iter = table.iterator();
            HashMap<Integer, Vector<MascotSearch>> mascotResults = new HashMap<Integer, Vector<MascotSearch>>();
            // Change the message on the progress bar.
            iProgress.setMessage("Reading search results...");
            while (iter.hasNext()) {
                Map<String, Object> results = iter.next();
                Integer task_uid = (Integer) results.get("task_UID");
                String db = (String) results.get("seq_database");
                String mergefile = (String) results.get("input_data_file");
                String datfile = (String) results.get("result_url");
                Timestamp startDate = new Timestamp(((java.util.Date) results.get("submitted")).getTime());
                Timestamp endDate = new Timestamp(((java.util.Date) results.get("returned")).getTime());
                String searchTitle = (String) results.get("result_comment");
                String accession = (String) results.get("accession");
                // Distiller project can be null!
                String distiller_project = (String) results.get("distiller_project");

                // First see if there is a datfile.
                int searchStatus = MascotSearch.STATUS_NORMAL;
                // If there is none, no identifications were made!
                if (datfile == null || !(datfile.indexOf(".dat") >= 0) || accession == null || (accession.toLowerCase().indexOf("<no result") >= 0)) {
                    // This search is skipped.
                    searchStatus = MascotSearch.STATUS_ERROR;
                } else if (searchTitle == null) {
                    searchTitle = "<No title>";
                    searchStatus = MascotSearch.STATUS_WARNING;
                }

                MascotSearch lMascotSearch = new MascotSearch(searchStatus, searchTitle, db, mergefile, datfile, startDate, endDate, task_uid);

                // If there is a distiller project together with this search, then add it to the search.
                if (distiller_project != null) {
                    lMascotSearch.setDistiller_project(distiller_project);
                }

                if (mascotResults.containsKey(task_uid)) {
                    // Add result to existing task UID.
                    Vector temp = mascotResults.get(task_uid);
                    temp.add(lMascotSearch);
                } else {
                    // First occurrence of Task UID. Create new arraylist and insert.
                    Vector<MascotSearch> temp = new Vector<MascotSearch>();
                    temp.add(lMascotSearch);
                    mascotResults.put(task_uid, temp);
                }
                iProgress.setValue(iProgress.getValue() + 1);
            }

            iProgress.setMessage("Reading tasks...");

            // Now get the tasks themselves
            table = iTaskDB.getTable("Mascot_Daemon_Tasks");
            iter = table.iterator();
            TreeSet<MascotTask> ts = new TreeSet<MascotTask>(new Comparator<MascotTask>() {
                public int compare(MascotTask o1, MascotTask o2) {
                    Integer i1 = new Integer(o1.getNumber());
                    Integer i2 = new Integer(o2.getNumber());
                    return -1*(i1.compareTo(i2));
                }
            });
            while (iter.hasNext()) {
                Map<String, Object> results = iter.next();
                Integer task_uid = (Integer) results.get("task_UID");
                String title = (String) results.get("task_label");
                String params = (String) results.get("parameter_set");
                String schedule = (String) results.get("schedule_type");
                String status = (String) results.get("task_status");

                Vector tempResults = new Vector();
                if (mascotResults.containsKey(task_uid)) {
                    tempResults = mascotResults.get(task_uid);
                }
                ts.add(new MascotTask(title, params, schedule, status, tempResults, task_uid));
                iProgress.setValue(iProgress.getValue() + 1);
            }
            iProgress.setMessage("Sorting tasks...");
            Iterator<MascotTask> taskIter = ts.iterator();
            while (taskIter.hasNext()) {
                iAllTasks.add(taskIter.next());
            }
            iProgress.setValue(iProgress.getValue() + 1);
            iProgress.setMessage("All data read!");
        } catch (IOException ioe) {
            logger.error(ioe.getMessage(), ioe);
            iProgress.dispose();
            iFlamable.passHotPotato(ioe);
        }
        iProgress.setValue(iProgress.getMaximum());
        return "";
    }
}
