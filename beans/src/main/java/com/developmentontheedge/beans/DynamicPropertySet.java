package com.developmentontheedge.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

/**
 * This interface defines a bean to which properties can be added or removed dynamically. Any
 * class implementing this interface will be recognized by BeanExplorer as a dynamic bean
 */
public interface DynamicPropertySet extends Serializable, Cloneable, Iterable<DynamicProperty>, PropertyChangeObservable
{
    /**
     * @return type for the property with specified name.
     * @param name - name of the property which type is desired
     */
    Class<?> getType( String name );

    /**
     * @return value for the property with specified name.
     * @param name - name of the property which value is desired
     */
    Object getValue( String name );
    String getValueAsString( String name );
    Long getValueAsLong(String name);
    <T> T cast( String name, Class<T> clazz );

    /**
     * @param name Name of the property
     * @param value  New value of the property
     */
    void setValue( String name, Object value );

    /**
     * Returns standard property descriptor for the property referenced via its name
     * @param name Name of the property
     * @return Value of the property descriptor
     */
    PropertyDescriptor getPropertyDescriptor( String name );

    void setPropertyAttribute( String propName, String attrName, Object attrValue );

    /**
     * Returns property referenced via its name
     * @param name Name of the property
     */
    DynamicProperty getProperty( String name );

    /**
     * Returns true if contains property with specified name.
     * @param name Name of the property
     */
    default boolean hasProperty( String name )
    {
        return getProperty( name ) != null;
    }

    /**
     * Renames property
     * @param from Name of the property
     * @param to Name of the property
     */
    void renameProperty( String from, String to );

    /**
     * Adds new property to the dynamic bean
     */
    void add( DynamicProperty property );

    /**
     * Adds new property and place it before property with name=propName
     * @return true if property propName exist. If not, the property is added to the end.  
     */
    boolean addBefore( String propName, DynamicProperty property );

    /** Adds new property and place it after property with name=propName
     * @return true if property propName exist. If not, the property is added to the end.
     */
    boolean addAfter(String propName, DynamicProperty property );


    /** Checks if property does exist in this set */
    boolean contains( DynamicProperty property );

    /**
     * Removes property from the bean
     * @return value of removed property if successful, otherwise null
     */
    Object remove( String name );

    /**
     * Moves property to the designated position
     * @return true if property was moved sucessfully
     */
    boolean moveTo( String name, int index );

    /**
     * Replaces named property with new one
     * @return true if property was replaced sucessfully
     */
    boolean replaceWith( String name, DynamicProperty prop );

    /**
     * Produces iterator through names of all properties of this bean. Iterated values
     * have a String type
     */
    Iterator<String> nameIterator();

    /**
     * Produces iterator through all the properties of this bean. Iterated values
     * have a DynamicProperty type
     * @see com.developmentontheedge.beans.DynamicProperty
     */
    Iterator<DynamicProperty> propertyIterator();

    Iterator<DynamicProperty> iterator();

    /**
     * Returns an unmodifiable view of this set as a map.
     */
    Map<String, Object> asMap();

    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     * @param listener  The PropertyChangeListener to be added
     */
    @Override
    void addPropertyChangeListener( PropertyChangeListener listener );

    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */
    void addPropertyChangeListener( String propertyName, PropertyChangeListener listener );

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     * @param listener  The PropertyChangeListener to be removed
     */
    @Override
    void removePropertyChangeListener( PropertyChangeListener listener );

    /**
     * Remove a PropertyChangeListener for a specific property.
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener( String propertyName, PropertyChangeListener listener );

    /**
     * Report a bound property update to any registered listeners.
     * No event is fired if old and new are equal and non-null.
     * @param propertyName  The programmatic name of the property
     *          that was changed.
     * @param oldValue  The old value of the property.
     * @param newValue  The new value of the property.
     */
    void firePropertyChange( String propertyName, Object oldValue, Object newValue );

    /**
     * Fire an existing PropertyChangeEvent to any registered listeners.
     * No event is fired if the given event's old and new values are
     * equal and non-null.
     * @param evt  The PropertyChangeEvent object.
     */
    void firePropertyChange( PropertyChangeEvent evt );

    /**
     * Check if there are any listeners for a specific property.
     * @param propertyName  the property name.
     * @return true if there are ore or more listeners for the given property
     */
    boolean hasListeners( String propertyName );

    /**
     * Returns properties count.
     */
    int size();

    /** Checks if DPS contains any element */
    boolean isEmpty();

    DynamicPropertyBuilder getAsBuilder(String name);

    Object clone();

    String serializeAsXml( String beanName, String offset );
}