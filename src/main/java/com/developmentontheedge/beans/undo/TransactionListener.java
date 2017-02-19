package com.developmentontheedge.beans.undo;

import java.util.EventListener;

import javax.swing.undo.UndoableEdit;

public interface TransactionListener extends EventListener
{
    public void     startTransaction(TransactionEvent te);
    public boolean  addEdit(UndoableEdit ue);
    public void     completeTransaction();
}
