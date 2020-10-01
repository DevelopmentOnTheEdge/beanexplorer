package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DynamicProperty;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import static com.developmentontheedge.beans.json.JsonPropertyAttributes.*;
import static com.developmentontheedge.beans.jsontest.DpsTest.oneQuotes;
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
                .getJsonString(displayName.name()).getString());
    }

    @Test
    public void testCanBeNull()
    {
        dynamicProperty.setCanBeNull(true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(canBeNull.name()));

        dynamicProperty.setCanBeNull(false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(canBeNull.name()));
    }

    @Test
    public void testReloadOnChange()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CHANGE, true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(reloadOnChange.name()));

        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CHANGE, false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(reloadOnChange.name()));
    }

    @Test
    public void testReloadOnClick()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CLICK, true);
        assertTrue(JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(reloadOnClick.name()));

        dynamicProperty.setAttribute(BeanInfoConstants.RELOAD_ON_CLICK, false);
        assertFalse(JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(reloadOnClick.name()));
    }

    @Test
    public void testReadonly()
    {
        dynamicProperty.setReadOnly(true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(readOnly.name()));

        dynamicProperty.setReadOnly(false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(readOnly.name()));
    }

    @Test
    public void testHidden()
    {
        dynamicProperty.setHidden(true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(hidden.name()));

        dynamicProperty.setHidden(false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(hidden.name()));
    }

    @Test
    public void testMultipleSelectionList()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.MULTIPLE_SELECTION_LIST, true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(multipleSelectionList.name()));

        dynamicProperty.setAttribute(BeanInfoConstants.MULTIPLE_SELECTION_LIST, false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(multipleSelectionList.name()));
    }

    @Test
    public void testRawValue()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.RAW_VALUE, true);
        assertEquals(true, JsonFactory.dynamicPropertyMeta(dynamicProperty).getBoolean(rawValue.name()));

        dynamicProperty.setAttribute(BeanInfoConstants.RAW_VALUE, false);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(rawValue.name()));
    }

    @Test
    public void testColumnSize()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.COLUMN_SIZE_ATTR, 30);
        assertEquals("'30'", oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(columnSize.name())));

        dynamicProperty.setAttribute(BeanInfoConstants.COLUMN_SIZE_ATTR, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(columnSize.name()));
    }

    @Test
    public void testInputSize()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.INPUT_SIZE_ATTR, 30);
        assertEquals("'30'", oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(inputSize.name())));

        dynamicProperty.setAttribute(BeanInfoConstants.INPUT_SIZE_ATTR, "30");
        assertEquals("'30'", oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(inputSize.name())));

        dynamicProperty.setAttribute(BeanInfoConstants.INPUT_SIZE_ATTR, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(inputSize.name()));
    }

    @Test
    public void testPlaceholder()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.PLACEHOLDER, "foo");
        assertEquals("'foo'", oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).get(placeholder.name()).toString()));

        dynamicProperty.setAttribute(BeanInfoConstants.PLACEHOLDER, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(placeholder.name()));
    }


    @Test
    public void testTagList()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.TAG_LIST_ATTR, new String[][]{
                new String[]{"foo","bar"},new String[]{"foo2","bar2"}
        });

        assertEquals("[['foo','bar'],['foo2','bar2']]",
                oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(tagList.name())));

        dynamicProperty.setAttribute(BeanInfoConstants.TAG_LIST_ATTR, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(tagList.name()));

        dynamicProperty.setAttribute(BeanInfoConstants.TAG_LIST_ATTR, new String[][]{});

        assertEquals("[]",
                oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(tagList.name())));
    }

    @Test
    public void testTagFromOneDimensionalArray()
    {
        String[] courtesies = new String[] {"Mr.", "Dr.", "Ms.", "Mrs.", "Miss", "Prof.", "Sir"};
        dynamicProperty.setAttribute(BeanInfoConstants.TAG_LIST_ATTR, courtesies);

        assertEquals("[['Mr.','Mr.'],['Dr.','Dr.'],['Ms.','Ms.'],['Mrs.','Mrs.'],['Miss','Miss'],['Prof.','Prof.'],['Sir','Sir']]",
                oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(tagList.name())));
    }

    @Test
    public void testExtraAttributes()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.EXTRA_ATTRS, new String[][]{
                new String[]{BeanInfoConstants.PROPERTY_INPUT_TYPE,"Creatable"},new String[]{"matchPos","any"}
        });

        assertEquals("[['" + BeanInfoConstants.PROPERTY_INPUT_TYPE + "','Creatable'],['matchPos','any']]",
                oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(extraAttrs.name())));

        dynamicProperty.setAttribute(BeanInfoConstants.EXTRA_ATTRS, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(extraAttrs.name()));
    }

