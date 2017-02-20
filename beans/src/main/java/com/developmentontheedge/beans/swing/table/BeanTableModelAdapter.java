package com.developmentontheedge.beans.swing.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import com.developmentontheedge.beans.log.Logger;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;

/** Adapter for adapt RowModel interface into TableModel interface. */
public class BeanTableModelAdapter extends AbstractTableModel implements RowModelListener, PropertyChangeListener
{
    public BeanTableModelAdapter(RowModel rm, ColumnModel columnModel)
    {
        this.rm = rm;
        //this.rm.addRowModelListener( this );
        this.columnModel = columnModel;
        //columnModel.addPropertyChangeListener( this );
        registerListeners();
    }

    public void registerListeners()
    {
        unregisterListeners();
        if( rm != null )
        {
            if( PropertyChangeListener.class.isAssignableFrom( rm.getClass() ) )
            {
                for( int i = rm.size() - 1; i >= 0; i-- )
                {
                    ComponentModel model = ComponentFactory.getModel( rm.getBean(i), ComponentFactory.Policy.UI );
                    model.addPropertyChangeListener( (PropertyChangeListener)rm );
                }
            }
            rm.addRowModelListener( this );
        }
        if( columnModel != null )
        {
            columnModel.addPropertyChangeListener( this );
        }
    }

    public void unregisterListeners()
    {
        if( rm != null )
        {
            if( PropertyChangeListener.class.isAssignableFrom( rm.getClass() ) )
            {
                for( int i = rm.size() - 1; i >= 0; i-- )
                {
                    ComponentModel model = ComponentFactory.getModel( rm.getBean(i), ComponentFactory.Policy.UI );
                    model.removePropertyChangeListener( (PropertyChangeListener)rm );
                }
            }
            rm.removeRowModelListener( this );
        }
        if( columnModel != null )
        {
            columnModel.removePropertyChangeListener( this );
        }
    }

    ////////////////////////////////////////
    // Properties
    //

    protected RowModel rm;

    public RowModel getRowModel()
    {
        return rm;
    }

    protected ColumnModel columnModel;

    public ColumnModel getColumnModel()
    {
        return columnModel;
    }

    private boolean rowHeader = false;

    public boolean getRowHeader()
    {
        return rowHeader;
    }

    /** @todo May be propertyChange event needed or tableChange. */
    public void setRowHeader(boolean rowHeader)
    {
        this.rowHeader = rowHeader;
        if( columnModel != null )
        {
            columnModel.setRowNumbersVisible( rowHeader );
        }
        fireTableStructureChanged();
    }


    ////////////////////////////////////////
    // TableModel implementation
    //
    // PENDING: currently we use plane table model, then
    // we can have complex columns

    @Override
    public int getRowCount()
    {
        return rm != null ? rm.size() : 0;
    }

    @Override
    synchronized public int getColumnCount()
    {
        int cnt = 0;
        if( columnModel != null )
        {
            Column[] fieldOptions = columnModel.getColumns();
            for( int i = 0; i < fieldOptions.length; i++ )
            {
                if( fieldOptions[i].getEnabled() )
                {
                    cnt++;
                }
            }
        }
        return cnt + ( getRowHeader() ? 1 : 0 );
    }

    @Override
    synchronized public String getColumnName(int index)
    {
        index -= ( getRowHeader() ? 1 : 0 );
        int cnt = 0;
        if( columnModel != null )
        {
            Column[] columns = columnModel.getColumns();
            for( int i = 0; i < columns.length; i++ )
            {
                if( columns[i].getEnabled() )
                {
                    if( cnt == index )
                    {
                        String name = columns[i].getName();
                        return name.replaceAll( "\\|", " " );
                    }
                    cnt++;
                }
            }
        }
        return null;
    }

