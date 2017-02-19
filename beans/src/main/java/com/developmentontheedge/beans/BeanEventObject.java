package com.developmentontheedge.beans;

import java.util.EventObject;

public class BeanEventObject extends EventObject
{
    private static final long serialVersionUID = 1L;

    public BeanEventObject( Object source, String name, String propertyName, String method,
        Object[] arguments )
        {
            super( source );
            this.name = name;
            this.propertyName = propertyName;
            this.method = method;
            this.arguments = arguments;
    }

    public String getName() { return name; }

    public String getPropertyName() { return propertyName; }

    public void setPropertyName( String propertyName ) { this.propertyName = propertyName; }

    public String getMethod() { return method; }

    public Object[] getArguments() { return arguments; }

    private final String name;
    private String propertyName;
    private final String method;
    private final Object[] arguments;

    @Override
    public String toString()
    {
        return "[name=" + name + ", propertyName=" + propertyName + ", method =" + method + "]";
    }
}
