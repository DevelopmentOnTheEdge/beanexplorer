package com.developmentontheedge.beans.json.presentations;

import com.developmentontheedge.beans.DynamicPropertySet;

import java.util.List;

public class Table
{
    public final String title;
    public final boolean selectable;
    public final Long totalNumberOfRows;
    public final List<DynamicPropertySet> rows;
    //public final List<TableOperationPresentation> operations;


    public Table(String title, boolean selectable, Long totalNumberOfRows, List<DynamicPropertySet> rows)
    {
        this.title = title;
        this.selectable = selectable;
        this.totalNumberOfRows = totalNumberOfRows;
        this.rows = rows;
    }
}
