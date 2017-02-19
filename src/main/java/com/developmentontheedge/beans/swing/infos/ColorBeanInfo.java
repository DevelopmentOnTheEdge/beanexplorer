package com.developmentontheedge.beans.swing.infos;

import java.awt.Color;
import java.beans.PropertyDescriptor;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.editors.ColorEditor;

public class ColorBeanInfo extends BeanInfoEx
{
    public ColorBeanInfo()
    {
        super(Color.class, ColorMessageBundle.class.getName());

        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
        setSimple(true);
        setBeanEditor(ColorEditor.class);
    }

    @Override
    protected void initProperties() throws Exception
    {
        super.initProperties();
        addHidden(new PropertyDescriptor("red", beanClass, "getRed", null));
        addHidden(new PropertyDescriptor("green", beanClass, "getGreen", null));
        addHidden(new PropertyDescriptor("blue", beanClass, "getBlue", null));
        addHidden(new PropertyDescriptor("alpha", beanClass, "getAlpha", null));
    }
}
