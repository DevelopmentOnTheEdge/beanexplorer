package com.developmentontheedge.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Helper abstract class implementing common functionality for DPS
 */
public abstract class AbstractDynamicPropertySet implements DynamicPropertySet
{
    private static final long serialVersionUID = 1L;
    protected PropertyChangeSupport pcSupport = null;
    
    abstract protected DynamicProperty findProperty( String name );

    @Override
    public Iterator<DynamicProperty> iterator()
    {
        return propertyIterator();
    }

    /** @return value for the property with specified name. */
    @Override
    public Object getValue(String name)
    {
        DynamicProperty property = findProperty(name);
        return property != null ? property.getValue() : null;
    }

    @Override
    public String getValueAsString(String name)
    {
        Object val = getValue( name ); 
        if( val == null )
            return null;
        return val.toString(); 
    }

    @Override
    public Long getValueAsLong(String name)
    {
        Object val = getValue( name );
        if( val == null )
            return null;
        return Long.parseLong(val.toString());
    }

    public <T> T cast( String name, Class<T> clazz )
    {
        return clazz.cast( getValue( name ) ); 
    }

    @Override
    public DynamicProperty getProperty(String name)
    {
        return findProperty( name );
    }

    public DynamicProperty retrieveProperty(String name)
    {
        StringTokenizer st = new StringTokenizer( name, "./" );
        if( st.countTokens() > 1 )
        {
            DynamicPropertySet bean = this; 
            DynamicProperty ret = null;
            while( st.hasMoreTokens() )
            {
                String propName = st.nextToken();
                if( ret != null )
                {
                    bean = ret.getValue() instanceof DynamicPropertySet ? ( DynamicPropertySet )ret.getValue() : null;
                } 
                if( bean != null )
                {
                    ret = bean.getProperty( propName );
                }
                else
                { 
                    return null;
                }
            }              
            return ret;
        }
        else 
        {
           return findProperty( name );
        } 
    }

    public String retrieveStringValue( String name )
    {
        DynamicProperty prop = retrieveProperty( name );
        if( prop == null )
        {
            return null;
        }   

        return prop.getValue().toString();
    }

    @Override
    public DynamicPropertyBuilder getAsBuilder(String name)
    {
        return new DynamicPropertyBuilder( findProperty( name ) );
    }

    @Override
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(CloneNotSupportedException c)
        {
            throw new InternalError();
        }
    }

    //// Listeners
    
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        if(pcSupport == null)
            pcSupport = new PropertyChangeSupport(this);
    
        pcSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if(pcSupport == null)
            pcSupport = new PropertyChangeSupport(this);
    
        pcSupport.addPropertyChangeListener(propertyName, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if(pcSupport != null)
            pcSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
        if(pcSupport != null)
            pcSupport.removePropertyChangeListener(propertyName, listener);
    }

    @Override
    public void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        if(pcSupport != null)
            pcSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public void firePropertyChange(PropertyChangeEvent evt)
    {
        if(pcSupport != null)
            pcSupport.firePropertyChange(evt);
    }

    /**
     * Check if there are any listeners for a specific property.
     * @param propertyName  the property name.
     * @return true if there are ore or more listeners for the given property
     */
    @Override
    public boolean hasListeners(String propertyName)
    {
        if(pcSupport == null)
            return false;
    
        return pcSupport.hasListeners(propertyName);
    }

    /** 
     * @return type for the property with specified name. 
     */
    @Override
    public Class<?> getType(String name)
    {
        DynamicProperty property = findProperty(name);
        return property != null ? property.getType() : null;
    }

    /** 
     * @return descriptor for the property with specified name. 
     */
    @Override
    public PropertyDescriptor getPropertyDescriptor(String name)
    {
        DynamicProperty property = findProperty(name);
        if(property != null)
             return property.getDescriptor();

        return null;
    }

    @Override
    public void setPropertyAttribute( String propName, String attrName, Object attrValue )
    {
        DynamicProperty property = findProperty( propName );
        if( property != null )
        {
            property.setAttribute( attrName, attrValue );
        }
    }

    /**
     * @return true if DynamicPropertySet is empty
     */
    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * Set up value for the specified property name.
     *
     * @throw IllegalArgumentException if property with specified name was not found.
     *
     * @pending firePropertyChange
     */
    @Override
    public void setValue( String name, Object value )
    {
        DynamicProperty property = findProperty( name );
        if( property == null )
        {
            throw new IllegalArgumentException( "Property \"" + name + "\" not found" );
        }
        Object oldValue = property.getValue();
        property.setValue(value);
        if( hasListeners( name ) )
        {
            firePropertyChange( new PropertyChangeEvent( this, name, oldValue, value ) );
        }
    }

    protected static String makeJSONPropName( String name )
    {
        return "\"" + name + "\"";
    }

