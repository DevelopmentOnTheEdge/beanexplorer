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

import java.awt.BasicStroke;

import com.developmentontheedge.beans.BeanInfoEx;

public class BasicStrokeBeanInfo extends BeanInfoEx
{
    public BasicStrokeBeanInfo()
    {
        super(BasicStroke.class, BasicStrokeMessageBundle.class.getName() );
        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
        setSimple( true );
    }
}
