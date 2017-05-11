package com.developmentontheedge.beans;

import org.junit.Test;

import static com.developmentontheedge.beans.BeanInfoConstants.RELOAD_ON_CHANGE;
import static org.junit.Assert.*;

public class DynamicPropertyBuilderTest
{
    @Test
    public void test(){
        DynamicProperty property = new DynamicPropertyBuilder("prop", String.class)
                .value("test")
                .nullable()
                .hidden()
                .reloadOnChange().get();

        assertEquals("test", property.getValue());
        assertEquals(String.class, property.getType());
        assertTrue(property.isCanBeNull());
        assertTrue(property.isHidden());
        assertEquals(true, property.getAttribute(RELOAD_ON_CHANGE));
    }

}