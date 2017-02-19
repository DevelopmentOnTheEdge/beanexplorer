/** $Id: ComponentModel.java,v 1.25 2003/03/22 04:37:49 zha Exp $ */

package com.developmentontheedge.beans.model;

import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.util.Vector;

import com.developmentontheedge.beans.BeanInfoEx;

/**
 */
public class ComponentModel extends CompositeProperty
{
    /** Property used to display component name. */
    protected Property displayNameProperty;
    ////////////////////////////////////////
    // Constructor
    //

    /**
     * Creates new ComponentModel for the specified component.
     * <p> In general, this function should be called by ComponentFactory only.
     */
    protected ComponentModel(Object comp, BeanInfo beanInfo)
    {
        super(null, comp, 
              ComponentFactory.merge( beanInfo.getBeanDescriptor(), null, false ), 
              beanInfo);
        setExpanded(true);
    }

    @Override
	public Object getValue()
    {
        return owner;
    }

    /** @pending high we should rebuild the model if owner != value */
    @Override
	public void setValue(Object value)
    {
        owner = value;
    }

    /** Returns a string representation of this ComponentModel. Currently it is the type name. */
    @Override
	public String toString()
    {
        return getTypeName();
        //return super.toString();
    }

    ////////////////////////////////////////
    // event functions
    //
    @Override
	public void propertyChange(PropertyChangeEvent evt)
    {
        super.propertyChange(evt);
    }

    ////////////////////////////////////////
    // component functions based on BeanInfo
    //
    public Class<?> getType()
    {
        return bean.getClass();
    }

    public String getTypeName()
    {
        return beanInfo.getBeanDescriptor().getDisplayName();
    }

    public String getTypeDescription()
    {
        return beanInfo.getBeanDescriptor().getShortDescription();
    }

    @Override
	public String getDisplayName()
    {
        String nameVal = null;
        if(displayNameProperty != null && displayNameProperty.getValue() != null)
            nameVal = displayNameProperty.getValue().toString();
        return nameVal == null ? getTypeName() : nameVal;
    }

    @Override
	public boolean isReadOnly()
    {
        return booleanFeature(BeanInfoEx.READ_ONLY);
    }

    private Vector<String> propertiesToRemove = null;
    public boolean hasPropertiesToRemove()
    {
        return (propertiesToRemove != null);
    }

    public void addPropertyToRemove(String property)
    {
        if(propertiesToRemove == null)
        {
            propertiesToRemove = new Vector<>();
        }
        propertiesToRemove.add(property);
    }

    public String[] getPropertiesToRemove()
    {
        return propertiesToRemove == null ?
               new String[0]   :
               (String[])propertiesToRemove.toArray(new String[0]);
    }

    public boolean isPropertyToRemove(String property)
    {
        String []props = getPropertiesToRemove();

        for( int i = 0; i < props.length; i++ )
        {
             boolean toRemove = property.equals( props[ i ] );
             if( toRemove ) return true;
        }

        return false;
    }


}

