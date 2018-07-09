package com.developmentontheedge.beans.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ColorEditor extends CustomEditorSupport
{
    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        return ColorComboBox.getValueRenderer( (Color)getValue() );
    }

    @Override
    public Component getCustomEditor()
    {
        ColorComboBox comboBox = ColorComboBox.getInstance(( Color )getValue());
        comboBox.addColorPropertyChangeListener(
            new PropertyChangeListener()
            {
                @Override
                public void propertyChange( PropertyChangeEvent evt )
                {
                    Color c=  (Color)evt.getNewValue();
                    
                    setValue( new Color(c.getRed(),c.getGreen(),c.getBlue())   );
                    
                }
            } );

        return comboBox;
    }

    /**
     * Paint a representation of the value into a given area of screen
     * real estate.  Note that the propertyEditor is responsible for doing
     * its own clipping so that it fits into the given rectangle.
     * If the PropertyEditor doesn't honor paint requests (see isPaintable)
     * this method should be a silent noop.
     * @param gfx  Graphics object to paint into.
     * @param box  Rectangle within graphics object into which we should paint.
     */
    @Override
    public void paintValue( Graphics gfx, Rectangle box )
    {
    }

    /** This method should return Object that is result of editing in custom editor. */
    @Override
    protected Object processValue()
    {
        return null;
    }
    
}
