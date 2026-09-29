package com.developmentontheedge.beans.model;

import java.lang.ref.WeakReference;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * {@link IdentityModelCache} in isolation: identity keys, weak keys, and
 * entries that survive GC while their bean is alive.
 */
public class IdentityModelCacheTest
{
    /** equals() everything, so any equals()-based lookup would conflate. */
    public static class AlwaysEqualBean
    {
        private final String name;

        public AlwaysEqualBean(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj instanceof AlwaysEqualBean;
        }

        @Override
        public int hashCode()
        {
            return 42;
        }
    }

    private static ComponentModel modelFor(Object bean)
    {
        return ComponentFactory.getModel( bean, ComponentFactory.Policy.UI, true );
    }

    @Test
    public void keysAreComparedByIdentity()
    {
        IdentityModelCache cache = new IdentityModelCache();
        AlwaysEqualBean bean1 = new AlwaysEqualBean( "first" );
        AlwaysEqualBean bean2 = new AlwaysEqualBean( "second" );
        ComponentModel model1 = modelFor( bean1 );

        cache.put( bean1, model1 );

        assertSame( model1, cache.get( bean1 ) );
        assertNull( cache.get( bean2 ) );

        ComponentModel model2 = modelFor( bean2 );
        cache.put( bean2, model2 );

        assertSame( model1, cache.get( bean1 ) );
        assertSame( model2, cache.get( bean2 ) );
        assertEquals( 2, cache.size() );
    }

    @Test
    public void putReplacesTheModelForTheSameBean()
    {
        IdentityModelCache cache = new IdentityModelCache();
        AlwaysEqualBean bean = new AlwaysEqualBean( "bean" );
        ComponentModel oldModel = modelFor( bean );
        ComponentModel newModel = modelFor( bean );

        cache.put( bean, oldModel );
        cache.put( bean, newModel );

        assertSame( newModel, cache.get( bean ) );
        assertEquals( 1, cache.size() );
    }

    @Test
    public void nullBeanIsNeverCached()
    {
        IdentityModelCache cache = new IdentityModelCache();
        cache.put( null, modelFor( new AlwaysEqualBean( "x" ) ) );

        assertNull( cache.get( null ) );
        assertEquals( 0, cache.size() );
    }

    /**
     * Guards against wrapping the bean in an identity key and storing the
     * wrapper in a WeakHashMap: the map would hold only the wrapper weakly,
     * and the entry would vanish at the first GC while bean and model are
     * both still in use.
     */
    @Test
    public void entrySurvivesGcWhileBeanAndModelAreAlive()
    {
        IdentityModelCache cache = new IdentityModelCache();
        AlwaysEqualBean bean = new AlwaysEqualBean( "alive" );
        ComponentModel model = modelFor( bean );
        cache.put( bean, model );

        for( int i = 0; i < 5; i++ )
            System.gc();

        assertSame( model, cache.get( bean ) );
        assertEquals( 1, cache.size() );
    }

    @Test
    public void entryIsReleasedWhenTheBeanIsCollected()
    {
        IdentityModelCache cache = new IdentityModelCache();
        WeakReference<Object> beanRef = putUnreachableBean( cache );
        assertEquals( 1, cache.size() );

        assertTrue( "bean was retained by the cache", ComponentFactoryCacheTest.awaitCleared( beanRef ) );
        assertEquals( 0, cache.size() );
    }

    private static WeakReference<Object> putUnreachableBean(IdentityModelCache cache)
    {
        AlwaysEqualBean bean = new AlwaysEqualBean( "gone" );
        cache.put( bean, modelFor( bean ) );
        return new WeakReference<>( bean );
    }
}
