package com.developmentontheedge.beans.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.UIManager;

/**
 * This is a support class to help build custom property editor.
 *
 * <p>Child classes should add this class as FocusListener
 * and as ActionListener (if possible) to JComponent used as editor.
 */
public class CustomEditorSupport extends PropertyEditorSupport
                                 implements PropertyEditorEx,  ActionListener
{
    /**
     * Determines whether the class will honor the paintValue method.
     *
     * @return  True if the class will honor the paintValue method.
     */
    @Override
    public boolean isPaintable()
    {
        return true;
    }

    @Override
    public Component getCustomRenderer(Component parent, boolean isSelected, boolean hasFocus)
    {
        return null;
    }

    /**
     * Determines whether this property editor supports a custom editor.
     *
     * @return true
     */
    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    @Override
    public Component getCustomEditor(Component parent, boolean isSelected)
    {
        return getCustomEditor();
    }

    /**
     * Invoked when an action occurs.
     * @see ActionListener
     */
    @Override
    public void actionPerformed( ActionEvent evt )
    {
        firePropertyChange();
    }

    /**
     * Override this method for processing edited value.
     * This method is called when action is performed.
     *
     * @return result object of edit operation
     */
    protected Object processValue()
    {
        firePropertyChange();
        return getValue();
    }


    /** Current bean. */
    protected Object bean;

    /**
     * Gets current bean
     *
     * @return the current bean
     */
    @Override
    public Object getBean()
    {
        return bean;
    }

    /**
     * Sets current bean
     *
     * @param bean   the current bean
     */
    @Override
    public void setBean(Object bean)
    {
        this.bean = bean;
    }

    protected FeatureDescriptor descriptor;
  
    @Override
    public FeatureDescriptor getDescriptor()
    {
        return descriptor;
    }

    @Override
    public void setDescriptor( FeatureDescriptor descriptor )
    {
        this.descriptor = descriptor;
    }

    /**
     * This method helps to make color of main editor or renederer component.
     * Use this method in {@link #getCustomEditor} and {@link #getCustomRenderer} methods
     *
     * @param component  the Component representing editor or renderer.
     * @param isSelected is component selected.
     * @param parent  the Component representing PropertyInspector that is asking the renderer or editor to draw.
     */
    public static void setColor(JComponent component, boolean isSelected, Component parent)
    {
        Color background = null;
        Color foreground = null;
        component.setOpaque( true );

        if( parent instanceof JTable )
        {
            if (isSelected)
            {
                background = ((JTable)parent).getSelectionBackground();
                foreground = ((JTable)parent).getSelectionForeground();
            }
            else
            {
                background = ((JTable)parent).getBackground();
                foreground = ((JTable)parent).getForeground();
            }
        }
        else if( parent instanceof JComponent )
        {
            final String PREFIX = "javax.swing.J";
            Class<?> parentClass = parent.getClass();
            String className = parentClass.getName();
            while( !parentClass.equals( Object.class ) && !className.startsWith( PREFIX ) )
            {
                parentClass = parentClass.getSuperclass();
                className = parentClass.getName();
            }
            if( className.startsWith( PREFIX ) )
            {
                String colorKey = className.substring( PREFIX.length() );
                if( isSelected )
                {
                    background = UIManager.getColor( colorKey + ".selectionBackground" );
                    foreground = UIManager.getColor( colorKey + ".selectionForeground" );
                }
                if( background == null )
                {
                    background = UIManager.getColor( colorKey + ".background" );
                }
                if( foreground == null )
                {
                    foreground = UIManager.getColor( colorKey + ".foreground" );
                }
            }
        }

        if( background != null )
            component.setBackground( background );
        if( foreground != null )
            component.setForeground( foreground );
    }
}
