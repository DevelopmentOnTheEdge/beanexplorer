package com.developmentontheedge.beans.swing.table;

import java.util.ListResourceBundle;

public class ColumnMessageBundle extends ListResourceBundle
{
    public Object[][] getContents() { return contents; }
    private final static Object[][] contents =
    {
        { "CN_CLASS", "Column"},
        { "CD_CLASS", "The column name displayed in the table header"},
    };
}
