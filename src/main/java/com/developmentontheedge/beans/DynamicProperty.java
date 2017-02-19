package com.developmentontheedge.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class describes individual property that can be dynamically added to the bean
 */
public class DynamicProperty implements Serializable
{
    private static final long serialVersionUID = 1L;
	private DynamicPropertySet parent;

    /**
     * Constructs the dynamic property with empty value
     *
     * @param name programmatic name of the property
     * @param type Java class of the property
     */
    public DynamicProperty( String name, Class<?> type )
    {
        this( name, null, null, type, null );
    }

    /**
     * Constructs the dynamic property with empty value
     *
     * @param descriptor - provides standard descriptor for the property
     * @param type       - Java class of the property
     */
    public DynamicProperty( PropertyDescriptor descriptor, Class<?> type )
    {
        this( descriptor, type, null );
    }

    /**
     * Constructs the dynamic property
     *
     * @param name  - programmatic name of the property
     * @param type  - Java class of the property
     * @param value - initial value of the property
     */
    public DynamicProperty( String name, Class<?> type, Object value )
    {
        this( name, null, null, type, value );
    }

    /**
     * Constructs the dynamic property
     *
     * @param name        programmatic name of the property
     * @param displayName localized display name of this property
     * @param type        Java class of the property
     */
    public DynamicProperty( String name, String displayName, Class<?> type )
    {
        this( name, displayName, null, type, null );
    }

    /**
     * Constructs the dynamic property
     *
     * @param name        programmatic name of the property
     * @param displayName localized display name of this property
     * @param shortDesc   short description of the property
     * @param type        Java class of the property
     */
    public DynamicProperty( String name, String displayName, String shortDesc, Class<?> type )
    {
        this( name, displayName, shortDesc, type, null );
    }

    /**
     * Constructs the dynamic property
     *
     * @param name        programmatic name of the property
     * @param displayName localized display name of this property
     * @param type        Java class of the property
     * @param value       initial value of the property
     */
    public DynamicProperty( String name, String displayName, Class<?> type, Object value )
    {
        this( name, displayName, null, type, value );
    }

    /**
     * Constructs the dynamic property
     *
     * @param name        programmatic name of the property
     * @param displayName localized display name of this property
     * @param shortDesc   short description of the property
     * @param type        Java class of the property
     * @param value       initial value of the property
     */
    public DynamicProperty( String name, String displayName, String shortDesc, Class<?> type, Object value )
    {
        localName = name;  
        this.type = type;  
        this.value = value;  
        setDisplayName( displayName );
        setShortDescription( shortDesc );
    }

    /**
     * Constructs the dynamic property
     *
     * @param descriptor provides standard descriptor for the property
     * @param type       Java class of the property
     * @param value      initial value of the property
     */
    public DynamicProperty( PropertyDescriptor descriptor, Class<?> type, Object value )
    {
        this.descriptor = descriptor;
        this.type = type;
        this.value = value;
    }

    /**
     * The dynamic property descriptor.
     */
    protected transient PropertyDescriptor descriptor;
    // transient - see readObject, writeObject - adolg

    public boolean hasDescriptor()
    {
        return descriptor != null;
    }

    public PropertyDescriptor getDescriptor()
    {
        if( descriptor == null )  
        {
            try
            { 
                descriptor = new PropertyDescriptor( localName, null, null ); 
                descriptor.setDisplayName( localDisplayName );
                descriptor.setShortDescription( localShortDescription );
                descriptor.setExpert( localExpert );
                descriptor.setHidden( localHidden );
                if( localAttrs != null )
                {
                    for( Map.Entry<String,Object> entry : localAttrs.entrySet() )
                    {
                         descriptor.setValue( entry.getKey(), entry.getValue() );
                    }
                } 
                localName = null;                
                localDisplayName = null;
                localShortDescription = null;
                localAttrs = null;                
            } 
            catch( IntrospectionException ex )
            {
                throw new RuntimeException( ex );
            }
        }
        return descriptor;
    }

