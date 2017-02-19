package com.developmentontheedge.beans.swing.table;

import java.util.EventListener;

/**
 * RowModelListener defines the interface for an object that listens
 * to changes in a RowModel.
 * @see RowModel
 */
public interface RowModelListener extends EventListener
{
    /**
     * This fine grain notification tells listeners the exact range of rows that changed.
     * @param e information about {@link RowModel} changes.
     */
    void tableChanged( RowModelEvent e );
}