//    @Test
//    public void testValidationRules()
//    {
//        dynamicProperty.setAttribute(BeanInfoConstants.VALIDATION_RULES, new String[][]{
//                new String[]{"integer","Please specify an integer number."}
//        });
//
//        assertEquals("[['integer','Please specify an integer number.']]",
//                oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(validationRules.name())));
//
//        dynamicProperty.setAttribute(BeanInfoConstants.VALIDATION_RULES, null);
//        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(validationRules.name()));
//
//        //map
//        dynamicProperty.setAttribute(BeanInfoConstants.VALIDATION_RULES,
//                Collections.singletonMap("number", "Please enter only digits.")
//        );
//        assertEquals("[['number','Please enter only digits.']]",
//                oneQuotes("" + JsonFactory.dynamicPropertyMeta(dynamicProperty).get(validationRules.name())));
//    }

    @Test
    public void testGroupName()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_NAME, "foo");
        assertEquals("'foo'", oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).get(groupName.name()).toString()));

        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_NAME, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(groupName.name()));
    }

    @Test
    public void testGroupId()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_ID, "1");

        assertEquals("{'displayName':'Name','groupId':'1'}",
                oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).toString())) ;

        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_ID, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(groupId.name()));
    }

    @Test
    public void testGroupIdInt()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.GROUP_ID, 1);

        assertEquals("{'displayName':'Name','groupId':'1'}",
                oneQuotes( JsonFactory.dynamicPropertyMeta(dynamicProperty).toString()));
    }

    @Test
    public void testTabId()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.TAB_ID, "1");

        assertEquals("{'displayName':'Name','tabId':'1'}",
                oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).toString())) ;

        dynamicProperty.setAttribute(BeanInfoConstants.TAB_ID, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(groupId.name()));
    }

    @Test
    public void testTabIdInt()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.TAB_ID, 1);

        assertEquals("{'displayName':'Name','tabId':'1'}",
                oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).toString())) ;

        dynamicProperty.setAttribute(BeanInfoConstants.TAB_ID, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(groupId.name()));
    }

    @Test
    public void testTabName()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.TAB_NAME, "tab 1");

        assertEquals("{'displayName':'Name','tabName':'tab 1'}",
                oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).toString())) ;

        dynamicProperty.setAttribute(BeanInfoConstants.TAB_NAME, null);
        assertEquals(false, JsonFactory.dynamicPropertyMeta(dynamicProperty).containsKey(tabName.name()));
    }

    @Test
    public void testDefaultValue()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.DEFAULT_VALUE, "test");

        assertEquals("{'displayName':'Name','defaultValue':'test'}",
                oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).toString()));
    }

    @Test
    @Ignore
    public void testDefaultValueArray()
    {
        dynamicProperty.setAttribute(BeanInfoConstants.DEFAULT_VALUE, new String[]{"test", "test2"});

        assertEquals("{'displayName':'Name','defaultValue':'test'}",
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
                        "'groupId':'1'," +
                        "'groupName':'foo'" +
            "}",oneQuotes(JsonFactory.dynamicPropertyMeta(dynamicProperty).toString()));
    }
}