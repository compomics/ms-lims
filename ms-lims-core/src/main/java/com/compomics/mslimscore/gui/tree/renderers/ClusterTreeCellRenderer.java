package com.compomics.mslimscore.gui.tree.renderers;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 17-feb-2005
 * Time: 12:00:38
 */

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/17 15:33:52 $
 */

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.HashMap;

/**
 * This class provides a TreeCellRenderer that indicates with a red color any treeleaf that has no identification
 * associated.
 *
 * @author Lennart Martens
 * @version $Id: ClusterTreeCellRenderer.java,v 1.1 2005/02/17 15:33:52 lennart Exp $
 */
public class ClusterTreeCellRenderer extends DefaultTreeCellRenderer {
    // Class specific log4j logger for ClusterTreeCellRenderer instances.
    private static Logger logger = Logger.getLogger(ClusterTreeCellRenderer.class);

    /**
     * The HashMap in which the String representation of the tree leaf serves as as key, and the Identification as
     * value. If this is 'null', this renderer behaves exactly like the default renderer.
     */
    private HashMap iIDLookup = null;

    /**
     * This constructor initializes the renderer.
     *
     * @param aIDLookup HashMap in which the String representation of the tree leaf serves as as key, and the
     *                  Identification as value. If this is 'null', this renderer behaves exactly like the default
     *                  renderer.
     */
    public ClusterTreeCellRenderer(HashMap aIDLookup) {
        this.iIDLookup = aIDLookup;
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
        if (iIDLookup != null && leaf && (iIDLookup.get(value) == null)) {
            this.setForeground(Color.red);
        }

        return this;
    }
}
