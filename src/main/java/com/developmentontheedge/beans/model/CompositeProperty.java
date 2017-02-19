/** $Id: CompositeProperty.java,v 1.38 2012/09/26 08:45:28 lan Exp $ */

package com.developmentontheedge.beans.model;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Vector;

import com.developmentontheedge.beans.BeanEventListener;
import com.developmentontheedge.beans.BeanInfoConstants;
import com.developmentontheedge.beans.DefaultValue;
import com.developmentontheedge.beans.log.Logger;

/**
 * ...
 */
public class CompositeProperty extends Property
{
    /** Component that is wrapped by this ComponentModel. */
    protected Object bean;

    /** BeanInfo for Component. */
    protected BeanInfo beanInfo;
    protected Vector<Property> properties; // or maybe hashtable?
    protected Vector<BeanEvent> events; // or maybe hashtable?
    protected Vector<BeanMethod> methods; // or maybe hashtable?

    protected volatile boolean propertiesInitialized = false;
    protected boolean eventsInitialized = false;
    protected boolean methodsInitialized = false;
    // Recursion may occur in initProperties if some read method accesses this
    // ComponentModel and it will start to initialize again
    // This flag is aimed to prevent this
    private boolean propertiesInitializing = false;

    // //////////////////////////////////////
    // Constructor
    //

    /**
     * Creates new CompositeProperty for the specified component.
     * <p>
     * In general, this function should be called by ComponentFactory only.
     */
    protected CompositeProperty(Property parent, Object owner, PropertyDescriptor descriptor, BeanInfo beanInfo)
    {
        super( parent, owner, descriptor );
        this.bean = owner;
        this.beanInfo = beanInfo;
        setExpanded( false );

        propertiesInitialized = false;
        eventsInitialized = false;
        methodsInitialized = false;
    }

    protected void initProperties()
    {
        if( propertiesInitialized )
            return;

        synchronized( this )
        {
            if( propertiesInitialized || propertiesInitializing )
                return;
            propertiesInitializing = true;

            properties = new Vector<>();

            try
            {
                if( parent != null )
                {
                    // i.e. we are called for composite properies
                    Object value = getValue();
                    if( value == null )
                    {
                        Object defValue = descriptor.getValue( BeanInfoConstants.DEFAULT_VALUE );
                        if( defValue != null )
                        {
                            if( defValue instanceof DefaultValue )
                                value = ( (DefaultValue)defValue ).clone();
                            else if( defValue instanceof Method )
                                value = ( (Method)defValue ).invoke( getOwner(), (Object[])null );
                            else
                                value = defValue;
                        }
                    }
                    ComponentFactory.createProperties( value, getValueClass(), beanInfo, this, properties );
                }
                else
                {
                    // we are called for bean itself
                    ComponentFactory.createProperties( bean, bean.getClass(), beanInfo, this, properties );
                }
            }
            catch( Exception e )
            {
                Logger.getLogger().error( "initProperties()", e );
            }
            propertiesInitializing = false;
            propertiesInitialized = true;
        }
    }

    protected void initEvents()
    {
        if( eventsInitialized )
            return;

        events = new Vector<>();

        EventSetDescriptor eventsArray[] = beanInfo.getEventSetDescriptors();
        for( int i = 0; i < eventsArray.length; i++ )
        {
            BeanEvent evt = ComponentFactory.createEvent( this, eventsArray[i] );
            if( evt != null )
                events.add( evt );
        }

        eventsInitialized = true;
    }

    protected void initMethods()
    {
        if( methodsInitialized )
            return;

        methods = new Vector<>();

        MethodDescriptor methodsArray[] = beanInfo.getMethodDescriptors();
        Object methodOwner = getValue();
        for( int i = 0; i < methodsArray.length; i++ )
        {
            BeanMethod meth = ComponentFactory.createMethod( methodOwner, methodsArray[i] );
            if( meth != null )
                methods.add( meth );
        }

        methodsInitialized = true;
    }

    // //////////////////////////////////////
    // utilites
    //
    @Override
    public int getPropertyCount()
    {
        initProperties();
        return properties.size();
    }

    @Override
    public Property getPropertyAt(int index)
    {
        initProperties();
        return properties.elementAt( index );
    }

    public int getEventCount()
    {
        if( !eventsInitialized )
            initEvents();
        return events.size();
    }

    public BeanEvent getEventAt(int index)
    {
        if( !eventsInitialized )
            initEvents();
        return events.elementAt( index );
    }

    public int getMethodCount()
    {
        if( !methodsInitialized )
            initMethods();
        return methods.size();
    }

    public BeanMethod getMethodAt(int index)
    {
        if( !methodsInitialized )
            initMethods();
        return methods.elementAt( index );
    }

    /** Returns component assotiated with this ComponentModel. */
    public Object getBean()
    {
        return bean;
    }

    /** Returns BeanInfo for component associated with this ComponentModel. */
    public BeanInfo getBeanInfo()
    {
        return beanInfo;
    }

    // //////////////////////////////////////
    // event functions
    //
    /*
     * public void propertyChange( PropertyChangeEvent evt ) {
     * super.propertyChange( evt ); String name = evt.getPropertyName(); if (
     * name.endsWith( EventConstants.EVT_DISPLAY_NAME ) ) { } else if (
     * name.endsWith( EventConstants.EVT_PROPERTY_ADDED ) ) { } else if (
     * name.endsWith( EventConstants.EVT_PROPERTY_REMOVED ) ) { } else if (
     * name.endsWith( EventConstants.EVT_READ_ONLY ) ) { } else if (
     * name.endsWith( EventConstants.EVT_SET_VALUE ) ) { } else { } }
     */

    @Override
    public String toString()
    {
        return getDisplayName();
    }

    /**
     * @pending Write Tests!
     */
    public void addBeanEventListener(String eventName, BeanEventListener listener)
    {
        for( BeanEvent be : events )
        {
            if( be.getName().equals( eventName ) )
            {
                be.addBeanEventListener( listener );
                break;
            }
        }
    }

    public void removeBeanEventListener(String eventName, BeanEventListener listener)
    {
        for( BeanEvent be : events )
        {
            if( be.getName().equals( eventName ) )
            {
                be.removeBeanEventListener( listener );
                break;
            }
        }
    }

    public void addBeanEventListener(BeanEventListener listener)
    {
        for( BeanEvent be : events )
        {
            be.addBeanEventListener( listener );
        }
    }

    public void removeBeanEventListener(BeanEventListener listener)
    {
        for( BeanEvent be : events )
        {
            be.removeBeanEventListener( listener );
        }
    }
}
