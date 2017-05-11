package com.developmentontheedge.beans;

import org.junit.Test;

import static com.developmentontheedge.beans.BeanInfoConstants.GROUP_ID;
import static com.developmentontheedge.beans.BeanInfoConstants.RELOAD_ON_CHANGE;
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
            .select2()
            .chosen()
            .multiple()
            .get();

        assertEquals(value, property.getValue());
        assertEquals(String.class, property.getType());
        assertTrue(property.isCanBeNull());
        assertTrue(!property.isHidden());
        assertEquals(true, property.getAttribute(RELOAD_ON_CHANGE));
    }

}