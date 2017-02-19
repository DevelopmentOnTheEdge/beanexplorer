package com.developmentontheedge.beans.swing.table;

import java.util.ListResourceBundle;

public class ColumnModelMessageBundle extends ListResourceBundle
{
    @Override
    public Object[][] getContents() { return contents; }
    private final static Object[][] contents =
    {
        { "CN_CLASS", "Table options"},
        { "CD_CLASS", "Table options"},
        { "PN_COLUMNS", "Columns"},
        { "PD_COLUMNS", "Columns of the table"},
    };
}
