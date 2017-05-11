/* $Id: PreferencesTest.java,v 1.6 2007/10/04 06:19:06 adolg Exp $ */
package com.developmentontheedge.beans;

import com.developmentontheedge.beans.swing.PropertyInspector;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;

public class PreferencesTest extends TestCase
{
    public PreferencesTest( String name )
    {
        super( name );
    }

    public static junit.framework.Test suite()
    {
        TestSuite suite = new TestSuite( PreferencesTest.class.getName() );

        suite.addTest( new PreferencesTest("createPreferences") );
        suite.addTest( new PreferencesTest("savePreferences") );
        suite.addTest( new PreferencesTest("loadPreferences") );
//        suite.addTest( new PreferencesTest("showPreferences") );

        return suite;
    }

    static Preferences preferences;
    public void createPreferences() throws Exception
    {

        preferences = new Preferences();

        preferences.add(new DynamicProperty("string", "String prop",  "String description",  String.class,  "aaa"));
        preferences.add(new DynamicProperty("bool",   "Boolean prop", "Boolean description", Boolean.class, Boolean.TRUE));
        preferences.add(new DynamicProperty("int",    "Integer prop", "Integer description", Integer.class, new Integer(1)));
        preferences.add(new DynamicProperty("long",   "Long prop",    "Long description",    Long.class,    new Long(2)));
        preferences.add(new DynamicProperty("float",  "Float prop",   "Float description",   Float.class,   new Float(0.01f)));
        preferences.add(new DynamicProperty("double", "Double prop",  "Double description",  Double.class,  new Double(5e-2)));

        Preferences pref2 = new Preferences();
        preferences.add(new DynamicProperty("child", "child prop",  "Child description",  Preferences.class,  pref2));
        pref2.add(new DynamicProperty("string", "String2 prop",  "String description",  String.class,  "aaa"));
        pref2.add(new DynamicProperty("bool",   "Boolean2 prop", "Boolean description", Boolean.class, Boolean.TRUE));
        pref2.add(new DynamicProperty("int",    "Integer2 prop", "Integer description", Integer.class, new Integer(1)));
        pref2.add(new DynamicProperty("long",   "Long2 prop",    "Long description",    Long.class,    new Long(1)));
        pref2.add(new DynamicProperty("float",  "Float2 prop",   "Float description",   Float.class,   new Float(0.01f)));
        pref2.add(new DynamicProperty("double", "Double2 prop",  "Double description",  Double.class,  new Double(5e-2)));

        Preferences pref3 = new Preferences();
        pref2.add(new DynamicProperty("child",  "child3 prop",  "Child description",  Preferences.class,  pref3));
        pref3.add(new DynamicProperty("string", "String3 prop",  "String description",  String.class,  "aaa"));
        pref3.add(new DynamicProperty("bool",   "Boolean3 prop", "Boolean description", Boolean.class, Boolean.TRUE));

        // attributes
        PropertyDescriptor descr = new PropertyDescriptor("hidden", null, null);
        descr.setHidden(true);
        preferences.add(new DynamicProperty(descr, String.class, "it is hide"));

        descr = new PropertyDescriptor("expert", null, null);
        descr.setExpert(true);
        preferences.add(new DynamicProperty(descr, String.class, "it is expert"));

        descr = new PropertyDescriptor("read only", null, null);
        descr.setValue(BeanInfoEx.READ_ONLY, Boolean.TRUE);
        preferences.add(new DynamicProperty(descr, String.class, "it is read only"));
    }

    public void savePreferences() throws Exception
    {
        FileOutputStream out = new FileOutputStream("preferences.xml");
        preferences.save(out);
    }

    public void loadPreferences() throws Exception
    {
        preferences = new Preferences();
        preferences.load("preferences.xml");
    }

    public void showPreferences()
    {
        PropertyInspector inspector = new PropertyInspector();
        javax.swing.JFrame frame = new javax.swing.JFrame(this.toString() + " test" );
        frame.getContentPane().add(inspector);
        frame.setSize( 400, 500 );
        frame.setVisible(true);

        inspector.explore(preferences);
    }

}