package com.developmentontheedge.beans;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Implimentation of DynamicPropertySet as map
 * to store and edit application preferences.
 *
 * Methods get/setValue, getProperty, getType, getPropertyDescriptor uses
 * findProperty method that supports access to internal properties in preferences tree.
 * The property name format is following: [parent/]*child
 *
 * @pending stored property can be only one of the allowed type:
 * String, Boolean, Int, Long, Float, Double, Preferences (this allows
 * to organise preferences in tree structure).
 */
public class Preferences extends DynamicPropertySetAsMap
{
    private static final long serialVersionUID = 1L;

    /** Utility constant to save preferences for dialogs. */
    public static final String DIALOGS = "dialogs";

    public Preferences()
    {
        super();
    }

    public Preferences(DynamicPropertySet dps)
    {
        for( DynamicProperty property : dps )
        {
            propertiesMap.put(property.getName(), property);
            propertiesList.add(property);
        }
    }


    /**
     * list to store the order of properties
     */
    protected List<DynamicProperty> propertiesList = new ArrayList<>();

    protected boolean saveOrder = false;
    public boolean isSaveOrder()
    {
        return saveOrder;
    }

    public void setSaveOrder(boolean saveOrder)
    {
        this.saveOrder = saveOrder;
    }

    public void load(String fileName)
    {
        ( new DynamicPropertySetSerializer() ).load(this, fileName);
    }

    public void load(String fileName, ClassLoader classLoader)
    {
        ( new DynamicPropertySetSerializer() ).load(this, fileName, classLoader);
    }

    public void save(String fileName) throws IOException
    {
        ( new DynamicPropertySetSerializer() ).save(fileName, this);
    }

    public void save(OutputStream out) throws IOException
    {
        ( new DynamicPropertySetSerializer() ).save(out, this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Access methods
    //

    /** Find the property descriptor by its name. */
    @Override
    protected DynamicProperty findProperty(String name)
    {
        if( name.indexOf("/") == -1 )
            return propertiesMap.get(name);

        return findProperty(new StringTokenizer(name, "/"), this);
    }

    protected DynamicProperty findProperty(StringTokenizer tokens, DynamicPropertySet ancestor)
    {
        if( tokens.hasMoreTokens() )
        {
            String name = tokens.nextToken();
            DynamicProperty dp = ancestor.getProperty(name);
            if( !tokens.hasMoreTokens() )
                return dp;

            if( dp != null && dp.getValue() instanceof DynamicPropertySet )
                return findProperty(tokens, (DynamicPropertySet)dp.getValue());
        }

        return null;
    }

    /**
     * @return map that can be used to synchronize property values
     * for 'cancel' action.
     * key - property complete name
     * value - property value.
     */
    public Map<String, Object> valuesMap()
    {
        Map<String, Object> map = new HashMap<>();
        fillMap(map, this, null);
        return map;
    }

    protected void fillMap(Map<String, Object> map, Preferences preferences, String prefix)
    {
        if( prefix == null )
            prefix = "";
        else
            prefix += "/";

        Iterator<DynamicProperty> i = preferences.propertyIterator();
        while( i.hasNext() )
        {
            DynamicProperty dp = i.next();

            if( dp.getValue() instanceof Preferences )
                fillMap(map, (Preferences)dp.getValue(), prefix + dp.getName());
            else
                map.put(prefix + dp.getName(), dp.getValue());
        }
    }

    public void updateValues(Map<String, ?> map)
    {
        for(String name : map.keySet())
        {
            Object oldValue = getValue(name);
            Object newValue = map.get(name);

            if( oldValue != newValue )
                setValue(name, newValue);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // getValue methods
    //

    /** @return value for the property with specified name. */
    public Object getValue(String name, Object defaultValue)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
            return property.getValue();
        return defaultValue;
    }

    public boolean getBooleanValue(String name, boolean defaultValue)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
            return ( (Boolean)property.getValue() ).booleanValue();
        return defaultValue;
    }

    public int getIntValue(String name, int defaultValue)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
            return ( (Integer)property.getValue() ).intValue();
        return defaultValue;
    }

    public long getLongValue(String name, long defaultValue)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
            return ( (Long)property.getValue() ).longValue();
        return defaultValue;
    }

