/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 2-jan-2006
 * Time: 13:59:07
 */
package com.compomics.mslims.db.conversiontool.interfaces;

import org.apache.log4j.Logger;

import java.sql.Connection;
/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2006/01/02 16:47:15 $
 */

/**
 * This interface describes the behaviour for a class that will take care of a specific database conversion step. It is
 * a command pattern.
 *
 * @author Lennart Martens
 * @version $Id: DBConverterStep.java,v 1.1 2006/01/02 16:47:15 lennart Exp $
 */
public interface DBConverterStep {

    /**
     * This method will be called whenever this step should be executed.
     *
     * @param aConn Connection on which to perform the step.
     * @return boolean that indicates success ('false') or failure ('true').
     */
    boolean performConversionStep(Connection aConn);
}
