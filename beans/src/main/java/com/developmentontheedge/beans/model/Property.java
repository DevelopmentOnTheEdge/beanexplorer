/** $Id: Property.java,v 1.99 2013/02/15 09:09:32 lan Exp $ */

package com.developmentontheedge.beans.model;

import java.awt.Dimension;
import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.event.EventListenerList;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.DynamicPropertySet;
import com.developmentontheedge.beans.EventConstants;
import com.developmentontheedge.beans.IconResource;
import com.developmentontheedge.beans.log.Logger;

abstract public class Property implements PropertyChangeListener
{
    /** The property parent. */
    protected Property parent;

    /** The object that is owner of this property. */
    protected Object owner;

    public Object getOwner()
    {
        return owner != null ? owner : parent.getOwner();
    }

    protected Object readValue( Method method )
    {
        if ( method == null )
        {
            return null;
        }
        try
        {
            return method.invoke( getOwner(), ( Object[] )null );
        }
        catch ( Exception e )
        {
            Logger.getLogger().error( "When invoking method '" + method + "' on " + getOwner().getClass(), e );
        }
        return null;
    }
    ////////////////////////////////////////
    // Constructor
    //

    /**
     * Creates a new Property. In general, this function should be called only
     * by ComponentFactory.
     */
    protected Property( Property parent, Object theOwner, FeatureDescriptor theDescriptor )
    {
        this.parent = parent;
        owner = theOwner;
        descriptor = theDescriptor;

        if ( owner instanceof DynamicPropertySet )
        {
            DynamicPropertySet dps = (DynamicPropertySet)owner;
            Class<?> type = dps.getType(descriptor.getName());

            // hack values for to use wrappers for dynamic properties
            try
            {
                if ( theDescriptor instanceof IndexedPropertyDescriptor )
                {
                    descriptor = new DynValueIndexedPropertyDescriptor( theOwner, type,
                        ( IndexedPropertyDescriptor )theDescriptor );
                }
                else
                {
                    descriptor = new DynValuePropertyDescriptor( theOwner, type,
                        ( PropertyDescriptor )theDescriptor );
                }
            }
            catch ( IntrospectionException ex ) // this VERY unlikely to be thrown!
            {
                Logger.getLogger().error( "Error creating dynamic property", ex );
            }
            owner = new PropWrapper( theOwner, theDescriptor.getName() );
        }

        ownerForPropertyChanges = theOwner;
    }

    protected Object ownerForPropertyChanges;

    static boolean invokeAddRemovePropertyChangeListenerMethod( Object firer, String propName,
        String methodName, PropertyChangeListener pcl )
        {
            boolean bFirer = false;
            try
            {
                Method method = firer.getClass().getMethod( methodName,
                    new Class[] { String.class, PropertyChangeListener.class } );
                method.invoke( firer,
                    new Object[] { propName, pcl } );
                method.invoke( firer,
                    new Object[] { propName + EventConstants.EVT_SET_VALUE, pcl } );
                method.invoke( firer,
                    new Object[] { propName + EventConstants.EVT_READ_ONLY, pcl } );
                method.invoke( firer,
                    new Object[] { propName + EventConstants.EVT_DISPLAY_NAME, pcl } );
                method.invoke( firer,
                    new Object[] { propName + EventConstants.EVT_PROPERTY_ADDED, pcl } );
                method.invoke( firer,
                    new Object[] { propName + EventConstants.EVT_PROPERTY_REMOVED, pcl } );
                bFirer = true;
            }
            catch ( NoSuchMethodException ignore ) { }
            catch ( IllegalAccessException ignore ) { }
            catch ( InvocationTargetException ignore ) { }

            if ( !bFirer )
            {
                try
                {
                    Method method = firer.getClass().getMethod( methodName,
                        new Class[] { PropertyChangeListener.class } );
                    method.invoke( firer,
                        new Object[] { pcl } );
                    bFirer = true;
                }
                catch ( NoSuchMethodException ignore ) { }
                catch ( InvocationTargetException ignore ) { }
                catch ( IllegalAccessException ignore ) { }
            }
            return bFirer;
    }

