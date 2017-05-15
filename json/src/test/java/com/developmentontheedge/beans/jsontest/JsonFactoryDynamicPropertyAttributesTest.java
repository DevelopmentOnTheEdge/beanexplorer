package com.developmentontheedge.beans.jsontest;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;
import com.developmentontheedge.beans.json.JsonFactory;
import com.developmentontheedge.beans.json.JsonPropertyAttributes;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;


import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryDynamicPropertyAttributesTest implements JsonPropertyAttributes
{
    private DynamicProperty dynamicProperty;

    @Before
    public void init()
    {
        dynamicProperty = new DynamicProperty("name", "Name", String.class, "testName");
        assertEquals("Name", JsonFactory.getPropertyMeta(dynamicProperty)
                .getJsonString(DISPLAY_NAME_ATTR).getString());
    }

    @Test
    public void testCanBeNull()
    {
        dynamicProperty.setCanBeNull(true);
        assertEquals(true, JsonFactory.getPropertyMeta(dynamicProperty).getBoolean(CAN_BE_NULL_ATTR));

        dynamicProperty.setCanBeNull(false);
        assertEquals(false, JsonFactory.getPropertyMeta(dynamicProperty).containsKey(CAN_BE_NULL_ATTR));
    }

    @Test
    public void testReloadOnChange()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CHANGE, true);
        assertEquals(true, JsonFactory.getPropertyMeta(dynamicProperty).getBoolean(RELOAD_ON_CHANGE_ATTR));

        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CHANGE, false);
        assertEquals(false, JsonFactory.getPropertyMeta(dynamicProperty).containsKey(RELOAD_ON_CHANGE_ATTR));
    }

    @Test
    public void testReadonly()
    {
        dynamicProperty.setReadOnly(true);
        assertEquals(true, JsonFactory.getPropertyMeta(dynamicProperty).getBoolean(READONLY_ATTR));

        dynamicProperty.setReadOnly(false);
        assertEquals(false, JsonFactory.getPropertyMeta(dynamicProperty).containsKey(READONLY_ATTR));
    }

    @Test
    public void testHidden()
    {
        dynamicProperty.setHidden(true);
        assertEquals(true, JsonFactory.getPropertyMeta(dynamicProperty).getBoolean(HIDDEN_ATTR));

        dynamicProperty.setHidden(false);
        assertEquals(false, JsonFactory.getPropertyMeta(dynamicProperty).containsKey(HIDDEN_ATTR));
    }

    @Test
    public void testRawValue()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.RAW_VALUE, true);
        assertEquals(true, JsonFactory.getPropertyMeta(dynamicProperty).getBoolean(RAW_VALUE_ATTR));

        dynamicProperty.setAttribute(BeanInfoConstants.RAW_VALUE, false);
        assertEquals(false, JsonFactory.getPropertyMeta(dynamicProperty).containsKey(RAW_VALUE_ATTR));
    }

    @Test
    public void testColumnSize()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.COLUMN_SIZE_ATTR, 30);
        assertEquals("'30'", oneQuotes("" + JsonFactory.getPropertyMeta(dynamicProperty).get(COLUMN_SIZE_ATTR)));

        dynamicProperty.setAttribute(BeanInfoConstants.COLUMN_SIZE_ATTR, null);
        assertEquals(false, JsonFactory.getPropertyMeta(dynamicProperty).containsKey(COLUMN_SIZE_ATTR));
    }

    @Test
    public void testTagList()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.TAG_LIST_ATTR, ImmutableMap.of("foo","bar","foo2","bar2"));
        assertEquals("[{'key':'foo','value':'bar'},{'key':'foo2','value':'bar2'}]",
                oneQuotes("" + JsonFactory.getPropertyMeta(dynamicProperty).get(TAG_LIST_ATTR)));

        dynamicProperty.setAttribute(BeanInfoConstants.TAG_LIST_ATTR, null);
        assertEquals(false, JsonFactory.getPropertyMeta(dynamicProperty).containsKey(TAG_LIST_ATTR));
    }

    @Test
    public void testGroupName()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_NAME, "foo");
        assertEquals("'foo'", oneQuotes(JsonFactory.getPropertyMeta(dynamicProperty).get(GROUP_NAME).toString()));

        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_NAME, null);
        assertEquals(false, JsonFactory.getPropertyMeta(dynamicProperty).containsKey(GROUP_NAME));
    }

    @Test
    public void testGroupId()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_ID, 1);

        assertEquals(1L, Long.parseLong(JsonFactory.getPropertyMeta(dynamicProperty).get(GROUP_ID).toString()) );
        assertEquals(1, Integer.parseInt(JsonFactory.getPropertyMeta(dynamicProperty).get(GROUP_ID).toString()) );

        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_ID, null);
        assertEquals(false, JsonFactory.getPropertyMeta(dynamicProperty).containsKey(GROUP_ID));
    }

}