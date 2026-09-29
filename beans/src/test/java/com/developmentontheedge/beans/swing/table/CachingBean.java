package com.developmentontheedge.beans.swing.table;

/**
 * A value-based bean used by {@link BeanTableModelAdapterCacheTest}.
 * <p>Two distinct instances are {@code equals()} (and have the same
 * {@code hashCode()}) when they share the same {@code id}, but carry
 * different {@code value}s. This lets the test verify the adapter's cache
 * is keyed by identity: a cache keyed by {@code equals()}/{@code hashCode()}
 * would conflate the two equal beans and one row would show the other's
 * value.
 * <p>Declared as a top-level {@code public} class (not a package-private
 * nested class) because ComponentFactory, which lives in a different
 * package, invokes the bean's read method reflectively when it builds a
 * model. Invoking a public method of a non-public class from another
 * package throws IllegalAccessException unless the method is made
 * accessible, so the property would be dropped from the model.
 */
public class CachingBean
{
    private final String id;
    private final String value;

    public CachingBean( String id, String value )
    {
        this.id = id;
        this.value = value;
    }

    public String getStringProperty()
    {
        return value;
    }

    public void setStringProperty( String value )
    {
        // immutable value; nothing to do
    }

    @Override
    public boolean equals( Object obj )
    {
        return obj instanceof CachingBean && ( (CachingBean)obj ).id.equals( id );
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}
