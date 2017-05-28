package com.developmentontheedge.beans.swing.table;

import com.developmentontheedge.beans.swing.TabularPropertyInspector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class TabularPropertyInspectorTest extends TestCase
{
    /** Wait finalizing of object in millisecs. */
    static private int WAIT_FINALIZE = 500;

    /** Standart JUnit constructor */
    public TabularPropertyInspectorTest( String name )
    {
        super( name );
    }

    public void testExploreNullNull()
    {
        TabularPropertyInspector inspector = new TabularPropertyInspector();
        inspector.explore( null,null );
    }

    public void testBeanTableModelListener()
    {
        TabularPropertyInspector inspector = new TabularPropertyInspector();
        DefaultRowModel beanTableModel = new DefaultRowModel();
        beanTableModel.add( new Object() );
        inspector.explore( beanTableModel, new ColumnModel(beanTableModel.getBean(0)) );
        assertEquals( "Wrong number of listeners on BeanTableModel",1,beanTableModel.getListeners(RowModelListener.class).length );
    }

    public void testBeanTableModelFinalize()
    {
        TabularPropertyInspector inspector = new TabularPropertyInspector();
        DefaultRowModel beanTableModel = new DefaultRowModel();
        ReferenceQueue queue = new ReferenceQueue();
        WeakReference weakBeanTableModel = new WeakReference( beanTableModel,queue );

        beanTableModel.add( new Object() );
        inspector.explore( beanTableModel, new ColumnModel(beanTableModel.getBean(0)) );
        beanTableModel = null;
        inspector.explore( null,null );
        System.gc();
        try
        {
            queue.remove( WAIT_FINALIZE );
        }
        catch( InterruptedException exc )
        {
            fail( "Reference to DefaultRowModel not finalized." );
        }
        System.gc();
        assertNull( "WeakReference to DefaultRowModel not cleared.",weakBeanTableModel.get());
    }

    /** Make suite of tests. */
    public static Test suite()
    {
        TestSuite suite = new TestSuite( TabularPropertyInspectorTest.class );
//        TestSuite suite = new TestSuite();
//        suite.addTest( new TabularPropertyInspectorTest("testBeanTableModelFinalize") );
        return suite;
    }
} // end of TabularPropertyInspectorTest