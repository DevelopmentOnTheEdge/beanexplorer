package com.developmentontheedge.beans;

public class DPBuilder
{
    private final static String UNSETTLED_CONSTANT_STRING_VALUE = "UNSETTLED_CONSTANT_STRING_VALUE";
    private final static String[][] UNSETTLED_CONSTANT_TAGS_VALUE =
            new String[][]{new String[]{"UNSETTLED_CONSTANT_STRING_VALUE"}};
    private final DynamicProperty property;

    public Object value = UNSETTLED_CONSTANT_STRING_VALUE;
    public Class<?> type = null;
    public String title = null;

    public Boolean hidden = null;
    public Boolean nullable = null;
    public Boolean readonly = null;
    public Boolean reloadOnChange = null;
    public String[][] tags = UNSETTLED_CONSTANT_TAGS_VALUE;
    public Boolean multiple = null;

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
        if (hidden != null) property.setHidden(hidden);
        if (nullable != null) property.setCanBeNull(nullable);
        if (readonly != null) property.setReadOnly(readonly);
        if (reloadOnChange != null) attr(BeanInfoConstants.RELOAD_ON_CHANGE, reloadOnChange);

        if (title != null) property.setDisplayName(title);
        if (tags != UNSETTLED_CONSTANT_TAGS_VALUE) attr(BeanInfoConstants.TAG_LIST_ATTR, tags);
        if (multiple != null) attr(BeanInfoConstants.MULTIPLE_SELECTION_LIST, multiple);
        return property;
    }
}
