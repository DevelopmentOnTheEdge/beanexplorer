// $Id: AutoTest.java,v 1.1 2009/01/10 10:30:30 puz Exp $
package com.developmentontheedge.enterprise.caches;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * @author puz
 *
 */
public class AutoTest
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite( "Test for com.beanexplorer.enterprise.caches._test" );
        suite.addTestSuite( CacheFactoryTest.class );
        return suite;
    }
}