    ////////////////////////////////////////
    // get/set value methods
    //

    /** Returns value of this property. */
    public Object getValue()
    {
        Method method = null;
        try
        {
            method = ((PropertyDescriptor)descriptor).getReadMethod();
            if(method != null)
                return  method.invoke( getOwner(), ( Object[] )null );

            // hopefully write only properties
            // that do not allow to create dummy instances
            // were screened out at creation stage

            // fedor: is it wise to create instance here?
            // I comment it to supress bug #283
            // if(descriptor instanceof PropertyDescriptor)
            //    return ((PropertyDescriptor)descriptor).getPropertyType().newInstance();

            //    return ((BeanDescriptor)descriptor).getBeanClass().newInstance();
        }
        catch ( Exception e )
        {
            if ( !( e instanceof InvocationTargetException ) ) // i.e. not some application specific error
            {
                Logger.getLogger().error( "getValue : property apply error: " + e +
                    "\r\n\t property     = " + getClass() + "\r\n\t completeName = " +
                    getCompleteName() + "\r\n\t owner        = " + owner +
                    "\r\n\t getOwner()   = " + getOwner() + "\r\n\t ownerClass   = " +
                    owner.getClass() + "\r\n\t method       = " + method +
                    "\r\n\t parent       = " + parent + ":" +
                    ( parent == null ? null : parent.getClass() ), e );
            }
        }

        return null;
    }

    /** Set up a new value for the property. 
     * @throws NoSuchMethodException */
    public void setValue( Object value ) throws NoSuchMethodException
    {
        Method method = ( ( PropertyDescriptor )descriptor ).getWriteMethod();
        if ( method == null )
        {
            return;
        }
        try
        {
            Object oldVal = getValue();
            method.invoke( getOwner(),
                new Object[] { value } );
            if ( !bComponentFiresPropertyEventsItself && (listenerList != null || parent != null) )
            {
                firePropertyChange(
                    new PropertyChangeEvent( owner,
                    getCompleteName() + EventConstants.EVT_SET_VALUE, oldVal, value ) );
            }
        }
        catch ( Exception e )
        {
            Logger.getLogger().error( "property apply error: " + e + "\r\n\t this    = " + getClass() +
                ":" + getCompleteName() + ":" + hashCode() + "\r\n\t owner   = " + owner +
                "\r\n\t method  = " + method + "\r\n\t parent  = " + parent + ":" +
                ( parent == null ? null : parent.getClass() ) + "\r\n\t value   = " + value +
                ", class = " + ( value == null ? null : value.getClass() ), e );
        }
    }

    public Property getParent()
    {
        return parent;
    }

    ////////////////////////////////////////
    // metadata
    //

    /** Property descriptor based on BeanInfo. */
    protected FeatureDescriptor descriptor;

    public FeatureDescriptor getDescriptor()
    {
        return descriptor;
    }

    public Object getAttribute( String attrName )
    {
        Object value = descriptor.getValue( attrName );
        if ( value instanceof Method )
        {
            value = readValue( ( Method )value );
        }
        return value;
    }

    protected boolean booleanFeature( String feature )
    {
        Object value = descriptor.getValue( feature );
        if ( value instanceof Method )
        {
            value = readValue( ( Method )value );
        }
        if ( value instanceof Boolean )
        {
            return ( ( Boolean )value ).booleanValue();
        }
        return false;
    }

    public boolean getBooleanAttribute( String attrName )
    {
         return booleanFeature( attrName );
    }

    protected String stringFeature( String feature )
    {
        Object value = descriptor.getValue( feature );
        if ( value instanceof Method )
        {
            value = readValue( ( Method )value );
        }
        if ( value instanceof String )
        {
            return ( String )value;
        }
        return null;
    }

