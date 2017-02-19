package com.developmentontheedge.beans.swing.treetable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * This is a wrapper class takes a TreeTableModel and implements the table model interface. The implementation is
 * trivial, with all of the event dispatching support provided by the superclass: the AbstractTableModel.
 * @version 1.2 10/27/98
 * @author Philip Milne
 * @author Scott Violet
 */
public class TreeTableModelAdapter extends AbstractTableModel
    {
    JTree tree;
    TreeTableModel treeTableModel;

    public TreeTableModelAdapter( TreeTableModel treeTableModel, JTree tree )
        {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
        tree.addTreeExpansionListener(
            new TreeExpansionListener()
                {
                // Don't use fireTableRowsInserted() here; the selection model
                // would get updated twice.
                @Override
                public void treeExpanded( TreeExpansionEvent event )
                    {
                    fireTableDataChanged();
                    }
                @Override
                public void treeCollapsed( TreeExpansionEvent event )
                    {
                    fireTableDataChanged();
                    }
                } );
        // Install a TreeModelListener that can update the table when
        // tree changes. We use delayedFireTableDataChanged as we can
        // not be guaranteed the tree will have finished processing
        // the event before us.
        treeTableModel.addTreeModelListener(
            new TreeModelListener()
                {
                @Override
                public void treeNodesChanged( TreeModelEvent e )
                    {
                    delayedFireTableDataChanged();
                    }
                @Override
                public void treeNodesInserted( TreeModelEvent e )
                    {
                    delayedFireTableDataChanged();
                    }
                @Override
                public void treeNodesRemoved( TreeModelEvent e )
                    {
                    delayedFireTableDataChanged();
                    }
                @Override
                public void treeStructureChanged( TreeModelEvent e )
                    {
                    delayedFireTableDataChanged();
                    }
                } );
        }

    // Wrappers, implementing TableModel interface.
    @Override
    public int getColumnCount()
        {
        return treeTableModel.getColumnCount();
        }

    @Override
    public String getColumnName( int column )
        {
        return treeTableModel.getColumnName( column );
        }

    @Override
    public Class getColumnClass( int column )
        {
        return treeTableModel.getColumnClass( column );
        }

    @Override
    public int getRowCount()
        {
        return tree.getRowCount();
        }

    protected Object nodeForRow( int row )
        {
        TreePath treePath = tree.getPathForRow( row );
        
        
        return treePath==null ? null : treePath.getLastPathComponent();
        }

    @Override
    public Object getValueAt( int row, int column )
        {
        return treeTableModel.getValueAt( nodeForRow( row ), column );
        }

    @Override
    public boolean isCellEditable( int row, int column )
        {
        return treeTableModel.isCellEditable( nodeForRow( row ), column );
        }

    @Override
    public void setValueAt( Object value, int row, int column )
        {
        treeTableModel.setValueAt( value, nodeForRow( row ), column );
        }

    /**
     * Invokes fireTableDataChanged after all the pending events have been
     * processed. SwingUtilities.invokeLater is used to handle this.
     */
    protected void delayedFireTableDataChanged()
        {
        SwingUtilities.invokeLater(
            new Runnable()
                {
                @Override
                public void run()
                    {
                    fireTableDataChanged();
                    }
                } );
        }
    }
