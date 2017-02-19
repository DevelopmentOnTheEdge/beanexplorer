package com.developmentontheedge.beans.undo;

import java.util.EventObject;

public class TransactionEvent extends EventObject
{
    public TransactionEvent(Object source, String name)
    {
        super(source);
        this.name = name; 
    }

    /** The transaction presentation name. */
    private final String name;
    public String getName()
    {
        return name;
    }
}
