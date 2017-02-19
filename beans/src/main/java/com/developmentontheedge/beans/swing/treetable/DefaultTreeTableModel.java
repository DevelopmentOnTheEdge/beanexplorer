package com.developmentontheedge.beans.swing.treetable;

import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;

public class DefaultTreeTableModel extends AbstractTreeTableModel
    {
    private final TreeModel treeModel;
    private final TableModel tableModel;

    public DefaultTreeTableModel( TreeModel treeModel, TableModel tableModel )
        {
        super( treeModel.getRoot() );
        this.treeModel = treeModel;
        this.tableModel = tableModel;
        }

    @Override
    public int getColumnCount() { return tableModel.getColumnCount(); }

    @Override
    public String getColumnName( int col ) { return tableModel.getColumnName( col ); }

    @Override
    public Class getColumnClass( int column )
        {
        return column == 0 ? TreeTableModel.class : Object.class;
        }

    @Override
    public Object getValueAt( Object node, int column )
        {
        if( node != null )
            return ( ( TreeTableNode )node ).getValueAt( column );
        return null;
        }

    @Override
    public void setValueAt( Object aValue, Object node, int column )
        {
        if( node != null )
            ( ( TreeTableNode )node ).setValueAt( aValue, column );
        }

    @Override
    public Object getChild( Object parent, int index )
        {
        return treeModel.getChild( parent, index );
        }

    @Override
    public int getChildCount( Object parent )
        {
        return treeModel.getChildCount( parent );
        }
    }
