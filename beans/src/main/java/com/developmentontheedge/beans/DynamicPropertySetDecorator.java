/** $Id: DynamicPropertySetDecorator.java,v 1.5 2013/07/16 10:30:40 zha Exp $ */
package com.developmentontheedge.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.Map;

/**
 * Decorator of DynamicPropertySet
 *
 * @see com.developmentontheedge.beans.DynamicPropertySet
 */
@SuppressWarnings( "serial" )
public class DynamicPropertySetDecorator implements DynamicPropertySet
{
    protected DynamicPropertySet delegateDps = null;

    public DynamicPropertySetDecorator(DynamicPropertySet delegateDps )
    {
        this.delegateDps = delegateDps;
    }

    public DynamicPropertySet getDelegate()
    {
        return delegateDps;
    }

    public void setPropertyAttribute( String propName, String attrName, Object attrValue )
    {
        delegateDps.setPropertyAttribute( propName, attrName, attrValue );
    }

    public void renameProperty( String from, String to )
    {
        delegateDps.renameProperty( from, to );
    }

    public Class<?> getType( String name )
    {
        return delegateDps.getType( name );
    }

    public Object getValue( String name )
    {
        return delegateDps.getValue( name );
    }

    public String getValueAsString( String name )
    {
        return delegateDps.getValueAsString( name );
    }

    public Long getValueAsLong( String name )
    {
        return delegateDps.getValueAsLong( name );
    }

    public <T> T cast( String name, Class<T> clazz )
    {
        return clazz.cast( getValue( name ) ); 
    }

    public void setValue( String name, Object value )
    {
        delegateDps.setValue( name, value );
    }

    public DynamicProperty getProperty( String name )
    {
        return delegateDps.getProperty( name );
    }

    public PropertyDescriptor getPropertyDescriptor( String name )
    {
        return delegateDps.getPropertyDescriptor( name );
    }

    public void add( DynamicProperty property )
    {
        delegateDps.add( property );
    }

    public boolean addBefore( String propName, DynamicProperty property )
    {
        return delegateDps.addBefore( propName, property );
    }

    public boolean addAfter( String propName, DynamicProperty property )
    {
        return delegateDps.addAfter( propName, property );
    }

    public boolean contains( DynamicProperty property )
    {
        return delegateDps.contains( property );
    }

    public Object remove( String name )
    {
        return delegateDps.remove( name );
    }

    public boolean moveTo( String name, int index )
    {
        return delegateDps.moveTo( name, index );
    }

    public boolean replaceWith( String name, DynamicProperty prop )
    {
        return delegateDps.replaceWith( name, prop );
    }

    public Object clone()
    {
        return delegateDps.clone();
    }

    public Iterator<String> nameIterator()
    {
        return delegateDps.nameIterator();
    }

    public Iterator<DynamicProperty> propertyIterator()
    {
        return delegateDps.propertyIterator();
    }

    public Iterator<DynamicProperty> iterator()
    {
        return delegateDps.iterator();
    }

    public Map<String, Object> asModifiableMap()
    {
        return delegateDps.asModifiableMap();
    }

    public Map<String, Object> asMap()
    {
        return delegateDps.asMap();
    }

    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        delegateDps.addPropertyChangeListener( listener );
    }

    public void addPropertyChangeListener( String propertyName, PropertyChangeListener listener )
    {
        delegateDps.addPropertyChangeListener( propertyName, listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
        delegateDps.removePropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( String propertyName, PropertyChangeListener listener )
    {
        delegateDps.removePropertyChangeListener( propertyName, listener );
    }

    public void firePropertyChange( String propertyName, Object oldValue, Object newValue )
    {
        delegateDps.firePropertyChange( propertyName, oldValue, newValue );
    }

    public void firePropertyChange( PropertyChangeEvent evt )
    {
        delegateDps.firePropertyChange( evt );
    }

    public boolean hasListeners( String propertyName )
    {
        return delegateDps.hasListeners( propertyName );
    }

    public String toString()
    {
        return "DECORATOR->" + delegateDps.toString();
    }

    public int size()
    {
        return delegateDps.size();
    }

    public boolean isEmpty()
    {
        return delegateDps.isEmpty();
    }

    @Override
    public DynamicPropertyBuilder getAsBuilder(String name) {
        return delegateDps.getAsBuilder(name);
    }
}
