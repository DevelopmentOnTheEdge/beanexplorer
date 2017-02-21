package com.developmentontheedge.beans.lesson09.barchart;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;

/**
 * A simple bar chart demo
 */
public class BarChart extends JComponent implements PropertyChangeListener
{

    public BarChart(String title, Column[] columns)
    {
        setColumns(columns);
        setTitle(title);
        setPreferredSize(new Dimension(200, 150));
    }

    public BarChart(Column[] columns)
    {
        this("Bar chart", columns);
    }

    public BarChart()
    {
        this(new Column[]{new Column("Q1",5,Color.blue,true), 
                          new Column("Q2",10,Color.red,true), 
                          new Column("Q3",3,Color.cyan,true)});
    }

    public void propertyChange(PropertyChangeEvent e)
    {
        update();
    }

    ////////////////////////////////////////
    // Properties
    //

    private static final int VERTICAL = 0;
    private static final int HORIZONTAL = 1;

    private String title;
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
        update();
    }

    private int orientation;
    public int getOrientation()
    {
        return orientation;
    }
    public void setOrientation(int orientation)
    {
        this.orientation = orientation;
        update();
    }

    private Column[] columns;
    public Column [] getColumns()
    {
        return columns;
    }
    public void setColumns( Column []columns )
    {
        for(int i=0; i<columns.length; i++)
            columns[i].removePropertyChangeListener(this);

        this.columns = columns;
        for(int i=0; i<columns.length; i++)
            columns[i].addPropertyChangeListener(this);

        update();
    }

    public String calcColumnName( Integer index, Object obj )
    {
        return columns[ index.intValue() ].getLabel();
    }


    private Font font = new Font("Arial", Font.PLAIN, 11);
    public Font getDrawFont()
    {
        return font;
    }
    public void setDrawFont(Font font)
    {
        this.font = font;
        update();
    }

    private int scale = 10;
    public int getScale()
    {
        return scale;
    }
    public void setScale(int scale)
    {
        this.scale = scale;
        update();
    }

    private int  barSpacing = 10;
    public int getBarSpacing()
    {
        return barSpacing;
    }
    public void setBarSpacing(int barSpacing)
    {
        this.barSpacing = barSpacing;
        update();
    }

    ////////////////////////////////////////

    private int maxLabelWidth;
    private int maxValue;

    public void update()
    {
        maxLabelWidth = 0;
        maxValue = 0;

        FontMetrics metrics = getFontMetrics(font);
        for(int i=0; i<columns.length; i++)
        {
            maxValue      = Math.max(maxValue, columns[i].getValue());
            maxLabelWidth = Math.max(maxLabelWidth,
                                     metrics.stringWidth(columns[i].getLabel()));
        }

        repaint();
    }

    public void paint(Graphics g)
    {
        // draw the title centered at the bottom of the bar graph
        g.setColor(Color.black);
        g.setFont(font);

        g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);

        FontMetrics metrics = getFontMetrics(font);
        int titleWidth = metrics.stringWidth(title);
        int cx         = Math.max((getSize().width - titleWidth) / 2, 0);
        int cy         = getSize().height - metrics.getDescent();
        g.drawString(title, cx, cy);

        // draw the bars and their titles
        if (orientation == HORIZONTAL)
            paintHorizontal(g);
        else
            paintVertical(g);
    }

    private void paintHorizontal(Graphics g)
    {
        FontMetrics metrics = getFontMetrics(font);

        // x and y coordinates to draw/write to
        int cx, cy;
        int barHeight = metrics.getHeight();

        for (int i = 0; i < columns.length; i++)
        {
            if( !columns[ i ].isVisible() ) continue;

            // set the X coordinate for this bar and label and center it
            int widthOfItems = maxLabelWidth + 3 + (maxValue * scale) + 5
            + metrics.stringWidth(Integer.toString(maxValue));
            cx = Math.max((getSize().width - widthOfItems) / 2, 0);

            // set the Y coordinate for this bar and label
            cy = getSize().height - metrics.getDescent() - metrics.getHeight()
            - barSpacing - ((columns.length - i - 1) * (barSpacing + barHeight));

            // draw the label
            g.setColor(Color.black);
            cx += 10;
            g.drawString(columns[i].getLabel(), cx, cy);
            cx += maxLabelWidth + 3;

            // draw the shadow
            g.fillRect(cx + 4, cy - barHeight + 4,
            (columns[i].getValue() * scale), barHeight);

            // draw the bar
            g.setColor(columns[i].getColor());
            g.fillRect(cx, cy - barHeight, (columns[i].getValue() * scale) + 1, barHeight + 1);

            cx += (columns[i].getValue() * scale) + 4;

            // draw the value at the end of the bar
            g.setColor(g.getColor().darker());
            g.drawString(Integer.toString(columns[i].getValue()), cx + 5, cy);
        }
    }

    private void paintVertical(Graphics g)
    {
        int barWidth = maxLabelWidth;
        FontMetrics metrics = getFontMetrics(font);

        for (int i = 0; i < columns.length; i++)
        {
            if( !columns[ i ].isVisible() ) continue;

            // X coordinate for this label and bar (centered)
            int widthOfItems = (barWidth + barSpacing) * columns.length - barSpacing;
            int cx = Math.max((getSize().width - widthOfItems) / 2, 0);
            cx += (maxLabelWidth + barSpacing) * i;

            // Y coordinate for this label and bar
            int cy = getSize().height - metrics.getHeight()
            - metrics.getDescent() - 4;

            // draw the label
            g.setColor(Color.black);
            g.drawString(columns[i].getLabel(), cx, cy);
            cy -= metrics.getHeight() - 3;

            // draw the shadow
            g.fillRect(cx + 4, cy - (columns[i].getValue() * scale) - 4,
            barWidth, (columns[i].getValue() * scale));

            // draw the bar
            g.setColor(columns[i].getColor());
                g.fillRect(cx, cy - (columns[i].getValue() * scale),
                barWidth + 1, (columns[i].getValue() * scale) + 1);

            cy -= (columns[i].getValue() * scale) + 5;

            // draw the value on top of the bar
            g.setColor(g.getColor().darker());
            g.drawString(Integer.toString(columns[i].getValue()), cx, cy);
        }
    }
}