/*
    protected static String makeJSONStringValue( String value )
    {
        if( value == null )
        {
            return value;
        }
        value = value.replace( "\\", "\\\\" );
        value = value.replace( "\"", "\\\"" );
        value = value.replace( "\n", "\\n" );
        value = value.replace( "\r", "\\r" );
        value = value.replace( "\t", "\\t" );
        value = value.replace( "\b", "\\b" );
        value = value.replace( "\f", "\\f" );
        value = value.replace( "/", "\\/" );
        return makeJSONPropName( value );
    }
*/

    public static String makeJSONStringValue( String string ) 
    {
        if( string == null )
        {
            return null;
        }
        if( string.length() == 0 ) 
        {
            return "\"\"";
        }

        char b;
        char c = 0;
        int i;
        int len = string.length();
        StringBuffer sb = new StringBuffer( len * 2 );
        String t;
        char[] chars = string.toCharArray();
        char[] buffer = new char[1030];
        int bufferIndex = 0;
        sb.append( '"' );
        for( i = 0; i < len; i += 1 ) 
        {
           if( bufferIndex > 1024 ) 
           {
              sb.append( buffer, 0, bufferIndex );
              bufferIndex = 0;
           }
           b = c;
           c = chars[i];
           switch( c ) 
           {
              case '\\':
              case '"':
                 buffer[bufferIndex++] = '\\';
                 buffer[bufferIndex++] = c;
                 break;
              case '/':
                 if( b == '<' ) 
                 {
                    buffer[bufferIndex++] = '\\';
                 }
                 buffer[bufferIndex++] = c;
                 break;
              default:
                 if( c < ' ' ) 
                 {
                    switch( c ) 
                    {
                       case '\b':
                          buffer[bufferIndex++] = '\\';
                          buffer[bufferIndex++] = 'b';
                          break;
                       case '\t':
                          buffer[bufferIndex++] = '\\';
                          buffer[bufferIndex++] = 't';
                          break;
                       case '\n':
                          buffer[bufferIndex++] = '\\';
                          buffer[bufferIndex++] = 'n';
                          break;
                       case '\f':
                          buffer[bufferIndex++] = '\\';
                          buffer[bufferIndex++] = 'f';
                          break;
                       case '\r':
                          buffer[bufferIndex++] = '\\';
                          buffer[bufferIndex++] = 'r';
                          break;
                       default:
                          t = "000" + Integer.toHexString( c );
                          int tLength = t.length();
                          buffer[bufferIndex++] = '\\';
                          buffer[bufferIndex++] = 'u';
                          buffer[bufferIndex++] = t.charAt( tLength - 4 );
                          buffer[bufferIndex++] = t.charAt( tLength - 3 );
                          buffer[bufferIndex++] = t.charAt( tLength - 2 );
                          buffer[bufferIndex++] = t.charAt( tLength - 1 );
                    }
                 } 
                 else 
                 {
                    buffer[bufferIndex++] = c;
                 }
           }
        }
        sb.append( buffer, 0, bufferIndex );
        sb.append( '"' );
        return sb.toString();
    }
}
