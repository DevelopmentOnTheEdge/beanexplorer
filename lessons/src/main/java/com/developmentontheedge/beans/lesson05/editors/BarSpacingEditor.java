package com.developmentontheedge.beans.lesson05.editors;

import java.beans.PropertyEditorSupport;

public class BarSpacingEditor extends PropertyEditorSupport
{
    public String getAsText()
    {
        return getValue().toString();
    }

    public void setAsText( String text ) throws IllegalArgumentException
    {
        try
        {
            int value = Integer.parseInt(text);
            if (value > 10)
            {
                value = 10;
            }
            else if (value < 1)
            {
                value = 1;
            }
            setValue(new Integer(value));
        }
        catch(Exception ex)
        {
            throw new IllegalArgumentException();
        }
    }
}