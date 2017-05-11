/* $Id: TabularPropertyInspectorTest.java,v 1.9 2007/10/04 06:19:06 adolg Exp $ */
package com.developmentontheedge.beans;

import com.developmentontheedge.beans.swing.TabularPropertyInspector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.swing.*;

public class TabularPropertyInspectorTest extends TestCase
{

    protected static JFrame frame = null;
    protected static TabularPropertyInspector insp = null;

    public TabularPropertyInspectorTest( String name )
    {
        super( name );

        if ( frame == null )
        {
            frame = new JFrame( "TabularPropertyInspector test" );

            insp = new TabularPropertyInspector();
            frame.getContentPane().add( insp );
            frame.setSize( 400, 500 );
            frame.setVisible(true);
        }
    }

    public void testCreate()
    {
        assertTrue( "failed to create TabularPropertyInspector", insp != null );
    }

/*  TabularPropertyInspector.explore( ... ) removed
    public void testSettingModel()
    {
        final Vector list = new Vector();
        list.add(
            new SimpleBean() );
        list.add(
            new SimpleBean() );
        insp.explore( list.iterator(),
//            new String[] { "stringProperty", "colorProperty" } );
//        insp.explore(
            new AbstractTabularPropertyInspectorModel(
            new String[] { "stringProperty", "booleanProperty", "colorProperty" } )
            {
                public int getRowCount() { return list.size(); }

                public Object getRowAt( int index ) { return list.elementAt( index ); }
            } );
    }
*/

/*  @TODO !!Fix later!!
    public void testSettingIteratorModel()
    {
        insp.explore(
            new Iterator()
            {
                int count = 0;
                public boolean hasNext() { return count < 1000; }

                SimpleBean bean = new SimpleBean();
                public Object next() { return bean; }

                public void remove() { }

            },
            new String[] { "stringProperty", "booleanProperty", "colorProperty" } );
    }
*/


    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        try
        {
            suite.addTest(
                new TabularPropertyInspectorTest( "testCreate" ) );
//            suite.addTest(
//                new TabularPropertyInspectorTest( "testSettingIteratorModel" ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return suite;
    }
}
