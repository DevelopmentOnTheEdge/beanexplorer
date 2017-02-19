package com.developmentontheedge.beans.editors;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JEditorPane;

public class HtmlEditor extends CustomEditorSupport
{
    protected JEditorPane pane;

    public HtmlEditor()
    {
        // redfined for performance issues like javax.swing.table.DefaultTableCellRenderer
        pane = new JEditorPane()
        {
            @Override
            public void invalidate() {}
            @Override
            public void validate() {}
            @Override
            public void revalidate() {}
            @Override
            public void repaint(long tm, int x, int y, int width, int height) {}
            @Override
            public void repaint(Rectangle r) { }
            @Override
            public void repaint() {}

            @Override
            protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) 
            {  
              if (propertyName.equals("document") )
                    super.firePropertyChange(propertyName, oldValue, newValue);
            }

            @Override
            public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
        }; 

        pane.setContentType("text/html"); 
    }

    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        pane.setEditable(false);
        setColor(pane, isSelected, parent);

        return pane;
    }

    @Override
    public Component getCustomEditor(Component parent, boolean isSelected)
    {
        pane.setEditable(true);
        return pane;
    }

    @Override
    public Object getValue()
    {
        return pane.getText();
    }

    @Override
    public void setValue(Object value)
    {
        pane.setText("" + value);
    }
}