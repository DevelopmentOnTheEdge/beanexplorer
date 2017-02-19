package com.developmentontheedge.beans;

import java.awt.Dimension;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.NumberFormat;

/**
 * <code>PropertyDescriptorEx</code> is an extension of standard
 * java.beans.PropertyDescriptor to simplify definition of the components
 * according to BeanExplorer's new approaches.
 */
public class PropertyDescriptorEx extends PropertyDescriptor
implements IndexedPropertyConstants
{
    /**
     * Creates property descriptor for the property.
     * @param propertyName Name of the property.
     * @param beanClass Class of bean that contains this property.
     */
    public PropertyDescriptorEx( String propertyName, Class<?> beanClass )
        throws IntrospectionException
        {
            super( propertyName, beanClass );
    }

    /**
     * Creates property descriptor for the property with explicit getter and setter methods.
     * @param propertyName Name of the property.
     * @param beanClass Class of the bean that contains this property.
     * @param getterName Name of getter method for this property.
     * @param setterName Name of setter method for this property.
     */
    public PropertyDescriptorEx( String propertyName, Class<?> beanClass, String getterName, String setterName )
        throws IntrospectionException
        {
            super( propertyName, beanClass, getterName, setterName );
    }

    /**
     * Creates property descriptor for the property with explicit getter and setter methods.
     * @param propertyName Name of the property.
     * @param beanClass Class of the bean that contains this property.
     * @param getter Getter method for this property.
     * @param setter Setter method for this property.
     */
    public PropertyDescriptorEx( String propertyName, Method getter, Method setter )
        throws IntrospectionException
        {
            super( propertyName, getter, setter );
    }

    /** This constructor must be used only to create dynamic properties */
    public PropertyDescriptorEx( String propertyName ) throws IntrospectionException
    {
        super( propertyName, null, null );
    }

    ////////////////////////////////////////
    // Properties
    //

    /**
     * Sets context for the property in some help system defined by an application.
     * @param helpId Some identifier of help resource. This can be help message itself as well.
     * @see BeanInfoConstants#HELP_ID
     */
    public void setHelpId( String helpId )
    {
        setValue( BeanInfoConstants.HELP_ID, helpId );
    }

    /**
     * Indicates that property as 'read only'.
     * @param value Flag indicating whether property is 'read only' or not.
     */
    public void setReadOnly( boolean value )
    {
        setValue( BeanInfoConstants.READ_ONLY, 
            value ? Boolean.TRUE : Boolean.FALSE );            
    }

    /**
     * Sets a method which determines whether the property is 'read only'.
     * @param method Method which determines whether property is 'read only'.
     *               Signature of method: <code>boolean X()</code>
     * @see BeanInfoConstants#READ_ONLY
     */
    public void setReadOnly( Method method )
    {
        setValue( BeanInfoConstants.READ_ONLY, method );
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
     * Sets the initial value for the property when its getter method returns <code>null</code>.
     * @param value Default value of the property.
     * @see BeanInfoConstants#DEFAULT_VALUE
     */
    public void setDefaultValue( DefaultValue value )
    {
        setValue( BeanInfoConstants.DEFAULT_VALUE, value );
    }

    /**
     * Sets a method to generate initial value for the property when its getter method returns <code>null</code>.
     * @param value Method which generates default value of the property.
     * @see BeanInfoConstants#DEFAULT_VALUE
     */
    public void setDefaultValue( Method method )
    {
        setValue( BeanInfoConstants.DEFAULT_VALUE, method );
    }

    /**
     * Sets a method which generates format string for the property value.
     * @param method Method which generates format string for the property value.
     * @see BeanInfoConstants#FORMAT_MASK
     */
    public void setFormatMask(Method method)
    {
        setValue( BeanInfoConstants.FORMAT_MASK, method );
    }

    /**
     * Sets a method for generating display name of the property.
     * @param method Method for generating display name of the property.
     * @see BeanInfoConstants#DISPLAY_NAME
     * @see java.beans.FeatureDescriptor#setDisplayName(String)
     */
    public void setDisplayName( Method method )
    {
        setValue( BeanInfoConstants.DISPLAY_NAME, method );
    }

    /**
     * Sets a method for generating tooltip for the property.
     * @param method Method for generating tooltip for the property.
     * @see BeanInfoConstants#TOOLTIP
     */
    public void setToolTip( Method method )
    {
        setValue( BeanInfoConstants.TOOLTIP, method );
    }

    /**
     * Sets a flag for no recursion check.
     * This flag is inspired by classes like <code>java.awt.Dimension</code>
     * which contain property of the type <code>java.awt.Dimension</code>
     * thus causing endless loop while recursively introspecting the beans.
     * Normally we suppress recursion but in some situations this
     * screens out required properties. In such situation the developer
     * can use this flag to aviod recusrion suppresion, but he may be
     * required to write explicit BeanInfo for the class in this case.
     * @see BeanInfoConstants#NO_RECURSION_CHECK
     */
    public void setNoRecursionCheck(boolean noRecursionCheck)
    {
        setValue(BeanInfoConstants.NO_RECURSION_CHECK, noRecursionCheck);
    }

    /**
     * Indicates that the property can be <code>null</code>.
     * @param value Flag that indicates that the property can be <code>null</code>.
     * @see BeanInfoConstants#CAN_BE_NULL
     */
    public void setCanBeNull( boolean value )
    {
        setValue(BeanInfoConstants.CAN_BE_NULL, value);
    }

    /**
     * Sets a method for determining whether the property can be <code>null</code>.
     * @method method Method that determines whether the property can be <code>null</code>.
     * @see BeanInfoConstants#CAN_BE_NULL
     */
    public void setCanBeNull( Method method )
    {
        setValue( BeanInfoConstants.CAN_BE_NULL, method );
    }


    /**
     * This flag forces interpretation of composite properties as simple.
     * @see BeanInfoConstants#SIMPLE
     */
    public void setSimple( boolean value )
    {
        setValue(BeanInfoConstants.SIMPLE, value);
    }

    /**
     * Specifies whether the children of a composite property should be visible or not.
     * @param value Flag that specifies whether the children of composite property should be visible or not.
     * @see BeanInfoConstants#HIDE_CHILDREN
     */
    public void setHideChildren( boolean value )
    {
        setValue(BeanInfoEx.HIDE_CHILDREN, value);
    }

    /**
     * Sets a method for determining whether the children of composite property should be visible or not.
     * @param method Method that determines whether the children of composite property should be visible or not.
     * @see BeanInfoConstants#HIDE_CHILDREN
     */
    public void setHideChildren( Method method )
    {
        setValue( BeanInfoEx.HIDE_CHILDREN, method );
    }

    /**
     * Sets an icon for the property.
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
     * Sets a method for determining visibility of the property.
     * @param method Method for determining visibility of the property.
     * @see BeanInfoConstants#HIDDEN
     */
    public void setHidden(Method method)
    {
        setValue( BeanInfoConstants.HIDDEN, method );
    }

    /**
     * Sets up composite editor for the property.
     * @param propertyList List of property names for the composite editor.
     *  <p>Semicolon is used as a delimiter of property names.
     *  <p>Nested subproperties can be used i.e. properties can be designated via complete name.
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

    /**
     * This method allows the developer to specify preferred size
     * of the property when it is displayed in some visual UI
     * like PropertyInspector or DialogPropertyInspector
     * @param editorPeferredSize Preferred size of the property view.
     * @see BeanInfoConstants#EDITOR_PREFERRED_SIZE
     */
    public void setEditorPreferredSize(Dimension editorPreferredSize)
    {
        setValue(BeanInfoConstants.EDITOR_PREFERRED_SIZE, editorPreferredSize);
    }


    /**
     * Sets up number format to display property whose value is number.
     *
     * @param format - number format for property value.
     *
     * @see BeanInfoConstants#NUMBER_FORMAT
     * @see java.text.NumberFormat
     */
    public void setNumberFormat(NumberFormat format)
    {
        setValue( BeanInfoConstants.NUMBER_FORMAT, format);
    }

    /**
     * Sets up pattern for DecimalFormat to display property whose value is number.
     *
     * @param pattern - pattern for DecimalFormat to display property value.
     *
     * @see BeanInfoConstants#NUMBER_FORMAT
     * @see java.text.DecimalFormat
     */
    public void setNumberFormat(String pattern)
    {
        setValue( BeanInfoConstants.NUMBER_FORMAT, pattern);
    }

    ////////////////////////////////////////
    // Properties specific for arrays
    //

    /**
     * Specifies that number elements of the indexed property can be changed at run time.
     * @param value Flag which determines whether the number of elements of the indexed property can be changed at run time.
     * @see IndexedPropertyConstants#RESIZABLE
     */
    public void setResizable(Boolean value)
    {
        setValue(RESIZABLE, value);
    }

    /**
     * Sets a method which determines whether elements of the indexed property can be changed at run time.
     * @param m Method which determines whether elements of the indexed property can be changed at run time.
     * @see IndexedPropertyConstants#RESIZABLE
     */
    public void setResizable(Method m)
    {
        setValue(RESIZABLE, m);
    }

    /**
     * Sets a method which determines whether elements of the indexed property can be added at run time.
     * @param m Method which determines whether elements of the indexed property can be added at run time.
     * @see IndexedPropertyConstants#CAN_INSERT
     */
    public void setCanInsert(Method m)
    {
        setValue(CAN_INSERT, m);
    }

    /**
     * Sets a method which determines whether the order of the elements of the indexed property can be changed at run time.
     * @param m Method which determines whether the order of the elements of the indexed property can be changed at run time.
     * @see IndexedPropertyConstants#CAN_MOVE
     */
    public void setCanMove(Method m)
    {
        setValue(CAN_MOVE, m);
    }

    /**
     * Sets a method which determines whether elements of the indexed property can be removed at run time.
     * @param m Method which determines is elements of the indexed property can be removed at run time.
     * @see IndexedPropertyConstants#CAN_DELETE
     */
    public void setCanDelete(Method m)
    {
        setValue(CAN_DELETE, m);
    }

    /**
     * Sets a method for calculating display names for the elements of the indexed property.
     * @param method Method calculating the names.
     * @see BeanInfoConstants#CHILD_DISPLAY_NAME
     */
    public void setChildDisplayName(Method method)
    {
        setValue(BeanInfoEx.CHILD_DISPLAY_NAME, method);
    }
}