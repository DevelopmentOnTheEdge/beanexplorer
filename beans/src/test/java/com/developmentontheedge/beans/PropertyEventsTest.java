/** $Id: PropertyEventsTest.java,v 1.13 2004/09/04 13:56:39 fedor Exp $ */

package com.developmentontheedge.beans;

import com.developmentontheedge.beans.model.ComponentFactory;
import com.developmentontheedge.beans.model.ComponentModel;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PropertyEventsTest extends TestCase
{
    EventFirer firer;
    StandaloneEventFirer loneFirer;
    ComponentModel model, loneModel;
    String beanName, loneBeanName;

    PropertyChangeEvent event;

    boolean bListenerRegistered = false;

    public PropertyEventsTest( String name )
    {
        super( name );
    }

    /** main function */
    public static void main( String[] args )
    {
        TestRunner.run( suite() );
    }

    /** suite function */
    public static Test suite()
    {
        return new TestSuite( PropertyEventsTest.class );
    }

    @Override
    protected void setUp()
    {
        beanName = EventFirer.class.getName();
        beanName = beanName.substring( beanName.lastIndexOf( "." ) + 1 );
        loneBeanName = StandaloneEventFirer.class.getName();
        loneBeanName = loneBeanName.substring( loneBeanName.lastIndexOf( "." ) + 1 );
        firer = new EventFirer();
        loneFirer = new StandaloneEventFirer();
        model = ComponentFactory.getModel( firer );
        loneModel = ComponentFactory.getModel( loneFirer );
    }

    @Override
    protected void tearDown()
    {
        model = null;
        firer = null;
        loneFirer = null;
        loneModel = null;
    }

    ////////////////////////////////////////////////////////////////////////////

    public void testDefaultEventProcessing() throws Exception
    {
        event = null;
        TestListener listener = new TestListener();
        model.addPropertyChangeListener( listener );

        assertNotNull( "'intValue' property is not found",     model.findProperty( "intValue" ) );
        assertEquals( "intValue's getName() is incorrect",     "intValue", model.findProperty( "intValue" ).getName() );
        assertEquals( "'intValue' property has wrong value: ",  60, model.findProperty( "intValue" ).getValue() );

        model.findProperty( "intValue" ).setValue( 50 );
        assertNotNull( "event was not fired", event );

        assertEquals( "Event's old value is incorrect", 60, event.getOldValue() );
        assertEquals( "Event's new value is incorrect", 50, event.getNewValue() );
        assertEquals( "Event's propertyName is incorrect", /*be5anName + "/" +*/ "intValue" + EventConstants.EVT_SET_VALUE,
                                                           event.getPropertyName() );

        model.removePropertyChangeListener( listener );
    }

    public void testStandaloneFirer() throws Exception
    {
        assertEquals("Initially no listeners should be assigned to the bean.", 0, loneFirer.pcSupport.getPropertyChangeListeners().length);

        TestListener listener = new TestListener();
        loneModel.addPropertyChangeListener(listener);
        assertEquals("One propertyChangeListner should be added to the bean.", 1, loneFirer.pcSupport.getPropertyChangeListeners().length);

        loneModel.findProperty( "intValue" ).setValue( 51 );
        assertNotNull( "event was not fired", event );
        assertEquals(  "Event's old value is incorrect", 60, event.getOldValue() );
        assertEquals(  "Event's new value is incorrect", 51, event.getNewValue() );
        assertEquals( "Event's propertyName is incorrect", /*loneBeanName + "/" + */ "intValue" + EventConstants.EVT_SET_VALUE,
                                                           event.getPropertyName() );

        event = null;
        loneFirer.pcSupport.firePropertyChange("bad value", 50, 60);
//        assertNull( "Event should not be fired", event);


        loneModel.removePropertyChangeListener(listener);
        assertEquals("Listener should be removed from bean.", 1, loneFirer.pcSupport.getPropertyChangeListeners().length);
    }

    public void testFilteredEventProcessing() throws Exception
    {
        event = null;
        ComponentModel filt = ComponentFactory.filterByRemovingParentProperties( model );
        TestListener listener = new TestListener();
        filt.addPropertyChangeListener( listener );
        filt.findProperty( "intValue" ).setValue(
            50 );
        assertNotNull( "event was not fired", event );
        assertEquals( "Event's propertyName is incorrect", "intValue" + EventConstants.EVT_SET_VALUE, event.getPropertyName() );
        filt.removePropertyChangeListener( listener );
    }


    private class TestListener implements PropertyChangeListener
    {
        public TestListener()
        {
        }

        @Override
        public void propertyChange( PropertyChangeEvent evt )
        {
//            System.out.println("PropertyChanged: " + evt.getPropertyName() + 
//                ", old value=" +  evt.getOldValue() + ", new value=" + evt.getNewValue() );
            event = evt;
        }
    }


    public class EventFirer
    {
        public EventFirer()
        {
        }

        public int getIntValue()
        {
            return intValue;
        }

        public void setIntValue( int intValue )
        {
            this.intValue = intValue;
        }

        private int intValue = 60;
    }


    public class StandaloneEventFirer
    {
        PropertyChangeSupport pcSupport;

        public StandaloneEventFirer()
        {
            pcSupport = new PropertyChangeSupport( this );
        }

        public int getIntValue()
        {
            return intValue;
        }

        public void setIntValue( int intValue )
        {
            int oldVal = this.intValue;
            this.intValue = intValue;
            pcSupport.firePropertyChange( "intValue", oldVal, intValue );
        }

        public void  addPropertyChangeListener( PropertyChangeListener listener )
        {
            bListenerRegistered = true;
            pcSupport.addPropertyChangeListener( listener );
        }

        public void  removePropertyChangeListener( PropertyChangeListener listener )
        {
            pcSupport.removePropertyChangeListener( listener );
        }

        private int intValue = 60;
    }

}
