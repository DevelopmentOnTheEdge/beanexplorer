package com.developmentontheedge.beans.swing;

import com.developmentontheedge.beans.model.ArrayProperty;
import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import com.developmentontheedge.beans.model.Property;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class PropertyInspectorDerivedPropertyTest extends TestCase
{
    /** Standart JUnit constructor */
    public PropertyInspectorDerivedPropertyTest( String name )
    {
        super( name );
    }

    public void testDerived()
    {
        BeanWithDerived bean = new BeanWithDerived();
        PropertyInspector inspector = new PropertyInspector();
        inspector.explore( bean );
        ComponentModel model = ComponentFactory.getModel( bean );
        assertNotNull("Can't create component model for BeanWithDerived",model);
        Property p = model.findProperty("derived");
        assertNotNull("Can't find property 'derived'",p);
        Object o = p.getValue();
        inspector.addPropertyChangeListener("derived",new PropertyChangeListener()
        {
            public void propertyChange( PropertyChangeEvent evt )
            {
                BeanWithDerived sb = (BeanWithDerived)evt.getSource();
                assertNotNull( "Source not specified",sb );
                assertTrue("Class of property is wrong (was:"+sb.getDerived().getClass()+")",sb.getDerived().getClass()==DerivedProperty.class);
            }
        });
        bean.setDerived( new DerivedProperty() );
    }

    public void testDerivedArray()
    {
        BeanWithDerived bean = new BeanWithDerived();
        PropertyInspector inspector = new PropertyInspector();
        inspector.explore( bean );
        inspector.addPropertyChangeListener("derivedArray",new PropertyChangeListener()
        {
            public void propertyChange( PropertyChangeEvent evt )
            {
                BeanWithDerived sb = (BeanWithDerived)evt.getSource();
                assertNotNull( "Source not specified",sb );
                assertTrue("Class of property is wrong (was:"+sb.getDerivedArray().getClass()+")",sb.getDerivedArray()[0].getClass()==DerivedProperty.class);

                ComponentModel componentModel = ComponentFactory.getModel( sb );
                ArrayProperty p = (ArrayProperty)componentModel.findProperty( evt.getPropertyName() );
                Property p1 = (Property)p.getPropertyAt(0);
                Object o = p1.getValue();
                assertTrue( "Property at index 0 should be DerivedProperty but was "+o.getClass(),o.getClass()==DerivedProperty.class );
            }
        });
        DerivedProperty[] array = new DerivedProperty[2];
        array[0] = new DerivedProperty();
        array[1] = new DerivedProperty();
        bean.setDerivedArray( array );
    }

    /** Make suite of tests. */
    public static Test suite()
    {
        TestSuite suite = new TestSuite( PropertyInspectorDerivedPropertyTest.class );
        return suite;
    }
} // end of PropertyInspectorDerivedPropertyTest