    public float getFloatValue(String name, float defaultValue)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
            return ( (Float)property.getValue() ).floatValue();
        return defaultValue;
    }

    public double getDoubleValue(String name, double defaultValue)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
            return ( (Double)property.getValue() ).doubleValue();
        return defaultValue;
    }

    public String getStringValue(String name, String defaultValue)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
        {
            if( property.getValue() == null )
                return null;
            else if( property.getValue() instanceof String )
                return (String)property.getValue();
            else
                return "" + property.getValue();
        }
        return defaultValue;
    }

    public Preferences getPreferencesValue(String name)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
            return (Preferences)property.getValue();

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////

    public void setValue(String name, Object value, PropertyDescriptor descriptor)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
            setValue(name, value);
        else
        {
            property = new DynamicProperty(descriptor, value.getClass(), value);
            add(property);
        }
    }
    
    /**
     * Updates value or adds new one if specified value exists.
     * Unlike setValue(String, Object, PropertyDescriptor) it works for deep values also
     * and creates necessary sub-Preferences automatically
     * @param name name of property. May contain slashes for deep properties.
     * @param value value of property. Cannot be null.
     * @param displayName display name for newly-created property. Will be ignored if property already present. Can be null.
     * @param shortDescription short description for newly-created property. Will be ignored if property already present. Can be null.
     */
    public void addValue(String name, Object value, String displayName, String shortDescription)
    {
        DynamicProperty property = findProperty(name);
        if( property != null )
            setValue(name, value);
        else
        {
            String[] fields = name.split("/");
            String simpleName = fields[fields.length-1];
            Preferences preferences = this;
            for(int i=0; i<fields.length-1; i++)
            {
                Preferences subPreferences = preferences.getPreferencesValue(fields[i]);
                if(subPreferences == null)
                {
                    subPreferences = new Preferences();
                    preferences.add(new DynamicProperty(fields[i], Preferences.class, subPreferences));
                }
                preferences = subPreferences;
            }
            preferences.add(new DynamicProperty(simpleName, displayName, shortDescription, value.getClass(), value));
        }
    }
    
    /**
     * Updates value or adds new one if specified value exists.
     * Unlike setValue(String, Object, PropertyDescriptor) it works for deep values also
     * and creates necessary sub-Preferences automatically
     * @param name name of property. May contain slashes for deep properties.
     * @param value value of property. Cannot be null.
     */
    public void addValue(String name, Object value)
    {
        addValue(name, value, null, null);
    }

    /** Adds the specified property. */
    @Override
    public void add(DynamicProperty property)
    {
        Class<?> type = property.getType();
        if( ! ( type.isAssignableFrom(String.class) || type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Integer.class)
                || type.isAssignableFrom(Long.class) || type.isAssignableFrom(Float.class) || type.isAssignableFrom(Double.class) || type
                .isAssignableFrom(Preferences.class) ) )
            throw new IllegalArgumentException("Unsupported property type " + type.getName());

        propertiesMap.put(property.getName(), property);
        propertiesList.add(property);
        property.setParent(this);
    }

    @Override
    public Iterator<DynamicProperty> propertyIterator()
    {
        if( !saveOrder )
        {
            return propertiesMap.values().iterator();
        }
        else
        {
            return propertiesList.iterator();
        }
    }

    @Override
    public Object remove(String name)
    {
        if( super.remove(name) != null )
        {
            for( int i = 0; i < propertiesList.size(); i++ )
            {
                if( propertiesList.get(i).getName().equals(name) )
                {
                    propertiesList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }
}
