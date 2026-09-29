package com.developmentontheedge.beans.model;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Tests {@link Property#invokeAddRemovePropertyChangeListenerMethod} and its
 * per-class {@code ClassValue} cache.
 * <p>Covers the three bean shapes: one with the named
 * {@code (String, PropertyChangeListener)} method, one with only the plain
 * {@code (PropertyChangeListener)} method, and one with neither. Each
 * registers twice so the second call hits the cache, and asserts the
 * listener is actually invoked (i.e. the cached {@code Method} is the
 * correct one for the bean's class). Also asserts that the cache is hit
 * (same array returned) and that entries are keyed per method name.
 */
public class ListenerMethodCacheTest
{
    private static final PropertyChangeListener NOOP = new PropertyChangeListener()
    {
        @Override
        public void propertyChange( PropertyChangeEvent evt )
        {
        }
    };

    /** Bean that records listener registrations via the named overload. */
    static class NamedBean
    {
        static final List<String> calls = new ArrayList<>();

        public void addPropertyChangeListener( String prop, PropertyChangeListener l )
        {
            calls.add( "named:" + prop );
        }

        public void removePropertyChangeListener( String prop, PropertyChangeListener l )
        {
        }
    }

    /** Bean that records listener registrations via the plain overload. */
    static class PlainBean
    {
        static final List<String> calls = new ArrayList<>();

        public void addPropertyChangeListener( PropertyChangeListener l )
        {
            calls.add( "plain" );
        }

        public void removePropertyChangeListener( PropertyChangeListener l )
        {
        }
    }

    /** Bean with neither overload — the negative-result path. */
    static class NoListenerBean
    {
    }

    /**
     * Bean with the named {@code add} overload but only the plain
     * {@code remove} overload. Used to pin down that cache entries are
     * keyed per method name: {@code add} and {@code remove} must get
     * separate entries.
     */
    static class MixedBean
    {
        static final List<String> calls = new ArrayList<>();

        public void addPropertyChangeListener( String prop, PropertyChangeListener l )
        {
            calls.add( "add:" + prop );
        }

        public void removePropertyChangeListener( PropertyChangeListener l )
        {
            calls.add( "remove" );
        }
    }

    @Test
    public void testNamedMethod()
    {
        NamedBean.calls.clear();
        Object bean = new NamedBean();

        // First call: resolves and caches. Registers the listener 6 times
        // (base name + 5 event-suffix names).
        boolean firer1 = Property.invokeAddRemovePropertyChangeListenerMethod(
                bean, "p", "addPropertyChangeListener", NOOP );
        assertTrue( "named overload should be found", firer1 );
        assertEquals( 6, NamedBean.calls.size() );
        assertEquals( "named:p", NamedBean.calls.get( 0 ) );

        // Second call: cache hit. Must still invoke the same (correct) method.
        NamedBean.calls.clear();
        boolean firer2 = Property.invokeAddRemovePropertyChangeListenerMethod(
                bean, "q", "addPropertyChangeListener", NOOP );
        assertTrue( "named overload should still be found on cache hit", firer2 );
        assertEquals( 6, NamedBean.calls.size() );
        assertEquals( "named:q", NamedBean.calls.get( 0 ) );
    }

    @Test
    public void testPlainMethod()
    {
        PlainBean.calls.clear();
        Object bean = new PlainBean();

        // No (String, PCL) overload, so the named lookup is negative; the
        // plain (PCL) overload is found and invoked.
        boolean firer1 = Property.invokeAddRemovePropertyChangeListenerMethod(
                bean, "p", "addPropertyChangeListener", NOOP );
        assertTrue( "plain overload should be found", firer1 );
        assertEquals( 1, PlainBean.calls.size() );
        assertEquals( "plain", PlainBean.calls.get( 0 ) );

        // Second call: cache hit on both the negative named result and the
        // positive plain result. Must still invoke the plain method.
        PlainBean.calls.clear();
        boolean firer2 = Property.invokeAddRemovePropertyChangeListenerMethod(
                bean, "q", "addPropertyChangeListener", NOOP );
        assertTrue( "plain overload should still be found on cache hit", firer2 );
        assertEquals( 1, PlainBean.calls.size() );
        assertEquals( "plain", PlainBean.calls.get( 0 ) );
    }

    @Test
    public void testNoMethod()
    {
        Object bean = new NoListenerBean();

        // Neither overload exists; both lookups are negative (cached).
        boolean firer1 = Property.invokeAddRemovePropertyChangeListenerMethod(
                bean, "p", "addPropertyChangeListener", NOOP );
        assertFalse( "no overload should be found", firer1 );

        // Second call: cache hit on the negative results. Must still return
        // false (and not throw).
        boolean firer2 = Property.invokeAddRemovePropertyChangeListenerMethod(
                bean, "q", "addPropertyChangeListener", NOOP );
        assertFalse( "no overload should still not be found on cache hit", firer2 );
    }

    @Test
    public void testCacheIsHit()
    {
        // Two lookups for the same (class, name) must return the same
        // cached array — proving the cache is actually used.
        Method[] first = Property.listenerMethods( PlainBean.class, "addPropertyChangeListener" );
        Method[] second = Property.listenerMethods( PlainBean.class, "addPropertyChangeListener" );
        assertSame( "second lookup should hit the cache", first, second );
    }

    @Test
    public void testPerNameKeying()
    {
        // MixedBean has the named add overload but only the plain remove
        // overload. The two method names must get separate cache entries,
        // each with the correct shape for that name.
        Method[] add = Property.listenerMethods( MixedBean.class, "addPropertyChangeListener" );
        Method[] remove = Property.listenerMethods( MixedBean.class, "removePropertyChangeListener" );

        assertNotSame( "add and remove should be separate entries", add, remove );
        // add: named overload present, plain overload absent.
        assertNotNull( "add should resolve the named overload", add[0] );
        assertNull( "add should not resolve the plain overload", add[1] );
        // remove: named overload absent, plain overload present.
        assertNull( "remove should not resolve the named overload", remove[0] );
        assertNotNull( "remove should resolve the plain overload", remove[1] );
    }
}
