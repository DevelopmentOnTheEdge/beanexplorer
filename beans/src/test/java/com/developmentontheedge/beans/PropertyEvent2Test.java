/** $Id: PropertyEvent2Test.java,v 1.1 2001/05/20 13:22:48 fedor Exp $ */
package com.developmentontheedge.beans;

import junit.framework.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PropertyEvent2Test extends BeanTest implements PropertyChangeListener
{
    public PropertyEvent2Test(String name)
    {
        super(name);
    }

    public void testModelProperties() throws Exception
    {}

    public void testViewModel() throws Exception
    {
        super.testViewModel();
        model.addPropertyChangeListener(this);
    }

    public void testMessageFromValueEditor()                {}
    public void testMessagePropagationFromValueEditor()     {}

    public void testMessageFromBindedProperty()             {}
    public void testMessagePropagationFromBindedProperty()  {}

    public void propertyChange(PropertyChangeEvent evt)
    {
        String className = evt.getSource().getClass().getName();
        className = className.substring(className.lastIndexOf('.') + 1);

        cat.info("change: " + className + "." + evt.getPropertyName() +
                 ": '" + evt.getOldValue() + "' -> '" + evt.getNewValue() +
                 "'; propagated by " + evt.getPropagationId());
    }

    public static Test suite()
    {
        beanClass = ChildBean.class;
        testClass = PropertyEvent2Test.class;

        return BeanTest.suite();
    }
}


