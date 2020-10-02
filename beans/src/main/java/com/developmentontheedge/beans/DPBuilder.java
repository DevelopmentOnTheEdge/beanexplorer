package com.developmentontheedge.beans;

public class DPBuilder
{
    private final static String UNSETTLED_CONSTANT_STRING_VALUE = "UNSETTLED_CONSTANT_STRING_VALUE";
    private final static String[][] UNSETTLED_CONSTANT_TAGS_VALUE =
            new String[][]{new String[]{UNSETTLED_CONSTANT_STRING_VALUE}};
    private final DynamicProperty property;

    public Object value = UNSETTLED_CONSTANT_STRING_VALUE;
    public Class<?> type = null;
    public String title = null;
    public String[][] tags = UNSETTLED_CONSTANT_TAGS_VALUE;

    public Boolean hidden = null;
    public Boolean nullable = null;
    public Boolean readonly = null;
    public Boolean reloadOnChange = null;
    public Boolean reloadOnClick = null;
    public Boolean rawValue = null;
    public Boolean multiple = null;
    public Boolean passwordField = null;

    public String groupID = null;
    public String groupName = null;
    public String groupClasses = null;
    public Boolean groupInitiallyClosed = null;

    public String tabID = null;
    public String tabName = null;
    public String tabClasses = null;

    public String cssClasses = null;
    public String status = null;
    public String message = null;
    public String placeholder = null;

    public int size = -1;
    public int columnSize = -1;

    public DPBuilder(String name)
    {
        property = new DynamicProperty(name, String.class);
    }

    public DPBuilder(String name, String title)
    {
        this(name);
        this.title = title;
    }

    public DPBuilder(DynamicProperty property)
    {
        this.property = property;
    }

    public void attr( String name, Object value )
    {
        property.setAttribute( name, value );
    }

    public DynamicProperty build()
    {
        if (value != UNSETTLED_CONSTANT_STRING_VALUE) property.setValue(value);
        if (type != null) property.setType(type);
        if (title != null) property.setDisplayName(title);
        if (tags != UNSETTLED_CONSTANT_TAGS_VALUE) attr(BeanInfoConstants.TAG_LIST_ATTR, tags);

        if (hidden != null) property.setHidden(hidden);
        if (nullable != null) property.setCanBeNull(nullable);
        if (readonly != null) property.setReadOnly(readonly);
        if (reloadOnChange != null) attr(BeanInfoConstants.RELOAD_ON_CHANGE, reloadOnChange);
        if (reloadOnClick != null) attr(BeanInfoConstants.RELOAD_ON_CLICK, reloadOnClick);
        if (rawValue != null) attr(BeanInfoConstants.RAW_VALUE, rawValue);
        if (multiple != null) attr(BeanInfoConstants.MULTIPLE_SELECTION_LIST, multiple);
        if (passwordField != null) attr(BeanInfoConstants.PASSWORD_FIELD, passwordField);

        if (groupID != null) attr(BeanInfoConstants.GROUP_ID, groupID);
        if (groupName != null) attr(BeanInfoConstants.GROUP_NAME, groupName);
        if (groupClasses != null) attr(BeanInfoConstants.GROUP_CLASSES, groupClasses);
        if (groupInitiallyClosed != null) attr(BeanInfoConstants.GROUP_INITIALLY_CLOSED, groupInitiallyClosed);

        if (tabID != null) attr(BeanInfoConstants.TAB_ID, tabID);
        if (tabName != null) attr(BeanInfoConstants.TAB_NAME, tabName);
        if (tabClasses != null) attr(BeanInfoConstants.TAB_CLASSES, tabClasses);

        if (cssClasses != null) attr(BeanInfoConstants.CSS_CLASSES, cssClasses);
        if (status != null) attr(BeanInfoConstants.STATUS, status);
        if (message != null) attr(BeanInfoConstants.MESSAGE, message);
        if (placeholder != null) attr(BeanInfoConstants.PLACEHOLDER, placeholder);

        if (size != -1) attr(BeanInfoConstants.INPUT_SIZE_ATTR, size);
        if (columnSize != -1) attr(BeanInfoConstants.COLUMN_SIZE_ATTR, columnSize);

        return property;
    }
}
