package com.developmentontheedge.beans.undo;

/**
 * General interface for entities grouping undoable actions into transactions.
 */
public interface Transactable
{
    public void addTransactionListener(TransactionListener tl);
    public void removeTransactionListener(TransactionListener tl);

}
