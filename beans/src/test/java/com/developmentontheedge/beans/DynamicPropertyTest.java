package com.developmentontheedge.beans;

import org.junit.BeforeClass;
import org.junit.Test;

import java.beans.PropertyDescriptor;
import java.sql.Date;

import static com.developmentontheedge.beans.BeanInfoConstants.*;
import static org.junit.Assert.*;

public class DynamicPropertyTest
{
    private static DynamicProperty property;
    private static Date date = new Date(0);
    private static String[][] tags = {{"1", "ФИО"}, {"2", "ИД"}};
    private static String propName = "reasonDocissueDate";

    @BeforeClass
    public static void init(){
        property = new DynamicProperty( propName, "Дата выдачи", Date.class,  date);
        property.setCanBeNull( true );
        property.setReadOnly( true );
        property.setHidden(true);
        property.setAttribute( GROUP_ID, 0 );
        property.setAttribute( GROUP_NAME, "reasonDocissue" );
        property.setAttribute( RELOAD_ON_CHANGE, true);
        property.setAttribute( TAG_LIST_ATTR, tags );
    }

    @Test
    public void testDynamicProperty()
    {
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
    public void testGetPropertyDescriptor()
    {
        PropertyDescriptor propertyDescriptor = property.getDescriptor();
        assertNotNull(propertyDescriptor);
        assertEquals(null, propertyDescriptor.getPropertyType());
        assertEquals(propName, propertyDescriptor.getName());
        assertEquals("Дата выдачи", propertyDescriptor.getDisplayName());
        assertEquals(true, propertyDescriptor.isHidden());
    }
}