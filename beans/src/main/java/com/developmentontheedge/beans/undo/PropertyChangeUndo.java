package com.developmentontheedge.beans.undo;

import java.beans.PropertyChangeEvent;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;

public class PropertyChangeUndo extends AbstractUndoableEdit
{
    PropertyChangeEvent pce;

    public PropertyChangeUndo(Object source, String propertyName, Object oldValue, Object newValue)
    {
        this(new PropertyChangeEvent(source, propertyName, oldValue, newValue));
    }

    /* @todo : add loging */
    public PropertyChangeUndo(PropertyChangeEvent pce)
    {
        this.pce = pce;
    }

    /* @todo : add loging */
    @Override
    public void undo() throws CannotUndoException
    {
        try
        {
            super.undo();
            getProperty().setValue(pce.getOldValue());
        }
        catch(Exception e)
        {
            //throw new CannotRedoException();
        }
    }

    /* @todo : add loging */
    @Override
    public void redo() throws CannotRedoException
    {
        try
        {
            super.redo();
            getProperty().setValue(pce.getNewValue());
        }
        catch(Exception e)
        {
            //throw new CannotRedoException();
        }
    }
    
    public Object getSource()
    {
        return pce.getSource();
    }
    
    public Object getOldValue()
    {
        return pce.getOldValue();
    }

    public Object getNewValue()
    {
        return pce.getNewValue();
    }
    
    public String getPropertyName()
    {
        return pce.getPropertyName();
    }
    
    public Property getProperty()
    {
        Object source = pce.getSource();
        ComponentModel model = ComponentFactory.getModel(source, true);
        String name = pce.getPropertyName();
        return model.findProperty(name);
    }

    @Override
    public String getPresentationName()
    {
        return "Change property '" + pce.getPropertyName()  + "' of '" + pce.getSource()+"'";
    }
    
    public void setUndone()
    {
        try
        {
            super.undo();
        }
        catch( CannotUndoException e )
        {
        }
    }

    @Override
    public boolean canRedo()
    {
        return true;
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }
}
