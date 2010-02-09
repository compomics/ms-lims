package com.compomics.mslims.gui.tree;

/**
 * Created by IntelliJ IDEA.
 * User: Lennart
 * Date: 17-feb-2005
 * Time: 13:29:54
 */

/*
 * CVS information:
 *
 * $Revision: 1.1 $
 * $Date: 2005/02/17 15:33:52 $
 */

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

/**
 * This class implements a tree model for clusters, based on a HashMap.
 *
 * @author Lennart Martens
 * @version $Id: ClusterTreeModel.java,v 1.1 2005/02/17 15:33:52 lennart Exp $
 */
public class ClusterTreeModel implements TreeModel {

    /**
     * This Vector will hold all the clusters.
     */
    private HashMap iClusters = null;

    /**
     * The sorted array of cluster names.
     */
    private String[] iClustersArray = null;

    /**
     * This constructor takes a list of clusters.
     *
     * @param aClusters  HashMap with the clusters.
     */
    public ClusterTreeModel(HashMap aClusters) {
        iClusters = aClusters;
        iClustersArray = new String[iClusters.size()];
        iClusters.keySet().toArray(iClustersArray);
        Arrays.sort(iClustersArray, new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = (String)o1;
                String s2 = (String)o2;
                String t1 = s1.substring(s1.indexOf(" ")+1).trim();
                String t2 = s2.substring(s1.indexOf(" ")+1).trim();
                int i1 = Integer.parseInt(t1);
                int i2 = Integer.parseInt(t2);
                return i1-i2;
            }
        });
    }

    /**
     * Adds a listener for the <code>TreeModelEvent</code>
     * posted after the tree changes.
     *
     * @param l the listener to add
     * @see #removeTreeModelListener
     */
    public void addTreeModelListener(TreeModelListener l) {
        // Not implemented.
    }

    /**
     * Returns the child of <code>parent</code> at index <code>index</code>
     * in the parent's
     * child array.  <code>parent</code> must be a node previously obtained
     * from this data source. This should not return <code>null</code>
     * if <code>index</code>
     * is a valid index for <code>parent</code> (that is <code>index >= 0 &&
     * index < getChildCount(parent</code>)).
     *
     * @param parent a node in the tree, obtained from this data source
     * @return the child of <code>parent</code> at index <code>index</code>
     */
    public Object getChild(Object parent, int index) {
        Object result = null;
        if("Clusters".equals(parent)) {
            result = iClustersArray[index];
        } else if(iClusters.containsKey(parent)) {
            Vector spectra = (Vector)iClusters.get(parent);
            if((0 <= index) && (index < spectra.size())) {
                result = spectra.get(index);
            }
        }
        return result;
    }

    /**
     * Returns the number of children of <code>parent</code>.
     * Returns 0 if the node
     * is a leaf or if it has no children.  <code>parent</code> must be a node
     * previously obtained from this data source.
     *
     * @param parent a node in the tree, obtained from this data source
     * @return the number of children of the node <code>parent</code>
     */
    public int getChildCount(Object parent) {
        int count = 0;
        if("Clusters".equals(parent)) {
            count = iClusters.size();
        } else if(iClusters.containsKey(parent)) {
            count = ((Vector)iClusters.get(parent)).size();
        }
        return count;
    }

    /**
     * Returns the index of child in parent.  If <code>parent</code>
     * is <code>null</code> or <code>child</code> is <code>null</code>,
     * returns -1.
     *
     * @param parent a note in the tree, obtained from this data source
     * @param child  the node we are interested in
     * @return the index of the child in the parent, or -1 if either
     *         <code>child</code> or <code>parent</code> are <code>null</code>
     */
    public int getIndexOfChild(Object parent, Object child) {
        int index = -1;
        if(iClusters.containsKey(parent)) {
            Vector spectra = (Vector)iClusters.get(parent);
            index = spectra.indexOf(child);
        }
        return index;
    }

    /**
     * Returns the root of the tree.  Returns <code>null</code>
     * only if the tree has no nodes.
     *
     * @return the root of the tree
     */
    public Object getRoot() {
        return "Clusters";
    }

    /**
     * Returns <code>true</code> if <code>node</code> is a leaf.
     * It is possible for this method to return <code>false</code>
     * even if <code>node</code> has no children.
     * A directory in a filesystem, for example,
     * may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     *
     * @param node a node in the tree, obtained from this data source
     * @return true if <code>node</code> is a leaf
     */
    public boolean isLeaf(Object node) {
        boolean leaf = false;
        if(!"Clusters".equals(node) && !iClusters.containsKey(node)) {
            leaf = true;
        }
        return leaf;
    }

    /**
     * Removes a listener previously added with
     * <code>addTreeModelListener</code>.
     *
     * @param l the listener to remove
     * @see #addTreeModelListener
     */
    public void removeTreeModelListener(TreeModelListener l) {
        // not implemented.
    }

    /**
     * Messaged when the user has altered the value for the item identified
     * by <code>path</code> to <code>newValue</code>.
     * If <code>newValue</code> signifies a truly new value
     * the model should post a <code>treeNodesChanged</code> event.
     *
     * @param path     path to the node that the user has altered
     * @param newValue the new value from the TreeCellEditor
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        // Not implemented.
    }
}