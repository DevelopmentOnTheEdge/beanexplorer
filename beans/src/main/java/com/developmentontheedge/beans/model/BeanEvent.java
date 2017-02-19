/** $Id: BeanEvent.java,v 1.3 2001/04/27 04:03:33 zha Exp $ */

package com.developmentontheedge.beans.model;

import java.beans.EventSetDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.swing.event.EventListenerList;

import com.developmentontheedge.beans.BeanEventListener;
import com.developmentontheedge.beans.BeanEventObject;

public class BeanEvent implements InvocationHandler
{
    private final EventListenerList listenerList = new EventListenerList();

    public BeanEvent( Property ownerProperty, EventSetDescriptor descriptor )
    {
        this.ownerProperty = ownerProperty;
        this.descriptor = descriptor;
    }

    @Override
	public Object invoke( Object proxy, Method method, Object[] args )
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for( int i = listeners.length - 2; i >= 0; i -= 2 )
        {
            if( listeners[ i ] == BeanEventListener.class )
            {
                ( ( BeanEventListener )listeners[ i + 1 ] ).beanEvent(
                    new BeanEventObject( ownerProperty.getValue(), getName(), ownerProperty.getCompleteName(),
                    method.getName(), args ) );
            }
        }
        return null;
    }

    public void addBeanEventListener( BeanEventListener listener )
    {
        listenerList.add( BeanEventListener.class, listener );
    }

    public void removeBeanEventListener( BeanEventListener listener )
    {
        listenerList.remove( BeanEventListener.class, listener );
    }


    public String getName() { return descriptor.getName(); }

    public String getDisplayName() { return descriptor.getDisplayName(); }

    private final EventSetDescriptor descriptor;
    private final Property ownerProperty;

    public EventSetDescriptor getDescriptor() { return descriptor; }

    public Property getOwnerProperty() { return ownerProperty; }
}