    public String getStringAttribute( String attrName )
    {
         return stringFeature( attrName );
    }

    public void setAttribute( String attrName, Object attrValue )
    {
        descriptor.setValue( attrName, attrValue );
    }


    /**
     * Gets the Class object for the property value.
     *
     * The following algorithm is used: if value is not null, <code>value.getClass</code>
     * will be returned. Otherwise we use information from PropertyDescriptor or BeanDescriptor.
     *
     * @see PropertyDescriptor#getPropertyType
     * @see BeanDescriptor#getBeanClass
     */
    public Class<?> getValueClass()
    {
        Object value = getValue();
        if(value != null)
            return value.getClass();

        if(descriptor instanceof PropertyDescriptor)
            return ((PropertyDescriptor)descriptor).getPropertyType();

        return ((BeanDescriptor)descriptor).getBeanClass();
    }

    /**
     * Indicates is property is recursive that is some of its parents has the same
     * property value class.
     */
    public boolean isRecursive( Class<?> c )
    {
        Property p = this;
        while ( p != null )
        {
            Class<?> other = p.getValueClass();
            // if 'other' equals null
            // this means that 'p' is indexed property that doesn't support not indexed access
            // It shouldn't happen buf if it happens i.e. exception is thrown
            // you will know what is the reason
            if ( other.equals( c ) )
                return true;

            p = p.parent;
        }

        return false;
    }

    /** Returns property name. */
    public String getName()
    {
        return descriptor.getName();
    }

    /** Returns property complete name: parent_complete_name/name. */
    public String getCompleteName()
    {
        if ( parent == null ) // most likely component itself
        { return getName(); }
        if ( parent.parent == null ) // i.e. it is first-level property in the component
        { return getName(); }
        return parent.getCompleteName() + '/' + getName();
    }

    /** Returns property display name. */
    public String getDisplayName()
    {
        String displayName = null;

        // process the case of bean, when it is itself responsible for name generation
        Object bean = getValue();
        if( bean != null
            && descriptor.getValue(BeanInfoEx.BEAN_DISPLAY_NAME) != null
            && descriptor.getValue(BeanInfoEx.DISPLAY_NAME) == null
            && !(parent != null && parent instanceof ArrayProperty
                 && parent.getDescriptor().getValue(BeanInfoEx.CHILD_DISPLAY_NAME) != null) )
        {
            try
            {
                Method method = (Method)descriptor.getValue(BeanInfoEx.BEAN_DISPLAY_NAME);
                displayName = (String)method.invoke(bean, ( Object[] )null);
                return displayName;
            }
            catch ( Exception e )
            {
                Logger.getLogger().error( "Error getting bean display name", e);
            }
        }

        // get the name for not array child property
        if( !(parent != null && parent instanceof ArrayProperty) )
        {
            displayName = stringFeature(BeanInfoEx.DISPLAY_NAME);
        }

        // process array property child name
        else
        {
            Object value = parent.getDescriptor().getValue(BeanInfoEx.CHILD_DISPLAY_NAME);
            if ( value instanceof Method )
            {
                try
                {
                    displayName = (String) ((Method )value ).invoke( parent.getOwner(),
                        new Object[]
                        {
                            descriptor.getValue( InternalConstants.CHILD_INDEX ), getValue()
                        } );
                }
                catch ( Exception e )
                {
                    Logger.getLogger().error( "Error getting child display name for Array Property", e );
                }
            }
        }

        return displayName != null ? displayName : descriptor.getDisplayName();
    }

    public Image getIcon()
    {
        Image image = getIcon(BeanInfo.ICON_COLOR_16x16);
        if( image != null)
            return image;

        return getIcon(BeanInfo.ICON_MONO_16x16);
    }

