package com.developmentontheedge.beans.editors;

public class FloatEditor extends IntegerEditor
{
    @Override
    public Object getValue()
    {
        Float value = (Float)superValue();

        if(field == null || field.getText().length() == 0)
            value = new Float(0);
        else
        {
            try
            {
                return new Float(field.getText());
            }
            catch(Exception e)
            {
            }
        }

        return value;
    }
}
