package com.developmentontheedge.beans.awt.infos;

import java.awt.Font;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.log.Logger;

public class FontBeanInfo extends BeanInfoEx
{
    public FontBeanInfo()
    {
        super(Font.class, FontMessageBundle.class.getName());
        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
        setSimple( true );
        try
        {
            setBeanEditor(Class.forName("sun.beans.editors.FontEditor"));
        }
        catch( ClassNotFoundException exc )
        {
            Logger.getLogger().error( getClass().getName(), exc );
        }
    }
}
