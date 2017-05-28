package com.developmentontheedge.beans.swing.table;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.*;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.EventListener;
import java.util.Vector;


public class DefaultBeanTableModelTest extends TestCase
{
    /** Wait finalizing of object in millisecs. */
    static private int WAIT_FINALIZE = 500;

    /** Standart JUnit constructor */
    public DefaultBeanTableModelTest( String name )
    {
        super( name );
    }

    public void testConstructors()
    {
        try
        {
            DefaultRowModel tableModel = new DefaultRowModel();
        }
        catch( Throwable t )
        {
            fail("This constructor can't throw any exceptions.");
        }
    }

    public void testListeners()
    {
        DefaultRowModel tableModel = new DefaultRowModel();
        RowModelListener listener = new MyBeanTableModelListener();
        tableModel.addRowModelListener( listener );

        // check listeners
        EventListener[] listeners = tableModel.getListeners( RowModelListener.class );
        assertNotNull( "Listener was not added.",listeners );
        assertEquals( "Some listeners not added/or removed",1,listeners.length );
        tableModel.removeRowModelListener( listener );
        listeners = tableModel.getListeners( RowModelListener.class );
        assertNotNull( "Array of listeners must be returned.",listeners );
        assertEquals( "Array of listeners must be empty.",0,listeners.length );
    }

    public void testAddRemove()
    {
        Object o = new Object();
        DefaultRowModel tableModel = new DefaultRowModel();
        RowModelListener listener = new MyBeanTableModelListener();
        tableModel.addRowModelListener( listener );

        // check adding/removing
        tableModel.add( o );
        assertNotNull("Object was not added to begin of BeanTableModel",tableModel.getBean(0));
        assertEquals("Object not added (size wrong)",1,tableModel.size());
        tableModel.remove( 0 );
        assertEquals("Object not removed (size wrong)",0,tableModel.size());
        try
        {
            assertNotNull("Object was not removed from begin of BeanTableModel",tableModel.getBean(0));
            fail("Access by wrong index should raise exception");
        }
        catch( java.lang.IndexOutOfBoundsException exc )
        {
        }

        // remove listeners
        tableModel.removeRowModelListener( listener );
    }

    public void testAddChange()
    {
        Object o         = new Object();
        Object newObject = new Object();
        DefaultRowModel tableModel = new DefaultRowModel();
        RowModelListener listener = new MyBeanTableModelListener();
        tableModel.addRowModelListener( listener );

        // check adding/removing
        tableModel.add( o );
        assertNotNull("Object was not added to begin of BeanTableModel",tableModel.getBean(0));
        assertEquals("Object not added (size wrong)",1,tableModel.size());
        tableModel.change( 0,newObject );
        assertEquals("Object not changed (size wrong)",1,tableModel.size());
        assertEquals("Wrong object after change",newObject,tableModel.getBean(0));

        // remove listeners
        tableModel.removeRowModelListener( listener );
    }


    public void testAddEvents()
    {
        events.clear();
        Object o = new Object();
        DefaultRowModel tableModel = new DefaultRowModel();
        RowModelListener listener = new RowModelListener()
        {
            @Override
            public void tableChanged( RowModelEvent evt )
            {
                events.add( evt );
            }
        };
        tableModel.addRowModelListener( listener );
        tableModel.add( o );

        // remove listeners
        tableModel.removeRowModelListener( listener );

        // check events
        assertEquals( "Wrong number of events",1,events.size() );
        RowModelEvent evt = (RowModelEvent)events.get(0);
        assertEquals("First row from event is invalid",0,evt.getFirstRow());
        assertEquals("Last row from event is invalid",0,evt.getLastRow());
        assertEquals("Wrong source of event",tableModel,evt.getSource());
        assertEquals("Wrong type of event",RowModelEvent.INSERT,evt.getType());
    }

    public void testRemoveEvents()
    {
        events.clear();
        Object o = new Object();
        DefaultRowModel tableModel = new DefaultRowModel();
        RowModelListener listener = new RowModelListener()
        {
            @Override
            public void tableChanged( RowModelEvent evt )
            {
                events.add( evt );
            }
        };
        tableModel.addRowModelListener( listener );
        tableModel.add( o );
        events.clear();
        tableModel.remove(0);

        // remove listeners
        tableModel.removeRowModelListener( listener );

        // check events
        assertEquals( "Wrong number of events",1,events.size() );
        RowModelEvent evt = (RowModelEvent)events.get(0);
        assertEquals("First row from event is invalid",0,evt.getFirstRow());
        assertEquals("Last row from event is invalid",0,evt.getLastRow());
        assertEquals("Wrong source of event",tableModel,evt.getSource());
        assertEquals("Wrong type of event",RowModelEvent.DELETE,evt.getType());
    }

    public void testChangeEvents()
    {
        events.clear();
        Object o = new Object();
        DefaultRowModel tableModel = new DefaultRowModel();
        RowModelListener listener = new RowModelListener()
        {
            @Override
            public void tableChanged( RowModelEvent evt )
            {
                events.add( evt );
            }
        };
        tableModel.addRowModelListener( listener );
        tableModel.add( o );
        events.clear();
        tableModel.change(0,new Object());

        // remove listeners
        tableModel.removeRowModelListener( listener );

        // check events
        assertEquals( "Wrong number of events",1,events.size() );
        RowModelEvent evt = (RowModelEvent)events.get(0);
        assertEquals("First row from event is invalid",0,evt.getFirstRow());
        assertEquals("Last row from event is invalid",0,evt.getLastRow());
        assertEquals("Wrong source of event",tableModel,evt.getSource());
        assertEquals("Wrong type of event",RowModelEvent.UPDATE,evt.getType());
    }

    public void testCleanUp()
    {
        events.clear();
        BorderLayout b1 = new BorderLayout(1,1);
        DefaultRowModel tableModel = new DefaultRowModel();
        tableModel.add( b1 );
        ReferenceQueue queue = new ReferenceQueue();
        WeakReference weakTableModel = new WeakReference( tableModel,queue );
        tableModel.remove( 0 );
        tableModel = null;
        System.gc();
        try
        {
            queue.remove( WAIT_FINALIZE );
        }
        catch( InterruptedException exc )
        {
            fail("Reference to DefaultRowModel not enqueued.");
        }
        System.gc();
        assertNull("Reference to DefaultRowModel not cleared.",weakTableModel.get());
    }

    /** Make suite of tests. */
    public static Test suite()
    {
        TestSuite suite = new TestSuite( DefaultBeanTableModelTest.class );
//        TestSuite suite = new TestSuite();
//        suite.addTest( new DefaultBeanTableModelTest("testCleanUp") );
        return suite;
    }


    private final Vector events = new Vector();

    private static class MyBeanTableModelListener implements RowModelListener
    {
        @Override
        public void tableChanged( RowModelEvent evt )
        {
        }
    };
} // end of DefaultBeanTableModelTest