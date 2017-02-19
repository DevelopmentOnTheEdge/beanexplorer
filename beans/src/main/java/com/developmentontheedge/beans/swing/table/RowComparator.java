package com.developmentontheedge.beans.swing.table;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import com.developmentontheedge.beans.model.Property;

/**
 * Compare table rows.
 */
public class RowComparator implements Comparator
{
    private final SortedBeanTableModelAdapter model;
    private final Vector<Column> sortFieldOptions = new Vector<>();

    /**
     * Create row comparator for specified model.
     * @param model Table model.
     */
    public RowComparator( SortedBeanTableModelAdapter model )
    {
        this.model   = model;
    }

    /**
     *  Add column to comparator.
     *  @param column Column.
     */
    public void add( Column column )
    {
        if( sortFieldOptions.indexOf(column)==-1 )
            sortFieldOptions.add( column );
    }

    /**
     * Compare two rows.
     * @param o1 Number of first row (as {@link java.lang.Integer}).
     * @param o2 Number of second row (as {@link java.lang.Integer}).
     */
    @Override
    public int compare(Object o1, Object o2)
    {
        int result = 0;
        Integer i1 = (Integer)o1;
        Integer i2 = (Integer)o2;

        Iterator<Column> iter = sortFieldOptions.iterator();
        while( result==0 && iter.hasNext() )
        {
            ColumnWithSort column = (ColumnWithSort)iter.next();
            int colIndex = model.getVisibleColumnIndex(column);
            int asc = (column.getSorting()==ColumnWithSort.SORTING_DESCENT) ? -1 : 1;

            Property property1 = model.getPropertyAt(i1.intValue(),colIndex);
            Property property2 = model.getPropertyAt(i2.intValue(),colIndex);
            result = asc*compare( property1,property2 );
        }
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        return this==obj;
    }

    /**
     * Compare two properties.
     */
    protected int compare( Property o1, Property o2 )
    {
        if(o1 == null && o2 == null)
        {
            return  0;
        }
        else if(o1 == null)
        {
            return -1;
        }
        else if(o2 == null)
        {
            return  1;
        }
        Object value1 = o1.getValue();
        Object value2 = o2.getValue();
        if(value1 == null && value2 == null)
        {
            return  0;
        }
        else if(value1 == null)
        {
            return -1;
        }
        else if(value2 == null)
        {
            return  1;
        }
        Class<?> type = value1.getClass();

        if (type.getSuperclass() == Number.class)
        {
            return compare((Number)value1, (Number)value2);
        }
        else if (type == String.class)
        {
            return ((String)value1).compareTo((String)value2);
        }
        else if (type == Date.class)
        {
            return compare((Date)value1, (Date)value2);
        }
        else if (type == Boolean.class)
        {
            return compare((Boolean)value1, (Boolean)value2);
        }
        else
        {
            return (value1.toString()).compareTo(value2.toString());
        }
    }

    /**
     * Compare two numbers.
     */
    final private int compare( Number o1, Number o2 )
    {
        return Double.compare(o1.doubleValue(), o2.doubleValue());
    }

    /**
     * Compare two dates.
     */
    final private int compare( Date o1, Date o2 )
    {
        long n1 = o1.getTime();
        long n2 = o2.getTime();
        if( n1 < n2 )
        {
            return -1;
        }
        else if( n1 > n2 )
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Compare two boolean.
     */
    final private int compare( Boolean o1, Boolean o2 )
    {
        boolean b1 = o1.booleanValue();
        boolean b2 = o2.booleanValue();
        if( b1 == b2 )
        {
            return 0;
        }
        else if( b1 )
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}