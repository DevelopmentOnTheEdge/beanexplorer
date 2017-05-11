/* $Id: DerivedProperty.java,v 1.1 2001/10/01 10:18:56 vladz Exp $ */
package com.developmentontheedge.beans.swing;

public class DerivedProperty extends BaseProperty
{
    String second = "second";
    public String getSecond()
    {
        return second;
    }
    public void setSecond( String newSecond )
    {
        second = newSecond;
    }
}