/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 23-jun-2004
 * Time: 13:23:30
 */
package com.compomics.mslims.gui.tree.renderers;

import com.compomics.mslims.gui.tree.MascotTask;
import org.apache.log4j.Logger;


import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.net.URL;

/*
 * CVS information:
 *
 * $Revision: 1.2 $
 * $Date: 2006/06/07 09:04:52 $
 */

/**
 * This class implements a TreeCellRenderer for a JTree containing Mascot tasks.
 *
 * @author Lennart Martens
 * @version $Id: MascotTasksTreeCellRenderer.java,v 1.2 2006/06/07 09:04:52 lennart Exp $
 */
public class MascotTasksTreeCellRenderer extends DefaultTreeCellRenderer {
    // Class specific log4j logger for MascotTasksTreeCellRenderer instances.
    private static Logger logger = Logger.getLogger(MascotTasksTreeCellRenderer.class);

    private Icon iCancelled = null;
    private Icon iFollowUp = null;
    private Icon iPaused = null;
    private Icon iRunBusy = null;
    private Icon iRunCompleted = null;

    /**
     * This constructor initializes the icons needed for display on the tree.
     */
    public MascotTasksTreeCellRenderer() {
        this.iCancelled = this.loadIcon("cancelled.gif");
        this.iFollowUp = this.loadIcon("followup.gif");
        this.iPaused = this.loadIcon("paused.gif");
        this.iRunBusy = this.loadIcon("runbusy.gif");
        this.iRunCompleted = this.loadIcon("runcompleted.gif");
    }

    /**
     * Sets the value of the current tree cell to <code>value</code>. If <code>selected</code> is true, the cell will be
     * drawn as if selected. If <code>expanded</code> is true the node is currently expanded and if <code>leaf</code> is
     * true the node represets a leaf and if <code>hasFocus</code> is true the node currently has focus.
     * <code>tree</code> is the <code>JTree</code> the receiver is being configured for.  Returns the
     * <code>Component</code> that the renderer uses to draw the value.
     *
     * @return the <code>Component</code> that the renderer uses to draw the value
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(
                tree, value, selected,
                expanded, leaf, row,
                hasFocus);
        if (!leaf) {
            setIcon(this.determineIcon(value, expanded));
        }

        return this;
    }

    /**
     * This method loads the specified icon from file.
     *
     * @param aName String with the name for the desired icon.
     * @return Icon with the icon.
     */
    private Icon loadIcon(String aName) {
        URL imgURL = this.getClass().getClassLoader().getResource(aName);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            throw new IllegalArgumentException("Unable to find icon '" + aName + "' in the classpath!");
        }
    }

    /**
     * This method will read the 'status' and 'schedule' fields and use these to determine which icon should be
     * displayed.
     *
     * @param aValue    Object with the MascotTask (or any other object, in which case the icon is the default one for
     *                  the tree).
     * @param aExpanded boolean to indicate whether the icon stands for an expanded node or not.
     * @return Icon with the icon to display.
     */
    private Icon determineIcon(Object aValue, boolean aExpanded) {
        Icon icon = null;

        if (aValue instanceof MascotTask) {
            MascotTask task = (MascotTask) aValue;
            String schedule = task.getSchedule();
            String status = task.getStatus();
            if (schedule.equalsIgnoreCase("now") && status.equalsIgnoreCase("running")) {
                icon = this.iRunBusy;
            } else if (schedule.equalsIgnoreCase("now") && status.equalsIgnoreCase("completed")) {
                icon = this.iRunCompleted;
            } else if (schedule.equalsIgnoreCase("follower") && status.equalsIgnoreCase("running")) {
                icon = this.iFollowUp;
            } else if (status.equalsIgnoreCase("paused")) {
                icon = this.iPaused;
            } else if (status.equalsIgnoreCase("cancelled")) {
                icon = this.iCancelled;
            }
        } else {
            if (aExpanded) {
                icon = getDefaultOpenIcon();
            } else {
                icon = getDefaultClosedIcon();
            }
        }
        return icon;
    }
}
