/*
 * Author:  Igor V. Tyazhev  (champ@developmentontheedge.com)
 *
 * Created: 27.03.2001
 *
 * Description:
 *
 * Copyright (C) 2000, 2001 DevelopmentOnTheEdge.com. All rights reserved.
 */
package com.developmentontheedge.beans.swing.infos;

import java.awt.Point;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.PropertyDescriptorEx;

public class PointBeanInfo extends BeanInfoEx
{
    public PointBeanInfo()
    {
        super(Point.class, PointMessageBundle.class.getName() );
        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
    }

    @Override
    protected void initProperties() throws Exception
    {
        PropertyDescriptorEx pd = new PropertyDescriptorEx("x", beanClass.getMethod("getX", ( Class[] )null), null);
        add(pd,
            getResourceString("X_NAME"),           //  display_name ()
            getResourceString("X_DESCRIPTION"));   //  description

        pd = new PropertyDescriptorEx("y", beanClass.getMethod("getY", ( Class[] )null), null);
        add(pd,
            getResourceString("Y_NAME"),
            getResourceString("Y_DESCRIPTION"));
    }
}
