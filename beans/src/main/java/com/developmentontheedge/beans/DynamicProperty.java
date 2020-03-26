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

    public static final String XML_NAMESPACE = "xml-name-space";
    public static final String XML_ATTRIBUTES = "xml-attributes";
    public static final String XML_ATTRIBUTES_ARRAY = "xml-attributes-array";

    private final static String[] digits = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

    public static Object reuseValue( Object value )
    {
        if( !( value instanceof String ) )
        {
            return value;
        }

        String vals = ( String )value;
        if( vals.length() == 1 && Character.isDigit( vals.charAt( 0 ) ) )
        {
            return digits[ vals.charAt( 0 ) - 48 ];
        } 
    
        for( String sval : new String[]{ 
             DbStrings.NO, DbStrings.YES, 
             DbStrings.JS_CLASS, DbStrings.CSS_CLASS, 
             DbStrings.FILTER,
             DbStrings.OP_TYPE_JAVA, DbStrings.OP_TYPE_JAVA_DOTNET, 
             DbStrings.QUERY_TYPE_1D, DbStrings.QUERY_TYPE_1D_UNKNOWN, 
             DbStrings.ALL_RECORDS_VIEW, DbStrings.SELECTION_VIEW,
             DbStrings.INSERT, DbStrings.EDIT, DbStrings.CLONE, DbStrings.DELETE, 
             DbStrings.MALE, DbStrings.FEMALE 
            } )
        {
            if( sval.equals( value ) )
            {
                return sval;
            }
        } 

        return value;
    }

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
        this.value = reuseValue( value );  
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
        this.value = reuseValue( value );
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
        this.value = reuseValue( value );
        if( parent != null )
        {
            parent.firePropertyChange( getName(), oldValue, this.value );
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

    public String getStringAttribute( String attrName )
    {
        Object val = getAttribute( attrName );
        if( val instanceof String )
        {
            return ( String )val;
        }
        return null;
    }

    public void setAttribute( String attrName, Object attrValue )
    {
        attrValue = reuseValue( attrValue );
        if( descriptor != null )
        {
            descriptor.setValue( attrName, attrValue );
            return;
        }

        if( localAttrs == null )
        {
            localAttrs = new HashMap<String,Object>();
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
            ( ( DynamicPropertySetSupport )value ).setAttributeToAllChildren( attrName, attrValue );
        }
        else if( value instanceof DynamicPropertySetSupport[] )
        {
            for( DynamicPropertySetSupport propBean : ( DynamicPropertySetSupport[] )value )
            {
                propBean.setAttributeToAllChildren( attrName, attrValue );
            }
        }
    }

    public void setAttributeToAllChildrenIfEmpty( String attrName, Object attrValue )
            throws Exception
    {
        Object value = this.getValue();
        if( value instanceof DynamicPropertySetSupport )
        {
            ( ( DynamicPropertySetSupport )value ).setAttributeToAllChildrenIfEmpty( attrName, attrValue );
        }
        else if( value instanceof DynamicPropertySetSupport[] )
        {
            for( DynamicPropertySetSupport propBean : ( DynamicPropertySetSupport[] )value )
            {
                propBean.setAttributeToAllChildrenIfEmpty( attrName, attrValue );
            }
        }
    }


    protected Object[] getOtherArray( Object dpValue )
    {
        return null; 
    }

    protected String getNamespace()
    {
        Object ns = getAttribute( XML_NAMESPACE );
        //System.out.println( getName() + " ns = " + ns );
        if( ns == null )
        {
            return "";
        }

        return ns.toString() + ":";
    }  

    public String serializeAsXml( String offset )
    {
        String retXml = "";
        try
        {
            Object dpValue = getValue();
            if( dpValue == null || dpValue instanceof String && "".equals( ( ( String )dpValue ).trim() ) )
            {
                //System.out.println( " 11111 " + getName() + ": " + dpValue );
                retXml += "\n" + offset + "<" + getNamespace() + getName() + serializeAsXmlAttributes() + "/>";
            }
            else
            {
                Object []otherArray = null;
                if( dpValue instanceof DynamicPropertySet )
                {
                    //System.out.println( " 22222 " + getName() + ": " + dpValue.getClass() + "\n"  + dpValue );
                    retXml += "\n" + offset + "<" + getNamespace() + getName() + serializeAsXmlAttributes() + ">";
                    for( DynamicProperty dp : ( DynamicPropertySet )dpValue )
                    {
                        retXml += ( ( DynamicProperty )dp ).serializeAsXml( "   " + offset );
                    }
                    retXml += "\n" + offset + "</" + getNamespace() + getName() + ">";
                }
                else if( dpValue instanceof DynamicPropertySet[] )
                {
                    //System.out.println( " 444444 " + getName() + ": " + dpValue.getClass() + "\n"  + dpValue );

                    DynamicPropertySet[] arrp = ( DynamicPropertySet[] )dpValue;
                    DynamicPropertySet []arratt = ( DynamicPropertySet[] )getAttribute( XML_ATTRIBUTES_ARRAY );
                    if( arratt == null )
                    {
                        arratt = new DynamicPropertySet[ arrp.length ];
                    }
                    for( int i = 0; i < arrp.length; i++ )
                    {
                        DynamicPropertySet dps = arrp[ i ];  
                        //System.out.println( "---------------------" );
                        //System.out.println( "dps = " + dps );
                        //System.out.println( "---------------------" );
                        retXml += "\n" + offset + "<" + getNamespace() + getName() + serializeAsXmlAttributesCommon( i < arratt.length ? arratt[ i ] : null ) + ">";
                        for( DynamicProperty dp : dps )
                        {
                            retXml += ( ( DynamicProperty )dp ).serializeAsXml( "   " + offset );
                        }
                        retXml += "\n" + offset + "</" + getNamespace() + getName() + ">";
                    }
                }
                else if( ( otherArray = getOtherArray( dpValue ) ) != null )
                {
                    //System.out.println( " 55555 " + getName() + ": " + dpValue.getClass() + "\n"  + dpValue );
                    for( Object scr : otherArray )
                    {
                        String value = scr.toString();
                        value = value.replace( "\\", "\\\\" );
                        value = value.replace( "\"", "\\\"" );
                        value = value.replace( "&", "&amp;" );
                        value = value.replace( ">", "&gt;" );
                        value = value.replace( "<", "&lt;" );

                        retXml += "\n" + offset + "<" + getNamespace() + getName() + serializeAsXmlAttributes() + ">" + value + "</" + getNamespace() + getName() + ">";
                        ;
                    }
                }
                else if( dpValue instanceof Object[] )
                {
                    //System.out.println( " 666666 " + getName() + ": " + dpValue.getClass() + "\n"  + dpValue );
                    for( Object obj : ( Object[] )dpValue )
                    {
                        String value = obj.toString().replace( "\\", "\\\\" );
                        value = value.replace( "\"", "\\\"" );
                        value = value.replace( "&", "&amp;" );
                        value = value.replace( ">", "&gt;" );
                        value = value.replace( "<", "&lt;" );

                        retXml += "\n" + offset + "<" + getNamespace() + getName() + serializeAsXmlAttributes() + ">" + value + "</" + getNamespace() + getName() + ">";
                        ;
                    }
                }
                else
                {
                    //System.out.println( " 33333 " + getName() + ": " + dpValue );
                    String value = dpValue.toString().replace( "\\", "\\\\" );
                    value = value.replace( "\"", "\\\"" );
                    value = value.replace( "&", "&amp;" );
                    value = value.replace( ">", "&gt;" );
                    value = value.replace( "<", "&lt;" );

                    retXml += "\n" + offset + "<" + getNamespace() + getName() + serializeAsXmlAttributes() + ">" + value + "</" + getNamespace() + getName() + ">";
                }
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }

        return retXml;
    }

    public String serializeAsXmlAttributes()
    {
        return serializeAsXmlAttributesCommon( ( DynamicPropertySet )getAttribute( XML_ATTRIBUTES ) );
    }

    public String serializeAsXmlAttributesCommon( DynamicPropertySet attrs )
    {
        if( attrs == null )
        {
            return "";
        }

        String result = "";
        for( DynamicProperty attr : attrs )
        {
            if( attr.getValue() != null && !"".equals( ( attr.getValue().toString() ).trim() ) )
            {
                String val = attr.getValue().toString().replace( "'", "&apos;" );
                val = val.replace( "\"", "&quot;" );
                result += " " + attr.getName() + "=\"" + val + "\"";
            }
            else
            {
                result += " " + attr.getName() + "=\"\"";
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        return "name: " + getName() + ", type:" + this.type + ", value: " + this.value;
    }
}


