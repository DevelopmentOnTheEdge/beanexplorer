package com.developmentontheedge.beans.swing;

import com.developmentontheedge.beans.BeanInfoEx;
import junit.framework.TestCase;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import static org.junit.Assert.*;

public class ToolTipPaneTest extends TestCase
{
    /** Standart JUnit constructor */
    public ToolTipPaneTest( String name )
    {
        super(name);
    }

    //////////////////////////////////////////////////////////////////
    // Test cases
    //

    static PropertyInspector inspector;
    public void testToolTipPane() throws Exception
    {
        Object bean = new SimpleBean();

        inspector = new PropertyInspector();
        inspector.setShowToolTipPane(true);
        inspector.explore(bean);

        JFrame frame = new JFrame();
        frame.getContentPane().add(inspector, BorderLayout.CENTER);
        //frame.setSize(400, 500);
        frame.pack();
        frame.setVisible(true);
    }


    public static class SimpleBean
    {
        public boolean isShowToolTipPane()                  { return inspector.isShowToolTipPane();   }
        public void    setShowToolTipPane(boolean value)    { inspector.setShowToolTipPane(value); }

        String stringProperty = "stringValue";
        public String getStringProperty()                   { return stringProperty;  }
        public void   setStringProperty(String value)       { stringProperty = value; }

        int integerProperty = 20;
        public int getIntegerProperty()                     { return integerProperty; }
        public void setIntegerProperty(int value)           { integerProperty = value; }

        Color colorProperty = Color.pink;
        public Color getColorProperty()                     { return colorProperty; }
        public void setColorProperty(Color value)           { colorProperty = value; }
    }

    public static class SimpleBeanBeanInfo extends BeanInfoEx
    {
        public SimpleBeanBeanInfo()
        {
            super( SimpleBean.class, null);
            beanDescriptor.setDisplayName("Simple bean");
            beanDescriptor.setShortDescription("Simple bean description");
        }

        public void initProperties() throws IntrospectionException
        {
            add(new PropertyDescriptor("showToolTipPane", beanClass), "tool tip pane", "Indicates whether tool tip pane should be shown");
            add(new PropertyDescriptor("stringProperty",  beanClass), "string",  "string <b>field</b> description");
            add(new PropertyDescriptor("integerProperty", beanClass), "int",     "integer field description very " +
                    "very very very very very very very very very very very very very very very very very very very " +
                    "very very very very very very very very very very very very very very very very very very very " +
                    "long desription");
            add(new PropertyDescriptor("colorProperty",   beanClass), "color",   "color field description");
        }
    }

}