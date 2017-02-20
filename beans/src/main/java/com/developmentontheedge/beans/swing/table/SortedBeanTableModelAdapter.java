package com.developmentontheedge.beans.swing.table;

import java.beans.PropertyChangeEvent;

import javax.swing.event.TableModelEvent;

import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;

public class SortedBeanTableModelAdapter extends BeanTableModelAdapter implements SortedTableModel
{
    private Integer[] indices = null;

    public SortedBeanTableModelAdapter( RowModel rm, ColumnModel columnModel )
    {
        super( rm, columnModel );
        if (columnModel != null)
        {
            columnModel.setSortEnabled( true );
        }
    }

    public Integer[] getIndices()
    {
        int n = rm.size();
        if ( indices == null || indices.length != n )
        {
            indices = new Integer[ n ];
            for ( int i = 0; i < n; i++ )
            {
                indices[ i ] = new Integer( i );
            }
        }
        return indices;
    }

    @Override
    public Object getValueAt( int row, int column )
    {
        Property property = null;
        if ( column == 0 && getRowHeader() )
        {
            RowHeaderBean rowHeaderBean = new RowHeaderBean();
            rowHeaderBean.setNumber( row );
            ComponentModel rowHeaderModel = ComponentFactory.getModel( rowHeaderBean, ComponentFactory.Policy.UI );
            property = rowHeaderModel.findProperty( "number" );
        }
        else
        {
            if ( indices != null )
            {
                row = indices[ row ].intValue();
            }
            property = getPropertyAt( row, column );
        }
        return property;
    }

    @Override
    public void setValueAt( Object aValue, int row, int column )
    {
        if( indices != null )
                row = indices[ row ].intValue();

        super.setValueAt(aValue, row, column);
    }

    public int getVisibleColumnIndex( Column column )
    {
        int cnt = ( getRowHeader() ? 1 : 0 );
        if (columnModel != null)
        {
            Column[] columns = columnModel.getColumns();
            if ( columns != null )
            {
                for ( int i = 0; i < columns.length; i++ )
                {
                    if ( columns[ i ].getEnabled() )
                    {
                        if ( columns[ i ] == column )
                        {
                            return cnt;
                        }
                        cnt++;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public Object getModelForRow( int row )
    {
        row = (indices == null) ? row : indices[row].intValue();
        return row < 0 ? null : rm.getBean(row);
    }

    @Override
    synchronized public void propertyChange( final PropertyChangeEvent evt )
    {
        super.propertyChange( evt );
        String name = evt.getPropertyName();
        if ( evt.getSource() != columnModel )
        {
            if ( name.equals( "sorting" ) )
            {
                Column column = ( Column )evt.getSource();
                if ( !column.getEnabled() )
                {
                    return;
                }
                sort();
            }
        }
    }

    @Override
    synchronized public void tableChanged( RowModelEvent evt )
    {
        indices = null;
        super.tableChanged( evt );
        sort();
    }

    private TableSorter sorter = null;

    @Override
    public void sort()
    {
        if ( sorter == null )
        {
            sorter = new TableSorter( SortedBeanTableModelAdapter.this );
        }
        sorter.sort();
        fireTableDataChanged();
        fireTableChanged(
            new TableModelEvent( SortedBeanTableModelAdapter.this, TableModelEvent.HEADER_ROW ) );
    }

/*
    public void sort( int column, boolean isAscent )
    {
        if ( sorter == null )
        {
            sorter = new TableSorter( SortedBeanTableModelAdapter.this );
        }
        sorter.sort( column, isAscent );


        fireTableDataChanged();
        fireTableChanged(
            new TableModelEvent( SortedBeanTableModelAdapter.this, TableModelEvent.HEADER_ROW ) );
    }
*/

}
