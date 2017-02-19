package com.developmentontheedge.beans.swing.table;

import com.developmentontheedge.beans.Option;

/**
 *  This class is used for creating of properties for controlling of the table header
 *  Each instance of this class represents one column of the table.
 *  @see ColumnModel
 */
public class Column extends Option
{
    ////////////////////////////////////////
    // Constructors
    //

    /**
     * Creates column with specified parent, data source, display name and visibility.
     * @param parent Parent for notification about changes.
     * @param columnKey Name used for access to data.
     * @param name Display name, used to label the coluymn.
     * @param enabled Whether this column will be visible.
     */
    public Column( Option parent, String columnKey, String name, boolean enabled )
    {
        super( parent );
        this.columnKey = columnKey;
        this.name = name;
        this.enabled = enabled;
    }

    /**
     * Creates column with specified parent, data source and visibility.
     * Display name is not used.
     * @param parent Parent for notification about changes.
     * @param columnKey Name used for access to data.
     * @param enabled Is this header and whole the column will be visible.
     */
    public Column( Option parent, String columnKey, boolean enabled )
    {
        this( parent, columnKey, null, enabled );
    }

    /**
     * Creates column with specified parent,data source and visibility.
     * Display name is not used.
     * Column by default is visible.
     * @param parent parent for notification about changes.
     * @param columnKey name used to access the data.
     */
    public Column( Option parent, String columnKey )
    {
        this( parent, columnKey, null, true );
    }

    ////////////////////////////////////////
    // Properties
    //

    /** Name of the column to display. Used to access data. */
    private final String columnKey;

    /**
     * Returns datasource name for this column.
     * @return Key to access the column data.
     */
    public String getColumnKey()
    {
        return columnKey;
    }

    /** Name to display in the header of the table of the column. */
    private String name;

    /**
     * Returns display name for this column.
     * @return Name of this column. It used to label a header in the table
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets new name of this column. Used as a display name of this column.
     * @param name new name of this column.
     */
    public void setName( String name )
    {
        String oldValue = this.name;
        this.name = name;
        firePropertyChange( "name", oldValue, name );
    }

    /** Visibility of column and header. */
    private boolean enabled;

    /**
     * Is this column enabled (visible)?
     * @return <code>true</code> if this column is visible, <code>false</code> otherwise.
     * @todo Rename to 'isEnabled'
     */
    public boolean getEnabled()
    {
        return enabled;
    }

    /**
     * Enables/disables this column.
     * @param value if <code>true</code> then this column will be visible.
     * If <code>false</code> this column will be invisible.
     */
    public void setEnabled( boolean value )
    {
        boolean oldValue = enabled;
        enabled = value;
        firePropertyChange( "enabled", oldValue, value );
    }
}
