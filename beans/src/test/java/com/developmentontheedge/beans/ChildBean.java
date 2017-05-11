/** $Id: ChildBean.java,v 1.1 2001/05/20 13:22:48 fedor Exp $ */
package com.developmentontheedge.beans;

import javax.swing.event.EventListenerList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

public class ChildBean
{
    protected Logger cat = Logger.getLogger(BeanTest.class.getName());
    ////////////////////////////////////////
    // Property change issues
    //

    protected EventListenerList listenerList = new EventListenerList();
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        listenerList.add(PropertyChangeListener.class, listener);
        cat.info("PC listener added: " + listener.getClass() + ":" + listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        listenerList.remove(PropertyChangeListener.class, listener);
        cat.info("PC listener removed: " + listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
        Object[] listeners = listenerList.getListenerList();
        PropertyChangeEvent pce = new PropertyChangeEvent(this, propertyName, oldValue, newValue);

        for (int i = listeners.length-2; i>=0; i-=2)
        {
            if ( listeners[i] == PropertyChangeListener.class)
                ((PropertyChangeListener)listeners[i+1]).propertyChange(pce);
        }
    }

    ////////////////////////////////////////
    // properties
    //

    protected String simpleProperty = "simple";
    public String getSimpleProperty()
    {
        return simpleProperty;
    }
    public void setSimpleProperty(String simpleProperty)
    {
        this.simpleProperty = simpleProperty;
        setBindedProperty("binded: " + getSimpleProperty());
    }

    protected String bindedProperty = "binded";
    public String getBindedProperty()
    {
        return bindedProperty;
    }
    public void setBindedProperty(String bindedProperty)
    {
        String oldValue = this.bindedProperty;
        this.bindedProperty = bindedProperty;
        firePropertyChange("bindedProperty", oldValue, bindedProperty);
    }

    protected Object compositeProperty = "composite";
    public Object getCompositeProperty()
    {
        return compositeProperty;
    }
    public void setCompositeProperty(Object compositeProperty)
    {
        this.compositeProperty = compositeProperty;
    }

    protected SimpleBean simpleBean = new SimpleBean();
    public SimpleBean getSimpleBean()
    {
        return simpleBean;
    }
    public void setSimpleBean(SimpleBean simpleBean)
    {
        this.simpleBean = simpleBean;
    }

    /*
    protected DynamicPropertiesBean dynamicPropertiesBean = new DynamicPropertiesBean();
    public DynamicPropertiesBean getDynamicPropertiesBean()
    {
        return dynamicPropertiesBean;
    }
    public void setDynamicPropertiesBean(DynamicPropertiesBean dynamicPropertiesBean)
    {
        this.dynamicPropertiesBean = dynamicPropertiesBean;
    }*/
}



