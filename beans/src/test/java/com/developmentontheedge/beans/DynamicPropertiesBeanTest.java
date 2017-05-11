/** $Id: DynamicPropertiesBeanTest.java,v 1.3 2001/10/04 09:03:15 champ Exp $ */
package com.developmentontheedge.beans;

import junit.framework.Test;

public class DynamicPropertiesBeanTest extends BeanTest
{
    public DynamicPropertiesBeanTest(String name)
    {
        super(name);
    }

    public void testModelProperties() throws Exception
    {
/*
        assertEquals("Property count", new Integer(5), // plus one for getClass
                     new Integer(model.getPropertyCount()));

        SimpleBean simpleBean = (SimpleBean)bean;

        checkProperty( "stringProperty",  String.class,   simpleBean.getStringProperty(),               false );
        checkProperty( "booleanProperty", boolean.class,  new Boolean(simpleBean.getBooleanProperty()), false);
        checkProperty( "integerProperty", int.class,      new Integer(simpleBean.getIntegerProperty()), false);
        checkProperty( "colorProperty",   Color.class,    simpleBean.getColorProperty(),                false);
*/
    }

/*
    public void testCreateDynamicModel()
    {
        DynamicPropertyContainer dpc = new DynamicPropertyContainer();
        model = ComponentFactory.getModel( dpc );
        assertNotNull( "model is created", model );
        assertEquals( "correct property count",
            new Integer( 3 ),
            new Integer( model.getPropertyCount() ) ); // plus one for getClass
        assertTrue( "Component name is correct",
        model.findProperty( "name" ).getValue().equals( "DynamicPropertyContainer" ) );
    }



    public void testDynamicProperties() throws Exception
    { //lesa
        Property dyn = model.findProperty( "myProperties" );
        assertNotNull( "Property containing dynanmic properties exists", dyn );
        DynamicPropertySet set = ( DynamicPropertySet )dyn.getValue();
        assertNotNull( "Dynamic Property container is initialized", set );

        PropertyDescriptorEx pd = new PropertyDescriptorEx( "stringProperty" );

        assertNotNull( "Newly created string property has a name", pd.getName() );
        set.add( pd );
        set.setValue( "stringProperty", "Value" );
        assertEquals( "Property value is set and got via DynamicPropertySet interface", "Value",
        set.getValue( "stringProperty" ) );
        assertEquals( "correct propertyDescriptors array size",
            new Integer( 1 ),
            new Integer( set.getPropertyDescriptors().length ) ); // plus one for getClass
        // some code to reinitialize 'myProperties'
        ComponentFactory.recreateChildProperties( model );
        dyn = model.findProperty( "myProperties" );
        assertNotNull( "Property containing dynamic properties exists after adding string property", dyn );
        assertEquals( "dynamic property container has correct count",
            new Integer( 1 ),
            new Integer( dyn.getPropertyCount() ) ); // plus one for getClass
        Property simple = dyn.findProperty( "stringProperty" );
        assertNotNull( "Dynamically added string property exists", simple );
        assertEquals( "Property value via Component Factory", "Value", simple.getValue() );
        assertNotNull( "Dynamically added property is not found via complete name",
        model.findProperty( "myProperties/stringProperty" ) );
    }

    public void testDynamicArrayProperties() throws Exception
    {
        IndexedPropertyDescriptorEx pd = new IndexedPropertyDescriptorEx( "arrayProperty" );
        Property dyn = model.findProperty( "myProperties" );
        DynamicPropertySet set = ( DynamicPropertySet )dyn.getValue();
        set.add( pd );
        set.setValue( "arrayProperty",
            new String[] { "Value 1", "Value 2" } );
        assertEquals( "correct propertyDescriptors array size",
            new Integer( 2 ),
            new Integer( set.getPropertyDescriptors().length ) ); // stringProperty and arrayProperty

        ComponentFactory.recreateChildProperties( model );
        dyn = model.findProperty( "myProperties" );
        assertNotNull( "Property containing dynamic properties exists after adding array property", dyn );
        assertEquals( "dynamic property container has correct count",
            new Integer( 2 ),
            new Integer( dyn.getPropertyCount() ) );
        ArrayProperty array = ( ArrayProperty )dyn.findProperty( "arrayProperty" );
        assertEquals( "array property has correct number of elements:",
            new Integer( 2 ),
            new Integer( array.getPropertyCount() ) );
        assertEquals( "array property element has correct class:", String.class, array.getItemClass() );
        assertEquals( "array property has correct element #0", "Value 1", array.getPropertyAt( 0 ).getValue() );
        assertEquals( "array property has correct element #1", "Value 2", array.getPropertyAt( 1 ).getValue() );
        assertTrue( "array property must not contain element #2", array.getPropertyAt( 2 ) == null );
        assertNotNull( "Dynamically added array property's first value is not found via complete name",
        model.findProperty( "myProperties/arrayProperty/[0]" ) );
    }
*/
    public static Test suite()
    {
        beanClass = DynamicPropertiesBean.class;
        testClass = DynamicPropertiesBeanTest.class;

        return BeanTest.suite();
    }

    public static void main(String[] args)
    {
        beanClass = DynamicPropertiesBean.class;
        testClass = DynamicPropertiesBeanTest.class;

        try
        {
            BeanTest beanTest = new DynamicPropertiesBeanTest("XXX");
            beanTest.testCreateBeanInstance();
            beanTest.testCreateModel();
            beanTest.testViewModel();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
