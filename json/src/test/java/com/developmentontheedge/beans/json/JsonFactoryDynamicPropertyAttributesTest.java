package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.DynamicPropertySetSupport;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonFactoryDynamicPropertyAttributesTest implements JsonPropertyAttributes
{
    private DynamicPropertySetSupport dps;
    private String name = "name";

    @Before
    public void init()
    {
        dps = new DynamicPropertySetSupport();
        dps.add(new DynamicProperty(name, "Name", String.class, "testName"));
        assertEquals("Name", JsonFactory.dpsMeta(dps).getJsonObject(name)
                .getJsonString(DISPLAY_NAME_ATTR).getString());
    }

    @Test
    public void testCanBeNull()
    {
        dps.getAsBuilder(name).nullable();

        assertNotNull(JsonFactory.dpsMeta(dps).getJsonObject(name));
        assertEquals(true, JsonFactory.dpsMeta(dps).getJsonObject(name)
                .getBoolean(CAN_BE_NULL_ATTR));
    }

    @Test
    public void testReloadOnChange()
    {
        dps.getAsBuilder(name).reloadOnChange();

        assertNotNull(JsonFactory.dpsMeta(dps).getJsonObject(name));
        assertEquals(true, JsonFactory.dpsMeta(dps).getJsonObject(name)
                .getBoolean(RELOAD_ON_CHANGE_ATTR));
    }

    @Test
    public void testReadonly()
    {
        dps.getAsBuilder(name).readonly();

        assertNotNull(JsonFactory.dpsMeta(dps).getJsonObject(name));
        assertEquals(true, JsonFactory.dpsMeta(dps).getJsonObject(name)
                .getBoolean(READONLY_ATTR));
    }

    @Test
    public void testRawValue()
    {
        dps.getAsBuilder(name).rawValue();

        assertNotNull(JsonFactory.dpsMeta(dps).getJsonObject(name));
        assertEquals(true, JsonFactory.dpsMeta(dps).getJsonObject(name)
                .getBoolean(RAW_VALUE_ATTR));
    }

}