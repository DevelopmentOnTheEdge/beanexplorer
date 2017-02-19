package com.developmentontheedge.beans.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;

public class Beans
{
    public static class BeanException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        public BeanException(Throwable cause, Object bean, String propertyName, String what)
        {
            super( "Error " + what + " property '" + propertyName + "' of " + bean + ": " + cause.getMessage(), cause );
        }
    }

    /**
     * Copies one bean to another
     * It tries to read all the properties values from src bean and store them into dst bean ignoring any possible errors
     * @param src bean to copy
     * @param dst destination bean
     */
    public static void copyBean(Object src, Object dst)
    {
        if( src == null || dst == null )
            return;
        ComponentModel srcModel = ComponentFactory.getModel( src );
        ComponentModel dstModel = ComponentFactory.getModel( dst );
        for( int i = 0; i < srcModel.getPropertyCount(); i++ )
        {
            Property srcProperty = srcModel.getPropertyAt( i );
            Property dstProperty = dstModel.findProperty( srcProperty.getName() );
            if( dstProperty == null )
                continue;
            try
            {
                dstProperty.setValue( srcProperty.getValue() );
            }
            catch( Exception e )
            {
            }
        }
    }

    /**
     * Faster way to get bean property value than using ComponentModel
     * @param bean
     * @param property - name of the property
     * @return
     * @throws Exception if something wrong happens
     */
    private static ConcurrentHashMap<Class<?>, Map<String, BeanPropertyAccessor>> accessors = new ConcurrentHashMap<>();

    private static interface BeanPropertyAccessor
    {
        public Object get(Object bean) throws Exception;
        public void set(Object bean, Object value) throws Exception;
        public Class<?> getType(Object bean);
    }

    private static class ReflectionAccessor implements BeanPropertyAccessor
    {
        private final Method readMethod, writeMethod;

        public ReflectionAccessor(PropertyDescriptor pd)
        {
            this.readMethod = pd.getReadMethod();
            if( readMethod != null )
                readMethod.setAccessible( true );
            this.writeMethod = pd.getWriteMethod();
            if( writeMethod != null )
                writeMethod.setAccessible( true );
        }

        @Override
        public Object get(Object bean) throws ReflectiveOperationException
        {
            if( readMethod == null )
            {
                throw new IllegalArgumentException( "Read method is not found" );
            }
            return readMethod.invoke( bean );
        }

        @Override
        public void set(Object bean, Object value) throws ReflectiveOperationException
        {
            if( writeMethod == null )
            {
                throw new IllegalArgumentException( "Write method is not found" );
            }
            writeMethod.invoke( bean, value );
        }

        @Override
        public Class<?> getType(Object bean)
        {
            if( readMethod == null )
            {
                throw new IllegalArgumentException( "Read method is not found" );
            }
            return readMethod.getReturnType();
        }
    }

    private static class ArrayAccessor implements BeanPropertyAccessor
    {
        int index;

        public ArrayAccessor(String propertyName)
        {
            if( !propertyName.startsWith( "[" ) || !propertyName.endsWith( "]" ) )
            {
                throw new RuntimeException( "Trying to access array property with invalid property name " + propertyName );
            }
            try
            {
                index = Integer.parseInt( propertyName.substring( 1, propertyName.length() - 1 ) );
            }
            catch( NumberFormatException e )
            {
                throw new RuntimeException( "Trying to access array property with invalid property name " + propertyName );
            }
        }

        @Override
        public Object get(Object bean) throws Exception
        {
            return Array.get( bean, index );
        }

        @Override
        public void set(Object bean, Object value) throws Exception
        {
            Array.set( bean, index, value );
        }

        @Override
        public Class<?> getType(Object bean)
        {
            return bean.getClass().getComponentType();
        }

    }

    private static class DynamicPropertyAccessor implements BeanPropertyAccessor
    {
        private final String propertyName;

        public DynamicPropertyAccessor(String propertyName)
        {
            this.propertyName = propertyName;
        }

        @Override
        public Object get(Object bean) throws Exception
        {
            return ( (DynamicPropertySet)bean ).getValue( propertyName );
        }

        @Override
        public void set(Object bean, Object value) throws Exception
        {
            ( (DynamicPropertySet)bean ).setValue( propertyName, value );
        }

        @Override
        public Class<?> getType(Object bean)
        {
            return ( (DynamicPropertySet)bean ).getType( propertyName );
        }
    }

    public static class ObjectPropertyAccessor
    {
        private final Object bean;
        private final BeanPropertyAccessor primary;

        public ObjectPropertyAccessor(BeanPropertyAccessor primary, Object bean)
        {
            this.bean = bean;
            this.primary = primary;
        }

        public Object get() throws Exception
        {
            return primary.get( this.bean );
        }

        public void set(Object value) throws Exception
        {
            primary.set( this.bean, value );
        }

        public Class<?> getType()
        {
            return primary.getType( this.bean );
        }
    }

    /**
     * Looks for
     * @param bean
     * @param propertyName
     * @return ObjectPropertyAccessor (like simplified PropertyDescriptor)
     * @throws IntrospectionException if property cannot be found
     */
    public static ObjectPropertyAccessor getBeanPropertyAccessor(Object bean, String propertyName) throws IntrospectionException
    {
        String[] components = propertyName.split( "/", -1 );
        BeanPropertyAccessor accessor = null;
        for( String component : components )
        {
            if( accessor != null )
            {
                try
                {
                    bean = accessor.get( bean );
                }
                catch( Exception e )
                {
                    throw new IntrospectionException( "Unable to read property from bean " + bean + " (path: " + propertyName
                            + "); error: " + e.getMessage() );
                }
            }
            if( bean == null )
            {
                throw new IntrospectionException( "Null object encountered during the property introspection (path: " + propertyName + ")" );
            }
            if( bean instanceof DynamicPropertySet )
            {
                accessor = new DynamicPropertyAccessor( component );
            }
            else if( bean.getClass().isArray() )
            {
                accessor = new ArrayAccessor( component );
            }
            else
            {
                Map<String, BeanPropertyAccessor> classAccessors = accessors.get( bean.getClass() );
                if( classAccessors == null )
                {
                    classAccessors = new HashMap<>();
                    Map<String, BeanPropertyAccessor> oldClassAccessors = accessors.putIfAbsent( bean.getClass(), classAccessors );
                    if( oldClassAccessors != null )
                        classAccessors = oldClassAccessors;
                }
                synchronized( classAccessors )
                {
                    accessor = classAccessors.get( component );
                    if( accessor == null )
                    {
                        ComponentModel model = ComponentFactory.getModel( bean );
                        Property property = model.findProperty( component );
                        if( property == null )
                        {
                            throw new IntrospectionException( "Cannot find property " + component + " in object of class "
                                    + bean.getClass() + " (full requested property path: " + propertyName + ")" );
                        }
                        accessor = new ReflectionAccessor( (PropertyDescriptor)property.getDescriptor() );
                        classAccessors.put( propertyName, accessor );
                    }
                }
            }
        }
        return new ObjectPropertyAccessor( accessor, bean );
    }

    /**
     * Faster equivalent of ComponentFactory.getModel(bean).findProperty(propertyName).getValue()
     * @param bean
     * @param propertyName path to the bean property
     * @return value of given bean property
     * @throws Exception
     */
    public static Object getBeanPropertyValue(Object bean, String propertyName) throws BeanException
    {
        try
        {
            return getBeanPropertyAccessor( bean, propertyName ).get();
        }
        catch( Exception e )
        {
            throw new BeanException( e, bean, propertyName, "reading" );
        }
    }

    /**
     * Faster equivalent of ComponentFactory.getModel(bean).findProperty(propertyName).setValue(value)
     * @param bean
     * @param propertyName path to the bean property
     * @param value value to set
     * @throws Exception
     */
    public static void setBeanPropertyValue(Object bean, String propertyName, Object value) throws BeanException
    {
        try
        {
            getBeanPropertyAccessor( bean, propertyName ).set( value );
        }
        catch( Exception e )
        {
            throw new BeanException( e, bean, propertyName, "writing" );
        }
    }

    /**
     * Faster equivalent of ComponentFactory.getModel(bean).findProperty(propertyName).getPropertyType()
     * @param bean
     * @param propertyName path to the bean property
     * @return property type
     * @throws Exception
     */
    public static Class<?> getBeanPropertyType(Object bean, String propertyName) throws BeanException
    {
        try
        {
            return getBeanPropertyAccessor( bean, propertyName ).getType();
        }
        catch( IntrospectionException e )
        {
            throw new BeanException( e, bean, propertyName, "reading type of" );
        }
    }
}
