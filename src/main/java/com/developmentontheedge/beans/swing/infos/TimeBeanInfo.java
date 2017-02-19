package com.developmentontheedge.beans.swing.infos;

import java.sql.Time;

import com.developmentontheedge.beans.BeanInfoEx;

public class TimeBeanInfo extends BeanInfoEx
{
    public TimeBeanInfo()
    {
        super(Time.class, TimeMessageBundle.class.getName());
        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
        setSimple(true);
    }
}
