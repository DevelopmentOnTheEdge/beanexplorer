package com.developmentontheedge.beans;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.developmentontheedge.beans.log.Logger;

/**
 * Implementation of DynamicPropertySet as map.
 *
 * By default TreeMap is used. String comparator is used to sort properties by name 
 * in the map.
 *
 * @see java.util.TreeMap
 */
public class DynamicPropertySetAsMap extends AbstractDynamicPropertySet
{
    private static final long serialVersionUID = 1L;
	protected Map<String, DynamicProperty> propertiesMap;

    public DynamicPropertySetAsMap()
    {
        propertiesMap = new TreeMap<>(new PropertyComparator());
    }

    public static class PropertyComparator implements Comparator<String>, Serializable
    {
        static final long serialVersionUID = 1L;

        @Override
        public int compare(String o1, String o2)
        {
            return o1.compareTo(o2);
        }

        @Override
        public boolean equals(Object obj)
        {
            return this == obj;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Access methods
    //

    /** Checks if property does exist in this set */
    @Override
    public boolean contains(DynamicProperty property)
    {
        return propertiesMap.containsValue(property);
    }

    /** Find the property descriptor by its name. */
    @Override
    protected DynamicProperty findProperty(String name)
    {
        return propertiesMap.get(name);
    }

    @Override
    public void renameProperty( String from, String to )
    {
        throw new RuntimeException( "Not yet implmented!" );
    }

    @Override
    public Object clone()
    {
        DynamicPropertySetAsMap retVal = (DynamicPropertySetAsMap)super.clone();
        retVal.pcSupport = null;
        retVal.propertiesMap = new TreeMap<>(new PropertyComparator());
        for( Iterator<DynamicProperty> iter = propertyIterator(); iter.hasNext(); )
        {
            DynamicProperty prop = iter.next();
   
            try
            {
                retVal.add( DynamicPropertySetSupport.cloneProperty( prop ) );
            }
            catch( java.beans.IntrospectionException wierd )
            {
                Logger.getLogger().error( "Unable to clone property " + prop.getName(), wierd );
            }
        }
        return retVal;
    }

    @Override
    public Iterator<String> nameIterator()
    {
        return propertiesMap.keySet().iterator();
    }

    @Override
    public Iterator<DynamicProperty> propertyIterator()
    {
        return propertiesMap.values().iterator();
    }

    @Override
    public Map<String, Object> asMap()
    {
        HashMap<String, Object> viewMap = new HashMap<>( propertiesMap.size() );
        for( Iterator<Map.Entry<String, DynamicProperty>> entries = propertiesMap.entrySet().iterator(); entries.hasNext(); )
        {
            Map.Entry<String, DynamicProperty> entry = entries.next();
            DynamicProperty prop = entry.getValue();
            viewMap.put( entry.getKey(), prop.getValue() );
        }

        return Collections.unmodifiableMap( viewMap );
    }

    /** Adds the specified property. */
    @Override
    public void add(DynamicProperty property)
    {
        Object oldValue = getValue(property.getName());
        propertiesMap.put(property.getName(), property);
        property.setParent(this);

        if(hasListeners(property.getName()))
        {
            Object newValue = property.getValue();
            if(oldValue != newValue && (oldValue == null || newValue == null || !oldValue.equals(newValue)))
                firePropertyChange(new PropertyChangeEvent(this, property.getName(), oldValue, newValue));
        }
            
    }

    @Override
    public Object remove(String name)
    {
        if(propertiesMap.containsKey(name))
        {
            DynamicProperty property = propertiesMap.get(name);
            property.setParent(null);
            propertiesMap.remove(name);

            return property.getValue();
        }

        return null;
    }

    @Override
    public boolean addBefore( String propName, DynamicProperty property )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAfter( String propName, DynamicProperty property )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean moveTo(String name, int index)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean replaceWith(String name, DynamicProperty prop)
    {
        if( findProperty( name ) != null )
        {
            remove(name); 
            add(prop);
            return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // listener issues
    //

    @Override
    public int size()
    {
        return propertiesMap.size();
    }

    @Override
    public String toString()
    {
        StringWriter out = new StringWriter();
        printProperties( out );
        return "DynamicPropertySetAsMap:" + out.toString();
    }

    protected void printProperties( Writer out )
    {
        try
        {
            int i=0;
            for( DynamicProperty dp: propertiesMap.values() )
            {
                i++;
                out.write( "\n  " + ( i + 1 ) + ". " );
                out.write( dp.getName() + " (" + dp.getType().getSimpleName() + ") - " + dp.getValue() );
            }
        }
        catch( Exception e )
        {
        }
    }
}

