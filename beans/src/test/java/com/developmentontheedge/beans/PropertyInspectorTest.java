/** $Id: PropertyInspectorTest.java,v 1.11 2007/10/04 06:19:06 adolg Exp $ */

package com.developmentontheedge.beans;

import com.developmentontheedge.beans.swing.PropertyInspector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.swing.*;


public class PropertyInspectorTest extends TestCase
{

    protected static JFrame frame = null;
    protected static PropertyInspector insp = null; 

    public PropertyInspectorTest(String name)
    {
        super(name);

   if( frame == null )
      {
             frame = new JFrame("PropertyInspector test");

             insp = new PropertyInspector();
             frame.getContentPane().add(insp);
             frame.setSize(400, 500);
             frame.setVisible(true);
      }
    }

    public void testCreate()
    {
    assertTrue( "failed to create PropertyInspector", insp != null );
    }


/*
    public void testSettingModel()
    {
        insp.setComponentModel(ComponentFactory.getModel( insp ) );
        assertTrue( "failed to set component model", insp.getComponentModel() != null );
    }

    public void gridToggle()
    {
        JOptionPane.showMessageDialog( insp, "Press OK to toggle grid" );
        insp.setShowGrid( !insp.isShowGrid() );
        assertTrue( "grid toggle was not seen",
      JOptionPane.showConfirmDialog( insp, "Does the grid toggle?" ) ==
      JOptionPane.YES_OPTION  );
    }

    public void testDisplayFromJar()
    {
        JOptionPane.showMessageDialog( insp, "Press OK to Timer bean" );
        ComponentFactory.loadComponents( "com/beanexplorer/_test/jars", "" );
        insp.setComponentModel(ComponentFactory.getModel( "Timer" ) );
        assertTrue( "Timer is not set",
      JOptionPane.showConfirmDialog( insp, "Does it show?" ) ==
      JOptionPane.YES_OPTION  );
    }

*/
    public void testDisplayFromArray()
    {
        JOptionPane.showMessageDialog( insp, "Press OK to Test an Array" );
        insp.explore( new String[] { "String1", "String2" } );
        assertTrue( "Array is not set",
      JOptionPane.showConfirmDialog( insp, "Does it show?" ) ==
      JOptionPane.YES_OPTION  );
    }


    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTest(new PropertyInspectorTest("testCreate"));
//        suite.addTest(new PropertyInspectorTest("testSettingModel"));
//        suite.addTest(new PropertyInspectorTest("gridToggle"));
//        suite.addTest(new PropertyInspectorTest("testDisplayFromJar"));
        //suite.addTest(new PropertyInspectorTest("testDisplayFromArray"));

        return suite;
    }
}