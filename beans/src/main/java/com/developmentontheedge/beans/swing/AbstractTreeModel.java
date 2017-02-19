package com.developmentontheedge.beans.swing;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/** 
 * An abstract implementation of the TreeModel interface, handling the list of listeners. 
 * @author DevelopmentOnTheEdge
 * @pending no class in this package uses this - perhaps it should be moved elsewhere 
 * @version 1.0
 */
public abstract class AbstractTreeModel implements TreeModel
{
    protected Object root;
    protected EventListenerList listenerList = new EventListenerList();

    public AbstractTreeModel(Object root)
    {
        this.root = root;
    }

    ////////////////////////////////////////
    // Default implmentations for methods in the TreeModel interface.
    //

    @Override
    public Object getRoot()
    {
        return root;
    }

    @Override
    public boolean isLeaf(Object node)
    {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) { }

    // This is not called in the JTree's default mode: use a naive implementation.
    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        for (int i = 0; i < getChildCount(parent); i++)
        {
            if (getChild(parent, i).equals(child))
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l)
    {
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l)
    {
        listenerList.remove(TreeModelListener.class, l);
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see javax.swing.event.EventListenerList
     */

    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices,
    Object[] children)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener)listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see javax.swing.event.EventListenerList
     */

    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices,
    Object[] children)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener)listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see javax.swing.event.EventListenerList
     */

    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices,
    Object[] children)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener)listeners[i + 1]).treeNodesRemoved(e);
            }
        }
    }

    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see javax.swing.event.EventListenerList
     */

    public void fireTreeStructureChanged(TreeModelEvent e)
    {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2)
        {
            if (listeners[i]==TreeModelListener.class)
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
        }
    }

    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices,
    Object[] children)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path, childIndices, children);
                ((TreeModelListener)listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }
}
