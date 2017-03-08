// $Id: MapCacheImplFactory.java,v 1.2 2012/04/06 03:57:06 zha Exp $
package com.developmentontheedge.enterprise.caches.impl;

import com.developmentontheedge.enterprise.caches.AbstractCacheImpl;
import com.developmentontheedge.enterprise.caches.CacheImplFactory;

/**
 * 
 * @author puz
 *
 */
public class MapCacheImplFactory implements CacheImplFactory
{
    public AbstractCacheImpl getCacheImpl(String namespace)
    {
        return new MapCacheImpl( namespace );
    }

    public AbstractCacheImpl getCacheImpl( String namespace, int size )
    {
        return new MapCacheImpl( namespace, size );
    }
}
