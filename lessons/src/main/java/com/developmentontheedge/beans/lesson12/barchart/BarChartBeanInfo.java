package com.developmentontheedge.beans.lesson12.barchart;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class BarChartBeanInfo extends BeanInfoEx
{
    public BarChartBeanInfo()
    {
        super(BarChart.class, BarChartMessageBundle.class.getName() );
        beanDescriptor.setDisplayName( getResourceString("CN_CLASS") );
        beanDescriptor.setShortDescription( getResourceString("CD_CLASS") );
    }

    public void initProperties() throws Exception
    {
        PropertyDescriptorEx pde;

        pde = new PropertyDescriptorEx("preferences", beanClass);
        add( pde, getResourceString("PN_PREF"),getResourceString( "PD_PREF") );

        pde = new PropertyDescriptorEx("columns",beanClass);
        add( pde,getResourceString("PN_COLUMNS"),getResourceString( "PD_COLUMNS") );
    }
}
