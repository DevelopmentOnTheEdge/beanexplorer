package com.developmentontheedge.beans.undo;

import com.developmentontheedge.beans.BeanInfoEx;
import com.developmentontheedge.beans.model.Property;
import com.developmentontheedge.beans.swing.ValueEditor;
import com.developmentontheedge.beans.swing.treetable.JTreeTable;
import junit.framework.TestCase;

import javax.swing.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

/**
 * @todo test undo/redo for bounded properties
 */
public class TestUndoRedo extends TestCase
{
    public TestUndoRedo(String name)
    {
        super(name);
    }

    // to get access to ptotected members
    static class PropertyInspector extends com.developmentontheedge.beans.swing.PropertyInspector
    {
        public JTreeTable getTreeTable()
        {
            return treeTable;
        }
    }

    public static class Simple
    {
        int integerProperty = 20;
        public int getIntegerProperty()                     { return integerProperty; }
        public void setIntegerProperty(int value)           { integerProperty = value; }

        String stringProperty = "stringValue";
        public String getStringProperty()                   { return stringProperty;  }
        public void   setStringProperty(String value)       { stringProperty = value; }
    }

    public static class SimpleBeanInfo extends BeanInfoEx
    {
        public SimpleBeanInfo()
        {
            super(TestUndoRedo.Simple.class, null);
        }

        public void initProperties() throws IntrospectionException
        {
            add(new PropertyDescriptor("integerProperty", beanClass));
            add(new PropertyDescriptor("stringProperty", beanClass));
        }
    }



    public void testUndoRedo() throws Exception
    {
        Simple simple = new Simple();
        PropertyInspector propertyInspector = new PropertyInspector();
        propertyInspector.explore( simple); //new BorderLayout() );

/*
javax.swing.JFrame frame = new javax.swing.JFrame();
frame.getContentPane().add(propertyInspector);
frame.pack();
frame.setVisible(true);
*/

        TransactionUndoManager undoManager = new TransactionUndoManager();
        propertyInspector.addTransactionListener( undoManager );
        assertTrue("Should be can't undo", !undoManager.canUndo());
        assertTrue("Should be can't redo", !undoManager.canRedo());

        JTreeTable treeTable = propertyInspector.getTreeTable();
        assertNotNull( "TreeTable can't be null", treeTable );

        ValueEditor editor = (ValueEditor)treeTable.getCellEditor(1,1);
        assertNotNull( "Editor not found",editor );

        JTextField textField = (JTextField)treeTable.prepareEditor( editor,1,1);
        assertNotNull( textField );

        Integer value_1 = new Integer(100);
        Integer value_2 = new Integer(200);

        Property prop = (Property)editor.getCellEditorValue();
        assertNotNull( "'integerProperty' property is null", prop );
        textField.setText( value_1.toString() );
        textField.postActionEvent();
        assertTrue(   "Value is not Integer", (prop.getValue() instanceof Integer));
        assertEquals( "Wrong initial value",  value_1, (Integer)prop.getValue());

        textField.setText( value_2.toString() );
        textField.postActionEvent();
        assertTrue( "Value is not Integer", (prop.getValue() instanceof Integer));
        assertEquals( "Wrong value", value_2, (Integer)prop.getValue());

        assertTrue("Can't undo the edit",  undoManager.canUndo());
        assertTrue("Should be can't redo", !undoManager.canRedo());

        undoManager.undo();
        assertEquals( "Wrong undone value ", value_1, (Integer)prop.getValue());
        assertTrue("Should be redo enabled", undoManager.canRedo());

        undoManager.redo();
        assertEquals( "Wrong redone value ", value_2, (Integer)prop.getValue());
    }

    protected void setUp()
    {
    }

    protected void tearDown()
    {
    }
//
//    private static class My
}