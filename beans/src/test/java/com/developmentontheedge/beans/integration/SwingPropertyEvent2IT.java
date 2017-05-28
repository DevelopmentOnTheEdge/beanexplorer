/** $Id: PropertyEvent2Test.java,v 1.1 2001/05/20 13:22:48 fedor Exp $ */
package com.developmentontheedge.beans.integration;

import com.developmentontheedge.beans.ChildBean;
import junit.framework.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SwingPropertyEvent2IT extends SwingBeanIT implements PropertyChangeListener
{
    public SwingPropertyEvent2IT(String name)
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
        testClass = SwingPropertyEvent2IT.class;

        return SwingBeanIT.suite();
    }
}


