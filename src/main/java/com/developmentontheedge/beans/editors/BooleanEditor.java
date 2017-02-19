package com.developmentontheedge.beans.editors;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class BooleanEditor extends CustomEditorSupport
{
    private final JCheckBox checkBox;
    JPanel panel = new JPanel(new BorderLayout());

    public BooleanEditor()
    {
        checkBox = new JCheckBox("", false );
        checkBox.setOpaque(false);
        checkBox.addActionListener(this);
        
        panel.add(checkBox,BorderLayout.WEST);
    }

    @Override
    public Component getCustomEditor()
    {
        setColor(panel, true, null);
        return panel;
    }

    @Override
    public Component getCustomEditor(Component parent, boolean isSelected)
    {
        setColor(panel, isSelected, parent);
        return panel;
    }

    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        setColor( panel,isSelected, parent);
        return panel;
    }

    @Override
    public Object getValue()
    {
        return checkBox.isSelected();
    }

    @Override
    public void setValue(Object value)
    {
        checkBox.setSelected( ((Boolean)value).booleanValue() );
    }
}
