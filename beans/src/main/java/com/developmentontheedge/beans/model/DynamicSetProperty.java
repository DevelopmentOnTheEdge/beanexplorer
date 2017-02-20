/** $Id: DynamicSetProperty.java,v 1.1 2001/04/09 12:17:56 fedor Exp $ */
package com.developmentontheedge.beans.model;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;

public class DynamicSetProperty extends CompositeProperty 
{
    protected DynamicSetProperty(Property parent, Object owner,
                                 PropertyDescriptor descriptor, BeanInfo beanInfo, ComponentFactory.Policy policy)
    {
        super(parent, owner, descriptor, beanInfo, policy);
    }

    /**
     * Returns true if property is read only. If parent property is read only then all
     * child properties are read only too.
     *
     * @todo process read only flags from PropertyDescriptor.
     */
    public boolean isReadOnly()
    {
        if(parent != null && parent.isReadOnly())
            return true;

        return false;
    }
}


