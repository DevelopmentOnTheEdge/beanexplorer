package com.developmentontheedge.beans.model;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class PropertyChangeUndo extends AbstractUndoableEdit
{
    private static final long serialVersionUID = 1L;
    private final Property property;
    private final Object oldValue;
    private final Object newValue;

    public PropertyChangeUndo(Property property, Object oldValue, Object newValue)
    {
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public void undo() throws CannotUndoException
    {
        try
        {
            property.setValue(oldValue);
        }
        catch(Exception e)
        {
            throw new CannotUndoException();
        }
    }

    @Override
    public void redo() throws CannotRedoException
    {
        try
        {
            property.setValue(newValue);
        }
        catch(Exception e)
        {
            throw new CannotRedoException();
        }
    }

    @Override
    public String getPresentationName()
    {
        return "Property change: " + property.getCompleteName();
    }
}
