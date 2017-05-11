/** $Id: SimpleBeanTest.java,v 1.3 2001/10/04 07:38:40 zha Exp $ */
package com.developmentontheedge.beans;

import junit.framework.Test;

import java.awt.*;

/**
 *
 */
public class SimpleBeanTest extends BeanTest
{
    public SimpleBeanTest(String name)
    {
        super(name);
    }

    @Override
    public void testModelProperties() throws Exception
    {
        assertEquals("Property count", new Integer(5), // plus one for getClass
                     new Integer(model.getPropertyCount()));

        SimpleBean simpleBean = (SimpleBean)bean;

        checkProperty( "stringProperty",  String.class,   simpleBean.getStringProperty(),  false);
        checkProperty( "booleanProperty", Boolean.class,  simpleBean.getBooleanProperty(), false);
        checkProperty( "integerProperty", Integer.class,  simpleBean.getIntegerProperty(), false);
        checkProperty( "colorProperty",   Color.class,    simpleBean.getColorProperty(),   false);
    }

    public static Test suite()
    {
        beanClass = SimpleBean.class;
        testClass = SimpleBeanTest.class;

        return BeanTest.suite();
    }
}
