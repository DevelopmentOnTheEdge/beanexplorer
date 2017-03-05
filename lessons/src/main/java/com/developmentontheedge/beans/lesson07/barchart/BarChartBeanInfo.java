package com.developmentontheedge.beans.lesson07.barchart;

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

/*->*/  pde = new PropertyDescriptorEx("columns", beanClass);
/*->*/  add(pde, getResourceString("PN_COLUMNS0"), getResourceString("PD_COLUMNS0") );

/*->*/  pde = new PropertyDescriptorEx("columns", beanClass);
/*->*/  pde.setName( "columns1" ); // change programmatic name 
/*->*/  pde.setChildDisplayName(beanClass.getMethod("calcColumnName",
/*->*/       new Class[] { Integer.class, Object.class } ) );
/*->*/  add( pde,getResourceString("PN_COLUMNS1"), getResourceString("PD_COLUMNS1") );

/*->*/  pde = new PropertyDescriptorEx("columns",beanClass);
/*->*/  pde.setName( "columns2" ); // change programmatic name 
/*->*/  pde.setCompositeEditor( "[0]/label;[0]/visible;[1]/label;[1]/visible;[2]/label;[2]/visible;[3]/label;[3]/visible", new java.awt.GridLayout( 1, 8 ) );
/*->*/  add( pde,getResourceString("PN_COLUMNS2"),getResourceString("PD_COLUMNS2") );

    }
}
