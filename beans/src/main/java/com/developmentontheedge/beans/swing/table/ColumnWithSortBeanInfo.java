package com.developmentontheedge.beans.swing.table;

import java.beans.IntrospectionException;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.ChoicePropertyDescriptorEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

/**
 * @todo Comment
 * @todo move text constants into MessageBundle
 */
public class ColumnWithSortBeanInfo extends BeanInfoEx
{
    public ColumnWithSortBeanInfo()
    {
        super( ColumnWithSort.class, ColumnWithSortMessageBundle.class.getName() );
        beanDescriptor.setDisplayName( getResourceString( "CN_CLASS" ) );
        beanDescriptor.setShortDescription( getResourceString( "CD_CLASS" ) );
        setCompositeEditor("enabled;sorting", new java.awt.GridLayout(1, 2));
    }

    @Override
    public void initProperties() throws IntrospectionException
    {
        addHidden(new PropertyDescriptorEx("enabled", beanClass));
        //addHidden(new PropertyDescriptorEx("sorting", beanClass));
        addHidden(new ChoicePropertyDescriptorEx("sorting", beanClass, SortEditor.class ));
    }
}