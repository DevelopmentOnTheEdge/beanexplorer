package com.developmentontheedge.beans.json;

import com.developmentontheedge.beans.BeanInfoConstants;

import java.lang.reflect.Array;


enum JsonPropertyAttributes
{
    type(null, Class.class),
    description(null, String.class),

    displayName(BeanInfoConstants.DISPLAY_NAME, String.class),
    canBeNull(BeanInfoConstants.CAN_BE_NULL, Boolean.class),
    hidden(BeanInfoConstants.HIDDEN, Boolean.class),
    rawValue(BeanInfoConstants.RAW_VALUE, Boolean.class),
    readOnly(BeanInfoConstants.READ_ONLY, Boolean.class),
    reloadOnChange(BeanInfoConstants.RELOAD_ON_CHANGE, Boolean.class),
    reloadOnFocusOut(BeanInfoConstants.RELOAD_ON_FOCUS_OUT, Boolean.class),
    tagList(BeanInfoConstants.TAG_LIST_ATTR, Array.class),
    extraAttrs(BeanInfoConstants.EXTRA_ATTRS, Array.class),

    groupId(BeanInfoConstants.GROUP_ID, Object.class),
    groupName(BeanInfoConstants.GROUP_NAME, String.class),
    groupClasses(BeanInfoConstants.GROUP_CLASSES, String.class),

    multipleSelectionList(BeanInfoConstants.MULTIPLE_SELECTION_LIST, Boolean.class),
    passwordField(BeanInfoConstants.PASSWORD_FIELD, Boolean.class),
    labelField(BeanInfoConstants.LABEL_FIELD, Boolean.class),

    columnSize(BeanInfoConstants.COLUMN_SIZE_ATTR, Object.class),
    inputSize(BeanInfoConstants.INPUT_SIZE_ATTR, Object.class),
    cssClasses(BeanInfoConstants.CSS_CLASSES, String.class),
    placeholder(BeanInfoConstants.PLACEHOLDER, String.class),

    defaultValue(BeanInfoConstants.DEFAULT_VALUE, Object.class),
    validationRules(BeanInfoConstants.VALIDATION_RULES, POJOorListOfPOJO.class),
    status(BeanInfoConstants.STATUS, String.class),
    message(BeanInfoConstants.MESSAGE, String.class),

    children(null, null),
    dictionary(null, null);

    public String beanInfoConstant;
    public Class<?> attrType;

    JsonPropertyAttributes(String beanInfoConstant, Class<?> attrType)
    {
        this.beanInfoConstant = beanInfoConstant;
        this.attrType = attrType;
    }

    @Override
    public String toString()
    {
        return "JsonPropertyAttributes{" +
                "beanInfoConstant='" + beanInfoConstant + '\'' +
                ", name='" + name() + '\'' +
                '}';
    }

    class POJOorListOfPOJO {

    }
}
