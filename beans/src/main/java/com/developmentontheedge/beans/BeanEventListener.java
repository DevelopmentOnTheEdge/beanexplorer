package com.developmentontheedge.beans;

import java.util.EventListener;

@FunctionalInterface
public interface BeanEventListener extends EventListener
{
    public void beanEvent( BeanEventObject evt );
}