    public Image getIcon( int type )
    {
        Object icon = null;
        switch ( type )
        {
            case BeanInfo.ICON_COLOR_16x16:
                icon = descriptor.getValue(BeanInfoEx.NODE_ICON_COLOR_16x16); break;
            case BeanInfo.ICON_COLOR_32x32:
                icon = descriptor.getValue(BeanInfoEx.NODE_ICON_COLOR_32x32); break;
            case BeanInfo.ICON_MONO_16x16:
                icon = descriptor.getValue(BeanInfoEx.NODE_ICON_MONO_16x16);  break;
            case BeanInfo.ICON_MONO_32x32:
                icon = descriptor.getValue(BeanInfoEx.NODE_ICON_MONO_32x32);  break;
        }

        // for compatibility with MatchPro
        if(icon instanceof Image)
            return (Image)icon;

        if(icon instanceof IconResource)
            return ((IconResource)icon).getImage();

        return null;
    }

    public Class<?> getPropertyEditorClass()
    {
        Class<?> editor = ( ( PropertyDescriptor )descriptor ).getPropertyEditorClass();
        return editor;
        //return null;
    }

    @Override
    public String toString()
    {
        //return getDisplayName()+" : "+getValue();
        return getDisplayName();
    }

    /** Returns property short description. */
    public String getShortDescription()
    {
        return descriptor.getShortDescription();
    }

    /**
     * Returns property tool tip. Special method may be used to acees tooltip text,
     * otherwise it equals to shortDescription.
     */
    public String getToolTip()
    {
        String tooltip = stringFeature( BeanInfoEx.TOOLTIP );
        return tooltip != null ? tooltip : getShortDescription();
    }

    /**
     * Returns true if property is read only. If parent property is read only then all
     * child properties are read only too.
     */
    public boolean isReadOnly()
    {
        if ( parent != null && !(parent instanceof ArrayProperty) && parent.isReadOnly() ) { return true; }

        if ( parent instanceof ArrayProperty &&
             parent.booleanFeature( BeanInfoEx.CHILD_READ_ONLY ) )
           return true;

        return ( booleanFeature( BeanInfoEx.READ_ONLY ) ||
            ( ( PropertyDescriptor )descriptor ).getWriteMethod() == null );
    }

    public boolean isHideChildren()
    {
        return booleanFeature( BeanInfoEx.HIDE_CHILDREN );
    }


    ////////////////////////////////////////
    // Functions for TreeNode interface
    //
    public boolean isLeaf()
    {
        return getPropertyCount() == 0;
    }

    public boolean isSubstituteByChild()
    {
        return booleanFeature( BeanInfoEx.SUBSTITUTE_BY_CHILD );
    }

    public Dimension getEditorPreferredSize()
    {
        Dimension value = (Dimension)descriptor.getValue( BeanInfoEx.EDITOR_PREFERRED_SIZE );
        return value;
    }

    public void setEditorPreferredSize( Dimension dim )
    {
        descriptor.setValue( BeanInfoEx.EDITOR_PREFERRED_SIZE, dim );
    }

    abstract public int getPropertyCount();

    abstract public Property getPropertyAt( int i );

    /** Returns child property by name. */
    public Property findProperty( String name )
    {
        if ( name == null ) { return this; }

        if ( name.startsWith("../") )
        {
            if ( parent == null )
                return null;

            return parent.findProperty( name.substring(3) );
        }

        int index = name.indexOf( '/' );
        String prefix = ( index == -1 ) ? name : name.substring( 0, index );
        String suffix = ( index == -1 ) ? null : name.substring( index + 1 );
        for ( int i = 0; i < getPropertyCount(); i++ )
        {
            Property p = getPropertyAt( i );
            if ( p.getName().equals( prefix ) )
            {
                return suffix == null ? p : p.findProperty( suffix );
            }
        }
        return null;
    }

    ////////////////////////////////////////
    // Property visible issues
    //

