/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 16-jul-2003
 * Time: 16:06:27
 */
package com.compomics.mslims.util.workers;

import com.compomics.mslims.gui.progressbars.DefaultProgressBar;
import com.compomics.mslims.gui.tree.MascotSearch;
import com.compomics.mslims.gui.tree.MascotTask;
import com.compomics.util.interfaces.Flamable;
import com.compomics.util.sun.SwingWorker;

import java.sql.*;
import java.util.Vector;

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

    /** This is the Connection to the Mascot Task DB. */
    private Connection iConn = null;

    /** This is the reference to the Vector that will hold all tasks. */
    private Vector iAllTasks = null;

    /** This variable contains a reference to the caller. */
    private Flamable iFlamable = null;

    /** Th progress bar. */
    private DefaultProgressBar iProgress = null;

    /**
     * This constructor allows the creation and initialization of this Runner. It takes the necessary arguments to
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
    }

    /** This method reads all the data from the Mascot Task DB via ODBC (using the Sun JDBC-ODBC bridge driver). */
    public Object construct() {
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
            sqle.printStackTrace();
            iProgress.dispose();
            iFlamable.passHotPotato(sqle);
        }
        iProgress.setValue(iProgress.getMaximum());
        return "";
    }
}