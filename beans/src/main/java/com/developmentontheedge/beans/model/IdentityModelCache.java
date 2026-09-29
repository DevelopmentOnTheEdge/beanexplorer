package com.developmentontheedge.beans.model;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache of {@link ComponentModel}s used by {@link ComponentFactory}, keyed by
 * bean <em>identity</em> and holding both the bean and the model weakly.
 *
 * <p>Two properties matter here:
 * <ul>
 * <li><b>Identity, not {@code equals()}.</b> A model reads and writes one
 * particular bean instance, so two distinct beans must never share a model,
 * even when they are {@code equals()}. A {@link java.util.WeakHashMap} keyed
 * by the bean compares keys with {@code equals()}/{@code hashCode()}, so it
 * handed the first bean's model to every later equal bean: reads showed the
 * wrong bean's values and writes went to the wrong bean (issue #10). Keying
 * by identity also keeps working when a bean's {@code hashCode()} changes
 * after its model was cached.</li>
 * <li><b>Weak keys.</b> The cache must not keep beans alive. An identity
 * wrapper cannot simply be put into a {@code WeakHashMap}: the map would hold
 * the <em>wrapper</em> weakly, nothing else references the wrapper, and the
 * entry would be dropped at the next GC while the bean is still in use. Here
 * the key is itself the weak reference to the bean, so an entry lives exactly
 * as long as its bean.</li>
 * </ul>
 *
 * <p>Values are held through a {@link WeakReference} as before, because a
 * model references its bean: a strongly held model would keep its own key
 * reachable and the entry would never be released.
 *
 * <p>Thread-safe: every access synchronizes on the cache.
 */
final class IdentityModelCache
{
    private final Map<Object, WeakReference<ComponentModel>> map = new HashMap<>();
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();

    /**
     * Returns the cached model for this exact bean instance, or null if there
     * is none or it has been garbage collected.
     */
    synchronized ComponentModel get(Object bean)
    {
        if( bean == null )
            return null;
        expungeStaleEntries();
        WeakReference<ComponentModel> ref = map.get( new Lookup( bean ) );
        return ref == null ? null : ref.get();
    }

    /** Caches the model for this exact bean instance, replacing any previous one. */
    synchronized void put(Object bean, ComponentModel model)
    {
        if( bean == null )
            return;
        expungeStaleEntries();
        map.put( new Key( bean, queue ), new WeakReference<>( model ) );
    }

    /** Number of live entries; for tests. */
    synchronized int size()
    {
        expungeStaleEntries();
        return map.size();
    }

    /** Removes the entries whose bean has been garbage collected. */
    private void expungeStaleEntries()
    {
        for( Reference<?> ref; ( ref = queue.poll() ) != null; )
        {
            // HashMap matches the stored key by reference (==) before calling
            // equals(), so this removes exactly the entry for the cleared key.
            map.remove( ref );
        }
    }

    /** Stored key: a weak reference to the bean, compared by identity. */
    private static final class Key extends WeakReference<Object>
    {
        private final int hash;

        Key(Object bean, ReferenceQueue<Object> queue)
        {
            super( bean, queue );
            this.hash = System.identityHashCode( bean );
        }

        @Override
        public int hashCode()
        {
            return hash;
        }

        @Override
        public boolean equals(Object obj)
        {
            if( obj == this )
                return true;
            Object bean = get();
            if( bean == null )
                return false; // a cleared key only matches itself
            if( obj instanceof Key )
                return bean == ( (Key)obj ).get();
            if( obj instanceof Lookup )
                return bean == ( (Lookup)obj ).bean;
            return false;
        }
    }

    /**
     * Short-lived probe used for lookups, so a get does not allocate and
     * register a new {@link WeakReference}.
     */
    private static final class Lookup
    {
        private final Object bean;

        Lookup(Object bean)
        {
            this.bean = bean;
        }

        @Override
        public int hashCode()
        {
            return System.identityHashCode( bean );
        }

        @Override
        public boolean equals(Object obj)
        {
            if( obj instanceof Key )
                return bean == ( (Key)obj ).get();
            return obj instanceof Lookup && bean == ( (Lookup)obj ).bean;
        }
    }
}
