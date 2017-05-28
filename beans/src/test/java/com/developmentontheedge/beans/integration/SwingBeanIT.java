/** $Id: BeanTest.java,v 1.5 2007/10/01 12:36:08 adolg Exp $ */
package com.developmentontheedge.beans.integration;

import com.developmentontheedge.beans.BeanTest;
import com.developmentontheedge.beans.swing.PropertyInspector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.swing.*;



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
abstract public class SwingBeanIT extends BeanTest
{

    public SwingBeanIT(String name)
    {
        super(name);
    }

    static protected String[] testNames  = {"testCreateBeanInstance", "testCreateModel",
            "testModelProperties", "testViewModel"};


    @SuppressWarnings( "deprecation" )
    public void testViewModel() throws Exception
    {
        BeanTest.inspector = new PropertyInspector();
        JFrame frame = new JFrame( this.toString() + " test" );
        frame.getContentPane().add( BeanTest.inspector );
        frame.setSize( 400, 500 );
        frame.setVisible( true );

        BeanTest.inspector.setComponentModel( BeanTest.model );
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        try
        {
            Class<?>[] parameterTypes = { String.class };
            Object[] parameters = new Object[1];
            java.lang.reflect.Constructor<?> constructor = BeanTest.testClass.getConstructor( parameterTypes );

            for( int i=0; i < testNames.length; i++ )
            {
                parameters[0] = testNames[i];
                suite.addTest( (Test)constructor.newInstance( parameters ) );
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            TestCase.fail(e.toString());
        }

        return suite;
    }
}