    synchronized protected String getColumnKey(int index)
    {
        index -= ( getRowHeader() ? 1 : 0 );
        int cnt = 0;
        if( columnModel != null )
        {
            Column[] columns = columnModel.getColumns();
            for( int i = 0; i < columns.length; i++ )
            {
                if( columns[i].getEnabled() )
                {
                    if( cnt == index )
                    {
                        return columns[i].getColumnKey();
                    }
                    cnt++;
                }
            }
        }
        return null;
    }

    /** @todo Remove temp implementation */
    @Override
    public Class<?> getColumnClass(int column)
    {
        return ComponentFactory.getPropertyClassInObfuscatedVersion().getSuperclass();
    }

    /** @todo are there fields which has getPropertyAt(row, column) == null? */
    @Override
    public boolean isCellEditable(int row, int column)
    {
        Property property = (Property)getValueAt( row, column );
        return property == null ? true : !property.isReadOnly();
    }

    @Override
    public Object getValueAt(int row, int column)
    {
        return getPropertyAt( row, column );
    }

    @Override
    public void setValueAt(Object aValue, int row, int column)
    {
        Property property = getPropertyAt( row, column );
        try
        {
            if( aValue instanceof Property )
            {
                aValue = ( (Property)aValue ).getValue();
            }
            property.setValue( aValue );
            fireTableDataChanged();
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "BeanTableModelAdapter:", e );
        }
    }

    /** @pending high optimize log messages */
    protected Property getPropertyAt(int row, int column)
    {
        try
        {
            if( column == 0 && getRowHeader() )
            {
                RowHeaderBean rowHeaderBean = new RowHeaderBean();
                rowHeaderBean.setNumber( row + 1 );
                ComponentModel rowHeaderModel = ComponentFactory.getModel( rowHeaderBean, ComponentFactory.Policy.UI );
                return rowHeaderModel.findProperty( "number" );
            }

            String propertyName = getColumnKey( column );
            if( propertyName == null )
            {
                throw new ArrayIndexOutOfBoundsException( "Name for column " + column + " not found." );
            }

            Object bean = rm.getBean( row );
            if( bean == null )
            {
                throw new ArrayIndexOutOfBoundsException( "Bean for row " + row + " not found." );
            }
            Property property = ComponentFactory.getModel( bean, ComponentFactory.Policy.UI ).findProperty( propertyName );
            //            Property property = componentModel.findProperty(propertyName);
            //Logger.debug( cat,"property = " + property + "; value = " +
            //(property == null ? "null" : property.getValue()) + " Model=" + beanModel );
            return property;
        }
        catch( ArrayIndexOutOfBoundsException exc )
        {
            Logger.getLogger().error( "BeanTableModelAdapter:", exc );
            throw exc;
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "BeanTableModelAdapter:", e );
        }
        return null;
    }

    public Object getModelForRow(int row)
    {
        return row < 0 ? null : rm.getBean( row );
    }

    ////////////////////////////////////////
    // implements PropertyChangeListener
    //

    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *      and the property that has changed.
     */
    @Override
    synchronized public void propertyChange(final PropertyChangeEvent evt)
    {
        String name = evt.getPropertyName();
        if( evt.getSource() == columnModel )
        {
            if( name.equals( "rowNumbersVisible" ) )
            {
                setRowHeader( ( (Boolean)evt.getNewValue() ).booleanValue() );
            }
            if( name.equals( "sortEnabled" ) )
            {
                this.fireTableChanged( new TableModelEvent( this, TableModelEvent.HEADER_ROW ) );
            }
            //...
        }
        else
        {
            String sourceName = ( (Column)evt.getSource() ).getName();
            if( name.equals( "enabled" ) )
            {
                fireTableStructureChanged();
            }
        }
    }

    @Override
    synchronized public void tableChanged(RowModelEvent evt)
    {
        if( evt == null )
            fireTableChanged( new TableModelEvent( this ) );
        else
            fireTableChanged( new TableModelEvent( this, evt.getFirstRow(), evt.getLastRow(), TableModelEvent.ALL_COLUMNS, evt.getType() ) );
    }
}
