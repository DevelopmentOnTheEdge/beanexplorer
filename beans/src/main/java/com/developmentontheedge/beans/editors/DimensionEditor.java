package com.developmentontheedge.beans.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DimensionEditor extends CustomEditorSupport
{
    private final JPanel panel      = new JPanel( new GridLayout(1, 2) );
    private final JTextField width  = new JTextField();
    private final JTextField height = new JTextField();

    public DimensionEditor()
    {
        width.setOpaque( false );
        panel.add( width );

        height.setOpaque( false );
        panel.add( height );

        width.setNextFocusableComponent( height );
        setColor(width,  true, null);
        setColor(height, true, null);
        setColor(panel,  true, null);
    }

    @Override
    public Component getCustomEditor()
    {
        return panel;
    }

    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        Dimension dim = (Dimension)getValue();
        if (dim == null)
            dim = new Dimension( 0, 0 );

        JPanel panel = new JPanel(new GridLayout(1, 2));
        JLabel width = new JLabel("" + dim.getWidth());
        width.setOpaque( false );
        panel.add(width);

        JLabel height = new JLabel("" + dim.getHeight());
        height.setOpaque( false );
        panel.add( height );

        setColor(panel, isSelected, parent);
        return panel;
    }

    @Override
    protected Object processValue()
    {
        if( width == null || height == null)
            return null;

        return new Dimension( Integer.parseInt(width.getText()), Integer.parseInt(height.getText()) );
    }
}
