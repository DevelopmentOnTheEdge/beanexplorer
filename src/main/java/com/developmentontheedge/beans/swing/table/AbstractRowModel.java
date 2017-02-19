package com.developmentontheedge.beans.swing.table;

import java.util.EventListener;
import javax.swing.event.EventListenerList;

/**
 *  This abstract class takes care of management of the listeners and provides some
 *  convenience methods for generating <code>RowModelEvent</code> and dispatching them
 *  to the listeners.
 *  <p>To create a concrete <code>RowModel</code> as a sublcass of
 *  <code>AbstractRowModel</code> you need provide implementations
 *  for the following two methods:
 *  <pre>
 *  public int size();
 *  public int getBean(int row);
 *  </pre>
 */
abstract public class AbstractRowModel implements RowModel
{
    ////////////////////////////////////////
    // Listener registration issues
    //

    /** List of listeners */
    private EventListenerList listenerList = new EventListenerList();

    /** Returns class of first bean on row model or if it is empty returns Object.class. */
    public Class getBeanClass()
    {
        if ( size() != 0 )
        {
            return getBean( 0 ).getClass();
        }
        return Object.class;
    }

    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     * @param l the RowModelListener
     */
    public void addRowModelListener( RowModelListener l )
    {
        listenerList.add( RowModelListener.class, l );
    }

    /**
     * Removes a listener from the list that's notified each time a
     * change to the data model occurs.
     * @param l the RowModelListener
     */
    public void removeRowModelListener( RowModelListener l )
    {
        listenerList.remove( RowModelListener.class, l );
    }

    /**
     * Returns an array of all the listeners of the given type that
     * were added to this model.
     * @returns all of the objects receiving <code>listenerType</code>
     * notifications from this model
     */
    public EventListener[] getListeners( Class listenerType )
    {
        return listenerList.getListeners( listenerType );
    }

    ////////////////////////////////////////
    //  Fire methods
    //

    /**
     * Notifies all listeners that all property values may have been changed.
     * The number of beans may also have been changed and the <code>TabularPropertyInspector</code>
     * should redraw the table from scratch.
     * @see RowModelEvent
     * @see EventListenerList
     */
    public void fireTableDataChanged()
    {
        fireTableChanged(
            new RowModelEvent( this ) );
    }

    /**
     * Notifies all listeners that rows (beans) in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
     * @param firstRow  the first row
     * @param lastRow   the last row
     * @see RowModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsInserted( int firstRow, int lastRow )
    {
        fireTableChanged(
            new RowModelEvent( this, firstRow, lastRow, RowModelEvent.INSERT ) );
    }

    /**
     * Notifies all the listeners that rows (beans) in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been updated.
     * @param firstRow  the first row
     * @param lastRow   the last row
     * @see RowModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsUpdated( int firstRow, int lastRow )
    {
        fireTableChanged(
            new RowModelEvent( this, firstRow, lastRow, RowModelEvent.UPDATE ) );
    }

    /**
     * Notifies all the listeners that rows (beans) in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
     * @param firstRow  the first row
     * @param lastRow   the last row
     * @see RowModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsDeleted( int firstRow, int lastRow )
    {
        fireTableChanged(
            new RowModelEvent( this, firstRow, lastRow, RowModelEvent.DELETE ) );
    }

    /**
     * Forwards the given notification event to all * <code>RowModelListeners</code>
     * that registered themselves as listeners for this table model.
     * @param e  the event to be forwarded
     * @see #addRowModelListener
     * @see RowModelEvent
     * @see EventListenerList
     */
    public void fireTableChanged( RowModelEvent e )
    {
        Object[] listeners = listenerList.getListenerList();
        for ( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if ( listeners[ i ] == RowModelListener.class )
            {
                ( ( RowModelListener )listeners[ i + 1 ] ).tableChanged( e );
            }
        }
    }
}

