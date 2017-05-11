/** $Id: BeanTest.java,v 1.5 2007/10/01 12:36:08 adolg Exp $ */
package com.developmentontheedge.beans;


import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.swing.PropertyInspector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * General class to test a bean.
 *
 * To define a subclass that provides testing of a specific bean type you
 * should to do the following:
 * <ol>
 * <li> implement <code>testModelProperties</code> method. </li>
 * <li> redefine <code>suite</code> method. While there is a general scenarious
 * of test you may redefine this method by following way:
 * <pre>
 * public static Test suite()
 * {
 *     beanClass = YOUR_BEAN.class;
 *     testClass = YOUR_BEAN_TEST.class;
 *
 *     // in such way you can specify tests and order haow the will be invoked
 *     //testNames  = {"testCreateBeanInstance", "testCreateModel", "testModelProperties"};
 *
 *     return BeanTest.suite();
 *   }
 * </pre></li></ol>
 */
abstract public class BeanTest extends TestCase
{
    /**
     * The bean class.
     * It should be specified in subclass init method.
     */
    static protected Class<?> beanClass;

    /**
     * The BeanTest subclass.
     * It should be specified in subclass init method.
     */
    static protected Class<?> testClass;

    /**
     * Array of test case names.
     * Can be redefined in in subclass init method.
     */
    static protected String[] testNames  = {"testCreateBeanInstance", "testCreateModel",
                                            "testModelProperties"};

    /** The tested bean. */
    static protected Object bean;

    /** The bean model. */
    static protected ComponentModel model;

    static protected PropertyInspector inspector;

    protected Logger cat = Logger.getLogger(BeanTest.class.getName());

    public BeanTest( String name )
    {                                                                                 
        super( name );
        cat.info( "Start test: " + name );
     }

    /**
     * The basic procedure to check the bean property.
     *
     * @pending medium check other property atrtributes.
     */
    protected void checkProperty(String propertyName, Class<?> type, Object value, boolean isReadOnly )
    throws NoSuchMethodException
    {
        String msg = "Property <" + propertyName + "> ";
        Property property = model.findProperty( propertyName );

        assertNotNull( msg + "not found", property );
        assertEquals( msg + "getName() is incorrect", propertyName, property.getName() );
        assertNotNull( msg + "value is null", property.getValue() );
        assertEquals( msg + "type is wrong", property.getValueClass(), type );
        assertEquals( msg + "value is correct", property.getValue(), value );
        assertEquals( msg + "read-only flag incorrect", isReadOnly, property.isReadOnly() );
    }

    public void testCreateBeanInstance() throws Exception
    {
        bean = beanClass.newInstance();
        assertNotNull("Bean instance is created", bean);
    }

    /** Creates the bean model. */
    public void testCreateModel() throws Exception
    {
        model = ComponentFactory.getModel( bean );
        assertNotNull( "model is created", model );
    }

    abstract public void testModelProperties() throws Exception;

    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        try
        {
            Class<?>[] parameterTypes = { String.class };
            Object[] parameters = new Object[1];
            java.lang.reflect.Constructor<?> constructor = testClass.getConstructor( parameterTypes );

            for( int i=0; i < testNames.length; i++ )
            {
                parameters[0] = testNames[i];
                suite.addTest( (Test)constructor.newInstance( parameters ) );
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail(e.toString());
        }

        return suite;
    }
}
