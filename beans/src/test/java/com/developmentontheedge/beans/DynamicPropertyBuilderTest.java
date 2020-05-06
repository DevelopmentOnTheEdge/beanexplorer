package com.developmentontheedge.beans;

import org.junit.Test;

import static com.developmentontheedge.beans.BeanInfoConstants.GROUP_ID;
import static com.developmentontheedge.beans.BeanInfoConstants.RELOAD_ON_CHANGE;
import static com.developmentontheedge.beans.BeanInfoConstants.TAB_ID;
import static com.developmentontheedge.beans.BeanInfoConstants.TAB_NAME;
import static org.junit.Assert.*;

public class DynamicPropertyBuilderTest
{
    @Test
    public void test(){
        String[] tags = new String[] { "one", "two", "three" };
        String value = "two";
        DynamicProperty property = new DynamicPropertyBuilder( "propname", String.class )
            .title( "Some title" )
            .tags( tags )
            .value( value )
            .onchange( "this.form.submit()" )
            .columnSize( 200 )
            .hidden( false )
            .nullable()
            .readonly()
            .reloadOnChange()
            .attr(GROUP_ID, 0)
            .tabName("tab 1")
            .tabId(1)
            .select2()
            .chosen()
            .multiple()
            .get();

        assertEquals(value, property.getValue());
        assertEquals(String.class, property.getType());
        assertTrue(property.isCanBeNull());
        assertTrue(!property.isHidden());
        assertEquals(true, property.getAttribute(RELOAD_ON_CHANGE));
        assertEquals(0, property.getAttribute(GROUP_ID));
        assertEquals(1, property.getAttribute(TAB_ID));
        assertEquals("tab 1", property.getAttribute(TAB_NAME));
    }

}