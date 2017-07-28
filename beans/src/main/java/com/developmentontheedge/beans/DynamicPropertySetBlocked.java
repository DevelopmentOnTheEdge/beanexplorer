package com.developmentontheedge.beans;

import java.util.Iterator;

/**
 * Dynamic property set with immutable properties.
 * Be careful, property value object can be mutable.
 * @author ruslan
 *
 */
@SuppressWarnings("serial")
public class DynamicPropertySetBlocked extends DynamicPropertySetDecorator {

    String MODIFICATION_EXCEPTION_MESSAGE = "You can't use this operation for unmodifiable class";
    public DynamicPropertySetBlocked(DynamicPropertySet delegateDps )
    {
        super( delegateDps );
    }
    
    protected void setValueHidden( String name, Object value ) 
    {
        super.setValue( name, value );
    }
    
    @Override
    public void setValue( String name, Object value ) 
    {
        throw new UnsupportedOperationException( MODIFICATION_EXCEPTION_MESSAGE );
    }

    @Override
    public void setPropertyAttribute( String propName, String attrName, Object attrValue ) 
    {
        throw new UnsupportedOperationException( MODIFICATION_EXCEPTION_MESSAGE );
    }

    @Override
    public DynamicProperty getProperty( String name ) 
    {
        try 
        {
            DynamicProperty orig = super.getProperty( name );
            return orig != null ? DynamicPropertySetSupport.cloneProperty( orig ) : null;
        } 
        catch( Exception e ) 
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void renameProperty( String from, String to ) 
    {
        throw new UnsupportedOperationException( MODIFICATION_EXCEPTION_MESSAGE );
    }

    @Override
    public void add( DynamicProperty property ) 
    {
        throw new UnsupportedOperationException( MODIFICATION_EXCEPTION_MESSAGE );
    }

    @Override
    public boolean addBefore( String propName, DynamicProperty property ) 
    {
        throw new UnsupportedOperationException( MODIFICATION_EXCEPTION_MESSAGE );
    }

    @Override
    public boolean addAfter( String propName, DynamicProperty property ) 
    {
        throw new UnsupportedOperationException( MODIFICATION_EXCEPTION_MESSAGE );
    }
    
    @Override
    public Object remove( String name ) 
    {
        throw new UnsupportedOperationException( MODIFICATION_EXCEPTION_MESSAGE );
    }
    
    @Override
    public boolean replaceWith( String name, DynamicProperty prop ) 
    {
        throw new UnsupportedOperationException( MODIFICATION_EXCEPTION_MESSAGE );
    }

    @Override
    public Iterator<DynamicProperty> propertyIterator() 
    {
        return immutableIterator( super.propertyIterator() );
    }

    @Override
    public Iterator<DynamicProperty> iterator() 
    {
        return immutableIterator( super.iterator() );
    }
    
    private static <T> Iterator<T> immutableIterator(final Iterator<T> iterator)
    {
        return new Iterator<T>()
        {
            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public T next()
            {
                return iterator.next();
            }
        };
    }
}
