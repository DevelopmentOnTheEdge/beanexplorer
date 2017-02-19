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

import java.awt.Dimension;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.editors.DimensionEditor;

public class DimensionBeanInfo extends BeanInfoEx
{
    public DimensionBeanInfo()
    {
        super(Dimension.class, DimensionMessageBundle.class.getName());
        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
//        setCompositeEditor("width;height", new java.awt.GridLayout(1, 2));
        setBeanEditor(DimensionEditor.class);
        setSimple( true );
    }

/*
    protected void initProperties() throws Exception
    {
        PropertyDescriptorEx pd = new PropertyDescriptorEx("width", beanClass.getMethod("getWidth", null), null);
        pd.setHidden( true );
        add(pd,
            getResourceString("WIDTH_NAME"),           //  display_name ()
            getResourceString("WIDTH_DESCRIPTION"));   //  description

        pd = new PropertyDescriptorEx("height", beanClass.getMethod("getHeight", null), null);
        pd.setHidden( true );
        add(pd,
            getResourceString("HEIGHT_NAME"),
            getResourceString("HEIGHT_DESCRIPTION"));
    }
*/

}
