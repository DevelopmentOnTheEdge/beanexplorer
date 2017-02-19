package com.developmentontheedge.beans.editors;

import java.awt.Component;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.developmentontheedge.beans.BeanInfoConstants;

public class StringEditor extends CustomEditorSupport
{
    private JTextField field = new JTextField();

    @Override
    public Component getCustomEditor()
    {
        if (!(field instanceof JPasswordField)
                && Boolean.parseBoolean(String.valueOf(getDescriptor()
                        .getValue(BeanInfoConstants.PASSWORD_FIELD))))
            field = new JPasswordField(field.getText());
        field.addActionListener(this);
        
        return field;
    }

    @Override
    public Object getValue()
    {
        return field.getText();
    }

    @Override
    public void setValue(Object value)
    {
        field.setText("" + value);
    }
}
