package com.developmentontheedge.beans.swing;

import com.developmentontheedge.beans.swing.table.ColumnModel;
import com.developmentontheedge.beans.swing.table.DefaultRowModel;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.awt.*;

public class TabularPropertyInspectorEventTest extends TestCase
{
    private TabularPropertyInspector tabularInspector = null;

    /** Standart JUnit constructor */
    public TabularPropertyInspectorEventTest( String name )
    {
        super( name );
    }

    public void setUp()
    {
        tabularInspector = new TabularPropertyInspector();
    }

    public void tearDown()
    {
        tabularInspector = null;
        System.gc();
    }

    public void testRowHeaderChange()
    {
        DefaultRowModel model = new DefaultRowModel();
        model.add( new BorderLayout(1,1) );
        model.add( new BorderLayout(2,2) );
        model.add( new BorderLayout(3,3) );
        tabularInspector.explore(model, new ColumnModel(new BorderLayout()));
        ColumnModel options = tabularInspector.getColumnModel();
        assertTrue( "By default rowHeader should be 'true'",tabularInspector.getRowHeader() );
        assertTrue( "By default rowNumbers should be 'true'",options.isRowNumbersVisible() );
        tabularInspector.setRowHeader( false );
        assertTrue( "rowHeader not changed to 'false' from inspector",!tabularInspector.getRowHeader() );
        assertTrue( "rowNumbers not changed to 'false' from inspector",!options.isRowNumbersVisible() );
        options.setRowNumbersVisible( true );
        assertTrue( "rowHeader not changed to 'true' from table options",tabularInspector.getRowHeader() );
        assertTrue( "rowNumbers not changed to 'true' from table options",options.isRowNumbersVisible() );
    }

    public void testSortEnabledChange()
    {
        DefaultRowModel model = new DefaultRowModel();
        model.add( new BorderLayout(1,1) );
        model.add( new BorderLayout(2,2) );
        model.add( new BorderLayout(3,3) );
        tabularInspector.explore(model, new ColumnModel(new BorderLayout()));
        ColumnModel options = tabularInspector.getColumnModel();
        assertTrue( "By default sortEnabled should be 'true'",tabularInspector.getSortEnabled() );
        assertTrue( "By default sortEnabled should be 'true'",options.isSortEnabled() );
        tabularInspector.setSortEnabled( false );
        assertTrue( "sortEnabled not changed to 'false' from inspector",!tabularInspector.getSortEnabled() );
        assertTrue( "ColumnModel.sortEnabled not changed to 'false' from inspector",!options.isSortEnabled() );
        options.setSortEnabled( true );
        assertTrue( "sortEnabled not changed to 'true' from table options",tabularInspector.getSortEnabled() );
        assertTrue( "ColumnModel.sortEnabled not changed to 'true' from table options",options.isSortEnabled() );
    }

    public void testRowAndSortChange()
    {
        DefaultRowModel model = new DefaultRowModel();
        model.add( new BorderLayout(1,1) );
        model.add( new BorderLayout(2,2) );
        model.add( new BorderLayout(3,3) );
        tabularInspector.explore(model, new ColumnModel(new BorderLayout()));
        ColumnModel options = tabularInspector.getColumnModel();
        assertTrue( "By default rowHeader should be 'true'",tabularInspector.getRowHeader() );
        assertTrue( "By default sortEnabled should be 'true'",tabularInspector.getSortEnabled() );
        tabularInspector.setSortEnabled( false );
        assertTrue( "rowHeader shouldn't change",tabularInspector.getRowHeader() );
    }


    /** Make suite of tests. */
    public static Test suite()
    {
        TestSuite suite = new TestSuite( TabularPropertyInspectorEventTest.class );
        return suite;
    }

} // end of TabularPropertyInspectorEventTest