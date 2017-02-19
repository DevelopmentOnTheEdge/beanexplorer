package com.developmentontheedge.beans.swing.table;

import com.developmentontheedge.beans.swing.TabularPropertyInspector;

/**
 * The <code>RowModel</code> interface specifies the methods
 * the <code>TabularPropertyInspector</code> will use to interrogate a tabular data model.
 * @see TabularPropertyInspector
 */
public interface RowModel
{
    /**
     * Add a listener to the list that's notified each time a change to the data model occurs.
     * @param l the RowModelListener
     */
    void addRowModelListener( RowModelListener l );

    /**
     * Remove a listener from the list that's notified each time a change to the data model occurs.
     * @param l the RowModelListener
     */
    void removeRowModelListener( RowModelListener l );

    /**
     * Returns a number of beans in the model.
     * A <code>TabularPropertyInspector</code> uses this method to determine how many rows
     * it should display. This method should be quick, as it is called frequently during rendering.
     */
    int size();

    /** Returns a bean at the index. */
    Object getBean( int index );

    /**
     * Return class of beans in this row model.
     * This method is needed for empty row model and for row model that contains beans
     * of different classes.
     * @return Class of beans in this row model.
     */
    Class getBeanClass();
}


