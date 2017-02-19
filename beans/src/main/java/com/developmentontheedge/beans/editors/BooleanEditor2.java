package com.developmentontheedge.beans.editors;

import java.beans.PropertyEditorSupport;

/**
 * Implements TagEditor for boolean type.
 */
public class BooleanEditor2 extends PropertyEditorSupport
{
    protected static String[] values = {"true", "false"};

    @Override
    public String getAsText()
    {
        return ( ( Boolean )getValue() ).booleanValue() ? values[ 0 ] : values[ 1 ];
    }

    @Override
    public void setAsText( String text )
    {
        setValue(values[0].equalsIgnoreCase(text));
    }

    @Override
    public String[] getTags()
    {
        return values;
    }
}
