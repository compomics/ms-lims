/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 26-jan-2004
 * Time: 11:22:40
 */
package com.compomics.mslimscore.gui.interfaces;


import com.compomics.util.interfaces.Flamable;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2004/07/08 13:14:19 $
 */

/**
 * This interface describes the behaviour for a GUI that allows the management of projects via the ProjectDialog class.
 * Note that a ProjectManager is flamable as well!
 *
 * @author Lennart Martens
 * @version $Id: ProjectManager.java,v 1.2 2004/07/08 13:14:19 lennart Exp $
 */
public interface ProjectManager extends Flamable {

    /**
     * This method will be called by the ProjectDialog whenever an operation changed the projects.
     */
    public void projectsChanged();
}
