package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;

/**
 * @see BeanInfoConstants
 */
public enum JsonPropertyAttributes
{
    TYPE_ATTR(null, "type"),
    DESCRIPTION_ATTR(null, "description"),

    DISPLAY_NAME_ATTR(BeanInfoConstants.DISPLAY_NAME, "displayName"),
    CAN_BE_NULL_ATTR(BeanInfoConstants.CAN_BE_NULL, "canBeNull"),
    HIDDEN_ATTR(BeanInfoConstants.HIDDEN, "hidden"),
    RAW_VALUE_ATTR(BeanInfoConstants.RAW_VALUE, "rawValue"),
    READONLY_ATTR(BeanInfoConstants.READ_ONLY, "readOnly"),
    RELOAD_ON_CHANGE_ATTR(BeanInfoConstants.RELOAD_ON_CHANGE, "reloadOnChange"),
    COLUMN_SIZE_ATTR(BeanInfoConstants.COLUMN_SIZE_ATTR, "columnSize"),
    TAG_LIST_ATTR(BeanInfoConstants.TAG_LIST_ATTR, "tagList"),
    GROUP_NAME_ATTR(BeanInfoConstants.GROUP_NAME, "groupName"),
    GROUP_ID_ATTR(BeanInfoConstants.GROUP_ID,"groupId"),
    MULTIPLE_SELECTION_LIST(BeanInfoConstants.MULTIPLE_SELECTION_LIST,"groupId"),

    STATUS_ATTR(BeanInfoConstants.STATUS,"groupId"),
    MESSAGE_ATTR(BeanInfoConstants.MESSAGE,"groupId");

    String beanInfoConstants;
    String name;

    JsonPropertyAttributes(String beanInfoConstants, String name)
    {
        this.beanInfoConstants = beanInfoConstants;
        this.name = name;
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
