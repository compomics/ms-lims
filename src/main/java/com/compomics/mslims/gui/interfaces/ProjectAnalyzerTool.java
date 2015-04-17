/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 7-mrt-2005
 * Time: 7:39:58
 */
package com.compomics.mslims.gui.interfaces;


import com.compomics.mslims.db.accessors.Project;
import com.compomics.mslims.gui.ProjectAnalyzer;

import java.sql.Connection;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2005/03/21 14:18:54 $
 */

/**
 * This interface describes the behaviour for a tool that can be called from the ProjectAnalyzer application.
 *
 * @author Lennart Martens
 * @version $Id: ProjectAnalyzerTool.java,v 1.1 2005/03/21 14:18:54 lennart Exp $
 */
public interface ProjectAnalyzerTool {


    /**
     * This method represents the 'command-pattern' design of the ProjectAnalyzerTool. It will actually allow the tool
     * to run.
     *
     * @param aParent     ProjectAnalyzer with the parent that launched this tool.
     * @param aToolName   String with the name for the tool.
     * @param aParameters String with the parameters as stored in the database for this tool.
     * @param aConn       Connection with the DB connection to use.
     * @param aDBName     String with the name of the database we're connected to via 'aConn'.
     * @param aProject    Project with the project we should be analyzing.
     */
    void engageTool(ProjectAnalyzer aParent, String aToolName, String aParameters, Connection aConn, String aDBName, Project aProject);

    /**
     * This method should return a meaningful name for the tool.
     *
     * @return String with a meaningful name for the tool.
     */
    String getToolName();

    /**
     * This method will be called when the tool should show itself on the foreground and request the focus.
     */
    void setActive();

    /**
     * This method should be called when the tool has to shut down.
     */
    void close();
}
