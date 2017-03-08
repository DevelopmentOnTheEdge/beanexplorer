package com.developmentontheedge.enterprise.caches.impl;

import com.developmentontheedge.enterprise.caches.AbstractCacheImpl;
import com.developmentontheedge.enterprise.caches.CacheImplFactory;

/**
 * 
 * @author puz
 *
 */
public class JCSCacheImplFactory implements CacheImplFactory
{
    /* (non-Javadoc)
     * @see com.developmentontheedge.enterprise.caches.CacheImplFactory#getCacheImpl(java.lang.String)
     */
    public AbstractCacheImpl getCacheImpl(String namespace)
    {
        return new JCSCacheImpl( namespace );
    }

    public AbstractCacheImpl getCacheImpl(String namespace, int size )
    {
        return this.getCacheImpl( namespace );
    }
}