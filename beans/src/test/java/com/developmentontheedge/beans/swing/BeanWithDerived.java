/* $Id: BeanWithDerived.java,v 1.2 2005/01/19 15:44:52 fedor Exp $ */
package com.developmentontheedge.beans.swing;


import com.developmentontheedge.beans.Option;

public class BeanWithDerived extends Option
{
    public BeanWithDerived()
    {
        derivedArray = new BaseProperty[2];
        derivedArray[0] = new BaseProperty();
        derivedArray[1] = new BaseProperty();
    }

    private BaseProperty derived = new BaseProperty();
    public BaseProperty getDerived()
    {
        return derived;
    }
    public void setDerived( BaseProperty newDerived )
    {
        derived = newDerived;
        firePropertyChange("derived",null,null);
    }

    private BaseProperty[] derivedArray = null;
    public BaseProperty[] getDerivedArray()
    {
        return derivedArray;
    }
    public void setDerivedArray( BaseProperty[] newDerivedArray )
    {
        derivedArray = newDerivedArray;
        firePropertyChange("derivedArray",null,null);
    }
}