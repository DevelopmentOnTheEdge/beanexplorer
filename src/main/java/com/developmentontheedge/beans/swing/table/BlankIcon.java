package com.developmentontheedge.beans.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class BlankIcon implements Icon
{
    static private final int ICON_SIZE = 11;

    private final Color fillColor;
    private final int size;

    public BlankIcon()
    {
        this(null, ICON_SIZE);
    }

    public BlankIcon(Color fillColor, int size)
    {
        this.fillColor = fillColor;
        this.size = size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
        if(fillColor != null)
        {
            g.setColor(fillColor);
            g.drawRect(x, y, size-1, size-1);
        }
    }

    @Override
    public int getIconWidth()
    {
        return size;
    }

    @Override
    public int getIconHeight()
    {
        return size;
    }
}
