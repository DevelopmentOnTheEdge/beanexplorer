package com.developmentontheedge.beans.lesson05.barchart;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Column
{
    public Column(String label, int value, Color color, boolean visible)
    {
        this.label = label;
        this.value = value;
        this.color = color;
        this.visible = visible;

        pcSupport = new PropertyChangeSupport(this);
    }

    public Column()
    {
        this("label", 20, Color.red, true);
    }

    ////////////////////////////////////////
    // PropertyChange issues
    //

    PropertyChangeSupport pcSupport;
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        pcSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        pcSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String property)
    {
        pcSupport.firePropertyChange(property, null, null);
    }

    ////////////////////////////////////
    // Properties
    //

    private String label;
    public String getLabel()
    {
        return label;
    }
    public void setLabel(String label)
    {
        this.label = label;
        firePropertyChange("label");
    }

    private int value;
    public int getValue()
    {
        return value;
    }
    public void setValue(int value)
    {
        this.value = value;
        firePropertyChange("value");
    }

    private Color color;
    public Color getColor()
    {
        return color;
    }
    public void setColor(Color color)
    {
        this.color = color;
        firePropertyChange("color");
    }

    private boolean visible;
    public boolean isVisible()
    {
        return visible;
    }
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        firePropertyChange("visible");
    }


}
