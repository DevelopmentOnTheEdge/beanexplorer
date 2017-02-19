package com.developmentontheedge.beans.awt.infos;

import java.awt.Color;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.log.Logger;

public class ColorBeanInfo extends BeanInfoEx
{
    public ColorBeanInfo()
    {
        super(Color.class, ColorMessageBundle.class.getName());
        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
        setSimple(true);
        try
        {
            setBeanEditor(Class.forName("sun.beans.editors.ColorEditor"));
        }
        catch( ClassNotFoundException exc )
        {
            Logger.getLogger().error( getClass().getName(), exc );
        }
    }
}