    public static final int SHOW_USUAL     = 0;
    public static final int SHOW_EXPERT    = 1;
    public static final int SHOW_HIDDEN    = 2;
    public static final int SHOW_PREFERRED = 4;

    public boolean isVisible( int mode )
    {
        long start = System.currentTimeMillis();
        try
        {
            if ( ( ( descriptor.isHidden() || booleanFeature( BeanInfoEx.HIDDEN ) ) &&
                ( mode & SHOW_HIDDEN ) == 0 ) ||
                ( descriptor.isExpert() && ( mode & SHOW_EXPERT ) == 0 ) ||
                ( descriptor.isPreferred() && ( mode & SHOW_PREFERRED ) == 0 ) )
                { return false; }

            return true;
        }
        finally
        {
            if( System.currentTimeMillis() - start > 100 )
            {
                Logger.getLogger().error( "TOO SLOW isVisible()!" );
                Logger.getLogger().error( " - prop.name=" + getName() );
                Logger.getLogger().error( " - disp.name=" + getDisplayName() );
                Enumeration<?> en = getDescriptor().attributeNames();
                int count = 0;
                while( en.hasMoreElements() )
                {
                    en.nextElement();
                    count++;
                }
                Logger.getLogger().error( " - desc.size=" + count );
            }
        }
    }

    public int getVisibleCount( int mode )
    {
        int count = getPropertyCount();
        int visibleCount = 0;
        for ( int i = 0; i < count; i++ )
        {
            if ( getPropertyAt( i ).isVisible( mode ) ) { visibleCount++; }
        }
        return visibleCount;
    }

    /** return count of visible childs nodes. */
    public Property getVisiblePropertyAt( int index, int mode )
    {
        int count = getPropertyCount();
        int visibleCount = 0;
        for ( int i = 0; i < count; i++ )
        {
            Property p = getPropertyAt( i );
            if ( p.isVisible( mode ) )
            {
                if ( index == visibleCount ) { return p; }

                visibleCount++;
            }
        }

        return null;
    }

    ////////////////////////////////////////
    //
    // This functions are used to save & restore
    // the tree collaption state.
    //

    /** Indicates whether a property node is expanded. */
    protected boolean expanded = false;

