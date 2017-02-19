package com.developmentontheedge.beans.undo;

import java.util.Collections;
import java.util.List;

import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

public class Transaction extends CompoundEdit
{
    public Transaction(TransactionEvent te)
    {
        this.te = te;
    }

    private final TransactionEvent te;
    public TransactionEvent getTransactionEvent()
    {
        return te;
    }

    @Override
    public String getPresentationName()
    {
        return te.getName();
    }
    
    private String comment;

    /**
     * @return the comment
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public boolean isEmpty()
    {
        return lastEdit() == null;
    }
    
    public List<UndoableEdit> getEdits()
    {
        return Collections.unmodifiableList(edits);
    }

    @Override
    public boolean canRedo()
    {
        return !isInProgress();
    }

    @Override
    public boolean canUndo()
    {
        return !isInProgress();
    }

    @Override
    public String toString()
    {
        return te.getName();
    }
}
