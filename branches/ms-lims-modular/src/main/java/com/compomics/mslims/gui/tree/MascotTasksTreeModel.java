/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 18-jun-2004
 * Time: 18:19:37
 */
package com.compomics.mslims.gui.tree;

import org.apache.log4j.Logger;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Vector;

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2004/06/30 08:46:22 $
 */

/**
 * This class presents a TreeModel implementation for the Tree with the Mascot tasks and searches.
 *
 * @author Lennart Martens
 * @version $Id: MascotTasksTreeModel.java,v 1.1 2004/06/30 08:46:22 lennart Exp $
 */
public class MascotTasksTreeModel implements TreeModel {
    // Class specific log4j logger for MascotTasksTreeModel instances.
    private static Logger logger = Logger.getLogger(MascotTasksTreeModel.class);

    /**
     * This Vector will hold all the searches done. key
     */
    private Vector iTasks = null;

    /**
     * This constructor takes a list of MascotTask instances that have been retrieved from the database.
     *
     * @param aMascotTasks Vector with all the Mascot tasks that were retrieved from the TaskDB.
     */
    public MascotTasksTreeModel(Vector aMascotTasks) {
        iTasks = aMascotTasks;
    }

    /**
     * Adds a listener for the <code>TreeModelEvent</code> posted after the tree changes.
     *
     * @param l the listener to add
     * @see #removeTreeModelListener
     */
    public void addTreeModelListener(TreeModelListener l) {
        // Not implemented.
    }

    /**
     * Returns the child of <code>parent</code> at index <code>index</code> in the parent's child array.
     * <code>parent</code> must be a node previously obtained from this data source. This should not return
     * <code>null</code> if <code>index</code> is a valid index for <code>parent</code> (that is <code>index >= 0 &&
     * index < getChildCount(parent</code>)).
     *
     * @param parent a node in the tree, obtained from this data source
     * @return the child of <code>parent</code> at index <code>index</code>
     */
    public Object getChild(Object parent, int index) {
        Object result = null;
        if (iTasks.contains(parent)) {
            MascotTask mt = (MascotTask) parent;
            if ((0 <= index) && (index < mt.countSearches())) {
                result = mt.getSearch(index);
            }
        } else if ("Tasks".equals(parent)) {
            return this.iTasks.get(index);
        }
        return result;
    }

    /**
     * Returns the number of children of <code>parent</code>. Returns 0 if the node is a leaf or if it has no children.
     * <code>parent</code> must be a node previously obtained from this data source.
     *
     * @param parent a node in the tree, obtained from this data source
     * @return the number of children of the node <code>parent</code>
     */
    public int getChildCount(Object parent) {
        int count = 0;
        if ("Tasks".equals(parent)) {
            count = this.iTasks.size();
        } else {
            count = ((MascotTask) parent).countSearches();
        }
        return count;
    }

    /**
     * Returns the index of child in parent.  If <code>parent</code> is <code>null</code> or <code>child</code> is
     * <code>null</code>, returns -1.
     *
     * @param parent a note in the tree, obtained from this data source
     * @param child  the node we are interested in
     * @return the index of the child in the parent, or -1 if either <code>child</code> or <code>parent</code> are
     *         <code>null</code>
     */
    public int getIndexOfChild(Object parent, Object child) {
        return ((MascotTask) parent).getSearches().indexOf(child);
    }

    /**
     * Returns the root of the tree.  Returns <code>null</code> only if the tree has no nodes.
     *
     * @return the root of the tree
     */
    public Object getRoot() {
        return "Tasks";
    }

    /**
     * Returns <code>true</code> if <code>node</code> is a leaf. It is possible for this method to return
     * <code>false</code> even if <code>node</code> has no children. A directory in a filesystem, for example, may
     * contain no files; the node representing the directory is not a leaf, but it also has no children.
     *
     * @param node a node in the tree, obtained from this data source
     * @return true if <code>node</code> is a leaf
     */
    public boolean isLeaf(Object node) {
        boolean leaf = false;
        if (!"Tasks".equals(node) && !iTasks.contains(node)) {
            leaf = true;
        }
        return leaf;
    }

    /**
     * Removes a listener previously added with <code>addTreeModelListener</code>.
     *
     * @param l the listener to remove
     * @see #addTreeModelListener
     */
    public void removeTreeModelListener(TreeModelListener l) {
        // not implemented.
    }

    /**
     * Messaged when the user has altered the value for the item identified by <code>path</code> to
     * <code>newValue</code>. If <code>newValue</code> signifies a truly new value the model should post a
     * <code>treeNodesChanged</code> event.
     *
     * @param path     path to the node that the user has altered
     * @param newValue the new value from the TreeCellEditor
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        // Not implemented.
    }
}
