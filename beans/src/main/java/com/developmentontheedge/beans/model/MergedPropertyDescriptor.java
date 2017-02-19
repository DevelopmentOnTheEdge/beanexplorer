package com.developmentontheedge.beans.model;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Enumeration;

public class MergedPropertyDescriptor extends PropertyDescriptor implements InternalConstants
{
    private PropertyDescriptor propertyDescriptor;
    private BeanDescriptor beanDescriptor;
    private boolean isDynamic;

    public MergedPropertyDescriptor(PropertyDescriptor propertyDescriptor, BeanDescriptor beanDescriptor, boolean isDynamic)
            throws IntrospectionException
    {
        super( propertyDescriptor.getName(), propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod() );
        this.propertyDescriptor = propertyDescriptor;
        this.beanDescriptor = beanDescriptor;
        this.isDynamic = isDynamic;
        
        Enumeration<String> e = beanDescriptor.attributeNames();
        while(e.hasMoreElements())
        {
            String attr = e.nextElement();
            setValue( attr, beanDescriptor.getValue( attr ) );
        }
        e = propertyDescriptor.attributeNames();
        while(e.hasMoreElements())
        {
            String attr = e.nextElement();
            setValue( attr, propertyDescriptor.getValue( attr ) );
        }
        setValue( BEAN_DESCRIPTOR, beanDescriptor );
        setValue( PROPERTY_DESCRIPTOR, propertyDescriptor );
    }

    @Override
    public boolean isBound()
    {
        return propertyDescriptor.isBound();
    }

    @Override
    public void setBound(boolean bound)
    {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean isHidden()
    {
        return propertyDescriptor.isHidden();
    }

    @Override
    public void setHidden(boolean hidden)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConstrained()
    {
        return propertyDescriptor.isConstrained();
    }

    @Override
    public void setConstrained(boolean constrained)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isExpert()
    {
        return propertyDescriptor.isExpert();
    }

    @Override
    public boolean isPreferred()
    {
        return propertyDescriptor.isPreferred();
    }

    @Override
    public void setPreferred(boolean preferred)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDisplayName()
    {
        if( !isDynamic && propertyDescriptor.getDisplayName().equals( propertyDescriptor.getName() )
                && !beanDescriptor.getDisplayName().equals( beanDescriptor.getName() ))
            return beanDescriptor.getDisplayName();
        return propertyDescriptor.getDisplayName();
    }
    
    @Override
    public void setDisplayName(String displayName)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String getShortDescription()
    {
        if( propertyDescriptor.getShortDescription().equals( propertyDescriptor.getName() )
                && !beanDescriptor.getShortDescription().equals( beanDescriptor.getName() ) )
            return beanDescriptor.getShortDescription();
        return propertyDescriptor.getShortDescription();
    }
    
    @Override
    public void setShortDescription(String text)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Class<?> getPropertyEditorClass()
    {
        if( propertyDescriptor.getPropertyEditorClass() != null )
                            {
            return propertyDescriptor.getPropertyEditorClass();
        }
        else
        {
            Class<?> editor = (Class<?>)beanDescriptor.getValue( BEAN_EDITOR_CLASS );
            if( editor == null )
            {
                Class<?> cc = beanDescriptor.getCustomizerClass();
                if( cc != null && PropertyEditor.class.isAssignableFrom( cc ) )
                {
                    editor = cc;
                    beanDescriptor.setValue( BEAN_EDITOR_CLASS, editor );
                }
            }
            return editor;
        }

    }
    
    @Override
    public void setPropertyEditorClass(Class<?> propertyEditorClass)
    {
        throw new UnsupportedOperationException();
    }
    
}
