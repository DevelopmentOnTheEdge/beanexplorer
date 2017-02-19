package com.developmentontheedge.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Wraps Properties object into DynamicPropertySet
 * @author lan
 */
public class PropertiesDPS extends Option implements DynamicPropertySet
{
    private static final long serialVersionUID = 1L;
    private final Properties properties;
    private boolean readOnly;

    public PropertiesDPS(Properties properties)
    {
        this.properties = properties;
    }
    
    public PropertiesDPS(Properties properties, boolean readOnly)
    {
        this.properties = properties;
        this.readOnly = readOnly;
    }
    
    @Override
    public Class<?> getType(String name)
    {
        try
        {
            return properties.get(name).getClass();
        }
        catch( Exception e )
        {
            return null;
        }
    }

    @Override
    public Object getValue(String name)
    {
        return properties.get(name);
    }

    @Override
    public String getValueAsString( String name )
    {
        Object val = getValue( name );
        if( val == null )
            return null;
        return val.toString();
    }

    @Override
    public void setValue(String name, Object value)
    {
        if(readOnly) throw new SecurityException();
        properties.put(name, value);
    }

    @Override
    public PropertyDescriptor getPropertyDescriptor(String name)
    {
        try
        {
            return getProperty(name).getDescriptor();
        }
        catch( Exception e )
        {
            return null;
        }
    }

    @Override
    public DynamicProperty getProperty(String name)
    {
        try
        {
            Object value = properties.get(name);
            DynamicProperty dynamicProperty = new DynamicProperty(name, value.getClass(), value);
            dynamicProperty.setReadOnly(readOnly);
            return dynamicProperty;
        }
        catch( Exception e )
        {
            return null;
        }
    }

    @Override
    public void renameProperty(String from, String to)
    {
        if(readOnly) throw new SecurityException();
        Object value = properties.get(from);
        properties.remove(from);
        properties.put(to, value);
    }

    @Override
    public void add(DynamicProperty property)
    {
        if(readOnly) throw new SecurityException();
        properties.put(property.getName(), property.getValue());
    }

    @Override
    public boolean addBefore(String propName, DynamicProperty property)
    {
        if(!properties.containsKey(propName)) return false;
        add(property);
        return true;
    }

    @Override
    public boolean addAfter(String propName, DynamicProperty property)
    {
        if(!properties.containsKey(propName)) return false;
        add(property);
        return true;
    }

    @Override
    public boolean contains(DynamicProperty property)
    {
        return properties.containsKey(property.getName());
    }

    @Override
    public Object remove(String name)
    {
        if(readOnly) throw new SecurityException();
        if(!properties.containsKey(name)) return null;
        return properties.remove(name);
    }

    @Override
    public boolean moveTo(String name, int index)
    {
        if(readOnly) throw new SecurityException();
        if(!properties.containsKey(name)) return false;
        return true;
    }

    @Override
    public boolean replaceWith(String name, DynamicProperty prop)
    {
        if(readOnly) throw new SecurityException();
        if(!properties.containsKey(name)) return false;
        properties.put(name, prop.getValue());
        return true;
    }

    @Override
    public Iterator<String> nameIterator()
    {
        return properties.keySet().stream().map(Object::toString).iterator();
    }

    @Override
    public Iterator<DynamicProperty> propertyIterator()
    {
        return properties.keySet().stream().map(Object::toString).map(this::getProperty).iterator();
    }

    @Override
    public Iterator<DynamicProperty> iterator()
    {
        return propertyIterator();
    }

    @Override
    public Map<String, Object> asMap()
    {
        @SuppressWarnings ( "unchecked" )
        Map<String, Object> map = (Map<String, Object>)(Map<?,?>)properties;
        if(readOnly)
            map = Collections.unmodifiableMap( map );
        return map;
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        // TODO Use propertyName
        addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        // TODO Use propertyName
        removePropertyChangeListener(listener);
    }

    @Override
    public boolean hasListeners(String propertyName)
    {
        // TODO Use propertyName
        return listenerList != null && listenerList.getListenerCount() > 0;
    }

    @Override
    public int size()
    {
        return properties.size();
    }

    @Override
    public boolean isEmpty()
    {
        return properties.isEmpty();
    }

    @Override
    public Object clone()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void firePropertyChange(PropertyChangeEvent evt)
    {
        super.firePropertyChange(evt);
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        super.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public void setPropertyAttribute( String propName, String attrName, Object attrValue )
    {
        throw new UnsupportedOperationException();
    }
}
