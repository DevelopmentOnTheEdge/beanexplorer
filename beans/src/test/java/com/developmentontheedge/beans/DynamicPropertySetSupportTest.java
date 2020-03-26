package com.developmentontheedge.beans;

import org.junit.BeforeClass;
import org.junit.Test;

import java.beans.IntrospectionException;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.developmentontheedge.beans.BeanInfoConstants.GROUP_ID;
import static com.developmentontheedge.beans.BeanInfoConstants.RELOAD_ON_CHANGE;
import static org.junit.Assert.*;

public class DynamicPropertySetSupportTest
{
    private static DynamicPropertySet parameters = new DynamicPropertySetSupport();
    private static Date date = new Date(0);
    private static String propName = "test";

    @BeforeClass
    public static void init()
    {
        DynamicProperty prop = new DynamicProperty( propName, "Дата выдачи", Date.class,  date);
        parameters.add( prop );
        assertEquals(1, parameters.size());
        assertEquals(propName, parameters.getProperty(propName).getName());

        assertEquals("DPS(com.developmentontheedge.beans.DynamicPropertySetSupport):\n" +
                "  1. test (class java.sql.Date) - 1970-01-01", parameters.toString());
    }

    @Test
    public void testDpsGet()
    {
        assertEquals(Date.class, parameters.getType(propName));
        assertEquals(date, parameters.getValue(propName));
        assertEquals(date.toString(), parameters.getValueAsString(propName));
    }

    @Test
    public void test() throws Exception
    {
        parameters.addAfter(propName, new DynamicProperty( "nameAfter", "Name", String.class,  "value"));
        assertEquals("value", parameters.getProperty("nameAfter").getValue());

        parameters.addBefore(propName, new DynamicProperty( "nameBefore", "Name", String.class,  "value"));
        assertEquals("value", parameters.getProperty("nameBefore").getValue());

        List<DynamicProperty> list = StreamSupport.stream(parameters.spliterator(), false)
                .collect(Collectors.toList());

        assertEquals("nameBefore",list.get(0).getName());
        assertEquals(propName,list.get(1).getName());
        assertEquals("nameAfter",list.get(2).getName());


        ((DynamicPropertySetSupport)parameters).setReadOnlyToAllChildren(true);
        ((DynamicPropertySetSupport)parameters).setCanBeNullToAllChildren(true);
        ((DynamicPropertySetSupport)parameters).setExpertToAllChildren(true);
        ((DynamicPropertySetSupport)parameters).setAttributeToAllChildren(RELOAD_ON_CHANGE, true);
        parameters.setPropertyAttribute(propName, GROUP_ID, 0);

        for (Map.Entry<String, Object> p : parameters.asMap().entrySet()){
            DynamicProperty property = parameters.getProperty(p.getKey());
            assertEquals(true, property.isReadOnly());
            assertEquals(true, property.isCanBeNull());
            assertEquals(true, property.isExpert());
            assertEquals(true, property.getAttribute(RELOAD_ON_CHANGE));
        }
        assertEquals(0, parameters.getProperty(propName).getAttribute(GROUP_ID));

    }

    @Test
    public void testRenameProperty()
    {
        parameters.add(new DynamicProperty( "nameR1", "Name", String.class,  "valueR1"));
        parameters.renameProperty("nameR1", "nameR2");
        assertEquals("valueR1", parameters.getProperty("nameR2").getValue());

        assertEquals("valueR1", parameters.remove("nameR2"));
    }

    @Test
    public void testMakeBetterDisplayName(){
        DynamicProperty dynamicProperty = new DynamicProperty("nameBetterDisplayName",
                DynamicPropertySetSupport.makeBetterDisplayName("phonenumber"), String.class, "valueR1");
        assertEquals("Phone Number",dynamicProperty.getDisplayName());
    }

    @Test
    public void testClone() throws Exception
    {
        DynamicProperty property = new DynamicProperty("nameR1", "Name", String.class, "value");
        DynamicProperty newProperty;

        newProperty = DynamicPropertySetSupport.cloneProperty(property);
        property.setValue("newValue");
        assertEquals("value",newProperty.getValue());
    }

    @Test
    public void getAsBuilder() throws Exception
    {
        ((DynamicPropertySetSupport)parameters).getAsBuilder(propName).expert();
        assertTrue(parameters.getProperty(propName).isExpert());
    }

}