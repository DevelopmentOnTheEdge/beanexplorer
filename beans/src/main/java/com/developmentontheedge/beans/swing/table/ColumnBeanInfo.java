package com.developmentontheedge.beans.swing.table;

import java.beans.IntrospectionException;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

/**
 * @todo Comment
 * @todo move text constants into MessageBundle
 */
public class ColumnBeanInfo extends BeanInfoEx
{
    public ColumnBeanInfo()
    {
        super( Column.class, ColumnMessageBundle.class.getName() );
        beanDescriptor.setDisplayName( getResourceString( "CN_CLASS" ) );
        beanDescriptor.setShortDescription( getResourceString( "CD_CLASS" ) );
        setCompositeEditor("enabled", new java.awt.GridLayout(1, 1));
    }

    @Override
    public void initProperties() throws IntrospectionException
    {
        addHidden(new PropertyDescriptorEx("enabled", beanClass));
    }
}