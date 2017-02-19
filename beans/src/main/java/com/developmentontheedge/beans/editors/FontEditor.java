package com.developmentontheedge.beans.editors;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class FontEditor extends CustomEditorSupport
{
    JLabel label;

    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        if (label==null)
            label = createLabel();

        setColor(label, isSelected, parent);
        return label;
    }

    @Override
    public Component getCustomEditor(Component parent, boolean isSelected)
    {
        label = createLabel();

        label.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mousePressed(MouseEvent e)
                {
                    if(!label.isEnabled()) // property read-only
                    {
                        return;
                    }

                    Font font = FontChooser.showDialog(null, null, (Font)(getValue() != null ?
                                                                          getValue() : getDefaultFont()));
                    setValue(font);

                    JLabel source = (JLabel)e.getSource();
                    source.setFont(font);
                    source.setText(font.getFamily()+", "+font.getSize());
                }
            });
        return label;
    }

    private static Font getDefaultFont()
    {
        return new Font(null, Font.PLAIN, 12);
    }

    private JLabel createLabel()
    {
        Font font = (Font)getValue();
        if(font == null)
            font = getDefaultFont();

        String info = font.getFamily()+", "+font.getSize();
        JLabel label = new JLabel(info, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setFont(font);

        return label;
    }

    /** This method should return Object that is result of editing in custom editor. */
    @Override
    protected Object processValue()
    {
        Font value = (Font)getValue();

        if (label == null)
            value = new Font("Courier", Font.BOLD, 14);

        return value;
    }
}
