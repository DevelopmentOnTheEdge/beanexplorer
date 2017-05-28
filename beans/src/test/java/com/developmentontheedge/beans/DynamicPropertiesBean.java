/** $Id: DynamicPropertiesBean.java,v 1.1 2001/04/13 06:10:12 fedor Exp $ */
package com.developmentontheedge.beans;

import java.awt.*;

public class DynamicPropertiesBean
{
    public DynamicPropertiesBean()
    {
        properties = new DynamicPropertySetSupport();

        properties.add(new DynamicProperty("stringValue",  String.class,  "stringValue"));
        properties.add(new DynamicProperty("integerValue", int.class,     20));
        properties.add(new DynamicProperty("colorValue",   Color.class,   Color.pink));
    }

    public String getName() { return "DynamicPropertiesBean"; }

    private final DynamicPropertySetSupport properties;
    public DynamicPropertySet getDynamicProperties() { return properties; }
}
