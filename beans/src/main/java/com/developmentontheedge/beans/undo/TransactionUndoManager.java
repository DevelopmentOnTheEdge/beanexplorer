package com.developmentontheedge.beans.undo;

import java.util.Collections;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class TransactionUndoManager extends UndoManager implements TransactionListener
{
    protected Transaction currentTransaction;
    protected boolean undoInProgress = false;

    protected Transaction createTransaction(TransactionEvent te)
    {
        return new Transaction(te);
    }

    /**
     * Called when TransactionManager wants to create new TransactionEvent
     * Override this to use custom TransactionEvent's
     * @param name a proposed name for the new transaction event
     * @return
     */
    protected TransactionEvent createTransactionEvent(String name)
    {
        return new TransactionEvent(this, name);
    }

    public boolean hasTransaction()
    {
        return currentTransaction != null;
    }    

    ////////////////////////////////////////////////////////////////////////////
    // TransactionListener interface implementation
    //

    @Override
    public synchronized void startTransaction(TransactionEvent te)
    {
        // complete previous transaction if it is not completed
        if(currentTransaction != null && !currentTransaction.isEmpty())
            completeTransaction();

        currentTransaction = createTransaction(te);
    }

    @Override
    public synchronized boolean addEdit(UndoableEdit anEdit)
    {
        if(undoInProgress) return false;
        if(currentTransaction != null)
            return currentTransaction.addEdit(anEdit);
        if(anEdit instanceof Transaction)
        {
            return super.addEdit(anEdit);
        }
        startTransaction(createTransactionEvent(anEdit.getPresentationName()));
        currentTransaction.addEdit(anEdit);
        completeTransaction();
        return true;
    }

    /**
     * TODO: full support of nested transactions
     * Complete transaction 
     */
    @Override
    public synchronized void completeTransaction()
    {
        if(currentTransaction !=  null)
        {
            currentTransaction.end();
            if(currentTransaction.getEdits().size() > 0)
                super.addEdit(currentTransaction);
            currentTransaction = null;
        }
    }
    
    public List<UndoableEdit> getEdits()
    {
        return Collections.unmodifiableList(edits);
    }
    
    /**
     * @param comment 
     * 
     */
    public void setTransactionComment(String comment)
    {
        if(currentTransaction != null)
            currentTransaction.setComment(comment);
    }

    @Override
    public synchronized void redo() throws CannotRedoException
    {
        completeTransaction();
        undoInProgress = true;
        try
        {
            super.redo();
        }
        finally
        {
            undoInProgress = false;
        }
        
    }

    @Override
    public synchronized void undo() throws CannotUndoException
    {
        completeTransaction();
        undoInProgress = true;
        try
        {
            super.undo();
        }
        finally
        {
            undoInProgress = false;
        }
    }
}
