package com.developmentontheedge.beans.editors;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Utility class that consists from JTextField to edit property value and some button.
 */
public class TextButtonEditor extends CustomEditorSupport
{
    protected JPanel editor;
    
    public TextButtonEditor()
    {
        editor = new JPanel(new BorderLayout());
        button = createButton();
        editor.add(textField, BorderLayout.CENTER);
        editor.add(button, BorderLayout.EAST);

        textField.addActionListener(this);
        textField.addFocusListener(new FocusListener()
        {
            @Override
            public void  focusGained(FocusEvent e) {}
            @Override
            public void  focusLost(FocusEvent e) { firePropertyChange(); }
        });

        button.addActionListener ( new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                buttonPressed();
            }
        });

    }

    protected JTextField textField  = new JTextField();
    public JTextField getTextField()
    {
        return textField;
    }

    protected JButton button;
    public JButton getButton()
    {
        return button;
    }

    public void setEnabled( boolean enabled )
    {
        textField.setEnabled( enabled );
        button.setEnabled( enabled );
    }

    /** Subclasses can redefine this method to customize the button. */
    protected JButton createButton()
    {
        return new JButton("...");
    }

    /** Subclasses should redefine this method to handle the event. */
    protected void buttonPressed()
    {}

    ///////////////////////////////////////////////////////////////////
    // CustomEditor methods
    //

    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    { 
        return editor; 
    }

    @Override
    public Component getCustomEditor() 
    { 
        return editor; 
    }

    @Override
    public Object getValue() 
    { 
        return textField.getText(); 
    }

    @Override
    public void setValue(Object value)
    { 
        textField.setText(value.toString());
        super.setValue(value);
    }
}
