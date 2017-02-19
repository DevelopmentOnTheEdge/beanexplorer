package com.developmentontheedge.beans;

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;

public class IndexedPropertyDescriptorEx extends IndexedPropertyDescriptor
implements IndexedPropertyConstants
{
    /**
     * Creates property descriptor for the property.
     * @param propertyName name of the property.
     * @param beanClass class of the bean that contains this property.
     */
    public IndexedPropertyDescriptorEx(String propertyName, Class<?> beanClass)
    throws IntrospectionException
    {
        super(propertyName, beanClass);
    }

    /**
     * Creates property descriptor for the property with explicit getter and setter methods.
     * @param propertyName name of the property.
     * @param beanClass class of the bean that contains this property.
     * @param getterName name of getter method for this property.
     * @param setterName name of setter method for this property.
     * @param indexedGetterName name of indexed getter method for this property.
     * @param indexedSetterName name of indexed setter method for this property.
     */
    public IndexedPropertyDescriptorEx(String propertyName, Class<?> beanClass, String getterName,
    String setterName, String indexedGetterName, String indexedSetterName)
    throws IntrospectionException
    {
        super(propertyName, beanClass, getterName, setterName, indexedGetterName,
        indexedSetterName);
    }

    /**
     * Creates property descriptor for the property with explicit getter and setter methods.
     * @param propertyName name of the property.
     * @param beanClass class of the bean contains this property.
     * @param getter getter method for this property.
     * @param setter setter method for this property.
     * @param indexedGetter indexed getter method for this property.
     * @param indexedSetter indexed setter method for this property.
     */
    public IndexedPropertyDescriptorEx(String propertyName, Method getter, Method setter,
    Method indexedGetter, Method indexedSetter) throws IntrospectionException
    {
        super(propertyName, getter, setter, indexedGetter, indexedSetter);
    }

    /** This constructor must be used only to create dynamic properties.
     *  See {@link DynamicProperty} for details
     */
    public IndexedPropertyDescriptorEx(String propertyName) throws IntrospectionException
    {
        super(propertyName, null, null, null, null);
    }

    ////////////////////////////////////////
    // Common properties wih PropertyDescriptorEx
    //

    /**
     * Sets a context for the property in some application-defined help system.
     * @param helpId some identifier of help resource or help message itself as well.
     * @see BeanInfoConstants#HELP_ID
     */
    public void setHelpId( String helpId )
    {
        setValue( BeanInfoEx.HELP_ID, helpId );
    }

    /**
     * Indicates that property is 'read only'.
     * @param value value of the attribute.
     */
    public void setReadOnly( boolean value )
    {
        setValue( BeanInfoEx.READ_ONLY, value );
    }

    /**
     * Sets a method which determines whether the property is 'read only'.
     * @param method method which determines whether the property is 'read only'.
     *               Signature of method: <code>boolean X()</code>
     * @see BeanInfoConstants#READ_ONLY
     */
    public void setReadOnly( Method method )
    {
        setValue( BeanInfoEx.READ_ONLY, method );
    }

    /**
     * Indicates that array items are read only and can not be edited.
     *
     * @param value Flag indicating whether array items are 'read only' or not.
     * @see BeanInfoConstants#CHILD_READ_ONLY
     */
    public void setChildReadOnly( boolean value )
    {
        setValue( BeanInfoConstants.CHILD_READ_ONLY, value );
    }

    /**
     * Sets initial value for the property when its getter method returns <code>null</code>.
     * @param value Default value of the property.
     * @see BeanInfoConstants#DEFAULT_VALUE
     */
    public void setDefaultValue( Object value )
    {
        setValue( BeanInfoEx.DEFAULT_VALUE, value );
    }

    /**
     * Sets a method for generating initial value for the property when its getter method returns <code>null</code>.
     * @param value Method which generates default value of the property.
     * @see BeanInfoConstants#DEFAULT_VALUE
     */
    public void setDefaultValue( Method method )
    {
        setValue( BeanInfoEx.DEFAULT_VALUE, method );
    }

    /**
     * Sets a method which generates format string for the property value.
     * @param method method which generates format string for the property value.
     * @see BeanInfoConstants#FORMAT_MASK
     */
    public void setFormatMask(Method method)
    {
        setValue( BeanInfoEx.FORMAT_MASK, method );
    }

    /**
     * Sets a method for generating display name of the property.
     * @param method method for generating display name of the property.
     * @see BeanInfoConstants#DISPLAY_NAME
     */
    public void setDisplayName( Method method )
    {
        setValue( BeanInfoEx.DISPLAY_NAME, method );
    }

    /**
     * Sets a method for generating tooltip for the property.
     * @param method method for generating tooltip for the property.
     * @see BeanInfoConstants#TOOLTIP
     */
    public void setToolTip( Method method )
    {
        setValue( BeanInfoEx.TOOLTIP, method );
    }

    /**
     * Sets flag for no recursion check.
     * This flag is inspired by classes like <code>java.awt.Dimension</code>
     * which contain property of the type <code>java.awt.Dimension</code>
     * thus causing endless loop while recursively introspecting beans.
     * Normally we suppress recursion but in some situations this
     * screens out required properties. In such situation the developer
     * can use this flag to aviod recusrion suppresion, but he may be
     * required to write BeanInfo for the class in this case.
     * @see BeanInfoConstants#NO_RECURSION_CHECK
     */
    public void setNoRecursionCheck(boolean noRecursionCheck)
    {
        setValue(BeanInfoEx.NO_RECURSION_CHECK, noRecursionCheck);
    }

    /**
     * Specifies whether the property can have a <code>null</code> value.
     * @param value value of the flag
     * @see BeanInfoConstants#CAN_BE_NULL
     */
    public void setCanBeNull( boolean value )
    {
        setValue(BeanInfoEx.CAN_BE_NULL, value);
    }

    /**
     * Forces interpretation of the property as simple
     * @param value flag disabling recursive introspection.
     * @see BeanInfoConstants#SIMPLE
     */
    public void setSimple( boolean value )
    {
        setValue(BeanInfoEx.SIMPLE, value);
    }

    /**
     * Specifies whether the children of array property should be visible or not.
     * @param value flag hiding the children
     * @see BeanInfoConstants#HIDE_CHILDREN
     */
    public void setHideChildren( boolean value )
    {
        setValue(BeanInfoEx.HIDE_CHILDREN, value);
    }

    /**
     * Sets a method for determining whether the children of array property should be visible or not.
     * @param method method calculation the value.
     * @see BeanInfoConstants#HIDE_CHILDREN
     */
    public void setHideChildren( Method method )
    {
        setValue( BeanInfoEx.HIDE_CHILDREN, method );
    }

    /**
     * Sets an icon for property.
     * @param type Type of icon. Constant from {@link java.beans.BeanInfo}.
     * @param icon Icon for property.
     * @see java.beans.BeanInfo#ICON_COLOR_16x16
     * @see java.beans.BeanInfo#ICON_COLOR_32x32
     * @see java.beans.BeanInfo#ICON_MONO_16x16
     * @see java.beans.BeanInfo#ICON_MONO_32x32
     */
    public void setNodeIcon( int type, java.awt.Image icon )
    {
        switch ( type )
        {
            case BeanInfo.ICON_COLOR_16x16:
                setValue( BeanInfoEx.NODE_ICON_COLOR_16x16, icon );
                break;
            case BeanInfo.ICON_COLOR_32x32:
                setValue( BeanInfoEx.NODE_ICON_COLOR_32x32, icon );
                break;
            case BeanInfo.ICON_MONO_16x16:
                setValue( BeanInfoEx.NODE_ICON_MONO_16x16, icon );
                break;
            case BeanInfo.ICON_MONO_32x32:
                setValue( BeanInfoEx.NODE_ICON_MONO_32x32, icon );
                break;
            default:
                break;
        }
    }

    /**
     * Sets a method for determining visibility of property.
     * @param method method for determining visibility of property.
     * @see BeanInfoConstants#HIDDEN
     */
    public void setHidden(Method method)
    {
        setValue( BeanInfoEx.HIDDEN, method );
    }

    /**
     * Sets up a composite editor for the property.
     * @param propertyList List of property names for composite editor.
     *  <p>Semicolon is used as a delimiter of property names.
     *  <p>Nested subproperties can be used i.e. properties can be referenced via complete name.
     *  <p> <i>Examples:</i>
     *  <ul>
     *      <li><code>"color;width;stroke"</code></li>
     *      <li><code>"pen/color;pen/width;brush/stroke"</code></li>
     *  </ul>
     * @param layoutManager Layout manager to arrange property renderers/editors inside the composite editor.
     *  <p> Currently meaningful layout managers are only:
     *  <code>GridLayout</code> and <code>FlowLayout</code>.
     * @see BeanInfoConstants#COMPOSITE_EDITOR_PROPERTY_LIST
     * @see BeanInfoConstants#COMPOSITE_EDITOR_LAYOUT_MANAGER
     */
    public void setCompositeEditor(String propertyNameList, java.awt.LayoutManager layoutManager)
    {
        setValue(BeanInfoConstants.COMPOSITE_EDITOR_PROPERTY_LIST,  propertyNameList);
        setValue(BeanInfoConstants.COMPOSITE_EDITOR_LAYOUT_MANAGER, layoutManager);
    }

    ////////////////////////////////////////
    // Properties specific for arrays
    //

    /**
     * Specifies that number of elements of the indexed property can be changed at run time.
     * @param value value of the attribute.
     * @see IndexedPropertyConstants#RESIZABLE
     */
    public void setResizable(Boolean value)
    {
        setValue(RESIZABLE, value);
    }

    /**
     * Sets a method which determines whether the number of elements of the indexed property can be changed at run time.
     * @param m method to calculate a value.
     * @see IndexedPropertyConstants#RESIZABLE
     */
    public void setResizable(Method m)
    {
        setValue(RESIZABLE, m);
    }

    /**
     * Sets a method which determines whether elements can be added the indexed property at run time.
     * @param m method to calculate a value.
     * @see IndexedPropertyConstants#CAN_INSERT
     */
    public void setCanInsert(Method m)
    {
        setValue(CAN_INSERT, m);
    }

    /**
     * Sets a method which determines whether the order of elements of the indexed property can be changed at run time.
     * @param m method to calculate a value.
     * @see IndexedPropertyConstants#CAN_MOVE
     */
    public void setCanMove(Method m)
    {
        setValue(CAN_MOVE, m);
    }

    /**
     * Sets a method which determines whther elements can be removed from the indexed property at run time.
     * @param m method to calculate a value.
     * @see IndexedPropertyConstants#CAN_DELETE
     */
    public void setCanDelete(Method m)
    {
        setValue(CAN_DELETE, m);
    }

    /**
     * Sets a method for generating display names for the elements of the indexed property.
     * @param method method to calculate a display name.
     * @see BeanInfoConstants#CHILD_DISPLAY_NAME
     */
    public void setChildDisplayName(Method method)
    {
        setValue(BeanInfoEx.CHILD_DISPLAY_NAME, method);
    }
}