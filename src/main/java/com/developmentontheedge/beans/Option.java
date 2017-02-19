package com.developmentontheedge.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.EventListenerList;

import java.io.Serializable;

/**
 * This class implements generic approach for propagating <code>PropertyChangeEvent</code>
 * from children to parent.
 * Suppose class A contains option B and B has nested options C1, C2, C3, so
 * B is parent for C1, C2, C3.
 * Let's L will be <code>PropertyChangeListener</code> of A.
 * Then, if some of the children options C1, C2 or C3 is changed, it will notify
 * its listener and parent listeners as well (i.e. L).
 */
public class Option implements Serializable, PropertyChangeObservable
{
    private static final long serialVersionUID = -1728854926703429994L;

    /** Constructs option without parent. */
    public Option()
    {
        this( null );
    }

    /**
     * Constructs Option with parent.
     * Parent is used for delegating events to the parent nodes.
     * @param parent parent option
     */
    public Option( Option parent )
    {
        setParent( parent );
    }

    transient private Option parent;
    
    /**
     * nameInParent is the bean property name in parent object to access this object
     * Thus when nameInParent is set, option.getParent().findProperty(option.getNameInParent()).getValue() must return option
     */
    transient private String nameInParent;

    /**
     * Changes parent property of this option.
     * @param parent new parent property
     */
    public void setParent( Option parent )
    {
        this.parent = parent;
    }

    /**
     * Changes parent property of this option.
     * @param parent new parent property
     */
    public void setParent( Option parent, String nameInParent )
    {
        this.parent = parent;
        this.nameInParent = nameInParent;
    }

    /** Returns parent of this option. */
    public Option getParent()
    {
        return parent;
    }

    ////////////////////////////////////////
    // Listener issues
    //

    /* List of listeners. */

    transient protected EventListenerList listenerList;

    /**
     * Add a PropertyChangeListener to the listener list. The listener is registered
     * for all the properties.
     * @param l the PropertyChangeListener to be added
     */
    @Override
    public void addPropertyChangeListener( PropertyChangeListener l )
    {
        if(listenerList == null) listenerList = new EventListenerList();
        listenerList.add( PropertyChangeListener.class, l );
    }

    /**
     * Remove PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties.
     * @param l the PropertyChangeListener to be removed
     */
    @Override
    public void removePropertyChangeListener( PropertyChangeListener l )
    {
        if(listenerList == null) return;
        listenerList.remove( PropertyChangeListener.class, l );
    }

    /**
     * Copies all listeners to other option.
     * @param options Destination for copying listeners.
     */
    public void copyListenersTo( Option options )
    {
        if(listenerList == null) return;
        Object[] listSource = listenerList.getListenerList();
        for ( int i = 0; i < listSource.length; i++ )
        {
            options.addPropertyChangeListener( ( PropertyChangeListener )listSource[ i ] );
        }
    }

    ////////////////////////////////////////
    // firePropertyChange issues
    //

    transient protected boolean notificationEnabled = true;
    public boolean isNotificationEnabled()
    {
        return notificationEnabled;
    }
    public void setNotificationEnabled(boolean enabled)
    {
        notificationEnabled = enabled;
    }

    transient protected boolean propagationEnabled = true;
    public boolean isPropagationEnabled()
    {
        return propagationEnabled;
    }    
    public void setPropagationEnabled(boolean enabled)
    {
        propagationEnabled = enabled;
    }


    /**
     * Reports a bound property update to any registered listeners. No event is fired if old and
     * new values are equal and non-null.
     * @param evt the PropertyChangeEvent object.
     * @pending for propagation PropertyChangeEvent should be created more accurately
     */
    protected void firePropertyChange( PropertyChangeEvent evt )
    {
        if( !notificationEnabled || 
            (   evt.getOldValue() != null && evt.getNewValue() != null
             && evt.getOldValue().equals(evt.getNewValue()) 
            )
          )
        {
            return;
        }

        // first of all notify direct listeners
        if( listenerList != null )
        {
            Object[] listeners = listenerList.getListenerList();
            for( int i = listeners.length - 2; i >= 0; i -= 2 )
            {
                if( listeners[i] == PropertyChangeListener.class )
                {
                    ( (PropertyChangeListener)listeners[i + 1] ).propertyChange(evt);
                }
            }
        }

        // then propagate
        // see pending
        if ( notificationEnabled && propagationEnabled && parent != null )
        {
            evt.setPropagationId( this );
            parent.firePropertyChange( evt );
        }
    }

    /**
     * Reports a bound property update to any of the registered listeners. No event is fired if old and
     * new values are equal and non-null.
     * @param propertyName  programmatic name of the property that was changed.
     * @param oldValue old value of the property.
     * @param newValue new value of the property.
     */
    protected void firePropertyChange( String propertyName, Object oldValue, Object newValue )
    {
        if( notificationEnabled )
            firePropertyChange( new PropertyChangeEvent( this, propertyName, oldValue, newValue ) );
    }

    /**
     * Reports a bound property update for a boolean property to any of the registered listeners. No event is fired if old
     * and new values are equal and non-null.
     * This is merely a convenience wrapper around the more general firePropertyChange method that
     * takes Object values.
     * @param propertyName programmatic name of the property that was changed.
     * @param oldValue old value of the property.
     * @param newValue new value of the property.
     */
    protected void firePropertyChange( String propertyName, boolean oldValue, boolean newValue )
    {
        if( !notificationEnabled || oldValue == newValue )
            return;

        firePropertyChange(propertyName, (Boolean)oldValue, (Boolean)newValue);
    }

    /**
     * Reports a bound property update for a integer property to any of the registered listeners. No event is fired if
     * old and new values are equal and non-null.
     * This is merely a convenience wrapper around the more general firePropertyChange method that
     * takes Object values.
     * @param propertyName programmatic name of the property that was changed.
     * @param oldValue old value of the property.
     * @param newValue new value of the property.
     */
    protected void firePropertyChange( String propertyName, int oldValue, int newValue )
    {
        if( !notificationEnabled || oldValue == newValue )
            return;

        firePropertyChange(propertyName, (Integer)oldValue, (Integer)newValue);
    }

    /**
     * Reports a bound property update for a double property to any of the registered listeners. No event is fired if
     * old and new values are equal and non-null.
     * This is merely a convenience wrapper around the more general firePropertyChange method that
     * takes Object values.
     * @param propertyName programmatic name of the property that was changed
     * @param oldValue old value of the property
     * @param newValue new value of the property
     */
    protected void firePropertyChange( String propertyName, double oldValue, double newValue )
    {
        if( !notificationEnabled || oldValue == newValue )
            return;

        firePropertyChange(propertyName, (Double)oldValue, (Double)newValue);
    }

    /**
     * @return the nameInParent
     * nameInParent is the bean property name in parent object to access this object
     * Thus when nameInParent is set, option.getParent().findProperty(option.getNameInParent()).getValue() must return option
     */
    public String getNameInParent()
    {
        return nameInParent;
    }
}
