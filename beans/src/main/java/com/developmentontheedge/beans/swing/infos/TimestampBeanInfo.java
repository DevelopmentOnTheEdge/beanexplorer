package com.developmentontheedge.beans.swing.infos;

import java.sql.Timestamp;

import com.developmentontheedge.beans.BeanInfoEx;

public class TimestampBeanInfo extends BeanInfoEx
{
    public TimestampBeanInfo()
    {
        super(Timestamp.class, TimestampMessageBundle.class.getName());
        beanDescriptor.setDisplayName(getResourceString("DISPLAY_NAME"));
        beanDescriptor.setShortDescription(getResourceString("SHORT_DESCRIPTION"));
        setSimple(true);
    }
}
