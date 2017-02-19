package com.developmentontheedge.beans.swing.table;

import java.util.Arrays;

/**
 * Sort table model by column or by field options.
 * @see mgl3.access.table.DataCollectionTableModelAdapter
 * @see mgl3.access.table.FieldOptions
 */
public class TableSorter
{
    /** Table model for sorting by this sorter. */
    private final SortedBeanTableModelAdapter model;

    /**
     * Create table sorter.
     * @param model Model of table for sorting by this sorter.
     */
    public TableSorter( SortedBeanTableModelAdapter model )
    {
        this.model = model;
    }

    /**
     * Sort table model according its {@link ColumnWithSort field options}.
     */
    public void sort()
    {
        final RowComparator comparator = new RowComparator( model );
        Column[] columns = model.getColumnModel().getColumns();
        for( int i=0; i<columns.length; i++ )
        {
            ColumnWithSort fieldOption = (ColumnWithSort)columns[i];
            if( fieldOption.getEnabled() &&
                fieldOption.getSorting()!=ColumnWithSort.SORTING_NONE )
            {
                comparator.add( fieldOption );
            }
        }
        Arrays.sort( model.getIndices(), comparator );
    }
}