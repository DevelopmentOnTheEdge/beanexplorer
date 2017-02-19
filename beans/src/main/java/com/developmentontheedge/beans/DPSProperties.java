package com.developmentontheedge.beans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * Properties interface implementation based on DynamicPropertySet instead of Hashtable
 * @author lan
 */
public class DPSProperties extends Properties
{
    private static final long serialVersionUID = 1L;
    private final DynamicPropertySet dps;
    
    public DPSProperties(DynamicPropertySet dps)
    {
        super();
        this.dps = dps;
    }

    @Override
    public synchronized void clear()
    {
        Iterator<String> nameIterator = dps.nameIterator();
        List<String> names = new ArrayList<>();
        while(nameIterator.hasNext())
            names.add(nameIterator.next());
        for(String name: names) dps.remove(name);
    }

    @Override
    public synchronized Object clone()
    {
        return new DPSProperties((DynamicPropertySet)dps.clone());
    }

    @Override
    public synchronized boolean contains(Object element)
    {
        Iterator<DynamicProperty> iterator = dps.propertyIterator();
        while(iterator.hasNext())
        {
            DynamicProperty property = iterator.next();
            try
            {
                if(property.getValue().equals(element)) return true;
            }
            catch(Exception e)
            {
            }
        }
        return false;
    }

    @Override
    public synchronized boolean containsKey(Object elementName)
    {
        return dps.getProperty(elementName.toString()) != null;
    }

    @Override
    public boolean containsValue(Object element)
    {
        return contains(element);
    }

    @Override
    public synchronized Enumeration<Object> elements()
    {
        return Collections.enumeration(asMap().values());
    }
    
    private Map<Object, Object> asMap()
    {
        Map<Object, Object> result = new HashMap<>();
        Iterator<DynamicProperty> iterator = dps.propertyIterator();
        while(iterator.hasNext())
        {
            DynamicProperty property = iterator.next();
            result.put(property.getName(), property.getValue());
        }
        return result;
    }

    @Override
    public Set<Entry<Object, Object>> entrySet()
    {
        return asMap().entrySet();
    }

    @Override
    public synchronized Object get(Object elementName)
    {
        return dps.getValue(elementName.toString());
    }

    @Override
    public synchronized boolean isEmpty()
    {
        return dps.isEmpty();
    }

    @Override
    public synchronized Enumeration<Object> keys()
    {
        return Collections.enumeration(asMap().keySet());
    }

    @Override
    public Set<Object> keySet()
    {
        return asMap().keySet();
    }

    @Override
    public synchronized Object put(Object key, Object value)
    {
        DynamicProperty property = dps.getProperty(key.toString());
        if(property == null)
        {
            property = new DynamicProperty(key.toString(), value==null?String.class:value.getClass(), value);
            dps.add(property);
            return null;
        }
        Object oldValue = property.getValue();
        property.setValue(value);
        return oldValue;
    }

    @Override
    public synchronized void putAll(Map<? extends Object, ? extends Object> map)
    {
        map.forEach( this::put );
    }

    @Override
    public synchronized Object remove(Object key)
    {
        Object obj = dps.getValue(key.toString());
        dps.remove(key.toString());
        return obj;
    }

    @Override
    public synchronized int size()
    {
        return dps.size();
    }

    @Override
    public Collection<Object> values()
    {
        return asMap().values();
    }

    /*
     * This should be unnecessary to reimplement, but for some reason java.util.Properties implementation uses super.get instead of get.
     * Curiously enough setProperty implemented correctly, so no need to reimplement
     */
    @Override
    public String getProperty(String key)
    {
        Object oval = get(key);
        String sval = (oval instanceof String) ? (String)oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }
}