    public void setExpanded( boolean expanded )
    {
        this.expanded = expanded;
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    public boolean isEnabled()
    {
        return true;
    }

    /**
     * This flag indicated that component fires property event itself
     * i.e. has <code>addPropertyChangeListener</code>.
     * Otherwise we will fire PropertyChangeEvents on component behalf.
     */
    protected boolean bComponentFiresPropertyEventsItself;

    /** @todo Implement this method */
    public Object[] getPathToRoot()
    {
        return null;
    }

    public String getNiceDescription()
    {
        String ret;
        String s = "";
        if ( parent != null )
        {
            s = "+";
            StringTokenizer st = new StringTokenizer( parent.getCompleteName(), "/" );
            for ( int i = 0; i < st.countTokens(); i++ )
            {
                s = "    " + s;
            }
        }
        try
        {
            ret = s + getName() + ": " + getValue() + " #" + this.getClass().getName() + "\n";
        }
        catch ( Exception ignore ) { ret = s + getName() + ": ???" + "\n"; }
        for ( int i = 0; i < getPropertyCount(); i++ )
        {
            String nice = getPropertyAt( i ).getNiceDescription();
            if ( !nice.endsWith( "\n" ) ) { nice += "\n"; }
            ret += nice;
        }
        return ret;
    }

    ////////////////////////////////////////
    // property change issues
    //

    protected EventListenerList listenerList;

    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        if( listenerList == null )
        {
            synchronized( this ) 
            {
                if( listenerList == null )
                {
                    listenerList = new EventListenerList();

                    PropertyChangeListener pcl = new PropertyChangeListener()
                    {
                        @Override
                        public void propertyChange( PropertyChangeEvent evt )
                        {
                            Property.this.propertyChange( evt );
                        }
                    };

                    // If a component implements some interface
                    // that has addPropertyChangeListener method
                    // it is capable to fire PropertyChangeEvents itself
                    // In this case we want to catch these events
                    // so we will be able preprocess them
                    bComponentFiresPropertyEventsItself = false;
                    if ( descriptor instanceof PropertyDescriptor )
                    {
                        // we use REAL owner not 'owner' since it can be wrapper
                        bComponentFiresPropertyEventsItself = invokeAddRemovePropertyChangeListenerMethod(
                            ownerForPropertyChanges, getName(), "addPropertyChangeListener", pcl );
                    }

                    if( parent != null && !parent.equals( listener ) )
                    {
                        listenerList.add( PropertyChangeListener.class, parent );
                    }
                }
            } //synchronized( this ) 
        }

        synchronized( listenerList ) 
        {
            PropertyChangeListener []previous = listenerList.getListeners( PropertyChangeListener.class );
         
            // it is == not equals to avoid adding same listener twice  
            if( java.util.Arrays.stream( previous ).noneMatch( e -> e == listener ) )
            {
                listenerList.add( PropertyChangeListener.class, listener );
            } 
        }
    }

    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
        if( listenerList != null )
            listenerList.remove( PropertyChangeListener.class, listener );
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        String origPropName = evt.getPropertyName();
        try
        {
            Class<?> ownerClass = owner.getClass();
            Class<?> ejbClass = Class.forName("javax.ejb.EJBObject");
            if(ejbClass.isAssignableFrom(ownerClass) && !bComponentFiresPropertyEventsItself)
            {
                Property model = ComponentFactory.getModel(owner, ComponentFactory.Policy.DEFAULT);
                if(model != null)
                {
                    model.firePropertyChange(new PropertyChangeEvent(evt.getSource(), "*", null, null));
                    return;
                }
            }
        }
        catch(Exception e)
        {
        }

        // First, hanlde the situations when we must
        // simply propagate the event

        // event fired by component itself
        if ( origPropName.equals(getName()) || origPropName.indexOf(getName()) >= 0 && getPropertyCount() == 0 )
        {
            // in this case we want to supply complete name
            int dot = origPropName.indexOf( "." );

            Object propagationId = evt.getPropagationId();
            evt = new PropertyChangeEvent( evt.getSource(),
                getCompleteName() + ( dot > 0 ? origPropName.substring( dot ) : "" ), evt.getOldValue(),
                evt.getNewValue() );
            evt.setPropagationId( propagationId );

            firePropertyChange( evt );
            return;
        }

