/** $Id: ArrayProperty.java,v 1.34 2011/09/13 05:29:32 tolstyh Exp $ */

package com.developmentontheedge.beans.model;

import java.beans.BeanDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.IndexedPropertyConstants;
import com.developmentontheedge.beans.log.Logger;

/**
 * ... PENDING: - array element display name
 */
public class ArrayProperty extends Property implements IndexedPropertyConstants
{
    private static final int MAX_SAFE_ELEMENT = 100;

    /** Array of real values. */
    protected Object array = null;

    /** Array of properties corresponding to array items. */
    protected Vector<Property> itemProperties = null;
    protected boolean itemPropertiesInitialized;

    /** Type of array items. */
    private Class<?> itemClass;

    /** BeanDescriptor for array item type. */
    protected BeanDescriptor itemInfo;
    private boolean onlyIndexedGetter;
    
    /** Prototype for creating new array items. For example, DynamicPropertySet object with set of properties.*/
    protected Object itemPrototype; 
    
    protected ComponentFactory.Policy policy;
    public ComponentFactory.Policy getPolicy()
    {
        return policy;
    }

    /**
     * Creates a new ArrayProperty. In general, this function should be called by ComponentFactory
     */
    protected ArrayProperty( Property parent, Object theOwner, PropertyDescriptor theDescriptor, ComponentFactory.Policy policy)
    {
        super( parent, theOwner, theDescriptor );
        // from now on we shouldn't use 'theDescriptor' value
        // since it can be replaced by wrapper

        this.policy = policy;

        try
        {
            if( descriptor instanceof IndexedPropertyDescriptor )
                itemClass = ( ( IndexedPropertyDescriptor )descriptor ).getIndexedPropertyType();
            else
            {
                itemClass = ( ( PropertyDescriptor )descriptor ).getPropertyType().getComponentType();
            }

            itemInfo = Introspector.getBeanInfo( itemClass ).getBeanDescriptor();

            onlyIndexedGetter = false;

            if( !(theOwner instanceof DynamicPropertySet) )
            {            
                Method readMethod = ( ( PropertyDescriptor )descriptor ).getReadMethod();
                onlyIndexedGetter = readMethod == null;

                // additional check for array properties!
                if( !onlyIndexedGetter ) 
                {
                    Class<?> retType = readMethod.getReturnType();
                    if( !retType.isArray() )
                    {
                        onlyIndexedGetter = true;
                    }
                }
            }

            itemPropertiesInitialized = false; 
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "Array property init error: " + e + "\r\n\t name   = " +
                descriptor.getDisplayName() + "\r\n\t owner   = " + owner +
                "\r\n\t parent  = " + parent, e );
        }

    }

    /*
     * All array values we wrap as a properties and put them in the itemProperties vector.
     */

    private void init()
    {
        if( itemProperties == null )
            itemProperties = new Vector<>();
        try
        {
            itemProperties.clear();

            if( !onlyIndexedGetter )
            {
                array = getValue();
                if( array == null )
                { 
                    array = Array.newInstance( itemClass, 0 ); 
                }

                int size = Array.getLength( array );
                if( size > MAX_SAFE_ELEMENT )
                {
                        addPropertyForIndex( 0 );
                        addPropertyForIndex( size - 1 );
                }
                else
                {
                    for( int i = 0; i < size; i++ )
                    {
                        addPropertyForIndex( i );
                    }
                }
            }
            else
            {
                array = Array.newInstance( itemClass, MAX_SAFE_ELEMENT );
                for( int i = 0; i < MAX_SAFE_ELEMENT; i++ )
                {
                    Object val = getIndexedValue( i );
                    if( val == null ) { break; }
                    Array.set( array, i, val );

                    addPropertyForIndex( i );
                }
                Object newArray = Array.newInstance( itemClass, itemProperties.size() );
                System.arraycopy( array, 0, newArray, 0, Array.getLength( newArray ) );
                array = newArray;
            }
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "Exception in ArrayProperty.init(): " + getCompleteName() + ", " +
                e + ", --- " + array + ", onlyIndexedGetter = " + onlyIndexedGetter, e );
        }
        itemPropertiesInitialized = true;
    }

    private void addPropertyForIndex( int index ) throws Exception
    {
         Class<?> type = itemClass;
         PropertyDescriptor pd = new ValuePropertyDescriptor( this, index );
         Object owner = new Value( this, index );
         Class<?> realType = ComponentFactory.derivePropertyType( owner, pd );
         if( !realType.equals( type ) )
         {
             BeanDescriptor newItemInfo = Introspector.getBeanInfo( realType ).getBeanDescriptor();
             pd = new ValuePropertyDescriptor( newItemInfo, this, index );
             type = realType;
         }
         Property p = ComponentFactory.createProperty( type, owner, pd, this, policy );
         if( p != null ) { itemProperties.addElement( p ); }
    }

    ////////////////////////////////////////
    // in general, set value methods should not be called.
    //
    @Override
    public void setValue( Object value ) throws NoSuchMethodException
    {
        if( !onlyIndexedGetter ) { super.setValue( value ); }
        // rebuild properties representation
        init();
    }

    /** Returns value of this property. */
    @Override
    public Object getValue()
    {
        if( onlyIndexedGetter ) { return array; }
        return super.getValue();
    }

    public Object getIndexedValue( int index )
    {
        Method method = null;
        try
        {
            method = ( ( IndexedPropertyDescriptor )descriptor ).getIndexedReadMethod();
            if( method == null ) { return null; }
            Object value = method.invoke( getOwner(), index );
            return value;
        }
        catch( Exception ignore )
        {
            // ignore exceptions because they are most likely application-specific
            // i.e. we shouldn't call getXXXXXX arbitrary though we are doing so
        }
        return null;
    }

    public boolean isResizable()
    {
        return booleanFeature(CAN_INSERT) && booleanFeature(CAN_DELETE);
    }
    ////////////////////////////////////////
    // modification methods
    //

    /**
     * Add new item into array property.
     * @return new Property that assotiated with new item, or null if
     * instantiation error is occur.
     */
    protected Property addItem()
    {
        if( !itemPropertiesInitialized )
            init();
        try
        {
            int index = Array.getLength( array );
            insertItem( index, null );
            return getPropertyAt( index );
        }
        catch( Exception e )
        {
            Logger.getLogger().error( "ArrayProperty.addItem: " + e, e );
        }
        return null;
    }

    /**
     * Provides item with the specified index for array property.
     * @return Property that assotiated with the specified item number, or null if
     * instantiation error is occur.
     */
    protected Property provideItem( int index )
    {
        if( !itemPropertiesInitialized )
            init();
        for( int i = itemProperties.size(); i <= index; i++ )
        {
            if( addItem() == null ) { return null; }
        }
        return getPropertyAt( index );
    }

    public void insertItem( int index, Object item ) throws InstantiationException,
        IllegalAccessException
        {
            if( !itemPropertiesInitialized )
                init();
            if( ( index < 0 ) || ( index > Array.getLength( array ) ) )
            { throw new ArrayIndexOutOfBoundsException( index ); }
            int size = Array.getLength( array ) + 1;
            Object value = Array.newInstance( itemClass, size );
            if( item == null )
            {
                item = getDefaultItem();
            }
            for( int i = 0, ii = 0; i < size; i++, ii++ )
            {
                if( i != index )
                {
                    Array.set( value, i, Array.get( array, ii ) );
                }
                else
                {
                    Array.set( value, i, item );
                    ii--;
                }
            }
            try
            {
                setValue( value );
            }
            catch( NoSuchMethodException ignore ) { }
    }

    private Object getDefaultItem() throws InstantiationException, IllegalAccessException
    {
        Object item = getDefaultPrimitiveInstance( itemClass );
        if( item != null)
            return item;
        if( itemPrototype != null )
            try
            {
                return itemPrototype.getClass().getMethod("clone").invoke(itemPrototype);
            }
            catch(Exception e)
            {
            }
        return itemClass.newInstance(); 
    }

    public void removeItem( int index )
    {
        if( !itemPropertiesInitialized )
            init();
        if( ( index < 0 ) || ( index > Array.getLength( array ) - 1 ) )
        { throw new ArrayIndexOutOfBoundsException( index ); }
        int size = Array.getLength( array );
        Object value = Array.newInstance( itemClass, size - 1 );
        int del = 0;
        for( int i = 0; i < size; i++, del++ )
        {
            if( i == index ) { del--; }
            else { Array.set( value, del, Array.get( array, i ) ); }
        }
        itemProperties.removeElement( itemProperties.lastElement() );
        try
        {
            setValue( value );
        }
        catch( NoSuchMethodException ignore ) { }
    }

    public void moveDown( int index )
    {
        if( !itemPropertiesInitialized )
            init();
        if( ( index >= 0 ) && ( index < Array.getLength( array ) - 1 ) )
        {
            Object up = Array.get( array, index + 1 );
            Array.set( array, index + 1, Array.get( array, index ) );
            Array.set( array, index, up );

            try
            {
                super.setValue( array );
            }
            catch( NoSuchMethodException ignore ) { }
        }
        // notify about changes
        firePropertyChange( new PropertyChangeEvent( getOwner(), getPropertyAt( index ).getName(), null, null ) );
        firePropertyChange( new PropertyChangeEvent( getOwner(), getPropertyAt( index + 1 ).getName(), null, null ) );
    }

    ////////////////////////////////////////
    // implemeents TreeNode interface
    //
    @Override
    public int getPropertyCount()
    {
        if( !itemPropertiesInitialized )
            init();
        return itemProperties == null ? 0 : itemProperties.size();
    }

    @Override
    public Property getPropertyAt( int i )
    {
        if( !itemPropertiesInitialized )
            init();
        try { return itemProperties.elementAt( i ); }
        catch( java.lang.ArrayIndexOutOfBoundsException e ) { return null; }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        init();
        super.propertyChange( evt );
    }

    /**
     * return object with default value by passed primitive type class
     */
    public static Object getDefaultPrimitiveInstance( Class<?> paramType )
    {
        Object object = null;
        String type = paramType.getName();
        if( type.equals( "java.lang.String" ) ) { object = ""; }
        else if( type.equals( "java.lang.Boolean" ) || type.equals( "boolean" ) )
        { object = Boolean.FALSE; }
        else if( type.equals( "java.lang.Byte" ) || type.equals( "byte" ) )
            { object = Byte.valueOf((byte) 0); }
        else if( type.equals( "java.lang.Character" ) || type.equals( "char" ) )
        { object = Character.valueOf('\0'); }
        else if( type.equals( "java.lang.Double" ) || type.equals( "double" ) )
        { object = Double.valueOf(0.0); }
        else if( type.equals( "java.lang.Float" ) || type.equals( "float" ) )
        { object = Float.valueOf(0.0f); }
        else if( type.equals( "java.lang.Long" ) || type.equals( "long" ) )
            { object = Long.valueOf(0l); }
        else if( type.equals( "java.lang.Short" ) || type.equals( "short" ) )
        { object = Short.valueOf((short) 0); }
        else if( type.equals( "java.lang.Integer" ) || type.equals( "int" ) )
        { object = Integer.valueOf(0); }
        return object;
    }

    public boolean isOnlyIndexedGetter() { return onlyIndexedGetter; }

    public void setOnlyIndexedGetter( boolean onlyIndexedGetter )
    { this.onlyIndexedGetter = onlyIndexedGetter; }

    /**
     * Gets the Class object for the property value.
     * @see PropertyDescriptor#getPropertyType
     */
    @Override
    public Class<?> getValueClass()
    {
        if( array == null )
        {
            return Array.newInstance( itemClass, 0 ).getClass();
        }
        return array.getClass();
    }

    public Class<?> getItemClass()
    {
        return itemClass;
    }
    
    /**
     * Set prototype object for array item.
     * This object should implements 'Object clone()' function
     */
    public void setItemPrototype(Object itemPrototype)
    {
        this.itemPrototype = itemPrototype;
    }

    ////////////////////////////////////////
    // Internal classes
    //

    /** The internal class for array value access. */
    static private class Value
    {
        int index;
        ArrayProperty owner;

        public Value( ArrayProperty owner, int index )
        {
            this.owner = owner;
            this.index = index;
        }

        public Object getValue()
        {
            int size = Array.getLength( owner.array );
            if( index < size ) { return Array.get( owner.array, index ); }
            else { throw new ArrayIndexOutOfBoundsException( index ); }
        }

        public void setValue( Object value )
        {
            Array.set( owner.array, index, value );
        }
    }


    static private class ValuePropertyDescriptor extends PropertyDescriptor
    {

        private final ArrayProperty owner;

        // for the obfuscator
        static private ArrayProperty.Value classRetriever = new ArrayProperty.Value( null, 0 );

        public ValuePropertyDescriptor( ArrayProperty owner, int index ) throws IntrospectionException
        {
            this( owner.itemInfo, owner, index );
        }

        public ValuePropertyDescriptor( BeanDescriptor itemInfo, ArrayProperty owner, int index ) throws IntrospectionException
        {
            super( "[" + index + "]", classRetriever.getClass(), "getValue", "setValue" );

            this.owner = owner;

            // copy necessary data from informant
            this.setExpert( itemInfo.isExpert() );
            this.setHidden( itemInfo.isHidden() );
            this.setPreferred( itemInfo.isPreferred() );
            this.setShortDescription( itemInfo.getShortDescription() );
            //this.setDisplayName( itemInfo.getDisplayName() );
            super.setValue( InternalConstants.CHILD_INDEX, index );

            Enumeration<String> e = itemInfo.attributeNames();
            while( e.hasMoreElements() )
            {
                String atr = e.nextElement();
                super.setValue( atr, itemInfo.getValue( atr ) );
            }
        }

        @Override
        public Class<?> getPropertyType()
        {
            return owner.itemClass;
        }
    }
}