    public void setDescriptor( PropertyDescriptor desc )
    {
        localName = null;
        localDisplayName = null;
        localShortDescription = null;
        descriptor = desc;
    }

    String localName = null;
    String localDisplayName = null;
    String localShortDescription = null;
    /**
     * The dynamic property name.
     */
    public String getName()
    {
        if( descriptor != null )
        {
            return descriptor.getName();
        }
        return localName;  
    }

    public void setName( String name )
    {
        if( descriptor != null )
        {
            descriptor.setName( name );
        }
        localName = name;
    }

    public String getDisplayName()
    {
        if( descriptor != null )
        {
            return descriptor.getDisplayName();
        }
        return localDisplayName == null ? localName : localDisplayName;  
    }

    public void setDisplayName( String name )
    {
        if( descriptor != null )
        {
            descriptor.setDisplayName( name );
        }
        if( name != null && name.equals( localName ) )
        {
            localDisplayName = null;
        }
        else
        {
            localDisplayName = name;
        }
    }

    public String getShortDescription()
    {
        if( descriptor != null )
        {
            return descriptor.getShortDescription();
        }
        return localShortDescription == null ? getDisplayName() : localShortDescription;
    }

    public void setShortDescription( String shortDesc )
    {
        if( descriptor != null )
        {
            descriptor.setShortDescription( shortDesc );
        }
        if( shortDesc != null && shortDesc.equals( getDisplayName() ) )
        {
            localShortDescription = null;
        }
        else
        {
            localShortDescription = shortDesc;
        }

    }

    /**
     * The dynamic property type.
     */
    Class<?> type;

    public Class<?> getType()
    {
        return type;
    }

    public void setType( Class<?> type )
    {
        this.type = type;
    }

    /**
     * The dynamic property value.
     */
    protected Object value;

    public Object getValue()
    {
        return value;
    }

    public void setValue( Object value )
    {
        Object oldValue = this.value;
        this.value = value;
        if( parent != null )
        {
            parent.firePropertyChange( getName(), oldValue, value );
        }
    }

    private HashMap<String,Object> localAttrs = null;

    public Object getAttribute( String attrName )
    {
        if( descriptor != null )
        {
            return descriptor.getValue( attrName );
        }
        if( localAttrs != null )
        {
            return localAttrs.get( attrName );
        }
        return null; 
    }

    public boolean getBooleanAttribute( String attrName )
    {
        Object val = getAttribute( attrName );
        if( val instanceof Boolean )
        {
            return ( Boolean )val; 
        }
        return false;
    }

    public void setAttribute( String attrName, Object attrValue )
    {
        if( descriptor != null )
        {
            descriptor.setValue( attrName, attrValue );
            return;
        }

        if( localAttrs == null )
        {
            localAttrs = new HashMap<>();
        }

        localAttrs.put( attrName, attrValue );
    }

    private static Set<String> emptySet = Collections.emptySet();

    public Enumeration<String> attributeNames()
    {
        if( descriptor != null )
        {
            return descriptor.attributeNames();
        }  
        if( localAttrs == null )
        {
            return Collections.enumeration( emptySet );
        }
        return Collections.enumeration( localAttrs.keySet() );
    }

    private boolean localHidden = false;

    public boolean isHidden()
    {
        if( descriptor != null )
        {
            return descriptor.isHidden();
        }
        return localHidden;
    }

    public void setHidden( boolean hidden )
    {
        if( descriptor != null )
        {
            descriptor.setHidden( hidden );
        }
        localHidden = hidden;
    }

    private boolean localExpert = false;

    public boolean isExpert()
    {
        if( descriptor != null )
        {
            return descriptor.isExpert();
        }
        return localExpert;
    }

    public void setExpert( boolean expert )
    {
        if( descriptor != null )
        {
            descriptor.setExpert( expert );
        }
        localExpert = expert;
    }

    public boolean isReadOnly()
    {
        Object attr = getAttribute( BeanInfoConstants.READ_ONLY );
        return attr != null && Boolean.TRUE.equals( attr );
    }

