package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;

import java.lang.reflect.Array;

enum JsonPropertyAttributes
{
    TYPE_ATTR(null, "type", Class.class),
    DESCRIPTION_ATTR(null, "description", String.class),

    DISPLAY_NAME_ATTR(BeanInfoConstants.DISPLAY_NAME, "displayName", String.class),
    CAN_BE_NULL_ATTR(BeanInfoConstants.CAN_BE_NULL, "canBeNull", Boolean.class),
    HIDDEN_ATTR(BeanInfoConstants.HIDDEN, "hidden", Boolean.class),
    RAW_VALUE_ATTR(BeanInfoConstants.RAW_VALUE, "rawValue", Boolean.class),
    READ_ONLY_ATTR(BeanInfoConstants.READ_ONLY, "readOnly", Boolean.class),
    RELOAD_ON_CHANGE_ATTR(BeanInfoConstants.RELOAD_ON_CHANGE, "reloadOnChange", Boolean.class),
    TAG_LIST_ATTR(BeanInfoConstants.TAG_LIST_ATTR, "tagList", Array.class),
    EXTRA_ATTRS(BeanInfoConstants.EXTRA_ATTRS, "extraAttrs", Array.class),
    GROUP_NAME_ATTR(BeanInfoConstants.GROUP_NAME, "groupName", String.class),
    GROUP_ID_ATTR(BeanInfoConstants.GROUP_ID, "groupId", Object.class),

    MULTIPLE_SELECTION_LIST_ATTR(BeanInfoConstants.MULTIPLE_SELECTION_LIST, "multipleSelectionList", Boolean.class),
    PASSWORD_FIELD(BeanInfoConstants.PASSWORD_FIELD, "passwordField", Boolean.class),
    LABEL_FIELD(BeanInfoConstants.LABEL_FIELD, "labelField", Boolean.class),

    COLUMN_SIZE_ATTR(BeanInfoConstants.COLUMN_SIZE_ATTR, "columnSize", Object.class),
    CSS_CLASSES(BeanInfoConstants.CSS_CLASSES, "cssClasses", String.class),
    STATUS_ATTR(BeanInfoConstants.STATUS, "status", String.class),
    MESSAGE_ATTR(BeanInfoConstants.MESSAGE, "message", String.class);


    public String beanInfoConstant;
    public String key;
    public Class<?> type;

    JsonPropertyAttributes(String beanInfoConstant, String key, Class<?> type)
    {
        this.beanInfoConstant = beanInfoConstant;
        this.key = key;
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "JsonPropertyAttributes{" +
                "beanInfoConstant='" + beanInfoConstant + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
