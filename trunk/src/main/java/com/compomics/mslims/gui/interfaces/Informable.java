/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 12-feb-2004
 * Time: 12:19:36
 */
package com.compomics.mslims.gui.interfaces;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2004/02/12 13:30:42 $
 */

/**
 * This interface describes the behaviour for a GUI class that can receive generic information
 * from a dialog.
 *
 * @author Lennart Martens
 * @version $Id: Informable.java,v 1.1 2004/02/12 13:30:42 lennart Exp $
 */
public interface Informable {

    /**
     * This method can be called by a child component (typically a dialog) that wants to inform the
     * parent class of a certain event.
     *
     * @param o Object with the information to transfer.
     */
    public void inform(Object o);
}
