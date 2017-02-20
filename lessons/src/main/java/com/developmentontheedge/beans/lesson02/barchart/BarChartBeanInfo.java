package com.developmentontheedge.beans.lesson02.barchart;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.IndexedPropertyDescriptorEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class BarChartBeanInfo extends BeanInfoEx
{
    public BarChartBeanInfo()
    {
        super( BarChart.class, BarChartMessageBundle.class.getName() );
        beanDescriptor.setDisplayName( getResourceString("CN_CLASS") );
        beanDescriptor.setShortDescription( getResourceString("CD_CLASS") );
    }

    public void initProperties() throws Exception
    {
        PropertyDescriptorEx pde;

        pde = new PropertyDescriptorEx("title", beanClass);
        add(pde, getResourceString("PN_TITLE"), getResourceString("PD_TITLE") );

        pde = new PropertyDescriptorEx("orientation", beanClass);
        add(pde, getResourceString("PN_ORIENTATION"), getResourceString("PD_ORIENTATION") );

        pde = new PropertyDescriptorEx("drawFont", beanClass);
        add(pde, getResourceString("PN_FONT"), getResourceString("PD_FONT") );

        pde = new PropertyDescriptorEx("preferredSize", beanClass);
        add(pde, getResourceString("PN_PREFERRED_SIZE"), getResourceString("PD_PREFERRED_SIZE") );

        pde = new PropertyDescriptorEx("barSpacing", beanClass);
        add(pde, getResourceString("PN_BAR_SPACING"), getResourceString("PD_BAR_SPACING") );

        pde = new PropertyDescriptorEx("scale", beanClass);
        add(pde, getResourceString("PN_SCALE"), getResourceString("PD_SCALE") );

        pde = new PropertyDescriptorEx("columns", beanClass);
        add(pde, getResourceString("PN_COLUMNS"), getResourceString("PD_COLUMNS") );
    }
}
