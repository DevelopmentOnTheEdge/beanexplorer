package com.developmentontheedge.beans.lesson11.barchart;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.IndexedPropertyDescriptorEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class ColumnBeanInfo extends BeanInfoEx
{
    public ColumnBeanInfo()
    {
        super( Column.class, ColumnMessageBundle.class.getName() );
        beanDescriptor.setDisplayName( getResourceString("CN_CLASS") );
        beanDescriptor.setShortDescription( getResourceString("CD_CLASS") );
    }

    public void initProperties() throws Exception
    {
        PropertyDescriptorEx pde;

        pde = new PropertyDescriptorEx("label", beanClass);
        add(pde, getResourceString("PN_LABEL"), getResourceString("PD_LABEL") );

        pde = new PropertyDescriptorEx("value", beanClass);
        add(pde, getResourceString("PN_VALUE"), getResourceString("PD_VALUE") );

        pde = new PropertyDescriptorEx("color", beanClass);
        add(pde, getResourceString("PN_COLOR"), getResourceString("PD_COLOR") );

        pde = new PropertyDescriptorEx("visible", beanClass);
        add(pde, getResourceString("PN_VISIBLE"), getResourceString("PD_VISIBLE") );
    }
}
