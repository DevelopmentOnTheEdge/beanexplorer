package com.developmentontheedge.beans.editors;

public class LongEditor extends IntegerEditor
{
    @Override
    public Object getValue()
    {
        Long value = (Long)superValue();

        if(field == null || field.getText().length() == 0)
            value = 0l;
        else
        {
            try
            {
                return Long.valueOf(field.getText());
            }
            catch(NumberFormatException e)
            {
            }
        }

        return value;
    }
}
