/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 2-jan-2006
 * Time: 14:48:26
 */
package com.compomics.mslimscore.util.conversiontool.implementations;

import org.apache.log4j.Logger;

import com.compomics.mslimscore.util.conversiontool.interfaces.DBConverterStep;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2006/01/02 16:47:15 $
 */

/**
 * This class implements a DB conversion step that is based on an SQL statement.
 *
 * @author Lennart Martens
 * @version $Id: SQLDBConverterStepImpl.java,v 1.1 2006/01/02 16:47:15 lennart Exp $
 */
public class SQLDBConverterStepImpl implements DBConverterStep {
    // Class specific log4j logger for SQLDBConverterStepImpl instances.
    private static Logger logger = Logger.getLogger(SQLDBConverterStepImpl.class);

    /**
     * The SQL statement to execute.
     */
    private String iStatement = null;

    /**
     * This constructor takes the SQL statement for this step.
     *
     * @param aStatement String with the SQL statement for this step.
     */
    public SQLDBConverterStepImpl(String aStatement) {
        this.iStatement = aStatement;
    }

    /**
     * This method will be called whenever this step should be executed.
     *
     * @param aConn Connection on which to perform the step.
     * @return boolean that indicates success ('false') or failure ('true').
     */
    public boolean performConversionStep(Connection aConn) {
        boolean errors = false;

        try {
            Statement stat = aConn.createStatement();
            stat.execute(iStatement);
            stat.close();
        } catch (SQLException sqle) {
            logger.error("\n\nError executing statement '" + iStatement + "':");
            logger.error(sqle.getMessage());
            logger.error(sqle.getMessage(), sqle);
            errors = true;
        }

        return errors;
    }
}
