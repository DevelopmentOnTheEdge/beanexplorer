package com.developmentontheedge.beans;

import org.junit.BeforeClass;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.sql.Date;

import static com.developmentontheedge.beans.BeanInfoConstants.*;
import static org.junit.Assert.*;

public class DynamicPropertySetSupportTest
{
    private static DynamicPropertySet parameters = new DynamicPropertySetSupport();
    private static Date date = new Date(0);
    private static String[][] tags = {{"1", "ФИО"}, {"2", "ИД"}};
    private static String propName = "reasonDocissueDate";
    @BeforeClass
    public static void init(){
        DynamicProperty prop = new DynamicProperty( propName, "Дата выдачи", Date.class,  date);
        prop.setCanBeNull( true );
        prop.setReadOnly( true );
        prop.setHidden(true);
        prop.setAttribute( GROUP_ID, 0 );
        prop.setAttribute( GROUP_NAME, "reasonDocissue" );
        prop.setAttribute( RELOAD_ON_CHANGE, true);
        prop.setAttribute( TAG_LIST_ATTR, tags );
        parameters.add( prop );
    }

    @Test
    public void testDynamicProperty()
    {
        assertEquals(1, parameters.size());
        DynamicProperty property = parameters.getProperty(propName);
        assertEquals(date, property.getValue());
        assertEquals(Date.class, property.getType());
        assertEquals("Дата выдачи", property.getDisplayName());
        assertTrue(property.isCanBeNull());
        assertTrue(property.isReadOnly());
        assertTrue(property.isHidden());
        assertEquals(0, property.getAttribute(GROUP_ID));
        assertEquals("reasonDocissue", property.getAttribute(GROUP_NAME));
        assertEquals(true, property.getAttribute(RELOAD_ON_CHANGE));
        assertEquals(tags, property.getAttribute(TAG_LIST_ATTR));
    }

    @Test
    public void testDpsGet(){
        assertEquals(Date.class, parameters.getType(propName));
        assertEquals(date, parameters.getValue(propName));
        assertEquals(date.toString(), parameters.getValueAsString(propName));
    }

    @Test
    public void testGetPropertyDescriptor()
    {
        PropertyDescriptor propertyDescriptor = parameters.getPropertyDescriptor(propName);
        assertNotNull(propertyDescriptor);
        assertEquals(null, propertyDescriptor.getPropertyType());
        assertEquals(propName, propertyDescriptor.getName());
        assertEquals("Дата выдачи", propertyDescriptor.getDisplayName());
        assertEquals(true, propertyDescriptor.isHidden());
    }
}