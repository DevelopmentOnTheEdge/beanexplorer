package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import static com.developmentontheedge.beans.json.JsonPropertyAttributes.*;
import static com.developmentontheedge.beans.jsontest.JsonFactoryDpsTest.oneQuotes;
import static org.junit.Assert.*;

public class JsonFactoryDynamicPropertyAttributesTest
{
    private DynamicProperty dynamicProperty;

    @Before
    public void init()
    {
        dynamicProperty = new DynamicProperty("name", "Name", String.class, "testName");
    }

    @Test
    public void testNameValue()
    {
        assertEquals("Name", JsonFactory.dynamicPropertyMeta(dynamicProperty)
                .getJsonString(DISPLAY_NAME_ATTR.key).getString());
    }

    @Test
    public void testCanBeNull()
    {
        dynamicProperty.setCanBeNull(true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(CAN_BE_NULL_ATTR.key));

        dynamicProperty.setCanBeNull(false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(CAN_BE_NULL_ATTR.key));
    }

    @Test
    public void testReloadOnChange()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CHANGE, true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(RELOAD_ON_CHANGE_ATTR.key));

        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CHANGE, false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(RELOAD_ON_CHANGE_ATTR.key));
    }

    @Test
    public void testReadonly()
    {
        dynamicProperty.setReadOnly(true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(READ_ONLY_ATTR.key));

        dynamicProperty.setReadOnly(false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(READ_ONLY_ATTR.key));
    }

    @Test
    public void testHidden()
    {
        dynamicProperty.setHidden(true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(HIDDEN_ATTR.key));

        dynamicProperty.setHidden(false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(HIDDEN_ATTR.key));
    }

    @Test
    public void testMultipleSelectionList()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.MULTIPLE_SELECTION_LIST, true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(MULTIPLE_SELECTION_LIST_ATTR.key));

        dynamicProperty.setAttribute(BeanInfoConstants.MULTIPLE_SELECTION_LIST, false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(MULTIPLE_SELECTION_LIST_ATTR.key));
    }

    @Test
    public void testRawValue()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.RAW_VALUE, true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(RAW_VALUE_ATTR.key));

        dynamicProperty.setAttribute(BeanInfoConstants.RAW_VALUE, false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(RAW_VALUE_ATTR.key));
    }

    @Test
    public void testColumnSize()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.COLUMN_SIZE_ATTR, 30);
        assertEquals("'30'", oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(COLUMN_SIZE_ATTR.key)));

        dynamicProperty.setAttribute(BeanInfoConstants.COLUMN_SIZE_ATTR, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(COLUMN_SIZE_ATTR.key));
    }

    @Test
    public void testTagList()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.TAG_LIST_ATTR, new String[][]{
                new String[]{"foo","bar"},new String[]{"foo2","bar2"}
        });

        assertEquals("[['foo','bar'],['foo2','bar2']]",
                oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(TAG_LIST_ATTR.key)));

        dynamicProperty.setAttribute(BeanInfoConstants.TAG_LIST_ATTR, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(TAG_LIST_ATTR.key));
    }

    @Test
    public void testExtraAttributes()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.EXTRA_ATTRS, new String[][]{
                new String[]{"inputType","Creatable"},new String[]{"matchPos","any"}
        });

        assertEquals("[['inputType','Creatable'],['matchPos','any']]",
                oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(EXTRA_ATTRS.key)));

        dynamicProperty.setAttribute(BeanInfoConstants.EXTRA_ATTRS, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(EXTRA_ATTRS.key));
    }

    @Test
    public void testGroupName()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_NAME, "foo");
        assertEquals("'foo'", oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).get(GROUP_NAME_ATTR.key).toString()));

        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_NAME, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(GROUP_NAME_ATTR.key));
    }

    @Test
    public void testGroupId()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_ID, "1");

        assertEquals("{'displayName':'Name','groupId':'1'}",
                oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).toString())) ;

        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_ID, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(GROUP_ID_ATTR.key));
    }

    @Test
    public void testGroupIdInt()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_ID, 1);

        assertEquals("{'displayName':'Name','groupId':'1'}",
                oneQuotes( JsonFactory.dynamicPropertyMeta(dynamicProperty).toString()));
    }

    @Test
    public void testSeveralAttr()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_NAME, "foo");
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_ID, 1);
        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CHANGE, true);

        assertEquals("{" +
                        "'displayName':'Name'," +
                        "'reloadOnChange':true," +
                        "'groupName':'foo'," +
                        "'groupId':'1'" +
            "}",oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).toString()));
    }
}