        if ( getPropertyCount() > 0 ) // event propagated from child property
        {
            // simply relay it
            firePropertyChange( evt );
        }
    }

    protected void firePropertyChange( PropertyChangeEvent evt )
    {
        if( listenerList != null )
        {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for ( int i = listeners.length - 2; i >= 0; i -= 2 )
            {
                if ( listeners[ i ] == PropertyChangeListener.class )
                {
                    ( ( PropertyChangeListener )listeners[ i + 1 ] ).propertyChange( evt );
                }
            }
        }

        if( parent != null )
            parent.propagatePropertyChange(evt);
    }

    protected void propagatePropertyChange( PropertyChangeEvent evt )
    {
        if( listenerList != null )
        {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for ( int i = listeners.length - 2; i >= 0; i -= 2 )
            {
                if ( listeners[ i ] == PropertyChangeListener.class )
                {
                    ( ( PropertyChangeListener )listeners[ i + 1 ] ).propertyChange( evt );
                }
            }
        }

        if( parent != null )
        {
            evt = new PropertyChangeEvent(evt.getSource(), descriptor.getName() + "/" + evt.getPropertyName(),
                                          evt.getOldValue(), evt.getNewValue() );
            parent.propagatePropertyChange(evt);
        }
    }

    ////////////////////////////////////////
    //
    //

    // for the obfuscator
    static private PropWrapper classRetriever = new PropWrapper( null, null );

    static class DynValuePropertyDescriptor extends PropertyDescriptor
    {
        private final Object owner;
        private final Class<?> type;
        private final PropertyDescriptor orig;

        public DynValuePropertyDescriptor( Object owner, Class<?> type, PropertyDescriptor orig)
            throws IntrospectionException
            {
                super( orig.getName(), classRetriever.getClass(), "getValue", "setValue" );

                this.owner = owner;
                this.type  = type;
                this.orig = orig;

                // copy needed data from informant
                this.setExpert( orig.isExpert() );
                this.setPreferred( orig.isPreferred() );
                this.setBound(orig.isBound());
                this.setConstrained(orig.isConstrained());
                this.setPropertyEditorClass(orig.getPropertyEditorClass());
                this.setDisplayName( orig.getDisplayName() );
                this.setShortDescription( orig.getShortDescription() );
                Enumeration<?> e = orig.attributeNames();
                while ( e.hasMoreElements() )
                {
                    String atr = ( String )e.nextElement();
                    super.setValue( atr, orig.getValue( atr ) );
                }
        }

        @Override
        public boolean isHidden()
        {
            return orig.isHidden();
        }

        @Override
        public void setHidden(boolean value)
        {
            orig.setHidden( value );
        }

        @Override
        public Class<?> getPropertyType()
        {
            Object value = ( ( DynamicPropertySet )owner ).getValue( this.getName() );
            return value != null ? value.getClass() : type;
        }

    }


    static class DynValueIndexedPropertyDescriptor extends IndexedPropertyDescriptor
    {
        private Object owner = null;
        private Class<?> type = null;

        public DynValueIndexedPropertyDescriptor( Object owner, Class<?> type, IndexedPropertyDescriptor orig )
            throws IntrospectionException
            {
                super( orig.getName(), classRetriever.getClass(),
                    "getValue", "setValue", "getIndexedValue", "setIndexedValue" );
                this.owner = owner;
                this.type = type;

                // copy needed data from informant
                this.setExpert( orig.isExpert() );
                this.setHidden( orig.isHidden() );
                this.setPreferred( orig.isPreferred() );
                this.setDisplayName( orig.getDisplayName() );
                this.setShortDescription( orig.getShortDescription() );
                Enumeration<?> e = orig.attributeNames();
                while ( e.hasMoreElements() )
                {
                    String atr = ( String )e.nextElement();
                    super.setValue( atr, orig.getValue( atr ) );
                }
        }

        /** @todo high check it */
        @Override
        public Class<?> getPropertyType()
        {
            // we try to use "type" subclasses if value is available
            if ( owner != null )
            {
                Object value = ( ( DynamicPropertySet )owner ).getValue( this.getName() );
                if ( value != null && value.getClass().isArray() )
                    return value.getClass();
            }

            return type;
        }

        @Override
        public Class<?> getIndexedPropertyType()
        {
            return getPropertyType().getComponentType();
        }
    }


    /** The internal class for accessing dynamic values. */
    public static class PropWrapper
    {
        private final String name;
        private final Object owner;

        public PropWrapper( Object owner, String name )
        {
            this.owner = owner;
            this.name = name;
        }

        public DynamicPropertySet getOwner()
        {
            return ( DynamicPropertySet )owner;
        }

        public Object getValue()
        {
            return ( ( DynamicPropertySet )owner ).getValue( name );
        }

        public void setValue( Object value )
        {
            ( ( DynamicPropertySet )owner ).setValue( name, value );
        }

        public Object getIndexedValue( int index )
        {
            return Array.get( ( ( DynamicPropertySet )owner ).getValue( name ), index );
        }

        public void setIndexedValue( int index, Object value )
        {
            Array.set( ( ( DynamicPropertySet )owner ).getValue( name ), index, value );
        }
    }
}
