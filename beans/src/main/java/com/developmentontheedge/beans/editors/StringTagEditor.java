package com.developmentontheedge.beans.editors;

import java.beans.FeatureDescriptor;

/**
 * Class to present property value defined by some enumeration.
 */
public class StringTagEditor extends CustomEditorSupport
{
    public static final String TAGS_KEY = "TAGS";

    protected StringTagEditor()
    {
    }

    @Override
    public String getAsText()
    {
        return (String)getValue();
    }

    @Override
    public void setAsText(String text)
    {
        setValue(text);
    }
    
    @Override
    public boolean supportsCustomEditor()
    {
        return false;
    }

    /**
     * Returns an array of String representation of Integer values (enum values).
     * This method is used for filling values of combo box editor.
     * If the property value must be one of a set of known tagged values,
     * then this method should return an array of the tags.
     *
     * @return The tag values for this property.  May be null if this
     *   property cannot be represented as a tagged value.
     *
     */
    @Override
    public String[] getTags()
    {
        FeatureDescriptor fd = getDescriptor();
        Object values = fd.getValue(TAGS_KEY);
        if( values instanceof String[] )
        {
            return (String[])values;
        }
        return null;
    }
}
//PropertyDescriptor