    public void setReadOnly( boolean value )
    {
        setAttribute( BeanInfoConstants.READ_ONLY, value ? Boolean.TRUE : Boolean.FALSE );
    }

    public boolean isCanBeNull()
    {
        Object attr = getAttribute( BeanInfoConstants.CAN_BE_NULL );
        return attr != null && Boolean.TRUE.equals( attr );
    }

    public void setCanBeNull( boolean value )
    {
        setAttribute( BeanInfoConstants.CAN_BE_NULL, value ? Boolean.TRUE : Boolean.FALSE );
    }

    public void resetTagList( Object tagList )
    {
        setAttribute( BeanInfoConstants.TAG_LIST_ATTR, tagList );
        if( getAttribute( BeanInfoConstants.EXTERNAL_TAG_LIST ) != null )
        {
            setAttribute( BeanInfoConstants.EXTERNAL_TAG_LIST, "" );
        }
    }

    public DynamicPropertySet getParent()
    {
        return parent;
    }

    public void setParent( DynamicPropertySet parent )
    {
        this.parent = parent;
    }

    private void readObject( ObjectInputStream aStream ) throws IOException, ClassNotFoundException
    {
        aStream.defaultReadObject();
        setName( ( String )aStream.readObject() );

        // for compatibility
        /*Method readMethod = ( Method )*/aStream.readObject();
        /*Method readMethod = ( Method )*/aStream.readObject();
        aStream.readBoolean();
        aStream.readBoolean();

        String dispName = ( String )aStream.readObject();
        setExpert( aStream.readBoolean() );
        setHidden( aStream.readBoolean() );
        setDisplayName( dispName );

        // for compatibility
        aStream.readBoolean();
        aStream.readObject();

        setShortDescription( ( String )aStream.readObject() );
        String attribute = ( String )aStream.readObject();
        while( !attribute.equals( "" ) )
        {
            Object value = aStream.readObject();
            setAttribute( attribute, value );
            attribute = ( String )aStream.readObject();
        }
    }

    private void writeObject( ObjectOutputStream aStream ) throws IOException
    {
        aStream.defaultWriteObject();

        aStream.writeObject( getName() );

        // for compatibility
        aStream.writeObject( null );
        aStream.writeObject( null );
        aStream.writeBoolean( false );
        aStream.writeBoolean( false );

        aStream.writeObject( getDisplayName() );
        aStream.writeBoolean( isExpert() );
        aStream.writeBoolean( isHidden() );

        // for compatibility
        aStream.writeBoolean( false );
        aStream.writeObject( null );

        aStream.writeObject( getShortDescription() );
        // let's serialize custom attributes
        Enumeration<String> attributeNames = attributeNames();
        while( attributeNames.hasMoreElements() )
        {
            String attribute = attributeNames.nextElement();
            aStream.writeObject( attribute );
            aStream.writeObject( getAttribute( attribute ) );
        }
        aStream.writeObject( "" );
    }

    public void setAttributeToAllChildren( String attrName, Object attrValue )
            throws Exception
    {
        Object value = this.getValue();
        if( value instanceof DynamicPropertySetSupport )
        {
            for( DynamicProperty prop: ( DynamicPropertySetSupport )value )
            {
                prop.setAttribute( attrName, attrValue );
            }
            ( ( DynamicPropertySetSupport )value ).setAttributeToAllChildren( attrName, attrValue );
        }
        else if( value instanceof DynamicPropertySetSupport[] )
        {
            for( DynamicPropertySetSupport propBean : ( DynamicPropertySetSupport[] )value )
            {
                for( DynamicProperty prop: propBean )
                {
                    prop.setAttribute( attrName, attrValue );
                }
                propBean.setAttributeToAllChildren( attrName, attrValue );
            }
        }
    }

    @Override
	public String toString()
    {
        return "name: " + getName() + ", type:" + this.type + ", value: " + this.value;
    }
}


