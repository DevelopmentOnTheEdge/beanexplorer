package com.developmentontheedge.beans.swing.table;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class RowHeaderBeanBeanInfo extends BeanInfoEx
{
    public RowHeaderBeanBeanInfo()
    {
        super( RowHeaderBean.class,null );
        beanDescriptor.setDisplayName( "row" );
        beanDescriptor.setShortDescription( "Row Number" );
    }
    @Override
    public void initProperties() throws Exception
    {
        PropertyDescriptorEx pde;
        pde = new PropertyDescriptorEx("number",beanClass);
        pde.setReadOnly(true);
        add( pde, RowHeaderRenderer.class, "Num", "Row Number" );
    }
}
