/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 31-jul-2003
 * Time: 15:16:17
 */
package com.compomics.mslimscore.util;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Properties;

/*
 * CVS information:
 *
 * $Revision: 1.1.1.1 $
 * $Date: 2004/01/29 16:18:18 $
 */

/**
 * This class is a very rugged, dirty, kludged first attempt to get PKLFiles from a Project-based DB into a folder.
 *
 * @author Lennart
 */
public class GetAllProjectPKLFiles {
    // Class specific log4j logger for GetAllProjectPKLFiles instances.
    private static Logger logger = Logger.getLogger(GetAllProjectPKLFiles.class);

    public static void main(String[] args) {
        if (args == null || args.length != 3) {
            logger.error("\n\nUsage:\n\tGetAllProjectPKLFiles <password> <project_title> <destination_dir>\n");
        }
        try {
            Driver d = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
            Properties p = new Properties();
            p.put("user", "martlenn");
            p.put("password", args[0]);
            Connection conn = d.connect("jdbc:mysql://localhost/projects", p);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
