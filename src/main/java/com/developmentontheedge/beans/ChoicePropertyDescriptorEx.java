package com.developmentontheedge.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;

import com.developmentontheedge.beans.editors.TagEditorSupport;

/**
 * This property descriptor simplify process of creating property with tag property editor.
 * @see TagEditorSupport
 */
public class ChoicePropertyDescriptorEx extends PropertyDescriptorEx
{
    protected PropertyEditor tagPropertyEditor;

    /** Creates type name using data from tagPropertyEditor. */
    protected void init(Class<?> tagPropertyEditorClass)
    {
        setPropertyEditorClass( tagPropertyEditorClass );
    }

    ////////////////////////////////////////
    // constructors
    //
    public ChoicePropertyDescriptorEx(String propertyName, Class<?> beanClass, Class<?> tagPropertyEditor) throws IntrospectionException
    {
        super( propertyName, beanClass );
        init( tagPropertyEditor );
    }

    public ChoicePropertyDescriptorEx(String propertyName, Class<?> beanClass, Class<?> tagPropertyEditor, String getterName,
            String setterName) throws IntrospectionException
    {
        super( propertyName, beanClass, getterName, setterName );
        init( tagPropertyEditor );
    }

    public ChoicePropertyDescriptorEx(String propertyName, Class<?> tagPropertyEditor, Method getter, Method setter)
            throws IntrospectionException
    {
        super( propertyName, getter, setter );
        init( tagPropertyEditor );
    }
}
