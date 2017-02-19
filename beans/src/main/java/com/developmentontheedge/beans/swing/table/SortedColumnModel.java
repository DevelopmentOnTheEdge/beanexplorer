package com.developmentontheedge.beans.swing.table;

public abstract class SortedColumnModel extends ColumnModel
{
    private static final long serialVersionUID = 1L;

    public abstract void sort();

    public SortedColumnModel(Class<?> templateBeanClass, int showMode)
    {
        super(templateBeanClass, showMode);
    }

    public SortedColumnModel(Column[] fields)
    {
        super(fields);
    }

    public SortedColumnModel(Object templateBean, int showMode)
    {
        super(templateBean, showMode);
    }

    public SortedColumnModel(Object templateBean)
    {
        super(templateBean);
    }
}
