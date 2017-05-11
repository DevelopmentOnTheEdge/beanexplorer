/** $Id: DialogPropertyInspectorTest.java,v 1.4 2007/10/04 06:16:59 adolg Exp $ */

package com.developmentontheedge.beans;

import com.developmentontheedge.beans.swing.DialogPropertyInspector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.swing.*;
import java.awt.*;


public class DialogPropertyInspectorTest extends TestCase
{

    protected static JFrame frame = null;
    protected static DialogPropertyInspector insp = null;
    //protected static PropertyInspector insp = null;

    public DialogPropertyInspectorTest( String name )
    {
        super( name );

        if ( frame == null )
        {
            frame = new JFrame( "DialogPropertyInspector test" );

            insp = new DialogPropertyInspector();
            //insp = new PropertyInspector();
            frame.getContentPane().add( insp );
            frame.setSize( 400, 500 );
            frame.setVisible(true);
        }
    }

    public void testCreate()
    {
        assertTrue( "failed to create DialogPropertyInspector", insp != null );
    }


    public void testSettingModel()
    {
        //Point pt = new Point(10, 20);
        //JPanel panel = new JPanel();
        Font font = new Font("Courier", Font.BOLD, 20);
        insp.explore(font);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        try
        {
            suite.addTest( new DialogPropertyInspectorTest( "testCreate" ) );
            suite.addTest( new DialogPropertyInspectorTest( "testSettingModel" ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return suite;
    }
}
