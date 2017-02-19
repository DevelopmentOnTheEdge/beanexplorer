package com.developmentontheedge.beans.swing.infos;

import java.sql.Date;

import com.developmentontheedge.beans.BeanInfoEx;

public class DateBeanInfo extends BeanInfoEx
{
    public DateBeanInfo()
    {
        super(Date.class, DateMessageBundle.class.getName());
        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
        setSimple(true);
    }
}
