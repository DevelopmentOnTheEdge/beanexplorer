package com.developmentontheedge.beans.swing.table;

import com.developmentontheedge.beans.Option;

/** This class is used for creating of columns that represent sortable series of data */
public class ColumnWithSort extends Column
{
    /** This column unsorted. */
    public final static int SORTING_NONE = 0;

    /** This column sorted in ascending order. */
    public final static int SORTING_ASCENT = 1;

    /** This column sorted in descending order. */
    public final static int SORTING_DESCENT = 2;

    ////////////////////////////////////////
    // Constructors
    //

    /**
     * Creates column description with specified parameters.
     * @param parent parent for notification about changes.
     * @param columnKey name used to access the column data.
     * @param name display name, used for labelling of the columns in the table header.
     * @param enabled specifies whether this column will be visible.
     * @param sorting sort order for the data in this column. Should be one of
     * SORTING_NONE,SORTING_ASCENT,SORTING_DESCENT.
     */
    public ColumnWithSort( Option parent, String columnKey, String name,
    boolean enabled, int sorting )
    {
        super( parent, columnKey, name, enabled );
        this.sorting = sorting;
    }

    /**
     * Creates column description with specified parameters.
     * @param parent parent for notification about changes.
     * @param columnKey name used to access the column data.
     * @param enabled specifies whether this column will be visible.
     * @param sorting sort order for the data in this column. Should be one of
     * SORTING_NONE,SORTING_ASCENT,SORTING_DESCENT.
     */
    public ColumnWithSort( Option parent, String columnKey, boolean enabled, int sorting )
    {
        this( parent, columnKey, null, enabled, SORTING_NONE );
    }

    /**
     * Creates column description with specified parameters.
     * @param parent parent for notification about changes.
     * @param columnKey name used to access the column data.
     * @param name display name, used for labelling of the columns in the table header.
     * @param enabled specifies whether this column will be visible.
     * @param sorting sort order for the data in this column. Should be one of
     * SORTING_NONE,SORTING_ASCENT,SORTING_DESCENT.
     */
    public ColumnWithSort( Option parent, String columnKey, String name, boolean enabled )
    {
        this( parent, columnKey, name, enabled, SORTING_NONE );
    }

    /**
     * Creates column description with specified parameters.
     * @param parent parent for notification about changes.
     * @param columnKey name used to access the column data.
     * @param name display name, used for labelling of the columns in the table header.
     * @param sorting sort order for the data in this column. Should be one of
     * SORTING_NONE,SORTING_ASCENT,SORTING_DESCENT.
     */
    public ColumnWithSort( Option parent, String columnKey, String name, int sorting )
    {
        this( parent, columnKey, name, true, sorting );
    }

    /**
     * Creates column description with specified parameters.
     * @param parent parent for notification about changes.
     * @param columnKey name used to access the column data.
     * @param enabled specifies whether this column will be visible.
     */
    public ColumnWithSort( Option parent, String columnKey, boolean enabled )
    {
        this( parent, columnKey, null, enabled, SORTING_NONE );
    }

    /**
     * Creates column description with specified parameters.
     * @param parent parent for notification about changes.
     * @param columnKey name used to access the column data.
     * @param sorting sort order for the data in this column. Should be one of
     * SORTING_NONE,SORTING_ASCENT,SORTING_DESCENT.
     */
    public ColumnWithSort( Option parent, String columnKey, int sorting )
    {
        this( parent, columnKey, null, true, sorting );
    }

    /**
     * Creates column description with specified parameters.
     * @param parent parent for notification about changes.
     * @param columnKey name used to access the column data.
     * @param name display name, used for labelling of the columns in the table header.
     */
    public ColumnWithSort( Option parent, String columnKey, String name )
    {
        this( parent, columnKey, name, true, SORTING_NONE );
    }

    /**
     * Creates column description with specified parameters.
     * @param parent parent for notification about changes.
     * @param columnKey name used to access the column data.
     */
    public ColumnWithSort( Option parent, String columnKey )
    {
        this( parent, columnKey, null, true, SORTING_NONE );
    }

    /**
     * Create clone of this field options.
     * @return Clone of this object.
     */
    @Override
    public Object clone()
    {
        return new ColumnWithSort( getParent(), getColumnKey(), getEnabled(), getSorting() );
    }

    ////////////////////////////////////////
    // Properties
    //

    /** Sort order of this column. */
    private int sorting;

    /**
     * Sets sorting order of this column.
     * @param value Sorting order. Should be one of SORTING_NONE,SORTING_ASCENT,SORTING_DESCENT.
     */
    public void setSorting( int value )
    {
        int oldValue = sorting;
        sorting = value;
        firePropertyChange( "sorting", oldValue, value );
    }

    /**
     * Returns sorting order of this column.
     * @return SORTING_NONE,SORTING_ASCENT or SORTING_DESCENT.
     */
    public int getSorting()
    {
        return sorting;
    }
}
