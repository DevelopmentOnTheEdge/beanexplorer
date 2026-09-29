package com.developmentontheedge.beans.swing.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Map;

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

    /**
     * Maximum number of per-bean ComponentModels kept in {@link #modelCache}.
     * Package-private so the test in the same package can reference it.
     */
    static final int MODEL_CACHE_SIZE = 1000;

    /**
     * Identity key for the model cache: equal only for the same object.
     */
    private static final class IdKey
    {
        private final Object object;

        IdKey( Object object )
        {
            this.object = object;
        }

        @Override
        public int hashCode()
        {
            return System.identityHashCode( object );
        }

        @Override
        public boolean equals( Object obj )
        {
            return obj instanceof IdKey && ( (IdKey)obj ).object == object;
        }
    }

    // TODO(follow-up): invalidate the per-bean entry on structural
    //   propertyChange (add/remove of a property) and confirm the
    //   listeners registered on bean models are removed, not just
    //   re-registered, across cache clears.
    /**
     * Cache of per-bean ComponentModels used by getPropertyAt so that the
     * expensive introspection path of ComponentFactory.getModel is not
     * repeated on every cell query.
     * <p>Keyed by bean identity (see {@link IdKey}) so that the adapter
     * does not add its own conflation of distinct beans. Note this is
     * necessary but not sufficient: ComponentFactory's own cache
     * (instanceList) is equals()-keyed, so two distinct beans that are
     * equals() still share a model until that is fixed (see
     * <a href="https://github.com/DevelopmentOnTheEdge/beanexplorer/issues/10">issue 10</a>).
     * Bounded in size (LRU), so the cache retains at most
     * {@link #MODEL_CACHE_SIZE} beans and does not keep rows alive for the
     * adapter's lifetime.
     * <p>Lock order is {@code this} (the synchronized event listeners)
     * then {@code modelCache}; getPropertyAt takes only modelCache. The
     * check-and-fill and the clear are each done under the modelCache lock,
     * so a concurrent clear cannot interleave between a fill's get() and
     * put(). (A thread may still read a bean before a tableChanged and fill
     * after the clear, leaving a bounded entry for a bean that may no
     * longer be in the table; it is identity-keyed and ages out, never
     * wrongly hit.)
     */
    private final Map<IdKey, ComponentModel> modelCache =
            new LinkedHashMap<IdKey, ComponentModel>( 64, 0.75f, true )
    {
        @Override
        protected boolean removeEldestEntry( Map.Entry<IdKey, ComponentModel> entry )
        {
            return size() > MODEL_CACHE_SIZE;
        }
    };

    // ---- test support (package-private, not part of the public API) ----

    /**
     * Returns the cached ComponentModel for the given bean, or null if the
     * bean is not currently cached.
     * <p>Note: this calls {@code get()} on an access-ordered map, so it
     * updates the LRU order as a side effect. Tests that assert LRU order
     * should use {@link #containsCachedModel(Object)} instead.
     */
    ComponentModel cachedModelFor( Object bean )
    {
        synchronized( modelCache )
        {
            return modelCache.get( new IdKey( bean ) );
        }
    }

    /**
     * Returns whether a model is currently cached for the given bean,
     * without changing the LRU order.
     */
    boolean containsCachedModel( Object bean )
    {
        synchronized( modelCache )
        {
            return modelCache.containsKey( new IdKey( bean ) );
        }
    }

    /**
     * Returns the number of entries in the cache.
     */
    int cacheSize()
    {
        synchronized( modelCache )
        {
            return modelCache.size();
        }
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
            // Use cached model to avoid re-introspecting the bean on every cell query.
            // The expensive initProperties / addPropertyChangeListener path in
            // ComponentFactory.getModel is executed only once per distinct bean.
            // The whole check-and-fill is done under the cache lock so a
            // concurrent tableChanged cannot clear the cache between the
            // lookup and the put of a stale model.
            ComponentModel model;
            synchronized( modelCache )
            {
                IdKey key = new IdKey( bean );
                model = modelCache.get( key );
                if( model == null )
                {
                    model = ComponentFactory.getModel( bean, ComponentFactory.Policy.UI );
                    if( model == null )
                    {
                        return null;
                    }
                    modelCache.put( key, model );
                }
            }
            return model.findProperty( propertyName );
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
        // Clear cached models on every refresh, including a null event,
        // which means "everything changed" and is exactly the case where
        // stale entries would otherwise survive. Guarded by the cache lock
        // so a concurrent fill cannot re-insert a stale model after the
        // clear.
        synchronized( modelCache )
        {
            modelCache.clear();
        }
        if( evt == null )
            fireTableChanged( new TableModelEvent( this ) );
        else
            fireTableChanged( new TableModelEvent( this, evt.getFirstRow(), evt.getLastRow(), TableModelEvent.ALL_COLUMNS, evt.getType() ) );
    }
}
