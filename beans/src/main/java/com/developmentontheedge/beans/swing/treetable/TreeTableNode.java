package com.developmentontheedge.beans.swing.treetable;

public interface TreeTableNode
{
    Object getValueAt(int column);

    void setValueAt(Object value, int column);
}
