package com.developmentontheedge.beans.swing.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;

/**
 * This is an implementation of <code>RowModel</code> that
 * uses a <code>Vector</code> to store the beans.
 */
public class DefaultRowModel extends AbstractRowModel implements PropertyChangeListener
{
    static private final int INITIAL_CAPACITY = 64;

    /** Returns a number of beans in this model. */
    @Override
    public int size()
    {
        return beans.size();
    }

    /**
     * Returns bean for teh specified row.
     * @param row row number from 0 to size()-1.
     * @return Bean at the specified row.
     * @throws ArrayIndexOutOfBoundsException row is out of range (row < 0 || row >= size()).
     */
    @Override
    public Object getBean( int row )
    {
        return beans.get( row );
    }

    /** Appends bean to the list in this model */
    public void add( Object bean )
    {
        add( size(), bean );
    }

    /**
     * Insert bean before specified row into this row model.
     * @param row Row before which a bean will be inserted. (must be >=0 && <=size())
     * @param bean a bean to be inserted.
     */
    public void add( int row, Object bean )
    {
        beans.add( row, bean );
        addListener( bean );
        fireTableRowsInserted( row, row );
    }

    /**
     * Change a bean at the specified row of this row model.
     * @param row row where bean should be replaced
     * @param bean new bean to used at this row
     * @throws ArrayIndexOutOfBoundsException if the row was invalid.
     */
    public void change( int row, Object bean )
    {
        Object oldBean = beans.get( row );
        removeListener( oldBean );
        beans.setElementAt( bean, row );
        addListener( bean );
        fireTableRowsUpdated( row, row );
    }

    /**
     * Removes specified row from this row model.
     * @param row row to remove
     * @throws ArrayIndexOutOfBoundsException row out of range (row < 0 || row >= size()).
     */
    public void remove( int row )
    {
        Object bean = beans.get( row );
        removeListener( bean );
        beans.remove( bean );
        fireTableRowsDeleted( row, row );
    }

    /**
     * Listens changes in the list of beans and forwards them to the table model.
     * Do not call this method directly.
     * @param evt Information about bean's property changes.
     * @todo optimize, not whole table should be redrawn, only row(s) whith changed beans,
     *       maybe BeanEvents should be used?
     */
    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        fireTableDataChanged();
    }

    /**
     * Adds listener to the component model of specified bean.
     * @param bean bean to listen properties of
     * @see ComponentModel#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    protected void addListener( Object bean )
    {
        ComponentModel model = ComponentFactory.getModel( bean, ComponentFactory.Policy.UI );
        if ( model != null )
        {
            model.addPropertyChangeListener( this );
        }
    }

    /**
     * Removes property change listener from component model of the specified bean.
     * @param bean bean to remove listener for
     * @see ComponentModel#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    protected void removeListener( Object bean )
    {
        ComponentModel model = ComponentFactory.getModel( bean, ComponentFactory.Policy.UI );
        if ( model != null )
        {
            model.removePropertyChangeListener( this );
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Private

    /** Vector for storing all beans of this bean table model. */
    private final Vector beans = new Vector( INITIAL_CAPACITY );
}
