package com.developmentontheedge.beans.swing.table;

import java.util.EventObject;

/**
 * RowModelEvent is used to notify listeners that row model (i.e. list of displayed beans)
 * has changed. The model event describes changes to a <code>RowModel</code>
 * and references row numbers (i.e beans) of the model.
 * Depending on the parameters used in the constructors, the <code>RowModelEvent</code>
 * can be used to specify the following types of changes: <p>
 * <pre>
 * RowModelEvent(source);               // The data, i. e. all rows (beans) are changed
 * RowModelEvent(source, 1);            // Row 1 changed
 * RowModelEvent(source, 3, 6);         // Rows from 3 to 6 inclusive are changed
 * RowModelEvent(source, 3, 6, INSERT); // Rows (3, 6) were inserted
 * RowModelEvent(source, 3, 6, DELETE); // Rows (3, 6) were deleted
 * </pre>
 * @see RowModel
 */
public class RowModelEvent extends EventObject
{
    /** Identifies the addtion of new rows (beans). */
    public static final int INSERT = 1;

    /** Identifies a change to existing data. */
    public static final int UPDATE = 0;

    /** Identifies the removal of rows (beans). */
    public static final int DELETE = -1;

    private final int type;
    private final int firstRow;
    private final int lastRow;

    ////////////////////////////////////////
    // Constructors
    //

    /**
     * All row data in the table has changed, listeners should discard any state
     * that was based on the rows and requery the <code>RowModel</code> to get the new
     * row count and all the appropriate values.
     * The <code>TabularPropertyInspector</code> will repaint the entire visible region
     * on receiving this event, querying the model for the property values that are visible.
     */
    public RowModelEvent( RowModel source )
    {
        // Use Integer.MAX_VALUE instead of getRowCount() in case rows were deleted.
        this( source, 0, Integer.MAX_VALUE, UPDATE );
    }

    /** This row of data has been updated. */
    public RowModelEvent( RowModel source, int row )
    {
        this( source, row, row, UPDATE );
    }

    /** The data in range of [<I>firstRow</I>, <I>lastRow</I>] were updated. */
    public RowModelEvent( RowModel source, int firstRow, int lastRow )
    {
        this( source, firstRow, lastRow, UPDATE );
    }

    /**
     *  The data in range of [<I>firstRow</I>, <I>lastRow</I>] were inserted or updated or deleted.
     *  The <I>type</I> should be one of: INSERT, UPDATE and DELETE.
     */
    public RowModelEvent( RowModel source, int firstRow, int lastRow, int type )
    {
        super( source );
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.type = type;
    }

    ////////////////////////////////////////
    // Querying Methods
    //

    /** Returns the first row that was changed. */
    public int getFirstRow()
    {
        return firstRow;
    };

    /** Returns the last row that was changed. */
    public int getLastRow()
    {
        return lastRow;
    };

    /** Returns the type of event - one of: INSERT, UPDATE and DELETE. */
    public int getType()
    {
        return type;
    }
}
