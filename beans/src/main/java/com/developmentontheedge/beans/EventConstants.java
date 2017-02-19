package com.developmentontheedge.beans;

public interface EventConstants
{
    /**
     * The EVT_XXXXXXXXXXXXX flags are intended to be postfixed
     * to property names when events like <i>PropertyChangeEvent</i>
     * are fired. This allows the component developer to pass some extra
     * information to PropertyInspector so it can utilize the event to redisplay
     * the bean. In a nutshell it is used to indicate changes of meta information
     * about the properties rather than property values.
     * <br>
     * The only exclusion is <i>EVT_SET_VALUE</i> flag which
     * means the same thing as no flag at all.
     */
    String EVT_SET_VALUE = "";

    /**
     * This flag is used by dynamic and indexed properties
     * when new element was added.
     */
    String EVT_PROPERTY_ADDED = ".added";

    /**
     * This flag is used by dynamic and indexed properties
     * when new element was removed.
     */
    String EVT_PROPERTY_REMOVED = ".removed";

    /**
     * Used to indicate the read-only attribute of the property has changed
     */
    String EVT_READ_ONLY = ".read-only";

    /**
     * Used to indicate the display name of the property has changed
     */
    String EVT_DISPLAY_NAME = ".display-name";

    /**
     * This must be postfixed to property name when visiblity flag
     * is changed at runtime using <i>setHidden</i>.
     */
    String EVT_VISIBILITY_CHANGE = ".visibility-change";
}


