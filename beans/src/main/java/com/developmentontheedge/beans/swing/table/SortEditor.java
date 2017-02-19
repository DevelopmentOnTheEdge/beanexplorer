package com.developmentontheedge.beans.swing.table;

import com.developmentontheedge.beans.editors.TagEditorSupport;

public class SortEditor extends TagEditorSupport
{
    public SortEditor()
    {
        super( ColumnWithSortMessageBundle.class.getName(), "SORT_TYPES", 0);
    }
}
