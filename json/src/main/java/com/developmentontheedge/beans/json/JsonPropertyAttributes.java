package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;

/**
 * @see BeanInfoConstants
 */
public enum JsonPropertyAttributes
{
    TYPE_ATTR(null, "type", Class.class),
    DESCRIPTION_ATTR(null, "description", String.class),

    DISPLAY_NAME_ATTR(BeanInfoConstants.DISPLAY_NAME, "displayName", String.class),
    CAN_BE_NULL_ATTR(BeanInfoConstants.CAN_BE_NULL, "canBeNull", Boolean.class),
    HIDDEN_ATTR(BeanInfoConstants.HIDDEN, "hidden", Boolean.class),
    RAW_VALUE_ATTR(BeanInfoConstants.RAW_VALUE, "rawValue", Boolean.class),
    READONLY_ATTR(BeanInfoConstants.READ_ONLY, "readOnly", Boolean.class),
    RELOAD_ON_CHANGE_ATTR(BeanInfoConstants.RELOAD_ON_CHANGE, "reloadOnChange", Boolean.class),
    COLUMN_SIZE_ATTR(BeanInfoConstants.COLUMN_SIZE_ATTR, "columnSize", String.class),
    TAG_LIST_ATTR(BeanInfoConstants.TAG_LIST_ATTR, "tagList", String.class),
    GROUP_NAME_ATTR(BeanInfoConstants.GROUP_NAME, "groupName", String.class),
    GROUP_ID_ATTR(BeanInfoConstants.GROUP_ID, "groupId", String.class),
    MULTIPLE_SELECTION_LIST(BeanInfoConstants.MULTIPLE_SELECTION_LIST, "multipleSelectionList", Boolean.class),

    STATUS_ATTR(BeanInfoConstants.STATUS, "status", String.class),
    MESSAGE_ATTR(BeanInfoConstants.MESSAGE, "message", String.class);


    public String beanInfoConstants;
    public String name;
    public Class<?> type;

    JsonPropertyAttributes(String beanInfoConstants, String name, Class<?> type)
    {
        this.beanInfoConstants = beanInfoConstants;
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "JsonPropertyAttributes{" +
                "beanInfoConstants='" + beanInfoConstants + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
