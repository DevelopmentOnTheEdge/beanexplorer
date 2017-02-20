package com.developmentontheedge.beans.swing.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.developmentontheedge.beans.Option;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.swing.PropertyInspector;
import com.developmentontheedge.beans.swing.TabularPropertyInspector;

/**
 * Represents column description for all the columns in the table
 * @see TabularPropertyInspector
 */
public class ColumnModel extends Option implements PropertyChangeListener
{
    ////////////////////////////////////////
    // Constructors
    //

    ColumnModel( ComponentModel model, int showMode )
    {
        initColumnNames( model, showMode );
    }

    ColumnModel( ComponentModel model )
    {
        this( model, PropertyInspector.SHOW_USUAL );
    }


    /**
     * Creates column model from set of columns.
     * @param fields array of columns
     */
    public ColumnModel( Column[] fields )
    {
        setColumns( fields );
    }

    /**
     * Creates column model for all properties visible
     * in the specified show mode of the template bean.
     * @param templateBean the bean to be used to initialize columns
     * @param showMode should be one of SHOW_USUAL, SHOW_EXPERT, SHOW_HIDDEN, SHOW_PREFERRED
     */
    public ColumnModel( Object templateBean, int showMode )
    {
        this( ComponentFactory.getModel( templateBean, ComponentFactory.Policy.UI ), showMode );
    }

    /**
     * Creates column model for all properties visible
     * in the specified show mode of the template bean.
     * @param templateBeanClass the class of the bean to be used to initialize field names.
     * @param showMode should be one of SHOW_USUAL, SHOW_EXPERT, SHOW_HIDDEN, SHOW_PREFERRED
     */
    public ColumnModel( Class<?> templateBeanClass, int showMode )
    {
        this( ComponentFactory.getModel( templateBeanClass, ComponentFactory.Policy.UI ), showMode );
    }

    /**
     * Creates a column model for all visible properties of the template bean.
     * @param templateBean the bean to be used to initialize field names.
     */
    public ColumnModel( Object templateBean )
    {
        this( ComponentFactory.getModel( templateBean, ComponentFactory.Policy.UI ) );
    }


    private ComponentModel columnsModel = null;

    /**
     * Returns component model of this table.
     * Component model contains all properties to be shown in the table.
     */
    ComponentModel getColumnsModel()
    {
        return columnsModel;
    }


    ////////////////////////////////////////
    // Properties
    //
    private boolean rowNumbersVisible;

    /**
     * Returns visibilty of row numbers.
     * @return <true> if in first column of the table displays number of the row.
     */
    public boolean isRowNumbersVisible()
    {
        return rowNumbersVisible;
    }

    /**
     * Sets visibilty of row numbers.
     * @param value visibility of 'row number' column.
     */
    public void setRowNumbersVisible( boolean value )
    {
        boolean oldValue = rowNumbersVisible;
        rowNumbersVisible = value;
        firePropertyChange( "rowNumbersVisible", oldValue, value );
    }

    /** Array of columns. */
    private Column[] columns = new Column[ 0 ];

    /**
     * Returns array of columns.
     * @todo HIGH Return sortedFieldOptions or FieldOptions.
     */
    public Column[] getColumns()
    {
        return columns;
    }

    /**
     * Set new columns for this column model.
     * @param fields Array of new columns.
     */
    public void setColumns( Column[] fields )
    {
        columns = fields;
        for(int i = fields.length - 1; i >= 0; i--)
        {
            fields[i].removePropertyChangeListener(this);
            fields[i].addPropertyChangeListener(this);
        }
    }

    /**
     * Returns a column by index.
     */
    public Column getColumns( int i )
    {
        return getColumns()[i];
    }

    /**
     * Sets a column by index.
     * @param i index of column.
     * @param field new column.
     */
    public void setColumns( int i, Column field )
    {
        getColumns()[i] = field;
        field.removePropertyChangeListener(this);
        field.addPropertyChangeListener(this);
    }

    private boolean sortEnabled;

    /**
     * Indicates whether column sorting is enabled
     * @return value indicating whether column sorting is enabled
     */
    public boolean isSortEnabled()
    {
        return sortEnabled;
    }

    /**
     * Specifies whether column sorting must be enabled
     * @param sortEnabled value indicating whether column sorting must be enabled
     */
    public void setSortEnabled( boolean sortEnabled )
    {
        this.sortEnabled = sortEnabled;
        firePropertyChange( "sortEnabled", null, null );
        firePropertyChange( "fieldOptions", null, null );
    }

    /**
     * Set new show mode for the table columns.
     */
    public void setShowMode( int showMode )
    {
        if ( columnsModel == null )
        {
            return;
        }
        int num = columnsModel.getPropertyCount();
        for ( int i = 0; i < num; i++ )
        {
            boolean isVisible = columnsModel.getPropertyAt( i ).isVisible( showMode );
            columns[ i ].setEnabled( isVisible );
        }
    }

    /** Init name to display in the columns headers */
    protected void initColumnNames( ComponentModel columnsModel, int showMode )
    {
        this.columnsModel = columnsModel;
        columns = new Column[ 0 ];
        if ( columnsModel == null )
        {
            return;
        }
        int num = columnsModel.getPropertyCount();
        columns = new Column[ num ];
        for ( int i = 0; i < num; i++ )
        {
            Property property = columnsModel.getPropertyAt( i );
            boolean isVisible = property.isVisible( showMode );

            columns[ i ] = new ColumnWithSort( this, property.getName(), property.getDisplayName(), isVisible );
        }
    }

    /**
     * Returns display name for column.
     * Used by {@link ColumnModelBeanInfo}.
     * @param index Index of column.
     * @param obj Not used.
     * @return display name for column.
     */
    public String calcEntryNameInArray( Integer index, Object obj )
    {
        return getColumns(index.intValue()).getName();
    }


    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *      and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        firePropertyChange(evt);
    }
}
