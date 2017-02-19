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

import java.awt.Font;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.editors.FontEditor;

public class FontBeanInfo extends BeanInfoEx
{
    public FontBeanInfo()
    {
        super(Font.class, FontMessageBundle.class.getName());
        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
        setSimple( true );
        setBeanEditor(FontEditor.class);
    }
}
