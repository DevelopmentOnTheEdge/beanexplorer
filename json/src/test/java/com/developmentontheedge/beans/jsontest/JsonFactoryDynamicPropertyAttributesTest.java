package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.json.JsonPropertyAttributes;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonFactoryDynamicPropertyAttributesTest implements JsonPropertyAttributes
{
    private DynamicProperty dynamicProperty;

    @Before
    public void init()
    {
        dynamicProperty = new DynamicProperty("name", "Name", String.class, "testName");
        assertEquals("Name", JsonFactory.getMeta(dynamicProperty)
                .getJsonString(DISPLAY_NAME_ATTR).getString());
    }

    @Test
    public void testCanBeNull()
    {
        dynamicProperty.setCanBeNull(true);

        assertEquals(true, JsonFactory.getMeta(dynamicProperty)
                .getBoolean(CAN_BE_NULL_ATTR));
    }

    @Test
    public void testReloadOnChange()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CHANGE, true);

        assertEquals(true, JsonFactory.getMeta(dynamicProperty)
                .getBoolean(RELOAD_ON_CHANGE_ATTR));
    }

    @Test
    public void testReadonly()
    {
        dynamicProperty.setReadOnly(true);

        assertEquals(true, JsonFactory.getMeta(dynamicProperty)
                .getBoolean(READONLY_ATTR));
    }

    @Test
    public void testHidden()
    {
        dynamicProperty.setHidden(true);

        assertEquals(true, JsonFactory.getMeta(dynamicProperty)
                .getBoolean(HIDDEN_ATTR));
    }

}