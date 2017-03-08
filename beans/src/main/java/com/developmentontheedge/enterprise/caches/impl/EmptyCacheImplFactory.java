// $Id: EmptyCacheImplFactory.java,v 1.3 2012/04/06 03:57:06 zha Exp $
package com.developmentontheedge.enterprise.caches.impl;

import com.developmentontheedge.enterprise.caches.AbstractCacheImpl;
import com.developmentontheedge.enterprise.caches.CacheImplFactory;

public class EmptyCacheImplFactory implements CacheImplFactory
{
    /* (non-Javadoc)
     * @see com.beanexplorer.enterprise.caches.CacheImplFactory#getCacheImpl(java.lang.String)
     */
    public AbstractCacheImpl getCacheImpl(String namespace)
    {
        AbstractCacheImpl ret = new EmptyCacheImpl();
        ret.setNamespace( namespace );
        return ret;
    }

    public AbstractCacheImpl getCacheImpl(String namespace, int size )
    {
        return this.getCacheImpl( namespace );
    }
}
