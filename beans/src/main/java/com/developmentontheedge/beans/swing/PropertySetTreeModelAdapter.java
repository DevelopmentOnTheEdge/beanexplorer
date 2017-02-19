package com.developmentontheedge.beans.swing;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.tree.TreePath;

import com.developmentontheedge.beans.model.Property;

public class PropertySetTreeModelAdapter extends AbstractTreeModel implements PropertyChangeListener
{
    public PropertySetTreeModelAdapter(Property property, int mode)
    {
        super( property );
        this.mode = mode;
        property.addPropertyChangeListener( this );
    }

    ////////////////////////////////////////
    // PENDING
    //
    protected int mode;

    protected int getShowMode()
    {
        return mode;
    }

    protected void setShowMode(int mode)
    {
        if( this.mode != mode )
        {
            this.mode = mode;
            update();
        }
    }

    public void update()
    {
        //inspector.getTreeTable().updateUI();
    }

    ////////////////////////////////////////
    // TreeModel methods implementation
    //
    @Override
    public int getChildCount(Object node)
    {
        if( node instanceof Property )
        {
            Property ps = (Property)node;
            if( ps.isHideChildren() )
                return 0;
            return ps.getVisibleCount( mode );
        }
        return 0;
    }

    /**
     * @pending Implement prcessing of Falttend child properties which is imagined as superior to Substitute-by-child idea. With flattened child properties all leaf propetrties are cobined to the single list regardless whether they are direct or indirect descendants 
     */
    @Override
    public Object getChild(Object node, int i)
    {
        if( ! ( node instanceof Property ) )
            // PENDING (fedor)
            return "???";
        Property p = null;
        Property ps = (Property)node;
        // take into account that property set
        // can contains hidden nodes
        p = ps.getVisiblePropertyAt( i, mode );

        while( p.getVisibleCount( mode ) == 1 && p.isSubstituteByChild() )
        {
            for( int k = 0; k < p.getPropertyCount(); k++ )
                if( p.getPropertyAt( k ).isVisible( mode ) )
                {
                    p = p.getPropertyAt( k );
                    break;
                }
        }

        return p;
    }

    @Override
    public boolean isLeaf(Object node)
    {
        if( node instanceof Property )
            return ( (Property)node ).isLeaf();
        return true;
    }
    ////////////////////////////////////////
    // PropertyChange
    //

    /** Creates tree path for the specified child node. */
    protected TreePath makeTreePath(Property last)
    {
        return null;

        /*
        PropertySet parent = last.getParent();
        return parent == null ? new TreePath(last)
                              : makeTreePath(parent).pathByAddingChild(last);
        */
    }

    /** Should translate PropertyChange events to tree events. PENDING: not implemented. */
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
    }
    // PENDING:
    // who will call removePropertyChangeListener(this) ?
    ////////////////////////////////////////
    // PENDING (to remove)
    //

    /*
    public void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        path[0] = inspector.getComponentModel();
        super.fireTreeNodesInserted(source, path, childIndices, children);
    }

    public void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        path[0] = inspector.getComponentModel();
        super.fireTreeNodesChanged(source, path, childIndices, children);
    }

    public void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        path[0] = inspector.getComponentModel();
        super.fireTreeNodesRemoved(source, path, childIndices, children);
    }
    */
}
