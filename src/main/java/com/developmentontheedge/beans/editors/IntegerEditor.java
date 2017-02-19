package com.developmentontheedge.beans.editors;

import java.awt.Component;

import javax.swing.JTextField;

public class IntegerEditor extends CustomEditorSupport
{
    protected JTextField field = new JTextField();

    public IntegerEditor()
    {
        field.addActionListener(this);
    }

    @Override
    public Component getCustomEditor()
    {
        return field;
    }

    @Override
    public void setValue(Object value)
    {
        field.setText("" + value);
        super.setValue(value);
    }

    @Override
    public Object getValue()
    {
        Integer value = (Integer)super.getValue();

        if(field == null || field.getText().length() == 0)
            value = 0;
        else
        {
            try
            {
                return Integer.valueOf(field.getText());
            }
            catch(Exception e)
            {
            }
        }

        return value;
    }

    protected Object superValue()
    {
        return super.getValue();
    }
}
