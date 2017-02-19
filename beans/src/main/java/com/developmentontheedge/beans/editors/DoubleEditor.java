package com.developmentontheedge.beans.editors;

public class DoubleEditor extends IntegerEditor
{
    @Override
    public Object getValue()
    {
        Double value = (Double)superValue();

        if(value == null || field == null || field.getText().length() == 0)
            value = new Double(0);
        else
        {
            try
            {
                return new Double(field.getText());
            }
            catch(Exception e)
            {
            }
        }

        return value;
    }
}
